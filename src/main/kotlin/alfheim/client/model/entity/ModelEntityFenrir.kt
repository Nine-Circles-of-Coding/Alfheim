package alfheim.client.model.entity

import alexsocol.asjlib.*
import alfheim.common.entity.boss.EntityFenrir
import net.minecraft.client.model.*
import net.minecraft.entity.*
import net.minecraft.util.MathHelper
import kotlin.math.sin

object ModelEntityFenrir: ModelBase() {
	
	val head: ModelRenderer
	val jaw: ModelRenderer
	val body: ModelRenderer
	val body_rotation: ModelRenderer
	val mane: ModelRenderer
	val cube_r2: ModelRenderer
	val mane_rotation: ModelRenderer
	val leg1: ModelRenderer
	val cube_r3: ModelRenderer
	val cube_r4: ModelRenderer
	val cube_r5: ModelRenderer
	val leg1_r1: ModelRenderer
	val leg2: ModelRenderer
	val cube_r6: ModelRenderer
	val cube_r7: ModelRenderer
	val cube_r8: ModelRenderer
	val leg1_r2: ModelRenderer
	val leg3: ModelRenderer
	val cube_r9: ModelRenderer
	val cube_r10: ModelRenderer
	val cube_r11: ModelRenderer
	val cube_r12: ModelRenderer
	val leg3_r1: ModelRenderer
	val leg4: ModelRenderer
	val cube_r13: ModelRenderer
	val cube_r14: ModelRenderer
	val cube_r15: ModelRenderer
	val cube_r16: ModelRenderer
	val leg3_r2: ModelRenderer
	val tail: ModelRenderer
	
