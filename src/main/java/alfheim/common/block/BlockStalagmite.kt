package alfheim.common.block

import net.minecraft.block.material.Material
import net.minecraft.world.World
import java.util.*

class BlockStalagmite: BlockHang(Material.rock, "Stalagmite", 8) {
	
	init {
		setBlockBounds(0.25f, 0.0f, 0.25f, 0.75f, 0.9f, 0.75f)
		setLightLevel(0.3f)
	}
	
	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = null

	override fun canBlockStay(world: World, x: Int, y: Int, z: Int) = world.getBlock(x, y - 1, z).material === Material.rock
}