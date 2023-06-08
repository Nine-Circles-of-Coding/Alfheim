package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.*
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.*
import alfheim.common.core.helper.*
import alfheim.common.core.helper.ElementalDamage.*
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.entity.ai.AIAttackOnIntersect
import alfheim.common.entity.ai.elf.*
import alfheim.common.entity.spell.EntitySpellFenrirStorm
import alfheim.common.item.AlfheimItems
import alfheim.common.item.equipment.bauble.faith.*
import alfheim.common.spell.illusion.SpellShadowVortex
import alfheim.common.spell.wind.SpellFenrirStorm
import cpw.mods.fml.common.eventhandler.*
import net.minecraft.command.IEntitySelector
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.*
import net.minecraft.entity.ai.*
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityLargeFireball
import net.minecraft.init.*
import net.minecraft.item.*
import net.minecraft.nbt.*
import net.minecraft.potion.*
import net.minecraft.server.MinecraftServer
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.common.ISpecialArmor.ArmorProperties
import net.minecraftforge.event.entity.living.LivingDeathEvent
import java.util.*
import kotlin.math.abs

class EntityElf(world: World): EntityCreature(world), INpc, IIntersectAttackEntity, IElementalEntity {
	
	var job
		get() = EnumElfJob[dataWatcher.getWatchableObjectInt(3)]
		set(value) {
			dataWatcher.updateObject(3, value.ordinal)
			
			if (value.isMilitant)
				targetTasks.addTask(3, EntityAINearestAttackableTarget(this, EntityLiving::class.java, 0, false, true, IMob.mobSelector))
			else
				targetTasks.taskEntries.removeAll { it as EntityAITasks.EntityAITaskEntry; it.action is EntityAINearestAttackableTarget }
		}
	
	/**
	 * priest god type (0-5) / junkman (0) or regular merchant (1+)
	 */
	var jobSubrole
		get() = dataWatcher.getWatchableObjectInt(4)
		set(value) = dataWatcher.updateObject(4, value)
	
	var race: EnumRace
		get() = EnumRace[dataWatcher.getWatchableObjectInt(2)]
		set(value) = dataWatcher.updateObject(2, value.ordinal)
	
	var skillCooldown
		get() = dataWatcher.getWatchableObjectInt(5)
		set(value) = dataWatcher.updateObject(5, value)
	
	var interactor: EntityPlayer? = null
	
	var dialog: Dialog? = null
	
	init {
		setSize(0.6f, 2f)
		tasks.addTask(0, EntityAISwimming(this))
		tasks.addTask(1, EntityAIAttackOnCollide(this, 1.0, true))
		tasks.addTask(1, AIAttackOnIntersect(this))
		tasks.addTask(2, EntityAIMoveTowardsTarget(this, 0.9, 32f))
		tasks.addTask(3, EntityAIMoveThroughVillage(this, 0.6, true))
		tasks.addTask(4, EntityAIOpenDoor(this, true))
		tasks.addTask(5, EntityAIMoveTowardsRestriction(this, 1.0))
		tasks.addTask(6, EntityAISwimming(this))
		tasks.addTask(7, EntityAIWander(this, 0.6))
		tasks.addTask(8, EntityAIWatchClosest(this, EntityPlayer::class.java, 6f))
		tasks.addTask(8, EntityAIWatchClosest(this, EntityElf::class.java, 6f))
		tasks.addTask(9, EntityAILookIdle(this))
		targetTasks.addTask(2, EntityAIElfHurtByTarget(this))
		
		job = EnumElfJob.WILD
	}
	
	override fun isAIEnabled() = true
	
	override fun canDespawn() = job == EnumElfJob.WILD

	override fun entityInit() {
		super.entityInit()
		// Entity:
		// 0 - flags
		// 1 - air
		dataWatcher.addObject(2, 0) // race
		dataWatcher.addObject(3, 0) // job
		dataWatcher.addObject(4, -1) // priest type
		dataWatcher.addObject(5, 0) // skill cd
		// EntityLivingBase:
		// 6 - health
		// 7 - potion particle color
		// 8 - should draw opaque particles
		// 9 - arrow count
		// EntityLiving:
		// 10 - custom name
		// 11 - should always render name
	}
	
