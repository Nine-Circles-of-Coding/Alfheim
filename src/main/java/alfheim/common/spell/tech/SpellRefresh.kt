package alfheim.common.spell.tech

import alfheim.api.AlfheimAPI
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.CardinalSystem
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayerMP

object SpellRefresh: SpellBase("refresh", EnumRace.LEPRECHAUN, 256000, 75000, 100, true) {
	
	override val usableParams = emptyArray<Any>()
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val pt = CardinalSystem.PartySystem.getMobParty(caster) ?: return SpellCastResult.NOTARGET
		if (pt.count == 1 && pt[0] === caster) return SpellCastResult.NOTARGET
		
		val result = checkCast(caster)
		if (result == SpellCastResult.OK) {
			for (i in 0..pt.count) {
				val mr = pt[i]
				if (mr === caster || mr !is EntityPlayerMP) continue
				
				for (spell in AlfheimAPI.spells)
					if (spell !== this && !spell.hard)
						CardinalSystem.SpellCastingSystem.setCoolDown(mr, spell, 0)
				
				CardinalSystem.SpellCastingSystem.transfer(mr)
				CardinalSystem.save()
			}
		}
		
		return result
	}
}