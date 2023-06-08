package alfheim.common.potion

import alexsocol.asjlib.*
import alfheim.api.event.SpellCastEvent
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.*
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.ai.attributes.BaseAttributeMap
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.Potion
import net.minecraftforge.event.entity.EntityStruckByLightningEvent

object PotionQuadDamage: PotionAlfheim(AlfheimConfigHandler.potionIDQuadDamage, "quadDamage", false, 0x22FFFF) {
	
	override fun applyAttributesModifiersToEntity(target: EntityLivingBase?, attributes: BaseAttributeMap, ampl: Int) {
		super.applyAttributesModifiersToEntity(target, attributes, ampl)
		if (AlfheimConfigHandler.enableMMO) VisualEffectHandler.sendPacket(VisualEffects.QUAD, target!!)
	}
	
	override fun isReady(dur: Int, amp: Int) = dur <= 100 && dur % 20 == 0
	
	override fun performEffect(target: EntityLivingBase, amp: Int) = VisualEffectHandler.sendPacket(VisualEffects.QUADH, target)
	
	@SubscribeEvent
	fun handleQuadDamageSequence(e: SpellCastEvent.Post) {
		if (e.caster !is EntityPlayer) return
		val player = e.caster
		val seg = CardinalSystem.forPlayer(player)
		
		when (seg.quadStage) {
			0    -> {
				if (e.spell.name == "stoneskin") {
					++seg.quadStage
				} else if (e.spell.name == "uphealth" && player.isPotionActive(AlfheimConfigHandler.potionIDStoneSkin)) {
					seg.quadStage += 2
				} else {
					seg.quadStage = 0
				}
			}
			
			1    -> if (e.spell.name == "uphealth" && player.isPotionActive(AlfheimConfigHandler.potionIDStoneSkin)) {
				++seg.quadStage
			} else {
				seg.quadStage = 0
			}
			
			2    -> if (e.spell.name == "icelens" && player.isPotionActive(AlfheimConfigHandler.potionIDStoneSkin) && player.getActivePotionEffect(Potion.field_76434_w.id)?.amplifier == 1) {
				++seg.quadStage
			} else {
				seg.quadStage = 0
			}
			
			3    -> if (e.spell.name == "battlehorn" && player.isPotionActive(AlfheimConfigHandler.potionIDStoneSkin) && player.getActivePotionEffect(Potion.field_76434_w.id)?.amplifier == 1 && player.isPotionActive(AlfheimConfigHandler.potionIDIceLens)) {
				++seg.quadStage
			} else {
				seg.quadStage = 0
			}
			
			4    -> if (e.spell.name == "thor" && player.isPotionActive(AlfheimConfigHandler.potionIDStoneSkin) && player.getActivePotionEffect(Potion.field_76434_w.id)?.amplifier == 1 && player.isPotionActive(AlfheimConfigHandler.potionIDIceLens)) {
				++seg.quadStage
			} else {
				seg.quadStage = 0
			}
			
			else -> {
				seg.quadStage = 0
			}
		}
	}
	
	@SubscribeEvent
	fun addQuadDamageEffect(e: EntityStruckByLightningEvent) {
		if (!AlfheimConfigHandler.enableMMO) return
		if (e.entity !is EntityPlayer) return
		val player = e.entity as EntityPlayer
		val seg = CardinalSystem.forPlayer(player)
		
		if (seg.quadStage < 5 || !player.isPotionActive(AlfheimConfigHandler.potionIDStoneSkin) || player.getActivePotionEffect(Potion.field_76434_w.id)?.amplifier != 1 || !player.isPotionActive(AlfheimConfigHandler.potionIDIceLens)) return
		seg.quadStage = 0
		player.removePotionEffect(AlfheimConfigHandler.potionIDStoneSkin)
		player.removePotionEffect(Potion.field_76434_w.id)
		player.removePotionEffect(AlfheimConfigHandler.potionIDIceLens)
		player.removePotionEffect(Potion.damageBoost.id)
		player.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDQuadDamage, 600, 24))
		e.isCanceled = true
	}
}
