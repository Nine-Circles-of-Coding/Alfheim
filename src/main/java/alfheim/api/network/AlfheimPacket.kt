package alfheim.api.network

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import net.minecraft.entity.player.EntityPlayerMP

interface AlfheimPacket<T : AlfheimPacket<T>> : IMessage, IMessageHandler<T, T> {
	override fun onMessage(packet: T, ctx: MessageContext): T? {
		if (ctx.side.isClient)
			handleClient(packet)
		else
			handleServer(packet, ctx.serverHandler.playerEntity)
		return null
	}

	fun handleClient(packet: T) = Unit
	fun handleServer(packet: T, player: EntityPlayerMP) = Unit
}