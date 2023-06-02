package alfheim.common.spell.illusion

import alexsocol.asjlib.*
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.CardinalSystem
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.MessageVisualEffect
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.*

object SpellTrueSight: SpellBase("truesight", EnumRace.SPRIGGAN, 2000, 2500, 40) {
	
	override val usableParams
		get() = emptyArray<Any>()
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		if (caster !is EntityPlayerMP) return SpellCastResult.NOTALLOW
		
		val tg = TargetingSystem.getTarget(caster)
		val tgt = tg.target ?: return SpellCastResult.NOTARGET
		
		if (tgt !is EntityPlayer || tgt === caster) return SpellCastResult.WRONGTGT
		if (ASJUtilities.isNotInFieldOfVision(tgt, caster)) return SpellCastResult.NOTSEEING
		if (!tg.isParty && !InteractionSecurity.canInteractWithEntity(caster, tgt)) return SpellCastResult.NOTALLOW
		
		val result = checkCast(caster)
		if (result == SpellCastResult.OK) {
			val mana = CardinalSystem.ManaSystem.getMana(tgt)
			NetworkService.sendTo(MessageVisualEffect(VisualEffects.MANA.ordinal, tgt.entityId.D, mana.D, 0.0), caster)
		}
		
		return result
	}
}