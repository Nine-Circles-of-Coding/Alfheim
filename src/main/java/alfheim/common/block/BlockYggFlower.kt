package alfheim.common.block

import alexsocol.asjlib.extendables.block.BlockModContainer
import alfheim.api.ModInfo
import alfheim.common.block.alt.BlockAltLeaves
import alfheim.common.block.tile.TileYggFlower
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.*

class BlockYggFlower: BlockModContainer(Material.plants) {
	
	init {
		setBlockBounds(0f, 0f, 0f, 1f, 0.25f, 1f)
		setBlockName("YggdrasilFlower")
		setBlockTextureName("${ModInfo.MODID}:YggdrasilFlower")
		setBlockUnbreakable()
		setLightOpacity(0)
	}
	
	override fun getLightValue(world: IBlockAccess, x: Int, y: Int, z: Int) =
		if ((world.getTileEntity(x, y, z) as? TileYggFlower)?.hasFruit == true) 15 else 0
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float) =
		(world.getTileEntity(x, y, z) as? TileYggFlower)?.harvestFruit() == true
	
	override fun canBlockStay(world: World, x: Int, y: Int, z: Int) =
		world.getBlock(x, y - 1, z) === AlfheimBlocks.altLeaves && world.getBlockMetadata(x, y - 1, z) % 8 == BlockAltLeaves.yggMeta
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileYggFlower()
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	override fun isOpaqueCube() = false
	override fun renderAsNormalBlock() = false
	override fun getRenderType() = -1
}
