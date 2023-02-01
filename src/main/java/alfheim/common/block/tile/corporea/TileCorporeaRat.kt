package alfheim.common.block.tile.corporea

import alexsocol.asjlib.I
import alexsocol.asjlib.math.Vector3
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.helper.CorporeaAdvancedHelper
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import vazkii.botania.api.corporea.*
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.block.tile.corporea.TileCorporeaBase
import kotlin.math.min

class TileCorporeaRat: TileCorporeaBase(), ICorporeaRequestor {
	
	val requestQueue = HashMap<Pair<String, Int>, MutableList<String>>()

	fun queueRequest(request: Any?, count: Int, commandSenderName: String) {
		if (request !is String) return
		(requestQueue[request to count] ?: ArrayList<String>().also { requestQueue[request to count] = it }).add(commandSenderName)
	}
	
	override fun doCorporeaRequest(request: Any?, count: Int, spark: ICorporeaSpark) {
		if (request !is String) return
		
		val stacks = CorporeaHelper.requestItem(request, count, spark, true)
		if (stacks.isEmpty()) return
		
		spark.onItemsRequested(stacks)
		
		val missing = count - stacks.sumBy { it?.stackSize ?: 0 }
		
		val name = requestQueue[request to count]?.removeFirstOrNull() ?: ""
		val requestor = MinecraftServer.getServer()?.configurationManager?.func_152612_a(name)
		
		if (missing > 0)
			queueRequest(request, missing, name)
		
		for (stack in stacks) {
			if (stack == null) continue
			
			if (requestor == null) {
				CorporeaAdvancedHelper.putOrDrop(this, spark, stack)
				continue
			}
			
			var cost = manaRequired(Vector3.entityTileDistance(requestor, this), count)
			
			if (requestor.dimension != this.worldObj.provider.dimensionId)
				cost *= CROSSDIM_COST
			
			if (!ManaItemHandler.requestManaExact(ItemStack(Blocks.stone), requestor, cost, true)) {
				CorporeaAdvancedHelper.putOrDrop(this, spark, stack)
				continue
			}
			
			if (!requestor.inventory.addItemStackToInventory(stack))
				requestor.dropPlayerItemWithRandomChoice(stack, true)
		}
	}
	
	fun manaRequired(distance: Double, count: Int) = (distance.I + BLOCK_COST) * MANA_BALANCE * min(1, count + SIZE_COST)
	
	// UNUSED
	override fun getSizeInventory() = 0
	override fun getInventoryName() = AlfheimBlocks.corporeaRatBase.localizedName!!
	
	companion object {
		const val BLOCK_COST = 1
		const val MANA_BALANCE = 1
		const val SIZE_COST = 1
		const val CROSSDIM_COST = 10
	}
}

