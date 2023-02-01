package alfheim.common.core.util

import alfheim.common.core.helper.*
import alfheim.common.core.helper.ElementalDamage.*
import alfheim.common.entity.boss.EntityFenrir
import alfheim.common.entity.spell.*
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.*

open class DamageSourceSpell(type: String): DamageSource(type) {
	
	companion object {
		
		/** Any anomaly */
		val anomaly = DamageSource("anomaly").setDamageBypassesArmor().setDamageIsAbsolute().setMagicDamage().setTo(PSYCHIC)
		
		/** Decay Spell */
		val bleeding = DamageSourceSpell("bleeding").setDamageBypassesArmor().setDamageIsAbsolute()!!
		
		/** Priest emblem damage */
		val faith = DamageSource("lackOfFaith").setDamageBypassesArmor().setDamageIsAbsolute().setMagicDamage().setTo(LIGHTNESS)
		
		val lightning = DamageSource("lightning").setDamageBypassesArmor().setTo(ELECTRIC)
		
		/** Death Mark Spell */
		val mark = DamageSourceSpell("mark").setDamageBypassesArmor().setDamageIsAbsolute().setMagicDamage().setTo(DARKNESS)
		
		val nifleice = DamageSource("nifleice").setDamageBypassesArmor().setDamageIsAbsolute().setTo(ICE)
		
		/** Regular poison */
		val poison = DamageSource("poison").setDamageBypassesArmor().setTo(NATURE)
		
		/** Magical poison */
		val poisonMagic = DamageSourceSpell("poison").setDamageBypassesArmor().setTo(NATURE)
		
		/** Tank Mask */
		val possession = DamageSource("possession").setDamageBypassesArmor().setDamageIsAbsolute().setMagicDamage().setTo(PSYCHIC)
		
		/** Sacrifice Spell */
		val sacrifice = DamageSourceSpell("sacrifice").setDamageBypassesArmor().setDamageIsAbsolute().setMagicDamage().setTo(PSYCHIC).setTo(DARKNESS)
		
		/** Red Flame */
		val soulburn = DamageSource("soulburn").setDamageBypassesArmor().setDamageIsAbsolute().setTo(PSYCHIC).setTo(FIRE)
		
		val wind = DamageSourceSpell("wind").setDamageBypassesArmor().setTo(AIR)
		
		fun explosion(dm: EntitySpellDriftingMine, caster: EntityLivingBase?) =
			EntityDamageSourceIndirectSpell("explosion.player", caster, dm).setFireDamage().setExplosion()!!
		
		fun fenrirroar(fenrir: EntityFenrir) =
			EntityDamageSource("fenrirroar", fenrir).setDamageBypassesArmor().setTo(AIR).setTo(ICE)
		
		fun fireball(fb: EntitySpellFireball, caster: EntityLivingBase?) =
			EntityDamageSourceIndirectSpell("fireball", caster, fb).setFireDamage().setExplosion().setProjectile()!!
		
		fun firewall(fw: EntitySpellFirewall, caster: EntityLivingBase?) =
			EntityDamageSourceIndirectSpell("firewall", caster, fw).setFireDamage()!!
		
		fun frost(caster: EntityLivingBase?) =
			EntityDamageSource("frost", caster).setDamageBypassesArmor().setMagicDamage().setTo(ICE)
		
		fun gravity(gt: EntitySpellGravityTrap, caster: EntityLivingBase?) =
			EntityDamageSourceIndirectSpell("gravity", caster, gt).setDamageBypassesArmor()!!
		
		fun godslayer(slayer: EntityPlayer?, creative: Boolean) =
			EntityDamageSource("player", slayer).setDamageBypassesArmor().setDamageIsAbsolute().apply { if (creative) setDamageAllowedInCreativeMode() }.setTo(PSYCHIC)
		
		fun hammerfall(caster: EntityLivingBase?) =
			EntityDamageSourceSpell("hammerfall", caster).setDamageBypassesArmor().setTo(EARTH)
		
		fun lightning(attacker: EntityLivingBase?) =
			EntityDamageSource("indirectLightning", attacker).setDamageBypassesArmor().setTo(ELECTRIC)
		
		fun lightningIndirect(st: Entity, caster: EntityLivingBase?) =
			EntityDamageSourceIndirectSpell("indirectLightning", caster, st).setDamageBypassesArmor().setTo(ELECTRIC)
		
		fun magic(attacker: EntityLivingBase?) =
			EntityDamageSource("indirectMagic", attacker).setDamageBypassesArmor().setMagicDamage()!!
		
		fun missile(im: EntitySpellIsaacMissile, caster: EntityLivingBase?) =
			EntityDamageSourceIndirectSpell("missile", caster, im).setMagicDamage()!!
		
		fun mortar(mt: EntitySpellMortar, caster: EntityLivingBase?) =
			EntityDamageSourceIndirectSpell("mortar", caster, mt).setProjectile().setTo(EARTH)
		
		fun nifleice(attacker: EntityLivingBase?) =
			EntityDamageSource("nifleice", attacker).setDamageBypassesArmor().setDamageIsAbsolute().setTo(ICE)
		
		fun sacrifice(caster: EntityLivingBase?) =
			EntityDamageSourceSpell("darkness_FF", caster).setDamageBypassesArmor().setDamageIsAbsolute().setMagicDamage().setTo(DARKNESS).setTo(PSYCHIC)
		
		fun shadow(caster: EntityLivingBase?) =
			EntityDamageSource("shadow", caster).setDamageBypassesArmor().setMagicDamage().setTo(DARKNESS)
		
		/** Shadow vortex type of damage */
		fun shadowSpell(caster: EntityLivingBase?) =
			EntityDamageSourceSpell("shadow", caster).setDamageBypassesArmor().setMagicDamage().setTo(DARKNESS)
		
		fun soulburn(attacker: EntityLivingBase?) =
			EntityDamageSource("soulburn", attacker).setDamageBypassesArmor().setDamageIsAbsolute().setTo(FIRE).setTo(PSYCHIC)
		
		/** Some water blades (?) type of damage  */
		fun water(caster: EntityLivingBase?) =
			EntityDamageSourceSpell("water", caster).setDamageBypassesArmor().setTo(WATER)
		
		fun windblade(wb: EntitySpellWindBlade, caster: EntityLivingBase?) =
			EntityDamageSourceIndirectSpell("windblade", caster, wb).setDamageBypassesArmor().setTo(AIR)
	}
}

