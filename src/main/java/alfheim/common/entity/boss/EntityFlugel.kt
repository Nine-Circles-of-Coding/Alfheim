package alfheim.common.entity.boss

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.block.tile.SubTileAnomalyBase
import alfheim.api.boss.IBotaniaBossWithName
import alfheim.common.achievement.AlfheimAchievements
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.tile.TileAnomaly
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.entity.boss.ai.flugel.*
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
import alfheim.common.item.relic.ItemFlugelSoul
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.relauncher.*
import net.minecraft.client.audio.ISound
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.entity.*
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry
import net.minecraft.entity.ai.EntityAIWatchClosest
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.*
import net.minecraft.init.*
import net.minecraft.item.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.*
import net.minecraft.tileentity.TileEntityBeacon
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraftforge.common.util.FakePlayer
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL
import vazkii.botania.client.core.handler.BossBarHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.core.handler.ConfigHandler
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.relic.*
import java.awt.Rectangle
import java.util.regex.*
import kotlin.math.*

class EntityFlugel(world: World): EntityCreature(world), IBotaniaBossWithName {
	
	val playersDamage: HashMap<String, Float> = HashMap()
	
	private var maxHit = 1f
	private var lastHit = 0f
	private var hurtTimeActual = 0
	
	val playersAround: List<EntityPlayer>
		get() {
			val (sx, sy, sz) = source
			val range = RANGE + 3
			val list = getEntitiesWithinAABB(worldObj, EntityPlayer::class.java, getBoundingBox(sx, sy, sz).offset(0.5).expand(range))
			list.removeAll { Vector3.pointDistanceSpace(it.posX, it.posY, it.posZ, sx + 0.5, sy + 0.5, sz + 0.5) >= range }
			return list
		}
	
	var stage: Int
		get() = dataWatcher.getWatchableObjectInt(22)
		set(stage) {
			dataWatcher.updateObject(22, stage)
			
			regens = when (stage) {
				STAGE_AGGRO    -> 3
				STAGE_MAGIC    -> 5
				STAGE_DEATHRAY -> 8
				else           -> 0
			}
		}
	
	var isHardMode: Boolean
		get() = getFlag(6)
		set(hard) = setFlag(6, hard)
	
	var isUltraMode: Boolean
		get() = getFlag(7)
		set(ultra) = setFlag(7, ultra)
	
	var source: ChunkCoordinates
		get() = dataWatcher.getWatchableObjectChunkCoordinates(23)
		set(value) = dataWatcher.updateObject(23, value)
	
	var playerCount: Int
		get() = dataWatcher.getWatchableObjectInt(24)
		set(count) = dataWatcher.updateObject(24, count)
	
	var aiTaskTimer: Int
		get() = dataWatcher.getWatchableObjectInt(25)
		set(time) = dataWatcher.updateObject(25, time)
	
	var summoner: String
		get() = dataWatcher.getWatchableObjectString(26)
		set(summoner) = dataWatcher.updateObject(26, summoner)
	
	var regens: Int
		get() = dataWatcher.getWatchableObjectInt(28)
		set(regens) = dataWatcher.updateObject(28, regens)
	
	var aiTask: AITask
		get() = AITask.values()[dataWatcher.getWatchableObjectInt(27)]
		set(ai) {
//			if (ModInfo.DEV) for (player in playersAround) ASJUtilities.say(player, "Set AI command to $ai")
			dataWatcher.updateObject(27, ai.ordinal)
		}
	
	// --------------------------------------------------------
	
	val isAggroed
		get() = dataWatcher.getWatchableObjectInt(22) > 0
	
	val isAlive
		get() = health > 0 && worldObj.difficultySetting != EnumDifficulty.PEACEFUL && !worldObj.isRemote
	
	val isDying
		get() = aiTask != AITask.INVUL && health / maxHealth <= 0.125
	
	val isCasting
		get() = aiTask.instant
	
	val isMiku
		get() = customNameTag == "Hatsune Miku"
	
	init {
		setSize(0.6f, 1.8f)
		navigator.setCanSwim(true)
		initAI()
		isImmuneToFire = true
		experienceValue = 1325
	}
	
	override fun attackEntityFrom(source: DamageSource, damage: Float): Boolean {
		val player = source.entity as? EntityPlayer ?: return false
		
		if ((source.damageType != "player" && source !is DamageSourceSpell) || !isTruePlayer(player) && isEntityInvulnerable)
			return false
		
		val prevDmg = playersDamage[player.commandSenderName] ?: return false
		if (!player.capabilities.isCreativeMode && player.capabilities.disableDamage) return false
		
		val crit = player.fallDistance > 0f && !player.onGround && !player.isOnLadder && !player.isInWater && !player.isPotionActive(Potion.blindness) && player.ridingEntity == null
		
		maxHit = if (player.capabilities.isCreativeMode) Float.MAX_VALUE else if (crit) 60f else 40f
		lastHit = min(maxHit, damage) * if (isUltraMode) 0.3f else if (isHardMode) 0.6f else 1f
		
		playersDamage[player.commandSenderName] = prevDmg + lastHit
		
		if (aiTask == AITask.REGEN || aiTask == AITask.TP) {
			lastHit /= 2f
			if (aiTask == AITask.REGEN) {
				lastHit /= 2f
				if (!isUltraMode)
					aiTaskTimer = 0
//				e.attackEntityFrom(source, lastHit) // why is it called twice ???
			}
		}
		
		reUpdate()
		return super.attackEntityFrom(source, lastHit)
	}
	
