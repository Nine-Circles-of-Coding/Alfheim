@file:Suppress("UNCHECKED_CAST")

package alfheim.common.core.handler

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.security.InteractionSecurity
import alexsocol.patcher.event.*
import alfheim.AlfheimCore
import alfheim.api.*
import alfheim.api.entity.*
import alfheim.api.event.SpellCastEvent
import alfheim.api.event.TimeStopCheckEvent.TimeStopEntityCheckEvent
import alfheim.api.spell.*
import alfheim.api.spell.SpellBase.SpellCastResult.*
import alfheim.common.core.handler.CardinalSystem.PartySystem.Party
import alfheim.common.item.relic.ItemTankMask.Companion.limboCounter
import alfheim.common.network.*
import alfheim.common.network.packet.*
import alfheim.common.spell.tech.SpellTimeStop
import com.google.common.collect.HashMultimap
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.PlayerEvent.*
import cpw.mods.fml.common.gameevent.TickEvent
import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import net.minecraft.entity.*
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.boss.IBossDisplayData
import net.minecraft.entity.player.*
import net.minecraft.server.MinecraftServer
import net.minecraft.util.DamageSource
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.*
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.lexicon.LexiconEntry
import vazkii.botania.api.mana.*
import vazkii.botania.common.item.*
import java.io.*
import java.util.*
import kotlin.math.max

object CardinalSystem {
	
	var playerSegments = HashMap<String, PlayerSegment>()
	
	fun load(save: String) {
		val file = File("$save/data/${ModInfo.MODID}/Cardinal.sys")
		if (!file.exists()) {
			ASJUtilities.log("Cardinal System data file not found. Generating default values...")
			playerSegments = HashMap()
			TimeStopSystem.tsAreas = HashMap()
			return
		}
		
		try {
			ObjectInputStream(FileInputStream(file)).use { ois ->
				playerSegments = ois.readObject() as HashMap<String, PlayerSegment>
				TimeStopSystem.tsAreas = ois.readObject() as HashMap<Int, LinkedList<TimeStopSystem.TimeStopArea>>
			}
		} catch (e: Throwable) {
			ASJUtilities.error("Unable to read whole Cardinal System data. Generating default values...")
			e.printStackTrace()
			playerSegments = HashMap()
			TimeStopSystem.tsAreas = HashMap()
		}
	}
	
	fun transfer(player: EntityPlayerMP) {
		NetworkService.sendTo(Message1d(M1d.LIMBO, player.limboCounter.D), player)
		KnowledgeSystem.transfer(player)
		CommonSystem.loseHearts(player)
		
		if (AlfheimConfigHandler.enableElvenStory) {
			NetworkService.sendTo(Message1d(M1d.ESMABIL, if (forPlayer(player).esmAbility) 1.0 else 0.0), player)
			ElvenStoryModeSystem.transfer(player)
			
			if (AlfheimConfigHandler.enableMMO) {
				SpellCastingSystem.transfer(player)
				HotSpellsSystem.transfer(player)
				PartySystem.transfer(player)
				TimeStopSystem.transfer(player, 0)
			}
		}
	}
	
	fun save() {
		try {
			ObjectOutputStream(FileOutputStream("${AlfheimCore.save}/data/${ModInfo.MODID}/Cardinal.sys")).use { oos ->
				oos.writeObject(playerSegments)
				oos.writeObject(TimeStopSystem.tsAreas)
			}
		} catch (e: Throwable) {
			ASJUtilities.error("Unable to save whole Cardinal System data. Discarding. Sorry :(")
			e.printStackTrace()
		}
	}
	
	fun ensureExistance(player: EntityPlayer): Boolean {
		if (!playerSegments.containsKey(player.commandSenderName)) {
			playerSegments[player.commandSenderName] = PlayerSegment(player)
		}
		return true
	}
	
	fun forPlayer(player: EntityPlayer): PlayerSegment {
		if (player.worldObj.isRemote)
			throw RuntimeException("You shouldn't access this from client")
		
		ensureExistance(player)
		return playerSegments[player.commandSenderName]!!
	}
	
	@SubscribeEvent
	fun load(e: ServerStartedEvent) {
		load(AlfheimCore.save)
	}
	
	@SubscribeEvent
	fun tick(e: TickEvent.ServerTickEvent) {
		if (e.phase == TickEvent.Phase.END) {
			if (MinecraftServer.getServer().tickCounter % 900 == 0) save()
			
			return
		}
		
		if (!AlfheimConfigHandler.enableMMO) return
		
		SpellCastingSystem.tick()
		TimeStopSystem.tick()
		TargetingSystem.tick()
	}
	
	@SubscribeEvent
	fun save(e: ServerStoppingEvent) {
		save()
	}
	
	// holds methods for all other stuff
	object CommonSystem {
		
		val heartLossUUID = UUID.fromString("5F618E7B-10E1-412F-A61C-A310D8C879BA")!!
		
		init {
			eventForge().eventFML()
		}
		
		fun cantLostHearts(player: EntityPlayerMP): Boolean {
			return player.getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue <= forPlayer(player).heartLoss * 2 + 2
		}
		
