package alfheim.common.entity.boss

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.AlfheimCore
import alfheim.api.boss.IBotaniaBossWithName
import alfheim.api.entity.INiflheimEntity
import alfheim.common.core.handler.*
import alfheim.common.core.handler.EventHandler.isMagical
import alfheim.common.core.helper.*
import alfheim.common.entity.EntitySniceBall
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.*
import alfheim.common.potion.PotionEternity
import cpw.mods.fml.relauncher.*
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.*
import net.minecraft.entity.ai.*
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.*
import net.minecraft.util.DamageSource
import net.minecraft.world.World
import vazkii.botania.client.core.handler.BossBarHandler
import java.awt.Rectangle
import kotlin.math.max

class EntityDedMoroz(world: World): EntityMob(world), IBotaniaBossWithName, INiflheimEntity {
	
	var noLoot: Boolean
		get() = getFlag(6)
		set(value) {
			setFlag(6, value)
			if (value) experienceValue = 0
		}
	
	init {
		setSize(1.5f, 4.5f)
		
		tasks.addTask(4, EntityAIAttackOnCollide(this, EntityLivingBase::class.java, 1.2, false))
		tasks.addTask(5, EntityAIWander(this, 1.0))
		tasks.addTask(6, EntityAIWatchClosest(this, EntityPlayer::class.java, 8f))
		tasks.addTask(6, EntityAILookIdle(this))
		
		targetTasks.addTask(1, EntityAIHurtByTarget(this, false))
		targetTasks.addTask(2, EntityAINearestAttackableTarget(this, EntityPlayer::class.java, 0, true))
		
		addRandomArmor()
	}
	
	constructor(world: World, x: Double, y: Double, z: Double): this(world) {
		setPosition(x, y, z)
	}
	
