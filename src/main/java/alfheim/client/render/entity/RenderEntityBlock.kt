package alfheim.client.render.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.common.entity.EntityBlock
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11.*

object RenderEntityBlock: Render() {
	
	override fun doRender(entity: Entity?, x: Double, y: Double, z: Double, yaw: Float, ticks: Float) {
		if (entity !is EntityBlock) return
		
		glPushMatrix()
		glTranslated(x - 0.5, y, z - 0.5)
		glDisable(GL_LIGHTING)
		ASJRenderHelper.setBlend()
		
		mc.renderEngine.bindTexture(getEntityTexture(entity))
		val tes = Tessellator.instance
		
		var u = 0.0
		var U = 0.0
		var v = 0.0
		var V = 0.0
		
		fun i(s: Int) {
			val icon = entity.block.getIcon(s, entity.meta)
			u = icon.minU.D
			U = icon.maxU.D
			v = icon.minV.D
			V = icon.maxV.D
		}
		fun a(x: Int, y: Int, z: Int) = tes.addVertexWithUV(x.D, y.D, z.D, U, V)
		fun b(x: Int, y: Int, z: Int) = tes.addVertexWithUV(x.D, y.D, z.D, U, v)
		fun c(x: Int, y: Int, z: Int) = tes.addVertexWithUV(x.D, y.D, z.D, u, v)
		fun d(x: Int, y: Int, z: Int) = tes.addVertexWithUV(x.D, y.D, z.D, u, V)
		fun color(c: Double) = tes.setColorOpaque_F(c.F, c.F, c.F)
		
		tes.startDrawingQuads()
		
		// yNeg
		i(0)
		color(0.5)
		c(0, 0, 0)
		b(1, 0, 0)
		a(1, 0, 1)
		d(0, 0, 1)
		
		// yPos
		i(1)
		color(1.0)
		a(1, 1, 1)
		b(1, 1, 0)
		c(0, 1, 0)
		d(0, 1, 1)
		
		// zNeg
		i(2)
		color(0.8)
		a(0, 0, 0)
		b(0, 1, 0)
		c(1, 1, 0)
		d(1, 0, 0)
		
		// zPos
		i(3)
		color(0.8)
		a(1, 0, 1)
		b(1, 1, 1)
		c(0, 1, 1)
		d(0, 0, 1)
		
		// xNeg
		i(4)
		color(0.6)
		a(0, 0, 1)
		b(0, 1, 1)
		c(0, 1, 0)
		d(0, 0, 0)
		
		// xPos
		i(5)
		color(0.6)
		a(1, 0, 0)
		b(1, 1, 0)
		c(1, 1, 1)
		d(1, 0, 1)
		
		tes.draw()
		ASJRenderHelper.discard()
		glEnable(GL_LIGHTING)
		glPopMatrix()
	}
	
	override fun getEntityTexture(entity: Entity?) = TextureMap.locationBlocksTexture!!
}
