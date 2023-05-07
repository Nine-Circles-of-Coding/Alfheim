package alfheim.client.render.entity

import alexsocol.asjlib.render.ResourceLocationAnimated
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.entity.ModelEntityJellyfish
import alfheim.common.entity.EntityJellyfish
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.entity.*
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

object RenderEntityJellyfish: RenderLiving(ModelEntityJellyfish, 0f) {
	
	override fun rotateCorpse(entity: EntityLivingBase, f1: Float, f2: Float, ticks: Float) {
		rotateCorpse(entity as EntityJellyfish, f2, ticks)
	}
	
	fun rotateCorpse(jelly: EntityJellyfish, f: Float, ticks: Float) {
		val f3 = jelly.prevJellyPitch + (jelly.jellyPitch - jelly.prevJellyPitch) * ticks
		val f4 = jelly.prevJellyYaw + (jelly.jellyYaw - jelly.prevJellyYaw) * ticks
		GL11.glTranslatef(0.0f, 0.5f, 0.0f)
		GL11.glRotatef(180.0f - f, 0.0f, 1.0f, 0.0f)
		GL11.glRotatef(f3, 1.0f, 0.0f, 0.0f)
		GL11.glRotatef(f4, 0.0f, 1.0f, 0.0f)
		GL11.glTranslatef(0.0f, -1.2f, 0.0f)
	}
	
	override fun handleRotationFloat(entity: EntityLivingBase, ticks: Float): Float {
		return handleRotationFloat(entity as EntityJellyfish, ticks)
	}
	
	fun handleRotationFloat(jelly: EntityJellyfish, ticks: Float): Float {
		return jelly.lastTentacleAngle + (jelly.tentacleAngle - jelly.lastTentacleAngle) * ticks
	}
	
	override fun getEntityTexture(entity: Entity?) = LibResourceLocations.jellyfish
	
	override fun bindTexture(res: ResourceLocation?) {
		if (res is ResourceLocationAnimated) res.bind() else super.bindTexture(res)
	}
}
