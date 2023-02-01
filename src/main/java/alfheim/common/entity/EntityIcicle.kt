package alfheim.common.entity

import alexsocol.asjlib.*
import alfheim.common.core.handler.CardinalSystem
import alfheim.common.core.util.DamageSourceSpell
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.World
import vazkii.botania.common.Botania
import kotlin.math.*

class EntityIcicle: Entity {
	
	var accelerationX = 0f
	var accelerationY = 0f
	var accelerationZ = 0f
	
	var caster: EntityLivingBase? = null
	var target: EntityLivingBase? = null
	
	constructor(worldObj: World): super(worldObj) {
		setSize(0.1f, 0.1f)
		renderDistanceWeight = 10.0
	}
	
	constructor(world: World, x: Double, y: Double, z: Double, accX: Float, accY: Float, accZ: Float): this(world) {
		setLocationAndAngles(x, y, z, rotationYaw, rotationPitch)
		val d = sqrt(accX * accX + accY * accY + accZ * accZ).F
		accelerationX = accX / d * 0.1f
		accelerationY = accY / d * 0.1f
		accelerationZ = accZ / d * 0.1f
	}
	
	constructor(world: World, shooter: EntityLivingBase): this(world, shooter.posX, shooter.posY + shooter.eyeHeight, shooter.posZ, shooter.lookVec.xCoord.F, shooter.lookVec.yCoord.F, shooter.lookVec.zCoord.F) {
		caster = shooter
		setRotation(shooter.rotationYaw, shooter.rotationPitch)
	}
	
	override fun attackEntityFrom(source: DamageSource?, damage: Float) = false
	
	fun onImpact(mop: MovingObjectPosition?) {
		if (worldObj.isRemote || isDead) return
		if (mop?.entityHit === caster || mop?.entityHit is EntityIcicle) return
		val dmg = DamageSourceSpell.nifleice(caster)
//		if (rand.nextInt(10) == 0) dmg.setDamageBypassesArmor().setDamageIsAbsolute()
		mop?.entityHit?.attackEntityFrom(dmg, 5f)
		setDead()
	}
	
	override fun onUpdate() {
		if (isDead) return
		
		if (!worldObj.isRemote && caster?.isDead == true) return setDead()
		
		super.onUpdate()
		
		prevPosX = posX
		prevPosY = posY
		prevPosZ = posZ
		
		val vec3 = Vec3.createVectorHelper(posX, posY, posZ)
		val vec31 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ)
		var movingobjectposition: MovingObjectPosition? = worldObj.rayTraceBlocks(vec3, vec31)
		
		if (movingobjectposition == null) run {
			val l = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox.addCoord(motionX, motionY, motionZ).expand(0.1, 0.1, 0.1))
			l.remove(caster)
			l.forEach {
				if (!it.canBeCollidedWith() || CardinalSystem.PartySystem.mobsSameParty(caster, it)) return@forEach
				
				movingobjectposition = MovingObjectPosition(it)
				return@run
			}
		}
		
		if (movingobjectposition != null || (target == null && ticksExisted >= 100))
			return onImpact(movingobjectposition)
		
		val target = target
		
		if (target == null) {
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
		
	}
	
	// code from Forgotten Relics
	fun chase() {
		val targetList = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, this.boundingBox(0.5))
		
		if (targetList.contains(target)) {
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
		val d2 = 10 * 0.1
		motionX += dx / d * d2
		motionY += dy / d * d2
		motionZ += dz / d * d2
		motionX = MathHelper.clamp_double(motionX, -d2, d2)
		motionY = MathHelper.clamp_double(motionY, -d2, d2)
		motionZ = MathHelper.clamp_double(motionZ, -d2, d2)
		
		setPosition(posX, posY, posZ)
	}
	
	override fun canBeCollidedWith() = true
	
	override fun getCollisionBorderSize() = 0.1f
	
	@SideOnly(Side.CLIENT)
	override fun getShadowSize() = 0f
	
	override fun entityInit() = Unit
	override fun readEntityFromNBT(nbt: NBTTagCompound) = Unit
	override fun writeEntityToNBT(nbt: NBTTagCompound) = Unit
}
