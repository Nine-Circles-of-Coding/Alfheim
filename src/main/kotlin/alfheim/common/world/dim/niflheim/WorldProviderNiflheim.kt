package alfheim.common.world.dim.niflheim

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.api.entity.INiflheimEntity
import alfheim.client.render.world.*
import alfheim.common.core.handler.*
import alfheim.common.core.handler.AlfheimConfigHandler.dimensionIDAlfheim
import alfheim.common.core.handler.AlfheimConfigHandler.dimensionIDNiflheim
import alfheim.common.core.handler.AlfheimConfigHandler.enableNiflheimRespawn
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.item.equipment.bauble.ItemPendant
import alfheim.common.world.data.CustomWorldData.Companion.customData
import alfheim.common.world.dim.niflheim.biome.*
import com.google.common.collect.HashMultimap
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.common.gameevent.TickEvent
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.*
import net.minecraft.potion.Potion
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.WorldProvider
import net.minecraft.world.biome.BiomeGenBase
import net.minecraft.world.chunk.IChunkProvider
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.event.entity.living.LivingEvent
import kotlin.math.abs

class WorldProviderNiflheim: WorldProvider() {
	
	override fun registerWorldChunkManager() {
		worldChunkMgr = ChunkManagerNiflheim(worldObj)
		dimensionId = dimensionIDNiflheim
	}
	
	override fun createChunkGenerator(): IChunkProvider {
		return ChunkProviderNiflheim(worldObj, worldObj.seed, worldObj.worldInfo.isMapFeaturesEnabled)
	}
	
	override fun doesXZShowFog(x: Int, z: Int): Boolean {
		return mc.thePlayer.run { posY > 111 && !capabilities.isCreativeMode }
	}
	
	override fun calcSunriseSunsetColors(sunAngle: Float, patialTicks: Float): FloatArray? {
		return if (mc.thePlayer.posY > 111)
			super.calcSunriseSunsetColors(sunAngle, patialTicks)
		else
			null
	}
	
	override fun generateLightBrightnessTable() {
		val f = 0.05f
		
		for (i in 0..15) {
			val f1 = 1f - i.F / 15f
			lightBrightnessTable[i] = (1f - f1) / (f1 * 3f + 1f) * (1f - f) + f
		}
	}
	
	override fun canBlockFreeze(x: Int, y: Int, z: Int, byWater: Boolean): Boolean {
		val f = ChunkProviderNiflheim.f(x)
		if (f in z.bidiRange(6)) {
			val deltaZ = 6 - abs(z - f)
			
			val deltaY = abs(127 - y) / 1.5
			if (deltaY <= deltaZ) return false
		}
		
		return super.canBlockFreeze(x, y, z, byWater)
	}
	
	override fun setSpawnPoint(x: Int, y: Int, z: Int) {
		if (ASJUtilities.isServer) worldObj.customData.spawnpoint = ChunkCoordinates(x, y, z)
	}
	override fun getSpawnPoint() = worldObj.customData.spawnpoint ?: ChunkCoordinates(0, 128, 16)
	override fun drawClouds(partialTicks: Float) = Vector3(0.001).toVec3() // ДРАВ ХУЯВ БЛЯТЬ ЭТО ПОЛУЧЕНИЕ ЦВЕТА А НЕ ОТРИСОВКА ТУПЫЕ ПИДАРАСЫ НА ФОРГАХ
	override fun getFogColor(sunAngle: Float, ticks: Float) = if (mc.thePlayer.capabilities.isCreativeMode) Vector3(1).toVec3() else Vector3(0.001).toVec3()
	override fun calculateCelestialAngle(ticks: Long, partial: Float) = 0.5f
	override fun getEntrancePortalLocation() = spawnPoint
	override fun getRandomizedSpawnPoint() = spawnPoint
	override fun shouldMapSpin(entity: String?, x: Double, y: Double, z: Double) = false
	override fun canCoordinateBeSpawn(x: Int, z: Int) = true
	override fun canRespawnHere() = enableNiflheimRespawn
	override fun getRespawnDimension(player: EntityPlayerMP?) = if (enableNiflheimRespawn) dimensionIDNiflheim else dimensionIDAlfheim
	override fun getCloudRenderer(): IRenderHandler? = null
	override fun getSkyRenderer() = DummyRenderHandler
	override fun getWeatherRenderer() = if (mc.thePlayer?.capabilities?.isCreativeMode == true) DummyRenderHandler else WeatherRendererNiflheim
	override fun getActualHeight() = super.getHeight()
	override fun isDaytime() = false
	override fun isBlockHighHumidity(x: Int, y: Int, z: Int) = false
	override fun getStarBrightness(par1: Float) = 0f
	override fun getSunBrightness(par1: Float) = 0f
	override fun getSunBrightnessFactor(par1: Float) = 0f
	override fun getCurrentMoonPhaseFactor() = 0f // ЧТО ЭТО ЗА ГОВНО ВООБЩЕ БЛЯТЬ И КАКОГО ХУЯ ОТ НЕГО ЗАВИСИТ ЛУТ ИЛИ ЧТО А А А
	override fun getMoonPhase(ticks: Long) = 4
	override fun getAverageGroundLevel() = 128
	override fun getWorldHasVoidParticles() = false
	override fun getHorizon() = if (mc.thePlayer.posY > 111) 126.0 else 189.0
	override fun getCloudHeight() = if (mc.thePlayer.run { posY > 111 && !capabilities.isCreativeMode }) 128f else -Float.MAX_VALUE
	override fun getDimensionName() = "Niflheim"
	override fun getBiomeGenForCoords(x: Int, z: Int): BiomeGenBase = super.getBiomeGenForCoords(x, z) as? BiomeNiflheim ?: BiomeGenIce // fuck server cores
	
