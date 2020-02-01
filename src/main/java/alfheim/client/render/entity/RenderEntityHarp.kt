package alfheim.client.render.entity

import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.client.core.util.mc
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.F
import alfheim.common.item.material.ItemElvenResource
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.AdvancedModelLoader
import org.lwjgl.opengl.GL11.*
import kotlin.math.sin

class RenderEntityHarp: Render() {
	init {
		shadowSize = 0f
	}
	
	override fun getEntityTexture(entity: Entity): ResourceLocation {
		return LibResourceLocations.harp
	}
	
	override fun doRender(entity: Entity, x: Double, y: Double, z: Double, yaw: Float, partialTick: Float) {
		glPushMatrix()
		glTranslated(x, y + 0.2 + sin((mc.theWorld.totalWorldTime.F + entity.ticksExisted.F + mc.timer.renderPartialTicks) / 50.0) / 10.0, z)
		glRotated((mc.theWorld.totalWorldTime + entity.ticksExisted + mc.timer.renderPartialTicks) * 0.5, 0.0, 1.0, 0.0)
		
		if (AlfheimConfigHandler.minimalGraphics) {
			glTranslated(-0.5, 0.0, 0.0)
			mc.renderEngine.bindTexture(TextureMap.locationItemsTexture)
			ItemRenderer.renderItemIn2D(Tessellator.instance, ItemElvenResource.harp.maxU, ItemElvenResource.harp.minV, ItemElvenResource.harp.minU, ItemElvenResource.harp.maxV, ItemElvenResource.harp.iconWidth, ItemElvenResource.harp.iconHeight, 1f / 16f)
			glTranslated(0.5, 0.0, 0.0)
		} else {
			mc.renderEngine.bindTexture(getEntityTexture(entity))
			model.renderAll()
		}
		glPopMatrix()
	}
	
	companion object {
		
		val model = AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/Arfa.obj"))
	}
}