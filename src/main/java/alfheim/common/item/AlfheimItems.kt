package alfheim.common.item

import alexsocol.asjlib.ASJUtilities.register
import alfheim.AlfheimCore
import alfheim.api.ModInfo
import alfheim.api.lib.LibOreDict
import alfheim.common.block.AlfheimBlocks
import alfheim.common.integration.thaumcraft.ThaumcraftAlfheimModule
import alfheim.common.item.creator.ItemRoyalStaff
import alfheim.common.item.equipment.armor.elemental.*
import alfheim.common.item.equipment.armor.elvoruim.*
import alfheim.common.item.equipment.bauble.*
import alfheim.common.item.equipment.tool.*
import alfheim.common.item.equipment.tool.elementuim.ItemElementiumHoe
import alfheim.common.item.equipment.tool.manasteel.ItemManasteelHoe
import alfheim.common.item.interaction.thaumcraft.*
import alfheim.common.item.material.*
import alfheim.common.item.relic.*
import alfheim.common.item.rod.*
import net.minecraft.item.*
import net.minecraftforge.oredict.OreDictionary
import vazkii.botania.common.Botania
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.equipment.bauble.ItemBalanceCloak
import vazkii.botania.common.item.record.ItemModRecord

object AlfheimItems {
	
	lateinit var astrolabe: Item
	lateinit var auraRingElven: Item
	lateinit var auraRingGod: Item
	lateinit var balanceCloak: Item
	lateinit var cloudPendant: Item
	lateinit var cloudPendantSuper: Item
	lateinit var creativeReachPendant: Item
	lateinit var crescentMoonAmulet: Item
	lateinit var dodgeRing: Item
	lateinit var elementalBoots: Item
	lateinit var elementalChestplate: Item
	lateinit var elementalHelmet: Item
	lateinit var elementalHelmetRevealing: Item
	fun elementalHelmetRevealingIsInitialized() = ::elementalHelmetRevealing.isInitialized
	lateinit var elementalLeggings: Item
	lateinit var elementiumHoe: Item
	lateinit var elfFirePendant: Item
	lateinit var elfIcePendant: Item
			 val elvenResource: Item = ItemElvenResource() // Because it MUST be constructed BEFORE blocks.
	lateinit var elvoriumBoots: Item
	lateinit var elvoriumChestplate: Item
	lateinit var elvoriumHelmet: Item
	lateinit var elvoriumHelmetRevealing: Item
	fun elvoriumHelmetRevealingIsInitialized() = ::elvoriumHelmetRevealing.isInitialized
	lateinit var elvoriumLeggings: Item
	lateinit var excaliber: Item
	lateinit var flugelDisc: Item
	lateinit var flugelDisc2: Item
	lateinit var flugelHead: Item
	lateinit var flugelHead2: Item
	lateinit var flugelSoul: Item
	lateinit var invisibilityCloak: Item
	lateinit var livingrockPickaxe: Item
	lateinit var lootInterceptor: Item
	lateinit var manaRingElven: Item
	lateinit var manaRingGod: Item
	lateinit var manasteelHoe: Item
	lateinit var manaStone: Item
	lateinit var manaStoneGreater: Item
	lateinit var mask: Item
	lateinit var moonlightBow: Item
	lateinit var multibauble: Item
	//lateinit var mjolnir: Item
	lateinit var paperBreak: Item
	lateinit var peacePipe: Item
	lateinit var pixieAttractor: Item
	lateinit var realitySword: Item
	lateinit var relicCleaner: Item
	lateinit var rodFire: Item
	lateinit var rodGrass: Item
	lateinit var rodIce: Item
	lateinit var subspaceSpear: Item
	lateinit var thinkingHand: Item
	
	lateinit var royalStaff: Item
	
	fun init() {
		construct()
		reg()
		regOreDict()
	}
	
