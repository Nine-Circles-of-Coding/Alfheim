package alfheim.common.world.dim.alfheim.biome

import alfheim.AlfheimCore
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.world.dim.alfheim.customgens.*
import alfheim.common.world.dim.alfheim.structure.*
import net.minecraft.init.Blocks
import net.minecraftforge.common.BiomeDictionary
import net.minecraftforge.common.BiomeDictionary.Type
import ru.vamig.worldengine.standardcustomgen.*
import vazkii.botania.common.block.ModBlocks

object BiomeField: BiomeAlfheim() {
	
	init {
		setBiomeName("Field")
		
		BiomeDictionary.registerBiomeType(this, Type.PLAINS, Type.DENSE)
		
		biomeMinValueOnMap = -0.4
		biomeMaxValueOnMap = 0.82
		biomePersistence = 1.8
		biomeNumberOfOctaves = 3
		biomeScaleX = 250.0
		biomeScaleY = 2.0
		biomeSurfaceHeight = 71 + offset
		biomeInterpolateQuality = 2
		
		var standardBiomeLayers = WE_BiomeLayer()
		standardBiomeLayers.add(Blocks.dirt, 0.toByte(), ModBlocks.livingrock, 0.toByte(), -256, 0, -4, -2, true)
		standardBiomeLayers.add(if (AlfheimCore.winter && AlfheimConfigHandler.winterGrassReadyGen) AlfheimBlocks.snowGrass else Blocks.grass, 0.toByte(), Blocks.dirt, 0.toByte(), -256, 0, -256, 0, false)
		createChunkGen_InXZ_List.add(standardBiomeLayers)
		standardBiomeLayers = WE_BiomeLayer()
		standardBiomeLayers.add(Blocks.bedrock, 0.toByte(), 0, 0, 0, 0, true)
		createChunkGen_InXZ_List.add(standardBiomeLayers)
		val t = WE_StructureGen()
		t.add(StructureArena, 1000)
		t.add(StructureShrine, 5000)
		decorateChunkGen_List.add(t)
		val g = WorldGenGrass(true, true, true, true, 1.0)
		decorateChunkGen_List.add(g)
		val r = WorldGenReedAlfheim(32)
		decorateChunkGen_List.add(r)
		val w = WorldGenGrapesWhiteAlfheim(4, AlfheimBlocks.grapesWhite)
		decorateChunkGen_List.add(w)
		val i = WorldGenIridescence()
		decorateChunkGen_List.add(i)
	}
}