package alfheim.api.event

import cpw.mods.fml.common.eventhandler.*
import net.minecraft.entity.Entity

@Cancelable
class EntityUpdateEvent(val entity: Entity): Event() {
	
	val isRiding = entity.ridingEntity != null
	
	companion object {
		
		/** Used in ASM  */
		@JvmStatic
		fun instantiate(e: Entity): EntityUpdateEvent {
			return EntityUpdateEvent(e)
		}
		
		/** Used in ASM  */
		@JvmStatic
		fun stub() {}
	}
}
