package alfheim.common.item.equipment.armor.elemental

import alexsocol.asjlib.PotionEffectU
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.potion.Potion
import net.minecraft.util.StatCollector
import net.minecraft.world.World
import vazkii.botania.api.mana.ManaItemHandler

class ItemElementalEarthChest: ElementalArmor(1, "ElementalEarthChest") {
	
	override fun getPixieChance(stack: ItemStack): Float {
		return 0.17f
	}
	
	override fun onArmorTick(world: World, player: EntityPlayer, stack: ItemStack) {
		if (ManaItemHandler.requestManaExact(stack, player, 1, !world.isRemote)) player.addPotionEffect(PotionEffectU(Potion.resistance.id, 1, 1))
	}
	
	@SideOnly(Side.CLIENT)
	override fun addInformation(stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, b: Boolean) {
		list.add(StatCollector.translateToLocal("item.ElementalArmor.desc3"))
		super.addInformation(stack, player, list, b)
	}
}
