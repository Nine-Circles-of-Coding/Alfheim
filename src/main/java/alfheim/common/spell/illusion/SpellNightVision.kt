package alfheim.common.spell.illusion

import alexsocol.asjlib.math.Vector3
import alfheim.AlfheimCore
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.SpellEffectHandlerClient.Spells
import alfheim.common.core.handler.CardinalSystem.PartySystem
import alfheim.common.core.handler.SpellEffectHandler
import alfheim.common.network.MessageEffect
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.*

class SpellNightVision: SpellBase("nightvision", EnumRace.SPRIGGAN, 6000, 1200, 30) {
	
	override fun performCast(caster: EntityLivingBase): SpellBase.SpellCastResult {
		val pt = (if (caster is EntityPlayer) PartySystem.getParty(caster) else PartySystem.getMobParty(caster))
				 ?: return SpellBase.SpellCastResult.NOTARGET
		
		val result = checkCast(caster)
		if (result != SpellBase.SpellCastResult.OK) return result
		
		for (i in 0 until pt.count) {
			val living = pt.get(i)
			if (living != null && Vector3.entityDistance(living!!, caster) < 32) {
				living!!.addPotionEffect(PotionEffect(Potion.nightVision.id, 36000, -1, true))
				AlfheimCore.network.sendToAll(MessageEffect(living!!.getEntityId(), Potion.nightVision.id, 36000, -1))
				SpellEffectHandler.sendPacket(Spells.NVISION, living!!)
			}
		}
		
		return result
	}
}