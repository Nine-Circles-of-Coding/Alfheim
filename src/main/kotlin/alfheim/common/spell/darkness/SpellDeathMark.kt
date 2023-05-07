package alfheim.common.spell.darkness

import alexsocol.asjlib.*
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

object SpellDeathMark: SpellBase("deathmark", EnumRace.IMP, 24000, 3000, 10) {
	
	override var damage = Float.MAX_VALUE
	override var duration = 600
	
	override val usableParams
		get() = arrayOf(damage, duration)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = TargetingSystem.getTarget(caster)
		if (tg.target == null)
			return SpellCastResult.NOTARGET
		
		if (tg.target === caster || tg.isParty)
			return SpellCastResult.WRONGTGT
		
		if (ASJUtilities.isNotInFieldOfVision(tg.target, caster)) return SpellCastResult.NOTSEEING
		
		if (!InteractionSecurity.canHurtEntity(caster, tg.target)) return SpellCastResult.NOTALLOW
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		tg.target.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDDeathMark, duration))
		VisualEffectHandler.sendPacket(VisualEffects.DISPEL, tg.target)
		
		return result
	}
}