open class EntityDamageSourceSpell(source: String, protected val attacker: Entity?): DamageSourceSpell(source) {
	
	override fun getEntity() = attacker
	
	override fun func_151519_b(target: EntityLivingBase): IChatComponent {
		val itemstack = if (attacker is EntityLivingBase) attacker.heldItem else null
		val s = "death.attack.$damageType"
		val s1 = "$s.item"
		val component = attacker?.func_145748_c_() ?: ChatComponentText("null")
		return if (itemstack != null && itemstack.hasDisplayName() && StatCollector.canTranslate(s1)) ChatComponentTranslation(s1, target.func_145748_c_(), component, itemstack.func_151000_E()) else ChatComponentTranslation(s, target.func_145748_c_(), component)
	}
	
	override fun isDifficultyScaled() =
		attacker != null && attacker is EntityLivingBase && attacker !is EntityPlayer
}

open class EntityDamageSourceIndirectSpell(type: String, attacker: Entity?, val dealer: Entity?): EntityDamageSourceSpell(type, attacker) {
	
	override fun getSourceOfDamage() = dealer
	
	override fun func_151519_b(target: EntityLivingBase): IChatComponent {
		val ichatcomponent = if (attacker == null) dealer?.func_145748_c_() ?: ChatComponentText("null") else attacker.func_145748_c_()
		val itemstack = if (attacker is EntityLivingBase) attacker.heldItem else null
		val s = "death.attack.$damageType"
		val s1 = "$s.item"
		return if (itemstack != null && itemstack.hasDisplayName() && StatCollector.canTranslate(s1)) ChatComponentTranslation(s1, target.func_145748_c_(), ichatcomponent, itemstack.func_151000_E()) else ChatComponentTranslation(s, target.func_145748_c_(), ichatcomponent)
	}
}