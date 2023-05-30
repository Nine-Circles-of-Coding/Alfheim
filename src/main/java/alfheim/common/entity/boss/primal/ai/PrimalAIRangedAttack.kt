package alfheim.common.entity.boss.primal.ai

import alexsocol.asjlib.*
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.entity.boss.primal.EntityPrimalBoss
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.EntityAIBase

class PrimalAIRangedAttack(val host: EntityPrimalBoss): EntityAIBase() {
	
	var charges = 10
	var timer = 0
	
	override fun getMutexBits() = 0b0101
	
	override fun shouldExecute(): Boolean {
		return host.attackTarget != null && host.shootingCD <= 0
	}
	
	override fun startExecuting() {
		host.isShooting = true
		host.navigator.clearPathEntity()
	}
	
	override fun continueExecuting() = charges > 0 && host.attackTarget?.isEntityAlive == true
	
	override fun updateTask() {
		host.navigator.clearPathEntity()
		if (host.attackTarget != null) host.lookHelper.setLookPositionWithEntity(host.attackTarget, 30f, 30f)
		
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.PRIMAL_BOSS_ATTACK, host.dimension, host.entityId.D)
		
		if (timer == 10) host.playSoundAtEntity(host.getRangedFormSound(), 1f, 1f)
		if (timer-- > 0) return
		timer = 60
		
		host.doRangedAttack(host.playersOnArena())
		
		--charges
	}
	
	override fun resetTask() {
		host.isShooting = false
		charges = 10
		timer = 0
		
		host.shootingCD = COOLDOWN / host.stage
		
		host.navigator.tryMoveToEntityLiving(host.attackTarget ?: return, host.getEntityAttribute(SharedMonsterAttributes.movementSpeed).attributeValue)
	}
	
	override fun isInterruptible() = true
	
	companion object {
		const val COOLDOWN = 600
	}
}