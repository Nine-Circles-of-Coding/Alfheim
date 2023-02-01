package alfheim.common.entity.spell

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.AlfheimCore
import alfheim.api.spell.ITimeStopSpecific
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.*
import alfheim.common.network.MessageVisualEffect
import alfheim.common.spell.illusion.SpellDarkness
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.Potion
import net.minecraft.world.World
import java.util.*

class EntitySpellDarkness(world: World?, val caster: EntityLivingBase?): Entity(world), ITimeStopSpecific {
	
	override val isImmune = false
	
	init {
		setSize(0f, 0f)
		if (caster != null) setPosition(caster.posX, caster.posY, caster.posZ)
	}
	
	constructor(world: World): this(world, null)
	
	override fun onEntityUpdate() {
		if (!AlfheimConfigHandler.enableMMO || caster == null || caster.isDead || ticksExisted > SpellDarkness.duration) {
			setDead()
			return
		}
		if (isDead || ASJUtilities.isClient) return
		
		if (ticksExisted % 5 == 0)
			for (player in worldObj.playerEntities)
				if (player is EntityPlayerMP && player !== caster && !CardinalSystem.PartySystem.sameParty(player, caster))
					AlfheimCore.network.sendTo(MessageVisualEffect(VisualEffectHandlerClient.VisualEffects.SMOKE.ordinal, posX, posY, posZ), player)
		
		val l = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, getBoundingBox(posX, posY, posZ).expand(SpellDarkness.radius))
		l.removeAll { Vector3.entityDistance(caster, it) > SpellDarkness.radius }
		l.forEach {
			if (it === caster || CardinalSystem.PartySystem.mobsSameParty(caster, it)) {
				it.addPotionEffect(PotionEffectU(Potion.damageBoost.id, 100))
				it.addPotionEffect(PotionEffectU(Potion.moveSpeed.id, 100, 5))
				it.addPotionEffect(PotionEffectU(Potion.regeneration.id, 100))
			} else {
				if (!InteractionSecurity.canHurtEntity(caster, it)) return@forEach
				
				it.addPotionEffect(PotionEffectU(Potion.blindness.id, 100, 4))
				it.addPotionEffect(PotionEffectU(Potion.moveSlowdown.id, 100, 4))
			}
		}
	}
	
	override fun entityInit() = Unit
	override fun readEntityFromNBT(nbt: NBTTagCompound?) = Unit
	override fun writeEntityToNBT(nbt: NBTTagCompound?) = Unit
	override fun affectedBy(uuid: UUID) = caster?.entityUniqueID != uuid
}
