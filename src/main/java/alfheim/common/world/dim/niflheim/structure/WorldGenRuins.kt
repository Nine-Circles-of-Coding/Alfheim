package alfheim.common.world.dim.niflheim.structure

import alexsocol.asjlib.inln
import alfheim.common.core.helper.RotateGenerationHelper.rotateGeneration
import alfheim.common.world.dim.niflheim.ChunkProviderNiflheim
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.item.ItemDoor
import net.minecraft.world.World
import vazkii.botania.common.block.*
import java.util.*

object WorldGenRuins {
	
	private var treasuryCount = 0
	
	fun generate(world: World, random: Random, j: Int, k: Int, l: Int): Boolean {
		treasuryCount = 0
		
		val type = 7
		val a = random.nextInt(10)
		if (a == 0) {
			val matrixX = random.nextInt(3) + 5
			val matrixY = matrixX + random.nextInt(3) + 5
			if (checkGenerate(world, j, k - 5, l, matrixX + 1, 15, matrixY + 1)) {
				val var15 = Array(matrixX) { BooleanArray(matrixY) }
				var info: Int
				var var16 = 0
				while (var16 < matrixX) {
					info = 0
					while (info < matrixY) {
						val ruinsRandom = random.nextInt(4)
						var15[var16][info] = ruinsRandom == 0 || ruinsRandom == 1 || ruinsRandom == 2
						++info
					}
					++var16
				}
				var16 = matrixX - 1
				while (var16 > 0) {
					info = matrixY - 1
					while (info > 0) {
						if (var15[var16][info]) {
							generateRoom(world, random, type, j, k, l, var16, info)
						}
						--info
					}
					--var16
				}
			}
		}
		return true
	}
	
	fun generateShaft(world: World, random: Random, j: Int, k: Int, l: Int) {
		var i: Int
		var height = -1
		while (height < 2) {
			i = -1
			while (i < 2) {
				if (world.getBlock(j + height, k, l + i) !== ModBlocks.customBrick) {
					return
				}
				++i
			}
			++height
		}
		height = 1
		while (world.getBlock(j, k + height, l) !== Blocks.air && height < 26) {
			++height
		}
		if (height != 26) {
			world.setBlock(j, k, l, Blocks.air)
			i = 1
			while (i <= height) {
				for (o in -1..1) {
					for (p in -1..1) {
						world.setBlock(j + o, k + i, l + p, ModBlocks.customBrick, 2, 3)
					}
				}
				world.setBlock(j, k + i, l, Blocks.air)
				++i
			}
			world.setBlock(j, k + height, l, Blocks.trapdoor)
		}
	}
	
