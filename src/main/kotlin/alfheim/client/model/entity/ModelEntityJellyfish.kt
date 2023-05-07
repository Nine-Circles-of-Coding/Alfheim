package alfheim.client.model.entity

import alexsocol.asjlib.render.ASJRenderHelper
import net.minecraft.client.model.*
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11.*

object ModelEntityJellyfish: ModelBase() {
	
	val Dome: ModelRenderer
	val cube_r1: ModelRenderer
	val cube_r2: ModelRenderer
	val cube_r3: ModelRenderer
	val Body: ModelRenderer
	val cube_r4: ModelRenderer
	val cube_r5: ModelRenderer
	val cube_r6: ModelRenderer
	val cube_r7: ModelRenderer
	val cube_r8: ModelRenderer
	val cube_r9: ModelRenderer
	val cube_r10: ModelRenderer
	val cube_r11: ModelRenderer
	val cube_r12: ModelRenderer
	val cube_r13: ModelRenderer
	val cube_r14: ModelRenderer
	
	init {
		textureWidth = 128
		textureHeight = 128
		Dome = ModelRenderer(this)
		Dome.setRotationPoint(0.0f, 25.0f, 0.0f)
		Dome.cubeList.add(ModelBox(Dome, 0, 0, -10.5f, -38.0f, -10.5f, 21, 3, 21, 0.0f))
		Dome.cubeList.add(ModelBox(Dome, 0, 24, -10.5f, -35.0f, -12.5f, 21, 14, 2, 0.0f))
		cube_r1 = ModelRenderer(this)
		cube_r1.setRotationPoint(0.0f, 0.0f, 0.0f)
		Dome.addChild(cube_r1)
		setRotationAngle(cube_r1, 0.0f, 3.1416f, 0.0f)
		cube_r1.cubeList.add(ModelBox(cube_r1, 0, 24, -10.5f, -35.0f, -12.5f, 21, 14, 2, 0.0f))
		cube_r2 = ModelRenderer(this)
		cube_r2.setRotationPoint(0.0f, 0.0f, 0.0f)
		Dome.addChild(cube_r2)
		setRotationAngle(cube_r2, 0.0f, 1.5708f, 0.0f)
		cube_r2.cubeList.add(ModelBox(cube_r2, 0, 24, -10.5f, -35.0f, -12.5f, 21, 14, 2, 0.0f))
		cube_r3 = ModelRenderer(this)
		cube_r3.setRotationPoint(0.0f, 0.0f, 0.0f)
		Dome.addChild(cube_r3)
		setRotationAngle(cube_r3, 0.0f, -1.5708f, 0.0f)
		cube_r3.cubeList.add(ModelBox(cube_r3, 0, 24, -10.5f, -35.0f, -12.5f, 21, 14, 2, 0.0f))
		Body = ModelRenderer(this)
		Body.setRotationPoint(0.0f, 24.0f, 0.0f)
		Body.cubeList.add(ModelBox(Body, 61, 33, -4.5f, -30.0f, -4.5f, 9, 12, 9, 0.0f))
		Body.cubeList.add(ModelBox(Body, 0, 40, -7.5f, -34.0f, -7.5f, 15, 4, 15, 0.0f))
		Body.cubeList.add(ModelBox(Body, 124, 0, -5.0f, -18.0f, 4.0f, 1, 8, 1, 0.0f))
		Body.cubeList.add(ModelBox(Body, 4, 61, -8.0f, -14.0f, -0.5f, 1, 14, 1, 0.0f))
		Body.cubeList.add(ModelBox(Body, 32, 64, -1.0f, -15.0f, 6.5f, 2, 9, 2, 0.0f))
		Body.cubeList.add(ModelBox(Body, 40, 86, -5.5f, -10.0f, 3.5f, 2, 9, 2, 0.0f))
		cube_r4 = ModelRenderer(this)
		cube_r4.setRotationPoint(0.0f, 0.0f, 0.0f)
		Body.addChild(cube_r4)
		setRotationAngle(cube_r4, 0.0f, 3.1416f, 0.0f)
		cube_r4.cubeList.add(ModelBox(cube_r4, 40, 97, -5.5f, -10.0f, 3.5f, 2, 9, 2, 0.0f))
		cube_r4.cubeList.add(ModelBox(cube_r4, 32, 75, -1.0f, -15.0f, 6.5f, 2, 9, 2, 0.0f))
		cube_r4.cubeList.add(ModelBox(cube_r4, 4, 76, -8.0f, -14.0f, -0.5f, 1, 14, 1, 0.0f))
		cube_r4.cubeList.add(ModelBox(cube_r4, 124, 9, -5.0f, -18.0f, 4.0f, 1, 8, 1, 0.0f))
		cube_r5 = ModelRenderer(this)
		cube_r5.setRotationPoint(0.0f, 0.0f, 0.0f)
		Body.addChild(cube_r5)
		setRotationAngle(cube_r5, 0.0f, 1.5708f, 0.0f)
		cube_r5.cubeList.add(ModelBox(cube_r5, 32, 97, -5.5f, -10.0f, 3.5f, 2, 9, 2, 0.0f))
		cube_r5.cubeList.add(ModelBox(cube_r5, 40, 64, -1.0f, -15.0f, 6.5f, 2, 9, 2, 0.0f))
		cube_r5.cubeList.add(ModelBox(cube_r5, 0, 76, -8.0f, -14.0f, -0.5f, 1, 14, 1, 0.0f))
		cube_r5.cubeList.add(ModelBox(cube_r5, 120, 0, -5.0f, -18.0f, 4.0f, 1, 8, 1, 0.0f))
		cube_r6 = ModelRenderer(this)
		cube_r6.setRotationPoint(0.0f, 0.0f, 0.0f)
		Body.addChild(cube_r6)
		setRotationAngle(cube_r6, 0.0f, -1.5708f, 0.0f)
		cube_r6.cubeList.add(ModelBox(cube_r6, 32, 86, -5.5f, -10.0f, 3.5f, 2, 9, 2, 0.0f))
		cube_r6.cubeList.add(ModelBox(cube_r6, 40, 75, -1.0f, -15.0f, 6.5f, 2, 9, 2, 0.0f))
		cube_r6.cubeList.add(ModelBox(cube_r6, 0, 61, -8.0f, -14.0f, -0.5f, 1, 14, 1, 0.0f))
		cube_r6.cubeList.add(ModelBox(cube_r6, 120, 9, -5.0f, -18.0f, 4.0f, 1, 8, 1, 0.0f))
		cube_r7 = ModelRenderer(this)
		cube_r7.setRotationPoint(0.0f, 0.0f, 0.0f)
		Body.addChild(cube_r7)
		setRotationAngle(cube_r7, 0.7854f, -1.5708f, 0.0f)
		cube_r7.cubeList.add(ModelBox(cube_r7, 4, 99, -0.5f, -10.2f, 14.95f, 1, 6, 1, 0.0f))
		cube_r8 = ModelRenderer(this)
		cube_r8.setRotationPoint(0.0f, 0.0f, 0.0f)
		Body.addChild(cube_r8)
		setRotationAngle(cube_r8, 0.7854f, 3.1416f, 0.0f)
		cube_r8.cubeList.add(ModelBox(cube_r8, 0, 99, -0.5f, -10.2f, 14.95f, 1, 6, 1, 0.0f))
		cube_r9 = ModelRenderer(this)
		cube_r9.setRotationPoint(0.0f, 0.0f, 0.0f)
		Body.addChild(cube_r9)
		setRotationAngle(cube_r9, 0.7854f, 1.5708f, 0.0f)
		cube_r9.cubeList.add(ModelBox(cube_r9, 4, 92, -0.5f, -10.2f, 14.95f, 1, 6, 1, 0.0f))
		cube_r10 = ModelRenderer(this)
		cube_r10.setRotationPoint(0.0f, 0.0f, 0.0f)
		Body.addChild(cube_r10)
		setRotationAngle(cube_r10, 0.7854f, 0.0f, 0.0f)
		cube_r10.cubeList.add(ModelBox(cube_r10, 0, 92, -0.5f, -10.2f, 14.95f, 1, 6, 1, 0.0f))
		cube_r11 = ModelRenderer(this)
		cube_r11.setRotationPoint(0.0f, 0.0f, 0.0f)
		Body.addChild(cube_r11)
		setRotationAngle(cube_r11, -0.4363f, -1.5708f, 0.0f)
		cube_r11.cubeList.add(ModelBox(cube_r11, 102, 83, -4.5f, -27.0f, -6.25f, 9, 9, 4, 0.0f))
		cube_r12 = ModelRenderer(this)
		cube_r12.setRotationPoint(0.0f, 0.0f, 0.0f)
		Body.addChild(cube_r12)
		setRotationAngle(cube_r12, -0.4363f, 3.1416f, 0.0f)
		cube_r12.cubeList.add(ModelBox(cube_r12, 102, 70, -4.5f, -27.0f, -6.25f, 9, 9, 4, 0.0f))
		cube_r13 = ModelRenderer(this)
		cube_r13.setRotationPoint(0.0f, 0.0f, 0.0f)
		Body.addChild(cube_r13)
		setRotationAngle(cube_r13, -0.4363f, 1.5708f, 0.0f)
		cube_r13.cubeList.add(ModelBox(cube_r13, 102, 57, -4.5f, -27.0f, -6.25f, 9, 9, 4, 0.0f))
		cube_r14 = ModelRenderer(this)
		cube_r14.setRotationPoint(0.0f, 0.0f, 0.0f)
		Body.addChild(cube_r14)
		setRotationAngle(cube_r14, -0.4363f, 0.0f, 0.0f)
		cube_r14.cubeList.add(ModelBox(cube_r14, 102, 44, -4.5f, -27.0f, -6.25f, 9, 9, 4, 0.0f))
	}
	
	fun setRotationAngle(modelRenderer: ModelRenderer, x: Float, y: Float, z: Float) {
		modelRenderer.rotateAngleX = x
		modelRenderer.rotateAngleY = y
		modelRenderer.rotateAngleZ = z
	}
	
	override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
		glPushMatrix()
		ASJRenderHelper.setBlend()
		ASJRenderHelper.setGlow()
		glTranslatef(0f, 0.5f, 0f)
		Body.render(f5)
		val s = 0.75f + f2 / 2
		glScalef(s, 1f, s)
		Dome.render(f5)
		ASJRenderHelper.discard()
		glPopMatrix()
	}
}