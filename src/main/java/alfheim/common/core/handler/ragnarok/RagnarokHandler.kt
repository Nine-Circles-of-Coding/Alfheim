package alfheim.common.core.handler.ragnarok

import Reika.ChromatiCraft.Registry.ExtraChromaIDs
import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.patcher.event.*
import alfheim.AlfheimCore
import alfheim.api.*
import alfheim.api.entity.*
import alfheim.common.block.*
import alfheim.common.block.tile.TileRealityAnchor
import alfheim.common.block.tile.sub.flower.SubTileBudOfYggdrasil
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem
import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem.Knowledge
import alfheim.common.entity.*
import alfheim.common.entity.boss.*
import alfheim.common.entity.boss.primal.EntityPrimalBoss
import alfheim.common.item.AlfheimItems
import alfheim.common.item.equipment.bauble.*
import alfheim.common.item.equipment.bauble.ItemPendant.Companion.EnumPrimalWorldType.NIFLHEIM
import alfheim.common.item.equipment.bauble.faith.ItemRagnarokEmblem
import alfheim.common.item.equipment.tool.ItemSoulSword
import alfheim.common.lexicon.AlfheimLexiconData
import alfheim.common.network.*
import alfheim.common.potion.PotionEternity
import alfheim.common.world.dim.alfheim.biome.BiomeAlfheim
import alfheim.common.world.dim.domains.gen.*
import baubles.common.lib.PlayerHandler
import com.rwtema.extrautils.ExtraUtils
import com.teammetallurgy.atum.handler.AtumConfig
import cpw.mods.fml.common.Loader
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.common.gameevent.*
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.entity.effect.EntityWeatherEffect
import net.minecraft.entity.monster.*
import net.minecraft.entity.passive.EntityWolf
import net.minecraft.entity.player.*
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.potion.Potion
import net.minecraft.server.MinecraftServer
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.StatCollector
import net.minecraft.world.*
import net.minecraft.world.storage.DerivedWorldInfo
import net.minecraftforge.client.event.EntityViewRenderEvent
import net.minecraftforge.common.IPlantable
import net.minecraftforge.event.entity.living.*
import org.lwjgl.opengl.GL11.*
import twilightforest.TwilightForestMod
import vazkii.botania.common.Botania
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.block.tile.TileSpecialFlower
import java.io.*
import java.util.*
import kotlin.math.*
import com.gildedgames.the_aether.AetherConfig as AetherIConfig
import ec3.utils.cfg.Config as EC3Config
import erebus.core.handler.configs.ConfigHandler as ErebusConfig
import net.aetherteam.aether.AetherConfig as AetherIIConfig
import thaumcraft.common.config.Config as ThaumcraftConfig
import thebetweenlands.utils.confighandler.ConfigHandler as BetweenlandsConfig

@Suppress("UNUSED_PARAMETER")
object RagnarokHandler {
	
	var canKillThrym = false
	
	var thrymFirstTime = true
	var surtrFirstTime = true
	
	var winter = false
		set(value) {
			BiomeAlfheim.alfheimBiomes.forEach { it.temperature = if (value) 0f else 0.5f }
			field = value
		}
	var winterTicks = 0
	val MAX_WINTER_TICKS = 20 * 60 * 60 * 24 * if (AlfheimConfigHandler.longSeasons) 3 else 1
	
	var summer = false
		set(value) {
			BlockSnowGrass.meltDelay = if (value) 1 else 20
			field = value
		}
	var summerTicks = 0
	val MAX_SUMMER_TICKS = 20 * 60 * 60 * 24 * if (AlfheimConfigHandler.longSeasons) 3 else 1
	
	var ragnarok = false
	var ragnarokTicks = 0
	
	var noSunAndMoon = false
	
	var ginnungagap = false
	
	var finished = false
	
	var fogFade = 1f
	
	var blizzards = HashSet<BlizzardData>()
	
	var blockedPowers = BooleanArray(6)
	
	init {
		eventForge()
		eventFML()
		
		RagnarokEmblemCraftHandler.eventForge()
		
		SurtrDomain
		ThrymDomain
		FenrirDomain
	}
	
	fun defaultData() {
		canKillThrym = false
		thrymFirstTime = true
		surtrFirstTime = true
		winter = false
		winterTicks = 0
		summer = false
		summerTicks = 0
		ragnarok = false
		ragnarokTicks = 0
		noSunAndMoon = false
		ginnungagap = false
		finished = false
		blockedPowers = BooleanArray(6)
	}
	
	@SubscribeEvent
	fun load(e: ServerStartedEvent) {
		val file = File("${AlfheimCore.save}/data/${ModInfo.MODID}/Ragnarok.sys")
		if (!file.exists()) {
			ASJUtilities.log("Ragnarok data file not found. Using default values...")
			
			defaultData()
			
			return
		}
		
		try {
			ObjectInputStream(FileInputStream(file)).use { oin ->
				canKillThrym = oin.readObject() as Boolean
				thrymFirstTime = oin.readObject() as Boolean
				surtrFirstTime = oin.readObject() as Boolean
				winter = oin.readObject() as Boolean
				winterTicks = oin.readObject() as Int
				summer = oin.readObject() as Boolean
				summerTicks = oin.readObject() as Int
				ragnarok = oin.readObject() as Boolean
				ragnarokTicks = oin.readObject() as Int
				noSunAndMoon = oin.readObject() as Boolean
				ginnungagap = oin.readObject() as Boolean
				finished = oin.readObject() as Boolean
				blockedPowers = oin.readObject() as BooleanArray
			}
		} catch (e: Throwable) {
			ASJUtilities.error("Unable to read whole Ragnarok data. Using default values...")
			e.printStackTrace()
			defaultData()
		}
	}
	
