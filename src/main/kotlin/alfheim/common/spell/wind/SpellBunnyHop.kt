package alfheim.common.spell.wind

import alexsocol.asjlib.*
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import alfheim.common.core.handler.VisualEffectHandler
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.Potion

object SpellBunnyHop: SpellBase("bunnyhop", EnumRace.SYLPH, 6000, 2400, 30) {
	
	override var duration = 3600
	override var efficiency = 1.0
	
	override val usableParams
		get() = arrayOf(duration, efficiency)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = TargetingSystem.getTarget(caster)
		if (tg.target == null) return SpellCastResult.NOTARGET
		
		if (!tg.isParty) return SpellCastResult.WRONGTGT
		
		if (tg.target !== caster && ASJUtilities.isNotInFieldOfVision(tg.target, caster)) return SpellCastResult.NOTSEEING
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		tg.target.addPotionEffect(PotionEffectU(Potion.jump.id, duration, efficiency.I))
		VisualEffectHandler.sendPacket(VisualEffects.HEAL, tg.target)
		
		return result
	}
}