package alfheim.common.entity.boss.ai.flugel

import alexsocol.asjlib.*
import alfheim.common.entity.boss.EntityFlugel
import alfheim.common.item.AlfheimItems
import alfheim.common.item.relic.ItemFlugelSoul.Companion.TAG_ATTACKER_ID
import net.minecraft.item.ItemStack
import net.minecraft.util.MathHelper
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.entity.EntityManaBurst
import java.awt.Color

class AIRays(flugel: EntityFlugel, task: AITask): AIBase(flugel, task) {
	
	override fun startExecuting() {
		flugel.aiTaskTimer = 20
		var player = ASJUtilities.getClosestVulnerablePlayerToEntity(flugel, EntityFlugel.RANGE * 2.0)
		if (player != null) player = flugel.worldObj.getClosestPlayerToEntity(flugel, EntityFlugel.RANGE * 2.0)
		if (player != null) flugel.setPosition(flugel.posX, player.posY, flugel.posZ)
		
		val more = if (flugel.isHardMode) 10 else 15
		var i = 0
		while (i < 360) {
			getBurst(flugel, i).spawn()
			i += more
		}
		flugel.playSoundAtEntity("botania:terraBlade", 0.4f, 1.4f)
	}
	
	fun getBurst(flugel: EntityFlugel, i: Int): EntityManaBurst {
		val burst = EntityManaBurst(flugel.worldObj)
		burst.color = Color(180, 75, 180).rgb
		burst.mana = 100
		burst.startingMana = 100
		burst.minManaLoss = 600
		burst.manaLossPerTick = 4f
		burst.gravity = 0f
		val lens = ItemStack(AlfheimItems.flugelSoul, 1, if (flugel.isUltraMode) 1 else 0)
		ItemNBTHelper.setInt(lens, TAG_ATTACKER_ID, flugel.entityId)
		burst.sourceLens = lens
		burst.setBurstSourceCoords(0, -1, 0)
		burst.setLocationAndAngles(flugel.posX, flugel.posY + flugel.eyeHeight, flugel.posZ, i.F, -flugel.rotationPitch)
		burst.posX -= MathHelper.cos(i / 180f * Math.PI.F) / 2.0
		burst.posY -= 0.1
		burst.posZ -= MathHelper.sin(i / 180f * Math.PI.F) / 2.0
		burst.setPosition(burst.posX, burst.posY, burst.posZ)
		burst.yOffset = 0f
		val f = 0.4f
		val mx = MathHelper.sin(burst.rotationYaw / 180f * Math.PI.F) * f / 2.0
		val mz = -(MathHelper.cos(burst.rotationYaw / 180f * Math.PI.F) * f) / 2.0
		burst.setMotion(mx * 5, 0.0, mz * 5)
		return burst
	}
	
	override fun continueExecuting(): Boolean {
		return canContinue()
	}
}