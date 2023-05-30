package alfheim.common.entity.boss.primal.ai.thrym

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.*
import alfheim.common.entity.EntitySnowSprite
import alfheim.common.entity.boss.primal.EntityThrym
import alfheim.common.potion.PotionEternity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.entity.player.*
import net.minecraft.network.play.server.S12PacketEntityVelocity
import kotlin.math.max

class ThrymAIThirdStageStart(val host: EntityThrym): EntityAIBase() {
	
	var inside = emptySet<String>()
	var shouldEnd = false
	var eternal = false
	var timer = 0
	
	override fun getMutexBits() = 0b1001
	
	override fun shouldExecute() = host.health <= host.maxHealth * 0.1 && host.stage < 3
	
	override fun startExecuting() {
		val src = host.source
		
		val players = host.playersOnArena()
		
		players.forEach {
			it.playSoundAtEntity("${ModInfo.MODID}:thrym.shield.teleport", 1f, 1f)
		}
		
		host.setPosition(src)
		host.playSoundAtEntity("${ModInfo.MODID}:thrym.shield.form", 1f, 1f)
		
		inside = players.apply { removeAll { it.health > it.maxHealth * 0.5 } }.mapTo(HashSet()) {
			it.setPositionAndUpdate(host.posX, host.posY, host.posZ)
			it.commandSenderName
		}
		if (inside.isEmpty()) return // no targets - just heal to 30%
		
		getEntitiesWithinAABB(host.worldObj, EntitySnowSprite::class.java, host.arenaBB).forEach { it.setDead() }
		
		for (i in -8..7) {
			val (x, y, z) = Vector3().rand().sub(0.5, 0, 0.5).normalize().mul(DOME_RADIUS + if (i < 0) -3 else 3).add(host)
			EntitySnowSprite(host.worldObj).apply {
				setPosition(x, y, z)
				entityData.setBoolean(TAG_INSIDE, i < 0)
				spawn()
			}
		}
	}
	
	override fun continueExecuting() = !shouldEnd || eternal
	
	override fun updateTask() {
		val src = Vector3(host.source).add(0.5)
		val (x, y, z) = src
		host.setPosition(x, y - 1.5, z)
		
		host.navigator.clearPathEntity()
		if (Vector3.vecEntityDistance(src, host) > 2.0)
			host.setPosition(x, y, z)
		
		if (host.attackTarget != null && Vector3.vecEntityDistance(src, host.attackTarget) > DOME_RADIUS) host.attackTarget = null
		
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.THRYM_DOME, host.dimension, x, y, z)
		
		val targets = selectEntitiesWithinAABB(host.worldObj, EntityLivingBase::class.java, host.arenaBB) { it is EntityPlayer && host.canTarget(it) || it is EntitySnowSprite }
		
		var sIn = 0
		var sOut = 0
		
		for (target in targets) {
			val targetPos = Vector3.fromEntity(target)
			
			val out = Vector3.vecDistance(targetPos, src) > DOME_RADIUS + 1
			val pushIn = Vector3.vecDistance(targetPos, src) > DOME_RADIUS - 1
			val pushOut = Vector3.vecDistance(targetPos, src) < DOME_RADIUS + 1
			
			if (target.isEntityAlive) {
				if (target is EntitySnowSprite)
					if (target.entityData.getBoolean(TAG_INSIDE)) ++sIn else ++sOut
				else if (target is EntityPlayer && !out) {
					if (host.attackTarget == null && host.canTarget(target)) host.attackTarget = target
				}
			}
			
			val (mx, my, mz) = src.copy().sub(targetPos).normalize().mul(0.5)
			
			if (target.commandSenderName in inside || target.entityData.getBoolean(TAG_INSIDE)) {
				if (!pushIn) continue
				
				target.motionX += mx
				target.motionY += my
				target.motionZ += mz
			} else if (pushOut) {
				target.motionX -= mx
				target.motionY -= my
				target.motionZ -= mz
			} else
				continue
			
			if (target is EntityPlayerMP)
				target.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(target))
		}
		
		if (inside.isEmpty()) {
			if (++timer >= 600) {
				shouldEnd = true
				host.health = max(host.health, host.maxHealth * 0.3f)
				return
			}
			
			host.invulnerable = true
			if (host.health >= host.maxHealth * 0.3f) return
			
			return host.heal(host.maxHealth * 0.0005f)
		}
		
		if (eternal) return
		
		if (sOut == 0) {
			targets.filterIsInstance<EntitySnowSprite>().forEach { sprite ->
				getEntitiesWithinAABB(sprite.worldObj, EntityPlayer::class.java, sprite.boundingBox(3)).forEach { it.heal(it.maxHealth) }
				sprite.setDead()
			}
			
			eternal = true
			return
		}
		
		if (sIn == 0) {
			targets.filterIsInstance<EntitySnowSprite>().forEach { sprite ->
				getEntitiesWithinAABB(sprite.worldObj, EntityPlayer::class.java, sprite.boundingBox(3)).forEach {
					if (it.commandSenderName !in inside) it.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDEternity, 100, PotionEternity.STUN))
				}
				
				sprite.setDead()
			}
			
			shouldEnd = true
			return
		}
		
		val pIn = targets.filterIsInstance<EntityPlayer>().any { it.isEntityAlive && it.commandSenderName in inside }
		
		if (!pIn) {
			shouldEnd = true
			return
		}
	}
	
	override fun resetTask() {
		host.stage = 3
		host.invulnerable = false
		shouldEnd = false
		eternal = false
		timer = 0
		host.playSoundAtEntity("${ModInfo.MODID}:thrym.shield.break", 1f, 1f)
	}
	
	override fun isInterruptible() = false
	
	companion object {
		
		const val TAG_INSIDE = "CFT.inside"
		const val DOME_RADIUS = 8
		
	}
}
