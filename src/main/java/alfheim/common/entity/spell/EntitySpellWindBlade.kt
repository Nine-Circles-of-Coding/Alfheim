package alfheim.common.entity.spell

import alexsocol.asjlib.*
import alexsocol.asjlib.math.*
import alfheim.api.spell.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.entity.boss.EntityFenrir
import alfheim.common.spell.wind.SpellWindBlades
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import vazkii.botania.common.Botania
import java.util.*

class EntitySpellWindBlade(world: World): Entity(world), ITimeStopSpecific {
	
	private var caster: EntityLivingBase? = null
	
	var body = OrientedBB()
	
	var isFenrir: Boolean
		get() = getFlag(6)
		set(value) {
			if (value)
				setSize(0.25f, 5f)
			else
				setSize(3f, 0.1f)
			
			setFlag(6, value)
		}
	
	override val isImmune = false
	
	constructor(world: World, caster: EntityLivingBase): this(world, caster, .0)
	
	constructor(world: World, caster: EntityLivingBase, i: Double): this(world) {
		this.caster = caster
		isFenrir = caster is EntityFenrir
		setPositionAndRotation(caster.posX, caster.posY + i + caster.height * 0.75, caster.posZ, caster.rotationYaw, 0f)
	}
	
	override fun onEntityUpdate() {
		if (isFenrir && height != 5f) isFenrir = true // FUCK YOU no-size-sync !!!
		
		if ((!AlfheimConfigHandler.enableMMO && !isFenrir) || !worldObj.isRemote && (caster == null || caster!!.isDead || ticksExisted > SpellWindBlades.duration)) {
			setDead()
			return
		}
		
		if (ASJUtilities.isClient) {
			if (isFenrir)
				Botania.proxy.wispFX(worldObj, posX, posY + Math.random() * 5, posZ, Math.random().F * 0.1f + 0.8f, Math.random().F * 0.1f + 0.9f, Math.random().F * 0.1f + 0.8f, Math.random().F * 0.3f + 0.2f, motionX.F / -10f, motionY.F / -10f, motionZ.F / -10f, 0.5f)
			else
				Botania.proxy.wispFX(worldObj, posX + Math.random() * 2 - 1, posY, posZ + Math.random() * 2 - 1, Math.random().F * 0.1f + 0.8f, Math.random().F * 0.1f + 0.9f, Math.random().F * 0.1f + 0.8f, Math.random().F * 0.3f + 0.2f, motionX.F / -10f, motionY.F / -10f, motionZ.F / -10f, 0.5f)
			
			return
		}
		
		if (isCollidedHorizontally) setDead()
		
		if (isDead) return
		
		prevPosX = posX
		prevPosY = posY
		prevPosZ = posZ
		
		val (mx, _, mz) = Vector3(ASJUtilities.getLookVec(this)).mul(if (isFenrir) 2 else SpellWindBlades.efficiency)
		moveEntity(mx, 0.0, mz)
		
		(if (isFenrir) body.fromParams(3, 0.1, 3) else body.fromParams(0.25, 5, 3)).setPosition(posX, posY, posZ)
		if (isFenrir) body.rotateOZ(90)
		
		body.rotateOY(rotationYaw.D)
		body.rotateOX(rotationPitch.D)
		
		val l = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, body.toAABB())
		l.remove(caster)
		
		for (e in l)
			if (body.intersectsWith(e.boundingBox))
				e.attackEntityFrom(DamageSourceSpell.windblade(this, caster), if (isFenrir) 10f else SpellBase.over(caster, SpellWindBlades.damage.D))
	}
	
	override fun affectedBy(uuid: UUID) = caster!!.uniqueID != uuid
	
	override fun entityInit() = Unit
	
	public override fun readEntityFromNBT(nbt: NBTTagCompound) {
		if (nbt.hasKey("castername")) caster = worldObj.getPlayerEntityByName(nbt.getString("castername")) else setDead()
		if (caster == null) setDead()
		isFenrir = nbt.getBoolean("TAG_FENRIR")
	}
	
	public override fun writeEntityToNBT(nbt: NBTTagCompound) {
		if (caster is EntityPlayer) nbt.setString("castername", caster!!.commandSenderName)
		nbt.setBoolean("TAG_FENRIR", isFenrir)
	}
}