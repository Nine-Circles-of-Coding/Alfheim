package alfheim.client.model.armor

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import net.minecraft.client.model.*
import net.minecraft.entity.*
import net.minecraft.entity.monster.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumAction
import net.minecraft.util.MathHelper
import org.lwjgl.opengl.GL11

/**
 * Created by wiiv.
 */
class ModelFenrirArmor(val slot: Int, gedeons: Boolean = false): ModelBiped() {
	
	private val helmAnchor: ModelRenderer
	private val helm: ModelRenderer
	private val helmFur: ModelRenderer
	private val helmSnout: ModelRenderer
	private val helmEarL: ModelRenderer
	private val helmEarR: ModelRenderer
	
	private val bodyAnchor: ModelRenderer
	private val bodyTop: ModelRenderer
	private val bodyBottom: ModelRenderer
	
	private val armLAnchor: ModelRenderer
	private val armL: ModelRenderer
	private val armLpauldron: ModelRenderer
	private val armLpaw: ModelRenderer
	
	private val armRAnchor: ModelRenderer
	private val armR: ModelRenderer
	private val armRpauldron: ModelRenderer
	private val armRpaw: ModelRenderer
	
	private val pantsAnchor: ModelRenderer
	private val belt: ModelRenderer
	private val legL: ModelRenderer
	private val legR: ModelRenderer
	
	private val bootL: ModelRenderer
	private val bootR: ModelRenderer
	
