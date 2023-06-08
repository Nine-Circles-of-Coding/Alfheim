package alfheim.common.network.packet

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.common.block.tile.TileRaceSelector
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer

class MessageRaceSelection(var doMeta: Boolean, var custom: Boolean, var female: Boolean, var give: Boolean, var meta: Int, var rot: Int, var arot: Int, var timer: Int, var x: Int, var y: Int, var z: Int, var dim: Int): ASJPacket(), AlfheimPacket<MessageRaceSelection> {
	override fun handleServer(packet: MessageRaceSelection, player: EntityPlayerMP) {
		if (Vector3.vecEntityDistance(Vector3(packet.x, packet.y, packet.z), player) > 5) return

		val world = MinecraftServer.getServer().worldServerForDimension(packet.dim) ?: return
		val tile = world.getTileEntity(packet.x, packet.y, packet.z) as? TileRaceSelector ?: return

		if (packet.doMeta) {
			world.setBlockMetadataWithNotify(packet.x, packet.y, packet.z, packet.meta, 3)

			tile.custom = packet.custom
			tile.female = packet.female
		}

		tile.activeRotation = packet.arot
		tile.rotation = packet.rot
		tile.timer = packet.timer

		if (packet.give) tile.giveRaceAndReset(player)

		ASJUtilities.dispatchTEToNearbyPlayers(tile)
	}
}