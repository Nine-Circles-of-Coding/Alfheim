package alfheim.common.network.packet

import alexsocol.asjlib.extendables.block.TileItemContainer
import alexsocol.asjlib.mc
import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import net.minecraft.item.ItemStack

class MessageTileItem(var x: Int, var y: Int, var z: Int, var s: ItemStack): ASJPacket(), AlfheimPacket<MessageTileItem> {
	override fun handleClient(packet: MessageTileItem) {
		val world = mc.theWorld
		val te = world.getTileEntity(packet.x, packet.y, packet.z)
		if (te is TileItemContainer) te.item = packet.s
	}
}