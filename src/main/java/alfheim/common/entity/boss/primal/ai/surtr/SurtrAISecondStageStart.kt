package alfheim.common.entity.boss.primal.ai.surtr

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.entity.boss.primal.EntitySurtr
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.ai.EntityAIBase

class SurtrAISecondStageStart(val host: EntitySurtr): EntityAIBase() {
	
	var timer = 0
	
	override fun getMutexBits() = 0b1001
	
	override fun shouldExecute() = host.stage < 2 && host.health < host.maxHealth * 0.5
	
	override fun startExecuting() {
		host.wall = true
		host.playSoundAtEntity("${ModInfo.MODID}:surtr.wall.form", 10f, 1f)
	}
	
	override fun continueExecuting() = timer++ < 300
	
	override fun updateTask() {
		host.navigator.clearPathEntity()
		
		val radius = timer / 20.0 + 3
		
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.SURTRWALL, host.dimension, host.posX, host.posY, host.posZ, radius)
		
		if (timer % 20 != 0) return
		
		getEntitiesWithinAABB(host.worldObj, EntityLivingBase::class.java, host.boundingBox().expand(radius, 0, radius)).forEach {
			if (!host.isAllie(it) && Vector3.entityDistancePlane(it, host) < radius) it.attackEntityFrom(host.defaultWeaponDamage(it), 2f)
		}
	}
	
	override fun resetTask() {
		timer = 0
		host.stage = 2
		host.wall = false
		
		val (x, y, z) = Vector3.fromEntity(host)
		host.worldObj.spawnParticle("hugeexplosion", x, y, z, 0.0, 0.0, 0.0)
		host.playSoundAtEntity("random.explode", 20f, (1f + (host.worldObj.rand.nextFloat() - host.worldObj.rand.nextFloat()) * 0.2f) * 0.7f)
		
		getEntitiesWithinAABB(host.worldObj, EntityLivingBase::class.java, host.arenaBB).forEach {
			if (!host.isAllie(it)) it.attackEntityFrom(host.defaultWeaponDamage(it), 5f)
		}
	}
}
