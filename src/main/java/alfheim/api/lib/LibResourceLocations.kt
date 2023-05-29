package alfheim.api.lib

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ResourceLocationAnimated
import alfheim.api.ModInfo
import net.minecraft.util.ResourceLocation

object LibResourceLocations {
	
	val akashicBox = ResourceLocationIL(ModInfo.MODID, "textures/model/item/AkashicBox.png")
	val akashicCube = ResourceLocationIL(ModInfo.MODID, "textures/model/item/AkashicRecordsCube.png")
	val akashicCube_ = ResourceLocationIL(ModInfo.MODID, "textures/model/item/AkashicRecordsCube_noShader.png")
	val akashicCyl = ResourceLocationIL(ModInfo.MODID, "textures/model/item/AkashicRecordsCyl.png")
	val altar9 = ResourceLocationIL(ModInfo.MODID, "textures/model/block/altar9.png")
	val anomalies = ResourceLocationIL(ModInfo.MODID, "textures/misc/anomalies.png")
	val antiPylon = ResourceLocationIL(ModInfo.MODID, "textures/model/block/AntiPylon.png")
	val antiPylonOld = ResourceLocationIL(ModInfo.MODID, "textures/model/block/AntiPylonOld.png")
	val anyavil = ResourceLocationIL(ModInfo.MODID, "textures/model/block/Anyavil.png")
	val avatarClicker = ResourceLocationIL(ModInfo.MODID, "textures/model/avatar/avatarClicker.png")
	val avatarColorDirt = ResourceLocationIL(ModInfo.MODID, "textures/model/avatar/avatarDirtRainbow.png")
	val avatarInterdiction = ResourceLocationIL(ModInfo.MODID, "textures/model/avatar/avatarInterdiction.png")
	val avatarLightning = ResourceLocationIL(ModInfo.MODID, "textures/model/avatar/avatarLightning.png")
	var babylon = ResourceLocationIL("botania", "textures/misc/babylon.png")
	var barrel = ResourceLocationIL(ModInfo.MODID, "textures/model/block/Barrel.png")
	var blank = ResourceLocationIL(ModInfo.MODID, "textures/misc/blank.png")
	val blood = ResourceLocationIL(ModInfo.MODID, "textures/misc/particles/blood.png")
	val bloodDrop = ResourceLocationIL(ModInfo.MODID, "textures/misc/particles/bloodDrop.png")
	val butterfly = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Butterfly.png")
	val butterflyFlat = ResourceLocationIL(ModInfo.MODID, "textures/misc/Butterfly.png")
	val cloakAesir = ResourceLocationIL(ModInfo.MODID, "textures/model/armor/cloak/Aesir.png")
	val cloakAesirGlow = ResourceLocationIL(ModInfo.MODID, "textures/model/armor/cloak/Aesir_glow.png")
	val cloakBalance = ResourceLocationIL(ModInfo.MODID, "textures/model/armor/cloak/Balance.png")
	val corrupt = ResourceLocationIL(ModInfo.MODID, "textures/misc/Corrupt.png")
	val creationPylon = if (ASJUtilities.isServer) ResourceLocation("textures/entity/steve.png") else ResourceLocationAnimated.local(ModInfo.MODID, "textures/model/block/CreationPylon.png")
	val creationPylonOld = if (ASJUtilities.isServer) ResourceLocation("textures/entity/steve.png") else ResourceLocationAnimated.local(ModInfo.MODID, "textures/model/block/CreationPylonOld.png")
	val cross = ResourceLocationIL(ModInfo.MODID, "textures/misc/crosshair.png")
	val deathTimer = ResourceLocationIL(ModInfo.MODID, "textures/gui/DeathTimer.png")
	val deathTimerBG = ResourceLocationIL(ModInfo.MODID, "textures/gui/DeathTimerBack.png")
	val dedMoroz = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/DedMoroz.png")
	val dedMorozEyes = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/DedMorozEyes.png")
	val domainDoor = ResourceLocationIL(ModInfo.MODID, "textures/blocks/DomainLobbyCoreFull.png")
	val elementiumBlock = ResourceLocationIL("botania", "textures/blocks/storage2.png")
	val elements = ResourceLocationIL(ModInfo.MODID, "textures/misc/elements.png")
	val elf = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Elf.png")
	var elvenPylon = ResourceLocationIL("botania", "textures/model/pylon2.png")
	var elvenPylonOld = ResourceLocationIL("botania", "textures/model/pylonOld2.png")
	val elvoriumArmor = ResourceLocationIL(ModInfo.MODID, "textures/model/armor/ElvoriumArmor.png")
	val feather = ResourceLocation(ModInfo.MODID, "textures/misc/particles/feather.png")
	val fenrir = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Fenrir.png")
	val fenrir1 = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Fenrir1.png")
	val fenrir2 = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Fenrir2.png")
	val fenrirArea = ResourceLocationIL(ModInfo.MODID, "textures/misc/IceArea.png")
	val fenrirClaw = ResourceLocationIL(ModInfo.MODID, "textures/items/FenrirClaws0.png")
	val fenrirClawOverlay = ResourceLocationIL(ModInfo.MODID, "textures/items/FenrirClaws1.png")
	var flowerBagExtended = ResourceLocationIL(ModInfo.MODID, "textures/gui/flowerBagExtended.png")
	val futhark = ResourceLocationIL(ModInfo.MODID, "textures/misc/FutharkMono.png")
	val gaiaPylon = ResourceLocationIL(ModInfo.MODID, "textures/model/block/GaiaPylon.png")
	val gaiaPylonOld = ResourceLocationIL(ModInfo.MODID, "textures/model/block/GaiaPylonOld.png")
	val gleipnir0 = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Gleipnir0.png")
	val gleipnir1 = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Gleipnir1.png")
	val gleipnir2 = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Gleipnir2.png")
	val glow = ResourceLocationIL(ModInfo.MODID, "textures/misc/glow.png")
	var glowCyan = ResourceLocationIL("botania", "textures/misc/glow1.png")
	val gravity = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Gravity.png")
	var halo = ResourceLocationIL("botania", "textures/misc/halo.png")
	val harp = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/harp.png")
	val health = ResourceLocationIL(ModInfo.MODID, "textures/gui/health.png")
	val hotSpells = ResourceLocationIL(ModInfo.MODID, "textures/gui/HotSpells.png")
	val ice = ResourceLocation("textures/blocks/ice.png")
	val iceLens = ResourceLocationIL(ModInfo.MODID, "textures/misc/IceLens.png")
	val jellyfish = if (ASJUtilities.isServer) ResourceLocation("textures/entity/steve.png") else ResourceLocationAnimated.local(ModInfo.MODID, "textures/model/entity/Jellyfish.png")
	val jibril = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Jibril.png")
	val jibrilDark = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/JibrilDark.png")
	var lexica = ResourceLocationIL("botania", "textures/model/lexica.png")
	val livingrock = ResourceLocationIL("botania", "textures/blocks/livingrock0.png")
	val lolicorn = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Lolicorn.png")
	var manaInfuserOverlay = ResourceLocationIL("botania", "textures/gui/manaInfusionOverlay.png")
	val markFire = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/MarkFire.png")
	val markIce = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/MarkIce.png")
	val markLightning = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/MarkLightning.png")
	val miko1 = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Miko1.png")
	val miko2 = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Miko2.png")
	val miku0 = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Miku0.png")
	val miku1 = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Miku1.png")
	val miku2 = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Miku2.png")
	val mimis = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/mimis.png")
	val mine1 = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/1.png")
	val mine2 = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/2.png")
	val mine3 = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/3.png")
	val miniIsland = ResourceLocationIL(ModInfo.MODID, "textures/model/block/miniIsland.png")
	val miniIslandOvergrowth = ResourceLocationIL(ModInfo.MODID, "textures/model/block/miniIslandOvergrowth.png")
	val mjolnir = ResourceLocationIL(ModInfo.MODID, "textures/model/item/Mjolnir.png")
	val mjolnirKitty = ResourceLocationIL(ModInfo.MODID, "textures/model/item/KittyHammer.png")
	val muspelson = if (ASJUtilities.isServer) ResourceLocation("textures/entity/steve.png") else ResourceLocationAnimated.local(ModInfo.MODID, "textures/model/entity/Muspelson.png")
	val nifleice = ResourceLocation(ModInfo.MODID, "textures/blocks/NiflheimIce.png")
	val palette = ResourceLocationIL(ModInfo.MODID, "textures/misc/pal.png")
	var petalOverlay = ResourceLocationIL("botania", "textures/gui/petalOverlay.png")
	var pixie = ResourceLocationIL("botania", "textures/model/pixie.png")
	val poolBlue = ResourceLocationIL(ModInfo.MODID, "textures/blocks/PoolBlue.png")
	val poolPink = ResourceLocationIL(ModInfo.MODID, "textures/blocks/PoolPink.png")
	val rationBelt = ResourceLocationIL(ModInfo.MODID, "textures/model/armor/rationBelt.png")
	val rollingMelon = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/WaterMelon.png")
	val rollingMelonLava = if (ASJUtilities.isServer) ResourceLocation("textures/entity/steve.png") else ResourceLocationAnimated.local(ModInfo.MODID, "textures/model/entity/LavaMelon.png")
	val rook = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Rook.png")
	val roricorn = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Roricorn.png")
	val sandstormWeather = ResourceLocationIL(ModInfo.MODID, "textures/environment/sandstorm.png")
	val skin = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/AlexSocol.png")
	val slash = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/slash.png")
	val sleipnir = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Sleipnir.png")
	val snow = ResourceLocation("textures/blocks/snow.png")
	val snowKatana = ResourceLocationIL(ModInfo.MODID, "textures/model/item/Katana.png")
	val snowSword = ResourceLocationIL(ModInfo.MODID, "textures/model/item/SnowKatana.png")
	val spearSubspace = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/subspace/spearsubspace.png")
	val spellFrame = ResourceLocationIL(ModInfo.MODID, "textures/gui/spellframe.png")
	val spellFrameEpic = ResourceLocationIL(ModInfo.MODID, "textures/gui/spellframeepic.png")
	val spellRace = ResourceLocationIL(ModInfo.MODID, "textures/gui/SpellRace.png")
	val spire = ResourceLocationIL(ModInfo.MODID, "textures/model/block/Spire.png")
	val spireRunes = ResourceLocationIL(ModInfo.MODID, "textures/model/block/SpireRunes.png")
	var spreader = ResourceLocationIL("botania", "textures/model/spreader.png")
	val sprite = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/SnowSprite.png")
	val subspace = if (ASJUtilities.isServer) ResourceLocation("textures/entity/steve.png") else ResourceLocationAnimated.local(ModInfo.MODID, "textures/model/entity/subspace/subspace.png")
	val suffuserOverlay = ResourceLocation(ModInfo.MODID, "textures/gui/SuffuserOverlay.png")
	val surtr = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Surtr.png")
	val surtrGlow = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Surtr_glow.png")
	val surtrSword = ResourceLocationIL(ModInfo.MODID, "textures/model/item/SurtrSword.png")
	val surtrSwordGlow = ResourceLocationIL(ModInfo.MODID, "textures/model/item/SurtrSword_glow.png")
	val target = ResourceLocationIL(ModInfo.MODID, "textures/misc/target.png")
	val targetq = ResourceLocationIL(ModInfo.MODID, "textures/misc/targetq.png")
	val tradePortalOverlay = ResourceLocationIL(ModInfo.MODID, "textures/gui/TradePortalOverlay.png")
	val thrym = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/Thrym.png")
	val thrymAxe = ResourceLocationIL(ModInfo.MODID, "textures/model/item/ThrymAxe.png")
	val uberSpreader = ResourceLocationIL(ModInfo.MODID, "textures/model/block/uberSpreader.png")
	val uberSpreaderFrame = ResourceLocationIL(ModInfo.MODID, "textures/model/block/uberSpreaderFrame.png")
	val uberSpreaderGolden = ResourceLocationIL(ModInfo.MODID, "textures/model/block/uberSpreaderGolden.png")
	val uberSpreaderHalloween = ResourceLocationIL(ModInfo.MODID, "textures/model/block/uberSpreaderHalloween.png")
	val uberSpreaderHalloweenGolden = ResourceLocationIL(ModInfo.MODID, "textures/model/block/uberSpreaderGoldenHalloween.png")
	val wind = ResourceLocationIL(ModInfo.MODID, "textures/model/entity/wind.png")
	val yggFlower = ResourceLocationIL(ModInfo.MODID, "textures/model/block/YggFlower.png")
	val yordinPylon = ResourceLocationIL(ModInfo.MODID, "textures/model/block/ElvenPylon.png")
	val yordinPylonOld = ResourceLocationIL(ModInfo.MODID, "textures/model/block/ElvenPylonOld.png")
	
