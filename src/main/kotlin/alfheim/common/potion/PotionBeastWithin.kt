package alfheim.common.potion

import alexsocol.asjlib.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.spell.nature.SpellBeastWithin
import cpw.mods.fml.common.eventhandler.*
import net.minecraft.entity.*
import net.minecraftforge.event.entity.living.LivingHurtEvent

object PotionBeastWithin: PotionAlfheim(AlfheimConfigHandler.potionIDBeastWithin, "beast", false, 0xFF8000) {
	
	init {
		func_111184_a(SharedMonsterAttributes.movementSpeed, "4B91E728-A16B-4D77-A81E-16F1286F7B1B", 0.3, 2)
		eventForge()
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun onLivingHurt(e: LivingHurtEvent) {
		val attacker = e.source.entity as? EntityLivingBase ?: return
		if (!attacker.isPotionActive(this)) return
		e.entityLiving.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDBleeding, SpellBeastWithin.damage.I, SpellBeastWithin.efficiency.I))
	}
}