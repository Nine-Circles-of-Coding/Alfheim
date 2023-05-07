package alfheim.common.world.dim.niflheim.customgens

import alexsocol.asjlib.bidiRange
import net.minecraft.init.Blocks
import net.minecraft.world.World
import java.util.*

object WorldGenHole {
	
	fun generate(world: World, random: Random, x: Int, y: Int, z: Int): Boolean {
		var i = x
		var j = y
		var k = z
		
		if (!check(world, i - 1, j + 1, k - 1, 4, 1, 4)) return false
		
		for (a in x.bidiRange(3))
			for (c in z.bidiRange(3))
				if (world.getBlock(x, 127, z) === Blocks.water)
					return false
		
		var time = 0
		val holeComplete = BooleanArray(12) { false }
		var notEnd = true
		while (notEnd && j > 90) {
			
			++time
			if (time == 4) {
				time = 0
				
				Arrays.fill(holeComplete, false)
				
				when (random.nextInt(5)) {
					0 -> ++k
					1 -> --k
					2 -> ++i
					3 -> --i
				}
			}
			
			if (!holeComplete[0] && !world.isAirBlock(i, j, k)) {
				world.setBlockToAir(i, j, k)
			} else {
				holeComplete[0] = true
			}
			if (!holeComplete[1] && !world.isAirBlock(i, j, k + 1)) {
				world.setBlockToAir(i, j, k + 1)
			} else {
				holeComplete[1] = true
			}
			if (!holeComplete[2] && !world.isAirBlock(i + 1, j, k + 1)) {
				world.setBlockToAir(i + 1, j, k + 1)
			} else {
				holeComplete[2] = true
			}
			if (!holeComplete[3] && !world.isAirBlock(i + 1, j, k)) {
				world.setBlockToAir(i + 1, j, k)
			} else {
				holeComplete[3] = true
			}
			if (!holeComplete[4] && !world.isAirBlock(i + 2, j, k + 1)) {
				world.setBlockToAir(i + 2, j, k + 1)
			} else {
				holeComplete[4] = true
			}
			if (!holeComplete[5] && !world.isAirBlock(i + 2, j, k)) {
				world.setBlockToAir(i + 2, j, k)
			} else {
				holeComplete[5] = true
			}
			if (!holeComplete[6] && !world.isAirBlock(i - 1, j, k + 1)) {
				world.setBlockToAir(i - 1, j, k + 1)
			} else {
				holeComplete[6] = true
			}
			if (!holeComplete[7] && !world.isAirBlock(i - 1, j, k)) {
				world.setBlockToAir(i - 1, j, k)
			} else {
				holeComplete[7] = true
			}
			if (!holeComplete[8] && !world.isAirBlock(i, j, k - 1)) {
				world.setBlockToAir(i, j, k - 1)
			} else {
				holeComplete[8] = true
			}
			if (!holeComplete[9] && !world.isAirBlock(i, j, k + 2)) {
				world.setBlockToAir(i, j, k + 2)
			} else {
				holeComplete[9] = true
			}
			if (!holeComplete[10] && !world.isAirBlock(i + 1, j, k + 2)) {
				world.setBlockToAir(i + 1, j, k + 2)
			} else {
				holeComplete[10] = true
			}
			if (!holeComplete[11] && !world.isAirBlock(i + 1, j, k - 1)) {
				world.setBlockToAir(i + 1, j, k - 1)
			} else {
				holeComplete[11] = true
			}
			--j
			notEnd = false
			for (b in holeComplete) {
				if (!b) {
					notEnd = true
					break
				}
			}
		}
		return true
	}
	
	fun check(world: World, j: Int, k: Int, l: Int, x: Int, y: Int, z: Int): Boolean {
		for (i in -1 until x + 1) {
			for (o in -1 until z + 1) {
				for (p in 1 until y + 1) {
					if (!world.isAirBlock(j + i, k + p, l + o)) {
						return false
					}
				}
			}
		}
		return true
	}
}