package alfheim.common.network.packet

import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.CardinalSystemClient

class MessageRaceInfo(var name: String, var raceID: Int): ASJPacket(), AlfheimPacket<MessageRaceInfo> {
	override fun handleClient(packet: MessageRaceInfo) {
		CardinalSystemClient.playerRaceIDs[packet.name] = packet.raceID
	}
}