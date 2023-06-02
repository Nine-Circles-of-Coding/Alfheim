package alfheim.common.world.dim.alfheim.customgens

import alexsocol.asjlib.ASJUtilities
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.BlockColoredSapling
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.*
import cpw.mods.fml.common.IWorldGenerator
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import vazkii.botania.common.item.ModItems
import java.util.*

class WorldGenIridescence: IWorldGenerator {
	
	override fun generate(rand: Random, chunkX: Int, chunkZ: Int, world: World, chunkGenerator: IChunkProvider, chunkProvider: IChunkProvider) {
		if (world.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim) return
		if (rand.nextInt(64) != 0) return
		
		val x = chunkX * 16 + rand.nextInt(16)
		val z = chunkZ * 16 + rand.nextInt(16)
		val y = world.getTopSolidOrLiquidBlock(x, z) - 1
		
		if (rand.nextInt(100) == 0) {
			ItemColorSeeds.addBlockSwapper(world, null, x, y, z, 1000)
			return
		}
		
		val success: Boolean
		if (rand.nextInt(3) == 0) {
			val block = world.getBlock(x, y, z)
			val bmeta = world.getBlockMetadata(x, y, z)
			
			// ItemGrassSeeds condition copy
			success = (block === Blocks.dirt || block === Blocks.grass) && bmeta == 0
			if (!success) return
			
			val type = ASJUtilities.randInBounds(3, 8, rand)
			ModItems.grassSeeds.onItemUse(ItemStack(ModItems.grassSeeds, 1, type), null, world, x, y, z, 1, 0f, 0f, 0f)
		} else {
			val color = ASJUtilities.randInBounds(0, ItemIridescent.TYPES + 1, rand)
			
			ItemColorSeeds.worldGen = true
			success = AlfheimItems.irisSeeds.onItemUse(ItemStack(AlfheimItems.irisSeeds, 1, color), null, world, x, y, z, 1, 0f, 0f, 0f)
			ItemColorSeeds.worldGen = false
		}
		
		if (!success || rand.nextInt(4) != 0) return
		
		world.setBlock(x, y + 1, z, AlfheimBlocks.irisSapling)
		(AlfheimBlocks.irisSapling as BlockColoredSapling).growTree(world, x, y + 1, z, rand)
	}
}
