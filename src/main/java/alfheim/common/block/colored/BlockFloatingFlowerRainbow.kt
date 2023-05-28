package alfheim.common.block.colored

import alfheim.common.block.tile.TileFloatingFlowerRainbow
import cpw.mods.fml.common.Optional
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.passive.EntitySheep
import net.minecraft.item.*
import net.minecraft.world.World
import thaumcraft.api.crafting.IInfusionStabiliser
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.common.Botania
import vazkii.botania.common.block.decor.BlockFloatingFlower
import vazkii.botania.common.core.handler.ConfigHandler
import java.util.*

@Optional.Interface(modid = "Thaumcraft", iface = "thaumcraft.api.crafting.IInfusionStabiliser", striprefs = true)
class BlockFloatingFlowerRainbow: BlockFloatingFlower("miniIslandRainbow"), ILexiconable, IInfusionStabiliser {
	
	val TYPES = 1
	
	override fun randomDisplayTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		val (r, g, b) = when (world.getBlockMetadata(x, y, z)) {
			0 -> EntitySheep.fleeceColorTable[random.nextInt(EntitySheep.fleeceColorTable.size)]
			else -> floatArrayOf(1f, 1f, 1f)
		}
		
		if (random.nextDouble() < ConfigHandler.flowerParticleFrequency)
			Botania.proxy.sparkleFX(world, x + 0.3 + random.nextFloat() * 0.5, y + 0.5 + random.nextFloat() * 0.5, z + 0.3 + random.nextFloat() * 0.5, r, g, b, random.nextFloat(), 5)
	}
	
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (i in 0 until TYPES) list.add(ItemStack(item, 1, i))
	}
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileFloatingFlowerRainbow()
}
