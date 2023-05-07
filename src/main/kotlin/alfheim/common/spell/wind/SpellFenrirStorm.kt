package alfheim.common.spell.wind

import alexsocol.asjlib.spawn
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.entity.spell.EntitySpellFenrirStorm
import net.minecraft.entity.EntityLivingBase

object SpellFenrirStorm: SpellBase("fenrirstorm", EnumRace.SYLPH, 1000, 100, 5) {
	
	override var damage = 10f
	override var radius = 8.0
	
	override val usableParams
		get() = arrayOf(damage, radius)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val result = checkCastOver(caster)
		if (result == SpellCastResult.OK) EntitySpellFenrirStorm(caster.worldObj, caster).spawn()
		return result
	}
}