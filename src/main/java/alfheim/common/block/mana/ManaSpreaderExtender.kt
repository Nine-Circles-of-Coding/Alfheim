package alfheim.common.block.mana

import alfheim.api.lib.LibResourceLocations
import alfheim.client.core.util.mc
import alfheim.client.model.block.ModelSpreaderFrame
import alfheim.common.core.helper.IconHelper
import gloomyfolken.hooklib.asm.*
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.client.renderer.texture.*
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import vazkii.botania.api.mana.BurstProperties
import vazkii.botania.client.core.handler.HUDHandler
import vazkii.botania.client.core.proxy.ClientProxy
import vazkii.botania.client.lib.LibResources
import vazkii.botania.client.model.ModelSpreader
import vazkii.botania.client.render.block.RenderSpreader
import vazkii.botania.client.render.tile.RenderTileSpreader
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.block.mana.BlockSpreader
import vazkii.botania.common.block.tile.mana.TileSpreader
import vazkii.botania.common.entity.EntityManaBurst

@Suppress("UNUSED_PARAMETER", "NAME_SHADOWING", "unused", "FunctionName")
object ManaSpreaderExtender {
	
	const val MAX_MANA = 1000
	const val ULTRA_MAX_MANA = 6400
	const val UBER_MAX_MANA = 24000
	
	lateinit var icon: IIcon
	
	var staticUber = false
	
	// ######## BlockSpreader
	
	@JvmStatic
	@Hook
	fun registerBlockIcons(spreader: BlockSpreader, reg: IIconRegister) {
		icon = IconHelper.forName(reg, "UberSpreader")
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun getIcon(spreader: BlockSpreader, side: Int, meta: Int): IIcon = if (meta == 4) icon else if (meta >= 2) ModBlocks.dreamwood.getIcon(side, 0) else ModBlocks.livingwood.getIcon(side, 0)
	
	// ######## TileSpreader
	
	var burstPropHook = false
	
	@JvmStatic
	@Hook
	fun getBurst(tile: TileSpreader, fake: Boolean): EntityManaBurst? {
		if (isUBER_SPREADER(tile)) burstPropHook = true
		
		return null
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "<init>")
	fun `BurstProperties$init`(bp: BurstProperties, maxMana: Int, ticksBeforeManaLoss: Int, manaLossPerTick: Float, gravity: Float, motionModifier: Float, color: Int) {
		if (burstPropHook) {
			bp.maxMana = 2400
			bp.color = 0xFFD400
			bp.ticksBeforeManaLoss = 180
			bp.manaLossPerTick = 32f
			bp.motionModifier = 3f
			
			burstPropHook = false
		}
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun getMaxMana(tile: TileSpreader) = if (isUBER_SPREADER(tile)) UBER_MAX_MANA else if (tile.isULTRA_SPREADER) ULTRA_MAX_MANA else MAX_MANA
	
	fun isUBER_SPREADER(tile: TileSpreader) = if (tile.worldObj == null) staticUber else tile.getBlockMetadata() == 4
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun renderHUD(tile: TileSpreader, mc: Minecraft, res: ScaledResolution) {
		val name = StatCollector.translateToLocal(ItemStack(ModBlocks.spreader, 1, tile.getBlockMetadata()).unlocalizedName.replace("tile.".toRegex(), "tile." + LibResources.PREFIX_MOD) + ".name")
		val color = if (isUBER_SPREADER(tile)) 0xFFD400 else if (tile.isRedstone) 0xFF0000 else if (tile.isDreamwood) 0xFF00AE else 0x00FF00
		HUDHandler.drawSimpleManaHUD(color, tile.knownMana, tile.maxMana, name, res)
		val lens: ItemStack? = tile.getStackInSlot(0)
		if (lens != null) {
			GL11.glEnable(GL11.GL_BLEND)
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
			val lensName = lens.displayName
			val width = 16 + mc.fontRenderer.getStringWidth(lensName) / 2
			val x = res.scaledWidth / 2 - width
			val y = res.scaledHeight / 2 + 50
			mc.fontRenderer.drawStringWithShadow(lensName, x + 20, y + 5, color)
			RenderHelper.enableGUIStandardItemLighting()
			RenderItem.getInstance().renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, lens, x, y)
			RenderHelper.disableStandardItemLighting()
			GL11.glDisable(GL11.GL_LIGHTING)
			GL11.glDisable(GL11.GL_BLEND)
		}
		if (tile.receiver != null) {
			val receiverTile = tile.receiver as TileEntity
			val recieverStack = ItemStack(tile.worldObj.getBlock(receiverTile.xCoord, receiverTile.yCoord, receiverTile.zCoord), 1, receiverTile.getBlockMetadata())
			GL11.glEnable(GL11.GL_BLEND)
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
			
			@Suppress("UNNECESSARY_SAFE_CALL")
			if (recieverStack?.item != null) {
				val stackName = recieverStack.displayName
				val width = 16 + mc.fontRenderer.getStringWidth(stackName) / 2
				val x = res.scaledWidth / 2 - width
				val y = res.scaledHeight / 2 + 30
				mc.fontRenderer.drawStringWithShadow(stackName, x + 20, y + 5, color)
				RenderHelper.enableGUIStandardItemLighting()
				RenderItem.getInstance().renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, recieverStack, x, y)
				RenderHelper.disableStandardItemLighting()
			}
			GL11.glDisable(GL11.GL_LIGHTING)
			GL11.glDisable(GL11.GL_BLEND)
		}
		GL11.glColor4f(1f, 1f, 1f, 1f)
	}
	
	
	// ######## RenderSpreader
	
	@JvmStatic
	@Hook
	fun renderInventoryBlock(render: RenderSpreader, block: Block, metadata: Int, modelID: Int, renderer: RenderBlocks) {
		staticUber = metadata == 4
	}
	
	// ######## RenderTileSpreader
	
	var textureHook = false
	var modelHook = false
	
	@JvmStatic
	@Hook
	fun renderTileEntityAt(render: RenderTileSpreader, tile: TileEntity, d0: Double, d1: Double, d2: Double, ticks: Float) {
		if (isUBER_SPREADER(tile as? TileSpreader ?: return)) {
			textureHook = true
			modelHook = true
		}
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE)
	fun bindTexture(tm: TextureManager, loc: ResourceLocation?): Boolean {
		if (textureHook) {
			textureHook = false
			tm.bindTexture(if (ClientProxy.dootDoot) LibResourceLocations.uberSpreaderHalloween else LibResourceLocations.uberSpreader)
			
			return true
		}
		
		return false
	}
	
	// ######## ModelSpreader
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun render(model: ModelSpreader) {
		if (modelHook) {
			mc.renderEngine.bindTexture(LibResourceLocations.uberSpreaderFrame)
			
			var s = 1.1f
			val t = s - 1
			glTranslatef(0f, -t, 0f)
			glScalef(s, s, s)
			ModelSpreaderFrame.render()
			s = 1 / s
			glScalef(s, s, s)
			glTranslatef(0f, t, 0f)
			
			modelHook = false
			
			mc.renderEngine.bindTexture(if (ClientProxy.dootDoot) LibResourceLocations.uberSpreaderHalloween else LibResourceLocations.uberSpreader)
		}
	}
}