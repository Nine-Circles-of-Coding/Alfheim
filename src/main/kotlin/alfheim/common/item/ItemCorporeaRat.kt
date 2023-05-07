package alfheim.common.item

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.block.tile.corporea.TileCorporeaRat
import cpw.mods.fml.common.eventhandler.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.event.ServerChatEvent
import org.apache.commons.lang3.text.WordUtils
import vazkii.botania.api.corporea.*
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.api.wand.ICoordBoundItem
import vazkii.botania.common.achievement.ModAchievements
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex
import kotlin.collections.sumOf
import kotlin.math.min

class ItemCorporeaRat: ItemMod("CorporeaRat"), ICoordBoundItem {
	
	init {
		maxStackSize = 1
	}
	
	override fun onItemUse(stack: ItemStack, player: EntityPlayer?, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (world.getTileEntity(x, y, z) !is TileCorporeaRat)
			return false
		
		ItemNBTHelper.setInt(stack, TAG_X, x)
		ItemNBTHelper.setInt(stack, TAG_Y, y)
		ItemNBTHelper.setInt(stack, TAG_Z, z)
		ItemNBTHelper.setInt(stack, TAG_D, world.provider.dimensionId)
		
		return true
	}
	
	override fun getBinding(stack: ItemStack) = getBindPos(mc.thePlayer, stack) // fuck you sideonly shit
	
	fun getBindPos(player: EntityPlayer, stack: ItemStack): ChunkCoordinates? {
		val y = ItemNBTHelper.getInt(stack, TAG_Y, -1)
		
		return if (y == -1 || player.dimension != ItemNBTHelper.getInt(stack, TAG_D, 0)) null else ChunkCoordinates(ItemNBTHelper.getInt(stack, TAG_X, 0), y, ItemNBTHelper.getInt(stack, TAG_Z, 0))
	}
	
	companion object RatInputHandler: ICorporeaAutoCompleteController {
		
		const val TAG_D = "toD"
		const val TAG_X = "toX"
		const val TAG_Y = "toY"
		const val TAG_Z = "toZ"
		
		init {
			CorporeaHelper.registerAutoCompleteController(this)
			eventForge()
		}
		
		override fun shouldAutoComplete() = if (ASJUtilities.isClient) mc.thePlayer.heldItem?.item === AlfheimItems.corporeaRat else false
		
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		fun onChatMessage(event: ServerChatEvent) {
			val stack = event.player.heldItem ?: return
			val item = stack.item as? ItemCorporeaRat ?: return
			if (TileCorporeaIndex.InputHandler.getNearbyIndexes(event.player).isNotEmpty()) return
			val (x, y, z) = item.getBindPos(event.player, stack) ?: return
			val world = MinecraftServer.getServer().worldServerForDimension(ItemNBTHelper.getInt(stack, TAG_D, 0)) ?: return
			val rat = world.getTileEntity(x, y, z) as? TileCorporeaRat ?: return
			val spark = rat.spark ?: return
			
			val msg = event.message.lowercase().trim { it <= ' ' }
			var name = ""
			var count = 0
			
			for (pattern in TileCorporeaIndex.patterns.keys) {
				val matcher = pattern.matcher(msg)
				
				if (matcher.matches()) {
					val stacker = TileCorporeaIndex.patterns[pattern]
					count = stacker!!.getCount(matcher)
					name = stacker.getName(matcher).lowercase().trim { it <= ' ' }
					pattern.toString()
				}
			}
			
			if (name == "this")
				name = stack.displayName.lowercase().trim { it <= ' ' }
			
			count = min(CorporeaHelper.requestItem(name, -1, spark, true, false).sumOf { it.stackSize }, count)
			
			val vecDst = Vector3.fromEntity(event.player).mul(event.player.worldObj.provider.movementFactor)
			val vecSrc = Vector3(x, y, z).mul(world.provider.movementFactor)
			val distance = Vector3.vecDistance(vecDst, vecSrc)
			val cost = (count + 1) * distance * if (world.provider.dimensionId != event.player.dimension) 2 else 1
			
			if (!ManaItemHandler.requestManaExactForTool(stack, event.player, (cost).I, true))
				return
			
			rat.queueRequest(name, count, event.player.commandSenderName)
			rat.doCorporeaRequest(name, count, spark)
			event.player.addChatMessage(ChatComponentTranslation("botaniamisc.requestMsg", count, WordUtils.capitalizeFully(name), CorporeaHelper.lastRequestMatches, CorporeaHelper.lastRequestExtractions).setChatStyle(ChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE)))
			
			if (CorporeaHelper.lastRequestExtractions >= 50000)
				event.player.addStat(ModAchievements.superCorporeaRequest, 1)
			
			event.isCanceled = true
		}
	}
}
