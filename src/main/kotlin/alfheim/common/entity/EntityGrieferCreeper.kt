package alfheim.common.entity

import alexsocol.asjlib.*
import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import vazkii.botania.common.entity.EntityManaStorm

class EntityGrieferCreeper(world: World): EntityCreeper(world) {
	
	private var lastActiveTime: Int = 0
	
	/** The amount of time since the creeper was close enough to the player to ignite  */
	private var timeSinceIgnited: Int = 0
	private var explosionRadius = 6
	private var fuseTime = 30
	
	override fun writeEntityToNBT(tag: NBTTagCompound) {
		super.writeEntityToNBT(tag)
		
		tag.setBoolean("powered", getFlag(6))
		tag.setInteger("Fuse", fuseTime)
		tag.setBoolean("ignited", func_146078_ca())
	}
	
	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	override fun readEntityFromNBT(tag: NBTTagCompound) {
		super.readEntityFromNBT(tag)
		setFlag(6, tag.getBoolean("powered"))
		
		if (tag.hasKey("Fuse", 99)) {
			fuseTime = tag.getInteger("Fuse")
		}
		
		if (tag.getBoolean("ignited")) {
			func_146079_cb()
		}
	}
	
	override fun onUpdate() {
		if (isEntityAlive) {
			this.lastActiveTime = this.timeSinceIgnited
			
			if (func_146078_ca()) {
				this.creeperState = 1
			}
			
			val i = this.creeperState
			
			if (i > 0 && this.timeSinceIgnited == 0) {
				playSound("creeper.primed", 1f, 0.5f)
			}
			
			this.timeSinceIgnited += i
			
			if (this.timeSinceIgnited < 0) {
				this.timeSinceIgnited = 0
			}
			
			if (this.timeSinceIgnited >= this.fuseTime) {
				this.timeSinceIgnited = this.fuseTime
				creeperGoBoom()
			}
		}
		super.onUpdate()
	}
	
	private fun creeperGoBoom() {
		if (worldObj.isRemote) return
		
		val flag = worldObj.gameRules.getGameRuleBooleanValue("mobGriefing")
		
		if (this.powered) {
			val storm = EntityManaStorm(worldObj)
			storm.setPosition(posX + 0.5, posY + 0.5, posZ + 0.5)
			storm.spawn()
		} else {
			worldObj.createExplosion(this, posX, posY, posZ, (this.explosionRadius).F, flag)
		}
		
		setDead()
	}
}
