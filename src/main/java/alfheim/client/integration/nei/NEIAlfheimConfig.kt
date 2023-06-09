package alfheim.client.integration.nei

import alexsocol.asjlib.mc
import alfheim.AlfheimCore
import alfheim.client.integration.nei.recipes.*
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.helper.ContributorsPrivacyHelper
import alfheim.common.item.AlfheimItems
import codechicken.nei.api.*
import net.minecraft.item.ItemStack
import vazkii.botania.common.block.ModBlocks

class NEIAlfheimConfig: IConfigureNEI {
	
	override fun loadConfig() {
		API.registerRecipeHandler(RecipeHandlerManaInfuser())
		API.registerUsageHandler(RecipeHandlerManaInfuser())
		API.registerRecipeHandler(RecipeHandlerTradePortal())
		API.registerUsageHandler(RecipeHandlerTradePortal())
		API.registerRecipeHandler(RecipeHandlerTreeCrafting())
		API.registerUsageHandler(RecipeHandlerTreeCrafting())
		
		API.hideItem(ItemStack(ModBlocks.manaFlame))
		API.hideItem(ItemStack(ModBlocks.gaiaHead))
		API.hideItem(ItemStack(AlfheimBlocks.anomaly))
		API.hideItem(ItemStack(AlfheimBlocks.flugelHeadBlock))
		API.hideItem(ItemStack(AlfheimBlocks.flugelHead2Block))
		API.hideItem(ItemStack(AlfheimBlocks.grapesRed[1]))
		API.hideItem(ItemStack(AlfheimBlocks.grapesRed[2]))
		API.hideItem(ItemStack(AlfheimBlocks.grapesRedPlanted))
		API.hideItem(ItemStack(AlfheimBlocks.powerStone, 1, 0))
		API.hideItem(ItemStack(AlfheimBlocks.rainbowFlame))
		API.hideItem(ItemStack(AlfheimBlocks.starBlock))
		API.hideItem(ItemStack(AlfheimBlocks.starBlock2))
		API.hideItem(ItemStack(AlfheimItems.discFlugelMeme))
		API.hideItem(ItemStack(AlfheimItems.flugelHead2))
		
		if (!ContributorsPrivacyHelper.isCorrect(mc.session.username, "AlexSocol"))
			API.hideItem(ItemStack(AlfheimItems.royalStaff))
		
		API.hideItem(ItemStack(AlfheimBlocks.anomalyHarvester)) // BACK
		API.hideItem(ItemStack(AlfheimBlocks.anomalyTransmitter)) // BACK
	}
	
	override fun getName() = AlfheimCore.meta.name!!
	
	override fun getVersion() = AlfheimCore.meta.version!!
}
