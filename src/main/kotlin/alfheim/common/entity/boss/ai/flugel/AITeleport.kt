package alfheim.common.entity.boss.ai.flugel

import alfheim.common.entity.boss.EntityFlugel

class AITeleport(flugel: EntityFlugel, task: AITask): AIBase(flugel, task) {
	
	override fun startExecuting() {
		flugel.aiTaskTimer = flugel.worldObj.rand.nextInt(100) + 100
	}
	
	override fun continueExecuting(): Boolean {
		if (flugel.aiTaskTimer % (if (flugel.isHardMode) if (flugel.isDying) 60 else 100 else if (flugel.isDying) 80 else 120) == 0)
			flugel.tryToTP()
		
		return canContinue()
	}
}