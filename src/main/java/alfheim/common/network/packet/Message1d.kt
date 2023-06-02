package alfheim.common.network.packet

import alexsocol.asjlib.*
import alexsocol.asjlib.network.ASJPacket
import alfheim.api.ModInfo
import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.CardinalSystemClient
import alfheim.common.core.handler.*
import alfheim.common.core.handler.SheerColdHandler.cold
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.helper.ElvenFlightHelper
import alfheim.common.network.M1d
import net.minecraft.client.gui.ChatLine
import net.minecraft.event.ClickEvent
import net.minecraft.util.*

class Message1d(ty: M1d, var data1: Double, var type: Int = ty.ordinal) : ASJPacket(), AlfheimPacket<Message1d> {
	override fun handleClient(packet: Message1d) {
		when (M1d.values()[packet.type]) {
			M1d.COLD             -> mc.thePlayer.cold = packet.data1.F
			M1d.DEATH_TIMER      -> AlfheimConfigHandler.deathScreenAddTime = packet.data1.I
			M1d.ESMABIL          -> CardinalSystemClient.PlayerSegmentClient.esmAbility = packet.data1 != 0.0
			M1d.ELVEN_FLIGHT_MAX -> {
				AlfheimConfigHandler.flightTime = packet.data1.I
				ElvenFlightHelper.max = packet.data1
			}
			M1d.KNOWLEDGE        -> CardinalSystemClient.PlayerSegmentClient.knowledge.add("${CardinalSystem.KnowledgeSystem.Knowledge.values()[packet.data1.I]}")
			M1d.LIMBO            -> CardinalSystemClient.PlayerSegmentClient.limbo = packet.data1.I
			M1d.TIME_STOP_REMOVE -> CardinalSystemClient.TimeStopSystemClient.remove(packet.data1.I)
			M1d.RLCM    -> {
				// sorry anyone whom RUN_COMMAND chat actions may have been deleted by this :sweat_smile: but I don't really care
				for (it in mc.ingameGUI.chatGUI.chatLines) {
					it as ChatLine
					val answer = it.func_151461_a() as? ChatComponentText ?: break
					val event = answer.chatStyle.chatClickEvent ?: break

					if (event.action === ClickEvent.Action.RUN_COMMAND)
						answer.chatStyle.chatClickEvent = null
				}

				for (it in mc.ingameGUI.chatGUI.field_146253_i) {
					it as ChatLine
					val compositeLine = it.func_151461_a() as? ChatComponentText ?: break

					for (sib in compositeLine.siblings) {
						sib as IChatComponent
						val event = sib.chatStyle.chatClickEvent ?: continue

						if (event.action === ClickEvent.Action.RUN_COMMAND)
							sib.chatStyle.chatClickEvent = null
					}
				}

				while (packet.data1-- > 0)
					mc.ingameGUI.chatGUI.sentMessages.removeLastOrNull() // for safety
			}
			M1d.NOSUNMOON        -> RagnarokHandler.noSunAndMoon = packet.data1 == 1.0
			M1d.GINNUNGAGAP      -> RagnarokHandler.ginnungagap = packet.data1 == 1.0
			M1d.RAGNAROK         -> {
				if (packet.data1 == -1.0) {
					RagnarokHandler.ragnarok = false
					RagnarokHandler.finished = true
					RagnarokHandler.fogFade = 1f
					return
				}

				RagnarokHandler.ragnarok = packet.data1 < 1
				RagnarokHandler.fogFade = packet.data1.F

				if (0 < packet.data1 && packet.data1 < 1)
					mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "${ModInfo.MODID}:fenrir.howl", 50f, 0.5f, false)
			}
		}
	}
}