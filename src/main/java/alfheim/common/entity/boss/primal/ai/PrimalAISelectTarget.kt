package alfheim.common.entity.boss.primal.ai

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.entity.boss.primal.EntityPrimalBoss
import net.minecraft.entity.*
import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.entity.player.EntityPlayer

class PrimalAISelectTarget(val host: EntityPrimalBoss, val searchDistance: Number, val chaseDistance: Number): EntityAIBase() {
	
	var targetEntity: EntityLivingBase? = null
	
	var udpatePathCD = 0
	
	init {
		mutexBits = 1
	}
	
	override fun shouldExecute(): Boolean {
		if (host.attackTarget != null) return false
		
		val list = selectEntitiesWithinAABB(host.worldObj, EntityPlayer::class.java, host.boundingBox(searchDistance)) { host.canTarget(it) }
		list.sortBy { Vector3.entityDistance(host, it) }
		targetEntity = list.firstOrNull()
		
		return targetEntity != null
	}
	
	override fun startExecuting() {
		host.attackTarget = targetEntity
	}
	
	override fun continueExecuting(): Boolean {
		val target = host.attackTarget
		
		if (target?.isEntityAlive != true) return false
		
		val distance = Vector3.entityDistance(host, target)
		if (distance > searchDistance.D) return false
		
		if (udpatePathCD-- <= 0 && distance <= chaseDistance.D) {
			udpatePathCD = 10
			host.navigator.tryMoveToEntityLiving(target, host.getEntityAttribute(SharedMonsterAttributes.movementSpeed).attributeValue)
		}
		
		return true
	}
	
	override fun resetTask() {
		host.attackTarget = null
	}
}