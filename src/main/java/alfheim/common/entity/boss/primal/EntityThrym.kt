package alfheim.common.entity.boss.primal

import alexsocol.asjlib.*
import alexsocol.asjlib.math.*
import alfheim.api.ModInfo
import alfheim.api.entity.INiflheimEntity
import alfheim.common.achievement.AlfheimAchievements
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem
import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem.Knowledge
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.helper.*
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.entity.*
import alfheim.common.entity.boss.EntityDedMoroz
import alfheim.common.entity.boss.primal.ai.thrym.*
import alfheim.common.item.AlfheimItems
import alfheim.common.item.equipment.tool.ItemThrymAxe
import alfheim.common.item.material.ElvenResourcesMetas
import alfheim.common.lexicon.AlfheimLexiconData
import alfheim.common.potion.PotionEternity
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.entity.player.*
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.potion.Potion
import net.minecraft.util.DamageSource
import net.minecraft.world.World
import java.awt.Rectangle
import kotlin.math.*

class EntityThrym(world: World): EntityPrimalBoss(world), INiflheimEntity {
	
	override val whirlParticleSet = 0
	override val arenaName = "Thrym"
	
	var sucks
		get() = getFlag(8)
		set(value) = setFlag(8, value)
	
	init {
		tasks.addTask(0, ThrymAIThirdStageStart(this))
		
		tasks.addTask(1, ThrymAISecondStageStart(this))
		
		mc.soundHandler.playSound(PrimalBossMovingSound(this, getChargeSound()) { host.ultAnimationTicks.also { volume = if (sucks || !ASJBitwiseHelper.getBit(it, 9) && it in 11..69) 1f else 0f } })
	}
	
	override fun doRangedAttack(players: ArrayList<EntityPlayer>) {
		for (c in 0..ASJUtilities.randInBounds(3, 5, rand)) {
			val target = (if (rand.nextInt(3) != 0) attackTarget else null) ?: players.random(rand) ?: attackTarget ?: continue
			lookHelper.setLookPosition(target.posX, target.posY + target.eyeHeight, target.posZ, 10f, verticalFaceSpeed.F)
			
			EntityIcicle(worldObj, this).apply {
				playSoundAtEntity("${ModInfo.MODID}:thrym.icicle.form", 0.1f, 1f)
				this.target = target
				setPosition(posX + Math.random() - 0.5, posY + Math.random() - 0.5, posZ + Math.random() - 0.5)
				spawn()
				playSoundAtEntity("${ModInfo.MODID}:thrym.icicle.shot", 0.1f, 1f)
			}
		}
	}
	
