package alfheim.common.item.equipment.tool

import alexsocol.asjlib.meta
import alfheim.client.core.helper.IconHelper
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraftforge.common.util.EnumHelper
import vazkii.botania.common.item.equipment.tool.manasteel.ItemManasteelSword

class ItemVolcanoMace: ItemManasteelSword(volcano, "VolcanoMace") {
	
	init {
		creativeTab = AlfheimTab
	}
	
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
	}
	
	override fun hitEntity(stack: ItemStack, target: EntityLivingBase, attacker: EntityLivingBase): Boolean {
		target.setFire(10)
		return super.hitEntity(stack, target, attacker)
	}
	
	override fun getIsRepairable(par1ItemStack: ItemStack?, stack: ItemStack) =
		stack.item === AlfheimItems.elvenResource && stack.meta == ElvenResourcesMetas.MuspelheimEssence.I
	
	companion object {
		
		val volcano = EnumHelper.addToolMaterial("Volcano", 0, 1200, 6f, 6f, 6)!!
	}
}