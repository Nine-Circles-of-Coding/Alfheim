package alfheim.common.world.dim.niflheim.structure

import net.minecraft.init.Blocks
import net.minecraft.world.World
import vazkii.botania.common.block.ModBlocks
import java.util.*

object WorldGenBigDungeons {
	
	fun generate(world: World, random: Random, par3: Int, par4: Int, par5: Int): Boolean {
		val byte0 = 3
		val i = 4
		val j = 4
		var k = 0
		val type = 3
		var l2: Int
		var i3: Int
		var j3: Int
		l2 = par3 - i - 1
		
		while (l2 <= par3 + i + 1) {
			i3 = par4 - 1
			while (i3 <= par4 + byte0 + 1) {
				j3 = par5 - j - 1
				while (j3 <= par5 + j + 1) {
					val material = world.getBlock(l2, i3, j3).material
					if (i3 == par4 - 1 && !material.isSolid) {
						return false
					}
					if (i3 == par4 + byte0 + 1 && !material.isSolid) {
						return false
					}
					if ((l2 == par3 - i - 1 || l2 == par3 + i + 1 || j3 == par5 - j - 1 || j3 == par5 + j + 1) && i3 == par4 && world.isAirBlock(l2, i3, j3) && world.isAirBlock(l2, i3 + 1, j3)) {
						++k
					}
					++j3
				}
				++i3
			}
			++l2
		}
		
		return if (k in 1..5) {
			l2 = par3 - i - 7
			while (l2 <= par3 + i + 7) {
				i3 = par4 + byte0
				while (i3 >= par4 - 7) {
					j3 = par5 - j - 7
					while (j3 <= par5 + j + 7) {
						if (l2 != par3 - i - 7 && i3 != par4 - 7 && j3 != par5 - j - 7 && l2 != par3 + i + 7 && i3 != par4 + byte0 && j3 != par5 + j + 7) {
							world.setBlock(l2, i3, j3, Blocks.air, 0, 3)
						} else if (i3 >= 0 && !world.getBlock(l2, i3 - 1, j3).material.isSolid) {
							world.setBlock(l2, i3, j3, Blocks.air, 0, 3)
						} else if (world.getBlock(l2, i3, j3).material.isSolid) {
							world.setBlock(l2, i3, j3, ModBlocks.customBrick, 2, 3)
						}
						++j3
					}
					--i3
				}
				++l2
			}
			l2 = par3 + random.nextInt(i * 2 + 7) - i
			j3 = par5 + random.nextInt(j * 2 + 2) - j
			StructureGenChest.generate(world, random, type, l2, par4, j3)
			StructureGenChest.generate(world, random, type, l2, par4, j3 - 1)
			world.setBlock(l2, par4 - 1, j3, ModBlocks.customBrick, 2, 3)
			world.setBlock(l2, par4 - 1, j3 - 1, ModBlocks.customBrick, 2, 3)
			StructureGenSpawner.generate(world, random, 3, par3, par4, par5)
			StructureGenSpawner.generate(world, random, 3, par3, par4, par5 - 1)
			StructureGenSpawner.generate(world, random, 3, par3 + 1, par4, par5)
			StructureGenSpawner.generate(world, random, 3, par3 + 1, par4, par5 - 1)
			true
		} else {
			false
		}
	}
}