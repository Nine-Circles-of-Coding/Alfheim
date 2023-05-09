package alfheim.client.sound

import alfheim.common.entity.boss.primal.EntityPrimalBoss
import net.minecraft.client.audio.MovingSound
import net.minecraft.util.*

class PrimalBossMovingSound(val host: EntityPrimalBoss, sound: String, val update: PrimalBossMovingSound.() -> Unit): MovingSound(ResourceLocation(sound)) {
	
	init {
		repeat = true
	}
	
	/**
	 * Updates the JList with a new model.
	 */
	override fun update() {
		if (host.isDead) {
			donePlaying = true
		} else {
			xPosF = host.posX.toFloat()
			yPosF = host.posY.toFloat()
			zPosF = host.posZ.toFloat()
			
			update(this)
		}
	}
	
	fun setVolume(new: Float) {
		volume = new
	}
}
