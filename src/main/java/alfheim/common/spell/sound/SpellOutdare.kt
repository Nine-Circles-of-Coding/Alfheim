package alfheim.common.spell.sound

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.entity.boss.EntityFlugel
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import kotlin.collections.set

object SpellOutdare: SpellBase("outdare", EnumRace.POOKA, 6000, 2400, 20) {
	
	override val usableParams
		get() = arrayOf<Any>(radius)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val l = getEntitiesWithinAABB(caster.worldObj, EntityLiving::class.java, caster.boundingBox(radius))
		l.remove(caster)
		if (l.isEmpty()) return SpellCastResult.NOTARGET
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		l.forEach {
			if (Vector3.entityDistance(caster, it) >= radius) return@forEach
			if (!InteractionSecurity.canInteractWithEntity(caster, it)) return@forEach
			
			if (it is EntityFlugel) {
				if (caster is EntityPlayer) it.playersDamage[caster.commandSenderName] = it.playersDamage.getOrDefault(caster.commandSenderName, 0f) + 1000f
				
				return@forEach
			}
			
			it.attackTarget = caster
			it.setLastAttacker(caster)
			it.setRevengeTarget(caster)
		}
		
		return result
	}
}