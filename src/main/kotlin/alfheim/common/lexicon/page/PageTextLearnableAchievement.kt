package alfheim.common.lexicon.page

import alexsocol.asjlib.*
import net.minecraft.stats.Achievement

class PageTextLearnableAchievement(name: String, val achievement: Achievement): PageTextConditional(name, { ASJUtilities.isClient && mc.thePlayer?.hasAchievement(achievement) == true })