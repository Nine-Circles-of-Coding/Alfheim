package alfheim.common.world.dim.alfheim

import alexsocol.asjlib.*
import alfheim.client.render.world.*
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.AlfheimConfigHandler.enableAlfheimRespawn
import alfheim.common.core.handler.AlfheimConfigHandler.enableElvenStory
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.world.data.CustomWorldData.Companion.customData
import alfheim.common.world.dim.alfheim.biome.*
import alfheim.common.world.dim.alfheim.customgens.*
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.biome.BiomeGenBase
import ru.vamig.worldengine.*
import ru.vamig.worldengine.standardcustomgen.*
import vazkii.botania.common.Botania
import vazkii.botania.common.block.ModBlocks
import kotlin.math.cos

class WorldProviderAlfheim: WE_WorldProvider() {
	
	override fun calculateCelestialAngle(worldTicks: Long, partialTicks: Float): Float {
		if (RagnarokHandler.ragnarok) return 0.5f
		
		val j = (worldTicks % 24000).I
		var f1 = (j.F + partialTicks) / 24000f - 0.25f
		
		if (f1 < 0f) {
			++f1
		}
		
		if (f1 > 1f) {
			--f1
		}
		
		val f2 = f1
		f1 = 1f - ((cos(f1.D * Math.PI) + 1.0) / 2.0).F
		f1 = f2 + (f1 - f2) / 3f
		
		return f1
	}
	
	override fun genSettings(cp: WE_ChunkProvider) {
		cp.createChunkGen_List.clear()
		cp.decorateChunkGen_List.clear()
		
		WE_Biome.setBiomeMap(cp, 1.2, 6, 8000.0, 0.4)
		
		val terrainGenerator = WE_TerrainGenerator()
		terrainGenerator.worldStoneBlock = ModBlocks.livingrock
		terrainGenerator.worldSeaGenMaxY += BiomeAlfheim.offset
		cp.createChunkGen_List.add(terrainGenerator)
		
		cp.createChunkGen_List.add(YggdrasilGenerator)
		cp.createChunkGen_List.add(NiflheimLocationGenerator)
		
		val cg = WE_CaveGen()
		cg.replaceBlocksList.clear()
		cg.replaceBlocksMetaList.clear()
		cg.addReplacingBlock(ModBlocks.livingrock, 0.toByte())
		cp.createChunkGen_List.add(cg)
		val rg = WE_RavineGen()
		rg.replaceBlocksList.clear()
		rg.replaceBlocksMetaList.clear()
		rg.addReplacingBlock(ModBlocks.livingrock, 0.toByte())
		cp.createChunkGen_List.add(rg)
		
//		val snowGen = WE_SnowGen()
//		snowGen.snowPoint = 164
//		snowGen.randomSnowPoint = 8
//		cp.createChunkGen_InXZ_List.add(snowGen)
		
		val ores = WE_OreGen()
		val m = AlfheimConfigHandler.oregenMultiplier
		ores.add(AlfheimBlocks.elvenOre, ModBlocks.livingrock, 0, 1, 8, 1 * m, 2 * m, 75, 1, 16)  // Dragonstone
		ores.add(AlfheimBlocks.elvenOre, ModBlocks.livingrock, 1, 1, 8, 3 * m, 6 * m, 100, 1, 64) // Elementium
		ores.add(AlfheimBlocks.elvenOre, ModBlocks.livingrock, 2, 4, 8, 1 * m, 1 * m, 100, 1, 48) // Quartz
		ores.add(AlfheimBlocks.elvenOre, ModBlocks.livingrock, 3, 1, 8, 2 * m, 3 * m, 100, 1, 32) // Gold
		ores.add(AlfheimBlocks.elvenOre, ModBlocks.livingrock, 4, 1, 4, 1 * m, 1 * m, 50, 1, 16)  // Iffesal
		ores.add(AlfheimBlocks.elvenOre, ModBlocks.livingrock, 5, 4, 8, 1 * m, 1 * m, 100, 1, 48) // Lapis
		
		cp.decorateChunkGen_List.add(WorldGenAlfheim)
		cp.decorateChunkGen_List.add(ores)
		if (Botania.thaumcraftLoaded)
			cp.decorateChunkGen_List.add(WorldGenAlfheimThaumOre)
		
		cp.decorateChunkGen_List.add(AlfheimLakeGen())
		
		WE_Biome.addBiomeToGeneration(cp, BiomeField)
		WE_Biome.addBiomeToGeneration(cp, BiomeBeach)
		WE_Biome.addBiomeToGeneration(cp, BiomeSandbank)
		WE_Biome.addBiomeToGeneration(cp, BiomeRiver)
		WE_Biome.addBiomeToGeneration(cp, BiomeMount1)
		WE_Biome.addBiomeToGeneration(cp, BiomeMount2)
		WE_Biome.addBiomeToGeneration(cp, BiomeMount3)
		WE_Biome.addBiomeToGeneration(cp, BiomeMount3Trees)
		WE_Biome.addBiomeToGeneration(cp, BiomeMount3Field)
		WE_Biome.addBiomeToGeneration(cp, BiomeForest)
		WE_Biome.addBiomeToGeneration(cp, BiomeForest2)
	}
	
	override fun getBiomeGenForCoords(x: Int, z: Int): BiomeGenBase {
		return if (ASJUtilities.isClient) BiomeField else super.getBiomeGenForCoords(x, z)
	}
	
	override fun setSpawnPoint(x: Int, y: Int, z: Int) {
		if (ASJUtilities.isServer) worldObj.customData.spawnpoint = ChunkCoordinates(x, y, z)
	}
	
	override fun getSpawnPoint() = worldObj.customData.spawnpoint ?: if (enableElvenStory) ChunkCoordinates(0, 2, 0) else ChunkCoordinates(0, 220, -3)
	override fun getEntrancePortalLocation() = spawnPoint
	override fun getRandomizedSpawnPoint() = spawnPoint
	override fun canRespawnHere() = enableElvenStory || enableAlfheimRespawn
	override fun getSkyRenderer() = SkyRendererAlfheim
	override fun getWeatherRenderer() = WeatherRendererAlfheim
	override fun getCloudHeight() = 164f
	override fun isSurfaceWorld() = true
	override fun shouldMapSpin(entity: String?, x: Double, y: Double, z: Double) = false
	override fun getDimensionName() = "Alfheim"
	override fun getWorldHasVoidParticles() = false
}