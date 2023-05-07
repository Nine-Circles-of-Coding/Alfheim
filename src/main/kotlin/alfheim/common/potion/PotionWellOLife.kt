package alfheim.common.potion

import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.spell.water.SpellWellOLife
import net.minecraft.entity.EntityLivingBase

object PotionWellOLife: PotionAlfheim(AlfheimConfigHandler.potionIDWellOLife, "wellolife", false, 0x00FFFF) {
	
	override fun isReady(time: Int, ampl: Int) = AlfheimConfigHandler.enableMMO && time % 10 == 0
	
	override fun performEffect(living: EntityLivingBase, ampl: Int) {
		if (!AlfheimConfigHandler.enableMMO) return
		if (living.isInWater) living.heal(SpellWellOLife.damage * (ampl + 1))
	}
}
