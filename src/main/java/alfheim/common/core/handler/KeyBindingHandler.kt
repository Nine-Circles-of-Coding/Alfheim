package alfheim.common.core.handler

import alexsocol.asjlib.*
import alfheim.api.AlfheimAPI
import alfheim.api.entity.*
import alfheim.api.spell.SpellBase.SpellCastResult.DESYNC
import alfheim.common.core.helper.*
import alfheim.common.item.AlfheimItems
import alfheim.common.network.*
import alfheim.common.network.packet.Message2d
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

object KeyBindingHandler {
	
	var flightEnableCooldown = 0
	
	fun enableFlight(player: EntityPlayerMP, boost: Boolean) {
		if (AlfheimConfigHandler.wingsBlackList.contains(player.worldObj.provider.dimensionId)) {
			ASJUtilities.say(player, "mes.flight.unavailable")
		} else {
			if (!AlfheimConfigHandler.enableElvenStory || (player.race == EnumRace.HUMAN && !player.capabilities.isCreativeMode) || (player.capabilities.isCreativeMode && boost)) return
			if (!CardinalSystem.forPlayer(player).esmAbility) return
			
			val isntFlying = !player.capabilities.isFlying
			if (flightEnableCooldown > 0 && isntFlying) return
			
			player.capabilities.allowFlying = true
			player.capabilities.isFlying = isntFlying
			player.sendPlayerAbilities()
			if (boost) ElvenFlightHelper.sub(player, 300)
		}
	}
	
	fun toggleESMAbility(player: EntityPlayerMP) {
		val seg = CardinalSystem.forPlayer(player)
		seg.toggleESMAbility()
		
		if (!seg.esmAbility && !player.capabilities.isCreativeMode) {
			player.capabilities.isFlying = false
			player.capabilities.allowFlying = false
			player.sendPlayerAbilities()
		}
	}
	
	fun cast(player: EntityPlayerMP, hotSpell: Boolean, id: Int) {
		val ids = if (hotSpell) CardinalSystem.HotSpellsSystem.getHotSpellID(player, id) else id
		val seg = CardinalSystem.forPlayer(player)
		val spell = AlfheimAPI.getSpellByIDs(ids shr 28 and 0xF, ids and 0xFFFFFFF)
		if (spell == null)
			NetworkService.sendTo(Message2d(M2d.COOLDOWN, ids.D, (-DESYNC.ordinal).D), player)
		else {
			seg.ids = ids
			seg.init = if (player.capabilities.isCreativeMode) 1 else spell.getCastTime()
			seg.castableSpell = spell
		}
	}
	
	fun unCast(player: EntityPlayerMP) {
		val seg = CardinalSystem.forPlayer(player)
		seg.ids = 0
		seg.init = 0
		seg.castableSpell = null
	}
	
	fun select(player: EntityPlayerMP, team: Boolean, id: Int) {
		if (team) {
			val mr = CardinalSystem.PartySystem.getParty(player)[id]
			if (mr is EntityLivingBase)
				CardinalSystem.TargetingSystem.setTarget(player, mr, true, id)
		} else {
			if (id != -1) {
				val e = player.worldObj.getEntityByID(id)
				if (e is EntityLivingBase) {
					CardinalSystem.TargetingSystem.setTarget(player, e, false)
				}
			} else
				CardinalSystem.TargetingSystem.setTarget(player, null, false)
		}
	}
	
	fun secret(player: EntityPlayerMP) { // now just gives me my staff in exchange for 9 sticks
		if (ContributorsPrivacyHelper.isCorrect(player, "AlexSocol")) {
			if (player.currentEquippedItem?.item === Items.stick && player.currentEquippedItem?.stackSize == 9) {
				player.setCurrentItemOrArmor(0, ItemStack(AlfheimItems.royalStaff))
			}
		}
	}
}