	@SubscribeEvent
	fun tick(e: TickEvent.ServerTickEvent) {
		val server = MinecraftServer.getServer()
		
		if (e.phase == TickEvent.Phase.END) {
			if (server.tickCounter % 900 == 0) save()
			
			return
		}
		
		server.configurationManager.playerEntityList.forEach { player -> player as EntityPlayerMP
			if (winter && KnowledgeSystem.know(player, Knowledge.NIFLHEIM))
				KnowledgeSystem.learn(player, Knowledge.NIFLHEIM_POST, AlfheimLexiconData.abyss)
			
			if ((summer || ragnarok) && KnowledgeSystem.know(player, Knowledge.NIFLHEIM_POST))
				KnowledgeSystem.learn(player, Knowledge.MUSPELHEIM, AlfheimLexiconData.abyss)
			
			if (!summer && ragnarok && KnowledgeSystem.know(player, Knowledge.MUSPELHEIM))
				KnowledgeSystem.learn(player, Knowledge.MUSPELHEIM_POST, AlfheimLexiconData.abyss)
		}
		
		if (winter) ++winterTicks
		
		if (canStartRagnarokAndSummer()) {
			startRagnarokAndSummer()
			
			AlfheimCore.network.sendToAll(Message1d(Message1d.M1d.NOSUNMOON, 1.0))
			AlfheimCore.network.sendToAll(Message1d(Message1d.M1d.RAGNAROK, 0.999))
			
			server.configurationManager.playerEntityList.forEach { player -> player as EntityPlayerMP
				ASJUtilities.say(player, "alfheimmisc.ragnarok.start", EnumChatFormatting.DARK_RED)
				player.playSoundAtEntity("${ModInfo.MODID}:surtr.laugh", 1f, 1f)
			}
			
			blizzards.iterator().onEach {
				AlfheimCore.network.sendToAll(MessageNI(MessageNI.Mni.BLIZZARD, -it.id))
				remove()
			}
		}
		
		if (summer) ++summerTicks
		
		if (ragnarok) run ragnarok@ {
			++ragnarokTicks
			
			if (ragnarokTicks % 12000 == 0 && canEndSummer()) {
				server.configurationManager.playerEntityList.forEach {
					val emblem = ItemPriestEmblem.getEmblem(-1, it as EntityPlayer) ?: return@forEach
					val color = when (emblem.meta) {
						0 -> EnumChatFormatting.YELLOW
						1 -> EnumChatFormatting.GREEN
						2 -> EnumChatFormatting.BLUE
						3 -> EnumChatFormatting.DARK_RED
						4 -> EnumChatFormatting.GOLD
						5 -> EnumChatFormatting.GRAY
						else -> EnumChatFormatting.RESET
					}
					ASJUtilities.say(it, "alfheimmisc.ragnarok.requestsurtrkill", color)
				}
			}
			
			if (summer || ragnarokTicks % 24000 != 6000) return@ragnarok
			
			server.configurationManager.playerEntityList.forEach { player -> player as EntityPlayerMP
				if (player.dimension == AlfheimConfigHandler.dimensionIDDomains || player.dimension == AlfheimConfigHandler.dimensionIDHelheim) return@forEach
				
				if (!KnowledgeSystem.know(player, Knowledge.MUSPELHEIM_POST)) return@forEach
				
				val emblem = ItemRagnarokEmblem.getEmblem(player) ?: return@forEach
				val arr = ItemNBTHelper.getByteArray(emblem, ItemRagnarokEmblem.TAG_CONSUMED, ByteArray(6)).mapIndexed { id, it -> if (it > 0) null else id }.filterNotNull()
				
				val missing = if (arr.size == 6) 4 else arr.random(player.rng) ?: return@forEach
				
				val suitablePlayer = if (!server.isPVPEnabled) null else server.configurationManager.playerEntityList.firstOrNull {
					ItemPriestEmblem.getEmblem(missing, it as EntityPlayer) != null && !it.entityData.getBoolean(TAG_ALREADY_TP)
				} as? EntityPlayer
				
				if (server.isSinglePlayer || suitablePlayer == null) {
					EntityElf(player.worldObj).setPriest(missing).apply {
						val (x, _, z) = Vector3().rand().normalize().mul(ASJUtilities.randInBounds(4, 8, player.rng)).add(player).mf()
						val y = player.worldObj.getTopSolidOrLiquidBlock(x, z) + 1
						setPosition(x.D, y.D, z.D)
						customNameTag = StatCollector.translateToLocal("entity.alfheim.priest$missing.name")
						spawn()
					}
				} else {
					ASJUtilities.say(suitablePlayer, "alfheimmisc.ragnarok.god${missing}sentyou")
					
					suitablePlayer.apply {
						entityData.setBoolean(TAG_ALREADY_TP, true)
						val (x, _, z) = Vector3().rand().normalize().mul(ASJUtilities.randInBounds(32, 64, player.rng)).add(player).mf()
						val y = player.worldObj.getTopSolidOrLiquidBlock(x, z) + 1
						
						ASJUtilities.sendToDimensionWithoutPortal(this, player.dimension, x.D, y.D, z.D)
					}
				}
				
				ASJUtilities.say(player, "alfheimmisc.ragnarok.god${missing}huntyou")
			}
			
			server.configurationManager.playerEntityList.forEach { (it as EntityPlayer).entityData.removeTag(TAG_ALREADY_TP) }
		}
	}
	
	const val TAG_ALREADY_TP = "${ModInfo.MODID}RagnarokTeleported"
	
	@SubscribeEvent
	fun save(e: ServerStoppingEvent) {
		save()
	}
	
