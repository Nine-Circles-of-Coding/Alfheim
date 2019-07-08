package alfheim.common.entity

import alexsocol.asjlib.ASJUtilities
import net.minecraft.entity.*
import net.minecraft.entity.ai.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.Potion
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks

@Suppress("LeakingThis")
open class EntityRidable(world: World): EntityCreature(world) {
	
	var rider: EntityPlayer? = null
	var walkSpeed = 0.22f
	
	var owner: String
		get() = dataWatcher.getWatchableObjectString(15)
		set(owner) = dataWatcher.updateObject(15, owner)
	
	init {
		navigator.avoidsWater = true
		tasks.addTask(0, EntityAISwimming(this))
		tasks.addTask(2, EntityAIWander(this, 1.0))
		tasks.addTask(3, EntityAIWatchClosest(this, EntityPlayer::class.java, 6f))
		tasks.addTask(4, EntityAILookIdle(this))
	}
	
	override fun entityInit() {
		super.entityInit()
		dataWatcher.addObject(15, "")
	}
	
	override fun isAIEnabled() = true
	
	override fun applyEntityAttributes() {
		super.applyEntityAttributes()
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 35.0
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).baseValue = 0.25
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		super.writeEntityToNBT(nbt)
		nbt.setString("Owner", owner)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		super.readEntityFromNBT(nbt)
		owner = nbt.getString("Owner")
	}
	
	override fun playLivingSound() {
		if (rng.nextInt(8) == 1) {
			super.playLivingSound()
		}
	}
	
	override fun onLivingUpdate() {
		if (riddenByEntity != null && riddenByEntity is EntityPlayer) {
			rider = riddenByEntity as EntityPlayer
		} else {
			rider = null
		}
		if (rider != null && rider!!.isJumping) {
			jumpHelper.setJumping()
		}
		super.onLivingUpdate()
	}
	
	override fun canBeCollidedWith() = !isDead
	
	override fun canBePushed() = riddenByEntity == null
	
	override fun getShadowSize() = 0.0f
	
	override fun canDespawn() = false
	
	override fun interact(player: EntityPlayer?): Boolean {
		if (worldObj.isRemote) {
			return false
		}
		if (owner.isNotEmpty() && player?.commandSenderName != owner) {
			ASJUtilities.say(player, "Owned by $owner")
			return false
		}
		if (rider == null && player != null) {
			mount(player)
			if (riddenByEntity != null && riddenByEntity is EntityPlayer) {
				rider = riddenByEntity as EntityPlayer
			}
			return true
		}
		return false
	}
	
	override fun moveEntityWithHeading(mS: Float, mF: Float) {
		var par1 = mS
		var par2 = mF
		if (rider != null) {
			rotationYaw = rider!!.rotationYaw
			prevRotationYaw = rotationYaw
			rotationPitch = rider!!.rotationPitch * 0.5f
			setRotation(rotationYaw, rotationPitch)
			renderYawOffset = rotationYaw
			rotationYawHead = renderYawOffset
			par1 = rider!!.moveStrafing * 0.5f
			par2 = rider!!.moveForward
			if (!worldObj.isRemote) {
				aiMoveSpeed = walkSpeed
				super.moveEntityWithHeading(par1, par2)
			} else {
				super.moveEntityWithHeading(par1, par2)
			}
			prevLimbSwingAmount = limbSwingAmount
			val d0 = posX - prevPosX
			val d1 = posZ - prevPosZ
			var f4 = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0f
			if (f4 > 1.0f) {
				f4 = 1.0f
			}
			limbSwingAmount += (f4 - limbSwingAmount) * 0.4f
			limbSwing += limbSwingAmount
		} else {
			super.moveEntityWithHeading(par1, par2)
		}
	}
	
	override fun jump() {
		motionY = 0.5
		if (isPotionActive(Potion.jump)) {
			motionY += ((getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f).toDouble()
		}
		val f = rotationYaw * 0.017453292f
		motionX -= (MathHelper.sin(f) * 0.2f).toDouble()
		motionZ += (MathHelper.cos(f) * 0.2f).toDouble()
		
		isAirBorne = true
		ForgeHooks.onLivingJump(this)
	}
	
	private fun mount(player: EntityPlayer) {
		rotationYaw = player.rotationYaw
		rotationPitch = player.rotationPitch
		if (!worldObj.isRemote) {
			player.mountEntity(this)
		}
	}
	
	override fun shouldDismountInWater(rider: Entity?) = false
	
	override fun isSprinting() = if (rider == null) false else rider!!.moveForward > 0.5f
}