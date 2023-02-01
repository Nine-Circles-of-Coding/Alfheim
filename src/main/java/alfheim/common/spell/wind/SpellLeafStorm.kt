package alfheim.common.spell.wind

import alexsocol.asjlib.spawn
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.entity.spell.*
import net.minecraft.entity.EntityLivingBase

object SpellLeafStorm: SpellBase("leafstorm", EnumRace.SYLPH, 30000, 36000, 60) {
	
	override var damage = 2f
	override var duration = 200
	override var efficiency = 1.0
	override var radius = 8.0
	
	override val usableParams = arrayOf(damage, duration, efficiency, radius)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val result = checkCastOver(caster)
		if (result == SpellCastResult.OK) EntitySpellLeafStorm(caster.worldObj, caster).spawn()
		return result
	}
}