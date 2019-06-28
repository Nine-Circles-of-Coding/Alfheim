package alfheim.common.entity.boss.ai.flugel

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.common.entity.boss.EntityFlugel
import vazkii.botania.common.Botania
import vazkii.botania.common.entity.EntityFallingStar
import java.util.*

class AIDeathray(flugel: EntityFlugel, task: AITask): AIBase(flugel, task) {
	
	override fun isInterruptible(): Boolean {
		return false
	}
	
	override fun startExecuting() {
		flugel.aiTaskTimer = EntityFlugel.DEATHRAY_TICKS
	}
	
	override fun continueExecuting(): Boolean {
		val deathray = flugel.aiTaskTimer
		val source = flugel.source
		val range = EntityFlugel.RANGE.toFloat()
		if (ModInfo.DEV) if (!flugel.worldObj.isRemote) for (player in flugel.playersAround) ASJUtilities.chatLog("Deathray in $deathray", player)
		flugel.setPosition(source.posX + 0.5, (source.posY + 3).toDouble(), source.posZ + 0.5)
		flugel.motionX = 0.0
		flugel.motionY = 0.0
		flugel.motionZ = 0.0
		if (deathray > 10) flugel.spawnPatyklz(true)
		
		if (deathray == 1) {
			val stars = ArrayList<EntityFallingStar>(16)
			val rang = Math.ceil(range.toDouble()).toInt()
			for (l in 0..3) {
				var i = 0
				while (i < 16) {
					val x = flugel.worldObj.rand.nextInt(rang * 2 + 1) - rang
					val z = flugel.worldObj.rand.nextInt(rang * 2 + 1) - rang
					if (vazkii.botania.common.core.helper.MathHelper.pointDistancePlane(x.toDouble(), z.toDouble(), 0.0, 0.0) <= range) {
						val posVec = Vector3((source.posX + x).toDouble(), (source.posY + l * 20 + 10).toDouble(), (source.posZ + z).toDouble())
						val motVec = Vector3((Math.random() - 0.5) * 18, 24.0, (Math.random() - 0.5) * 18)
						posVec.add(motVec)
						motVec.normalize().negate().mul(1.5)
						
						val star = EntityFallingStar(flugel.worldObj, flugel)
						star.setPosition(posVec.x, posVec.y, posVec.z)
						star.motionX = motVec.x
						star.motionY = motVec.y
						star.motionZ = motVec.z
						stars.add(star)
						i++
					}
				}
				
				val players = flugel.playersAround
				for (player in players) {
					val posVec = Vector3(player.posX, player.posY + (l * 10 * 2).toDouble() + 10.0, player.posZ)
					val motVec = Vector3((Math.random() - 0.5) * 18, 24.0, (Math.random() - 0.5) * 18)
					posVec.add(motVec)
					motVec.normalize().negate().mul(1.5)
					
					val star = EntityFallingStar(flugel.worldObj, flugel)
					star.setPosition(posVec.x, posVec.y, posVec.z)
					star.motionX = motVec.x
					star.motionY = motVec.y
					star.motionZ = motVec.z
					stars.add(star)
				}
			}
			
			for (star in stars) flugel.worldObj.spawnEntityInWorld(star)
			if (flugel.worldObj.isRemote) {
				for (i in 0..359) {
					val r = 0.2f + Math.random().toFloat() * 0.3f
					val g = Math.random().toFloat() * 0.3f
					val b = 0.2f + Math.random().toFloat() * 0.3f
					Botania.proxy.wispFX(flugel.worldObj, flugel.posX, flugel.posY + 1, flugel.posZ, r, g, b, 0.5f, Math.cos(i.toDouble()).toFloat() * 0.4f, 0f, Math.sin(i.toDouble()).toFloat() * 0.4f)
					Botania.proxy.wispFX(flugel.worldObj, flugel.posX, flugel.posY + 1, flugel.posZ, r, g, b, 0.5f, Math.cos(i.toDouble()).toFloat() * 0.3f, 0f, Math.sin(i.toDouble()).toFloat() * 0.3f)
					Botania.proxy.wispFX(flugel.worldObj, flugel.posX, flugel.posY + 1, flugel.posZ, r, g, b, 0.5f, Math.cos(i.toDouble()).toFloat() * 0.2f, 0f, Math.sin(i.toDouble()).toFloat() * 0.2f)
					Botania.proxy.wispFX(flugel.worldObj, flugel.posX, flugel.posY + 1, flugel.posZ, r, g, b, 0.5f, Math.cos(i.toDouble()).toFloat() * 0.1f, 0f, Math.sin(i.toDouble()).toFloat() * 0.1f)
				}
			}
		}
		
		return canContinue()
	}
	
	override fun resetTask() {
		flugel.stage = EntityFlugel.STAGE_DEATHRAY
		flugel.aiTaskTimer = 0
		flugel.aiTask = AITask.REGEN
	}
}