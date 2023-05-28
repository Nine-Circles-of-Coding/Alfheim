package alfheim.common.crafting.recipe

import alexsocol.asjlib.toItem
import alfheim.common.block.AlfheimBlocks
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.item.block.ItemBlockSpecialFlower

object RecipeSpecialFloatingFlower: IRecipe {
	
	override fun matches(inv: InventoryCrafting, world: World?): Boolean {
		var foundFloatingFlower = false
		var foundSpecialFlower = false
		
		for (i in 0 until inv.sizeInventory) {
			val stack = inv.getStackInSlot(i) ?: continue
			
			if (stack.item === AlfheimBlocks.rainbowFlowerFloating.toItem())
				foundFloatingFlower = true
			else if (stack.item === ModBlocks.specialFlower.toItem())
				foundSpecialFlower = true
			else
				return false // Found an invalid item, breaking the recipe
		}
		
		return foundFloatingFlower && foundSpecialFlower
	}
	
	override fun getCraftingResult(inv: InventoryCrafting): ItemStack? {
		var specialFlower: ItemStack? = null
		
		for (i in 0 until inv.sizeInventory) {
			val stack = inv.getStackInSlot(i) ?: continue
			
			if (stack.item === ModBlocks.specialFlower.toItem())
				specialFlower = stack
		}
		
		return if (specialFlower == null)
			null
		else
			ItemBlockSpecialFlower.ofType(ItemStack(ModBlocks.floatingSpecialFlower), ItemBlockSpecialFlower.getType(specialFlower))
	}
	
	override fun getRecipeSize() = 10
	
	override fun getRecipeOutput() = null
}