	val obelisk = Array(5) {
		ResourceLocationIL(ModInfo.MODID, "textures/model/block/Obelisc$it.png")
	}
	
	var godCloak = emptyArray<ResourceLocationIL>()
	var godCloakGlow = emptyArray<ResourceLocationIL>()
	
	val wings = arrayOf(
		null,
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/wings/SALAMANDER_wing.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/wings/SYLPH_wing.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/wings/CAITSITH_wing.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/wings/POOKA_wing.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/wings/GNOME_wing.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/wings/LEPRECHAUN_wing.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/wings/SPRIGGAN_wing.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/wings/UNDINE_wing.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/wings/IMP_wing.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/wings/ALV_wing.png")
	)
	
	const val MOB = 11
	const val NPC = 12
	const val BOSS = 13
	
	val icons = arrayOf(
		ResourceLocationIL(ModInfo.MODID, "textures/misc/icons/HUMAN.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/misc/icons/SALAMANDER.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/misc/icons/SYLPH.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/misc/icons/CAITSITH.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/misc/icons/POOKA.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/misc/icons/GNOME.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/misc/icons/LEPRECHAUN.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/misc/icons/SPRIGGAN.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/misc/icons/UNDINE.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/misc/icons/IMP.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/misc/icons/ALV.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/misc/icons/MOB.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/misc/icons/NPC.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/misc/icons/BOSS.png")
	)
	
