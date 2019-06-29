package alfheim.common.core.asm

import alfheim.AlfheimCore
import alfheim.common.core.handler.CardinalSystem.PartySystem
import alfheim.common.core.registry.AlfheimRegistry
import gloomyfolken.hooklib.asm.Hook
import gloomyfolken.hooklib.asm.Hook.ReturnValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.*

object AlfheimHPHooks {
	
	@JvmStatic
	@Hook(injectOnExit = true, isMandatory = true)
	fun getHealth(e: EntityLivingBase, @ReturnValue hp: Float): Float {
		return if (AlfheimCore.enableMMO && AlfheimRegistry.leftFlameIsInitialized() && e.activePotionsMap != null && e.isPotionActive(AlfheimRegistry.leftFlame))
			0.000000000000000000000000000000000000000000001f
		else
			hp
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, isMandatory = true)
	fun getMaxHealth(e: EntityLivingBase, @ReturnValue hp: Float): Float {
		return if (AlfheimCore.enableMMO && AlfheimRegistry.leftFlameIsInitialized() && e.activePotionsMap != null && e.isPotionActive(AlfheimRegistry.leftFlame))
			0.0f
		else
			hp
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, isMandatory = true)
	fun setHealth(e: EntityLivingBase, hp: Float) {
		var hp = hp
		if (!AlfheimCore.enableMMO)
		// e.getDataWatcher().updateObject(6, Float.valueOf(MathHelper.clamp_float(hp, 0.0F, e.getMaxHealth())));
			return
		
		val flame = AlfheimRegistry.leftFlameIsInitialized()
		if (flame && e.activePotionsMap != null && e.isPotionActive(AlfheimRegistry.leftFlame))
			hp = 0.000000000000000000000000000000000000000000001f
		
		if (AlfheimRegistry.sharedHPIsInitialized() && e.activePotionsMap != null && !e.isPotionActive(AlfheimRegistry.sharedHP)) {
			if (flame) e.dataWatcher.updateObject(6, MathHelper.clamp_float(hp, 0.0f, e.maxHealth))
			return
		}
		
		val pt = PartySystem.getMobParty(e)
		if (pt == null) {
			if (flame) e.dataWatcher.updateObject(6, MathHelper.clamp_float(hp, 0.0f, e.maxHealth))
			return
		}
		
		val mr = arrayOfNulls<EntityLivingBase>(pt.count)
		for (i in 0 until pt.count) mr[i] = pt[i]
		
		for (entityLivingBase in mr) {
			if (entityLivingBase != null) {
				entityLivingBase.dataWatcher.updateObject(6, MathHelper.clamp_float(hp, 0.0f, entityLivingBase.maxHealth))
				if (hp < 0.0f) entityLivingBase.onDeath(DamageSource.outOfWorld)
			}
		}
	}
}
