package alfheim.common.potion

import alexsocol.asjlib.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.spell.earth.SpellGoldRush
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed

object PotionGoldRush: PotionAlfheim(AlfheimConfigHandler.potionIDGoldRush, "goldRush", false, 0x55FF00) {
	
	init {
		eventForge()
	}
	
	@SubscribeEvent
	fun onBreakSpeed(e: BreakSpeed) {
		if (AlfheimConfigHandler.enableMMO && e.entityLiving.isPotionActive(this.id)) e.newSpeed *= SpellGoldRush.efficiency.F
	}
}
