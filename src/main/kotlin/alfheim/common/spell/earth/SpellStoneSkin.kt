package alfheim.common.spell.earth

import alexsocol.asjlib.PotionEffectU
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.PartySystem
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

object SpellStoneSkin: SpellBase("stoneskin", EnumRace.GNOME, 3000, 600, 20) {
	
	override var duration = 6000
	
	override val usableParams: Array<Any>
		get() = arrayOf(duration)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val pt = (if (caster is EntityPlayer) PartySystem.getParty(caster) else PartySystem.getMobParty(caster))
				 ?: return SpellCastResult.NOTARGET
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		for (i in 0 until pt.count) {
			val living = pt[i] ?: continue
			if (Vector3.entityDistance(living, caster) >= 32) continue
			
			living.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDStoneSkin, duration))
			VisualEffectHandler.sendPacket(VisualEffects.HEAL, living)
		}
		
		return result
	}
}