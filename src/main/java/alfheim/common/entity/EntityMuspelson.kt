package alfheim.common.entity

import alexsocol.asjlib.*
import alfheim.api.entity.IMuspelheimEntity
import alfheim.common.core.helper.ElementalDamage
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.*
import net.minecraft.block.Block
import net.minecraft.entity.*
import net.minecraft.entity.ai.*
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityLargeFireball
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.*
import net.minecraft.util.DamageSource
import net.minecraft.world.World
import java.util.*

class EntityMuspelson(world: World): EntityMob(world), IMuspelheimEntity {
	
	var noLoot
		get() = getFlag(6)
		set(value) {
			setFlag(6, value)
			if (value) experienceValue = 0
		}
	
	override val elements = EnumSet.of(ElementalDamage.FIRE, ElementalDamage.EARTH)!!
	
	init {
		tasks.addTask(4, EntityAIAttackOnCollide(this, EntityLivingBase::class.java, 1.2, false))
		tasks.addTask(4, EntityAIAttackOnCollide(this, EntityPlayer::class.java, 1.2, false))
		tasks.addTask(4, EntityAIAttackOnCollide(this, EntityFireSpirit::class.java, 1.2, false))
		tasks.addTask(5, EntityAIWander(this, 1.0))
		tasks.addTask(6, EntityAIWatchClosest(this, EntityPlayer::class.java, 8f))
		tasks.addTask(6, EntityAILookIdle(this))
		targetTasks.addTask(1, EntityAIHurtByTarget(this, false))
		targetTasks.addTask(2, EntityAINearestAttackableTarget(this, EntityPlayer::class.java, 0, true))
		targetTasks.addTask(2, EntityAINearestAttackableTarget(this, EntityFireSpirit::class.java, 0, true))
		isImmuneToFire = true
		navigator.avoidsWater = true
		addRandomArmor()
		setSize(0.9f, 2.7f)
	}
	
	override fun applyEntityAttributes() {
		super.applyEntityAttributes()
		getEntityAttribute(SharedMonsterAttributes.attackDamage).baseValue = 4.0
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).baseValue = 0.75
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 60.0
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).baseValue = 0.25
	}
	
	override fun onLivingUpdate() {
		super.onLivingUpdate()
		
		if (rand.nextInt(100) != 0) return
		val target = attackTarget ?: entityToAttack as? EntityLivingBase ?: entityLivingToAttack ?: attackingPlayer ?: return
		
		if (!target.isEntityAlive) {
			attackTarget = null
			entityToAttack = null
			entityLivingToAttack = null
			attackingPlayer = null
			return
		}
		
		EntityLargeFireball(worldObj, this, 0.0, -1.0, 0.0).apply {
			field_92057_e = rand.nextInt(3) + 2
			setPosition(target, oY = 5.0)
		}.spawn()
	}
	
	override fun isAIEnabled() = true
	override fun getLivingSound() = "mob.blaze.breathe"
	override fun getHurtSound() = "mob.wither.hurt"
	override fun getDeathSound() = "mob.blaze.death"
	override fun func_145780_a(x: Int, y: Int, z: Int, block: Block) = playSound("mob.irongolem.walk", 1f, 1f)
	
	override fun attackEntityFrom(source: DamageSource, damage: Float): Boolean {
		if (source.isExplosion) return false
		
		return super.attackEntityFrom(source, damage)
	}
	
	override fun attackEntityAsMob(entity: Entity): Boolean {
		return if (super.attackEntityAsMob(entity)) {
			if (entity is EntityLivingBase && rand.nextBoolean())
				entity.addPotionEffect(PotionEffect(Potion.wither.id, 200))
			
			entity.setFire(5)
			true
		} else false
	}
	
	override fun updateRidden() {
		super.updateRidden()
		if (ridingEntity is EntityCreature)
			renderYawOffset = (ridingEntity as EntityCreature).renderYawOffset
	}
	
	override fun getExperiencePoints(player: EntityPlayer?): Int {
		return if (noLoot) 0 else super.getExperiencePoints(player)
	}
	
	override fun getDropItem() = if (noLoot) null else
		when (rng.nextInt(32)) {
			in 0..2   -> AlfheimItems.volcanoMace
			in 3..5   -> AlfheimItems.volcanoHelmet
			in 6..8   -> AlfheimItems.volcanoChest
			in 9..11  -> AlfheimItems.volcanoLeggings
			in 12..14 -> AlfheimItems.volcanoBoots
			in 15..26 -> Items.fire_charge!!
			in 27..31 -> Items.coal!! // actually nethercoal, changed below
			else      -> AlfheimItems.elvenResource
		}
	
	override fun dropFewItems(gotHit: Boolean, looting: Int) {
		if (noLoot || !gotHit) return
		
		var item = dropItem!!
		
		val meta = when (item) {
			AlfheimItems.elvenResource -> ElvenResourcesMetas.MuspelheimEssence.I
			Items.coal                 -> ElvenResourcesMetas.NetherwoodCoal.I
			Items.fire_charge          -> 0
			else                       -> (item.maxDamage - 1) / (looting + 1)
		}
		
		val size = 1 + when (item) {
			AlfheimItems.elvenResource -> looting / 2
			Items.fire_charge          -> looting * 2
			Items.coal                 -> looting
			else                       -> 0
		}
		
		if (item == Items.coal) item = AlfheimItems.elvenResource
		
		val stack = if (ASJUtilities.chance(5)) // 5%
			ItemStack(AlfheimItems.eventResource, 1, EventResourcesMetas.VolcanoRelic)
		else
			ItemStack(item, size, meta)
		
		entityDropItem(stack, 0f)
	}
	
	override fun addRandomArmor() {
		setCurrentItemOrArmor(0, ItemStack(AlfheimItems.volcanoMace))
		setCurrentItemOrArmor(1, ItemStack(AlfheimItems.volcanoBoots))
		setCurrentItemOrArmor(2, ItemStack(AlfheimItems.volcanoLeggings))
		setCurrentItemOrArmor(3, ItemStack(AlfheimItems.volcanoChest))
		setCurrentItemOrArmor(4, ItemStack(AlfheimItems.volcanoHelmet))
		equipmentDropChances.fill(0f)
	}
	
	override fun getYOffset() = super.getYOffset() - 0.5
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		super.readEntityFromNBT(nbt)
		
		noLoot = nbt.getBoolean("noLoot")
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		super.writeEntityToNBT(nbt)
		
		nbt.setBoolean("noLoot", noLoot)
	}
}