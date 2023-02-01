package alfheim.client.core.handler

import alexsocol.asjlib.mc
import alfheim.common.item.AlfheimItems
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraftforge.client.event.RenderGameOverlayEvent
import org.lwjgl.opengl.*
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex
import kotlin.math.max

object HUDCorporeaRat {
	
	@SubscribeEvent
	fun onDrawScreenPost(event: RenderGameOverlayEvent.Post) {
		if (TileCorporeaIndex.InputHandler.getNearbyIndexes(mc.thePlayer).isNotEmpty() ||
			mc.thePlayer.heldItem?.item !== AlfheimItems.corporeaRat ||
			mc.currentScreen !is GuiChat ||
			event.type != RenderGameOverlayEvent.ElementType.ALL)
			return
		
		mc.mcProfiler.startSection("ratInHand")
		renderRatInHandDisplay(event.resolution)
		mc.mcProfiler.endSection()
	}
	
	fun renderRatInHandDisplay(res: ScaledResolution) {
		val txt0 = StatCollector.translateToLocal("alfheimmisc.quandex")
		val txt1 = EnumChatFormatting.GRAY.toString() + StatCollector.translateToLocal("botaniamisc.nearIndex1")
		val txt2 = EnumChatFormatting.GRAY.toString() + StatCollector.translateToLocal("botaniamisc.nearIndex2")
		val l = max(mc.fontRenderer.getStringWidth(txt0), max(mc.fontRenderer.getStringWidth(txt1), mc.fontRenderer.getStringWidth(txt2))) + 20
		val x = res.scaledWidth - l - 20
		val y = res.scaledHeight - 60
		Gui.drawRect(x - 6, y - 6, x + l + 6, y + 37, 0x44000000)
		Gui.drawRect(x - 4, y - 4, x + l + 4, y + 35, 0x44000000)
		RenderHelper.enableGUIStandardItemLighting()
		GL11.glEnable(GL12.GL_RESCALE_NORMAL)
		RenderItem.getInstance().renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, ItemStack(AlfheimItems.corporeaRat), x, y + 10)
		RenderHelper.disableStandardItemLighting()
		mc.fontRenderer.drawStringWithShadow(txt0, x + 20, y, 0xFFFFFF)
		mc.fontRenderer.drawStringWithShadow(txt1, x + 20, y + 14, 0xFFFFFF)
		mc.fontRenderer.drawStringWithShadow(txt2, x + 20, y + 24, 0xFFFFFF)
	}
}