	init {
		textureWidth = 64
		textureHeight = 64
		
		head = ModelRenderer(this)
		head.setRotationPoint(0.0f, 14.5f, -8.0f)
		head.cubeList.add(ModelBox(head, 24, 13, -3.0f, -3.0f, -3.0f, 6, 6, 4, 0.0f, false))
		head.cubeList.add(ModelBox(head, 0, 15, 2.0f, -6.0f, -1.0f, 1, 1, 1, 0.0f, false))
		head.cubeList.add(ModelBox(head, 0, 13, -3.0f, -6.0f, -1.0f, 1, 1, 1, 0.0f, false))
		head.cubeList.add(ModelBox(head, 0, 3, -3.0f, -5.0f, -1.0f, 2, 2, 1, 0.0f, false))
		head.cubeList.add(ModelBox(head, 0, 0, 1.0f, -5.0f, -1.0f, 2, 2, 1, 0.0f, false))
		head.cubeList.add(ModelBox(head, 24, 23, -1.5f, -0.02f, -6.0f, 3, 2, 4, 0.0f, false))
		
		jaw = ModelRenderer(this)
		jaw.setRotationPoint(0.0f, 2.5f, -2.75f)
		head.addChild(jaw)
		setRotationAngle(jaw, 0.1745f, 0.0f, 0.0f)
		jaw.cubeList.add(ModelBox(jaw, 0, 52, -1.5f, -0.5f, -3.25f, 3, 1, 3, 0.0f, false))
		
		body = ModelRenderer(this)
		body.setRotationPoint(0.0f, 15.0f, 2.0f)
		body_rotation = ModelRenderer(this)
		body_rotation.setRotationPoint(0.0f, -1.0f, 0.0f)
		body.addChild(body_rotation)
		setRotationAngle(body_rotation, 1.5708f, 0.0f, 0.0f)
		body_rotation.cubeList.add(ModelBox(body_rotation, 0, 13, -3.0f, -2.9564f, -3.749f, 6, 10, 6, 0.0f, false))
		body_rotation.cubeList.add(ModelBox(body_rotation, 51, 10, 0.0f, -3.0f, 2.0f, 0, 10, 3, 0.0f, false))
		
		mane = ModelRenderer(this)
		mane.setRotationPoint(1.0f, 15.0f, 2.0f)
		mane.cubeList.add(ModelBox(mane, 20, 40, 2.25f, 2.75f, -9.0f, 0, 5, 1, 0.0f, false))
		cube_r2 = ModelRenderer(this)
		cube_r2.setRotationPoint(-1.0f, -5.8045f, -2.6974f)
		mane.addChild(cube_r2)
		setRotationAngle(cube_r2, 1.6581f, 0.0f, 0.0f)
		cube_r2.cubeList.add(ModelBox(cube_r2, 0, 45, -3.4f, -5.375f, -1.85f, 6, 5, 1, 0.0f, false))
		mane_rotation = ModelRenderer(this)
		mane_rotation.setRotationPoint(-1.0f, 1.5f, -2.5f)
		mane.addChild(mane_rotation)
		setRotationAngle(mane_rotation, 1.5708f, 0.0f, 0.0f)
		mane_rotation.cubeList.add(ModelBox(mane_rotation, 0, 0, -4.0f, -5.5f, -1.5f, 8, 6, 7, 0.0f, false))
		mane_rotation.cubeList.add(ModelBox(mane_rotation, 23, 0, -3.5f, -6.5f, -1.25f, 7, 1, 6, 0.0f, false))
		leg1 = ModelRenderer(this)
		leg1.setRotationPoint(-4.5f, 15.5f, 6.75f)
		leg1.cubeList.add(ModelBox(leg1, 38, 32, -0.1f, 6.5f, -4.05f, 2, 2, 3, 0.0f, false))
		cube_r3 = ModelRenderer(this)
		cube_r3.setRotationPoint(-0.1f, 5.65f, -1.05f)
		leg1.addChild(cube_r3)
		setRotationAngle(cube_r3, 1.0036f, 0.0f, 0.0f)
		cube_r3.cubeList.add(ModelBox(cube_r3, 30, 7, 0.01f, -1.0f, -2.0f, 2, 2, 4, 0.0f, false))
		cube_r4 = ModelRenderer(this)
		cube_r4.setRotationPoint(-0.1f, 5.9737f, -6.562f)
		leg1.addChild(cube_r4)
		setRotationAngle(cube_r4, -0.5236f, -0.0873f, 0.0f)
		cube_r4.cubeList.add(ModelBox(cube_r4, 15, 45, 1.25f, -6.3f, 5.5f, 0, 5, 2, 0.0f, false))
		cube_r5 = ModelRenderer(this)
		cube_r5.setRotationPoint(0.9f, 4.25f, -0.25f)
		leg1.addChild(cube_r5)
		setRotationAngle(cube_r5, 0.3054f, 0.0f, 0.0f)
		cube_r5.cubeList.add(ModelBox(cube_r5, 38, 42, -1.0f, -2.5f, -1.0f, 2, 3, 2, 0.0f, false))
		leg1_r1 = ModelRenderer(this)
		leg1_r1.setRotationPoint(1.0f, 1.25f, -0.25f)
		leg1.addChild(leg1_r1)
		setRotationAngle(leg1_r1, -0.1745f, 0.0f, 0.0f)
		leg1_r1.cubeList.add(ModelBox(leg1_r1, 0, 38, -1.25f, -2.5f, -1.5f, 2, 4, 3, 0.0f, false))
		leg2 = ModelRenderer(this)
		leg2.setRotationPoint(4.5f, 15.5f, 6.75f)
		leg2.cubeList.add(ModelBox(leg2, 38, 32, -1.9f, 6.5f, -4.05f, 2, 2, 3, 0.0f, true))
		cube_r6 = ModelRenderer(this)
		cube_r6.setRotationPoint(0.1f, 5.65f, -1.05f)
		leg2.addChild(cube_r6)
		setRotationAngle(cube_r6, 1.0036f, 0.0f, 0.0f)
		cube_r6.cubeList.add(ModelBox(cube_r6, 30, 7, -2.01f, -1.0f, -2.0f, 2, 2, 4, 0.0f, true))
		cube_r7 = ModelRenderer(this)
		cube_r7.setRotationPoint(0.1f, 5.9737f, -6.562f)
		leg2.addChild(cube_r7)
		setRotationAngle(cube_r7, -0.5236f, 0.0873f, 0.0f)
		cube_r7.cubeList.add(ModelBox(cube_r7, 15, 45, -1.25f, -6.3f, 5.5f, 0, 5, 2, 0.0f, true))
		cube_r8 = ModelRenderer(this)
		cube_r8.setRotationPoint(-0.9f, 4.25f, -0.25f)
		leg2.addChild(cube_r8)
		setRotationAngle(cube_r8, 0.3054f, 0.0f, 0.0f)
		cube_r8.cubeList.add(ModelBox(cube_r8, 38, 42, -1.0f, -2.5f, -1.0f, 2, 3, 2, 0.0f, true))
		leg1_r2 = ModelRenderer(this)
		leg1_r2.setRotationPoint(-1.0f, 1.25f, -0.25f)
		leg2.addChild(leg1_r2)
		setRotationAngle(leg1_r2, -0.1745f, 0.0f, 0.0f)
		leg1_r2.cubeList.add(ModelBox(leg1_r2, 0, 38, -1.0f, -2.5f, -1.5f, 2, 4, 3, 0.0f, true))
		leg3 = ModelRenderer(this)
		leg3.setRotationPoint(-3.6f, 14.3861f, -2.7636f)
		leg3.cubeList.add(ModelBox(leg3, 40, 7, -1.0f, 7.6139f, -2.0864f, 2, 2, 3, 0.0f, false))
		cube_r9 = ModelRenderer(this)
		cube_r9.setRotationPoint(-1.0f, 7.0876f, 2.9517f)
		leg3.addChild(cube_r9)
		setRotationAngle(cube_r9, -0.5236f, -0.0873f, 0.0f)
		cube_r9.cubeList.add(ModelBox(cube_r9, 15, 45, 0.25f, -2.5f, -1.0f, 0, 5, 2, 0.0f, false))
		cube_r10 = ModelRenderer(this)
		cube_r10.setRotationPoint(-1.0f, -0.6842f, 3.1791f)
		leg3.addChild(cube_r10)
		setRotationAngle(cube_r10, 0.3491f, -0.0873f, 0.0f)
		cube_r10.cubeList.add(ModelBox(cube_r10, 22, 44, 0.0f, -3.0f, -1.5f, 0, 6, 3, 0.0f, false))
		cube_r11 = ModelRenderer(this)
		cube_r11.setRotationPoint(0.0f, 4.4357f, -1.7741f)
		leg3.addChild(cube_r11)
		setRotationAngle(cube_r11, 1.0472f, 0.0f, 0.0f)
		cube_r11.cubeList.add(ModelBox(cube_r11, 31, 32, -0.99f, 2.4f, -2.666f, 2, 2, 5, 0.0f, false))
		cube_r12 = ModelRenderer(this)
		cube_r12.setRotationPoint(0.0f, 1.4357f, -0.7741f)
		leg3.addChild(cube_r12)
		setRotationAngle(cube_r12, 0.3491f, 0.0f, 0.0f)
		cube_r12.cubeList.add(ModelBox(cube_r12, 40, 12, -1.0f, 2.3397f, -0.342f, 2, 2, 3, 0.0f, false))
		leg3_r1 = ModelRenderer(this)
		leg3_r1.setRotationPoint(2.5f, 0.4357f, 0.2259f)
		leg3.addChild(leg3_r1)
		setRotationAngle(leg3_r1, 0.3491f, 0.0f, 0.0f)
		leg3_r1.cubeList.add(ModelBox(leg3_r1, 0, 29, -3.5f, -2.0603f, -2.342f, 2, 5, 4, 0.0f, false))
		leg4 = ModelRenderer(this)
		leg4.setRotationPoint(3.35f, 14.3861f, -2.7636f)
		leg4.cubeList.add(ModelBox(leg4, 40, 7, -0.75f, 7.6139f, -2.0864f, 2, 2, 3, 0.0f, true))
		cube_r13 = ModelRenderer(this)
		cube_r13.setRotationPoint(1.25f, -0.6842f, 3.1791f)
		leg4.addChild(cube_r13)
		setRotationAngle(cube_r13, 0.3491f, 0.0873f, 0.0f)
		cube_r13.cubeList.add(ModelBox(cube_r13, 22, 44, 0.0f, -3.0f, -1.5f, 0, 6, 3, 0.0f, true))
		cube_r14 = ModelRenderer(this)
		cube_r14.setRotationPoint(1.25f, 7.0876f, 2.9517f)
		leg4.addChild(cube_r14)
		setRotationAngle(cube_r14, -0.5236f, 0.0873f, 0.0f)
		cube_r14.cubeList.add(ModelBox(cube_r14, 15, 45, -0.25f, -2.5f, -1.0f, 0, 5, 2, 0.0f, true))
		cube_r15 = ModelRenderer(this)
		cube_r15.setRotationPoint(0.25f, 6.2795f, 1.0874f)
		leg4.addChild(cube_r15)
		setRotationAngle(cube_r15, 1.0472f, 0.0f, 0.0f)
		cube_r15.cubeList.add(ModelBox(cube_r15, 31, 32, -1.01f, -1.0f, -2.5f, 2, 2, 5, 0.0f, true))
		cube_r16 = ModelRenderer(this)
		cube_r16.setRotationPoint(0.25f, 1.4357f, -0.7741f)
		leg4.addChild(cube_r16)
		setRotationAngle(cube_r16, 0.3491f, 0.0f, 0.0f)
		cube_r16.cubeList.add(ModelBox(cube_r16, 40, 12, -1.0f, 2.3397f, -0.342f, 2, 2, 3, 0.0f, true))
		leg3_r2 = ModelRenderer(this)
		leg3_r2.setRotationPoint(-2.25f, 0.4357f, 0.2259f)
		leg4.addChild(leg3_r2)
		setRotationAngle(leg3_r2, 0.3491f, 0.0f, 0.0f)
		leg3_r2.cubeList.add(ModelBox(leg3_r2, 0, 29, 1.5f, -2.0603f, -2.342f, 2, 5, 4, 0.0f, true))
		tail = ModelRenderer(this)
		tail.setRotationPoint(-0.0538f, 13.0f, 9.0595f)
		tail.cubeList.add(ModelBox(tail, 22, 37, -0.9462f, 0.0f, -1.0595f, 2, 8, 2, 0.0f, false))
		tail.cubeList.add(ModelBox(tail, 10, 29, -0.4462f, 8.0f, -1.0595f, 1, 2, 2, 0.0f, false))
		tail.cubeList.add(ModelBox(tail, 50, 33, 0.0f, 0.0f, -2.0f, 0, 12, 3, 0.0f, false))
	}
	
