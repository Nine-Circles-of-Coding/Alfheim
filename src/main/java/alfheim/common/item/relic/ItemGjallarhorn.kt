package alfheim.common.item.relic

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.entity.EntityElf
import alfheim.common.item.equipment.bauble.ItemPriestEmblem
import net.minecraft.entity.Entity
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.server.MinecraftServer
import net.minecraft.util.StatCollector
import net.minecraft.world.World
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.relic.ItemRelic

class ItemGjallarhorn: ItemRelic("Gjallarhorn") {
	
	init {
		setFull3D()
	}
	
	override fun onUpdate(stack: ItemStack, world: World, entity: Entity, slot: Int, inHand: Boolean) {
		super.onUpdate(stack, world, entity, slot, inHand)
		if (stack.cooldown > 0) --stack.cooldown
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (stack.cooldown > 0) return stack
		
		player.setItemInUse(stack, getMaxItemUseDuration(stack))
		return stack
	}
	
	override fun getMaxItemUseDuration(stack: ItemStack?) = 100
	
	override fun getItemUseAction(stack: ItemStack?) = EnumAction.bow
	
	override fun onEaten(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (world.isRemote || !ManaItemHandler.requestManaExact(stack, player, 50000, true)) return stack
		
		val count = if (RagnarokHandler.ragnarok) 5 else 1
		
		for (i in 0 until count) {
			EntityElf(world).apply {
				customNameTag = StatCollector.translateToLocal("entity.alfheim.Einherjar.name")
				
				setPosition(player)
				setPriest(-1)
				spawn()
			}
		}
		
		if (RagnarokHandler.ragnarok)
			MinecraftServer.getServer().configurationManager.playerEntityList.forEach {
				(it as EntityPlayer).worldObj.playSoundAtEntity(it, "${ModInfo.MODID}:horn.ghorn", 1f, 1f)
			}
		
		stack.cooldown = if (RagnarokHandler.ragnarok) 6000 else 1200
		
		return stack
	}
	
	companion object {
		private const val TAG_COOLDOWN = "cooldown"
		
		private var ItemStack.cooldown
			get() = ItemNBTHelper.getInt(this, TAG_COOLDOWN, 0)
			set(value) = ItemNBTHelper.setInt(this, TAG_COOLDOWN, value)
	}
}