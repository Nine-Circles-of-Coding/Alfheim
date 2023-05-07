package alfheim.common.item

import alfheim.api.ModInfo
import alfheim.api.lib.LibOreDict
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.WorkInProgressItemsHandler.WIP
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.*
import alfheim.common.item.creator.*
import alfheim.common.item.equipment.armor.elemental.*
import alfheim.common.item.equipment.armor.elvoruim.*
import alfheim.common.item.equipment.armor.fenrir.*
import alfheim.common.item.equipment.bauble.*
import alfheim.common.item.equipment.bauble.faith.ItemRagnarokEmblem
import alfheim.common.item.equipment.tool.*
import alfheim.common.item.equipment.tool.terrasteel.ItemTerraHoe
import alfheim.common.item.interaction.thaumcraft.*
import alfheim.common.item.material.*
import alfheim.common.item.material.ElvenResourcesMetas.*
import alfheim.common.item.relic.*
import alfheim.common.item.rod.*
import alfheim.common.item.equipment.armor.*
import alfheim.common.item.material.ItemEventResource
import net.minecraft.init.Items
import net.minecraft.item.*
import net.minecraftforge.oredict.OreDictionary
import vazkii.botania.common.Botania
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.item.ItemThornChakram
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.record.ItemModRecord
import vazkii.botania.common.item.relic.ItemDice

object AlfheimItems {
	
	val `DEV-NULL`: Item?
	
	val akashicRecords: Item
	val aesirCloak: Item
	val aesirEmblem: Item
	val armilla: Item
	val astrolabe: Item
	val attributionBauble: Item
	val auraRingElven: Item
	val auraRingGod: Item
	val auraRingPink: Item
	val balanceCloak: Item
	val cloudPendant: Item
	val cloudPendantSuper: Item
	val coatOfArms: Item
	val colorOverride: Item
	val corporeaRat: Item
	val creativeReachPendant: Item
	val crescentMoonAmulet: Item
	val daolos: Item
	val deathSeed: Item
	val dodgeRing: Item
	val elementalBoots: Item
	val elementalChestplate: Item
	val elementalHelmet: Item
	val elementalHelmetRevealing: Item?
	val elementalLeggings: Item
	val elfFirePendant: Item
	val elfIcePendant: Item
	val elvenFood: Item
	val elvenResource: Item
	val elvoriumBoots: Item
	val elvoriumChestplate: Item
	val elvoriumHelmet: Item
	val elvoriumHelmetRevealing: Item?
	val elvoriumLeggings: Item
	val enlighter: Item
	val eventResource: Item
	val excaliber: Item
	val fenrirBoots: Item
	val fenrirChestplate: Item
	val fenrirClaws: Item
	val fenrirHelmet: Item
	val fenrirHelmetRevealing: Item?
	val fenrirLeggings: Item
	val fireGrenade: Item
	val flugelDisc: Item
	val flugelDisc2: Item
	val flugelHead: Item
	val flugelHead2: Item
	val flugelSoul: Item
	val gaiaSlayer: Item
	val goddesCharm: Item
	val gjallarhorn: Item
	val gleipnir: Item
	val gungnir: Item
	val hyperBucket: Item
	val invisibilityCloak: Item
	val invisibleFlameLens: Item
	val irisSeeds: Item
	val livingrockPickaxe: Item
	val lootInterceptor: Item
	val manaGlove: Item
	val manaMirrorImba: Item
	val manaRingElven: Item
	val manaRingGod: Item
	val manaRingPink: Item
	val manaStone: Item
	val manaStoneGreater: Item
	val mask: Item
	val mjolnir: Item
	val moonlightBow: Item
	val multibauble: Item
	val paperBreak: Item
	val peacePipe: Item
	val pendantSuperIce: Item
	val pixieAttractor: Item
	val priestCloak: Item
	val priestEmblem: Item
	val priestRingHeimdall: Item
	val priestRingNjord: Item
	val priestRingSif: Item
	val ragnarokEmblem: Item
	val ragnarokEmblemF: Item
	val rationBelt: Item
	val realitySword: Item
	val ringFeedFlower: Item
	val ringSpider: Item
	val rodBlackHole: Item
	val rodColorfulSkyDirt: Item
	val rodClicker: Item
	val rodFlameStar: Item
	val rodGrass: Item
	val rodInterdiction: Item
	val rodLightning: Item
	val rodMuspelheim: Item
	val rodNiflheim: Item
	val rodPortal: Item
	val rodPrismatic: Item
	val rodRedstone: Item
	val snowSword: Item
	val snowHelmet: Item
	val snowHelmetRevealing: Item?
	val snowChest: Item
	val snowLeggings: Item
	val snowBoots: Item
	val soulHorn: Item
	val soulSword: Item
	val spatiotemporalRing: Item
	val spawnEgg: Item
	val splashPotion: Item
	val starPlacer: Item
	val starPlacer2: Item
	//val storyToken: Item
	val subspaceSpear: Item
	val surtrSword: Item
	val terraHoe: Item
	val thinkingHand: Item
	val thrymAxe: Item
	val thunderChakram: Item
	//val toolbelt: Item
	val trisDagger: Item
	val triquetrum: Item
	val volcanoMace: Item
	val volcanoHelmet: Item
	val volcanoHelmetRevealing: Item?
	val volcanoChest: Item
	val volcanoLeggings: Item
	val volcanoBoots: Item
	val wiltedLotus: Item
	val wireAxe: Item
	
