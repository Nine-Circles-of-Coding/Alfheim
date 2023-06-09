package alfheim.common.block

import alexsocol.asjlib.*
import alfheim.AlfheimCore
import alfheim.common.block.base.BlockMod
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.AlfheimTab
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.*
import net.minecraft.item.ItemStack
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.*
import java.util.*

class BlockSnowLayer: BlockMod(Material.snow) {
	
	init {
		setBlockBounds(0f, 0f, 0f, 1f, 0.125f, 1f)
		setBlockName("SnowLayer")
		setCreativeTab(AlfheimTab)
		setHardness(0.1f)
		setHarvestLevel("shovel", 0)
		stepSound = soundTypeSnow
		
		setSizeForMeta(0)
		tickRandomly = true
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) {
		blockIcon = reg.registerIcon("snow")
	}
	
	override fun getCollisionBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB {
		val l = world.getBlockMetadata(x, y, z) and 7
		val f = 0.125f
		return getBoundingBox(x + minX, y + minY, z + minZ, x + maxX, (y + l * f), z + maxZ)
	}
	
	override fun isOpaqueCube() = false
	override fun renderAsNormalBlock() = false
	override fun setBlockBoundsForItemRender() = setSizeForMeta(0)
	override fun setBlockBoundsBasedOnState(world: IBlockAccess, x: Int, y: Int, z: Int) = setSizeForMeta(world.getBlockMetadata(x, y, z))
	
	fun setSizeForMeta(meta: Int) {
		val j = meta and 7
		val f = (2 * (1 + j)).F / 16f
		setBlockBounds(0f, 0f, 0f, 1f, f, 1f)
	}
	
	override fun canPlaceBlockAt(world: World, x: Int, y: Int, z: Int): Boolean {
		val block = world.getBlock(x, y - 1, z)
		return if (block !== Blocks.ice && block !== Blocks.packed_ice) if (block.isLeaves(world, x, y - 1, z)) true else if (block === this && world.getBlockMetadata(x, y - 1, z) and 7 == 7) true else block.isOpaqueCube && block.material.blocksMovement() else false
	}
	
	override fun onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block?) {
		func_150155_m(world, x, y, z)
	}
	
	private fun func_150155_m(world: World, x: Int, y: Int, z: Int): Boolean {
		return if (!canPlaceBlockAt(world, x, y, z)) {
			world.setBlockToAir(x, y, z)
			false
		} else
			true
	}
	
	override fun harvestBlock(world: World, player: EntityPlayer?, x: Int, y: Int, z: Int, meta: Int) {
		super.harvestBlock(world, player, x, y, z, meta)
		world.setBlockToAir(x, y, z)
	}
	
	override fun getItemDropped(meta: Int, rand: Random?, fortune: Int) = Items.snowball!!
	
	override fun quantityDropped(rand: Random?) = 1
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, rand: Random?) {
		if (!AlfheimCore.winter && world.provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim && !world.isRemote && world.rand.nextInt(20) != 0)
			world.setBlockToAir(x, y, z)
	}
	
	@SideOnly(Side.CLIENT)
	override fun shouldSideBeRendered(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int) =
		if (side == 1) true else super.shouldSideBeRendered(world, x, y, z, side)
	
	override fun quantityDropped(meta: Int, fortune: Int, random: Random?) = (meta and 7) + 1
	
	override fun isReplaceable(world: IBlockAccess, x: Int, y: Int, z: Int): Boolean {
		val meta = world.getBlockMetadata(x, y, z)
		return if (meta >= 7) false else blockMaterial.isReplaceable
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hx: Float, hy: Float, hz: Float): Boolean {
		val meta = world.getBlockMetadata(x, y, z)
		if (player.currentEquippedItem?.item === this.toItem() && meta < 7) {
			world.setBlockMetadataWithNotify(x, y, z, meta + 1, 3)
			return true
		}
		
		if (player.currentEquippedItem == null && meta > 0) {
			world.setBlockMetadataWithNotify(x, y, z, meta - 1, 3)
			if (!player.inventory.addItemStackToInventory(ItemStack(Items.snowball))) {
				player.dropPlayerItemWithRandomChoice(ItemStack(Items.snowball), true)
			}
			return true
		}
		
		return false
	}
}
