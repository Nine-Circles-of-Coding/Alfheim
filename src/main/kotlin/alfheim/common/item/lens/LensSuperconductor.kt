package alfheim.common.item.lens

import alexsocol.asjlib.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.helper.*
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.item.equipment.armor.elvoruim.ItemElvoriumArmor
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import vazkii.botania.api.internal.IManaBurst
import vazkii.botania.api.mana.BurstProperties
import vazkii.botania.common.entity.EntityManaBurst
import vazkii.botania.common.item.lens.Lens

class LensSuperconductor: Lens() {
	
	override fun apply(stack: ItemStack, props: BurstProperties) {
		props.maxMana *= 8
		props.motionModifier *= 1.5f
		props.manaLossPerTick *= 16f
		props.ticksBeforeManaLoss = (props.ticksBeforeManaLoss * 0.8).I
	}
	
	override fun updateBurst(burst: IManaBurst, entity: EntityThrowable, stack: ItemStack) {
		burst as EntityManaBurst
		
		if (entity.worldObj.isRemote || burst.isFake) return
		
		val axis = getBoundingBox(entity.posX, entity.posY, entity.posZ, entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).expand(1.5)
		val list = getEntitiesWithinAABB(entity.worldObj, EntityLivingBase::class.java, axis)
		list.remove(burst.thrower)
		list.forEach {
			var admg = false
			val dmg = if (it !is EntityPlayer) 8f else if (ItemElvoriumArmor.hasSet(it) || !AlfheimConfigHandler.uberBlaster) 12f.also { if (AlfheimConfigHandler.uberBlaster) admg = true } else 25f
			
			if (!it.attackEntityFrom(SUPERCONDUCTOR(burst.thrower), dmg) || !admg) return@forEach
			(it as EntityPlayer).inventory.damageArmor(13f)
		}
	}
	
	fun SUPERCONDUCTOR(e: EntityLivingBase?) = (if (e != null) DamageSourceSpell.magic(e) else DamageSource("magic")).setTo(ElementalDamage.ELECTRIC)
}