	init {
		textureWidth = 64
		textureHeight = 128
		val s = 0.01f
		
		//helm
		helmAnchor = ModelRenderer(this, 0, 0)
		helmAnchor.setRotationPoint(0.0f, 0.0f, 0.0f)
		helmAnchor.addBox(-1.0f, -2.0f, 0.0f, 2, 2, 2, s)
		helm = ModelRenderer(this, 0, 0)
		helm.setRotationPoint(0.0f, 0.0f, 0.0f)
		helm.addBox(-4.5f, -9.0f, -4.5f, 9, 9, 9, s)
		helmFur = ModelRenderer(this, 0, 18)
		helmFur.setRotationPoint(0.0f, -2.0f, -4.5f)
		helmFur.addBox(-5.5f, 0.0f, -1.0f, 11, 5, 11, s)
		setRotateAngle(helmFur, 0.2617994f, 0.0f, 0.0f)
		helmSnout = ModelRenderer(this, 36, 0)
		helmSnout.setRotationPoint(0.0f, -3.0f, -4.5f)
		helmSnout.addBox(-2.5f, 0.0f, -5.0f, 5, 4, 6, s)
		setRotateAngle(helmSnout, 0.2617994f, 0.0f, 0.0f)
		helmEarL = ModelRenderer(this, 36, 10)
		helmEarL.mirror = true
		helmEarL.setRotationPoint(3.5f, -9.0f, -0.5f)
		helmEarL.addBox(-3.0f, -3.0f, 0.0f, 4, 5, 2, s)
		setRotateAngle(helmEarL, 0.0f, 0.0f, 0.2617994f)
		helmEarR = ModelRenderer(this, 36, 10)
		helmEarR.setRotationPoint(-3.5f, -9.0f, -0.5f)
		helmEarR.addBox(-1.0f, -3.0f, 0.0f, 4, 5, 2, s)
		setRotateAngle(helmEarR, 0.0f, 0.0f, -0.2617994f)
		
		//body
		bodyAnchor = ModelRenderer(this, 0, 0)
		bodyAnchor.setRotationPoint(0.0f, 0.0f, 0.0f)
		bodyAnchor.addBox(-1.0f, 0.0f, -1.0f, 2, 2, 2, s)
		bodyTop = ModelRenderer(this, 0, 35)
		bodyTop.setRotationPoint(0.0f, 0.0f, 0.0f)
		bodyTop.addBox(-4.5f, -0.5f, -3.0f, 9, 6, 7, s)
		bodyBottom = ModelRenderer(this, 0, 48)
		bodyBottom.setRotationPoint(0.0f, 0.0f, 0.0f)
		bodyBottom.addBox(-3.5f, 4.5f, -2.5f, 7, 5, 5, s)
		
		//armL
		armLAnchor = ModelRenderer(this, 0, 0)
		armLAnchor.mirror = true
		armLAnchor.setRotationPoint(4.0f, 2.0f, 0.0f)
		armLAnchor.addBox(0.0f, -1.0f, -1.0f, 2, 2, 2, s)
		armL = ModelRenderer(this, 0, 58)
		armL.mirror = true
		armL.setRotationPoint(0.0f, 0.0f, 0.0f)
		armL.addBox(-1.5f, 2.5f, -2.5f, 5, 3, 5, s)
		armLpauldron = ModelRenderer(this, 0, 66)
		armLpauldron.mirror = true
		armLpauldron.addBox(-0.5f, -2.5f, -3.5f, 5, 5, 7, s)
		armLpaw = ModelRenderer(this, 24, 66)
		armLpaw.mirror = true
		armLpaw.setRotationPoint(2.5f, 5.5f, 0.0f)
		armLpaw.addBox(-1.0f, 0.0f, -2.5f, 3, 6, 5, s)
		setRotateAngle(armLpaw, 0.0f, 0.0f, 0.2617994f)
		
		//armR
		armRAnchor = ModelRenderer(this, 0, 0)
		armRAnchor.mirror = true
		armRAnchor.setRotationPoint(-4.0f, 2.0f, 0.0f)
		armRAnchor.addBox(-2.0f, -1.0f, -1.0f, 2, 2, 2, s)
		armR = ModelRenderer(this, 0, 58)
		armR.setRotationPoint(0.0f, 0.0f, 0.0f)
		armR.addBox(-3.5f, 2.5f, -2.5f, 5, 3, 5, s)
		armRpauldron = ModelRenderer(this, 0, 66)
		armRpauldron.addBox(-4.5f, -2.5f, -3.5f, 5, 5, 7, s)
		armRpaw = ModelRenderer(this, 24, 66)
		armRpaw.setRotationPoint(-2.5f, 5.5f, 0.0f)
		armRpaw.addBox(-2.0f, 0.0f, -2.5f, 3, 6, 5, s)
		setRotateAngle(armRpaw, 0.0f, 0.0f, -0.2617994f)
		
		//pants
		pantsAnchor = ModelRenderer(this, 0, 0)
		pantsAnchor.setRotationPoint(0.0f, 0.0f, 0.0f)
		pantsAnchor.addBox(-1.0f, 0.0f, -1.0f, 2, 2, 2, s)
		belt = ModelRenderer(this, 0, 78)
		belt.setRotationPoint(0.0f, 0.0f, 0.0f)
		belt.addBox(-4.5f, 8.5f, -3.0f, 9, 5, 6, s)
		legL = ModelRenderer(this, 0, 89)
		legL.mirror = true
		legL.setRotationPoint(1.9f, 12.0f, 0.0f)
		legL.addBox(-2.39f, -0.5f, -2.5f, 5, 7, 5, s)
		legR = ModelRenderer(this, 0, 89)
		legR.setRotationPoint(-1.9f, 12.0f, 0.0f)
		legR.addBox(-2.61f, -0.5f, -2.5f, 5, 7, 5, s)
		
		//boots
		bootL = ModelRenderer(this, 0, 101)
		bootL.mirror = true
		bootL.setRotationPoint(1.9f, 12.0f, 0.0f)
		bootL.addBox(-2.39f, 8.5f, -2.5f, 5, 4, 5, s)
		bootR = ModelRenderer(this, 0, 101)
		bootR.setRotationPoint(-1.9f, 12.0f, 0.0f)
		bootR.addBox(-2.61f, 8.5f, -2.5f, 5, 4, 5, s)
		
		//hierarchy
		helmAnchor.addChild(helm)
		helm.addChild(helmEarL)
		helm.addChild(helmEarR)
		helm.addChild(helmSnout)
		helm.addChild(helmFur)
		
		bodyAnchor.addChild(bodyTop)
		bodyTop.addChild(bodyBottom)
		armLAnchor.addChild(armL)
		armL.addChild(armLpauldron)
		armL.addChild(armLpaw)
		armRAnchor.addChild(armR)
		armR.addChild(armRpauldron)
		armR.addChild(armRpaw)
		
		pantsAnchor.addChild(belt)
		
		if (gedeons) {
			belt.addChild(legL)
			belt.addChild(legR)
		}
	}
	
