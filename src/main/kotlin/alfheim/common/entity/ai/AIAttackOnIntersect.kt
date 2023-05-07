package alfheim.common.entity.ai

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.IIntersectAttackEntity
import net.minecraft.entity.*
import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.entity.player.EntityPlayer

class AIAttackOnIntersect<T>(val host: T): EntityAIBase()
		where T: EntityLiving, T: IIntersectAttackEntity // generic magic! >:D
{
	
	var attackCD = 0
	
	init {
		mutexBits = 0b0100
	}
	
	override fun shouldExecute(): Boolean {
		return attackCD-- <= 0
	}
	
	override fun startExecuting() {
		var target = host.attackTarget
		val bb = host.boundingBox.expand(host.getExtraReach())
		
		if (target == null || !bb.intersectsWith(target.boundingBox)) {
			val list = getEntitiesWithinAABB(host.worldObj, EntityLivingBase::class.java, bb)
			list.remove(host)
			list.removeAll { !it.isEntityAlive || (it as? EntityPlayer)?.capabilities?.disableDamage == true }
			list.sortBy { Vector3.entityDistance(host, it) }
			
			host.attackTarget = list.firstOrNull() ?: return
			target = host.attackTarget ?: return // event-canceled
		}
		
		if (bb.intersectsWith(target.boundingBox)) {
			if (host.heldItem != null && !host.isSwingInProgress) host.swingItem()
			host.attackEntityAsMob(target)
			attackCD = 20
		}
	}
	
	override fun continueExecuting() = false
}