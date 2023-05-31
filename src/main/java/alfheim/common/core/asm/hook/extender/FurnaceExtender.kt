package alfheim.common.core.asm.hook.extender

import alexsocol.asjlib.*
import cpw.mods.fml.relauncher.*
import gloomyfolken.hooklib.asm.*
import gloomyfolken.hooklib.asm.Hook.ReturnValue
import net.minecraft.block.*
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.World
import java.util.*

@Suppress("UNUSED_PARAMETER")
object FurnaceExtender {
	
	lateinit var iconFrontLit: IIcon
	lateinit var iconFrontUnlit: IIcon
	lateinit var iconSide: IIcon
	lateinit var iconTop: IIcon
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun damageDropped(furnace: BlockFurnace, meta: Int) = if (meta > 7) 8 else 0
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE)
	fun func_149930_e(furnace: BlockFurnace, world: World, x: Int, y: Int, z: Int): Boolean {
		if (world.getBlockMetadata(x, y, z) != 8) return false
		
		if (!world.isRemote) {
			val block = world.getBlock(x, y, z - 1)
			val block1 = world.getBlock(x, y, z + 1)
			val block2 = world.getBlock(x - 1, y, z)
			val block3 = world.getBlock(x + 1, y, z)
			
			var meta = 3 + 8
			
			if (block.func_149730_j() && !block1.func_149730_j()) {
				meta = 3 + 8
			}
			if (block1.func_149730_j() && !block.func_149730_j()) {
				meta = 2 + 8
			}
			if (block2.func_149730_j() && !block3.func_149730_j()) {
				meta = 5 + 8
			}
			if (block3.func_149730_j() && !block2.func_149730_j()) {
				meta = 4 + 8
			}
			
			world.setBlockMetadataWithNotify(x, y, z, meta, 2)
		}
		
		return true
	}
	
	@JvmStatic
	@Hook(targetMethod = "renderBlockAsItem")
	@SideOnly(Side.CLIENT)
	fun renderBlockAsItemPre(renderer: RenderBlocks, block: Block, meta: Int, colorMultiplier: Float) {
		forceMeta = block === Blocks.furnace && meta >= 8
	}
	
	var forceMeta = false
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_NOT_NULL)
	@SideOnly(Side.CLIENT)
	fun getIcon(furnace: BlockFurnace, side: Int, meta: Int): IIcon? {
		val actualMeta = if (forceMeta) 11 else meta
		return if (actualMeta < 8) null else if (side == 0 || side == 1) iconTop else if (side != (actualMeta - 8)) iconSide else if (furnace.field_149932_b) iconFrontLit else iconFrontUnlit
	}
	
	@JvmStatic
	@Hook(targetMethod = "renderBlockAsItem", injectOnExit = true)
	@SideOnly(Side.CLIENT)
	fun renderBlockAsItemPost(renderer: RenderBlocks, block: Block, meta: Int, colorMultiplier: Float) {
		forceMeta = false
	}
	
	@JvmStatic
	@Hook
	@SideOnly(Side.CLIENT)
	fun registerBlockIcons(furnace: BlockFurnace, reg: IIconRegister) {
		if (furnace.field_149932_b) return
		
		iconFrontLit = reg.registerIcon("furnace_front_on_living")
		iconFrontUnlit = reg.registerIcon("furnace_front_off_living")
		iconSide = reg.registerIcon("furnace_side_living")
		iconTop = reg.registerIcon("furnace_top_living")
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun onBlockPlacedBy(furnace: BlockFurnace, world: World, x: Int, y: Int, z: Int, placer: EntityLivingBase, stack: ItemStack) {
		if (stack.meta > 7)
			world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + 8, 3)
	}
	
	var hookMeta = false
	
	@JvmStatic
	@Hook
	@SideOnly(Side.CLIENT)
	fun randomDisplayTick(furnace: BlockFurnace, world: World, x: Int, y: Int, z: Int, rand: Random) {
		hookMeta = furnace.field_149932_b
	}
	
	// Helper for particles
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS, injectOnExit = true)
	fun getBlockMetadata(world: World, x: Int, y: Int, z: Int, @ReturnValue result: Int): Int {
		return if (hookMeta) {
			hookMeta = false
			
			if (result > 7) result - 8 else result
		} else
			result
	}
}