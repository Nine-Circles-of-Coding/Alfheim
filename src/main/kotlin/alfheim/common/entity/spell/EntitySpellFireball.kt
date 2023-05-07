package alfheim.common.entity.spell

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.spell.*
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.PartySystem
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.entity.boss.primal.EntitySurtr
import alfheim.common.spell.fire.SpellFireball
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.World
import vazkii.botania.common.Botania
import java.util.*
import kotlin.math.*

class EntitySpellFireball(world: World): Entity(world), ITimeStopSpecific {
	
	var accelerationX = 0.0
	var accelerationY = 0.0
	var accelerationZ = 0.0
	
	var caster: EntityLivingBase? = null
	var target: EntityLivingBase? = null
		set(value) {
			noClip = value != null
			field = value
		}
	
	override val isImmune = false
	
	init {
		setSize(0f, 0f)
	}
	
	constructor(world: World, x: Double, y: Double, z: Double, accX: Double, accY: Double, accZ: Double): this(world) {
		setLocationAndAngles(x, y, z, rotationYaw, rotationPitch)
		val d = sqrt(accX * accX + accY * accY + accZ * accZ)
		accelerationX = accX / d * SpellFireball.efficiency
		accelerationY = accY / d * SpellFireball.efficiency
		accelerationZ = accZ / d * SpellFireball.efficiency
	}
	
	constructor(world: World, shooter: EntityLivingBase): this(world, shooter.posX, shooter.posY + shooter.eyeHeight, shooter.posZ, shooter.lookVec.xCoord, shooter.lookVec.yCoord, shooter.lookVec.zCoord) {
		caster = shooter
		setRotation(shooter.rotationYaw, shooter.rotationPitch)
	}
	
	override fun attackEntityFrom(source: DamageSource?, damage: Float) = false
	
	fun onImpact(mop: MovingObjectPosition?) {
		if (worldObj.isRemote || isDead) return
		
		val l = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(SpellFireball.radius))
		l.remove(caster)
		
		val dmg = DamageSourceSpell.fireball(this, caster)
		if (caster is EntitySurtr && rand.nextInt(10) == 0) dmg.setDamageBypassesArmor().setDamageIsAbsolute()
		
		l.forEach {
			it.attackEntityFrom(dmg, if (caster is EntitySurtr) 10f else SpellBase.over(caster, SpellFireball.damage.D))
		}
		
		worldObj.playSoundEffect(posX, posY, posZ, "random.explode", 4f, (1f + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2f) * 0.7f)
		VisualEffectHandler.sendPacket(VisualEffects.EXPL, this)
		