	override fun render(entity: Entity, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, netHeadYaw: Float, headPitch: Float, scale: Float) {
		helmAnchor.showModel = slot == 0
		bodyAnchor.showModel = slot == 1
		armRAnchor.showModel = slot == 1
		armLAnchor.showModel = slot == 1
		legR.showModel = slot == 2
		legL.showModel = slot == 2
		bootL.showModel = slot == 3
		bootR.showModel = slot == 3
		bipedHeadwear.showModel = false
		
		bipedHead = helmAnchor
		bipedBody = bodyAnchor
		bipedRightArm = armRAnchor
		bipedLeftArm = armLAnchor
		if (slot == 2) {
			bipedBody = pantsAnchor
			bipedRightLeg = legR
			bipedLeftLeg = legL
		} else {
			bipedRightLeg = bootR
			bipedLeftLeg = bootL
		}
		
		prepareForRender(entity)
		setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity)
		
		if (entity is EntityZombie || entity is EntitySkeleton || entity is EntityGiantZombie) {
			val f6 = MathHelper.sin(onGround * Math.PI.F)
			val f7 = MathHelper.sin((1f - (1f - onGround) * (1f - onGround)) * Math.PI.F)
			
			bipedRightArm.rotateAngleZ = 0f
			bipedRightArm.rotateAngleY = -(0.1f - f6 * 0.6f)
			bipedRightArm.rotateAngleX = -(Math.PI.F / 2f)
			bipedRightArm.rotateAngleX -= f6 * 1.2f - f7 * 0.4f
			bipedRightArm.rotateAngleZ += MathHelper.cos(limbSwing * 0.09f) * 0.05f + 0.05f
			bipedRightArm.rotateAngleX += MathHelper.sin(limbSwing * 0.067f) * 0.05f
			
			bipedLeftArm.rotateAngleZ = 0f
			bipedLeftArm.rotateAngleY = 0.1f - f6 * 0.6f
			bipedLeftArm.rotateAngleX = -(Math.PI.F / 2f)
			bipedLeftArm.rotateAngleX -= f6 * 1.2f - f7 * 0.4f
			bipedLeftArm.rotateAngleZ -= MathHelper.cos(limbSwing * 0.09f) * 0.05f + 0.05f
			bipedLeftArm.rotateAngleX -= MathHelper.sin(limbSwing * 0.067f) * 0.05f
		}
		
		if (isChild) {
			val f6 = 2.0f
			GL11.glPushMatrix()
			GL11.glScalef(1.5f / f6, 1.5f / f6, 1.5f / f6)
			GL11.glTranslatef(0.0f, 16.0f * scale, 0.0f)
			bipedHead.render(scale)
			GL11.glPopMatrix()
			GL11.glPushMatrix()
			GL11.glScalef(1.0f / f6, 1.0f / f6, 1.0f / f6)
			GL11.glTranslatef(0.0f, 24.0f * scale, 0.0f)
			bipedRightArm.render(scale)
			bipedLeftArm.render(scale)
			bipedRightLeg.render(scale)
			bipedLeftLeg.render(scale)
			bipedBody.render(scale)
			GL11.glPopMatrix()
		} else {
			bipedHead.render(scale)
			bipedRightArm.render(scale)
			bipedLeftArm.render(scale)
			bipedRightLeg.render(scale)
			bipedLeftLeg.render(scale)
			bipedBody.render(scale)
		}
		
		ASJRenderHelper.discard()
	}
	
	fun prepareForRender(entity: Entity?) {
		val living = entity as EntityLivingBase?
		isSneak = living?.isSneaking ?: false
		if (living != null && living is EntityPlayer) {
			val itemstack = living.inventory.getCurrentItem()
			heldItemRight = if (itemstack != null) 1 else 0
			aimedBow = false
			if (itemstack != null && living.getItemInUseCount() > 0) {
				val enumaction = itemstack.itemUseAction
				if (enumaction == EnumAction.block) {
					heldItemRight = 3
				} else if (enumaction == EnumAction.bow) {
					aimedBow = true
				}
			}
		}
	}
	
	fun setRotateAngle(modelRenderer: ModelRenderer, x: Float, y: Float, z: Float) {
		modelRenderer.rotateAngleX = x
		modelRenderer.rotateAngleY = y
		modelRenderer.rotateAngleZ = z
	}
}
