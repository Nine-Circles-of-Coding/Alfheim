package alfheim.common.lexicon.page

import alexsocol.asjlib.ASJUtilities
import alfheim.client.core.handler.CardinalSystemClient.PlayerSegmentClient
import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem.Knowledge

class PageTextLearnableKnowledge(name: String, val knowledge: Knowledge): PageTextConditional(name, { ASJUtilities.isClient && PlayerSegmentClient.knowledge.contains(knowledge.toString()) })