		fun loseHearts(player: EntityPlayerMP, additionalLoss: Int = 0) {
			val segment = forPlayer(player)
			segment.heartLoss = max(0, segment.heartLoss + additionalLoss)
			
			updateLostHearts(player, segment.heartLoss)
			NetworkService.sendTo(MessageNI(Mni.HEARTLOSS, segment.heartLoss), player)
		}
		
		fun updateLostHearts(player: EntityPlayer, lost: Int) {
			val mm = HashMultimap.create<String, AttributeModifier>()
			mm.put(SharedMonsterAttributes.maxHealth.attributeUnlocalizedName, AttributeModifier(heartLossUUID, "Hel took your hearts", lost * -2.0, 0))
			player.getAttributeMap().applyAttributeModifiers(mm)
		}
		
		@SubscribeEvent
		fun onPlayerJoined(e: EntityJoinWorldEvent) {
			if (ASJUtilities.isServer) loseHearts(e.entity as? EntityPlayerMP ?: return)
		}
		
		@SubscribeEvent
		fun onPlayerRespawn(e: PlayerRespawnEvent) {
			if (ASJUtilities.isServer) loseHearts(e.player as? EntityPlayerMP ?: return)
		}
		
		@SubscribeEvent
		fun onPlayerDimChange(e: PlayerChangedDimensionEvent) {
			if (ASJUtilities.isServer) loseHearts(e.player as? EntityPlayerMP ?: return)
		}
		
		@SubscribeEvent
		fun onPlayerTick(e: LivingUpdateEvent) {
			val player = e.entityLiving as? EntityPlayerMP ?: return
			
			val wisdomLevel = forPlayer(player).wisdom
			if (wisdomLevel <= 0) {
				return player.removePotionEffect(AlfheimConfigHandler.potionIDWisdom)
			}
			
			if (!player.isPotionActive(AlfheimConfigHandler.potionIDWisdom))
				player.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDWisdom, Int.MAX_VALUE, wisdomLevel - 1))
			
