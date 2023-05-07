package alfheim.common.entity.spell

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.spell.*
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.*
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.spell.wind.SpellLeafStorm
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.*
import net.minecraft.world.World
import java.awt.Color
import java.util.*

class EntitySpellLeafStorm(world: World, val caster: EntityLivingBase?): Entity(world), ITimeStopSpecific {
	
	override val isImmune = false
	
	init {
		setSize(0f, 0f)
		if (caster != null) setPosition(caster.posX, caster.posY, caster.posZ)
	}
	
	constructor(world: World): this(world, null)
	
	override fun onEntityUpdate() {
		if (!AlfheimConfigHandler.enableMMO || caster == null || caster.isDead || ticksExisted > SpellLeafStorm.duration) {
			setDead()
			return
		}
		if (isDead || ASJUtilities.isClient) return
		
		val v = Vector3()
		
		for (i in 0..3) {
			val (x, _, z) = v.rand().sub(0.5).normalize().mul(Math.random() * SpellLeafStorm.radius).add(this)
			val (mx, _, mz) = v.rand().sub(0.5).mul(0.1)
			VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.FEATHER, dimension, x, posY + SpellLeafStorm.radius + Math.random(), z, mx, -0.5, mz, 0x00AA00.D, 2.0, 1.0, SpellLeafStorm.radius * 2)
		}
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, getBoundingBox(posX, posY, posZ).expand(SpellLeafStorm.radius)).forEach {
			if (Vector3.entityDistance(caster, it) > SpellLeafStorm.radius) return@forEach
			
			if (CardinalSystem.PartySystem.mobsSameParty(caster, it)) it.addPotionEffect(PotionEffect(Potion.moveSpeed.id, 50, SpellLeafStorm.efficiency.I))
			else if (InteractionSecurity.canHurtEntity(caster, it)) it.attackEntityFrom(DamageSourceSpell.wind, SpellBase.over(caster, SpellLeafStorm.damage.D))
		}
	}
	
	override fun entityInit() = Unit
	override fun readEntityFromNBT(nbt: NBTTagCompound?) = Unit
	override fun writeEntityToNBT(nbt: NBTTagCompound?) = Unit
	override fun affectedBy(uuid: UUID) = caster?.entityUniqueID != uuid
}
