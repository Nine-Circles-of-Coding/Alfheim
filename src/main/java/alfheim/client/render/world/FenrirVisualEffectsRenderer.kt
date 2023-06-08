package alfheim.client.render.world

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.lib.LibResourceLocations
import net.minecraft.client.renderer.Tessellator
import org.lwjgl.opengl.GL11.*

object FenrirVisualEffectsRenderer {
	
	val renderList = ArrayList<Triple<Array<Double>, Int, Int>>()
	
	fun addArea(x: Double, y: Double, z: Double, ticks: Int) {
		renderList.add(arrayOf(x, y, z) to 0 with ticks)
	}
	
	fun renderAll(ticks: Float) {
		if (renderList.isEmpty()) return
		
		glPushMatrix()
		ASJRenderHelper.interpolatedTranslationReverse(mc.thePlayer)
		
		ASJRenderHelper.setBlend()
		ASJRenderHelper.setTwoside()
		mc.renderEngine.bindTexture(LibResourceLocations.fenrirArea)
		
		Tessellator.instance.startDrawingQuads()
		val iterator = renderList.iterator()
		while (iterator.hasNext()) {
			val it = iterator.next()
			
			val (x, y, z) = it.first
			val age = it.second
			if (age > it.third) {
				iterator.remove()
				continue
			}
			
			val scale = if (age < 5) age / 5f else 1f
			glColor4f(1f, 1f, 1f, if (age >= (it.third - 5)) (it.third - age) / 5f else 1f)
			
			Tessellator.instance.setTranslation(x, y, z)
			Tessellator.instance.addVertexWithUV(-8.0 * scale, 0.01, -8.0 * scale, 0.0, 0.0)
			Tessellator.instance.addVertexWithUV(-8.0 * scale, 0.01,  8.0 * scale, 0.0, 1.0)
			Tessellator.instance.addVertexWithUV( 8.0 * scale, 0.01,  8.0 * scale, 1.0, 1.0)
			Tessellator.instance.addVertexWithUV( 8.0 * scale, 0.01, -8.0 * scale, 1.0, 0.0)
		}
		
		Tessellator.instance.draw()
		
		Tessellator.instance.setTranslation(0.0, 0.0, 0.0)
		
		ASJRenderHelper.discard()
		glPopMatrix()
	}
}