	fun generateCorridorX(world: World, random: Random, j: Int, k: Int, l: Int) {
		for (canGenerate in 1..2) {
			if (world.getBlock(j, k + 1, l + canGenerate) !== ModBlocks.customBrick || world.getBlock(j + 1, k, l + canGenerate) === ModBlocks.customBrick || world.getBlock(j - 1, k + 1, l + canGenerate) !== Blocks.air) {
				return
			}
		}
		var var12 = false
		var length = 1
		while (length < 50 && !var12) {
			if (world.getBlock(j + length, k + 1, l + 1) === ModBlocks.customBrick) {
				if (world.getBlock(j + length, k + 1, l + 2) !== ModBlocks.customBrick || world.getBlock(j + length + 1, k + 1, l + 1) !== Blocks.air || world.getBlock(j + length + 1, k + 1, l + 2) !== Blocks.air) {
					return
				}
				var12 = true
			} else if (world.getBlock(j + length, k + 1, l + 2) === ModBlocks.customBrick) {
				return
			}
			++length
		}
		if (var12) {
			for (doorPlacer in 0 until length) {
				var o = 0
				while (o < 4) {
					world.setBlock(j + doorPlacer, k, l + o, ModBlocks.customBrick, 2, 3)
					world.setBlock(j + doorPlacer, k + 4, l + o, ModBlocks.customBrick, 2, 3)
					++o
				}
				o = 1
				while (o < 4) {
					world.setBlock(j + doorPlacer, k + o, l, ModBlocks.customBrick, 2, 3)
					world.setBlock(j + doorPlacer, k + o, l + 1, Blocks.air)
					world.setBlock(j + doorPlacer, k + o, l + 2, Blocks.air)
					world.setBlock(j + doorPlacer, k + o, l + 3, ModBlocks.customBrick, 2, 3)
					++o
				}
			}
			var var13 = random.nextInt(6)
			if (var13 > 2) {
				if (var13 == 5) {
					ItemDoor.placeDoorBlock(world, j, k + 1, l + 1, 2, Blocks.iron_door)
					ItemDoor.placeDoorBlock(world, j, k + 1, l + 2, 2, Blocks.iron_door)
				} else {
					ItemDoor.placeDoorBlock(world, j, k + 1, l + 1, 2, Blocks.wooden_door)
					ItemDoor.placeDoorBlock(world, j, k + 1, l + 2, 2, Blocks.wooden_door)
				}
				world.setBlock(j, k + 3, l + 1, ModBlocks.customBrick, 2, 3)
				world.setBlock(j, k + 3, l + 2, ModBlocks.customBrick, 2, 3)
			}
			var13 = random.nextInt(6)
			if (var13 > 2) {
				if (var13 == 5) {
					ItemDoor.placeDoorBlock(world, j + length - 1, k + 1, l + 1, 0, Blocks.iron_door)
					ItemDoor.placeDoorBlock(world, j + length - 1, k + 1, l + 2, 0, Blocks.iron_door)
				} else {
					ItemDoor.placeDoorBlock(world, j + length - 1, k + 1, l + 1, 0, Blocks.wooden_door)
					ItemDoor.placeDoorBlock(world, j + length - 1, k + 1, l + 2, 0, Blocks.wooden_door)
				}
				world.setBlock(j + length - 1, k + 3, l + 1, ModBlocks.customBrick, 2, 3)
				world.setBlock(j + length - 1, k + 3, l + 2, ModBlocks.customBrick, 2, 3)
			}
		}
	}
	
