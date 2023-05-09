package alfheim.common.core.handler

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.ASJConfigHandler
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import net.minecraftforge.common.config.Configuration.*
import java.io.*
import kotlin.math.*

object AlfheimConfigHandler: ASJConfigHandler() {
	
	const val CATEGORY_PRELOAD = CATEGORY_GENERAL + CATEGORY_SPLITTER + "preload"
	const val CATEGORY_INTEGRATION = CATEGORY_GENERAL + CATEGORY_SPLITTER + "integration"
	const val CATEGORY_INT_TC = CATEGORY_INTEGRATION + CATEGORY_SPLITTER + "thaumcraft"
	const val CATEGORY_INT_TiC = CATEGORY_INTEGRATION + CATEGORY_SPLITTER + "tconstruct"
	const val CATEGORY_ALFHEIM = CATEGORY_GENERAL + CATEGORY_SPLITTER + "alfheim"
	const val CATEGORY_NIFLHEIM = CATEGORY_GENERAL + CATEGORY_SPLITTER + "niflheim"
	const val CATEGORY_DOMAINS = CATEGORY_GENERAL + CATEGORY_SPLITTER + "domains"
	const val CATEGORY_WORLDGEN_A = CATEGORY_ALFHEIM + CATEGORY_SPLITTER + "worldgen"
	const val CATEGORY_ENTITIES_A = CATEGORY_WORLDGEN_A + CATEGORY_SPLITTER + "entities"
	const val CATEGORY_WORLDGEN_D = CATEGORY_DOMAINS + CATEGORY_SPLITTER + "worldgen"
	const val CATEGORY_POTIONS = CATEGORY_GENERAL + CATEGORY_SPLITTER + "potions"
	const val CATEGORY_ESMODE = CATEGORY_GENERAL + CATEGORY_SPLITTER + "elvenstory"
	const val CATEGORY_MMO = CATEGORY_ESMODE + CATEGORY_SPLITTER + "mmo"
	const val CATEGORY_MMOP = CATEGORY_MMO + CATEGORY_SPLITTER + "potions"
	const val CATEGORY_HUD = CATEGORY_MMO + CATEGORY_SPLITTER + "hud"
	
	var enableElvenStory: Boolean
		get() = _enableElvenStory
		set(value) {
			_enableElvenStory = value
			config.get(CATEGORY_PRELOAD, "enableElvenStory", value, "Set this to false to disable ESM and MMO").set(value)
			config.save()
		}
	
	var enableMMO: Boolean
		get() = _enableMMO
		set(value) {
			_enableMMO = value
			config.get(CATEGORY_PRELOAD, "enableMMO", value, "Set this to false to disable MMO").set(value)
			config.save()
		}
	
	private var _enableElvenStory = true
	private var _enableMMO = true
	
	// PRELOAD
	var elementiumClusterMeta = 22
	var gaiaBarOffset = 1
	var gaiaNameColor = 0x00D5FF
	var hpHooks = true
	
	// DIMENSION
	// - ALFHEIM
	var dimensionIDAlfheim = -105
	var enableAlfheimRespawn = true
	var rainbowPolys = 360
	
	// - NIFLHEIM
	var dimensionIDNiflheim = -106
	var enableNiflheimRespawn = true
	var niflheimBiomeIDs: IntArray = intArrayOf(152, 153, 154)
	
	// - OTHER
	var dimensionIDDomains = -104
	var dimensionIDHelheim = -103
	
	// WORLDGEN
	// - ALFHEIM
	var anomaliesDispersion = 50
	var anomaliesUpdate = 6000
	var citiesDistance = 1000
	var oregenMultiplier = 3
	var winterGrassReadyGen = true
	
	// - DOMAINS
	var domainDistance = 1000
	var domainImmediate = false
	var domainMaxCount = 5
	var domainStartX = -5000
	var domainStartZ = -5000
	
	// ENTITIES
	// ALFHEIM
	var butterflySpawn = intArrayOf(10, 1, 2)
	var chickSpawn = intArrayOf(10, 4, 4)
	var cowSpawn = intArrayOf(8, 4, 4)
	var elvesSpawn = intArrayOf(10, 2, 4)
	var jellySpawn = intArrayOf(10, 4, 4)
	var pigSpawn = intArrayOf(10, 4, 4)
	var pixieSpawn = intArrayOf(10, 1, 2)
	var sheepSpawn = intArrayOf(12, 4, 4)
	var voidCreeper = intArrayOf(4, 1, 3)
	
	var despawnChunks = 2
	var minChunks = 1
	var maxChunks = 6
	var playerGroupDistance = maxChunks
	
