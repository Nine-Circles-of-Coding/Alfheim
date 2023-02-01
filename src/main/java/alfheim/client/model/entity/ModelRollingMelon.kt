package alfheim.client.model.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.entity.EntityRollingMelon
import net.minecraft.client.model.*
import net.minecraft.entity.Entity
import kotlin.math.PI

class ModelRollingMelon: ModelBase() {
	
	val melon = ModelRenderer(this, 64, 32).apply {
		addBox(-8f, -8f, -8f, 16, 16, 16)
		setRotationPoint(0f, 0f, 0f)
	}
	
	override fun render(entity: Entity?, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
		setRotationAngles(f, f1, f2, f3, f4, f5, entity)
		melon.render(f5)
	}
	
	override fun setRotationAngles(f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, entity: Entity?) {
		if (entity !is EntityRollingMelon) return
		
		melon.rotateAngleX = entity.rotation * PI.F + Vector3(entity.motionX, 0, entity.motionZ).mul(mc.timer.renderPartialTicks).length().F
	}
}
