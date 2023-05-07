package alfheim.common.world.dim.niflheim

import alexsocol.asjlib.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.world.dim.niflheim.biome.*
import alfheim.common.world.dim.niflheim.biome.BiomeGenSnow
import net.minecraft.world.*
import net.minecraft.world.biome.*
import net.minecraft.world.gen.layer.*
import java.util.*

class ChunkManagerNiflheim(world: World): WorldChunkManager() {

    private var genLayerRiverMix: GenLayer
    private var genLayerVoronoiZoom: GenLayer
    private val biomeCache: BiomeCache
    private val biomeList: MutableList<BiomeGenBase?>
    
    init {
        val layers = GenLayer.initializeAllBiomeGenerators(world.seed, WorldType("largeBiomes"))
        genLayerRiverMix = layers[0]
        genLayerVoronoiZoom = layers[1]
        biomeCache = BiomeCache(this)
        biomeList = mutableListOf(BiomeGenIce, BiomeGenSnow, BiomeGenPoison)
    }

    override fun getBiomesToSpawnIn() = biomeList

    override fun getBiomeGenAt(x: Int, y: Int): BiomeGenBase {
        return biomeCache.getBiomeGenAt(x, y)
    }

    override fun getRainfall(downfalls: FloatArray, x: Int, z: Int, width: Int, height: Int): FloatArray {
        IntCache.resetIntCache()

        val ints = genLayerVoronoiZoom.getInts(x, z, width, height)

        for (i in 0 until width * height) {
            var rainfall = (biomeList.getOrNull((ints.getOrNull(i) ?: 0) % biomeList.size) ?: BiomeGenIce).intRainfall.F / 65536f

            if (rainfall > 1f)
                rainfall = 1f

            downfalls[i] = rainfall
        }

        return downfalls
    }

    override fun getBiomesForGeneration(biomes: Array<BiomeGenBase?>?, x: Int, y: Int, width: Int, height: Int): Array<BiomeGenBase?> {
        var list: Array<BiomeGenBase?>? = biomes

        IntCache.resetIntCache()

        if (list == null || list.size < width * height)
            list = arrayOfNulls(width * height)

        val ints = genLayerRiverMix.getInts(x, y, width, height)

        for (i in 0 until width * height) {
            list[i] = biomeList.getOrNull((ints.getOrNull(i) ?: 0) % biomeList.size) ?: BiomeGenIce
        }
        return list
    }

    override fun loadBlockGeneratorData(biomes: Array<BiomeGenBase?>?, x: Int, z: Int, width: Int, height: Int): Array<BiomeGenBase?> {
        return this.getBiomeGenAt(biomes, x, z, width, height, true)
    }

    override fun getBiomeGenAt(biomes: Array<BiomeGenBase?>?, x: Int, z: Int, width: Int, height: Int, cacheFlag: Boolean): Array<BiomeGenBase?> {
        var list: Array<BiomeGenBase?>? = biomes

        IntCache.resetIntCache()

        if (list == null || list.size < width * height)
            list = arrayOfNulls(width * height)

        if (cacheFlag && width == 16 && height == 16 && x and 15 == 0 && z and 15 == 0) {
            val cache = biomeCache.getCachedBiomes(x, z)
            System.arraycopy(cache, 0, list, 0, width * height)
        } else {
            val ints = genLayerVoronoiZoom.getInts(x, z, width, height).toTypedArray()

            for (i in 0 until width * height)
                list[i] = biomeList.getOrNull((ints.getOrNull(i) ?: 0) % biomeList.size) ?: BiomeGenIce
        }

        return list
    }

    override fun areBiomesViable(x: Int, z: Int, offset: Int, list: List<Any?>): Boolean {
        IntCache.resetIntCache()

        val i = x - offset shr 2
        val k = z - offset shr 2
        val width = (x + offset shr 2) - i + 1
        val height = (z + offset shr 2) - k + 1

        val ints = genLayerRiverMix.getInts(i, k, width, height)

        for (j in 0 until width * height) {
            val biome = biomeList.getOrNull((ints.getOrNull(j) ?: 0) % biomeList.size) ?: BiomeGenIce
            if (!list.contains(biome))
                return false
        }

        return true
    }

    override fun findBiomePosition(x: Int, z: Int, offset: Int, biomes: List<Any?>, random: Random): ChunkPosition {
        IntCache.resetIntCache()

        val i = x - offset shr 2
        val k = z - offset shr 2
        val width = (x + offset shr 2) - i + 1
        val height = (z + offset shr 2) - k + 1

        val ints = genLayerRiverMix.getInts(i, k, width, height)

        var pos: ChunkPosition? = null

        var size = 0

        for (j in 0 until width * height) {
            val a = i + j % width shl 2
            val c = k + j / width shl 2

            val biome = biomeList.getOrNull((ints.getOrNull(j) ?: 0) % biomeList.size) ?: BiomeGenIce

            if (biomes.contains(biome) && (pos == null || random.nextInt(size + 1) == 0)) {
                pos = ChunkPosition(a, 0, c)
                ++size
            }
        }

        return pos ?: ChunkPosition(0, 0, 0)
    }

    override fun cleanupCache() {
        this.biomeCache.cleanupCache()
    }
}