package alfheim.client.render.entity

import alfheim.api.lib.*
import alfheim.common.entity.EntityMuspelheimSun
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity

object RenderEntityMuspelheimSun: Render() {
	
	override fun getEntityTexture(entity: Entity?) = LibResourceLocations.gravity // unused anyway
	
	override fun doRender(entity: Entity?, x: Double, y: Double, z: Double, yaw: Float, ticks: Float) {
		if (entity !is EntityMuspelheimSun) return

		val radius = entity.radius
		RenderEntityThrym.renderShadedSphere(x, y + radius, z, radius, LibShaderIDs.idSun)
	}
}
