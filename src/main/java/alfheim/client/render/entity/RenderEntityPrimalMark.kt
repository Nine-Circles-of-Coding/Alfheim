package alfheim.client.render.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.entity.ModelIcicle
import alfheim.client.render.world.VisualEffectHandlerClient.v
import alfheim.common.entity.EntityPrimalMark
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11.*
import java.util.*
import kotlin.math.*

object RenderEntityPrimalMark: Render() {
	
	val sof = ShadedObjectHaloPlane(LibResourceLocations.markFire)
	val soi = ShadedObjectHaloPlane(LibResourceLocations.markIce)
	
	private val rand = Random()
	
	init {
		shadowSize = 0f
	}
	
	override fun getEntityTexture(entity: Entity) = null
	
	override fun doRender(entity: Entity, x: Double, y: Double, z: Double, yaw: Float, partialTick: Float) {
		entity as EntityPrimalMark
		
		glPushMatrix()
		glTranslated(x, y + 0.01, z)
		glScalef(2f)
		if (!entity.isSpecial) (if (entity.isIce) soi else sof).addTranslation()
		glScaled(0.5)
		
		if (entity.isIce)
			renderIcicles(entity)
		else
			renderFlames(entity)
		
		glPopMatrix()
	}
	
	fun renderIcicles(mark: EntityPrimalMark) {
		val ticks = mark.ticksExisted - 50
		if (ticks < 0) return
		
		rand.setSeed(mark.uniqueID.mostSignificantBits)
		mc.renderEngine.bindTexture(LibResourceLocations.nifleice)
		ASJRenderHelper.setBlend()
		
		val count = if (mark.isSpecial) 359 else ASJUtilities.randInBounds(20, 40, rand)
		
		for (i in 0..count) {
			glPushMatrix()
			
			if (mark.isSpecial) {
				v.set(-cos(i.F).F * (rand.nextFloat() * 18 + 0.25f), 0, sin(i.F).F * (rand.nextFloat() * 18 + 0.25f))
			} else {
				val maxDist = mark.width * 0.75
				v.set(rand.nextFloat(), 0.5, rand.nextFloat()).sub(0.5).normalize().mul(rand.nextFloat() * maxDist - maxDist / 2)
			}
			
			val (x, y, z) = v
			glTranslated(x, y, z)
			
			if (mark.isSpecial) {
				val angle = Math.toDegrees(Vector3.oZ.angle(v)).F * if (x < 0) -1 else 1
				
				glRotatef(angle + 90, 0f, 1f, 0f)
				glRotatef(v.length().F * 2.5f, 0f, 0f, 1f)
			} else {
				glRotatef(rand.nextFloat() * 360f, 0f, 1f, 0f)
				glRotatef(rand.nextFloat() * 20f + 10, 0f, 0f, 1f)
			}
			
			glTranslatef(0f, min(ticks / 5f, 1f) + 0.5f, 0f)
			
			glScaled(rand.nextDouble() * 0.5 + 1)
			
			glRotatef(180f, 0f, 0f, 1f)
			ModelIcicle.render(0.0625f)
			
			glPopMatrix()
		}
		
		ASJRenderHelper.discard()
	}
	
	fun renderFlames(e: EntityPrimalMark) {
		if (mc.isGamePaused || e.ticksExisted < 70) return
		fun rand() = Math.random() * (if (e.isSpecial) 18 else 2) - (if (e.isSpecial) 9 else 1)
		e.worldObj.spawnParticle("lava", e.posX + rand(), e.posY + 0.25, e.posZ + rand(), 0.0, 0.0, 0.0)
	}
}