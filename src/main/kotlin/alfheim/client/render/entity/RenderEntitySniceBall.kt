package alfheim.client.render.entity

import alexsocol.asjlib.mc
import alfheim.api.lib.LibResourceLocations
import alfheim.client.render.entity.RenderEntityDriftingMine.model
import alfheim.common.item.equipment.tool.ItemSnowSword
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11.*

object RenderEntitySniceBall: Render() {
	
	init {
		shadowSize = 0.25f
	}
	
	override fun getEntityTexture(entity: Entity) = LibResourceLocations.ice
	
	override fun doRender(entity: Entity, x: Double, y: Double, z: Double, yaw: Float, partialTick: Float) {
		glPushMatrix()
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		
		glTranslated(x, y + 0.25, z)
		glRotated((mc.theWorld.totalWorldTime + entity.ticksExisted + mc.timer.renderPartialTicks) * 0.5, 1.0, 1.0, 1.0)
		
		alexsocol.asjlib.glScaled(0.5)
		
		if (model == null) {
			mc.renderEngine.bindTexture(TextureMap.locationItemsTexture)
			glTranslated(-0.5, -0.5, 0.03125)
			ItemRenderer.renderItemIn2D(Tessellator.instance, ItemSnowSword.snice.maxU, ItemSnowSword.snice.minV, ItemSnowSword.snice.minU, ItemSnowSword.snice.maxV, ItemSnowSword.snice.iconWidth, ItemSnowSword.snice.iconHeight, 1f / 16f)
			
			glRotated(90.0, 0.0, 1.0, 0.0)
			glTranslated(-0.5, 0.0, 0.5 + 0.0625)
			ItemRenderer.renderItemIn2D(Tessellator.instance, ItemSnowSword.snice.maxU, ItemSnowSword.snice.minV, ItemSnowSword.snice.minU, ItemSnowSword.snice.maxV, ItemSnowSword.snice.iconWidth, ItemSnowSword.snice.iconHeight, 1f / 16f)
			
			glRotated(90.0, 1.0, 0.0, 0.0)
			glTranslated(0.0, -0.5, -0.5 + 0.0625)
			ItemRenderer.renderItemIn2D(Tessellator.instance, ItemSnowSword.snice.maxU, ItemSnowSword.snice.minV, ItemSnowSword.snice.minU, ItemSnowSword.snice.maxV, ItemSnowSword.snice.iconWidth, ItemSnowSword.snice.iconHeight, 1f / 16f)
		} else {
			mc.renderEngine.bindTexture(LibResourceLocations.snow)
			model.renderPart("insphere")
			mc.renderEngine.bindTexture(LibResourceLocations.ice)
			model.renderPart("outsphere")
			model.renderPart("spikes")
		}
		
		glEnable(GL_BLEND)
		glPopMatrix()
	}
}