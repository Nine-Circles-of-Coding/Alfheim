package alfheim.common.core.asm.hook

import alfheim.common.core.helper.ElementalDamage.*
import alfheim.common.core.helper.setTo
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.entity.EntityMuspelson
import alfheim.common.entity.boss.EntityDedMoroz
import gloomyfolken.hooklib.asm.*
import gloomyfolken.hooklib.asm.Hook.ReturnValue
import net.minecraft.entity.*
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.entity.monster.EntitySlime
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.*
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import thaumcraft.api.aspects.Aspect
import thaumcraft.common.entities.monster.*
import thaumcraft.common.entities.monster.boss.EntityEldritchWarden
import thaumcraft.common.entities.projectile.*
import thaumcraft.common.items.equipment.ItemElementalSword
import thaumcraft.common.items.wands.foci.ItemFocusShock
import vazkii.botania.api.internal.IManaBurst
import vazkii.botania.common.entity.*
import vazkii.botania.common.item.equipment.tool.ItemThunderSword
import vazkii.botania.common.item.equipment.tool.terrasteel.ItemTerraSword
import vazkii.botania.common.item.relic.ItemRelic
import java.util.*

@Suppress("unused", "UNUSED_PARAMETER")
object ElementalDamageAdapter {
	
	var setAir = false
	var setAlien = false
	var setDarkness = false
	var setEarth = false
	var setElectric = false
	var setFire = false
	var setIce = false
	var setLightness = false
	var setNature = false
	var setPsychic = false
	var setWater = false
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "<init>")
	fun `DamageSource$init`(thiz: DamageSource, type: String?) {
		thiz.set()
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ReturnCondition.ALWAYS)
	fun causeIndirectMagicDamage(static: DamageSource?, entity1: Entity?, entity2: Entity?, @ReturnValue src: DamageSource): DamageSource {
		return src.set()
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ReturnCondition.ALWAYS)
	fun causeMobDamage(static: DamageSource?, attacker: EntityLivingBase?, @ReturnValue src: DamageSource): DamageSource {
		return src.set()
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ReturnCondition.ALWAYS)
	fun causePlayerDamage(static: DamageSource?, attacker: EntityPlayer?, @ReturnValue src: DamageSource): DamageSource {
		return src.set()
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ReturnCondition.ALWAYS)
	fun causeThrownDamage(static: DamageSource?, entity1: Entity?, entity2: Entity?, @ReturnValue src: DamageSource): DamageSource {
		return src.set()
	}
	
	private fun DamageSource.set(): DamageSource {
		if (setAir) setTo(AIR)
		if (setAlien) setTo(ALIEN)
		if (setDarkness) setTo(DARKNESS)
		if (setEarth) setTo(EARTH)
		if (setElectric) setTo(ELECTRIC)
		if (setFire) setTo(FIRE)
		if (setLightness) setTo(LIGHTNESS)
		if (setIce) setTo(ICE)
		if (setNature) setTo(NATURE)
		if (setPsychic) setTo(PSYCHIC)
		if (setWater) setTo(WATER)
		
		return this
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "hitEntity")
	fun hitEntityPre(sword: ItemThunderSword, stack: ItemStack?, entity: EntityLivingBase?, attacker: EntityLivingBase?): Boolean {
		setElectric = true
		return false
	}
	
	@JvmStatic
	@Hook(targetMethod = "hitEntity", injectOnExit = true)
	fun hitEntityPost(sword: ItemThunderSword, stack: ItemStack?, entity: EntityLivingBase?, attacker: EntityLivingBase?): Boolean {
		setElectric = false
		return false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "updateBurst")
	fun updateBurstPre(sword: ItemTerraSword, burst: IManaBurst?, stack: ItemStack?) {
		setNature = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "updateBurst", injectOnExit = true)
	fun updateBurstPost(sword: ItemTerraSword, burst: IManaBurst?, stack: ItemStack?) {
		setNature = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "onImpact")
	fun onImpactPre(entity: EntityThornChakram, mop: MovingObjectPosition?) {
		setNature = true
		setFire = entity.isFire
	}
	
	@JvmStatic
	@Hook(targetMethod = "onImpact", injectOnExit = true)
	fun onImpactPost(entity: EntityThornChakram, mop: MovingObjectPosition?) {
		setNature = false
		setFire = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "onUpdate")
	fun onUpdatePre(entity: EntityBabylonWeapon) {
		setLightness = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "onUpdate", injectOnExit = true)
	fun onUpdatePost(entity: EntityBabylonWeapon) {
		setLightness = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "attackTargetEntityWithCurrentItem")
	fun attackTargetEntityWithCurrentItemPre(sword: ItemElementalSword, target: Entity?, player: EntityPlayer?) {
		setAir = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "attackTargetEntityWithCurrentItem", injectOnExit = true)
	fun attackTargetEntityWithCurrentItemPost(sword: ItemElementalSword, target: Entity?, player: EntityPlayer?) {
		setAir = false
	}
	
	@JvmStatic
	@Hook(targetMethod = "inflictDamage")
	fun inflictDamagePre(entity: EntityPrimalArrow, mop: MovingObjectPosition?): Boolean {
		when (entity.type) {
			0 -> setAir = true
			1 -> setFire = true
			2 -> setWater = true
			3 -> setEarth = true
			4 -> setLightness = true
			5 -> setDarkness = true
			else -> return false
		}
		
		return false
	}
	
	@JvmStatic
	@Hook(targetMethod = "inflictDamage", injectOnExit = true)
	fun inflictDamagePost(entity: EntityPrimalArrow, mop: MovingObjectPosition?): Boolean {
		setAir = false
		setFire = false
		setWater = false
		setEarth = false
		setLightness = false
		setDarkness = false
		
		return false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "onImpact")
	fun onImpactPre(entity: EntityFrostShard, mop: MovingObjectPosition?) {
		setIce = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "onImpact", injectOnExit = true)
	fun onImpactPost(entity: EntityFrostShard, mop: MovingObjectPosition?) {
		setIce = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "doLightningBolt")
	fun doLightningBoltPre(focus: ItemFocusShock, stack: ItemStack?, p: EntityPlayer?, count: Int) {
		setElectric = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "doLightningBolt", injectOnExit = true)
	fun doLightningBoltPost(focus: ItemFocusShock, stack: ItemStack?, p: EntityPlayer?, count: Int) {
		setElectric = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "onImpact")
	fun onImpactPre(entity: EntityShockOrb, mop: MovingObjectPosition?) {
		setElectric = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "onImpact", injectOnExit = true)
	fun onImpactPost(entity: EntityShockOrb, mop: MovingObjectPosition?) {
		setElectric = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "onImpact")
	fun onImpactPre(entity: EntityPechBlast, mop: MovingObjectPosition?) {
		setNature = true
		setDarkness = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "onImpact", injectOnExit = true)
	fun onImpactPost(entity: EntityPechBlast, mop: MovingObjectPosition?) {
		setNature = false
		setDarkness = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "onImpact")
	fun onImpactPre(entity: EntitySnowball, mop: MovingObjectPosition?) {
		setIce = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "onImpact", injectOnExit = true)
	fun onImpactPost(entity: EntitySnowball, mop: MovingObjectPosition?) {
		setIce = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "onImpact")
	fun onImpactPre(entity: EntityWitherSkull, mop: MovingObjectPosition?) {
		setDarkness = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "onImpact", injectOnExit = true)
	fun onImpactPost(entity: EntityWitherSkull, mop: MovingObjectPosition?) {
		setDarkness = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "onCollideWithPlayer")
	fun onCollideWithPlayerPre(entity: EntitySlime, player: EntityPlayer?) {
		setNature = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "onCollideWithPlayer", injectOnExit = true)
	fun onCollideWithPlayerPost(entity: EntitySlime, player: EntityPlayer?) {
		setNature = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "attackEntitiesInList")
	fun attackEntitiesInListPre(entity: EntityDragon, list: List<Entity>) {
		setAlien = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "attackEntitiesInList", injectOnExit = true)
	fun attackEntitiesInListPost(entity: EntityDragon, list: List<Entity>) {
		setAlien = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "attackEntityAsMob")
	fun attackEntityAsMobPre(entity: EntityEldritchGuardian, target: Entity?) {
		setAlien = true
		setDarkness = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "attackEntityAsMob", injectOnExit = true)
	fun attackEntityAsMobPost(entity: EntityEldritchGuardian, target: Entity?) {
		setAlien = false
		setDarkness = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "attackEntityAsMob")
	fun attackEntityAsMobPre(entity: EntityEldritchWarden, target: Entity?) {
		setAlien = true
		setDarkness = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "attackEntityAsMob", injectOnExit = true)
	fun attackEntityAsMobPost(entity: EntityEldritchWarden, target: Entity?) {
		setAlien = false
		setDarkness = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "attackEntityAsMob")
	fun attackEntityAsMobPre(entity: EntityDedMoroz, target: Entity?) {
		setIce = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "attackEntityAsMob", injectOnExit = true)
	fun attackEntityAsMobPost(entity: EntityDedMoroz, target: Entity?) {
		setIce = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "attackEntityAsMob")
	fun attackEntityAsMobPre(entity: EntityMuspelson, target: Entity?) {
		setFire = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "attackEntityAsMob", injectOnExit = true)
	fun attackEntityAsMobPost(entity: EntityMuspelson, target: Entity?) {
		setFire = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "onImpact")
	fun onImpactPre(entity: EntityEldritchOrb, mop: MovingObjectPosition?) {
		setAlien = true
		setDarkness = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "onImpact", injectOnExit = true)
	fun onImpactPost(entity: EntityEldritchOrb, mop: MovingObjectPosition?) {
		setAlien = false
		setDarkness = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "onImpact")
	fun onImpactPre(entity: EntityGolemOrb, mop: MovingObjectPosition?) {
		setDarkness = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "onImpact", injectOnExit = true)
	fun onImpactPost(entity: EntityGolemOrb, mop: MovingObjectPosition?) {
		setDarkness = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "attackEntity")
	fun attackEntityPre(entity: EntityFireBat, target: Entity?, dist: Float) {
		setFire = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "attackEntity", injectOnExit = true)
	fun attackEntityPost(entity: EntityFireBat, target: Entity?, dist: Float) {
		setFire = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "attackEntity")
	fun attackEntityPre(entity: EntityMindSpider, target: Entity?, dist: Float) {
		setPsychic = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "attackEntity", injectOnExit = true)
	fun attackEntityPost(entity: EntityMindSpider, target: Entity?, dist: Float) {
		setPsychic = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "onCollideWithPlayer")
	fun onCollideWithPlayerPre(entity: EntityThaumicSlime, player: EntityPlayer?) {
		setDarkness = true
	}
	
	@JvmStatic
	@Hook(targetMethod = "onCollideWithPlayer", injectOnExit = true)
	fun onCollideWithPlayerPost(entity: EntityThaumicSlime, player: EntityPlayer?) {
		setDarkness = false
	}
	
	
	@JvmStatic
	@Hook(targetMethod = "updateEntityActionState")
	fun updateEntityActionStatePre(entity: EntityWisp) {
		when (entity.type) {
			Aspect.AIR.tag       -> setAir = true
			Aspect.EARTH.tag     -> setEarth = true
			Aspect.FIRE.tag      -> setFire = true
			Aspect.ORDER.tag     -> setLightness = true
			Aspect.ENTROPY.tag   -> setElectric = true
			Aspect.DEATH.tag,
			Aspect.DARKNESS.tag,
			Aspect.UNDEAD.tag    -> setDarkness = true
			Aspect.WATER.tag     -> setWater = true
			Aspect.ELDRITCH.tag  -> setAlien = true
			Aspect.POISON.tag    -> setNature = true
		}
	}
	
	@JvmStatic
	@Hook(targetMethod = "updateEntityActionState", injectOnExit = true)
	fun updateEntityActionStatePost(entity: EntityWisp) {
		when (entity.type) {
			Aspect.AIR.tag       -> setAir = false
			Aspect.EARTH.tag     -> setEarth = false
			Aspect.FIRE.tag      -> setFire = false
			Aspect.ORDER.tag     -> setLightness = false
			Aspect.ENTROPY.tag   -> setElectric = false
			Aspect.DEATH.tag,
			Aspect.DARKNESS.tag,
			Aspect.UNDEAD.tag    -> setDarkness = false
			Aspect.WATER.tag     -> setWater = false
			Aspect.ELDRITCH.tag  -> setAlien = false
			Aspect.POISON.tag    -> setNature = false
		}
	}
	
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun getElements(creeper: EntityCreeper) = EnumSet.of(if (creeper.powered) ELECTRIC else COMMON)!!
	
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun getElements(wisp: EntityWisp) = EnumSet.of(when (wisp.type) {
	    Aspect.EARTH.tag    -> EARTH
	    Aspect.FIRE.tag     -> FIRE
	    Aspect.ORDER.tag    -> LIGHTNESS
	    Aspect.ENTROPY.tag  -> ELECTRIC
	    Aspect.DEATH.tag,
	    Aspect.DARKNESS.tag,
	    Aspect.UNDEAD.tag   -> DARKNESS
	    Aspect.WATER.tag    -> WATER
	    Aspect.ELDRITCH.tag -> ALIEN
	    Aspect.POISON.tag   -> NATURE
	    else                -> AIR
	})!!
	
	
	
	// util
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ReturnCondition.ALWAYS)
	fun damageSource(static: ItemRelic?, @ReturnValue src: DamageSource): DamageSource {
		return src.setTo(PSYCHIC).setTo(LIGHTNESS)
	}
	
	@JvmStatic
	@Hook
	fun setFireDamage(src: DamageSource): DamageSource {
		return src.setTo(FIRE)
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun onStruckByLightning(entity: Entity, bolt: EntityLightningBolt?) {
		entity.attackEntityFrom(DamageSourceSpell.lightning, 5f)
	}
}
