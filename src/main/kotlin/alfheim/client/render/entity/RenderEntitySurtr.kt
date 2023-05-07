package alfheim.client.render.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper.discard
import alexsocol.asjlib.render.ASJRenderHelper.glColor1u
import alexsocol.asjlib.render.ASJRenderHelper.interpolate
import alexsocol.asjlib.render.ASJRenderHelper.interpolatedTranslation
import alexsocol.asjlib.render.ASJRenderHelper.interpolatedTranslationReverse
import alexsocol.asjlib.render.ASJRenderHelper.setBlend
import alexsocol.asjlib.render.ASJRenderHelper.setGlow
import alexsocol.asjlib.render.ASJRenderHelper.setTwoside
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.entity.ModelEntitySurtr
import alfheim.client.model.item.ModelSurtrSword
import alfheim.common.entity.boss.primal.*
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderBiped
import net.minecraft.entity.*
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import vazkii.botania.client.core.handler.*
import java.util.Random
import kotlin.math.*

object RenderEntitySurtr: RenderBiped(ModelEntitySurtr, 2f) {
	
	init {
		eventForge()
	}
	
	override fun doRender(living: EntityLiving?, x: Double, y: Double, z: Double, f1: Float, f2: Float) {
		super.doRender(living, x, y, z, f1, f2)
		renderSwordCircle(living as EntitySurtr)
		renderShield(living)
	}
	
	fun renderShield(boss: EntityPrimalBoss) {
		val random = Random(boss.uniqueID.mostSignificantBits)
		val total = boss.shield.div(10).mceil()
		
		glPushMatrix()
		setBlend()
		setGlow()
		setTwoside()
		glColor1u(boss.shieldColor)
		interpolatedTranslationReverse(mc.thePlayer)
		interpolatedTranslation(boss)
		glTranslatef(0f, 4f, 0f)
		glRotatef(-interpolate(boss.prevRenderYawOffset.D, boss.renderYawOffset.D).F + 90f, 0f, 1f, 0f)
		mc.renderEngine.bindTexture(LibResourceLocations.futhark)
		
		val counts = arrayOf(total * 0.4, total * 0.3, total * 0.3).mapTo(ArrayList()) { it.mceil() }
		if (counts[2] < 24) counts[1] += counts.removeAt(2)
		if (counts[1] < 24) counts[0] += counts.removeAt(1)
		
		val tes = Tessellator.instance
		val angle = Vector3()
		val accum = Vector3()
		
		for ((id, count) in counts.withIndex()) {
			val time = ClientTickHandler.total
			if (id == 1) glRotatef(time, 1f, 0f, 0f)
			if (id == 2) glRotatef(-time, 1f, 0f, 0f)
			
			angle.set(0, 0, 5)
			angle.rotateOY(time)
			val perRotate = 360f / count
			tes.startDrawingQuads()
			
			repeat(count) {
				accum.set(angle)
				val (x1, _, z1) = angle
				angle.rotateOY(perRotate)
				val (x2, _, z2) = angle
				val y1 = -Vector3.vecDistance(angle, accum) / 2
				val y2 = -y1
				
				val u1 = random.nextInt(24) / 24.0
				val u2 = u1 + 1/24.0
				
				tes.addVertexWithUV(x1, y2, z1, u2, 0.0)
				tes.addVertexWithUV(x1, y1, z1, u2, 1.0)
				tes.addVertexWithUV(x2, y1, z2, u1, 1.0)
				tes.addVertexWithUV(x2, y2, z2, u1, 0.0)
			}
			
			tes.draw()
			
			if (id == 1) glRotatef(-time, 1f, 0f, 0f)
			if (id == 2) glRotatef(time, 1f, 0f, 0f)
		}
		
		discard()
		glColor1u(0xFFFFFFFFU)
		glPopMatrix()
	}
	
	fun renderSwordCircle(surtr: EntitySurtr) {
		if (surtr.ultAnimationTicks < 512) return

		if (surtr.shouldSpinProtect)
			renderDefence(surtr)
		else
			renderOffence(surtr)
	}
	