			player.getActivePotionEffect(AlfheimConfigHandler.potionIDWisdom)?.apply {
				duration = Int.MAX_VALUE
				amplifier = wisdomLevel - 1
			}
		}
	}
	
	object KnowledgeSystem {
		
		/**
		 * @return true if successfully learned, false otherwise
		 */
		fun learn(player: EntityPlayerMP, kn: Knowledge, forceEntry: LexiconEntry? = null): Boolean {
			val seg = forPlayer(player)
			if (know(player, kn)) return false
			
			seg.knowledge.add("$kn")
			NetworkService.sendTo(Message1d(M1d.KNOWLEDGE, kn.ordinal.D), player)
			save()
			
			forceEntry ?: return true
			
			ASJUtilities.say(player, "alfheimmisc.newentry")
			
			val slot = ASJUtilities.getSlotWithItem(ModItems.lexicon, player.inventory)
			if (slot == -1) return true
			
			val lexicon = player.inventory[slot]!!
			ItemLexicon.setForcedPage(lexicon, forceEntry.getUnlocalizedName())
			
			return true
		}
		
		fun know(player: EntityPlayerMP, kn: Knowledge) = forPlayer(player).knowledge.contains("$kn")
		
		fun transfer(player: EntityPlayerMP) {
			for (kn in Knowledge.values()) if (know(player, kn)) NetworkService.sendTo(Message1d(M1d.KNOWLEDGE, kn.ordinal.D), player)
		}
		
		enum class Knowledge {
			ABYSS,
			NIFLHEIM,
			NIFLHEIM_POST,
			MUSPELHEIM,
			MUSPELHEIM_POST,
			ABYSS_TRUTH,
			
			PYLONS
		}
	}
	
	object SpellCastingSystem {
		
		fun transfer(player: EntityPlayerMP) {
			for (affinity in EnumRace.values())
				for (spell in AlfheimAPI.getSpellsFor(affinity))
					NetworkService.sendTo(Message2d(M2d.COOLDOWN, (affinity.ordinal and 0xF shl 28 or (AlfheimAPI.getSpellID(spell) and 0xFFFFFFF)).D, getCoolDown(player, spell).D), player)
		}
		
		fun setCoolDown(caster: EntityPlayer, spell: SpellBase, cd: Int): Int {
			forPlayer(caster).coolDown[spell.name] = cd
			
			if (cd > 6000) save()
			
			return cd
		}
		
		fun getCoolDown(caster: EntityPlayer, spell: SpellBase): Int {
			return try {
				forPlayer(caster).coolDown[spell.name] ?: 0
			} catch (e: Throwable) {
				ASJUtilities.error(String.format("Something went wrong getting cooldown for %s. Returning 0.", spell))
				e.printStackTrace()
				0
			}
		}
		
		fun tick() {
			try {
				playerSegments.forEach { (name, segment) ->
					for (spell in segment.coolDown.keys) {
						var time = segment.coolDown[spell] ?: 0
						if (time > 5 && MinecraftServer.getServer().configurationManager.func_152612_a(name)?.capabilities?.isCreativeMode == true) time = 5
						if (time > 0) segment.coolDown[spell] = time - 1
					}
					
					val player = MinecraftServer.getServer().configurationManager.func_152612_a(segment.userName)
					if (player != null) {
						if (segment.init > 0) --segment.init
						else {
							if (segment.ids != 0 && segment.castableSpell != null) {
								try {
									NetworkService.sendTo(Message2d(M2d.COOLDOWN, segment.ids.D, performCast(player, segment.ids shr 28 and 0xF, segment.ids and 0xFFFFFFF).D), player)
								} finally {
									segment.ids = 0
									segment.init = 0
									segment.castableSpell = null
								}
							}
						}
					} else {
						segment.ids = 0
						segment.init = 0
						segment.castableSpell = null
					}
				}
			} catch (e: Throwable) {
				ASJUtilities.error("Something went wrong ticking spells. Skipping this tick. Caused by: ${e.message}")
				e.printStackTrace()
			}
		}
		
		fun performCast(caster: EntityPlayerMP, raceID: Int, spellID: Int): Int {
			if (!AlfheimConfigHandler.enableMMO) return -NOTALLOW.ordinal
			if (caster.isPotionActive(AlfheimConfigHandler.potionIDLeftFlame)) return -NOTALLOW.ordinal
			val spell = AlfheimAPI.getSpellByIDs(raceID, spellID) ?: return -DESYNC.ordinal
			if (getCoolDown(caster, spell) > 0) return -NOTREADY.ordinal
			val result = spell.performCast(caster)
			if (result == OK) {
				// SpellBase.say(caster, spell);
				val e = SpellCastEvent.Post(spell, caster, spell.getCooldown())
				MinecraftForge.EVENT_BUS.post(e)
				return setCoolDown(caster, spell, e.cd)
			}
			return -result.ordinal
		}
		
		init {
			SpellCastingHandler.eventForge()
		}
		
		object SpellCastingHandler {
			
			@SubscribeEvent
			fun onSpellCasting(e: SpellCastEvent.Pre) {
				if (!e.caster.isEntityAlive || e.caster.isDead) {
					e.isCanceled = true
					return
				}
				
				if (e.caster.isPotionActive(AlfheimConfigHandler.potionIDLeftFlame)) {
					e.isCanceled = true
					return
				}
				
				if (TimeStopSystem.affected(e.caster)) {
					e.isCanceled = true
				}
			}
			
			@SubscribeEvent
			fun onSpellCasted(e: SpellCastEvent.Post) {
				if (ModInfo.DEV || e.caster is EntityPlayer && e.caster.capabilities.isCreativeMode) e.cd = 5
			}
		}
	}
	
	object ManaSystem {
		
		/**
		 * Sends [player]'s mana to everyone in his party
		 */
		fun handleManaChange(player: EntityPlayer) {
			PartySystem.getParty(player).sendMana(player, getMana(player))
		}
		
		fun getMana(player: EntityPlayer): Int {
			var totalMana = 0
			
			val mainInv = player.inventory
			val baublesInv = BotaniaAPI.internalHandler.getBaublesInventory(player)
			
			val invSize = mainInv.sizeInventory
			var size = invSize
			if (baublesInv != null)
				size += baublesInv.sizeInventory
			
			for (i in 0 until size) {
				val useBaubles = i >= invSize
				val inv = if (useBaubles) baublesInv else mainInv
				val stack = inv[i - if (useBaubles) invSize else 0]
				
				if (stack != null) {
					val item = stack.item
					
					if (item is ICreativeManaProvider && (item as ICreativeManaProvider).isCreative(stack)) return Integer.MAX_VALUE
					
					if (item is IManaItem)
						if (!(item as IManaItem).isNoExport(stack)) {
							if (Integer.MAX_VALUE - (item as IManaItem).getMana(stack) <= totalMana)
								return Integer.MAX_VALUE
							totalMana += (item as IManaItem).getMana(stack)
						}
				}
			}
			
			return totalMana
		}
		
		fun getMana(mr: EntityLivingBase) = (mr as? EntityPlayer)?.let { getMana(it) } ?: 0
		
		init {
			MinecraftForge.EVENT_BUS.register(ManaSyncHandler())
		}
		
		class ManaSyncHandler {
			
			@SubscribeEvent
			fun onLivingUpdate(e: LivingUpdateEvent) {
				if (AlfheimConfigHandler.enableMMO && ASJUtilities.isServer && e.entityLiving is EntityPlayer) {
					val player = e.entityLiving as EntityPlayer
					if (player.worldObj.totalWorldTime % 20 == 0L) handleManaChange(player)
				}
			}
		}
	}
	
	object TargetingSystem {
		
		fun tick() {
			for (name in playerSegments.keys) {
				val player = MinecraftServer.getServer().configurationManager.func_152612_a(name)
				
				if (player == null) {
					val s = playerSegments[name]
					s?.target = null
					s?.isParty = false
				} else {
					val tg = getTarget(player).target ?: continue
					
					if (tg.isDead || Vector3.entityDistance(player, tg) > if (tg is IBossDisplayData) 128.0 else 32.0) {
						setTarget(player, null, false, -2)
					}
				}
			}
		}
		
		/**
		 * @param partyIndex 0 - self; -1 - enemy; -2 - ignore changes
		 */
		fun setTarget(player: EntityPlayer, target: EntityLivingBase?, isParty: Boolean, partyIndex: Int = -1) {
			val c = forPlayer(player)
			c.target = target
			
			if (partyIndex != -2) {
				c.isParty = isParty
				c.partyIndex = partyIndex
			}
		}
		
		fun getTarget(player: EntityLivingBase): Target {
			if (player !is EntityPlayer) return Target(null, false, -1)
			
			val c = forPlayer(player)
			// stupid kotlin -_-
			return Target(c.target, c.isParty, c.partyIndex)
		}
		
		data class Target(val target: EntityLivingBase?, val isParty: Boolean, val partyIndex: Int)
	}
	
	object PartySystem {
		
		fun transfer(player: EntityPlayerMP) {
			NetworkService.sendTo(MessageParty(forPlayer(player).party), player)
		}
		
		fun setParty(player: EntityPlayer, party: Party) {
			forPlayer(player).party = party
			party.sendChanges()
			
			save()
		}
		
		fun getParty(player: EntityPlayer) = forPlayer(player).party
		
		fun getMobParty(living: EntityLivingBase?): Party? =
			playerSegments.values.firstOrNull { it.party.isMember(living) }?.party
		
		fun getUUIDParty(id: UUID?): Party? =
			playerSegments.values.firstOrNull { it.party.isMember(id) }?.party
		
		fun sameParty(p1: EntityPlayer, p2: EntityLivingBase?) = getParty(p1).isMember(p2)
		
		fun sameParty(id: UUID?, e: EntityLivingBase?) =
			playerSegments.values.any { it.party.isMember(id) && it.party.isMember(e) }
		
		fun mobsSameParty(e1: EntityLivingBase?, e2: EntityLivingBase?) =
			playerSegments.values.any { it.party.isMember(e1) && it.party.isMember(e2) }
		
		fun friendlyFire(entityLiving: EntityLivingBase, source: DamageSource): Boolean {
			if (!AlfheimConfigHandler.enableMMO || source.damageType.contains("_FF")) return false
			
			if (ASJUtilities.isClient) return false
			if (source.entity != null && source.entity is EntityPlayer) {
				if (getParty(source.entity as EntityPlayer).isMember(entityLiving))
					return true
			}
			if (entityLiving is EntityPlayer && source.entity != null && source.entity is EntityLivingBase) {
				if (getParty(entityLiving).isMember(source.entity as EntityLivingBase))
					return true
			}
			return source.entity != null && source.entity is EntityLivingBase && mobsSameParty(entityLiving, source.entity as EntityLivingBase)
		}
		
		@Suppress("unused") // Used in ASM class: `EntityTrackerEntry$tryStartWachingThis$MethodVisitor`
		@JvmStatic
		fun notifySpawn(e: Entity) {
			if (e is EntityLivingBase) {
				for (segment in playerSegments.values) {
					if (segment.party.isMember(e)) {
						for (i in 0 until segment.party.count) {
							if (segment.party.isPlayer(i)) {
								val mr = segment.party[i]
								if (mr is EntityPlayerMP) {
									NetworkService.sendTo(Message2d(M2d.UUID, e.getEntityId().D, segment.party.indexOf(e).D), mr)
								}
							}
						}
					}
				}
			}
		}
		
		class Party: Serializable, Cloneable {
			
			private var members: Array<Member?>
			var count: Int = 0
			
			val pl: EntityPlayer?
				get() = get(0) as EntityPlayer?
			
			constructor() {
				members = arrayOfNulls(AlfheimConfigHandler.maxPartyMembers)
			}
			
			private constructor(i: Int) {
				members = arrayOfNulls(i)
			}
			
			constructor(pl: EntityPlayer, isMemberTypeKnown: Boolean = true): this() {
				members[count++] = Member(pl.commandSenderName, pl.uniqueID, ManaSystem.getMana(pl), true, !pl.isEntityAlive, pl.health, pl.maxHealth, (if (isMemberTypeKnown) Member.MemberType.typeOf(pl) else Member.MemberType.HUMAN).ordinal)
			}
			
			operator fun get(i: Int): EntityLivingBase? {
				if (members[i]?.isPlayer == true) {
					return if (ASJUtilities.isServer) {
						MinecraftServer.getServer().configurationManager.func_152612_a(members[i]?.name)
					} else {
						mc.theWorld.getPlayerEntityByName(members[i]?.name)
					}
				}
				if (ASJUtilities.isServer) {
					for (world in MinecraftServer.getServer().worldServers) {
						for (entity in world.loadedEntityList) {
							if (entity is EntityLivingBase && entity.uniqueID == members[i]?.uuid) {
								return entity
							}
						}
					}
				} else {
					val e = mc.theWorld.getEntityByID(members[i]?.uuid?.mostSignificantBits?.I
													  ?: 0)
					return if (e is EntityLivingBase) e else null
				}
				return null
			}
			
			operator fun get(name: String) =
				(0 until count).firstOrNull { members[it]?.name == name }?.let { get(it) }
			
			fun getHealth(i: Int) = members[i]?.health ?: 0f
			
			fun getMaxHealth(i: Int) = members[i]?.maxHealth ?: 0f
			
			fun getMana(i: Int) = members[i]?.mana ?: 0
			
			fun getName(i: Int) = members[i]?.name ?: ""
			
			fun getType(i: Int) = members[i]?.type ?: 0
			
			fun isPlayer(i: Int) = members[i]?.isPlayer ?: false
			
			fun isDead(i: Int) = members[i]?.isDead ?: false
			
			fun setHealth(i: Int, health: Float) {
				val mr = members[i] ?: return
				val was = mr.health
				if (was != health) {
					mr.health = health
					if (ASJUtilities.isServer) sendHealth(i, health)
				}
			}
			
			fun setMaxHealth(i: Int, maxHealth: Float) {
				val mr = members[i] ?: return
				val was = mr.maxHealth
				if (was != maxHealth) {
					mr.maxHealth = maxHealth
					if (ASJUtilities.isServer) sendMaxHealth(i, maxHealth)
				}
			}
			
			fun setMana(i: Int, mana: Int) {
				members[i]?.mana = mana
			}
			
			fun setType(i: Int, type: Int) {
				members[i]?.type = type
				if (ASJUtilities.isServer) sendType(i, type)
			}
			
			fun indexOf(mr: EntityLivingBase?): Int {
				if (mr == null) return -1
				return (0 until count).firstOrNull {
					if (ASJUtilities.isServer)
						mr.uniqueID == members[it]?.uuid
					else
						mr.entityId.toLong() == members[it]?.uuid?.mostSignificantBits
				} ?: -1
			}
			
			fun indexOf(name: String?): Int {
				if (name.isNullOrEmpty()) return -1
				return (0 until count).firstOrNull { name == members[it]?.name } ?: -1
			}
			
			fun isMember(mr: EntityLivingBase?): Boolean {
				if (mr != null)
					for (i in 0 until count) {
						if (ASJUtilities.isServer) {
							if (mr.uniqueID == members[i]?.uuid) return true
						} else {
							if (mr.entityId.toLong() == members[i]?.uuid?.mostSignificantBits) return true
						}
						
					}
				return false
			}
			
			fun isMember(uuid: UUID?): Boolean {
				if (uuid != null)
					for (i in 0 until count) {
						if (ASJUtilities.isServer) {
							if (uuid == members[i]?.uuid) return true
						} else {
							if (uuid.mostSignificantBits == members[i]?.uuid?.mostSignificantBits) return true
						}
						
					}
				return false
			}
			
			fun setDead(i: Int, d: Boolean) {
				if (ASJUtilities.isClient) members[i]?.isDead = d
			}
			
			fun setDead(mr: EntityLivingBase, d: Boolean) {
				val i = indexOf(mr)
				if (i != -1) {
					if (mr is EntityPlayer) {
						members[i]?.isDead = d
						sendDead(i, d)
					} else if (d) {
						remove(mr)
						for (j in 0 until count) {
							if (members[j]?.isPlayer == true) {
								val e = get(j)
								if (e is EntityPlayer)
									ASJUtilities.say(e, "alfheimmisc.party.memberdied", mr.commandSenderName)
							}
						}
					}
				}
			}
			
			fun setUUID(i: Int, enID: Int) {
				if (members[i] != null) members[i]?.uuid = UUID(enID.toLong(), enID.toLong())
			}
			
			fun add(mr: EntityLivingBase?): Boolean {
				if (mr == null) return false
				if (indexOf(mr) != -1) return false
				if (count >= members.size) return false
				members[count++] = Member(mr.commandSenderName, mr.uniqueID, ManaSystem.getMana(mr), mr is EntityPlayer, !mr.isEntityAlive, mr.health, mr.maxHealth, Member.MemberType.typeOf(mr).ordinal)
				sendChanges()
				
				save()
				
				return true
			}
			
			fun remove(mr: EntityLivingBase?): Boolean {
				if (mr == null) return false
				return if (mr is EntityPlayer && members[0]?.name == mr.commandSenderName) removePL() else removeSafe(mr)
			}
			
			fun remove(name: String?): Boolean {
				if (name.isNullOrEmpty()) return false
				return if (members[0]?.name == name) removePL() else removeSafe(name)
			}
			
			private fun removePL(): Boolean {
				if (pl == null) return false
				setParty(pl!!, Party(pl!!))
				var i = 1
				while (i < count) {
					if (members[i]?.isPlayer == true) {
						members[0] = members[i]
						--count
						while (i < count) {
							members[i] = members[i + 1]
							i++
						}
						sendChanges()
					}
					i++
				}
				
				save()
				
				return true
			}
			
			private fun removeSafe(name: String?): Boolean {
				if (name.isNullOrEmpty()) return false
				val mr = get(name)
				var id = indexOf(name)
				if (mr == null && id != -1 && !isPlayer(id)) {
					--count
					while (id < count) {
						members[id] = members[id + 1]
						id++
					}
					members[count] = null
					
					sendChanges()
					
					save()
					
					return true
				}
				return removeSafe(mr)
			}
			
			private fun removeSafe(mr: EntityLivingBase?): Boolean {
				if (mr == null) return false
				var id = indexOf(mr)
				if (id == -1) return false
				--count
				if (mr is EntityPlayer)
					setParty(mr, Party(mr))
				while (id < count) {
					members[id] = members[id + 1]
					id++
				}
				members[count] = null
				
				sendChanges()
				
				save()
				
				return true
			}
			
			fun sendChanges() {
				if (ASJUtilities.isServer)
					for (i in 0 until count) {
						val e = get(i)
						if (e != null && members[i]?.isPlayer == true && e is EntityPlayerMP)
							transfer(e)
					}
			}
			
			fun sendDead(id: Int, d: Boolean) {
				for (i in 0 until count) {
					val e = get(i)
					if (e != null && members[i]?.isPlayer == true && e is EntityPlayerMP)
						NetworkService.sendTo(Message3d(M3d.PARTY_STATUS, PartyStatus.DEAD.ordinal.D, id.D, (if (d) -10 else -100).D), e)
				}
			}
			
			fun sendHealth(index: Int, health: Float) {
				for (i in 0 until count) {
					val e = get(i)
					if (e != null && members[i]?.isPlayer == true && e is EntityPlayerMP)
						NetworkService.sendTo(Message3d(M3d.PARTY_STATUS, PartyStatus.HEALTH.ordinal.D, index.D, health.D), e)
				}
			}
			
			fun sendMaxHealth(index: Int, maxHealth: Float) {
				for (i in 0 until count) {
					val e = get(i)
					if (e != null && members[i]?.isPlayer == true && e is EntityPlayerMP)
						NetworkService.sendTo(Message3d(M3d.PARTY_STATUS, PartyStatus.MAXHEALTH.ordinal.D, index.D, maxHealth.D), e)
				}
			}
			
			fun sendMana(player: EntityPlayer, mana: Int) {
				val index = indexOf(player)
				
				for (i in 0 until count) {
					val e = get(i)
					if (e != null && members[i]?.isPlayer == true && e is EntityPlayerMP)
						NetworkService.sendTo(Message3d(M3d.PARTY_STATUS, PartyStatus.MANA.ordinal.D, index.D, mana.D), e)
				}
			}
			
			fun sendType(index: Int, type: Int) {
				for (i in 0 until count) {
					val e = get(i)
					if (e != null && members[i]?.isPlayer == true && e is EntityPlayerMP)
						NetworkService.sendTo(Message3d(M3d.PARTY_STATUS, PartyStatus.TYPE.ordinal.D, index.D, type.D), e)
				}
			}
			
			enum class PartyStatus {
				DEAD, HEALTH, MAXHEALTH, MANA, TYPE
			}
			
			fun write(buf: ByteBuf) {
				buf.writeInt(members.size)
				buf.writeInt(count)
				var mr: EntityLivingBase?
				for (i in 0 until count) {
					mr = get(i)
					if (serverIO) {
						buf.writeLong(mr?.uniqueID?.mostSignificantBits ?: 0)
						buf.writeLong(mr?.uniqueID?.leastSignificantBits ?: 0)
					} else {
						buf.writeInt(mr?.entityId ?: 0)
					}
					ByteBufUtils.writeUTF8String(buf, members[i]?.name ?: "")
					buf.writeInt(members[i]?.mana ?: 0)
					buf.writeBoolean(members[i]?.isPlayer ?: false)
					buf.writeBoolean(members[i]?.isDead ?: false)
					buf.writeFloat(members[i]?.health ?: 0f)
					buf.writeFloat(members[i]?.maxHealth ?: 0f)
					buf.writeInt(members[i]?.type ?: Member.MemberType.MOB.ordinal)
				}
			}
			
			public override fun clone(): Any {
				val result = Party()
				result.members = members.clone()
				result.count = count
				return result
			}
			
			private class Member(val name: String, var uuid: UUID, var mana: Int, val isPlayer: Boolean, var isDead: Boolean, var health: Float, var maxHealth: Float, var type: Int): Serializable, Cloneable {
				
				public override fun clone() = Member(name, uuid, mana, isPlayer, isDead, health, maxHealth, type)
				
				@Suppress("unused")
				enum class MemberType {
					
					HUMAN, SALAMANDER, SYLPH, CAITSITH, POOKA, GNOME, LEPRECHAUN, SPRIGGAN, UNDINE, IMP, ALV, MOB, NPC, BOSS;
					
					companion object {
						
						fun typeOf(e: EntityLivingBase) = when (e) {
							is EntityPlayer     -> values()[e.raceID]
							is IBossDisplayData -> BOSS
							is INpc             -> NPC
							else                -> MOB
						}
					}
				}
				
				companion object {
					
					private const val serialVersionUID = 8416468367146381L
				}
			}
			
			companion object {
				
				private const val serialVersionUID = 84616843168484257L
				
				/** Flag for server's storing functions, always false */
				@Transient
				var serverIO = false
				
				fun read(buf: ByteBuf): Party {
					val size = buf.readInt()
					val count = buf.readInt()
					val pt = Party(size)
					pt.count = count
					var most: Long
					var least: Long
					for (i in 0 until count) {
						if (serverIO) {
							most = buf.readLong()
							least = buf.readLong()
						} else {
							least = buf.readInt().toLong()
							most = least
						}
						pt.members[i] = Member(ByteBufUtils.readUTF8String(buf), UUID(most, least), buf.readInt(), buf.readBoolean(), buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readInt())
					}
					return pt
				}
			}
		}
		
		init {
			MinecraftForge.EVENT_BUS.register(PartyThingsListener())
		}
		
		class PartyThingsListener {
			
			@SubscribeEvent
			fun onClonePlayer(e: PlayerEvent.Clone) {
				if (AlfheimConfigHandler.enableMMO && e.wasDeath) getParty(e.entityPlayer).setDead(e.entityPlayer, false)
			}
			
			@SubscribeEvent
			fun onPlayerRespawn(e: PlayerRespawnEvent) {
				if (AlfheimConfigHandler.enableMMO) getParty(e.player).setDead(e.player, false)
			}
		}
	}
	
	object HotSpellsSystem {
		
		fun transfer(player: EntityPlayerMP) = NetworkService.sendTo(MessageHotSpellC(forPlayer(player).hotSpells), player)
		
		fun getHotSpellID(player: EntityPlayer, slot: Int) = forPlayer(player).hotSpells[slot]
		
		fun setHotSpellID(player: EntityPlayer, slot: Int, id: Int) {
			forPlayer(player).hotSpells[slot] = id
			
			save()
		}
	}
	
	object TimeStopSystem {
		
		var tsAreas = HashMap<Int, LinkedList<TimeStopArea>>()
		
		fun transfer(player: EntityPlayerMP, fromDim: Int) {
			for (tsa in tsAreas[fromDim] ?: emptyList()) NetworkService.sendTo(Message1d(M1d.TIME_STOP_REMOVE, tsa.id.D), player)
			for (tsa in tsAreas[player.dimension] ?: emptyList()) NetworkService.sendTo(MessageTimeStop(PartySystem.getUUIDParty(tsa.uuid), tsa.pos.x, tsa.pos.y, tsa.pos.z, tsa.id), player)
		}
		
		fun stop(caster: EntityLivingBase) {
			caster.worldObj.playBroadcastSound(1013, caster.posX.I, caster.posY.I, caster.posZ.I, 0)
			(tsAreas[caster.dimension] ?: LinkedList<TimeStopArea>().also { tsAreas[caster.dimension] = it }).addLast(TimeStopArea(caster))
			NetworkService.sendToDim(MessageTimeStop(PartySystem.getMobParty(caster), caster.posX, caster.posY, caster.posZ, TimeStopArea.nextID), caster.dimension)
		}
		
		fun tick() {
			var tsa: TimeStopArea
			for ((dim, tsas) in tsAreas) {
				val i = tsas.iterator()
				while (i.hasNext()) {
					tsa = i.next()
					if (--tsa.life <= 0) {
						i.remove()
						NetworkService.sendToDim(Message1d(M1d.TIME_STOP_REMOVE, tsa.id.D), dim)
					}
				}
			}
		}
		
		fun affected(e: Entity?): Boolean {
			if (e == null) return false
			
			val ev = TimeStopEntityCheckEvent(e)
			if (MinecraftForge.EVENT_BUS.post(ev)) return ev.result
			
			if (e is IBossDisplayData) return false
			
			if (e is ITimeStopSpecific && (e as ITimeStopSpecific).isImmune) return false
			
			for (tsa in tsAreas[e.dimension] ?: return false) {
				if (Vector3.vecEntityDistance(tsa.pos, e) < SpellTimeStop.radius) {
					if (MinecraftServer.getServer().configurationManager.playerEntityList.firstOrNull { it as EntityPlayerMP; it.uniqueID == tsa.uuid }?.let { !InteractionSecurity.canInteractWithEntity(it as EntityPlayerMP, e) } != false) return false
					
					if (e is ITimeStopSpecific && (e as ITimeStopSpecific).affectedBy(tsa.uuid)) return true
					
					if (e is EntityLivingBase) {
						if (!PartySystem.sameParty(tsa.uuid, e)) return true
					} else {
						return true
					}
				}
			}
			return false
		}
		
		class TimeStopArea(caster: EntityLivingBase): Serializable {
			
			val pos = Vector3.fromEntity(caster)
			val uuid: UUID = caster.uniqueID
			
			@Transient
			val id = ++nextID
			var life = SpellTimeStop.duration
			
			companion object {
				
				private const val serialVersionUID = 4146871637815241L
				
				@Transient
				var nextID = -1
			}
		}
		
		init {
			MinecraftForge.EVENT_BUS.register(TimeStopThingsListener)
			FMLCommonHandler.instance().bus().register(TimeStopThingsListener)
		}
		
		object TimeStopThingsListener {
			
			@SubscribeEvent
			fun onPlayerChangedDimension(e: PlayerChangedDimensionEvent) {
				if (AlfheimConfigHandler.enableMMO && e.player is EntityPlayerMP) transfer(e.player as EntityPlayerMP, e.fromDim)
			}
			
			@SubscribeEvent
			fun onEntityUpdate(e: EntityUpdateEvent) {
				if (!e.entity.isEntityAlive) return
				if (AlfheimConfigHandler.enableMMO && ASJUtilities.isServer && affected(e.entity)) e.isCanceled = true
			}
			
			@SubscribeEvent
			fun onLivingUpdate(e: LivingUpdateEvent) {
				if (AlfheimConfigHandler.enableMMO && ASJUtilities.isServer && affected(e.entity)) e.isCanceled = true
			}
			
			@SubscribeEvent
			fun onChatEvent(e: ServerChatEvent) {
				if (AlfheimConfigHandler.enableMMO && ASJUtilities.isServer && affected(e.player)) e.isCanceled = true
			}
			
			@SubscribeEvent
			fun onCommandEvent(e: CommandEvent) {
				if (AlfheimConfigHandler.enableMMO && ASJUtilities.isServer && e.sender is EntityPlayer && affected(e.sender as EntityPlayer)) e.isCanceled = true
			}
		}
	}
	
	object ElvenStoryModeSystem {
		
		fun getGender(player: EntityPlayer) = forPlayer(player).gender
		
		fun setGender(player: EntityPlayer, isFemale: Boolean) {
			forPlayer(player).gender = isFemale
		}
		
		fun setCustomSkin(player: EntityPlayer, skinOn: Boolean) {
			forPlayer(player).customSkin = skinOn
		}
		
		fun transfer(player: EntityPlayerMP) {
			playerSegments.forEach { (name, seg) ->
				NetworkService.sendTo(MessageRaceInfo(name, seg.raceID), player)
				NetworkService.sendTo(MessageSkinInfo(name, seg.gender, seg.customSkin), player)
			}
		}
	}
	
	object ElvenReputationSystem {
		
		/**
		 * Level of [player]'s reputation with certain [race].
		 *
		 * -1 - enemy
		 * 0 - unfriendly
		 * 1 - guest
		 * 2 - friendly
		 * 3 - hero
		 */
		fun getReputationLevel(player: EntityPlayer, race: EnumRace): Int {
			return when (getReputationValue(player, race)) {
				in 0..99              -> 0
				in 100..249           -> 1
				in 250..499           -> 2
				in 500..Int.MAX_VALUE -> 3
				else                  -> -1
			}
		}
		
		/**
		 * Reputation value is affected by player's race:
		 *
		 * ALVs are threatened as friends,
		 * HUMANs or same race reputation is default,
		 * other races are not really welcomed and any misdemeanor will lead to becoming an enemy
		 */
		fun getReputationValue(player: EntityPlayer, race: EnumRace): Int {
			return forPlayer(player).reputation[race.ordinal] + if (player.race == EnumRace.ALV) 100 else if (player.race != race && player.race != EnumRace.HUMAN) -50 else 0
		}
		
		fun changeReputationValue(player: EntityPlayer, race: EnumRace, amount: Int) {
			forPlayer(player).reputation[race.ordinal] += amount
		}
		
		fun changeReputationValueBasedOnLevel(player: EntityPlayer, race: EnumRace, amount: Int) {
			val lvl = getReputationLevel(player, race)
			
			val multiplier = if (amount < 0) when (lvl) {
				3    -> 3.0 // hero's reputation will fall quickest
				2    -> 1.5 // friend's less quick
				else -> 1.0 // others the same
			} else when (lvl) {
				-1   -> 0.25 // it is hard to recover for an enemy
				1    -> 1.25 // wow guest is making some good things!
				2    -> 0.99 // you won't become a hero just by trading :D
				3    -> 0.75 // nah, nothing special for a hero...
				else -> 1.00 // others the same
			}
			
			forPlayer(player).reputation[race.ordinal] += (amount * multiplier).I
		}
	}
	
	class PlayerSegment(player: EntityPlayer): Serializable {
		
		var party: Party = Party(player, false)
		
		@Transient
		var target: EntityLivingBase? = null
		
		@Transient
		var isParty = false
		
		@Transient
		var partyIndex = -1
		
		var coolDown = HashMap<String, Int>()
		var hotSpells = IntArray(12)
		
		@Transient
		var castableSpell: SpellBase? = null
		
		@Transient
		var ids: Int = 0
		
		@Transient
		var init: Int = 0
		
		var knowledge: MutableSet<String> = HashSet()
		
		var userName: String = player.commandSenderName
		
		var raceID = 0
		
		var esmAbility = true
		
		fun toggleESMAbility() {
			esmAbility = !esmAbility
		}
		
		/** isFemale otherwise */
		var gender = false
		var customSkin = false
		
		var reputation = Array(EnumRace.values().size) { 150 }
		
		@Transient
		var quadStage = 0
		
		@Transient
		var standingStill = 0 // POOKA ability
		
		@Transient
		var lastPos = Vector3.fromEntity(player)
		
		var abyss = 0
		
		var limbo = 0 // counter for going to limbo
		var heartLoss = 0 // counter incrementing each time getting out of Helheim
		
		var cold = 0
		
		var wisdom = 0
		
		companion object {
			
			private const val serialVersionUID = 4267437884578826733L
		}
	}
}