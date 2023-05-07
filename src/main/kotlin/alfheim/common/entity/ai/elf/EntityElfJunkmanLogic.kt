package alfheim.common.entity.ai.elf

import alexsocol.asjlib.with
import alfheim.api.entity.EnumRace
import net.minecraft.item.ItemStack

object EntityElfJunkmanLogic {

	val trades = Array(EnumRace.values().size) { HashSet<TradeEntry>() }
	
	/**
	 * @param lvl Player reputation level. May be in range -1..3,
	 * with -1 or 0 meaning nothing can be traded and 3 are the best offers
	 */
	fun getTrade(offer: ItemStack, race: EnumRace, lvl: Int): TradeResult {
		if (lvl == 0) return emptyTrade
		val available = trades[race.ordinal].firstOrNull { offer.isItemEqual(it.toGive) } ?: return emptyTrade
		
		val willGet = when(lvl) {
			1    -> available.willGet.first
			2    -> available.willGet.second
			3    -> available.willGet.third
			else -> null
		}
		
		return TradeResult(available.toGive.stackSize, willGet)
	}
	
	fun registerTrade(race: Int, toGive: ItemStack, willGet1: ItemStack?, willGet2: ItemStack?, willGet3: ItemStack) {
		if (race == -1)
			trades.forEach { it += TradeEntry(toGive, willGet1 to willGet2 with willGet3) }
		else
			trades.get(race) += TradeEntry(toGive, willGet1 to willGet2 with willGet3)
	}
	
	val emptyTrade = TradeResult(0, null)
}

class TradeResult(val more: Int, val result: ItemStack?)
class TradeEntry(val toGive: ItemStack, val willGet: Triple<ItemStack?, ItemStack?, ItemStack>)