	companion object {
		
		private val mists: HashMultimap<String, Mist> = HashMultimap.create()
		
		init {
			eventForge()
			eventFML()
		}
		
		@SubscribeEvent
		fun onWorldTick(e: TickEvent.WorldTickEvent) {
			if (e.world.provider.dimensionId != dimensionIDNiflheim) return
		
			e.world.worldInfo.isRaining = true
			e.world.rainingStrength = 1f
		
			e.world.worldInfo.isThundering = false
			e.world.thunderingStrength = 0f
		
			val iter = mists.values().iterator()
		
			for (mist in iter) {
				// current pos
				val (cx, cy, cz) = mist.origin.copy().add(mist.motion.copy().mul(mist.speed))
				
				val list = getEntitiesWithinAABB(e.world, EntityLivingBase::class.java, getBoundingBox(cx, cy, cz).expand(10.0, 6.0, 10.0))
				list.removeAll { it is INiflheimEntity }
				list.removeAll { it is EntityPlayer && (it.capabilities.isCreativeMode || ItemPendant.canProtect(it, ItemPendant.Companion.EnumPrimalWorldType.NIFLHEIM, 300)) }
				list.forEach {
					it.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDIceLens, 50))
					it.addPotionEffect(PotionEffectU(Potion.moveSlowdown.id, 50, 4))
					
					it.entityData.setBoolean(TAG_IN_MIST, true)
					
					if (ASJUtilities.chance(5))
						it.attackEntityFrom(DamageSourceSpell.nifleice, 1f)
				}
				
				VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.MIST, dimensionIDNiflheim, cx, cy, cz)
				
				val host = MinecraftServer.getServer().configurationManager.func_152612_a(mist.target)
				if (host == null) {
					iter.remove()
					continue
				}
				
				// host pos
				val (hx, _, hz) = Vector3.fromEntity(host)
				val far = Vector3.pointDistancePlane(cx, cz, hx, hz) > 96
				val old = ++mist.life > mist.maxLife
				
				if (far || old) iter.remove()
			}
		}
	
		@SubscribeEvent
		fun onLivingUpdate(e: LivingEvent.LivingUpdateEvent) {
			val player = e.entity as? EntityPlayer ?: return
			if (player.worldObj.provider.dimensionId != dimensionIDNiflheim) return
			
			val pos = Vector3.fromEntity(player)
			
			val name = player.commandSenderName
			for (i in 0 until (1 - mists[name].size)) {
				val origin = pos.mul(1, 0, 1).add(Vector3().rand().sub(0.5).mul(1, 0, 1).normalize().mul(ASJUtilities.randInBounds(16, 64)))

				if (player.rng.nextBoolean())
					origin.y = ASJUtilities.randInBounds(35, 100).D
				else
					origin.y = 35.0

				mists[name].add(Mist(origin, Vector3().rand().sub(0.5).mul(1, 0, 1).normalize(), name))
			}
		}
		
		@SubscribeEvent(priority = EventPriority.HIGH)
		fun freezeEntities(e: SheerColdHandler.SheerColdTickEvent) {
			val entity = e.entityLiving
			if (entity.worldObj.provider.dimensionId != dimensionIDNiflheim) return
			
			val (x, y, z) = Vector3.fromEntity(entity).mf()
			
			var cold = 0.05f
			if (entity.worldObj.getPrecipitationHeight(x, z) >= y) { // if in snow
				if (y >= 128) // if above ground
					cold += 0.0375f
				
				cold += 0.0375f
			}
			
			if (entity.isWet)
				cold += 0.0375f
			
			if (entity.entityData.getBoolean(TAG_IN_MIST)) {
				entity.entityData.removeTag(TAG_IN_MIST)
				cold += 0.0375f
			}
			
			e.delta = (e.delta ?: 0f) + cold
		}
		
		const val TAG_IN_MIST = "${ModInfo.MODID}.inmist"
	}
	
	private class Mist(val origin: Vector3, val motion: Vector3, val target: String) {
		var life = 0
		val maxLife = ASJUtilities.randInBounds(1200, 6000)
		val slowdownFactor = ASJUtilities.randInBounds(25, 75)
		val speed get() = life / slowdownFactor
	}
}