	fun save() {
		try {
			ObjectOutputStream(FileOutputStream("${AlfheimCore.save}/data/${ModInfo.MODID}/Ragnarok.sys")).use { oos ->
				oos.writeObject(canKillThrym)
				oos.writeObject(thrymFirstTime)
				oos.writeObject(surtrFirstTime)
				oos.writeObject(winter)
				oos.writeObject(winterTicks)
				oos.writeObject(summer)
				oos.writeObject(summerTicks)
				oos.writeObject(ragnarok)
				oos.writeObject(ragnarokTicks)
				oos.writeObject(noSunAndMoon)
				oos.writeObject(ginnungagap)
				oos.writeObject(finished)
				oos.writeObject(blockedPowers)
			}
		} catch (e: Throwable) {
			ASJUtilities.error("Unable to save whole Ragnarok data. Discarding. Sorry :(")
			e.printStackTrace()
		}
	}
	
	fun allowThrymKill() {
		val prev = canKillThrym
		canKillThrym = true
		
		if (!prev) save()
	}
	
	fun canStartWinter() = canKillThrym && !finished && !winter && !summer && !ragnarok && !ginnungagap // for the sake of completeness

	fun startWinter() {
		winter = true
		
		save()
	}

	fun canStartRagnarokAndSummer() = !finished && !summer && !ragnarok && winter && winterTicks >= MAX_WINTER_TICKS

	fun startRagnarokAndSummer() {
		winter = false
		winterTicks = 0

		summer = true
		ragnarok = true
		noSunAndMoon = true

		save()
	}

	fun canEndSummer() = summer && summerTicks >= MAX_SUMMER_TICKS

	fun endSummer() {
		summer = false
		summerTicks = 0

		save()
	}

	fun canStartGinnungagap() = !finished && ragnarok

	fun startGinnungagap() {
		ginnungagap = true
		
		if (ASJUtilities.isServer) AlfheimCore.network.sendToAll(Message1d(Message1d.M1d.GINNUNGAGAP, 1.0))
		
		save()
	}

	fun canEndRagnarok(): Boolean {
		return !summer && !ginnungagap && !noSunAndMoon && ragnarok
	}

	fun endRagnarok() {
		ragnarok = false
		ragnarokTicks = 0
		
		finished = true
		blockedPowers = BooleanArray(6)
		
		if (ASJUtilities.isServer) AlfheimCore.network.sendToAll(Message1d(Message1d.M1d.RAGNAROK, -1.0))

		save()
	}

	fun canBringBackSunAndMoon() = !winter && !summer && !ginnungagap && noSunAndMoon

	fun bringBackSunAndMoon() {
		noSunAndMoon = false

		if (ASJUtilities.isServer) AlfheimCore.network.sendToAll(Message1d(Message1d.M1d.NOSUNMOON, 0.0))
		
		if (canEndRagnarok()) endRagnarok()
		
		save()
	}

	fun endGinnungagap() {
		ginnungagap = false
		
		if (ASJUtilities.isServer) AlfheimCore.network.sendToAll(Message1d(Message1d.M1d.GINNUNGAGAP, 0.0))
		
		save()
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST) // let it be canceled
	fun consumeEmblems(e: LivingDeathEvent) {
		val killer = e.source.entity as? EntityLivingBase ?: return
		val target = e.entityLiving
		
		consumePriestEmblem(killer, target, e.source !is ItemSoulSword.DamageSourceSoulSword)
		endRagnarokIfNoRagnars(killer, target)
	}
	
	fun consumePriestEmblem(ragnar: EntityLivingBase, priest: EntityLivingBase, cantConsume: Boolean) {
		if (!ragnarok || summer) return
		if (ragnar !is EntityPlayerMP || !KnowledgeSystem.know(ragnar, Knowledge.MUSPELHEIM_POST)) return
		if (ragnar.heldItem?.item !== AlfheimItems.soulSword) return
		if (priest !is EntityPlayer && priest !is EntityElf) return
		
		val emblemDark = ItemRagnarokEmblem.getEmblem(ragnar) ?: return
		val arr = ItemNBTHelper.getByteArray(emblemDark, ItemRagnarokEmblem.TAG_CONSUMED, ByteArray(6))
		
		val powerToBlock: Int
		
		if (priest is EntityPlayer) {
			val emblemLight = ItemPriestEmblem.getEmblem(-1, priest) ?: return
			
			when (emblemLight.item) {
				AlfheimItems.priestEmblem -> {
					priest.timesDied++
					if (cantConsume) return
					
					powerToBlock = emblemLight.meta
					arr[emblemLight.meta] = 1
				}
				
				AlfheimItems.aesirEmblem  -> {
					priest.timesDied++
					if (cantConsume) return
					
					val id = arr.indexOfFirst { it < 1 }
					if (id == -1) return
					
					arr[id] = 1
					powerToBlock = id
				}
				
				else                      -> return
			}
			
			PlayerHandler.getPlayerBaubles(priest)[0] = null
		} else if (priest is EntityElf && priest.job == EntityElf.EnumElfJob.PRIEST) {
			if (priest.entityData.getBoolean(ItemSoulSword.TAG_WONT_DROP_SOUL) || cantConsume) return
			
			val meta = priest.jobSubrole
			if (meta == -1 || arr[meta] > 0) return
			arr[meta] = 1
			powerToBlock = meta
		} else {
			return
		}
		
		ItemNBTHelper.setByteArray(emblemDark, ItemRagnarokEmblem.TAG_CONSUMED, arr)
		if (powerToBlock in blockedPowers.indices) {
			blockedPowers[powerToBlock] = true
			save()
		}
		
		if (arr.all { it > 0 }) {
			if (canStartGinnungagap()) {
				ragnar.playSoundAtEntity("mob.enderdragon.growl", 10f, 0.1f)
				startGinnungagap()
			}
		} else {
			ragnar.playSoundAtEntity("mob.enderdragon.growl", 10f, 0.1f)
		}
	}
	
