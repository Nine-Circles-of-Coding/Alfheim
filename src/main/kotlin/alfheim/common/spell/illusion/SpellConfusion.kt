package alfheim.common.spell.illusion

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import alfheim.common.core.handler.VisualEffectHandler
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.*

object SpellConfusion: SpellBase("confusion", EnumRace.SPRIGGAN, 4000, 1200, 15) {
	
	override var duration = 600
	
	override val usableParams: Array<Any>
		get() = arrayOf(duration)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = TargetingSystem.getTarget(caster)
		val tgt = tg.target ?: return SpellCastResult.NOTARGET
		
		if (tgt === caster || tg.isParty) return SpellCastResult.WRONGTGT
		
		if (ASJUtilities.isNotInFieldOfVision(tgt, caster)) return SpellCastResult.NOTSEEING
		
		if (!InteractionSecurity.canHurtEntity(caster, tgt)) return SpellCastResult.NOTALLOW
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		tgt.addPotionEffect(PotionEffect(Potion.confusion.id, duration))
		VisualEffectHandler.sendPacket(VisualEffects.DISPEL, tgt)
		
		return result
	}
}