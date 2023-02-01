package alfheim.client.render.world

import alexsocol.asjlib.*
import alexsocol.asjlib.render.*
import alfheim.api.lib.LibShaderIDs
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.WorldClient
import net.minecraftforge.client.IRenderHandler
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*
import java.awt.Color

open class SkyRendererDomains(/** 0xAARRGGBBu */ val color: UInt, val secondaryColor: UInt): IRenderHandler() {
	
	override fun render(partialTicks: Float, world: WorldClient?, mc: Minecraft) {
		val size = mc.gameSettings.renderDistanceChunks - 1
		
		glPushMatrix()
		glScalef(-size.F)
		
		glEnable(GL12.GL_RESCALE_NORMAL)
		
		glDisable(GL_LIGHTING)
		glDisable(GL_TEXTURE_2D)
		
		ASJRenderHelper.glColor1u(color)

		if (RenderPostShaders.allowShaders)
			ASJShaderHelper.useShader(LibShaderIDs.idNoise) {
				val (r, g, b) = Color(secondaryColor.toInt()).getRGBColorComponents(null)
				GL20.glUniform3f(GL20.glGetUniformLocation(it, "color2"), r, g, b)
			}
		
		SpellVisualizations.renderSphere(32.0)
		
		if (RenderPostShaders.allowShaders) ASJShaderHelper.releaseShader()
		
		glColor4f(1f, 1f, 1f, 1f)
		glEnable(GL_TEXTURE_2D)
		glEnable(GL_LIGHTING)
		
		glDisable(GL12.GL_RESCALE_NORMAL)
		
		glPopMatrix()
	}
}
