package alfheim.common.world.dim.domains

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper.toVec3
import alfheim.api.*
import alfheim.api.event.*
import alfheim.api.world.domain.Domain
import alfheim.client.render.world.*
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.tile.TileDomainLobby
import alfheim.common.core.handler.AlfheimConfigHandler
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent
import net.minecraft.entity.Entity
import net.minecraft.entity.boss.IBossDisplayData
import net.minecraft.entity.player.*
import net.minecraft.server.MinecraftServer
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraft.world.chunk.*
import net.minecraftforge.event.entity.living.*
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent
import net.minecraftforge.event.entity.player.*
import net.minecraftforge.event.world.BlockEvent.BreakEvent
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent
import net.minecraftforge.event.world.BlockEvent.PlaceEvent
import net.minecraftforge.event.world.ExplosionEvent
import java.awt.Color
import java.util.*

class WorldProviderDomains: WorldProvider() {
	
	init {
		worldChunkMgr = WorldChunkManagerDomains
	}
	
	override fun getRespawnDimension(player: EntityPlayerMP) = player.entityData.run {
		setBoolean(TAG_SHOULD_TELEPORT, true)
		getIntArray(TileDomainLobby.TAG_DOMAIN_ENTRANCE).getOrNull(3) ?: 0
	}
	
	override fun getFogColor(sunAngle: Float, partialTicks: Float): Vec3 {
		return Color((getDomainAtPlayer(mc.thePlayer)?.skyRenderer as? SkyRendererDomains)?.color?.toInt() ?: return Color.BLACK.toVec3(), true).toVec3()
	}
	
	override fun registerWorldChunkManager() = Unit
	override fun generateLightBrightnessTable() = lightBrightnessTable.fill(1f)
	override fun createChunkGenerator(): IChunkProvider = ChunkProviderDomains(worldObj)
	override fun canCoordinateBeSpawn(x: Int, z: Int) = false
	override fun calculateCelestialAngle(time: Long, partialTicks: Float) = 0.25f
	override fun getMoonPhase(time: Long) = 4
	override fun calcSunriseSunsetColors(sunAngle: Float, partialTicks: Float) = null
	override fun canRespawnHere() = false
	override fun getCloudHeight() = Float.MAX_VALUE
	override fun isSkyColored() = false
	override fun getAverageGroundLevel() = 0
	override fun getWorldHasVoidParticles() = false
	override fun getVoidFogYFactor() = 1.0
	override fun getMovementFactor() = 0.0
	override fun getSkyRenderer() = getDomainAtPlayer(mc.thePlayer)?.skyRenderer ?: DummyRenderHandler
	override fun getCloudRenderer() = null
	override fun getWeatherRenderer() = null
	override fun getRandomizedSpawnPoint() = ChunkCoordinates(0, 64, 0)
	override fun shouldMapSpin(entity: String?, x: Double, y: Double, z: Double) = false
	override fun isDaytime() = true
	override fun getSunBrightnessFactor(partialTicks: Float) = 1f
	override fun getSkyColor(cameraEntity: Entity?, partialTicks: Float) = Vec3(1, 1, 1)
	override fun drawClouds(partialTicks: Float) = Vec3(1, 1, 1)
	override fun getSunBrightness(partialTicks: Float) = 1f
	override fun getStarBrightness(par1: Float) = 0f
	override fun calculateInitialWeather() = Unit
	override fun updateWeather() = Unit
	override fun canBlockFreeze(x: Int, y: Int, z: Int, byWater: Boolean) = false
	override fun canSnowAt(x: Int, y: Int, z: Int, checkLight: Boolean) = false
	override fun setWorldTime(time: Long) = Unit
	override fun getSeed() = 0L
	override fun getWorldTime() = 6000L
	override fun getSpawnPoint() = ChunkCoordinates(0, 64, 0)
	override fun setSpawnPoint(x: Int, y: Int, z: Int) = Unit
	override fun canMineBlock(player: EntityPlayer?, x: Int, y: Int, z: Int) = false
	override fun isBlockHighHumidity(x: Int, y: Int, z: Int) = false
	override fun getHorizon() = -Double.MAX_VALUE
	override fun canDoLightning(chunk: Chunk?) = false
	override fun canDoRainSnowIce(chunk: Chunk?) = false
	override fun getDimensionName() = "Domains"
	