	// OHTER
	var alfheimSleepExtraCheck = true
	var authTimeout = 200
	var blackLotusDropRate = 0.05
	var cataclysmCooldown = 600
	var enderOreWeights = arrayOf("oreEndCoal:9000", "oreEndDiamond:500", "oreEndEmerald:500", "oreEndGold:3635", "oreEndIron:5790", "oreEndLapis:3250", "oreEndRedstone:5600", "oreDraconium:200")
	var eventBanner = true
	var fancies = true
	var faultLinePersistence = 3000
	var flugelSwapBlackList = emptyArray<String>()
	var gourmaryllisDifficulty = 2
	var hotHell = true
	var imPatheticWeakAndScaredDontTouchMyWorlds = false
	var interactEventChecks = false
	var lightningsSpeed = 20
	var longSeasons = true
	var looniumOverseed = false
	var mountAlfheimOnly = false
	var mountCost = 1000
	var mountLife = 600
	var minimalGraphics = false
	var mobPriests = true
	var moonbowMaxDmg = 20
	var moonbowVelocity = 0.5f
	var multibaubleBlacklist = emptyArray<String>()
	var multibaubleCount = 6
	var notifications = true
	var numericalMana = true
	var overcoldBlacklist = arrayOf("alfheim.DedMoroz", "alfheim.SnowSprite", "Skeleton", "SnowMan")
	var overheatBlacklist = arrayOf("alfheim.Muspelson", "alfheim.FireSpirit", "Blaze", "Ghast", "LavaSlime", "PigZombie", "Skeleton", "WitherBoss")
	var realLightning = false
	var repairBlackList = emptyArray<String>()
	var rocketRide = 2
	var searchTabAlfheim = true
	var searchTabBotania = true
	var schemaArray = IntArray(17) { -1 + it }
	var schemaMaxSize = 64
	var soulSwordMaxLvl = Int.MAX_VALUE
	var storyLines = 4
	var tradePortalRate = 1200
	var triquetrumBlackList = emptyArray<String>()
	var triquetrumManaUsage = intArrayOf(100, 60)
	var triquetrumMaxDiagonal = 128.0
	var triquetrumTiles = true
	var uberBlaster = true
	var uberSpreaderCapacity = 24000
	var uberSpreaderSpeed = 2400
	var voidCreepBiomeBlackList = intArrayOf(8, 9, 14, 15)
	var wireoverpowered = true
	lateinit var worldDestroyConfig: Map<Int, Int>
	
	// INTEGRATION
	var chatLimiters = "%s"
	var poolRainbowCapacity = 1000000 // TilePool.MAX_MANA
	
	// TC INTEGRATION
	var addAspectsToBotania = true
	var addTincturemAspect = true
	var thaumTreeSuffusion = true
	
	// TiC INTEGRATION
	var materialIDs = intArrayOf(50, 51, 52, 53, 54, 55, 56, 57, 3, 4)
	var modifierIDs = intArrayOf(20)
	
	// POTIONS
	var potionID___COUNTER = 30
	var potionIDBeastWithin = potionID___COUNTER++
	var potionIDBerserk = potionID___COUNTER++
	var potionIDBleeding = potionID___COUNTER++
	var potionIDButterShield = potionID___COUNTER++
	var potionIDDeathMark = potionID___COUNTER++
	var potionIDDecay = potionID___COUNTER++
	var potionIDEternity = potionID___COUNTER++
	var potionIDGoldRush = potionID___COUNTER++
	var potionIDIceLens = potionID___COUNTER++
	var potionIDLeftFlame = potionID___COUNTER++
	var potionIDLightningShield = potionID___COUNTER++
	var potionIDManaVoid = potionID___COUNTER++
	var potionIDNineLifes = potionID___COUNTER++
	var potionIDNinja = potionID___COUNTER++
	var potionIDNoclip = potionID___COUNTER++
	var potionIDOvermage = potionID___COUNTER++
	var potionIDPossession = potionID___COUNTER++
	var potionIDQuadDamage = potionID___COUNTER++
	var potionIDSacrifice = potionID___COUNTER++
	var potionIDShowMana = potionID___COUNTER++
	var potionIDSoulburn = potionID___COUNTER++
	var potionIDStoneSkin = potionID___COUNTER++
	var potionIDTank = potionID___COUNTER++
	var potionIDThrow = potionID___COUNTER++
	var potionIDWellOLife = potionID___COUNTER++
	var potionIDWisdom = potionID___COUNTER++
	
	// Elven Story
	var bonusChest = false
	var bothSpawnStructures = false
	var flightTime = 12000
	var flightRecover = 1.0
	var wingsBlackList = IntArray(0)
	val zones = Array(9) { Vector3() }
	
