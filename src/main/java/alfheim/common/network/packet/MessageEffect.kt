package alfheim.common.network.packet

import alexsocol.asjlib.getActivePotionEffect
import alexsocol.asjlib.mc
import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect

/**
 * @param state 1 - add, 0 - update, -1 - remove
 */
class MessageEffect @JvmOverloads constructor(var entity: Int, var id: Int, var dur: Int, var amp: Int, var readd: Boolean = false, var state: Byte = 1): ASJPacket(), AlfheimPacket<MessageEffect> {
	constructor(e: Entity, p: PotionEffect): this(e.entityId, p.potionID, p.duration, p.amplifier)

	override fun handleClient(packet: MessageEffect) {
		val e = mc.theWorld.getEntityByID(packet.entity)

		if (e !is EntityLivingBase) return

		val pe = e.getActivePotionEffect(packet.id)

		when (packet.state.toInt()) {
			1 -> {
				if (pe == null) {
					e.addPotionEffect(PotionEffect(packet.id, packet.dur, packet.amp))
					Potion.potionTypes[packet.id].applyAttributesModifiersToEntity(e, e.getAttributeMap(), packet.amp)
				} else {
					if (packet.readd) Potion.potionTypes[packet.id].removeAttributesModifiersFromEntity(e, e.getAttributeMap(), packet.amp)
					pe.amplifier = packet.amp
					pe.duration = packet.dur
					if (packet.readd) Potion.potionTypes[packet.id].applyAttributesModifiersToEntity(e, e.getAttributeMap(), packet.amp)
				}
			}

			0 -> {
				if (pe == null) {
					e.addPotionEffect(PotionEffect(packet.id, packet.dur, packet.amp))
					Potion.potionTypes[packet.id].applyAttributesModifiersToEntity(e, e.getAttributeMap(), packet.amp)
				} else {
					if (packet.readd) Potion.potionTypes[packet.id].removeAttributesModifiersFromEntity(e, e.getAttributeMap(), packet.amp)
					pe.amplifier = packet.amp
					pe.duration = packet.dur
					if (packet.readd) Potion.potionTypes[packet.id].applyAttributesModifiersToEntity(e, e.getAttributeMap(), packet.amp)
				}
			}

			-1 -> {
				if (pe != null) {
					e.removePotionEffect(packet.id)
					Potion.potionTypes[packet.id].removeAttributesModifiersFromEntity(e, e.getAttributeMap(), packet.amp)
				}
			}
		}
	}
}