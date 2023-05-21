package alfheim.common.lexicon

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.AlfheimCore
import alfheim.api.*
import alfheim.client.core.handler.CardinalSystemClient.PlayerSegmentClient
import alfheim.common.achievement.AlfheimAchievements
import alfheim.common.block.*
import alfheim.common.block.tile.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem.Knowledge
import alfheim.common.crafting.recipe.AlfheimRecipes
import alfheim.common.integration.thaumcraft.ThaumcraftSuffusionRecipes
import alfheim.common.integration.tinkersconstruct.TinkersConstructAlfheimConfig
import alfheim.common.item.*
import alfheim.common.item.block.*
import alfheim.common.item.material.ElvenFoodMetas.*
import alfheim.common.item.material.ElvenResourcesMetas.*
import alfheim.common.item.material.EventResourcesMetas
import alfheim.common.lexicon.AlfheimLexiconEntry.Companion.setIcon
import alfheim.common.lexicon.page.*
import net.minecraft.item.ItemStack
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.lexicon.*
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.brew.ModBrews
import vazkii.botania.common.core.handler.ConfigHandler
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.lexicon.LexiconData
import vazkii.botania.common.lexicon.page.*

object AlfheimLexiconData {
	
	lateinit var categoryAlfheim: LexiconCategory
	lateinit var categotyDendrology: AlfheimLexiconCategory
	lateinit var categoryDivinity: LexiconCategory
	lateinit var categoryEvents: LexiconCategory
	
	// Main addon content
	lateinit var advancedMana: LexiconEntry
	lateinit var alfheim: LexiconEntry
	lateinit var amplifier: LexiconEntry
	lateinit var amuletCirus: LexiconEntry
	lateinit var amuletIceberg: LexiconEntry
	lateinit var amuletNimbus: LexiconEntry
	lateinit var amulterCrescent: LexiconEntry
	lateinit var animatedTorch: LexiconEntry
	lateinit var anomaly: LexiconEntry
	lateinit var anyavil: LexiconEntry
	lateinit var astrolabe: LexiconEntry
	lateinit var armilla: LexiconEntry
	lateinit var aurora: LexiconEntry
	lateinit var beltRation: LexiconEntry
	lateinit var calicoSapling: LexiconEntry
	lateinit var circuitSapling: LexiconEntry
	lateinit var cloakInvis: LexiconEntry
	lateinit var coatOfArms: LexiconEntry
	lateinit var colorOverride: LexiconEntry
	lateinit var coloredDirt: LexiconEntry
	lateinit var corpInj: LexiconEntry
	lateinit var corpSeq: LexiconEntry
	lateinit var dagger: LexiconEntry
	lateinit var dasGold: LexiconEntry
	lateinit var daolos: LexiconEntry
	lateinit var deathSeed: LexiconEntry
	lateinit var elementalSet: LexiconEntry
	lateinit var elvenSet: LexiconEntry
	lateinit var elvorium: LexiconEntry
	lateinit var enderAct: LexiconEntry
	lateinit var elves: LexiconEntry
	lateinit var essences: LexiconEntry
	lateinit var excaliber: LexiconEntry
	lateinit var fenrir: LexiconEntry
	lateinit var fenrirDrop: LexiconEntry
	lateinit var flowerAconite: LexiconEntry
	lateinit var flowerAquapanthus: LexiconEntry
	lateinit var flowerBud: LexiconEntry
	lateinit var flowerCrysanthermum: LexiconEntry
	lateinit var flowerEnderchid: LexiconEntry
	lateinit var flowerPetronia: LexiconEntry
	lateinit var flowerRain: LexiconEntry
	lateinit var flowerSnow: LexiconEntry
	lateinit var flowerStorm: LexiconEntry
	lateinit var flowerWind: LexiconEntry
	lateinit var flugel: LexiconEntry
	lateinit var fracturedSpace: LexiconEntry
	lateinit var frozenStar: LexiconEntry
	lateinit var gjallarhorn: LexiconEntry
	lateinit var gleipnir: LexiconEntry
	lateinit var goddessCharm: LexiconEntry
	lateinit var gungnir: LexiconEntry
	lateinit var hyperBucket: LexiconEntry
	lateinit var infuser: LexiconEntry
	lateinit var irisSapling: LexiconEntry
	lateinit var itemDisplay: LexiconEntry
	lateinit var kindling: LexiconEntry
	lateinit var lamp: LexiconEntry
	lateinit var legends: LexiconEntry
	lateinit var lembas: LexiconEntry
	lateinit var lightningSapling: LexiconEntry
	lateinit var livingwoodFunnel: LexiconEntry
	lateinit var lootInt: LexiconEntry
	lateinit var manaAccelerator: LexiconEntry
	lateinit var manaImba: LexiconEntry
	lateinit var manaLamp: LexiconEntry
	lateinit var mask: LexiconEntry
	lateinit var mitten: LexiconEntry
	lateinit var mjolnir: LexiconEntry
	lateinit var mobs: LexiconEntry
	lateinit var moonbow: LexiconEntry
	lateinit var multbauble: LexiconEntry
	lateinit var netherSapling: LexiconEntry
	lateinit var ores: LexiconEntry
	lateinit var pastoralSeeds: LexiconEntry
	lateinit var pixie: LexiconEntry
	lateinit var portal: LexiconEntry
	lateinit var pylons: LexiconEntry
	lateinit var corpQuandex: LexiconEntry
	lateinit var rainbowFlora: LexiconEntry
	lateinit var reality: LexiconEntry
	lateinit var ringAnomaly: LexiconEntry
	lateinit var ringDodge: LexiconEntry
	lateinit var ringManaDrive: LexiconEntry
	lateinit var ringSpider: LexiconEntry
	lateinit var ringsAura: LexiconEntry
	lateinit var rodClick: LexiconEntry
	lateinit var rodGreen: LexiconEntry
	lateinit var rodPrismatic: LexiconEntry
	lateinit var rodRedstone: LexiconEntry
	lateinit var ruling: LexiconEntry
	lateinit var runes: LexiconEntry
	lateinit var sealCreepers: LexiconEntry
	lateinit var shimmer: LexiconEntry
	lateinit var shrines: LexiconEntry
	lateinit var silencer: LexiconEntry
	lateinit var soul: LexiconEntry
	lateinit var soulHorn: LexiconEntry
	lateinit var soulSword: LexiconEntry
	lateinit var specialAxe: LexiconEntry
	lateinit var subspear: LexiconEntry
	lateinit var tctrees: LexiconEntry
	lateinit var temperature: LexiconEntry
	lateinit var terraHarvester: LexiconEntry
	lateinit var throwablePotions: LexiconEntry
	lateinit var thunderChakram: LexiconEntry
	lateinit var trade: LexiconEntry
	lateinit var treeCrafting: LexiconEntry
	lateinit var triquetrum: LexiconEntry
	lateinit var uberSpreader: LexiconEntry
	lateinit var winery: LexiconEntry
	lateinit var worldgen: LexiconEntry
	
	// Elven Story information
	var esm: LexiconEntry? = null
	var races: LexiconEntry? = null
	
	// MMO info
	var parties: LexiconEntry? = null
	var spells: LexiconEntry? = null
	var targets: LexiconEntry? = null
	
	// divinity
	lateinit var divIntro: LexiconEntry
	lateinit var abyss: LexiconEntry
	lateinit var vafthrudnir: LexiconEntry
	
	lateinit var cloakThor: LexiconEntry
	lateinit var cloakSif: LexiconEntry
	lateinit var cloakNjord: LexiconEntry
	lateinit var cloakLoki: LexiconEntry
	lateinit var cloakHeimdall: LexiconEntry
	lateinit var cloakOdin: LexiconEntry
	
	lateinit var emblemThor: LexiconEntry
	lateinit var emblemSif: LexiconEntry
	lateinit var emblemNjord: LexiconEntry
	lateinit var emblemLoki: LexiconEntry
	lateinit var emblemHeimdall: LexiconEntry
	lateinit var emblemOdin: LexiconEntry
	
	lateinit var ringSif: LexiconEntry
	lateinit var ringNjord: LexiconEntry
	lateinit var ringHeimdall: LexiconEntry
	
	lateinit var rodThor: LexiconEntry
	lateinit var rodSif: LexiconEntry
	lateinit var rodNjord: LexiconEntry
	lateinit var rodLoki: LexiconEntry
	lateinit var rodOdin: LexiconEntry
	
	lateinit var HV: LexiconEntry
	lateinit var WOTW: LexiconEntry
	
