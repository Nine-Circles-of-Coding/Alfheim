package alfheim.common.entity

import alexsocol.asjlib.F
import alexsocol.asjlib.math.Vector3
import alfheim.common.core.helper.*
import alfheim.common.entity.boss.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.*
import net.minecraft.world.World
import vazkii.botania.common.Botania
import vazkii.botania.common.entity.EntityThrowableCopy

class EntitySniceBall: EntityThrowableCopy {
	
	constructor(world: World): super(world)
	constructor(world: World, thrower: EntityLivingBase): super(world, thrower)
	constructor(world: World, x: Double, y: Double, z: Double, thrower: EntityLivingBase? = null): super(world, x, y, z) {
		this.thrower = thrower
	}
	
	init {
		setSize(1f, 1f)
	}
	
	override fun onEntityUpdate() {
		super.onEntityUpdate()
		
		if (ticksExisted >= 200)
			return onImpact(null)
		
		if (!worldObj.isRemote)
			return
		
		if (thrower == null) {
			motionX = 0.0
			motionY = 0.0
			motionZ = 0.1
		}
		
		val v = Vector3()
		for (i in 0..1) {
			v.rand().sub(0.5).normalize().mul(Math.random() * 0.3 + 0.3)
			Botania.proxy.sparkleFX(worldObj, posX + v.x, posY + v.y + 0.25, posZ + v.z, (Math.random() * 0.25 + 0.25).F, 1f, 1f, 1f + Math.random().F * 0.25f, 10)
		}
	}
	
	override fun onImpact(mop: MovingObjectPosition?) {
		val target = mop?.entityHit as? EntityLivingBase
		
		if (target != null)
			if (target is EntityDedMoroz) {
				target.heal(3f)
			} else {
				val was = target.health
				target.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()).setTo(ElementalDamage.ICE), 5f)
				target.lastDamage = was - target.health
				target.attackEntityFrom(EntityDamageSourceIndirect("frost", this, thrower).setDamageBypassesArmor().setProjectile().setTo(ElementalDamage.ICE), (if (thrower is EntityFenrir) 3 else 1) * 2f)
			}
		
		for (i in 0..7)
			worldObj.spawnParticle("snowballpoof", posX, posY, posZ, 0.0, 0.0, 0.0)
		
		if (!worldObj.isRemote)
			setDead()
	}
}