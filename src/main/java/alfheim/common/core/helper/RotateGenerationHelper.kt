package alfheim.common.core.helper

import alfheim.common.world.dim.niflheim.structure.StructureGenChest.generate
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.world.World
import java.util.*

object RotateGenerationHelper {
	
	fun rotateGeneration(world: World, random: Random, i: Int, j: Int, k: Int, chestSizeType: Int, structure: Array<Array<Array<Block>>>, metadata: Array<Array<Array<Int>>>, x: Int, y: Int, z: Int, rotationID: Int) {
		when (rotationID) {
			0 -> genRotated0(world, random, i, j, k, chestSizeType, structure, metadata, x, y, z)
			1 -> genRotated1(world, random, i, j, k, chestSizeType, structure, metadata, x, y, z)
			2 -> genRotated2(world, random, i, j, k, chestSizeType, structure, metadata, x, y, z)
			3 -> genRotated3(world, random, i, j, k, chestSizeType, structure, metadata, x, y, z)
			4 -> genRotated4(world, random, i, j, k, chestSizeType, structure, metadata, x, y, z)
			5 -> genRotated5(world, random, i, j, k, chestSizeType, structure, metadata, x, y, z)
			6 -> genRotated6(world, random, i, j, k, chestSizeType, structure, metadata, x, y, z)
			7 -> genRotated7(world, random, i, j, k, chestSizeType, structure, metadata, x, y, z)
		}
	}
	
	private fun genRotated0(world: World, random: Random, i: Int, j: Int, k: Int, type: Int, structure: Array<Array<Array<Block>>>, metadata: Array<Array<Array<Int>>>, x: Int, y: Int, z: Int) {
		for (a in 0 until x) {
			for (b in 0 until y) {
				for (c in 0 until z) {
					if (structure[a][b][c] === Blocks.chest) {
						generate(world, random, type, i + a, j + b, k + c)
					} else {
						world.setBlock(i + a, j + b, k + c, structure[a][b][c], metadata[a][b][c], 3)
					}
				}
			}
		}
	}
	
	private fun genRotated1(world: World, random: Random, i: Int, j: Int, k: Int, type: Int, structure: Array<Array<Array<Block>>>, metadata: Array<Array<Array<Int>>>, x: Int, y: Int, z: Int) {
		for (a in 0 until x) {
			for (b in 0 until y) {
				for (c in 0 until z) {
					if (structure[x - a - 1][b][c] === Blocks.chest) {
						generate(world, random, type, i + a, j + b, k + c)
					} else {
						world.setBlock(i + a, j + b, k + c, structure[x - a - 1][b][c], metadata[x - a - 1][b][c], 3)
					}
				}
			}
		}
	}
	
	private fun genRotated2(world: World, random: Random, i: Int, j: Int, k: Int, type: Int, structure: Array<Array<Array<Block>>>, metadata: Array<Array<Array<Int>>>, x: Int, y: Int, z: Int) {
		for (a in 0 until x) {
			for (b in 0 until y) {
				for (c in 0 until z) {
					if (structure[a][b][z - c - 1] === Blocks.chest) {
						generate(world, random, type, i + a, j + b, k + c)
					} else {
						world.setBlock(i + a, j + b, k + c, structure[a][b][z - c - 1], metadata[a][b][z - c - 1], 3)
					}
				}
			}
		}
	}
	
	private fun genRotated3(world: World, random: Random, i: Int, j: Int, k: Int, type: Int, structure: Array<Array<Array<Block>>>, metadata: Array<Array<Array<Int>>>, x: Int, y: Int, z: Int) {
		for (a in 0 until x) {
			for (b in 0 until y) {
				for (c in 0 until z) {
					if (structure[x - a - 1][b][z - c - 1] === Blocks.chest) {
						generate(world, random, type, i + a, j + b, k + c)
					} else {
						world.setBlock(i + a, j + b, k + c, structure[x - a - 1][b][z - c - 1], metadata[x - a - 1][b][z - c - 1], 3)
					}
				}
			}
		}
	}
	