	fun generateCorridorZ(world: World, random: Random, j: Int, k: Int, l: Int) {
		for (canGenerate in 1..2) {
			if (world.getBlock(j + canGenerate, k + 1, l) !== ModBlocks.customBrick || world.getBlock(j + canGenerate, k, l + 1) === ModBlocks.customBrick || world.getBlock(j + canGenerate, k + 1, l - 1) !== Blocks.air) {
				return
			}
		}
		var var12 = false
		var length = 1
		while (length < 50 && !var12) {
			if (world.getBlock(j + 1, k + 1, l + length) === ModBlocks.customBrick) {
				if (world.getBlock(j + 2, k + 1, l + length) !== ModBlocks.customBrick || world.getBlock(j + 1, k + 1, l + length + 1) !== Blocks.air || world.getBlock(j + 2, k + 1, l + length + 1) !== Blocks.air) {
					return
				}
				var12 = true
			} else if (world.getBlock(j + 2, k + 1, l + length) === ModBlocks.customBrick) {
				return
			}
			++length
		}
		if (var12) {
			for (doorPlacer in 0 until length) {
				var o = 0
				while (o < 4) {
					world.setBlock(j + o, k, l + doorPlacer, ModBlocks.customBrick, 2, 3)
					world.setBlock(j + o, k + 4, l + doorPlacer, ModBlocks.customBrick, 2, 3)
					++o
				}
				o = 1
				while (o < 4) {
					world.setBlock(j, k + o, l + doorPlacer, ModBlocks.customBrick, 2, 3)
					world.setBlock(j + 1, k + o, l + doorPlacer, Blocks.air)
					world.setBlock(j + 2, k + o, l + doorPlacer, Blocks.air)
					world.setBlock(j + 3, k + o, l + doorPlacer, ModBlocks.customBrick, 2, 3)
					++o
				}
			}
			var var13 = random.nextInt(6)
			if (var13 > 2) {
				if (var13 == 5) {
					ItemDoor.placeDoorBlock(world, j + 1, k + 1, l, 3, Blocks.iron_door)
					ItemDoor.placeDoorBlock(world, j + 2, k + 1, l, 3, Blocks.iron_door)
				} else {
					ItemDoor.placeDoorBlock(world, j + 1, k + 1, l, 3, Blocks.wooden_door)
					ItemDoor.placeDoorBlock(world, j + 2, k + 1, l, 3, Blocks.wooden_door)
				}
				world.setBlock(j + 1, k + 3, l, ModBlocks.customBrick, 2, 3)
				world.setBlock(j + 2, k + 3, l, ModBlocks.customBrick, 2, 3)
			}
			var13 = random.nextInt(6)
			if (var13 > 2) {
				if (var13 == 5) {
					ItemDoor.placeDoorBlock(world, j + 1, k + 1, l + length - 1, 1, Blocks.iron_door)
					ItemDoor.placeDoorBlock(world, j + 2, k + 1, l + length - 1, 1, Blocks.iron_door)
				} else {
					ItemDoor.placeDoorBlock(world, j + 1, k + 1, l + length - 1, 1, Blocks.wooden_door)
					ItemDoor.placeDoorBlock(world, j + 2, k + 1, l + length - 1, 1, Blocks.wooden_door)
				}
				world.setBlock(j + 1, k + 3, l + length - 1, ModBlocks.customBrick, 2, 3)
				world.setBlock(j + 2, k + 3, l + length - 1, ModBlocks.customBrick, 2, 3)
			}
		}
	}
	
	fun generateRoom(world: World, random: Random, type: Int, j: Int, k: Int, l: Int, i2: Int, i3: Int) {
		val roomNumber = random.nextInt(4)
		val maxTreasuryCount = 2
		when (roomNumber) {
			0 -> roomA(world, random, type, j + i2 * 8 + random.nextInt(6), k - 1, l + i3 * 8 + random.nextInt(6))
			1 -> roomWell(world, random, type, j + i2 * 8 + random.nextInt(6), k - 1, l + i3 * 8 + random.nextInt(6))
			2 -> roomFountain(world, random, type, j + i2 * 8 + random.nextInt(6), k - 1, l + i3 * 8 + random.nextInt(6))
			3 -> if (treasuryCount < maxTreasuryCount && random.nextInt(2) == 0) {
				roomTreasury(world, random, type, j + i2 * 8 + random.nextInt(6), k - 1, l + i3 * 8 + random.nextInt(6))
			} else {
				generateRoom(world, random, type, j, k, l, i2, i3)
			}
		}
	}
	
	fun roomA(world: World, random: Random, type: Int, j: Int, k: Int, l: Int) {
		val x = 7
		val y = 6
		val z = 7
		if (this.check(world, j, k, l, x, y, z)) {
			val structure = defaultMatrixFillB(x, y, z)
			val metadata = defaultMatrixFill(x, y, z)
			var o: Int
			var i = 0
			while (i < 7) {
				o = 0
				while (o < 7) {
					structure[i][0][o] = ModBlocks.customBrick
					structure[i][5][o] = ModBlocks.customBrick
					metadata[i][0][o] = 2
					metadata[i][5][o] = 2
					++o
				}
				++i
			}
			i = 0
			while (i < 7) {
				o = 1
				while (o < 5) {
					structure[i][o][0] = ModBlocks.customBrick
					structure[i][o][6] = ModBlocks.customBrick
					metadata[i][o][0] = 2
					metadata[i][o][6] = 2
					++o
				}
				++i
			}
			i = 1
			while (i < 6) {
				o = 1
				while (o < 5) {
					structure[0][o][i] = ModBlocks.customBrick
					structure[6][o][i] = ModBlocks.customBrick
					metadata[0][o][i] = 2
					metadata[6][o][i] = 2
					++o
				}
				++i
			}
			rotateGeneration(world, random, j, k, l, type, structure, metadata, x, y, z, random.nextInt(8))
			if (random.nextInt(2) == 0) {
				StructureGenSpawner.generate(world, random, 4, j + 3, k + 1, l + 3)
			}
			i = 0
			while (i < 6) {
				generateCorridorX(world, random, j + 6, k, l + 1 + random.nextInt(5))
				generateCorridorZ(world, random, j + 1 + random.nextInt(5), k, l + 6)
				++i
			}
			generateShaft(world, random, j + 3, k + 5, l + 3)
		}
	}
	
