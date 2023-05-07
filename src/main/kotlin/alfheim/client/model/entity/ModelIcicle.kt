package alfheim.client.model.entity

import net.minecraft.client.model.*
import net.minecraft.entity.Entity

object ModelIcicle: ModelBase() {
	
	var shape1: ModelRenderer
	var shape2: ModelRenderer
	var shape3: ModelRenderer
	var shape4: ModelRenderer
	
	init {
		textureWidth = 16
		textureHeight = 16
		shape4 = ModelRenderer(this, 6, -1)
		shape4.setRotationPoint(0.0f, 8.0f, 0.0f)
		shape4.addBox(-0.5f, 0.0f, -0.5f, 1, 4, 1, 0.0f)
		shape2 = ModelRenderer(this, 2, 5)
		shape2.setRotationPoint(0.0f, 16.0f, 0.0f)
		shape2.addBox(-1.5f, 0.0f, -1.5f, 3, 4, 3, 0.0f)
		shape1 = ModelRenderer(this, 0, 8)
		shape1.setRotationPoint(0.0f, 20.0f, 0.0f)
		shape1.addBox(-2.0f, 0.0f, -2.0f, 4, 4, 4, 0.0f)
		shape3 = ModelRenderer(this, 4, 2)
		shape3.setRotationPoint(0.0f, 12.0f, 0.0f)
		shape3.addBox(-1.0f, 0.0f, -1.0f, 2, 4, 2, 0.0f)
	}
	
	override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
		render(f5)
	}
	
	fun render(f5: Float) {
		shape4.render(f5)
		shape2.render(f5)
		shape1.render(f5)
		shape3.render(f5)
	}
	
	fun setRotateAngle(modelRenderer: ModelRenderer, x: Float, y: Float, z: Float) {
		modelRenderer.rotateAngleX = x
		modelRenderer.rotateAngleY = y
		modelRenderer.rotateAngleZ = z
	}
}