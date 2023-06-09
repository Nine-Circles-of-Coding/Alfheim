package alfheim.client.render.item

import alexsocol.asjlib.*
import alfheim.api.AlfheimAPI
import alfheim.api.lib.LibResourceLocations
import alfheim.common.block.AlfheimBlocks
import alfheim.common.item.block.ItemBlockAnomaly
import net.minecraft.client.renderer.Tessellator
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer
import net.minecraftforge.client.IItemRenderer.ItemRenderType
import org.lwjgl.opengl.GL11.*

//Render from Thaumcraft nodes by Azanor
object RenderItemAnomaly: IItemRenderer {
	
	override fun handleRenderType(item: ItemStack?, type: ItemRenderType) =
		item != null && item.item === AlfheimBlocks.anomaly.toItem() && ItemBlockAnomaly.getType(item) != ItemBlockAnomaly.TYPE_UNDEFINED
	
	override fun shouldUseRenderHelper(type: ItemRenderType, item: ItemStack, helper: IItemRenderer.ItemRendererHelper) =
		helper != IItemRenderer.ItemRendererHelper.EQUIPPED_BLOCK
	
	override fun renderItem(type: ItemRenderType, item: ItemStack, vararg data: Any) {
		if (type == ItemRenderType.ENTITY) {
			glScaled(2.0)
			glTranslated(-0.5, -0.25, -0.5)
		} else if (type == ItemRenderType.EQUIPPED && data[1] is EntityPlayer) {
			glTranslated(0.0, 0.0, -0.5)
		} else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			glRotated(93.2, 1.0, 0.0, 0.0)
			glTranslated(0.0, -0.5, -1.0)
		}
		
		val (_, _, strip, color) = AlfheimAPI.getAnomaly(ItemBlockAnomaly.getType(item))
		renderItemAnomaly(strip, color, 32)
	}
	
	fun renderItemAnomaly(strip: Int, color: Int, frames: Int) {
		glPushMatrix()
		glAlphaFunc(GL_GREATER, 1 / 255f)
		glDepthMask(false)
		glDisable(GL_CULL_FACE)
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		glColor4d(1.0, 1.0, 1.0, 1.0)
		glPushMatrix()
		
		mc.renderEngine.bindTexture(LibResourceLocations.anomalies)
		val frame = ((System.nanoTime() / 40000000L + 1L) % 32.toLong()).I
		
		glTranslated(0.5, 0.5, 0.5)
		renderAnimatedQuadStrip(1.5f, 1f, frames, strip, frame, color)
		glRotated(90.0, 0.0, 1.0, 0.0)
		renderAnimatedQuadStrip(1.5f, 1f, frames, strip, frame, color)
		glRotatef(90f, 1f, 0f, 0f)
		renderAnimatedQuadStrip(1.5f, 1f, frames, strip, frame, color)
		
		glPopMatrix()
		glDisable(GL_BLEND)
		glEnable(GL_CULL_FACE)
		glDepthMask(true)
		glAlphaFunc(GL_GREATER, 0.1f)
		glPopMatrix()
	}
	
	fun renderAnimatedQuadStrip(scale: Float, alpha: Float, frames: Int, strip: Int, cframe: Int, color: Int) {
		if (mc.renderViewEntity is EntityPlayer) {
			val tessellator = Tessellator.instance
			tessellator.startDrawingQuads()
			tessellator.setBrightness(220)
			tessellator.setColorRGBA_I(color, (alpha * 255f).I)
			val f2 = cframe.F / frames.F
			val f3 = (cframe + 1).F / frames.F
			val f4 = strip.F / frames.F
			val f5 = (strip + 1).F / frames.F
			tessellator.setNormal(0f, 0f, -1f)
			tessellator.addVertexWithUV(-0.5 * scale.D, 0.5 * scale.D, 0.0, f2.D, f5.D)
			tessellator.addVertexWithUV(0.5 * scale.D, 0.5 * scale.D, 0.0, f3.D, f5.D)
			tessellator.addVertexWithUV(0.5 * scale.D, -0.5 * scale.D, 0.0, f3.D, f4.D)
			tessellator.addVertexWithUV(-0.5 * scale.D, -0.5 * scale.D, 0.0, f2.D, f4.D)
			tessellator.draw()
		}
	}
}