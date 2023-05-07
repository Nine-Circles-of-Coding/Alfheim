package alfheim.api.crafting.recipe

import alexsocol.asjlib.*
import net.minecraft.item.ItemStack
import vazkii.botania.api.recipe.RecipePetals

/**
 * A recipe for the Dendric Suffuser.
 */
class RecipeTreeCrafting(val manaUsage: Int, output: ItemStack, val outTileId: String?, val core: ItemStack, vararg inputs: Any): RecipePetals(output, *inputs) {
	
	var throttle = -1
	
	constructor(mana: Int, out: ItemStack, outTileId: String?, core: ItemStack, throttle: Int, vararg inputs: Any): this(mana, out, outTileId, core, *inputs) {
		this.throttle = throttle
	}
	
	init {
		if (inputs.size > 8) throw IllegalArgumentException("Maximal suffusion inputs size is 8")
		if (output.block == null) throw IllegalArgumentException("Can't fetch block from output stack '$output'")
	}
	
	fun matches(items: List<ItemStack>, mid: ItemStack): Boolean {
		if (!ASJUtilities.isItemStackEqualCrafting(core, mid)) return false
		
		val inputsMissing = inputs
		
		for (i in items) {
			for (j in inputsMissing.indices) {
				val inp = inputsMissing[j]
				if (inp is ItemStack && inp.meta == 32767)
					inp.meta = i.meta
				
				if (i.itemEquals(inp)) {
					inputsMissing.removeAt(j)
					break
				}
			}
		}
		return inputsMissing.isEmpty()
	}
	
	override fun toString(): String {
		val s = StringBuilder()
		for (ing in inputs) s.append("$ing + ")
		return "Recipe: ($s + mana*$manaUsage) -> $core => $output"
	}
	
	override fun equals(other: Any?): Boolean {
		if (other is RecipeTreeCrafting)
			return other.manaUsage == manaUsage && ItemStack.areItemStacksEqual(output, other.output) && inputs.containsAll(other.inputs) && other.inputs.containsAll(inputs)
		
		return false
	}
}
