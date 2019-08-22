package alfheim.common.block

import alexsocol.asjlib.math.Vector3
import alfheim.common.block.base.BlockMod
import alfheim.common.block.tile.TileCracklingStar
import alfheim.common.item.block.ItemStarPlacer2
import alfheim.common.lexicon.ShadowFoxLexiconData
import cpw.mods.fml.common.Optional
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.*
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.api.wand.IWandable
import java.util.*

class BlockCracklingStar: BlockMod(Material.cloth), IWandable, ILexiconable {
	
	override val registerInCreative = false
	
	init {
		setBlockName("cracklingStar")
		val f = 0.25f
		setStepSound(soundTypeCloth)
		setBlockBounds(f, f, f, 1.0f - f, 1.0f - f, 1.0f - f)
		setLightLevel(1.0f)
	}
	
	@Optional.Method(modid = "easycoloredlights")
	override fun getLightValue(world: IBlockAccess, x: Int, y: Int, z: Int) =
		(world.getTileEntity(x, y, z) as TileCracklingStar).getLightColor()
	
	override fun registerBlockIcons(par1IconRegister: IIconRegister) = Unit
	
	override fun getRenderType(): Int = -1
	
	override fun getIcon(side: Int, meta: Int) = Blocks.fire.getIcon(side, meta)!!
	
	override fun isOpaqueCube(): Boolean = false
	
	override fun renderAsNormalBlock(): Boolean = false
	
	override fun hasTileEntity(metadata: Int): Boolean = true
	
	override fun getBlocksMovement(world: IBlockAccess?, x: Int, y: Int, z: Int): Boolean = true
	
	override fun getCollisionBoundingBoxFromPool(world: World?, x: Int, y: Int, z: Int): AxisAlignedBB? = null
	
	override fun getDrops(world: World, x: Int, y: Int, z: Int, metadata: Int, fortune: Int): ArrayList<ItemStack> = ArrayList()
	
	override fun onBlockHarvested(world: World, x: Int, y: Int, z: Int, meta: Int, player: EntityPlayer?) {
		if (player?.capabilities?.isCreativeMode != true) {
			val te = world.getTileEntity(x, y, z)
			if (te is TileCracklingStar) {
				val f = 0.5
				
				val color = te.color
				val stack = ItemStarPlacer2.colorStack(color)
				
				val entityitem = EntityItem(world, x.toFloat() + f, y.toFloat() + f, z.toFloat() + f, stack)
				
				val f3 = 0.05f
				entityitem.motionX = (world.rand.nextGaussian().toFloat() * f3).toDouble()
				entityitem.motionY = (world.rand.nextGaussian().toFloat() * f3 + 0.2f).toDouble()
				entityitem.motionZ = (world.rand.nextGaussian().toFloat() * f3).toDouble()
				world.spawnEntityInWorld(entityitem)
			}
		}
		
		super.onBlockHarvested(world, x, y, z, meta, player)
	}
	
	override fun getPickBlock(target: MovingObjectPosition, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): ItemStack? {
		val te = world.getTileEntity(x, y, z)
		if (te is TileCracklingStar) {
			return ItemStarPlacer2.colorStack(te.color)
		}
		return super.getPickBlock(target, world, x, y, z, player)
	}
	
	override fun createTileEntity(world: World?, meta: Int) = TileCracklingStar()
	
	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = ShadowFoxLexiconData.frozenStar
	
	companion object {
		val playerPositions = mutableMapOf<UUID, DimWithPos>()
	}
	
	override fun onUsedByWand(player: EntityPlayer?, stack: ItemStack?, world: World, x: Int, y: Int, z: Int, meta: Int): Boolean {
		if (player == null || world.isRemote) return false
		val dwp = playerPositions[player.uniqueID]
		
		val here = DimWithPos(world.provider.dimensionId, x, y, z)
		
		if (dwp == null)
			playerPositions[player.uniqueID] = here
		else {
			playerPositions.remove(player.uniqueID)
			if (dwp == here) {
				val te = world.getTileEntity(x, y, z) as? TileCracklingStar ?: return true
				te.pos = null
			} else if (dwp.dim == here.dim) {
				val otherTe = world.getTileEntity(dwp.x, dwp.y, dwp.z) as? TileCracklingStar ?: return true
				otherTe.pos = Vector3(x.toDouble(), y.toDouble(), z.toDouble())
				otherTe.markDirty()
				world.markBlockForUpdate(dwp.x, dwp.y, dwp.z)
			}
		}
		return true
	}
	
	data class DimWithPos(val dim: Int, val x: Int, val y: Int, val z: Int) {
		constructor(world: World, x: Int, y: Int, z: Int) : this(world.provider.dimensionId, x, y, z)
		
		override fun toString() = "$dim:$x:$y:$z"
		
		companion object {
			@JvmStatic
			fun fromString(s: String): DimWithPos {
				val split = s.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
				return DimWithPos(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]))
			}
		}
	}
}