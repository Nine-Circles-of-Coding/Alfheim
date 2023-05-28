package alfheim.common.network.packet

import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.common.item.rod.RedstoneSignal
import alfheim.common.item.rod.RedstoneSignalHandlerClient
import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.JsonToNBT
import net.minecraft.nbt.NBTTagCompound

class MessageRedstoneSignalsSync(var signals: HashSet<RedstoneSignal>): ASJPacket(), AlfheimPacket<MessageRedstoneSignalsSync> {

	override fun fromCustomBytes(buf: ByteBuf) {
		val size = buf.readInt()
		signals = HashSet(size)

		repeat(size) { signals.add(
			RedstoneSignal.readFromNBT(
				JsonToNBT.func_150315_a(ByteBufUtils.readUTF8String(buf)) as? NBTTagCompound ?: return@repeat)) }
	}

	override fun toCustomBytes(buf: ByteBuf) {
		buf.writeInt(signals.size)

		signals.forEach { ByteBufUtils.writeUTF8String(buf, it.writeToNBT(NBTTagCompound()).toString()) }
	}

	override fun handleClient(packet: MessageRedstoneSignalsSync) {
		RedstoneSignalHandlerClient.redstoneSignals = packet.signals
	}
}