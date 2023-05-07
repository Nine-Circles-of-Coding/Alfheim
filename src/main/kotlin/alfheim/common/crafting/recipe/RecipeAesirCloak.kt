package alfheim.common.crafting.recipe

import alexsocol.asjlib.*
import alfheim.common.item.AlfheimItems
import baubles.common.lib.PlayerHandler
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World

object RecipeAesirCloak: IRecipe {
	
	override fun matches(inv: InventoryCrafting, world: World?): Boolean {
		val crafter = (inv.eventHandler as? ContainerWorkbench)?.alfheim_synthetic_thePlayer ?: return false
		if (PlayerHandler.getPlayerBaubles(crafter)[0]?.item !== AlfheimItems.aesirEmblem) return false
		
		var foundCloak0 = false
		var foundCloak1 = false
		var foundCloak2 = false
		var foundCloak3 = false
		var foundCloak4 = false
		var foundCloak5 = false
		
		for (i in 0 until inv.sizeInventory) {
			val stack = inv[i] ?: continue
			
			if (stack.item === AlfheimItems.priestCloak && stack.meta == 0 && !foundCloak0) foundCloak0 = true else
			if (stack.item === AlfheimItems.priestCloak && stack.meta == 1 && !foundCloak1) foundCloak1 = true else
			if (stack.item === AlfheimItems.priestCloak && stack.meta == 2 && !foundCloak2) foundCloak2 = true else
			if (stack.item === AlfheimItems.priestCloak && stack.meta == 3 && !foundCloak3) foundCloak3 = true else
			if (stack.item === AlfheimItems.priestCloak && stack.meta == 4 && !foundCloak4) foundCloak4 = true else
			if (stack.item === AlfheimItems.priestCloak && stack.meta == 5 && !foundCloak5) foundCloak5 = true else
			return false // Found an invalid item, breaking the recipe
		}
		
		return foundCloak0 && foundCloak1 && foundCloak2 && foundCloak3 && foundCloak4 && foundCloak5
	}
	
	override fun getCraftingResult(inv: InventoryCrafting) = recipeOutput
	override fun getRecipeSize() = 10
	override fun getRecipeOutput() = ItemStack(AlfheimItems.aesirCloak)
}