	// There is some alphabetic mess cause Botania .setUnlocalizedName method includes registration,
	// so I need to construct some items in odd places to get beautiful Creative Tab representation :D
	// and yes, I'm too lazy to just reOverride Vazkii's code :P
	private fun construct() {
		astrolabe = ItemAstrolabe()
		elementalHelmet = ItemElementalWaterHelm()
		if (Botania.thaumcraftLoaded) elementalHelmetRevealing = ItemElementalWaterHelmRevealing()
		elementalChestplate = ItemElementalEarthChest()
		elementalLeggings = ItemElementalFireLeggings()
		elementalBoots = ItemElementalAirBoots()
		elementiumHoe = ItemElementiumHoe()
		elvoriumHelmet = ItemElvoriumHelmet()
		if (Botania.thaumcraftLoaded) elvoriumHelmetRevealing = ItemElvoriumHelmetRevealing()
		elvoriumChestplate = ItemElvoriumArmor(1, "ElvoriumChestplate")
		elvoriumLeggings = ItemElvoriumArmor(2, "ElvoriumLeggings")
		elvoriumBoots = ItemElvoriumArmor(3, "ElvoriumBoots")
		flugelHead = ItemHeadFlugel()
		flugelHead2 = ItemHeadMiku()
		realitySword = ItemRealitySword()
		excaliber = ItemExcaliber()
		//mjolnir = new ItemMjolnir();
		mask = ItemTankMask()
		flugelSoul = ItemFlugelSoul()
		flugelDisc = ItemModRecord("flugel", "FlugelDisc").setCreativeTab(AlfheimCore.alfheimTab)
		flugelDisc2 = ItemModRecord("miku", "MikuDisc").setCreativeTab(AlfheimCore.alfheimTab)
		elfFirePendant = ItemPendant("FirePendant")
		elfIcePendant = ItemPendant("IcePendant")
		creativeReachPendant = ItemCreativeReachPendant()
		pixieAttractor = ItemPendant("PixieAttractor")
		livingrockPickaxe = ItemLivingrockPickaxe()
		lootInterceptor = ItemLootInterceptor()
		manasteelHoe = ItemManasteelHoe()
		manaStone = ItemManaStorage("ManaStone", 3.0)
		manaStoneGreater = ItemManaStorage("ManaStoneGreater", 8.0)
		manaRingElven = ItemManaStorageRing("ManaRingElven", 5.0)
		auraRingElven = ItemAuraRingAlfheim("AuraRingElven")
		manaRingGod = ItemManaStorageRing("ManaRingGod", 10.0)
		auraRingGod = object: ItemAuraRingAlfheim("AuraRingGod") {
			override val delay: Int
				get() = 2
		}
		dodgeRing = ItemDodgeRing()
		cloudPendant = ItemCloudPendant()
		cloudPendantSuper = ItemCloudPendant("SuperCloudPendant", 3)
		balanceCloak = ItemBalanceCloak()
		invisibilityCloak = ItemInvisibilityCloak()
		crescentMoonAmulet = ItemCrescentMoonAmulet()
		rodFire = ItemRodElemental("MuspelheimRod", AlfheimBlocks.redFlame)
		rodGrass = ItemRodGrass()
		rodIce = ItemRodElemental("NiflheimRod", AlfheimBlocks.poisonIce)
		paperBreak = ItemPaperBreak()
		peacePipe = ItemPeacePipe()
		thinkingHand = ItemThinkingHand()
		
		if (Botania.thaumcraftLoaded) {
			ModItems.elementiumHelmRevealing.creativeTab = ThaumcraftAlfheimModule.tcnTab
			ModItems.manasteelHelmRevealing.creativeTab = ThaumcraftAlfheimModule.tcnTab
			ModItems.terrasteelHelmRevealing.creativeTab = ThaumcraftAlfheimModule.tcnTab
		}
		
		relicCleaner = ItemRelicCleaner()
		multibauble = ItemMultibauble()
		subspaceSpear = ItemSpearSubspace()
		moonlightBow = ItemMoonlightBow()
		
		royalStaff = ItemRoyalStaff()
	}
	
	private fun reg() {
		register(flugelHead)
		register(flugelHead2)
		register(realitySword)
		register(livingrockPickaxe)
		register(manasteelHoe)
		register(elementiumHoe)
		register(astrolabe)
		register(lootInterceptor)
		register(rodFire)
		register(rodIce)
		register(rodGrass)
		register(peacePipe)
		register(paperBreak)
		register(elvenResource)
		if (ModInfo.DEV) register(TheRodOfTheDebug())
	}
	
