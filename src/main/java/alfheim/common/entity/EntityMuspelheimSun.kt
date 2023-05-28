package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.client.sound.EntityBoundMovingSound
import alfheim.common.core.util.DamageSourceSpell
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

class EntityMuspelheimSun(world: World?): Entity(world) {
	
	var size: Int
		get() = dataWatcher.getWatchableObjectInt(2)
		set(value) {
			dataWatcher.updateObject(2, value)
			slashes = maxSlashes
		}
	
	var slashes
		get() = dataWatcher.getWatchableObjectInt(3)
		set(value) = dataWatcher.updateObject(3, value)
	
	var timer
		get() = dataWatcher.getWatchableObjectInt(4)
		set(value) = dataWatcher.updateObject(4, value)
	
	val maxSlashes get() = 20 - (9 - size) * 2
	
	val radius get() = timer / 300.0
	
	init {
		setSize(18f, 18f)
	}
	
	lateinit var sunSound: EntityBoundMovingSound<EntityMuspelheimSun>
	
	private fun playSounds() {
		if (!ASJUtilities.isClient || (::sunSound.isInitialized && !sunSound.isDonePlaying))
			return
		
		sunSound = EntityBoundMovingSound(this, "${ModInfo.MODID}:surtr.sun.exist").apply { volume = 1f }
		mc.soundHandler.playSound(sunSound)
	}
	
	override fun onEntityUpdate() {
		super.onEntityUpdate()
		
		playSounds()
		
		if (worldObj.isRemote) return
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox).forEach {
			if (Vector3.entityDistance(this, it) <= radius) it.attackEntityFrom(DamageSourceSpell.soulburn, 5f)
		}
		
		if (timer % 300 == 0) if (--size <= 0) return setDead()
		
		if (timer % (300 / maxSlashes) == 0 && --slashes > 0) {
			EntityMuspelheimSunSlash(worldObj).apply {
				rotation = rand.nextFloat() * 360
				setPosition(this@EntityMuspelheimSun, oY = radius)
				spawn()
				playSoundAtEntity("${ModInfo.MODID}:surtr.sun.shot", 10f, 1f)
			}
		}
		
		if (--timer <= 0) setDead()
	}
	
	@SideOnly(Side.CLIENT)
	override fun setPositionAndRotation2(x: Double, y: Double, z: Double, yaw: Float, pitch: Float, nope: Int) {
		setPosition(x, y, z)
		setRotation(yaw, pitch)
		// fuck you "push out of blocks"!
	}
	
	override fun entityInit() {
		dataWatcher.addObject(2, 10)
		dataWatcher.addObject(3, 20)
		dataWatcher.addObject(4, 2700)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		if (nbt.hasKey("size")) size = nbt.getInteger("size")
		if (nbt.hasKey("slashes")) slashes = nbt.getInteger("slashes")
		if (nbt.hasKey("timer")) timer = nbt.getInteger("timer")
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setInteger("size", size)
		nbt.setInteger("slashes", slashes)
		nbt.setInteger("timer", timer)
	}
}
