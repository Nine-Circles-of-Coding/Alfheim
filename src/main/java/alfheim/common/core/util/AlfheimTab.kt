package alfheim.common.core.util

import alexsocol.asjlib.*
import alfheim.AlfheimCore
import alfheim.common.block.AlfheimBlocks.airyVirus
import alfheim.common.block.AlfheimBlocks.alfStorage
import alfheim.common.block.AlfheimBlocks.alfheimPortal
import alfheim.common.block.AlfheimBlocks.alfheimPylon
import alfheim.common.block.AlfheimBlocks.altLeaves
import alfheim.common.block.AlfheimBlocks.altPlanks
import alfheim.common.block.AlfheimBlocks.altSlabs
import alfheim.common.block.AlfheimBlocks.altStairs
import alfheim.common.block.AlfheimBlocks.altWood0
import alfheim.common.block.AlfheimBlocks.altWood1
import alfheim.common.block.AlfheimBlocks.amplifier
import alfheim.common.block.AlfheimBlocks.animatedTorch
import alfheim.common.block.AlfheimBlocks.anyavil
import alfheim.common.block.AlfheimBlocks.auroraDirt
import alfheim.common.block.AlfheimBlocks.auroraLeaves
import alfheim.common.block.AlfheimBlocks.auroraPlanks
import alfheim.common.block.AlfheimBlocks.auroraSlab
import alfheim.common.block.AlfheimBlocks.auroraStairs
import alfheim.common.block.AlfheimBlocks.auroraWood
import alfheim.common.block.AlfheimBlocks.barrel
import alfheim.common.block.AlfheimBlocks.calicoLeaves
import alfheim.common.block.AlfheimBlocks.calicoPlanks
import alfheim.common.block.AlfheimBlocks.calicoSapling
import alfheim.common.block.AlfheimBlocks.calicoSlabs
import alfheim.common.block.AlfheimBlocks.calicoStairs
import alfheim.common.block.AlfheimBlocks.calicoWood
import alfheim.common.block.AlfheimBlocks.circuitLeaves
import alfheim.common.block.AlfheimBlocks.circuitPlanks
import alfheim.common.block.AlfheimBlocks.circuitSapling
import alfheim.common.block.AlfheimBlocks.circuitSlabs
import alfheim.common.block.AlfheimBlocks.circuitStairs
import alfheim.common.block.AlfheimBlocks.circuitWood
import alfheim.common.block.AlfheimBlocks.corporeaAutocrafter
import alfheim.common.block.AlfheimBlocks.corporeaInjector
import alfheim.common.block.AlfheimBlocks.corporeaRatBase
import alfheim.common.block.AlfheimBlocks.corporeaSparkBase
import alfheim.common.block.AlfheimBlocks.dreamSapling
import alfheim.common.block.AlfheimBlocks.elvenOre
import alfheim.common.block.AlfheimBlocks.elvenSand
import alfheim.common.block.AlfheimBlocks.enderActuator
import alfheim.common.block.AlfheimBlocks.grapesRed
import alfheim.common.block.AlfheimBlocks.grapesWhite
import alfheim.common.block.AlfheimBlocks.icicle
import alfheim.common.block.AlfheimBlocks.irisDirt
import alfheim.common.block.AlfheimBlocks.irisGrass
import alfheim.common.block.AlfheimBlocks.irisLamp
import alfheim.common.block.AlfheimBlocks.irisLeaves0
import alfheim.common.block.AlfheimBlocks.irisLeaves1
import alfheim.common.block.AlfheimBlocks.irisPlanks
import alfheim.common.block.AlfheimBlocks.irisSapling
import alfheim.common.block.AlfheimBlocks.irisSlabs
import alfheim.common.block.AlfheimBlocks.irisStairs
import alfheim.common.block.AlfheimBlocks.irisTallGrass0
import alfheim.common.block.AlfheimBlocks.irisTallGrass1
import alfheim.common.block.AlfheimBlocks.irisWood0
import alfheim.common.block.AlfheimBlocks.irisWood1
import alfheim.common.block.AlfheimBlocks.irisWood2
import alfheim.common.block.AlfheimBlocks.irisWood3
import alfheim.common.block.AlfheimBlocks.itemDisplay
import alfheim.common.block.AlfheimBlocks.kindling
import alfheim.common.block.AlfheimBlocks.lightningLeaves
import alfheim.common.block.AlfheimBlocks.lightningPlanks
import alfheim.common.block.AlfheimBlocks.lightningSapling
import alfheim.common.block.AlfheimBlocks.lightningSlabs
import alfheim.common.block.AlfheimBlocks.lightningStairs
import alfheim.common.block.AlfheimBlocks.lightningWood
import alfheim.common.block.AlfheimBlocks.livingcobble
import alfheim.common.block.AlfheimBlocks.livingwoodFunnel
import alfheim.common.block.AlfheimBlocks.manaAccelerator
import alfheim.common.block.AlfheimBlocks.manaInfuser
import alfheim.common.block.AlfheimBlocks.netherLeaves
import alfheim.common.block.AlfheimBlocks.netherPlanks
import alfheim.common.block.AlfheimBlocks.netherSapling
import alfheim.common.block.AlfheimBlocks.netherSlabs
import alfheim.common.block.AlfheimBlocks.netherStairs
import alfheim.common.block.AlfheimBlocks.netherWood
import alfheim.common.block.AlfheimBlocks.niflheimBlock
import alfheim.common.block.AlfheimBlocks.powerStone
import alfheim.common.block.AlfheimBlocks.rainbowDirt
import alfheim.common.block.AlfheimBlocks.rainbowFlowerFloating
import alfheim.common.block.AlfheimBlocks.rainbowGrass
import alfheim.common.block.AlfheimBlocks.rainbowLeaves
import alfheim.common.block.AlfheimBlocks.rainbowMushroom
import alfheim.common.block.AlfheimBlocks.rainbowPetalBlock
import alfheim.common.block.AlfheimBlocks.rainbowPlanks
import alfheim.common.block.AlfheimBlocks.rainbowSlab
import alfheim.common.block.AlfheimBlocks.rainbowStairs
import alfheim.common.block.AlfheimBlocks.rainbowTallFlower
import alfheim.common.block.AlfheimBlocks.rainbowTallGrass
import alfheim.common.block.AlfheimBlocks.rainbowWood
import alfheim.common.block.AlfheimBlocks.realityAnchor
import alfheim.common.block.AlfheimBlocks.rpc
import alfheim.common.block.AlfheimBlocks.schemaAnnihilator
import alfheim.common.block.AlfheimBlocks.schemaController
import alfheim.common.block.AlfheimBlocks.schemaFiller
import alfheim.common.block.AlfheimBlocks.schemaGenerator
import alfheim.common.block.AlfheimBlocks.schemaMarker
import alfheim.common.block.AlfheimBlocks.sealingLeaves
import alfheim.common.block.AlfheimBlocks.sealingPlanks
import alfheim.common.block.AlfheimBlocks.sealingSapling
import alfheim.common.block.AlfheimBlocks.sealingSlabs
import alfheim.common.block.AlfheimBlocks.sealingStairs
import alfheim.common.block.AlfheimBlocks.sealingWood
import alfheim.common.block.AlfheimBlocks.shimmerQuartz
import alfheim.common.block.AlfheimBlocks.shimmerQuartzSlab
import alfheim.common.block.AlfheimBlocks.shimmerQuartzStairs
import alfheim.common.block.AlfheimBlocks.snowGrass
import alfheim.common.block.AlfheimBlocks.snowLayer
import alfheim.common.block.AlfheimBlocks.spire
import alfheim.common.block.AlfheimBlocks.stalactite
import alfheim.common.block.AlfheimBlocks.stalagmite
import alfheim.common.block.AlfheimBlocks.tradePortal
import alfheim.common.block.AlfheimFluffBlocks.dreamwoodBarkFence
import alfheim.common.block.AlfheimFluffBlocks.dreamwoodBarkFenceGate
import alfheim.common.block.AlfheimFluffBlocks.dreamwoodFence
import alfheim.common.block.AlfheimFluffBlocks.dreamwoodFenceGate
import alfheim.common.block.AlfheimFluffBlocks.dwarfLantern
import alfheim.common.block.AlfheimFluffBlocks.dwarfPlanks
import alfheim.common.block.AlfheimFluffBlocks.dwarfTrapDoor
import alfheim.common.block.AlfheimFluffBlocks.elvenSandstone
import alfheim.common.block.AlfheimFluffBlocks.elvenSandstoneSlab
import alfheim.common.block.AlfheimFluffBlocks.elvenSandstoneSlab2
import alfheim.common.block.AlfheimFluffBlocks.elvenSandstoneStairs
import alfheim.common.block.AlfheimFluffBlocks.elvenSandstoneWalls
import alfheim.common.block.AlfheimFluffBlocks.livingMountain
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleSlab
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleSlab1
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleSlab2
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleStairs
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleStairs1
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleStairs2
import alfheim.common.block.AlfheimFluffBlocks.livingcobbleWall
import alfheim.common.block.AlfheimFluffBlocks.livingrockBrickWall
import alfheim.common.block.AlfheimFluffBlocks.livingrockDark
import alfheim.common.block.AlfheimFluffBlocks.livingrockDarkSlabs
import alfheim.common.block.AlfheimFluffBlocks.livingrockDarkStairs
import alfheim.common.block.AlfheimFluffBlocks.livingrockDarkWalls
import alfheim.common.block.AlfheimFluffBlocks.livingwoodBarkFence
import alfheim.common.block.AlfheimFluffBlocks.livingwoodBarkFenceGate
import alfheim.common.block.AlfheimFluffBlocks.livingwoodFence
import alfheim.common.block.AlfheimFluffBlocks.livingwoodFenceGate
import alfheim.common.block.AlfheimFluffBlocks.roofTile
import alfheim.common.block.AlfheimFluffBlocks.roofTileSlabs
import alfheim.common.block.AlfheimFluffBlocks.roofTileStairs
import alfheim.common.block.AlfheimFluffBlocks.shrineGlass
import alfheim.common.block.AlfheimFluffBlocks.shrineLight
import alfheim.common.block.AlfheimFluffBlocks.shrinePanel
import alfheim.common.block.AlfheimFluffBlocks.shrinePillar
import alfheim.common.block.AlfheimFluffBlocks.shrineRock
import alfheim.common.block.AlfheimFluffBlocks.shrineRockWhiteSlab
import alfheim.common.block.AlfheimFluffBlocks.shrineRockWhiteStairs
import alfheim.common.block.AlfheimFluffBlocks.yggDecor
import alfheim.common.core.asm.AlfheimClassTransformer
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.helper.ContributorsPrivacyHelper
import alfheim.common.item.AlfheimItems
import alfheim.common.item.AlfheimItems.`DEV-NULL`
import alfheim.common.item.AlfheimItems.aesirCloak
import alfheim.common.item.AlfheimItems.aesirEmblem
import alfheim.common.item.AlfheimItems.akashicRecords
import alfheim.common.item.AlfheimItems.armilla
import alfheim.common.item.AlfheimItems.astrolabe
import alfheim.common.item.AlfheimItems.attributionBauble
import alfheim.common.item.AlfheimItems.auraRingElven
import alfheim.common.item.AlfheimItems.auraRingGod
import alfheim.common.item.AlfheimItems.auraRingPink
import alfheim.common.item.AlfheimItems.balanceCloak
import alfheim.common.item.AlfheimItems.cloudPendant
import alfheim.common.item.AlfheimItems.cloudPendantSuper
import alfheim.common.item.AlfheimItems.coatOfArms
import alfheim.common.item.AlfheimItems.colorOverride
import alfheim.common.item.AlfheimItems.corporeaRat
import alfheim.common.item.AlfheimItems.creativeReachPendant
import alfheim.common.item.AlfheimItems.crescentMoonAmulet
import alfheim.common.item.AlfheimItems.daolos
import alfheim.common.item.AlfheimItems.deathSeed
import alfheim.common.item.AlfheimItems.discFenrir
import alfheim.common.item.AlfheimItems.discFlugel
import alfheim.common.item.AlfheimItems.discFlugelUltra
import alfheim.common.item.AlfheimItems.discSurtr
import alfheim.common.item.AlfheimItems.discThrym
import alfheim.common.item.AlfheimItems.dodgeRing
import alfheim.common.item.AlfheimItems.elementalBoots
import alfheim.common.item.AlfheimItems.elementalChestplate
import alfheim.common.item.AlfheimItems.elementalHelmet
import alfheim.common.item.AlfheimItems.elementalLeggings
import alfheim.common.item.AlfheimItems.elfFirePendant
import alfheim.common.item.AlfheimItems.elfIcePendant
import alfheim.common.item.AlfheimItems.elvenFood
import alfheim.common.item.AlfheimItems.elvenResource
import alfheim.common.item.AlfheimItems.elvoriumBoots
import alfheim.common.item.AlfheimItems.elvoriumChestplate
import alfheim.common.item.AlfheimItems.elvoriumHelmet
import alfheim.common.item.AlfheimItems.elvoriumLeggings
import alfheim.common.item.AlfheimItems.enlighter
import alfheim.common.item.AlfheimItems.excaliber
import alfheim.common.item.AlfheimItems.fenrirBoots
import alfheim.common.item.AlfheimItems.fenrirChestplate
import alfheim.common.item.AlfheimItems.fenrirClaws
import alfheim.common.item.AlfheimItems.fenrirHelmet
import alfheim.common.item.AlfheimItems.fenrirLeggings
import alfheim.common.item.AlfheimItems.fenrirLoot
import alfheim.common.item.AlfheimItems.fireGrenade
import alfheim.common.item.AlfheimItems.flugelHead
import alfheim.common.item.AlfheimItems.flugelSoul
import alfheim.common.item.AlfheimItems.gaiaSlayer
import alfheim.common.item.AlfheimItems.gjallarhorn
import alfheim.common.item.AlfheimItems.gleipnir
import alfheim.common.item.AlfheimItems.gungnir
import alfheim.common.item.AlfheimItems.hyperBucket
import alfheim.common.item.AlfheimItems.invisibilityCloak
import alfheim.common.item.AlfheimItems.invisibleFlameLens
import alfheim.common.item.AlfheimItems.irisSeeds
import alfheim.common.item.AlfheimItems.livingrockPickaxe
import alfheim.common.item.AlfheimItems.lootInterceptor
import alfheim.common.item.AlfheimItems.manaGlove
import alfheim.common.item.AlfheimItems.manaMirrorImba
import alfheim.common.item.AlfheimItems.manaRingElven
import alfheim.common.item.AlfheimItems.manaRingGod
import alfheim.common.item.AlfheimItems.manaRingPink
import alfheim.common.item.AlfheimItems.manaStone
import alfheim.common.item.AlfheimItems.manaStoneGreater
import alfheim.common.item.AlfheimItems.mask
import alfheim.common.item.AlfheimItems.mjolnir
import alfheim.common.item.AlfheimItems.moonlightBow
import alfheim.common.item.AlfheimItems.multibauble
import alfheim.common.item.AlfheimItems.paperBreak
import alfheim.common.item.AlfheimItems.peacePipe
import alfheim.common.item.AlfheimItems.pendantSuperIce
import alfheim.common.item.AlfheimItems.pixieAttractor
import alfheim.common.item.AlfheimItems.priestCloak
import alfheim.common.item.AlfheimItems.priestEmblem
import alfheim.common.item.AlfheimItems.priestRingHeimdall
import alfheim.common.item.AlfheimItems.priestRingNjord
import alfheim.common.item.AlfheimItems.priestRingSif
import alfheim.common.item.AlfheimItems.ragnarokEmblem
import alfheim.common.item.AlfheimItems.rationBelt
import alfheim.common.item.AlfheimItems.realitySword
import alfheim.common.item.AlfheimItems.ringFeedFlower
import alfheim.common.item.AlfheimItems.ringSpider
import alfheim.common.item.AlfheimItems.rodBlackHole
import alfheim.common.item.AlfheimItems.rodClicker
import alfheim.common.item.AlfheimItems.rodColorfulSkyDirt
import alfheim.common.item.AlfheimItems.rodFlameStar
import alfheim.common.item.AlfheimItems.rodGrass
import alfheim.common.item.AlfheimItems.rodInterdiction
import alfheim.common.item.AlfheimItems.rodLightning
import alfheim.common.item.AlfheimItems.rodMuspelheim
import alfheim.common.item.AlfheimItems.rodNiflheim
import alfheim.common.item.AlfheimItems.rodPortal
import alfheim.common.item.AlfheimItems.rodPrismatic
import alfheim.common.item.AlfheimItems.rodRedstone
import alfheim.common.item.AlfheimItems.royalStaff
import alfheim.common.item.AlfheimItems.snowBoots
import alfheim.common.item.AlfheimItems.snowChest
import alfheim.common.item.AlfheimItems.snowHelmet
import alfheim.common.item.AlfheimItems.snowLeggings
import alfheim.common.item.AlfheimItems.snowSword
import alfheim.common.item.AlfheimItems.soulHorn
import alfheim.common.item.AlfheimItems.soulSword
import alfheim.common.item.AlfheimItems.spatiotemporalRing
import alfheim.common.item.AlfheimItems.spawnEgg
import alfheim.common.item.AlfheimItems.splashPotion
import alfheim.common.item.AlfheimItems.starPlacer
import alfheim.common.item.AlfheimItems.starPlacer2
import alfheim.common.item.AlfheimItems.subspaceSpear
import alfheim.common.item.AlfheimItems.surtrSword
import alfheim.common.item.AlfheimItems.terraHoe
import alfheim.common.item.AlfheimItems.thrymAxe
import alfheim.common.item.AlfheimItems.thunderChakram
import alfheim.common.item.AlfheimItems.triquetrum
import alfheim.common.item.AlfheimItems.trisDagger
import alfheim.common.item.AlfheimItems.volcanoBoots
import alfheim.common.item.AlfheimItems.volcanoChest
import alfheim.common.item.AlfheimItems.volcanoHelmet
import alfheim.common.item.AlfheimItems.volcanoLeggings
import alfheim.common.item.AlfheimItems.volcanoMace
import alfheim.common.item.AlfheimItems.wiltedLotus
import alfheim.common.item.AlfheimItems.wireAxe
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Blocks
import net.minecraft.item.*
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.block.ItemBlockSpecialFlower

