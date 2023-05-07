package alfheim.common.world.dim.niflheim.structure

import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.util.WeightedRandomChestContent
import net.minecraft.world.World
import net.minecraftforge.common.ChestGenHooks
import java.util.*

object StructureGenChest {
	
	fun generate(world: World, random: Random, sizeType: Int, i: Int, j: Int, k: Int): Boolean {
		world.setBlock(i, j, k, Blocks.chest, 0, 3)
		val tile = world.getTileEntity(i, j, k) as? TileEntityChest ?: return false
		
		val size = when (sizeType) {
			0 -> 4 + random.nextInt(4)
			1 -> 4 + random.nextInt(6)
			2 -> 5 + random.nextInt(3)
			3 -> 10 + random.nextInt(5)
			4 -> 5 + random.nextInt(3)
			5 -> 1 + random.nextInt(2)
			6 -> 2 + random.nextInt(3)
			7 -> 4 + random.nextInt(7)
			else -> return false
		}
		
		WeightedRandomChestContent.generateChestContents(random, ChestGenHooks.getItems(ChestGenHooks.DUNGEON_CHEST, random), tile, size)
		
		return true
	}
}