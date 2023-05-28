package alfheim.common.network

import alexsocol.asjlib.ASJUtilities
import alfheim.api.ModInfo
import alfheim.api.network.AlfheimPacket
import alfheim.api.network.SimpleNetworkService
import alfheim.common.network.packet.*
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
import cpw.mods.fml.relauncher.Side
import kotlin.reflect.KClass

object NetworkService : SimpleNetworkService() {
	override val network: SimpleNetworkWrapper = SimpleNetworkWrapper(ModInfo.MODID)

	override fun register() {
		registry(Message0dC::class, 0, Side.CLIENT)
		registry(Message0dS::class, 1, Side.SERVER)
		registry(Message1d::class, 2, Side.CLIENT)
		registry(Message1l::class, 3, Side.CLIENT)
		registry(Message2d::class, 4, Side.CLIENT)
		registry(Message3d::class, 5, Side.CLIENT)
		registry(MessageContributor::class, 6, Side.CLIENT)
		registry(MessageContributor::class, 7, Side.SERVER)
		registry(MessageEffect::class, 8, Side.CLIENT)
		registry(MessageGleipnirLeash::class, 9, Side.CLIENT)
		registry(MessageHotSpellC::class, 10, Side.CLIENT)
		registry(MessageHotSpellS::class, 11, Side.SERVER)
		registry(MessageKeyBindS::class, 12, Side.SERVER)
		registry(MessageNI::class, 13, Side.CLIENT)
		registry(MessageNI::class, 14, Side.SERVER)
		registry(MessageParty::class, 15, Side.CLIENT)
		registry(MessageRaceInfo::class, 16, Side.CLIENT)
		registry(MessageRedstoneSignalsSync::class, 17, Side.CLIENT)
		registry(MessageSkinInfo::class, 18, Side.CLIENT)
		registry(MessageSpellParams::class, 19, Side.CLIENT)
		registry(MessageTileItem::class, 20, Side.CLIENT)
		registry(MessageTimeStop::class, 21, Side.CLIENT)
		registry(MessageVisualEffect::class, 22, Side.CLIENT)
	}

	override fun <T : AlfheimPacket<T>> registry(clazz: KClass<out T>, id: Int, side: Side) {
		try {
			network.registerMessage(clazz.java.newInstance(), clazz.java, id, side)
		} catch (e: Exception) {
			ASJUtilities.error("Can`t register packet: Class: ${clazz.qualifiedName} ID: $id Side:${side.name}")
		}
	}
}