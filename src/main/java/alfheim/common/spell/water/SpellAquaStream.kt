package alfheim.common.spell.water

import alexsocol.asjlib.spawn
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.entity.spell.EntitySpellAquaStream
import net.minecraft.entity.EntityLivingBase

object SpellAquaStream: SpellBase("aquastream", EnumRace.UNDINE, 2000, 100, 5) {
	
	override var duration = 50
	
	override val usableParams: Array<Any>
		get() = arrayOf(damage, duration, radius)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val result = checkCastOver(caster)
		if (result == SpellCastResult.OK) EntitySpellAquaStream(caster.worldObj, caster).spawn()
		return result
	}
}