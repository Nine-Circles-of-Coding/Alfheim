package alfheim.common.spell.sound

import alexsocol.asjlib.spawn
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.entity.spell.EntitySpellNoteshot
import net.minecraft.entity.EntityLivingBase

object SpellNoteshot: SpellBase("noteshot", EnumRace.POOKA, 1000, 50, 10) {
	
	override var damage = 2f
	
	override val usableParams = arrayOf<Any>(damage)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val result = checkCastOver(caster)
		if (result == SpellCastResult.OK) EntitySpellNoteshot(caster.worldObj, caster).spawn()
		return result
	}
}