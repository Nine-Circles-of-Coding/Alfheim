package alfheim.common.block

import alexsocol.asjlib.extendables.block.BlockModContainerMeta
import alexsocol.asjlib.render.IGlowingLayerBlock
import alfheim.api.ModInfo
import alfheim.api.lib.LibRenderIDs
import alfheim.client.core.helper.IconHelper
import alfheim.common.block.tile.TileDomainLobby
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.*
import net.minecraft.world.*

class BlockDomainDoor: BlockModContainerMeta(Material.rock, 5, ModInfo.MODID, "DomainDoor", hard = -1f, resist = Float.MAX_VALUE), IGlowingLayerBlock {

	lateinit var iconCore: IIcon
	lateinit var iconGlow: IIcon
	lateinit var iconDoor: IIcon
	lateinit var iconPillar: IIcon
	lateinit var iconPillarTop: IIcon
	
	override fun setBlockBoundsBasedOnState(world: IBlockAccess, x: Int, y: Int, z: Int) {
		when (world.getBlockMetadata(x, y, z)) {
			2    -> setBlockBounds(0f, 0f, 0.25f, 1f, 1f, 0.75f)
			4    -> setBlockBounds(0f, 0f, 0f, 1f, 0.5f, 1f)
			else -> setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f)
		}
	}
	
	override fun addCollisionBoxesToList(world: World, x: Int, y: Int, z: Int, aabb: AxisAlignedBB?, list: MutableList<Any?>?, collider: Entity?) {
		setBlockBoundsBasedOnState(world, x, y, z)
		
		super.addCollisionBoxesToList(world, x, y, z, aabb, list, collider)
	}
	
	override fun registerBlockIcons(reg: IIconRegister) {
		blockIcon = IconHelper.forName(reg, "DomainLobbyWall")
		iconCore = IconHelper.forName(reg, "DomainLobbyCore")
		iconGlow = IconHelper.forName(reg, "DomainLobbyCore_glow")
		iconDoor = IconHelper.forName(reg, "DomainLobbyDoor")
		iconPillar = IconHelper.forName(reg, "DomainLobbyPillar")
		iconPillarTop = IconHelper.forName(reg, "DomainLobbyPillarTop")
	}
	
	override fun getIcon(side: Int, meta: Int) = when (meta) {
		1 -> iconCore
		2 -> iconDoor
		3, 4 -> if (side < 2) iconPillarTop else iconPillar
		else -> blockIcon!!
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileDomainLobby ?: return false
		return tile.onBlockActivated(player)
	}
	
	override fun hasTileEntity(meta: Int) = meta == 1
	override fun createNewTileEntity(world: World?, meta: Int) = if (hasTileEntity(meta)) TileDomainLobby() else null
	override fun getRenderType() = LibRenderIDs.idDomainDoor
	override fun getGlowIcon(side: Int, meta: Int) = iconGlow
	override fun isOpaqueCube() = false
	override fun renderAsNormalBlock() = false
}
