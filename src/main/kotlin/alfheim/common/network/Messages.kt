package alfheim.common.network

import alexsocol.asjlib.network.ASJPacket
import alfheim.common.core.handler.CardinalSystem.PartySystem.Party
import alfheim.common.core.handler.CardinalSystem.PartySystem.Party.Companion.read
import alfheim.common.item.rod.RedstoneSignal
import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.JsonToNBT
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.PotionEffect

class Message0dC(ty: M0dc, var type: Int = ty.ordinal): ASJPacket() {
	enum class M0dc { MTSPELL }
}

class Message0dS(ty: M0ds, var type: Int = ty.ordinal): ASJPacket() {
	enum class M0ds { DODGE, JUMP, HEIMBLINK }
}

class Message1d(ty: M1d, var data1: Double, var type: Int = ty.ordinal): ASJPacket() {
	enum class M1d { COLD, DEATH_TIMER, ELVEN_FLIGHT_MAX, ESMABIL, GINNUNGAGAP, KNOWLEDGE, LIMBO, NOSUNMOON, RAGNAROK, RLCM, TIME_STOP_REMOVE }
}

class Message1l(t: M1l, var data1: Long, var type: Int = t.ordinal): ASJPacket() {
	enum class M1l { SEED }
}

class Message2d(ty: M2d, var data1: Double, var data2: Double, var type: Int = ty.ordinal): ASJPacket() {
	enum class M2d { UUID, COOLDOWN, ATTRIBUTE, MODES, FIREBALLSYNC }
}

class Message3d(ty: M3d, var data1: Double, var data2: Double, var data3: Double, var type: Int = ty.ordinal): ASJPacket() {
	enum class M3d { PARTY_STATUS, KEY_BIND, WEATHER, TOGGLER }
}

class MessageNI(ty: Mni, vararg var intArray: Int, var type: Int = ty.ordinal): ASJPacket() {
	
	override fun fromCustomBytes(buf: ByteBuf) {
		intArray = IntArray(buf.readInt()) { buf.readInt() }
	}
	
	override fun toCustomBytes(buf: ByteBuf) {
		write(buf, intArray.size)
		for (value in intArray) write(buf, value)
	}
	
	enum class Mni { BLIZZARD, HEARTLOSS, INTERACTION, WINGS_BL }
}

class MessageContributor(var key: String = "", var value: String = key, var isRequest: Boolean = false): ASJPacket()

class MessageEffect @JvmOverloads constructor(var entity: Int, var id: Int, var dur: Int, var amp: Int, var readd: Boolean = false, /** 1 - add, 0 - update, -1 - remove */ var state: Int = 1): ASJPacket() {
	constructor(e: Entity, p: PotionEffect): this(e.entityId, p.potionID, p.duration, p.amplifier)
}

class MessageGleipnirLeash(var targetID: String, var playerName: String): ASJPacket()

class MessageHotSpellC(var ids: IntArray): ASJPacket() {
	
	override fun fromCustomBytes(buf: ByteBuf) {
		ids = IntArray(12) { buf.readInt() }
	}
	
	override fun toCustomBytes(buf: ByteBuf) {
		for (id in ids) buf.writeInt(id)
	}
}

class MessageHotSpellS(var slot: Int, var id: Int): ASJPacket()

class MessageKeyBindS(var action: Int, var state: Boolean, var data: Int): ASJPacket()

class MessageParty(var party: Party): ASJPacket() {
	
	override fun fromCustomBytes(buf: ByteBuf) {
		party = read(buf)
	}
	
	override fun toCustomBytes(buf: ByteBuf) {
		party.write(buf)
	}
}

class MessageRaceSelection(var doMeta: Boolean, var custom: Boolean, var female: Boolean, var give: Boolean, var meta: Int, var rot: Int, var arot: Int, var timer: Int, var x: Int, var y: Int, var z: Int, var dim: Int): ASJPacket()

class MessageRaceInfo(var name: String, var raceID: Int): ASJPacket()

class MessageRedstoneSignalsSync(var signals: HashSet<RedstoneSignal>): ASJPacket() {
	
	override fun fromCustomBytes(buf: ByteBuf) {
		val size = buf.readInt()
		signals = HashSet(size)
		
		repeat(size) { signals.add(RedstoneSignal.readFromNBT(JsonToNBT.func_150315_a(ByteBufUtils.readUTF8String(buf)) as? NBTTagCompound ?: return@repeat)) }
	}
	
	override fun toCustomBytes(buf: ByteBuf) {
		buf.writeInt(signals.size)
		
		signals.forEach { ByteBufUtils.writeUTF8String(buf, it.writeToNBT(NBTTagCompound()).toString()) }
	}
}

class MessageSkinInfo(var name: String, var isFemale: Boolean, var isSkinOn: Boolean): ASJPacket()

class MessageSpellParams(var name: String, var damage: Float, var duration: Int, var efficiency: Double, var radius: Double): ASJPacket()

class MessageTileItem(var x: Int, var y: Int, var z: Int, var s: ItemStack): ASJPacket()

class MessageTimeStop(var party: Party?, var x: Double, var y: Double, var z: Double, var id: Int): ASJPacket() {
	
	override fun fromCustomBytes(buf: ByteBuf) {
		if (buf.readBoolean()) party = read(buf)
	}
	
	override fun toCustomBytes(buf: ByteBuf) {
		buf.writeBoolean(party != null)
		if (party != null) party!!.write(buf)
	}
}

class MessageVisualEffect(var type: Int, vararg var data: Double): ASJPacket() {
	
	override fun fromCustomBytes(buf: ByteBuf) {
		data = DoubleArray(buf.readInt()) { buf.readDouble() }
	}
	
	override fun toCustomBytes(buf: ByteBuf) {
		buf.writeInt(data.size)
		for (d in data) buf.writeDouble(d)
	}
}