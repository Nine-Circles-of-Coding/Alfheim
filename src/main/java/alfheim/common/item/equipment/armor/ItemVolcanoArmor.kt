package alfheim.common.item.equipment.armor

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.client.core.helper.IconHelper
import alfheim.client.model.armor.ModelArmorVolcano
import alfheim.common.core.handler.SheerColdHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.common.entity.EntitySnowSprite
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.*
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.relauncher.*
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.Entity
import net.minecraft.entity.monster.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector
import net.minecraft.world.World
import net.minecraftforge.common.util.EnumHelper
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent
import net.minecraftforge.event.entity.living.LivingHurtEvent
import vazkii.botania.api.mana.*
import vazkii.botania.common.Botania
import vazkii.botania.common.core.handler.ConfigHandler
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.equipment.armor.manasteel.ItemManasteelArmor
import kotlin.math.min

open class ItemVolcanoArmor(type: Int, name: String): ItemManasteelArmor(type, name, volcano), IManaDiscountArmor {
	
	init {
		creativeTab = AlfheimTab
	}
	
	fun repair(stack: ItemStack, world: World, player: EntityPlayer) {
		if (stack.meta > 0 && ManaItemHandler.requestManaExact(stack, player, MANA_PER_DAMAGE * 2, world.isRemote))
			stack.meta = stack.meta - 1
	}
	
	override fun onUpdate(stack: ItemStack, world: World, player: Entity, slot: Int, inHand: Boolean) {
		repair(stack, world, player as? EntityPlayer ?: return)
	}
	
	override fun onArmorTick(world: World, player: EntityPlayer, stack: ItemStack) {
		repair(stack, world, player)
	}
	
