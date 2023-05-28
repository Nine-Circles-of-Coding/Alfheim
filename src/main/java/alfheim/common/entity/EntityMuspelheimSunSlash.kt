package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.*
import alfheim.api.entity.IMuspelheimEntity
import alfheim.client.render.entity.RenderEntityMuspelheimSunSlash
import alfheim.common.core.util.DamageSourceSpell
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.*
import net.minecraft.world.World

class EntityMuspelheimSunSlash(world: World): Entity(world) {
	
	var rotation
		get() = dataWatcher.getWatchableObjectFloat(2)
		set(value) = dataWatcher.updateObject(2, value)
	
	init {
		setSize(1f, 1f)
	}
	
	override fun onEntityUpdate() {
		super.onEntityUpdate()
		
		if (worldObj.isRemote) return RenderEntityMuspelheimSunSlash.queueRender(this)
		
		if (ticksExisted > 100) return setDead()
		
		val s = ticksExisted * ticksExisted / 100.D + 1
		
		val all = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(128))
		if (all.isEmpty()) return
		
		val collided = ArrayList<EntityLivingBase>()
		for (a in 0 until 22) {
			val (i, j, k) = v.set(0, -1.2125 * s, 0).rotate(a * 11.25 - 39.375, Vector3.oX).rotate(rotation, Vector3.oY).add(this)
			obb.fromParams(0.2 * s, 0.2 * s, 0.25 * s).translate(0, -0.1 * s, 0).rotateLocal(-(a * 11.25 - 39.375), Vector3.oX).rotateLocal(-rotation, Vector3.oY).translate(i, j, k)
			val filter = all.filterTo(LinkedHashSet()) { obb.intersectsWith(it.boundingBox()) }
			collided += filter
			all -= filter
			
			if (all.isEmpty()) break
		}
		
		collided.forEach {
			if (it is IMuspelheimEntity)
				return@forEach it.heal(5f)
			
			if (it is EntityPlayer && it.health < it.maxHealth * 0.5) {
				if (!it.attackEntityFrom(DamageSourceSpell.soulburn, 0.1f)) return@forEach
				
				for (i in 0 until 5) EntityMuspelson(worldObj).apply {
					setPosition(it)
					spawn()
				}
			}
			
			it.attackEntityFrom(DamageSourceSpell.soulburn, 5f)
			it.addPotionEffect(PotionEffect(Potion.wither.id, 100, 2))
		}
	}
	
	override fun entityInit() {
		dataWatcher.addObject(2, 0f)
		dataWatcher.addObject(3, 0f)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		rotation = nbt.getFloat("rot")
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setFloat("rot", rotation)
	}
	
	companion object {
		private val obb = OrientedBB()
		private val v = Vector3()
	}
}
