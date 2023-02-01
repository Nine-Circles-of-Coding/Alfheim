package alfheim.common.entity

import net.minecraft.entity.Entity
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

class EntityLightningMark(world: World): Entity(world) {
	
	init {
		setSize(1.5f, 0.0001f)
	}
	
	constructor(world: World, x: Double, y: Double, z: Double): this(world) {
		setPosition(x, y, z)
	}
	
	override fun onEntityUpdate() {
		if (ticksExisted > 50) {
			if (!worldObj.isRemote)
				worldObj.addWeatherEffect(EntityLightningBolt(worldObj, posX, posY, posZ))
			
			setDead()
		}
	}
	
	override fun entityInit() = Unit
	override fun readEntityFromNBT(nbt: NBTTagCompound) = Unit
	override fun writeEntityToNBT(nbt: NBTTagCompound) = Unit
}