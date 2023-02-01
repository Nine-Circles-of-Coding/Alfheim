package alfheim.client.gui

import alexsocol.asjlib.*
import alfheim.common.core.handler.SheerColdHandler.cold
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.client.gui.Gui
import net.minecraftforge.client.event.RenderGameOverlayEvent
import org.lwjgl.opengl.GL11.*
import vazkii.botania.client.core.helper.RenderHelper
import vazkii.botania.common.core.handler.ConfigHandler
import java.awt.Color
import kotlin.math.abs

object GUISheerCold: Gui() {
	
	@SubscribeEvent
	fun onOverlayRendering(e: RenderGameOverlayEvent.Post) {
		if (e.type != RenderGameOverlayEvent.ElementType.ALL) return
		
		val cold = mc.thePlayer.cold
		if (cold == 0f) return
		
		val res = e.resolution
		val width = (182 * (abs(cold) / 100f)).I
		val x = res.scaledWidth / 2 - 91
		val y = res.scaledHeight - ConfigHandler.manaBarHeight + 5
		
		glDisable(GL_TEXTURE_2D)
		val (r, g, b) = (if (cold > 0) Color(0xBFF4FF) else Color(0xFF4D00)).getRGBColorComponents(null)
		glColor4f(r, g, b, 1f)
		RenderHelper.drawTexturedModalRect(x, y, 0f, 0, 0, width, 1)
		glColor4f(1f, 1f, 1f, 1f)
		glEnable(GL_TEXTURE_2D)
	}
}