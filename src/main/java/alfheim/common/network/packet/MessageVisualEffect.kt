package alfheim.common.network.packet

import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.client.render.world.VisualEffectHandlerClient
import io.netty.buffer.ByteBuf

class MessageVisualEffect(var type: Int, vararg var data: Double): ASJPacket(), AlfheimPacket<MessageVisualEffect> {
	override fun fromCustomBytes(buf: ByteBuf) {
		data = DoubleArray(buf.readInt()) { buf.readDouble() }
	}

	override fun toCustomBytes(buf: ByteBuf) {
		buf.writeInt(data.size)
		for (d in data) buf.writeDouble(d)
	}

	override fun handleClient(packet: MessageVisualEffect) {
		VisualEffectHandlerClient.select(VisualEffectHandlerClient.VisualEffects.values()[packet.type], packet.data)
	}
}