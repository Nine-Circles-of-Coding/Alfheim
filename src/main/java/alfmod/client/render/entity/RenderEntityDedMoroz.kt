package alfmod.client.render.entity

import alfheim.client.core.util.glScaled
import alfmod.AlfheimModularCore
import alfmod.client.render.model.ModelBipedEyes
import alfmod.common.entity.boss.EntityDedMoroz
import net.minecraft.client.renderer.entity.RenderBiped
import net.minecraft.entity.*
import net.minecraft.util.ResourceLocation
import vazkii.botania.client.core.handler.BossBarHandler

object RenderEntityDedMoroz: RenderBiped(ModelBipedEyes(), 0.5f) {
	
	val texture = ResourceLocation(AlfheimModularCore.MODID, "textures/model/entity/DedMoroz.png")
	
	override fun getEntityTexture(entity: Entity?) = texture
	override fun getEntityTexture(entity: EntityLiving?) = texture
	override fun preRenderCallback(entity: EntityLivingBase?, ticks: Float) {
		if (entity is EntityDedMoroz) BossBarHandler.setCurrentBoss(entity)
		glScaled(2.5)
	}
}
