package alfheim.common.world.dim.alfheim.customgens

import alexsocol.asjlib.*
import alfheim.api.*
import alfheim.api.block.tile.SubTileAnomalyBase
import alfheim.api.block.tile.SubTileAnomalyBase.EnumAnomalyRarity
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.tile.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.world.dim.alfheim.structure.*
import cpw.mods.fml.common.IWorldGenerator
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import java.util.*

object WorldGenAlfheim: IWorldGenerator {
	
	val common = ArrayList<String>()
	val epic = ArrayList<String>()
	val rare = ArrayList<String>()
	
	init {
		for (name in AlfheimAPI.anomalies.keys)
			when (AlfheimAPI.getAnomaly(name).rarity) {
				EnumAnomalyRarity.COMMON -> common.add(name)
				EnumAnomalyRarity.EPIC   -> epic.add(name)
				EnumAnomalyRarity.RARE   -> rare.add(name)
			}
	}
	
	override fun generate(rand: Random, chunkX: Int, chunkZ: Int, world: World, chunkGenerator: IChunkProvider, chunkProvider: IChunkProvider) {
		if (world.provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim)
			generateAlfheim(world, chunkX, chunkZ, rand)
	}
	
	fun generateAlfheim(world: World, chunkX: Int, chunkZ: Int, rand: Random) {
		if (chunkX == 0 && chunkZ == 0) StructureSpawnpoint.generate(world, rand)
		if (chunkX == 0 && chunkZ == -3) {
			world.setBlock(2, 226, -45, AlfheimBlocks.yggFlower)
			(world.getTileEntity(2, 226, -45) as TileYggFlower).setup()
		}
		if (chunkX == 0 && chunkZ == -6) {
			SchemaUtils.generate(world, 0, 6, -96, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/FenrirCave"))
			(world.getTileEntity(0, 8, -70) as TileDomainLobby).apply {
				lock(0, 8, -70, AlfheimConfigHandler.dimensionIDAlfheim)
				name = "Fenrir"
			}
		}
		
		StructurePortalToNiflheim.generate(world, chunkX, chunkZ)
		generateAnomalies(world, chunkX, chunkZ, rand)
	}
	
	fun generateAnomalies(world: World, chunkX: Int, chunkZ: Int, rand: Random) {
		if (AlfheimConfigHandler.anomaliesDispersion <= 0) return
		if (chunkX in -16 until 16 || chunkZ in -16 until 16) return // no anomalies near Yggdrasil
		
		if (rand.nextInt(AlfheimConfigHandler.anomaliesDispersion) != 0) return
		val chance = rand.nextInt(32) + 1
		when {
			chance == 32 -> genRandomAnomalyOfRarity(world, chunkX, chunkZ, rand, EnumAnomalyRarity.EPIC)
			chance >= 24 -> genRandomAnomalyOfRarity(world, chunkX, chunkZ, rand, EnumAnomalyRarity.RARE)
			chance >= 16 -> genRandomAnomalyOfRarity(world, chunkX, chunkZ, rand, EnumAnomalyRarity.COMMON)
		}
	}
	
	fun genRandomAnomalyOfRarity(world: World, chunkX: Int, chunkZ: Int, rand: Random, rarity: EnumAnomalyRarity) {
		val type = when (rarity) {
			EnumAnomalyRarity.COMMON -> common.random(rand)
			EnumAnomalyRarity.EPIC   -> epic.random(rand)
			EnumAnomalyRarity.RARE   -> rare.random(rand)
		} ?: return
		
		setAnomaly(world, chunkX, chunkZ, rand, type)
	}
	
	fun setAnomaly(world: World, chunkX: Int, chunkZ: Int, rand: Random, type: String) {
		val x = chunkX * 16 + rand.nextInt(16)
		val z = chunkZ * 16 + rand.nextInt(16)
		val y = world.getTopSolidOrLiquidBlock(x, z) + 1
		
		world.setBlock(x, y, z, AlfheimBlocks.anomaly)
		val te = world.getTileEntity(x, y, z) as? TileAnomaly ?: return
		
		te.lock(x, y, z, world.provider.dimensionId)
		
		val sub = SubTileAnomalyBase.forName(type) ?: return
		sub.worldGen = true
		
		te.addSubTile(sub, type)
		
		for (i in 0 until AlfheimConfigHandler.anomaliesUpdate) te.updateEntity()
		
		sub.worldGen = false
	}
}