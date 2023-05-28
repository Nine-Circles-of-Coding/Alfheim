package alfheim.common.network.packet

import alexsocol.asjlib.F
import alexsocol.asjlib.I
import alexsocol.asjlib.mc
import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.CardinalSystemClient
import alfheim.client.core.proxy.ClientProxy
import alfheim.common.core.handler.CardinalSystem.PartySystem.Party.PartyStatus
import alfheim.common.network.M3d

class Message3d(ty: M3d, var data1: Double, var data2: Double, var data3: Double, var type: Int = ty.ordinal) : ASJPacket(), AlfheimPacket<Message3d> {
	override fun handleClient(packet: Message3d) {
		when (M3d.values()[packet.type]) {
			M3d.KEY_BIND     -> Unit

			M3d.PARTY_STATUS -> {
				when (PartyStatus.values()[packet.data1.I]) {
					PartyStatus.DEAD      -> CardinalSystemClient.PlayerSegmentClient.party?.setDead(packet.data2.I, packet.data3.I == -10)
					PartyStatus.MANA      -> CardinalSystemClient.PlayerSegmentClient.party?.setMana(packet.data2.I, packet.data3.I)
					PartyStatus.HEALTH    -> CardinalSystemClient.PlayerSegmentClient.party?.setHealth(packet.data2.I, packet.data3.F)
					PartyStatus.MAXHEALTH -> CardinalSystemClient.PlayerSegmentClient.party?.setMaxHealth(packet.data2.I, packet.data3.F)
					PartyStatus.TYPE      -> CardinalSystemClient.PlayerSegmentClient.party?.setType(packet.data2.I, packet.data3.I)
				}
			}

			M3d.WEATHER -> {
				if (mc.theWorld == null) return

				mc.theWorld.setRainStrength(if (packet.data1.I > 0) 1f else 0f)
				mc.theWorld.setThunderStrength(if (packet.data1.I > 1) 1f else 0f)

				val info = mc.theWorld.worldInfo
				info.isRaining = packet.data1.I > 0
				info.rainTime = packet.data2.I
				info.isThundering = packet.data1.I > 1
				info.thunderTime = packet.data3.I
			}

			M3d.TOGGLER      -> ClientProxy.toggelModes(packet.data1 > 0, packet.data2.I and 1 > 0, packet.data3.I and 1 > 0, packet.data2.I shr 1 and 1 > 0, packet.data3.I shr 1 and 1 > 0)
		}
	}
}