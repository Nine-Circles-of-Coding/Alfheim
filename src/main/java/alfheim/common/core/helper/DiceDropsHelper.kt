package alfheim.common.core.helper

import alexsocol.asjlib.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import vazkii.botania.common.item.ModItems

object DiceDropsHelper {
	
	// rewards[side] = chance to DropDataList
	val rewards = arrayOf(
		100 to listOf(
			DropData(ModItems.manaResource, arrayOf(5), 2, 16, 1, 1) // spirit
			  ),
		90 to listOf(
			DropData(ModItems.manaResource, arrayOf(5), 2, 4, 1, 1), // spirit
			DropData(ModItems.overgrowthSeed, arrayOf(0), 1, 1, 1, 1) // seed
		      ),
		80 to listOf(
			DropData(ModItems.manaResource, arrayOf(5), 4, 6, 1, 1), // spirit
			DropData(ModItems.rune, Array(16) {it}, 1, 3, 2, 4), // runes
		      ),
		70 to listOf(
			DropData(ModItems.manaResource, arrayOf(5), 4, 8, 1, 1), // spirit
			DropData(ModItems.manaResource, Array(3) {it}, 2, 10, 1, 1), // resources
		      ),
		60 to listOf(
			DropData(ModItems.manaResource, arrayOf(5), 6, 10, 1, 1), // spirit
			DropData(ModItems.manaResource, Array(3) {it}, 4, 8, 1, 1), // resources
			DropData(ModItems.rune, Array(16) {it}, 1, 3, 0, 3), // runes
			),
		50 to listOf(
			DropData(ModItems.manaResource, arrayOf(5), 8, 12, 1, 1), // spirit
			DropData(ModItems.manaResource, Array(3) {it}, 5, 10, 1, 1), // resources
			DropData(ModItems.rune, Array(16) {it}, 1, 3, 1, 3), // runes
			DropData(ModItems.blackLotus, arrayOf(0, 0, 1), 1, 1, 1, 1) // lotus
		      )
	                          )
	
	fun addRewards(player: EntityPlayer, side: Int): Boolean {
		if (side !in rewards.indices) return false
		
		val rewardData = rewards[side]
		if (!ASJUtilities.chance(rewardData.first)) return false
		
		val rewardList = rewardData.second
		rewardList.forEach { (item, _metas, minCount, maxCount, minStacks, maxStacks) ->
			
			val stacks = ASJUtilities.randInBounds(minStacks, maxStacks, player.worldObj.rand)
			if (stacks == 0) return@forEach
			
			val metas = _metas.toMutableList()
			repeat(stacks) {
				val count = ASJUtilities.randInBounds(minCount, maxCount, player.worldObj.rand)
				if (count == 0) return@repeat
				
				val meta = metas.removeRandom() ?: return@repeat
				val stack = ItemStack(item, count, meta)
				
				if (!player.inventory.addItemStackToInventory(stack))
					player.dropPlayerItemWithRandomChoice(stack, true)
			}
		}
		
		return true
	}
	
	@Suppress("ArrayInDataClass")
	data class DropData(val item: Item, val metas: Array<Int>, val minCount: Int, val maxCount: Int, val minStacks: Int, val maxStacks: Int)
}