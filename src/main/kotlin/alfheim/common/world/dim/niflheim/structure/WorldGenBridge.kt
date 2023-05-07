package alfheim.common.world.dim.niflheim.structure

import alfheim.common.core.helper.RotateGenerationHelper
import net.minecraft.init.Blocks
import net.minecraft.world.World
import vazkii.botania.common.block.ModBlocks
import java.util.*

object WorldGenBridge {
	
	var isGenerating = false
	
	fun generateX(world: World, random: Random, j: Int, k: Int, l: Int) {
		if (isGenerating) return
		isGenerating = true

		val type = 6
		//		if (world.getBlock(j, k, l) == Blocks.snow && world.getBlock(j, k, l + 1) == Blocks.snow) {
		if (!world.isAirBlock(j, k, l) && !world.isAirBlock(j, k, l + 1)) {
			var o = 1
			while (o < 31) {
				if (world.getBlock(j + o, k, l) !== Blocks.air && world.getBlock(j + o, k, l + 1) !== Blocks.air) {
					isGenerating = false
					return
				}
				++o
			}
			o = 1
			while (world.getBlock(j + o, k, l) === Blocks.air || world.getBlock(j + o, k, l + 1) === Blocks.air) {
				++o
			}

//			if (world.getWorldChunkManager().getBiomeGenAt(j + o, l) is BiomeGenSnow) {
			for (i in 1..o) {
				for (y in 0..2) {
					val block = if (y == 0) ModBlocks.customBrick else Blocks.air
					world.setBlock(j + i, k + y, l, block, 2, 3)
					world.setBlock(j + i, k + y, l + 1, block, 2, 3)
				}
			}
			if (o > 80)
				bridgeRoom(world, random, type, j + o / 2 + 6, k, l - 3, random.nextInt(2))
//			}
		}
		isGenerating = false
	}
	
	fun generateZ(world: World, random: Random, j: Int, k: Int, l: Int) {
		if (isGenerating) return
		isGenerating = true
		
		val type = 6
		//		if (world.getBlock(j, k, l) === Blocks.snow && world.getBlock(j + 1, k, l) === Blocks.snow) {
		if (!world.isAirBlock(j, k, l) && !world.isAirBlock(j, k, l + 1)) {
			var o = 1
			while (o < 31) {
				if (world.getBlock(j, k, l + o) !== Blocks.air && world.getBlock(j + 1, k, l + o) !== Blocks.air) {
					isGenerating = false
					return
				}
				++o
			}
			o = 1
			while (world.getBlock(j, k, l + o) === Blocks.air || world.getBlock(j + 1, k, l + o) === Blocks.air) {
				++o
			}

//			if (world.getWorldChunkManager().getBiomeGenAt(j + o, l) is BiomeGenSnow) {
			for (i in 1..o) {
				world.setBlock(j, k, l + i, ModBlocks.customBrick, 2, 3)
				world.setBlock(j + 1, k, l + i, ModBlocks.customBrick, 2, 3)
			}
			
			if (o > 80) {
				if (random.nextInt(2) == 0) {
					bridgeRoom(world, random, type, j - 3, k, l + o / 2 + 6, 4)
				} else {
					bridgeRoom(world, random, type, j - 3, k, l + o / 2 + 6, 6)
				}
			}
			//			}
		}
		isGenerating = false
	}
	
	fun bridgeRoom(world: World, random: Random, type: Int, j: Int, k: Int, l: Int, rotationID: Int) {
		val x = 12
		val y = 5
		val z = 8
		val structure = Array(x) { Array(y) { Array(z) { Blocks.air } } }
		val metadata = Array(x) { Array(y) { Array(z) { 0 } } }
		
		for (a in 0 until x)
			for (c in 1 until z - 1)
				for (b in arrayOf(0, 4)) {
					structure[a][b][c] = ModBlocks.customBrick
					metadata[a][b][c] = 2
				}
		
		for (a in arrayOf(0, 11))
			for (b in arrayOf(0, 4))
				for (c in arrayOf(1, 6))
					structure[a][b][c] = Blocks.air
		
		for (a in 7 until 10)
			for (b in arrayOf(0, 4))
				for (c in arrayOf(0, 7)) {
					structure[a][b][c] = ModBlocks.customBrick
					metadata[a][b][c] = 2
				}
		
		for (a in arrayOf(2, 6, 10))
			for (b in 1 until 4)
				for (c in arrayOf(1, 6)) {
					structure[a][b][c] = ModBlocks.customBrick
					metadata[a][b][c] = 2
				}
		
		RotateGenerationHelper.rotateGeneration(world, random, j, k, l, type, structure, metadata, x, y, z, rotationID)
		genSpawners(world, random, j, k, l, rotationID, x)
	}
	
	fun genSpawners(world: World, random: Random, i: Int, j: Int, k: Int, rotationID: Int, x: Int) {
			when (rotationID) {
				0       -> {
					StructureGenSpawner.generate(world, random, 0, i + 8, j + 1, k + 1)
					StructureGenSpawner.generate(world, random, 0, i + 8, j + 1, k + 6)
				}
				
				1       -> {
					StructureGenSpawner.generate(world, random, 0, i + x - 9, j + 1, k + 1)
					StructureGenSpawner.generate(world, random, 0, i + x - 9, j + 1, k + 6)
				}
				
				4       -> {
					StructureGenSpawner.generate(world, random, 0, i + 1, j + 1, k + 8)
					StructureGenSpawner.generate(world, random, 0, i + 6, j + 1, k + 8)
				}
				
				6       -> {
					StructureGenSpawner.generate(world, random, 0, i + 1, j + 1, k + x - 9)
					StructureGenSpawner.generate(world, random, 0, i + 6, j + 1, k + x - 9)
				}
			}
	}
}