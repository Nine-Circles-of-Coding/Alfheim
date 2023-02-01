package alfheim.common.world.dim.niflheim.biome

import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.world.biome.BiomeGenBase

object BiomeGenIce: BiomeNiflheim(AlfheimConfigHandler.niflheimBiomeIDs[0], Blocks.packed_ice)
object BiomeGenPoison: BiomeNiflheim(AlfheimConfigHandler.niflheimBiomeIDs[1], AlfheimBlocks.poisonIce, Blocks.packed_ice)
object BiomeGenSnow: BiomeNiflheim(AlfheimConfigHandler.niflheimBiomeIDs[2], Blocks.snow)

open class BiomeNiflheim(id: Int, top: Block, filler: Block = top): BiomeGenBase(id) {
	
	init {
		setColor(0x80AAFF)
		setBiomeName("Niflheim")
		
		spawnableCaveCreatureList.clear()
		spawnableCreatureList.clear()
		spawnableMonsterList.clear()
		spawnableWaterCreatureList.clear()
		
		topBlock = top
		fillerBlock = filler
		theBiomeDecorator.treesPerChunk = 0
		theBiomeDecorator.flowersPerChunk = 0
		theBiomeDecorator.grassPerChunk = 0
		waterColorMultiplier = 0x121D47
		temperature = 0f
		rainfall = 0f
	}
	
	override fun getSkyColorByTemp(temp: Float) = 0x0
	override fun getBiomeGrassColor(x: Int, y: Int, z: Int) = 0x4FA390
	override fun getBiomeFoliageColor(x: Int, y: Int, z: Int) = 0x4FA390
	
}