	override fun damageEntity(source: DamageSource, damage: Float) {
		super.damageEntity(source, lastHit)
		
		if (source.entity == null || health <= 0)
			return
		
		val motionVector = Vector3.fromEntityCenter(this).sub(Vector3.fromEntityCenter(source.entity)).normalize().mul(0.25)
		
		motionX = motionVector.x
		motionY = motionVector.y
		motionZ = motionVector.z
	}
	
	override fun setHealth(set: Float) {
		var hp = set
		prevHealth = health
		hp = max(prevHealth - min(lastHit, maxHit), hp)
		
//		ASJUtilities.chatLog("${-(prevHealth - hp)}")
		
		if (aiTask != AITask.INVUL && hp < prevHealth && hurtTimeActual > 0) return
		
		if (maxHit != Float.MAX_VALUE) hurtTimeActual = 20
		super.setHealth(hp)
		
		if (aiTask == AITask.INVUL) return
		if (health < prevHealth && (health / (maxHealth / 10)).I < (prevHealth / (maxHealth / 10)).I) {
			if (!aiTask.instant && aiTask != AITask.DEATHRAY)
				aiTaskTimer = 0
		}
		
		lastHit = 0f
		maxHit = 0f
	}
	
	override fun setDead() {
		if (isAlive)
			return tryToTP()
		
		val (x, y, z) = source
		worldObj.stopRecord(x, y, z)
		
		super.setDead()
	}
	
	override fun onDeath(source: DamageSource) {
		if (isAlive) return
		
		for (player in playersAround)
			if ((playersDamage[player.commandSenderName] ?: 0f) > 0) // only for those who attacked
				player.triggerAchievement(if (isHardMode) AlfheimAchievements.flugelHardKill else AlfheimAchievements.flugelKill)
		
		super.onDeath(source)
		
		playSoundAtEntity("random.explode", 20f, (1f + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2f) * 0.7f)
		worldObj.spawnParticle("hugeexplosion", posX, posY, posZ, 1.0, 0.0, 0.0)
		
		if (!isHardMode || worldObj.isRemote || worldObj.rand.nextInt(5) != 0) return
		
		val (x, y, z) = Vector3.fromEntity(this).mf()
		
		while (worldObj.setBlock(x, y, z, AlfheimBlocks.anomaly)) {
			(worldObj.getTileEntity(x, y, z) as? TileAnomaly ?: break).addSubTile(SubTileAnomalyBase.forName("Lightning") ?: break, "Lightning").lock(x, y, z, worldObj.provider.dimensionId)
			return
		}
		
		worldObj.setBlockToAir(x, y, z)
	}
	
