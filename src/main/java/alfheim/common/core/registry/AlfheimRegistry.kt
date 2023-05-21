package alfheim.common.core.registry

import alexsocol.asjlib.ASJUtilities.registerEntity
import alfheim.api.*
import alfheim.api.AlfheimAPI.addPink
import alfheim.api.AlfheimAPI.registerAnomaly
import alfheim.api.AlfheimAPI.registerSpell
import alfheim.api.block.tile.SubTileAnomalyBase.EnumAnomalyRarity.*
import alfheim.common.block.*
import alfheim.common.block.tile.*
import alfheim.common.block.tile.corporea.*
import alfheim.common.block.tile.sub.anomaly.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.entity.*
import alfheim.common.entity.boss.*
import alfheim.common.entity.boss.primal.*
import alfheim.common.entity.item.*
import alfheim.common.entity.spell.*
import alfheim.common.item.*
import alfheim.common.item.material.ElvenResourcesMetas
import alfheim.common.potion.*
import alfheim.common.spell.darkness.*
import alfheim.common.spell.earth.*
import alfheim.common.spell.fire.*
import alfheim.common.spell.illusion.*
import alfheim.common.spell.nature.*
import alfheim.common.spell.sound.*
import alfheim.common.spell.tech.*
import alfheim.common.spell.water.*
import alfheim.common.spell.wind.*
import cpw.mods.fml.common.registry.EntityRegistry
import cpw.mods.fml.common.registry.GameRegistry.registerTileEntity
import net.minecraft.entity.*
import net.minecraft.init.*
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.biome.BiomeGenBase
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.common.Botania
import vazkii.botania.common.block.*
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.block.ItemBlockSpecialFlower
import vazkii.botania.common.lib.LibBlockNames

object AlfheimRegistry {
	
	fun preInit() {
		registerPotions()
		registerEntities()
		registerTileEntities()
	}
	
	fun init() {
		registerSpells()
		loadAllPinkStuff()
	}
	
	fun postInit() {
		if (AlfheimConfigHandler.looniumOverseed)
			BotaniaAPI.looniumBlacklist.remove(ModItems.overgrowthSeed)
		
		val (w, n, x) = AlfheimConfigHandler.voidCreeper
		
		for (i in BiomeGenBase.getBiomeGenArray()) {
			if (i != null && !AlfheimConfigHandler.voidCreepBiomeBlackList.contains(i.biomeID))
				EntityRegistry.addSpawn(EntityVoidCreeper::class.java, w, n, x, EnumCreatureType.monster, i)
		}
		
		registerEnderOres()
	}
	
	private fun registerPotions() {
		PotionBeastWithin
		PotionBerserk
		PotionBleeding
		PotionButterShield
		PotionDeathMark
		PotionAlfheim(AlfheimConfigHandler.potionIDDecay, "decay", true, 0x553355)
		PotionEternity
		PotionGoldRush
		PotionAlfheim(AlfheimConfigHandler.potionIDIceLens, "icelens", false, 0xDDFFFF)
		PotionLeftFlame
		PotionLightningShield
		PotionManaVoid
		PotionAlfheim(AlfheimConfigHandler.potionIDNineLifes, "nineLifes", false, 0xDD2222)
		PotionNinja
		PotionNoclip
		PotionAlfheim(AlfheimConfigHandler.potionIDOvermage, "overmage", false, 0x88FFFF)
		PotionAlfheim(AlfheimConfigHandler.potionIDPossession, "possession", true, 0xCC0000)
		PotionQuadDamage
		PotionSacrifice
		PotionShowMana
		PotionSoulburn
		PotionAlfheim(AlfheimConfigHandler.potionIDStoneSkin, "stoneSkin", false, 0x593C1F)
		PotionTank
		PotionThrow
		PotionWellOLife
		PotionAlfheim(AlfheimConfigHandler.potionIDWisdom, "wisdom", false, 0xFFC880)
	}
	
	var nextEntityID = 0
		get() = field++
	
