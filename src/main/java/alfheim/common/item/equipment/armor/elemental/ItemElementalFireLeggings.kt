package alfheim.common.item.equipment.armor.elemental

import alexsocol.asjlib.eventForge
import alfheim.common.item.AlfheimItems
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector
import net.minecraft.world.World
import net.minecraftforge.event.entity.living.LivingEvent
import vazkii.botania.api.mana.ManaItemHandler

class ItemElementalFireLeggings: ElementalArmor(2, "ElementalFireLeggings") {
	
	override fun getPixieChance(stack: ItemStack): Float {
		return 0.15f
	}
	
	override fun onArmorTick(world: World, player: EntityPlayer, stack: ItemStack) {
		if (player.isBurning && ManaItemHandler.requestManaExact(stack, player, 10, !world.isRemote)) player.extinguish()
	}
	
	@SideOnly(Side.CLIENT)
	override fun addInformation(stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, b: Boolean) {
		list.add(StatCollector.translateToLocal("item.ElementalArmor.desc2"))
		super.addInformation(stack, player, list, b)
	}
	
	companion object {
		
		init {
			eventForge()
		}
		
		@SubscribeEvent
		fun updatePlayerStepStatus(event: LivingEvent.LivingUpdateEvent) {
			val player = event.entityLiving as? EntityPlayer ?: return
			val legs = player.getCurrentArmor(1) ?: return
			if (legs.item !== AlfheimItems.elementalLeggings || !ManaItemHandler.requestManaExact(legs, player, 1, false)) return
			if (!(player.onGround || player.capabilities.isFlying) || player.moveForward <= 0f || player.isInsideOfMaterial(Material.water)) return
			val speed = 0.185F
			player.moveFlying(0f, 1f, if (player.capabilities.isFlying) speed else speed)
			ManaItemHandler.requestManaExact(legs, player, 1, true)
		}
	}
}
