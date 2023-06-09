package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.*
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.EventResourcesMetas
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.*
import net.minecraft.entity.ai.*
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.monster.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraftforge.event.entity.living.*
import thaumcraft.common.entities.monster.EntityFireBat
import vazkii.botania.common.Botania
import vazkii.botania.common.item.ModItems
import kotlin.math.*

class EntityFireSpirit(world: World): EntityLiving(world) {
	
	var timer = 0
	
	var master: Boolean
		get() = getFlag(6)
		set(master) = setFlag(6, master)
	
	var position: ChunkCoordinates
		get() = dataWatcher.getWatchedObject(12).`object` as ChunkCoordinates
		set(pos) = dataWatcher.updateObject(12, pos)
	
	override fun entityInit() {
		super.entityInit()
		noClip = true
		setSize(0f, 0f)
		dataWatcher.addObject(12, ChunkCoordinates())
	}
	
	override fun onEntityUpdate() {
		val (x, y, z) = position
		val master = master
		setPosition(x + 0.5, y + if (master) 0.5 else 1.5, z + 0.5)
		setMotion(0.0)
		clearActivePotions()
		
		getPlayersAround(worldObj, x, y, z).forEach {
			if (it.heldItem?.item === AlfheimItems.rodInterdiction)
				if (it.dropOneItem(true) == null)
					it.setCurrentItemOrArmor(0, null)
		}
		
		super.onEntityUpdate()
		
		if (if (master) {
				!checkStructure(worldObj, x, y - 1, z, timer <= 0, null)
			} else {
				!(worldObj.getBlock(x, y, z) === AlfheimBlocks.redFlame)
			}) {
			if (!worldObj.isRemote) {
				health = 0f
				setDead()
			}
			return
		}
		
		if (!worldObj.isRemote) {
			val angles = arrayOf(0, 120, 240)
			
			if (master) {
				for (a in arrayOf(60, 180, 300)) {
					val i = cos(Math.toRadians(-ticksExisted * 4.0 - a))
					val k = sin(Math.toRadians(-ticksExisted * 4.0 - a))
					VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.WISP, dimension, posX + i, posY + 1, posZ + k, 1.0, Math.random() * 0.5, 0.0, 0.25, 0.0, 0.0, 0.0, 0.5)
				}
				
				VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.WISP, dimension, posX, posY + 0.5, posZ, 1.0, Math.random() * 0.5, 0.0, 0.25, 0.0, 0.0, 0.0, 0.5)
			}
			