	private fun registerEntities() {
		registerEntity(EntityButterfly::class.java, "Butterfly", nextEntityID, 0, -1)
		registerEntity(EntityDedMoroz::class.java, "DedMoroz", nextEntityID)
		registerEntity(EntityElf::class.java, "Elf", nextEntityID, 0x1A660A, 0x4D3422)
		registerEntity(EntityFireSpirit::class.java, "FireSpirit", nextEntityID)
		registerEntity(EntityFenrir::class.java, "Fenrir", nextEntityID)
		registerEntity(EntityFlugel::class.java, "Flugel", nextEntityID)
		registerEntity(EntityGrieferCreeper::class.java, "GrieferCreeper", nextEntityID, 0xFFFFFF, 0)
		registerEntity(EntityJellyfish::class.java, "Jellyfish", nextEntityID, 0xFFFFFF, -1)
		registerEntity(EntityLolicorn::class.java, "Lolicorn", nextEntityID)
		registerEntity(EntityMuspelson::class.java, "Muspelson", nextEntityID, 0x3E1900, 0xD05D14)
		registerEntity(EntityAlfheimPixie::class.java, "Pixie", nextEntityID, 0xFF76D6, 0xFFE3FF)
		registerEntity(EntityRollingMelon::class.java, "RollingMelon", nextEntityID, 0xBECB25, 0x5B751A)
		registerEntity(EntityRook::class.java, "Rook", nextEntityID)
		registerEntity(EntitySnowSprite::class.java, "SnowSprite", nextEntityID, 0xEEFFFF, 0xE3F3F3)
		registerEntity(EntitySurtr::class.java, "Surtr", nextEntityID)
		registerEntity(EntityThrym::class.java, "Thrym", nextEntityID)
		registerEntity(EntityVoidCreeper::class.java, "VoidCreeper", nextEntityID, 0xcc11d3, 0xfb9bff)
		
		registerEntity(EntityBlackBolt::class.java, "BlackBolt", nextEntityID)
		registerEntity(EntityBlock::class.java, "Block", nextEntityID)
		registerEntity(EntityCharge::class.java, "Charge", nextEntityID)
		registerEntity(EntityEarthquake::class.java, "Earthquake", nextEntityID)
		registerEntity(EntityEarthquakeFracture::class.java, "EarthquakeFracture", nextEntityID)
		registerEntity(EntityFireAura::class.java, "FireAura", nextEntityID)
		registerEntity(EntityFireTornado::class.java, "FireTornado", nextEntityID)
		registerEntity(EntityFracturedSpaceCollector::class.java, "FracturedSpaceCollector", nextEntityID)
		registerEntity(EntityIcicle::class.java, "Icicle", nextEntityID)
		registerEntity(EntityItemImmortal::class.java, "ImmortalItem", nextEntityID)
		registerEntity(EntityItemImmortalRelic::class.java, "ImmortalRelicItem", nextEntityID)
		registerEntity(EntityLightningMark::class.java, "LightningMark", nextEntityID)
		registerEntity(EntityMeteor::class.java, "Meteor", nextEntityID)
		registerEntity(EntityMuspelheimSun::class.java, "MuspelheimSun", nextEntityID)
		registerEntity(EntityMuspelheimSunSlash::class.java, "MuspelheimSunSlash", nextEntityID)
		registerEntity(EntityPrimalBossChunkAttack::class.java, "ChunkAttack", nextEntityID)
		registerEntity(EntityPrimalMark::class.java, "PrimalMark", nextEntityID)
		registerEntity(EntitySniceBall::class.java, "SniceBall", nextEntityID)
		registerEntity(EntityThrowableItem::class.java, "ThrownItem", nextEntityID)
		registerEntity(EntityThrownPotion::class.java, "ThrownPotion", nextEntityID)
		registerEntity(EntityThunderChakram::class.java, "ThunderChakram", nextEntityID)
		
		registerEntity(EntityGleipnir::class.java, "Gleipnir", nextEntityID)
		registerEntity(EntityMjolnir::class.java, "Mjolnir", nextEntityID)
		
		registerEntity(EntityMagicArrow::class.java, "MagicArrow", nextEntityID)
		registerEntity(EntitySubspace::class.java, "Subspace", nextEntityID)
		registerEntity(EntitySubspaceSpear::class.java, "SubspaceSpear", nextEntityID)
		registerEntity(FakeLightning::class.java, "FakeLightning", nextEntityID)
		
		registerEntity(EntitySpellAcidMyst::class.java, "SpellAcidMyst", nextEntityID)
		registerEntity(EntitySpellAquaStream::class.java, "SpellAquaStream", nextEntityID)
		registerEntity(EntitySpellDarkness::class.java, "SpellDarkness", nextEntityID)
		registerEntity(EntitySpellDriftingMine::class.java, "SpellDriftingMine", nextEntityID)
		registerEntity(EntitySpellFenrirStorm::class.java, "SpellFenrirStorm", nextEntityID)
		registerEntity(EntitySpellFireball::class.java, "SpellFireball", nextEntityID)
		registerEntity(EntitySpellFirestar::class.java, "SpellFirestar", nextEntityID)
		registerEntity(EntitySpellFirewall::class.java, "SpellFirewall", nextEntityID)
		registerEntity(EntitySpellGravityTrap::class.java, "SpellGravityTrap", nextEntityID)
		registerEntity(EntitySpellHarp::class.java, "SpellHarp", nextEntityID)
		registerEntity(EntitySpellLeafStorm::class.java, "SpellLeafStorm", nextEntityID)
		registerEntity(EntitySpellIsaacMissile::class.java, "SpellIsaacMissile", nextEntityID)
		registerEntity(EntitySpellMortar::class.java, "SpellMortar", nextEntityID)
		registerEntity(EntitySpellNoteshot::class.java, "SpellNoteshot", nextEntityID)
		registerEntity(EntitySpellWindBlade::class.java, "SpellWindBlade", nextEntityID)
	}
	