	override fun dropFewItems(byPlayer: Boolean, looting: Int) {
		if (worldObj.isRemote) return
		if (isAlive) return
		if (!byPlayer) return
		
		val hard = isHardMode
		val ultra = isUltraMode
		var lot = true
		// For everyone
		if (hard) for (name in playersDamage.keys) {
			val player = worldObj.getPlayerEntityByName(name) as? EntityPlayerMP ?: continue
			var droppedRecord = false
			
			if (ConfigHandler.relicsEnabled && name == summoner) {
				var bind = true
				val relic = when {
					ultra                                                                                                        -> {
						val map = mutableMapOf(AlfheimAchievements.excaliber to AlfheimItems.excaliber,
											   AlfheimAchievements.moonlightBow to AlfheimItems.moonlightBow,
											   AlfheimAchievements.akashic to AlfheimItems.akashicRecords,
											   AlfheimAchievements.ringSif to AlfheimItems.priestRingSif,
											   AlfheimAchievements.ringNjord to AlfheimItems.priestRingNjord,
											   AlfheimAchievements.ringHeimdall to AlfheimItems.priestRingHeimdall)
						
						map.iterator().onEach { (k, _) ->
							if (player.hasAchievement(k))
								remove()
						}
						
						if (map.isEmpty()) {
							bind = false
							ElvenResourcesMetas.IffesalDust.stack(ASJUtilities.randInBounds(4, 6, rand))
						} else {
							val ach = map.keys.random()
							player.triggerAchievement(ach)
							ItemStack(map[ach]!!)
						}
					}
					
					(worldObj.getPlayerEntityByName(name) as? EntityPlayerMP)?.hasAchievement(AlfheimAchievements.mask) == false -> {
						worldObj.getPlayerEntityByName(name)?.triggerAchievement(AlfheimAchievements.mask)
						ItemStack(AlfheimItems.mask)
					}
					
					else                                                                                                         -> {
						bind = false
						ElvenResourcesMetas.IffesalDust.stack(ASJUtilities.randInBounds(2, 3, rand))
					}
				}
				
				if (bind)
					ItemRelic.bindToUsernameS(name, relic)
				
				entityDropItem(relic, 1f)
				lot = false
			}
			
			entityDropItem(ItemStack(ModItems.ancientWill, 1, rand.nextInt(if (ultra) 7 else 6)), 1f)
			val count = if (lot) {
				if (ultra) 10 else 6
			} else {
				if (ultra) 6 else 4
			}
			entityDropItem(ElvenResourcesMetas.MuspelheimEssence.stack(count), 1f)
			entityDropItem(ElvenResourcesMetas.NiflheimEssence.stack(count), 1f)
			
			lot = false
			
			if (Math.random() < 0.9) entityDropItem(ItemStack(ModItems.manaResource, 16 + rand.nextInt(12)), 1f)     // Manasteel
			if (Math.random() < 0.7) entityDropItem(ItemStack(ModItems.manaResource, 8 + rand.nextInt(6), 1), 1f)    // Manapearl
			if (Math.random() < 0.5) entityDropItem(ItemStack(ModItems.manaResource, 4 + rand.nextInt(3), 2), 1f)    // Manadiamond
			if (Math.random() < 0.25) entityDropItem(ItemStack(ModItems.overgrowthSeed, rand.nextInt(3) + 1), 1f)
			
			if (Math.random() < 0.5) {
				val voidLotus = Math.random() < 0.3f
				entityDropItem(ItemStack(ModItems.blackLotus, if (voidLotus) 1 else rand.nextInt(3) + 1, if (voidLotus) 1 else 0), 1f)
			}
			
			val runes = rand.nextInt(6) + 1
			for (i in 0 until runes) if (Math.random() < 0.3) entityDropItem(ItemStack(ModItems.rune, 2 + rand.nextInt(3), rand.nextInt(16)), 1f)
			if (Math.random() < 0.2) entityDropItem(ItemStack(ModItems.pinkinator), 1f)
			
			if (Math.random() < 0.3) {
				val i = Items.record_13.id
				val j = Items.record_wait.id
				val k = i + rand.nextInt(j - i + 1)
				entityDropItem(ItemStack(Item.getItemById(k)), 1f)
				droppedRecord = true
			}
			
			if (!droppedRecord && Math.random() < 0.2) entityDropItem(ItemStack(if (isMiku) AlfheimItems.discFlugelMeme else if (isUltraMode) AlfheimItems.discFlugelUltra else AlfheimItems.discFlugel), 1f)
		}
		
		if (ConfigHandler.relicsEnabled && !hard) {
			val relic = ItemStack(AlfheimItems.flugelSoul)
			if (worldObj.getPlayerEntityByName(summoner) != null) worldObj.getPlayerEntityByName(summoner)?.addStat(AlfheimAchievements.flugelSoul, 1)
			ItemRelic.bindToUsernameS(summoner, relic)
			entityDropItem(relic, 1f)
		}
	}
	
