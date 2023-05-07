package alfheim.client.model.block

import alexsocol.asjlib.F
import net.minecraft.client.model.*

object ModelYggFlower: ModelBase() {
	
	val base: ModelRenderer
	val petal1: ModelRenderer
	val petal2: ModelRenderer
	val petal3: ModelRenderer
	val petal4: ModelRenderer
	val fruitBottom: ModelRenderer
	val fruitTop: ModelRenderer
	val fruitFront: ModelRenderer
	val fruitBack: ModelRenderer
	val fruitLeft: ModelRenderer
	val fruitRight: ModelRenderer
	val fruitCenter: ModelRenderer
	
	init {
		textureWidth = 40
		textureHeight = 24
		
		val r90 = Math.toRadians(90.0).F
		
		base = ModelRenderer(this, -16, -8)
		base.setRotationPoint(-8.0f, 0.0f, -8.0f)
		base.addBox(0.0f, 0.0f, 0.0f, 16, 1, 16, 0.0f)
		
		petal1 = ModelRenderer(this, -16, 0)
		petal1.setRotationPoint(-8.0f, -8.0f, -14.75f)
		petal1.addBox(0.0f, 0.0f, 0.0f, 16, 1, 16, 0.0f)
		setRotateAngle(petal1, Math.toRadians(-30.0).F, 0.0f, 0.0f)
		
		petal2 = ModelRenderer(this, -16, 0)
		petal2.setRotationPoint(-8.0f, -8.0f, 14.75f)
		petal2.addBox(0.0f, 0.0f, 0.0f, 16, 1, 16, 0.0f)
		setRotateAngle(petal2, Math.toRadians(-150.0).F, 0.0f, 0.0f)
		
		petal3 = ModelRenderer(this, -16, 0)
		petal3.setRotationPoint(-14.75f, -8.0f, 8.0f)
		petal3.addBox(0.0f, 0.0f, 0.0f, 16, 1, 16, 0.0f)
		setRotateAngle(petal3, 0.0f, r90, Math.toRadians(30.0).F)
		
		petal4 = ModelRenderer(this, -16, 0)
		petal4.setRotationPoint(14.75f, -8.0f, 8.0f)
		petal4.addBox(0.0f, 0.0f, 0.0f, 16, 1, 16, 0.0f)
		setRotateAngle(petal4, 0.0f, r90, Math.toRadians(150.0).F)
		
		fruitCenter = ModelRenderer(this, 32, 4)
		fruitCenter.setRotationPoint(-1.0f, -3.0f, -1.0f)
		fruitCenter.addBox(0.0f, 0.0f, 0.0f, 2, 2, 2, 0.0f)
		
		fruitTop = ModelRenderer(this, 32, 0)
		fruitTop.setRotationPoint(-1.0f, -3.0f, -1.0f)
		fruitTop.addBox(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f)
		setRotateAngle(fruitTop, r90, 0.0f, 0.0f)
		
		fruitBottom = ModelRenderer(this, 32, 0)
		fruitBottom.setRotationPoint(-1.0f, 0.0f, -1.0f)
		fruitBottom.addBox(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f)
		setRotateAngle(fruitBottom, r90, 0.0f, 0.0f)
		
		fruitFront = ModelRenderer(this, 32, 0)
		fruitFront.setRotationPoint(-1.0f, -3.0f, -2.0f)
		fruitFront.addBox(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f)
		
		fruitBack = ModelRenderer(this, 32, 0)
		fruitBack.setRotationPoint(-1.0f, -3.0f, 1.0f)
		fruitBack.addBox(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f)
		
		fruitLeft = ModelRenderer(this, 32, 0)
		fruitLeft.setRotationPoint(1.0f, -3.0f, 1.0f)
		fruitLeft.addBox(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f)
		setRotateAngle(fruitLeft, 0.0f, r90, 0.0f)
		
		fruitRight = ModelRenderer(this, 32, 0)
		fruitRight.setRotationPoint(-2.0f, -3.0f, 1.0f)
		fruitRight.addBox(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f)
		setRotateAngle(fruitRight, 0.0f, r90, 0.0f)
	}
	
	private const val f5 = 0.0625f
	
	fun render() {
		base.render(f5)
		petal1.render(f5)
		petal2.render(f5)
		petal3.render(f5)
		petal4.render(f5)
	}
	
	fun renderFruit() {
		fruitCenter.render(f5)
		fruitBottom.render(f5)
		fruitFront.render(f5)
		fruitBack.render(f5)
		fruitLeft.render(f5)
		fruitRight.render(f5)
		fruitTop.render(f5)
	}
	
	fun setRotateAngle(modelRenderer: ModelRenderer, x: Float, y: Float, z: Float) {
		modelRenderer.rotateAngleX = x
		modelRenderer.rotateAngleY = y
		modelRenderer.rotateAngleZ = z
	}
}