	/**
	 * Registers new entity with egg. -1 color is rainbow color
	 * @param entityClass Entity's class file
	 * @param name The name of this entity
	 * @param id Mod-specific entity id
	 * @param color1 Egg color
	 * @param color2 Dots color
	 */
	fun registerEntity(entityClass: Class<out Entity>, name: String, id: Int, color1: Int, color2: Int) {
		ItemSpawnEgg.addMapping(entityClass, color1, color2)
		registerEntity(entityClass, name, id)
	}
	
	private fun registerTileEntities() {
		registerTile(TileAlfheimPortal::class.java, "AlfheimPortal")
		registerTile(TileAlfheimPylon::class.java, "AlfheimPylon")
		registerTile(TileAnimatedTorch::class.java, "AnimatedTorch")
		registerTile(TileAnomaly::class.java, "Anomaly")
		registerTile(TileAnomalyHarvester::class.java, "AnomalyHarvester")
		registerTile(TileAnyavil::class.java, "Anyavil")
		registerTile(TileBarrel::class.java, "Barrel")
		registerTile(TileCorporeaAutocrafter::class.java, "CorporeaAutocrafter")
		registerTile(TileCorporeaInjector::class.java, "CorporeaInjector")
		registerTile(TileCorporeaRat::class.java, "CorporeaRat")
		registerTile(TileCorporeaSparkBase::class.java, "CorporeaSparkBase")
		registerTile(TileDomainLobby::class.java, "DomainLobby")
		registerTile(TileEnderActuator::class.java, "EnderActuator")
		registerTile(TileFloatingFlowerRainbow::class.java, "miniIslandRainbow")
		registerTile(TileHeadFlugel::class.java, "HeadFlugel")
		registerTile(TileHeadMiku::class.java, "HeadMiku")
		registerTile(TileManaAccelerator::class.java, "ItemHolder")
		registerTile(TileManaInfuser::class.java, "ManaInfuser")
		registerTile(TilePowerStone::class.java, "PowerStone")
		registerTile(TileRaceSelector::class.java, "RaceSelector")
		registerTile(TileRealityAnchor::class.java, "RealityAnchor")
		registerTile(TileRift::class.java, "Rift")
		registerTile(TileSpire::class.java, "Spire")
		registerTile(TileTradePortal::class.java, "TradePortal")
		registerTile(TileVafthrudnirSoul::class.java, "VafthrudnirSoul")
		registerTile(TileYggFlower::class.java, "YggFlower")
		
		registerAnomalies()
		
		registerTile(TileCracklingStar::class.java, "StarPlacer2")
		registerTile(TileStar::class.java, "StarPlacer")
		registerTile(TileItemDisplay::class.java, "ItemDisplay")
		registerTile(TileLightningRod::class.java, "RodLightning")
		registerTile(TileLivingwoodFunnel::class.java, "LivingwoodFunnel")
		registerTile(TileRainbowManaFlame::class.java, "ManaFlame")
		registerTile(TileSchemaController::class.java, "SchemaController")
		registerTile(TileSchemaAnnihilator::class.java, "SchemaAnnihilator")
		registerTile(TileTreeCook::class.java, "TreeCook")
		registerTile(TileTreeCrafter::class.java, "TreeCrafter")
	}
	
