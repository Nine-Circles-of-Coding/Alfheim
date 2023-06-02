package alfheim.common.core.proxy

import alexsocol.asjlib.*
import alfheim.api.*
import alfheim.common.achievement.AlfheimAchievements
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.*
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.helper.*
import alfheim.common.core.registry.AlfheimRegistry
import alfheim.common.crafting.recipe.AlfheimRecipes
import alfheim.common.entity.*
import alfheim.common.integration.etfuturum.EtFuturumAlfheimConfig
import alfheim.common.integration.multipart.MultipartAlfheimConfig
import alfheim.common.integration.thaumcraft.TCHandlerShadowFoxAspects
import alfheim.common.item.AlfheimItems
import alfheim.common.lexicon.*
import alfheim.common.world.dim.alfheim.WorldProviderAlfheim
import alfheim.common.world.dim.alfheim.biome.*
import alfheim.common.world.dim.alfheim.biome.BiomeAlfheim.Companion.addEntry
import alfheim.common.world.dim.domains.WorldProviderDomains
import alfheim.common.world.dim.helheim.WorldProviderHelheim
import alfheim.common.world.dim.niflheim.WorldProviderNiflheim
import alfheim.common.world.mobspawn.MobSpawnHandler
import cpw.mods.fml.client.event.ConfigChangedEvent
import cpw.mods.fml.common.*
import cpw.mods.fml.common.eventhandler.*
import net.minecraft.entity.EnumCreatureType
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraft.world.biome.BiomeGenBase
import vazkii.botania.common.Botania
import vazkii.botania.common.core.handler.ConfigHandler
import vazkii.botania.common.item.ModItems

open class CommonProxy {
	
	open fun preInit() {
		AlfheimAPI.RUNEAXE.setRepairItem(ItemStack(ModItems.manaResource, 1, 7)) // Elementium
		
		AlfheimLexiconData.preInit()
		AlfheimBlocks
		AlfheimItems
		AlfheimRegistry.preInit()
		AlfheimAchievements
		BifrostFlowerDispenserHandler
		ThrownPotionDispenserHandler
		ThrownItemDispenserHandler
		WaterBowlDispenserHandler
		if (Botania.thaumcraftLoaded) TCHandlerShadowFoxAspects.initAspects()
		AlfheimMultiblocks
	}
	
	open fun registerRenderThings() = Unit
	
	open fun registerKeyBinds() = Unit
	
	fun init() {
		AlfheimRecipes
		AlfheimRegistry.init()
		ASJUtilities.registerDimension(AlfheimConfigHandler.dimensionIDAlfheim, WorldProviderAlfheim::class.java, true)
		ASJUtilities.registerDimension(AlfheimConfigHandler.dimensionIDNiflheim, WorldProviderNiflheim::class.java, false)
		ASJUtilities.registerDimension(AlfheimConfigHandler.dimensionIDDomains, WorldProviderDomains::class.java, true)
		ASJUtilities.registerDimension(AlfheimConfigHandler.dimensionIDHelheim, WorldProviderHelheim::class.java, true)
		AlfheimBlocks.registerBurnables()
		if (Loader.isModLoaded("ForgeMultipart")) MultipartAlfheimConfig.loadConfig()
		if (Loader.isModLoaded("etfuturum")) EtFuturumAlfheimConfig.loadConfig()
	}
	
	open fun postInit() {
		AlfheimBlocks.regOreDict()
		AlfheimItems.regOreDict()
		AlfheimRecipes.postInit()
		AlfheimLexiconData.init()
		if (ConfigHandler.relicsEnabled) AlfheimLexiconData.initRelics()
		//AlfheimLexiconData.postInit()
		AlfheimRegistry.postInit()
	}
	
	open fun initializeAndRegisterHandlers() {
		EventHandler.eventForge().eventFML()
		ESMHandler.eventForge().eventFML()
		ElvenFlightHandler.eventForge().eventFML()
		ChestGenHandler
		HilarityHandler
		RagnarokHandler
		SoulRestructuringHandler.eventForge()
		MobSpawnHandler
		ElementalDamageHandler.eventForge()
		CardinalSystem.eventForge().eventFML()
		EventHandlerWinter.eventFML()
		EventHandlerSummer.eventForge()
		SpriteKillHandler.eventForge()
		SheerColdHandler.eventForge()
		
		FMLCommonHandler.instance().bus().register(object {
			@SubscribeEvent(priority = EventPriority.HIGHEST)
			fun onConfigChanged(e: ConfigChangedEvent.OnConfigChangedEvent) {
				if (e.modID == ModInfo.MODID) AlfheimConfigHandler.syncConfig()
			}
		})
		
		ContributorsPrivacyHelper
		
		if (HELLISH_VACATION) {
			arrayOf(BiomeBeach, BiomeSandbank, BiomeGenBase.jungle, BiomeGenBase.jungleEdge, BiomeGenBase.jungleHills, BiomeGenBase.beach).forEach {
				it.addEntry(EntityRollingMelon::class.java, AlfheimConfigHandler.pixieSpawn.map { v -> v * 4 }.toIntArray())
			}
			
			BiomeGenBase.hell.getSpawnableList(EnumCreatureType.monster).add(BiomeGenBase.SpawnListEntry(EntityMuspelson::class.java, 20, 4, 4))
		}
	}
	
	open fun bloodFX(world: World, x: Double, y: Double, z: Double, lifetime: Int = 100, size: Float = 1f, gravity: Float = 1f) = Unit
	
	open fun featherFX(world: World, x: Double, y: Double, z: Double, color: Int, size: Float = 1f, lifetime: Float = 1f, distance: Float = 16f, must: Boolean = false, motionX: Double = 0.0, motionY: Double = 0.0, motionZ: Double = 0.0) = Unit
	
	open fun sparkleFX(world: World, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float, size: Float, ageMultiplier: Int = 2, motionX: Double = 0.0, motionY: Double = 0.0, motionZ: Double = 0.0, fake: Boolean = false, noclip: Boolean = false) = Unit
	
	open fun doParticle() = false
}