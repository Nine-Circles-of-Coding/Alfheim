package alfheim.common.entity

import alexsocol.asjlib.D
import alexsocol.asjlib.math.Vector3
import alfheim.common.item.AlfheimItems
import net.minecraft.block.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.common.Botania
import java.awt.Color
import vazkii.botania.common.core.helper.Vector3 as Bectro3

class EntityMjolnir: EntityThrowable {
	
	var bounced = false
	
	var stack: ItemStack
	
	constructor(world: World?): super(world) {
		stack = ItemStack(AlfheimItems.mjolnir)
	}
	
	constructor(world: World?, e: EntityLivingBase?, stack: ItemStack): super(world, e) {
		this.stack = stack
	}
	
	override fun entityInit() {
		dataWatcher.addObject(30, 0)
		dataWatcher.setObjectWatched(30)
	}
	
	override fun onUpdate() {
		val mx = motionX
		val my = motionY
		val mz = motionZ
		
		super.onUpdate()
		
		Botania.proxy.lightningFX(worldObj, Bectro3.fromEntity(this), Bectro3.fromEntity(this).sub(Bectro3(mx, my, mz).multiply(1.25)), 1f, color, colorB)
		
		val bounces = timesBounced
		if (bounces >= MAX_BOUNCES || ticksExisted > 30) {
			val thrower = thrower
			noClip = true
			if (thrower == null)
				dropAndKill()
			else {
				val motion = Vector3.fromEntityCenter(thrower).sub(Vector3.fromEntityCenter(this)).normalize()
				motionX = motion.x
				motionY = motion.y
				motionZ = motion.z
				if (Vector3.pointDistanceSpace(posX, posY, posZ, thrower.posX, thrower.posY, thrower.posZ) < 1)
					if (!(thrower is EntityPlayer && thrower.inventory.addItemStackToInventory(stack.copy())))
						dropAndKill()
					else if (!worldObj.isRemote)
						setDead()
			}
		} else {
			if (!bounced) {
				motionX = mx
				motionY = my
				motionZ = mz
			}
			bounced = false
		}
	}
	
	private fun dropAndKill() {
		if (!worldObj.isRemote) {
			worldObj.spawnEntityInWorld(EntityItem(worldObj, posX, posY, posZ, stack.copy()))
			setDead()
		}
	}
	
	override fun onImpact(pos: MovingObjectPosition) {
		val thrower = thrower
		
		if (pos.entityHit != null && pos.entityHit is EntityLivingBase && pos.entityHit !== thrower) {
			pos.entityHit.attackEntityFrom(
				when (thrower) {
					null            -> DamageSource.generic
					is EntityPlayer -> DamageSource.causePlayerDamage(thrower)
					else            -> DamageSource.causeMobDamage(thrower)
				}, 12f)
		} else {
			if (noClip) return
			
			val block = worldObj.getBlock(pos.blockX, pos.blockY, pos.blockZ)
			if (block is BlockBush || block is BlockLeaves) return
			
			val bounces = timesBounced
			if (bounces < MAX_BOUNCES) {
				val currentMovementVec = Vector3(motionX, motionY, motionZ)
				val dir = ForgeDirection.getOrientation(pos.sideHit)
				val normalVector = Vector3(dir.offsetX.D, dir.offsetY.D, dir.offsetZ.D).normalize()
				val movementVec = normalVector.mul(-2 * currentMovementVec.dotProduct(normalVector)).add(currentMovementVec)
				motionX = movementVec.x
				motionY = movementVec.y
				motionZ = movementVec.z
				bounced = true
			}
		}
	}
	
	override fun getGravityVelocity(): Float {
		return 0f
	}
	
	override fun attackEntityFrom(dmg: DamageSource?, amount: Float) = false
	override fun isEntityInvulnerable() = true
	
	var timesBounced: Int
		get() = dataWatcher.getWatchableObjectInt(30)
		set(times) {
			dataWatcher.updateObject(30, times)
		}
	
	companion object {
		private const val MAX_BOUNCES = 2
		
		val color = Color(0x0079C4).rgb
		val colorB = Color(0x0079C4).brighter().brighter().rgb
	}
}