package alfheim.common.entity.boss

import alexsocol.asjlib.*
import alexsocol.asjlib.math.*
import alfheim.api.ModInfo
import alfheim.api.boss.IBotaniaBossWithName
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.achievement.AlfheimAchievements
import alfheim.common.block.tile.*
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.util.*
import alfheim.common.entity.EntitySniceBall
import alfheim.common.entity.boss.EntityFlugel.Companion.isRecordPlaying
import alfheim.common.entity.boss.EntityFlugel.Companion.playRecord
import alfheim.common.entity.boss.EntityFlugel.Companion.stopRecord
import alfheim.common.entity.boss.ai.fenrir.*
import alfheim.common.entity.boss.ai.fenrir.EntityAIFenrirLeapAtTarget.Companion.leapTo
import alfheim.common.entity.spell.*
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
import alfheim.common.world.dim.domains.gen.FenrirDomain
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.*
import net.minecraft.entity.ai.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.*
import vazkii.botania.client.core.handler.BossBarHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.item.relic.ItemRelic
import java.awt.Rectangle
import kotlin.math.*

class EntityFenrir(world: World): EntityCreature(world), IBotaniaBossWithName {
	
	// data
	var bladesCooldown = 0
	var dashCooldown = 0
	var iceShowerCooldown = 0
	var leapCooldown = 0
	var roarCooldown = 0
	var slashCooldown = 0
	var spinCooldown = 0
	var stormCooldown = 0
	
	var spinStartYaw: Float
		get() = dataWatcher.getWatchableObjectFloat(2)
		set(value) = dataWatcher.updateObject(2, value)
	
	var stage: Int
		get() = dataWatcher.getWatchableObjectInt(3)
		set(value) = dataWatcher.updateObject(3, value)
	
	var source: ChunkCoordinates
		get() = dataWatcher.getWatchableObjectChunkCoordinates(4)
		set(value) = dataWatcher.updateObject(4, value)
	
	// render
	var isDripping = false
	var isShaking = false
	var timeWolfShaking = 0f
	var prevTimeWolfShaking = 0f
	
	// reusables
	val ov = Vector3()
	val main = OrientedBB()
	
	init {
		setSize(3f, 4f)
		navigator.avoidsWater = true
		tasks.addTask(1, EntityAISwimming(this))
		tasks.addTask(2, EntityAIFenrirDashAtTarget(this))
		tasks.addTask(3, EntityAIFenrirLeapAtTarget(this))
		tasks.addTask(4, EntityAIAttackOnCollide(this, 1.0, true))
		tasks.addTask(7, EntityAIWander(this, 1.0))
		tasks.addTask(9, EntityAIWatchClosest(this, EntityPlayer::class.java, 8f))
		tasks.addTask(9, EntityAILookIdle(this))
		targetTasks.addTask(1, EntityAINearestAttackableTarget(this, EntityPlayer::class.java, 0, false))
		targetTasks.addTask(3, EntityAIHurtByTarget(this, true))
	}
	
	override fun entityInit() {
		super.entityInit()
		
		dataWatcher.addObject(2, 0f) // spin yaw
		dataWatcher.addObject(3, 0) // stage
		dataWatcher.addObject(4, ChunkCoordinates()) // spawn
	}
	
