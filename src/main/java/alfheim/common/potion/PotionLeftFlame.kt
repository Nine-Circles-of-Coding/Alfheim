package alfheim.common.potion

import alexsocol.asjlib.*
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.AlfheimConfigHandler
import cpw.mods.fml.common.eventhandler.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.ai.attributes.BaseAttributeMap
import net.minecraft.entity.player.*
import net.minecraft.util.DamageSource
import net.minecraftforge.event.*
import net.minecraftforge.event.entity.item.ItemTossEvent
import net.minecraftforge.event.entity.living.LivingHealEvent
import net.minecraftforge.event.entity.player.PlayerEvent.*
import net.minecraftforge.event.world.BlockEvent.*

object PotionLeftFlame: PotionAlfheim(AlfheimConfigHandler.potionIDLeftFlame, "leftFlame", false, 0x0) {
	
	init {
		eventForge()
	}
	
	override fun applyAttributesModifiersToEntity(target: EntityLivingBase?, attributes: BaseAttributeMap, ampl: Int) {
		super.applyAttributesModifiersToEntity(target, attributes, ampl)
		if (AlfheimConfigHandler.enableMMO && target is EntityPlayer) {
			target.capabilities.allowEdit = false
			target.capabilities.allowFlying = true
			target.capabilities.disableDamage = true
			target.capabilities.isFlying = true
			target.sendPlayerAbilities()
			if (target is EntityPlayerMP) target.theItemInWorldManager.blockReachDistance = 0.1
			if (ASJUtilities.isClient) VisualEffectHandlerClient.onDeath(target)
		}
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase?, attributes: BaseAttributeMap, ampl: Int) {
		super.removeAttributesModifiersFromEntity(target, attributes, ampl)
		if (AlfheimConfigHandler.enableMMO && target is EntityPlayer) {
			target.capabilities.allowEdit = true
			target.capabilities.allowFlying = false
			target.capabilities.disableDamage = false
			target.capabilities.isFlying = false
			target.sendPlayerAbilities()
			if (target is EntityPlayerMP) target.theItemInWorldManager.blockReachDistance = 5.0
		}
		
		if (ampl != 0) return
		
		target?.let {
			it.dataWatcher.updateObject(6, 0f)
			it.onDeath(DamageSource("Respawn"))
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onBreakSpeed(e: BreakSpeed) {
		if (check(e.entityLiving)) e.newSpeed = 0f
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onHarvestCheck(e: HarvestCheck) {
		if (check(e.entityLiving)) e.success = false
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onBlockBreak(e: BreakEvent) {
		if (check(e.player)) e.isCanceled = true
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onBlockPlace(e: PlaceEvent) {
		if (check(e.player)) e.isCanceled = true
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onBlockMultiPlace(e: MultiPlaceEvent) {
		if (check(e.player)) e.isCanceled = true
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onPlayerSaid(e: ServerChatEvent) {
		if (check(e.player)) e.isCanceled = true
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onPlayerDrop(e: ItemTossEvent) {
		if (check(e.player)) {
			e.isCanceled = true
			e.player.inventory.addItemStackToInventory(e.entityItem.entityItem.copy())
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onHeal(e: LivingHealEvent) {
		if (check(e.entityLiving)) e.isCanceled = true
	}
	
	@SubscribeEvent
	fun onChatEvent(e: ServerChatEvent) {
		if (check(e.player)) e.isCanceled = true
	}
	
	@SubscribeEvent
	fun onCommandEvent(e: CommandEvent) {
		if (check(e.sender as? EntityLivingBase)) e.isCanceled = true
	}
	
	fun check(e: EntityLivingBase?) = AlfheimConfigHandler.enableMMO && e?.isPotionActive(this) == true
}
