package alfheim.common.world.dim.alfheim.biome

import alfheim.AlfheimCore
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.world.dim.alfheim.customgens.*
import net.minecraft.init.Blocks
import net.minecraftforge.common.BiomeDictionary
import net.minecraftforge.common.BiomeDictionary.Type
import ru.vamig.worldengine.standardcustomgen.WE_BiomeLayer
import vazkii.botania.common.block.ModBlocks

object BiomeMount3: BiomeAlfheim() {
	
	init {
		setBiomeName("High plateau")
		
		BiomeDictionary.registerBiomeType(this, Type.MOUNTAIN, Type.FOREST, Type.PLAINS)
		
		biomeMinValueOnMap = 0.4
		biomeMaxValueOnMap = 0.7
		biomePersistence = 1.8
		biomeNumberOfOctaves = 3
		biomeScaleX = 250.0
		biomeScaleY = 2.4
		biomeSurfaceHeight = 144 + offset
		biomeInterpolateQuality = 1
		
		var standardBiomeLayers = WE_BiomeLayer()
		standardBiomeLayers.add(Blocks.dirt, 0.toByte(), ModBlocks.livingrock, 0.toByte(), -256, 0, -256, -2, true)
		standardBiomeLayers.add(if (AlfheimCore.winter && AlfheimConfigHandler.winterGrassReadyGen) AlfheimBlocks.snowGrass else Blocks.grass, 0.toByte(), Blocks.dirt, 0.toByte(), -256, 0, -256, 0, false)
		createChunkGen_InXZ_List.add(standardBiomeLayers)
		standardBiomeLayers = WE_BiomeLayer()
		standardBiomeLayers.add(Blocks.bedrock, 0.toByte(), 0, 0, 0, 0, true)
		createChunkGen_InXZ_List.add(standardBiomeLayers)
		decorateChunkGen_List.add(AlfheimLakeGen(1, 124))
		val g = WorldGenGrass(true, true, true, true, 1.2)
		decorateChunkGen_List.add(g)
	}
}