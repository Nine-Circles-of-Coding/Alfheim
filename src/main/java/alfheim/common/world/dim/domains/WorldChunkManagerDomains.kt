package alfheim.common.world.dim.domains

import net.minecraft.world.*
import net.minecraft.world.biome.*
import net.minecraft.world.gen.layer.GenLayer
import java.util.*

object WorldChunkManagerDomains: WorldChunkManager() {
    override fun getBiomesToSpawnIn() = listOf(BiomeGenBase.sky)
    override fun getBiomeGenAt(x: Int, z: Int) = BiomeGenBase.sky!!
    override fun getRainfall(reuse: FloatArray?, x: Int, z: Int, width: Int, height: Int) = reuse ?: FloatArray(width * height)
    override fun getTemperatureAtHeight(baseTemp: Float, y: Int) = 0.5f
    override fun getBiomesForGeneration(reuse: Array<BiomeGenBase?>?, x: Int, z: Int, width: Int, height: Int) = reuse ?: Array(width * height) { getBiomeGenAt(x, z) }
    override fun getBiomeGenAt(reuse: Array<BiomeGenBase?>?, x: Int, z: Int, width: Int, height: Int, flag: Boolean) = getBiomesForGeneration(reuse, x, z, width, height)
    override fun areBiomesViable(x: Int, y: Int, z: Int, list: List<Any?>) = list.size == 1 && list[0] == getBiomeGenAt(x, z)
    override fun findBiomePosition(x: Int, y: Int, z: Int, list: List<Any?>, random: Random) = ChunkPosition(0, 64, 0)
    override fun getModdedBiomeGenerators(worldType: WorldType?, seed: Long, original: Array<GenLayer?>?) = arrayOfNulls<GenLayer?>(2)
}
