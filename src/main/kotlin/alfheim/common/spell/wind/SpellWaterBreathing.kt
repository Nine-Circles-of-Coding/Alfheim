package alfheim.common.spell.wind

import alexsocol.asjlib.PotionEffectU
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.CardinalSystem.PartySystem
import alfheim.common.core.handler.VisualEffectHandler
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.Potion

object SpellWaterBreathing: SpellBase("waterbreathing", EnumRace.SYLPH, 2000, 600, 30) {
	
	override var duration = 2400
	
	override val usableParams: Array<Any>
		get() = arrayOf(duration)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val pt = (if (caster is EntityPlayer) PartySystem.getParty(caster) else PartySystem.getMobParty(caster)) ?: return SpellCastResult.NOTARGET
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		for (i in 0 until pt.count) {
			val living = pt[i] ?: continue
			if (Vector3.entityDistance(living, caster) >= 32) continue
			
			living.addPotionEffect(PotionEffectU(Potion.waterBreathing.id, duration))
			VisualEffectHandler.sendPacket(VisualEffects.HEAL, living)
		}
		
		return result
	}
}