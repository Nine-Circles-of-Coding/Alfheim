package alfheim.common.world.dim.niflheim.customgens

import alexsocol.asjlib.inln
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.alt.BlockAltLeaves
import alfheim.common.world.dim.niflheim.ChunkProviderNiflheim
import net.minecraft.init.Blocks
import net.minecraft.world.World
import java.util.*

object WorldGenRoot {
	
	fun generate(world: World, random: Random, j: Int, k: Int, l: Int): Boolean {
		
		if (!world.isAirBlock(j, k, l))
			return false
		
		if (world.getBlock(j, k + 1, l) inln ChunkProviderNiflheim.surfaceBlocks)
			return false
		
		world.setBlock(j, k, l, AlfheimBlocks.altWood1, BlockAltLeaves.yggMeta % 4, 3)
		
		for (var6 in 0..1499) {
			val var7 = j + random.nextInt(8) - random.nextInt(8)
			val var8 = k - random.nextInt(12)
			val var9 = l + random.nextInt(8) - random.nextInt(8)
			
			if (world.getBlock(var7, var8, var9) === Blocks.air) {
				var var10 = 0
				
				for (var11 in 0..5) {
					var var12 = Blocks.air
					
					if (var11 == 0)
						var12 = world.getBlock(var7 - 1, var8, var9)
					
					if (var11 == 1)
						var12 = world.getBlock(var7 + 1, var8, var9)
					
					if (var11 == 2)
						var12 = world.getBlock(var7, var8 - 1, var9)
					
					if (var11 == 3)
						var12 = world.getBlock(var7, var8 + 1, var9)
					
					if (var11 == 4)
						var12 = world.getBlock(var7, var8, var9 - 1)
					
					if (var11 == 5)
						var12 = world.getBlock(var7, var8, var9 + 1)
					
					if (var12 === AlfheimBlocks.altWood1)
						++var10
				}
				
				if (var10 == 1)
					world.setBlock(var7, var8, var9, AlfheimBlocks.altWood1, BlockAltLeaves.yggMeta % 4, 3)
			}
		}
		
		return true
	}
}