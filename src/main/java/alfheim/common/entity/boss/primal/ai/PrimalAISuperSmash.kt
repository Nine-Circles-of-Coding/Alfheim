package alfheim.common.entity.boss.primal.ai

import alexsocol.asjlib.*
import alfheim.common.entity.boss.primal.EntityPrimalBoss
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.ai.EntityAIBase

class PrimalAISuperSmash(val host: EntityPrimalBoss): EntityAIBase() {
	
	override fun getMutexBits() = 0b1101
	
	override fun shouldExecute() = host.rng.nextBoolean() && host.canUlt()
	
	override fun startExecuting() = host.navigator.clearPathEntity()
	
	override fun continueExecuting() = host.ultAnimationTicks in 0..275 && if (host.ultAnimationTicks > 75) host.recentlyHit < 1 else true
	
	override fun updateTask() {
		host.navigator.clearPathEntity()
		val tick = ++host.ultAnimationTicks
		
		if (tick < 75) return
		
		if (tick > 75) {
			host.shield += host.maxShield * 0.25f / 200
			return
		}
		
		host.playSoundAtEntity(host.getStrikeSound(), 1f, 1f)
		val list = getEntitiesWithinAABB(host.worldObj, EntityLivingBase::class.java, host.boundingBox().expand(9.0, 4.5, 9.0).offset(0.0, 4.5, 0.0))
		list.removeAll { !host.canTarget(it) }
		list.forEach(host::doSuperSmashAttack)
		host.recentlyHit = 0
	}
	
	override fun resetTask() {
		host.ultAnimationTicks = 0
		
		if (host.stage == 1)
			--host.ultsCounter
		
		host.ultCD = 1200
	}
}
