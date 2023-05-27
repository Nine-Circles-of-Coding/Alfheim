package alfheim.common.network.packet

import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.mc
import alexsocol.asjlib.network.ASJPacket
import alfheim.api.event.PlayerInteractAdequateEvent
import alfheim.api.network.AlfheimPacket
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.CardinalSystem
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.network.Mni
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.common.MinecraftForge
import kotlin.math.max

class MessageNI(ty: Mni, vararg var intArray: Int, var type: Int = ty.ordinal) : ASJPacket(), AlfheimPacket<MessageNI> {
	override fun fromCustomBytes(buf: ByteBuf) {
		intArray = IntArray(buf.readInt()) { buf.readInt() }
	}

	override fun toCustomBytes(buf: ByteBuf) {
		write(buf, intArray.size)
		for (value in intArray) write(buf, value)
	}

	override fun handleServer(packet: MessageNI, player: EntityPlayerMP) = when (Mni.values()[packet.type]) {
		Mni.INTERACTION -> with(packet) {
			operator fun IntArray.component6() = this[5]
			operator fun IntArray.component7() = this[6]

			val (left, type, x, y, z, side, id) = packet.intArray
			val entity = player.worldObj.getEntityByID(id)

			if (AlfheimConfigHandler.interactEventChecks) {
				val src = Vector3.fromEntity(player)
				val dst = if (y != -1) Vector3(x, y, z) else if (entity != null) Vector3.fromEntity(entity) else src
				if (Vector3.vecDistance(src, dst) > player.theItemInWorldManager.blockReachDistance + if (entity != null) max(entity.width, entity.height) else 0f)
					return@with
			}

			MinecraftForge.EVENT_BUS.post(
				if (left == 1)
					PlayerInteractAdequateEvent.LeftClick(player, PlayerInteractAdequateEvent.LeftClick.Action.values()[type], x, y, z, side, entity)
				else
					PlayerInteractAdequateEvent.RightClick(player, PlayerInteractAdequateEvent.RightClick.Action.values()[type], x, y, z, side, entity))
		}
		Mni.BLIZZARD,
		Mni.HEARTLOSS,
		Mni.WINGS_BL    -> Unit // client
	}

	override fun handleClient(packet: MessageNI) {
		when (Mni.values()[packet.type]) {
			Mni.BLIZZARD -> RagnarokHandler.blizzards.apply {
				val (id) = packet.intArray

				if (id < 0) removeAll { it.id == id }
				else if (packet.intArray.size == 5) {
					val (_, x1, z1, x2, z2) = packet.intArray
					add(RagnarokHandler.BlizzardData(x1, z1, x2, z2).apply { setId(id) })
				}
			}

			Mni.HEARTLOSS -> CardinalSystem.CommonSystem.updateLostHearts(mc.thePlayer, packet.intArray[0])
			Mni.INTERACTION -> Unit // server
			Mni.WINGS_BL -> AlfheimConfigHandler.wingsBlackList = packet.intArray
		}
	}
}