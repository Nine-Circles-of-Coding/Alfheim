package alfheim.common.block.magtrees.circuit

import alfheim.common.block.base.BlockMod
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.*
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.api.lexicon.ILexiconable
import java.util.*

class BlockCircuitPlanks: BlockMod(Material.wood), ICircuitBlock, ILexiconable {
	
	private val name = "circuitPlanks"
	
	init {
		blockHardness = 2F
		
		stepSound = soundTypeWood
		setBlockName(name)
		
		tickRandomly = true
	}
	
	override fun onBlockAdded(world: World, x: Int, y: Int, z: Int) {
		val below = world.getBlock(x, y - 1, z)
		if (below !is ICircuitBlock) return
		
		below.updateTick(world, x, y - 1, z, world.rand)
	}
	
	override fun breakBlock(world: World, x: Int, y: Int, z: Int, block: Block?, meta: Int) {
		onBlockAdded(world, x, y, z)
	}
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "axe")
	
	override fun getHarvestTool(metadata: Int) = "axe"
	
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): ItemStack {
		val meta = world.getBlockMetadata(x, y, z)
		return ItemStack(this, 1, meta)
	}
	
	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.circuitSapling
	
	// ####
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		world.notifyBlocksOfNeighborChange(x, y, z, this)
		onBlockAdded(world, x, y, z)
	}
	
	override fun getLightValue(world: IBlockAccess?, x: Int, y: Int, z: Int) = 8
	
	override fun canProvidePower() = true
	
	override fun tickRate(world: World) = 1
	
	override fun isProvidingWeakPower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, meta: Int) = ICircuitBlock.getPower(blockAccess, x, y, z)
	
	override fun isSideSolid(world: IBlockAccess?, x: Int, y: Int, z: Int, side: ForgeDirection?) = true
}