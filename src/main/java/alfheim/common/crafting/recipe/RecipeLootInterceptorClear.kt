package alfheim.common.crafting.recipe

import alexsocol.asjlib.get
import alfheim.common.item.ItemLootInterceptor
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World

object RecipeLootInterceptorClear: IRecipe {
	
	override fun matches(inv: InventoryCrafting, world: World?): Boolean {
		var inter = false
		
		for (i in 0 until inv.sizeInventory) {
			val stack = inv[i] ?: continue
			if (stack.item is ItemLootInterceptor) {
				if (!inter)
					inter = true
				else
					return false
			} else
				return false
		}
		
		return inter
	}
	
	override fun getCraftingResult(inv: InventoryCrafting): ItemStack? {
		var inter: ItemStack? = null
		
		for (i in 0 until inv.sizeInventory) {
			val stack = inv[i]
			if (stack != null && stack.item is ItemLootInterceptor) {
				if (inter == null)
					inter = stack.copy()
				else
					return null
			}
		}
		
		if (inter == null) return null
		
		ItemLootInterceptor.setIDs(inter, IntArray(0))
		ItemLootInterceptor.setMetas(inter, IntArray(0))
		
		return inter
	}
	
	override fun getRecipeSize() = 10
	
	override fun getRecipeOutput() = null
}