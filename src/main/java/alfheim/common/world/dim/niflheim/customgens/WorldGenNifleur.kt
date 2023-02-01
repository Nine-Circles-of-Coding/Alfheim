package alfheim.common.world.dim.niflheim.customgens

import alfheim.common.block.*
import net.minecraft.world.World
import java.util.*

object WorldGenNifleur {
	
	fun generate(world: World, random: Random, j: Int, k: Int, l: Int): Boolean {
		if (world.getBlock(j, k, l) !== AlfheimBlocks.niflheimBlock || world.getBlockMetadata(j, k, l) != 0)
			return false
		
		return world.setBlock(j, k, l, AlfheimBlocks.niflheimBlock, BlockNiflheim.NiflheimBlockMetas.ORE.I, 3)
	}
}