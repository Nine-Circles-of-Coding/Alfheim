package alfheim.common.spell.earth

import alexsocol.asjlib.*
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

object SpellNoclip: SpellBase("noclip", EnumRace.GNOME, 24000, 2400, 20) {
	
	override var duration = 200
	
	override val usableParams: Array<Any>
		get() = arrayOf(duration)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = TargetingSystem.getTarget(caster)
		val tgt = tg.target ?: return SpellCastResult.NOTARGET
		if (tgt is EntityPlayer && !tgt.capabilities.allowFlying) return SpellCastResult.WRONGTGT
		if (tgt !== caster && ASJUtilities.isNotInFieldOfVision(tg.target, caster)) return SpellCastResult.NOTSEEING
		
		if (!tg.isParty && !InteractionSecurity.canInteractWithEntity(caster, tgt)) return SpellCastResult.NOTALLOW
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		tg.target.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDNoclip, duration))
		VisualEffectHandler.sendPacket(VisualEffects.UPHEAL, tg.target)
		
		return result
	}
}