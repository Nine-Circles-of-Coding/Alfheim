package alfheim.common.world.mobspawn

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo.MODID
import alfheim.common.core.handler.AlfheimConfigHandler.butterflySpawn
import alfheim.common.core.handler.AlfheimConfigHandler.chickSpawn
import alfheim.common.core.handler.AlfheimConfigHandler.cowSpawn
import alfheim.common.core.handler.AlfheimConfigHandler.despawnChunks
import alfheim.common.core.handler.AlfheimConfigHandler.dimensionIDAlfheim
import alfheim.common.core.handler.AlfheimConfigHandler.elvesSpawn
import alfheim.common.core.handler.AlfheimConfigHandler.jellySpawn
import alfheim.common.core.handler.AlfheimConfigHandler.maxChunks
import alfheim.common.core.handler.AlfheimConfigHandler.minChunks
import alfheim.common.core.handler.AlfheimConfigHandler.pigSpawn
import alfheim.common.core.handler.AlfheimConfigHandler.pixieSpawn
import alfheim.common.core.handler.AlfheimConfigHandler.playerGroupDistance
import alfheim.common.core.handler.AlfheimConfigHandler.sheepSpawn
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.common.gameevent.TickEvent
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent
import net.minecraft.entity.*
import net.minecraft.entity.player.*
import net.minecraft.util.WeightedRandom
import net.minecraft.world.*
import net.minecraftforge.event.ForgeEventFactory
import java.util.*
import kotlin.collections.sumOf
import kotlin.math.*

object MobSpawnHandler {
	
	val despawnRadiusHard = maxChunks * 16 + despawnChunks * 16
	val despawnRadiusSoft = maxChunks * 8 + despawnChunks * 16
	val registeredMobs = HashMap<Int, ArrayList<MobData>>()
	val mobNames = HashMap<Int, HashSet<String>>()
	
	init {
		eventFML()
		
		registerMob("$MODID.Butterfly", butterflySpawn)
		registerMob("Chicken", chickSpawn)
		registerMob("Cow", cowSpawn)
		registerMob("$MODID.Elf", elvesSpawn)
		registerMob("$MODID.Jellyfish", jellySpawn)
		registerMob("Pig", pigSpawn)
		registerMob("$MODID.Pixie", pixieSpawn)
		registerMob("Sheep", sheepSpawn)
		registerMob("$MODID.SnowSprite", pixieSpawn.map { it * 2 }.toIntArray())
	}
	
	fun registerMob(name: String, data: IntArray, dim: Int = dimensionIDAlfheim) {
		val (m, n, b) = data
		registerMob(name, dim, m, n, b)
	}
	
	fun registerMob(name: String, dim: Int, maxCountPerPlayer: Int, minBatchSize: Int, maxBatchSize: Int) {
		registeredMobs.computeIfAbsent(dim) { ArrayList() } += MobData(name, maxCountPerPlayer, minBatchSize, maxBatchSize)
		mobNames.computeIfAbsent(dim) { HashSet() } += name
	}
	
	@SubscribeEvent
	fun worldTickEvent(e: WorldTickEvent) {
		val world = e.world
		
		if (registeredMobs.computeIfAbsent(world.provider.dimensionId) { ArrayList() }.isEmpty()) return
		
		if (e.phase == TickEvent.Phase.START)
			doDespawn(world)
		else
			doSpawning(world)
	}
	
	fun doSpawning(world: World) {
		if (world.playerEntities.isEmpty()) return
		if (!world.gameRules.getGameRuleBooleanValue("doMobSpawning")) return
		
		val rand = world.rand
		val (pcx, pcz) = selectBalancedSpawnLocation(world)
		
		val candidates = HashSet<ChunkCoordIntPair>()
		for (rcx in 0.bidiRange(maxChunks)) {
			if (abs(rcx) <= minChunks) continue
			
			for (rcz in 0.bidiRange(maxChunks)) {
				if (abs(rcz) <= minChunks) continue
				
				val candidate = ChunkCoordIntPair(pcx + rcx, pcz + rcz)
				if (candidate !in world.activeChunkSet) continue
				
				candidates += candidate
			}
		}
		
		val chunk = selectClosestChunkPrioritizedToDistant(candidates, ChunkCoordIntPair(pcx, pcz), rand) ?: return
		
		val suitablePos = Vector3()
		var canSpawn = 0
		val (name, _, min, max) = registeredMobs[world.provider.dimensionId]!!.run {
			shuffle()
			firstOrNull { data ->
				val mob = EntityList.createEntityByName(data.name, world) as? EntityLiving ?: return@firstOrNull false
				val (x, _, z) = Vector3().rand().mul(16).add(chunk.chunkXPos * 16, 0, chunk.chunkZPos * 16)
				val y = world.getTopSolidOrLiquidBlock(x.mfloor(), z.mfloor()).D
				
				mob.setPosition(x, y, z)
				if (!mob.canSpawnHere) return@firstOrNull false
				suitablePos.set(x, y, z)
				
				val mobsInWorld = world.loadedEntityList.count { EntityList.getEntityString(it as Entity) == data.name }
				val mobsInWorldMax = data.maxCountPerPlayer * world.playerEntities.size
				canSpawn = mobsInWorldMax - mobsInWorld
				canSpawn > 0
			}
		} ?: return
		
		val count = min(ASJUtilities.randInBounds(min, max, rand), canSpawn)
		
		val offset = Vector3()
		var data: IEntityLivingData? = null
		
		for (i in 0 until count) {
			val mob = EntityList.createEntityByName(name, world) as EntityLiving
			
			var retries = 10
			do {
				val (x, _, z) = offset.rand().sub(0.5).normalize().mul(Math.random() * 3 + 1).add(suitablePos)
				val y = world.getTopSolidOrLiquidBlock(x.mfloor(), z.mfloor()).D
				
				mob.setPosition(x, y, z)
			} while (!checkSpawn(mob) && retries-- > 0)
			
			val (x, y, z) = suitablePos
			if (!checkSpawn(mob)) mob.setPosition(x, y, z)
			if (!checkSpawn(mob, force = true)) continue
			
			mob.spawn(world)
			
			if (!ForgeEventFactory.doSpecialSpawn(mob, world, mob.posX.F, mob.posY.F, mob.posZ.F))
				data = mob.onSpawnWithEgg(data)
		}
	}
	
