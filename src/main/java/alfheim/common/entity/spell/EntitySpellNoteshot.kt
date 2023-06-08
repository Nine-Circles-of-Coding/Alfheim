package alfheim.common.entity.spell

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.spell.ITimeStopSpecific
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.*
import alfheim.common.core.helper.*
import alfheim.common.potion.PotionEternity
import alfheim.common.spell.sound.SpellNoteshot
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.PotionEffect
import net.minecraft.util.DamageSource
import net.minecraft.world.World
import java.util.*

class EntitySpellNoteshot: Entity, ITimeStopSpecific {
	
	val caster: EntityLivingBase?
	
	override val isImmune = false
	
	constructor(world: World): this(world, null)
	
	constructor(world: World, caster: EntityLivingBase?): super(world) {
		setSize(0f, 0f)
		this.caster = caster
		if (caster == null) return
		setLocationAndAngles(caster.posX, caster.posY + caster.eyeHeight, caster.posZ, caster.rotationYaw, caster.rotationPitch)
		val (mx, my, mz) = Vector3(caster.lookVec)
		setMotion(mx, my, mz)
	}
	
	override fun onEntityUpdate() {
		if (!AlfheimConfigHandler.enableMMO || caster == null || caster.isDead || ticksExisted > 100 || isCollided) {
			setDead()
			return
		}
		if (isDead || ASJUtilities.isClient) return
		
		super.onEntityUpdate()
		moveEntity(motionX, motionY, motionZ)
		
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.NOTE, this)
		
		val list = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(0.25))
		list.remove(caster)
		for (e in list) {
			if (CardinalSystem.PartySystem.mobsSameParty(caster, e)) continue
			
			val damage = (if (caster is EntityPlayer) DamageSource.causePlayerDamage(caster) else DamageSource.causeMobDamage(caster)).setTo(ElementalDamage.PSYCHIC).setTo(ElementalDamage.AIR)
			if (e.attackEntityFrom(damage, SpellNoteshot.damage)) {
				e.addPotionEffect(PotionEffect(AlfheimConfigHandler.potionIDEternity, 50, PotionEternity.STUN or PotionEternity.IRREMOVABLE))
				setDead()
			}
		}
	}
	
	override fun entityInit() = Unit
	override fun readEntityFromNBT(nbt: NBTTagCompound?) = Unit
	override fun writeEntityToNBT(nbt: NBTTagCompound?) = Unit
	override fun affectedBy(uuid: UUID) = caster?.uniqueID == uuid
}
