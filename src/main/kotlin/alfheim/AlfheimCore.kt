package alfheim

import alexsocol.patcher.*
import alfheim.api.ModInfo.MODID
import alfheim.client.core.handler.PacketHandlerClient
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
	
	lateinit var network: SimpleNetworkWrapper
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
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID)
		
		if (AlfheimConfigHandler.notifications) InfoLoader.start()
		
		registerPackets()
		
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
	
	fun registerPackets() {
		network.registerMessage(PacketHandlerServer, Message0dS::class.java, nextPacketID++, Side.SERVER)
		network.registerMessage(PacketHandlerServer, MessageContributor::class.java, nextPacketID++, Side.SERVER)
		network.registerMessage(PacketHandlerServer, MessageHotSpellS::class.java, nextPacketID++, Side.SERVER)
		network.registerMessage(PacketHandlerServer, MessageKeyBindS::class.java, nextPacketID++, Side.SERVER)
		network.registerMessage(PacketHandlerServer, MessageRaceSelection::class.java, nextPacketID++, Side.SERVER)
		network.registerMessage(PacketHandlerServer, MessageNI::class.java, nextPacketID++, Side.SERVER)
		
		network.registerMessage(PacketHandlerClient, Message0dC::class.java, nextPacketID++, Side.CLIENT)
		network.registerMessage(PacketHandlerClient, Message1d::class.java, nextPacketID++, Side.CLIENT)
		network.registerMessage(PacketHandlerClient, Message1l::class.java, nextPacketID++, Side.CLIENT)
		network.registerMessage(PacketHandlerClient, Message2d::class.java, nextPacketID++, Side.CLIENT)
		network.registerMessage(PacketHandlerClient, Message3d::class.java, nextPacketID++, Side.CLIENT)
		network.registerMessage(PacketHandlerClient, MessageNI::class.java, nextPacketID++, Side.CLIENT)
		
		network.registerMessage(PacketHandlerClient, MessageContributor::class.java, nextPacketID++, Side.CLIENT)
		network.registerMessage(PacketHandlerClient, MessageEffect::class.java, nextPacketID++, Side.CLIENT)
		network.registerMessage(PacketHandlerClient, MessageGleipnirLeash::class.java, nextPacketID++, Side.CLIENT)
		network.registerMessage(PacketHandlerClient, MessageHotSpellC::class.java, nextPacketID++, Side.CLIENT)
		network.registerMessage(PacketHandlerClient, MessageParty::class.java, nextPacketID++, Side.CLIENT)
		network.registerMessage(PacketHandlerClient, MessageRaceInfo::class.java, nextPacketID++, Side.CLIENT)
		network.registerMessage(PacketHandlerClient, MessageRedstoneSignalsSync::class.java, nextPacketID++, Side.CLIENT)
		network.registerMessage(PacketHandlerClient, MessageSkinInfo::class.java, nextPacketID++, Side.CLIENT)
		network.registerMessage(PacketHandlerClient, MessageSpellParams::class.java, nextPacketID++, Side.CLIENT)
		network.registerMessage(PacketHandlerClient, MessageTileItem::class.java, nextPacketID++, Side.CLIENT)
		network.registerMessage(PacketHandlerClient, MessageTimeStop::class.java, nextPacketID++, Side.CLIENT)
		network.registerMessage(PacketHandlerClient, MessageVisualEffect::class.java, nextPacketID++, Side.CLIENT)
	}
}
