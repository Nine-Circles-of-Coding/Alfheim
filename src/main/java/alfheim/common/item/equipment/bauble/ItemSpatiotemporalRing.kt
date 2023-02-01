package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.get
import alfheim.common.core.util.AlfheimTab
import baubles.api.BaubleType
import baubles.common.lib.PlayerHandler
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector
import net.minecraft.world.World
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.equipment.bauble.ItemBauble

class ItemSpatiotemporalRing: ItemBauble("spatiotemporalRing") {
	
	init {
		creativeTab = AlfheimTab
	}
	
	override fun getBaubleType(p0: ItemStack?) = BaubleType.RING
	
	fun isActive(stack: ItemStack, player: EntityPlayer) = ItemNBTHelper.getBoolean(stack, TAG_ALWAYS_ON, true) || !player.isSneaking
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (!player.isSneaking) return super.onItemRightClick(stack, world, player)
		
		ItemNBTHelper.setBoolean(stack, TAG_ALWAYS_ON, !ItemNBTHelper.getBoolean(stack, TAG_ALWAYS_ON, true))
		return stack
	}
	
	override fun addHiddenTooltip(stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, adv: Boolean) {
		super.addHiddenTooltip(stack, player, list, adv)
		list.add(StatCollector.translateToLocal("item.botania:spatiotemporalRing.desc.${ItemNBTHelper.getBoolean(stack, TAG_ALWAYS_ON, true)}"))
	}
	
	companion object {
		
		const val TAG_ALWAYS_ON = "alwaysOn"
		
		fun hasProtection(player: EntityLivingBase): Boolean {
			if (player !is EntityPlayer) return false
			
			for (i in 1..2) {
				val stack = PlayerHandler.getPlayerBaubles(player)[i] ?: continue
				val item = stack.item as? ItemSpatiotemporalRing ?: continue
				
				return item.isActive(stack, player) && ManaItemHandler.requestManaExact(stack, player, 10, true)
			}
			
			return false
		}
	}
}