	fun preInit() {
		categoryAlfheim = AlfheimLexiconCategory("Alfheim", 5)
		categoryDivinity = AlfheimLexiconCategory("Divinity", 5)
		categotyDendrology = AlfheimLexiconCategory("dendrology", 1)
		categoryEvents = AlfheimLexiconCategory("events", 4)
		
		advancedMana = AlfheimLexiconEntry("advMana", categoryAlfheim)
		alfheim = AlfheimLexiconEntry("alfheim", categoryAlfheim)
		amplifier = AlfheimLexiconEntry("amplifier", categoryAlfheim)
		amuletCirus = AlfheimLexiconEntry("amulCirs", categoryAlfheim)
		amulterCrescent = AlfheimLexiconEntry("crescent", categoryAlfheim)
		amuletIceberg = AlfheimLexiconEntry("iceberg", categoryAlfheim)
		amuletNimbus = AlfheimLexiconEntry("amulNimb", categoryAlfheim)
		animatedTorch = AlfheimLexiconEntry("aniTorch", categoryAlfheim)
		anomaly = AlfheimLexiconEntry("anomaly", categoryAlfheim)
		anyavil = AlfheimLexiconEntry("anyavil", categoryAlfheim)
		astrolabe = AlfheimLexiconEntry("astrolab", categoryAlfheim)
		armilla = AlfheimLexiconEntry("armilla", categoryAlfheim)
		aurora = AlfheimLexiconEntry("aurora", categoryAlfheim)
		beltRation = AlfheimLexiconEntry("ration", categoryAlfheim)
		calicoSapling = AlfheimLexiconEntry("calicoSapling", categotyDendrology)
		circuitSapling = AlfheimLexiconEntry("circuitSapling", categotyDendrology)
		cloakInvis = AlfheimLexiconEntry("cloakInv", categoryAlfheim)
		coatOfArms = AlfheimLexiconEntry("coatOfArms", categoryAlfheim)
		coloredDirt = AlfheimLexiconEntry("coloredDirt", categoryAlfheim)
		colorOverride = AlfheimLexiconEntry("colorOverride", categoryAlfheim)
		corpInj = AlfheimLexiconEntry("corpInj", categoryAlfheim)
		corpQuandex = AlfheimLexiconEntry("corpQuandex", categoryAlfheim)
		corpSeq = AlfheimLexiconEntry("corpSeq", categoryAlfheim)
		dagger = AlfheimRelicLexiconEntry("dagger", categoryAlfheim)
		dasGold = AlfheimLexiconEntry("dasGold", categoryAlfheim)
		deathSeed = AlfheimLexiconEntry("deathSeed", categoryAlfheim)
		elementalSet = AlfheimLexiconEntry("elemSet", categoryAlfheim)
		elvenSet = AlfheimLexiconEntry("elvenSet", categoryAlfheim)
		elves = AlfheimLexiconEntry("elves", categoryAlfheim)
		enderAct = AlfheimLexiconEntry("endAct", categoryAlfheim)
		essences = AlfheimLexiconEntry("essences", categoryAlfheim)
		elvorium = AlfheimLexiconEntry("elvorium", categoryAlfheim)
		fenrir = AlfheimLexiconEntry("fenrir", categoryAlfheim)
		fenrirDrop = AlfheimLexiconEntry("fenrirDrop", categoryAlfheim)
		flowerAconite = AlfheimLexiconEntry("aconite", categoryAlfheim)
		flowerAquapanthus = AlfheimLexiconEntry("aquapanthus", categoryAlfheim)
		flowerBud = AlfheimLexiconEntry("bud", categoryAlfheim)
		flowerCrysanthermum = AlfheimLexiconEntry("crysanthermum", categoryAlfheim)
		flowerEnderchid = AlfheimLexiconEntry("flowerEnderchid", categoryAlfheim)
		flowerPetronia = AlfheimLexiconEntry("flowerPetronia", categoryAlfheim)
		flowerRain = AlfheimLexiconEntry("flowerRain", categoryAlfheim)
		flowerSnow = AlfheimLexiconEntry("flowerSnow", categoryAlfheim)
		flowerStorm = AlfheimLexiconEntry("flowerStorm", categoryAlfheim)
		flowerWind = AlfheimLexiconEntry("flowerWind", categoryAlfheim)
		flugel = AlfheimLexiconEntry("flugel", categoryAlfheim)
		fracturedSpace = AlfheimLexiconEntry("fracturedSpace", categoryAlfheim)
		frozenStar = AlfheimLexiconEntry("starBlock", categoryAlfheim)
		goddessCharm = AlfheimLexiconEntry("goddessCharm", categoryAlfheim)
		hyperBucket = AlfheimLexiconEntry("hyperBuk", categoryAlfheim)
		infuser = AlfheimLexiconEntry("infuser", categoryAlfheim)
		irisSapling = AlfheimLexiconEntry("irisSapling", categotyDendrology)
		itemDisplay = AlfheimLexiconEntry("itemDisplay", categoryAlfheim)
		kindling = AlfheimLexiconEntry("kindling", categoryAlfheim)
		lamp = AlfheimLexiconEntry("lamp", categoryAlfheim)
		legends = AlfheimLexiconEntry("legends", categoryAlfheim)
		lembas = AlfheimLexiconEntry("lembas", categoryAlfheim)
		lightningSapling = AlfheimLexiconEntry("lightningSapling", categotyDendrology)
		livingwoodFunnel = AlfheimLexiconEntry("livingwoodFunnel", categoryAlfheim)
		lootInt = AlfheimLexiconEntry("lootInt", categoryAlfheim)
		manaAccelerator = AlfheimLexiconEntry("itemHold", categoryAlfheim)
		manaImba = AlfheimLexiconEntry("manaImba", categoryAlfheim)
		manaLamp = AlfheimLexiconEntry("manaLamp", categoryAlfheim)
		mitten = AlfheimLexiconEntry("mitten", categoryAlfheim)
		mobs = AlfheimLexiconEntry("mobs", categoryAlfheim)
		multbauble = AlfheimLexiconEntry("multbaub", categoryAlfheim)
		netherSapling = AlfheimLexiconEntry("infernalSapling", categotyDendrology)
		ores = AlfheimLexiconEntry("ores", categoryAlfheim)
		pastoralSeeds = AlfheimLexiconEntry("irisSeeds", categoryAlfheim)
		pixie = AlfheimLexiconEntry("pixie", categoryAlfheim)
		portal = AlfheimLexiconEntry("portal", categoryAlfheim)
		pylons = AlfheimLexiconEntry("pylons", categoryAlfheim)
		rainbowFlora = AlfheimLexiconEntry("rainbowFlora", categoryAlfheim)
		reality = AlfheimLexiconEntry("reality", categoryAlfheim)
		ringsAura = AlfheimLexiconEntry("auraAlf", categoryAlfheim)
		ringAnomaly = AlfheimLexiconEntry("anomaRing", categoryAlfheim)
		ringDodge = AlfheimLexiconEntry("dodgRing", categoryAlfheim)
		ringManaDrive = AlfheimLexiconEntry("manaDrive", categoryAlfheim)
		ringSpider = AlfheimLexiconEntry("spider", categoryAlfheim)
		rodClick = AlfheimLexiconEntry("rodClick", categoryAlfheim)
		rodGreen = AlfheimLexiconEntry("greenRod", categoryAlfheim)
		rodPrismatic = AlfheimLexiconEntry("rodPrismatic", categoryAlfheim)
		rodRedstone = AlfheimLexiconEntry("rodRedstone", categoryAlfheim)
		ruling = AlfheimLexiconEntry("ruling", categoryAlfheim)
		runes = AlfheimLexiconEntry("runes", categoryAlfheim)
		sealCreepers = AlfheimLexiconEntry("sealCreepers", categoryAlfheim)
		shimmer = AlfheimLexiconEntry("shimmer", categoryAlfheim)
		shrines = AlfheimLexiconEntry("shrines", categoryAlfheim)
		silencer = AlfheimLexiconEntry("silencer", categotyDendrology)
		soulSword = AlfheimLexiconEntry("soulSword", categoryAlfheim)
		specialAxe = AlfheimRelicLexiconEntry("andmyaxe", categoryAlfheim)
		temperature = AlfheimLexiconEntry("temperature", categoryAlfheim)
		terraHarvester = AlfheimLexiconEntry("terraHarvester", categoryAlfheim)
		throwablePotions = AlfheimLexiconEntry("throwablePotions", categoryAlfheim)
		thunderChakram = AlfheimLexiconEntry("thunderChakram", categoryAlfheim)
		trade = AlfheimLexiconEntry("trade", categoryAlfheim)
		treeCrafting = AlfheimLexiconEntry("treeCrafting", categotyDendrology)
		triquetrum = AlfheimLexiconEntry("triquetrum", categoryAlfheim)
		uberSpreader = AlfheimLexiconEntry("uberSpreader", categoryAlfheim)
		winery = AlfheimLexiconEntry("winery", categoryAlfheim)
		worldgen = AlfheimLexiconEntry("worldgen", categoryAlfheim)
		
		
		
		divIntro = AlfheimLexiconEntry("divinity_intro", categoryDivinity)
		abyss = AlfheimAbyssalLexiconEntry("abyss", "aesir", categoryDivinity)
		vafthrudnir = object: AlfheimLexiconEntry("vafthrudnir", categoryDivinity) {
			override fun isVisible() = Knowledge.ABYSS_TRUTH.toString() in PlayerSegmentClient.knowledge
		}
		
		cloakThor = AlfheimLexiconEntry("garb_thor", categoryDivinity)
		cloakSif = AlfheimLexiconEntry("garb_sif", categoryDivinity)
		cloakNjord = AlfheimLexiconEntry("garb_njord", categoryDivinity)
		cloakLoki = AlfheimLexiconEntry("garb_loki", categoryDivinity)
		cloakHeimdall = AlfheimLexiconEntry("garb_heimdall", categoryDivinity)
		cloakOdin = AlfheimLexiconEntry("garb_odin", categoryDivinity)
		
		emblemThor = AlfheimLexiconEntry("thor", categoryDivinity)
		emblemSif = AlfheimLexiconEntry("sif", categoryDivinity)
		emblemNjord = AlfheimLexiconEntry("njord", categoryDivinity)
		emblemLoki = AlfheimLexiconEntry("loki", categoryDivinity)
		emblemHeimdall = AlfheimLexiconEntry("heimdall", categoryDivinity)
		emblemOdin = AlfheimLexiconEntry("odin", categoryDivinity)
		
		rodThor = AlfheimLexiconEntry("rod_thor", categoryDivinity)
		rodSif = AlfheimLexiconEntry("rod_sif", categoryDivinity)
		rodNjord = AlfheimLexiconEntry("rod_njord", categoryDivinity)
		rodLoki = AlfheimLexiconEntry("rod_loki", categoryDivinity)
		rodOdin = AlfheimLexiconEntry("rod_odin", categoryDivinity)
		
		HV = AlfheimLexiconEntry("HV", categoryEvents)
		WOTW = AlfheimLexiconEntry("WOTW", categoryEvents)
		
		if (AlfheimConfigHandler.enableElvenStory)
			preInitElvenStory()
	}
	
