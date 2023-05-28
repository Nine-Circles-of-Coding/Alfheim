package alfheim.client.render.entity

import alexsocol.asjlib.math.Vector3
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.entity.ModelEntityFlugel
import alfheim.common.entity.boss.EntityFlugel
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import vazkii.botania.client.core.handler.BossBarHandler

object RenderEntityFlugel: RenderLiving(ModelEntityFlugel, 0.25f) {
	
	val so = ShadedObjectHaloPlane(LibResourceLocations.halo)
	
	override fun getEntityTexture(entity: Entity?) =
		if (entity is EntityFlugel) getEntityTexture(entity) else LibResourceLocations.jibril
	
	fun getEntityTexture(flugel: EntityFlugel): ResourceLocation {
		if (Vector3.fromEntity(flugel) != Vector3.zero) BossBarHandler.setCurrentBoss(flugel)
		
		return if (flugel.isUltraMode) LibResourceLocations.jibrilDark else LibResourceLocations.jibril
	}
}