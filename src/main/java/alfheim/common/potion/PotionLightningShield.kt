package alfheim.common.potion

import alexsocol.asjlib.*
import alfheim.api.item.ColorOverrideHelper
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.item.equipment.bauble.ItemPriestCloak
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.entity.living.LivingAttackEvent
import vazkii.botania.common.Botania
import vazkii.botania.common.core.helper.Vector3
import java.awt.Color
import kotlin.math.min

object PotionLightningShield: PotionAlfheim(AlfheimConfigHandler.potionIDLightningShield, "lightningShield", false, 0x0079C4) {
	
	init {
		eventForge()
	}
	
	override fun isReady(time: Int, mod: Int): Boolean {
		return true
	}
	
	override fun performEffect(entity: EntityLivingBase, mod: Int) {
		if (!entity.worldObj.isRemote) return
		if (Math.random() > 0.25 || (mc.gameSettings.thirdPersonView == 0 && mc.thePlayer === entity)) return
		
		val start = Vector3.fromEntity(entity)
		val end = start.copy().add(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1)
		var color = 0x0079C4
		if (entity is EntityPlayer) color = ColorOverrideHelper.getColor(entity, color)
		val innerColor = Color(color).brighter().brighter().rgb
		Botania.proxy.lightningFX(entity.worldObj, start, end, 2f, color, innerColor)
	}
	
	@SubscribeEvent
	fun onPlayerAttacked(e: LivingAttackEvent) {
		if (e.source.damageType == "lightning") return // Stack overflow fix
		
		val attacker = e.source.entity as? EntityLivingBase ?: return
		val player = e.entityLiving as? EntityPlayer ?: return
		if (!player.isPotionActive(this.id)) return
		if (ItemPriestCloak.getCloak(0, player) == null) return
		
		attacker.attackEntityFrom(DamageSourceSpell.lightning(player), min(e.ammount, 2f))
	}
}
