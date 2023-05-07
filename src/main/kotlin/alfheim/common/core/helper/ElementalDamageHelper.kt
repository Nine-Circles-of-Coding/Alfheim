@file:Suppress("unused")

package alfheim.common.core.helper

import alexsocol.asjlib.*
import alfheim.api.entity.*
import alfheim.common.core.helper.ElementalDamage.*
import alfheim.common.core.helper.ElementalDamageBridge.*
import cpw.mods.fml.common.eventhandler.*
import net.minecraft.entity.*
import net.minecraft.util.DamageSource
import net.minecraftforge.event.entity.living.*
import java.util.*

object ElementalDamageHandler {
	
	init {
		for (ed in ElementalDamage.values()) require(ElementalDamageBridge.valueOf("${ed}_").real == ed)
		
		DamageSource.inWall.setTo(EARTH)
		DamageSource.drown.setTo(WATER)
		DamageSource.cactus.setTo(NATURE)
		DamageSource.fall.setTo(EARTH)
		DamageSource.wither.setTo(DARKNESS)
		DamageSource.anvil.setTo(EARTH)
		DamageSource.fallingBlock.setTo(EARTH)
	}
	
	private val elementalMobs: Map<String, EnumSet<ElementalDamage>> get() = mapOf(
		"Blaze" to EnumSet.of(FIRE),
		"EnderDragon" to EnumSet.of(ALIEN),
		"Enderman" to EnumSet.of(ALIEN),
		"Ghast" to EnumSet.of(AIR, PSYCHIC),
		"LavaSlime" to EnumSet.of(FIRE),
		"MushroomCow" to EnumSet.of(NATURE),
		"SnowMan" to EnumSet.of(ICE),
		"Slime" to EnumSet.of(NATURE, WATER),
		"VillagerGolem" to EnumSet.of(EARTH),
		"WitherBoss" to EnumSet.of(DARKNESS),
		"alfheim.Jellyfish" to EnumSet.of(WATER),
		"alfheim.Muspelson" to EnumSet.of(FIRE, EARTH),
		"alfheim.DedMoroz" to EnumSet.of(ICE),
		"alfheim.SnowSprite" to EnumSet.of(ICE),
		"Thaumcraft.EldritchGolem" to EnumSet.of(EARTH),
		"Thaumcraft.EldritchGuardian" to EnumSet.of(ALIEN, DARKNESS, PSYCHIC),
		"Thaumcraft.EldritchWarden" to EnumSet.of(ALIEN, DARKNESS, PSYCHIC),
		"Thaumcraft.Firebat" to EnumSet.of(FIRE),
		"Thaumcraft.Golem" to EnumSet.of(EARTH),
		"Thaumcraft.MindSpider" to EnumSet.of(PSYCHIC),
		"Thaumcraft.ThaumSlime" to EnumSet.of(WATER, DARKNESS),
		"Thaumcraft.Wisp" to EnumSet.of(AIR),
		"ThermalFoundation.Blizz" to EnumSet.of(ICE),
		"ThermalFoundation.Blitz" to EnumSet.of(ELECTRIC),
		"ThermalFoundation.Basalz" to EnumSet.of(ICE),
	                                                                          )
	
	val EntityLivingBase.elements: Set<ElementalDamage>
		get() {
			if (this is IElementalEntity) return elements
			if (this is IMuspelheimEntity) return EnumSet.of(FIRE)
			if (this is INiflheimEntity) return EnumSet.of(ICE)
			
			return elementalMobs[EntityList.getEntityString(this)] ?: EnumSet.of(COMMON)
		}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onAttacked(e: LivingAttackEvent) {
		val targetEl = e.entityLiving.elements
		val attackEl = e.source.elements()
		
		e.isCanceled = attackEl.any { targetEl.any(it::isImmune) }
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun onHurt(e: LivingHurtEvent) {
		val targetEl = e.entityLiving.elements
		val attackEl = e.source.elements()
		
		if (targetEl.any { attackEl.any(it::isVulnerable) } ) e.ammount *= 2f
		if (targetEl.any { attackEl.any(it::isResistant) } ) e.ammount *= 0.5f
	}
}

private enum class ElementalDamageBridge {
	COMMON_, FIRE_, WATER_, AIR_, EARTH_, ICE_, ELECTRIC_, NATURE_, LIGHTNESS_, DARKNESS_, PSYCHIC_, ALIEN_;
	val real get() = ElementalDamage.values()[ordinal]
}

enum class ElementalDamage(private val x2: Array<ElementalDamageBridge>, private val x05: Array<ElementalDamageBridge>) {
	COMMON(arrayOf(), arrayOf()),
	FIRE(arrayOf(WATER_, EARTH_), arrayOf(NATURE_, ICE_)),
	WATER(arrayOf(ELECTRIC_, NATURE_), arrayOf(FIRE_, ICE_)),
	AIR(arrayOf(ELECTRIC_, ICE_), arrayOf(EARTH_, NATURE_)),
	EARTH(arrayOf(WATER_, NATURE_), arrayOf(FIRE_, ELECTRIC_)),
	ICE(arrayOf(FIRE_, ELECTRIC_), arrayOf(PSYCHIC_, NATURE_)),
	ELECTRIC(arrayOf(FIRE_, ICE_), arrayOf(NATURE_, AIR_)),
	NATURE(arrayOf(FIRE_, ICE_), arrayOf(WATER_, EARTH_)),
	LIGHTNESS(arrayOf(DARKNESS_), arrayOf(PSYCHIC_)),
	DARKNESS(arrayOf(LIGHTNESS_), arrayOf(ALIEN_)),
	PSYCHIC(arrayOf(LIGHTNESS_, DARKNESS_), arrayOf(COMMON_)),
	ALIEN(arrayOf(LIGHTNESS_), arrayOf(DARKNESS_));
	
	fun isVulnerable(type: ElementalDamage): Boolean {
		return ElementalDamageBridge.values()[type.ordinal] in x2
	}
	
	fun isResistant(type: ElementalDamage): Boolean {
		return ElementalDamageBridge.values()[type.ordinal] in x05
	}
	
	fun isImmune(type: ElementalDamage): Boolean {
		return if (type == COMMON) false else this == type
	}
}

interface IElementalEntity {
	val elements: EnumSet<ElementalDamage>
}

fun DamageSource.setTo(type: ElementalDamage): DamageSource {
	alfheim_synthetic_elementalFlag = if (type == COMMON) 0 else ASJBitwiseHelper.setBit(alfheim_synthetic_elementalFlag, type.ordinal, true)
	return this
}

fun DamageSource.isOf(type: ElementalDamage): Boolean {
	return if (type == COMMON) alfheim_synthetic_elementalFlag == 0 else {
		val stored = ASJBitwiseHelper.getBit(alfheim_synthetic_elementalFlag, type.ordinal)
		if (type == FIRE) isFireDamage || stored else stored
	}
}

fun DamageSource.elements(): EnumSet<ElementalDamage> {
	return when (damageType) {
		"basalz" -> EnumSet.of(EARTH)
		"blitz"  -> EnumSet.of(ELECTRIC)
		"blizz"  -> EnumSet.of(ICE)
		else     -> EnumSet.copyOf(ElementalDamage.values().filter { isOf(it) })
	}
}