	override fun applyEntityAttributes() {
		super.applyEntityAttributes()
		getEntityAttribute(SharedMonsterAttributes.attackDamage).baseValue = 1.0
		getEntityAttribute(SharedMonsterAttributes.followRange).baseValue = 20.0
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).baseValue = 0.9
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).baseValue = 0.25
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 400.0
	}
	
	override fun isAIEnabled() = true
	
	override fun attackEntityAsMob(target: Entity): Boolean {
		return if (super.attackEntityAsMob(target) && target is EntityLivingBase) {
			target.addPotionEffect(PotionEffect(Potion.moveSlowdown.id, 200))
			true
		} else false
	}
	
	override fun damageEntity(src: DamageSource, amount: Float) {
		if (attackTarget == null && src.entity is EntityLivingBase) {
			attackTarget = src.entity as EntityLivingBase
			setRevengeTarget(attackTarget)
		}
		
		super.damageEntity(src, amount * if (src.isMagical) 0.1f else if (src.isFireDamage) 1.5f else 0.75f)
	}
	
	override fun getExperiencePoints(player: EntityPlayer?): Int {
		return if (noLoot) 0 else super.getExperiencePoints(player)
	}
	
	override fun getDropItem() = if (noLoot) null else
		when (rng.nextInt(30)) {
			in 0..2   -> AlfheimItems.snowSword
			in 3..5   -> AlfheimItems.snowHelmet
			in 6..8   -> AlfheimItems.snowChest
			in 9..11  -> AlfheimItems.snowLeggings
			in 12..14 -> AlfheimItems.snowBoots
			18        -> AlfheimItems.eventResource
			else      -> AlfheimItems.elvenResource
		}
	
	override fun dropFewItems(gotHit: Boolean, looting: Int) {
		if (noLoot || !gotHit) return
		
		val item = dropItem!!
		val size = 1 + when (item) {
			AlfheimItems.elvenResource -> looting * 2
			else                       -> 0
		}
		
		val meta = when (item) {
			AlfheimItems.elvenResource        -> ElvenResourcesMetas.IffesalDust.I
			AlfheimItems.eventResource        -> EventResourcesMetas.SnowRelic
			else                              -> (item.maxDamage - 1) / (looting + 1)
		}
		entityDropItem(ItemStack(item, size, meta), 0f)
	}
	
	override fun addRandomArmor() {
		setCurrentItemOrArmor(0, ItemStack(AlfheimItems.snowSword))
		setCurrentItemOrArmor(1, ItemStack(AlfheimItems.snowBoots))
		setCurrentItemOrArmor(2, ItemStack(AlfheimItems.snowLeggings))
		setCurrentItemOrArmor(3, ItemStack(AlfheimItems.snowChest))
		setCurrentItemOrArmor(4, ItemStack(AlfheimItems.snowHelmet))
		equipmentDropChances.fill(0f)
	}
	
	override fun onLivingUpdate() {
//		if (!WRATH_OF_THE_WINTER) setDead().also { return }
		
		super.onLivingUpdate()
		
		val iter = activePotionEffects.iterator()
		
		while (iter.hasNext()) if (Potion.potionTypes[(iter.next() as PotionEffect).potionID].isBadEffect) iter.remove()
		
		worldObj.worldInfo.isRaining = true
		worldObj.worldInfo.rainTime = max(worldObj.worldInfo.rainTime, 3600)
		worldObj.prevRainingStrength = 1f
		worldObj.rainingStrength = 1f
		
		if (AlfheimCore.winter)
			heal(if (attackTarget == null) 0.1f else 0.02f)
		
		if (attackTarget != null) {
			(attackTarget as? EntityPlayer)?.let {
				if (it.capabilities.isFlying) {
					it.capabilities.isFlying = false
					ASJUtilities.say(it, "alfheimmisc.ded.icebound")
					it.sendPlayerAbilities()
				}
			}
			
			val dist = Vector3.entityDistance(this, attackTarget)
			val follow = getAttributeMap().getAttributeInstance(SharedMonsterAttributes.followRange).attributeValue
			
			if (ticksExisted % 40 == 0 && 4 <= dist && dist <= follow) {
				if (!worldObj.isRemote) EntitySniceBall(worldObj, this).spawn()
			}
			
			if (ticksExisted % 600 == 0 && dist <= follow) {
				val v = Vector3()
				
				attackTarget.addPotionEffect(PotionEffect(Potion.moveSlowdown.id, 20, 4))
				
				for (i in 0..64) {
					if (!worldObj.isRemote) {
						v.rand().sub(0.5).normalize().mul(Math.random() * 16, Math.random() * 16, Math.random() * 16)
						EntitySniceBall(worldObj, attackTarget.posX + v.x, attackTarget.posY + v.y + 20, attackTarget.posZ + v.z, this).spawn()
					}
				}
				
				EntitySniceBall(worldObj, attackTarget.posX, attackTarget.posY + 8, attackTarget.posZ, this).spawn()
			}
		}
	}
	
	override fun setAttackTarget(target: EntityLivingBase?) {
		if (target is EntityPlayer && target.capabilities.disableDamage) return
		
		if (target != null && target != attackTarget) {
			if (target.isEntityInvulnerable) return
			
			ASJUtilities.faceEntity(target, this, 360f, 360f)
			
			target.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDEternity, 100, PotionEternity.STUN or PotionEternity.ATTACK))
		}
		
		super.setAttackTarget(target)
	}
	
	override fun getYOffset() = super.getYOffset() - 0.5
	
	override fun canDespawn() = !WRATH_OF_THE_WINTER
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		super.readEntityFromNBT(nbt)
		
		noLoot = nbt.getBoolean("noLoot")
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		super.writeEntityToNBT(nbt)
		
		nbt.setBoolean("noLoot", noLoot)
	}
	
	@SideOnly(Side.CLIENT)
	override fun getNameColor() = 0x0095FF
	
	@SideOnly(Side.CLIENT)
	override fun getBossBarTexture() = BossBarHandler.defaultBossBar!!
	
	@SideOnly(Side.CLIENT)
	override fun getBossBarTextureRect(): Rectangle {
		if (barRect == null)
			barRect = Rectangle(0, 88, 185, 15)
		return barRect!!
	}
	
	@SideOnly(Side.CLIENT)
	override fun getBossBarHPTextureRect(): Rectangle {
		if (hpBarRect == null)
			hpBarRect = Rectangle(0, 37, 181, 7)
		return hpBarRect!!
	}
	
	@SideOnly(Side.CLIENT)
	override fun bossBarRenderCallback(res: ScaledResolution, x: Int, y: Int) = Unit
	
	@SideOnly(Side.CLIENT)
	var barRect: Rectangle? = null
	
	@SideOnly(Side.CLIENT)
	var hpBarRect: Rectangle? = null
}