	override fun onLivingUpdate() {
		super.onLivingUpdate()
		
		if (ridingEntity != null) {
			if (ridingEntity.riddenByEntity != null)
				ridingEntity.riddenByEntity = null
			ridingEntity = null
		}
		
		if (!worldObj.isRemote && worldObj.difficultySetting == EnumDifficulty.PEACEFUL) setDead()
		
		val (sx, sy, sz) = source
		if (!worldObj.isRemote) {
			checkArena(worldObj, sx, sy, sz, true)
		}
		
		if (playersDamage.isEmpty()) playersDamage[summoner] = 0.1f
		var players = playersAround
		// if (players.isNotEmpty() && worldObj.isRemote && AlfheimConfigHandler.flugelBossBar)
		
		if (players.isEmpty() && aiTask != AITask.NONE) dropState()
		
		if (ASJUtilities.isClient && !isDead && players.isNotEmpty() && !worldObj.isRecordPlaying(sx, sy, sz))
			worldObj.playRecord((if (isMiku) AlfheimItems.discFlugelMeme else if (isUltraMode) AlfheimItems.discFlugelUltra else AlfheimItems.discFlugel) as ItemRecord, sx, sy, sz)
		
		if (ticksExisted % 20 == 0) {
			// PARTYKLZ!!!
			val mod = 10
			var pitch = 0
			while (pitch <= 180) {
				run {
					var yaw = 0
					while (yaw < 360) {
						// angle in rads
						val radY = yaw * PI.F / 180f
						val radP = pitch * PI.F / 180f
						
						// world coords
						val wX = sx + 0.5
						val wY = sy + 0.5
						val wZ = sz + 0.5
						
						// local coords
						val x = sin(radP.D) * cos(radY.D) * RANGE.D
						val y = cos(radP.D) * RANGE
						val z = sin(radP.D) * sin(radY.D) * RANGE.D
						
						// particle source position
						val nrm = Vector3(x, y, z).normalize()
						
						// noraml to pos
						val radp = (pitch + 90f) * PI.F / 180f
						val kx = sin(radp.D) * cos(radY.D)
						val ky = cos(radp.D)
						val kz = sin(radp.D) * sin(radY.D)
						val kos = Vector3(kx, ky, kz).normalize().rotate(Math.toRadians(PI * 2.0 * Math.random()), nrm).mul(0.1)
						
						val motX = kos.x.F
						val motY = kos.y.F
						val motZ = kos.z.F
						
						if (isMiku) Botania.proxy.wispFX(worldObj, wX - x, wY - y, wZ - z, 0f, 0.75f, 1f, 1f, motX, motY, motZ)
						else Botania.proxy.wispFX(worldObj, wX - x, wY - y, wZ - z, 0.5f, 0f, 1f, 1f, motX, motY, motZ)
						yaw += mod
					}
				}
				pitch += mod
			}
		}
		
		for (player in players) {
			// Get player back!
			if (Vector3.pointDistanceSpace(player.posX, player.posY, player.posZ, sx + 0.5, sy + 0.5, sz + 0.5) >= RANGE) {
				val (mx, my, mz) = Vector3(sx + 0.5, sy + 0.5, sz + 0.5).sub(Vector3.fromEntityCenter(player)).normalize()
				
				player.setMotion(mx, my, mz)
			}
			
			if (player.capabilities.isCreativeMode)
				continue
			
			// No beacon potions allowed!
			(player.activePotionEffects as MutableCollection<PotionEffect>).filter {
				it.duration < 200 && it.isAmbient && !Potion.potionTypes[it.potionID].isBadEffect
			}.forEach {
				if (worldObj.isRemote)
					player.removePotionEffectClient(it.potionID)
				else
					player.removePotionEffect(it.potionID)
			}
			
			(player.activePotionEffects as Collection<PotionEffect>).forEach {
				if (!Potion.potionTypes[it.potionID].isBadEffect)
					it.amplifier = if (isUltraMode) min(it.amplifier, 0) else if (isHardMode) min(it.amplifier, 2) else it.amplifier
			}
			
			// no GOD-mode allowed
			player.capabilities.disableDamage = false
			
			// remove player
			val baubles = PlayerHandler.getPlayerBaubles(player)
			val tiara = baubles[0]
			val wasHere = player.commandSenderName in playersDamage
			
			if (tiara?.item === ModItems.flightTiara && tiara!!.meta == 1 && wasHere)
				ItemNBTHelper.setInt(tiara, TAG_TIME_LEFT, 1200)
			else {
				if (!worldObj.isRemote) {
					if (wasHere)
						ASJUtilities.say(player, "alfheimmisc.flugel.notallowed")
					else
						ASJUtilities.say(player, "alfheimmisc.flugel.interuptor")
					
					fun isTooNear(bed: ChunkCoordinates?) =
						if (bed == null) true
						else Vector3.pointDistanceSpace(bed.posX.D, bed.posY.D, bed.posZ.D, sx, sy, sz) <= RANGE + 3
					
					if (isTooNear(player.getBedLocation(player.dimension))) {
						if (isTooNear(player.worldObj.spawnPoint)) {
							val v = Vector3(Math.random() * 100 + RANGE, 0, 0).rotateOY(Math.random() * 360)
							val newPosY = ASJUtilities.getTopLevel(worldObj, v.x.mfloor(), v.z.mfloor())
							player.setPositionAndUpdate(v.x, newPosY.D, v.z)
						} else {
							val bed = player.worldObj.spawnPoint
							player.setPositionAndUpdate(bed.posX.D, bed.posY.D, bed.posZ.D)
						}
					} else {
						val bed = player.getBedLocation(player.dimension)
						player.setPositionAndUpdate(bed.posX.D, bed.posY.D, bed.posZ.D)
					}
					
					continue
				}
			}
		}
		
		if (isDead) return
		
		if (!onGround) motionY += 0.075
		
		val invul = if (isEntityInvulnerable) aiTaskTimer else 0
		
		if (invul > 10) spawnPatyklz(false)
		
		if (invul <= 0) {
			if (Vector3.pointDistanceSpace(posX, posY, posZ, sx, sy, sz) > RANGE) teleportTo(sx + 0.5, sy + 1.6, sz + 0.5)
			if (isAggroed) {
				worldObj.getPlayerEntityByName(playersDamage.maxByOrNull { it.value }?.key ?: "Notch")?.let { ASJUtilities.faceEntity(this, it, 360f, 360f) }
				
				if (aiTask == AITask.NONE) reUpdate()
				if (aiTask != AITask.INVUL && health / maxHealth <= 0.6 && stage < STAGE_MAGIC) stage = STAGE_MAGIC
				if (isDying && stage < STAGE_DEATHRAY && aiTask != AITask.DEATHRAY) {
					aiTask = AITask.DEATHRAY
					aiTaskTimer = 0
				}
			} else {
				val range = 3
				players = getEntitiesWithinAABB(worldObj, EntityPlayer::class.java, boundingBox(range))
				if (players.any { !it.capabilities.isCreativeMode }) attackEntityFrom(DamageSource.causePlayerDamage(players[0]), 0f)
			}
		}
		
		hurtTimeActual = max(0, --hurtTimeActual)
	}
	
	// from pylons
	fun spawnPatyklz(deathRay: Boolean) {
		val (sx, sy, sz) = source
		val pos = Vector3.fromEntityCenter(this).sub(0.0, 0.2, 0.0)
		
		for (arr in PYLON_LOCATIONS) {
			val x = arr[0]
			val y = arr[1]
			val z = arr[2]
			
			val pylonPos = Vector3(sx + x, sy + y, sz + z)
			var worldTime = ticksExisted.D
			worldTime /= 5.0
			
			val rad = 0.75f + Math.random().F * 0.05f
			val xp = pylonPos.x + 0.5 + cos(worldTime) * rad
			val zp = pylonPos.z + 0.5 + sin(worldTime) * rad
			
			val partPos = Vector3(xp, pylonPos.y, zp)
			val mot = pos.copy().sub(partPos).mul(0.04)
			
			var r = (if (deathRay) 0.2f else 0.7f) + Math.random().F * 0.3f
			var g = Math.random().F * 0.3f
			var b = (if (deathRay) 0.7f else 0.2f) + Math.random().F * 0.3f
			
			if (isMiku) {
				r = Math.random().F * 0.3f
				g = (if (deathRay) 0.2f else 0.7f) + Math.random().F * 0.3f
				b = 0.7f + Math.random().F * 0.3f
			}
			
			Botania.proxy.wispFX(worldObj, partPos.x, partPos.y, partPos.z, r, g, b, 0.25f + Math.random().F * 0.1f, -0.075f - Math.random().F * 0.015f)
			Botania.proxy.wispFX(worldObj, partPos.x, partPos.y, partPos.z, r, g, b, 0.4f, mot.x.F, mot.y.F, mot.z.F)
		}
	}
	
