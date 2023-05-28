package alfheim.common.network.packet

import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.CardinalSystemClient
import alfheim.common.core.handler.CardinalSystem
import io.netty.buffer.ByteBuf

class MessageTimeStop(var party: CardinalSystem.PartySystem.Party?, var x: Double, var y: Double, var z: Double, var id: Int): ASJPacket(), AlfheimPacket<MessageTimeStop> {
	override fun fromCustomBytes(buf: ByteBuf) {
		if (buf.readBoolean()) party = CardinalSystem.PartySystem.Party.read(buf)
	}

	override fun toCustomBytes(buf: ByteBuf) {
		buf.writeBoolean(party != null)
		if (party != null) party!!.write(buf)
	}

	override fun handleClient(packet: MessageTimeStop) {
		if (packet.party == null) packet.party = CardinalSystem.PartySystem.Party()
		CardinalSystemClient.TimeStopSystemClient.stop(packet.x, packet.y, packet.z, packet.party!!, packet.id)
	}
}