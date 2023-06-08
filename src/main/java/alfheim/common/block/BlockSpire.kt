package alfheim.common.block

import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileSpire
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.world.World

class BlockSpire: BlockContainerMod(Material.iron) {
	
	init {
		setBlockBounds(0f, 0f, 0f, 1f, 2f, 1f)
		setBlockName("Spire")
		setHardness(20f)
	}
	
	override fun renderAsNormalBlock() = false
	override fun isOpaqueCube() = false
	override fun getRenderType() = LibRenderIDs.idSpire
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	override fun createNewTileEntity(world: World, meta: Int) = TileSpire()
}
