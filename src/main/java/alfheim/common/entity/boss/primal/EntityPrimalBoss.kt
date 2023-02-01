package alfheim.common.entity.boss.primal

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ICustomArmSwingEndEntity
import alfheim.api.AlfheimAPI
import alfheim.api.boss.IBotaniaBossWithName
import alfheim.api.entity.IIntersectAttackEntity
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.*
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.entity.*
import alfheim.common.entity.ai.AIAttackOnIntersect
import alfheim.common.entity.boss.*
import alfheim.common.entity.boss.primal.ai.*
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.relauncher.*
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.*
import net.minecraft.entity.ai.*
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.Potion
import net.minecraft.stats.Achievement
import net.minecraft.util.*
import net.minecraft.world.*
import vazkii.botania.client.core.handler.BossBarHandler
import vazkii.botania.common.item.relic.ItemRelic
import java.awt.Rectangle
import kotlin.math.*

@Suppress("LeakingThis")
abstract class EntityPrimalBoss(world: World): EntityCreature(world), IBotaniaBossWithName, IIntersectAttackEntity, ICustomArmSwingEndEntity, IForceKill {
	
	private var maxHit = 1f
	private var lastHit = 0f
	private var hurtTimeActual = 0
	
	var stage = 1
		get() = max(1, field)
	
	var shield
		get() = dataWatcher.getWatchableObjectFloat(2)
		set(value) = dataWatcher.updateObject(2, max(0f, min(value, maxShield)))
	
	val maxShield get() = maxHealth / 2
	
	var whirl
		get() = getFlag(6)
		set(value) = setFlag(6, value)
	
	var isShooting // transient
		get() = getFlag(7)
		set(value) = setFlag(7, value)
	
	var shootingCD = 0
	var ultCD = 0
	var chunkAttackCounter = 0
	var ultsCounter = 0
	var meleeVSdistAttacks = 0
		set(value) {
			field = max(-10, min(value, 10))
		}
	var whirlTicks = 0
	var whirledDamage = 0f
	
	var supportersSet = Array(3) { true }
	
	var ultAnimationTicks
		get() = dataWatcher.getWatchableObjectInt(3)
		set(value) = dataWatcher.updateObject(3, value)
	
	var source
		get() = dataWatcher.getWatchableObjectChunkCoordinates(4)
		set(value) = dataWatcher.updateObject(4, value)
	
	private var posWatcher = HashMap<String, WatchedChunk>()
	
	// mutex bits:
	// 1st - motion
	// 2nd - look
	// 3rd - attack
	// 4th - special attack
	init {
		setSize(2f, 7f)
		
		tasks.addTask(2, PrimalAISuperSmash(this))
		tasks.addTask(2, PrimalAISpinning(this))
		
		tasks.addTask(3, PrimalAIRangedAttack(this))
		tasks.addTask(4, AIAttackOnIntersect(this))
		
		tasks.addTask(5, EntityAIMoveTowardsRestriction(this, 1.0).apply { mutexBits = 0b0001 })
		tasks.addTask(6, EntityAIWatchClosest(this, EntityPlayer::class.java, 32f).apply { mutexBits = 0b0010 })
		tasks.addTask(7, EntityAILookIdle(this).apply { mutexBits * 0b0010 })
		
		targetTasks.addTask(0, PrimalAISelectTarget(this, 64, 16))
		
		addRandomArmor()
	}
	
	override fun entityInit() {
		super.entityInit()
		
		dataWatcher.addObject(2, 0f) // shield
		dataWatcher.addObject(3, 0) // animation
		dataWatcher.addObject(4, ChunkCoordinates()) // source
	}
	
