package alfheim.common.entity.boss.ai.fenrir

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.entity.boss.EntityFenrir
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.util.DamageSource

class EntityAIFenrirLeapAtTarget(var fenrir: EntityFenrir): EntityAIBase() {
	
	init {
		mutexBits = 5
	}
	
	override fun shouldExecute(): Boolean {
		if (fenrir.stage > 0) return false
		
		if (!fenrir.onGround || fenrir.attackTarget?.onGround != true || fenrir.leapCooldown > 0) return false
		val d = Vector3.entityDistance(fenrir, fenrir.attackTarget)
		return 16 < d && d < 48
	}
	
	override fun continueExecuting(): Boolean {
		if (fenrir.attackTarget != null)
			ASJUtilities.faceEntity(fenrir, fenrir.attackTarget, 360f, 360f)
		
		return !fenrir.onGround
	}
	
	override fun startExecuting() {
		fenrir.leapTo(Vector3.fromEntity(fenrir.attackTarget ?: return))
		fenrir.leapCooldown = COOLDOWN
		
		if (!fenrir.worldObj.isRemote)
			VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.FENRIR_AREA, fenrir.attackTarget)
	}
	
	override fun resetTask() {
		val list = getEntitiesWithinAABB(fenrir.worldObj, EntityLivingBase::class.java, fenrir.boundingBox().expand(6.5, 0.0, 6.5))
		
		list.removeAll { Vector3.entityDistancePlane(fenrir, it) > 8 }
		list.remove(fenrir)
		list.forEach { it.attackEntityFrom(DamageSource.causeMobDamage(fenrir), 20f) }
		
		if (!fenrir.worldObj.isRemote)
			VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.FENRIR_AREA_END, fenrir)
	}
	
	companion object {
		
		const val COOLDOWN = 200
		
		fun EntityFenrir.leapTo(target: Vector3) {
			val d = Vector3.vecEntityDistance(target, this)
			val (mx, _, mz) = target.sub(this).mul(0.15)
			setMotion(mx, d / 48, mz)
			onGround = false
		}
	}
}