package alfheim.common.network.packet

import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.KeyBindingHandlerClient
import alfheim.common.core.handler.KeyBindingHandler
import alfheim.common.entity.EntityLolicorn
import net.minecraft.entity.player.EntityPlayerMP

class MessageKeyBindS(var action: Int, var state: Boolean, var data: Int): ASJPacket(), AlfheimPacket<MessageKeyBindS> {
	override fun handleServer(packet: MessageKeyBindS, player: EntityPlayerMP) {
		when (KeyBindingHandlerClient.KeyBindingIDs.values()[packet.action]) {
			KeyBindingHandlerClient.KeyBindingIDs.CORN    -> EntityLolicorn.call(player)
			KeyBindingHandlerClient.KeyBindingIDs.FLIGHT  -> KeyBindingHandler.enableFlight(player, packet.state)
			KeyBindingHandlerClient.KeyBindingIDs.ESMABIL -> KeyBindingHandler.toggleESMAbility(player)
			KeyBindingHandlerClient.KeyBindingIDs.CAST    -> KeyBindingHandler.cast(player, packet.state, packet.data)
			KeyBindingHandlerClient.KeyBindingIDs.UNCAST  -> KeyBindingHandler.unCast(player)
			KeyBindingHandlerClient.KeyBindingIDs.SEL     -> KeyBindingHandler.select(player, packet.state, packet.data)
			KeyBindingHandlerClient.KeyBindingIDs.SECRET  -> KeyBindingHandler.secret(player)
		}
	}
}