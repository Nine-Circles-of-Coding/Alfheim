package alfheim.common.lexicon.page

import vazkii.botania.common.lexicon.page.PageText

open class PageTextConditional(name: String, val condition: () -> Boolean): PageText(name) {
	override fun getUnlocalizedName(): String {
		return if (condition()) unlocalizedName!! else "${unlocalizedName}u"
	}
}