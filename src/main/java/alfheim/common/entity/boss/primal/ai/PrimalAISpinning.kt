package alfheim.common.entity.boss.primal.ai

import alfheim.common.entity.boss.primal.EntityPrimalBoss
import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.item.ItemStack

class PrimalAISpinning(val host: EntityPrimalBoss): EntityAIBase() {
	
	override fun getMutexBits() = 0b1101
	
	override fun shouldExecute() = host.rng.nextBoolean() && host.canUlt()
	
	override fun startExecuting() {
		host.ultAnimationTicks = 512
		host.navigator.clearPathEntity()
		host.setCurrentItemOrArmor(0, null)
	}
	
	override fun continueExecuting() = (host.ultAnimationTicks - 512) in 0..if (host.stage > 1) 720 else 360
	
	override fun updateTask() {
		host.swingItem()
		
		val ticks = host.ultAnimationTicks++ -512
		if (ticks < 10) return
		
		host.doSpinningAttack(ticks)
	}
	
	override fun resetTask() {
		host.ultAnimationTicks = 0
		
		if (host.stage == 1)
			--host.ultsCounter
		else
			host.ultCD = 1200
		
		host.setCurrentItemOrArmor(0, ItemStack(host.getEquipment()[0]))
	}
}
