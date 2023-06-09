package alfheim.common.spell.illusion

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import alfheim.common.core.util.DamageSourceSpell
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

object SpellSwap: SpellBase("swap", EnumRace.LEPRECHAUN, 12000, 1200, 20) {
	
	override var damage = 10f
	
	override val usableParams: Array<Any>
		get() = arrayOf(damage)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = TargetingSystem.getTarget(caster)
		
		if (tg.target == null && (!tg.isParty || tg.partyIndex < 0)) return SpellCastResult.NOTARGET
		
		val tgt = tg.target ?: return SpellCastResult.NOTARGET
		
		if (tgt === caster) return SpellCastResult.WRONGTGT
		
		if (tgt !is EntityPlayer && tgt.dimension != caster.dimension) return SpellCastResult.WRONGTGT
		
		if (!tg.isParty && ASJUtilities.isNotInFieldOfVision(tgt, caster)) return SpellCastResult.NOTSEEING
		
		if (tg.isParty) {
			if (InteractionSecurity.isInteractionBanned(caster)) return SpellCastResult.NOTALLOW
			if (InteractionSecurity.isInteractionBanned(tgt)) return SpellCastResult.NOTALLOW
			if (InteractionSecurity.isInteractionBanned(caster, tgt.posX, tgt.posY, tgt.posZ, tgt.worldObj)) return SpellCastResult.NOTALLOW
			if (InteractionSecurity.isInteractionBanned(tgt, caster.posX, caster.posY, caster.posZ, caster.worldObj)) return SpellCastResult.NOTALLOW
		} else {
			if (!InteractionSecurity.canHurtEntity(caster, tgt)) return SpellCastResult.NOTALLOW
		}
		
		val result = checkCast(caster)
		if (result == SpellCastResult.OK) {
			val (cx, cy, cz) = Vector3.fromEntity(caster)
			
			if (tgt is EntityPlayer) {
				val cd = caster.dimension
				
				ASJUtilities.sendToDimensionWithoutPortal(caster, tgt.dimension, tgt.posX, tgt.posY, tgt.posZ)
				ASJUtilities.sendToDimensionWithoutPortal(tgt, cd, cx, cy, cz)
			} else {
				val cry = caster.rotationYaw
				val crp = caster.rotationPitch
				
				caster.setLocationAndAngles(tgt.posX, tgt.posY, tgt.posZ, tgt.rotationYaw, tgt.rotationPitch)
				caster.setPositionAndRotation(tgt.posX, tgt.posY, tgt.posZ, tgt.rotationYaw, tgt.rotationPitch)
				caster.setPositionAndUpdate(tgt.posX, tgt.posY, tgt.posZ)
				
				tgt.setPositionAndRotation(cx, cy, cz, cry, crp)
				tgt.setLocationAndAngles(cx, cy, cz, cry, crp)
			}
			
			val hp = tgt.maxHealth / damage
			if (tg.isParty)
				tgt.heal(hp)
			else
				tgt.attackEntityFrom(DamageSourceSpell.magic(caster), hp)
		}
		
		return result
	}
}