package alfheim.common.lexicon

import alfheim.api.ModInfo
import net.minecraft.block.Block
import net.minecraft.item.*
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.lexicon.*

open class AlfheimLexiconEntry: LexiconEntry, IAddonEntry {
	
	constructor(unlocalizedName: String, category: LexiconCategory, stack: ItemStack?): super(unlocalizedName, category) {
		if (stack != null) icon = stack
		BotaniaAPI.addEntry(this, category)
	}
	
	constructor(unlocalizedName: String, category: LexiconCategory, block: Block): this(unlocalizedName, category, ItemStack(block))
	
	constructor(unlocalizedName: String, category: LexiconCategory, item: Item): this(unlocalizedName, category, ItemStack(item))
	
	constructor(unlocalizedName: String, category: LexiconCategory): this(unlocalizedName, category, null)
	
	override fun setLexiconPages(vararg pages: LexiconPage): LexiconEntry {
		for (page in pages) {
			page.unlocalizedName = "${ModInfo.MODID}.page.$unlocalizedName${page.unlocalizedName}"
			if (page is ITwoNamedPage)
				page.secondUnlocalizedName = "${ModInfo.MODID}.page.$unlocalizedName${page.secondUnlocalizedName}"
		}
		
		return super.setLexiconPages(*pages)
	}
	
	override fun getUnlocalizedName() = "${ModInfo.MODID}.entry.$unlocalizedName"
	
	override fun getTagline() = "${ModInfo.MODID}.tagline.$unlocalizedName"
	
	override fun getSubtitle() = "[Alfheim]"
	
	companion object {
		fun LexiconEntry.setIcon(block: Block): LexiconEntry {
			icon = ItemStack(block)
			return this
		}
		
		fun LexiconEntry.setIcon(item: Item): LexiconEntry {
			icon = ItemStack(item)
			return this
		}
	}
}
