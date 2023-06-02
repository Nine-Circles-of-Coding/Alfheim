package alfheim.common.entity.boss.primal

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.api.entity.IMuspelheimEntity
import alfheim.client.sound.EntityBoundMovingSound
import alfheim.common.achievement.AlfheimAchievements
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem
import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem.Knowledge
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.helper.*
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.entity.*
import alfheim.common.entity.boss.primal.ai.surtr.*
import alfheim.common.entity.spell.EntitySpellFireball
import alfheim.common.item.AlfheimItems
import alfheim.common.item.equipment.tool.ItemSurtrSword
import alfheim.common.item.material.ElvenResourcesMetas
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.entity.player.*
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.util.DamageSource
import net.minecraft.world.World
import java.awt.Rectangle

class EntitySurtr(world: World): EntityPrimalBoss(world), IMuspelheimEntity {
	
	override val whirlParticleSet = 1
	override val arenaName = "Surtr"
	
	var invulnerabilityTicks
		get() = dataWatcher.getWatchableObjectInt(5)
		set(value) = dataWatcher.updateObject(5, value)

	var shouldSpinProtect
		get() = getFlag(8)
		set(value) = setFlag(8, value)
	
	var wall
		get() = getFlag(9)
		set(value) = setFlag(9, value)
	
	init {
		tasks.addTask(0, SurtrAIThirdStageStart(this))
		tasks.addTask(1, SurtrAISecondStageStart(this))
	}
	
	private fun playSounds() {
		if (!ASJUtilities.isClient || ticksExisted != 1) return
		
		mc.soundHandler.playSound(EntityBoundMovingSound(mc.thePlayer, "${ModInfo.MODID}:surtr.wall.exist") {
			volume = if (wall) 0.5f else 0.01f
			isDonePlaying = this@EntitySurtr.isDead
		})
		mc.soundHandler.playSound(PrimalBossMovingSound(this, getChargeSound()) { volume = if (!ASJBitwiseHelper.getBit(host.ultAnimationTicks, 9) && host.ultAnimationTicks in 11..69) 1f else 0.01f })
	}
	
	override fun entityInit() {
		super.entityInit()
		
		dataWatcher.addObject(5, 0) // invulnerability
	}
	
	override fun onLivingUpdate() {
		super.onLivingUpdate()
		
		playSounds()
		
		if (invulnerabilityTicks > 0) --invulnerabilityTicks
		
		if (stage < 3) return
		
		if (ticksExisted % 20 != 0) return
		
		val bb = arenaBB
		if (getEntitiesWithinAABB(worldObj, EntityMuspelheimSun::class.java, bb).isEmpty()) return
		
		val son = getEntitiesWithinAABB(worldObj, EntityMuspelson::class.java, bb).random(rand) ?: return
		if (son.attackEntityFrom(DamageSource.generic, 1f)) {
			health += 4f
			shield += 8f
		}
		
		if (ultAnimationTicks == 10) playSoundAtEntity(getChargeSound(), 1f, 1f)
	}
	
	override fun doRangedAttack(players: ArrayList<EntityPlayer>) {
		val max = if (stage > 1) 3 else 1
		for (i in 0 until max) {
			val target = (if (rand.nextInt(3) != 0) attackTarget else null) ?: players.random(rand) ?: attackTarget ?: return
			lookHelper.setLookPosition(target.posX, target.posY + target.eyeHeight, target.posZ, 10f, verticalFaceSpeed.F)
			
			EntitySpellFireball(worldObj, this).apply {
				this.target = target
				noClip = false
				setPosition(posX + Math.random() - 0.5, posY + Math.random() - 0.5, posZ + Math.random() - 0.5)
				spawn()
				playSoundAtEntity("${ModInfo.MODID}:surtr.fireball.shot", 1f, 1f)
			}
		}
	}
	