	override fun getArmorTextureAfterInk(stack: ItemStack?, slot: Int) =
		"${ModInfo.MODID}:textures/model/armor/volcano${if (ConfigHandler.enableArmorModels) "New" else if (slot == 2) "1" else "0"}.png"
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
	}
	
	override fun getIsRepairable(stack: ItemStack?, material: ItemStack) =
		material.item === AlfheimItems.elvenResource && material.meta == ElvenResourcesMetas.MuspelheimPowerIngot.I
	
	var armorSet: Array<ItemStack>? = null
	
	override fun getArmorSetStacks(): Array<ItemStack> {
		if (armorSet == null)
			armorSet = arrayOf(ItemStack(AlfheimItems.volcanoHelmet), ItemStack(AlfheimItems.volcanoChest), ItemStack(AlfheimItems.volcanoLeggings), ItemStack(AlfheimItems.volcanoBoots))
		
		return armorSet!!
	}
	
	override fun hasArmorSetItem(player: EntityPlayer, i: Int): Boolean {
		val stack = player.inventory.armorInventory[3 - i] ?: return false
		when (i) {
			0 -> return stack.item === AlfheimItems.volcanoHelmet || (Botania.thaumcraftLoaded && stack.item === AlfheimItems.volcanoHelmetRevealing)
			1 -> return stack.item === AlfheimItems.volcanoChest
			2 -> return stack.item === AlfheimItems.volcanoLeggings
			3 -> return stack.item === AlfheimItems.volcanoBoots
		}
		return false
	}
	
	override fun getArmorSetName() = StatCollector.translateToLocal("alfheim.armorset.volcano.name")!!
	
	override fun getArmorSetTitle(player: EntityPlayer?) =
		"${StatCollector.translateToLocal("botaniamisc.armorset")} $armorSetName (${getSetPiecesEquipped(player)}/${armorSetStacks.size})"
	
	override fun addArmorSetDescription(stack: ItemStack?, list: MutableList<String>?) {
		addStringToTooltip(StatCollector.translateToLocal("alfheim.armorset.volcano.desc"), list)
	}
	
	@SideOnly(Side.CLIENT)
	override fun provideArmorModelForSlot(stack: ItemStack?, slot: Int): ModelBiped? {
		models[slot] = ModelArmorVolcano(slot)
		return models[slot]
	}
	
	override fun getDiscount(stack: ItemStack?, slot: Int, player: EntityPlayer?) = 0.05f
	
	companion object {
		
		const val MAX_CHARGE = 1000f
		const val MANA_PER_DAMAGE = 70
		const val TAG_POWER = "firepower"
		
		val volcano = EnumHelper.addArmorMaterial("Volcano", 26, intArrayOf(3, 7, 6, 3), 6)!!
		
		init {
			eventForge()
		}
		
		fun hasSet(player: EntityPlayer) = (AlfheimItems.volcanoChest as ItemVolcanoArmor).hasArmorSet(player)
		
		fun getCharge(player: EntityPlayer): Float {
			val stack = player.inventory.armorItemInSlot(2) ?: return 0f
			if (stack.item !is ItemVolcanoArmor) return 0f
			return ItemNBTHelper.getFloat(stack, TAG_POWER, 0f)
		}
		
		fun addCharge(player: EntityPlayer, charge: Float) {
			val stack = player.inventory.armorItemInSlot(2) ?: return
			if (stack.item !is ItemVolcanoArmor) return
			ItemNBTHelper.setFloat(stack, TAG_POWER, min(ItemNBTHelper.getFloat(stack, TAG_POWER, 0f) + charge, MAX_CHARGE))
		}
		
		@SubscribeEvent
		fun onPlayerHurt(e: LivingHurtEvent) {
			val player = e.entityLiving as? EntityPlayer ?: return
			if (!hasSet(player)) return
			
			if (e.source.damageType.contains("frost", true) || e.source.damageType.contains("ice", true)) {
				e.ammount /= 2
				return
			}
			
			if (e.source.isFireDamage) {
				if (getCharge(player) + e.ammount > MAX_CHARGE)
					return
				
				addCharge(player, e.ammount)
				e.isCanceled = true
				
				val block = player.worldObj.getBlock(player)
				val lava = block === Blocks.lava && player.worldObj.getBlockMeta(player) == 0
				val add = if (lava) 10f else 1f
				
				if ((lava || block === Blocks.fire) && getCharge(player) + add <= MAX_CHARGE && player.worldObj.setBlock(player, Blocks.air)) {
					addCharge(player, add)
				}
			}
		}
		
		@SubscribeEvent
		fun onPlayerHurting(e: LivingHurtEvent) {
			if (e.entityLiving !is EntitySlime || e.entityLiving !is EntitySnowSprite || e.entityLiving is EntityMagmaCube) return
			if (!hasSet(e.source.entity as? EntityPlayer ?: return)) return
			e.ammount *= 1.25f
		}
		
		@SubscribeEvent
		fun onLivingUpdate(e: LivingUpdateEvent) {
			val player = e.entityLiving as? EntityPlayer ?: return
			if (!hasSet(player) || !player.isSneaking) return
			
			for (x in -1..1) {
				for (y in -1..1) {
					for (z in -1..1) {
						val new = when (player.worldObj.getBlock(player, x, y, z)) {
									  Blocks.obsidian                            -> Blocks.lava
									  Blocks.web, Blocks.snow_layer              -> Blocks.air
									  Blocks.ice, Blocks.packed_ice, Blocks.snow -> Blocks.water
									  else                                       -> null
								  } ?: continue
						
						if (getCharge(player) < 5f) return
						
						if (player.worldObj.setBlock(player, new, x, y, z))
							addCharge(player, -5f)
					}
				}
			}
		}
		
		@SubscribeEvent(priority = EventPriority.LOW)
		fun warmPlayer(e: SheerColdHandler.SheerColdTickEvent) {
			val target = e.entityLiving
			if (!RagnarokHandler.checkSet(target, (AlfheimItems.volcanoChest as ItemVolcanoArmor).armorSetStacks)) return
			if (target is EntityPlayer && ASJUtilities.getAmount(target.inventory, ItemStack(AlfheimItems.eventResource, 1, EventResourcesMetas.SnowRelic)) > 0) return
			e.delta = min(e.delta ?: 0f, -100f/(30*60*20)) // minimal so that if other source gives more heat it won't override
		}
	}
}