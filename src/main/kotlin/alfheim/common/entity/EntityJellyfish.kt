package alfheim.common.entity

import alexsocol.asjlib.*
import alfheim.common.core.helper.*
import alfheim.common.item.material.ElvenFoodMetas
import alfheim.common.world.dim.alfheim.biome.BiomeRiver
import net.minecraft.block.material.Material
import net.minecraft.entity.*
import net.minecraft.entity.passive.EntityWaterMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.potion.*
import net.minecraft.util.*
import net.minecraft.world.World
import ru.vamig.worldengine.*
import kotlin.math.*

class EntityJellyfish(world: World): EntityWaterMob(world) {
	
	var jellyPitch = 0f
	var prevJellyPitch = 0f
	var jellyYaw = 0f
	var prevJellyYaw = 0f
	var jellyRoll = 0f
	var prevJellyRoll = 0f
	var tentacleAngle = 0f
	var lastTentacleAngle = 0f
	private var randomMotionSpeed = 0f
	private var rotationVelocity = 0f
	private var randomMotionVecX = 0f
	private var randomMotionVecY = 0f
	private var randomMotionVecZ = 0f
	
	override fun entityInit() {
		super.entityInit()
		setSize(0.95f, 0.95f)
		rotationVelocity = 1f / (rand.nextFloat() + 1f) * 0.2f
	}
	
	override fun applyEntityAttributes() {
		super.applyEntityAttributes()
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 8.0
	}
	
	override fun dropFewItems(wasHit: Boolean, looting: Int) {
		entityDropItem(ItemStack(Items.slime_ball), 0f)
	}
	
	override fun isInWater(): Boolean {
		return worldObj.handleMaterialAcceleration(boundingBox.expand(0.0, -0.6000000238418579, 0.0), Material.water, this)
	}
	
	override fun onEntityUpdate() {
		super.onEntityUpdate()
		
		if (isEntityAlive && !isInWater && air == -19) {
			air = 0
			attackEntityFrom(DamageSource.inWall, 1.9f)
		}
	}
	
	override fun onLivingUpdate() {
		super.onLivingUpdate()
		prevJellyPitch = jellyPitch
		prevJellyYaw = jellyYaw
		prevJellyRoll = jellyRoll
		lastTentacleAngle = tentacleAngle
		jellyRoll += rotationVelocity
		if (jellyRoll > Math.PI * 2) {
			jellyRoll -= Math.PI.F * 2f
			if (rand.nextInt(10) == 0)
				rotationVelocity = 1f / (rand.nextFloat() + 1f) * 0.2f
		}
		if (isInWater) {
			heal(0.1f)
			var f = 0f
			if (jellyRoll < Math.PI) {
				val d = jellyRoll / Math.PI
				tentacleAngle = (sin(d * d * Math.PI) * Math.PI * 0.25f).F
				if (d > 0.75) {
					randomMotionSpeed = 1f
					f = 1f
				} else
					f *= 0.8f
			} else {
				tentacleAngle = 0f
				randomMotionSpeed *= 0.9f
				f *= 0.99f
			}
			if (!worldObj.isRemote) {
				motionX = (randomMotionVecX * randomMotionSpeed).D
				motionY = (randomMotionVecY * randomMotionSpeed).D
				motionZ = (randomMotionVecZ * randomMotionSpeed).D
			}
			val d = sqrt(motionX * motionX + motionZ * motionZ)
			renderYawOffset += (-atan2(motionX, motionZ) * 180 / Math.PI - renderYawOffset).F * 0.1f
			rotationYaw = renderYawOffset
			jellyYaw += Math.PI.F * f * 1.5f
			jellyPitch += (-atan2(d, motionY) * 180 / Math.PI - jellyPitch).F * 0.1f
			
			getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(1)).forEach {
				if (it is EntityJellyfish) return@forEach
				
				if (it.attackEntityFrom(DamageSource.causeMobDamage(this).setTo(ElementalDamage.NATURE), 0.5f))
					it.addPotionEffect(PotionEffect(Potion.poison.id, 100, 3))
			}
			
		} else {
			tentacleAngle = abs(sin(jellyRoll)) * Math.PI.F * 0.25f
			if (!worldObj.isRemote) {
				motionX = 0.0
				motionY -= 0.08
				motionY *= 0.9800000190734863
				motionZ = 0.0
			}
			jellyPitch += (-90f - jellyPitch) * 0.02f
			
		}
	}
	
	override fun moveEntityWithHeading(strafe: Float, forward: Float) {
		moveEntity(motionX, motionY, motionZ)
	}
	
	override fun updateEntityActionState() {
		++entityAge
		
		if (entityAge > 100) {
			randomMotionVecZ = 0f
			randomMotionVecY = randomMotionVecZ
			randomMotionVecX = randomMotionVecY
		} else if (rand.nextInt(50) == 0 || !inWater || randomMotionVecX == 0f && randomMotionVecY == 0f && randomMotionVecZ == 0f) {
			val f = rand.nextFloat() * Math.PI.toFloat() * 2f
			randomMotionVecX = MathHelper.cos(f) * 0.2f
			randomMotionVecY = -0.1f + rand.nextFloat() * 0.2f
			randomMotionVecZ = MathHelper.sin(f) * 0.2f
		}
		
		despawnEntity()
	}
	
	override fun getCanSpawnHere(): Boolean {
		var flagBiome = false
		
		val chunk = (worldObj.provider as? WE_WorldProvider)?.cp
		if (chunk != null)
			flagBiome = WE_Biome.getBiomeAt(chunk, posX.mfloor().toLong(), posZ.mfloor().toLong()).isEqualTo(BiomeRiver)
		
		return flagBiome && isInsideOfMaterial(Material.water) && super.getCanSpawnHere()
	}
	
	override fun interact(player: EntityPlayer): Boolean {
		val stack = player.heldItem
		
		if (stack?.item !== Items.glass_bottle)
			return super.interact(player)
		
		if (!player.capabilities.isCreativeMode) {
			if (player.experienceLevel > 0) player.addExperienceLevel(-1)
			else player.addPotionEffect(PotionEffect(Potion.poison.id, 100))
		}
		
		val result = ElvenFoodMetas.JellyBottle.stack
		if (stack.stackSize-- == 1) {
			player.inventory.setInventorySlotContents(player.inventory.currentItem, result)
		} else if (!player.inventory.addItemStackToInventory(result)) {
			player.dropPlayerItemWithRandomChoice(result, false)
		}
		
		return true
	}
}