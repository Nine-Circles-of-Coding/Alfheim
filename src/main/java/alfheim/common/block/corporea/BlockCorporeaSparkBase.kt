package alfheim.common.block.corporea

import alfheim.client.core.helper.IconHelper
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.corporea.TileCorporeaSparkBase
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.util.IIcon
import net.minecraft.world.World

class BlockCorporeaSparkBase: BlockContainerMod(Material.iron) {
	
	lateinit var sideIcon: IIcon
	
	init {
		setBlockBounds(0.375f, 0f, 0.375f, 0.625f, 1f, 0.625f)
		setBlockName("CorporeaSparkBase")
		setHardness(5f)
		setLightOpacity(0)
	}
	
	override fun isOpaqueCube() = false
	
	override fun registerBlockIcons(reg: IIconRegister) {
		blockIcon = IconHelper.forBlock(reg, this)
		sideIcon = IconHelper.forBlock(reg, this, "Side")
	}
	
	override fun getIcon(side: Int, meta: Int) = if (side > 1) sideIcon else blockIcon!!
	
	override fun createNewTileEntity(world: World, meta: Int) = TileCorporeaSparkBase()
}
