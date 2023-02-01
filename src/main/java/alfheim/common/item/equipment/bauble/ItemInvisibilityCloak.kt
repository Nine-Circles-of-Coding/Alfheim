package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.PotionEffectU
import alfheim.api.lib.LibResourceLocations
import alfheim.common.core.util.AlfheimTab
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.potion.*
import net.minecraftforge.client.event.RenderPlayerEvent
import vazkii.botania.api.item.IBaubleRender
import vazkii.botania.api.mana.*
import vazkii.botania.client.core.helper.IconHelper

class ItemInvisibilityCloak: ItemBaubleCloak("InvisibilityCloak"), IManaUsingItem {
	
	init {
		creativeTab = AlfheimTab
	}
	
	override fun registerIcons(reg: IIconRegister?) {
		itemIcon = IconHelper.forName(reg, "cloak_invisibility")
	}
	
	override fun onWornTick(stack: ItemStack, player: EntityLivingBase) {
		super.onWornTick(stack, player)
		
		if (player !is EntityPlayer || player.worldObj.isRemote) return
		
		val manaCost = 2
		val hasMana = ManaItemHandler.requestManaExact(stack, player, manaCost, false)
		if (!hasMana)
			onUnequipped(stack, player)
		else {
			ManaItemHandler.requestManaExact(stack, player, manaCost, true)
			player.addPotionEffect(PotionEffectU(Potion.invisibility.id, 10))
		}
	}
	
	override fun usesMana(stack: ItemStack) = true
	
	override fun getCloakTexture(stack: ItemStack) = LibResourceLocations.cloakBalance
	override fun onPlayerBaubleRender(stack: ItemStack, event: RenderPlayerEvent, type: IBaubleRender.RenderType) = Unit
}