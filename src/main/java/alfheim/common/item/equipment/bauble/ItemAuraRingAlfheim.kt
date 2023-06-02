package alfheim.common.item.equipment.bauble

import alfheim.common.core.util.AlfheimTab
import baubles.api.BaubleType
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import vazkii.botania.api.item.IPixieSpawner
import vazkii.botania.api.mana.*
import vazkii.botania.common.item.equipment.bauble.ItemBauble

open class ItemAuraRingAlfheim(name: String, val delay: Int = 5, val pixieChance: Float = 0f): ItemBauble(name), IManaGivingItem, IPixieSpawner {
	
	init {
		creativeTab = AlfheimTab
	}
	
	override fun onWornTick(stack: ItemStack, player: EntityLivingBase) {
		super.onWornTick(stack, player)
		if (player is EntityPlayer && player.ticksExisted % delay == 0)
			if (!ManaItemHandler.dispatchManaExact(stack, player, 10, true))
				ManaItemHandler.dispatchMana(stack, player, 10, true)
	}
	
	override fun getBaubleType(itemstack: ItemStack): BaubleType {
		return BaubleType.RING
	}
	
	override fun getPixieChance(stack: ItemStack?) = pixieChance
}