	// MMO
	var deathScreenAddTime = 1200
	var disabledSpells = emptyArray<String>()
	var disableWireframe = false
	var frienldyFire = false
	var legendarySpells = arrayOf("sacrifice", "isaacstorm", "resurrect", "timestop", "warhood")
	var maxPartyMembers = 5
	var raceManaMult = 2.toByte()
	var superSpellBosses = false
	
	// MMO HUD
	var partyHUDScale = 1.0
	var selfHealthUI = true
	var spellsFadeOut = false
	var targetUI = true
	
	override fun addCategories() {
		addCategory(CATEGORY_PRELOAD, "Alfheim coremod and preload settings")
		addCategory(CATEGORY_ALFHEIM, "Alfheim dimension settings")
		addCategory(CATEGORY_NIFLHEIM, "Niflheim dimension settings")
		addCategory(CATEGORY_WORLDGEN_A, "Alfheim worldgen settings")
		addCategory(CATEGORY_ENTITIES_A, "Alfheim entities settings")
		addCategory(CATEGORY_POTIONS, "Potion IDs")
		addCategory(CATEGORY_INTEGRATION, "Cross-mods and modpacks integration")
		addCategory(CATEGORY_INT_TC, "Thaumcraft integration")
		addCategory(CATEGORY_INT_TiC, "Tinkers' Construct integration")
		addCategory(CATEGORY_ESMODE, "Elvenstory Mode optional features")
		addCategory(CATEGORY_MMO, "MMO Mode optional features")
		addCategory(CATEGORY_HUD, "HUD elements customizations")
		addCategory(CATEGORY_MMOP, "Potion IDs")
	}
	
