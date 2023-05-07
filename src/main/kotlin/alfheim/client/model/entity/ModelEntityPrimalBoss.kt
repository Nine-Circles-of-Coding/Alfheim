package alfheim.client.model.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.*
import alfheim.AlfheimCore
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.entity.EntityPrimalMark
import alfheim.common.entity.boss.primal.*
import net.minecraft.client.model.*
import net.minecraft.entity.Entity
import net.minecraft.util.MathHelper
import kotlin.math.*

@Suppress("DuplicatedCode")
abstract class ModelEntityPrimalBoss: ModelBiped() {
	
	abstract var head: ModelRenderer
	abstract var body: ModelRenderer
	abstract var rightarm: ModelRenderer
	abstract var leftarm: ModelRenderer
	abstract var rightleg: ModelRenderer
	abstract var leftleg: ModelRenderer
	
	fun reassignDefaultParts() {
		bipedHeadwear.cubeList.clear()
		bipedEars.cubeList.clear()
		bipedHead.cubeList.clear()
		bipedBody.cubeList.clear()
		bipedLeftArm.cubeList.clear()
		bipedLeftLeg.cubeList.clear()
		bipedRightArm.cubeList.clear()
		bipedRightLeg.cubeList.clear()
		
		bipedHead = head
		bipedBody = body
		bipedLeftArm = leftarm
		bipedLeftLeg = leftleg
		bipedRightArm = rightarm
		bipedRightLeg = rightleg
	}
	
	override fun render(entity: Entity?, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
		setRotationAngles(f, f1, f2, f3, f4, f5, entity)
		head.render(f5)
		body.render(f5)
		rightarm.render(f5)
		leftarm.render(f5)
		rightleg.render(f5)
		leftleg.render(f5)
	}
	
