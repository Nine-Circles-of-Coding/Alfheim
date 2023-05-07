package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.item.AlfheimItems
import net.minecraft.block.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.item.ItemStack
import net.minecraft.potion.*
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.common.Botania
import vazkii.botania.common.core.helper.MathHelper
import vazkii.botania.common.core.helper.Vector3 as Bector3

class EntityThunderChakram: EntityThrowable {
	
	private val MAX_BOUNCES = 16
	
	var timesBounced
		get() = dataWatcher.getWatchableObjectInt(30)
		set(times) {
			dataWatcher.updateObject(30, times)
		}
	
	val itemStack get() = ItemStack(AlfheimItems.thunderChakram)
	
	constructor(world: World): super(world)
	constructor(world: World, player: EntityPlayer): super(world, player)
	
	override fun entityInit() {
		dataWatcher.addObject(30, 0)
		dataWatcher.setObjectWatched(30)
	}
	
	override fun onUpdate() {
		val mx = motionX
		val my = motionY
		val mz = motionZ
		
		super.onUpdate()
		
		if (worldObj.isRemote && rand.nextInt(5) == 0) {
			val (i, j, k) = Vector3().rand().sub(0.5).normalize().mul(Math.random() * 0.5 + 0.5)
			Botania.proxy.lightningFX(worldObj, Bector3.fromEntity(this), Bector3.fromEntity(this).add(i, j, k), 0.5f, 0xFFDDFF, 0xAA44AA)
		}
		
		val thrower = getThrower()
		
		if (timesBounced < MAX_BOUNCES && ticksExisted <= 60) {
			if (timesBounced <= 0) {
				motionX = mx
				motionY = my
				motionZ = mz
			}
			
			getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(2)).forEach {
				if (it === thrower) return@forEach
				
				val damage = if (thrower != null) DamageSourceSpell.lightningIndirect(this, thrower) else DamageSourceSpell.lightning
				it.attackEntityFrom(damage, 8f)
				it.addPotionEffect(PotionEffect(Potion.moveSlowdown.id, 60))
				
				if (worldObj.isRemote)
					Botania.proxy.lightningFX(worldObj, Bector3.fromEntity(this), Bector3.fromEntity(it), 0.5f, 0xFFDDFF, 0xAA44AA)
			}
			
			return
		}
		
		noClip = true
		
		if (thrower == null) return dropAndKill()
		
		val motion = Vector3.fromEntityCenter(thrower).sub(Vector3.fromEntityCenter(this)).normalize()
		motionX = motion.x
		motionY = motion.y
		motionZ = motion.z
		
		if (MathHelper.pointDistanceSpace(posX, posY, posZ, thrower.posX, thrower.posY, thrower.posZ) >= 1) return
		
		if (thrower !is EntityPlayer || !thrower.capabilities.isCreativeMode && !thrower.inventory.addItemStackToInventory(itemStack))
			dropAndKill()
		else if (!worldObj.isRemote)
			setDead()
	}
	
	private fun dropAndKill() {
		if (worldObj.isRemote) return
		
		EntityItem(worldObj, posX, posY, posZ, itemStack).spawn()
		setDead()
	}
	
	override fun onImpact(pos: MovingObjectPosition) {
		if (noClip) return
		
		val block = worldObj.getBlock(pos.blockX, pos.blockY, pos.blockZ)
		if (block is BlockBush || block is BlockLeaves) return
		
		if (pos.entityHit != null) return
		if (timesBounced >= MAX_BOUNCES) return
		
		val currentMovementVec = Vector3(motionX, motionY, motionZ)
		val dir = ForgeDirection.getOrientation(pos.sideHit)
		val normalVector = Vector3(dir.offsetX.toDouble(), dir.offsetY.toDouble(), dir.offsetZ.toDouble()).normalize()
		val movementVec = normalVector.mul(-2 * currentMovementVec.dotProduct(normalVector)).add(currentMovementVec)
		motionX = movementVec.x
		motionY = movementVec.y
		motionZ = movementVec.z
		timesBounced++
	}
	
	override fun getGravityVelocity() = 0f
}
