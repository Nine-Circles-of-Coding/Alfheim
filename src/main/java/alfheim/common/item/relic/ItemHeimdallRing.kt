package alfheim.common.item.relic

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.item.*
import baubles.api.BaubleType
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.INpc
import net.minecraft.entity.boss.IBossDisplayData
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.event.entity.living.*
import vazkii.botania.common.Botania
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.relic.ItemRelicBauble
import java.awt.Color

class ItemHeimdallRing: ItemRelicBauble("HeimdallRing") {
	
	init {
		eventForge()
	}
	
	override fun getBaubleType(stack: ItemStack?) = BaubleType.RING
	
	@SubscribeEvent
	fun onWornTick(e: LivingEvent.LivingUpdateEvent) {
		if (RagnarokHandler.blockedPowers[4]) return
		
		val player = e.entityLiving as? EntityPlayer ?: return
		getHeimdallRing(player) ?: return
		
		leadToDungeon(player)
	}
	
	fun leadToDungeon(player: EntityPlayer) {
		if (player.heldItem?.item !== Items.ender_eye) return
		val pos = player.worldObj.findClosestStructure("Stronghold", player.posX.mfloor(), player.posY.mfloor(), player.posZ.mfloor()) ?: return
		val m = Vector3(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ).sub(player.posX, player.posY, player.posZ).normalize()
		
		val color = Color(ItemIridescent.rainbowColor())
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.WISP, player.dimension, player.posX, player.posY, player.posZ, color.red / 255.0, color.green / 255.0, color.blue / 255.0, 1.0, m.x, m.y, m.z, 1.0)
	}
	
	@SubscribeEvent
	fun trackEntities(e: LivingEvent.LivingUpdateEvent) {
		if (RagnarokHandler.blockedPowers[4]) return
		
		if (ASJUtilities.isServer) return
		if (e.entity === mc.thePlayer) return
		
		if (getHeimdallRing(mc.thePlayer) == null) return
		
		val color = when (e.entity) {
			is IBossDisplayData -> Color.magenta
			is IMob             -> Color.red
			is INpc             -> Color.white
			is EntityAnimal     -> Color.green
			is EntityPlayer     -> Color.blue
			else                -> Color.yellow
		}
		
		Botania.proxy.setWispFXDepthTest(false)
		Botania.proxy.wispFX(mc.theWorld, e.entity.posX, e.entity.posY, e.entity.posZ, color.red / 255f, color.green / 255f, color.blue / 255f, 0.1f)
		Botania.proxy.setWispFXDepthTest(true)
	}
	
	@SubscribeEvent
	fun onEndermanTeleported(e: EnderTeleportEvent) {
		if (RagnarokHandler.blockedPowers[4]) return
		
		if (e.entity is EntityPlayer) return
		if (e.entity.worldObj.playerEntities.any { Vector3.entityDistance(e.entity, it as EntityPlayer) < 16 && getHeimdallRing(it) != null })
			e.isCanceled = true
	}
	
	companion object {
		
		fun getHeimdallRing(player: EntityPlayer): ItemStack? {
			val baubles = PlayerHandler.getPlayerBaubles(player)
			val stack1 = baubles[1]
			val stack2 = baubles[2]
			return if (isHeimdallRing(stack1)) stack1 else if (isHeimdallRing(stack2)) stack2 else null
		}
		
		private fun isHeimdallRing(stack: ItemStack?): Boolean {
			return stack != null && (stack.item === AlfheimItems.priestRingHeimdall || stack.item === ModItems.aesirRing)
		}
	}
}
