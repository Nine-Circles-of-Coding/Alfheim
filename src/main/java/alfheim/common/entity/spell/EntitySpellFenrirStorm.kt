package alfheim.common.entity.spell

import alexsocol.asjlib.*
import alexsocol.asjlib.math.*
import alfheim.api.spell.*
import alfheim.common.core.handler.*
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.entity.boss.EntityFenrir
import alfheim.common.spell.wind.SpellFenrirStorm
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import java.util.*

class EntitySpellFenrirStorm(world: World): Entity(world), ITimeStopSpecific {
	
	val area: OrientedBB
	var caster: EntityLivingBase? = null
	var mjolnir: Boolean
		get() = getFlag(6)
		set(build) = setFlag(6, build)
	
	override val isImmune = false
	
	val radius: Double
		get() = if (caster is EntityFenrir) 24.0 else SpellFenrirStorm.radius
	
	init {
		setSize(0.25f, 0.25f)
		area = OrientedBB(getBoundingBox(-0.5, -0.5, -radius, 0.5, 0.5, radius))
		renderDistanceWeight = radius / 2
	}
	
	constructor(world: World, caster: EntityLivingBase, mjolnir: Boolean = false): this(world) {
		this.caster = caster
		val l = Vector3(caster.lookVec).mul(0.1)
		setPositionAndRotation(caster.posX + l.x, caster.posY + caster.eyeHeight.D + l.y, caster.posZ + l.z, caster.rotationYaw, caster.rotationPitch)
		
		area.translate(caster.posX, caster.posY + caster.eyeHeight, caster.posZ)
		area.rotateOX(-caster.rotationPitch) // sign is ok!!!
		area.rotateOY(caster.rotationYaw) // sign is ok!!!
		
		val v = Vector3(caster.lookVec).mul(radius + 0.5)
		area.translate(v.x, v.y, v.z)
		
		this.mjolnir = mjolnir
	}
	
	override fun onEntityUpdate() {
		if ((!AlfheimConfigHandler.enableMMO && !mjolnir) || (!worldObj.isRemote && caster == null) || ticksExisted > 12) {
			setDead()
			return
		}
		if (isDead || ASJUtilities.isClient) return
		
		val caster = caster ?: return
		
		if (mjolnir) {
			rotationYaw = caster.rotationYaw
			rotationPitch = caster.rotationPitch

			val l = Vector3(caster.lookVec).mul(0.1)
			setPositionAndRotation(caster.posX + l.x, caster.posY + caster.eyeHeight.D + l.y, caster.posZ + l.z, caster.rotationYaw, caster.rotationPitch)

			area.fromAABB(getBoundingBox(-0.5, -0.5, -radius, 0.5, 0.5, radius))
			area.translate(caster.posX, caster.posY + caster.eyeHeight, caster.posZ)
			area.rotateOX(-caster.rotationPitch) // sign is ok!!!
			area.rotateOY(caster.rotationYaw) // sign is ok!!!

			val v = Vector3(caster.lookVec).mul(radius + 0.5)
			area.translate(v.x, v.y, v.z)
		}
		
		if (ticksExisted != 4 && !mjolnir) return
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, area.toAABB()).forEach { e ->
			if (e !== caster && area.intersectsWith(OrientedBB(e.boundingBox()))) e.attackEntityFrom(DamageSourceSpell.lightningIndirect(this, caster), SpellBase.over(caster, SpellFenrirStorm.damage.D))
		}
	}
	
	override fun affectedBy(uuid: UUID) = caster!!.uniqueID != uuid
	
	public override fun entityInit() = Unit
	
	public override fun readEntityFromNBT(nbt: NBTTagCompound) {
		if (nbt.hasKey("castername")) caster = worldObj.getPlayerEntityByName(nbt.getString("castername")) else setDead()
		if (caster == null) setDead()
	}
	
	public override fun writeEntityToNBT(nbt: NBTTagCompound) {
		if (caster is EntityPlayer) nbt.setString("castername", caster!!.commandSenderName)
	}
}