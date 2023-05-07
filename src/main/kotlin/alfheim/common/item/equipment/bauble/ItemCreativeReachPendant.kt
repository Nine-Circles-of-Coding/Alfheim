package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.ASJUtilities
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderPlayerEvent
import vazkii.botania.api.item.IBaubleRender
import vazkii.botania.client.core.helper.IconHelper
import vazkii.botania.common.Botania

class ItemCreativeReachPendant: ItemPendant("CreativeReachPendant") {
	
	override fun onItemRightClick(stack: ItemStack?, world: World?, player: EntityPlayer): ItemStack? {
		if (ASJUtilities.isServer)
			ASJUtilities.say(player, "item.CreativeReachPendant.warn")
		
		return stack
	}
	
	override fun onEquippedOrLoadedIntoWorld(stack: ItemStack?, player: EntityLivingBase?) {
		Botania.proxy.setExtraReach(player, 100f)
	}
	
	override fun onUnequipped(stack: ItemStack?, player: EntityLivingBase?) {
		Botania.proxy.setExtraReach(player, -100f)
	}
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack): String {
		return getUnlocalizedName(stack)
	}
	
	override fun onPlayerBaubleRender(stack: ItemStack, event: RenderPlayerEvent, type: IBaubleRender.RenderType) = Unit
	
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
	}
}