	/*	================================	AI and Data STUFF	================================	*/
	
	public override fun applyEntityAttributes() {
		super.applyEntityAttributes()
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).baseValue = 0.5
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = MAX_HP.D
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).baseValue = 1.0
	}
	
	public override fun canDespawn() = false
	
	public override fun isAIEnabled() = true
	
	public override fun entityInit() {
		super.entityInit()
		dataWatcher.addObject(22, 0)                  // Stage
		dataWatcher.addObject(23, ChunkCoordinates()) // Source position
		dataWatcher.addObject(24, 0)                  // Player count
		dataWatcher.addObject(25, 0)                  // AI task timer
		dataWatcher.addObject(26, "")                 // Summoner
		dataWatcher.addObject(27, 0)                  // Current AI task
		dataWatcher.addObject(28, 0)                  // Regens count
	}
	
	override fun isEntityInvulnerable() = /* playersAround.isNotEmpty() && */ aiTask == AITask.INVUL && aiTaskTimer > 0
	
	fun initAI() {
		tasks.taskEntries.clear()
		var i = 0
/*0*/   tasks.addTask(i, EntityAIWatchClosest(this, EntityPlayer::class.java, java.lang.Float.MAX_VALUE))
/*1*/   tasks.addTask(++i, AITeleport(this, AITask.TP))
/*1*/   tasks.addTask(i, AIChase(this, AITask.CHASE))
/*1*/   tasks.addTask(i, AIRegen(this, AITask.REGEN))
/*1*/   tasks.addTask(i, AILightning(this, AITask.LIGHTNING))
/*1*/   tasks.addTask(i, AIRays(this, AITask.RAYS))
/*1*/   tasks.addTask(i, AIEnergy(this, AITask.DARK))
/*1*/   tasks.addTask(i, AIDeathray(this, AITask.DEATHRAY))
/*2*/   tasks.addTask(++i, AIInvul(this, AITask.INVUL))
/*3*/   tasks.addTask(++i, AIWait(this, AITask.NONE))
	}
	
	// --------------------------------------------------------
	
	override fun setCustomNameTag(name: String) = if (health == 1f) dataWatcher.updateObject(10, name) else Unit
	
	fun dropState() {
		if (worldObj.isRemote) return
		val (x, y, z) = source
		teleportTo(x + 0.5, y + 1.6, z + 0.5)
		stage = 0
		health = maxHealth
		aiTask = AITask.NONE
		aiTaskTimer = 0
		playersDamage.forEach { (k, _) -> playersDamage[k] = 0f }
		playersDamage[summoner] = 0.1f
	}
	
	fun reUpdate() {
		if (worldObj.isRemote) return
		if (stage < 0)
			stage = -stage
		else if (stage == 0) stage = STAGE_AGGRO
		if (aiTask == AITask.NONE) {
			aiTaskTimer = 0
			aiTask = nextTask()
		}
	}
	
	fun nextTask(): AITask {
		if (stage < STAGE_AGGRO) return AITask.NONE
		val next = AITask.values()[rand.nextInt(AITask.values().size)]
		if (/*next.instant && getAITask().instant &&*/ aiTask == next) return nextTask()
		if (Math.random() < next.chance) return nextTask()
		return if (stage < next.stage) nextTask() else next
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		super.writeEntityToNBT(nbt)
		if (hasCustomNameTag()) nbt.setString(TAG_NAME, customNameTag)
		
		nbt.setInteger(TAG_STAGE, stage)
		nbt.setBoolean(TAG_HARDMODE, isHardMode)
		nbt.setBoolean(TAG_ULTRAMODE, isUltraMode)
		
		val (x, y, z) = source
		nbt.setInteger(TAG_SOURCE_X, x)
		nbt.setInteger(TAG_SOURCE_Y, y)
		nbt.setInteger(TAG_SOURCE_Z, z)
		
		nbt.setInteger(TAG_PLAYER_COUNT, playerCount)
		nbt.setInteger(TAG_AI_TASK, aiTask.ordinal)
		nbt.setInteger(TAG_AI_TIMER, aiTaskTimer)
		
		for (ai in tasks.executingTaskEntries)
			if ((ai as EntityAITaskEntry).action is AIBase) {
				val path = ai.action.javaClass.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
				nbt.setString(TAG_AI, path[path.size - 1])
			}
		
		nbt.setString(TAG_SUMMONER, summoner)
		
		val map = NBTTagCompound()
		for ((key, value) in playersDamage) map.setFloat(key, value)
		nbt.setTag(TAG_ATTACKED, map)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		super.readEntityFromNBT(nbt)
		if (nbt.hasKey(TAG_PLAYER_COUNT)) dataWatcher.updateObject(10, nbt.getString(TAG_NAME))
		
		stage = nbt.getInteger(TAG_STAGE)
		if (nbt.getBoolean(TAG_HARDMODE)) isHardMode = true
		if (nbt.getBoolean(TAG_ULTRAMODE)) isUltraMode = true
		
		source = ChunkCoordinates(nbt.getInteger(TAG_SOURCE_X), nbt.getInteger(TAG_SOURCE_Y), nbt.getInteger(TAG_SOURCE_Z))
		
		playerCount = if (nbt.hasKey(TAG_PLAYER_COUNT))
			nbt.getInteger(TAG_PLAYER_COUNT)
		else
			1
		
		aiTask = AITask.values()[nbt.getInteger(TAG_AI_TASK)]
		
		//if (ModInfo.DEV) ASJUtilities.log("Scrolling AIs for " + nbt.getString(TAG_AI));
		for (e in tasks.taskEntries) {
			//if (ModInfo.DEV) ASJUtilities.log("At " + ((EntityAITaskEntry) e).action.getClass().getName());
			val path = (e as EntityAITaskEntry).action.javaClass.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
			if (e.action is AIBase && path[path.size - 1] == nbt.getString(TAG_AI)) {
				tasks.executingTaskEntries.add(e)
			}
		}
		
		aiTaskTimer = nbt.getInteger(TAG_AI_TIMER)
		summoner = nbt.getString(TAG_SUMMONER)
		
		val map = nbt.getCompoundTag(TAG_ATTACKED)
		for (o in map.func_150296_c()) playersDamage[o as String] = map.getFloat(o)
	}
	
	// EntityEnderman code below ============================================================================
	
	fun tryToTP() {
		var tries = 0
		while (!teleportRandomly() && tries < 50) tries++
		
		if (tries >= 50) {
			val src = source
			teleportTo(src.posX + 0.5, src.posY + 1.6, src.posZ + 0.5)
		}
	}
	
	fun teleportRandomly(): Boolean {
		val d0 = posX + (rand.nextDouble() - 0.5) * RANGE / 2.0
		val d1 = posY + (rand.nextDouble() - 0.5) * RANGE / 2.0
		val d2 = posZ + (rand.nextDouble() - 0.5) * RANGE / 2.0
		return teleportTo(d0, d1, d2)
	}
	
	fun teleportTo(par1: Double, par3: Double, par5: Double): Boolean {
		val d3 = posX
		val d4 = posY
		val d5 = posZ
		posX = par1
		posY = par3
		posZ = par5
		var flag = false
		val i = posX.mfloor()
		val j = posY.mfloor()
		val k = posZ.mfloor()
		
		if (worldObj.blockExists(i, j, k)) {
			setPosition(posX, posY, posZ)
			motionZ = 0.0
			motionY = motionZ
			motionX = motionY
			
			if (worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox)) flag = true
			
			// Prevent out of bounds teleporting
			val (x, y, z) = source
			if (Vector3.pointDistanceSpace(posX, posY, posZ, x, y, z) > RANGE) flag = false
		}
		
		if (!flag) {
			setPosition(d3, d4, d5)
			return false
		}
		
		val short1: Short = 128
		
		for (l in 0 until short1) {
			val d6 = l / (short1 - 1.0)
			val f = (rand.nextFloat() - 0.5f) * 0.2f
			val f1 = (rand.nextFloat() - 0.5f) * 0.2f
			val f2 = (rand.nextFloat() - 0.5f) * 0.2f
			val d7 = d3 + (posX - d3) * d6 + (rand.nextDouble() - 0.5) * width.D * 2.0
			val d8 = d4 + (posY - d4) * d6 + rand.nextDouble() * height
			val d9 = d5 + (posZ - d5) * d6 + (rand.nextDouble() - 0.5) * width.D * 2.0
			worldObj.spawnParticle("portal", d7, d8, d9, f.D, f1.D, f2.D)
		}
		
		worldObj.playSoundEffect(d3, d4, d5, "mob.endermen.portal", 1f, 1f)
		playSound("mob.endermen.portal", 1f, 1f)
		return true
	}
	
	// EntityFireball code below ============================================================================
	
	fun checkCollision() {
		var vec3 = Vec3.createVectorHelper(posX, posY, posZ)
		var vec31 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ)
		var mop: MovingObjectPosition? = worldObj.rayTraceBlocks(vec3, vec31)
		vec3 = Vec3.createVectorHelper(posX, posY, posZ)
		vec31 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ)
		
		if (mop != null) {
			vec31 = Vec3.createVectorHelper(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord)
		}
		
		var entity: Entity? = null
		val list = getEntitiesWithinAABB(worldObj, Entity::class.java, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0))
		list.remove(this)
		var d0 = 0.0
		
		list.forEach {
			if (!it.canBeCollidedWith()) return@forEach
			val f = 0.3f
			val axisalignedbb = it.boundingBox.expand(f.D, f.D, f.D)
			val movingobjectposition1 = axisalignedbb.calculateIntercept(vec3, vec31) ?: return@forEach
			
			val d1 = vec3.distanceTo(movingobjectposition1.hitVec)
			
			if (d1 >= d0 && d0 != 0.0) return@forEach
			entity = it
			d0 = d1
		}
		
		if (entity != null)
			mop = MovingObjectPosition(entity)
		
		if (mop != null)
			onImpact(mop)
	}
	
	fun onImpact(mop: MovingObjectPosition) {
		if (mop.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) return
		
		val shadowChance = when {
			isUltraMode -> 40
			isHardMode  -> 20
			else        -> 10
		}
		val shadow = ASJUtilities.chance(shadowChance)
		
		val damage = when {
			isUltraMode -> 20f
			isHardMode  -> 15f
			else        -> 10f
		} / if (shadow) 2 else 1
		
		mop.entityHit.attackEntityFrom(if (shadow) DamageSourceSpell.shadow(this) else DamageSource.causeMobDamage(this), damage)
	}
	
	/*	================================	HEALTHBAR STUFF	================================	*/
	
	@SideOnly(Side.CLIENT)
	var barRect: Rectangle? = null
	
	@SideOnly(Side.CLIENT)
	var hpBarRect: Rectangle? = null
	
	@SideOnly(Side.CLIENT)
	override fun getNameColor() = if (isMiku) 0x0066FF else if (isUltraMode) 0xAA0000 else 0xFF80FF
	
	@SideOnly(Side.CLIENT)
	override fun getBossBarTexture() = BossBarHandler.defaultBossBar!!
	
	@SideOnly(Side.CLIENT)
	override fun getBossBarTextureRect(): Rectangle {
		if (barRect == null)
			barRect = Rectangle(0, 44, 185, 15)
		
		return barRect!!
	}
	
	@SideOnly(Side.CLIENT)
	override fun getBossBarHPTextureRect(): Rectangle {
		if (hpBarRect == null)
			hpBarRect = Rectangle(0, if (isMiku) 37 else if (isUltraMode) 59 else 15, 181, 7)
		
		return hpBarRect!!
	}
	
	@SideOnly(Side.CLIENT)
	override fun bossBarRenderCallback(res: ScaledResolution, x: Int, y: Int) {
		glPushMatrix()
		val px = x + 160
		val py = y + 12
		
		val stack = ItemStack(Items.skull, 1, 3)
		mc.renderEngine.bindTexture(TextureMap.locationItemsTexture)
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting()
		glEnable(GL_RESCALE_NORMAL)
		RenderItem.getInstance().renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, stack, px, py)
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting()
		
		val unicode = mc.fontRenderer.unicodeFlag
		mc.fontRenderer.unicodeFlag = true
		mc.fontRenderer.drawStringWithShadow("" + playerCount, px + 15, py + 4, 0xFFFFFF)
		mc.fontRenderer.unicodeFlag = unicode
		glPopMatrix()
	}
	
	companion object {
		
		const val TAG_TIME_LEFT = "timeLeft" // from vazkii.botania.common.item.equipment.bauble.ItemFlightTiara
		
		const val SPAWN_TICKS = 160
		const val DEATHRAY_TICKS = 200
		const val RANGE = 24
		const val MAX_HP = 800f
		
		const val TAG_NAME = "name"
		const val TAG_STAGE = "stage"
		const val TAG_HARDMODE = "hardmode"
		const val TAG_ULTRAMODE = "ultramode"
		const val TAG_SOURCE_X = "sourceX"
		const val TAG_SOURCE_Y = "sourceY"
		const val TAG_SOURCE_Z = "sourceZ"
		const val TAG_PLAYER_COUNT = "playerCount"
		const val TAG_AI_TASK = "task"
		const val TAG_AI = "ai"
		const val TAG_AI_TIMER = "aiTime"
		const val TAG_SUMMONER = "summoner"
		const val TAG_ATTACKED = "attacked"
		
		const val STAGE_AGGRO = 1       // 100%  hp
		const val STAGE_MAGIC = 2       // 60%   hp
		const val STAGE_DEATHRAY = 3    // 12.5% hp
		
		fun spawn(player: EntityPlayer, stack: ItemStack, world: World, x: Int, y: Int, z: Int, hard: Boolean, ultra: Boolean): Boolean {
			val beacon = world.getTileEntity(x, y, z) as? TileEntityBeacon ?: return false
			
			if (getEntitiesWithinAABB(world, EntityFlugel::class.java, beacon.boundingBox(RANGE)).isNotEmpty()) {
				if (!world.isRemote) ASJUtilities.say(player, "alfheimmisc.flugel.alreadyhere")
				return false
			}
			
			if (!isTruePlayer(player)) {
				if (!world.isRemote) ASJUtilities.say(player, "alfheimmisc.flugel.fakeplayer")
				return false
			}
			
			if (world.difficultySetting == EnumDifficulty.PEACEFUL) {
				if (!world.isRemote) ASJUtilities.say(player, "alfheimmisc.flugel.peacefulNoob")
				return false
			}
			
			if ((hard && !player.hasAchievement(AlfheimAchievements.flugelKill)) || (ultra && !player.hasAchievement(AlfheimAchievements.flugelHardKill))) {
				if (!world.isRemote) ASJUtilities.say(player, "alfheimmisc.flugel.tooweak.$ultra")
				return false
			}
			
			if (beacon.levels < 1 || beacon.primaryEffect == 0) {
				if (!world.isRemote) ASJUtilities.say(player, "alfheimmisc.flugel.inactive")
				return false
			}
			
			for (coords in PYLON_LOCATIONS) {
				val i = x + coords[0]
				val j = y + coords[1]
				val k = z + coords[2]
				
				val blockat = world.getBlock(i, j, k)
				val meta = world.getBlockMetadata(i, j, k)
				if (blockat !== ModBlocks.pylon || meta != 2) {
					if (!world.isRemote) ASJUtilities.say(player, "alfheimmisc.flugel.needsCatalysts")
					return false
				}
			}
			
			if (!checkArena(world, x, y, z, false)) {
				if (!world.isRemote) ASJUtilities.say(player, "alfheimmisc.flugel.badArena")
				return false
			}
			
			var miku = false
			var crds: ChunkCoordinates? = null
			
			if (stack.item === ModItems.flugelEye) {
				crds = (ModItems.flugelEye as ItemFlugelEye).getBinding(stack)
			} else if (stack.item === AlfheimItems.flugelSoul) {
				crds = ItemFlugelSoul.getFirstCoords(stack)
			}
			
			if (crds != null) miku = crds.posX == 39 && crds.posY == 39 && crds.posZ == 39
			
			if (!hard) stack.stackSize--
			if (world.isRemote) return true
			
			val e = EntityFlugel(world)
			e.setPosition(x + 0.5, y + 3.0, z + 0.5)
			e.aiTask = AITask.INVUL
			e.aiTaskTimer = 0
			e.dataWatcher.updateObject(6, 1f)
			e.source = ChunkCoordinates(x, y, z)
			
			if (hard) e.isHardMode = true
			if (ultra) e.isUltraMode = true
			
			e.summoner = player.commandSenderName
			
			if (miku) {
				e.alwaysRenderNameTag = true
				e.customNameTag = "Hatsune Miku"
			}
			
			val players = e.playersAround
			var playerCount = 0
			
			players.forEach {
				if (!isTruePlayer(it)) return@forEach
				e.playersDamage[it.commandSenderName] = 0f
				++playerCount
			}
			
			e.playersDamage[player.commandSenderName] = 0.1f
			
			e.playerCount = playerCount
			e.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.maxHealth).baseValue = (MAX_HP * playerCount * if (hard) 2 else 1).D
			e.noClip = true
			
			e.playSoundAtEntity("mob.enderdragon.growl", 10f, 0.1f)
			e.spawn()
			return true
		}
		
		/*	================================	UTILITY STUFF	================================	*/
		
		val PYLON_LOCATIONS = arrayOf(intArrayOf(4, 1, 4), intArrayOf(4, 1, -4), intArrayOf(-4, 1, 4), intArrayOf(-4, 1, -4))
		
		fun checkArena(world: World, sx: Int, sy: Int, sz: Int, destroy: Boolean): Boolean {
			var proper = true
			Botania.proxy.setWispFXDepthTest(false)
			
			for (i in 0.bidiRange(RANGE))
				for (j in 0.bidiRange(RANGE))
					for (k in 0.bidiRange(RANGE)) {
						if (j == 1 && abs(i) == 4 && abs(k) == 4 || j == 0 && i == 0 && k == 0 || j == -1 && i in -1..1 && k in -1..1 || Vector3.pointDistanceSpace(i, k, j, 0, 0, 0) > RANGE)
							continue // Ignore pylons, beacon+base and out of circle
						
						val x = sx + i
						val y = sy + j
						val z = sz + k
						
						val block = world.getBlock(x, y, z)
						if (block !== Blocks.air) {
							proper = false
							
							if (destroy) {
								val items = block.getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0)
								for (stack in items) EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack).spawn()
								if (ConfigHandler.blockBreakParticles) world.playAuxSFX(2001, x, y, z, block.id + (world.getBlockMetadata(x, y, z) shl 12))
								world.setBlockToAir(x, y, z)
							} else
								Botania.proxy.wispFX(world, x + 0.5, y + 0.5, z + 0.5, 1f, 0f, 0f, 0.5f, 0f, 10f)
						}
					}
			
			Botania.proxy.setWispFXDepthTest(true)
			return proper
		}
		
		val FAKE_PLAYER_PATTERN: Pattern = Pattern.compile("^\\[.*]|ComputerCraft$")
		
		fun isTruePlayer(e: EntityPlayer): Boolean {
			val name = e.commandSenderName
			return !(e is FakePlayer || FAKE_PLAYER_PATTERN.matcher(name).matches())
		}
		
		fun World.isRecordPlaying(x: Int, y: Int, z: Int): Boolean {
			if (ASJUtilities.isServer) return false
			val coords = ChunkCoordinates(x, y, z)
			return worldAccesses.filterIsInstance<RenderGlobal>().any {
				val sound = it.mapSoundPositions[coords] as? ISound ?: return@any false
				mc.soundHandler.isSoundPlaying(sound)
			}
		}
		
		fun World.playRecord(record: ItemRecord, x: Int, y: Int, z: Int) {
			playRecord("records." + record.recordName, x, y, z)
		}
		
		fun World.stopRecord(x: Int, y: Int, z: Int) {
			playRecord(null, x, y, z)
		}
	}
}