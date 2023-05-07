package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.core.helper.*
import alfheim.common.item.AlfheimItems
import alfheim.common.item.relic.ItemMjolnir
import net.minecraft.block.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.common.core.helper.ItemNBTHelper
import java.awt.Color

class EntityMjolnir: EntityThrowable {
	
	var bounced: Boolean
		get() = getFlag(6)
		set(bounced) = setFlag(6, bounced)
	
	var timesBounced: Int
		get() = dataWatcher.getWatchableObjectInt(3)
		set(times) {
			dataWatcher.updateObject(3, times)
		}
	
	var stack: ItemStack
		get() = dataWatcher.getWatchableObjectItemStack(4)
		set(stack) {
			dataWatcher.updateObject(4, stack)
		}
	
	constructor(world: World?): super(world)
	
	constructor(world: World?, e: EntityLivingBase?, stack: ItemStack): super(world, e) {
		this.stack = stack
	}
	
	override fun entityInit() {
		dataWatcher.addObject(3, 0)
		dataWatcher.addObject(4, ItemStack(AlfheimItems.mjolnir))
	}
	
	override fun onUpdate() {
		super.onUpdate()
		
		if (worldObj.isRemote) return
		
		val mx = motionX
		val my = motionY
		val mz = motionZ
		
		val bounces = timesBounced
		if (bounces >= MAX_BOUNCES || ticksExisted > 30) {
			val thrower = thrower
			noClip = true
			if (thrower == null)
				setDead()
			else {
				val motion = Vector3.fromEntityCenter(thrower).sub(Vector3.fromEntityCenter(this)).normalize()
				
				motionX = motion.x
				motionY = motion.y
				motionZ = motion.z
				
				if (Vector3.pointDistanceSpace(posX, posY, posZ, thrower.posX, thrower.posY, thrower.posZ) < 1) {
					if (thrower is EntityPlayer) {
						val slot = ASJUtilities.getSlotWithItem(AlfheimItems.mjolnir, thrower.inventory)
						
						if (slot != -1)
							thrower.inventory[slot]?.let { if (it.item === AlfheimItems.mjolnir) ItemNBTHelper.setInt(it, ItemMjolnir.TAG_COOLDOWN, 0) }
					}
					
					setDead()
				}
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
	
	override fun onImpact(pos: MovingObjectPosition) {
		val thrower = thrower
		
		if (pos.entityHit != null && pos.entityHit is EntityLivingBase && pos.entityHit !== thrower) {
			pos.entityHit.attackEntityFrom(
				when (thrower) {
					null            -> DamageSource.generic
					is EntityPlayer -> DamageSource.causePlayerDamage(thrower).setTo(ElementalDamage.ELECTRIC)
					else            -> DamageSource.causeMobDamage(thrower).setTo(ElementalDamage.ELECTRIC)
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
	
	companion object {
		
		private const val MAX_BOUNCES = 16
		
		val color = Color(0x0079C4).rgb
		val colorB = Color(0x0079C4).brighter().brighter().rgb
		
		val colorP = Color(0xc40058).rgb
		val colorBP = Color(0xc40058).brighter().brighter().rgb
	}
}