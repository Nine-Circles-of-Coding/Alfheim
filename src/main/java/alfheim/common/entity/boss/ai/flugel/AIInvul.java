package alfheim.common.entity.boss.ai.flugel;

import alfheim.common.entity.boss.EntityFlugel;

public class AIInvul extends AIBase {

	public AIInvul(EntityFlugel flugel, AITask task) {
		super(flugel, task);
	}
	
	@Override
	public boolean isInterruptible() {
		return false;
	}
	
	@Override
	public void startExecuting() {
		flugel.setAITaskTimer(EntityFlugel.SPAWN_TICKS);
	}

	@Override
	public boolean continueExecuting() {
		flugel.setHealth(flugel.getHealth() + (flugel.getMaxHealth() - 1F) / EntityFlugel.SPAWN_TICKS);
		flugel.motionX = flugel.motionY = flugel.motionZ = 0;
		return canContinue();
	}
	
	@Override
	public void resetTask() {
		flugel.dropState();
	}
}