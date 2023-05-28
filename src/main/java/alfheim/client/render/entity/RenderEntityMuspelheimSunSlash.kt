package alfheim.client.render.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper.discard
import alexsocol.asjlib.render.ASJRenderHelper.interpolatedTranslation
import alexsocol.asjlib.render.ASJRenderHelper.interpolatedTranslationReverse
import alexsocol.asjlib.render.ASJRenderHelper.setBlend
import alexsocol.asjlib.render.ASJRenderHelper.setGlow
import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.common.entity.EntityMuspelheimSunSlash
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.client.model.AdvancedModelLoader
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12

object RenderEntityMuspelheimSunSlash: Render() {
	
	private var model = AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/slash.obj"))
	
	private val renderQueue = HashMap<Int, SlashRenderQueueEntry>()
	
	init {
		eventForge()
	}
	
	fun queueRender(slash: EntityMuspelheimSunSlash) {
		val (x, y, z) = Vector3.fromEntity(slash)
		renderQueue[slash.entityId] = SlashRenderQueueEntry(x, y, z, slash.rotation, slash.ticksExisted)
	}
	
	@SubscribeEvent
	fun renderQueue(e: RenderWorldLastEvent) {
		interpolatedTranslationReverse(mc.thePlayer)
		renderQueue.keys.removeAll { mc.theWorld.getEntityByID(it) == null }
		renderQueue.values.forEach(::renderEntry)
		interpolatedTranslation(mc.thePlayer)
	}
	
	private fun renderEntry(entry: SlashRenderQueueEntry) {
		val (x, y, z, r, t) = entry
		val s = t * t / 100.0 + 1
		
		glPushMatrix()
		setBlend()
		setGlow()
		glEnable(GL12.GL_RESCALE_NORMAL)
		glAlphaFunc(GL_GREATER, 1 / 255f)
		glTranslated(x, y, z)
		glRotatef(r, 0f, 1f, 0f)
		glRotatef(45f, 1f, 0f, 0f)
		glScaled(0.005 * s)
		mc.renderEngine.bindTexture(getEntityTexture(null))
		glCullFace(GL_FRONT)
		model.renderAll()
		glCullFace(GL_BACK)
		model.renderAll()
		discard()
		glAlphaFunc(GL_GREATER, 0.1f)
		glPopMatrix()
	}
	
	override fun getEntityTexture(entity: Entity?) = LibResourceLocations.slash
	override fun doRender(entity: Entity?, x: Double, y: Double, z: Double, yaw: Float, ticks: Float) = Unit
	
	private data class SlashRenderQueueEntry(val x: Double, val y: Double, val z: Double, val r: Float, val t: Int)
}
