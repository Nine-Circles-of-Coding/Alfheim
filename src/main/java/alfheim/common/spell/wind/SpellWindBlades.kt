package alfheim.common.spell.wind

import alexsocol.asjlib.spawn
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.entity.spell.EntitySpellWindBlade
import net.minecraft.entity.EntityLivingBase

object SpellWindBlades: SpellBase("windblades", EnumRace.SYLPH, 8000, 120, 10) {
	
	override var damage = 6f
	override var duration = 50
	override var efficiency = 1.0
	
	override val usableParams
		get() = arrayOf(damage, duration, efficiency)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val result = checkCastOver(caster)
		if (result == SpellCastResult.OK) {
			EntitySpellWindBlade(caster.worldObj, caster, -1.0).spawn()
			EntitySpellWindBlade(caster.worldObj, caster).spawn()
			EntitySpellWindBlade(caster.worldObj, caster, 1.0).spawn()
		}
		return result
	}
}