	private fun regOreDict() {
		OreDictionary.registerOre(LibOreDict.ELVORIUM_INGOT, ItemStack(elvenResource, 1, AlfheimItems.ElvenResourcesMetas.ElvoriumIngot))
		OreDictionary.registerOre(LibOreDict.MAUFTRIUM_INGOT, ItemStack(elvenResource, 1, AlfheimItems.ElvenResourcesMetas.MauftriumIngot))
		OreDictionary.registerOre(LibOreDict.MUSPELHEIM_POWER_INGOT, ItemStack(elvenResource, 1, AlfheimItems.ElvenResourcesMetas.MuspelheimPowerIngot))
		OreDictionary.registerOre(LibOreDict.NIFLHEIM_POWER_INGOT, ItemStack(elvenResource, 1, AlfheimItems.ElvenResourcesMetas.NiflheimPowerIngot))
		OreDictionary.registerOre(LibOreDict.ELVORIUM_NUGGET, ItemStack(elvenResource, 1, AlfheimItems.ElvenResourcesMetas.ElvoriumNugget))
		OreDictionary.registerOre(LibOreDict.MAUFTRIUM_NUGGET, ItemStack(elvenResource, 1, AlfheimItems.ElvenResourcesMetas.MauftriumNugget))
		OreDictionary.registerOre(LibOreDict.MUSPELHEIM_ESSENCE, ItemStack(elvenResource, 1, AlfheimItems.ElvenResourcesMetas.MuspelheimEssence))
		OreDictionary.registerOre(LibOreDict.NIFLHEIM_ESSENCE, ItemStack(elvenResource, 1, AlfheimItems.ElvenResourcesMetas.NiflheimEssence))
		OreDictionary.registerOre(LibOreDict.IFFESAL_DUST, ItemStack(elvenResource, 1, AlfheimItems.ElvenResourcesMetas.IffesalDust))
		OreDictionary.registerOre(LibOreDict.ARUNE[0], ItemStack(elvenResource, 1, AlfheimItems.ElvenResourcesMetas.PrimalRune))
		OreDictionary.registerOre(LibOreDict.ARUNE[1], ItemStack(elvenResource, 1, AlfheimItems.ElvenResourcesMetas.MuspelheimRune))
		OreDictionary.registerOre(LibOreDict.ARUNE[2], ItemStack(elvenResource, 1, AlfheimItems.ElvenResourcesMetas.NiflheimRune))
		OreDictionary.registerOre(LibOreDict.INFUSED_DREAM_TWIG, ItemStack(elvenResource, 1, AlfheimItems.ElvenResourcesMetas.InfusedDreamwoodTwig))
	}
	
	object ElvenResourcesMetas {
		val InterdimensionalGatewayCore: Int
		val ManaInfusionCore: Int
		val ElvoriumIngot: Int
		val MauftriumIngot: Int
		val MuspelheimPowerIngot: Int
		val NiflheimPowerIngot: Int
		val ElvoriumNugget: Int
		val MauftriumNugget: Int
		val MuspelheimEssence: Int
		val NiflheimEssence: Int
		val IffesalDust: Int
		val PrimalRune: Int
		val MuspelheimRune: Int
		val NiflheimRune: Int
		val InfusedDreamwoodTwig: Int//Transferer BACK
		
		init {
			val items = listOf(*ItemElvenResource.subItems)
			InterdimensionalGatewayCore = items.indexOf("InterdimensionalGatewayCore")
			ManaInfusionCore = items.indexOf("ManaInfusionCore")
			ElvoriumIngot = items.indexOf("ElvoriumIngot")
			MauftriumIngot = items.indexOf("MauftriumIngot")
			MuspelheimPowerIngot = items.indexOf("MuspelheimPowerIngot")
			NiflheimPowerIngot = items.indexOf("NiflheimPowerIngot")
			ElvoriumNugget = items.indexOf("ElvoriumNugget")
			MauftriumNugget = items.indexOf("MauftriumNugget")
			MuspelheimEssence = items.indexOf("MuspelheimEssence")
			NiflheimEssence = items.indexOf("NiflheimEssence")
			IffesalDust = items.indexOf("IffesalDust")
			PrimalRune = items.indexOf("PrimalRune")
			MuspelheimRune = items.indexOf("MuspelheimRune")
			NiflheimRune = items.indexOf("NiflheimRune")
			InfusedDreamwoodTwig = items.indexOf("InfusedDreamwoodTwig")
			//Transferer = items.indexOf("Transferer"); BACK
		}
	}
}
