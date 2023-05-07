package alfheim.common.spell.illusion

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.CardinalSystem.PartySystem
import alfheim.common.core.handler.VisualEffectHandler
import net.minecraft.entity.EntityLivingBase
import net.minecraft.potion.*

object SpellSmokeScreen: SpellBase("smokescreen", EnumRace.SPRIGGAN, 5000, 600, 20) {
	
	override var duration = 200
	
	override val usableParams
		get() = arrayOf(duration, radius)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		val list = getEntitiesWithinAABB(caster.worldObj, EntityLivingBase::class.java, caster.boundingBox.expand(radius))
		list.forEach {
			if (PartySystem.mobsSameParty(caster, it) || Vector3.entityDistance(it, caster) > radius) return@forEach
			if (!InteractionSecurity.canHurtEntity(caster, it)) return@forEach
			
			it.addPotionEffect(PotionEffect(Potion.blindness.id, duration))
		}
		VisualEffectHandler.sendPacket(VisualEffects.SMOKE, caster)
		return result
	}
}