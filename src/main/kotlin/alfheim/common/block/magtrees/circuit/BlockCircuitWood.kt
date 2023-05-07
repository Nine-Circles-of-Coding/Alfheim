package alfheim.common.block.magtrees.circuit

import alfheim.common.block.base.BlockModRotatedPillar
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.*
import net.minecraftforge.common.util.ForgeDirection
import java.util.*

class BlockCircuitWood: BlockModRotatedPillar(Material.wood), ICircuitBlock {
	
	init {
		setBlockName("circuitWood")
		blockHardness = 2f
		tickRandomly = true
	}
	
	override fun onBlockAdded(world: World, x: Int, y: Int, z: Int) {
		val below = world.getBlock(x, y - 1, z)
		if (below !is ICircuitBlock) return
		
		below.updateTick(world, x, y - 1, z, world.rand)
	}
	
	override fun canSustainLeaves(world: IBlockAccess, x: Int, y: Int, z: Int) = true
	
	override fun isWood(world: IBlockAccess?, x: Int, y: Int, z: Int) = true
	
	override fun breakBlock(world: World, x: Int, y: Int, z: Int, block: Block, meta: Int) {
		val b0: Byte = 4
		val i1: Int = b0 + 1
		
		if (world.checkChunksExist(x - i1, y - i1, z - i1, x + i1, y + i1, z + i1)) {
			for (j1 in -b0..b0) for (k1 in -b0..b0)
				for (l1 in -b0..b0) {
					val blockInWorld: Block = world.getBlock(x + j1, y + k1, z + l1)
					if (blockInWorld.isLeaves(world, x + j1, y + k1, z + l1)) {
						blockInWorld.beginLeavesDecay(world, x + j1, y + k1, z + l1)
					}
				}
		}
		
		onBlockAdded(world, x, y, z)
	}
	
	override fun damageDropped(meta: Int) = 0
	
	override fun quantityDropped(random: Random) = 1
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
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