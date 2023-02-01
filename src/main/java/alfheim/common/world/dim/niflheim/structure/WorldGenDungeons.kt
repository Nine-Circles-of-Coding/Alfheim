package alfheim.common.world.dim.niflheim.structure

import alfheim.common.block.*
import alfheim.common.world.dim.niflheim.biome.*
import net.minecraft.init.Blocks
import net.minecraft.world.World
import net.minecraft.world.biome.BiomeGenBase
import vazkii.botania.common.block.ModBlocks
import java.util.*

object WorldGenDungeons {
	
	fun generate(world: World, random: Random, par3: Int, par4: Int, par5: Int, biomegenbase: BiomeGenBase?): Boolean {
		val byte0: Byte = 3
		val i = random.nextInt(2) + 2
		val j = random.nextInt(2) + 2
		var k = 0
		val type: Byte = 4
		var chestX: Int
		var chestZ: Int
		var chestGenerate = par3 - i - 1
		while (chestGenerate <= par3 + i + 1) {
			chestX = par4 - 1
			while (chestX <= par4 + byte0 + 1) {
				chestZ = par5 - j - 1
				while (chestZ <= par5 + j + 1) {
					val material = world.getBlock(chestGenerate, chestX, chestZ).material
					if (chestX == par4 - 1 && !material.isSolid) {
						return false
					}
					if (chestX == par4 + byte0 + 1 && !material.isSolid) {
						return false
					}
					if ((chestGenerate == par3 - i - 1 || chestGenerate == par3 + i + 1 || chestZ == par5 - j - 1 || chestZ == par5 + j + 1) && chestX == par4 && world.isAirBlock(chestGenerate, chestX, chestZ) && world.isAirBlock(chestGenerate, chestX + 1, chestZ)) {
						++k
					}
					++chestZ
				}
				++chestX
			}
			++chestGenerate
		}
		
		if (k !in 1..5) return false
		
		chestGenerate = par3 - i - 1
		while (chestGenerate <= par3 + i + 1) {
			chestX = par4 + byte0
			while (chestX >= par4 - 1) {
				chestZ = par5 - j - 1
				while (chestZ <= par5 + j + 1) {
					if (chestGenerate != par3 - i - 1 && chestX != par4 - 1 && chestZ != par5 - j - 1 && chestGenerate != par3 + i + 1 && chestX != par4 + byte0 + 1 && chestZ != par5 + j + 1) {
						world.setBlock(chestGenerate, chestX, chestZ, Blocks.air)
					} else if (chestX >= 0 && !world.getBlock(chestGenerate, chestX - 1, chestZ).material.isSolid) {
						world.setBlock(chestGenerate, chestX, chestZ, Blocks.air)
					} else if (world.getBlock(chestGenerate, chestX, chestZ).material.isSolid) {
						when (biomegenbase) {
							is BiomeGenIce  -> {
								world.setBlock(chestGenerate, chestX, chestZ, AlfheimBlocks.niflheimBlock, BlockNiflheim.NiflheimBlockMetas.COBBLESTONE.I, 3)
							}
							is BiomeGenSnow   -> {
								world.setBlock(chestGenerate, chestX, chestZ, ModBlocks.customBrick, 2, 3)
							}
							is BiomeGenPoison -> {
								world.setBlock(chestGenerate, chestX, chestZ, AlfheimBlocks.niflheimBlock, BlockNiflheim.NiflheimBlockMetas.COBBLESTONE.I, 3)
							}
						}
					}
					++chestZ
				}
				--chestX
			}
			++chestGenerate
		}
		chestGenerate = 0
		while (chestGenerate < 2) {
			chestX = par3 + random.nextInt(i * 2 + 1) - i
			chestZ = par5 + random.nextInt(j * 2 + 1) - j
			if (world.isAirBlock(chestX, par4, chestZ)) {
				StructureGenChest.generate(world, random, type.toInt(), chestX, par4, chestZ)
			}
			++chestGenerate
		}
		StructureGenSpawner.generate(world, random, 4, par3, par4, par5)
		return true
	}
}