package alfheim.client.render.entity

import alexsocol.asjlib.render.*
import net.minecraft.client.renderer.Tessellator
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import vazkii.botania.client.core.helper.ShaderHelper

open class ShadedObjectHaloPlane(texture: ResourceLocation): ShadedObject(ShaderHelper.halo, RenderPostShaders.nextAvailableRenderObjectMaterialID, texture) {
	
	init {
		RenderPostShaders.registerShadedObject(this)
	}
	
	override fun preRender() {
		GL11.glEnable(GL11.GL_BLEND)
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
		GL11.glDisable(GL11.GL_CULL_FACE)
		GL11.glShadeModel(GL11.GL_SMOOTH)
	}
	
	override fun drawMesh(data: Array<out Any?>) {
		val tes = Tessellator.instance
		tes.startDrawingQuads()
		tes.addVertexWithUV(-1.0, 0.0, -1.0, 0.0, 0.0)
		tes.addVertexWithUV(-1.0, 0.0, 1.0, 0.0, 1.0)
		tes.addVertexWithUV(1.0, 0.0, 1.0, 1.0, 1.0)
		tes.addVertexWithUV(1.0, 0.0, -1.0, 1.0, 0.0)
		tes.draw()
	}
	
	override fun postRender() {
		GL11.glShadeModel(GL11.GL_FLAT)
		GL11.glEnable(GL11.GL_CULL_FACE)
		GL11.glDisable(GL11.GL_BLEND)
	}
}