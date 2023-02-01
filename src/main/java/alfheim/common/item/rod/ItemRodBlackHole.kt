package alfheim.common.item.rod

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.lib.LibResourceLocations
import alfheim.common.entity.EntityFracturedSpaceCollector
import alfheim.common.item.ItemMod
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.World
import vazkii.botania.api.item.*
import vazkii.botania.api.mana.IManaUsingItem
import vazkii.botania.api.wand.ICoordBoundItem
import vazkii.botania.common.block.tile.*
import vazkii.botania.common.core.helper.ItemNBTHelper

class ItemRodBlackHole: ItemMod("RodFracturedSpace"), IManaUsingItem, ICoordBoundItem, IAvatarWieldable {
	
	init {
		setFull3D()
		setMaxStackSize(1)
	}
	
	override fun onItemUse(stack: ItemStack, player: EntityPlayer?, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (world.getTileEntity(x, y, z) is TileOpenCrate) {
			//Clicked a crate.
			//Remember this position.
			ItemNBTHelper.setInt(stack, TAG_X, x)
			ItemNBTHelper.setInt(stack, TAG_Y, y)
			ItemNBTHelper.setInt(stack, TAG_Z, z)
			ItemNBTHelper.setInt(stack, TAG_D, world.provider.dimensionId)
			
			if (!world.isRemote)
				ASJUtilities.say(player, "alfheimmisc.fracturedSpace.savedPos")
			
			return true
		} else {
			//Didn't click a crate.
			if (side != 1) return false
			
			//Sanity check that the crate position is still ok.
			
			val j = ItemNBTHelper.getInt(stack, TAG_Y, -1)
			
			if (j == -1) {
				if (!world.isRemote) ASJUtilities.say(player, "alfheimmisc.fracturedSpace.noPos")
				return false
			}
			
			val d = ItemNBTHelper.getInt(stack, TAG_D, 0)
			
			if (d != world.provider.dimensionId) {
				if (!world.isRemote)
					ASJUtilities.say(player, "alfheimmisc.fracturedSpace.wrongDimension")
				
				return false
			}
			
			if (!world.isRemote) {
				//One final server-only sanity check, since this loads the chunk
				val i = ItemNBTHelper.getInt(stack, TAG_X, 0)
				val k = ItemNBTHelper.getInt(stack, TAG_Z, 0)
				
				val rememberedTile = world.getTileEntity(i, j, k)
				if (rememberedTile !is TileOpenCrate) {
					ASJUtilities.say(player, "alfheimmisc.fracturedSpace.noCrateThere")
					return false
				}
				
				//Spawn the entity.
				val fsc = EntityFracturedSpaceCollector(world, i, j, k, player)
				fsc.setPosition(x.D + hitX, y + 1.0, z.D + hitZ)
				fsc.spawn()
			}
			
			return true
		}
	}
	
	override fun onAvatarUpdate(tile: IAvatarTile, stack: ItemStack) {
		var (x, y, z) = Vector3.fromTileEntity(tile as? TileAvatar ?: return).I
		val world = tile.worldObj
		
		if (world.isRemote || tile.currentMana == 0 || !world.isBlockIndirectlyGettingPowered(x, y, z)) return
		
		when (tile.getBlockMetadata() - 2) {
			0    -> z -= 2
			2    -> x -= 2
			3    -> x += 2
			else -> z += 2
		}
		
		onItemUse(stack, null, world, x, y - 1, z, 1, 0.5f, 0.5f, 0.5f)
	}
	
	@SideOnly(Side.CLIENT)
	override fun getBinding(stack: ItemStack): ChunkCoordinates? {
		val y = ItemNBTHelper.getInt(stack, TAG_Y, -1)
		
		return if (y == -1 || mc.thePlayer.dimension != ItemNBTHelper.getInt(stack, TAG_D, 0)) null else ChunkCoordinates(ItemNBTHelper.getInt(stack, TAG_X, 0), y, ItemNBTHelper.getInt(stack, TAG_Z, 0))
	}
	
	override fun addInformation(stack: ItemStack?, player: EntityPlayer, tooltip: MutableList<Any?>, adv: Boolean) {
		val y = ItemNBTHelper.getInt(stack, TAG_Y, -1)
		if (y == -1) {
			addStringToTooltip(tooltip, "alfheimmisc.fracturedSpace.tooltipNotBound")
		} else {
			addStringToTooltip(tooltip, "alfheimmisc.fracturedSpace.tooltipBound")
			
			val d = ItemNBTHelper.getInt(stack, TAG_D, 0)
			
			if (player.dimension != d)
				addStringToTooltip(tooltip, "alfheimmisc.fracturedSpace.tooltipWrongDimension")
			
			if (adv) {
				addStringToTooltip(tooltip, "alfheimmisc.fracturedSpace.debug.tooltipPos", ItemNBTHelper.getInt(stack, TAG_X, 0).toString(), "$y", ItemNBTHelper.getInt(stack, TAG_Z, 0).toString())
				addStringToTooltip(tooltip, "alfheimmisc.fracturedSpace.debug.tooltipDim", "$d")
			}
		}
	}
	
	override fun getOverlayResource(tile: IAvatarTile?, stack: ItemStack?) = LibResourceLocations.avatarClicker
	
	override fun usesMana(stack: ItemStack): Boolean {
		return true
	}
	
	companion object {
		const val TAG_D = "toD"
		const val TAG_X = "toX"
		const val TAG_Y = "toY"
		const val TAG_Z = "toZ"
	}
}