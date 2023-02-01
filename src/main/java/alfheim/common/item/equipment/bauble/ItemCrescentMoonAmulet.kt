package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.*
import alfheim.common.core.handler.EventHandler.isMagical
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.MathHelper
import net.minecraftforge.event.entity.living.LivingHurtEvent
import vazkii.botania.api.mana.*
import vazkii.botania.common.core.helper.ItemNBTHelper
import kotlin.math.max

class ItemCrescentMoonAmulet: ItemPendant("CrescentMoonAmulet"), IManaUsingItem {
	
	init {
		maxStackSize = 1
	}
	
	override fun onWornTick(stack: ItemStack, player: EntityLivingBase) {
		super.onWornTick(stack, player)
		val cd = ItemNBTHelper.getInt(stack, TAG_COOLDOWN, 0)
		if (cd > 0) ItemNBTHelper.setInt(stack, TAG_COOLDOWN, cd - 1)
	}
	
	override fun usesMana(stack: ItemStack) = true
	
	companion object {
		
		const val MANA_PER_DAMAGE = 100
		private const val TAG_COOLDOWN = "cooldown"
		
		init {
			eventForge()
		}
		
		@SubscribeEvent
		fun onWearerHurt(e: LivingHurtEvent) {
			if (e.source.isDamageAbsolute) return
			
			val player = e.entityLiving as? EntityPlayer ?: return
			val bbls = PlayerHandler.getPlayerBaubles(player)
			
			if (bbls[0]?.item !is ItemCrescentMoonAmulet) return
			
			if (e.source.isMagical) {
				if (ItemNBTHelper.getInt(bbls[0], TAG_COOLDOWN, 0) <= 0) {
					ItemNBTHelper.setInt(bbls[0], TAG_COOLDOWN, 100)
					e.ammount = max(0f, e.ammount - 10)
				}
			} else
				e.ammount -= ManaItemHandler.requestMana(bbls[0], player, MathHelper.ceiling_float_int(e.ammount * MANA_PER_DAMAGE), true) / (MANA_PER_DAMAGE * 10f)
		}
	}
}