	val royalStaff: Item
	
	init {
		akashicRecords = ItemAkashicRecords().WIP()
		aesirCloak = ItemAesirCloak()
		aesirEmblem = ItemAesirEmblem()
		armilla = ItemArmilla()
		astrolabe = ItemAstrolabe()
		attributionBauble = ItemAttributionBauble()
		auraRingElven = ItemAuraRingAlfheim("AuraRingElven")
		auraRingGod = ItemAuraRingAlfheim("AuraRingGod", 2)
		auraRingPink = ItemAuraRingAlfheim("AuraRingPink", 50)
		balanceCloak = ItemBalanceCloak()
		cloudPendant = ItemCloudPendant()
		cloudPendantSuper = ItemCloudPendant("SuperCloudPendant", 3)
		coatOfArms = ItemCoatOfArms()
		colorOverride = ItemColorOverride()
		corporeaRat = ItemCorporeaRat()
		creativeReachPendant = ItemCreativeReachPendant()
		crescentMoonAmulet = ItemCrescentMoonAmulet()
		daolos = ItemDaolos()
		deathSeed = ItemDeathSeed()
		dodgeRing = ItemDodgeRing()
		fireGrenade = ItemFireGrenade()
		elementalHelmet = ItemElementalWaterHelm()
		elementalHelmetRevealing = if (Botania.thaumcraftLoaded) ItemElementalWaterHelmRevealing() else null
		elementalChestplate = ItemElementalEarthChest()
		elementalLeggings = ItemElementalFireLeggings()
		elementalBoots = ItemElementalAirBoots()
		elfFirePendant = ItemPendant("FirePendant")
		elfIcePendant = ItemPendant("IcePendant")
		elvenFood = ItemElvenFood()
		elvenResource = ItemElvenResource()
		elvoriumHelmet = ItemElvoriumHelmet()
		elvoriumHelmetRevealing = if (Botania.thaumcraftLoaded) ItemElvoriumHelmetRevealing() else null
		elvoriumChestplate = ItemElvoriumArmor(1, "ElvoriumChestplate")
		elvoriumLeggings = ItemElvoriumArmor(2, "ElvoriumLeggings")
		elvoriumBoots = ItemElvoriumArmor(3, "ElvoriumBoots")
		enlighter = ItemEnlighter()
		eventResource = ItemEventResource()
		excaliber = ItemExcaliber()
		fenrirHelmet = ItemFenrirArmor(0, "FenrirHelmet")
		fenrirHelmetRevealing = if (Botania.thaumcraftLoaded) ItemFenrirHelmetRevealing() else null
		fenrirChestplate = ItemFenrirArmor(1, "FenrirChestplate")
		fenrirLeggings = ItemFenrirArmor(2, "FenrirLeggings")
		fenrirBoots = ItemFenrirArmor(3, "FenrirBoots")
		fenrirClaws = ItemFenrirClaws()
		flugelDisc = ItemModRecord("flugel", "FlugelDisc").setCreativeTab(AlfheimTab)
		flugelDisc2 = ItemModRecord("miku", "MikuDisc").setCreativeTab(null)
		flugelHead = ItemHeadFlugel()
		flugelHead2 = ItemHeadMiku()
		flugelSoul = ItemFlugelSoul()
		gaiaSlayer = ItemGaiaSlayer()
		goddesCharm = ItemGoddessCharm()
		gjallarhorn = ItemGjallarhorn()
		gleipnir = ItemGleipnir()
		gungnir = ItemGungnir()
		hyperBucket = ItemHyperBucket()
		invisibilityCloak = ItemInvisibilityCloak()
		invisibleFlameLens = ItemLensFlashInvisible()
		irisSeeds = ItemColorSeeds()
		livingrockPickaxe = ItemLivingrockPickaxe()
		lootInterceptor = ItemLootInterceptor()
		manaGlove = ItemManaweaveGlove()
		manaMirrorImba = ItemManaMirrorImba()
		manaRingElven = ItemManaStorageRing("ManaRingElven", 5.0)
		manaRingGod = ItemManaStorageRing("ManaRingGod", 10.0)
		manaRingPink = ItemManaStorageRing("ManaRingPink", 1.0)
		manaStone = ItemManaStorage("ManaStone", 3.0)
		manaStoneGreater = ItemManaStorage("ManaStoneGreater", 8.0)
		mask = ItemTankMask()
		mjolnir = ItemMjolnir()
		moonlightBow = ItemMoonlightBow()
		multibauble = ItemMultibauble()
		paperBreak = ItemPaperBreak()
		peacePipe = ItemPeacePipe()
		pendantSuperIce = ItemSuperIcePendant()
		pixieAttractor = ItemPendant("PixieAttractor")
		priestCloak = ItemPriestCloak()
		priestEmblem = ItemPriestEmblem()
		priestRingHeimdall = ItemHeimdallRing()
		priestRingNjord = ItemNjordRing()
		priestRingSif = ItemSifRing()
		ragnarokEmblem = ItemRagnarokEmblem()
		ragnarokEmblemF = ItemRagnarokEmblemF()
		rationBelt = ItemRationBelt()
		realitySword = ItemRealitySword()
		ringFeedFlower = ItemFeedFlowerRing()
		ringSpider = ItemSpiderRing()
		rodBlackHole = ItemRodBlackHole()
		rodColorfulSkyDirt = ItemRodIridescent()
		rodClicker = ItemRodClicker()
		rodGrass = ItemRodGrass()
		rodFlameStar = ItemRodFlameStar()
		rodInterdiction = ItemRodInterdiction()
		rodLightning = ItemRodLightning()
		rodMuspelheim = ItemRodElemental("MuspelheimRod") { AlfheimBlocks.redFlame }
		rodNiflheim = ItemRodElemental("NiflheimRod") { AlfheimBlocks.poisonIce }
		rodPortal = ItemRodPortal()
		rodPrismatic = ItemRodPrismatic()
		rodRedstone = ItemRedstoneRod()
		snowSword = ItemSnowSword()
		snowHelmet = ItemSnowArmor(0, "SnowHelmet")
		snowHelmetRevealing = if (Botania.thaumcraftLoaded) ItemSnowHelmetRevealing() else null
		snowChest = ItemSnowArmor(1, "SnowChest")
		snowLeggings = ItemSnowArmor(2, "SnowLeggings")
		snowBoots = ItemSnowArmor(3, "SnowBoots")
		soulHorn = ItemSoulHorn()
		soulSword = ItemSoulSword()
		spatiotemporalRing = ItemSpatiotemporalRing()
		splashPotion = ItemSplashPotion()
		spawnEgg = ItemSpawnEgg()
		starPlacer = ItemStarPlacer()
		starPlacer2 = ItemStarPlacer2()
		//storyToken = ItemStoryToken()
		subspaceSpear = ItemSpearSubspace()
		surtrSword = ItemSurtrSword()
		terraHoe = ItemTerraHoe()
		thinkingHand = ItemThinkingHand()
		thrymAxe = ItemThrymAxe()
		thunderChakram = ItemThunderChakram()
		trisDagger = ItemTrisDagger()
		triquetrum = ItemTriquetrum()
		//toolbelt = ItemToolbelt()
		volcanoMace = ItemVolcanoMace()
		volcanoHelmet = ItemVolcanoArmor(0, "VolcanoHelmet")
		volcanoHelmetRevealing = if (Botania.thaumcraftLoaded) ItemVolcanoHelmetRevealing() else null
		volcanoChest = ItemVolcanoArmor(1, "VolcanoChest")
		volcanoLeggings = ItemVolcanoArmor(2, "VolcanoLeggings")
		volcanoBoots = ItemVolcanoArmor(3, "VolcanoBoots")
		wireAxe = ItemWireAxe()
		wiltedLotus = ItemWiltedLotus()
		
		royalStaff = ItemRoyalStaff()
		`DEV-NULL` = if (ModInfo.DEV) TheRodOfTheDebug() else null
		
		// that's ok because there is check on first 6 array elements in the dice
		ItemDice.relicStacks += arrayOf(ItemStack(flugelSoul),
										ItemStack(mask),
										ItemStack(excaliber),
										ItemStack(subspaceSpear),
										ItemStack(moonlightBow),
										ItemStack(mjolnir),
										ItemStack(gungnir),
										ItemStack(priestRingHeimdall),
										ItemStack(priestRingNjord),
										ItemStack(priestRingSif),
										ItemStack(akashicRecords))
	}
	