	private fun registerTile(tileEntityClass: Class<out TileEntity>, id: String) {
		registerTileEntity(tileEntityClass, "${ModInfo.MODID}:$id")
	}
	
	private fun registerAnomalies() {
		registerAnomaly("Antigrav", SubTileAntigrav::class.java, COMMON, 7)
		registerAnomaly("Gravity", SubTileGravity::class.java, COMMON, 0)
		registerAnomaly("Killer", SubTileKiller::class.java, EPIC, 5)
		registerAnomaly("Lightning", SubTileLightning::class.java, COMMON, 1)
		registerAnomaly("ManaTornado", SubTileManaTornado::class.java, RARE, 2)
		registerAnomaly("ManaVoid", SubTileManaVoid::class.java, COMMON, 3)
		registerAnomaly("SpeedUp", SubTileSpeedUp::class.java, EPIC, 4)
		registerAnomaly("Warp", SubTileWarp::class.java, RARE, 6)
	}
	
	private fun registerSpells() {
		registerSpell(SpellAcidMyst)
		registerSpell(SpellAquaBind)
		registerSpell(SpellAquaStream)
		registerSpell(SpellBattleHorn)
		registerSpell(SpellBeastWithin)
		registerSpell(SpellBlink)
		registerSpell(SpellBunnyHop)
		registerSpell(SpellButterflyShield)
		registerSpell(SpellCall)
		registerSpell(SpellConfusion)
		registerSpell(SpellDay)
		registerSpell(SpellDarkness)
		registerSpell(SpellDeathMark)
		registerSpell(SpellDecay)
		registerSpell(SpellDispel)
		registerSpell(SpellDriftingMine)
		registerSpell(SpellDragonGrowl)
		registerSpell(SpellEcho)
		registerSpell(SpellFenrirStorm)
		registerSpell(SpellFireball)
		registerSpell(SpellFirestar)
		registerSpell(SpellFirewall)
		registerSpell(SpellGravityTrap)
		registerSpell(SpellGoldRush)
		registerSpell(SpellHammerfall)
		registerSpell(SpellHarp)
		registerSpell(SpellHealing)
		registerSpell(SpellHollowBody)
		registerSpell(SpellIceLens)
		registerSpell(SpellIgnition)
		registerSpell(SpellIsaacStorm)
		registerSpell(SpellJoin)
		registerSpell(SpellLeafStorm)
		registerSpell(SpellLiquification)
		registerSpell(SpellMortar)
		registerSpell(SpellNight)
		registerSpell(SpellNightVision)
		registerSpell(SpellNineLifes)
		registerSpell(SpellNoclip)
		registerSpell(SpellNoteshot)
		registerSpell(SpellOutdare)
		registerSpell(SpellPoisonRoots)
		registerSpell(SpellPurifyingSurface)
		registerSpell(SpellRain)
		registerSpell(SpellRefresh)
		registerSpell(SpellRepair)
		registerSpell(SpellResurrect)
		registerSpell(SpellSacrifice)
		registerSpell(SpellShadowVortex)
		registerSpell(SpellSmokeScreen)
		registerSpell(SpellStoneSkin)
		registerSpell(SpellSun)
		registerSpell(SpellSwap)
		registerSpell(SpellThor)
		registerSpell(SpellThrow)
		registerSpell(SpellThunder)
		registerSpell(SpellTimeStop)
		registerSpell(SpellTitanHit)
		registerSpell(SpellTrueSight)
		registerSpell(SpellUphealth)
		registerSpell(SpellWallWarp)
		registerSpell(SpellWarhood)
		registerSpell(SpellWaterBreathing)
		registerSpell(SpellWellOLife)
		registerSpell(SpellWindBlades)
	}
	
