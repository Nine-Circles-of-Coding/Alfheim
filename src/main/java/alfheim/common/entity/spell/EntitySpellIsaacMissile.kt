package alfheim.common.entity.spell

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.spell.sound.SpellIsaacStorm
import net.minecraft.entity.*
import net.minecraft.entity.monster.IMob
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World
import vazkii.botania.common.Botania
import vazkii.botania.common.entity.EntityThrowableCopy
import kotlin.math.abs

class EntitySpellIsaacMissile(world: World): EntityThrowableCopy(world) {
	
	var userSelected: Boolean
		get() = getFlag(6)
		set(value) = setFlag(6, value)
	
	var targetEntity: EntityLivingBase?
		get() {
			val id = dataWatcher.getWatchableObjectInt(26)
			val e = worldObj.getEntityByID(id)
			return if (e is EntityLivingBase) e else null
		}
		set(entity) {
			dataWatcher.updateObject(26, entity?.entityId ?: -1)
		}
	
	var targetClass: Class<*>?
		get() {
			val name = dataWatcher.getWatchableObjectString(27)
			if (name.isNullOrBlank()) return null
			val cached = Class.forName(name) ?: return null
			return if (EntityLivingBase::class.java.isAssignableFrom(cached)) cached else null
		}
		set(clazz) {
			clazz?.also { dataWatcher.updateObject(27, it.name) }
		}
	
	fun findTarget(): Boolean {
		var target = targetEntity
		
		if (target != null && target.health > 0 && !target.isDead && worldObj.loadedEntityList.contains(target))
			return true
		
		if (userSelected) {
			setDead()
			return false
		}
		
		val entities = getEntitiesWithinAABB(worldObj, targetClass ?: IMob::class.java, getBoundingBox(posX, posY, posZ).expand(SpellIsaacStorm.radius))
		while (entities.size > 0) {
			val e = entities[worldObj.rand.nextInt(entities.size)] as Entity
			if (e !is EntityLivingBase || e.isDead) {
				entities.remove(e)
				continue
			}
			
			target = e
			targetEntity = target
			break
		}
		
		return target != null
	}
	
	init {
		setSize(0f, 0f)
	}
	
	constructor(thrower: EntityLivingBase): this(thrower.worldObj) {
		this.thrower = thrower
	}
	
	override fun entityInit() {
		dataWatcher.addObject(26, 0)
		dataWatcher.addObject(27, "")
	}
	
	override fun onUpdate() {
		val lastTickPosX = lastTickPosX
		val lastTickPosY = lastTickPosY
		val lastTickPosZ = lastTickPosZ
		
		super.onUpdate()
		
		if (!worldObj.isRemote && (!findTarget() || ticksExisted > SpellIsaacStorm.duration)) {
			setDead()
			return
		}
		
		val thisVec = Vector3.fromEntityCenter(this)
		val oldPos = Vector3(lastTickPosX, lastTickPosY, lastTickPosZ)
		val diff = thisVec.copy().sub(oldPos)
		val step = diff.copy().normalize().mul(0.05)
		val steps = (diff.length() / step.length()).I
		val particlePos = oldPos.copy()
		
		for (i in 0 until steps) {
			Botania.proxy.sparkleFX(worldObj, particlePos.x, particlePos.y, particlePos.z, 1f, 0.4f, 1f, 0.8f, 2)
			if (worldObj.rand.nextInt(steps) <= 1)
				Botania.proxy.sparkleFX(worldObj, particlePos.x + (Math.random() - 0.5) * 0.4, particlePos.y + (Math.random() - 0.5) * 0.4, particlePos.z + (Math.random() - 0.5) * 0.4, 1f, 0.4f, 1f, 0.8f, 2)
			
			particlePos.add(step)
		}
		
		chaseTarget(this, targetEntity ?: return) { target ->
			target.hurtResistantTime = 0
			target.attackEntityFrom(DamageSourceSpell.missile(this, thrower), SpellIsaacStorm.damage)
			
			setDead()
		}
		
		if (ticksExisted < 10)
			motionY = abs(motionY)
	}
	
	override fun onImpact(pos: MovingObjectPosition) = Unit
	
	companion object {
		
		fun chaseTarget(entity: Entity, target: EntityLivingBase, onTargetCollide: (EntityLivingBase) -> Unit) {
			val thisVec = Vector3.fromEntityCenter(entity)
			val targetVec = Vector3.fromEntityCenter(target)
			val diffVec = targetVec.copy().sub(thisVec)
			val motionVec = diffVec.copy().normalize().mul(0.6)
			entity.motionX = motionVec.x
			entity.motionY = motionVec.y
			entity.motionZ = motionVec.z
			
			val targetList = getEntitiesWithinAABB(entity.worldObj, EntityLivingBase::class.java, getBoundingBox(entity.posX, entity.posY, entity.posZ).expand(0.5))
			if (targetList.contains(target))
				onTargetCollide(target)
		}
	}
}