	fun roomWell(world: World, random: Random, type: Int, j: Int, k: Int, l: Int) {
		val x = 7
		val y = 6
		val z = 7
		if (this.check(world, j, k, l, x, y, z)) {
			val structure = defaultMatrixFillB(x, y, z)
			val metadata = defaultMatrixFill(x, y, z)
			var o: Int
			var i = 0
			while (i < 7) {
				o = 0
				while (o < 7) {
					structure[i][0][o] = ModBlocks.customBrick
					structure[i][5][o] = ModBlocks.customBrick
					metadata[i][0][o] = 2
					metadata[i][5][o] = 2
					++o
				}
				++i
			}
			i = 0
			while (i < 7) {
				o = 1
				while (o < 5) {
					structure[i][o][0] = ModBlocks.customBrick
					structure[i][o][6] = ModBlocks.customBrick
					metadata[i][o][0] = 2
					metadata[i][o][6] = 2
					++o
				}
				++i
			}
			i = 1
			while (i < 6) {
				o = 1
				while (o < 5) {
					structure[0][o][i] = ModBlocks.customBrick
					structure[6][o][i] = ModBlocks.customBrick
					metadata[0][o][i] = 2
					metadata[6][o][i] = 2
					++o
				}
				++i
			}
			structure[3][1][2] = ModBlocks.customBrick
			metadata[3][1][2] = 2
			structure[3][1][4] = ModBlocks.customBrick
			metadata[3][1][4] = 2
			structure[2][1][3] = ModBlocks.customBrick
			metadata[2][1][3] = 2
			structure[4][1][3] = ModBlocks.customBrick
			metadata[4][1][3] = 2
			structure[2][1][2] = Blocks.iron_bars
			structure[2][1][4] = Blocks.iron_bars
			structure[4][1][2] = Blocks.iron_bars
			structure[4][1][4] = Blocks.iron_bars
			rotateGeneration(world, random, j, k, l, type, structure, metadata, x, y, z, random.nextInt(8))
			i = 2
			while (i < 5) {
				o = 2
				while (o < 5) {
					for (p in -4..-1) {
						if (world.getBlock(j + i, k + p, l + o) !== Blocks.air) {
							world.setBlock(j + i, k + p, l + o, ModBlocks.customBrick, 2, 3)
						}
					}
					++o
				}
				++i
			}
			i = -4
			while (i < 1) {
				world.setBlock(j + 3, k + i, l + 3, Blocks.air)
				++i
			}
			if (world.getBlock(j + 3, k - 5, l + 3) !== Blocks.air) {
				world.setBlock(j + 3, k - 5, l + 3, ModBlocks.customBrick, 2, 3)
				world.setBlock(j + 3, k - 4, l + 3, Blocks.water)
				if (random.nextInt(3) == 0) {
					StructureGenSpawner.generate(world, random, 2, j + 3, k - 4, l + 3)
				} else if (random.nextInt(3) == 0) {
					StructureGenChest.generate(world, random, 4, j + 3, k - 4, l + 3)
					world.setBlock(j + 3, k - 3, l + 3, Blocks.water)
				}
			}
			i = 0
			while (i < 6) {
				generateCorridorX(world, random, j + 6, k, l + 1 + random.nextInt(5))
				generateCorridorZ(world, random, j + 1 + random.nextInt(5), k, l + 6)
				++i
			}
		}
	}
	
