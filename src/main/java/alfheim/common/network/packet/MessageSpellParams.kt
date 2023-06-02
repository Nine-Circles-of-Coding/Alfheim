package alfheim.common.network.packet

import alexsocol.asjlib.network.ASJPacket
import alfheim.api.AlfheimAPI
import alfheim.api.network.AlfheimPacket

class MessageSpellParams(var name: String, var damage: Float, var duration: Int, var efficiency: Double, var radius: Double): ASJPacket(), AlfheimPacket<MessageSpellParams> {
	override fun handleClient(packet: MessageSpellParams) {
		val spell = AlfheimAPI.getSpellInstance(packet.name) ?: return
		spell.damage = packet.damage
		spell.duration = packet.duration
		spell.efficiency = packet.efficiency
		spell.radius = packet.radius
	}
}