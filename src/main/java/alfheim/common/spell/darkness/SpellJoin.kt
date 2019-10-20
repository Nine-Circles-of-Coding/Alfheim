package alfheim.common.spell.darkness

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.CardinalSystem
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

class SpellJoin: SpellBase("join", EnumRace.IMP, 10000, 1800, 30) {
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		if (caster !is EntityPlayer) return SpellCastResult.NOTARGET // TODO add targets for mobs
		
		val tg = CardinalSystem.TargetingSystem.getTarget(caster)
		val pt = CardinalSystem.PartySystem.getMobParty(caster) ?: return SpellCastResult.NOTARGET
		
		val tgt = pt[tg.partyIndex] ?: return SpellCastResult.NOTARGET
		
		if (tgt === caster || !tg.isParty) return SpellCastResult.WRONGTGT
		
		val result = checkCast(caster)
		if (result == SpellCastResult.OK) {
			val (tx, ty, tz) = Vector3.fromEntity(tgt)
			
			ASJUtilities.sendToDimensionWithoutPortal(caster, tgt.dimension, tx, ty, tz)
		}
		
		return result
	}
}