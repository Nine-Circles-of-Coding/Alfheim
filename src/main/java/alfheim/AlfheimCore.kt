package alfheim

import alexsocol.patcher.*
import alfheim.api.ModInfo.MODID
import alfheim.common.core.command.*
import alfheim.common.core.handler.*
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.proxy.CommonProxy
import alfheim.common.core.util.*
import alfheim.common.integration.minetweaker.MinetweakerAlfheimConfig
import alfheim.common.integration.thaumcraft.*
import alfheim.common.integration.tinkersconstruct.TinkersConstructAlfheimConfig
import alfheim.common.integration.travellersgear.TravellersGearAlfheimConfig
import alfheim.common.integration.waila.WAILAAlfheimConfig
import alfheim.common.network.*
import alfheim.common.network.packet.*
import cpw.mods.fml.common.*
import cpw.mods.fml.common.Mod.*
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.*
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
import cpw.mods.fml.relauncher.Side
import vazkii.botania.common.Botania
import java.util.*

@Suppress("UNUSED_PARAMETER")
@Mod(modid = MODID, dependencies = "required-after:Botania", useMetadata = true, guiFactory = "$MODID.client.gui.GUIFactory", modLanguageAdapter = KotlinAdapter.className)
object AlfheimCore {
	
	@KotlinProxy(clientSide = "$MODID.client.core.proxy.ClientProxy", serverSide = "$MODID.common.core.proxy.CommonProxy")
	lateinit var proxy: CommonProxy
	
	@Metadata(MODID)
	lateinit var meta: ModMetadata

	var nextPacketID = 0
	
	var save = ""
	
	var MineTweakerLoaded = false
	var NEILoaded = false
	var stupidMode = false
	var TiCLoaded = false
	var TravellersGearLoaded = false
	
	val jingleTheBells: Boolean
	
	// do not reassign this unless you know what you are doing
	var winter: Boolean
		get() {
			return when {
				RagnarokHandler.winter -> true
				RagnarokHandler.summer -> false
				else                   -> field
			}
		}
	
	init {
		AlfheimTab
		
		jingleTheBells = (TimeHandler.month == 12 && TimeHandler.day >= 16 || TimeHandler.month == 1 && TimeHandler.day <= 8)
		winter = TimeHandler.month in arrayOf(1, 2, 12, 13)
	}
	
	@EventHandler
	fun preInit(e: FMLPreInitializationEvent) {
		MineTweakerLoaded = Loader.isModLoaded("MineTweaker3")
		NEILoaded = Loader.isModLoaded("NotEnoughItems")
		TiCLoaded = Loader.isModLoaded("TConstruct")
		TravellersGearLoaded = Loader.isModLoaded("TravellersGear")
		
		stupidMode = Loader.isModLoaded("Avaritia")
		
		if (AlfheimConfigHandler.notifications) InfoLoader.start()
		
		NetworkService.register()
		
		proxy.preInit()
		if (Botania.thaumcraftLoaded) ThaumcraftAlfheimModule.preInit()
	}
	
	@EventHandler
	fun init(e: FMLInitializationEvent) {
		proxy.init()
		proxy.initializeAndRegisterHandlers()
	}
	
	@EventHandler
	fun postInit(e: FMLPostInitializationEvent) {
		proxy.registerKeyBinds()
		proxy.registerRenderThings()
		proxy.postInit()
		if (MineTweakerLoaded) MinetweakerAlfheimConfig.loadConfig()
		if (Botania.thaumcraftLoaded) {
			ThaumcraftAlfheimConfig.loadConfig()
			ThaumcraftAlfheimModule.postInit()
		}
		if (TravellersGearLoaded) TravellersGearAlfheimConfig.loadConfig()
		if (TiCLoaded) TinkersConstructAlfheimConfig.loadConfig()
		if (Loader.isModLoaded("Waila")) WAILAAlfheimConfig.loadConfig()
	}
	
	@EventHandler
	fun starting(e: FMLServerStartingEvent) {
		save = e.server.entityWorld.saveHandler.worldDirectory.absolutePath
		
		if (AlfheimConfigHandler.enableElvenStory) AlfheimConfigHandler.initWorldCoordsForElvenStory(save)
		AlfheimConfigHandler.syncConfig()
		e.registerServerCommand(CommandAlfheim)
		e.registerServerCommand(CommandDebug)
		if (MineTweakerLoaded) e.registerServerCommand(CommandMTSpellInfo)
	}
}
