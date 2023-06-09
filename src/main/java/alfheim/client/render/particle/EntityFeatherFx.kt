package alfheim.client.render.particle

import alexsocol.asjlib.*
import alfheim.api.lib.LibResourceLocations
import cpw.mods.fml.relauncher.*
import net.minecraft.client.particle.EntityFX
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.world.World
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.util.*

/**
 * @author riskyken
 */
@SideOnly(Side.CLIENT)
class EntityFeatherFx(world: World, x: Double, y: Double, z: Double, colour: Int, size: Float, lifetime: Float): EntityFX(world, x, y, z) {
	
	var rotationSpeed: Float
	var f0 = 0f
	var f1 = 0f
	var f2 = 0f
	var f3 = 0f
	var f4 = 0f
	var f5 = 0f
	
	init {
		posX = x
		posY = y
		posZ = z
		prevPosX = x
		prevPosY = y
		prevPosZ = z
		particleScale = size
		val c = Color(colour)
		val red = c.red.F / 255
		val green = c.green.F / 255
		val blue = c.blue.F / 255
		particleRed = red
		particleGreen = green
		particleBlue = blue
		particleMaxAge = (50 * lifetime).I
		rotationSpeed = 2.0f + rand.nextFloat()
		rotationPitch = rand.nextFloat() * 20 - 10
		if (rand.nextFloat() >= 0.5f) {
			rotationSpeed = -rotationSpeed
		}
		particleGravity = 0f
		particleTextureIndexX = 0
		particleTextureIndexY = 1
	}
	
	override fun onUpdate() {
		super.onUpdate()
		if (isCollidedVertically) {
			rotationSpeed = 0f
		}
		rotationPitch += rotationSpeed
		if (rotationPitch > 360f) {
			rotationPitch -= 360f
		}
		if (particleMaxAge - particleAge < 50) {
			particleAlpha = 1 + -((particleAge - particleMaxAge + 50).F / 50)
		}
	}
	
	override fun renderParticle(tessellator: Tessellator, f0: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
		this.f0 = f0
		this.f1 = f1
		this.f2 = f2
		this.f3 = f3
		this.f4 = f4
		this.f5 = f5
		renderQueue.add(this)
	}
	
	fun postRender() {
		if (isDead) return
		
		Tessellator.instance.setBrightness(getBrightnessForRender(0f))
		
		val x = (prevPosX + (posX - prevPosX) * f0 - interpPosX).F
		val y = (prevPosY + (posY - prevPosY) * f0 - interpPosY).F
		val z = (prevPosZ + (posZ - prevPosZ) * f0 - interpPosZ).F
		
		glPushMatrix()
		Tessellator.instance.startDrawingQuads()
		drawBillboard(x.D, y.D, z.D, rotationPitch)
		Tessellator.instance.draw()
		glPopMatrix()
	}
	
	fun drawBillboard(x: Double, y: Double, z: Double, rotation: Float) {
		val scale = 0.05f
		glTranslatef(x.F, y.F, z.F)
		glRotatef(-RenderManager.instance.playerViewY, 0.0f, 1.0f, 0.0f)
		glRotatef(RenderManager.instance.playerViewX, 1.0f, 0.0f, 0.0f)
		glRotatef(180f, 0f, 0f, 1f)
		glTranslatef(-0.01f)
		glRotatef(rotation, 0f, 0f, 1f)
		glTranslatef(0.01f)
		glScalef(scale)
		glScalef(particleScale)
		Tessellator.instance.setColorRGBA_F(particleRed, particleGreen, particleBlue, particleAlpha * 0.85f)
		Tessellator.instance.addVertexWithUV(-1.0, -1.0, 0.0, 0.0, 0.0)
		Tessellator.instance.addVertexWithUV(-1.0, 1.0, 0.0, 0.0, 1.0)
		Tessellator.instance.addVertexWithUV(1.0, 1.0, 0.0, 1.0, 1.0)
		Tessellator.instance.addVertexWithUV(1.0, -1.0, 0.0, 1.0, 0.0)
	}
	
	companion object {
		
		val renderQueue: Queue<EntityFeatherFx> = ArrayDeque()
		
		fun renderQueue() {
			glEnable(GL_BLEND)
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
			mc.renderEngine.bindTexture(LibResourceLocations.feather)
			for (featherFx in renderQueue) {
				featherFx.postRender()
			}
			renderQueue.clear()
			glDisable(GL_BLEND)
		}
	}
}