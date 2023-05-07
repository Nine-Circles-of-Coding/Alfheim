package alfheim.client.render.entity

import alexsocol.asjlib.mc
import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.entity.spell.EntitySpellWindBlade
import alfheim.common.item.material.ItemElvenResource
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.AdvancedModelLoader
import org.lwjgl.opengl.GL11.*

object RenderEntityWindBlade: Render() {
	
	val model = if (AlfheimConfigHandler.minimalGraphics) null else AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/tor.obj"))
	
	init {
		shadowSize = 0f
	}
	
	override fun getEntityTexture(entity: Entity) = LibResourceLocations.wind
	
	override fun doRender(e: Entity, x: Double, y: Double, z: Double, yaw: Float, partialTick: Float) {
		e as EntitySpellWindBlade
		
		glPushMatrix()
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		glAlphaFunc(GL_GREATER, 1 / 255f)
		glTranslated(x, y + 0.05, z)
		
		if (e.isFenrir)
			glTranslatef(0f, 5/2f, 0f)
			
		val time = if (e.isFenrir) 0f else mc.theWorld.totalWorldTime + e.ticksExisted + partialTick
		
		// fucking rotations :3
		if (e.isFenrir) {
			glRotatef(90f, 0f, 0f, 1f)
			glRotatef(-e.rotationYaw, 1f, 0f, 0f)
		} else {
			glRotatef(-e.rotationYaw + time, 0f, 1f, 0f)
		}
		
		if (model == null) {
			if (e.isFenrir)
				glScaled(5.0, 2.5, 1.0)
			else
				glScaled(3.0, 0.5, 3.0)
			
			glRotated(90.0, 1.0, 0.0, 0.0)
			glTranslated(-0.5, -0.5, 0.03125)
			mc.renderEngine.bindTexture(TextureMap.locationItemsTexture)
			ItemRenderer.renderItemIn2D(Tessellator.instance, ItemElvenResource.wind.maxU, ItemElvenResource.wind.minV, ItemElvenResource.wind.minU, ItemElvenResource.wind.maxV, ItemElvenResource.wind.iconWidth, ItemElvenResource.wind.iconHeight, 1f / 16f)
		} else {
			if (e.isFenrir)
				glScaled(5/3.0, 0.25, 0.25)
			else
				glScalef(1f, 0.1f, 1f)
			mc.renderEngine.bindTexture(LibResourceLocations.wind)
			model.renderAll()
		}
		
		glDisable(GL_BLEND)
		glPopMatrix()
	}
}