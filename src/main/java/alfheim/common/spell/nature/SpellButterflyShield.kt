package alfheim.common.spell.nature

import alexsocol.asjlib.ASJUtilities
import alfheim.AlfheimCore
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import alfheim.common.core.registry.AlfheimRegistry
import alfheim.common.network.MessageEffect
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.PotionEffect

class SpellButterflyShield: SpellBase("butterflyshield", EnumRace.CAITSITH, 8000, 12000, 30) {
	
	override fun performCast(caster: EntityLivingBase): SpellBase.SpellCastResult {
		if (caster !is EntityPlayer) return SpellBase.SpellCastResult.NOTARGET // TODO add targets for mobs
		
		val tg = TargetingSystem.getTarget(caster)
		if (tg == null || tg.target == null)
			return SpellBase.SpellCastResult.NOTARGET
		
		if (!tg.isParty)
			return SpellBase.SpellCastResult.WRONGTGT
		
		if (tg.target !== caster && ASJUtilities.isNotInFieldOfVision(tg.target, caster)) return SpellBase.SpellCastResult.NOTSEEING
		
		val result = checkCast(caster)
		if (result == SpellBase.SpellCastResult.OK) {
			tg.target.addPotionEffect(PotionEffect(AlfheimRegistry.butterShield.id, 6000, 3, true))
			AlfheimCore.network.sendToAll(MessageEffect(tg.target.entityId, AlfheimRegistry.butterShield.id, 6000, 3))
		}
		
		return result
	}
}