package alfheim.common.world.dim.alfheim.customgens

import alexsocol.asjlib.*
import cpw.mods.fml.common.IWorldGenerator
import net.minecraft.init.Blocks
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import vazkii.botania.common.block.ModBlocks
import java.util.*

class AlfheimLakeGen(val chunksForLake: Int = 12, val minY: Int = 0, val maxY: Int = 255): IWorldGenerator {
	
	val lakeBlock = Blocks.water
	
	val allowedReplacements = arrayOf(Blocks.air, Blocks.grass, Blocks.dirt, ModBlocks.livingrock, lakeBlock)
	
	override fun generate(random: Random, chunkX: Int, chunkZ: Int, world: World, chunkGenerator: IChunkProvider?, chunkProvider: IChunkProvider?) {
		if (random.nextInt(chunksForLake) != 0) return
		
		val x = chunkX * 16 + random.nextInt(16)
		val z = chunkZ * 16 + random.nextInt(16)
		val y = ASJUtilities.randInBounds(minY, maxY, random)
		//-//
		lake(world, random, x, y, z)
	}
	
	fun lake(world: World, random: Random, x: Int, _y: Int, z: Int) {
		var y = _y
		
		while (y > 7 && world.isAirBlock(x, y, z)) --y
		
		if (world.getBlock(x, y, z) inln allowedReplacements) return
		
		if (y <= 6) return
		y -= 6
		
		val aboolean = BooleanArray(2048)
		for (w in 0 until random.nextInt(4) + 4) {
			val d0 = random.nextDouble() * 6 + 3
			val d1 = random.nextDouble() * 4 + 2
			val d2 = random.nextDouble() * 6 + 3
			val d3 = random.nextDouble() * (14 - d0) + 1 + d0 / 2
			val d4 = random.nextDouble() * (4 - d1) + 2 + d1 / 2
			val d5 = random.nextDouble() * (14 - d2) + 1 + d2 / 2
			//-//
			for (bx in 1..14)
				for (bz in 1..14)
					for (by in 1..6) {
				val d6 = (bx.D - d3) * 2 / d0
				val d7 = (by.D - d4) * 2 / d1
				val d8 = (bz.D - d5) * 2 / d2
				val d9 = d6 * d6 + d7 * d7 + d8 * d8
				//-//
				if (d9 < 1) aboolean[(bx * 16 + bz) * 8 + by] = true
			}
		}
		
		fun checkOrGenerate(doit: Boolean): Boolean {
			for (bx in 0..15)
				for (bz in 0..15) {
					for (by in 0..7) {
						val i = (bx * 16 + bz) * 8 + by
						val b = aboolean[i]
						if (!b && (
							bx < 15 && aboolean[((bx + 1) * 16 + bz) * 8 + by] || bx > 0 && aboolean[((bx - 1) * 16 + bz) * 8 + by] ||
							bz < 15 && aboolean[(bx * 16 + bz + 1) * 8 + by] || bz > 0 && aboolean[(bx * 16 + bz - 1) * 8 + by] ||
							by < 7 && aboolean[(bx * 16 + bz) * 8 + by + 1] || by > 0 && aboolean[i - 1])) {
							
							val material = world.getBlock(x + bx, y + by, z + bz).material
							if (by >= 4 && material.isLiquid) return true
							if (by < 4 && !material.isSolid && world.getBlock(x + bx, y + by, z + bz) !== lakeBlock) return true
						}
						//-//
						if (b) {
							if (doit)
								world.setBlock(x + bx, y + by, z + bz, if (by >= 4) Blocks.air else lakeBlock, 0, 3)
							else
								if (world.getBlock(x + bx, y + by, z + bz) inln allowedReplacements) return false
						}
					}
				}
			return true
		}
		
		if (checkOrGenerate(false)) checkOrGenerate(true)
	}
}
