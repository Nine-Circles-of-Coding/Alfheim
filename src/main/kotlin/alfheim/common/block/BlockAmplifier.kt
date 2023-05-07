package alfheim.common.block

import alfheim.common.block.base.BlockMod
import alfheim.common.block.magtrees.sealing.ISoundSilencer
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import vazkii.botania.api.lexicon.ILexiconable

class BlockAmplifier: BlockMod(Material.wood), ISoundSilencer, ILexiconable {
	
	init {
		setBlockName("amplifier")
		setHardness(0.8f)
		setStepSound(soundTypeCloth)
	}
	
	override fun canSilence(world: World, x: Int, y: Int, z: Int, dist: Double): Boolean = dist <= 2
	
	override fun getVolumeMultiplier(world: World, x: Int, y: Int, z: Int, dist: Double): Float = 5f
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.amplifier
}