	fun regOreDict() {
		OreDictionary.registerOre(LibOreDict.ELVORIUM_INGOT, ElvoriumIngot.stack)
		OreDictionary.registerOre(LibOreDict.MAUFTRIUM_INGOT, MauftriumIngot.stack)
		OreDictionary.registerOre(LibOreDict.MUSPELHEIM_POWER_INGOT, MuspelheimPowerIngot.stack)
		OreDictionary.registerOre(LibOreDict.NIFLHEIM_POWER_INGOT, NiflheimPowerIngot.stack)
		OreDictionary.registerOre(LibOreDict.ELVORIUM_NUGGET, ElvoriumNugget.stack)
		OreDictionary.registerOre(LibOreDict.MAUFTRIUM_NUGGET, MauftriumNugget.stack)
		OreDictionary.registerOre(LibOreDict.MUSPELHEIM_ESSENCE, MuspelheimEssence.stack)
		OreDictionary.registerOre(LibOreDict.NIFLHEIM_ESSENCE, NiflheimEssence.stack)
		OreDictionary.registerOre(LibOreDict.IFFESAL_DUST, IffesalDust.stack)
		OreDictionary.registerOre(LibOreDict.FENRIR_FUR, FenrirFur.stack)
		OreDictionary.registerOre(LibOreDict.ARUNE[0], PrimalRune.stack)
		OreDictionary.registerOre(LibOreDict.ARUNE[1], MuspelheimRune.stack)
		OreDictionary.registerOre(LibOreDict.ARUNE[2], NiflheimRune.stack)
		OreDictionary.registerOre(LibOreDict.INFUSED_DREAM_TWIG, InfusedDreamwoodTwig.stack)
		
		// Iridescense
		
		OreDictionary.registerOre(LibOreDict.TWIG_THUNDERWOOD, ThunderwoodTwig.stack)
		OreDictionary.registerOre(LibOreDict.SPLINTERS_THUNDERWOOD, ThunderwoodSplinters.stack)
		OreDictionary.registerOre(LibOreDict.TWIG_NETHERWOOD, NetherwoodTwig.stack)
		OreDictionary.registerOre(LibOreDict.SPLINTERS_NETHERWOOD, NetherwoodSplinters.stack)
		OreDictionary.registerOre(LibOreDict.COAL_NETHERWOOD, NetherwoodCoal.stack)
		OreDictionary.registerOre(LibOreDict.DYES[16], RainbowDust.stack)
		OreDictionary.registerOre(LibOreDict.FLORAL_POWDER, RainbowDust.stack)
		OreDictionary.registerOre(LibOreDict.RAINBOW_PETAL, RainbowPetal.stack)
		OreDictionary.registerOre(LibOreDict.RAINBOW_QUARTZ, RainbowQuartz.stack)
		OreDictionary.registerOre(LibOreDict.PETAL_ANY, RainbowPetal.stack)
		
		OreDictionary.registerOre(LibOreDict.HOLY_PENDANT, ItemStack(attributionBauble, 1, OreDictionary.WILDCARD_VALUE))
		
		OreDictionary.registerOre(LibOreDict.DYES[16], ItemStack(ModBlocks.bifrostPerm))
		OreDictionary.registerOre(LibOreDict.FLORAL_POWDER, ItemStack(ModItems.dye, 1, OreDictionary.WILDCARD_VALUE))
		OreDictionary.registerOre(LibOreDict.PETAL_ANY, ItemStack(ModItems.petal, 1, OreDictionary.WILDCARD_VALUE))
		
		
		
		OreDictionary.registerOre("coal", ItemStack(Items.coal))
		OreDictionary.registerOre("coal", ItemStack(Items.coal, 1, 1))
	}
}
