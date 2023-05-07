package alfheim.common.entity.boss.ai.fenrir

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.entity.boss.EntityFenrir
import net.minecraft.entity.ai.EntityAIBase

class EntityAIFenrirDashAtTarget(val fenrir: EntityFenrir): EntityAIBase() {
	
	init {
		mutexBits = 5
	}
	
	override fun shouldExecute(): Boolean {
		if (fenrir.stage > 0) return false
		
		if (!fenrir.onGround || fenrir.attackTarget?.onGround != true || fenrir.dashCooldown > 0) return false
		val d = Vector3.entityDistance(fenrir, fenrir.attackTarget)
		return 6 < d && d < 16
	}
	
	override fun continueExecuting() = false
	
	override fun startExecuting() {
		val target = Vector3.fromEntity(fenrir.attackTarget ?: return)
		val d = Vector3.vecEntityDistance(target, fenrir)
		val (mx, _, mz) = target.sub(fenrir).normalize().mul(d / 1.5)
		fenrir.setMotion(mx, fenrir.motionY, mz)
		fenrir.dashCooldown = COOLDOWN
		
		if (!fenrir.worldObj.isRemote)
			VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.FENRIR_DASH, fenrir.dimension, fenrir.entityId.D, fenrir.motionX, fenrir.motionZ)
	}
	
	companion object {
		const val COOLDOWN = 200
	}
}