	fun renderOffence(surtr: EntitySurtr) {
		glPushMatrix()
		interpolatedTranslationReverse(mc.thePlayer)
		interpolatedTranslation(surtr)
		glRotatef(sin(ClientTickHandler.total / 10) * 5f, 1f, 0f, 0f)
		glTranslatef(0f, 1.5f, 0f)
		glRotatef(ClientTickHandler.total * 3, 0f, 1f, 0f)
		
		repeat(2) {
			if (it == 1) setGlow()
			
			for (i in 0 until 36) {
				glPushMatrix()
				glRotatef(90f, 1f, 0f, 0f)
				glRotatef(10f * i, 0f, 0f, 1f)
				glTranslatef(0f, -5f, 0f)
				glScaled(0.75, 1.5, 0.75)
				
				mc.renderEngine.bindTexture(if (it == 0) LibResourceLocations.surtrSword else LibResourceLocations.surtrSwordGlow)
				ModelSurtrSword.render(0.0625f)
				
				glPopMatrix()
			}
			
			if (it == 1) discard()
		}
		
		glPopMatrix()
	}
	
	fun renderDefence(surtr: EntitySurtr) {
		val player = mc.thePlayer
		
		glPushMatrix()
		interpolatedTranslationReverse(player)
		interpolatedTranslation(surtr)
		
		val pxi = interpolate(player.lastTickPosX, player.posX)
		val pyi = interpolate(player.lastTickPosY, player.posY) - 1.62
		val pzi = interpolate(player.lastTickPosZ, player.posZ)
		
		val sxi = interpolate(surtr.lastTickPosX, surtr.posX)
		val syi = interpolate(surtr.lastTickPosY, surtr.posY)
		val szi = interpolate(surtr.lastTickPosZ, surtr.posZ)
		
		val dif = pyi - syi
		glTranslatef(0f, max(1f, min(dif, 7.0).F), 0f)
		
		if (dif > 7) {
			val vec3d = Vector3(pxi, pyi, pzi).sub(sxi, syi, szi).sub(0, 7, 0)
			val vec2d = vec3d.copy().set(vec3d.x, 0, vec3d.z)
			val angle = Math.toDegrees(vec3d.angle(vec2d))
			vec2d.normalize()
			glRotated(angle, -vec2d.z, 0.0, vec2d.x)
		}
		
		repeat(2) {
			if (it == 1) setGlow()
			
			for (i in 0 until 36) {
				glPushMatrix()
				glRotatef(i * 10f + ClientTickHandler.total * 3, 0f, 1f, 0f)
				glTranslatef(0f, 1.5f, 5f)
				
				mc.renderEngine.bindTexture(if (it == 0) LibResourceLocations.surtrSword else LibResourceLocations.surtrSwordGlow)
				ModelSurtrSword.render(0.0625f)
				
				glPopMatrix()
			}
			
			if (it == 1) discard()
		}
		
		glPopMatrix()
	}
	
	override fun getEntityTexture(entity: Entity?): ResourceLocation {
		if (entity is EntitySurtr && Vector3.fromEntity(entity) != Vector3.zero) BossBarHandler.setCurrentBoss(entity)
		return LibResourceLocations.surtr
	}
	
	// should render armor
	override fun shouldRenderPass(entity: EntityLiving?, pass: Int, ticks: Float): Int {
		if (pass == 1) {
			bindTexture(LibResourceLocations.surtrGlow)
			setRenderPassModel(ModelEntitySurtr)
			setGlow()
			return 1
		} else if (pass == 2) {
			discard()
		}
		
		return -1
	}
	
	override fun inheritRenderPass(entity: EntityLivingBase?, pass: Int, ticks: Float) = -1
	
	override fun renderEquippedItems(entity: EntityLiving?, f: Float) {
		glPushMatrix()
		glScalef(3f)
		glTranslatef(0f, -0.5f, -0.1f)
		super.renderEquippedItems(entity, f)
		glPopMatrix()
	}
}
