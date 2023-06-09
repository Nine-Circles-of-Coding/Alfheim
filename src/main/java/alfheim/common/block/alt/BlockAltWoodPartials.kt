package alfheim.common.block.alt

import alexsocol.asjlib.*
import alfheim.api.lib.LibOreDict
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.base.*
import alfheim.common.item.block.*
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.common.IFuelHandler
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.*
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.util.IIcon
import net.minecraft.world.*
import net.minecraftforge.common.util.ForgeDirection

class BlockAltWoodSlab(full: Boolean, source: Block = AlfheimBlocks.altPlanks):
	BlockSlabMod(full, 0, source, source.unlocalizedName.replace("tile.".toRegex(), "") + "Slab" + (if (full) "Full" else "")), IFuelHandler {
	
	init {
		GameRegistry.registerFuelHandler(this)
	}
	
	override fun getExplosionResistance(entity: Entity?, world: World, x: Int, y: Int, z: Int, explosionX: Double, explosionY: Double, explosionZ: Double) =
		if (world.getBlockMetadata(x, y, z) % 8 == 6)
			Float.MAX_VALUE
		else
			super.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ)
	
	override fun getBlockHardness(world: World, x: Int, y: Int, z: Int) =
		if (world.getBlockMetadata(x, y, z) % 8 == 6)
			-1f
		else
			super.getBlockHardness(world, x, y, z)
	
	override fun getFlammability(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) =
		if (world.getBlockMetadata(x, y, z) % 8 == 6) 0 else super.getFlammability(world, x, y, z, face)
	
	override fun getFireSpreadSpeed(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) =
		if (world.getBlockMetadata(x, y, z) % 8 == 6) 0 else super.getFireSpreadSpeed(world, x, y, z, face)
	
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (i in 0 until LibOreDict.ALT_TYPES.size - 1)
			list.add(ItemStack(item, 1, i))
	}
	
	override fun getFullBlock() = AlfheimBlocks.altSlabsFull as BlockSlab
	
	override fun getIcon(side: Int, meta: Int): IIcon {
		return source.getIcon(side, meta % 8)!!
	}
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemMetaSlabMod::class.java, name)
	}
	
	override fun getSingleBlock() = AlfheimBlocks.altSlabs as BlockSlab
	
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) if (fuel.meta == BlockAltLeaves.yggMeta) Int.MAX_VALUE / 13 / 8 else if (field_150004_a) 300 else 150 else 0
	
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = if (world.getBlockMetadata(x, y, z) == BlockAltLeaves.yggMeta) null else AlfheimLexiconData.irisSapling
}

open class BlockAltWoodStairs(meta: Int, source: Block = AlfheimBlocks.altPlanks):
	BlockStairsMod(source, meta, source.unlocalizedName.replace("tile.".toRegex(), "") + "Stairs" + meta), IFuelHandler {
	
	init {
		GameRegistry.registerFuelHandler(this)
	}
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
	}
	
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = if (world.getBlockMetadata(x, y, z) == BlockAltLeaves.yggMeta) null else AlfheimLexiconData.irisSapling
	
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) 300 else 0
}

class BlockYggStairs: BlockAltWoodStairs(BlockAltLeaves.yggMeta), IFuelHandler {
	
	init {
		setBlockUnbreakable()
	}
	
	override fun getExplosionResistance(entity: Entity?, world: World, x: Int, y: Int, z: Int, explosionX: Double, explosionY: Double, explosionZ: Double) = Float.MAX_VALUE
	
	override fun getBlockHardness(world: World, x: Int, y: Int, z: Int) = -1f
	
	override fun getFlammability(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) = 0
	
	override fun getFireSpreadSpeed(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) = 0
	
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = null
	
	override fun isToolEffective(type: String?, metadata: Int) = false
	
	override fun getHarvestTool(metadata: Int) = "Odin"
	
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) Int.MAX_VALUE / 13 / 8 * 3 else 0
}