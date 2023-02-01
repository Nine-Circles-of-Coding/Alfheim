package alfheim.client.render.entity

import alexsocol.asjlib.render.ResourceLocationAnimated
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.entity.ModelRollingMelon
import alfheim.common.entity.EntityRollingMelon
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.entity.*
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.glTranslatef

object RenderEntityRollingMelon: RenderLiving(ModelRollingMelon(), 0.5f) {
	
	override fun getEntityTexture(entity: Entity?) = if ((entity as? EntityRollingMelon)?.isLava == true) LibResourceLocations.rollingMelonLava else LibResourceLocations.rollingMelon
	
	override fun bindTexture(loc: ResourceLocation?) {
		if (loc is ResourceLocationAnimated) loc.bind() else super.bindTexture(loc)
	}
	
	override fun preRenderCallback(entity: EntityLivingBase?, ticks: Float) {
		glTranslatef(0f, 1f, 0f)
	}
}
