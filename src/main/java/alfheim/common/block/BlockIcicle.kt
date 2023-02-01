package alfheim.common.block

import net.minecraft.block.material.Material
import net.minecraft.world.World
import java.util.*

class BlockIcicle: BlockHang(Material.ice, "Icicle", 4) {
	
	init {
		setBlockBounds(0.25f, 0.1f, 0.25f, 0.75f, 1.0f, 0.75f)
	}
	
	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = null
	
	override fun canBlockStay(world: World, x: Int, y: Int, z: Int): Boolean {
		val mat = world.getBlock(x, y + 1, z).material
		return mat === Material.snow || mat === Material.craftedSnow || mat === Material.ice || mat === Material.packedIce
	}
}