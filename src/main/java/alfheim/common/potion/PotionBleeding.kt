package alfheim.common.potion

import alexsocol.asjlib.F
import alfheim.AlfheimCore
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.DamageSourceSpell
import net.minecraft.entity.EntityLivingBase
import kotlin.math.max

object PotionBleeding: PotionAlfheim(AlfheimConfigHandler.potionIDBleeding, "bleeding", true, 0xFF0000) {
	
	override fun isReady(time: Int, ampl: Int) = time % (20 / max(1, ampl)) == 0
	
	override fun performEffect(living: EntityLivingBase, ampl: Int) {
		living.attackEntityFrom(DamageSourceSpell.bleeding, (ampl + 1).F)
		AlfheimCore.proxy.bloodFX(living.worldObj, living.posX, living.posY + living.height, living.posZ, 200, (Math.random() * 2 + 1).F / 10, 0.5F)
	}
}