	override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
		setRotationAngles(f, f1, f2, f3, f4, f5, entity)
		head.render(f5)
		body.render(f5)
		leg1.render(f5)
		leg2.render(f5)
		leg3.render(f5)
		leg4.render(f5)
		tail.render(f5)
		mane.render(f5)
	}
	
	override fun setLivingAnimations(entity: EntityLivingBase, f1: Float, f2: Float, f3: Float) {
		val (hz, mz, bz, tz) = if (entity is EntityFenrir) {
			arrayOf(entity.getShakeAngle(f3, 0f),
			        entity.getShakeAngle(f3, -0.08f),
			        entity.getShakeAngle(f3, -0.16f),
			        entity.getShakeAngle(f3, -0.2f))
		} else {
			Array(4) { 0f }
		}
		
		tail.rotateAngleY = 0f
		tail.setRotationPoint(0f, 12f, 8f)
		leg1.rotateAngleX = MathHelper.cos(f1 * 0.6662f) * 1.4f * f2
		leg2.rotateAngleX = MathHelper.cos(f1 * 0.6662f + Math.PI.F) * 1.4f * f2
		leg3.rotateAngleX = MathHelper.cos(f1 * 0.6662f + Math.PI.F) * 1.4f * f2
		leg4.rotateAngleX = MathHelper.cos(f1 * 0.6662f) * 1.4f * f2
		head.rotateAngleZ = hz
		mane.rotateAngleZ = mz
		body.rotateAngleZ = bz
		tail.rotateAngleZ = tz
	}
	
	override fun setRotationAngles(f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, entity: Entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity)
		head.rotateAngleX = f4 / (180f / Math.PI.F)
		head.rotateAngleY = f3 / (180f / Math.PI.F)
		tail.rotateAngleX = f2
		jaw.rotateAngleX = Math.toRadians(5 + sin((entity.ticksExisted + mc.timer.renderPartialTicks) / 20.0) * 3).F
	}
	
	fun setRotationAngle(modelRenderer: ModelRenderer, x: Float, y: Float, z: Float) {
		modelRenderer.rotateAngleX = x
		modelRenderer.rotateAngleY = y
		modelRenderer.rotateAngleZ = z
	}
	
	fun ModelBox(model: ModelRenderer, u: Int, v: Int, x: Float, y: Float, z: Float, a: Int, b: Int, c: Int, scale: Float, mirror: Boolean): ModelBox {
		model.mirror = mirror
		return  ModelBox(model, u, v, x, y, z, a, b, c, scale)
	}
}