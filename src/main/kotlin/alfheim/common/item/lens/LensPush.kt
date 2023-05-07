package alfheim.common.item.lens

import alexsocol.asjlib.*
import alexsocol.asjlib.security.InteractionSecurity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.item.ItemStack
import net.minecraft.network.play.server.S12PacketEntityVelocity
import vazkii.botania.api.internal.IManaBurst
import vazkii.botania.common.item.lens.Lens

class LensPush: Lens() {
	
	private val TAG_HOME_ID = "homeID"
	
	override fun updateBurst(burst: IManaBurst, entity: EntityThrowable, stack: ItemStack?) {
		if (entity.worldObj.isRemote) return
		
		val axis = getBoundingBox(entity.posX, entity.posY, entity.posZ, entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).expand(0.5)
		val entities = getEntitiesWithinAABB(entity.worldObj, EntityLivingBase::class.java, axis)
		val homeID = entity.entityData.getInteger(TAG_HOME_ID)
		
		if (!entity.worldObj.isRemote && entity.thrower != null)
			entities.removeAll { !InteractionSecurity.canInteractWithEntity(entity.thrower, it) }
		
		for (living in entities) {
			entity.entityData.setInteger(TAG_HOME_ID, living.entityId)
			break
		}
		
		val result = entity.worldObj.getEntityByID(homeID)
		if (result == null || result.getDistanceToEntity(entity) >= 2f || burst.isFake) return
		
		result.motionX = entity.motionX
		result.motionY = entity.motionY
		result.motionZ = entity.motionZ
		
		if (result is EntityPlayerMP)
			result.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(result))
	}
}
