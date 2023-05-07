package alfheim.client.render.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper.discard
import alexsocol.asjlib.render.ASJRenderHelper.interpolatedTranslation
import alexsocol.asjlib.render.ASJRenderHelper.interpolatedTranslationReverse
import alexsocol.asjlib.render.ASJRenderHelper.setBlend
import alexsocol.asjlib.render.ASJShaderHelper
import alfheim.api.lib.*
import alfheim.client.model.entity.ModelEntityThrym
import alfheim.client.model.item.ModelThrymAxe
import alfheim.client.render.world.SpellVisualizations
import alfheim.common.entity.boss.primal.EntityThrym
import alfheim.common.entity.boss.primal.ai.thrym.ThrymAIThirdStageStart
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.client.renderer.entity.RenderBiped
import net.minecraft.entity.*
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.RenderWorldLastEvent
import org.lwjgl.opengl.GL11.*
import vazkii.botania.client.core.handler.BossBarHandler
import kotlin.math.*

object RenderEntityThrym: RenderBiped(ModelEntityThrym, 2f) {
	
	init {
		eventForge()
	}
	
	override fun doRender(living: EntityLiving?, x: Double, y: Double, z: Double, f1: Float, f2: Float) {
		super.doRender(living, x, y, z, f1, f2)
		renderFlyingAxe(living as EntityThrym)
		RenderEntitySurtr.renderShield(living)
	}
	
	fun renderFlyingAxe(thrym: EntityThrym) {
		if (thrym.ultAnimationTicks < 512) return
		
		val ticks = thrym.ultAnimationTicks - 512 + mc.timer.renderPartialTicks
//		val corpseAngle = ASJRenderHelper.interpolate(thrym.prevRenderYawOffset.D, thrym.renderYawOffset.D).F
		
		val stage = thrym.stage
		val speed = if (stage > 1) 20 else 10
		val x = -cos(Math.toRadians(ticks.D * speed)) * 5
		val y = 1.5
		val z = sin(Math.toRadians(ticks.D * speed)) * 5
		
		glPushMatrix()
//		glRotatef(-corpseAngle, 0f, 1f, 0f)
		interpolatedTranslationReverse(mc.thePlayer)
		interpolatedTranslation(thrym)
		glTranslated(x, y, z)
		glRotatef(90f, 0f, 0f, 1f)
		glRotatef(ticks * 50, 1f, 0f, 0f)
		glScalef(3f)
		
		mc.renderEngine.bindTexture(LibResourceLocations.thrymAxe)
		ModelThrymAxe.render(0.0625f)
		
		glPopMatrix()
	}
	
	override fun getEntityTexture(entity: Entity?): ResourceLocation {
		if (entity is EntityThrym && Vector3.fromEntity(entity) != Vector3.zero) BossBarHandler.setCurrentBoss(entity)
		return LibResourceLocations.thrym
	}
	
	// should render armor
	override fun shouldRenderPass(entity: EntityLiving?, slot: Int, ticks: Float) = -1
	
	override fun renderEquippedItems(entity: EntityLiving?, f: Float) {
		glPushMatrix()
		glScalef(3f)
		glTranslatef(0.75f, 2.35f, -0.1f)
		super.renderEquippedItems(entity, f)
		glPopMatrix()
	}
	
	val domes = HashMap<Vector3, Long>()
	
	@SubscribeEvent
	fun onWorldLastRender(e: RenderWorldLastEvent) {
		val iterator = domes.keys.iterator()
		glColor4f(0f, 0.5f, 0.75f, 0.75f)
		iterator.onEach { pos ->
			if (domes[pos]!! < mc.theWorld.totalWorldTime)
				iterator.remove()
			
			val (x, y, z) = pos
			interpolatedTranslationReverse(mc.thePlayer)
			renderShadedSphere(x, y, z, ThrymAIThirdStageStart.DOME_RADIUS.D, LibShaderIDs.idWorley)
			interpolatedTranslation(mc.thePlayer)
		}
		glColor4f(1f, 1f, 1f, 1f)
	}
	
	fun renderShadedSphere(x: Double, y: Double, z: Double, radius: Double, shaderID: Int) {
		glPushMatrix()
		glTranslated(x, y, z)
		
		setBlend()
		glDisable(GL_TEXTURE_2D)
		
		ASJShaderHelper.useShader(shaderID)
		glCullFace(GL_FRONT)
		SpellVisualizations.renderSphere(radius)
		glCullFace(GL_BACK)
		SpellVisualizations.renderSphere(radius)
		ASJShaderHelper.releaseShader()
		
		glEnable(GL_TEXTURE_2D)
		discard()
		
		glPopMatrix()
	}
}