	override fun applyEntityAttributes() {
		super.applyEntityAttributes()
		
		val (f, k, m, h) = getAttributeValues()
		
		getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).baseValue = 0.5
		getEntityAttribute(SharedMonsterAttributes.followRange).baseValue = f
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).baseValue = k
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).baseValue = m
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = h
		
		stepHeight = 1.5f
	}
	
	abstract fun getAttributeValues(): DoubleArray
	
	override fun addRandomArmor() {
		getEquipment().forEachIndexed { id, it -> setCurrentItemOrArmor(id, ItemStack(it)) }
		
		equipmentDropChances.fill(0.05f)
	}
	
	abstract fun getEquipment(): Array<Item>
	
	abstract fun getRelics(): Array<Pair<Achievement, Item>>
	
	override fun onLivingUpdate() {
		updateArmSwingProgress()
		super.onLivingUpdate()
		extinguish()
		
		if (attackTarget?.isEntityAlive == false) attackTarget = null
		
		if (attackTarget != null) navigator.tryMoveToEntityLiving(attackTarget, getEntityAttribute(SharedMonsterAttributes.movementSpeed).attributeValue)
		
		val s = Vector3(source)
		if (Vector3.vecEntityDistance(s, this) > 100) {
			ASJUtilities.sendToDimensionWithoutPortal(this, AlfheimConfigHandler.dimensionIDDomains, s.x, s.y, s.z)
		}
		
		val arenaBB = arenaBB
		val players = playersOnArena(arenaBB)
		
		if (players.isEmpty() && ASJUtilities.isServer) return forceKill()
		
		players.forEach {
			if (!it.capabilities.isCreativeMode) it.capabilities.isFlying = false
		}
		
		--shootingCD
		--ultCD
		--hurtTimeActual
		
		if (!worldObj.isRemote) {
			tickWhirl(players)
			doChunkAttack(players)
			doElementalMarks(arenaBB)
		}
		
		if (stage > 1 && getEntitiesWithinAABB(worldObj, protectorEntityClass(), arenaBB).isNotEmpty()) {
			if (shield < maxShield) shield++
			else health += 0.25f
		}
	}
	
	abstract fun protectorEntityClass(): Class<*>
	
	open fun canUlt(): Boolean {
		if (ultCD > 0) return false
		if (!ASJUtilities.chance(playersOnArena().count { Vector3.entityDistance(this, it as EntityPlayerMP) <= 7 } * 20)) {
			ultCD = 100
			return false
		}
		
		return if (stage == 1) ultsCounter > 0 else true
	}
	
	override fun swingItem() {
		if (ultAnimationTicks > 0) return
		
		super.swingItem()
	}
	
	override fun setAttackTarget(target: EntityLivingBase?) {
		if (target != null && !canTarget(target)) return
		
		super.setAttackTarget(target)
	}
	
	override fun attackEntityAsMob(target: Entity): Boolean {
		if (ultAnimationTicks > 0) return false
		
		var damaged = false
		
		getEntitiesWithinAABB(worldObj, Entity::class.java, target.boundingBox(2)).forEach { around ->
			if (around === this) return@forEach
			if (!around.attackEntityFrom(defaultWeaponDamage(around), getEntityAttribute(SharedMonsterAttributes.attackDamage).attributeValue.F * if (stage > 1) 1.5f else 1f)) return@forEach
			damaged = true
			applyCustomWeaponDamage(around)
		}
		
		return damaged
	}
	
	abstract fun defaultWeaponDamage(target: Entity): DamageSource
	abstract fun applyCustomWeaponDamage(target: Entity)
	
	override fun attackEntityFrom(source: DamageSource, damage: Float): Boolean {
		val player = source.entity as? EntityPlayer ?: return false
		
		if (isShieldBreakingType(source) && whirl) return releaseWhirledEnergy(mod = 1)
		
		if ((source.damageType != "player" && source !is DamageSourceSpell) || !EntityFlugel.isTruePlayer(player) || invulnerable || hurtTimeActual > 0)  return false
		if (!player.capabilities.isCreativeMode && player.capabilities.disableDamage) return false
		if (getEntitiesWithinAABB(worldObj, protectorEntityClass(), arenaBB).isNotEmpty()) return false
		
		val crit = player.fallDistance > 0f && !player.onGround && !player.isOnLadder && !player.isInWater && !player.isPotionActive(Potion.blindness) && player.ridingEntity == null
		
		maxHit = if (player.capabilities.isCreativeMode) Float.MAX_VALUE else (if (crit) 60f else 40f) * if (isDamageTypeCritical(source)) 1.5f else 1f
		lastHit = min(maxHit, damage)
		
		if (isShieldBreakingType(source) && shield > 0) {
			shield -= lastHit
			recentlyHit = 60
			hurtTimeActual = 20
			return false
		}
		
		if (Vector3.entityDistance(player, this) > 5) run {
			if (whirl) {
				whirledDamage += lastHit
				return false
			}
			
			if (--meleeVSdistAttacks > -10) return@run
			
			meleeVSdistAttacks = 0
			whirl = true
			whirlTicks = 600
		} else ++meleeVSdistAttacks
		
		if (!ASJBitwiseHelper.getBit(ultAnimationTicks, 9) && ultAnimationTicks > 80) ultAnimationTicks = -100
		
		if (shield > 0) {
			if (!isDamageTypeCritical(source) && !player.capabilities.isCreativeMode) {
				lastHit = 0f
				maxHit = 0f
				
				return false
			} else {
				lastHit /= 2
				maxHit /= 2
			}
			
			shield -= lastHit
			if (shield >= 0) {
				lastHit = 0f
				maxHit = 0f
				
				return false
			} else {
				lastHit += shield
			}
		}
		
		return super.attackEntityFrom(source, lastHit)
	}
	
	/** This type can effectively decrease shield value */
	abstract fun isShieldBreakingType(type: DamageSource): Boolean
	
	/**
	 * This source can decrease shield value after all checks.
	 * This will also rise upper damage limit for 50%.
	 *
	 * The type will be "player" or from spell
	 */
	abstract fun isDamageTypeCritical(type: DamageSource): Boolean
	
	open fun tickWhirl(players: MutableList<EntityPlayer>) {
		if (--whirlTicks <= 0) releaseWhirledEnergy(players)
		else {
			shootingCD = 0
			navigator.clearPathEntity()
			health += 0.5f
			VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.WHIRL, dimension, posX, posY, posZ, whirlParticleSet.D)
		}
	}
	
	abstract val whirlParticleSet: Int
	
	fun releaseWhirledEnergy(players: MutableList<EntityPlayer> = playersOnArena(), mod: Int = 2): Boolean {
		if (!whirl) return false
		
		whirledDamage *= mod
		whirledDamage /= players.size
		
		players.forEach {
			it.attackEntityFrom(defaultWeaponDamage(it), whirledDamage)
		}
		
		whirledDamage = 0f
		whirlTicks = 0
		whirl = false
		
		return true
	}
	
	fun doChunkAttack(players: MutableList<EntityPlayer>) {
		for (player in players) {
			if (chunkAttackCounter < 1) break
			
			val pos = posWatcher[player.commandSenderName]
			
			if (pos != null && pos.x == player.chunkCoordX && pos.z == player.chunkCoordZ && pos.ticksIn++ <= 300) continue
			
			if ((pos?.ticksIn ?: 0) > 300) {
				if (worldObj.loadedEntityList.filterIsInstance<EntityPrimalBossChunkAttack>().any { player in it }) continue
				
				EntityPrimalBossChunkAttack(worldObj, this, player).spawn()
				--chunkAttackCounter
			}
			
			posWatcher[player.commandSenderName] = WatchedChunk(player.chunkCoordX, player.chunkCoordZ, 0)
		}
	}
	
	fun doElementalMarks(arenaBB: AxisAlignedBB) {
		if (ticksExisted % 600 != 0) return
		
		val j = 64
		var c = ASJUtilities.randInBounds(5, maxMarks, rand)
		
		var tries = 100
		while (tries-- > 0 && c > 0) {
			val i = ASJUtilities.randInBounds(arenaBB.minX.mfloor(), arenaBB.maxX.mfloor(), rand)
			val k = ASJUtilities.randInBounds(arenaBB.minZ.mfloor(), arenaBB.maxZ.mfloor(), rand)
			
			if (!worldObj.isAirBlock(i, j, k) || worldObj.isAirBlock(i, j - 1, k)) continue
			
			EntityPrimalMark(worldObj, i + 0.5, j + 0.1, k + 0.5, this).spawn()
			--c
			tries = 100
		}
	}
	
	override fun damageEntity(source: DamageSource, damage: Float) {
		super.damageEntity(source, lastHit)
	}
	
	override fun setHealth(set: Float) {
		prevHealth = health
		val hp = max(prevHealth - min(lastHit, maxHit), set)

		if (hp < prevHealth && hurtTimeActual > 0) return
		
		if (hp < prevHealth && maxHit != Float.MAX_VALUE) hurtTimeActual = 20
		super.setHealth(hp)
		
		lastHit = 0f
		maxHit = 0f
		
		if (health > maxHealth * 0.5) return
		
		val pf = prevHealth / maxHealth
		val cf = health / maxHealth
		
		val ids = playersOnArena().indices
		
		for (i in supportersSet.indices) {
			if ((0.1 * i + 0.2) !in cf..pf || !supportersSet[i]) continue
			for (`_` in ids) summonProtector()
			
			supportersSet[i] = false
		}
	}
	
	abstract fun summonProtector(): Any
	
	override fun setDead() {
		if (isAlive) return
		super.setDead()
	}
	
	override fun forceKill() {
		super.setDead()
	}
	
	override fun onDeath(source: DamageSource) {
		if (isAlive) return
		
		killer = source.entity as? EntityPlayer
		
		super.onDeath(source)
		
		if (!worldObj.isRemote && isFirstTime())
			doFirstTimeStuff()
		
		val (x, y, z) = Vector3.fromEntity(this).mf()
		
		worldObj.playBroadcastSound(1018, x, y, z, 0)
		worldObj.spawnParticle("hugeexplosion", posX, posY, posZ, 1.0, 0.0, 0.0)
	}
	
	abstract fun isFirstTime(): Boolean
	abstract fun doFirstTimeStuff()
	
	protected var killer: EntityPlayer? = null
	
	override fun dropFewItems(byPlayer: Boolean, looting: Int) {
		if (worldObj.isRemote || isAlive || !byPlayer) return
		
		dropItems()
	}
	
	abstract fun dropItems()
	
	override fun dropEquipment(byPlayer: Boolean, looting: Int) {
		if (worldObj.isRemote || isAlive || !byPlayer || isFirstTime()) return
		
		captureDrops = true
		super.dropEquipment(true, looting)
		captureDrops = false
		
		if (capturedDrops.isNotEmpty())
			capturedDrops.forEach(EntityItem::spawn)
		else {
			playersOnArena().shuffled().forEach { player ->
				val data = getRelics().firstOrNull { !player.hasAchievement(it.first) } ?: return@forEach
				val stack = ItemStack(data.second)
				
				player.triggerAchievement(data.first)
				ItemRelic.bindToPlayer(player, stack)
				entityDropItem(stack, 0f)
				return
			}
		}
	}
	
	override fun canBePushed() = false
	override fun canDespawn() = false
	override fun canPickUpLoot() = false
	override fun getCustomArmSwingAnimationEnd() = 10
	override fun getExtraReach() = 5.5
	override fun isAIEnabled() = true
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		super.writeEntityToNBT(nbt)
		
		nbt.setByteArray(TAG_SUPPORTERS, ByteArray(supportersSet.size) { if (supportersSet[it]) 1 else 0 })
		nbt.setInteger(TAG_SHOOTING_CD, shootingCD)
		nbt.setInteger(TAG_MELEE_VS_RANGED, meleeVSdistAttacks)
		nbt.setInteger(TAG_EPBCAC, chunkAttackCounter)
		nbt.setFloat(TAG_SHIELD, shield)
		nbt.setInteger(TAG_STAGE, stage)
		nbt.setInteger(TAG_ULT_CD, ultCD)
		nbt.setInteger(TAG_ULTS_COUNTER, ultsCounter)
		nbt.setInteger(TAG_ULT_ANIMATION_TICKS, ultAnimationTicks)
		nbt.setInteger(TAG_WHIRL_TICKS, whirlTicks)
		nbt.setFloat(TAG_WHIRL_DAMAGE, whirledDamage)
		
		val (x, y, z) = source
		nbt.setInteger(TAG_SRC_X, x)
		nbt.setInteger(TAG_SRC_Y, y)
		nbt.setInteger(TAG_SRC_Z, z)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		super.readEntityFromNBT(nbt)
		
		supportersSet = nbt.getByteArray(TAG_SUPPORTERS).map { it > 0 }.toTypedArray()
		shootingCD = nbt.getInteger(TAG_SHOOTING_CD)
		meleeVSdistAttacks = nbt.getInteger(TAG_MELEE_VS_RANGED)
		chunkAttackCounter = nbt.getInteger(TAG_EPBCAC)
		shield = nbt.getFloat(TAG_SHIELD)
		stage = nbt.getInteger(TAG_STAGE)
		source = ChunkCoordinates(nbt.getInteger(TAG_SRC_X), nbt.getInteger(TAG_SRC_Y), nbt.getInteger(TAG_SRC_Z))
		ultCD = nbt.getInteger(TAG_ULT_CD)
		ultsCounter = nbt.getInteger(TAG_ULTS_COUNTER)
		ultAnimationTicks = nbt.getInteger(TAG_ULT_ANIMATION_TICKS)
		whirlTicks = nbt.getInteger(TAG_WHIRL_TICKS)
		whirledDamage = nbt.getFloat(TAG_WHIRL_DAMAGE)
		whirl = whirlTicks > 0
	}
	
	fun canTarget(target: Entity) = !(target === this || target !is EntityLivingBase) && !isAllie(target)
	
	val isAlive get() = health > 0 && worldObj.difficultySetting != EnumDifficulty.PEACEFUL && !worldObj.isRemote
	val arenaBB get() = AlfheimAPI.domains[arenaName]!!.boundBox.copy().offset(source).offset(0, 0, 40)
	val maxMarks get() = if (stage > 1) 15 else 10
	
	abstract val arenaName: String
	
	fun playersOnArena(bb: AxisAlignedBB = arenaBB) = selectEntitiesWithinAABB(worldObj, EntityPlayer::class.java, bb) { canTarget(it) }
	
	abstract val shieldColor: UInt
	
	@SideOnly(Side.CLIENT)
	var barRect: Rectangle? = null
	
	@SideOnly(Side.CLIENT)
	var hpBarRect: Rectangle? = null
	
	@SideOnly(Side.CLIENT)
	override fun getBossBarTexture() = BossBarHandler.defaultBossBar!!
	
	@SideOnly(Side.CLIENT)
	override fun bossBarRenderCallback(res: ScaledResolution, x: Int, y: Int) = Unit
	
	abstract fun doRangedAttack(players: ArrayList<EntityPlayer>)
	abstract fun doSuperSmashAttack(target: EntityLivingBase)
	abstract fun doSpinningAttack(tick: Int)
	
	companion object {
		const val TAG_SHOOTING_CD = "shootingCD"
		const val TAG_MELEE_VS_RANGED = "attacksCounter"
		const val TAG_EPBCAC = "EPBCAC"
		const val TAG_SHIELD = "shield"
		const val TAG_SRC_X = "srcX"
		const val TAG_SRC_Y = "srcY"
		const val TAG_SRC_Z = "srcZ"
		const val TAG_STAGE = "stage"
		const val TAG_SUPPORTERS = "supporters"
		const val TAG_WHIRL_TICKS = "whirlTicks"
		const val TAG_WHIRL_DAMAGE = "whirlDamage"
		const val TAG_ULT_CD = "ultCD"
		const val TAG_ULT_ANIMATION_TICKS = "ultAnimationTicks"
		const val TAG_ULTS_COUNTER = "ultsCounter"
		
		fun summon(e: EntityPrimalBoss, x: Int, y: Int, z: Int, players: List<EntityPlayer>) {
			e.source = ChunkCoordinates(x, y + 1, z)
			e.setPosition(e.source, oX = 0.5, oZ = 0.5)
			
			e.chunkAttackCounter = players.size * 3
			e.ultsCounter = players.size * 2
			e.shield = e.maxShield
			
			e.forceSpawn = true
			
			e.spawn()
		}
	}
}

private fun AxisAlignedBB.offset(coord: ChunkCoordinates) = offset(coord.posX, coord.posY, coord.posZ)

data class WatchedChunk(val x: Int, val z: Int, var ticksIn: Int)