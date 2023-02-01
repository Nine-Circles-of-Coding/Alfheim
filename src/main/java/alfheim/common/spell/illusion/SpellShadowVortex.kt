package alfheim.common.spell.illusion

import alexsocol.asjlib.*
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.*
import alfheim.common.core.util.DamageSourceSpell
import net.minecraft.entity.EntityLivingBase

object SpellShadowVortex: SpellBase("shadowvortex", EnumRace.SPRIGGAN, 2000, 80, 10) {
	
	override var damage = 6f
	override var efficiency = 16.0
	override var radius = 5.0
	
	override val usableParams
		get() = arrayOf(damage, efficiency, radius)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val res = checkCastOver(caster)
		if (res != SpellCastResult.OK) return res
		
		val list = getEntitiesWithinAABB(caster.worldObj, EntityLivingBase::class.java, caster.boundingBox.expand(radius, 0.0, radius))
		list.forEach {
			if (it == caster || CardinalSystem.PartySystem.mobsSameParty(caster, it) || !InteractionSecurity.canHurtEntity(caster, it)) return@forEach
			for (i in 1..50) {
				if (!it.teleportRandomly(efficiency * 2)) continue
				VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.SHADOW, it)
				it.attackEntityFrom(DamageSourceSpell.shadowSpell(caster), over(caster, damage.D))
				VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.SHADOW, it)
				break
			}
		}
		
		return res
	}
}