	fun roomFountain(world: World, random: Random, type: Int, j: Int, k: Int, l: Int) {
		val x = 7
		val y = 6
		val z = 11
		if (this.check(world, j, k, l, x, y, z)) {
			val structure = defaultMatrixFillB(x, y, z)
			val metadata = defaultMatrixFill(x, y, z)
			var o: Int
			var i = 0
			while (i < 7) {
				o = 0
				while (o < 11) {
					structure[i][0][o] = ModBlocks.customBrick
					structure[i][5][o] = ModBlocks.customBrick
					metadata[i][0][o] = 2
					metadata[i][5][o] = 2
					++o
				}
				++i
			}
			i = 0
			while (i < 7) {
				o = 1
				while (o < 5) {
					structure[i][o][0] = ModBlocks.customBrick
					structure[i][o][10] = ModBlocks.customBrick
					metadata[i][o][0] = 2
					metadata[i][o][10] = 2
					++o
				}
				++i
			}
			i = 1
			while (i < 10) {
				o = 1
				while (o < 5) {
					structure[0][o][i] = ModBlocks.customBrick
					structure[6][o][i] = ModBlocks.customBrick
					metadata[0][o][i] = 2
					metadata[6][o][i] = 2
					++o
				}
				++i
			}
			i = 1
			while (i < 10) {
				structure[1][4][i] = ModBlocks.customBrick
				metadata[1][4][i] = 2
				++i
			}
			i = 3
			while (i < 8) {
				o = 1
				while (o < 4) {
					structure[1][o][i] = ModBlocks.customBrick
					metadata[1][o][i] = 2
					++o
				}
				++i
			}
			structure[2][1][3] = ModBlocks.customBrick
			metadata[2][1][3] = 2
			structure[2][1][7] = ModBlocks.customBrick
			metadata[2][1][7] = 2
			structure[2][1][5] = ModFluffBlocks.snowBrickSlab
			structure[1][3][5] = Blocks.flowing_lava
			if (random.nextInt(2) == 0) {
				structure[1][3][5] = Blocks.flowing_lava
			}
			i = 4
			while (i < 7) {
				structure[3][1][i] = ModBlocks.customBrick
				metadata[3][1][i] = 2
				++i
			}
			rotateGeneration(world, random, j, k, l, type, structure, metadata, x, y, z, random.nextInt(8))
			i = 0
			while (i < 6) {
				generateCorridorX(world, random, j + 6, k, l + 1 + random.nextInt(9))
				generateCorridorZ(world, random, j + 1 + random.nextInt(5), k, l + 10)
				++i
			}
		}
	}
	
