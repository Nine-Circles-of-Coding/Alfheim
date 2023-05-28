package alfheim.client.sound

import net.minecraft.client.audio.MovingSound
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation

open class EntityBoundMovingSound<E : Entity>(val host: E, sound: String, val update: (EntityBoundMovingSound<E>.() -> Unit)? = null): MovingSound(ResourceLocation(sound)) {
	
	init {
		repeat = true
		field_147665_h = 1
//		volume = 0.01f
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
			
			update?.invoke(this)
		}
	}
	
	fun setVolume(new: Float) {
		volume = new
	}
}
