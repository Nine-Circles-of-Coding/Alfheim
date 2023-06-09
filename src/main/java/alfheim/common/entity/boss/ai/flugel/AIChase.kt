package alfheim.common.entity.boss.ai.flugel

import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.random
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.entity.boss.EntityFlugel
import cpw.mods.fml.common.registry.GameRegistry

class AIChase(flugel: EntityFlugel, task: AITask): AIBase(flugel, task) {
	
	var lowest = false
	
	override fun startExecuting() {
		flugel.noClip = true
		val s = flugel.stage
		val i = if (s == 1) 200 else if (s == 2) 100 else 50
		flugel.aiTaskTimer = flugel.worldObj.rand.nextInt(i) + i
		
		lowest = flugel.worldObj.rand.nextInt(10) == 0
		
		if (flugel.worldObj.rand.nextInt(4) == 0) {
			val player = flugel.worldObj.getPlayerEntityByName(flugel.playersDamage.keys.random(flugel.rng) ?: return) ?: return
			if (player.capabilities.isCreativeMode) return
			
			for (a in 0..9)
				for (slot1 in player.inventory.mainInventory.indices) {
					val slot2 = flugel.worldObj.rand.nextInt(player.inventory.mainInventory.size)
					
					val stack1 = player.inventory.mainInventory[slot1]
					val stack2 = player.inventory.mainInventory[slot2]
					
					var flagStack1 = stack1 != null
					if (flagStack1) {
						val id = GameRegistry.findUniqueIdentifierFor(stack1.item)
						flagStack1 = if (id != null)
							flagStack1 && AlfheimConfigHandler.flugelSwapBlackList.contains(id.toString())
						else
							false
					}
					
					var flagStack2 = stack2 != null
					if (flagStack2) {
						val id = GameRegistry.findUniqueIdentifierFor(stack2.item)
						flagStack2 = if (id != null)
							flagStack2 && AlfheimConfigHandler.flugelSwapBlackList.contains(id.toString())
						else
							false
					}
					
					if (flagStack1 || flagStack2) continue
					
					player.inventory.mainInventory[slot1] = stack2
					player.inventory.mainInventory[slot2] = stack1
				}
		}
	}
	
	override fun continueExecuting(): Boolean {
		flugel.checkCollision()
		if (flugel.aiTaskTimer % 10 == 0) {
			val name = if (lowest)
				flugel.playersDamage.minByOrNull { it.value }?.key ?: "Notch"
			else
				flugel.playersDamage.maxByOrNull { it.value }?.key ?: "Notch"
			
			val target = flugel.worldObj.getPlayerEntityByName(name)
			
			if (target != null) {
				val mot = Vector3(target.posX - flugel.posX, target.posY - flugel.posY, target.posZ - flugel.posZ).normalize()
				flugel.motionX = mot.x
				flugel.motionY = mot.y
				flugel.motionZ = mot.z
				
			} else {
				flugel.playersDamage.remove(name)
			}
		}
		return canContinue()
	}
}