	override fun applyEntityAttributes() {
		super.applyEntityAttributes()
		
		getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage)
		getEntityAttribute(SharedMonsterAttributes.attackDamage).baseValue = 2.0
		
		getEntityAttribute(SharedMonsterAttributes.followRange).baseValue = 64.0
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).baseValue = 0.25
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 25.0 + rng.nextInt(15)
	}
	
	fun setGuard() {
		job = EnumElfJob.GUARD
	}
	
	fun setPriest(type: Int): EntityElf {
		targetTasks.addTask(1, EntityAINearestAttackableTarget(this, EntityPlayer::class.java, 0, false, false, RagnarSelector))
		
		jobSubrole = type
		job = EnumElfJob.PRIEST
		race = EnumRace.ALV
		
		setCurrentItemOrArmor(0, ItemStack(AlfheimItems.realitySword))
		setCurrentItemOrArmor(1, ItemStack(AlfheimItems.elvoriumBoots))
		setCurrentItemOrArmor(2, ItemStack(AlfheimItems.elvoriumLeggings))
		setCurrentItemOrArmor(3, ItemStack(AlfheimItems.elvoriumChestplate))
		setCurrentItemOrArmor(4, ItemStack(AlfheimItems.elvoriumHelmet))
		
		equipmentDropChances.fill(0f)
		
		val newHP = 60f + rng.nextInt(40)
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = newHP.D
		health = newHP
		
		return this
	}
	
	override fun interact(player: EntityPlayer): Boolean {
		if (worldObj.isRemote) return false
		
//		if (interactor != null && interactor !== player) {
//			ASJUtilities.say(player, "dialog.occupied")
//			return true
//		}
//
//		interactor = player
		
//		if (dialog == null)
//			dialog = EntityElfDialogLogic.launchDialog(this, player) // FIXME crashes in jar
//		else
//			dialog!!.onInteract(this, player)
		
		return true
	}
	
	override fun onLivingUpdate() {
//		if (interactor != null) {
//			ASJUtilities.faceEntity(this, interactor!!, 360f, 360f)
//			prevRotationYaw = rotationYaw
//			newRotationYaw = rotationYaw.D
//			renderYawOffset = rotationYaw
//			prevRenderYawOffset = rotationYaw
//			rotationYawHead = rotationYaw
//			prevRotationYawHead = rotationYaw
//		}
		
//		if (dialog != null) { navigator.clearPathEntity() }
		super.onLivingUpdate()
//		if (dialog != null) { navigator.clearPathEntity() }
		
		heal(0.02f)
		
		if (attackTarget?.isEntityAlive == false)
			attackTarget = null
		
		if (job == EnumElfJob.PRIEST)
			searchForPriestTargetAndAttack()
	}
	
	fun searchForPriestTargetAndAttack() {
		val target = attackTarget
		
		if (target == null) {
			skillCooldown = 0
			return
		}
		
		if (--skillCooldown > 0) {
			if (health < maxHealth * 0.5 && ASJUtilities.chance((1 - health/maxHealth) / 10)) {
				bringHeartsBack()
				setDead()
			}
			return
		}
		
		when (jobSubrole) {
			-1 -> Unit // TODO Einherjar ???
			
			0 -> {
				if (Vector3.entityDistance(this, target) > SpellFenrirStorm.radius - 1) return
				ASJUtilities.faceEntity(this, target, 360f, 360f)
				worldObj.spawnEntityInWorld(EntitySpellFenrirStorm(worldObj, this, true))
				skillCooldown = ASJUtilities.randInBounds(250, 350, rand)
			}
			
			1 -> {
				if (rand.nextInt(200) == 0 && target is EntityPlayer) target.capabilities.isFlying = false
				
				val (x, y, z) = Vector3.fromEntity(target).mf()
				val oxzs = MathHelper.ceiling_float_int(target.width) / 2
				val oye = MathHelper.ceiling_float_int(target.height)
				
				for (i in x.bidiRange(oxzs + 2))
					for (j in (y - 2)..(y + oye + 2))
						for (k in z.bidiRange(oxzs + 2)) {
							if (!worldObj.isAirBlock(i, j, k)) continue
							worldObj.setBlock(i, j, k, AlfheimBlocks.dirtDissolvable)
							worldObj.scheduleBlockUpdate(i, j, k, AlfheimBlocks.dirtDissolvable, AlfheimBlocks.dirtDissolvable.tickRate(worldObj))
						}
				
				skillCooldown = ASJUtilities.randInBounds(550, 650, rand)
			}
			
			2 -> {
				val (x, y, z) = Vector3.fromEntityCenter(target).mf()
				val yOff = target.eyeHeight.mfloor()
				
				if (!worldObj.isAirBlock(x, y + yOff, z)) return
				
				if (worldObj.isAirBlock(x, y + yOff - 1, z))
					worldObj.setBlock(x, y + yOff - 1, z, Blocks.web)
				
				worldObj.setBlock(x, y + yOff, z, Blocks.water)
				
				target.air = 0
				skillCooldown = ASJUtilities.randInBounds(350, 450, rand)
			}
			
			3 -> {
				val (i, j, k) = Vector3(lookVec).mul(3)
				val ball = EntityLargeFireball(worldObj, this, target.posX - posX, (target.posY + target.height / 2f) - (posY + height / 2f), target.posZ - posZ)
				ball.setPosition(ball.posX + i, ball.posY + j, ball.posZ + k)
				ball.spawn()
				skillCooldown = ASJUtilities.randInBounds(150, 250, rand)
			}
			
			4 -> {
				if (!worldObj.isRemote)
					for (i in 0..50)
						if (target.teleportRandomly(8.0)) {
							val (x, y, z) = Vector3.fromEntity(target)
							target.setPositionAndUpdate(x, y + ASJUtilities.randInBounds(8, 16, rand), z)
							target.attackEntityFrom(DamageSourceSpell.shadowSpell(this), SpellBase.over(this, SpellShadowVortex.damage.D))
							break
						}
				
				skillCooldown = ASJUtilities.randInBounds(250, 350, rand)
			}
			
			5 -> {
				target.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.maxHealth).apply {
					val prev = getModifier(entityUniqueID)
					if (prev != null) removeModifier(prev)
					if (getModifier(entityUniqueID) != null) return // crashfix :o
					var amount = (prev?.amount ?: 0.0) - 2
					if (abs(amount) >= baseValue) amount = -baseValue + 1
					applyModifier(AttributeModifier(entityUniqueID, "OdinPriestTookYourHeart", amount, 0))
					
					val back = entityData.getTagList(TAG_BRING_HEART_BACK, 8)
					back.appendTag(NBTTagString(target.commandSenderName))
					entityData.setTag(TAG_BRING_HEART_BACK, back)
				}
				
				heal(10f)
				skillCooldown = ASJUtilities.randInBounds(450, 550, rand)
			}
		}
	}
	
	fun bringHeartsBack() {
		if (worldObj.isRemote) return
		
		entityData.getTagList(TAG_BRING_HEART_BACK, 8).tagList.forEach {
			if (it !is NBTTagString) return@forEach
			val player = MinecraftServer.getServer().configurationManager.func_152612_a(it.func_150285_a_()) ?: return@forEach
			player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.maxHealth).apply {
				val prev = getModifier(entityUniqueID)
				if (prev != null) removeModifier(prev)
			}
		}
	}
	
	override fun isAllie(e: Entity?) = if (e is EntityElf) race == EnumRace.ALV || e.race == race else job != EnumElfJob.WILD && e is EntityPlayer && ItemRagnarokEmblem.getEmblem(e) == null

	override fun setAttackTarget(target: EntityLivingBase?) {
		if (!isAllie(target))
			super.setAttackTarget(target)
	}
	
	override fun attackEntityFrom(source: DamageSource, dmg: Float): Boolean {
		if (isAllie(source.entity) || source.damageType == DamageSource.fall.damageType) return false
		
		return super.attackEntityFrom(source, dmg)
	}
	
	override fun collideWithEntity(entity: Entity?) {
		if (entity is IMob && attackTarget == null && job.isMilitant)
			attackTarget = entity as? EntityLivingBase
		
		super.collideWithEntity(entity)
	}
	
	override fun attackEntityAsMob(target: Entity): Boolean {
		val source = DamageSource.causeMobDamage(this)
		var amount = getEntityAttribute(SharedMonsterAttributes.attackDamage).attributeValue.F
		var i = 0
		
		if (target is EntityLivingBase) {
			amount += EnchantmentHelper.getEnchantmentModifierLiving(this, target)
			i += EnchantmentHelper.getKnockbackModifier(this, target)
		}
		
		if (job == EnumElfJob.PRIEST) when (jobSubrole) {
			-1 -> Unit // TODO Einherjar ???
			0 -> { // Thor
				if (target is EntityLivingBase && !target.isPotionActive(Potion.moveSlowdown))
					target.addPotionEffect(PotionEffect(Potion.moveSlowdown.id, 75 + rand.nextInt(25), 1))
				
				FaithHandlerThor.traceLightning(this, target)
				source.setDamageBypassesArmor()
				amount /= 2
			}
			
			1 -> run { // Sif
				if (rand.nextBoolean()) return@run
				
				if (target is EntityLivingBase && !target.isPotionActive(Potion.poison))
					target.addPotionEffect(PotionEffect(Potion.poison.id, 25 + rand.nextInt(25), 1))
			}
			
			2 -> { // Njord
				target.motionY += 0.5
				VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.SPLASH, target)
			}
			
			3 -> { // Loki
				if (rand.nextInt(12) == 0 && target is EntityLivingBase)
					target.removePotionEffect(Potion.fireResistance.id)
				
				target.setFire(rand.nextInt(5) + 3)
			}
			
			4 -> { // Heimdall
				if (target is EntityLivingBase && !target.isPotionActive(Potion.blindness))
					target.addPotionEffect(PotionEffect(Potion.blindness.id, 100 + rand.nextInt(100), 1))
			}
			
			5 -> run { // Odin
				if (rand.nextBoolean() && target is EntityPlayer)
					target.addExhaustion(rand.nextFloat() * 2 + 3)
				
				if (rand.nextInt(20) != 0) return@run
				if (target !is EntityLivingBase) return@run
				
				if (target is EntityPlayer)
					target.dropOneItem(true)
				else {
					target.entityDropItem(target.heldItem ?: return@run, target.height / 2)
					target.setCurrentItemOrArmor(0, null)
				}
			}
		}
		
		if (!target.attackEntityFrom(source, amount)) return false
		
		if (i > 0) {
			target.addVelocity((-MathHelper.sin(rotationYaw * Math.PI.toFloat() / 180.0f) * i.toFloat() * 0.5f).toDouble(), 0.1, (MathHelper.cos(rotationYaw * Math.PI.toFloat() / 180.0f) * i.toFloat() * 0.5f).toDouble())
			motionX *= 0.6
			motionZ *= 0.6
		}
		
		val j = EnchantmentHelper.getFireAspectModifier(this)
		
		if (j > 0) {
			target.setFire(j * 4)
		}
		
		if (target is EntityLivingBase) {
			EnchantmentHelper.func_151384_a(target, this)
		}
		
		EnchantmentHelper.func_151385_b(this, target)
		
		return true
	}
	
	override val elements: EnumSet<ElementalDamage>
		get() = if (job == EnumElfJob.PRIEST) when (jobSubrole) {
			-1 -> EnumSet.of(LIGHTNESS)
			0 -> EnumSet.of(ELECTRIC)
			1 -> EnumSet.of(EARTH, NATURE)
			2 -> EnumSet.of(AIR, WATER)
			3 -> EnumSet.of(FIRE)
			4 -> EnumSet.of(PSYCHIC)
			5 -> EnumSet.of(LIGHTNESS)
			else -> throw IllegalArgumentException("Unknown priest type $jobSubrole")
		} else EnumSet.of(COMMON)
	
	fun getHackyEquip(): Array<ItemStack?> {
		val equip = Array<ItemStack?>(5) { lastActiveItems[it] }
		job.fillHackyEquip(equip)
		return equip
	}
	
	override fun getExtraReach() = if (job == EnumElfJob.PRIEST) 3.0 else 0.0
	
	override fun applyArmorCalculations(src: DamageSource, dmg: Float) = ArmorProperties.ApplyArmor(this, getHackyEquip(), src, dmg.D)
	
	override fun getTotalArmorValue() = getHackyEquip().sumOf { if (it?.item is ItemArmor) (it.item as ItemArmor).damageReduceAmount else 0 }
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		super.writeEntityToNBT(nbt)
		nbt.setInteger(TAG_COOLDOWN, skillCooldown)
		nbt.setInteger(TAG_JOB, job.ordinal)
		nbt.setInteger(TAG_JOBSUB, jobSubrole)
		nbt.setInteger(TAG_RACE, race.ordinal)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		super.readEntityFromNBT(nbt)
		dataWatcher.updateObject(2, nbt.getInteger(TAG_RACE))
		dataWatcher.updateObject(3, nbt.getInteger(TAG_JOB))
		jobSubrole = if (nbt.hasKey(TAG_JOBSUB)) nbt.getInteger(TAG_JOBSUB) else -1
		skillCooldown = nbt.getInteger(TAG_COOLDOWN)
		
		if (job == EnumElfJob.PRIEST) {
			targetTasks.addTask(1, EntityAINearestAttackableTarget(this, EntityPlayer::class.java, 0, false, false, RagnarSelector))
		}
	}
	
	override fun allowLeashing() = false
	
	companion object {
		
		const val TAG_COOLDOWN = "cooldown"
		const val TAG_JOB = "job"
		const val TAG_RACE = "race"
		const val TAG_JOBSUB = "jobsub"
		const val TAG_BRING_HEART_BACK = "bringHeartsBack"
		
		val RagnarSelector = IEntitySelector { target -> target is EntityPlayer && ItemRagnarokEmblem.getEmblem(target) != null }
		
		init {
			eventForge()
		}
		
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		fun protectFromEnvironment(e: SheerColdHandler.SheerColdTickEvent) {
			val elf = e.entityLiving as? EntityElf ?: return
			if (elf.job != EnumElfJob.PRIEST) return
			e.isCanceled = true
		}
		
		@SubscribeEvent
		fun bringBackHearts(e: LivingDeathEvent) {
			val elf = e.entityLiving as? EntityElf ?: return
			if (elf.job != EnumElfJob.PRIEST || elf.jobSubrole != 5) return
			elf.bringHeartsBack()
		}
	}
	
	enum class EnumElfJob(val isMilitant: Boolean) {
		WILD(true), // default, wild elf
		CITIZEN(false),
		MERCHANT(false),
		GUARD(true), // will attack nearby mobs
		PRAETOR(false),
		PRIEST(true); // will attack nearby mobs and ragnars if ragnarok is ongoing
		
		fun fillHackyEquip(equip: Array<ItemStack?>) {
			when (this) {
				WILD     -> {
					equip.forEachIndexed { id, it ->
						if (it != null) return@forEachIndexed
						
						val item = when (id) {
							4    -> Items.iron_helmet
							3    -> Items.leather_chestplate
							2    -> Items.leather_leggings
							1    -> Items.leather_boots
							else -> return@forEachIndexed
						}
						
						equip[id] = ItemStack(item)
					}
				}
//				GUARD    -> Unit
//				PRAETOR  -> Unit
				else     -> Unit
			}
		}
		
		companion object {
			operator fun get(i: Int) = if (i !in values().indices) CITIZEN else values()[i]
		}
	}
}

