package alfheim.client.render.world

import alexsocol.asjlib.*
import alexsocol.asjlib.render.*
import alfheim.api.lib.LibShaderIDs
import alfheim.client.core.handler.CardinalSystemClient.TimeStopSystemClient
import net.minecraft.client.renderer.Tessellator
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL
import org.lwjgl.opengl.GL20
import kotlin.math.*

object SpellVisualizations {
	
	val so: ShadedObject
	
	init {
		so = object: ShadedObject(LibShaderIDs.idNoise, RenderPostShaders.nextAvailableRenderObjectMaterialID, null) {
			
			override fun preRender() {
				glEnable(GL_RESCALE_NORMAL)
				
				glDisable(GL_LIGHTING)
				glDisable(GL_TEXTURE_2D)
				glColor4f(1f, 0f, 0f, 1f)
				
				glEnable(GL_CULL_FACE)
				glCullFace(GL_BACK)
			}
			
			override fun drawMesh(data: Array<out Any?>) {
				GL20.glUniform3f(GL20.glGetUniformLocation(shaderID, "color2"), 0f, 0f, 0f)
				renderSphere(data[0] as Double)
			}
			
			override fun postRender() {
				glColor4f(1f, 1f, 1f, 1f)
				glEnable(GL_TEXTURE_2D)
				glEnable(GL_LIGHTING)
				
				glDisable(GL_RESCALE_NORMAL)
			}
		}
		
		RenderPostShaders.registerShadedObject(so)
	}
	
	fun redSphere(x: Double, y: Double, z: Double, radius: Double) {
		glPushMatrix()
		ASJRenderHelper.interpolatedTranslationReverse(mc.thePlayer)
		val inside = TimeStopSystemClient.inside(mc.thePlayer)
		glTranslated(x, y, z)
		glEnable(GL_RESCALE_NORMAL)
		
		glDisable(GL_LIGHTING)
		glDisable(GL_TEXTURE_2D)
		glColor4f(0.25f, 0f, 0f, 1f)
		
		if (inside) {
			glColorMask(false, true, true, false)
			glCullFace(GL_FRONT)
			glDisable(GL_DEPTH_TEST)
			renderSphere(radius)
			glEnable(GL_DEPTH_TEST)
			glColorMask(true, true, true, true)
			glCullFace(GL_BACK)
		} else {
			if (RenderPostShaders.allowShaders)
				so.addTranslation(radius)
			else
				renderSphere(radius)
		}
		
		glColor4f(1f, 1f, 1f, 1f)
		glEnable(GL_TEXTURE_2D)
		glEnable(GL_LIGHTING)
		
		glDisable(GL_RESCALE_NORMAL)
		glPopMatrix()
	}
	
	fun negateSphere(radius: Double) {
		glPushMatrix()
		ASJRenderHelper.interpolatedTranslation(mc.thePlayer)
		val tes = Tessellator.instance
		
		glEnable(GL_RESCALE_NORMAL)
		glDisable(GL_CULL_FACE)
		glDepthMask(false)
		glDepthFunc(GL_LEQUAL)
		glEnable(GL_BLEND)
		glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR)
		glDisable(GL_TEXTURE_2D)
		glDisable(GL_LIGHTING)
		glColor4f(1f, 1f, 1f, 1f)
		
		glPushMatrix()
		glLoadIdentity()
		val z = -0.1
		tes.startDrawingQuads()
		tes.addVertex(-10.0, -10.0, z)
		tes.addVertex(-10.0, 10.0, z)
		tes.addVertex(10.0, 10.0, z)
		tes.addVertex(10.0, -10.0, z)
		tes.draw()
		glPopMatrix()
		
		renderSphere(radius)
		
		glEnable(GL_LIGHTING)
		glEnable(GL_TEXTURE_2D)
		glDisable(GL_BLEND)
		glDepthMask(true)
		glEnable(GL_CULL_FACE)
		glDisable(GL_RESCALE_NORMAL)
		
		glPopMatrix()
	}
	
	private const val MAGIC_RESCALING_CONST = 2.0317460317460316
	
	/**
	 * @author thKaguya
	 */
	fun renderSphere(radius: Double) {
		val width = radius * MAGIC_RESCALING_CONST
		val tes = Tessellator.instance
		val maxWidth = width / 2
		val zAngleDivNum = 18
		var angleZ: Double
		val angleSpanZ = PI * 2 / zAngleDivNum.D
		val zDivNum = 9
		var zPos = sin(-PI / 2) * maxWidth
		var zPosOld = zPos
		var xPos: Double
		var yPos: Double
		var xPos2: Double
		var yPos2: Double
		var xPosOld: Double
		var yPosOld: Double
		var xPos2Old: Double
		var yPos2Old: Double
		var w: Double
		var angle = -PI / 2
		val angleSpan = PI / zDivNum
		angle += angleSpan
		var widthOld = 0.0
		for (j in 0 until zDivNum) {
			zPos = sin(angle) * maxWidth
			w = cos(angle) * maxWidth
			angleZ = 0.0
			xPosOld = cos(angleZ) * w
			yPosOld = sin(angleZ) * w
			xPos2Old = cos(angleZ) * widthOld
			yPos2Old = sin(angleZ) * widthOld
			angleZ = angleSpanZ
			for (i in 1..zAngleDivNum) {
				xPos = cos(angleZ) * w
				yPos = sin(angleZ) * w
				xPos2 = cos(angleZ) * widthOld
				yPos2 = sin(angleZ) * widthOld
				tes.startDrawingQuads()
				//tes.setColorRGBA_F(1f, 1f, 1f , alpha);
				tes.setNormal(0f, 1f, 0f)
				tes.addVertexWithUV(xPos, yPos, zPos, 1.0, 0.0)
				tes.addVertexWithUV(xPosOld, yPosOld, zPos, 0.0, 0.0)
				tes.addVertexWithUV(xPos2Old, yPos2Old, zPosOld, 0.0, 1.0)
				tes.addVertexWithUV(xPos2, yPos2, zPosOld, 1.0, 1.0)
				tes.draw()
				xPosOld = xPos
				yPosOld = yPos
				xPos2Old = xPos2
				yPos2Old = yPos2
				angleZ += angleSpanZ
			}
			zPosOld = zPos
			angle += angleSpan
			widthOld = w
		}
	}
	
}