	private fun preInitElvenStory() {
		if (esm == null) esm = AlfheimLexiconEntry("es", categoryAlfheim)
		if (races == null) races = AlfheimLexiconEntry("races", categoryAlfheim)
		
		if (AlfheimConfigHandler.enableMMO)
			preInitMMO()
	}
	
	private fun preInitMMO() {
		if (parties == null) parties = AlfheimLexiconEntry("parties", categoryAlfheim)
		if (spells == null) spells = AlfheimLexiconEntry("spells", categoryAlfheim)
		if (targets == null) targets = AlfheimLexiconEntry("targets", categoryAlfheim)
	}
	
	fun init() {
		advancedMana.setLexiconPages(PageText("0"), PageText("1"),
		                             PageManaInfuserRecipe("2", AlfheimRecipes.recipeManaStone),
		                             PageManaInfuserRecipe("3", AlfheimRecipes.recipeManaStoneGreater),
		                             PageText("4"),
		                             PageCraftingRecipe("5", AlfheimRecipes.recipeManaRingPink),
		                             PageCraftingRecipe("6", AlfheimRecipes.recipeManaRingElven),
		                             PageCraftingRecipe("7", AlfheimRecipes.recipeManaRingGod))
					.setIcon(AlfheimItems.manaStone)
		
		alfheim.setLexiconPages(PageText("0"), PageText("1")).setPriority()
		
		amplifier.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeAmplifier))
				 .setIcon(AlfheimBlocks.amplifier)
		
		amuletCirus.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeCloudPendant))
		
		amulterCrescent.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeCrescentAmulet))
		
		amuletIceberg.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipePendantSuperIce))
		
		amuletNimbus.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeCloudPendantSuper))
		
		animatedTorch.setLexiconPages(PageText("0"), PageText("1"), PageText("2"), PageCraftingRecipe("3", AlfheimRecipes.recipeAnimatedTorch))
		
		anomaly.setLexiconPages(PageText("0"))
		for (name in AlfheimAPI.anomalies.keys) {
			anomaly.setLexiconPages(
				PageImage("$name.t", ModInfo.MODID + ":textures/gui/entries/Anomaly" + name + ".png"),
				if (name != "Lightning")
					PageText("$name.d")
				else
					PageTextLearnableKnowledge("$name.d", Knowledge.PYLONS))
			
			LexiconRecipeMappings.map(ItemBlockAnomaly.ofType(name), anomaly, anomaly.pages.size - 1)
		}
		anomaly.icon = ItemBlockAnomaly.ofType("Lightning")
		
		anyavil.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeAnyavil))
		
		astrolabe.setLexiconPages(PageText("0"), PageText("1"), PageCraftingRecipe("2", AlfheimRecipes.recipeAstrolabe))
		
		armilla.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeArmilla))
		
		aurora.setLexiconPages(PageText("0"),
							   PageCraftingRecipe("1", AlfheimRecipes.recipeAuroraDirt),
							   PageCraftingRecipe("2", AlfheimRecipes.recipeAuroraPlanks),
							   PageCraftingRecipe("3", AlfheimRecipes.recipeAuroraStairs),
							   PageCraftingRecipe("4", AlfheimRecipes.recipeAuroraSlabs),
							   PageCraftingRecipe("5", AlfheimRecipes.recipeAuroraPlanksFromSlabs))
				.setIcon(AlfheimBlocks.auroraDirt)
		
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.auroraLeaves), aurora, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.auroraWood), aurora, 0)
		
		beltRation.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeRationBelt))
		
		cloakInvis.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeInvisibilityCloak))
		
		coatOfArms.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipesCoatOfArms)).icon = ItemStack(AlfheimItems.coatOfArms, 1, 16)
		
		for (i in 0..18)
			LexiconRecipeMappings.map(ItemStack(AlfheimItems.coatOfArms, 1, i), coatOfArms, 1)
		
		coloredDirt.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipesColoredDirt))
				.setIcon(AlfheimBlocks.rainbowDirt)
		
		for (i in 0..15)
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.irisDirt, 1, i), coloredDirt, 1)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.rainbowDirt), coloredDirt, 1)
		
		colorOverride.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeColorOverride)).setIcon(AlfheimItems.colorOverride)
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.colorOverride), colorOverride, 1)
		
		corpInj.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeInjector))
		
		corpQuandex.setLexiconPages(PageText("0"), PageText("1"), PageCraftingRecipe("2", AlfheimRecipes.recipeQuandexBase), PageCraftingRecipe("3", AlfheimRecipes.recipeQuandex))
		
		corpSeq.setLexiconPages(PageText("0"), PageText("1"), PageText("2"), PageCraftingRecipe("3", AlfheimRecipes.recipeAutocrafter))
		
		dagger.setLexiconPages(PageText("0")).icon = ItemStack(AlfheimItems.trisDagger)
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.trisDagger), dagger, 0)
		
		dasGold.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeRelicCleaner))
		
		deathSeed.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeDeathSeed))
		
		elementalSet.setLexiconPages(PageText("0"),
									 PageCraftingRecipe("1", AlfheimRecipes.recipeElementalHelmet),
									 PageCraftingRecipe("2", AlfheimRecipes.recipeElementalChestplate),
									 PageCraftingRecipe("3", AlfheimRecipes.recipeElementalLeggings),
									 PageCraftingRecipe("4", AlfheimRecipes.recipeElementalBoots)).icon = ItemStack(AlfheimItems.elementalHelmet)
		AlfheimItems.elementalHelmetRevealing?.let { elementalSet.addExtraDisplayedRecipe(ItemStack(it)) }
		
		elvenSet.setLexiconPages(PageText("0"),
								 PageCraftingRecipe("1", AlfheimRecipes.recipeElvoriumHelmet),
								 PageCraftingRecipe("2", AlfheimRecipes.recipeElvoriumChestplate),
								 PageCraftingRecipe("3", AlfheimRecipes.recipeElvoriumLeggings),
								 PageCraftingRecipe("4", AlfheimRecipes.recipeElvoriumBoots)).icon = ItemStack(AlfheimItems.elvoriumHelmet)
		AlfheimItems.elvoriumHelmetRevealing?.let { elvenSet.addExtraDisplayedRecipe(ItemStack(it)) }
		
		elves.setLexiconPages(*Array(5) { PageText("$it") }).setPriority()
		
		elvorium.setLexiconPages(PageText("0"), PageManaInfuserRecipe("1", AlfheimRecipes.recipeElvorium)).icon = ElvoriumIngot.stack
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.alfStorage, 1, 0), elvorium, 0)
		LexiconRecipeMappings.map(ElvoriumNugget.stack, elvorium, 1)
		
		enderAct.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeEnderActuator))
		
		essences.setLexiconPages(PageText("0"),
		                         PageTextLearnableAchievement("2", AlfheimAchievements.flugelHardKill),
		                         PageText("4"), PageText("5"), PageCraftingRecipe("6", listOf(AlfheimRecipes.recipeMuspelheimPowerIngot, AlfheimRecipes.recipeNiflheimPowerIngot)),
		                         PageText("7"), PageManaInfuserRecipe("8", AlfheimRecipes.recipeMauftrium)).icon = ItemStack(ModItems.manaResource, 1, 5)
		essences.addExtraDisplayedRecipe(NiflheimPowerIngot.stack)
		essences.addExtraDisplayedRecipe(NiflheimEssence.stack)
		essences.addExtraDisplayedRecipe(MuspelheimEssence.stack)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.alfStorage, 1, 1), essences, 6)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.alfStorage, 1, 2), essences, 4)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.alfStorage, 1, 3), essences, 4)
		LexiconRecipeMappings.map(MuspelheimEssence.stack, essences, 1)
		LexiconRecipeMappings.map(NiflheimEssence.stack, essences, 1)
		LexiconRecipeMappings.map(MauftriumNugget.stack, essences, 6)
		
		fenrir.setLexiconPages(PageText("0"), PageText("1"),
//		                       PageCraftingRecipe("2", AlfheimRecipes.recipeFenrirClaws),
		                       PageCraftingRecipe("3", AlfheimRecipes.recipeFenrirHelmet),
		                       PageCraftingRecipe("4", AlfheimRecipes.recipeFenrirChestplate),
		                       PageCraftingRecipe("5", AlfheimRecipes.recipeFenrirLeggings),
		                       PageCraftingRecipe("6", AlfheimRecipes.recipeFenrirBoots)).setIcon(AlfheimItems.fenrirHelmet)
		LexiconRecipeMappings.map(FenrirFur.stack, fenrir, 0)
		
		fenrirDrop.setLexiconPages(*Array(6) { PageText("$it") })
		ItemFenrirLoot.FenrirLootMetas.values().forEach {
			LexiconRecipeMappings.map(it.stack, fenrirDrop, it.ordinal + 1)
		}
		
		flowerAconite.setLexiconPages(PageText("0"), PagePetalRecipe("1", AlfheimRecipes.recipeWitherAconite))
		flowerAquapanthus.setLexiconPages(PageText("0"), PagePetalRecipe("1", AlfheimRecipes.recipeAquapanthus))
		flowerBud.setLexiconPages(PageText("0"), PageText("1"), PagePetalRecipe("2", AlfheimRecipes.recipeBud))
		flowerCrysanthermum.setLexiconPages(PageText("0"), PageText("1"), PageText("2"), PagePetalRecipe("3", AlfheimRecipes.recipeCrysanthermum))
		flowerEnderchid.setLexiconPages(PageText("0"), PagePetalRecipe("1", AlfheimRecipes.recipeOrechidEndium)).icon = BotaniaAPI.internalHandler.getSubTileAsStack("orechidEndium")
		flowerPetronia.setLexiconPages(PageText("0"), PagePetalRecipe("1", AlfheimRecipes.recipePetronia)).icon = BotaniaAPI.internalHandler.getSubTileAsStack("petronia")
		flowerRain.setLexiconPages(PageText("0"), PagePetalRecipe("1", AlfheimRecipes.recipeRainFlower)).icon = BotaniaAPI.internalHandler.getSubTileAsStack("rainFlower")
		flowerSnow.setLexiconPages(PageText("0"), PagePetalRecipe("1", AlfheimRecipes.recipeSnowFlower)).icon = BotaniaAPI.internalHandler.getSubTileAsStack("snowFlower")
		flowerStorm.setLexiconPages(PageText("0"), PagePetalRecipe("1", AlfheimRecipes.recipeStormFlower)).icon = BotaniaAPI.internalHandler.getSubTileAsStack("stormFlower")
		flowerWind.setLexiconPages(PageText("0"), PagePetalRecipe("1", AlfheimRecipes.recipeWindFlower)).icon = BotaniaAPI.internalHandler.getSubTileAsStack("windFlower")
		
		flugel.setLexiconPages(*Array(3) { PageText("$it") }).icon = ItemStack(ModItems.flightTiara, 1, 1)
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.discFlugel), flugel, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.discFlugelMeme), flugel, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.discFlugelUltra), flugel, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.flugelHead), flugel, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.flugelHead2), flugel, 0)
		
		fracturedSpace.setLexiconPages(*Array(3) { PageText("$it") }, PageCraftingRecipe("3", AlfheimRecipes.recipeRodBlackhole))
		
		frozenStar.setLexiconPages(PageText("0"),
								   PageCraftingRecipe("1", AlfheimRecipes.recipesStar),
								   PageText("2"),
								   PageCraftingRecipe("3", AlfheimRecipes.recipesStar2)).icon = ItemStarPlacer.forColor(16)
		
		goddessCharm.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeGoddessCharm))
		
		hyperBucket.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeHyperBucket))
		
		irisSapling.setLexiconPages(PageText("0"),
		                            PagePureDaisyRecipe("1", AlfheimRecipes.recipeIrisSapling),
									PageCraftingRecipe("2", AlfheimRecipes.recipesColoredPlanks + AlfheimRecipes.recipesAltPlanks),
									PageCraftingRecipe("3", AlfheimRecipes.recipesColoredStairs + AlfheimRecipes.recipesAltStairs),
									PageCraftingRecipe("4", AlfheimRecipes.recipesColoredSlabs + AlfheimRecipes.recipesAltSlabs),
									PageCraftingRecipe("5", AlfheimRecipes.recipesColoredPlanksFromSlabs + AlfheimRecipes.recipesAltPlanksFromSlabs),
									PageCraftingRecipe("6", AlfheimRecipes.recipesLeafDyes))
			.setPriority().setIcon(AlfheimBlocks.irisSapling)
		
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.irisSapling), irisSapling, 0)
		
		for (i in 0..3) {
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.irisWood0, 1, i), irisSapling, 0)
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.irisWood1, 1, i), irisSapling, 0)
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.irisWood2, 1, i), irisSapling, 0)
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.irisWood3, 1, i), irisSapling, 0)
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.altWood0, 1, i), irisSapling, 0)
		}
		
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.altWood1, 1, 0), irisSapling, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.altWood1, 1, 1), irisSapling, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.rainbowWood), irisSapling, 0)
		
		for (i in 0..15) {
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.irisPlanks, 1, i), irisSapling, 1)
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.irisSlabs[i], 1), irisSapling, 2)
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.irisStairs[i], 1), irisSapling, 3)
		}
		
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.rainbowPlanks), irisSapling, 1)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.rainbowSlab), irisSapling, 2)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.rainbowStairs), irisSapling, 3)
		
		for (i in 0..7) {
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.irisLeaves0, 1, i), irisSapling, 0)
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.irisLeaves1, 1, i), irisSapling, 0)
		}
		for (i in 0..5) {
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.altLeaves, 1, i), irisSapling, 0)
		}
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.rainbowLeaves), irisSapling, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.rainbowLeaves), irisSapling, 0)
		
		infuser.setLexiconPages(PageText("0"), PageText("1"),
								PageCraftingRecipe("2", AlfheimRecipes.recipeManaInfusionCore),
								PageCraftingRecipe("3", AlfheimRecipes.recipeManaInfuser),
								PageText("4"),
								PageMultiblockLearnable("5", AlfheimMultiblocks.infuserU, AlfheimMultiblocks.infuser, AlfheimAchievements.infuser)).setIcon(AlfheimBlocks.manaInfuser)
		
		itemDisplay.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipesItemDisplay)).icon = ItemStack(AlfheimBlocks.itemDisplay, 1, 1)
		for (i in 0..BlockItemDisplay.TYPES)
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.itemDisplay, 1, i), itemDisplay, 1)
		
		kindling.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeKindling)).setIcon(AlfheimBlocks.kindling)
		
		lamp.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeLamp)).setIcon(AlfheimBlocks.irisLamp)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.irisLamp), lamp, 1)
		
		legends.setLexiconPages(*Array(6) { PageText("$it") }).setPriority()
		LexiconRecipeMappings.map(YggFruit.stack, legends, 1)
		legends.icon = null
		
		lembas.setLexiconPages(PageText("0"), PageText("1"), PageCraftingRecipe("2", AlfheimRecipes.recipeLembas))
		
		livingwoodFunnel.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeLivingwoodFunnel)).setIcon(AlfheimBlocks.livingwoodFunnel)
		
		lootInt.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeLootInterceptor))
		
		manaAccelerator.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeItemHolder))
		
		manaImba.setLexiconPages(*Array(3) { PageText("$it") }, PageCraftingRecipe("3", AlfheimRecipes.recipeManaMirrorImba))
		
		manaLamp.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeEnlighter))
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.enlighter, 1, 1), manaLamp, 1)
		
		mitten.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeManaweaveGlove))
		
		mobs.setLexiconPages(*Array(6) { PageText("$it") },
		                    PageCraftingRecipe("6", AlfheimRecipes.recipeJellybread),
		                    PageCraftingRecipe("7", AlfheimRecipes.recipeJellyfish))
			.icon = ItemStack(ModItems.manaResource, 1, 8)
		
		LexiconRecipeMappings.map(Nectar.stack, mobs, 2)
		LexiconRecipeMappings.map(JellyBottle.stack, mobs, 3)
		
		multbauble.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeMultibauble))
		
		ores.setLexiconPages(*Array(3) { PageText("$it") }).icon = ItemStack(AlfheimBlocks.elvenOre, 1, 4)
		for (i in 0 until (AlfheimBlocks.elvenOre as BlockModMeta).subtypes)
			ores.addExtraDisplayedRecipe(ItemStack(AlfheimBlocks.elvenOre, 1, i))
		
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.elvenOre, 1, 1), ores, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.elvenOre, 1, 0), ores, 1)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.elvenOre, 1, 2), ores, 1)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.elvenOre, 1, 3), ores, 1)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.elvenOre, 1, 4), ores, 2)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.elvenOre, 1, 5), ores, 2)
		LexiconRecipeMappings.map(IffesalDust.stack, ores, 2)
		
		pastoralSeeds.setLexiconPages(PageText("0"),
									  PageCraftingRecipe("1", AlfheimRecipes.recipesRedstoneRoot),
									  PageManaInfusionRecipe("2", AlfheimRecipes.recipesPastoralSeeds)).setIcon(AlfheimBlocks.rainbowGrass)
		
		for (i in 0..1) {
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.rainbowTallGrass, 1, i), pastoralSeeds, 0)
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.rainbowGrass, 1, i), pastoralSeeds, 0)
		}
		for (i in 0..7) {
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.irisTallGrass0, 1, i), pastoralSeeds, 0)
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.irisTallGrass1, 1, i), pastoralSeeds, 0)
		}
		for (i in 0..15)
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.irisGrass, 1, i), pastoralSeeds, 0)
		for (i in 0..16)
			LexiconRecipeMappings.map(ItemStack(AlfheimItems.irisSeeds, 1, i), pastoralSeeds, 2)
		
		pixie.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipePixieAttractor)).icon = ItemStack(AlfheimItems.pixieAttractor)
		
		portal.setLexiconPages(*Array(3) { PageText("$it") },
							   PageCraftingRecipe("3", AlfheimRecipes.recipeAlfheimPortal),
							   PageText("4"), PageElvenRecipe("5", AlfheimRecipes.recipeInterdimensional),
							   PageMultiblock("6", AlfheimMultiblocks.portal),
							   PageText("7"), PageText("8")).setPriority()
		
		pylons.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeElvenPylon), PageCraftingRecipe("2", AlfheimRecipes.recipeGaiaPylon)).setPriority().icon = ItemStack(AlfheimBlocks.alfheimPylon, 1, 0)
		
		rainbowFlora.setLexiconPages(PageText("0"),
									 PageCraftingRecipe("1", AlfheimRecipes.recipesRainbowPetal),
									 PageCraftingRecipe("2", AlfheimRecipes.recipeRainbowPetalGrinding),
									 PageCraftingRecipe("3", AlfheimRecipes.recipeRainbowPetalBlock)).icon = ItemStack(AlfheimBlocks.rainbowGrass, 1, 2)
		
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.rainbowTallFlower), rainbowFlora, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.rainbowGrass, 1, 2), rainbowFlora, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.rainbowGrass, 1, 3), LexiconData.shinyFlowers, 2)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.rainbowFlowerFloating, 1, 3), LexiconData.shinyFlowers, 3 )
		
		reality.setLexiconPages(PageText("0"), PageText("1"), PageCraftingRecipe("2", AlfheimRecipes.recipeSword))
		
		ringAnomaly.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeSpatiotemporal))
		
		ringsAura.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeAuraRingPink), PageCraftingRecipe("2", AlfheimRecipes.recipeAuraRingElven), PageCraftingRecipe("3", AlfheimRecipes.recipeAuraRingGod)).icon = ItemStack(AlfheimItems.auraRingElven)
		
		ringDodge.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeDodgeRing))
		
		ringManaDrive.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeRingFeedFlower))
		
		ringSpider.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeRingSpider))
		
		rodClick.setLexiconPages(*Array(3) { PageText("$it") }, PageCraftingRecipe("3", AlfheimRecipes.recipeRodClicker))
		
		rodGreen.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeRodGreen))
		
		rodPrismatic.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeRodPrismatic))
		
		rodRedstone.setLexiconPages(PageText("0"), PageText("1"), PageCraftingRecipe("2", AlfheimRecipes.recipeRodRedstone))
		
		ruling.setLexiconPages(PageText("0"), PageText("1"),
							   PageCraftingRecipe("2", AlfheimRecipes.recipeRodMuspelheim),
							   PageCraftingRecipe("3", AlfheimRecipes.recipeRodNiflheim),
							   PageText("4"), PageCraftingRecipe("5", listOf(AlfheimRecipes.recipeMuspelheimPendant, AlfheimRecipes.recipeNiflheimPendant))).icon = ItemStack(AlfheimItems.rodMuspelheim)
		
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.redFlame), ruling, 2)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.poisonIce), ruling, 3)
		
		runes.setLexiconPages(PageText("0"), PageRuneRecipe("1", listOf(AlfheimRecipes.recipeMuspelheimRune, AlfheimRecipes.recipeNiflheimRune)),
							  PageText("2"), PageText("3"), PageRuneRecipe("4", AlfheimRecipes.recipeRealityRune)).icon = PrimalRune.stack
		runes.addExtraDisplayedRecipe(NiflheimRune.stack)
		
		sealCreepers.setLexiconPages(PageText("0"), PageText("1${if (AlfheimConfigHandler.blackLotusDropRate > 0.0) "" else "No"}Drop")).setIcon(AlfheimItems.wiltedLotus)
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.wiltedLotus, 1, 0), sealCreepers, 1)
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.wiltedLotus, 1, 1), sealCreepers, 1)
		
		shimmer.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeShimmerQuartz)).icon = RainbowQuartz.stack
		for (i in arrayOf(0, 1, 2, 5, 6))
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.shimmerQuartz, 1, i), shimmer, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.shimmerQuartzSlab), shimmer, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.shimmerQuartzStairs), shimmer, 0)
		LexiconRecipeMappings.map(RainbowQuartz.stack, shimmer, 1)
		
		shrines.setLexiconPages(PageText("0"), PageText("1")).icon = ItemStack(AlfheimBlocks.powerStone)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.powerStone, 1, 0), shrines, 0)
		for (i in 1..4)
			LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.powerStone, 1, i), shrines, 1)
		
		soulSword.setLexiconPages(PageText("0"), PageText("1"), PageCraftingRecipe("2", AlfheimRecipes.recipeSoulSword))
		
		specialAxe.setLexiconPages(PageText("0"), PageText("1")).icon = ItemStack(AlfheimItems.wireAxe)
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.wireAxe), specialAxe, 0)
		
		temperature.setLexiconPages(PageText("0"), PageText("1")).setPriority()
		
		terraHarvester.setLexiconPages(PageText("0"), PageText("1"), PageCraftingRecipe("2", AlfheimRecipes.recipeTerraHarvester))
		
		throwablePotions.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeSplashPotions),
		                                 PageText("2"), PageCraftingRecipe("3", AlfheimRecipes.recipeGrenade)).icon = (AlfheimItems.splashPotion as ItemSplashPotion).getItemForBrew(ModBrews.absorption, null)
		
		thunderChakram.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeThunderChakram))
		
		trade.setLexiconPages(PageText("0"), PageText("1"),
							  PageCraftingRecipe("2", AlfheimRecipes.recipeElvoriumPylon),
							  PageCraftingRecipe("3", AlfheimRecipes.recipeTradePortal),
							  PageMultiblock("4", AlfheimMultiblocks.yordin)).icon = ItemStack(AlfheimBlocks.tradePortal)
		
		triquetrum.setLexiconPages(PageText("0"), PageText("1"), PageCraftingRecipe("2", AlfheimRecipes.recipeTriquetrum))
		
		uberSpreader.setLexiconPages(PageText("0"), PageText("1"),
									 if (AlfheimCore.TiCLoaded && !AlfheimCore.stupidMode && AlfheimConfigHandler.materialIDs[TinkersConstructAlfheimConfig.MAUFTRIUM] != -1) PageText("2t")
									 else PageCraftingRecipe(if (AlfheimCore.stupidMode) "2s" else "2", AlfheimRecipes.recipeUberSpreader)).icon = ItemStack(ModBlocks.spreader, 1, 4)
		LexiconRecipeMappings.map(ItemStack(ModBlocks.spreader, 1, 4), uberSpreader, 2)
		
		winery.setLexiconPages(*Array(12) { PageText("$it") },
							   PageCraftingRecipe("12", AlfheimRecipes.recipeBarrel),
							   PageCraftingRecipe("13", AlfheimRecipes.recipeJug))
		winery.addExtraDisplayedRecipe(GrapeLeaf.stack)
		winery.addExtraDisplayedRecipe(Nectar.stack)
		winery.addExtraDisplayedRecipe(RedGrapes.stack)
		winery.addExtraDisplayedRecipe(WhiteGrapes.stack)
		winery.addExtraDisplayedRecipe(RedWine.stack)
		winery.addExtraDisplayedRecipe(WhiteWine.stack)
		LexiconRecipeMappings.map(GrapeLeaf.stack, winery, 0)
		LexiconRecipeMappings.map(RedGrapes.stack, winery, 0)
		LexiconRecipeMappings.map(WhiteGrapes.stack, winery, 0)
		LexiconRecipeMappings.map(RedWine.stack, winery, 0)
		LexiconRecipeMappings.map(WhiteWine.stack, winery, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.grapesRed[0]), winery, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.grapesRed[1]), winery, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.grapesRed[2]), winery, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.grapesRedPlanted), winery, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.grapesWhite), winery, 0)
		
		worldgen.setLexiconPages(PageText("0"),
								 PagePureDaisyRecipe("1", AlfheimRecipes.recipeDreamwood),
			// PageCraftingRecipe("2", AlfheimRecipes.recipeGlowstone),
								 PageText("3"),
								 PageCraftingRecipe("4", AlfheimRecipes.recipeLivingcobble),
								 PageCraftingRecipe("5", AlfheimRecipes.recipeLivingrockPickaxe),
								 PageCraftingRecipe("6", AlfheimRecipes.recipeFurnace),
								 PageCraftingRecipe("7", AlfheimRecipes.recipesApothecary)).icon = ItemStack(AlfheimBlocks.altLeaves, 1, 7)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.elvenSand), worldgen, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.altWood1, 1, 3), worldgen, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.altLeaves, 1, 7), worldgen, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.dreamSapling), worldgen, 0)
		LexiconRecipeMappings.map(DreamCherry.stack, worldgen, 0)
		
		(LexiconData.vineBall.pages[4] as PageCraftingRecipe).apply {
			recipes = recipes.toMutableList()
			recipes.add(AlfheimRecipes.recipeLivingCobbleMossy)
		}
		
		LexiconData.decorativeBlocks.setLexiconPages(PageCraftingRecipe("24", AlfheimRecipes.recipesLivingDecor))
		
		(LexiconData.decorativeBlocks.pages[20] as PageCraftingRecipe).apply {
			recipes = recipes.toMutableList()
			recipes.addAll(AlfheimRecipes.recipesRoofTile)
		}
		
		LexiconData.luminizerTransport.setLexiconPages(PageText("7"), PageCraftingRecipe("8", AlfheimRecipes.recipeLuminizer2),
		                                               PageText("9"), PageText("10"), PageCraftingRecipe("11", AlfheimRecipes.recipeLuminizer3))
		
		LexiconData.arcaneRose.pages[0].unlocalizedName += "a"
		
		LexiconData.overgrowthSeed.setLexiconPages(PageText("2"))
		
		if (AlfheimConfigHandler.enableElvenStory) initElvenStory()
		
		// ################################################################
		
		treeCrafting.setLexiconPages(PageText("0"),
									 PageText("1"),
									 PageMultiblock("2", AlfheimMultiblocks.treeCrafter))
			.setPriority().setIcon(AlfheimBlocks.treeCrafterBlockRB)
		
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.treeCrafterBlock), treeCrafting, 2)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.treeCrafterBlockRB), treeCrafting, 2)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.treeCrafterBlockAU), treeCrafting, 2)
		
		lightningSapling.setLexiconPages(PageText("0"),
		                                 PageTreeCrafting("1", AlfheimRecipes.recipeLightningTree),
		                                 PageCraftingRecipe("2", AlfheimRecipes.recipeThunderousPlanks),
		                                 PageCraftingRecipe("3", AlfheimRecipes.recipeThunderousStairs),
		                                 PageCraftingRecipe("4", AlfheimRecipes.recipeThunderousSlabs),
		                                 PageCraftingRecipe("5", AlfheimRecipes.recipeThunderousTwig),
		                                 PageFurnaceRecipe("6", ItemStack(AlfheimBlocks.lightningPlanks)))
				.setIcon(AlfheimBlocks.lightningSapling)
		
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.lightningSapling), lightningSapling, 1)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.lightningWood), lightningSapling, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.lightningLeaves), lightningSapling, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.lightningPlanks), lightningSapling, 2)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.lightningSlabs), lightningSapling, 3)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.lightningStairs), lightningSapling, 4)
		LexiconRecipeMappings.map(ThunderwoodTwig.stack, lightningSapling, 5)
		LexiconRecipeMappings.map(ThunderwoodSplinters.stack, lightningSapling, 6)
		
		netherSapling.setLexiconPages(PageText("0"),
		                              PageTreeCrafting("1", AlfheimRecipes.recipeInfernalTree),
		                              PageCraftingRecipe("2", AlfheimRecipes.recipeInfernalPlanks),
		                              PageCraftingRecipe("3", AlfheimRecipes.recipeInfernalStairs),
		                              PageCraftingRecipe("4", AlfheimRecipes.recipeInfernalSlabs),
		                              PageCraftingRecipe("5", AlfheimRecipes.recipeInfernalTwig),
		                              PageFurnaceRecipe("6", ItemStack(AlfheimBlocks.netherWood)),
		                              PageFurnaceRecipe("7", ItemStack(AlfheimBlocks.netherPlanks)))
				.setIcon(AlfheimBlocks.netherSapling)
		
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.netherSapling), netherSapling, 1)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.netherWood), netherSapling, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.netherLeaves), netherSapling, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.netherPlanks), netherSapling, 2)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.netherSlabs), netherSapling, 3)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.netherStairs), netherSapling, 4)
		LexiconRecipeMappings.map(NetherwoodTwig.stack, netherSapling, 5)
		LexiconRecipeMappings.map(NetherwoodSplinters.stack, netherSapling, 6)
		LexiconRecipeMappings.map(NetherwoodCoal.stack, netherSapling, 7)
		
		circuitSapling.setLexiconPages(PageText("0"),
		                               PageTreeCrafting("1", AlfheimRecipes.recipeCircuitTree),
		                               PageCraftingRecipe("2", AlfheimRecipes.recipeCircuitPlanks),
		                               PageCraftingRecipe("3", AlfheimRecipes.recipeCircuitStairs),
		                               PageCraftingRecipe("4", AlfheimRecipes.recipeCircuitSlabs))
				.setIcon(AlfheimBlocks.circuitSapling)
		
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.circuitWood), circuitSapling, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.circuitLeaves), circuitSapling, 0)
		
		calicoSapling.setLexiconPages(PageText("0"),
		                              PageTreeCrafting("1", AlfheimRecipes.recipeCalicoTree),
		                              PageCraftingRecipe("2", AlfheimRecipes.recipeCalicoPlanks),
		                              PageCraftingRecipe("3", AlfheimRecipes.recipeCalicoStairs),
		                              PageCraftingRecipe("4", AlfheimRecipes.recipeCalicoSlabs))
				.setIcon(AlfheimBlocks.calicoSapling)
		
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.calicoWood), calicoSapling, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.calicoLeaves), calicoSapling, 0)
		
		silencer.setLexiconPages(PageText("0"),
		                         PageTreeCrafting("1", AlfheimRecipes.recipeSealingTree),
		                         PageCraftingRecipe("2", AlfheimRecipes.recipeSealingPlanks),
		                         PageCraftingRecipe("3", AlfheimRecipes.recipeSealingStairs),
		                         PageCraftingRecipe("4", AlfheimRecipes.recipeSealingSlabs))
				.setIcon(AlfheimBlocks.sealingSapling)
		
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.sealingSapling), silencer, 1)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.sealingWood), silencer, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.sealingLeaves), silencer, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.sealingPlanks), silencer, 2)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.sealingStairs), silencer, 3)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.sealingSlabs), silencer, 4)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.amplifier), amplifier, 1)
		
		HV.setLexiconPages(*Array(5) { PageText("$it") })
			.setKnowledgeType(BotaniaAPI.elvenKnowledge)
			.icon = ItemStack(AlfheimItems.eventResource, 1, EventResourcesMetas.VolcanoRelic)
		
		WOTW.setLexiconPages(*Array(3) { PageText("$it") })
			.setKnowledgeType(BotaniaAPI.elvenKnowledge)
			.icon = ItemStack(AlfheimItems.eventResource, 1, EventResourcesMetas.SnowRelic)
		
		if (ThaumcraftSuffusionRecipes.recipesLoaded) {
			tctrees = object: AlfheimLexiconEntry("tctrees", categotyDendrology) {
				override fun getSubtitle() = "[Alfheim x Thaumcraft]"
			}
			
			tctrees.setLexiconPages(PageText("0"),
			                        PageTreeCrafting("1", ThaumcraftSuffusionRecipes.greatwoodRecipe),
			                        PageTreeCrafting("2", ThaumcraftSuffusionRecipes.silverwoodRecipe),
			                        PageText("3"),
			                        PageTreeCrafting("4", ThaumcraftSuffusionRecipes.shimmerleafRecipe),
			                        PageTreeCrafting("5", ThaumcraftSuffusionRecipes.cinderpearlRecipe),
			                        PageTreeCrafting("6", ThaumcraftSuffusionRecipes.vishroomRecipe)).icon = ItemStack(ThaumcraftSuffusionRecipes.plantBlock)
			
			LexiconRecipeMappings.map(ItemStack(ThaumcraftSuffusionRecipes.plantBlock, 1, 0), tctrees, 1)
			LexiconRecipeMappings.map(ItemStack(ThaumcraftSuffusionRecipes.plantBlock, 1, 1), tctrees, 2)
			LexiconRecipeMappings.map(ItemStack(ThaumcraftSuffusionRecipes.plantBlock, 1, 2), tctrees, 4)
			LexiconRecipeMappings.map(ItemStack(ThaumcraftSuffusionRecipes.plantBlock, 1, 3), tctrees, 5)
			LexiconRecipeMappings.map(ItemStack(ThaumcraftSuffusionRecipes.plantBlock, 1, 5), tctrees, 6)
			
			LexiconRecipeMappings.map(ItemStack(ModItems.elementiumHelmRevealing), LexiconData.tcIntegration, 2)
			LexiconRecipeMappings.map(ItemStack(ModItems.terrasteelHelmRevealing), LexiconData.tcIntegration, 2)
			LexiconRecipeMappings.map(ItemStack(AlfheimItems.elementalHelmetRevealing), LexiconData.tcIntegration, 2)
			LexiconRecipeMappings.map(ItemStack(AlfheimItems.elvoriumHelmetRevealing), LexiconData.tcIntegration, 2)
			LexiconRecipeMappings.map(ItemStack(AlfheimItems.fenrirHelmetRevealing), LexiconData.tcIntegration, 2)
			LexiconRecipeMappings.map(ItemStack(AlfheimItems.snowHelmetRevealing), LexiconData.tcIntegration, 2)
			LexiconRecipeMappings.map(ItemStack(AlfheimItems.volcanoHelmetRevealing), LexiconData.tcIntegration, 2)
		}
		
		// ################################################################
		
		divIntro.setLexiconPages(PageText("0"), PageText("1"), PageCraftingRecipe("2", AlfheimRecipes.recipeAttribution), PageText("3")).setPriority()
		abyss.setLexiconPages(PageText("0"), PageText("1"),
		                      PageTextConditional("2") { ASJUtilities.isServer || "${Knowledge.ABYSS_TRUTH}" in PlayerSegmentClient.knowledge },
		                      PageTextConditional("3") { ASJUtilities.isServer || "${Knowledge.NIFLHEIM}" in PlayerSegmentClient.knowledge },
		                      PageTextConditional("4") { ASJUtilities.isServer || "${Knowledge.NIFLHEIM_POST}" in PlayerSegmentClient.knowledge },
		                      PageTextConditional("5") { ASJUtilities.isServer || "${Knowledge.MUSPELHEIM}" in PlayerSegmentClient.knowledge },
		                      PageTextConditional("6") { ASJUtilities.isServer || "${Knowledge.MUSPELHEIM_POST}" in PlayerSegmentClient.knowledge }).setPriority()
		
		vafthrudnir.setLexiconPages(PageText("0"), PageText("1"), PageCraftingRecipe("2", AlfheimRecipes.recipeRealityAnchor), PageMultiblock("3", AlfheimMultiblocks.anchor),
		                            *Array(4) { PageText("${it+4}") },
			                        PageText("8"), PageText("9"), PageManaInfusionRecipe("10", AlfheimRecipes.recipeRiftShard),
		                            PageCraftingRecipe("11", AlfheimRecipes.recipesRealmCore),
			                        PageCraftingRecipe("12", AlfheimRecipes.recipesRealmFrame),
			                        PageCraftingRecipe("13", AlfheimRecipes.recipeSpire),
			                        PageCraftingRecipe("14", AlfheimRecipes.recipeCreationPylon),
			                        PageMultiblock("15", AlfheimMultiblocks.spire),
			                        PageText("16"), PageText("17")).setPriority().setIcon(AlfheimBlocks.spire)
		
		LexiconRecipeMappings.remove(AlfheimRecipes.recipeRealityAnchor.recipeOutput)
		LexiconRecipeMappings.remove(AlfheimRecipes.recipeRiftShard.output)
		LexiconRecipeMappings.remove(AlfheimRecipes.recipesRealmCore[0].recipeOutput)
		LexiconRecipeMappings.remove(AlfheimRecipes.recipesRealmCore[1].recipeOutput)
		LexiconRecipeMappings.remove(AlfheimRecipes.recipesRealmFrame[0].recipeOutput)
		LexiconRecipeMappings.remove(AlfheimRecipes.recipesRealmFrame[1].recipeOutput)
		LexiconRecipeMappings.remove(AlfheimRecipes.recipeSpire.recipeOutput)
		LexiconRecipeMappings.remove(AlfheimRecipes.recipeCreationPylon.recipeOutput)
		LexiconRecipeMappings.remove(AlfheimRecipes.recipeRealityAnchor.recipeOutput)
		
		cloakThor.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeCloakThor))
		cloakSif.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeCloakSif))
		cloakNjord.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeCloakNjord))
		cloakLoki.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeCloakLoki))
		cloakHeimdall.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeCloakHeimdall))
		cloakOdin.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeCloakOdin))
		
		emblemThor.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipePriestOfThor))
		emblemSif.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipePriestOfSif))
		emblemNjord.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipePriestOfNjord))
		emblemLoki.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipePriestOfLoki))
		emblemHeimdall.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipePriestOfHeimdall))
		emblemOdin.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipePriestOfOdin))
		
		rodThor.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeRodLightning), PageText("2"))
		rodSif.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipesRodColoredSkyDirt), PageText("2")).icon = ItemStack(AlfheimItems.rodColorfulSkyDirt, 1, 16)
		rodNjord.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeRodInterdiction), PageText("2"))
		rodLoki.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeRodFlame), PageText("2"))
		rodOdin.setLexiconPages(PageText("0"), PageText("1"), PageCraftingRecipe("2", AlfheimRecipes.recipeRodPortal), PageText("3"))
		
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.rodColorfulSkyDirt), rodSif, 1)

