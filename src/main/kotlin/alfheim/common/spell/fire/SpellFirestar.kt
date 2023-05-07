package alfheim.common.spell.fire

import alexsocol.asjlib.spawn
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.entity.spell.EntitySpellFirestar
import net.minecraft.entity.EntityLivingBase

object SpellFirestar: SpellBase("firestar", EnumRace.SALAMANDER, 6000, 2400, 40) {
	
	override var damage = 1f
	override var duration = 300
	override var efficiency = 0.025
	override var radius = 8.0
	
	override val usableParams = arrayOf(damage, duration, efficiency, radius)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val result = checkCastOver(caster)
		if (result == SpellCastResult.OK) EntitySpellFirestar(caster.worldObj, caster).spawn()
		return result
	}
}