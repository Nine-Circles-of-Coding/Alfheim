package alfheim.common.lexicon

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.client.core.handler.CardinalSystemClient.PlayerSegmentClient
import alfheim.common.core.asm.hook.AlfheimHookHandler
import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem.Knowledge
import alfheim.common.item.AlfheimItems
import alfheim.common.item.equipment.bauble.faith.ItemRagnarokEmblem
import net.minecraft.item.ItemStack
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.lexicon.LexiconCategory
import vazkii.botania.common.core.helper.ItemNBTHelper

class AlfheimAbyssalLexiconEntry(unlocalizedName: String, val fakeName: String, category: LexiconCategory): AlfheimLexiconEntry(unlocalizedName, category) {
	
	val iconFake by lazy { ItemStack(AlfheimItems.aesirEmblem) }
	val iconReal by lazy { ItemStack(AlfheimItems.ragnarokEmblem).apply { ItemNBTHelper.setBoolean(this, ItemRagnarokEmblem.TAG_GEM_FLAG, true) } }
	
	override fun isPriority() = true
	
	override fun getKnowledgeType() = BotaniaAPI.basicKnowledge!!
	
	override fun isVisible() = PlayerSegmentClient.knowledge.contains(Knowledge.ABYSS.toString())
	
	override fun getIcon() = if (ASJUtilities.isServer || ItemRagnarokEmblem.canSeeTruth(mc.thePlayer)) iconReal else iconFake
	
	override fun getUnlocalizedName(): String {
		val trueName = if (ASJUtilities.isServer) true else ItemRagnarokEmblem.canSeeTruth(mc.thePlayer)
		return "${ModInfo.MODID}.entry.${if (trueName || AlfheimHookHandler.forceHack) unlocalizedName else fakeName}"
	}
}
