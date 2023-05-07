package alfheim.common.lexicon

import alexsocol.asjlib.*
import net.minecraft.item.*
import net.minecraft.stats.Achievement
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.item.IRelic
import vazkii.botania.api.lexicon.LexiconCategory

/**
 * @author WireSegal
 * Created at 6:22 PM on 2/4/16.
 */
class AlfheimRelicLexiconEntry: AlfheimLexiconEntry {
	
	val achievement: Achievement?
	
	init {
		knowledgeType = BotaniaAPI.relicKnowledge
	}
	
	constructor(unlocalizedName: String, category: LexiconCategory): super(unlocalizedName, category) {
		achievement = null
	}
	
	constructor(unlocalizedName: String, category: LexiconCategory, a: Achievement): super(unlocalizedName, category) {
		achievement = a
	}
	
	constructor(unlocalizedName: String, category: LexiconCategory, item: Item): super(unlocalizedName, category, item) {
		achievement = if (item is IRelic) item.bindAchievement else null
		setIcon(item)
	}
	
	override fun isVisible(): Boolean = mc.thePlayer.capabilities.isCreativeMode || if (achievement != null) mc.thePlayer.hasAchievement(achievement) else mc.thePlayer.inventory.hasItem(icon.item)
}
