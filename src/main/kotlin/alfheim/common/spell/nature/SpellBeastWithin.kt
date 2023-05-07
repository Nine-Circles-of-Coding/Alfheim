package alfheim.common.spell.nature

import alexsocol.asjlib.*
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

object SpellBeastWithin: SpellBase("beastwithin", EnumRace.CAITSITH, 10000, 1200, 25) {
	
	override var damage = 100f
	override var duration = 200
	
	override val usableParams = arrayOf(damage, duration, efficiency)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = CardinalSystem.TargetingSystem.getTarget(caster)
		val tgt = tg.target ?: return SpellCastResult.NOTARGET
		if (!tg.isParty) return SpellCastResult.WRONGTGT
		if (tgt !== caster && ASJUtilities.isNotInFieldOfVision(tgt, caster)) return SpellCastResult.NOTSEEING
		
		val result = checkCast(caster)
		if (result == SpellCastResult.OK)
			tgt.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDBeastWithin, duration, efficiency.I))
		return result
	}
}