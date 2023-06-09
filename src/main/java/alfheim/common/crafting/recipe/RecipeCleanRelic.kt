package alfheim.common.crafting.recipe

import alexsocol.asjlib.*
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World
import vazkii.botania.api.item.IRelic
import vazkii.botania.common.core.helper.ItemNBTHelper

/**
 * @author ExtraMeteorP, CKATEPTb
 */
object RecipeCleanRelic: IRecipe {
	
	override fun matches(inv: InventoryCrafting, world: World?): Boolean {
		var foundRelic = false
		var foundItem = false
		
		for (i in 0 until inv.sizeInventory) {
			val stack = inv[i] ?: continue
			if (stack.item is IRelic && !foundRelic)
				foundRelic = true
			else if (!foundItem) {
				if (stack.item === AlfheimItems.elvenResource && stack.meta == ElvenResourcesMetas.DasRheingold.I)
					foundItem = true
				else
					return false
			}
		}
		
		return foundRelic && foundItem
	}
	
	override fun getCraftingResult(inv: InventoryCrafting): ItemStack? {
		var item: ItemStack? = null
		var cloth: ItemStack? = null
		
		for (i in 0 until inv.sizeInventory) {
			val stack = inv[i] ?: continue
			if (stack.item is IRelic)
				item = stack
			else if (stack.item === AlfheimItems.elvenResource && stack.meta == ElvenResourcesMetas.DasRheingold.I)
				cloth = stack
		}
		
		if (item == null) return null
		
		val copy = item.copy()
		val nick = ItemNBTHelper.getString(cloth, "nick", "")
		
		if (nick.isNotEmpty())
			ItemNBTHelper.setString(copy, "soulbind", nick)
		else
			copy.tagCompound.removeTag("soulbind")
		
		return copy
	}
	
	override fun getRecipeSize() = 2
	
	override fun getRecipeOutput() = null
}