	fun roomTreasury(world: World, random: Random, type: Int, j: Int, k: Int, l: Int) {
		val x = 7
		val y = 6
		val z = 11
		if (this.check(world, j, k, l, x, y, z)) {
			val structure = defaultMatrixFillB(x, y, z)
			val metadata = defaultMatrixFill(x, y, z)
			var o: Int
			var i = 0
			while (i < 7) {
				o = 0
				while (o < 11) {
					structure[i][0][o] = ModBlocks.customBrick
					structure[i][5][o] = ModBlocks.customBrick
					metadata[i][0][o] = 2
					metadata[i][5][o] = 2
					++o
				}
				++i
			}
			i = 0
			while (i < 7) {
				o = 1
				while (o < 5) {
					structure[i][o][0] = ModBlocks.customBrick
					structure[i][o][10] = ModBlocks.customBrick
					metadata[i][o][0] = 2
					metadata[i][o][10] = 2
					++o
				}
				++i
			}
			i = 1
			while (i < 10) {
				o = 1
				while (o < 5) {
					structure[0][o][i] = ModBlocks.customBrick
					structure[6][o][i] = ModBlocks.customBrick
					metadata[0][o][i] = 2
					metadata[6][o][i] = 2
					++o
				}
				++i
			}
			i = 1
			while (i < 4) {
				structure[1][i][1] = ModBlocks.customBrick
				metadata[1][i][1] = 2
				structure[2][i][1] = ModBlocks.customBrick
				metadata[2][i][1] = 2
				structure[5][i][1] = ModBlocks.customBrick
				metadata[5][i][1] = 2
				structure[1][i][3] = ModBlocks.customBrick
				metadata[1][i][3] = 2
				structure[1][i][5] = ModBlocks.customBrick
				metadata[1][i][5] = 2
				structure[1][i][7] = ModBlocks.customBrick
				metadata[1][i][7] = 2
				structure[1][i][9] = ModBlocks.customBrick
				metadata[1][i][9] = 2
				structure[2][i][9] = ModBlocks.customBrick
				metadata[2][i][9] = 2
				structure[5][i][9] = ModBlocks.customBrick
				metadata[5][i][9] = 2
				++i
			}
			i = 1
			while (i <= 4) {
				structure[1][1][i * 2] = Blocks.chest
				++i
			}
			i = 1
			while (i < 10) {
				structure[1][4][i] = ModBlocks.customBrick
				metadata[1][4][i] = 2
				structure[5][4][i] = ModBlocks.customBrick
				metadata[5][4][i] = 2
				++i
			}
			i = 2
			while (i < 5) {
				structure[i][4][1] = ModBlocks.customBrick
				metadata[i][4][1] = 2
				structure[i][4][9] = ModBlocks.customBrick
				metadata[i][4][9] = 2
				++i
			}
			rotateGeneration(world, random, j, k, l, type, structure, metadata, x, y, z, random.nextInt(8))
			i = 0
			while (i < 6) {
				generateCorridorX(world, random, j + 6, k, l + 2 + random.nextInt(7))
				generateCorridorZ(world, random, j + 2, k, l + 10)
				++i
			}
			++treasuryCount
		}
	}
	
	fun check(world: World, j: Int, k: Int, l: Int, x: Int, y: Int, z: Int): Boolean {
		var bump = 0
		for (i in -1 until x + 1) {
			for (o in -1 until z + 1) {
				for (p in -1 until y + 1) {
					if (bump > 30) {
						return false
					}
					if (world.getBlock(j + i, k + p, l + o) inln ChunkProviderNiflheim.surfaceBlocks) {
						++bump
					}
					if (world.getBlock(j + i, k + p, l + o) === ModBlocks.customBrick) {
						return false
					}
				}
			}
		}
		return true
	}
	
	fun checkGenerate(world: World, j: Int, k: Int, l: Int, x: Int, y: Int, z: Int): Boolean {
		var bump = 0
		for (c1 in -1 until x + 1) {
			for (c2 in -1 until y + 1) {
				for (c3 in 0 until z + 1) {
					if (bump > 20) {
						return false
					}
					if (world.getBlock(j + c1 * 8, k + c2, l + c3 * 8) === ModBlocks.customBrick) {
						return false
					}
					if (world.getBlock(j + c1 * 8, k + c2, l + c3 * 8) === Blocks.air) {
						++bump
					}
				}
			}
		}
		return bump > 2
	}
	
	fun defaultMatrixFillB(x: Int, y: Int, z: Int): Array<Array<Array<Block>>> {
		return Array(x) { Array(y) { Array(z) { Blocks.air } } }
	}
	
	fun defaultMatrixFill(x: Int, y: Int, z: Int): Array<Array<Array<Int>>> {
		return Array(x) { Array(y) { Array(z) { 0 } } }
	}
}