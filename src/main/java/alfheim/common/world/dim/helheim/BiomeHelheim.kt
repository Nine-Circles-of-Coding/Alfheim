package alfheim.common.world.dim.helheim

import net.minecraftforge.common.BiomeDictionary
import ru.vamig.worldengine.*

object BiomeHelheim: WE_Biome(WE_WorldProvider.we_id) {

	init {
		createChunkGen_InXZ_List.clear()
		decorateChunkGen_List.clear()
		setColor(0x222222)
		setDisableRain()
		
		BiomeDictionary.registerBiomeType(this, BiomeDictionary.Type.DEAD)
		BiomeDictionary.registerBiomeType(this, BiomeDictionary.Type.WASTELAND)
		
		clearSpawn()
		
		biomeMinValueOnMap = -0.4
		biomeMaxValueOnMap = 0.82
		biomePersistence = 1.8
		biomeNumberOfOctaves = 3
		biomeScaleX = 250.0
		biomeScaleY = 2.0
		biomeSurfaceHeight = 24
		biomeInterpolateQuality = 1
	}
	
	override fun getModdedBiomeGrassColor(original: Int) = 0x222222
	override fun getModdedBiomeFoliageColor(original: Int) = 0x222222
	override fun getBiomeGrassColor(R: Int, G: Int, B: Int) = 0x222222
	override fun getBiomeFoliageColor(R: Int, G: Int, B: Int) = 0x222222
	override fun getWaterColorMultiplier() = 0xAA0000
	override fun getSkyColorByTemp(temp: Float) = 0x222222
	override fun getFloatTemperature(x: Int, y: Int, z: Int) = 0f
}
