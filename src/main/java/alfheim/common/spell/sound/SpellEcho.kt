package alfheim.common.spell.sound

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.AlfheimCore
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.network.MessageVisualEffect
import net.minecraft.entity.*
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.player.*

object SpellEcho: SpellBase("echo", EnumRace.POOKA, 4000, 1500, 5) {
	
	override val usableParams: Array<Any>
		get() = arrayOf(radius)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		val list = getEntitiesWithinAABB(caster.worldObj, Entity::class.java, caster.boundingBox.expand(radius))
		list.forEach {
			if (Vector3.entityDistance(it, caster) > radius) return@forEach
			
			when (it) {
				is EntityItem       -> VisualEffectHandler.sendPacket(VisualEffects.ECHO_ITEM, it)
				is IMob             -> VisualEffectHandler.sendPacket(VisualEffects.ECHO_MOB, it)
				is EntityPlayer     -> VisualEffectHandler.sendPacket(VisualEffects.ECHO_PLAYER, it)
				is EntityLivingBase -> VisualEffectHandler.sendPacket(VisualEffects.ECHO_ENTITY, it)
			}
		}
		
		if (caster is EntityPlayerMP) AlfheimCore.network.sendTo(MessageVisualEffect(VisualEffects.ECHO.ordinal, caster.posX, caster.posY, caster.posZ), caster)
		return result
	}
}