	private fun loadAllPinkStuff() {
		addPink(ItemStack(Blocks.wool, 1, 6), 1)
		addPink(ItemStack(Blocks.red_flower, 1, 7), 1)
		addPink(ItemStack(Blocks.stained_hardened_clay, 1, 6), 1)
		addPink(ItemStack(Blocks.stained_glass, 1, 6), 1)
		addPink(ItemStack(Blocks.stained_glass_pane, 1, 6), 1)
		addPink(ItemStack(Blocks.carpet, 1, 6), 1)
		addPink(ItemStack(Blocks.double_plant, 1, 5), 2)
		
		addPink(ItemStack(Items.dye, 1, 9), 1)
		addPink(ItemStack(Items.potionitem, 1, 8193), 2)
		addPink(ItemStack(Items.potionitem, 1, 8225), 3)
		addPink(ItemStack(Items.potionitem, 1, 8257), 3)
		addPink(ItemStack(Items.potionitem, 1, 16385), 2)
		addPink(ItemStack(Items.potionitem, 1, 16417), 3)
		addPink(ItemStack(Items.potionitem, 1, 16449), 3)
		addPink(ItemStack(Items.porkchop), 1)
		
		
		
		addPink(ItemStack(ModBlocks.corporeaCrystalCube), 9)
		addPink(ItemStack(ModBlocks.corporeaFunnel), 9)
		addPink(ItemStack(ModBlocks.corporeaIndex), 27)
		addPink(ItemStack(ModBlocks.corporeaInterceptor), 9)
		addPink(ItemStack(ModBlocks.corporeaRetainer), 9)
		addPink(ItemStack(ModBlocks.flower, 1, 6), 2)
		addPink(ItemStack(ModBlocks.floatingFlower, 1, 6), 2)
		addPink(ItemStack(ModBlocks.doubleFlower1, 1, 6), 4) // upper part
		addPink(ItemStack(ModBlocks.doubleFlower1, 1, 14), 4) // bottom part just in case
		addPink(ItemStack(ModBlocks.manaBeacon, 1, 6), 8)
		addPink(ItemStack(ModBlocks.mushroom, 1, 6), 4)
		addPink(ItemStack(ModBlocks.petalBlock, 1, 6), 9)
		addPink(ItemStack(ModBlocks.shinyFlower, 1, 6), 2)
		addPink(ItemStack(ModBlocks.spawnerClaw), 18)
		addPink(ItemStack(ModBlocks.spreader, 1, 3), 18)
		addPink(ItemStack(ModBlocks.starfield), 45)
		addPink(ItemStack(ModBlocks.storage, 1, 2), 81)
		addPink(ItemStack(ModBlocks.storage, 1, 4), 81)
		addPink(ItemStack(ModBlocks.tinyPotato), 1)
		addPink(ItemStack(ModBlocks.unstableBlock, 1, 6), 2)
		addPink(ItemBlockSpecialFlower.ofType(LibBlockNames.SUBTILE_ARCANE_ROSE), 2) // was 4
		
		addPink(ItemStack(ModFluffBlocks.lavenderQuartz), 4)
		addPink(ItemStack(ModFluffBlocks.lavenderQuartz, 1, 1), 4)
		addPink(ItemStack(ModFluffBlocks.lavenderQuartz, 1, 2), 4)
		addPink(ItemStack(ModFluffBlocks.lavenderQuartzSlab), 2)
		addPink(ItemStack(ModFluffBlocks.lavenderQuartzStairs), 4)
		
//		addPink(ItemStack(ModItems.aesirRing), 6000)
		addPink(ItemStack(ModItems.baubleBox), 5)
		addPink(ItemStack(ModItems.blackHoleTalisman), 36)
		addPink(ItemStack(ModItems.corporeaSpark), 9)
		addPink(ItemStack(ModItems.cosmetic, 1, 8), 4) // was 8
		addPink(ItemStack(ModItems.cosmetic, 1, 30), 1)
		addPink(ItemStack(ModItems.dye, 1, 6), 1)
		for (i in 0..9) addPink(ItemStack(ModItems.flightTiara, 1, i), 88)
		addPink(ItemStack(ModItems.manaResource, 1, 7), 9)
		addPink(ItemStack(ModItems.manaResource, 1, 8), 9)
		addPink(ItemStack(ModItems.manaResource, 1, 9), 9)
		addPink(ItemStack(ModItems.manaResource, 1, 19), 1)
		addPink(ItemStack(ModItems.elementiumAxe), 27)
		addPink(ItemStack(ModItems.elementiumBoots), 36)
		addPink(ItemStack(ModItems.elementiumChest), 72)
		addPink(ItemStack(ModItems.elementiumHelm), 45)
		if (Botania.thaumcraftLoaded) addPink(ItemStack(ModItems.elementiumHelmRevealing), 45)
		addPink(ItemStack(ModItems.elementiumLegs), 63)
		addPink(ItemStack(ModItems.elementiumPick), 27)
		addPink(ItemStack(ModItems.elementiumShears), 18)
		addPink(ItemStack(ModItems.elementiumShovel), 9)
		addPink(ItemStack(ModItems.elementiumSword), 18)
		addPink(ItemStack(ModItems.lens, 1, 14), 18)
//		addPink(ItemStack(ModItems.lokiRing), 1000)
//		addPink(ItemStack(ModItems.odinRing), 1000)
		addPink(ItemStack(ModItems.openBucket), 27)
		addPink(ItemStack(ModItems.petal, 1, 6), 1)
		addPink(ItemStack(ModItems.pinkinator), 100)
		addPink(ItemStack(ModItems.pixieRing), 45)
		addPink(ItemStack(ModItems.quartz, 1, 3), 1)
		addPink(ItemStack(ModItems.rainbowRod), 45)
		addPink(ItemStack(ModItems.reachRing), 36)
		addPink(ItemStack(ModItems.rune, 1, 4), 10)
		addPink(ItemStack(ModItems.spawnerMover), 63)
		addPink(ItemStack(ModItems.slimeBottle), 45)
		addPink(ItemStack(ModItems.starSword), 20)
		addPink(ItemStack(ModItems.superTravelBelt), 27) // was 38
//		addPink(ItemStack(ModItems.thorRing), 1000)
		
		
		
		addPink(ItemStack(AlfheimBlocks.anyavil), 297)
		addPink(ItemStack(AlfheimBlocks.alfheimPylon), 45)
		addPink(ItemStack(AlfheimBlocks.elvenOre), 9)
		addPink(ItemStack(AlfheimBlocks.elvenOre, 1, 1), 9)
		addPink(ItemStack(AlfheimBlocks.irisDirt, 1, 6), 2)
		addPink(ItemStack(AlfheimBlocks.irisTallGrass0, 1, 6), 2)
		addPink(ItemStack(AlfheimBlocks.irisGrass, 1, 6), 1)
		addPink(ItemStack(AlfheimBlocks.irisLeaves0, 1, 6), 1)
		addPink(ItemStack(AlfheimBlocks.irisLeaves0, 1, 14), 1)
		addPink(ItemStack(AlfheimBlocks.irisPlanks, 1, 6), 2)
		addPink(ItemStack(AlfheimBlocks.irisSlabs[6]), 1)
		addPink(ItemStack(AlfheimBlocks.irisStairs[6]), 2)
		addPink(ItemStack(AlfheimBlocks.irisWood1, 1, 2), 2)
		addPink(ItemStack(AlfheimBlocks.itemDisplay, 1, 2), 1)
		addPink(ItemStack(AlfheimBlocks.manaInfuser), 90)
		
		addPink(ItemStack(AlfheimFluffBlocks.shrineRock, 1, 6), 1)
		
		addPink(ItemStack(AlfheimItems.aesirEmblem), 18)
		addPink(ItemStack(AlfheimItems.astrolabe), 54)
		addPink(ItemStack(AlfheimItems.colorOverride), 54)
		addPink(ItemStack(AlfheimItems.cloudPendantSuper), 18)
		addPink(ItemStack(AlfheimItems.elementalBoots), 36)
		addPink(ItemStack(AlfheimItems.elementalChestplate), 72)
		addPink(ItemStack(AlfheimItems.elementalHelmet), 45)
		if (Botania.thaumcraftLoaded) addPink(ItemStack(AlfheimItems.elementalHelmetRevealing), 45)
		addPink(ItemStack(AlfheimItems.elementalLeggings), 63)
		addPink(ElvenResourcesMetas.ManaInfusionCore.stack, 9)
		addPink(ElvenResourcesMetas.ElvenWeed.stack, 8)
		addPink(ItemStack(AlfheimItems.discFlugel), 13)
		addPink(ItemStack(AlfheimItems.flugelHead), 5)
		for (i in 0..6) addPink(ItemStack(AlfheimItems.hyperBucket, 1, i), 27)
		addPink(ItemStack(AlfheimItems.irisSeeds, 1, 6), 2)
		addPink(ItemStack(AlfheimItems.multibauble), 18)
		addPink(ItemStack(AlfheimItems.pixieAttractor), 54)
		addPink(ItemStack(AlfheimItems.priestEmblem, 1, 3), 18)
//		addPink(ItemStack(AlfheimItems.priestRingHeimdall), 1000)
//		addPink(ItemStack(AlfheimItems.priestRingNjord), 1000)
//		addPink(ItemStack(AlfheimItems.priestRingSif), 1000)
		addPink(ItemStack(AlfheimItems.rodClicker), 29)
		addPink(ItemStack(AlfheimItems.rodColorfulSkyDirt), 27)
		addPink(ItemStack(AlfheimItems.spatiotemporalRing), 54)
		addPink(ItemStack(AlfheimItems.trisDagger), 36)
		addPink(ItemStack(AlfheimItems.wireAxe), 81)
	}
	