	override fun readProperties() {
		_enableElvenStory = loadProp(CATEGORY_PRELOAD, "enableElvenStory", _enableElvenStory, true, "Set this to false to disable ESM and MMO")
		_enableMMO = _enableElvenStory && loadProp(CATEGORY_PRELOAD, "enableMMO", _enableMMO, true, "Set this to false to disable MMO")
		
		elementiumClusterMeta = loadProp(CATEGORY_PRELOAD, "elementiumClusterMeta", elementiumClusterMeta, true, "Effective only if Thaumcraft is installed. Change this if some other mod adds own clusters (max value is 63); also please, edit and spread modified .lang files")
		gaiaBarOffset = loadProp(CATEGORY_PRELOAD, "gaiaBarOffset", gaiaBarOffset, true, "Gaia hp and bg boss bar variant (from default texture pairs)")
		gaiaNameColor = loadProp(CATEGORY_PRELOAD, "gaiaNameColor", gaiaNameColor, false, "Gaia name color on boss bar")
		hpHooks = loadProp(CATEGORY_PRELOAD, "hpHooks", hpHooks, true, "Toggles hooks to vanilla health system. Set this to false if you have any issues with other systems")
		
		dimensionIDAlfheim = loadProp(CATEGORY_ALFHEIM, "dimensionIDAlfheim", dimensionIDAlfheim, true, "Dimension ID for Alfheim")
		enableAlfheimRespawn = loadProp(CATEGORY_ALFHEIM, "enableAlfheimRespawn", enableAlfheimRespawn, false, "Set this to false to disable respawning in Alfheim")
		rainbowPolys = loadProp(CATEGORY_ALFHEIM, "rainbowPolys", rainbowPolys, false, "How smooth will rainbow and rays in Alfheim sky be (higher number - more polygons)")
		
		dimensionIDNiflheim = loadProp(CATEGORY_NIFLHEIM, "dimensionIDNiflheim", dimensionIDNiflheim, true, "Dimension ID for Niflheim")
		enableNiflheimRespawn = loadProp(CATEGORY_NIFLHEIM, "enableNiflheimRespawn", enableNiflheimRespawn, false, "Set this to false to disable respawning in Niflheim")
		niflheimBiomeIDs = loadProp(CATEGORY_NIFLHEIM, "niflheimBiomeIDs", niflheimBiomeIDs, true, "List of Niflheim biome IDs")
		
		dimensionIDDomains = loadProp(CATEGORY_DOMAINS, "dimensionIDDomains", dimensionIDDomains, true, "Dimension ID for Domains world")
		dimensionIDHelheim = loadProp(CATEGORY_GENERAL, "dimensionIDHelheim", dimensionIDHelheim, true, "Dimension ID for Helheim")
		
		anomaliesDispersion = loadProp(CATEGORY_WORLDGEN_A, "anomaliesDispersion", anomaliesDispersion, false, "How rare anomalies are (one per N chunks)")
		anomaliesUpdate = loadProp(CATEGORY_WORLDGEN_A, "anomaliesUpdate", anomaliesUpdate, false, "How many times anomaly will simulate tick while being generated")
		citiesDistance = loadProp(CATEGORY_WORLDGEN_A, "citiesDistance", citiesDistance, true, "Distance between any elven city and worlds center")
		oregenMultiplier = loadProp(CATEGORY_WORLDGEN_A, "oregenMultiplier", oregenMultiplier, true, "Multiplier for Alfheim oregen")
		winterGrassReadyGen = loadProp(CATEGORY_WORLDGEN_A, "winterGrassReadyGen", winterGrassReadyGen, false, "Set this to false to prevent ready generation snow grass instead of regular")
		
		domainDistance = loadProp(CATEGORY_WORLDGEN_D, "domainDistance", domainDistance, true, "Distance between domains")
		domainImmediate = loadProp(CATEGORY_WORLDGEN_D, "domainImmediate", domainImmediate, true, "Set this to true to immediately generate max domains instead of generating new one in same type line if and only if all previously generated ones are occupied")
		domainMaxCount = loadProp(CATEGORY_WORLDGEN_D, "domainMaxCount", domainMaxCount, true, "Count of domains of the same type")
		domainStartX = loadProp(CATEGORY_WORLDGEN_D, "domainStartX", domainStartX, true, "X-position of first domain in different type line")
		domainStartZ = loadProp(CATEGORY_WORLDGEN_D, "domainStartZ", domainStartZ, true, "Z-position of first domain in same type line")
		
		despawnChunks = loadProp(CATEGORY_ENTITIES_A, "despawnChunks", despawnChunks, false, "Additional chunks for if-no-player-nearby despawning")
		minChunks = loadProp(CATEGORY_ENTITIES_A, "minChunks", minChunks, false, "Min distance in chunks from player at which mobs can spawn")
		maxChunks = loadProp(CATEGORY_ENTITIES_A, "maxChunks", maxChunks, false, "Max distance in chunks from player at which mobs can spawn")
		playerGroupDistance = loadProp(CATEGORY_ENTITIES_A, "playerGroupDistance", playerGroupDistance, false, "Distance in chunks for players to be considered as player group (for mob spawning balance)")
		butterflySpawn = loadProp(CATEGORY_ENTITIES_A, "butterflySpawn", butterflySpawn, false, "Butterfly max count per player, min and max group count")
		cowSpawn = loadProp(CATEGORY_ENTITIES_A, "cowSpawn", cowSpawn, false, "Cows max count per player, min and max group count")
		chickSpawn = loadProp(CATEGORY_ENTITIES_A, "chickSpawn", chickSpawn, false, "Chicken max count per player, min and max group count")
		elvesSpawn = loadProp(CATEGORY_ENTITIES_A, "elvesSpawn", elvesSpawn, false, "Elves max count per player, min and max group count")
		jellySpawn = loadProp(CATEGORY_ENTITIES_A, "jellySpawn", jellySpawn, false, "Jellyfish max count per player, min and max group count")
		pigSpawn = loadProp(CATEGORY_ENTITIES_A, "pigSpawn", pigSpawn, false, "Pig max count per player, min and max group count")
		pixieSpawn = loadProp(CATEGORY_ENTITIES_A, "pixieSpawn", pixieSpawn, false, "Pixie max count per player, min and max group count")
		sheepSpawn = loadProp(CATEGORY_ENTITIES_A, "sheepSpawn", sheepSpawn, false, "Sheep max count per player, min and max group count")
		voidCreeper = loadProp(CATEGORY_ENTITIES_A, "voidCreeper", voidCreeper, false, "Manaseal Creeper spawn weight (chance), min and max group count")
		
		alfheimSleepExtraCheck = loadProp(CATEGORY_GENERAL, "alfheimSleepExtraCheck", alfheimSleepExtraCheck, false, "Set this to false if you are skipping whole day while sleeping")
		authTimeout = loadProp(CATEGORY_GENERAL, "authTimeout", authTimeout, false, "Time limit for client to send authentication credentials", 100, 600)
		blackLotusDropRate = loadProp(CATEGORY_GENERAL, "blackLotusDropRate", blackLotusDropRate, false, "Rate of black loti dropping from Manaseal Creepers")
		cataclysmCooldown = loadProp(CATEGORY_GENERAL, "cataclysmCooldown", cataclysmCooldown, false, "Average ticks between cataclysms", 100, 6000)
		enderOreWeights = loadProp(CATEGORY_GENERAL, "enderOreWeights", enderOreWeights, false, "Map of OreDict name to ore weight (more weight - more chace to spawn) for Orechid Endium")
		eventBanner = loadProp(CATEGORY_GENERAL, "eventBanner", eventBanner, false, "Set this to false to disable event banner popup")
		fancies = loadProp(CATEGORY_GENERAL, "fancies", fancies, false, "Set this to false to locally disable fancies rendering on you (for contributors only)")
		faultLinePersistence = loadProp(CATEGORY_GENERAL, "faultLinePersistence", faultLinePersistence, false, "Persistence for Fault Lines (lower value - smaller faults)")
		flugelSwapBlackList = loadProp(CATEGORY_GENERAL, "flugelSwapBlackList", flugelSwapBlackList, false, "Blacklist for items that flugel can't swap [modid:name]", false)
		gourmaryllisDifficulty = loadProp(CATEGORY_GENERAL, "gourmaryllisDifficulty", gourmaryllisDifficulty, false, "Difficulty of Gourmaryllis functionality: 0 - default, 1 - as in 1.12.2, 2 - hardcore", 0, 2)
		hotHell = loadProp(CATEGORY_GENERAL, "hotHell", hotHell, false, "Set this to false to remove overheating in Muspleheim (Hell/Nether)")
		imPatheticWeakAndScaredDontTouchMyWorlds = loadProp(CATEGORY_GENERAL, "imPatheticWeakAndScaredDontTouchMyWorlds", imPatheticWeakAndScaredDontTouchMyWorlds, false, "Set this to true to disable hardcoded world destruction while Ragnarok and affect ONLY Alfheim")
		interactEventChecks = loadProp(CATEGORY_GENERAL, "interactEventChecks", interactEventChecks, false, "Distance checks when firing interaction events, results may be unclear")
		lightningsSpeed = loadProp(CATEGORY_GENERAL, "lightningsSpeed", lightningsSpeed, false, "How many ticks it takes between two lightings are spawned in Lightning Anomaly render")
		longSeasons = loadProp(CATEGORY_GENERAL, "longSeasons", longSeasons, true, "Set this to false to make seasons last 1 real day instead of 3")
		looniumOverseed = loadProp(CATEGORY_GENERAL, "looniumOverseed", looniumOverseed, true, "Set this to true to make loonium spawn overgrowth seeds (for servers with limited dungeons so all players can craft Gaia pylons)")
		minimalGraphics = loadProp(CATEGORY_GENERAL, "minimalGraphics", minimalGraphics, true, "Set this to true to disable .obj models and shaders")
		mobPriests = loadProp(CATEGORY_GENERAL, "mobPriests", mobPriests, false, "Set this to false so that only players can be priests")
		moonbowMaxDmg = loadProp(CATEGORY_GENERAL, "moonbowMaxDmg", moonbowMaxDmg, false, "Max base damage for Phoebus Catastrophe")
		moonbowVelocity = loadProp(CATEGORY_GENERAL, "moonbowVelocity", moonbowVelocity.D, false, "Phoebus Catastrophe charge speed").F
		mountAlfheimOnly = loadProp(CATEGORY_GENERAL, "mountAlfheimOnly", mountAlfheimOnly, false, "Set this to false to make mounts summonable only in Alfheim")
		mountCost = loadProp(CATEGORY_GENERAL, "mountCost", mountCost, false, "How much mana mount consumes on summoning (not teleporting)")
		mountLife = loadProp(CATEGORY_GENERAL, "mountLife", mountLife, false, "How many ticks mount can stay unmounted")
		multibaubleBlacklist = loadProp(CATEGORY_GENERAL, "multibaubleBlacklist", multibaubleBlacklist, false, "Blacklist for Ring of Elven King [modid:name]", false)
		multibaubleCount = loadProp(CATEGORY_GENERAL, "multibaubleCount", multibaubleCount, false, "How many bauble box slots will be activated by Ring of Elven King")
		notifications = loadProp(CATEGORY_GENERAL, "notifications", notifications, false, "Set this to false to disable custom notifications and version check")
		numericalMana = loadProp(CATEGORY_GENERAL, "numericalMana", numericalMana, false, "Set this to false to disable numerical mana representation")
		overcoldBlacklist = loadProp(CATEGORY_GENERAL, "overcoldBlacklist", overcoldBlacklist, false, "List of entity names with no cold gauge filling ", false)
		overheatBlacklist = loadProp(CATEGORY_GENERAL, "overheatBlacklist", overheatBlacklist, false, "List of entity names with no heat gauge filling ", false)
		realLightning = loadProp(CATEGORY_GENERAL, "realLightning", realLightning, false, "Set this to true to make lightning rod summon real (weather) lightning")
		repairBlackList = loadProp(CATEGORY_GENERAL, "repairBlackList", repairBlackList, false, "Blacklist of repairable items (ex: for anyavil) [modid:name]", false)
		rocketRide = loadProp(CATEGORY_GENERAL, "rocketRide", rocketRide, false, "Rocket ride [-1 - not players, 0 - none, 1 - players, 2 - anyone]")
		searchTabAlfheim = loadProp(CATEGORY_GENERAL, "searchTabAlfheim", searchTabAlfheim, false, "Set this to false to disable searchbar in Alfheim Tab")
		searchTabBotania = loadProp(CATEGORY_GENERAL, "searchTabBotania", searchTabBotania, false, "Set this to false to disable searchbar in Botania Tab")
		schemaArray = loadProp(CATEGORY_GENERAL, "schemaArray", schemaArray, false, "Which schemas are allowed to be generated", false)
		schemaMaxSize = loadProp(CATEGORY_GENERAL, "schemaMaxSize", schemaMaxSize, false, "Max schema cuboid side length")
		soulSwordMaxLvl = loadProp(CATEGORY_GENERAL, "soulSwordMaxLvl", soulSwordMaxLvl, false, "Sword of Ragnarok max level")
		storyLines = loadProp(CATEGORY_GENERAL, "storyLines", storyLines, false, "Number of lines for story token")
		tradePortalRate = loadProp(CATEGORY_GENERAL, "tradePortalRate", tradePortalRate, false, "Portal updates every [N] ticks")
		triquetrumBlackList = loadProp(CATEGORY_GENERAL, "triquetrumBlackList", triquetrumBlackList, false, "Blacklist for blocks that triquetrum can't swap [modid:name]", false)
		triquetrumManaUsage = loadProp(CATEGORY_GENERAL, "triquetrumManaUsage", triquetrumManaUsage, false, "Mana usage for triquetrum, 1st is for tiles, 2nd for regular blocks")
		triquetrumMaxDiagonal = loadProp(CATEGORY_GENERAL, "triquetrumMaxDiagonal", triquetrumMaxDiagonal, false, "Change this to limit triquetrum area")
		triquetrumTiles = loadProp(CATEGORY_GENERAL, "triquetrumTiles", triquetrumTiles, false, "Set this to false to forbid triquetrum to move tiles")
		uberBlaster = loadProp(CATEGORY_GENERAL, "uberBlaster", uberBlaster, false, "Set this to false to nerf blasters")
		uberSpreaderCapacity = loadProp(CATEGORY_GENERAL, "uberSpreaderCapacity", uberSpreaderCapacity, false, "Mauftrium Spreader max mana cap")
		uberSpreaderSpeed = loadProp(CATEGORY_GENERAL, "uberSpreaderSpeed", uberSpreaderSpeed, false, "Mauftrium Spreader mana per shot")
		voidCreepBiomeBlackList = loadProp(CATEGORY_GENERAL, "voidCreepersBiomeBlackList", voidCreepBiomeBlackList, true, "Biome blacklist for Manaseal Creepers", false)
		wireoverpowered = loadProp(CATEGORY_GENERAL, "wire.overpowered", wireoverpowered, false, "Allow WireSegal far more power than any one person should have")
		worldDestroyConfig = loadProp(CATEGORY_GENERAL, "worldDestroyConfig", emptyArray(), false, "List of world destruction types while Ragnarok in form of string 'dimID:type' (types: 0 - none, 1 - only while ginnungagap, 2 - all)", false).map {
			val (id, type) = it.split(':')
			id.toInt() to type.toInt()
		}.associate { it }
		
		chatLimiters = loadProp(CATEGORY_INTEGRATION, "chatLimiters", chatLimiters, false, "Chat limiters for formtatting special chat lines when using chat plugins")
		poolRainbowCapacity = loadProp(CATEGORY_INTEGRATION, "poolRainbowCapacity", poolRainbowCapacity, false, "Fabulous manapool capacity (for custom modpacks with A LOT of mana usage. Can be applied only to NEW pools)")
		
		addAspectsToBotania = loadProp(CATEGORY_INT_TC, "TC.botaniaAspects", addAspectsToBotania, true, "Set this to false to disable adding aspects to Botania")
		addTincturemAspect = loadProp(CATEGORY_INT_TC, "TC.tincturem", addTincturemAspect, true, "Set this to false to use Sensus instead of Color aspect")
		thaumTreeSuffusion = loadProp(CATEGORY_INT_TC, "TC.treeCrafting", thaumTreeSuffusion, true, "[GoG] Set this to false to remove Thaumcraft plants Dendric Suffusion")
		
		materialIDs = loadProp(CATEGORY_INT_TiC, "TiC.materialIDs", materialIDs, true, "IDs for Elementium, Elvorium, Manasteel, Mauftrium, Terrasteel, Livingwood, Dreamwood, Livingrock, Redstring, Manastring materials respectively")
		modifierIDs = loadProp(CATEGORY_INT_TiC, "TiC.modifierIDs", modifierIDs, true, "IDs for ManaCore modifiers respectively")
		
		potionIDBeastWithin = loadProp(CATEGORY_POTIONS, "potionIDBeastWithin", potionIDBeastWithin, true, "Potion id Beast Within")
		potionIDBerserk = loadProp(CATEGORY_POTIONS, "potionIDBerserk", potionIDBerserk, true, "Potion id for Berserk")
		potionIDBleeding = loadProp(CATEGORY_POTIONS, "potionIDBleeding", potionIDBleeding, true, "Potion id for Bleeding")
		potionIDButterShield = loadProp(CATEGORY_MMOP, "potionIDButterShield", potionIDButterShield, true, "Potion id for Butterfly Shield")
		potionIDDeathMark = loadProp(CATEGORY_MMOP, "potionIDDeathMark", potionIDDeathMark, true, "Potion id for Death Mark")
		potionIDDecay = loadProp(CATEGORY_MMOP, "potionIDDecay", potionIDDecay, true, "Potion id for Decay")
		potionIDEternity = loadProp(CATEGORY_POTIONS, "potionIDEternity", potionIDEternity, true, "Potion id for Eternity")
		potionIDGoldRush = loadProp(CATEGORY_MMOP, "potionIDGoldRush", potionIDGoldRush, true, "Potion id for Gold Rush")
		potionIDIceLens = loadProp(CATEGORY_POTIONS, "potionIDIceLens", potionIDIceLens, true, "Potion id for Ice Lense")
		potionIDLeftFlame = loadProp(CATEGORY_MMOP, "potionIDLeftFlame", potionIDLeftFlame, true, "Potion id for Leftover Flame")
		potionIDLightningShield = loadProp(CATEGORY_POTIONS, "potionIDLightningShield", potionIDLightningShield, true, "Potion id for Lightning Shield")
		potionIDManaVoid = loadProp(CATEGORY_POTIONS, "potionIDManaVoid", potionIDManaVoid, true, "Potion id for Mana Void")
		potionIDNineLifes = loadProp(CATEGORY_MMOP, "potionIDNineLifes", potionIDNineLifes, true, "Potion id for Nine Lifes")
		potionIDNinja = loadProp(CATEGORY_POTIONS, "potionIDNinja", potionIDNinja, true, "Potion id for Ninja")
		potionIDNoclip = loadProp(CATEGORY_MMOP, "potionIDNoclip", potionIDNoclip, true, "Potion id for Noclip")
		potionIDOvermage = loadProp(CATEGORY_POTIONS, "potionIDOvermage", potionIDOvermage, true, "Potion id for Overmage")
		potionIDPossession = loadProp(CATEGORY_POTIONS, "potionIDPossession", potionIDPossession, true, "Potion id for Possession")
		potionIDQuadDamage = loadProp(CATEGORY_MMOP, "potionIDQuadDamage", potionIDQuadDamage, true, "Potion id for Quad Damage")
		potionIDSacrifice = loadProp(CATEGORY_MMOP, "potionIDSacrifice", potionIDSacrifice, true, "Potion id for Sacrifice")
		potionIDShowMana = loadProp(CATEGORY_MMOP, "potionIDShowMana", potionIDShowMana, true, "Potion id for Mana Showing Effect")
		potionIDSoulburn = loadProp(CATEGORY_POTIONS, "potionIDSoulburn", potionIDSoulburn, true, "Potion id for Soulburn")
		potionIDStoneSkin = loadProp(CATEGORY_MMOP, "potionIDStoneSkin", potionIDStoneSkin, true, "Potion id for Stone Skin")
		potionIDTank = loadProp(CATEGORY_POTIONS, "potionIDTank", potionIDTank, true, "Potion id for Tank")
		potionIDThrow = loadProp(CATEGORY_MMOP, "potionIDThrow", potionIDThrow, true, "Potion id for Throw")
		potionIDWellOLife = loadProp(CATEGORY_MMOP, "potionIDWellOLife", potionIDWellOLife, true, "Potion id for Well'o'Life")
		potionIDWisdom = loadProp(CATEGORY_POTIONS, "potionIDWisdom", potionIDWisdom, true, "Potion id for Wisdom")
		
		bonusChest = loadProp(CATEGORY_WORLDGEN_A, "bonusChest", bonusChest, false, "Set this to true to generate bonus chest in ESM sky box")
		bothSpawnStructures = loadProp(CATEGORY_ESMODE, "bothSpawnStructures", bothSpawnStructures, false, "Set this to true to generate both race room inside and portal on top of Yggdrasil on zero coords of Alfheim")
		flightTime = loadProp(CATEGORY_ESMODE, "flightTime", flightTime, false, "Elven flight fly points (faster you move - more you spend)")
		flightRecover = loadProp(CATEGORY_ESMODE, "flightRecover", flightRecover, false, "Flight recover efficiency")
		wingsBlackList = loadProp(CATEGORY_ESMODE, "wingsBlackList", wingsBlackList, false, "Wings will be unavailable in this dimension(s)", false)
		
		deathScreenAddTime = loadProp(CATEGORY_MMO, "deathScreenAdditionalTime", deathScreenAddTime, false, "Duration of death screen timer (in ticks)")
		disabledSpells = loadProp(CATEGORY_MMO, "disabledSpells", disabledSpells, true, "List of spell name IDs that won't be registered", false)
		disableWireframe = loadProp(CATEGORY_MMO, "disableWireframe", disableWireframe, false, "Set this to true to disable rendering block wireframe in noclip mode")
		frienldyFire = loadProp(CATEGORY_MMO, "frienldyFire", frienldyFire, false, "Set this to true to enable damage to party members")
		legendarySpells = loadProp(CATEGORY_MMO, "legendarySpells", legendarySpells, false, "Spells that are considered 'epic' thus costing same for all races", false)
		maxPartyMembers = loadProp(CATEGORY_MMO, "maxPartyMembers", maxPartyMembers, false, "How many people can be in single party at the same time")
		raceManaMult = loadProp(CATEGORY_MMO, "raceManaMult", raceManaMult.I, false, "Mana cost multiplier for spells with not your affinity").toByte()
		superSpellBosses = loadProp(CATEGORY_MMO, "superSpellBoss", superSpellBosses, false, "Set this to true to make bosses vulnerable to legendary spells")
		
		partyHUDScale = loadProp(CATEGORY_HUD, "partyHUDScale", partyHUDScale, false, "Party HUD Scale (1 < bigger; 1 > smaller)")
		selfHealthUI = loadProp(CATEGORY_HUD, "selfHealthUI", selfHealthUI, false, "Set this to false to hide player's healthbar")
		spellsFadeOut = loadProp(CATEGORY_HUD, "spellsFadeOut", spellsFadeOut, false, "Set this to true to make spell UI fade out when not active")
		targetUI = loadProp(CATEGORY_HUD, "targethUI", targetUI, false, "Set this to false to hide target's healthbar")
	}
	
