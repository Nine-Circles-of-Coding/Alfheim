package alfheim.common.entity.boss.primal.ai.thrym

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.*
import alfheim.common.entity.boss.primal.EntityThrym
import alfheim.common.potion.PotionEternity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.entity.player.*
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.potion.Potion

class ThrymAISecondStageStart(val host: EntityThrym): EntityAIBase() {
	
	var ticks = 0
	
	override fun getMutexBits() = 0b1101
	
	override fun shouldExecute() = host.health <= host.maxHealth * 0.5 && host.stage < 2
	
	override fun startExecuting() {
		host.sucks = true
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.FENRIR_AREA, host.dimension, host.posX, host.posY, host.posZ, 300.0)
	}
	
	override fun continueExecuting() = ticks++ < 300
	
	override fun updateTask() {
		host.navigator.clearPathEntity()
		
		val list = getEntitiesWithinAABB(host.worldObj, EntityLivingBase::class.java, host.boundingBox(64))
		list.remove(host)
		list.removeAll { !host.canTarget(it) || (it as? EntityPlayer)?.capabilities?.disableDamage == true }
		list.forEach {
			val (x, y, z) = Vector3.fromEntity(it).sub(host).normalize().mul(0.08)
			
			it.motionX -= x
			it.motionY -= y
			it.motionZ -= z
			
			if (it is EntityPlayerMP) it.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(it))
			if (Vector3.entityDistancePlane(it, host) > 3) return@forEach
			
			if (it.attackEntityFrom(host.defaultWeaponDamage(it), 1f))
				it.playSoundAtEntity("${ModInfo.MODID}:thrym.icicle.hit", 0.1f, 1f)
		}
	}
	
	override fun resetTask() {
		host.sucks = false
		
		ticks = 0
		host.stage = 2
		host.chunkAttackCounter += 2 + host.playersOnArena().size * 3
		
		val list = getEntitiesWithinAABB(host.worldObj, EntityLivingBase::class.java, host.boundingBox(64))
		list.remove(host)
		list.removeAll { (it as? EntityPlayer)?.capabilities?.disableDamage == true }
		list.forEach {
			val dist = Vector3.fromEntity(it).sub(host).normalize()
			
			it.motionX += dist.x
			it.motionY += dist.y
			it.motionZ += dist.z
			
			if (it is EntityPlayerMP) it.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(it))
		}
		
		val freezeTarget = host.attackTarget ?: list.firstOrNull { it is EntityPlayer } ?: list.firstOrNull() ?: return
		host.attackTarget = freezeTarget
		freezeTarget.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDEternity, 100, PotionEternity.STUN))
		freezeTarget.addPotionEffect(PotionEffectU(Potion.digSlowdown.id, 100, 1))
	}
}
