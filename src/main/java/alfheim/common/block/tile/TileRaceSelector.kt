package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import alfheim.api.ModInfo
import alfheim.api.entity.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.CardinalSystem.ElvenStoryModeSystem
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ChunkCoordinates

class TileRaceSelector: ASJTile() {
	
	fun giveRaceAndReset(player: EntityPlayer): Boolean {
		if (!AlfheimConfigHandler.enableElvenStory) return false
		if (!ModInfo.DEV && player.race != EnumRace.HUMAN) return false
		
		val race = EnumRace[rotation + 1]
		selectRace(player, race)
		
		if (ASJUtilities.isServer) {
			ElvenStoryModeSystem.setGender(player, female)
			ElvenStoryModeSystem.setCustomSkin(player, custom)

			NetworkService.sendToAll(MessageRaceInfo(player.commandSenderName, rotation + 1))
			NetworkService.sendToAll(MessageSkinInfo(player.commandSenderName, female, custom))
		}
		
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3)
		
		female = false
		custom = false
		activeRotation = 0
		rotation = 0
		
		return true
	}
	
	fun selectRace(player: EntityPlayer, race: EnumRace) {
		player.race = race
		player.capabilities.allowFlying = true
		player.sendPlayerAbilities()
		teleport(player)
	}
	
	fun teleport(player: EntityPlayer) {
		val id = player.race.ordinal - 1
		if (id !in AlfheimConfigHandler.zones.indices) return
		
		val (x, y, z) = AlfheimConfigHandler.zones[id].I
		player.setSpawnChunk(ChunkCoordinates(x, y, z), true, AlfheimConfigHandler.dimensionIDAlfheim)
		ASJUtilities.sendToDimensionWithoutPortal(player, AlfheimConfigHandler.dimensionIDAlfheim, x + 0.5, y + 0.5, z + 0.5)
	}
	
	var timer = 0
	
	var female = false
	var custom = false
	var activeRotation = 0
	var rotation = 0
		set(value) {
			//val lower = value > field
			
			field = when {
				value < 0 -> 8
				value > 8 -> 0
				else      -> value
			}
			
			//activeRotation = 20 * if (lower) -1 else 1
		}
	
	override fun updateEntity() {
		if (activeRotation != 0) if (activeRotation > 0) --activeRotation else ++activeRotation
		if (--timer == 0) {
			female = false
			custom = false
			activeRotation = 0
			rotation = 0
			
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3)
		}
		
		// remove when there will be genders
		// if (getBlockMetadata() != 1) worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 1, 3)
	}
	
	override fun getRenderBoundingBox() = getBoundingBox(xCoord - 3, yCoord, zCoord - 6, xCoord + 4, yCoord + 2, zCoord + 1)
	
	val TAG_TIMER = "timer"
	val TAG_GENDER = "gender"
	val TAG_ROTATION = "rotation"
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		super.writeCustomNBT(nbt)
		nbt.setInteger(TAG_TIMER, timer)
		nbt.setBoolean(TAG_GENDER, female)
		nbt.setInteger(TAG_ROTATION, rotation)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		super.readCustomNBT(nbt)
		timer = nbt.getInteger(TAG_TIMER)
		female = nbt.getBoolean(TAG_GENDER)
		rotation = nbt.getInteger(TAG_ROTATION)
	}
}
