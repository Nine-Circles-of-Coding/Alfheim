package alfheim.common.network.packet

import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.CardinalSystemClient
import alfheim.common.core.handler.CardinalSystem
import io.netty.buffer.ByteBuf

class MessageParty(var party: CardinalSystem.PartySystem.Party): ASJPacket(), AlfheimPacket<MessageParty> {
	override fun fromCustomBytes(buf: ByteBuf) {
		party = CardinalSystem.PartySystem.Party.read(buf)
	}

	override fun toCustomBytes(buf: ByteBuf) {
		party.write(buf)
	}

	override fun handleClient(packet: MessageParty) {
		CardinalSystemClient.PlayerSegmentClient.party = packet.party
		CardinalSystemClient.PlayerSegmentClient.partyIndex = 0
	}
}