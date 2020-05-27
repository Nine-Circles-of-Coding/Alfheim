package alexsocol.asjlib.render

import alexsocol.asjlib.D
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.entity.Entity
import net.minecraft.util.*
import org.lwjgl.opengl.GL11.*
import java.awt.Color

object ASJRenderHelper {
	
	/**
	 * Draws three basis vectors x y z
	 */
	@JvmStatic
	fun drawGuideArrows() {
		glDisable(GL_TEXTURE_2D)
		
		glColor4f(0f, 0f, 1f, 1f)
		glBegin(GL_LINES)
		glVertex3i(0, 0, 0)
		glVertex3i(0, 0, 1)
		glEnd()
		
		glColor4f(0f, 1f, 0f, 1f)
		glBegin(GL_LINES)
		glVertex3i(0, 0, 0)
		glVertex3i(0, 1, 0)
		glEnd()
		
		glColor4f(1f, 0f, 0f, 1f)
		glBegin(GL_LINES)
		glVertex3i(0, 0, 0)
		glVertex3i(1, 0, 0)
		glEnd()
		
		glColor4f(1f, 1f, 1f, 1f)
		
		glEnable(GL_TEXTURE_2D)
	}
	
	val colorCode = IntArray(32)
	
	init {
		for (i in 0..31) {
			val j = (i shr 3 and 1) * 85
			var k = (i shr 2 and 1) * 170 + j
			var l = (i shr 1 and 1) * 170 + j
			var i1 = (i and 1) * 170 + j
			
			if (i == 6) {
				k += 85
			}
			
			if (i >= 16) {
				k /= 4
				l /= 4
				i1 /= 4
			}
			
			colorCode[i] = k and 255 shl 16 or (l and 255 shl 8) or (i1 and 255)
		}
	}
	
	fun Color.toVec3() = Vec3.createVectorHelper(red.D, green.D, blue.D)
	
	/**
	 * @return enum color packed in uInt with max alpha
	 * @author qiexie
	 */
	@JvmStatic
	fun enumColorToRGB(eColor: EnumChatFormatting) = addAlpha(colorCode[eColor.ordinal], 0xff)
	
	/**
	 * Adds `alpha` value to @{code color}
	 */
	@JvmStatic
	fun addAlpha(color: Int, alpha: Int) = alpha and 0xFF shl 24 or (color and 0x00FFFFFF)
	
	/**
	 * Sets render color unpacked from uInt
	 */
	@JvmStatic
	fun glColor1u(color: Int) {
		glColor4ub((color shr 16 and 0xFF).toByte(), (color shr 8 and 0xFF).toByte(), (color and 0xFF).toByte(), (color shr 24 and 0xFF).toByte())
	}
	
	/**
	 * Interpolates values, e.g. for smoother render
	 */
	@JvmStatic
	fun interpolate(last: Double, now: Double) = last + (now - last) * Minecraft.getMinecraft().timer.renderPartialTicks
	
	/**
	 * Translates matrix to follow player (if something is bound to world's zero coords)
	 */
	@JvmStatic
	fun interpolatedTranslation(entity: Entity) =
		glTranslated(interpolate(entity.lastTickPosX, entity.posX), interpolate(entity.lastTickPosY, entity.posY), interpolate(entity.lastTickPosZ, entity.posZ))
	
	/**
	 * Translates matrix not to follow player (if something is bound to camera's zero coords)
	 */
	@JvmStatic
	fun interpolatedTranslationReverse(entity: Entity) =
		glTranslated(-interpolate(entity.lastTickPosX, entity.posX), -interpolate(entity.lastTickPosY, entity.posY), -interpolate(entity.lastTickPosZ, entity.posZ))
	
	/**
	 * Sets matrix and translation to world's zero coordinates
	 * so you can render something as in TileEntitySpecialRenderer (if you are used to it)
	 * Don't forget to call [.postRenderISBRH]
	 * Use this before your render something in ISimpleBlockRenderingHandler
	 */
	@JvmStatic
	fun preRenderISBRH(x: Int, z: Int) {
		val X = (x / 16 - if (x < 0 && x % 16 != 0) 1 else 0) * -16
		val Z = (z / 16 - if (z < 0 && z % 16 != 0) 1 else 0) * -16
		Tessellator.instance.draw()
		Tessellator.instance.setTranslation(0.0, 0.0, 0.0)
		glPushMatrix()
		glTranslated(X.D, 0.0, Z.D)
	}
	
	/**
	 * This gets everything back for other blocks to render properly
	 * Don't use unless you've used [.preRenderISBRH]
	 * Use this after your render something in ISimpleBlockRenderingHandler
	 */
	@JvmStatic
	fun postRenderISBRH(x: Int, z: Int) {
		val X = (x / 16 - if (x < 0 && x % 16 != 0) 1 else 0) * -16
		val Z = (z / 16 - if (z < 0 && z % 16 != 0) 1 else 0) * -16
		glPopMatrix()
		Tessellator.instance.startDrawingQuads()
		Tessellator.instance.setTranslation(X.D, 0.0, Z.D)
	}
}