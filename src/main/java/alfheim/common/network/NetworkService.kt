package alfheim.common.network

import alexsocol.asjlib.ASJUtilities
import alfheim.api.ModInfo
import alfheim.api.network.AlfheimPacket
import alfheim.common.network.packet.*
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.player.EntityPlayerMP
import kotlin.reflect.KClass

object NetworkService {
	
	val network: SimpleNetworkWrapper = SimpleNetworkWrapper(ModInfo.MODID)
	
	var nextPacketID = 0
	
	fun register() {
		registerPacket(Message0dC::class, Side.CLIENT)
		registerPacket(Message1d::class, Side.CLIENT)
		registerPacket(Message1l::class, Side.CLIENT)
		registerPacket(Message2d::class, Side.CLIENT)
		registerPacket(Message3d::class, Side.CLIENT)
		registerPacket(MessageContributor::class, Side.CLIENT)
		registerPacket(MessageEffect::class, Side.CLIENT)
		registerPacket(MessageGleipnirLeash::class, Side.CLIENT)
		registerPacket(MessageHotSpellC::class, Side.CLIENT)
		registerPacket(MessageNI::class, Side.CLIENT)
		registerPacket(MessageParty::class, Side.CLIENT)
		registerPacket(MessageRaceInfo::class, Side.CLIENT)
		registerPacket(MessageRedstoneSignalsSync::class, Side.CLIENT)
		registerPacket(MessageSkinInfo::class, Side.CLIENT)
		registerPacket(MessageSpellParams::class, Side.CLIENT)
		registerPacket(MessageTileItem::class, Side.CLIENT)
		registerPacket(MessageTimeStop::class, Side.CLIENT)
		registerPacket(MessageVisualEffect::class, Side.CLIENT)
		
		registerPacket(Message0dS::class, Side.SERVER)
		registerPacket(MessageContributor::class, Side.SERVER)
		registerPacket(MessageHotSpellS::class, Side.SERVER)
		registerPacket(MessageKeyBindS::class, Side.SERVER)
		registerPacket(MessageNI::class, Side.SERVER)
	}

	private fun <T : AlfheimPacket<T>> registerPacket(clazz: KClass<out T>, side: Side) {
		val id = nextPacketID++
		
		try {
			network.registerMessage(clazz.java.newInstance(), clazz.java, id, side)
		} catch (e: Exception) {
			ASJUtilities.error("Can`t register packet: Class: ${clazz.qualifiedName} ID: $id Side:${side.name}")
		}
	}
	
	fun sendTo(packet: AlfheimPacket<*>, receiver: EntityPlayerMP) = network.sendTo(packet, receiver)
	
	fun sendToAll(packet: AlfheimPacket<*>) = network.sendToAll(packet)
	
	fun sendToDim(packet: AlfheimPacket<*>, dimId: Int) = network.sendToDimension(packet, dimId)
	
	fun sendToAllAround(packet: AlfheimPacket<*>, targetPoint: NetworkRegistry.TargetPoint) = network.sendToAllAround(packet, targetPoint)
	
	@SideOnly(Side.CLIENT)
	fun sendToServer(packet: AlfheimPacket<*>) = network.sendToServer(packet)
}