			for (a in angles) {
				val i = cos(Math.toRadians(ticksExisted * 4.0 + a)) / 2
				val k = sin(Math.toRadians(ticksExisted * 4.0 + a)) / 2
				VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.WISP, dimension, posX + i, posY, posZ + k, 1.0, Math.random() * 0.5, 0.0, 0.25, 0.0, 0.0, 0.0, 0.5)
			}
		}
		
		if (master) {
			if (timer-- <= 0) {
				if (!worldObj.isRemote) setDead()
				return
			}
		} else {
			return
		}
		
		if (timer == 1) {
			if (!worldObj.isRemote) {
				EntityItem(worldObj, x + 0.5, y + 1.5, z + 0.5, ItemStack(AlfheimItems.eventResource, 1, EventResourcesMetas.VolcanoRelic)).apply { setMotion(0.0) }.spawn()
				getPlayersAround(worldObj, x, y, z).forEach { ASJUtilities.say(it, "alfheimmisc.spirit.success") }
			}
			setDead()
			return
		}
		
		if (timer % 50 == 0) {
			if (!worldObj.isRemote) selectModded(this, x, y, z)?.spawn() ?: when (rand.nextInt(11)) {
					0       -> {
						EntityMuspelson(worldObj).apply {
							setRandomPos(x, y, z, false)
						}
					}
					
					// no targeting so no need in many
					1       -> EntityMagmaCube(worldObj).apply {
						setRandomPos(x, y, z, false)
						
						slimeSize = rand.nextInt(3) + 4
						getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = health * 1.25
						health = maxHealth
					}
					
					in 3..4 -> EntityBlaze(worldObj).apply {
						setRandomPos(x, y, z, true)
						
						setTarget(this@EntityFireSpirit)
						
						getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 50.0
						health = maxHealth
					}
					
					in 5..6 -> EntityPigZombie(worldObj).apply {
						setRandomPos(x, y, z, false)
						
						when (rand.nextInt(10)) {
							0       -> epicSet
							in 1..3 -> eliteSet
							else    -> regularSet
						}.forEachIndexed { id, it ->
							setCurrentItemOrArmor(id, ItemStack(it))
							setEquipmentDropChance(id, 0f)
						}
						
						getEntityAttribute(SharedMonsterAttributes.attackDamage).baseValue = 3.0
						getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 30.0
						health = maxHealth
						
						setTarget(this@EntityFireSpirit)
					}
					
					7       -> EntityGhast(worldObj).apply {
						setRandomPos(x, y, z, true)
						
						targetedEntity = this@EntityFireSpirit
						aggroCooldown = this@EntityFireSpirit.timer + 50
						
						explosionStrength = rand.nextInt(3) + 2
					}

//					8..10
					else    -> EntitySkeleton(worldObj).apply {
						setRandomPos(x, y, z, false)
						skeletonType = 1
						
						when (rand.nextInt(10)) {
							0       -> epicSet
							in 1..3 -> eliteSet
							else    -> regularSet
						}.forEachIndexed { id, it ->
							setCurrentItemOrArmor(id, ItemStack(it))
							setEquipmentDropChance(id, 0f)
						}
						
						getEntityAttribute(SharedMonsterAttributes.attackDamage).baseValue = 4.0
						getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 40.0
						health = maxHealth
						
						tasks.addTask(4, EntityAIAttackOnCollide(this, EntityFireSpirit::class.java, 1.2, true))
						targetTasks.addTask(2, EntityAINearestAttackableTarget(this, EntityFireSpirit::class.java, 0, true))
					}
				}.apply { entityData.setBoolean(TAG_RITUAL_SUMMONED, true) }.spawn()
		}
	}
	
	override fun attackEntityFrom(src: DamageSource?, amount: Float): Boolean {
		// protecting spirit for being accidentally harmed by a player
		if (src?.entity is EntityPlayer) return false
		val (x, y, z) = position
		
		val cout = min(4, PYLONS.sumOf { min(1, getEntitiesWithinAABB(worldObj, EntityFireSpirit::class.java, getBoundingBox(x + it[0] + 0.5, y + it[1] + if (master) 0.5 else -0.5, z + it[2] + 0.5).expand(0.5)).size) })
		return super.attackEntityFrom(src, amount * (1 - cout * 0.25f))
	}
	
	override fun setDead() {
		if (isDead) return
		super.setDead()
		
		val (x, y, z) = position
		val master = master
		
		for (i in 0..(if (master) 1 else 0)) {
			worldObj.playAuxSFX(2001, x, y - i, z, worldObj.getBlock(x, y - i, z).id + (worldObj.getBlockMetadata(x, y - i, z) shl 12))
			worldObj.setBlockToAir(x, y - i, z)
		}
		
		if (!master && health > 0f)
			worldObj.setBlock(x, y, z, AlfheimBlocks.alfheimPylon, 1, 3)
		
		for (c in PYLONS) {
			getEntitiesWithinAABB(worldObj, EntityFireSpirit::class.java, getBoundingBox(x + c[0] + 0.5, y + c[1] + if (master) 0.5 else -0.5, z + c[2] + 0.5).expand(0.5)).forEach {
				if (master) {
					it.setDead()
				} else if (it.master) {
					it.timer += 500
					val (ix, iy, iz) = it.position
					if (!worldObj.isRemote && health <= 0f) getPlayersAround(it.worldObj, ix, iy, iz).forEach { player -> ASJUtilities.say(player, "alfheimmisc.spirit.pylondied") }
				}
			}
		}
	}
	
	override fun getCommandSenderName() = StatCollector.translateToLocal("entity.${EntityList.getEntityString(this) ?: "generic"}${if (master) ".greater" else ""}.name")!!
	
	override fun canDespawn() = false
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		super.writeEntityToNBT(nbt)
		
		val (x, y, z) = position
		nbt.setInteger(TAG_POS_X, x)
		nbt.setInteger(TAG_POS_Y, y)
		nbt.setInteger(TAG_POS_Z, z)
		
		nbt.setBoolean(TAG_MASTER, master)
//		nbt.setInteger(TAG_TIMER, timer)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		super.readEntityFromNBT(nbt)
		
		position = ChunkCoordinates(
			nbt.getInteger(TAG_POS_X),
			nbt.getInteger(TAG_POS_Y),
			nbt.getInteger(TAG_POS_Z)
		)
		
		master = nbt.getBoolean(TAG_MASTER)
