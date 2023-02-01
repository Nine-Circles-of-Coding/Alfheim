package alfheim.common.core.asm.hook.extender

import alexsocol.asjlib.*
import alfheim.common.block.tile.TileAnimatedTorch
import cpw.mods.fml.relauncher.*
import gloomyfolken.hooklib.asm.*
import net.minecraft.block.Block
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.*
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.client.core.helper.IconHelper
import vazkii.botania.client.render.tile.RenderTileLightRelay
import vazkii.botania.common.block.*
import vazkii.botania.common.block.tile.TileLightRelay

@Suppress("UNUSED_PARAMETER", "FunctionName")
object LightRelayExtender {
	
	lateinit var invIconToggle: IIcon
	lateinit var worldIconToggle: IIcon
	lateinit var invIconFork: IIcon
	lateinit var worldIconFork: IIcon
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun getSubBlocks(block: BlockLightRelay, item: Item?, tab: CreativeTabs?, list: MutableList<ItemStack>) {
		for (i in 2..3) list.add(ItemStack(item, 1, i))
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun damageDropped(block: BlockLightRelay, meta: Int) = meta
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun isProvidingWeakPower(block: BlockLightRelay, world: IBlockAccess, x: Int, y: Int, z: Int, s: Int): Int {
		val meta = world.getBlockMetadata(x, y, z)
		return if (meta == 1 || meta == 9) 15 else 0
	}
	
	@SideOnly(Side.CLIENT)
	@JvmStatic
	@Hook(injectOnExit = true)
	fun registerBlockIcons(block: BlockLightRelay, reg: IIconRegister?) {
		invIconToggle = IconHelper.forBlock(reg, block, 4)
		worldIconToggle = IconHelper.forBlock(reg, block, 5)
		invIconFork = IconHelper.forBlock(reg, block, 6)
		worldIconFork = IconHelper.forBlock(reg, block, 7)
	}
	
	@SideOnly(Side.CLIENT)
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun getIcon(block: BlockLightRelay, side: Int, meta: Int) = when (meta) {
		1 -> BlockLightRelay.invIconRed!!
		2 -> invIconToggle
		3 -> invIconFork
		else -> BlockLightRelay.invIcon!!
	}
	
	// Tile
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun isValidBinding(tile: TileLightRelay): Boolean {
		val (x, y, z) = tile.binding
		return tile.worldObj.getBlock(x, y, z) === ModBlocks.lightRelay
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun getBinding(tile: TileLightRelay): ChunkCoordinates? {
		val default = if (tile.bindY == -1) null else ChunkCoordinates(tile.bindX, tile.bindY, tile.bindZ)
		
		return when (tile.getBlockMetadata()) {
			2, 10 -> if (tile.worldObj.isBlockIndirectlyGettingPowered(tile.xCoord, tile.yCoord, tile.zCoord)) null else default
			3, 11 -> {
				val torch = tile.worldObj.getTileEntity(tile.xCoord, tile.yCoord - 2, tile.zCoord) as? TileAnimatedTorch ?:
							tile.worldObj.getTileEntity(tile.xCoord, tile.yCoord - 1, tile.zCoord) as? TileAnimatedTorch ?:
							tile.worldObj.getTileEntity(tile.xCoord, tile.yCoord + 1, tile.zCoord) as? TileAnimatedTorch ?:
							tile.worldObj.getTileEntity(tile.xCoord, tile.yCoord + 2, tile.zCoord) as? TileAnimatedTorch ?:
							return default
				
				val dir = when (torch.side) {
					0 -> ForgeDirection.SOUTH
					1 -> ForgeDirection.WEST
					2 -> ForgeDirection.NORTH
					3 -> ForgeDirection.EAST
					else -> ForgeDirection.UNKNOWN
				}
				
				for (i in 1..20) {
					val target = tile.worldObj.getTileEntity(tile.xCoord + dir.offsetX * i, tile.yCoord, tile.zCoord + dir.offsetZ * i) as? TileLightRelay ?: continue
					
					return ChunkCoordinates(target.xCoord, target.yCoord, target.zCoord)
				}
				
				return default
			}
			else -> default
		}
	}
	
	@SideOnly(Side.CLIENT)
	@JvmStatic
	@Hook(targetMethod = "renderTileEntityAt")
	fun renderTileEntityAt(renderer: RenderTileLightRelay, tile: TileEntity, x: Double, y: Double, z: Double, pticks: Float) {
		val meta = tile.getBlockMetadata()

		newWorldIcon = when (meta) {
			2    -> worldIconToggle
			3    -> worldIconFork
			else -> null
		}
	}

	var newWorldIcon: IIcon? = null

	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE)
	fun func_77026_a(renderer: RenderTileLightRelay, tes: Tessellator, icon: IIcon?): Boolean {
		if (newWorldIcon == null)
			return false

		val newIcon = newWorldIcon!!
		newWorldIcon = null

		run {
			var f = newIcon.minU.D
			var f1 = newIcon.maxU.D
			var f2 = newIcon.minV.D
			var f3 = newIcon.maxV.D
			val size = f1 - f
			val pad = size / 8
			
			f += pad
			f1 -= pad
			f2 += pad
			f3 -= pad
			
			val f4 = 1.0
			val f5 = 0.5
			val f6 = 0.25
			
			tes.startDrawingQuads()
			tes.setNormal(0f, 1f, 0f)
			tes.setBrightness(240)
			tes.addVertexWithUV(0.0 - f5, 0.0 - f6, 0.0, f, f3)
			tes.addVertexWithUV((f4 - f5), 0.0 - f6, 0.0, f1, f3)
			tes.addVertexWithUV((f4 - f5), (f4 - f6), 0.0, f1, f2)
			tes.addVertexWithUV(0.0 - f5, (f4 - f6), 0.0, f, f2)
			tes.draw()
		}

		return true
	}
}