package alfheim.common.block

import alfheim.common.block.base.BlockMod
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.init.Blocks
import net.minecraft.world.World
import java.util.*

class BlockDirtDissolvable: BlockMod(Material.ground) {
	
	init {
		setBlockName("dirt")
		setHardness(50f)
		setHarvestLevel("shovel", 3)
	}
	
	override fun onBlockAdded(world: World, x: Int, y: Int, z: Int) {
		world.scheduleBlockUpdate(x, y, z, this, tickRate(world))
	}
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, rand: Random) {
		if (rand.nextInt(10) == 0) world.setBlockToAir(x, y, z)
		else world.scheduleBlockUpdate(x, y, z, this, tickRate(world))
	}
	
	override fun getItemDropped(meta: Int, rand: Random, fortune: Int) = null
	override fun quantityDropped(rand: Random?) = 0
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	override fun getIcon(side: Int, meta: Int) = Blocks.dirt.getIcon(0, 0)
}