//		var memes = LexiconData.tinyPotato
//		for (entry in BotaniaAPI.getAllEntries())
//			if (entry.getUnlocalizedName() == "botania.entry.wrap")
//				memes = entry
//
//		LexiconRecipeMappings.map(ItemStack(AlfheimItems.attributionBauble, 1, 1), memes, 1)
		
		LexiconData.thorRing.apply {
			pages[0].unlocalizedName += "n"
			category.entries.remove(this)
			category = categoryDivinity
			categoryDivinity.entries.add(this)
		}
		
		LexiconData.lokiRing.apply {
			pages[0].unlocalizedName += "n"
			pages[3].unlocalizedName += "n"
			
			category.entries.remove(this)
			category = categoryDivinity
			categoryDivinity.entries.add(this)
		}
		
		LexiconData.odinRing.apply {
			pages[0].unlocalizedName += "n"
			category.entries.remove(this)
			category = categoryDivinity
			categoryDivinity.entries.add(this)
		}
		
		LexiconData.infiniteFruit.apply {
			category.entries.remove(this)
			category = categoryDivinity
			categoryDivinity.entries.add(this)
		}
		
		// ################################################################
		// ################################################################
		// ################################################################
		
		for ((i, page) in LexiconData.gaiaRitual.pages.withIndex()) {
			if (!page.unlocalizedName.endsWith("1")) continue
			LexiconData.gaiaRitual.pages[i] = PageCraftingRecipe(page.unlocalizedName, AlfheimRecipes.recipeGaiaPylon)
			break
		}
		
