package alfheim.client.render.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
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
		
		mc.renderEngine.bindTexture(getEntityTexture(entity))
		val tes = Tessellator.instance
		
		val icon = entity.block.getIcon(0, entity.meta)
		val u = icon.minU.D
		val U = icon.maxU.D
		val v = icon.minV.D
		val V = icon.maxV.D
		
		fun a(x: Int, y: Int, z: Int) = tes.addVertexWithUV(x.D, y.D, z.D, U, V)
		fun b(x: Int, y: Int, z: Int) = tes.addVertexWithUV(x.D, y.D, z.D, U, v)
		fun c(x: Int, y: Int, z: Int) = tes.addVertexWithUV(x.D, y.D, z.D, u, v)
		fun d(x: Int, y: Int, z: Int) = tes.addVertexWithUV(x.D, y.D, z.D, u, V)
		fun color(c: Double) = tes.setColorOpaque_F(c.F, c.F, c.F)
		
		tes.startDrawingQuads()
		
		// yNeg
		color(0.5)
		c(0, 0, 0)
		b(1, 0, 0)
		a(1, 0, 1)
		d(0, 0, 1)
		
		// zPos
		color(0.8)
		a(1, 0, 1)
		b(1, 1, 1)
		c(0, 1, 1)
		d(0, 0, 1)
		
		// xPos
		color(0.6)
		a(1, 0, 0)
		b(1, 1, 0)
		c(1, 1, 1)
		d(1, 0, 1)
		
		// zNeg
		color(0.8)
		a(0, 0, 0)
		b(0, 1, 0)
		c(1, 1, 0)
		d(1, 0, 0)
		
		// xNeg
		color(0.6)
		a(0, 0, 1)
		b(0, 1, 1)
		c(0, 1, 0)
		d(0, 0, 0)
		
		// yPos
		color(1.0)
		a(1, 1, 1)
		b(1, 1, 0)
		c(0, 1, 0)
		d(0, 1, 1)
		
		tes.draw()
		glEnable(GL_LIGHTING)
		glPopMatrix()
	}
	
	override fun getEntityTexture(entity: Entity?) = TextureMap.locationBlocksTexture!!
}
