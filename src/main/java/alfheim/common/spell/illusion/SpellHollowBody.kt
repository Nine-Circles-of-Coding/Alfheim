package alfheim.common.spell.illusion

import alexsocol.asjlib.*
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import alfheim.common.core.handler.VisualEffectHandler
import net.minecraft.entity.EntityLivingBase
import net.minecraft.potion.Potion

object SpellHollowBody: SpellBase("hollowbody", EnumRace.SPRIGGAN, 10000, 1200, 20) {
	
	override var duration = 3600
	
	override val usableParams: Array<Any>
		get() = arrayOf(duration)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = TargetingSystem.getTarget(caster)
		if (tg.target == null) return SpellCastResult.NOTARGET
		
		if (!tg.isParty)
			return SpellCastResult.WRONGTGT
		
		if (tg.target !== caster && ASJUtilities.isNotInFieldOfVision(tg.target, caster)) return SpellCastResult.NOTSEEING
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		tg.target.addPotionEffect(PotionEffectU(Potion.invisibility.id, duration))
		VisualEffectHandler.sendPacket(VisualEffects.PURE, tg.target)
		
		return result
	}
}