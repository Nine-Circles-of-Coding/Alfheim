package alfheim.common.potion

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.CardinalSystem.PartySystem
import alfheim.common.core.handler.CardinalSystem.PartySystem.Party
import alfheim.common.core.helper.*
import alfheim.common.spell.wind.SpellThrow
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.DamageSource

object PotionThrow: PotionAlfheim(AlfheimConfigHandler.potionIDThrow, "throw", false, 0xAAFFFF) {
	
	override fun isReady(time: Int, mod: Int) = AlfheimConfigHandler.enableMMO
	
	override fun performEffect(target: EntityLivingBase, mod: Int) {
		if (!AlfheimConfigHandler.enableMMO) return
		
		val v = Vector3(target.lookVec).mul(mod + 1)
		target.motionX = v.x
		target.motionY = v.y
		target.motionZ = v.z
		
		var pt = PartySystem.getMobParty(target)
		if (pt == null) pt = Party()
		
		val l = getEntitiesWithinAABB(target.worldObj, EntityLivingBase::class.java, target.boundingBox.copy().expand(SpellThrow.radius))
		l.remove(target)
		for (e in l) if (!pt.isMember(e)) e.attackEntityFrom((if (target is EntityPlayer) DamageSource.causePlayerDamage(target) else DamageSource.causeMobDamage(target)).setTo(ElementalDamage.AIR), SpellThrow.damage)
	}
}
