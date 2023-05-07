package alfheim.common.block.tile

import alfheim.common.block.AlfheimBlocks
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import vazkii.botania.common.block.tile.TileFloatingFlower

class TileFloatingFlowerRainbow: TileFloatingFlower() {
	
	override fun getDisplayStack() = getDisplayStack { getBlockMetadata() }

	companion object {
		fun getDisplayStack(meta: () -> Int): ItemStack {
			if (forcedStack != null) {
				try {
					return forcedStack
				} finally {
					forcedStack = null
				}
			}
			
			return when(meta()) {
				0 -> ItemStack(AlfheimBlocks.rainbowGrass, 1, 3)
				else -> ItemStack(Blocks.red_flower)
			}
		}
	}
}
