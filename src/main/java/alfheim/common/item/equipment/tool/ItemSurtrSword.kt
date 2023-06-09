package alfheim.common.item.equipment.tool

import alexsocol.asjlib.PotionEffectU
import alfheim.api.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.equipment.bauble.ItemPendant
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*

class ItemSurtrSword: ItemSword(AlfheimAPI.SURTR) {
	
	init {
		creativeTab = AlfheimTab
		maxDamage = 0
		unlocalizedName = "SurtrSword"
	}
	
	override fun onLeftClickEntity(stack: ItemStack, player: EntityPlayer, target: Entity): Boolean {
		leftClickEntity(player, target)
		return false
	}
	
	fun leftClickEntity(attacker: EntityLivingBase, target: Entity) {
		if (!target.canAttackWithItem()) return
		if (target.hitByEntity(attacker)) return
		
		if (target !is EntityLivingBase) return target.setFire(10)
		if (target is EntityPlayer && ItemPendant.canProtect(target, ItemPendant.Companion.EnumPrimalWorldType.MUSPELHEIM, 300)) return
		
		return target.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDSoulburn, 300))
	}
	
	override fun setUnlocalizedName(name: String): Item {
		GameRegistry.registerItem(this, name)
		return super.setUnlocalizedName(name)
	}
	
	override fun getItemStackDisplayName(stack: ItemStack) = super.getItemStackDisplayName(stack).replace("&".toRegex(), "\u00a7")
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack) = super.getUnlocalizedNameInefficiently(stack).replace("item\\.".toRegex(), "item.${ModInfo.MODID}:")
	
	override fun registerIcons(reg: IIconRegister) = Unit
}
