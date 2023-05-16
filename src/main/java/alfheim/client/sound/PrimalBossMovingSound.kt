package alfheim.client.sound

import alfheim.common.entity.boss.primal.EntityPrimalBoss
import net.minecraft.client.audio.MovingSound
import net.minecraft.util.*

class PrimalBossMovingSound(val host: EntityPrimalBoss, sound: String, val update: PrimalBossMovingSound.() -> Unit): MovingSound(ResourceLocation(sound)) {
	
	init {
		repeat = true
		// starting at 0 because NEI (or something else) instantiates bosses and the sound is there on first world join
		volume = 0f
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
