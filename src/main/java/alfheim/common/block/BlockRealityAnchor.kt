package alfheim.common.block

import alfheim.api.ModInfo
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileRealityAnchor
import net.minecraft.block.material.Material
import net.minecraft.world.World

class BlockRealityAnchor: BlockContainerMod(Material.iron) {
	
	init {
		setBlockName("RealityAnchor")
		setBlockTextureName("${ModInfo.MODID}:RealityAnchor")
		setHardness(10f)
	}
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileRealityAnchor()
}