	fun checkSpawn(entity: EntityLiving, force: Boolean = false): Boolean {
		return when (ForgeEventFactory.canEntitySpawn(entity, entity.worldObj, entity.posX.F, entity.posY.F, entity.posZ.F)) {
			Event.Result.DENY    -> false
			Event.Result.ALLOW   -> true
			else                 -> force || entity.canSpawnHere
		}
	}
	
	fun selectBalancedSpawnLocation(world: World): Pair<Int, Int> {
		val playerCoords = world.playerEntities.mapTo(ArrayList()) { it as EntityPlayer
			it.chunkCoordX to it.chunkCoordZ
		}
		
		val balanced = HashSet<Pair<Int, Int>>()
		val ignore = HashSet<Pair<Int, Int>>()
		
		for (coord in playerCoords) {
			if (coord in ignore) continue
			
			val group = playerCoords.filterTo(ArrayList()) { Vector3.pointDistancePlane(it.first, it.second, coord.first, coord.second) <= playerGroupDistance }
			group += coord
			
			ignore += group
			
			val centerX = group.sumOf { it.first.D } / group.size
			val centerZ = group.sumOf { it.second.D } / group.size
			balanced += centerX.mfloor() to centerZ.mfloor()
		}
		
		return balanced.random(world.rand) ?: (0 to 0) // (0 to 0) theoretically unreachable, but it's better to be overdressed than underdressed /shrug
	}
	
	fun selectClosestChunkPrioritizedToDistant(candidates: HashSet<ChunkCoordIntPair>, center: ChunkCoordIntPair, rand: Random): ChunkCoordIntPair? {
		val list = ArrayList<WeightedRandom.Item>()
		
		for (i in minChunks..maxChunks) {
			list.add(WeightedRandom.Item(maxChunks + 1 - i))
		}
		
		val distance = WeightedRandom.getRandomItem(rand, list).itemWeight
		
		return candidates.filter { c -> max(abs(center.chunkXPos - c.chunkXPos), abs(center.chunkZPos - c.chunkZPos)) == distance }.random(rand)
	}
	
	// FUCKING PROTECTED METHODS
	val canDespawn = ASJReflectionHelper.getMethod(EntityLiving::class.java, arrayOf("canDespawn", "func_70692_ba", "v"), emptyArray())
	
	fun doDespawn(world: World) {
		val namesForWorld = mobNames.computeIfAbsent(world.provider.dimensionId) { HashSet() }
		
		world.loadedEntityList.filter { EntityList.getEntityString(it as Entity) in namesForWorld }.forEach { entity ->
			if (entity !is EntityLiving) return@forEach
			if (!ASJReflectionHelper.invoke<Nothing, Boolean>(canDespawn, entity, emptyArray())!!) return@forEach
			
			if (entity.isNoDespawnRequired) {
				entity.entityAge = 0
				return@forEach
			}
			
			if (entity.entityAge and 0x1F == 0x1F) {
				when (ForgeEventFactory.canEntityDespawn(entity)) {
					Event.Result.DENY  -> {
						entity.entityAge = 0
						return@forEach
					}
					Event.Result.ALLOW -> return@forEach entity.setDead()
					else               -> Unit
				}
			}
			
			val minDistance = world.playerEntities.minOfOrNull { player -> player as EntityPlayerMP
				min(abs(entity.posX - player.posX), abs(entity.posZ - player.posZ))
			} ?: Double.MAX_VALUE
			
			if (minDistance > despawnRadiusHard) return@forEach entity.setDead()
			
			if (minDistance > despawnRadiusSoft) {
				if (entity.entityAge > 1200 || (entity.entityAge > 600 && entity.rng.nextInt(800) == 0))
					entity.setDead()
			} else entity.entityAge = 0
		}
	}
	
	data class MobData(val name: String, val maxCountPerPlayer: Int, val minBatchSize: Int, val maxBatchSize: Int)
}