	private fun registerEnderOres() {
		AlfheimConfigHandler.enderOreWeights.forEach {
			val (name, weight) = it.split(':')
			AlfheimAPI.addOreWeightEnd(name, weight.toInt())
		}
		
//		// Vanilla
//		AlfheimAPI.addOreWeightEnd("oreEndCoal", 9000)
//		AlfheimAPI.addOreWeightEnd("oreEndDiamond", 500)
//		AlfheimAPI.addOreWeightEnd("oreEndEmerald", 500)
//		AlfheimAPI.addOreWeightEnd("oreEndGold", 3635)
//		AlfheimAPI.addOreWeightEnd("oreEndIron", 5790)
//		AlfheimAPI.addOreWeightEnd("oreEndLapis", 3250)
//		AlfheimAPI.addOreWeightEnd("oreEndRedstone", 5600)
//
//		// Common tech ores
//		AlfheimAPI.addOreWeightEnd("oreEndCopper", 4700)
//		AlfheimAPI.addOreWeightEnd("oreEndTin", 3750)
//		AlfheimAPI.addOreWeightEnd("oreEndLead", 2790)
//		AlfheimAPI.addOreWeightEnd("oreEndNickel", 1790)
//		AlfheimAPI.addOreWeightEnd("oreEndPlatinum", 350)
//		AlfheimAPI.addOreWeightEnd("oreEndSilver", 1550)
//		AlfheimAPI.addOreWeightEnd("oreEndSteel", 1690)
//		AlfheimAPI.addOreWeightEnd("oreEndMithril", 1000)
//		AlfheimAPI.addOreWeightEnd("oreEndUranium", 2000)
//		AlfheimAPI.addOreWeightEnd("oreEndOsmium", 1000)
//		AlfheimAPI.addOreWeightEnd("oreEndIridium", 850)
//
//		// Tinker's Construct
//		AlfheimAPI.addOreWeightEnd("oreEndArdite", 1000)
//		AlfheimAPI.addOreWeightEnd("oreEndCobalt", 1000)
//
//		// Applied Energistics
//		AlfheimAPI.addOreWeightEnd("oreEndCertusQuartz", 2000)
//		AlfheimAPI.addOreWeightEnd("oreEndChargedCertusQuartz", 950)
//
//		// idk
//		AlfheimAPI.addOreWeightEnd("oreEndYellorite", 3000)
//		AlfheimAPI.addOreWeightEnd("oreClathrateEnder", 800)
//		AlfheimAPI.addOreWeightEnd("oreEndProsperity", 200)
//		AlfheimAPI.addOreWeightEnd("oreEndInferium", 500)
//		AlfheimAPI.addOreWeightEnd("oreEndBiotite", 500) // OreDictionary.registerOre("oreEndBiotite", Biotite.biotite_ore)
//
//		// Draconic Evolution
//		AlfheimAPI.addOreWeightEnd("oreDraconium", 200)
//
//		// Hardcore Ender Expansion (WRONG WEIGHTS)
//		AlfheimAPI.addOreWeightEnd("oreHeeStardust", 200)
//		AlfheimAPI.addOreWeightEnd("oreHeeInstabilityOrb", 200)
//		AlfheimAPI.addOreWeightEnd("oreHeeEndium", 200)
//		AlfheimAPI.addOreWeightEnd("oreHeeIgneousRock", 200)
//		AlfheimAPI.addOreWeightEnd("oreHeeEndPowder", 200)
	}
}
