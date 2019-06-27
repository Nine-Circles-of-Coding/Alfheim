package alfheim.common.core.handler

import alexsocol.asjlib.ASJUtilities
import alfheim.AlfheimCore
import alfheim.api.AlfheimAPI
import alfheim.api.entity.EnumRace
import alfheim.api.event.SpellCastEvent
import alfheim.api.spell.SpellBase.SpellCastResult.*
import alfheim.common.core.handler.CardinalSystem.SpellCastingSystem
import alfheim.common.core.registry.AlfheimRegistry
import alfheim.common.entity.Flight
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.MovingObjectPosition.MovingObjectType
import net.minecraftforge.common.MinecraftForge

object KeyBindingHandler {
	
	fun enableFlight(player: EntityPlayerMP, boost: Boolean) {
		if (!AlfheimCore.enableElvenStory) return
		if (player.capabilities.isCreativeMode || EnumRace.getRace(player) == EnumRace.HUMAN) return
		player.capabilities.allowFlying = true
		player.capabilities.isFlying = !player.capabilities.isFlying
		player.sendPlayerAbilities()
		if (boost) Flight.sub(player, 300.0)
	}
	
	fun atack(player: EntityPlayerMP) {
		val mop = ASJUtilities.getMouseOver(player, player.theItemInWorldManager.blockReachDistance, true)
		if (mop != null && mop.typeOfHit == MovingObjectType.ENTITY && mop.entityHit != null) {
			player.attackTargetEntityWithCurrentItem(mop.entityHit)
		}
	}
	
	fun cast(caster: EntityPlayerMP, raceID: Int, spellID: Int): Int {
		if (!AlfheimCore.enableMMO) return -NOTALLOW.ordinal
		if (caster.isPotionActive(AlfheimRegistry.leftFlame)) return -NOTALLOW.ordinal
		val spell = AlfheimAPI.getSpellByIDs(raceID, spellID) ?: return -DESYNC.ordinal
		if (SpellCastingSystem.getCoolDown(caster, spell) > 0) return -NOTREADY.ordinal
		val result = spell.performCast(caster)
		if (result == OK) {
			// SpellBase.say(caster, spell);
			val e = SpellCastEvent.Post(spell, caster, spell.getCooldown())
			MinecraftForge.EVENT_BUS.post(e)
			return SpellCastingSystem.setCoolDown(caster, spell, e.cd)
		}
		return -result.ordinal
	}
}