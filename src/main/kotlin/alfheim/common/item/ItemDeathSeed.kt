package alfheim.common.item

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import cpw.mods.fml.common.eventhandler.*
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.potion.Potion
import net.minecraft.world.World
import net.minecraftforge.event.entity.living.LivingDeathEvent
import vazkii.botania.common.Botania
import vazkii.botania.common.item.*

class ItemDeathSeed: ItemMod("DeathSeed") {
	
	init {
		maxStackSize = 1
	}
	
	override fun onUpdate(stack: ItemStack, world: World?, entity: Entity?, slot: Int, inHand: Boolean) {
		if (!ItemNBTHelper.getBoolean(stack, ItemKeepIvy.TAG_KEEP, false))
			ItemNBTHelper.setBoolean(stack, ItemKeepIvy.TAG_KEEP, true)
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		val d = ItemNBTHelper.getInt(stack, TAG_D, 0)
		val x = ItemNBTHelper.getDouble(stack, TAG_X, 0.0)
		val y = ItemNBTHelper.getDouble(stack, TAG_Y, -1.0)
		val z = ItemNBTHelper.getDouble(stack, TAG_Z, 0.0)
		
		if (y < 0) return stack
		
		player.addPotionEffect(PotionEffectU(Potion.resistance.id, 100, 4))
		ASJUtilities.sendToDimensionWithoutPortal(player, d, x, y, z)
		
		world.playSoundAtEntity(player, "mob.endermen.portal", 1f, 1f)
		for (i in 0..49)
			Botania.proxy.sparkleFX(player.worldObj, player.posX + Math.random() * player.width, player.posY - 1.6 + Math.random() * player.height, player.posZ + Math.random() * player.width, 0.25f, 1f, 0.25f, 1f, 10)
		
		--stack.stackSize
		
		return stack
	}
	
	companion object {
		
		const val TAG_D = "d"
		const val TAG_X = "x"
		const val TAG_Y = "y"
		const val TAG_Z = "z"
		
		init {
			eventForge()
		}
		
		@SubscribeEvent(priority = EventPriority.LOWEST)
		fun onPlayerDied(e: LivingDeathEvent) {
			val player = e.entityLiving as? EntityPlayer ?: return
			val slot = ASJUtilities.getSlotWithItem(AlfheimItems.deathSeed, player.inventory)
			if (slot == -1) return
			val stack = player.inventory[slot] ?: return
			val (x, y, z) = Vector3.fromEntity(player)
			ItemNBTHelper.setInt(stack, TAG_D, player.dimension)
			ItemNBTHelper.setDouble(stack, TAG_X, x)
			ItemNBTHelper.setDouble(stack, TAG_Y, y)
			ItemNBTHelper.setDouble(stack, TAG_Z, z)
		}
	}
}
