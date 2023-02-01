package alfheim.common.potion

import alexsocol.asjlib.*
import alfheim.AlfheimCore
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.alt.BlockAltLeaves
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.network.MessageEffect
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.ai.attributes.BaseAttributeMap
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.DamageSource
import net.minecraftforge.client.event.*
import net.minecraftforge.event.entity.living.LivingAttackEvent

object PotionNoclip: PotionAlfheim(AlfheimConfigHandler.potionIDNoclip, "noclip", false, 0xAAAAAA) {
	
	init {
		if (ASJUtilities.isClient)
			ClientEventHandler.eventForge()
		
		CommonEventHandler.eventForge()
	}
	
	override fun isReady(time: Int, amp: Int) = AlfheimConfigHandler.enableMMO
	
	override fun performEffect(target: EntityLivingBase, time: Int) {
		if (AlfheimConfigHandler.enableMMO) // hacky shit to forbid noclip through Yggdrasil
			target.noClip = if (target is EntityPlayer)
				if (target.capabilities.isCreativeMode)
					true
				else
					!(target.worldObj.getBlock(target) === AlfheimBlocks.altLeaves && target.worldObj.getBlockMeta(target) % 8 == BlockAltLeaves.yggMeta ||
					 (target.worldObj.getBlock(target) === AlfheimBlocks.altWood1  && target.worldObj.getBlockMeta(target) % 4 == 2))
			else
				true
	}
	
	override fun applyAttributesModifiersToEntity(target: EntityLivingBase, attributes: BaseAttributeMap, amp: Int) {
		if (!AlfheimConfigHandler.enableMMO) return
		target.noClip = true
		if (target !is EntityPlayer) return
		target.capabilities.allowFlying = true
		target.capabilities.isFlying = true
		target.onGround = false
		target.sendPlayerAbilities()
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase, attributes: BaseAttributeMap, amp: Int) {
		if (!AlfheimConfigHandler.enableMMO) return
		target.noClip = false
		if (ASJUtilities.isServer) AlfheimCore.network.sendToAll(MessageEffect(target.entityId, this.id, 0, 0))
	}
	
	object CommonEventHandler {
		
		@SubscribeEvent
		fun onEntityAttacked(e: LivingAttackEvent) {
			if (!AlfheimConfigHandler.enableMMO || !((e.source.damageType.equals(DamageSource.inWall.damageType, true) || e.source.damageType.equals(DamageSource.drown.damageType, true)) && e.entityLiving.isPotionActive(AlfheimConfigHandler.potionIDNoclip))) return
			e.isCanceled = true
			return
		}
	}
	
	object ClientEventHandler {
		
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		fun onBlockOverlay(e: RenderBlockOverlayEvent) {
			if (AlfheimConfigHandler.enableMMO && e.overlayType != RenderBlockOverlayEvent.OverlayType.FIRE)
				e.isCanceled = e.player.isPotionActive(AlfheimConfigHandler.potionIDNoclip)
		}
		
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		fun onWireframeRender(e: DrawBlockHighlightEvent) {
			if (AlfheimConfigHandler.enableMMO && AlfheimConfigHandler.disableWireframe) e.isCanceled = e.player.isPotionActive(AlfheimConfigHandler.potionIDNoclip)
		}
	}
}
