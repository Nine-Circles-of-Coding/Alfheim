package alfheim.common.block.magtrees.sealing

import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.rainbow.*
import alfheim.common.item.block.*
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.*
import net.minecraft.world.World

class BlockSealingWoodSlab(full: Boolean, source: Block = AlfheimBlocks.sealingPlanks): BlockRainbowWoodSlab(full, source), ISoundSilencer {
	
	init {
		setStepSound(Block.soundTypeCloth)
	}
	
	override fun getFullBlock() = AlfheimBlocks.sealingSlabsFull as BlockSlab
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemSlabMod::class.java, name)
	}
	
	override fun getSingleBlock() = AlfheimBlocks.sealingSlabs as BlockSlab
	
	override fun canSilence(world: World, x: Int, y: Int, z: Int, dist: Double) = dist <= 8
	
	override fun getVolumeMultiplier(world: World, x: Int, y: Int, z: Int, dist: Double) = 0.5f
}

class BlockSealingWoodStairs(source: Block = AlfheimBlocks.sealingPlanks): BlockRainbowWoodStairs(source), ISoundSilencer {
	
	init {
		setStepSound(Block.soundTypeCloth)
	}
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
	}
	
	override fun canSilence(world: World, x: Int, y: Int, z: Int, dist: Double) = dist <= 8
	
	override fun getVolumeMultiplier(world: World, x: Int, y: Int, z: Int, dist: Double) = 0.5f
}
