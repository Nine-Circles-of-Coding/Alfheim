package alfheim.client.core.proxy

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.entity.ModelIcicle
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.entity.EntityIcicle
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.AdvancedModelLoader
import org.lwjgl.opengl.GL11.*
import kotlin.math.*

object RenderEntityIcicle: Render() {
	
	val model = if (AlfheimConfigHandler.minimalGraphics) null else AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/Icicle.obj"))
	
	override fun doRender(entity: Entity, x: Double, y: Double, z: Double, yaw: Float, ticks: Float) {
		entity as EntityIcicle
		
		glPushMatrix()
		glTranslated(x, y, z)
		if (model != null) glScaled(0.1)
		ASJRenderHelper.setBlend()
		
		// FUCKING ROTATIONS!!!
		val (mx, my, mz) = Vector3(entity.motionX, entity.motionY, entity.motionZ)
		glRotatef(-90f, 1f, 0f, 0f)
		glRotatef(Math.toDegrees(atan(mx / mz)).F + if (mz < 0) 0 else 180, 0f, 0f, 1f)
		glRotatef(-Math.toDegrees(atan(sqrt(mx * mx + mz * mz) / my)).F + if (my < 0) -90 else 90, 1f, 0f, 0f)
		
		mc.renderEngine.bindTexture(getEntityTexture(entity))
		model?.renderAll() ?: run {
			glRotatef(180f, 1f, 0f, 0f)
			glTranslatef(0f, -1f, 0f)
			ModelIcicle.render(0.0625f)
		}
		
		ASJRenderHelper.discard()
		glPopMatrix()
	}
	
	override fun getEntityTexture(entity: Entity?) = LibResourceLocations.nifleice
}
