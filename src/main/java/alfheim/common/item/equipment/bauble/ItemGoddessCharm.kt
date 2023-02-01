package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.*
import alfheim.common.item.AlfheimItems
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.event.world.ExplosionEvent
import vazkii.botania.api.mana.*

class ItemGoddessCharm: ItemPendant("GoddessCharm"), IManaUsingItem {
	
	override fun registerGem(reg: IIconRegister) {
		gemIcon = itemIcon
	}
	
	override fun usesMana(stack: ItemStack) = true
	
	companion object {
		
		init {
			eventForge()
		}
		
		@SubscribeEvent
		fun onExplosion(event: ExplosionEvent.Detonate) {
			val e = event.explosion
			
			val players = getEntitiesWithinAABB(event.world, EntityPlayer::class.java, getBoundingBox(e.explosionX, e.explosionY, e.explosionZ).expand(8))
			players.forEach {
				val charm = PlayerHandler.getPlayerBaubles(it)[0] ?: return@forEach
				if (charm.item !== AlfheimItems.goddesCharm) return
				
				if (!ManaItemHandler.requestManaExact(charm, it, (e.explosionSize * 500).I, true)) return@forEach
				event.affectedBlocks.clear()
				return
			}
		}
	}
}
