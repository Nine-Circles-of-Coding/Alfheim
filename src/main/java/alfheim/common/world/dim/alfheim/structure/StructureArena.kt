package alfheim.common.world.dim.alfheim.structure

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.common.world.data.CustomWorldData.Companion.customData
import alfheim.common.world.dim.alfheim.biome.BiomeField
import net.minecraft.init.Blocks
import net.minecraft.world.World
import ru.vamig.worldengine.*
import ru.vamig.worldengine.standardcustomgen.StructureBaseClass
import vazkii.botania.common.block.ModBlocks
import java.util.*

object StructureArena: StructureBaseClass() {
	
	// corner coordinates
	val xs = arrayOf(0, 0, 0, 20, 20, 20, 40, 40, 40)
	val zs = arrayOf(0, 20, 40, 0, 20, 40, 0, 20, 40)
	
	val arenaSchema = SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/Arena")
	
	override fun generate(world: World, rand: Random, x: Int, y: Int, z: Int): Boolean {
		if (ASJUtilities.isClient) return false // custom data is only on server
		if (x shr 4 in -32 until 32 || z shr 4 in -32 until 32) return false // no arenas in Yggdrasil pit
		
		val data = world.customData
		val locs = data.structures["any"]
		
		if (locs.any { Vector3.pointDistancePlane(x, z, it.first, it.second) < 128 }) return false
		
		(world.provider as? WE_WorldProvider)?.cp?.also { cp ->
			val biomes = Array(xs.size) { WE_Biome.getBiomeAt(cp, x + xs[it], z + zs[it]) }
			if (biomes.any { it !is BiomeField }) return false
		}
		
		generate01(world, x, y + 1, z)
		
		locs.add(x to z)
		data.markDirty()
		
		return true
	}
	
	fun generate01(world: World, x: Int, y: Int, z: Int) {
		SchemaUtils.generate(world, x, y, z, arenaSchema)
		
		ASJUtilities.fillGenHoles(world, Blocks.grass, 0, x + 20, y, z + 20, 22)
		
		var count = world.rand.nextInt(3) + 1
		var index: Int
		
		if (count < 3 && world.rand.nextInt(count * 2) == 0) {
			index = world.rand.nextInt(4)
			when (index) {
				0 -> world.setBlock(x + 16, y + 1, z + 16, ModBlocks.pylon, 2, 3)
				1 -> world.setBlock(x + 24, y + 1, z + 16, ModBlocks.pylon, 2, 3)
				2 -> world.setBlock(x + 16, y + 1, z + 24, ModBlocks.pylon, 2, 3)
				3 -> world.setBlock(x + 24, y + 1, z + 24, ModBlocks.pylon, 2, 3)
			}
		}
		
		val pos = BooleanArray(9)
		val yo = IntArray(9)
		
		while (count > 0) {
			index = world.rand.nextInt(9)
			if (pos[index]) {
				count++
				count--
				continue
			}
			
			pos[index] = true
			yo[index] = if (world.rand.nextInt(3) == 0) 0 else 1
			count--
		}
		
		if (pos[0]) world.setBlock(x + 19, y + yo[0], z + 19, ModBlocks.storage, 2, 3)
		if (pos[1]) world.setBlock(x + 20, y + yo[1], z + 19, ModBlocks.storage, 2, 3)
		if (pos[2]) world.setBlock(x + 21, y + yo[2], z + 19, ModBlocks.storage, 2, 3)
		if (pos[3]) world.setBlock(x + 19, y + yo[3], z + 20, ModBlocks.storage, 2, 3)
		if (pos[4]) world.setBlock(x + 20, y + yo[4], z + 20, ModBlocks.storage, 2, 3)
		if (pos[5]) world.setBlock(x + 21, y + yo[5], z + 20, ModBlocks.storage, 2, 3)
		if (pos[6]) world.setBlock(x + 19, y + yo[6], z + 21, ModBlocks.storage, 2, 3)
		if (pos[7]) world.setBlock(x + 20, y + yo[7], z + 21, ModBlocks.storage, 2, 3)
		if (pos[8]) world.setBlock(x + 21, y + yo[8], z + 21, ModBlocks.storage, 2, 3)
	}
}