	override fun setRotationAngles(f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, entity: Entity?) {
		if (entity !is EntityPrimalBoss) return
		
		val swingProgress = onGround
		val rotate = ASJBitwiseHelper.getBit(entity.ultAnimationTicks, 9)
		val ult = if (!rotate) entity.ultAnimationTicks else 0
		
		head.rotateAngleY = f3 / (180f / Math.PI.F)
		head.rotateAngleX = f4 / (180f / Math.PI.F)
		rightarm.rotateAngleX = MathHelper.cos(f * 0.6662f + Math.PI.F) * 2f * f1 * 0.5f
		leftarm.rotateAngleX = MathHelper.cos(f * 0.6662f) * 2f * f1 * 0.5f
		rightarm.rotateAngleZ = 0f
		leftarm.rotateAngleZ = 0f
		rightleg.rotateAngleX = MathHelper.cos(f * 0.6662f) * 1.4f * f1
		leftleg.rotateAngleX = MathHelper.cos(f * 0.6662f + Math.PI.F) * 1.4f * f1
		rightleg.rotateAngleY = 0f
		leftleg.rotateAngleY = 0f
		
		if (isRiding) {
			rightarm.rotateAngleX += -(Math.PI.F / 5f)
			leftarm.rotateAngleX += -(Math.PI.F / 5f)
			rightleg.rotateAngleX = -(Math.PI.F * 2f / 5f)
			leftleg.rotateAngleX = -(Math.PI.F * 2f / 5f)
			rightleg.rotateAngleY = Math.PI.F / 10f
			leftleg.rotateAngleY = -(Math.PI.F / 10f)
		}
		if (!rotate && heldItemLeft != 0) {
			leftarm.rotateAngleX = leftarm.rotateAngleX * 0.5f - Math.PI.F / 10f * heldItemLeft.F
		}
		if (heldItemRight != 0) {
			rightarm.rotateAngleX = rightarm.rotateAngleX * 0.5f - Math.PI.F / 10f * heldItemRight.F
		}
		rightarm.rotateAngleY = 0f
		leftarm.rotateAngleY = 0f
		var f6: Float
		var f7: Float
		
		if (ult in 1..85) when (ult) {
			in 1..10  -> rightarm.rotateAngleX -= Math.toRadians(min(ult + mc.timer.renderPartialTicks, 10f) * 10.0).F
			in 11..69 -> run {
				rightarm.rotateAngleX -= Math.toRadians(100.0).F
				
				if (mc.isGamePaused) return@run
				
				val (r, g, b) = getSuperSmashParticlesColor()
				
				repeat(3) {
					val (x, y, z) = Vector3.fromEntity(entity).add(Vector3(-1, 0, 0).rotateOY(-entity.renderYawOffset))
					val v = Vector3().rand().sub(0.5).normalize().mul(5)
					val (ox, oy, oz) = v
					val (mx, my, mz) = v.mul(0.075).negate()
					
					AlfheimCore.proxy.sparkleFX(mc.theWorld, x + ox, y + oy + 9, z + oz, r, g, b, 2f, 5, mx, my, mz)
				}
			}
			in 70..75 -> run {
				rightarm.rotateAngleX -= Math.toRadians(100.0).F
				rightarm.rotateAngleX += Math.toRadians(min(ult - 70 + mc.timer.renderPartialTicks, 5f) * 30.0).F
				
				if (ult != 75) return@run
				
				val (x, y, z) = Vector3.fromEntity(entity).add(Vector3(-1.5, 0, 1.75).rotateOY(-entity.renderYawOffset))
				for (i in 0..511) {
					VisualEffectHandlerClient.v.set(Math.random() - 0.5, 0.0, Math.random() - 0.5).normalize().mul(Math.random() * 1.5 + 0.5).set(VisualEffectHandlerClient.v.x, Math.random() * 0.25, VisualEffectHandlerClient.v.z)
					mc.theWorld.spawnParticle("blockdust_${ getSuperSmashParticlesBlockIDs().random(entity.rng) }_0", x, y + 0.5, z, VisualEffectHandlerClient.v.x, VisualEffectHandlerClient.v.y, VisualEffectHandlerClient.v.z)
				}
				
				EntityPrimalMark(mc.theWorld, x, y, z, entity).apply {
					isSpecial = true
					ticksExisted = 49
					spawn()
				}
			}
			in 76..79 -> rightarm.rotateAngleX += Math.toRadians(50.0).F
			in 80..85 -> {
				rightarm.rotateAngleX += Math.toRadians(50.0).F
				rightarm.rotateAngleX -= Math.toRadians(min(ult - 80 + mc.timer.renderPartialTicks, 5f) * 10.0).F
			}
		} else if (swingProgress > -9990f) {
			f6 = swingProgress
			body.rotateAngleY = MathHelper.sin(sqrt(f6) * Math.PI.F * 2f) * 0.2f
			rightarm.rotationPointZ = MathHelper.sin(body.rotateAngleY) * 5f + 0.5f
			rightarm.rotationPointX = -MathHelper.cos(body.rotateAngleY) * 5f * 3.6f
			leftarm.rotationPointZ = -MathHelper.sin(body.rotateAngleY) * 5f + 0.5f
			leftarm.rotationPointX = MathHelper.cos(body.rotateAngleY) * 5f * 3.6f
			rightarm.rotateAngleY += body.rotateAngleY
			leftarm.rotateAngleY += body.rotateAngleY
			leftarm.rotateAngleX += body.rotateAngleY
			f6 = 1f - swingProgress
			f6 *= f6
			f6 *= f6
			f6 = 1f - f6
			f7 = MathHelper.sin(f6 * Math.PI.F)
			val f8 = MathHelper.sin(swingProgress  * Math.PI.F) * -(head.rotateAngleX - 0.7f) * 0.75f
			rightarm.rotateAngleX = (rightarm.rotateAngleX.toDouble() - (f7.toDouble() * 1.2 + f8.toDouble())).F
			rightarm.rotateAngleY += body.rotateAngleY * 2f
			rightarm.rotateAngleZ = MathHelper.sin(swingProgress  * Math.PI.F) * -0.4f
		}
		
		rightarm.rotateAngleZ += MathHelper.cos(f2 * 0.09f) * 0.05f + 0.05f
		leftarm.rotateAngleZ -= MathHelper.cos(f2 * 0.09f) * 0.05f + 0.05f
		rightarm.rotateAngleX += MathHelper.sin(f2 * 0.067f) * 0.05f
		leftarm.rotateAngleX -= MathHelper.sin(f2 * 0.067f) * 0.05f
		
		if (entity.isShooting) {
			f6 = 0f
			f7 = 0f
			
			leftarm.rotateAngleZ = 0f
			leftarm.rotateAngleY = 0.1f - f6 * 0.6f + head.rotateAngleY + 0.4f
			leftarm.rotateAngleX = -(Math.PI.F / 2f) + head.rotateAngleX
			leftarm.rotateAngleX -= f6 * 1.2f - f7 * 0.4f
			leftarm.rotateAngleZ -= MathHelper.cos(f2 * 0.09f) * 0.05f + 0.05f
			leftarm.rotateAngleX -= MathHelper.sin(f2 * 0.067f) * 0.05f
			
			// TODO
//			val (x, y, z) = Vector3.fromEntity(entity)
//			val (i, _, k) = VisualEffectHandlerClient.v.set(0, 0, 2).rotate(leftarm.rotateAngleY, Vector3.oY).rotate(leftarm.rotateAngleX, Vector3.oX)
//
//			for (a in 0..7)
//				Botania.proxy.wispFX(mc.theWorld, x + i + Math.random() - 0.5, y + 5 + Math.random() - 0.5, z + k + Math.random() - 0.5, 0.0125f, 0.0125f, 0.025f, 1f)
		}
	}
	
	abstract fun getSuperSmashParticlesBlockIDs(): List<Int>
	
	abstract fun getSuperSmashParticlesColor(): FloatArray
	
	fun setRotateAngle(modelRenderer: ModelRenderer, x: Float, y: Float, z: Float) {
		modelRenderer.rotateAngleX = x
		modelRenderer.rotateAngleY = y
		modelRenderer.rotateAngleZ = z
	}
}