	fun endRagnarokIfNoRagnars(killer: EntityLivingBase, ragnar: EntityLivingBase) {
		if (ragnar !is EntityPlayer) return
		ItemRagnarokEmblem.getEmblem(ragnar) ?: return
		
		if (!(killer is EntityPlayer && ItemPriestEmblem.getEmblem(-1, killer) != null || killer is EntityPrimalBoss || killer is EntityFenrir || killer is EntityElf && killer.job == EntityElf.EnumElfJob.PRIEST)) return
		if (!(winter || ragnarok)) return
		
		if (ragnar.timesDied++ < 5) return
		if (!canEndRagnarok()) return
		
		if (MinecraftServer.getServer().configurationManager.playerEntityList.none { ItemRagnarokEmblem.getEmblem(it as EntityPlayer) != null })
			endRagnarok()
	}
	
	// technical stuff
	
	val winterIDs = mutableListOf(
		"alfheim.DedMoroz" to 1,
		"alfheim.SnowSprite" to 4,
		"Blizz" to 3,
		"AWWayofTime.IceDemon" to 2,
		"GrimoireOfGaia.Yeti" to 2,
		"GrimoireOfGaia.Yuki-Onna" to 2,
		"arcticmobs.reiver" to 2,
		"arcticmobs.wendigo" to 1,
		"arcticmobs.arix" to 2,
		"arcticmobs.serpix" to 1,
		"arcticmobs.yeti" to 2,
		"arcticmobs.frostweaver" to 3,
	                       )
	
	val summerIDs = mutableListOf(
		"Blaze" to 3,
		"LavaSlime" to 3,
		"alfheim.Muspelson" to 1,
		"arsmagica2.MobFireElemental" to 2,
		"Automagy.WispNether" to 3,
		"AWWayofTime.FireElemental" to 2,
		"AWWayofTime.WingedFireDemon" to 1,
		"demonmobs.cacodemon" to 2,
		"demonmobs.pinky" to 3,
		"demonmobs.nethersoul" to 4,
		"demonmobs.behemoth" to 1,
		"demonmobs.belph" to 3,
//		"demonmobs.rahovart" to 1, // TOO BAD
		"infernomobs.cinder" to 2,
		"infernomobs.khalk" to 1,
		"infernomobs.lobber" to 1,
		"GrimoireOfGaia.Baphomet" to 2,
		"GrimoireOfGaia.Succubus" to 2,
		"HardcoreEnderExpansion.FireGolem" to 3,
		"LavaMonsters.LavaMonster" to 4,
		"MoCreatures.HellRat" to 4,
		"MoCreatures.FlameWraith" to 3,
		"Natura.FlameSpider" to 2,
		"Natura.FlameSpiderBaby" to 4,
		"Natura.Imp" to 2,
		"primitivemobs.BlazingJuggernaut" to 3,
		"Thaumcraft.Firebat" to 3,
		"witchery.demon" to 1,
		"witchery.hellhound" to 4,
		"witchery.imp" to 3,
	                       )
	
	@SubscribeEvent
	fun spawnSeasonalMobs(e: LivingEvent.LivingUpdateEvent) {
		val player = e.entityLiving as? EntityPlayer ?: return
		val world = player.worldObj
		
		if (world.isRemote || world.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim) return
		if (!world.gameRules.getGameRuleBooleanValue("doMobSpawning")) return
		
		val rand = world.rand
		if (rand.nextInt(600) != 0) return
		
		if (winter)
			trySpawn(world, winterIDs, player.posX, player.posZ)
		
		if (summer)
			trySpawn(world, summerIDs, player.posX, player.posZ)
	}
	
	fun trySpawn(world: World, idList: MutableList<Pair<String, Int>>, px: Double, pz: Double) {
		val x = px + ASJUtilities.randInBounds(-64, 64, world.rand)
		val z = pz + ASJUtilities.randInBounds(-64, 64, world.rand)
		
		while (idList.isNotEmpty()) {
			val (id, max) = idList.random() ?: continue
			
			if (id !in EntityList.stringToClassMapping) {
				idList.removeAll { it.first == id }
				continue
			}
			
			if (id == "alfheim.DedMoroz" && ASJUtilities.chance(90)) return
			
			var count = ASJUtilities.randInBounds(1, max, world.rand)
			while (count-- > 0) {
				val entity = EntityList.createEntityByName(id, world) ?: continue
				
				val i = x + ASJUtilities.randInBounds(-4, 4, world.rand)
				val k = z + ASJUtilities.randInBounds(-4, 4, world.rand)
				val j = world.getTopSolidOrLiquidBlock(i.I, k.I).D
				
				entity.setPosition(i, j, k)
				
				if (entity is EntityWeatherEffect)
					world.addWeatherEffect(entity)
				else
					entity.spawn()
			}
			
			return
		}
	}
	
	@SubscribeEvent
	fun doNotBetrayTheEnd(e: LivingDeathEvent) {
		val victim = e.entityLiving
		if (!(victim is EntityFenrir || victim is EntityDedMoroz || victim is EntityMuspelson)) return
		if (victim.worldObj.isRemote) return
		
		val killer = e.source.entity as? EntityPlayer ?: return
		if (killer.isPotionActive(Potion.invisibility)) return
		if (ItemRagnarokEmblem.getEmblem(killer) == null) return
		
		val world = killer.worldObj
		
		when (victim) {
			is EntityFenrir     -> {
				for (i in 0..ASJUtilities.randInBounds(12, 18, killer.rng))
					EntityWolf(world).apply {
						setPosition(victim)
						// not sure, which one so both
						attackTarget = killer
						setRevengeTarget(killer)
						getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = ASJUtilities.randInBounds(26, 38, killer.rng).D
					}.spawn()
			}
			
			is EntityDedMoroz   -> {
				if (!(winter || summer || ragnarok) || victim.worldObj.provider.dimensionId == AlfheimConfigHandler.dimensionIDDomains) return
				
				for (i in 0..ASJUtilities.randInBounds(3, 5, killer.rng))
					EntityDedMoroz(world).apply {
						setPosition(victim)
						noLoot = true
						// not sure which one so both
						attackTarget = killer
						setRevengeTarget(killer)
						getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = ASJUtilities.randInBounds(420, 480, killer.rng).D
					}.spawn()
			}
			
			is EntityMuspelson -> {
				if (!(summer || ragnarok)) return
				for (i in 0..ASJUtilities.randInBounds(10, 16, killer.rng))
					when (killer.rng.nextInt(5)) {
						in 1..2 -> EntityBlaze(world)
						in 3..4 -> EntityMagmaCube(world)
						else    -> EntityMuspelson(world)
					}.apply {
						if (this is EntityMuspelson) noLoot = true
						
						setPosition(victim)
						// not sure which one so both
						attackTarget = killer
						setRevengeTarget(killer)
						
						if (this is EntityMagmaCube) slimeSize = killer.rng.nextInt(3) + 2
						
						val baseHealth = if (this is EntityMuspelson) 60 else 24
						getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = ASJUtilities.randInBounds(baseHealth, (baseHealth * 1.5).I, killer.rng).D
					}.spawn()
			}
		}
	}
	
