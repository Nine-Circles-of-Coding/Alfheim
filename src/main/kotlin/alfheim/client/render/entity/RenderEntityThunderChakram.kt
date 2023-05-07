package alfheim.client.render.entity

import alexsocol.asjlib.*
import alfheim.common.entity.EntityThunderChakram
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.entity.Entity
import net.minecraft.util.*
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*

object RenderEntityThunderChakram: Render() {
	
	override fun doRender(entity: Entity, x: Double, y: Double, z: Double, yaw: Float, ticks: Float) {
		val c = entity as EntityThunderChakram
		val icon = c.itemStack.item.getIconFromDamage(0)
		
		glPushMatrix()
		glTranslated(x, y, z)
		glEnable(GL12.GL_RESCALE_NORMAL)
		glScalef(0.5f)
		
		bindEntityTexture(entity)
		drawBillboard(icon)
		
		glDisable(GL12.GL_RESCALE_NORMAL)
		glPopMatrix()
	}
	
	private fun drawBillboard(icon: IIcon) {
		val tessellator = Tessellator.instance
		
		glRotatef(180f - renderManager.playerViewY, 0f, 1f, 0f)
		glRotatef(-renderManager.playerViewX, 1f, 0f, 0f)
		
		tessellator.startDrawingQuads()
		tessellator.setNormal(0f, 1f, 0f)
		tessellator.setBrightness(240)
		tessellator.addVertexWithUV(-0.5, -0.25, 0.0, icon.minU.D, icon.maxV.D)
		tessellator.addVertexWithUV(0.5, -0.25, 0.0, icon.maxU.D, icon.maxV.D)
		tessellator.addVertexWithUV(0.5, 0.75, 0.0, icon.maxU.D, icon.minV.D)
		tessellator.addVertexWithUV(-0.5, 0.75, 0.0, icon.minU.D, icon.minV.D)
		tessellator.draw()
	}
	
	override fun getEntityTexture(entity: Entity) = TextureMap.locationItemsTexture!!
}