	fun initWorldCoordsForElvenStory(save: String) {
		val file = File("$save/data/${ModInfo.MODID}/AlfheimCoords.txt")
		if (!file.exists()) makeDefaultWorldCoords(file)
		
		try {
			val fr = FileReader(file)
			val br = BufferedReader(fr)
			for (i in zones.indices) {
				br.readLine()
				try {
					zones[i] = makeVectorFromString(br.readLine())
				} catch (e: IllegalArgumentException) {
					br.close()
					fr.close()
					throw e
				}
				
			}
			br.close()
			fr.close()
		} catch (e: IOException) {
			System.err.println("Unable to read Alfheim Coords data. Creating default...")
			e.printStackTrace()
			makeDefaultWorldCoords(file)
		}
		
	}
	
	private fun makeDefaultWorldCoords(file: File) {
		try {
			file.parentFile.mkdirs()
			val fw = FileWriter(file)
			
			val s = StringBuilder()
			s.append("Salamander start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(0.000))
			s.append("Sylph start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(40.00))
			s.append("Cait Sith start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(80.00))
			s.append("Puca start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(120.0))
			s.append("Gnome start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(160.0))
			s.append("Leprechaun start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(200.0))
			s.append("Spriggan start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(240.0))
			s.append("Undine start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(280.0))
			s.append("Imp start city and players spawnpoint coords:\n")
			s.append(writeStandardCoords(320.0))
			fw.write("$s")
			fw.close()
		} catch (e: IOException) {
			ASJUtilities.error("Unable to generate default Alfheim Coords data. Setting all to [0, 300, 0]...")
			e.printStackTrace()
			
			for (i in zones.indices) {
				zones[i] = Vector3(0, 300, 0)
			}
		}
	}
	
	private fun writeStandardCoords(angle: Double): String {
		val v = mkVecLenRotMine(citiesDistance, angle)
		return "${v.x.mfloor()} : 300 : ${v.z.mfloor()}\n"
	}
	
	private fun makeVectorFromString(s: String): Vector3 {
		val ss = s.split(" : ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		require(ss.size == 3) { String.format("Wrong coords count. Expected 3 got %d", ss.size) }
		return Vector3(ss[0].toInt(), ss[1].toInt(), ss[2].toInt())
	}
	
	private fun mkVecLenRotMine(length: Int, angle: Double) =
		makeVectorOfLengthRotated(length, angle + 90)
	
	private fun makeVectorOfLengthRotated(length: Int, angle: Double) =
		Vector3(cos(Math.toRadians(angle)) * length, 64.0, sin(Math.toRadians(angle)) * length)
}
