package alfheim.common.core.handler

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.block.tile.TileItemDisplay
import alfheim.common.core.handler.HilarityHandler.AttributionNameChecker.getCurrentNickname
import alfheim.common.crafting.recipe.AlfheimRecipes
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
import baubles.common.lib.PlayerHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.oredict.OreDictionary
import vazkii.botania.common.item.ModItems
import java.net.URL

/**
 * @author WireSegal
 * Created at 6:11 PM on 2/4/16.
 */
object HilarityHandler {
	
	private val handlers: ArrayList<CraftHandler> = ArrayList()
	
	private fun registerHandler(
		playerName: String, cheatyString: String,
		gender: String, chatColor: EnumChatFormatting,
		neededItems: List<ItemStack>, normalString: String,
		resourceItem: ItemStack, outputItem: ItemStack,
	) {
		
		handlers.add(CraftHandler(playerName, cheatyString,
								  gender, chatColor,
								  neededItems, normalString,
								  resourceItem, outputItem))
	}
	
	val itemsRequiredWire = arrayListOf(                                  // + Elementium Axe (in hand)
		ItemStack(ModItems.dice),                                         //   Dice of Fate              Chaos
		ItemStack(ModItems.manaResource, 1, 5),                           //   Gaia Spirit               Divinity
		ElvenResourcesMetas.ThunderwoodSplinters.stack,                   //   Thunderous Splinters      Lightning
		AlfheimRecipes.skullStack(getCurrentNickname("Tristaric")),       //   Tris's head               Humanity
		ItemStack(ModItems.rainbowRod, 1, OreDictionary.WILDCARD_VALUE),  //   The Rod of the Bifrost    Order
		ItemStack(ModItems.manaResource, 1, 4)                            //   Terrasteel                Earth
	)
	
	val itemsRequiredTris = arrayListOf(                                  // + Elementium Sword (in hand)
		ItemStack(ModItems.dice),                                         //   Dice of Fate              Chaos
		ItemStack(ModItems.manaResource, 1, 5),                           //   Gaia Spirit               Divinity
		ItemStack(ModItems.rune, 1, 13),                                  //   Rune of Wrath             Lightning
		AlfheimRecipes.skullStack(getCurrentNickname("yrsegal")),         //   Wire's head               Humanity
		ItemStack(ModItems.laputaShard, 1, OreDictionary.WILDCARD_VALUE), //   The Shard of Laputa       Order
		ItemStack(ModItems.dirtRod)                                       //   The Rod of the Lands      Earth
	)
	
	val itemsRequiredAesir = arrayListOf(
		ItemStack(AlfheimItems.priestEmblem, 1, 0),
		ItemStack(AlfheimItems.priestEmblem, 1, 1),
		ItemStack(AlfheimItems.priestEmblem, 1, 2),
		ItemStack(AlfheimItems.priestEmblem, 1, 3),
		ItemStack(AlfheimItems.priestEmblem, 1, 4),
		ItemStack(AlfheimItems.priestEmblem, 1, 5),
		ElvenResourcesMetas.MauftriumIngot.stack,
		ElvenResourcesMetas.YggFruit.stack
	)
	
	init {
		eventForge()
		
		registerHandler("yrsegal", "I claim the Blade of Chaos!", "Male", EnumChatFormatting.GOLD,
						itemsRequiredWire, "I awaken the Ancients within all of you! From my soul's fire the world burns anew!",
						ItemStack(ModItems.elementiumAxe, 1, OreDictionary.WILDCARD_VALUE), ItemStack(AlfheimItems.wireAxe))
		registerHandler("Tristaric", "I claim the Blade of Order!", "Female", EnumChatFormatting.LIGHT_PURPLE,
						itemsRequiredTris, "My inward eye sees the depths of my soul! I accept both sides, and reject my downfall!",
						ItemStack(ModItems.elementiumSword, 1, OreDictionary.WILDCARD_VALUE), ItemStack(AlfheimItems.trisDagger))
		registerHandler("", "", "", EnumChatFormatting.GOLD,
		                itemsRequiredAesir, "I enclose divine power in a vessel whose strength is comparable to gods blessed!",
		                ItemStack(AlfheimItems.attributionBauble), ItemStack(AlfheimItems.aesirEmblem))
	}
	