	@SubscribeEvent
	fun controlWeather(e: TickEvent.WorldTickEvent) {
		if (e.world.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim) return
		
		val time = if (winter) 0 else if (summer || ragnarok) Int.MAX_VALUE else return
		
		var info = e.world.worldInfo
		if (info is DerivedWorldInfo) info = info.theWorldInfo
		
		e.world.prevRainingStrength = (if (time == 0) 1f else 0f)
		e.world.rainingStrength = e.world.prevRainingStrength
		info.rainTime = time
		info.isRaining = time == 0
		
		// no thunder though
		e.world.prevThunderingStrength = 0f
		e.world.thunderingStrength = 0f
		info.thunderTime = 0
		info.isThundering = false
		
		AlfheimCore.network.sendToDimension(Message3d(Message3d.M3d.WEATHER, if (time == 0) 1.0 else 0.0, time.D, time.D), e.world.provider.dimensionId)
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun doAshParticles(e: TickEvent.ClientTickEvent) {
		if (mc.theWorld == null || mc.theWorld.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim || mc.thePlayer == null || !summer) return

		val (x, y, z) = Vector3.fromEntity(mc.thePlayer ?: return).mf()
		val b0 = 16

		for (l in 0..999) {
			val i = x + mc.theWorld.rand.nextInt(b0) - mc.theWorld.rand.nextInt(b0)
			val j = y + mc.theWorld.rand.nextInt(b0) - mc.theWorld.rand.nextInt(b0)
			val k = z + mc.theWorld.rand.nextInt(b0) - mc.theWorld.rand.nextInt(b0)

			if (blizzards.none { it.contains(i, k) }) continue
			if (mc.theWorld.getPrecipitationHeight(i, k) > j) continue
			if (!mc.theWorld.isAirBlock(i, j, k)) continue

			mc.theWorld.spawnParticle("depthsuspend", (i + mc.theWorld.rand.nextFloat()).D, (j + mc.theWorld.rand.nextFloat()).D, (k + mc.theWorld.rand.nextFloat()).D, 0.0, 0.0, 0.0)
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun fogDistance(e: EntityViewRenderEvent.RenderFogEvent) {
		if (!summer || mc.theWorld.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim || !blizzards.any { it.contains(mc.thePlayer.posX.mfloor(), mc.thePlayer.posZ.mfloor()) }) return
		
		glFogi(GL_FOG_MODE, GL_EXP)
		glFogf(GL_FOG_DENSITY, 0.15f)
	}
	
	/**
	 * Contains the current Linear Congruential Generator seed for block updates.
	 * Used with an A value of 3 and a C value of 0x3c6ef35f,
	 * producing a highly planar series of values ill-suited for choosing random blocks in a 16x128x16 field.
	 */
	var updateLCG = Random().nextInt()
	
	/** Magic number, used to generate fast random numbers for 3d distribution within a chunk. */
	const val DIST_HASH_MAGIC = 0x3C6EF35F
	
	@SubscribeEvent
	fun destroyTheWorld(e: TickEvent.WorldTickEvent) {
		if (e.world.isRemote) return
		if (e.phase != TickEvent.Phase.END || e.side != Side.SERVER) return
		if (!winter && !summer && !ragnarok && !ginnungagap) return
		
		val worldEffect = WorldAffectionLevel.getWorldAffectionLevel(e.world)
		if (worldEffect == WorldAffectionLevel.NONE) return
		
		e.world.theProfiler.startSection("${ModInfo.MODID}.WorldDestruction")
		
		if (ginnungagap && e.world.rand.nextInt(AlfheimConfigHandler.cataclysmCooldown) == 0)
			doGinnungagapCataclysm(e.world)
		
		if (worldEffect == WorldAffectionLevel.GINNUNGAGAP) return
		
		for (pair in e.world.activeChunkSet) {
			if (e.world.rand.nextInt(16) != 0) continue
			
			pair as ChunkCoordIntPair
			
			val k = pair.chunkXPos * 16
			val l = pair.chunkZPos * 16
			
			e.world.theProfiler.startSection("${ModInfo.MODID}.DestroyOnTop")
			destroyTopBlocks(e, k, l)
		
//			e.world.theProfiler.endStartSection("getChunk")
//			val chunk = e.world.getChunkFromChunkCoords(pair.chunkXPos, pair.chunkZPos)
//
//			e.world.theProfiler.endStartSection("${ModInfo.MODID}DestroyEverywhere")
//			run {
//				for (ebs in chunk.blockStorageArray) {
//					ebs ?: continue
//
//					for (i3 in 0..2) {
//						updateLCG = updateLCG * 3 + DIST_HASH_MAGIC
//						val i2 = updateLCG shr 2
//						val j2 = i2 and 15
//						val k2 = i2 shr 8 and 15
//						val l2 = i2 shr 16 and 15
//						val block = ebs.getBlockByExtId(j2, l2, k2)
//
//						if (block === Blocks.water) run water@ {
//							if (freezeWater && ebs.getExtBlockMetadata(j2, l2, k2) != 0) return@water
//							e.world.setBlock(j2 + k, l2 + ebs.yLocation, k2 + l, if (freezeWater) Blocks.ice else if (summer) Blocks.water else return@water)
//						}
//
//						if (summer && block === Blocks.ice || block === Blocks.snow_layer || block === AlfheimBlocks.snowLayer)
//							e.world.setBlockToAir(j2 + k, l2 + ebs.yLocation, k2 + l)
//					}
//				}
//			}
			
			e.world.theProfiler.endSection()
		}
		
		e.world.theProfiler.endSection()
	}
	
	private enum class WorldAffectionLevel {
		NONE, GINNUNGAGAP, ALL;
		
		companion object {
			
			val aether1ID = if (Loader.isModLoaded("aether_legacy")) AetherIConfig.getAetherDimensionID() else null
			val aether2ID = if (Loader.isModLoaded("aether_legacy")) AetherIIConfig.AetherDimensionID else null
			val atumID = if (Loader.isModLoaded("atum")) AtumConfig.DIMENSION_ID else null
			val betweenlandsID = if (Loader.isModLoaded("thebetweenlands")) BetweenlandsConfig.DIMENSION_ID else null
			val chromaID = if (Loader.isModLoaded("ChromatiCraft")) ExtraChromaIDs.DIMID.value else null
			val deepDarkID = if (Loader.isModLoaded("ExtraUtilities")) ExtraUtils.underdarkDimID else null
			val erebusID = if (Loader.isModLoaded("erebus")) ErebusConfig.INSTANCE.erebusDimensionID else null
			val hoannaID = if (Loader.isModLoaded("EssentialCraftIII") || Loader.isModLoaded("essentialcraft")) EC3Config.dimensionID else null
			val outerLandsID = if (Botania.thaumcraftLoaded) ThaumcraftConfig.dimensionOuterId else null
			val twillightForestID = if (Loader.isModLoaded("TwilightForest")) TwilightForestMod.dimensionID else null
			
			fun getWorldAffectionLevel(world: World): WorldAffectionLevel {
				val dimensionId: Int?
				dimensionId = world.provider.dimensionId
				
				if (AlfheimConfigHandler.imPatheticWeakAndScaredDontTouchMyWorlds) return if (dimensionId == AlfheimConfigHandler.dimensionIDAlfheim) ALL else NONE
				
				return when (dimensionId) {
					// default
					-1 -> GINNUNGAGAP
					0 -> ALL
					1 -> NONE
					// added
					AlfheimConfigHandler.dimensionIDAlfheim -> ALL
					AlfheimConfigHandler.dimensionIDDomains -> NONE
					AlfheimConfigHandler.dimensionIDHelheim -> NONE
					AlfheimConfigHandler.dimensionIDNiflheim -> GINNUNGAGAP
					// integration
					aether1ID, aether2ID -> ALL
					atumID -> ALL
					betweenlandsID -> ALL
					chromaID -> ALL
					deepDarkID   -> GINNUNGAGAP
					erebusID     -> GINNUNGAGAP
					hoannaID    -> ALL
					outerLandsID -> NONE
					twillightForestID -> ALL
					// configured
					in AlfheimConfigHandler.worldDestroyConfig.keys -> values()[AlfheimConfigHandler.worldDestroyConfig[dimensionId]!!]
					// all other
					else -> NONE
				}
			}
		}
	}
	
	const val MANA_FOR_BLOCK = 10
	const val MANA_FOR_WHOLE_THING = 50_000
	
	fun World.isProtected(x: Int, y: Int, z: Int, wholeThing: Boolean, checkAnchor: Boolean = true): Boolean {
		if (provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim && abs(x) <= 512 && abs(z) < 512) return true
		val cost = if (wholeThing) MANA_FOR_WHOLE_THING else MANA_FOR_BLOCK
		
		if (checkAnchor) loadedTileEntityList.forEach {
			if (it !is TileRealityAnchor || Vector3.vecTileDistance(Vector3(x, y, z), it) > 343 || !it.checkStructure() || it.currentMana < cost) return@forEach
			it.recieveMana(-cost)
			return true
		}
		
		loadedTileEntityList.forEach {
			if (it !is TileSpecialFlower) return@forEach
			
			val subTile = it.subTile
			if (subTile !is SubTileBudOfYggdrasil || subTile.mana < cost || it.xCoord shr 4 != x shr 4 || it.zCoord shr 4 != z shr 4) return@forEach
			
			subTile.mana -= cost
			return true
		}
		
		return false
	}
	
	fun doGinnungagapCataclysm(world: World) {
		val pair = world.activeChunkSet.random(world.rand) as? ChunkCoordIntPair ?: return
		
		val x = pair.chunkXPos * 16 + world.rand.nextInt(15)
		val z = pair.chunkZPos * 16 + world.rand.nextInt(15)
		val y = max(64, world.getPrecipitationHeight(x, z))
		
		if (world.isProtected(x, y, z, true)) return
		
		when (world.rand.nextInt(10)) {
			0       -> if (world.loadedEntityList.count { it is EntityEarthquake } < 2) EntityEarthquake(world, x, z) else return
			in 1..2 -> {
				world.setBlock(x, y, z, AlfheimBlocks.rift)
				return
			}
			in 3..5 -> if (world.loadedEntityList.count { it is EntityFireTornado } > 3) return else {
					EntityFireTornado(world).apply {
						setPosition(x.D, y.D, z.D)
						setMotion(Math.random() * 0.2 - 0.1, 0.0, Math.random() * 0.2 - 0.1)
					}
			}
			6 -> if (world.loadedEntityList.count { EntityList.getEntityString(it as Entity) == "VoidMonster.Void Monster" } > 3) return else {
				EntityList.createEntityByName("VoidMonster.Void Monster", world)?.apply {
					setPosition(x.D, y.D, z.D)
					ASJReflectionHelper.setValue(this, true, "forceSpawn")
				} ?: return
			}
			7 -> EntityBlackBolt(world).apply { setPosition(x + 0.5, y + 0.5, z + 0.5) }
			else    -> EntityMeteor(world, x, z)
		}.spawn()
	}
	
	fun destroyTopBlocks(e: TickEvent.WorldTickEvent, k: Int, l: Int) {
		if (!winter && !summer) return
		
		updateLCG = updateLCG * 3 + DIST_HASH_MAGIC
		val r = updateLCG shr 2
		
		val world = e.world
		val x = (r and 15) + k
		val z = ((r shr 8) and 15) + l
		var y = world.getPrecipitationHeight(x, z)
		
		if (world.isProtected(x, y, z, false)) return
		
		val freezeWater = winterTicks / MAX_WINTER_TICKS.F > 2/3f
		val burnLife = summerTicks / MAX_SUMMER_TICKS.F > 1/3f
		
		var at = world.getBlock(x, y, z)
		var meta = world.getBlockMetadata(x, y, z)
		
		while (y >= 0) { // lowering Y through wood & leaves, replacing
			val air = at === Blocks.air
			val leaves = at.isLeaves(world, x, y, z)
			val wood = at.isWood(world, x, y, z)
			val water = at === Blocks.water && meta == 0
			val meltable = at === Blocks.snow_layer || at === AlfheimBlocks.snowLayer || at === Blocks.ice
			val bush = at is IPlantable
			
			if (leaves) run leaves@ {
				if (winter) {
					world.setBlockToAir(x, y, z)
					return
				}
				
				if (!summer) return@leaves
				
				val mustMeta = if (burnLife) 3 else 0
				
				if (at === AlfheimBlocks.altLeaves) {
					if (meta == 3 || meta == mustMeta) return@leaves
					
					world.setBlockMetadataWithNotify(x, y, z, mustMeta, 3)
					return
				} else {
					world.setBlock(x, y, z, AlfheimBlocks.altLeaves, mustMeta, 3)
					return
				}
			} else if (wood) run wood@ {
				if (!summer) return@wood
				
				val mustMeta = meta / 4 * 4 + if (burnLife) 3 else 0
				
				if (at === AlfheimBlocks.altWood0) {
					if (meta % 4 == 3 || meta == mustMeta) {
						return@wood
					}
					
					world.setBlockMetadataWithNotify(x, y, z, mustMeta, 3)
					return
				} else {
					world.setBlock(x, y, z, AlfheimBlocks.altWood0, mustMeta, 3)
					return
				}
			} else if (meltable && summer) run melt@ {
				if (at === Blocks.ice) {
					if (meta == 1) return@melt
					
					world.setBlock(x, y, z, Blocks.flowing_water)
				} else
					world.setBlockToAir(x, y, z)
				
				return
			} else if (water) run water@ {
				if (winter) {
					if (!freezeWater && !world.isAirBlock(x, y + 1, z)) return@water
					
					world.setBlock(x, y, z, Blocks.ice)
					return
				}
				
				if (!burnLife) return@water
				
				world.setBlockToAir(x, y, z)
				return
			} else if (bush) run bush@ {
				if (at === Blocks.tallgrass) {
					if (meta == 0) return@bush
					world.setBlockMetadataWithNotify(x, y, z, 0, 3)
					return
				}
				
				world.setBlock(x, y, z, Blocks.tallgrass)
				return
			}
			
			if (wood || air || meltable || leaves) {
				at = world.getBlock(x, --y, z)
				meta = world.getBlockMetadata(x, y, z)
			} else break
		}
		
		val flagAlt = at === ModBlocks.altGrass
		val flagSnowGrass = at === AlfheimBlocks.snowGrass
		
		if (at === Blocks.grass || flagAlt || flagSnowGrass) {
			if (winter) {
				if (flagSnowGrass) return
				
				world.setBlock(x, y, z, AlfheimBlocks.snowGrass)
				return
			}
			
			val metaMust = if (burnLife) 3 else 0
			
			if (flagAlt) {
				if (meta == 3 || meta == metaMust) return
				
				world.setBlockMetadataWithNotify(x, y, z, metaMust, 3)
				return
			}
			
			world.setBlock(x, y, z, ModBlocks.altGrass, metaMust, 3)
		}
	}
	
	const val TAG_STUN = "${ModInfo.MODID}.stun"
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	fun changeEntityTempInBlizzards(e: SheerColdHandler.SheerColdTickEvent) {
		e.entity.dimension = e.entity.worldObj.provider.dimensionId
		
		if (!winter && !summer && !ragnarok) return
		
		val target = e.entityLiving
		if (target.dimension != AlfheimConfigHandler.dimensionIDAlfheim) return
		if (winter && target is INiflheimEntity || summer && target is IMuspelheimEntity) return
		
		val (x, y, z) = Vector3.fromEntity(target).mf()
		val onAir = target.worldObj.getPrecipitationHeight(x, z) <= y
		
		val time = if (winter) winterTicks / MAX_WINTER_TICKS.F else 0.5f
		var delta = when {
			time in 1/3f..2/3f -> if (onAir) 0.05f else 0f
			2/3f < time        -> if (onAir) 10f else 0.125f
			else               -> 0f
		} * if (winter) 1 else -1
		
		if (delta == 10f) target.entityData.setBoolean(TAG_STUN, true)
		
		if (onAir) run {
			val blizzard = blizzards.firstOrNull { it.contains(x, z) } ?: return@run
			
			if (target !is EntityPlayer || !target.capabilities.isCreativeMode) {
				val (mx, mz) = Vector3(0, 0, 1).rotateOY(blizzard.rotation).mul(blizzard.speed / 10)
				target.motionX += mx / 10
				target.motionZ += mz / 10
			}
			
			delta += blizzard.speed.F / if (winter) 100 else -100
		}
		
		if (target.worldObj.loadedEntityList.any { it is EntityFireTornado && it.getBoundingBox().intersectsWith(target.boundingBox) })
			delta -= 0.25f
		
		e.delta = (e.delta ?: 0f) + delta
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	fun stunEntities(e: SheerColdHandler.SheerColdTickEvent) {
		val was: Boolean
		
		val entity = e.entityLiving
		entity.entityData.apply {
			was = getBoolean(TAG_STUN)
			removeTag(TAG_STUN)
		}
		
		if (e.isCanceled) return
		val protected = entity is EntityPlayer && (entity.capabilities.isCreativeMode || ItemPendant.canProtect(entity, NIFLHEIM, 10))
		if (was && !protected) entity.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDEternity, 1200, PotionEternity.STUN))
	}
	
	fun checkSet(e: EntityLivingBase, set: Array<ItemStack>): Boolean {
		for (i in 1..4) {
			val stack = e.getEquipmentInSlot(5 - i) ?: return false
			val item = stack.item
			if (item != set[i - 1].item)
				return false
		}
		
		return true
	}
	
	@SubscribeEvent
	fun informAboutRagnarok(e: PlayerEvent.PlayerLoggedInEvent) {
		AlfheimCore.network.sendTo(Message1d(Message1d.M1d.GINNUNGAGAP, if (ginnungagap) 1.0 else 0.0), e.player as EntityPlayerMP)
		AlfheimCore.network.sendTo(Message1d(Message1d.M1d.NOSUNMOON, if (noSunAndMoon) 1.0 else 0.0), e.player as EntityPlayerMP)
		AlfheimCore.network.sendTo(Message1d(Message1d.M1d.RAGNAROK, if (finished) -1.0 else if (ragnarok) 0.0 else 1.0), e.player as EntityPlayerMP)
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun fogColor(e: EntityViewRenderEvent.FogColors) {
		if ((mc.thePlayer.getActivePotionEffect(Potion.blindness)?.duration ?: 0) >= 20) return
		
		if (summer && mc.theWorld.provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim && blizzards.any { it.contains(mc.thePlayer.posX.mfloor(), mc.thePlayer.posZ.mfloor()) }) {
			e.red = 1f
			e.green = 0.9f
			e.blue = 0.5f
			
			return
		}
		
		if (!ragnarok) return
		
		if (e.entity.dimension == 1 || e.entity.dimension == AlfheimConfigHandler.dimensionIDHelheim || e.entity.dimension == AlfheimConfigHandler.dimensionIDDomains) return
		
		if (fogFade > 0) fogFade -= 0.001f
		
		e.red += (1f - e.red) * (1 - fogFade)
		e.green *= fogFade
		e.blue *= fogFade
	}
	
	@SubscribeEvent
	fun manageBlizzards(e: TickEvent.WorldTickEvent) {
		if ((!winter && !summer) || e.world.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim) return
		
		val iter = blizzards.iterator()
		
		for (it in iter) {
			if (--it.timeLeft <= 0) {
				AlfheimCore.network.sendToAll(MessageNI(MessageNI.Mni.BLIZZARD, -it.id))
				iter.remove()
				continue
			}
			
			if (--it.rotationTimer <= 0)
				it.rotateWind(e.world.rand)
		}
		
		val maxCount = max(e.world.playerEntities.size / 2, 1)
		var need = maxCount - blizzards.size
		if (need <= 0) return
		
		do {
			val player = e.world.playerEntities.random(e.world.rand) as? EntityPlayer ?: break
			val (cx, _, cz) = Vector3().rand().normalize().mul(64).add(player).mf()
			val blizzard = BlizzardData(cx - 128, cz - 128, cx + 128, cz + 128).setup(e.world.rand)
			blizzards.add(blizzard)
			val (x1, z1, x2, z2, id) = blizzard
			AlfheimCore.network.sendToAll(MessageNI(MessageNI.Mni.BLIZZARD, id, x1, z1, x2, z2))
		} while (--need > 0)
	}
	
	fun addMadness(player: EntityPlayerMP) {
		if (finished) return
		
		val segment = CardinalSystem.forPlayer(player)
		if (++segment.abyss < 100) return
		
		if (!KnowledgeSystem.learn(player, Knowledge.ABYSS, AlfheimLexiconData.abyss)) return
	}
	
	private const val TAG_PRIEST_DEATH_COUNT = "${ModInfo.MODID}.priestDeathCount"
	
	var EntityPlayer.timesDied
		get() = persistentData.getInteger(TAG_PRIEST_DEATH_COUNT)
		set(value) = persistentData.setInteger(TAG_PRIEST_DEATH_COUNT, value)
	
	data class BlizzardData(val x1: Int, val z1: Int, val x2: Int, val z2: Int) {
		
		operator fun component5() = id
		
		var id = nextID
			private set
		
		var speed = 0.0
		var rotation = 0f
		var rotationTimer = 0
		var timeLeft = 0
		
		fun setup(rand: Random): BlizzardData {
			timeLeft = ASJUtilities.randInBounds(1200, 6000, rand)
			
			return rotateWind(rand)
		}
		
		fun rotateWind(rand: Random): BlizzardData {
			rotationTimer = ASJUtilities.randInBounds(200, 1200, rand)
			rotation += ASJUtilities.randInBounds(-45, 45, rand)
			
			speed = rand.nextDouble() * 0.5 + 0.5
			
			return this
		}
		
		fun contains(x: Int, z: Int): Boolean {
			val (x1, z1, x2, z2) = this
			return x in x1..x2 && z in z1..z2
		}
		
		@SideOnly(Side.CLIENT)
		fun setId(newID: Int) {
			id = newID
		}
		
		companion object {
			private var nextID = 0
				get() = field++
		}
	}
}
