package alfheim.common.block

import alfheim.common.item.block.ItemBlockMod
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.*
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.item.EntityFallingBlock
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.*
import vazkii.botania.api.lexicon.*
import java.util.*

class BlockPatternLexicon(modid: String, material: Material, name: String, tab: CreativeTabs, lightlvl: Float, lightOpacity: Int, hardness: Float, harvTool: String, harvLvl: Int, resistance: Float, sound: SoundType, private val isOpaque: Boolean, private val isBeacon: Boolean, private val isFalling: Boolean, private val entry: LexiconEntry): BlockFalling(material), ILexiconable {
	
	init {
		setBlockName(name)
		setBlockTextureName("$modid:$name")
		setCreativeTab(tab)
		setLightLevel(lightlvl)
		setLightOpacity(lightOpacity)
		setHardness(hardness)
		setHarvestLevel(harvTool, harvLvl)
		setResistance(resistance)
		setStepSound(sound)
	}
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockMod::class.java, name)
		return super.setBlockName(name)
	}
	
	override fun isOpaqueCube() = isOpaque
	
	override fun isBeaconBase(world: IBlockAccess?, x: Int, y: Int, z: Int, beaconX: Int, beaconY: Int, beaconZ: Int) = isBeacon
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, rand: Random?) {
		if (!world.isRemote && isFalling) func_149830_m(world, x, y, z)
	}
	
	private fun func_149830_m(world: World, x: Int, y: Int, z: Int) {
		var y = y
		if (func_149831_e(world, x, y - 1, z) && y >= 0) {
			val b0: Byte = 32
			
			if (!fallInstantly && world.checkChunksExist(x - b0, y - b0, z - b0, x + b0, y + b0, z + b0)) {
				if (!world.isRemote) {
					val entityfallingblock = EntityFallingBlock(world, (x.toFloat() + 0.5f).toDouble(), (y.toFloat() + 0.5f).toDouble(), (z.toFloat() + 0.5f).toDouble(), this, world.getBlockMetadata(x, y, z))
					func_149829_a(entityfallingblock)
					world.spawnEntityInWorld(entityfallingblock)
				}
			} else {
				world.setBlockToAir(x, y, z)
				while (func_149831_e(world, x, y - 1, z) && y > 0) --y
				if (y > 0) world.setBlock(x, y, z, this)
			}
		}
	}
	
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = entry
}