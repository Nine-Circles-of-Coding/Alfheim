package alfheim.common.spell.sound

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.CardinalSystem.PartySystem
import net.minecraft.entity.EntityLivingBase
import net.minecraft.potion.Potion

object SpellDragonGrowl: SpellBase("dragongrowl", EnumRace.POOKA, 12000, 2400, 20) {
	
	override var duration = 100
	override var efficiency = 2.0
	override var radius = 8.0
	
	override val usableParams
		get() = arrayOf(duration, efficiency, radius)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val list = getEntitiesWithinAABB(caster.worldObj, EntityLivingBase::class.java, caster.boundingBox.expand(radius))
		
		if (list.isEmpty()) return SpellCastResult.NOTARGET
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		list.forEach {
			if (PartySystem.mobsSameParty(caster, it) || Vector3.entityDistance(it, caster) > radius * 2) return@forEach
			if (!InteractionSecurity.canHurtEntity(caster, it)) return@forEach
			
			it.addPotionEffect(PotionEffectU(Potion.blindness.id, duration))
			it.addPotionEffect(PotionEffectU(Potion.moveSlowdown.id, duration, (efficiency * 2.5).I))
			it.addPotionEffect(PotionEffectU(Potion.weakness.id, duration, efficiency.I))
		}
		
		caster.worldObj.playSoundEffect(caster.posX, caster.posY, caster.posZ, "mob.enderdragon.growl", 100f, 0.8f + caster.worldObj.rand.nextFloat() * 0.2f)
		return result
	}
}