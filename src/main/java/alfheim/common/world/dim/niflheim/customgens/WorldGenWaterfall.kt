package alfheim.common.world.dim.niflheim.customgens

import alexsocol.asjlib.*
import alfheim.common.world.dim.niflheim.ChunkProviderNiflheim
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.world.World
import java.util.*

object WorldGenWaterfall {
	
	fun generateRandom(world: World, random: Random?, j: Int, k: Int, l: Int, liquidID: Block): Boolean {
		if (world.getBlock(j, k + 1, l) inln ChunkProviderNiflheim.surfaceBlocks)
			return false
		
		if (world.getBlock(j, k, l) inln ChunkProviderNiflheim.surfaceBlocks)
			return false
		
		var var6 = 0
		if (world.getBlock(j - 1, k, l) inl ChunkProviderNiflheim.surfaceBlocks) {
			++var6
		}
		if (world.getBlock(j + 1, k, l) inl ChunkProviderNiflheim.surfaceBlocks) {
			++var6
		}
		if (world.getBlock(j, k, l - 1) inl ChunkProviderNiflheim.surfaceBlocks) {
			++var6
		}
		if (world.getBlock(j, k, l + 1) inl ChunkProviderNiflheim.surfaceBlocks) {
			++var6
		}
		if (world.getBlock(j, k - 1, l) inl ChunkProviderNiflheim.surfaceBlocks) {
			++var6
		}
		var var7 = 0
		if (world.isAirBlock(j - 1, k, l)) {
			++var7
		}
		if (world.isAirBlock(j + 1, k, l)) {
			++var7
		}
		if (world.isAirBlock(j, k, l - 1)) {
			++var7
		}
		if (world.isAirBlock(j, k, l + 1)) {
			++var7
		}
		if (world.isAirBlock(j, k - 1, l)) {
			++var7
		}
		if (var6 == 4 && var7 == 1) {
			world.setBlock(j, k, l, liquidID)
			world.scheduledUpdatesAreImmediate = true
			liquidID.updateTick(world, j, k, l, random)
			world.scheduledUpdatesAreImmediate = false
		}
		return true
	}
	
	fun generateDirect(world: World, random: Random?, j: Int, k: Int, l: Int, liquidID: Block, poolID: Block): Boolean {
		return if (world.getBlock(j, k, l).let { it === poolID || it === liquidID } && world.getBlock(j, k + 1, l) === Blocks.air) {
			var i = 1
			while (world.getBlock(j, k + i, l) === Blocks.air || world.getBlock(j, k + i + 1, l) === Blocks.air) {
				++i
			}
			world.setBlock(j, k + i, l, liquidID)
			world.scheduledUpdatesAreImmediate = true
			liquidID.updateTick(world, j, k + i, l, random)
			world.scheduledUpdatesAreImmediate = false
			true
		} else {
			false
		}
	}
	
	fun generateFrozenDirect(world: World, j: Int, k: Int, l: Int, block: Block): Boolean {
		return if (world.getBlock(j, k, l).let { it === Blocks.ice || it === block } && world.getBlock(j, k + 1, l) === Blocks.air) {
			var i = 1
			while (world.getBlock(j, k + i, l) === Blocks.air || world.getBlock(j, k + i + 1, l) === Blocks.air) {
				world.setBlock(j, k + i, l, block)
				++i
			}
			world.setBlock(j, k + i, l, block)
			true
		} else {
			false
		}
	}
}