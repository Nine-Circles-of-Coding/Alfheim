package alfheim.common.entity

import alexsocol.asjlib.math.Vector3
import alfheim.client.render.world.SpellEffectHandlerClient.Spells
import alfheim.common.core.handler.SpellEffectHandler
import alfheim.common.item.AlfheimItems
import alfheim.common.item.relic.ItemMoonlightBow
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.World
import vazkii.botania.common.Botania
import vazkii.botania.common.entity.EntityThrowableCopy
import java.util.*
import kotlin.math.*

/**
 * @author ExtraMeteorP, CKATEPTb
 */
class EntityMagicArrow: EntityThrowableCopy {
	
	var damage: Float
		get() = dataWatcher.getWatchableObjectFloat(28)
		set(dmg) = dataWatcher.updateObject(28, dmg)
	
	var life: Int
		get() = dataWatcher.getWatchableObjectInt(29)
		set(delay) = dataWatcher.updateObject(29, delay)
	
	var rotation: Float
		get() = dataWatcher.getWatchableObjectFloat(30)
		set(rot) = dataWatcher.updateObject(30, rot)
	
	init {
		setSize(0f, 0f)
	}
	
	constructor(worldIn: World): super(worldIn)
	constructor(world: World, thrower: EntityLivingBase): super(world, thrower)
	
	override fun entityInit() {
		super.entityInit()
		dataWatcher.addObject(28, 0f)
		dataWatcher.addObject(29, 0)
		dataWatcher.addObject(30, 0f)
	}
	
	override fun getGravityVelocity() = 0f
	
	override fun onUpdate() {
		val thrower = thrower
		if (!worldObj.isRemote && (thrower == null || thrower !is EntityPlayer || thrower.isDead)) {
			setDead()
			return
		}
		
		super.onUpdate()

//		for (i in 0..5)
//			Botania.proxy.wispFX(worldObj, posX + Math.random() * 0.2 - 0.1, posY + Math.random() * 0.2 - 0.1, posZ + Math.random() * 0.2 - 0.1, 0.1f, 0.85f, 0.1f, 0.2f, 0f)
		
		val toMoon = damage == -1f
		val fromMoon = damage < 0f
		
		run {
			var r = 0.1f
			var g = 0.85f
			var b = 0.1f
			val or = r * if (fromMoon) 3 else 1
			val og = g
			val ob = if (fromMoon) g else b
			var size = (damage / (AlfheimItems.moonlightBow as ItemMoonlightBow).maxDmg) * 0.75f
			val osize = size
			val savedPosX = posX
			val savedPosY = posY
			val savedPosZ = posZ
			var currentPos = Vector3.fromEntity(this)
			val oldPos = Vector3(prevPosX, prevPosY, prevPosZ)
			var diffVec = oldPos.copy().sub(currentPos)
			val diffVecNorm = diffVec.copy().normalize()
			val distance = 0.095
			val rn = if (fromMoon) 0.1f else 0.25f
			
			do {
				r = or + (Math.random().toFloat() - 0.5f) * rn
				g = og + (Math.random().toFloat() - 0.5f) * rn
				b = ob + (Math.random().toFloat() - 0.5f) * rn
				size = osize + (Math.random().toFloat() - 0.5f) * 0.065f + sin(Random(entityUniqueID.mostSignificantBits).nextInt(9001).toDouble()).toFloat() * 0.4f
				Botania.proxy.wispFX(worldObj, posX, posY, posZ, r, g, b, 0.2f * size, (-motionX).toFloat() * 0.01f, (-motionY).toFloat() * 0.01f, (-motionZ).toFloat() * 0.01f)
				this.posX += diffVecNorm.x * distance
				this.posY += diffVecNorm.y * distance
				this.posZ += diffVecNorm.z * distance
				currentPos = Vector3.fromEntity(this)
				diffVec = oldPos.copy().sub(currentPos)
			} while (abs(diffVec.length()) > distance)
			
			Botania.proxy.wispFX(worldObj, posX, posY, posZ, r, g, b, 0.1f * size, (Math.random() - 0.5).toFloat() * 0.06f, (Math.random() - 0.5).toFloat() * 0.06f, (Math.random() - 0.5).toFloat() * 0.06f)
			posX = savedPosX
			posY = savedPosY
			posZ = savedPosZ
		}
		
		if (ticksExisted > life) {
			if (!worldObj.isRemote) {
				if (toMoon) {
					for (i in 1..66) {
						val arrow = EntityMagicArrow(worldObj, thrower)
						val yaw = (Math.random() * 360).toFloat()
						arrow.shoot(thrower, 90f, yaw, 0f, 5f, 1f)
						arrow.damage = -20f
						arrow.rotationYaw = thrower.rotationYaw
						arrow.rotation = MathHelper.wrapAngleTo180_float(yaw)
						arrow.life = 256
						
						arrow.setPosition(thrower.posX, 256.0, thrower.posZ)
						
						worldObj.spawnEntityInWorld(arrow)
					}
				}
			}
			
			setDead()
			return
		}
		
		if (!worldObj.isRemote) {
			val player = thrower as EntityPlayer?
			
			val axis = AxisAlignedBB.getBoundingBox(min(posX, lastTickPosX), min(posY, lastTickPosY), min(posZ, lastTickPosZ), max(posX, lastTickPosX), max(posY, lastTickPosY), max(posZ, lastTickPosZ)).expand(0.5, 0.5, 0.5)
			
			var entities = worldObj.getEntitiesWithinAABB(EntityLivingBase::class.java, axis) as List<EntityLivingBase>
			for (living in entities) {
				if (living === player) continue
				if (toMoon) {
					axis.expand(5.0, 5.0, 5.0)
					entities = worldObj.getEntitiesWithinAABB(EntityLivingBase::class.java, axis) as List<EntityLivingBase>
					entities.forEach {
						attackedFrom(it, player, 66f)
					}
					
					SpellEffectHandler.sendPacket(Spells.MOON, this)
					
					setDead()
					break
				}
				val attribute = player?.getEntityAttribute(SharedMonsterAttributes.attackDamage)?.attributeValue ?: 0.0
				if (living.hurtTime == 0) {
					attackedFrom(living, player, damage + attribute.toFloat())
				}
			}
		}
	}
	
	fun attackedFrom(target: EntityLivingBase, player: EntityPlayer?, dmg: Float) {
		val d = abs(dmg)
		if (player != null)
			target.attackEntityFrom(DamageSource.causePlayerDamage(player), d)
		else
			target.attackEntityFrom(DamageSource.generic, d)
	}
	
	override fun onImpact(pos: MovingObjectPosition) = Unit // NO-OP
	
	override fun writeEntityToNBT(cmp: NBTTagCompound) {
		super.writeEntityToNBT(cmp)
		cmp.setFloat(TAG_DAMAGE, damage)
		cmp.setInteger(TAG_LIFE, life)
		cmp.setFloat(TAG_ROTATION, rotation)
	}
	
	override fun readEntityFromNBT(cmp: NBTTagCompound) {
		super.readEntityFromNBT(cmp)
		damage = cmp.getFloat(TAG_DAMAGE)
		life = cmp.getInteger(TAG_LIFE)
		rotation = cmp.getFloat(TAG_ROTATION)
	}
	
	companion object {
		
		const val TAG_DAMAGE = "damage"
		const val TAG_LIFE = "life"
		const val TAG_ROTATION = "rotation"
		
	}
}