	val affinities = arrayOf(
		ResourceLocation("Omg dat's weird"),
		ResourceLocationIL(ModInfo.MODID, "textures/gui/spells/affinities/SALAMANDER.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/gui/spells/affinities/SYLPH.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/gui/spells/affinities/CAITSITH.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/gui/spells/affinities/POOKA.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/gui/spells/affinities/GNOME.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/gui/spells/affinities/LEPRECHAUN.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/gui/spells/affinities/SPRIGGAN.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/gui/spells/affinities/UNDINE.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/gui/spells/affinities/IMP.png")
	)
	
	val male = arrayOf(
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/Salamander.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/Sylph.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/CaitSith.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/Pooka.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/Gnome.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/Leprechaun.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/Spriggan.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/Undine.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/Imp.png")
	)
	
	val female = arrayOf(
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/Salamander.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/Sylph.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/CaitSith.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/Pooka.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/Gnome.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/Leprechaun.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/Spriggan.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/Undine.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/Imp.png")
	)
	
	val oldMale = arrayOf(
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/OldSalamander.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/OldSylph.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/OldCaitSith.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/OldPooka.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/OldGnome.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/OldLeprechaun.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/OldSpriggan.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/OldUndine.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/male/OldImp.png")
	)
	
	val oldFemale = arrayOf(
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/OldSalamander.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/OldSylph.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/OldCaitSith.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/OldPooka.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/OldGnome.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/OldLeprechaun.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/OldSpriggan.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/OldUndine.png"),
		ResourceLocationIL(ModInfo.MODID, "textures/model/entity/female/OldImp.png")
	)
	
