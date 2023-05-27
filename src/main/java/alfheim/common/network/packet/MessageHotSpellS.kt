package alfheim.common.network.packet

import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.common.core.handler.CardinalSystem
import net.minecraft.entity.player.EntityPlayerMP

class MessageHotSpellS(var slot: Int, var id: Int): ASJPacket(), AlfheimPacket<MessageHotSpellS> {
	override fun handleServer(packet: MessageHotSpellS, player: EntityPlayerMP) {
		CardinalSystem.HotSpellsSystem.setHotSpellID(player, packet.slot, packet.id);
	}
}