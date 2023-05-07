package alfheim.common.block

import alexsocol.asjlib.*
import alfheim.common.block.base.BlockMod
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import vazkii.botania.api.lexicon.*

class BlockBarrier: BlockMod(Material.cake), ILexiconable {
	
	init {
		setBlockName("barrier")
		setBlockUnbreakable()
		disableStats()
		setLightOpacity(0)
	}
	
	override fun getIcon(side: Int, meta: Int) = Blocks.bedrock.getIcon(side, meta)!!
	
	override fun getRenderType() = -1
	
	override fun isOpaqueCube() = false
	
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	
	@SideOnly(Side.CLIENT)
	override fun getAmbientOcclusionLightValue() = 1f
	
	override fun dropBlockAsItemWithChance(worldIn: World, x: Int, y: Int, z: Int, meta: Int, chance: Float, fortune: Int) = Unit
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = null
	
	companion object {
		
		init {
			if (ASJUtilities.isClient) eventForge()
		}
		
		@SubscribeEvent
		fun onHighlight(e: DrawBlockHighlightEvent) {
			if (mc.theWorld.getBlock(e.target.blockX, e.target.blockY, e.target.blockZ) === AlfheimBlocks.barrier) e.isCanceled = !e.player.capabilities.isCreativeMode
		}
	}
}