	private fun genRotated4(world: World, random: Random, i: Int, j: Int, k: Int, type: Int, structure: Array<Array<Array<Block>>>, metadata: Array<Array<Array<Int>>>, x: Int, y: Int, z: Int) {
		val structureTransposed = Array(z) { Array(y) { Array(x) { Blocks.air } } }
		val metadataTransposed = Array(z) { Array(y) { Array(x) { 0 } } }
		for (a in 0 until x) {
			for (b in 0 until y) {
				for (c in 0 until z) {
					structureTransposed[c][b][a] = structure[a][b][c]
					metadataTransposed[c][b][a] = metadata[a][b][c]
				}
			}
		}
		for (a in 0 until z) {
			for (b in 0 until y) {
				for (c in 0 until x) {
					if (structureTransposed[a][b][c] === Blocks.chest) {
						generate(world, random, type, i + a, j + b, k + c)
					} else {
						world.setBlock(i + a, j + b, k + c, structureTransposed[a][b][c], metadataTransposed[a][b][c], 3)
					}
				}
			}
		}
	}
	
	private fun genRotated5(world: World, random: Random, i: Int, j: Int, k: Int, type: Int, structure: Array<Array<Array<Block>>>, metadata: Array<Array<Array<Int>>>, x: Int, y: Int, z: Int) {
		val structureTransposed = Array(z) { Array(y) { Array(x) { Blocks.air } } }
		val metadataTransposed = Array(z) { Array(y) { Array(x) { 0 } } }
		for (a in 0 until x) {
			for (b in 0 until y) {
				for (c in 0 until z) {
					structureTransposed[c][b][a] = structure[a][b][c]
					metadataTransposed[c][b][a] = metadata[a][b][c]
				}
			}
		}
		for (a in 0 until z) {
			for (b in 0 until y) {
				for (c in 0 until x) {
					if (structureTransposed[z - a - 1][b][c] === Blocks.chest) {
						generate(world, random, type, i + a, j + b, k + c)
					} else {
						world.setBlock(i + a, j + b, k + c, structureTransposed[z - a - 1][b][c], metadataTransposed[z - a - 1][b][c], 3)
					}
				}
			}
		}
	}
	
	private fun genRotated6(world: World, random: Random, i: Int, j: Int, k: Int, type: Int, structure: Array<Array<Array<Block>>>, metadata: Array<Array<Array<Int>>>, x: Int, y: Int, z: Int) {
		val structureTransposed = Array(z) { Array(y) { Array(x) { Blocks.air } } }
		val metadataTransposed = Array(z) { Array(y) { Array(x) { 0 } } }
		for (a in 0 until x) {
			for (b in 0 until y) {
				for (c in 0 until z) {
					structureTransposed[c][b][a] = structure[a][b][c]
					metadataTransposed[c][b][a] = metadata[a][b][c]
				}
			}
		}
		for (a in 0 until z) {
			for (b in 0 until y) {
				for (c in 0 until x) {
					if (structureTransposed[a][b][x - c - 1] === Blocks.chest) {
						generate(world, random, type, i + a, j + b, k + c)
					} else {
						world.setBlock(i + a, j + b, k + c, structureTransposed[a][b][x - c - 1], metadataTransposed[a][b][x - c - 1], 3)
					}
				}
			}
		}
	}
	
	private fun genRotated7(world: World, random: Random, i: Int, j: Int, k: Int, type: Int, structure: Array<Array<Array<Block>>>, metadata: Array<Array<Array<Int>>>, x: Int, y: Int, z: Int) {
		val structureTransposed = Array(z) { Array(y) { Array(x) { Blocks.air } } }
		val metadataTransposed = Array(z) { Array(y) { Array(x) { 0 } } }
		for (a in 0 until x) {
			for (b in 0 until y) {
				for (c in 0 until z) {
					structureTransposed[c][b][a] = structure[a][b][c]
					metadataTransposed[c][b][a] = metadata[a][b][c]
				}
			}
		}
		for (a in 0 until z) {
			for (b in 0 until y) {
				for (c in 0 until x) {
					if (structureTransposed[z - a - 1][b][x - c - 1] === Blocks.chest) {
						generate(world, random, type, i + a, j + b, k + c)
					} else {
						world.setBlock(i + a, j + b, k + c, structureTransposed[z - a - 1][b][x - c - 1], metadataTransposed[z - a - 1][b][x - c - 1], 3)
					}
				}
			}
		}
	}
}