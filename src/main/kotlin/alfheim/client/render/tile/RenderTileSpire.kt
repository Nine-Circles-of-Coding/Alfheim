package alfheim.client.render.tile

import alexsocol.asjlib.mc
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.AdvancedModelLoader
import org.lwjgl.opengl.GL11.*
import vazkii.botania.client.core.helper.ShaderHelper

object RenderTileSpire: TileEntitySpecialRenderer() {
	
	val model = if (AlfheimConfigHandler.minimalGraphics) null else AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/Spire.obj"))
	
	override fun renderTileEntityAt(tile: TileEntity?, x: Double, y: Double, z: Double, partialTicks: Float) {
		if (model == null) return
		
		glPushMatrix()
		glDisable(GL_CULL_FACE)
		glAlphaFunc(GL_GREATER, 0f)
		
		glTranslated(x, y + 0.001, z)
		
		mc.renderEngine.bindTexture(LibResourceLocations.spire)
		model.renderPart("Spire")
		
		if (ShaderHelper.useShaders()) ShaderHelper.useShader(ShaderHelper.halo)
		ASJRenderHelper.setBlend()
		mc.renderEngine.bindTexture(LibResourceLocations.spireRunes)
		model.renderPart("SpireRunes")
		ASJRenderHelper.discard()
		if (ShaderHelper.useShaders()) ShaderHelper.releaseShader()
		
		glAlphaFunc(GL_GREATER, 0.1f)
		glEnable(GL_CULL_FACE)
		glPopMatrix()
	}
}
