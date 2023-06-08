package alfheim.common.network.packet

import alexsocol.asjlib.mc
import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.common.network.M1l

class Message1l(t: M1l, var data1: Long, var type: Int = t.ordinal) : ASJPacket(), AlfheimPacket<Message1l> {
	override fun handleClient(packet: Message1l) {
		when (M1l.values()[packet.type]) {
			M1l.SEED -> mc.theWorld.worldInfo.randomSeed = packet.data1
		}
	}
}