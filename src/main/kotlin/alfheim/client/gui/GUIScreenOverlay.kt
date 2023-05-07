package alfheim.client.gui

import alexsocol.asjlib.*
import alfheim.api.lib.LibResourceLocations
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.relic.ItemTankMask.Companion.MAX_CORRUPTION
import alfheim.common.item.relic.ItemTankMask.Companion.limboCounter
import cpw.mods.fml.common.eventhandler.*
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.Tessellator
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.RenderGameOverlayEvent
import org.lwjgl.opengl.GL11.*

object GUIScreenOverlay: Gui() {
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	fun onOverlayRendering(event: RenderGameOverlayEvent.Post) {
		if (event.type != RenderGameOverlayEvent.ElementType.HELMET) return
		val player = mc.thePlayer
		
		val corrupt = player.limboCounter / MAX_CORRUPTION.F
		if (corrupt > 0 && !player.capabilities.isCreativeMode) {
			glColor4f(1f, 1f, 1f, corrupt)
			drawScreen(LibResourceLocations.corrupt)
			glColor4f(1f, 1f, 1f, 1f)
		}
		
		if (AlfheimConfigHandler.enableMMO && player.isPotionActive(AlfheimConfigHandler.potionIDIceLens))
			drawScreen(LibResourceLocations.iceLens)
	}
	
	fun drawScreen(texture: ResourceLocation) {
		glPushMatrix()
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		glDisable(GL_DEPTH_TEST)
		glDepthMask(false)
//		glDisable(GL_ALPHA_TEST)
		glAlphaFunc(GL_GREATER, 0f)
		
		mc.renderEngine.bindTexture(texture)
		val res = ScaledResolution(mc, mc.displayWidth, mc.displayHeight)
		val u = res.scaledWidth
		val v = res.scaledHeight
		val tes = Tessellator.instance
		tes.startDrawingQuads()
		tes.addVertexWithUV(0.0, 0.0, 0.0, 0.0, 0.0)
		tes.addVertexWithUV(0.0, v.D, 0.0, 0.0, 1.0)
		tes.addVertexWithUV(u.D, v.D, 0.0, 1.0, 1.0)
		tes.addVertexWithUV(u.D, 0.0, 0.0, 1.0, 0.0)
		tes.draw()
		
		glAlphaFunc(GL_GREATER, 0.1f)
//		glEnable(GL_ALPHA_TEST)
		glDepthMask(true)
		glEnable(GL_DEPTH_TEST)
		glDisable(GL_BLEND)
		glPopMatrix()
	}
}