object AlfheimTab: CreativeTabs("Alfheim") {
	
	val subtiles = HashSet<String>()
	
	override fun getTabIconItem() = alfheimPortal.toItem()
	
	lateinit var list: MutableList<Any?>
	
	init {
		backgroundImageName = "Alfheim.png"
		setNoTitle()
	}
	
	override fun hasSearchBar() = AlfheimConfigHandler.searchTabAlfheim
	
	override fun displayAllReleventItems(list: MutableList<Any?>) {
		this.list = list
		
		`DEV-NULL`?.let { addItem(it) }
		
		addBlock(manaInfuser)
		addBlock(corporeaAutocrafter)
		addBlock(corporeaInjector)
		addBlock(corporeaRatBase)
		addBlock(corporeaSparkBase)
		addBlock(enderActuator)
		addBlock(alfheimPortal)
		addBlock(tradePortal)
		addBlock(realityAnchor)
		addBlock(rpc)
		addBlock(Blocks.furnace, 8)
		addBlock(ModBlocks.spreader, 4)
		// addBlock(anomalyHarvester) // BACK
		addBlock(anyavil)
		addBlock(spire)
		addBlock(alfheimPylon)
		addBlock(manaAccelerator)
		addBlock(itemDisplay)
		addBlock(animatedTorch)
		addBlock(ModBlocks.lightRelay, 2)
		addBlock(ModBlocks.lightRelay, 3)
		addBlock(livingwoodFunnel)
		addBlock(amplifier)
		addBlock(irisLamp)
		addBlock(kindling)
		addBlock(alfStorage)
		addBlock(rainbowPetalBlock)
		addBlock(barrel)
		
		if (AlfheimCore.winter) {
			addBlock(snowGrass)
			addBlock(snowLayer)
		}
		
		addItem(elvenResource)
		addItem(AlfheimItems.eventResource)
		addItem(elvenFood)
		addItem(wiltedLotus)
		addItem(ModItems.ancientWill, 6)
		addItem(deathSeed)
		addItem(flugelHead)
		addItem(discFlugel)
		addItem(discFlugelUltra)
		addItem(discThrym)
		addItem(discSurtr)
		addItem(discFenrir)
		addItem(fenrirLoot)
		
		addItem(priestCloak)
		addItem(aesirCloak)
		addItem(balanceCloak)
		addItem(invisibilityCloak)
		//addItem (toolbelt)
		addItem(manaStone)
		addItem(manaStoneGreater)
		addItem(manaRingPink)
		addItem(auraRingPink)
		addItem(manaRingElven)
		addItem(auraRingElven)
		addItem(manaRingGod)
		addItem(auraRingGod)
		addItem(manaGlove)
		addItem(dodgeRing)
		addItem(ringSpider)
		addItem(ringFeedFlower)
		addItem(colorOverride)
		addItem(multibauble)
		addItem(spatiotemporalRing)
		addItem(ModItems.thorRing)
		addItem(priestRingSif)
		addItem(priestRingNjord)
		addItem(ModItems.lokiRing)
		addItem(priestRingHeimdall)
		addItem(ModItems.odinRing)
		addItem(ModItems.aesirRing)
		addItem(attributionBauble)
		addItem(priestEmblem)
		addItem(aesirEmblem)
		addItem(ragnarokEmblem)
		addItem(creativeReachPendant)
		addItem(elfFirePendant)
		addItem(elfIcePendant)
		addItem(crescentMoonAmulet)
		addItem(pendantSuperIce)
		addItem(cloudPendant)
		addItem(cloudPendantSuper)
		addItem(pixieAttractor)
		addItem(rationBelt)
		
		addItem(astrolabe)
		addItem(triquetrum)
		addItem(armilla)
		addItem(enlighter)
		addItem(lootInterceptor)
		addItem(hyperBucket)
		addItem(manaMirrorImba)
		addItem(invisibleFlameLens)
		
		(22..(21 + AlfheimClassTransformer.moreLenses)).forEach {
			addItem(ModItems.lens, it)
		}
		
		addItem(ModItems.lens, 5000)
		
		addItem(soulHorn)
		addItem(soulHorn, 1)
		
		addItem(rodMuspelheim)
		addItem(rodNiflheim)
		addItem(rodLightning)
		addItem(rodColorfulSkyDirt)
		addItem(rodInterdiction)
		addItem(rodFlameStar)
		addItem(rodPrismatic)
		addItem(rodPortal)
		addItem(rodClicker)
		addItem(rodBlackHole)
		addItem(rodRedstone)
		addItem(corporeaRat)
		addItem(rodGrass)
		addItem(livingrockPickaxe)
		addItem(terraHoe)
		addItem(thunderChakram)
		
		addItem(elementalHelmet)
		addItem(elementalChestplate)
		addItem(elementalLeggings)
		addItem(elementalBoots)
		
		addItem(fenrirHelmet)
		addItem(fenrirChestplate)
		addItem(fenrirLeggings)
		addItem(fenrirBoots)
		addItem(fenrirClaws)
		
		addItem(snowHelmet)
		addItem(snowChest)
		addItem(snowLeggings)
		addItem(snowBoots)
		addItem(snowSword)
		
		addItem(volcanoHelmet)
		addItem(volcanoChest)
		addItem(volcanoLeggings)
		addItem(volcanoBoots)
		addItem(volcanoMace)
		
		addItem(elvoriumHelmet)
		addItem(elvoriumChestplate)
		addItem(elvoriumLeggings)
		addItem(elvoriumBoots)
		addItem(realitySword)
		
		addItem(soulSword)
		
		//addItem (storyToken)
		
		addItem(excaliber)
		addItem(daolos)
		addItem(mjolnir)
		addItem(subspaceSpear)
		addItem(gungnir)
		addItem(gleipnir)
		addItem(moonlightBow)
		addItem(mask)
		addItem(flugelSoul)
		addItem(gjallarhorn)
		addItem(akashicRecords)
		addItem(wireAxe)
		addItem(trisDagger)
		addItem(thrymAxe)
		addItem(surtrSword)
		
		addBlock(lightningWood)
		addBlock(netherWood)
		addBlock(circuitWood)
		addBlock(calicoWood)
		addBlock(sealingWood)
		addBlock(altWood0)
		addBlock(altWood1)
		
		addBlock(lightningPlanks)
		addBlock(netherPlanks)
		addBlock(circuitPlanks)
		addBlock(calicoPlanks)
		addBlock(sealingPlanks)
		addBlock(altPlanks)
		
		addBlock(lightningStairs)
		addBlock(netherStairs)
		addBlock(circuitStairs)
		addBlock(calicoStairs)
		addBlock(sealingStairs)
		altStairs.forEach { addBlock(it) }
		
		addBlock(lightningSlabs)
		addBlock(netherSlabs)
		addBlock(circuitSlabs)
		addBlock(calicoSlabs)
		addBlock(sealingSlabs)
		addBlock(altSlabs)
		
		addBlock(lightningLeaves)
		addBlock(netherLeaves)
		addBlock(circuitLeaves)
		addBlock(calicoLeaves)
		addBlock(sealingLeaves)
		addBlock(altLeaves)
		
		addBlock(lightningSapling)
		addBlock(netherSapling)
		addBlock(circuitSapling)
		addBlock(calicoSapling)
		addBlock(sealingSapling)
		addBlock(dreamSapling)
		
		addBlock(grapesRed[0])
		addBlock(grapesWhite)
		
		subtiles.mapTo(list) {
			ItemBlockSpecialFlower.ofType(it)
		}
		
		addBlock(rainbowFlowerFloating)
		
		if (AlfheimConfigHandler.enableMMO) addItem(paperBreak)
		if (AlfheimConfigHandler.enableMMO) addItem(peacePipe)
		
		addItem(spawnEgg)
		addItem(splashPotion)
		addItem(fireGrenade)
		
		addBlock(airyVirus)
		
		addBlock(powerStone)
		list.removeAt(list.size - 5)
		
		addBlock(dwarfLantern)
		
		addBlock(shrinePillar)
		addBlock(shrineRockWhiteStairs)
		addBlock(shrineRockWhiteSlab)
		addBlock(shrineRock)
		
		addBlock(roofTile)
		roofTileStairs.forEach { addBlock(it) }
		roofTileSlabs.forEach { addBlock(it) }
		
		addBlock(livingrockDark)
		livingrockDarkStairs.forEach { addBlock(it) }
		livingrockDarkSlabs.forEach { addBlock(it) }
		livingrockDarkWalls.forEach { addBlock(it) }
		addBlock(shimmerQuartz)
		addBlock(shimmerQuartzStairs)
		addBlock(shimmerQuartzSlab)
		addBlock(shrineLight)
		addBlock(shrineGlass)
		addBlock(shrinePanel)
		
		addBlock(niflheimBlock)
		addBlock(stalactite)
		addBlock(stalagmite)
		addBlock(icicle)
		
		addBlock(elvenOre)
		
		addBlock(livingMountain)
		addBlock(livingcobble)
		addBlock(livingcobbleStairs)
		addBlock(livingcobbleStairs1)
		addBlock(livingcobbleStairs2)
		addBlock(livingcobbleSlab)
		addBlock(livingcobbleSlab1)
		addBlock(livingcobbleSlab2)
		addBlock(livingcobbleWall)
		addBlock(livingrockBrickWall)
		
		addBlock(elvenSand)
		addBlock(elvenSandstone)
		elvenSandstoneStairs.forEach { addBlock(it) }
		addBlock(elvenSandstoneSlab)
		addBlock(elvenSandstoneSlab2)
		elvenSandstoneWalls.forEach { addBlock(it) }
		
		addBlock(livingwoodBarkFence)
		addBlock(livingwoodFence)
		addBlock(livingwoodBarkFenceGate)
		addBlock(livingwoodFenceGate)
		
		addBlock(dreamwoodBarkFence)
		addBlock(dreamwoodFence)
		addBlock(dreamwoodBarkFenceGate)
		addBlock(dreamwoodFenceGate)
		
		addBlock(yggDecor)
		addBlock(dwarfPlanks)
		addBlock(dwarfTrapDoor)
		
		addBlock(irisDirt)
		addBlock(rainbowDirt)
		addBlock(auroraDirt)
		
		addBlock(irisWood0)
		addBlock(irisWood1)
		addBlock(irisWood2)
		addBlock(irisWood3)
		addBlock(rainbowWood)
		addBlock(auroraWood)
		
		addBlock(irisPlanks)
		addBlock(rainbowPlanks)
		addBlock(auroraPlanks)
		
		irisStairs.forEach { addBlock(it) }
		addBlock(rainbowStairs)
		addBlock(auroraStairs)
		
		irisSlabs.forEach { addBlock(it) }
		addBlock(rainbowSlab)
		addBlock(auroraSlab)
		
		addBlock(irisLeaves0)
		addBlock(irisLeaves1)
		addBlock(rainbowLeaves)
		addBlock(auroraLeaves)
		
		addBlock(irisGrass)
		addBlock(rainbowGrass, 0)
		addBlock(rainbowGrass, 1)
		
		addBlock(irisTallGrass0)
		addBlock(irisTallGrass1)
		addBlock(rainbowTallGrass, 0)
		addBlock(rainbowTallGrass, 1)
		
		addBlock(rainbowTallFlower)
		addBlock(rainbowGrass, 2)
		addBlock(rainbowGrass, 3)
		addBlock(rainbowMushroom)
		
		addBlock(irisSapling)
		addItem(irisSeeds)
		addItem(starPlacer)
		addItem(starPlacer2)
		
		addItem(coatOfArms)
		
		addBlock(schemaController)
		addBlock(schemaFiller)
		addBlock(schemaMarker)
		addBlock(schemaGenerator)
		addBlock(schemaAnnihilator)
		
		if (ASJUtilities.isClient) {
			if (ContributorsPrivacyHelper.isCorrect(mc.thePlayer?.commandSenderName ?: "null", "AlexSocol"))
				addItem(royalStaff)
		}
		
		addItem(gaiaSlayer)
		
		additionalDisplays.forEach { it.invoke() }
	}
	
	fun addBlock(block: Block) {
		block.getSubBlocks(block.toItem(), this, list)
	}
	
	fun addItem(item: Item) {
		item.getSubItems(item, this, list)
	}
	
	fun addBlock(block: Block, meta: Int) {
		addStack(ItemStack(block, 1, meta))
	}
	
	fun addItem(item: Item, meta: Int) {
		addStack(ItemStack(item, 1, meta))
	}
	
	fun addStack(stack: ItemStack) {
		list.add(stack)
	}
	
	val additionalDisplays = ArrayList<() -> Unit>()
}
