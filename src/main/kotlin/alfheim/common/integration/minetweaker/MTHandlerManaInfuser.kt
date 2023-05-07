package alfheim.common.integration.minetweaker

import alfheim.api.*
import alfheim.api.crafting.recipe.RecipeManaInfuser
import alfheim.common.integration.minetweaker.MinetweakerAlfheimConfig.getObjects
import alfheim.common.integration.minetweaker.MinetweakerAlfheimConfig.getStack
import alfheim.common.lexicon.page.PageManaInfuserRecipe
import minetweaker.*
import minetweaker.api.item.*
import net.minecraft.item.ItemStack
import stanhebben.zenscript.annotations.*
import vazkii.botania.api.BotaniaAPI
import java.util.*

@ZenClass("mods." + ModInfo.MODID + ".ManaInfuser")
object MTHandlerManaInfuser {
	
	@ZenMethod
	@JvmStatic
	fun addRecipe(output: IItemStack, mana: Int, inputs: Array<IIngredient?>) {
		MineTweakerAPI.apply(Add(RecipeManaInfuser(mana, getStack(output), *getObjects(inputs))))
	}
	
	@ZenMethod
	@JvmStatic
	fun removeRecipe(output: IItemStack) {
		MineTweakerAPI.apply(Remove(getStack(output)))
	}
	
	@ZenMethod
	@JvmStatic
	fun addRecipePage(entryName: String, pageUnlocalizedName: String, pageIndex: Int, outputStack: IItemStack, recipeIndex: Int) {
		val entry = BotaniaAPI.getAllEntries().first { it.unlocalizedName == entryName }
		val recipe = AlfheimAPI.manaInfuserRecipes.filter { ItemStack.areItemStacksEqual(it.output, getStack(outputStack)) }[recipeIndex]
		val page = PageManaInfuserRecipe(pageUnlocalizedName, recipe)
		entry.pages.add(pageIndex, page)
		page.onPageAdded(entry, pageIndex)
	}
	
	private class Add(private val recipe: RecipeManaInfuser): IUndoableAction {
		
		override fun apply() {
			AlfheimAPI.addInfuserRecipe(recipe)
		}
		
		override fun canUndo(): Boolean {
			return true
		}
		
		override fun undo() {
			AlfheimAPI.removeInfusionRecipe(recipe)
		}
		
		override fun describe(): String {
			return "Adding Mana Infuser recipe $recipe"
		}
		
		override fun describeUndo(): String {
			return "Removing Mana Infuser recipe $recipe"
		}
		
		override fun getOverrideKey(): Any? {
			return null
		}
	}
	
	private class Remove(private val output: ItemStack): IUndoableAction {
		
		val removed = ArrayList<RecipeManaInfuser>()
		
		override fun apply() {
			var rec = AlfheimAPI.removeInfusionRecipe(output)
			while (rec != null) {
				removed.add(rec)
				rec = AlfheimAPI.removeInfusionRecipe(output)
			}
		}
		
		override fun canUndo(): Boolean {
			return true
		}
		
		override fun undo() {
			for (rec in removed) AlfheimAPI.addInfuserRecipe(rec)
		}
		
		override fun describe(): String {
			return "Removing all Mana Infuser recipes for ${output.unlocalizedName}"
		}
		
		override fun describeUndo(): String {
			return "Re-adding previously removed Mana Infuser recipes for ${output.unlocalizedName}"
		}
		
		override fun getOverrideKey(): Any? {
			return null
		}
	}
}