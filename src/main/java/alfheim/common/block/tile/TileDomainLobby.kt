package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper.toVec3
import alfheim.api.AlfheimAPI
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.asm.hook.AlfheimHookHandler
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.*
import alfheim.common.item.material.ElvenResourcesMetas
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.World
import vazkii.botania.common.Botania
import java.awt.Color

class TileDomainLobby: TileImmobile() {
	
	var name = ""
	var countdown = 0
	var cooldown = 0
	
	init {
		if (ASJUtilities.isServer) MinecraftServer.getServer()?.worldServerForDimension(AlfheimConfigHandler.dimensionIDDomains)?.getBlock(0, 0, 0)
	}
	
	override fun updateEntity() {
		if (worldObj.provider.dimensionId != AlfheimConfigHandler.dimensionIDDomains)
			super.updateEntity()
		
		--cooldown
		if (--countdown == 0) findFreeDomain(null)
		doParticles()
	}
	
	fun onBlockActivated(player: EntityPlayer): Boolean {
		if (worldObj.isRemote) return false
		if (getPlayerBack(player)) return true
		
		val domain = AlfheimAPI.domains[name] ?: return false
		if (player.heldItem?.item == AlfheimItems.elvenResource && player.heldItem.meta == ElvenResourcesMetas.DomainKey.I) {
			findFreeDomain(player)
			return true
		}
		
		if (cooldown > 0 || countdown > 0) return false
		if (domain.isLocked(worldObj)) {
			ASJUtilities.say(player, "alfheimmisc.ragnarok.domain.locked")
			return false
		}
		
		countdown = 200
		ASJUtilities.dispatchTEToNearbyPlayers(this)
		
		return true
	}
	
	fun getPlayerBack(player: EntityPlayer): Boolean {
		if (player.dimension != AlfheimConfigHandler.dimensionIDDomains) return false
		
		player.entityData.apply {
			if (!hasKey(TAG_DOMAIN_ENTRANCE)) return@apply
			
			val (x, y, z, d) = getIntArray(TAG_DOMAIN_ENTRANCE)
			removeTag(TAG_DOMAIN_ENTRANCE)
			ASJUtilities.sendToDimensionWithoutPortal(player, d, x + 0.5, y + 0.5, z + 0.5)
			
			return true
		}
		
		val (x, y, z) = player.getBedLocation(0) ?: MinecraftServer.getServer().worldServerForDimension(0).spawnPoint ?: ChunkCoordinates(0, 64, 0)
		ASJUtilities.sendToDimensionWithoutPortal(player, 0, x + 0.5, y + 0.5, z + 0.5)
		
		return true
	}
	
	fun findFreeDomain(forcePlayer: EntityPlayer?) {
		if (worldObj.isRemote) return
		
		val center = Vector3.fromTileEntityCenter(this).add(0, -3, -7)
		val bb = getBoundingBox(center.x, center.y, center.z).expand(5, 0.5, 5)
		var players = getEntitiesWithinAABB(worldObj, EntityPlayer::class.java, bb)
		
		players.removeAll {
			val (px, _, pz) = Vector3.fromEntity(it)
			val (cx, _, cz) = center
			Vector3.pointDistancePlane(cx, cz, px, pz) > 5
		}
		
		if (forcePlayer != null) players = arrayListOf(forcePlayer)
		
		if (players.isEmpty()) return
		
		val domain = AlfheimAPI.domains[name] ?: return
		
		if (forcePlayer == null && !MinecraftServer.getServer().isSinglePlayer && players.size < domain.minPlayers) {
			players.forEach { ASJUtilities.say(it, "alfheimmisc.ragnarok.domain.notEnoughParticipants") }
			cooldown = 300
			return
		}
		
		if (forcePlayer == null && !domain.canEnter(players)) {
			cooldown = 300
			return
		}
		
		val domainWorld: World?
		
		try {
			domainWorld = MinecraftServer.getServer().worldServerForDimension(AlfheimConfigHandler.dimensionIDDomains)
		} catch (e: RuntimeException) {
			ASJUtilities.error("Cannot load world for domains dimension: ${e.message}")
			e.printStackTrace()
			return
		}
		
		val x = domain.id * AlfheimConfigHandler.domainDistance + AlfheimConfigHandler.domainStartX
		for (c in 0 until AlfheimConfigHandler.domainMaxCount) {
			val z = c * AlfheimConfigHandler.domainDistance + AlfheimConfigHandler.domainStartZ
			
			if (domainWorld.isAirBlock(x, 0, z)) {
				val (i, j, k) = domain.genOffset
				SchemaUtils.generate(domainWorld, x + i, 64 + j, z + k, domain.schema)
				// mark that domain is generated
				domainWorld.setBlock(x, 0, z, AlfheimBlocks.barrier)
			} else if (getEntitiesWithinAABB(domainWorld, EntityPlayer::class.java, domain.boundBox.copy().offset(x.D, 64.0, z.D)).isNotEmpty()) continue
			
			players.forEach {
				it.entityData.setIntArray(TAG_DOMAIN_ENTRANCE, center.mf().toIntArray() + it.dimension)
				
				AlfheimHookHandler.allowtp = true
				ASJUtilities.sendToDimensionWithoutPortal(it, AlfheimConfigHandler.dimensionIDDomains, x + 0.5, 65.0, z + 0.5)
			}
			
			domain.restart(domainWorld, x, 64, z, players)
			
			return
		}
		
		cooldown = 600
		ASJUtilities.dispatchTEToNearbyPlayers(this)
	}
	
	fun doParticles() {
		if (countdown < 0) return
		
		val center = Vector3.fromTileEntityCenter(this).add(0, -3, -7)
		val rotator = Vector3.oZ.copy().mul(5)
		
		for (c in 0 until 360) {
			val (x, y, z) = rotator.add(center)
			val (r, g, b) = Color(ItemIridescent.rainbowColor()).toVec3()
			Botania.proxy.wispFX(worldObj, x, y, z, r.F, g.F, b.F, 0.5f)
			rotator.sub(center).rotateOY(1)
		}
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		super.writeCustomNBT(nbt)
		
		nbt.setString(TAG_DOMAIN_NAME, name)
		nbt.setInteger(TAG_COOLDOWN, cooldown)
		nbt.setInteger(TAG_COUNTDOWN, countdown)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		super.readCustomNBT(nbt)
		
		name = nbt.getString(TAG_DOMAIN_NAME)
		cooldown = nbt.getInteger(TAG_COOLDOWN)
		countdown = nbt.getInteger(TAG_COUNTDOWN)
	}
	
	override fun getRenderBoundingBox() = boundingBox().expand(2, 2, 0)
	
	companion object {
		
		const val TAG_COOLDOWN = "Cooldown"
		const val TAG_COUNTDOWN = "Countdown"
		const val TAG_DOMAIN_NAME = "DomainName"
		
		const val TAG_DOMAIN_ENTRANCE = "${alfheim.api.ModInfo.MODID}.DomainEntranceCoords"
	}
}