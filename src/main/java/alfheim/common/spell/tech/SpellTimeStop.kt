package alfheim.common.spell.tech

import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.SpellVisualizations
import alfheim.common.core.handler.CardinalSystem.TimeStopSystem
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.MathHelper

object SpellTimeStop: SpellBase("timestop", EnumRace.LEPRECHAUN, 256000, 75000, 100, true) {
	
	override var duration = 100
	
	override val usableParams: Array<Any>
		get() = arrayOf(duration, radius)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		if (InteractionSecurity.isInteractionBanned(caster)) return SpellCastResult.NOTALLOW
		
		val (x, y, z) = Vector3.fromEntity(caster)
		val rceil = MathHelper.ceiling_double_int(radius)
		val aoe = MathHelper.floor_double(-radius)..rceil
		for (rx in aoe)
			for (rz in aoe)
				if (Vector3.pointDistancePlane(0, 0, rx, rz) <= rceil && InteractionSecurity.isInteractionBanned(caster, x + rx, y, z + rz))
					return SpellCastResult.NOTALLOW
		
		val result = checkCast(caster)
		if (result == SpellCastResult.OK) TimeStopSystem.stop(caster)
		return result
	}
	
	override fun render(caster: EntityLivingBase) {
		SpellVisualizations.negateSphere(radius)
	}
}