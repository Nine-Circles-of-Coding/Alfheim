package alfheim.common.world.dim.alfheim.customgens

import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.AlfheimConfigHandler
import cpw.mods.fml.common.IWorldGenerator
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import java.util.*

class WorldGenGrapesWhiteAlfheim(val perChunk: Int): IWorldGenerator {
	
	override fun generate(random: Random, chunkX: Int, chunkZ: Int, world: World, chunkGenerator: IChunkProvider?, chunkProvider: IChunkProvider?) {
		if (world.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim) return
		
		for (i in 0 until perChunk) {
			val x = chunkX * 16 + random.nextInt(16) + 8
			val z = chunkZ * 16 + random.nextInt(16) + 8
			val y = world.getTopLiquidBlock(x, z)
			
			if (AlfheimBlocks.grapesWhite.canBlockStay(world, x, y, z))
 				if (world.isAirBlock(x, y, z)) {
					world.setBlock(x, y, z, AlfheimBlocks.grapesWhite, random.nextInt(3), 3)
				}
		}
	}
	
	fun World.getTopLiquidBlock(x: Int, y: Int): Int {
		val chunk = getChunkFromBlockCoords(x, y)
		var j = chunk.topFilledSegment + 15
		val x = x and 15
		val y = y and 15
		while (j > 0) {
			val block = chunk.getBlock(x, j, y)
			if (block.material.isLiquid)
				return j + 1
			
			--j
		}
		return -1
	}
}