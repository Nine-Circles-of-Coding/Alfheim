package alfheim.common.block

import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.world.World
import java.util.*

open class BlockHang(mat: Material, name: String, sub: Int): BlockModMeta(mat, sub, ModInfo.MODID, name, AlfheimTab, 0.3f) {
	
	override fun onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block) = checkChange(world, x, y, z)
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random) = checkChange(world, x, y, z)
	
	fun checkChange(world: World, x: Int, y: Int, z: Int) {
		if (canBlockStay(world, x, y, z)) return
		dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0)
		world.setBlockToAir(x, y, z)
	}
	
	override fun canPlaceBlockAt(world: World, x: Int, y: Int, z: Int) = canBlockStay(world, x, y, z)
	
	override fun getCollisionBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int) = null
	
	override fun isOpaqueCube() = false
	
	override fun renderAsNormalBlock() = false
	
	override fun getRenderType() = 1
}