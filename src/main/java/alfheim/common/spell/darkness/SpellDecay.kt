package alfheim.common.spell.darkness

import alexsocol.asjlib.*
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import net.minecraft.entity.EntityLivingBase

object SpellDecay: SpellBase("decay", EnumRace.IMP, 12000, 2400, 25) {
	
	override var duration = 600
	
	override val usableParams: Array<Any>
		get() = arrayOf(duration, efficiency)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = TargetingSystem.getTarget(caster)
		val tgt = tg.target ?: return SpellCastResult.NOTARGET
		
		if (tgt === caster || tg.isParty)
			return SpellCastResult.WRONGTGT
		
		if (ASJUtilities.isNotInFieldOfVision(tgt, caster)) return SpellCastResult.NOTSEEING
		
		if (!InteractionSecurity.canHurtEntity(caster, tgt)) return SpellCastResult.NOTALLOW
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		tgt.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDDecay, duration))
		VisualEffectHandler.sendPacket(VisualEffects.DISPEL, tgt)
		
		return result
	}
}