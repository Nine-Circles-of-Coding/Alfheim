package alfheim.common.lexicon.page

import net.minecraft.client.gui.GuiButton
import vazkii.botania.api.internal.IGuiLexiconEntry
import vazkii.botania.api.lexicon.multiblock.MultiblockSet
import vazkii.botania.common.lexicon.page.*

class PageMultiblockHidden(unlocalizedName: String, set: MultiblockSet, val condition: () -> Boolean): PageMultiblock(unlocalizedName, set) {
	
	override fun renderScreen(gui: IGuiLexiconEntry, mx: Int, my: Int) {
		if (condition())
			super.renderScreen(gui, mx, my)
		else {
			val width = gui.width - 30
			val x = gui.left + 16
			val y = gui.top + 2
			
			PageText.renderText(x, y, width, gui.height, getUnlocalizedName())
		}
	}
	
	override fun onOpened(gui: IGuiLexiconEntry?) {
		if (condition()) super.onOpened(gui)
	}
	
	override fun onClosed(gui: IGuiLexiconEntry?) {
		if (condition()) super.onClosed(gui)
	}
	
	override fun onActionPerformed(gui: IGuiLexiconEntry?, button: GuiButton?) {
		if (condition()) super.onActionPerformed(gui, button)
	}
	
	override fun getUnlocalizedName(): String {
		return if (condition()) unlocalizedName!! else "${unlocalizedName}u"
	}
}