package alfheim.common.world.dim.niflheim.customgens

import alfheim.common.block.AlfheimBlocks
import alfheim.common.world.dim.niflheim.biome.*
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.world.*
import net.minecraft.world.biome.BiomeGenBase
import java.util.*

object WorldGenLakes {
	
	// I'm not going to prettify this
	fun generate(world: World, random: Random, x: Int, y: Int, z: Int, block: Block, biomegenbase: BiomeGenBase): Boolean {
		var a = x
		var b = y
		var c = z
		var filler = block
		
		if (filler === Blocks.water && biomegenbase is BiomeGenSnow) filler = Blocks.ice
		
		a -= 8
		c -= 8
		
		while (b > 5 && world.isAirBlock(a, b, c)) --b
		
		if (b <= 4) return false
		
		b -= 4
		val aflag = BooleanArray(2048)
		val i = random.nextInt(4) + 4
		var k1: Int
		k1 = 0
		while (k1 < i) {
			val l2 = random.nextDouble() * 6.0 + 3.0
			val flag1 = random.nextDouble() * 4.0 + 2.0
			val d2 = random.nextDouble() * 6.0 + 3.0
			val d3 = random.nextDouble() * (16.0 - l2 - 2.0) + 1.0 + l2 / 2.0
			val d4 = random.nextDouble() * (8.0 - flag1 - 4.0) + 2.0 + flag1 / 2.0
			val d5 = random.nextDouble() * (16.0 - d2 - 2.0) + 1.0 + d2 / 2.0
			for (i4 in 1..14) {
				for (j4 in 1..14) {
					for (k4 in 1..6) {
						val d6 = (i4.toDouble() - d3) / (l2 / 2.0)
						val d7 = (k4.toDouble() - d4) / (flag1 / 2.0)
						val d8 = (j4.toDouble() - d5) / (d2 / 2.0)
						val d9 = d6 * d6 + d7 * d7 + d8 * d8
						if (d9 < 1.0) {
							aflag[(i4 * 16 + j4) * 8 + k4] = true
						}
					}
				}
			}
			++k1
		}
		var byte0: Int
		var var32: Int
		var var33: Boolean
		k1 = 0
		while (k1 < 16) {
			var32 = 0
			while (var32 < 16) {
				byte0 = 0
				while (byte0 < 8) {
					var33 = !aflag[(k1 * 16 + var32) * 8 + byte0] && (k1 < 15 && aflag[((k1 + 1) * 16 + var32) * 8 + byte0] || k1 > 0 && aflag[((k1 - 1) * 16 + var32) * 8 + byte0] || var32 < 15 && aflag[(k1 * 16 + var32 + 1) * 8 + byte0] || var32 > 0 && aflag[(k1 * 16 + (var32 - 1)) * 8 + byte0] || byte0 < 7 && aflag[(k1 * 16 + var32) * 8 + byte0 + 1] || byte0 > 0 && aflag[(k1 * 16 + var32) * 8 + (byte0 - 1)])
					if (var33) {
						val material = world.getBlock(a + k1, b + byte0, c + var32).material
						if (byte0 >= 4 && material.isLiquid) {
							return false
						}
						if (byte0 < 4 && !material.isSolid && world.getBlock(a + k1, b + byte0, c + var32) !== filler) {
							return false
						}
					}
					++byte0
				}
				++var32
			}
			++k1
		}
		k1 = 0
		while (k1 < 16) {
			var32 = 0
			while (var32 < 16) {
				byte0 = 0
				while (byte0 < 8) {
					if (aflag[(k1 * 16 + var32) * 8 + byte0]) {
						world.setBlock(a + k1, b + byte0, c + var32, if (byte0 < 4) filler else Blocks.air)
					}
					++byte0
				}
				++var32
			}
			++k1
		}
		k1 = 0
		while (k1 < 16) {
			var32 = 0
			while (var32 < 16) {
				byte0 = 4
				while (byte0 < 8) {
					if (aflag[(k1 * 16 + var32) * 8 + byte0] && world.getBlock(a + k1, b + byte0 - 1, c + var32) === AlfheimBlocks.niflheimBlock /*dirt*/ && world.getSavedLightValue(EnumSkyBlock.Sky, a + k1, b + byte0, c + var32) <= 0) {
						world.isRemote
					}
					++byte0
				}
				++var32
			}
			++k1
		}
		if (filler === AlfheimBlocks.poisonIce) {//. material === Material.lava) {
			k1 = 0
			while (k1 < 16) {
				var32 = 0
				while (var32 < 16) {
					byte0 = 0
					while (byte0 < 8) {
						var33 = !aflag[(k1 * 16 + var32) * 8 + byte0] && (k1 < 15 && aflag[((k1 + 1) * 16 + var32) * 8 + byte0] || k1 > 0 && aflag[((k1 - 1) * 16 + var32) * 8 + byte0] || var32 < 15 && aflag[(k1 * 16 + var32 + 1) * 8 + byte0] || var32 > 0 && aflag[(k1 * 16 + (var32 - 1)) * 8 + byte0] || byte0 < 7 && aflag[(k1 * 16 + var32) * 8 + byte0 + 1] || byte0 > 0 && aflag[(k1 * 16 + var32) * 8 + (byte0 - 1)])
						if (var33 && (byte0 < 4 || random.nextInt(2) != 0) && world.getBlock(a + k1, b + byte0, c + var32).material.isSolid) {
							if (world.getBlock(k1, var32, byte0) === AlfheimBlocks.niflheimBlock && biomegenbase is BiomeGenPoison) {
								world.setBlock(a + k1, b + byte0, c + var32, AlfheimBlocks.niflheimBlock/*cobblestone*/)
							} else if (world.getBlock(k1, var32, byte0) === AlfheimBlocks.niflheimBlock && biomegenbase is BiomeGenIce) {
								world.setBlock(a + k1, b + byte0, c + var32, AlfheimBlocks.niflheimBlock/*gravel*/)
							}
						}
						++byte0
					}
					++var32
				}
				++k1
			}
		}
		if (filler === Blocks.water) {
			k1 = 0
			while (k1 < 16) {
				var32 = 0
				while (var32 < 16) {
					byte0 = 0
					while (byte0 < 8) {
						var33 = !aflag[(k1 * 16 + var32) * 8 + byte0] && (k1 < 15 && aflag[((k1 + 1) * 16 + var32) * 8 + byte0] || k1 > 0 && aflag[((k1 - 1) * 16 + var32) * 8 + byte0] || var32 < 15 && aflag[(k1 * 16 + var32 + 1) * 8 + byte0] || var32 > 0 && aflag[(k1 * 16 + (var32 - 1)) * 8 + byte0] || byte0 < 7 && aflag[(k1 * 16 + var32) * 8 + byte0 + 1] || byte0 > 0 && aflag[(k1 * 16 + var32) * 8 + (byte0 - 1)])
						if (var33 && (byte0 < 4 || random.nextInt(5) == 0) && world.getBlock(a + k1, b + byte0, c + var32).material.isSolid) {
							if (world.getBlock(k1, var32, byte0) === AlfheimBlocks.niflheimBlock && biomegenbase is BiomeGenIce && b < 110) {
								world.setBlock(a + k1, b + byte0, c + var32, AlfheimBlocks.niflheimBlock/*sand*/)
							}
						}
						++byte0
					}
					++var32
				}
				++k1
			}
		}
		if (filler === Blocks.water) {
			k1 = 0
			while (k1 < 16) {
				var32 = 0
				while (var32 < 16) {
					val var34: Byte = 4
					if (world.isBlockFreezable(a + k1, b + var34, c + var32)) {
						world.setBlock(a + k1, b + var34, c + var32, Blocks.ice)
					}
					++var32
				}
				++k1
			}
		}
		return true
	}
}