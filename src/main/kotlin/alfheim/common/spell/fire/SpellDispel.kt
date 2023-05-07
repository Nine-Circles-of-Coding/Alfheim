package alfheim.common.spell.fire

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.*

object SpellDispel: SpellBase("dispel", EnumRace.SALAMANDER, 1000, 600, 25) {
	
	override var duration = 300
	
	override val usableParams: Array<Any>
		get() = arrayOf(duration)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = TargetingSystem.getTarget(caster)
		val tgt = tg.target ?: return SpellCastResult.NOTARGET
		
		if (tgt !== caster && ASJUtilities.isNotInFieldOfVision(tgt, caster)) return SpellCastResult.NOTSEEING
		
		if (!tg.isParty && !InteractionSecurity.canInteractWithEntity(caster, tgt)) return SpellCastResult.NOTALLOW
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		val l = ArrayList<PotionEffect>()
		for (o in tgt.activePotionEffects) if (Potion.potionTypes[(o as PotionEffect).potionID].isBadEffect == tg.isParty) if (o.potionID != AlfheimConfigHandler.potionIDLeftFlame) l.add(o)
		
		if (l.isEmpty())
			tgt.addPotionEffect(PotionEffect(Potion.confusion.id, duration))
		else for (pe in l)
			tgt.removePotionEffect(pe.potionID)
		
		VisualEffectHandler.sendPacket(VisualEffects.DISPEL, tgt)
		
		return result
	}
}