package alfheim.common.integration.minetweaker

import alfheim.api.*
import alfheim.api.crafting.recipe.RecipeTreeCrafting
import alfheim.common.integration.minetweaker.MinetweakerAlfheimConfig.getObjects
import alfheim.common.integration.minetweaker.MinetweakerAlfheimConfig.getStack
import alfheim.common.lexicon.page.PageTreeCrafting
import minetweaker.*
import minetweaker.api.item.*
import net.minecraft.item.ItemStack
import stanhebben.zenscript.annotations.*
import vazkii.botania.api.BotaniaAPI

@ZenClass("mods." + ModInfo.MODID + ".Suffuser")
object MTHandlerSuffuser {
	
	@ZenMethod
	@JvmStatic
	fun addRecipe(output: IItemStack, outTileId: String?, inner: IItemStack, mana: Int, speed: Int, inputs: Array<IIngredient?>) {
		val out = getStack(output)
		val core = getStack(inner)
		MineTweakerAPI.apply(Add(RecipeTreeCrafting(mana, out, outTileId, core, speed, *getObjects(inputs))))
	}
	
	@ZenMethod
	@JvmStatic
	fun removeRecipe(output: IItemStack) {
		MineTweakerAPI.apply(Remove(getStack(output)))
	}
	
	@ZenMethod
	@JvmStatic
	fun addRecipePage(entryName: String, pageUnlocalizedName: String, pageIndex: Int, outputStack: ItemStack) {
		val entry = BotaniaAPI.getAllEntries().first { it.unlocalizedName == entryName }
		val recipes = AlfheimAPI.treeRecipes.filter { ItemStack.areItemStacksEqual(it.output, outputStack) }
		val page = if (recipes.size == 1) PageTreeCrafting(pageUnlocalizedName, recipes[0]) else PageTreeCrafting(pageUnlocalizedName, recipes)
		entry.pages.add(pageIndex, page)
		page.onPageAdded(entry, pageIndex)
	}
	
	private class Add(private val recipe: RecipeTreeCrafting): IUndoableAction {
		
		override fun apply() {
			AlfheimAPI.addTreeRecipe(recipe)
		}
		
		override fun canUndo(): Boolean {
			return true
		}
		
		override fun undo() {
			AlfheimAPI.removeTreeRecipe(recipe.output)
		}
		
		override fun describe(): String {
			return String.format("Adding Tree Suffusion recipe %s", recipe)
		}
		
		override fun describeUndo(): String {
			return String.format("Removing Tree Suffusion recipe %s", recipe)
		}
		
		override fun getOverrideKey(): Any? {
			return null
		}
	}
	
	private class Remove(private val output: ItemStack): IUndoableAction {
		
		val removed = ArrayList<RecipeTreeCrafting>()
		
		override fun apply() {
			var rec = AlfheimAPI.removeTreeRecipe(output)
			while (rec != null) {
				removed.add(rec)
				rec = AlfheimAPI.removeTreeRecipe(output)
			}
		}
		
		override fun canUndo(): Boolean {
			return true
		}
		
		override fun undo() {
			for (rec in removed) AlfheimAPI.addTreeRecipe(rec)
		}
		
		override fun describe(): String {
			return String.format("Removing all Tree Suffusion recipes for %s", output.unlocalizedName)
		}
		
		override fun describeUndo(): String {
			return String.format("Re-adding previously removed Tree Suffusion recipes for %s", output.unlocalizedName)
		}
		
		override fun getOverrideKey(): Any? {
			return null
		}
	}
}