	private class CraftHandler(
		val playerName: String, val cheatyString: String,
		val gender: String, val chatColor: EnumChatFormatting,
		val neededItems: List<ItemStack>, val normalString: String,
		val resourceItem: ItemStack, val outputItem: ItemStack,
	) {
		
		fun execute(e: ServerChatEvent): Boolean {
			val msg = e.message.trim()
			val player = e.player
			
			if (outputItem.item === AlfheimItems.aesirEmblem && PlayerHandler.getPlayerBaubles(player)[0]?.item !== AlfheimItems.aesirEmblem)
				return false
			
			if (player.commandSenderName == playerName && msg == AlfheimConfigHandler.chatLimiters.format(cheatyString)) {
				if (replaceItemInHand(player, resourceItem, outputItem)) {
					e.component.chatStyle.color = chatColor
					player.playSoundAtEntity("ambient.weather.thunder", 100f, 0.8f + player.worldObj.rand.nextFloat() * 0.2f)
					return true
				}
			} else if (msg == AlfheimConfigHandler.chatLimiters.format(normalString)) {
				val items = getInfusionPlatforms(player.worldObj, player.posX.mfloor(), player.posY.mfloor(), player.posZ.mfloor())
				
				val itemsMissing = ArrayList(neededItems)
				for (itemPair in items) {
					val item = itemPair.stack
					for (itemNeeded in neededItems) {
						if (itemNeeded !in itemsMissing) continue
						if (itemNeeded.item != item.item) continue
						if (itemNeeded.meta != item.meta && itemNeeded.meta != OreDictionary.WILDCARD_VALUE) continue
						if (itemNeeded.hasTagCompound() && itemNeeded.tagCompound.getString("SkullOwner") != item.tagCompound.getString("SkullOwner")) continue
						
						itemsMissing.remove(itemNeeded)
						itemPair.flag = true
					}
				}
				if (itemsMissing.isEmpty()) {
					if (replaceItemInHand(player, resourceItem, outputItem)) {
						e.component.chatStyle.color = chatColor
						for (itemPair in items)
							if (itemPair.flag) {
								val te = itemPair.pos.getTileAt(player.worldObj, player.posX.mfloor(), player.posY.mfloor(), player.posZ.mfloor())
								if (te is TileItemDisplay)
									te[0] = null
							}
						player.playSoundAtEntity("botania:enchanterEnchant", 1f, 1f)
						return true
					}
				}
			} else if (msg == AlfheimConfigHandler.chatLimiters.format(cheatyString)) {
				val chat = ChatComponentText(StatCollector.translateToLocal("misc.${ModInfo.MODID}.youAreNotTheChosenOne$gender"))
				chat.chatStyle.color = chatColor
				player.addChatMessage(chat)
				e.isCanceled = true
				return true
			}
			return false
		}
		
		private class Pos(val x: Int, val y: Int, val z: Int) {
			
			fun getTileAt(world: World, x: Int, y: Int, z: Int): TileEntity? = world.getTileEntity(x + this.x, y + this.y, z + this.z)
		}
		
		private class PosPair(val pos: Pos, val stack: ItemStack) {
			
			var flag = false
		}
		
		private val platformPositions = arrayOf(
			Pos(2, 0, 2),
			Pos(-2, 0, 2),
			Pos(-2, 0, -2),
			Pos(2, 0, -2),
			Pos(4, 0, 0),
			Pos(0, 0, 4),
			Pos(-4, 0, 0),
			Pos(0, 0, -4)
		)
		
		private fun getInfusionPlatforms(world: World, x: Int, y: Int, z: Int): MutableList<PosPair> {
			val items = ArrayList<PosPair>()
			for (pos in platformPositions) {
				val tile = pos.getTileAt(world, x, y, z)
				if (tile is TileItemDisplay) {
					val stack = tile[0]
					if (stack != null) items.add(PosPair(pos, stack))
				}
			}
			return items
		}
		
		private fun replaceItemInHand(player: EntityPlayer, oldStack: ItemStack, newStack: ItemStack): Boolean {
			val stackInSlot = player.heldItem
			if (stackInSlot != null && stackInSlot.item == oldStack.item && (stackInSlot.meta == oldStack.meta || oldStack.meta == OreDictionary.WILDCARD_VALUE || stackInSlot.item.isDamageable)) {
				newStack.stackSize = oldStack.stackSize
				newStack.stackTagCompound = stackInSlot.tagCompound
				player.setCurrentItemOrArmor(0, newStack)
				return true
			}
			return false
		}
	}
	
	@SubscribeEvent
	fun someoneSaidSomething(whatWasIt: ServerChatEvent) {
		for (handler in handlers)
			if (handler.execute(whatWasIt))
				return
	}
	
	object AttributionNameChecker {
		
		val nickToIdMap = mapOf(
			"yrsegal" to "458391f563034649b416e4c0d18f837a",
			"l0nekitsune" to "d475af59d73c42be90edf1a78f10d452",
			"Tristaric" to "d7a5f99557d54077bc9f83cc36cadd66"
		                       )
		
		val actualNamesCache = HashMap<String, String>()
		
		val gson = Gson()
		val type = object: TypeToken<Map<String, Any>>() {}.type
		
		fun getCurrentNickname(oldName: String): String {
			return actualNamesCache.computeIfAbsent(oldName) {
				try {
					gson.fromJson<Map<String, Any>>(URL("https://sessionserver.mojang.com/session/minecraft/profile/${nickToIdMap[oldName]}").readText(), type)["name"].toString()
				} catch (e: Exception) {
					oldName
				}
			}
		}
	}
}
