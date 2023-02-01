package alfheim.client.render.entity

import alexsocol.asjlib.glScaled
import alexsocol.asjlib.math.Vector3
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.entity.ModelBipedEyes
import alfheim.common.entity.boss.EntityDedMoroz
import net.minecraft.client.renderer.entity.RenderBiped
import net.minecraft.entity.*
import vazkii.botania.client.core.handler.BossBarHandler

object RenderEntityDedMoroz: RenderBiped(ModelBipedEyes(LibResourceLocations.dedMorozEyes), 0.5f) {
	
	override fun getEntityTexture(entity: Entity?) = LibResourceLocations.dedMoroz
	override fun getEntityTexture(entity: EntityLiving?) = LibResourceLocations.dedMoroz
	
	override fun preRenderCallback(entity: EntityLivingBase?, ticks: Float) {
		if (entity is EntityDedMoroz && Vector3.fromEntity(entity) != Vector3.zero) BossBarHandler.setCurrentBoss(entity)
		glScaled(2.5)
	}
}