		setDead()
	}
	
	override fun onUpdate() {
		if (isDead) return
		
		if (!AlfheimConfigHandler.enableMMO && caster !is EntitySurtr && !worldObj.isRemote && (caster != null && caster!!.isDead))
			return setDead()
		
		super.onUpdate()
		
		val vec3 = Vec3.createVectorHelper(posX, posY, posZ)
		val vec31 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ)
		var movingobjectposition: MovingObjectPosition? = worldObj.rayTraceBlocks(vec3, vec31)
		
		if (movingobjectposition == null) run {
			val l = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox.addCoord(motionX, motionY, motionZ).expand(0.1, 0.1, 0.1))
			l.remove(caster)
			l.forEach {
				if (!it.canBeCollidedWith() || PartySystem.mobsSameParty(caster, it)) return@forEach
				movingobjectposition = MovingObjectPosition(it)
				return@run
			}
		}
		
		if (!noClip && (movingobjectposition != null || (ticksExisted >= SpellFireball.duration && riddenByEntity == null))) // a meme for fun
			return onImpact(movingobjectposition)
		
		val target = target
		
		if (target == null) {
			prevPosX = posX
			prevPosY = posY
			prevPosZ = posZ
			posX += motionX
			posY += motionY
			posZ += motionZ
			val f1 = sqrt(motionX * motionX + motionZ * motionZ)
			rotationYaw = (atan2(motionZ, motionX) * 180.0 / Math.PI).F + 90f
			
			rotationPitch = (atan2(f1.D, motionY) * 180.0 / Math.PI).F - 90f
			while (rotationPitch - prevRotationPitch < -180f) prevRotationPitch -= 360f
			while (rotationPitch - prevRotationPitch >= 180f) prevRotationPitch += 360f
			while (rotationYaw - prevRotationYaw < -180f) prevRotationYaw -= 360f
			while (rotationYaw - prevRotationYaw >= 180f) prevRotationYaw += 360f
			
			rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2f
			rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2f
			var f2 = 0.95f
			
			if (isInWater) {
				for (j in 0..3) {
					val f3 = 0.25f
					worldObj.spawnParticle("bubble", posX - motionX * f3.D, posY - motionY * f3.D, posZ - motionZ * f3.D, motionX, motionY, motionZ)
				}
				
				f2 = 0.8f
			}
			
			motionX += accelerationX
			motionY += accelerationY
			motionZ += accelerationZ
			motionX *= f2.D
			motionY *= f2.D
			motionZ *= f2.D
			setPosition(posX, posY, posZ)
		} else run el@{
			if (!target.isEntityAlive) {
				this.target = null
				return@el
			}
			
			chase()
		}
		
		for (i in 0..4) {
			val v = Vector3(motionX, motionY, motionZ)//.normalize().multiply(0.05);
			Botania.proxy.wispFX(worldObj, posX, posY - 0.2, posZ, 1f, Math.random().F * 0.25f, Math.random().F * 0.075f, 0.65f + Math.random().F * 0.45f, v.x.F, v.y.F, v.z.F, 0.1f)
			
			Botania.proxy.wispFX(worldObj, posX + Math.random() * 0.5 - 0.25, posY + Math.random() * 0.5 - 0.25, posZ + Math.random() * 0.5 - 0.25, 1f, Math.random().F * 0.25f, Math.random().F * 0.075f, Math.random().F * 0.25f, 0f, 0.5f)
			// smoke
			val gs = Math.random().F * 0.15f
			Botania.proxy.wispFX(worldObj, posX, posY - 0.25, posZ, gs, gs, gs, 2f, -0.15f)
		}
	}
	
	// code from Forgotten Relics
	fun chase() {
		if (getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, this.boundingBox(0.5)).contains(target)) {
			for (i in 0..6) {
				val r = 1.0f
				val g = 1.0f
				val b = 1.0f
				val s = 0.1f + Math.random().F * 0.1f
				val m = 0.15f
				val xm = (Math.random().F - 0.5f) * m
				val ym = (Math.random().F - 0.5f) * m
				val zm = (Math.random().F - 0.5f) * m
				Botania.proxy.wispFX(worldObj, posX + width / 2, posY + height / 2, posZ + width / 2, r, g, b, s, xm, ym, zm)
			}
			
			return onImpact(MovingObjectPosition(target))
		}
		
		posX += motionX
		posY += motionY
		posZ += motionZ
		
		val d: Double = getDistanceSqToEntity(target)
		val dx: Double = target!!.posX - posX
		val dy: Double = target!!.boundingBox.minY + target!!.height * 0.6 - posY
		val dz: Double = target!!.posZ - posZ
		val d2 = SpellFireball.efficiency * 10
		motionX += dx / d * d2
		motionY += dy / d * d2
		motionZ += dz / d * d2
		motionX = MathHelper.clamp_double(motionX, -d2, d2)
		motionY = MathHelper.clamp_double(motionY, -d2, d2)
		motionZ = MathHelper.clamp_double(motionZ, -d2, d2)
		
		setPosition(posX, posY, posZ)
	}
	
	override fun canBeCollidedWith() = true
	
	override fun getCollisionBorderSize() = 1f
	
	@SideOnly(Side.CLIENT)
	override fun getShadowSize() = 0f
	
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