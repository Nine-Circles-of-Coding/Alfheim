package alfheim.common.crafting.recipe

import alfheim.client.core.handler.CardinalSystemClient
import alfheim.common.core.handler.CardinalSystem
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.inventory.*
import net.minecraft.item.*
import net.minecraft.world.World
import net.minecraftforge.oredict.ShapedOreRecipe

class ShapedOreRecipeLearnable(val knowledge: CardinalSystem.KnowledgeSystem.Knowledge, result: ItemStack?, vararg recipe: Any?): ShapedOreRecipe(result, *recipe) {
	
	override fun matches(inv: InventoryCrafting?, world: World?): Boolean {
		val crafter = (inv?.eventHandler as? ContainerWorkbench)?.alfheim_synthetic_thePlayer ?: return false
		
		if (crafter is EntityPlayerMP) {
			if (!CardinalSystem.KnowledgeSystem.know(crafter, knowledge)) return false
		} else {
			if (knowledge.toString() !in CardinalSystemClient.PlayerSegmentClient.knowledge) return false
		}
		
		return super.matches(inv, world)
	}
	
	override fun getCraftingResult(inv: InventoryCrafting?): ItemStack? {
		val crafter = (inv?.eventHandler as? ContainerWorkbench)?.alfheim_synthetic_thePlayer ?: return null
		
		if (crafter is EntityPlayerMP) {
			if (!CardinalSystem.KnowledgeSystem.know(crafter, knowledge)) return null
		} else {
			if (knowledge.toString() !in CardinalSystemClient.PlayerSegmentClient.knowledge) return null
		}
		
		return super.getCraftingResult(inv)
	}
}