	companion object {
		
		init {
			eventForge().eventFML()
		}
		
		fun getDomainAtPlayer(player: EntityPlayer): Domain? {
			if (player.dimension != AlfheimConfigHandler.dimensionIDDomains) return null
			
			for (dom in AlfheimAPI.domains.values) {
				val x = dom.id * AlfheimConfigHandler.domainDistance + AlfheimConfigHandler.domainStartX
				val aabbxy = dom.boundBox.copy().offset(x, 64, 0)
				
				for (n in 0..AlfheimConfigHandler.domainMaxCount) {
					val z = n * AlfheimConfigHandler.domainDistance + AlfheimConfigHandler.domainStartZ
					val aabbxyz = aabbxy.copy().offset(0, 0, z)
					
					if (player.boundingBox.intersectsWith(aabbxyz)) return dom
				}
			}
			
			return null
		}
		
		fun forbid(e: Entity?) = e?.dimension == AlfheimConfigHandler.dimensionIDDomains && if (e is EntityPlayer) !e.capabilities.isCreativeMode else true
		
		@SubscribeEvent
		fun removePlayersNotOnArena(e: LivingUpdateEvent) {
			val player = e.entity as? EntityPlayer ?: return
			if (!forbid(player)) return
			if (ASJUtilities.isClient) return
			if (getDomainAtPlayer(player) != null) return
			
			player.entityData.apply {
				var d = 0
				val (x, y, z) = if (hasKey(TileDomainLobby.TAG_DOMAIN_ENTRANCE)) {
					val ints = getIntArray(TileDomainLobby.TAG_DOMAIN_ENTRANCE)
					d = ints[3]
					ints
				} else {
					Vector3(
						player.getBedLocation(d) ?:
						MinecraftServer.getServer().worldServerForDimension(d)?.spawnPoint ?:
						ChunkCoordinates(0, 64, 0)
					       ).I.toIntArray()
				}
				ASJUtilities.sendToDimensionWithoutPortal(player, d, x + 0.5, y.D, z + 0.5)
			}
		}
		
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		fun onPvP(e: LivingAttackEvent) {
			e.isCanceled = forbid(e.entity) && e.entity is EntityPlayer && e.source.entity is EntityPlayer
		}
		
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		fun onPvP(e: AttackEntityEvent) {
			e.isCanceled = forbid(e.entityPlayer) && e.target is EntityPlayer
		}
		
		@SubscribeEvent(priority = EventPriority.LOWEST)
		fun onBlockHarvest(e: HarvestDropsEvent) {
			if (!forbid(e.harvester)) return
			
			e.drops.clear()
			e.dropChance = 0f
		}
		
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		fun onBlockBreak(e: BreakEvent) {
			e.isCanceled = forbid(e.player)
		}
		
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		fun onBlockPlace(e: PlaceEvent) {
			e.isCanceled = forbid(e.player)
		}
		
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		fun onBlockMultiPlace(e: MultiPlaceEvent) {
			e.isCanceled = forbid(e.player)
		}
		
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		fun onBreakSpeed(e: PlayerEvent.BreakSpeed) {
			e.isCanceled = forbid(e.entityPlayer)
		}
		
		@SubscribeEvent(priority = EventPriority.LOWEST)
		fun onHarvestTry(e: PlayerEvent.HarvestCheck) {
			if (!e.success) return
			e.success = !forbid(e.entityPlayer)
		}
		
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		fun onInteract(e: PlayerInteractEvent) {
			if (!forbid(e.entityPlayer) || e.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return
			
			if (e.world.getBlock(e.x, e.y, e.z) === AlfheimBlocks.domainDoor) return
			
			e.isCanceled = true
		}
		
		@SubscribeEvent
		fun onSpellCast(e: SpellCastEvent.Pre) {
			if (forbid(e.caster) && (e.spell.name == "wallwarp" || e.spell.name == "noclip")) e.isCanceled = true
		}
		
		@SubscribeEvent
		fun onExplosion(e: ExplosionEvent.Detonate) {
			if (e.world.provider.dimensionId == AlfheimConfigHandler.dimensionIDDomains) e.affectedBlocks.clear()
		}
		
		@SubscribeEvent
		fun onDrops(e: LivingDropsEvent) {
			if (e.entity.worldObj.provider.dimensionId != AlfheimConfigHandler.dimensionIDDomains) return
			if (e is PlayerDropsEvent) return // not sure
			
			e.isCanceled = e.entity !is IBossDisplayData
		}
		
		@SubscribeEvent
		fun onPlayerClone(e: PlayerEvent.Clone) {
			val oldData = e.original.entityData
			e.entityPlayer.entityData.apply {
				setBoolean(TAG_SHOULD_TELEPORT, oldData.getBoolean(TAG_SHOULD_TELEPORT))
				val entrance = oldData.getIntArray(TileDomainLobby.TAG_DOMAIN_ENTRANCE)
				if (entrance.size != 4) return@apply
				setIntArray(TileDomainLobby.TAG_DOMAIN_ENTRANCE, entrance)
			}
		}
		
		@SubscribeEvent
		fun onPlayerRespawn(e: PlayerRespawnEvent) {
			val nbt = e.player.entityData
			if (!nbt.getBoolean(TAG_SHOULD_TELEPORT)) return
			
			nbt.setBoolean(TAG_SHOULD_TELEPORT, false)
			val (x, y, z, d) = if (nbt.hasKey(TileDomainLobby.TAG_DOMAIN_ENTRANCE)) nbt.getIntArray(TileDomainLobby.TAG_DOMAIN_ENTRANCE) else return
			
			ASJUtilities.sendToDimensionWithoutPortal(e.player, d, x + 0.5, y + 0.5, z + 0.5)
		}
		
		const val TAG_SHOULD_TELEPORT = "${ModInfo.MODID}.shouldTeleport"
	}
}
