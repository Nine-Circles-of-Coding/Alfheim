package alfheim.client.render.entity

import alexsocol.asjlib.glScaled
import alexsocol.asjlib.render.ResourceLocationAnimated
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.entity.ModelBipedGlowing
import net.minecraft.client.renderer.entity.RenderBiped
import net.minecraft.entity.*
import net.minecraft.util.ResourceLocation

object RenderEntityMuspelson: RenderBiped(ModelBipedGlowing(), 0.5f) {
	
	override fun getEntityTexture(entity: Entity?) = LibResourceLocations.muspelson
	
	override fun getEntityTexture(entity: EntityLiving?) = getEntityTexture(entity as Entity?)
	
	override fun bindTexture(loc: ResourceLocation?) {
		if (loc is ResourceLocationAnimated) loc.bind() else super.bindTexture(loc)
	}
	
	override fun preRenderCallback(entity: EntityLivingBase?, ticks: Float) {
		glScaled(1.5)
	}
}
