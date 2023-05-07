package alfheim.common.block

import net.minecraft.block.material.Material
import net.minecraft.world.World
import java.util.*

class BlockStalactite: BlockHang(Material.rock, "Stalactite", 8) {
	
	init {
		setBlockBounds(0.25f, 0.1f, 0.25f, 0.75f, 1.0f, 0.75f)
		setLightLevel(0.3f)
	}

	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = null
	
	override fun canBlockStay(world: World, i: Int, j: Int, k: Int) = world.getBlock(i, j + 1, k).material === Material.rock
}