//		LexiconData.gaiaRitual.pages.clear()
//		LexiconData.gaiaRitual.setLexiconPages(PageText("0"), PageCraftingRecipe("1", AlfheimRecipes.recipeGaiaPylon),
//											   PageMultiblock("2", ModMultiblocks.gaiaRitual), PageText("3"), PageText("4"),
//											   PageText("5"))
		
		for ((i, page) in LexiconData.sparks.pages.withIndex()) {
			if (!page.unlocalizedName.endsWith("2")) continue
			LexiconData.sparks.pages[i] = PageCraftingRecipe(page.unlocalizedName, AlfheimRecipes.recipesSpark)
			break
		}
		
//		LexiconData.sparks.pages.clear()
//		LexiconData.sparks.setLexiconPages(PageText("0"), PageText("1"),
//										   PageCraftingRecipe("2", AlfheimRecipes.recipesSpark),
//										   PageText("3"))
		
		LexiconData.cosmeticBaubles.setLexiconPages(PageCraftingRecipe("34", AlfheimRecipes.recipeThinkingHand))
		
		LexiconData.pool.setLexiconPages(PageManaInfusionRecipe("15", AlfheimRecipes.recipeInfusedDreamTwig))
		
		LexiconData.lenses.setLexiconPages(
			PageText("38"), PageCraftingRecipe("39", AlfheimRecipes.recipeLensMessenger),
			PageText("40"), PageCraftingRecipe("41", AlfheimRecipes.recipeLensPush),
			PageText("42"), PageCraftingRecipe("43", AlfheimRecipes.recipeLensSmelt),
			PageText("44"), PageCraftingRecipe("45", AlfheimRecipes.recipeLensTrack))
		
		LexiconData.elvenLenses.setLexiconPages(
			PageText("11"), PageCraftingRecipe("12", AlfheimRecipes.recipeLensTripwire),
			PageText("13"), PageCraftingRecipe("14", AlfheimRecipes.recipeLensSuperconductor),
			PageText("15"), PageCraftingRecipe("16", AlfheimRecipes.recipeLensPurification))
		
		PageText("botania.page.judgementCloaks1n").apply { LexiconData.judgementCloaks.pages[1] = this }.onPageAdded(LexiconData.judgementCloaks, 1)
		LexiconData.judgementCloaks.setLexiconPages(PageCraftingRecipe("4", AlfheimRecipes.recipeBalanceCloak))
		
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.invisibleFlameLens), LexiconData.lenses, 35)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.rainbowMushroom), LexiconData.mushrooms, 1)
		
		setKnowledgeTypes()
	}
	
	fun initRelics() {
		daolos = AlfheimRelicLexiconEntry("daolos", categoryDivinity, AlfheimItems.daolos)
		daolos.setLexiconPages(*Array(6) { PageText("$it") })
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.daolos), daolos, 0)
		
		excaliber = AlfheimRelicLexiconEntry("excaliber", categoryAlfheim, AlfheimItems.excaliber)
		excaliber.setLexiconPages(PageText("0"))
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.excaliber), excaliber, 0)
		
		gjallarhorn = AlfheimRelicLexiconEntry("gjallarhorn", categoryDivinity, AlfheimItems.gjallarhorn)
		gjallarhorn.setLexiconPages(PageText("0"), PageText("1"))
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.gjallarhorn), gjallarhorn, 0)
		
		gleipnir = AlfheimRelicLexiconEntry("gleipnir", categoryDivinity, AlfheimItems.gleipnir)
		gleipnir.setLexiconPages(PageText("0"))
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.gleipnir), gleipnir, 0)
		
		gungnir = AlfheimRelicLexiconEntry("gungnir", categoryDivinity, AlfheimItems.gungnir)
		gungnir.setLexiconPages(PageText("0"))
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.gungnir), gungnir, 0)
		
		mask = AlfheimRelicLexiconEntry("mask", categoryAlfheim, AlfheimItems.mask)
		mask.setLexiconPages(PageText("0"))
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.mask), mask, 0)
		
		mjolnir = AlfheimRelicLexiconEntry("mjolnir", categoryDivinity, AlfheimItems.mjolnir)
		mjolnir.setLexiconPages(PageText("0"))
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.mjolnir), mjolnir, 0)
		
		moonbow = AlfheimRelicLexiconEntry("moonbow", categoryAlfheim, AlfheimItems.moonlightBow)
		moonbow.setLexiconPages(PageText("0"))
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.moonlightBow), moonbow, 0)
		
		ringHeimdall = AlfheimRelicLexiconEntry("ring_heimdall", categoryDivinity, AlfheimItems.priestRingHeimdall)
		ringHeimdall.setLexiconPages(PageText("0"))
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.priestRingHeimdall), ringHeimdall, 0)
		
		ringNjord = AlfheimRelicLexiconEntry("ring_njord", categoryDivinity, AlfheimItems.priestRingNjord)
		ringNjord.setLexiconPages(PageText("0"))
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.priestRingNjord), ringNjord, 0)
		
		ringSif = AlfheimRelicLexiconEntry("ring_sif", categoryDivinity, AlfheimItems.priestRingSif)
		ringSif.setLexiconPages(PageText("0"))
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.priestRingSif), ringSif, 0)
		
		soul = AlfheimRelicLexiconEntry("soul", categoryAlfheim, AlfheimItems.flugelSoul)
		soul.setLexiconPages(*Array(9) { PageText("$it") },
							 PageMultiblock("9", AlfheimMultiblocks.soul),
							 PageText("10"), PageCraftingRecipe("11", AlfheimRecipes.recipeCleanPylon))
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.flugelSoul), soul, 0)
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.alfheimPylon, 1, 2), soul, 7)
		
		soulHorn = AlfheimRelicLexiconEntry("soulHorn", categoryAlfheim, AlfheimAchievements.flugelHardKill)
		soulHorn.setLexiconPages(PageText("0"), PageText("1"), PageCraftingRecipe("2", AlfheimRecipes.recipeSoulHorn), PageText("3")).icon = ItemStack(AlfheimItems.soulHorn)
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.soulHorn, 1, 1), soulHorn, 2)
		
		subspear = AlfheimRelicLexiconEntry("subspear", categoryAlfheim, AlfheimItems.subspaceSpear)
		subspear.setLexiconPages(PageText("0"))
		LexiconRecipeMappings.map(ItemStack(AlfheimItems.subspaceSpear), subspear, 0)
		
		if (ConfigHandler.relicsEnabled) {
			LexiconRecipeMappings.map(ItemStack(ModItems.dice), LexiconData.relicInfo, 0)
			LexiconRecipeMappings.map(ItemStack(ModItems.infiniteFruit), LexiconData.infiniteFruit, 0)
			LexiconRecipeMappings.map(ItemStack(ModItems.kingKey), LexiconData.kingKey, 0)
			LexiconRecipeMappings.map(ItemStack(ModItems.flugelEye), LexiconData.flugelEye, 0)
			LexiconRecipeMappings.map(ItemStack(ModItems.thorRing), LexiconData.thorRing, 0)
			LexiconRecipeMappings.map(ItemStack(ModItems.lokiRing), LexiconData.lokiRing, 0)
			LexiconRecipeMappings.map(ItemStack(ModItems.odinRing), LexiconData.odinRing, 0)
		}
	}
	
	private fun initElvenStory() {
		if (esm!!.pages.isEmpty())
			esm!!.setPriority()
				.setLexiconPages(PageText("0"))
		
		if (races!!.pages.isEmpty())
			races!!.setPriority().setLexiconPages(*Array(6) { PageText("$it") })
		LexiconRecipeMappings.map(ItemStack(AlfheimBlocks.raceSelector), races, 0)
		races?.icon = null
		
		if (AlfheimConfigHandler.enableMMO) initMMO()
	}
	
	private fun initMMO() {
		if (parties!!.pages.isEmpty())
			parties!!.setPriority()
				.setLexiconPages(PageText("0"), PageText("1"), PageCraftingRecipe("2", AlfheimRecipes.recipePeacePipe),
								 PageText("3"), PageCraftingRecipe("4", AlfheimRecipes.recipePaperBreak)).icon = null
		
		if (spells!!.pages.isEmpty()) {
			spells!!.setPriority()
				.setLexiconPages(*Array(4) { PageText("$it") })
			
			val l = ArrayList(AlfheimAPI.spells)
			l.sortBy { it.name }
			for (spell in l) spells!!.addPage(PageSpell(spell))
		}
		
		if (targets!!.pages.isEmpty())
			targets!!.setPriority()
				.setLexiconPages(PageText("0"), PageText("1"))
	}
	
	private fun setKnowledgeTypes() {
		advancedMana.knowledgeType = BotaniaAPI.elvenKnowledge
		amplifier.knowledgeType = BotaniaAPI.elvenKnowledge
		amuletIceberg.knowledgeType = BotaniaAPI.elvenKnowledge
		amuletNimbus.knowledgeType = BotaniaAPI.elvenKnowledge
		amulterCrescent.knowledgeType = BotaniaAPI.elvenKnowledge
		anomaly.knowledgeType = BotaniaAPI.elvenKnowledge
		anyavil.knowledgeType = BotaniaAPI.elvenKnowledge
		astrolabe.knowledgeType = BotaniaAPI.elvenKnowledge
		beltRation.knowledgeType = BotaniaAPI.elvenKnowledge
		corpInj.knowledgeType = BotaniaAPI.elvenKnowledge
		corpSeq.knowledgeType = BotaniaAPI.elvenKnowledge
		colorOverride.knowledgeType = BotaniaAPI.elvenKnowledge
		elementalSet.knowledgeType = BotaniaAPI.elvenKnowledge
		elvenSet.knowledgeType = BotaniaAPI.elvenKnowledge
		elves.knowledgeType = BotaniaAPI.elvenKnowledge
		elvorium.knowledgeType = BotaniaAPI.elvenKnowledge
		essences.knowledgeType = BotaniaAPI.elvenKnowledge
		flowerEnderchid.knowledgeType = BotaniaAPI.elvenKnowledge
		flowerPetronia.knowledgeType = BotaniaAPI.elvenKnowledge
		flugel.knowledgeType = BotaniaAPI.elvenKnowledge
		hyperBucket.knowledgeType = BotaniaAPI.elvenKnowledge
		infuser.knowledgeType = BotaniaAPI.elvenKnowledge
		lamp.knowledgeType = BotaniaAPI.elvenKnowledge
		lembas.knowledgeType = BotaniaAPI.elvenKnowledge
		lootInt.knowledgeType = BotaniaAPI.elvenKnowledge
		manaImba.knowledgeType = BotaniaAPI.elvenKnowledge
		manaLamp.knowledgeType = BotaniaAPI.elvenKnowledge
		mobs.knowledgeType = BotaniaAPI.elvenKnowledge
		multbauble.knowledgeType = BotaniaAPI.elvenKnowledge
		ores.knowledgeType = BotaniaAPI.elvenKnowledge
		pixie.knowledgeType = BotaniaAPI.elvenKnowledge
		portal.knowledgeType = BotaniaAPI.elvenKnowledge
		pylons.knowledgeType = BotaniaAPI.elvenKnowledge
		rainbowFlora.knowledgeType = BotaniaAPI.elvenKnowledge
		reality.knowledgeType = BotaniaAPI.elvenKnowledge
		ringAnomaly.knowledgeType = BotaniaAPI.elvenKnowledge
		ringsAura.knowledgeType = BotaniaAPI.elvenKnowledge
		rodClick.knowledgeType = BotaniaAPI.elvenKnowledge
		rodPrismatic.knowledgeType = BotaniaAPI.elvenKnowledge
		ruling.knowledgeType = BotaniaAPI.elvenKnowledge
		runes.knowledgeType = BotaniaAPI.elvenKnowledge
		shimmer.knowledgeType = BotaniaAPI.elvenKnowledge
		shrines.knowledgeType = BotaniaAPI.elvenKnowledge
		silencer.knowledgeType = BotaniaAPI.elvenKnowledge
		trade.knowledgeType = BotaniaAPI.elvenKnowledge
		uberSpreader.knowledgeType = BotaniaAPI.elvenKnowledge
		winery.knowledgeType = BotaniaAPI.elvenKnowledge
		worldgen.knowledgeType = BotaniaAPI.elvenKnowledge
		
		if (ThaumcraftSuffusionRecipes.recipesLoaded) {
			tctrees.knowledgeType = BotaniaAPI.elvenKnowledge
		}
	}
	
	fun disableESM() {
		setKnowledgeTypes()
		
		removeEntry(esm, categoryAlfheim)
		removeEntry(races, categoryAlfheim)
	}
	
	fun reEnableESM() {
		if (AlfheimConfigHandler.enableElvenStory) {
			preInitElvenStory()
			initElvenStory()
		}
		if (AlfheimConfigHandler.enableMMO) {
			preInitMMO()
			initMMO()
		}
		
		if (!categoryAlfheim.entries.contains(esm)) BotaniaAPI.addEntry(esm, categoryAlfheim)
		if (!categoryAlfheim.entries.contains(races)) BotaniaAPI.addEntry(races, categoryAlfheim)
		
		setKnowledgeTypes()
	}
	
	fun disableMMO() {
		setKnowledgeTypes()
		
		removeEntry(parties, categoryAlfheim)
		removeEntry(spells, categoryAlfheim)
		removeEntry(targets, categoryAlfheim)
	}
	
	fun reEnableMMO() {
		if (AlfheimConfigHandler.enableElvenStory) {
			preInitElvenStory()
			initElvenStory()
		}
		if (AlfheimConfigHandler.enableMMO) {
			preInitMMO()
			initMMO()
		}
		
		if (!categoryAlfheim.entries.contains(parties)) BotaniaAPI.addEntry(parties, categoryAlfheim)
		if (!categoryAlfheim.entries.contains(spells)) BotaniaAPI.addEntry(spells, categoryAlfheim)
		if (!categoryAlfheim.entries.contains(targets)) BotaniaAPI.addEntry(targets, categoryAlfheim)
		
		setKnowledgeTypes()
	}
	
	private fun removeEntry(entry: LexiconEntry?, category: LexiconCategory) {
		BotaniaAPI.getAllEntries().remove(entry)
		category.entries.remove(entry)
	}
}

object AlfheimMultiblocks {
	val anchor = TileRealityAnchor.makeMultiblockSet()
	val infuser = TileManaInfuser.makeMultiblockSet()
	val infuserU = TileManaInfuser.makeMultiblockSetUnknown()
	val portal = TileAlfheimPortal.makeMultiblockSet()
	val soul = TileManaInfuser.makeMultiblockSetSoul()
	val spire = TileSpire.makeMultiblockSet()
	val treeCrafter = TileTreeCrafter.makeMultiblockSet()
	val yordin = TileTradePortal.makeMultiblockSet()
}
