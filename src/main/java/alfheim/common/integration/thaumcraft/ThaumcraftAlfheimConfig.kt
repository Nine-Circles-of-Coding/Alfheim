package alfheim.common.integration.thaumcraft

import alfheim.common.core.handler.AlfheimConfigHandler
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator

object ThaumcraftAlfheimConfig {
	
	fun loadConfig() {
		TCHandlerAlfheimAspects.addAspects()
		TCHandlerShadowFoxAspects.addAspects()
		ThaumcraftWorldGenerator.addDimBlacklist(AlfheimConfigHandler.dimensionIDDomains, 2)
		ThaumcraftWorldGenerator.addDimBlacklist(AlfheimConfigHandler.dimensionIDHelheim, 2)
	}

//	val outerLandsID = if (Botania.thaumcraftLoaded) Config.dimensionOuterId else Int.MIN_VALUE
}