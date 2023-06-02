package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.*
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.entity.boss.primal.EntityPrimalBoss
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.entity.projectile.EntityLargeFireball
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

class EntityPrimalMark @JvmOverloads constructor(world: World, val summoner: EntityPrimalBoss? = null): Entity(world) {
	
	var isSpecial
		get() = getFlag(6)
		set(value) {
			if (value) setSize(18f, 0.0001f)
			setFlag(6, value)
		}
	
	var isIce
		get() = getFlag(7)
		set(value) = setFlag(7, value)
	
	init {
		setSize(5f, 0.0001f)
	}
	
	constructor(world: World, x: Double, y: Double, z: Double, summoner: EntityPrimalBoss?): this(world, summoner) {
		setPosition(x, y, z)
		isIce = summoner is INiflheimEntity
	}
	
	override fun onEntityUpdate() {
		val ice = isIce
		val lifetime = if (ice) 80 else 300
		
		if (ticksExisted >= lifetime) return setDead()
		
		val attack = ticksExisted == 50
		
		if (attack) {
			playSoundAtEntity(if (ice) "mob.zombie.remedy" else "fire.fire", 0.6f, 1f)
			
			val (x, y, z) = Vector3.fromEntity(this)
			if (!ice && !isSpecial && !worldObj.isRemote) EntityLargeFireball(worldObj).apply {
				// fuck you sideonly
				setLocationAndAngles(x, y + 10, z, 0f, 0f)
				setPosition(x, y + 10, z)
				accelerationY = -0.1
				spawn()
			}
		}
		
		if (ice && (isSpecial || !attack)) return
		if (!ice && ticksExisted < 70) return
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(1)).forEach {
			if (Vector3.entityDistancePlane(this, it) > width / 2 + it.width / 2 || (ice && it is INiflheimEntity) || (!ice && it is IMuspelheimEntity)) return@forEach
			
			val dmg = summoner?.let { s ->
				(if (ice) DamageSourceSpell.nifleice(s) else DamageSourceSpell.soulburn(s))
			} ?: if (ice) DamageSourceSpell.nifleice else DamageSourceSpell.soulburn
			
			it.attackEntityFrom(dmg, 5f)
		}
	}
	
	@SideOnly(Side.CLIENT)
	override fun setPositionAndRotation2(x: Double, y: Double, z: Double, yaw: Float, pitch: Float, nope: Int) {
		setPosition(x, y, z)
		setRotation(yaw, pitch)
		// fuck you "push out of blocks"!
	}
	
	override fun entityInit() = Unit
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		isIce = nbt.getBoolean(TAG_ICE)
		isSpecial = nbt.getBoolean(TAG_SPECIAL)
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setBoolean(TAG_ICE, isIce)
		nbt.setBoolean(TAG_SPECIAL, isSpecial)
	}
	
	companion object {
		const val TAG_ICE = "ice"
		const val TAG_SPECIAL = "special"
	}
}