package alfheim.common.crafting.recipe

import alexsocol.asjlib.get
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World
import vazkii.botania.api.mana.ICompositableLens

object RecipeLensSplit: IRecipe {
	
	override fun matches(inv: InventoryCrafting, world: World?) = getCraftingResult(inv) != null
	
	override fun getCraftingResult(inv: InventoryCrafting): ItemStack? {
		var lens: ItemStack? = null
		
		for (i in 0 until inv.sizeInventory) {
			val stack = inv[i] ?: continue
			if (stack.item !is ICompositableLens) continue
			if (lens == null) lens = stack else return null
		}
		
		return (lens?.item as? ICompositableLens)?.getCompositeLens(lens)
	}
	
	override fun getRecipeSize() = 1
	
	override fun getRecipeOutput() = null
}
