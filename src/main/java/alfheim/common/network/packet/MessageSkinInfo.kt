package alfheim.common.network.packet

import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.CardinalSystemClient

class MessageSkinInfo(var name: String, var isFemale: Boolean, var isSkinOn: Boolean): ASJPacket(), AlfheimPacket<MessageSkinInfo> {
	override fun handleClient(packet: MessageSkinInfo) {
		CardinalSystemClient.playerSkinsData[packet.name] = packet.isFemale to packet.isSkinOn
	}
}