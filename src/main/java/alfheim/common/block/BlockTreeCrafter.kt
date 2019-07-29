package alfheim.common.block

import alfheim.api.lib.LibRenderIDs
import alfheim.client.render.tile.MultipassRenderer
import alfheim.common.block.base.*
import alfheim.common.block.tile.TileTreeCrafter
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.world.World
import alfheim.common.lexicon.ShadowFoxLexiconData
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.api.wand.IWandHUD
import java.util.*

open class BlockTreeCrafter(name: String, val block: Block): BlockContainerMod<TileTreeCrafter>(Material.wood), IWandHUD, ILexiconable, IMultipassRenderer {
	
	internal var random: Random
	override val registerInCreative: Boolean = false
	
	init {
		setHardness(3.0f)
		setResistance(5.0f)
		setLightLevel(1.0f)
		setStepSound(Block.soundTypeWood)
		setBlockName(name)
		random = Random()
	}
	
	override fun isOpaqueCube() = false
	
	override fun createNewTileEntity(var1: World, var2: Int) = TileTreeCrafter()
	
	override fun hasComparatorInputOverride() = true
	
	override fun getComparatorInputOverride(par1World: World?, par2: Int, par3: Int, par4: Int, par5: Int) = (par1World!!.getTileEntity(par2, par3, par4) as TileTreeCrafter).signal
	
	override fun getItemDropped(meta: Int, random: Random?, fortune: Int) = Item.getItemFromBlock(innerBlock(0))!!
	
	override fun renderHUD(mc: Minecraft, res: ScaledResolution, world: World, x: Int, y: Int, z: Int) = (world.getTileEntity(x, y, z) as TileTreeCrafter).renderHUD(mc, res)
	
	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = ShadowFoxLexiconData.treeCrafting
	
	override fun renderAsNormalBlock(): Boolean = false
	
	override fun canRenderInPass(pass: Int) = true.also { MultipassRenderer.pass = pass }
	
	override fun getRenderBlockPass(): Int = 1
	
	override fun getRenderType(): Int = LibRenderIDs.idMultipass
	
	override fun innerBlock(meta: Int): Block = block
}