	override fun doSuperSmashAttack(target: EntityLivingBase) {
		val attacked = target.attackEntityFrom(defaultWeaponDamage(target), getEntityAttribute(SharedMonsterAttributes.attackDamage).attributeValue.F * if (stage > 1) 2f else 1.5f)
		if (!attacked) return
		
		target.knockback(this, 10f)
		if (target is EntityPlayerMP) target.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(target))
		target.addPotionEffect(PotionEffectU(Potion.moveSlowdown.id, 100, 4))
		target.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDIceLens, 100))
	}
	
	override fun doSpinningAttack(tick: Int) {
		val stage = stage
		val speed = if (stage > 1) 20 else 10
		
		val x = posX - cos(Math.toRadians(-tick.D * speed)) * 5
		val y = posY + 1.5
		val z = posZ + sin(Math.toRadians(-tick.D * speed)) * 5
		
		obb.fromParams(7, 1, 7).rotate(-tick.D * speed, Vector3.oY).translate(x, y, z)
		
		val list = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, obb.toAABB())
		list.removeAll { !canTarget(it) || !obb.intersectsWith(it.boundingBox) }
		list.forEach {
			val attacked = it.attackEntityFrom(defaultWeaponDamage(it), getEntityAttribute(SharedMonsterAttributes.attackDamage).attributeValue.F * 2 / 3 * if (stage > 1) 1.5f else 1f)
			if (!attacked) return@forEach
			
			it.knockback(this, 7.5f)
			if (it is EntityPlayerMP) it.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(it))
			it.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDEternity, 50, PotionEternity.ATTACK))
			
			playSoundAtEntity(getHitSound(), 1f, 1f)
		}
	}
	
	override fun getDeathSound() = "${ModInfo.MODID}:thrym.death"
	override fun getHurtSound() = "${ModInfo.MODID}:thrym.hurt"
	
	override fun getChargeSound() = "${ModInfo.MODID}:thrym.suction"
	override fun getHitSound() = "${ModInfo.MODID}:thrym.axe.hit"
	override fun getSpinningSound() = "${ModInfo.MODID}:thrym.axe.rotate"
	override fun getStrikeSound() = "${ModInfo.MODID}:thrym.axe.strike"
	override fun getSwingSound() = "${ModInfo.MODID}:thrym.axe.swing"
	override fun getWhirlwindSound() = "${ModInfo.MODID}:thrym.whirlwind"
	
	override fun getAttributeValues() = doubleArrayOf(64.0, 0.95, 0.5, 3000.0)
	override fun getEquipment() = equipment
	override fun getRelics() = arrayOf(AlfheimAchievements.mjolnir to AlfheimItems.mjolnir, AlfheimAchievements.gjallarhorn to AlfheimItems.gjallarhorn)
	override fun protectorEntityClass() = EntityDedMoroz::class.java
	override fun isAllie(e: Entity?) = e is EntityDedMoroz || e is EntitySnowSprite
	override fun defaultWeaponDamage(target: Entity) = DamageSourceSpell.nifleice(this)
	override fun applyCustomWeaponDamage(target: Entity) {
		heldItem?.item?.let { (it as? ItemThrymAxe)?.leftClickEntity(this, target) }
	}
	override fun isShieldBreakingType(type: DamageSource) = type.damageType == "fireball"
	override fun isDamageTypeCritical(type: DamageSource) = type.elements().any(ElementalDamage.ICE::isVulnerable)
	override fun summonProtector() = EntityDedMoroz(worldObj, posX, posY, posZ).apply {
		noLoot = true
		forceSpawn = true
		playSoundAtEntity("${ModInfo.MODID}:thrym.summon", 1f, 1f)
		spawn()
	}
	
	override fun isFirstTime() = RagnarokHandler.thrymFirstTime
	
	override fun doFirstTimeStuff() {
		if (!RagnarokHandler.canStartWinter()) return
		RagnarokHandler.thrymFirstTime = false
		RagnarokHandler.startWinter()
	}
	
	override fun dropItems() {
		if (isFirstTime()) {
			if (ASJUtilities.isServer) {
				var gain = 0
				playersOnArena().forEach {
					if (it !is EntityPlayerMP) return@forEach
					
					if (KnowledgeSystem.know(it, Knowledge.NIFLHEIM) && KnowledgeSystem.learn(it, Knowledge.NIFLHEIM_POST, AlfheimLexiconData.abyss))
						gain++
				}
				if (gain > 0)
					entityDropItem(ElvenResourcesMetas.WisdomBottle.stack(gain), 3f)
			}
		} else
			entityDropItem(ElvenResourcesMetas.NiflheimEssence.stack(ASJUtilities.randInBounds(32, 64, rand)), 3f)
	}
	
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
	override fun getNameColor() = 0x99A6BF
	
	override val shieldColor = 0xFFBFF4FFU
	
	override val battleMusicDisc get() = AlfheimItems.discThrym
	
	companion object {
		
		private val obb = OrientedBB()
		val equipment = arrayOf(AlfheimItems.thrymAxe, AlfheimItems.snowBoots, AlfheimItems.snowLeggings, AlfheimItems.snowChest, AlfheimItems.snowHelmet)
		
		fun summon(world: World, x: Int, y: Int, z: Int, players: List<EntityPlayer>) {
			val e = EntityThrym(world)
			summon(e, x, y, z, players)
		}
	}
}
