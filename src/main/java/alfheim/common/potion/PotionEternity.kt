package alfheim.common.potion

import alexsocol.asjlib.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.item.equipment.bauble.ItemPendant
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.*
import net.minecraft.entity.ai.attributes.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.entity.living.*
import java.util.*

/**
 * @author ExtraMeteorP, CKATEPTb
 */
object PotionEternity: PotionAlfheim(AlfheimConfigHandler.potionIDEternity, "eternity", false, 0xDAA520) {
	
	val uuid = UUID.fromString("0B02BC22-17AE-484C-8FD8-BA9BF3472D5C")!!
	
	const val STUN        = 0b001
	const val ATTACK      = 0b010
	const val IRREMOVABLE = 0b100
	
	init {
		eventForge()
	}
	
	override fun applyAttributesModifiersToEntity(target: EntityLivingBase, map: BaseAttributeMap, amp: Int) {
		super.applyAttributesModifiersToEntity(target, map, amp)
		if (amp != 0 && amp and STUN == 0) return
		
		val m = AttributeModifier(uuid, name, -1.0, 2)
		target.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(m)
		target.getEntityAttribute(SharedMonsterAttributes.movementSpeed).applyModifier(m)
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase, map: BaseAttributeMap, amp: Int) {
		super.removeAttributesModifiersFromEntity(target, map, amp)
		if (amp != 0 && amp and STUN == 0) return
		
		target.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(target.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getModifier(uuid) ?: return)
	}
	
	var time = 0
	
	override fun isReady(dur: Int, amp: Int): Boolean {
		time = dur
		return true
	}
	
	override fun performEffect(target: EntityLivingBase, amp: Int) {
		if (amp == 0) {
			if (target.isSneaking) target.getActivePotionEffect(id)?.duration = 0
			
			if (time >= 115) return
		} else if (amp and IRREMOVABLE == 0 && target is EntityPlayer && ItemPendant.canProtect(target, ItemPendant.Companion.EnumPrimalWorldType.NIFLHEIM, time)) {
			target.getActivePotionEffect(id)?.duration = 0
			return
		}
		
		val stun = amp == 0 || amp and STUN != 0
		val attack = amp and ATTACK != 0 && time % 20 == 0
		
		if (stun) {
			if (target is EntityPlayer && target.capabilities.isFlying) target.capabilities.isFlying = false

			if (!target.onGround) {
				target.motionX = 0.0
				target.motionY = 0.0
				target.motionZ = 0.0
				target.fallDistance = 0f
			}
		}
		
		if (attack) run {
			if (target is EntityPlayer && ItemPendant.canProtect(target, ItemPendant.Companion.EnumPrimalWorldType.NIFLHEIM, 300))
				return
			
			target.attackEntityFrom(DamageSourceSpell.nifleice, 1f)
			return
		}
	}
	
	@SubscribeEvent
	fun onDamageTaken(event: LivingHurtEvent) {
		val player = event.entityLiving as? EntityPlayer ?: return
		val eff = player.getActivePotionEffect(this.id) ?: return
		if (eff.amplifier == 0) event.ammount = 0f
	}
	
	@SubscribeEvent
	fun onHealing(e: LivingHealEvent) {
		val pe = e.entityLiving.getActivePotionEffect(this) ?: return
		if (pe.amplifier and STUN == 0) e.isCanceled
	}
}
