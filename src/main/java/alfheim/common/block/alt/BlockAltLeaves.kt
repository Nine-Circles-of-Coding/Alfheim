package alfheim.common.block.alt

import alexsocol.asjlib.*
import alexsocol.asjlib.render.*
import alfheim.api.lib.LibOreDict.ALT_TYPES
import alfheim.client.core.helper.IconHelper
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.base.BlockLeavesMod
import alfheim.common.core.handler.*
import alfheim.common.item.block.*
import alfheim.common.item.material.ElvenFoodMetas
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.util.IIcon
import net.minecraft.world.*
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.api.lexicon.LexiconEntry
import vazkii.botania.common.Botania
import java.util.*

class BlockAltLeaves: BlockLeavesMod(), IGlowingLayerBlock {
	
	init {
		setBlockName("altLeaves")
	}
	
	override fun getExplosionResistance(entity: Entity?, world: World, x: Int, y: Int, z: Int, explosionX: Double, explosionY: Double, explosionZ: Double) =
		if (world.getBlockMetadata(x, y, z) % 8 == yggMeta)
			Float.MAX_VALUE
		else
			super.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ)
	
	override fun getBlockHardness(world: World, x: Int, y: Int, z: Int) =
		if (world.getBlockMetadata(x, y, z) % 8 == yggMeta)
			-1f
		else
			super.getBlockHardness(world, x, y, z)
	
	override fun getFlammability(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) =
		if (world.getBlockMetadata(x, y, z) % 8 == yggMeta) 0 else super.getFlammability(world, x, y, z, face)
	
	override fun getFireSpreadSpeed(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) =
		if (world.getBlockMetadata(x, y, z) % 8 == yggMeta) 0 else super.getFireSpreadSpeed(world, x, y, z, face)
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemUniqueSubtypedBlockMod::class.java, name, ALT_TYPES.size)
	}
	
	override fun registerBlockIcons(reg: IIconRegister) {
		textures = arrayOf(
			Array(ALT_TYPES.size) { i -> IconHelper.forBlock(reg, this, ALT_TYPES[i]) },
			Array(ALT_TYPES.size) { i -> IconHelper.forBlock(reg, this, "${ALT_TYPES[i]}_opaque") }
		)
		
		glowIcon = IconHelper.forBlock(reg, this, "DreamwoodGlow")
	}
	
	override fun getIcon(side: Int, meta: Int): IIcon {
		setGraphicsLevel(mc.gameSettings.fancyGraphics)
		return textures[field_150127_b].safeGet(meta and decayBit().inv())
	}
	
	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = if (meta % 8 == yggMeta) null else if (meta % 8 == yggMeta + 1) AlfheimBlocks.dreamSapling.toItem() else AlfheimBlocks.irisSapling.toItem()
	
	override fun func_150124_c(world: World, x: Int, y: Int, z: Int, meta: Int, chance: Int) {
		if (meta % 8 != yggMeta + 1 || world.rand.nextInt(chance / 2) != 0) return
		
		dropBlockAsItem(world, x, y, z, ElvenFoodMetas.DreamCherry.stack)
	}
	
	override fun createStackedBlock(meta: Int) = ItemStack(this, 1, meta % 8)
	
	override fun func_150123_b(meta: Int) = if (meta == yggMeta) 0 else if (meta == yggMeta + 1) 100 else 60
	
	override fun quantityDropped(random: Random) = if (random.nextInt(func_150123_b(0)) == 0) 1 else 0
	
	override fun func_150125_e() = ALT_TYPES
	
	override fun getSubBlocks(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (i in ALT_TYPES.indices)
			list.add(ItemStack(item, 1, i))
	}
	
	override fun decayBit() = 0x8
	
	override fun canDecay(meta: Int) = if (meta % 8 == yggMeta) false else super.canDecay(meta)
	
	override fun isLeaves(world: IBlockAccess, x: Int, y: Int, z: Int) = if (world.getBlockMetadata(x, y, z) % 8 == yggMeta) false else super.isLeaves(world, x, y, z)
	
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?): LexiconEntry {
		val meta = world.getBlockMetadata(x, y, z)
		return when {
			meta % 8 == yggMeta + 1 -> AlfheimLexiconData.worldgen
			meta % 8 == yggMeta     -> AlfheimLexiconData.legends
			else                    -> AlfheimLexiconData.irisSapling
		}
	}
	
	override fun getRenderType() = if (ASJUtilities.isClient) RenderGlowingLayerBlock.glowBlockID else -1
	
	override fun getGlowIcon(side: Int, meta: Int) = if (meta % 8 == 7) glowIcon else null
	
	override fun randomDisplayTick(world: World, x: Int, y: Int, z: Int, rand: Random) {
		if (!AlfheimConfigHandler.increasedSpiritsRange && world.getBlockMetadata(x, y, z) % 8 == 7)
			spawnRandomSpirit(world, x, y, z, rand, 0f, rand.nextFloat() * 0.25f + 0.5f, 1f)
	}
	
	companion object {
		
		lateinit var textures: Array<Array<IIcon>>
		lateinit var glowIcon: IIcon
		
		val yggMeta = ALT_TYPES.indexOf("Wisdom")
		
		fun spawnRandomSpirit(world: World, x: Int, y: Int, z: Int, rand: Random, r: Float, g: Float, b: Float) {
			if (world.worldTime % 24000 !in 13333..22666 || rand.nextInt(512) != 0) return
			
			val i = Math.random()
			val j = Math.random()
			val k = Math.random()
			val s = Math.random()
			val m = Math.random()
			val n = Math.random()
			val o = Math.random()
			val l = Math.random()
			
			Botania.proxy.setWispFXDistanceLimit(false)
			for (q in 0..4)
				Botania.proxy.wispFX(world, x + i, y + j * 5 + 1, z + k, r, g, b, s.F * 0.25f + 0.1f, m.F * 0.1f - 0.05f, n.F * 0.01F, o.F * 0.1f - 0.05f, l.F * 20f + 5f)
			
			if (AlfheimConfigHandler.increasedSpiritsRange) // not so good in close range
				Botania.proxy.wispFX(world, x + i, y + j * 5 + 1, z + k, r / 2, g / 2, b / 2, s.F * 0.25f + 3f, m.F * 0.1f - 0.05f, n.F * 0.01F, o.F * 0.1f - 0.05f, l.F * 20f + 5f)
			
			Botania.proxy.setWispFXDistanceLimit(true)
		}
	}
}
