package alfheim.common.network.packet

import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.CardinalSystemClient
import io.netty.buffer.ByteBuf

class MessageHotSpellC(var ids: IntArray): ASJPacket(), AlfheimPacket<MessageHotSpellC> {
	override fun fromCustomBytes(buf: ByteBuf) {
		ids = IntArray(12) { buf.readInt() }
	}

	override fun toCustomBytes(buf: ByteBuf) {
		for (id in ids) buf.writeInt(id)
	}

	override fun handleClient(packet: MessageHotSpellC) {
		CardinalSystemClient.PlayerSegmentClient.hotSpells = packet.ids.clone()
	}
}