package alfheim.common.world.dim.niflheim.customgens

import alfheim.common.block.AlfheimBlocks.icicle
import alfheim.common.block.AlfheimBlocks.stalactite
import alfheim.common.block.AlfheimBlocks.stalagmite
import alfheim.common.world.dim.niflheim.biome.*
import net.minecraft.world.World
import net.minecraft.world.biome.BiomeGenBase
import java.util.*

object WorldGenCaveHangs {
	
	fun generate(world: World, random: Random, x: Int, y: Int, z: Int, biome: BiomeGenBase): Boolean {
		if (biome is BiomeGenIce) {
			if (stalagmite.canBlockStay(world, x, y + 1, z)&& world.isAirBlock(x, y + 1, z))
				world.setBlock(x, y + 1, z, stalagmite, random.nextInt(8), 3)
			
			if (stalactite.canBlockStay(world, x, y, z) && world.isAirBlock(x, y, z))
				world.setBlock(x, y, z, stalactite, random.nextInt(8), 3)
		} else if (biome is BiomeGenSnow) {
			if (world.isAirBlock(x, y - 1, z) && icicle.canBlockStay(world, x, y - 1, z))
				world.setBlock(x, y - 1, z, icicle, random.nextInt(4), 3)
		}
		
		return true
	}
}