//		timer = nbt.getInteger(TAG_TIMER)
	}
	
	companion object {
		
		const val TAG_RITUAL_SUMMONED = "firespiritsummoned"
		
		const val TAG_MASTER = "isMaster"
//		const val TAG_TIMER = "timer"
		
		const val TAG_POS_X = "posX"
		const val TAG_POS_Y = "posY"
		const val TAG_POS_Z = "posZ"
		
		const val RADIUS = 16
		
		val PYLONS = arrayOf(arrayOf(5, 1, 5), arrayOf(5, 1, -5), arrayOf(-5, 1, -5), arrayOf(-5, 1, 5))
		
		val regularSet = arrayOf(Items.iron_sword, Items.chainmail_boots, Items.chainmail_leggings, Items.chainmail_chestplate, Items.chainmail_helmet)
		val eliteSet = arrayOf(ModItems.elementiumSword, ModItems.elementiumBoots, ModItems.elementiumLegs, ModItems.elementiumChest, ModItems.elementiumHelm)
		val epicSet = arrayOf(AlfheimItems.volcanoMace, AlfheimItems.volcanoBoots, AlfheimItems.volcanoLeggings, AlfheimItems.volcanoChest, AlfheimItems.volcanoHelmet)
		
		init {
			eventForge()
		}
		
		fun startRitual(world: World, x: Int, y: Int, z: Int, player: EntityPlayer): Boolean {
			if (!checkStructure(world, x, y, z, true, player)) return false
			
			if (!world.setBlock(x, y + 1, z, AlfheimBlocks.redFlame)) return false
			
			var master = true
			
			for (c in arrayOf(arrayOf(0, 1, 0), *PYLONS)) {
				val spirit = EntityFireSpirit(world)
				spirit.master = master
				spirit.setPosition(x + c[0] + 0.5, y + c[1] + if (master) 0.5 else 1.5, z + c[2] + 0.5)
				spirit.position = ChunkCoordinates(x + c[0], y + c[1], z + c[2])
				spirit.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.maxHealth).baseValue = if (master) 1000.0 else 500.0
				spirit.health = spirit.maxHealth
				if (master) spirit.timer = 4000
				if (!spirit.spawn()) return false
				master = false
			}
			
			world.setBlock(x, y, z, Blocks.netherrack)
			
			for (c in PYLONS) {
				world.setBlock(x + c[0], y + c[1], z + c[2], AlfheimBlocks.redFlame)
				world.setBlock(x + c[0], y + c[1] - 1, z + c[2], Blocks.netherrack)
			}
			
			if (!player.capabilities.isCreativeMode) --player.heldItem.stackSize
			
			return true
		}
		
		fun checkStructure(world: World, x: Int, y: Int, z: Int, first: Boolean, player: EntityPlayer?): Boolean {
			if (!(first || world.getBlock(x, y + 1, z) === AlfheimBlocks.redFlame)) {
				if (!world.isRemote)
					getPlayersAround(world, x, y, z).forEach { ASJUtilities.say(it, "alfheimmisc.spirit.flamedied") }
				return false
			}
			
			if (world.isRaining) {
				if (!world.isRemote) {
					if (first)
						ASJUtilities.say(player, "alfheimmisc.spirit.rain.start")
					else {
						getPlayersAround(world, x, y, z).forEach { ASJUtilities.say(it, "alfheimmisc.spirit.rain.inmid") }
					}
				}
				
				return false
			}
			
			if (world.difficultySetting == EnumDifficulty.PEACEFUL) {
				if (!world.isRemote) {
					if (!first)
						getPlayersAround(world, x, y, z).forEach { ASJUtilities.say(it, "alfheimmisc.spirit.peaceful") }
					else
						ASJUtilities.say(player, "alfheimmisc.spirit.peaceful")
				}
				
				return false
			}
			
			if (first) {
				for (c in PYLONS)
					if (world.getBlock(x + c[0], y + c[1], z + c[2]) !== AlfheimBlocks.alfheimPylon || world.getBlockMetadata(x + c[0], y + c[1], z + c[2]) != 1) {
						if (!world.isRemote) {
							ASJUtilities.say(player, "alfheimmisc.spirit.nopylons")
							VisualEffectHandler.sendError(world.provider.dimensionId, x + c[0], y + c[1], z + c[2])
						}
						return false
					}
			}
			
			if (!hasProperArena(world, x, y, z)) {
				if (!world.isRemote) {
					if (first)
						ASJUtilities.say(player, "alfheimmisc.spirit.badarena")
					else
						getPlayersAround(world, x, y, z).forEach { ASJUtilities.say(it, "alfheimmisc.spirit.arenacorrupt") }
				}
				
				return false
			}
			
			return true
		}
		
		fun hasProperArena(world: World, x: Int, y: Int, z: Int): Boolean {
			for (i in x.bidiRange(RADIUS + 3))
				for (j in y..(y + RADIUS + 3))
					for (k in z.bidiRange(RADIUS + 3)) {
						if (i == x && j == y + 1 && k == z) continue // ignore fire
						if (abs(i - x) == 5 && abs(k - z) == 5 && j == y + 1 || Vector3.pointDistanceSpace(i, j, k, x, y, z) > RADIUS) continue  // Ignore pylons and out of circle
						
						if (world.getTileEntity(i, j, k) != null) {
							VisualEffectHandler.sendError(world.provider.dimensionId, i, j, k)
							
							return false
						}
						
						val block = world.getBlock(i, j, k)
						if (block === Blocks.fire) continue
						
						if (j != y) {
							if (!world.isAirBlock(i, j, k)) {
								VisualEffectHandler.sendError(world.provider.dimensionId, i, j, k)
								
								return false
							}
						} else {
							if (!block.material.blocksMovement() ||
								!block.renderAsNormalBlock() ||
								!block.isOpaqueCube ||
								!block.isNormalCube ||
								block.getCollisionBoundingBoxFromPool(world, i, j, k).let {
									it.minX != i.D ||
									it.minY != j.D ||
									it.minZ != k.D ||
									it.maxX != i + 1.0 ||
									it.maxY != j + 1.0 ||
									it.maxZ != k + 1.0
								}) {
								
								VisualEffectHandler.sendError(world.provider.dimensionId, i, j, k)
								
								return false
							}
						}
					}
			return true
		}
		
		fun getPlayersAround(world: World, x: Int, y: Int, z: Int) = getEntitiesWithinAABB(world, EntityPlayer::class.java, getBoundingBox(x, y, z).expand(RADIUS).apply { minY += RADIUS })
		
		private fun EntityLivingBase.setRandomPos(x: Int, y: Int, z: Int, above: Boolean) {
			val j = if (above) Math.random() * RADIUS else 0.0
			val (a, b, c) = Vector3(RADIUS * 0.75, 0, 0).rotateOY(Math.random() * 360).add(x, y + j, z)
			setPosition(a, b, c)
		}
		
		fun selectModded(spirit: EntityFireSpirit, x: Int, y: Int, z: Int): EntityLivingBase? {
			if (spirit.rand.nextInt(4) != 0) return null
			
			return when (spirit.rand.nextInt(10)) {
				in 0..9 -> if (Botania.thaumcraftLoaded)
					EntityList.createEntityByName("Thaumcraft.Firebat", spirit.worldObj).apply { this as EntityFireBat
						setRandomPos(x, y, z, true)
						
						isDevil = spirit.rand.nextBoolean()
						isExplosive = !isDevil
						
						setTarget(spirit)
						
						if (isDevil)
							getEntityAttribute(SharedMonsterAttributes.attackDamage).baseValue = 6.0
						
						getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = if (isExplosive) 12.0 else 24.0
						health = maxHealth
					} as EntityMob else null
				
				else    -> null
			}
		}
		
		@SubscribeEvent
		fun onLivingUpdate(e: LivingEvent.LivingUpdateEvent) {
			if (e.entityLiving.entityData.getBoolean(TAG_RITUAL_SUMMONED))
				e.entityLiving.activePotionsMap.keys.removeAll { it != AlfheimConfigHandler.potionIDSoulburn }
		}
		
		// disable friendly-fire for ritual summoned entities
		@SubscribeEvent
		fun onLivingAttacked(e: LivingAttackEvent) {
			if (e.entityLiving.entityData.getBoolean(TAG_RITUAL_SUMMONED)) {
				e.isCanceled = e.source.isExplosion || e.source.isFireDamage || e.source.damageType == DamageSource.fall.damageType || e.source.entity?.entityData?.getBoolean(TAG_RITUAL_SUMMONED) == true
			}
		}
		
		@SubscribeEvent
		fun onDrops(e: LivingDropsEvent) {
			if (e.entityLiving.entityData.getBoolean(TAG_RITUAL_SUMMONED)) e.isCanceled = true
		}
	}
}