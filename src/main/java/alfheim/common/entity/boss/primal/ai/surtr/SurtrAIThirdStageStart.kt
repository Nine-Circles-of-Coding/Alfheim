package alfheim.common.entity.boss.primal.ai.surtr

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.entity.EntityMuspelheimSun
import alfheim.common.entity.boss.primal.EntitySurtr
import net.minecraft.entity.ai.EntityAIBase

class SurtrAIThirdStageStart(val host: EntitySurtr): EntityAIBase() {
	
	override fun getMutexBits() = 0b1001
	
	override fun shouldExecute() = host.health <= host.maxHealth * 0.1 && host.stage < 3
	
	override fun startExecuting() {
		host.setPosition(host.source)
		EntityMuspelheimSun(host.worldObj).apply {
			setPosition(host, oY = 21.0)
			spawn()
			playSoundAtEntity("${ModInfo.MODID}:surtr.sun.form", 10f, 1f)
		}
		host.stage = 3
	}
	
	override fun continueExecuting() = false
}
