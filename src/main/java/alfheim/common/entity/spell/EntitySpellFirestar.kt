package alfheim.common.entity.spell

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.spell.*
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.*
import alfheim.common.spell.fire.SpellFirestar
import alfheim.common.spell.illusion.SpellDarkness
import net.minecraft.entity.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.Potion
import net.minecraft.util.DamageSource
import net.minecraft.world.World
import java.util.*

class EntitySpellFirestar(world: World, val caster: EntityLivingBase?): Entity(world), ITimeStopSpecific {
	
	override val isImmune = false
	
	init {
		setSize(0f, 0f)
		if (caster != null) setPosition(caster.posX, caster.posY + 1.5, caster.posZ)
	}
	
	constructor(world: World): this(world, null)
	
	override fun onEntityUpdate() {
		if (!AlfheimConfigHandler.enableMMO || caster == null || caster.isDead || ticksExisted > SpellFirestar.duration) {
			setDead()
			return
		}
		if (isDead || ASJUtilities.isClient) return
		
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.FLAMESTAR, dimension, posX, posY, posZ, 1.0, 52/255.0, 0.0, 5.0)
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.FLAMESTAR, dimension, posX, posY, posZ, 1.0, 208/255.0, 0.0, 3.0)
		
		val l = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, getBoundingBox(posX, posY, posZ).expand(SpellFirestar.radius))
		l.removeAll { Vector3.entityDistance(caster, it) > SpellFirestar.radius }
		
		l.forEach {
			if (it === caster || CardinalSystem.PartySystem.mobsSameParty(caster, it)) {
				it.addPotionEffect(PotionEffectU(Potion.fireResistance.id, 100))
				it.heal(SpellFirestar.efficiency.F)
			} else {
				if (!InteractionSecurity.canHurtEntity(caster, it)) return@forEach
				
				it.attackEntityFrom(DamageSource.inFire, SpellBase.over(caster, SpellDarkness.damage.D))
			}
		}
	}
	
	override fun entityInit() = Unit
	override fun readEntityFromNBT(nbt: NBTTagCompound?) = Unit
	override fun writeEntityToNBT(nbt: NBTTagCompound?) = Unit
	override fun affectedBy(uuid: UUID) = caster?.entityUniqueID != uuid
}
