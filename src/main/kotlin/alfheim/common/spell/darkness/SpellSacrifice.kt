package alfheim.common.spell.darkness

import alexsocol.asjlib.PotionEffectU
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.SpellVisualizations
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.entity.EntityLivingBase

object SpellSacrifice: SpellBase("sacrifice", EnumRace.IMP, 256000, 75000, 100, true) {
	
	override var damage = Float.MAX_VALUE
	override var radius = 32.0
	
	override val usableParams: Array<Any>
		get() = arrayOf(damage, radius)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		// if (!WorldGuardCommons.canDoSomethingHere(caster)) return SpellCastResult.NOTALLOW
		
		val result = checkCast(caster)
		if (result == SpellCastResult.OK)
			caster.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDSacrifice, 32))
		
		return result
	}
	
	override fun render(caster: EntityLivingBase) {
		SpellVisualizations.negateSphere(radius)
	}
}