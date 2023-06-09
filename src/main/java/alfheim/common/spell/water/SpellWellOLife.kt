package alfheim.common.spell.water

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.PartySystem
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

object SpellWellOLife: SpellBase("wellolife", EnumRace.UNDINE, 7000, 600, 30) {
	
	override var damage = 0.5f
	override var duration = 1200
	
	override val usableParams
		get() = arrayOf(damage, duration, efficiency)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val pt = (if (caster is EntityPlayer) PartySystem.getParty(caster) else PartySystem.getMobParty(caster)) ?: return SpellCastResult.NOTARGET
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		for (i in 0 until pt.count) {
			val living = pt[i] ?: continue
			if (Vector3.entityDistance(living, caster) < 32)
				living.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDWellOLife, duration, efficiency.I))
		}
		
		VisualEffectHandler.sendPacket(VisualEffects.PURE, caster)
		return result
	}
}