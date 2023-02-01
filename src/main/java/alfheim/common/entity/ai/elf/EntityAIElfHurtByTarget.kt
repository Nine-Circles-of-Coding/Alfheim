package alfheim.common.entity.ai.elf

import alexsocol.asjlib.*
import alfheim.common.entity.EntityElf
import net.minecraft.entity.ai.EntityAITarget

/**
 * Sets current [elf] attack target to entity, who attacked him
 * and calls for all [militant][alfheim.common.entity.EntityElf.EnumElfJob.isMilitant] elves around
 */
class EntityAIElfHurtByTarget(val elf: EntityElf): EntityAITarget(elf, false) {
	
	private var prevRevengeTimer = 0
	
	init {
		mutexBits = 1
	}
	
	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	override fun shouldExecute(): Boolean {
		val i = elf.func_142015_aE()
		return i != prevRevengeTimer && isSuitableTarget(elf.aiTarget, false)
	}
	
	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	override fun startExecuting() {
		elf.attackTarget = elf.aiTarget
		prevRevengeTimer = elf.func_142015_aE()
		
		val list = getEntitiesWithinAABB(elf.worldObj, elf.javaClass, elf.boundingBox(this.targetDistance))
		
		list.remove(elf)
		if (elf.job == EntityElf.EnumElfJob.WILD)
			list.removeAll { it.job != EntityElf.EnumElfJob.WILD }
		else
			list.removeAll { !it.job.isMilitant }
		
		for (otherElf in list)
			if (otherElf.attackTarget == null)
				otherElf.attackTarget = elf.aiTarget
		
		super.startExecuting()
	}
}