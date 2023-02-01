package alfheim.client.render.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.client.model.entity.*
import alfheim.common.entity.EntityLolicorn
import alfheim.common.entity.EntityLolicorn.Companion.EnumMountType
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.entity.*
import org.lwjgl.opengl.GL11.*
import kotlin.math.sin

object RenderEntityLolicorn: RenderLiving(ModelEntityLolicorn, 0.5f) {
	
	init {
		EnumMountType
	}
	
	override fun renderModel(entity: EntityLivingBase, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
		entity as EntityLolicorn
		
		mainModel = entity.type.model()
		
		val hide = mainModel is ModelEntityLolicorn && (mc.thePlayer === entity.riddenByEntity && mc.gameSettings?.thirdPersonView == 0)
		
		if (hide) {
			ASJRenderHelper.setBlend()
			glColor4f(1f, 1f, 1f, 0.5f)
		}
		
		if (entity.isInvisible) {
			mainModel.setRotationAngles(f, f1, f2, f3, f4, f5, entity)
		} else {
			bindEntityTexture(entity)
			mainModel.render(entity, f, f1, f2, f3, f4, f5)
		}
		
		if (hide) {
			glColor4f(1f, 1f, 1f, 1f)
			ASJRenderHelper.discard()
		}
	}
	
	override fun handleRotationFloat(entity: EntityLivingBase, ticks: Float): Float {
		return if (mainModel is ModelEntityFenrir) Math.toRadians(100 + sin((entity.ticksExisted + ticks).D / 10) * 5).F else super.handleRotationFloat(entity, ticks)
	}
	
	override fun getEntityTexture(entity: Entity?) = (entity as EntityLolicorn).type.texture
	
	override fun preRenderCallback(entity: EntityLivingBase, ticks: Float) {
		entity as EntityLolicorn
		entity.type.preRenderCallback()
	}
}