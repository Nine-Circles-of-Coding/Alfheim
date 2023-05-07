package alfheim.common.entity

import net.minecraft.entity.effect.EntityWeatherEffect
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

class FakeLightning(world: World): EntityWeatherEffect(world) {
	
	/** Declares which state the lightning bolt is in. Whether it's in the air, hit the ground, etc.  */
	var lightningState = 0
	/** A random long that is used to change the vertex of the lightning rendered in RenderLightningBolt  */
	var boltVertex: Long = 0
	/** Determines the time before the EntityLightningBolt is destroyed. It is a random integer decremented over time.  */
	var boltLivingTime = 0
	
	init {
		lightningState = 2
		boltVertex = rand.nextLong()
		boltLivingTime = rand.nextInt(3) + 1
	}
	
	override fun onUpdate() {
		super.onUpdate()
		
		if (lightningState == 2) {
			worldObj.playSoundEffect(posX, posY, posZ, "ambient.weather.thunder", 10000f, 0.8f + rand.nextFloat() * 0.2f)
			worldObj.playSoundEffect(posX, posY, posZ, "random.explode", 2f, 0.5f + rand.nextFloat() * 0.2f)
		}
		
		--lightningState
		
		if (lightningState >= 0 && worldObj.isRemote)
			worldObj.lastLightningBolt = 2
		
		if (lightningState >= 0) return
		if (boltLivingTime == 0) return setDead()
		
		if (lightningState < -rand.nextInt(10)) {
			--boltLivingTime
			lightningState = 1
			boltVertex = rand.nextLong()
		}
	}
	
	override fun entityInit() = Unit
	override fun readEntityFromNBT(tag: NBTTagCompound) = Unit
	override fun writeEntityToNBT(tag: NBTTagCompound) = Unit
}