	override fun doSuperSmashAttack(target: EntityLivingBase) {
		val attacked = target.attackEntityFrom(defaultWeaponDamage(target), 10f)
		if (!attacked) return
		
		target.knockback(this, 10f)
		if (target is EntityPlayerMP) target.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(target))
		target.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDSoulburn, 100))
	}
	
	override fun canUlt(): Boolean {
		val result = super.canUlt()
		if (result) shouldSpinProtect = rand.nextBoolean()
		return result
	}
	
	override fun doSpinningAttack(tick: Int) {
		if (shouldSpinProtect) invulnerabilityTicks = 20
		
		val dist = if (shouldSpinProtect) 5.5 else 7
		val aabb = getBoundingBox(posX, posY + 1, posZ).expand(dist, 0.5, dist)
		
		val list = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, aabb)
		list.removeAll { !canTarget(it) || Vector3.entityDistance(this, it) > dist.D}
		list.forEach {
			val attacked = it.attackEntityFrom(defaultWeaponDamage(it), if (shouldSpinProtect) if (stage > 1) 2f else 1f else getEntityAttribute(SharedMonsterAttributes.attackDamage).attributeValue.F * 2 / 3 * if (stage > 1) 1.5f else 1f)
			if (!attacked) return@forEach
			
			it.knockback(this, if (shouldSpinProtect) 0.5f else 7.5f)
			if (it is EntityPlayerMP) it.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(it))
			if (!shouldSpinProtect) it.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDSoulburn, 50))
			
			playSoundAtEntity(getHitSound(), 1f, 1f)
		}
	}
	
	override fun tickWhirl(players: MutableList<EntityPlayer>) {
		super.tickWhirl(players)
		if (!whirl) return
		
		val v = Vector3()
		players.filter { Vector3.entityDistance(this, it) < 24 }.forEach {
			val dist = Vector3.entityDistance(this, it)
			if (dist >= 3) {
				val (mx, my, mz) = v.set(this).sub(it).normalize().mul(24 / dist * 0.05).add(it.motionX, it.motionY, it.motionZ)
				it.setMotion(mx, my, mz)
				if (it is EntityPlayerMP) it.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(it))
			} else
				it.attackEntityFrom(defaultWeaponDamage(it), 1f)
		}
	}
	
	override fun attackEntityFrom(source: DamageSource, damage: Float): Boolean {
		if (invulnerabilityTicks > 0) return false
		return super.attackEntityFrom(source, damage)
	}
	
	override fun getDeathSound() = "${ModInfo.MODID}:surtr.death"
	override fun getHurtSound() = "${ModInfo.MODID}:surtr.hurt"
	
	override fun getChargeSound() = "${ModInfo.MODID}:surtr.sword.charge"
	override fun getHitSound() = "${ModInfo.MODID}:surtr.sword.hit"
	override fun getRangedFormSound() = "${ModInfo.MODID}:surtr.fireball.form"
	override fun getSpinningSound() = "${ModInfo.MODID}:surtr.sword.rotate"
	override fun getStrikeSound() = "${ModInfo.MODID}:surtr.sword.strike"
	override fun getSwingSound() = "${ModInfo.MODID}:thrym.axe.swing" // same
	override fun getWhirlwindSound() = "${ModInfo.MODID}:surtr.whirlwind"
	
	override fun getAttributeValues() = doubleArrayOf(64.0, 0.95, 0.5, 3500.0)
	override fun getEquipment() = equipment
	override fun getRelics() = arrayOf(AlfheimAchievements.daolos to AlfheimItems.daolos, AlfheimAchievements.subspace to AlfheimItems.subspaceSpear)
	override fun protectorEntityClass() = EntityMuspelson::class.java
	override fun isAllie(e: Entity?) = e is EntityMuspelson
	override fun defaultWeaponDamage(target: Entity) = DamageSourceSpell.soulburn(this)
	override fun applyCustomWeaponDamage(target: Entity) {
		heldItem?.item?.let { (it as? ItemSurtrSword)?.leftClickEntity(this, target) }
	}
	override fun isShieldBreakingType(type: DamageSource) = type.elements().any(ElementalDamage.FIRE::isVulnerable)
	override fun isDamageTypeCritical(type: DamageSource) = isShieldBreakingType(type)
	override fun summonProtector() {
		for (i in 0..2) EntityMuspelson(worldObj).apply {
			setPosition(this@EntitySurtr)
			noLoot = true
			forceSpawn = true
			playSoundAtEntity("${ModInfo.MODID}:surtr.summon", 1f, 1f)
			spawn()
		}
	}
	
	override fun isFirstTime() = RagnarokHandler.surtrFirstTime
	
	override fun doFirstTimeStuff() {
		if (!RagnarokHandler.canEndSummer()) return
		RagnarokHandler.surtrFirstTime = false
		RagnarokHandler.endSummer()
	}
	
	override fun setHealth(set: Float) {
		if (set < maxHealth * 0.01f && worldObj.loadedEntityList.any { it is EntityMuspelheimSun && arenaBB.intersectsWith(it.boundingBox) })
			return
		
		super.setHealth(set)
	}
	
	override fun onDeath(source: DamageSource) {
		super.onDeath(source)
		
		worldObj.loadedEntityList.forEach {
			if (it !is EntityMuspelheimSun && it !is EntityMuspelheimSunSlash) return@forEach
			
			if (arenaBB.intersectsWith((it as Entity).boundingBox))
				it.setDead()
		}
	}
	
	override fun dropItems() {
		if (isFirstTime()) {
			if (ASJUtilities.isServer)
				playersOnArena().forEach {
					if (it !is EntityPlayerMP) return@forEach
					if (KnowledgeSystem.know(it, Knowledge.MUSPELHEIM))
						KnowledgeSystem.learn(it, Knowledge.MUSPELHEIM_POST, AlfheimLexiconData.abyss)
				}
			
//				entityDropItem(ElvenResourcesMetas.SurtrHand.stack, 3f)
		} else
			entityDropItem(ElvenResourcesMetas.MuspelheimEssence.stack(ASJUtilities.randInBounds(32, 64, rand)), 3f)
	}
	
	@SideOnly(Side.CLIENT)
	override fun getBossBarTextureRect(): Rectangle {
		if (barRect == null)
			barRect = Rectangle(0, 44, 185, 15)
		
		return barRect!!
	}
	
	@SideOnly(Side.CLIENT)
	override fun getBossBarHPTextureRect(): Rectangle {
		if (hpBarRect == null)
			hpBarRect = Rectangle(0, 103, 181, 7)
		
		return hpBarRect!!
	}
	
	@SideOnly(Side.CLIENT)
	override fun getNameColor() = shieldColor.toInt()
	
	override val shieldColor = 0xFFFF4D00U
	
	override val battleMusicDisc get() = AlfheimItems.discSurtr
	
	companion object {
		val equipment = arrayOf(AlfheimItems.surtrSword, AlfheimItems.volcanoBoots, AlfheimItems.volcanoLeggings, AlfheimItems.volcanoChest, AlfheimItems.volcanoHelmet)
		
		fun summon(world: World, x: Int, y: Int, z: Int, players: List<EntityPlayer>) {
			val e = EntitySurtr(world)
			summon(e, x, y, z, players)
		}
	}
}
