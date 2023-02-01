package alfheim.common.world.dim.niflheim.customgens

import alfheim.common.block.AlfheimBlocks
import net.minecraft.block.material.Material
import net.minecraft.init.Blocks
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import java.util.*
import kotlin.math.abs

object WorldGenIcePikes {
	
	fun generate(world: World, rand: Random, x: Int, ay: Int, z: Int): Boolean {
		var y = ay
		
		while (world.isAirBlock(x, y, z) && y > 2) --y
		
		if (world.getBlock(x, y, z) !== Blocks.snow_layer) return false
		
		y += rand.nextInt(4)
		val l = rand.nextInt(4) + 7
		val i1 = l / 4 + rand.nextInt(2)
		if (i1 > 1 && rand.nextInt(60) == 0) y += 10 + rand.nextInt(30)
		
		var j1: Int
		var k1: Int
		var l1: Int
		
		j1 = 0
		
		while (j1 < l) {
			val f = (1.0f - j1.toFloat() / l.toFloat()) * i1.toFloat()
			k1 = MathHelper.ceiling_float_int(f)
			l1 = -k1
			while (l1 <= k1) {
				val f1 = MathHelper.abs_int(l1).toFloat() - 0.25f
				for (i2 in -k1..k1) {
					val f2 = MathHelper.abs_int(i2).toFloat() - 0.25f
					if ((l1 == 0 && i2 == 0 || f1 * f1 + f2 * f2 <= f * f) && (l1 != -k1 && l1 != k1 && i2 != -k1 && i2 != k1 || rand.nextFloat() <= 0.75f)) {
						var block = world.getBlock(x + l1, y + j1, z + i2)
						if (block.material === Material.air || block === Blocks.snow_layer) world.setBlock(x + l1, y + j1, z + i2, AlfheimBlocks.poisonIce, 0, 2)
						
						if (j1 != 0 && k1 > 1) {
							block = world.getBlock(x + l1, y - j1, z + i2)
							if (block.material === Material.air || block === Blocks.snow_layer) world.setBlock(x + l1, y - j1, z + i2, AlfheimBlocks.poisonIce, 0, 2)
						}
					}
				}
				++l1
			}
			++j1
		}
		j1 = i1 - 1
		
		if (j1 < 0) j1 = 0
		else if (j1 > 1) j1 = 1
		
		for (j2 in -j1..j1) {
			k1 = -j1
			while (k1 <= j1) {
				l1 = y - 1
				var k2 = 50
				if (abs(j2) == 1 && abs(k1) == 1) k2 = rand.nextInt(5)
				
				while (true) {
					if (l1 > 50) {
						val block1 = world.getBlock(x + j2, l1, z + k1)
						if (block1.material === Material.air || block1 === AlfheimBlocks.poisonIce || block1 === Blocks.snow_layer) {
							world.setBlock(x + j2, l1, z + k1, AlfheimBlocks.poisonIce, 0, 2)
							
							--l1
							--k2
							if (k2 <= 0) {
								l1 -= rand.nextInt(5) + 1
								k2 = rand.nextInt(5)
							}
							continue
						}
					}
					++k1
					break
				}
			}
		}
		
		return true
	}
}