	override fun applyEntityAttributes() {
		super.applyEntityAttributes()
		getEntityAttribute(SharedMonsterAttributes.followRange).baseValue = 48.0
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).baseValue = 1.0
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).baseValue = 0.5
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 3000.0
		stepHeight = 5f
	}
	
	override fun onLivingUpdate() {
		super.onLivingUpdate()
		
		val (x, y, z) = Vector3(source).add(0.5)
		val (sx, sy, sz) = Vector3(source).mf()
		
		if (ASJUtilities.isClient && !isDead && !worldObj.isRecordPlaying(sx, sy, sz))
			worldObj.playRecord(AlfheimItems.discFenrir as ItemRecord, sx, sy, sz)
		
		if (stage == 0 && health <= maxHealth / 2) {
			stage = 1
			leapTo(Vector3(x, y, z))
		}
		
		if (stage > 0) {
			if (onGround) {
				setPositionAndUpdate(x, y - 0.5, z)
				navigator.clearPathEntity()
			}
			
			val v = Vector3()
			repeat(100) {
				val (i, j, k) = v.rand().sub(0.5).mul(1, 0, 1).normalize().mul(Math.random() * 48).extend(15).add(x, y, z)
				Botania.proxy.sparkleFX(worldObj, i, j, k, 0.1f, 0.835f, 1f, Math.random().F * 3f + 1f, 20)
			}
			
			run {
				if (Vector3.entityDistance(this, attackTarget ?: return@run) < 6) return@run
				attackEntityAsMob(attackTarget)
			}
		}
		
		clearActivePotions()
		updateSpinningSlash()
		updateIceShower()
		updateRoar()
		updateCDs()
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, getBoundingBox(x, y + 15, z).expand(63, 15, 63)).forEach {
			if (it is EntityPlayer) if (!it.capabilities.isCreativeMode) it.capabilities.isFlying = false else return@forEach
			if (it !== this && it.boundingBox?.intersectsWith(boundingBox.expand(1)) == true) it.knockback(this, 2f)
			if (stage > 0 && Vector3.entityDistancePlane(this, it) > 15) it.attackEntityFrom(DamageSourceSpell.nifleice, 1f)
		}
	}
	
	override fun collideWithEntity(entity: Entity) {
		super.collideWithEntity(entity)
		
		if (dashCooldown <= EntityAIFenrirDashAtTarget.COOLDOWN - 20) return
		if (!entity.attackEntityFrom(DamageSourceSpell.frost(this), 7.5f)) return
		entity.knockback(this, 2f)
		playSound("${ModInfo.MODID}:fenrir.attack", 1f, 1f)
	}
	
	override fun attackEntityAsMob(target: Entity): Boolean {
		if (isBusy) return false
		
		ASJUtilities.faceEntity(this, target, 360f, 360f)
		
		val near = Vector3.entityDistance(this, target) < 6
		
		val chance = rand.nextInt(10)
		if (near && chance == 0 && startSpinningSlash())
			return true
		
		if (stage > 0 && trySpecialAttack(target, chance))
			return true
		
		return near && doWolfSlash(target)
	}
	
	fun trySpecialAttack(target: Entity, chance: Int): Boolean {
		when (chance) {
			1 -> {
				if (iceShowerCooldown > 0) return false
				iceShowerCooldown = SHOWER_CD + SHOWER_DUR
				isDripping = true
			}
			2 -> {
				if (roarCooldown > 0) return false
				roarCooldown = ROAR_CD + ROAR_DUR
			}
			3 -> {
				if (bladesCooldown > 0) return false
				bladesCooldown = BLADES_CD
				
				val prevYaw = rotationYaw
				
				rotationYaw -= 30f
				for (i in 0..2) {
					if (!worldObj.isRemote)
						EntitySpellWindBlade(worldObj, this, -3.0).spawn()
					
					rotationYaw += 30f
				}
				rotationYaw = prevYaw
			}
			4 -> {
				if (stormCooldown > 0) return false
				if (Vector3.entityDistance(this, target) > 24) return false
				
				stormCooldown = STORM_CD
				
				EntitySpellFenrirStorm(worldObj, this, true).spawn()
			}
			else -> return false
		}
		
		return true
	}
	
	fun doWolfSlash(target: Entity?, isSpinning: Boolean = false): Boolean {
		if (!isSpinning && slashCooldown > 0) return false
		
		val list = ArrayList<Entity>()
		
		ov.set(0, 0, 3.6).rotateOY(-renderYawOffset)
		main.fromParams(6, 3, 4).translate(ov.x, 2.5, ov.z).rotateOY(-renderYawOffset.D).translate(posX, posY, posZ)
		list += getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, main.toAABB()).filter { main.intersectsWith(it.boundingBox()) }
		
		ov.set(3, 0, 2.8).rotateOY(-renderYawOffset)
		main.fromParams(4, 3, 4).translate(ov.x, 2.5, ov.z).rotateOY(-renderYawOffset.D + 45).translate(posX, posY, posZ)
		list += getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, main.toAABB()).filter { main.intersectsWith(it.boundingBox()) }
		
		ov.set(-3, 0, 2.8).rotateOY(-renderYawOffset)
		main.fromParams(4, 3, 4).translate(ov.x, 2.5, ov.z).rotateOY(-renderYawOffset.D - 45).translate(posX, posY, posZ)
		list += getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, main.toAABB()).filter { main.intersectsWith(it.boundingBox()) }
		
		if (!isSpinning) list.add(target!!)
		list.remove(this)
		
		val count = list.count {
			val did = it.attackEntityFrom(DamageSource.causeMobDamage(this).setDamageBypassesArmor(), 5f)
			if (did) it.knockback(this, 1f)
			did
		}
		if (count <= 0) return false
		
		if (!isSpinning) {
			heal(count * 2f)
			slashCooldown = 50
			playSound("${ModInfo.MODID}:fenrir.attack", 1f, 1f)
		}
		
		return true
	}
	
	fun startSpinningSlash(): Boolean {
		if (spinCooldown > 0) return false
		spinCooldown = 300
		
		if (!worldObj.isRemote)
			VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.FENRIR_AREA, this)
		
		return true
	}
	
	fun updateSpinningSlash() {
		--spinCooldown
		
		if (spinCooldown == 280) spinStartYaw = renderYawOffset
		
		if (spinCooldown in 270..280) {
			prevRotationYaw = rotationYaw
			rotationYaw = (280 - spinCooldown) * 36 + spinStartYaw
			newRotationYaw = (280 - spinCooldown - 1) * 36 + spinStartYaw.D
			if (rotationYaw > 360) rotationYaw -= 360
			
			prevRenderYawOffset = renderYawOffset
			renderYawOffset = (280 - spinCooldown) * 36 + spinStartYaw
			if (renderYawOffset > 360) rotationYaw -= 360
			
			doWolfSlash(null, true)
		}
	}
	
	fun updateIceShower() {
		--iceShowerCooldown
		
		if (!isShowering || rand.nextInt(10) != 0) return

		val (x, y, z) = if (rand.nextInt(3) != 0 && attackTarget != null) {
			when (rand.nextInt(5)) {
				0    -> Vector3().rand().sub(0.5).mul(3, 0, 3).add(attackTarget)
				1, 2 -> Vector3.fromEntity(attackTarget).add(attackTarget.motionX, 0, attackTarget.motionZ)
				else -> Vector3.fromEntity(attackTarget)
			}
		} else {
			Vector3().rand().sub(0.5).mul(6).add(this)
		}.add(0, 16, 0)
		
		if (worldObj.isRemote) return
		
		EntitySniceBall(worldObj, x, y, z, this).apply { setMotion(0.0, -1.0, 0.0) }.spawn()
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.SNICE_MARK, dimension, x, y, z)
	}
	
	fun updateRoar() {
		--roarCooldown
		
		if (!isRoaring) return
		
		val list = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(16))
		list.remove(this)
		
		for (e in list) {
			val power = ov.set(e).sub(this).length()
			val (mx, my, mz) = ov.normalize().mul(1 / power / 1.5)
			
			e.motionX += mx
			e.motionY += my
			e.motionZ += mz
			
			e.attackEntityFrom(DamageSourceSpell.fenrirroar(this), 1f)
		}
		
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.FENRIR_AREA_END, this)
	}
	
	fun updateCDs() {
		--bladesCooldown
		--dashCooldown
		--leapCooldown
		--slashCooldown
		--stormCooldown
	}
	
	// ########################################################## MISC #########################################################
	
	val isBusy get() = isRoaring || isShaking
	val isRoaring get() = roarCooldown in ROAR_CD..(ROAR_CD + ROAR_DUR)
	val isShowering get() = iceShowerCooldown in SHOWER_CD..(SHOWER_CD + SHOWER_DUR)
	
	override fun attackEntityFrom(source: DamageSource, amount: Float): Boolean {
		if (source.entity === this || source.damageType == DamageSource.fall.damageType || source.sourceOfDamage is EntitySniceBall) return false
		val mod = if (source.entity != null && Vector3.entityDistance(source.entity, this) > 7) 10f else 2f
		return super.attackEntityFrom(source, amount / mod)
	}
	
	override fun onUpdate() {
		super.onUpdate()
		
		if (!worldObj.isRemote && isDripping && !isShaking && !hasPath() && onGround) {
			isShaking = true
			timeWolfShaking = 0f
			prevTimeWolfShaking = 0f
			worldObj.setEntityState(this, 8.toByte())
		}
		
		if (isWet) {
			isDripping = true
			isShaking = false
			timeWolfShaking = 0f
			prevTimeWolfShaking = 0f
		} else if ((isDripping || isShaking) && isShaking) {
			if (timeWolfShaking == 0f)
				playSound("mob.wolf.shake", soundVolume, (rand.nextFloat() - rand.nextFloat()) * 0.2f + 1f)
			
			prevTimeWolfShaking = timeWolfShaking
			timeWolfShaking += 0.05f
			
			if (prevTimeWolfShaking >= 2f) {
				isDripping = false
				isShaking = false
				prevTimeWolfShaking = 0f
				timeWolfShaking = 0f
			}
			
			if (timeWolfShaking > 0.4f) {
				val f = boundingBox.minY.F
				val i = (MathHelper.sin((timeWolfShaking - 0.4f) * Math.PI.F) * 7f).I
				
				for (j in 0 until i) {
					val f1 = (rand.nextFloat() * 2f - 1f) * width * 0.5f
					val f2 = (rand.nextFloat() * 2f - 1f) * width * 0.5f
					worldObj.spawnParticle("splash", posX + f1.D, (f + 0.8f).D, posZ + f2.D, motionX, motionY, motionZ)
				}
			}
		}
	}
	
	override fun onDeath(src: DamageSource?) {
		super.onDeath(src)
		
		val (sx, sy, sz) = Vector3(source).mf()
		worldObj.stopRecord(sx, sy, sz)
		
		if (RagnarokHandler.canBringBackSunAndMoon())
			RagnarokHandler.bringBackSunAndMoon()
	}
	
	override fun getDropItem() = AlfheimItems.elvenResource
	
	override fun dropFewItems(gotHit: Boolean, looting: Int) {
		entityDropItem(ElvenResourcesMetas.FenrirFur.stack(rand.nextInt(looting * 2 + 2) + 3), 5f)
		
		val relics = arrayOf(AlfheimAchievements.gungnir to AlfheimItems.gungnir, AlfheimAchievements.gleipnir to AlfheimItems.gleipnir)
		
		val (x, y, z) = Vector3(source).mf()
		getEntitiesWithinAABB(worldObj, EntityPlayer::class.java, FenrirDomain.boundBox.copy().offset(x, y, z)).shuffled().forEach { player ->
			val data = relics.shuffled().firstOrNull { !player.hasAchievement(it.first) } ?: return@forEach
			val stack = ItemStack(data.second)
			
			player.triggerAchievement(data.first)
			ItemRelic.bindToPlayer(player, stack)
			entityDropItem(stack, 0f)
			return
		}
		
		if (ASJUtilities.chance(5 + looting * 0.01)) entityDropItem(ItemStack(AlfheimItems.fenrirClaws), 0f)
	}
	
	override fun isAIEnabled() = true
	override fun canDespawn() = false
	override fun canAttackClass(clazz: Class<*>?) = true
	override fun canBePushed() = false
	override fun decreaseAirSupply(air: Int) = air
	override fun func_145780_a(x: Int, y: Int, z: Int, block: Block?) = playSound("mob.wolf.step", soundVolume, soundPitch)
	override fun getLivingSound() = if (ASJUtilities.chance(20)) "${ModInfo.MODID}:fenrir.hrrr" else null
	override fun getHurtSound() = if (ASJUtilities.chance(10)) "${ModInfo.MODID}:fenrir.hurt" else null
	override fun getDeathSound() = "${ModInfo.MODID}:fenrir.howl"
	override fun getSoundVolume() = 10f
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		super.writeEntityToNBT(nbt)
		
		val (x, y, z) = source
		nbt.setIntArray(TAG_SOURCE, intArrayOf(x, y, z))
		nbt.setInteger(TAG_STAGE, stage)
	}

	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		super.readEntityFromNBT(nbt)
		
		if (nbt.hasKey(TAG_SOURCE)) {
			val (x, y, z) = nbt.getIntArray(TAG_SOURCE)
			source = ChunkCoordinates(x, y, z)
		}
		
		stage = nbt.getInteger(TAG_STAGE)
	}
	
	// ######################################################### RENDER ########################################################
	
	@SideOnly(Side.CLIENT)
	fun getWolfShaking() = isDripping
	
	/**
	 * Used when calculating the amount of shading to apply while the wolf is shaking.
	 */
	@SideOnly(Side.CLIENT)
	fun getShadingWhileShaking(ticks: Float) = 0.75f + (prevTimeWolfShaking + (timeWolfShaking - prevTimeWolfShaking) * ticks) / 2f * 0.25f
	
	@SideOnly(Side.CLIENT)
	fun getShakeAngle(ticks: Float, phase: Float): Float {
		val f2 = max(0f, min(1f, (prevTimeWolfShaking + (timeWolfShaking - prevTimeWolfShaking) * ticks + phase) / 1.8f))
		return MathHelper.sin(f2 * Math.PI.F) * MathHelper.sin(f2 * Math.PI.F * 11f) * 0.15f * Math.PI.F
	}
	
	override fun getEyeHeight() = height * 0.8f
	
	@SideOnly(Side.CLIENT)
	override fun handleHealthUpdate(value: Byte) {
		if (value.I == 8) {
			isShaking = true
			timeWolfShaking = 0f
			prevTimeWolfShaking = 0f
		} else {
			super.handleHealthUpdate(value)
		}
	}
	
	@SideOnly(Side.CLIENT)
	fun getTailRotation(): Float = Math.toRadians(100 + sin((ticksExisted + mc.timer.renderPartialTicks) / 10.0) * 5).F
	
	override fun getNameColor() = 0xA00000
	
	@SideOnly(Side.CLIENT)
	override fun getBossBarTextureRect(): Rectangle {
		return barRect ?: Rectangle(0, 88, 185, 15).apply { barRect = this }
	}
	
	@SideOnly(Side.CLIENT)
	override fun getBossBarHPTextureRect(): Rectangle {
		return hpBarRect ?: Rectangle(0, 59, 181, 7).apply { hpBarRect = this }
	}
	
	override fun getBossBarTexture() = BossBarHandler.defaultBossBar!!
	
	override fun bossBarRenderCallback(res: ScaledResolution?, x: Int, y: Int) = Unit
	
	companion object {
		
		const val BLADES_CD = 100
		const val ROAR_CD = 600
		const val ROAR_DUR = 100
		const val SHOWER_CD = 200
		const val SHOWER_DUR = 200
		const val STORM_CD = 60
		
		const val TAG_SOURCE = "source"
		const val TAG_STAGE = "stage"
		
		var barRect: Rectangle? = null
		var hpBarRect: Rectangle? = null
		
		fun summon(world: World, x: Int, y: Int, z: Int) {
			val fenrir = EntityFenrir(world)
			fenrir.setPositionAndRotation(x + 0.5, y + 0.5, z + 0.5 + 31, 0f, 0f)
			fenrir.source = ChunkCoordinates(x, y, z)
			fenrir.forceSpawn = true
			fenrir.spawn()
		}
	}
}
