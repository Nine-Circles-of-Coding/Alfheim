package alfheim.api.network

import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.entity.player.EntityPlayerMP
import kotlin.reflect.KClass

@Suppress("unused")
abstract class SimpleNetworkService {
	protected abstract val network: SimpleNetworkWrapper

	abstract fun register()

	protected abstract fun <T : AlfheimPacket<T>> registry(clazz: KClass<out T>, id: Int, side: Side)

	fun sendTo(packet: AlfheimPacket<*>, receiver: EntityPlayerMP) = network.sendTo(packet, receiver)

	fun sendToAll(packet: AlfheimPacket<*>) = network.sendToAll(packet)

	fun sendToDim(packet: AlfheimPacket<*>, dimId: Int) = network.sendToDimension(packet, dimId)

	fun sendToAllAround(packet: AlfheimPacket<*>, targetPoint: NetworkRegistry.TargetPoint) = network.sendToAllAround(packet, targetPoint)

	@SideOnly(Side.CLIENT)
	fun sendToServer(packet: AlfheimPacket<*>) = network.sendToServer(packet)
}