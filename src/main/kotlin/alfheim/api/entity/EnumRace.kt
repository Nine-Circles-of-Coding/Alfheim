package alfheim.api.entity

import alexsocol.asjlib.*
import alfheim.api.event.PlayerChangedRaceEvent
import alfheim.client.core.handler.CardinalSystemClient
import alfheim.common.core.handler.CardinalSystem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.*
import net.minecraftforge.common.MinecraftForge

enum class EnumRace {
	
	HUMAN, SALAMANDER, SYLPH, CAITSITH, POOKA, GNOME, LEPRECHAUN, SPRIGGAN, UNDINE, IMP, ALV;
	
	val rgbColor: Int
		get() = getRGBColor(ordinal)
	
	val enumColor: EnumChatFormatting
		get() = getEnumColor(ordinal)
	
	fun glColor() {
		glColor(ordinal)
	}
	
	fun glColorA(alpha: Double) {
		glColorA(ordinal, alpha)
	}
	
	fun localize() =
		StatCollector.translateToLocal("race." + toString() + ".name")!!
	
	companion object {
		
		fun getRGBColor(id: Int): Int {
			return when (getByID(id)) {
				SALAMANDER -> 0xb61f24
				SYLPH      -> 0x5ee52e
				CAITSITH   -> 0xcdb878
				POOKA      -> 0x99cb3b
				GNOME      -> 0x816b57
				LEPRECHAUN -> 0x6d6b7b
				SPRIGGAN   -> 0x282739
				UNDINE     -> 0x40c0a4
				IMP        -> 0x786a89
				ALV        -> 0xFFEE99
				else       -> 0xffffff
			}
		}
		
		fun getEnumColor(id: Int): EnumChatFormatting {
			return when (getByID(id)) {
				SALAMANDER -> EnumChatFormatting.DARK_RED
				SYLPH      -> EnumChatFormatting.GREEN
				CAITSITH   -> EnumChatFormatting.YELLOW
				POOKA      -> EnumChatFormatting.GOLD
				GNOME      -> EnumChatFormatting.DARK_GREEN
				LEPRECHAUN -> EnumChatFormatting.GRAY
				SPRIGGAN   -> EnumChatFormatting.WHITE
				UNDINE     -> EnumChatFormatting.AQUA
				IMP        -> EnumChatFormatting.LIGHT_PURPLE
				ALV        -> EnumChatFormatting.WHITE
				else       -> EnumChatFormatting.WHITE
			}
		}
		
		fun glColor(id: Int) {
			glColor1u(addAlpha(getRGBColor(id), 255))
		}
		
		fun glColorA(id: Int, alpha: Double) {
			glColor1u(addAlpha(getRGBColor(id), (alpha * 255).I))
		}
		
		private fun addAlpha(color: Int, alpha: Int) =
			alpha and 0xFF shl 24 or (color and 0x00FFFFFF)
		
		private fun glColor1u(color: Int) {
			org.lwjgl.opengl.GL11.glColor4ub((color shr 16 and 0xFF).toByte(), (color shr 8 and 0xFF).toByte(), (color and 0xFF).toByte(), (color shr 24 and 0xFF).toByte())
		}
		
		private fun getByID(id: Int) = if (0 > id || id > values().size) HUMAN else values()[id]
		
		operator fun get(id: Int) = getByID(id)
		
		operator fun get(player: EntityPlayer): EnumRace {
			val id = if (ASJUtilities.isServer)
				CardinalSystem.forPlayer(player).raceID
			else
				CardinalSystemClient.playerRaceIDs[player.commandSenderName] ?: 0
			
			return getByID(id)
		}
		
		fun getRaceID(player: EntityPlayer): Int {
			return if (ASJUtilities.isServer)
				CardinalSystem.forPlayer(player).raceID
			else
				CardinalSystemClient.playerRaceIDs[player.commandSenderName] ?: 0
		}
		
		operator fun set(player: EntityPlayer, race: EnumRace) {
			if (ASJUtilities.isServer)
				CardinalSystem.forPlayer(player).raceID = race.ordinal
			else
				CardinalSystemClient.playerRaceIDs[player.commandSenderName] = race.ordinal
			
			MinecraftForge.EVENT_BUS.post(PlayerChangedRaceEvent(player, player.race, race))
		}
		
		internal fun setRaceID(player: EntityPlayer, raceID: Int) {
			if (ASJUtilities.isServer)
				CardinalSystem.forPlayer(player).raceID = raceID
			else
				CardinalSystemClient.playerRaceIDs[player.commandSenderName] = raceID
		}
	}
}

var EntityPlayer.race
	get() = EnumRace[this]
	set(value) {
		EnumRace[this] = value
	}

/**
 * Internal Alfheim value, please, don't set it unless you know what you are doing
 * <br>
 * and fire [an event][alfheim.api.event.PlayerChangedRaceEvent] if needed
 */
var EntityPlayer.raceID
	get() = EnumRace.getRaceID(this)
	internal set(value) {
		EnumRace.setRaceID(this, value)
	}