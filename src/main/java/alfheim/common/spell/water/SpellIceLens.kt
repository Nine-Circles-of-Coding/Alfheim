package alfheim.common.spell.water

import alexsocol.asjlib.PotionEffectU
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.MessageVisualEffect
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayerMP

object SpellIceLens: SpellBase("icelens", EnumRace.UNDINE, 6000, 1200, 30) {
	
	override var duration = 200
	
	override val usableParams: Array<Any>
		get() = arrayOf(duration)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val result = checkCast(caster)
		if (result == SpellCastResult.OK) {
			caster.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDIceLens, duration))
			
			if (caster is EntityPlayerMP) NetworkService.sendTo(MessageVisualEffect(VisualEffects.ICELENS.ordinal), caster)
		}
		return result
	}
}