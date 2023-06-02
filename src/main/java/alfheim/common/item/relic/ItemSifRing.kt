package alfheim.common.item.relic

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.item.AlfheimItems
import baubles.api.BaubleType
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.block.Block
import net.minecraft.entity.EntityAgeable
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.ChunkCoordinates
import net.minecraftforge.event.entity.living.LivingEvent
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.relic.ItemRelicBauble

class ItemSifRing: ItemRelicBauble("SifRing") {
	
	init {
		eventForge()
	}
	
	@SubscribeEvent
	fun onPlayerTick(e: LivingEvent.LivingUpdateEvent) {
		if (RagnarokHandler.blockedPowers[5]) return
		
		val player = e.entityLiving as? EntityPlayer ?: return
		val ring = getSifRing(player) ?: return
		
		reviveCacti(ring, player)
		supplyVineballs(ring, player)
		growAnimals(ring, player)
	}
	
	val list = ArrayList<Pair<ChunkCoordinates, Block>>()
	
	fun reviveCacti(stack: ItemStack, player: EntityPlayer) {
		if (!ManaItemHandler.requestManaExact(stack, player, 20, true)) return
		
		val world = player.worldObj
		val (x, y, z) = Vector3.fromEntity(player).mf()
		
		for (i in -4..4)
			for (j in -3..3)
				for (k in -4..4) {
					if (world.getBlock(x + i, y + j, z + k) !== Blocks.deadbush) continue
					
					val cactus = Blocks.cactus.canBlockStay(world, x + i, y + j, z + k)
					val sapling = Blocks.sapling.canBlockStay(world, x + i, y + j, z + k)
					
					if (cactus || sapling)
						list.add(ChunkCoordinates(x + i, y + j, z + k) to if (cactus) Blocks.cactus else Blocks.sapling)
				}
		
		val (pos, block) = list.firstOrNull {
			val (i, j, k) = it.first
			!InteractionSecurity.isPlacementBanned(player, i, j, k, world, Blocks.cactus)
		} ?: return
		
		val (i, j, k) = pos
		world.setBlock(i, j, k, block)
		
		list.clear()
	}
	
	fun supplyVineballs(stack: ItemStack, player: EntityPlayer) {
		if (player.heldItem?.item === ModItems.slingshot && !player.inventory.hasItem(ModItems.vineBall) && ManaItemHandler.requestManaExact(stack, player, 50, true)) {
			player.inventory.addItemStackToInventory(ItemStack(ModItems.vineBall))
		}
	}
	
	fun growAnimals(stack: ItemStack, player: EntityPlayer) {
		val list = getEntitiesWithinAABB(player.worldObj, EntityAgeable::class.java, player.boundingBox(8))
		list.removeAll { !it.isChild }
		list.forEach {
			if (!InteractionSecurity.canInteractWithEntity(player, it)) return
			if (!ManaItemHandler.requestManaExact(stack, player, 1, true)) return
			it.growingAge++
		}
	}
	
	override fun getBaubleType(stack: ItemStack?) = BaubleType.RING
	
	companion object {
		
		fun getSifRing(player: EntityPlayer): ItemStack? {
			val baubles = PlayerHandler.getPlayerBaubles(player)
			val stack1 = baubles[1]
			val stack2 = baubles[2]
			return if (isSifRing(stack1)) stack1 else if (isSifRing(stack2)) stack2 else null
		}
		
		private fun isSifRing(stack: ItemStack?): Boolean {
			return stack != null && (stack.item === AlfheimItems.priestRingSif || stack.item === ModItems.aesirRing)
		}
	}
}
