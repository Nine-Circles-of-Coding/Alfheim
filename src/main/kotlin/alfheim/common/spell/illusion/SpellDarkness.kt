package alfheim.common.spell.illusion

import alexsocol.asjlib.spawn
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.SpellVisualizations
import alfheim.common.entity.spell.*
import net.minecraft.entity.EntityLivingBase

object SpellDarkness: SpellBase("darkness", EnumRace.SPRIGGAN, 256000, 75000, 100, true) {
	
	override var duration = 300
	
	override val usableParams: Array<Any>
		get() = arrayOf(duration, radius)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val result = checkCastOver(caster)
		if (result == SpellCastResult.OK) EntitySpellDarkness(caster.worldObj, caster).spawn()
		return result
	}
	
	override fun render(caster: EntityLivingBase) {
		SpellVisualizations.negateSphere(radius)
	}
}