	private val spells = HashMap<String, ResourceLocationIL>()
	
	val inventory = ResourceLocationIL("textures/gui/container/inventory.png")
	val widgets = ResourceLocationIL("textures/gui/widgets.png")
	
	fun add(name: String) =
		spells.put(name, ResourceLocationIL(ModInfo.MODID, "textures/gui/spells/$name.png"))
	
	fun spell(name: String) =
		spells[name] ?: affinities[0]
	
	private val potions = HashMap<Int, ResourceLocationIL>()
	
	fun potions(set: Int): ResourceLocationIL {
		return potions.computeIfAbsent(set) {
			ResourceLocationIL(ModInfo.MODID, "textures/gui/Potions$set.png")
		}
	}
	
	// WTF
	class ResourceLocationIL: ResourceLocation {
		
		constructor(name: String): super(name)
		constructor(modid: String, name: String): super(modid, name)
		
		init {
			if (ASJUtilities.isClient) init(this)
		}
		
		companion object {
			
			val initLater = HashSet<ResourceLocationIL>()
			
			fun init(rl: ResourceLocationIL) {
				if (ModInfo.OBF) return
				
				if (mc.renderEngine == null) {
					initLater.add(rl)
					return
				}
				
				mc.renderEngine.bindTexture(rl)
				if (initLater.isEmpty()) return
				initLater.forEach(mc.renderEngine::bindTexture)
				initLater.clear()
			}
		}
	}
}