package alfheim.common.network.packet

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.mc
import alexsocol.asjlib.network.ASJPacket
import alfheim.api.AlfheimAPI
import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.KeyBindingHandlerClient
import alfheim.common.network.M0dc

class Message0dC(ty: M0dc, var type: Int = ty.ordinal) : ASJPacket(), AlfheimPacket<Message0dC> {
	override fun handleClient(packet: Message0dC) {
		when (M0dc.values()[packet.type]) {
			M0dc.MTSPELL -> {
				val spell = AlfheimAPI.getSpellByIDs(KeyBindingHandlerClient.raceID, KeyBindingHandlerClient.spellID) ?: return
				ASJUtilities.say(mc.thePlayer, "spell.$spell.mtinfo", *spell.usableParams)
			}
		}
	}
}