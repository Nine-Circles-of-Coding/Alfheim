package alfheim.common.block

import alexsocol.asjlib.extendables.block.BlockModContainer
import alfheim.common.block.tile.TileRift
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.world.World

class BlockRift: BlockModContainer(Material.portal) {
	
	init {
		setBlockBounds(0.46875f, 0.46875f, 0.46875f, 0.53125f, 0.53125f, 0.53125f)
		setBlockName("Rift")
		setBlockUnbreakable()
		setLightLevel(0.25f)
	}
	
	override fun isOpaqueCube() = false
	override fun registerBlockIcons(reg: IIconRegister?) = Unit
	override fun getIcon(side: Int, meta: Int) = AlfheimBlocks.helheimBlock.getIcon(side, meta)!!
	override fun createNewTileEntity(world: World, meta: Int) = TileRift()
}
