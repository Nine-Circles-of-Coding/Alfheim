package alfheim.client.render.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.entity.*
import alfheim.common.entity.boss.EntityFenrir
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.entity.*
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import vazkii.botania.client.core.handler.BossBarHandler

object RenderEntityFenrir: RenderLiving(ModelEntityFenrir, 2f) {
	
	init {
		setRenderPassModel(ModelEntityFenrir)
	}
	
	override fun doRender(entity: Entity, p_76986_2_: Double, p_76986_4_: Double, p_76986_6_: Double, p_76986_8_: Float, p_76986_9_: Float) {
		entity as EntityLivingBase
		
//		glPushMatrix()
//		ASJRenderHelper.interpolatedTranslationReverse(mc.thePlayer)
//		ASJRenderHelper.interpolatedTranslation(entity)
//
//		val v = Vector3(0, 0, 3.6).rotateOY(-entity.renderYawOffset)
//		OrientedBB(6, 3, 4).translate(v.x, 2.5, v.z).rotateOY(-entity.renderYawOffset.D).draw(0)
//
//		val v1 = Vector3(3, 0, 2.8).rotateOY(-entity.renderYawOffset)
//		OrientedBB(4, 3, 4).translate(v1.x, 2.5, v1.z).rotateOY(-entity.renderYawOffset.D + 45).draw(0)
//
//		val v2 = Vector3(-3, 0, 2.8).rotateOY(-entity.renderYawOffset)
//		OrientedBB(4, 3, 4).translate(v2.x, 2.5, v2.z).rotateOY(-entity.renderYawOffset.D - 45).draw(0)
//
//		glPopMatrix()
		
		super.doRender(entity, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_)
	}
	
	override fun shouldRenderPass(wolf: EntityLivingBase, pass: Int, ticks: Float): Int {
		wolf as EntityFenrir
		return when {
			pass == 0 && wolf.getWolfShaking() -> {
				val f1 = wolf.getBrightness(ticks) * wolf.getShadingWhileShaking(ticks)
				bindTexture(getEntityTexture(wolf))
				glColor3f(f1, f1, f1)
				1
			}
			pass == 1 && wolf.stage > 0        -> {
				ASJRenderHelper.setGlow()
				bindTexture(LibResourceLocations.fenrir2)
				1
			}
			pass == 2 && wolf.stage > 0        -> {
				ASJRenderHelper.discard()
				-1
			}
			else                               -> -1
		}
	}
	
	override fun handleRotationFloat(entity: EntityLivingBase, ticks: Float) = (entity as? EntityFenrir)?.getTailRotation() ?: 0f
	
	override fun getEntityTexture(entity: Entity?): ResourceLocation {
		if (entity is EntityFenrir && Vector3.fromEntity(entity) != Vector3.zero) BossBarHandler.setCurrentBoss(entity)
		return if (entity is EntityFenrir && entity.stage > 0) LibResourceLocations.fenrir1 else LibResourceLocations.fenrir
	}
	
	override fun preRenderCallback(entity: EntityLivingBase?, ticks: Float) {
		entity as EntityFenrir
		
		glTranslated(0.32, 0.0, -0.5)
		glScaled(5.0)
		
		if (entity.spinCooldown in 270..280)
			glRotatef((280 - entity.spinCooldown + ticks) * 36 + entity.spinStartYaw, 0f, -1f, 0f)
	}
}
