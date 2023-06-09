package alfheim.common.block.alt

import alexsocol.asjlib.*
import alfheim.api.lib.LibOreDict.ALT_TYPES
import alfheim.client.core.helper.*
import alfheim.common.block.base.BlockMod
import alfheim.common.item.block.ItemUniqueSubtypedBlockMod
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.common.IFuelHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.api.lexicon.*
import java.util.*

class BlockAltPlanks: BlockMod(Material.wood), ILexiconable, IFuelHandler {
	
	val name = "altPlanks"
	var icons: Array<IIcon?> = emptyArray()
	
	init {
		blockHardness = 2F
		setLightLevel(0f)
		stepSound = soundTypeWood
		
		setBlockName(name)
		GameRegistry.registerFuelHandler(this)
		if (ASJUtilities.isClient)
			MinecraftForge.EVENT_BUS.register(this)
	}
	
	override fun getExplosionResistance(entity: Entity?, world: World, x: Int, y: Int, z: Int, explosionX: Double, explosionY: Double, explosionZ: Double) =
		if (world.getBlockMetadata(x, y, z) == BlockAltLeaves.yggMeta)
			Float.MAX_VALUE
		else
			super.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ)
	
	override fun getBlockHardness(world: World, x: Int, y: Int, z: Int) =
		if (world.getBlockMetadata(x, y, z) == BlockAltLeaves.yggMeta)
			-1f
		else
			super.getBlockHardness(world, x, y, z)
	
	override fun getFlammability(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) =
		if (world.getBlockMetadata(x, y, z) == BlockAltLeaves.yggMeta) 0 else super.getFlammability(world, x, y, z, face)
	
	override fun getFireSpreadSpeed(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) =
		if (world.getBlockMetadata(x, y, z) == BlockAltLeaves.yggMeta) 0 else super.getFireSpreadSpeed(world, x, y, z, face)
	
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = Array(ALT_TYPES.size - 1) { i ->
			if (i == ALT_TYPES.indexOf("Scorched")) null else IconHelper.forBlock(reg, this, ALT_TYPES[i])
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	override fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 0) {
			icons[ALT_TYPES.indexOf("Scorched")] = InterpolatedIconHelper.forBlock(event.map, this, "Scorched")
		}
	}
	
	override fun getIcon(side: Int, meta: Int) = icons.safeGet(meta % (ALT_TYPES.size - 1))
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "axe")
	
	override fun getHarvestTool(metadata: Int) = "axe"
	
	override fun shouldRegisterInNameSet() = false
	
	override fun damageDropped(par1: Int) = par1
	
	override fun setBlockName(name: String): Block {
		register(name)
		return super.setBlockName(name)
	}
	
	override fun quantityDropped(random: Random) = 1
	
	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = this.toItem()
	
	internal fun register(name: String) {
		GameRegistry.registerBlock(this, ItemUniqueSubtypedBlockMod::class.java, name, ALT_TYPES.size - 1)
	}
	
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>?) {
		if (list != null && item != null)
			for (i in 0 until ALT_TYPES.size - 1) {
				list.add(ItemStack(item, 1, i))
			}
	}
	
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): ItemStack {
		val meta = world.getBlockMetadata(x, y, z)
		return ItemStack(this, 1, meta)
	}
	
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?): LexiconEntry? {
		return when (world.getBlockMetadata(x, y, z)) {
			BlockAltLeaves.yggMeta + 1 -> AlfheimLexiconData.worldgen
			BlockAltLeaves.yggMeta     -> null
			else                       -> AlfheimLexiconData.irisSapling
		}
	}
	
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) if (fuel.meta == BlockAltLeaves.yggMeta) Int.MAX_VALUE / 13 / 4 else 300 else 0
}
