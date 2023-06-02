package alfheim.common.entity.boss.ai.flugel

import alexsocol.asjlib.spawn
import alfheim.common.entity.EntityCharge
import alfheim.common.entity.boss.EntityFlugel

class AIEnergy(flugel: EntityFlugel, task: AITask): AIBase(flugel, task) {
	
	internal var left = 0
	internal var max = 0
	
	override fun startExecuting() {
		max = if (flugel.isUltraMode) 20 else if (flugel.isHardMode) 10 else 5
		left = max
		flugel.aiTaskTimer = max * 20
	}
	
	override fun continueExecuting(): Boolean {
		if (flugel.aiTaskTimer % 20 == 0) {
			--left
			val list = flugel.playersAround
			if (list.isEmpty()) return false
			val target = list[flugel.worldObj.rand.nextInt(list.size)]
			
			EntityCharge(flugel.worldObj, flugel, target, this).spawn()
		}
		return canContinue()
	}
}