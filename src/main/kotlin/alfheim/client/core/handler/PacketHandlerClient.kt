package alfheim.client.core.handler

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.TileItemContainer
import alfheim.api.*
import alfheim.api.entity.*
import alfheim.api.spell.SpellBase.SpellCastResult
import alfheim.client.core.handler.CardinalSystemClient.PlayerSegmentClient
import alfheim.client.core.handler.CardinalSystemClient.SpellCastingSystemClient
import alfheim.client.core.handler.CardinalSystemClient.TimeStopSystemClient
import alfheim.client.core.proxy.ClientProxy
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem.Knowledge
import alfheim.common.core.handler.CardinalSystem.PartySystem.Party
import alfheim.common.core.handler.CardinalSystem.PartySystem.Party.PartyStatus
import alfheim.common.core.handler.SheerColdHandler.cold
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.helper.*
import alfheim.common.entity.spell.EntitySpellFireball
import alfheim.common.item.relic.LeashingHandler.leashedTo
import alfheim.common.item.rod.RedstoneSignalHandlerClient
import alfheim.common.network.*
import alfheim.common.network.Message0dC.M0dc
import alfheim.common.network.Message1d.M1d
import alfheim.common.network.Message1l.M1l
import alfheim.common.network.Message2d.M2d
import alfheim.common.network.Message3d.M3d
import alfheim.common.network.MessageNI.Mni
import cpw.mods.fml.common.network.simpleimpl.*
import net.minecraft.client.gui.ChatLine
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.event.ClickEvent
import net.minecraft.potion.*
import net.minecraft.util.*
import java.io.File

object PacketHandlerClient: IMessageHandler<IMessage?, IMessage?> {
	
	override fun onMessage(message: IMessage?, ctx: MessageContext): IMessage? {
		val response: Any = when (message) {
			is Message0dC                 -> handle(message)
			is Message1d                  -> handle(message)
			is Message1l                  -> handle(message)
			is Message2d                  -> handle(message)
			is Message3d                  -> handle(message)
			is MessageNI                  -> handle(message)
			is MessageContributor         -> handle(message)
			is MessageEffect              -> handle(message)
			is MessageGleipnirLeash       -> handle(message)
			is MessageHotSpellC           -> PlayerSegmentClient.hotSpells = message.ids.clone()
			is MessageParty               -> handle(message)
			is MessageRaceInfo            -> CardinalSystemClient.playerRaceIDs[message.name] = message.raceID
			is MessageRedstoneSignalsSync -> handle(message)
			is MessageSkinInfo            -> CardinalSystemClient.playerSkinsData[message.name] = message.isFemale to message.isSkinOn
			is MessageSpellParams         -> handle(message)
			is MessageTileItem            -> handle(message)
			is MessageTimeStop            -> handle(message)
			is MessageVisualEffect        -> VisualEffectHandlerClient.select(VisualEffectHandlerClient.VisualEffects.values()[message.type], message.data)
			null                          -> Unit
			else                          -> throw IllegalArgumentException("Unknown message type: ${message::class.java}")
		}
		
		return if (response is IMessage) response else null
	}
	
	fun handle(packet: Message0dC) {
		when (M0dc.values()[packet.type]) {
			M0dc.MTSPELL -> {
				val spell = AlfheimAPI.getSpellByIDs(KeyBindingHandlerClient.raceID, KeyBindingHandlerClient.spellID) ?: return
				ASJUtilities.say(mc.thePlayer, "spell.$spell.mtinfo", *spell.usableParams)
			}
		}
	}
	
	fun handle(packet: Message1d) {
		when (M1d.values()[packet.type]) {
			M1d.COLD             -> mc.thePlayer.cold = packet.data1.F
			M1d.DEATH_TIMER      -> AlfheimConfigHandler.deathScreenAddTime = packet.data1.I
			M1d.ESMABIL          -> PlayerSegmentClient.esmAbility = packet.data1 != 0.0
			M1d.ELVEN_FLIGHT_MAX -> {
				AlfheimConfigHandler.flightTime = packet.data1.I
				ElvenFlightHelper.max = packet.data1
			}
			M1d.KNOWLEDGE        -> PlayerSegmentClient.knowledge.add("${Knowledge.values()[packet.data1.I]}")
			M1d.LIMBO            -> PlayerSegmentClient.limbo = packet.data1.I
			M1d.TIME_STOP_REMOVE -> TimeStopSystemClient.remove(packet.data1.I)
			M1d.RLCM    -> {
				// sorry anyone whom RUN_COMMAND chat actions may have been deleted by this :sweat_smile: but I don't really care
				for (it in mc.ingameGUI.chatGUI.chatLines) {
					it as ChatLine
					val answer = it.func_151461_a() as? ChatComponentText ?: break
					val event = answer.chatStyle.chatClickEvent ?: break
					
					if (event.action === ClickEvent.Action.RUN_COMMAND)
						answer.chatStyle.chatClickEvent = null
				}
				
				for (it in mc.ingameGUI.chatGUI.field_146253_i) {
					it as ChatLine
					val compositeLine = it.func_151461_a() as? ChatComponentText ?: break
					
					for (sib in compositeLine.siblings) {
						sib as IChatComponent
						val event = sib.chatStyle.chatClickEvent ?: continue
						
						if (event.action === ClickEvent.Action.RUN_COMMAND)
							sib.chatStyle.chatClickEvent = null
					}
				}
				
				while (packet.data1-- > 0)
					mc.ingameGUI.chatGUI.sentMessages.removeLastOrNull() // for safety
			}
			M1d.NOSUNMOON        -> RagnarokHandler.noSunAndMoon = packet.data1 == 1.0
			M1d.GINNUNGAGAP      -> RagnarokHandler.ginnungagap = packet.data1 == 1.0
			M1d.RAGNAROK         -> {
				if (packet.data1 == -1.0) {
					RagnarokHandler.ragnarok = false
					RagnarokHandler.finished = true
					RagnarokHandler.fogFade = 1f
					return
				}
				
				RagnarokHandler.ragnarok = packet.data1 < 1
				RagnarokHandler.fogFade = packet.data1.F
				
				if (0 < packet.data1 && packet.data1 < 1)
					mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "${ModInfo.MODID}:fenrir.howl", 50f, 0.5f, false)
			}
		}
	}
	
	fun handle(packet: Message1l) {
		when (M1l.values()[packet.type]) {
			M1l.SEED -> mc.theWorld.worldInfo.randomSeed = packet.data1
		}
	}
	
	fun handle(packet: Message2d) {
		when (M2d.values()[packet.type]) {
			M2d.ATTRIBUTE    -> {
				when (packet.data1.I) {
					0 -> mc.thePlayer.raceID = packet.data2.I
					1 -> mc.thePlayer.flight = packet.data2
				}
			}
			
			M2d.COOLDOWN     -> {
				when (if (packet.data2 > 0) SpellCastResult.OK else SpellCastResult.values()[(-packet.data2).I]) {
					SpellCastResult.DESYNC    -> throw IllegalArgumentException("Client-server spells desynchronization. Not found spell for ${EnumRace[packet.data1.I shr 28 and 0xF]} with id ${packet.data1.I and 0xFFFFFFF}")
					SpellCastResult.NOMANA    -> ASJUtilities.say(mc.thePlayer, "alfheimmisc.cast.nomana")
					SpellCastResult.NOTALLOW  -> ASJUtilities.say(mc.thePlayer, "alfheimmisc.cast.notallow")
					SpellCastResult.NOTARGET  -> ASJUtilities.say(mc.thePlayer, "alfheimmisc.cast.notarget")
					SpellCastResult.NOTREADY  -> Unit /*ASJUtilities.say(mc.thePlayer, "alfheimmisc.cast.notready");*/
					SpellCastResult.NOTSEEING -> ASJUtilities.say(mc.thePlayer, "alfheimmisc.cast.notseeing")
					SpellCastResult.OBSTRUCT  -> ASJUtilities.say(mc.thePlayer, "alfheimmisc.cast.obstruct")
					SpellCastResult.OK        -> SpellCastingSystemClient.setCoolDown(AlfheimAPI.getSpellByIDs(packet.data1.I shr 28 and 0xF, packet.data1.I and 0xFFFFFFF), packet.data2.I)
					SpellCastResult.WRONGTGT  -> ASJUtilities.say(mc.thePlayer, "alfheimmisc.cast.wrongtgt")
				}
			}
			
			M2d.UUID         -> PlayerSegmentClient.party?.setUUID(packet.data2.I, packet.data1.I)
			
			M2d.MODES        -> {
				if (packet.data1 > 0) ClientProxy.enableESM() else ClientProxy.disableESM()
				if (packet.data2 > 0) ClientProxy.enableMMO() else ClientProxy.disableMMO()
			}
			
			M2d.FIREBALLSYNC -> {
				(mc.theWorld.getEntityByID(packet.data1.I) as? EntitySpellFireball)?.target = mc.theWorld.getEntityByID(packet.data2.I) as? EntityLivingBase
			}
		}
	}
	
	fun handle(packet: Message3d) {
		when (M3d.values()[packet.type]) {
			M3d.KEY_BIND     -> Unit
			
			M3d.PARTY_STATUS -> {
				when (PartyStatus.values()[packet.data1.I]) {
					PartyStatus.DEAD      -> PlayerSegmentClient.party?.setDead(packet.data2.I, packet.data3.I == -10)
					PartyStatus.MANA      -> PlayerSegmentClient.party?.setMana(packet.data2.I, packet.data3.I)
					PartyStatus.HEALTH    -> PlayerSegmentClient.party?.setHealth(packet.data2.I, packet.data3.F)
					PartyStatus.MAXHEALTH -> PlayerSegmentClient.party?.setMaxHealth(packet.data2.I, packet.data3.F)
					PartyStatus.TYPE      -> PlayerSegmentClient.party?.setType(packet.data2.I, packet.data3.I)
				}
			}
			
			M3d.WEATHER -> {
				if (mc.theWorld == null) return
				
				mc.theWorld.setRainStrength(if (packet.data1.I > 0) 1f else 0f)
				mc.theWorld.setThunderStrength(if (packet.data1.I > 1) 1f else 0f)
				
				val info = mc.theWorld.worldInfo
				info.isRaining = packet.data1.I > 0
				info.rainTime = packet.data2.I
				info.isThundering = packet.data1.I > 1
				info.thunderTime = packet.data3.I
			}
			
			M3d.TOGGLER      -> ClientProxy.toggelModes(packet.data1 > 0, packet.data2.I and 1 > 0, packet.data3.I and 1 > 0, packet.data2.I shr 1 and 1 > 0, packet.data3.I shr 1 and 1 > 0)
		}
	}
	
	fun handle(packet: MessageNI) {
		when (Mni.values()[packet.type]) {
			Mni.BLIZZARD -> RagnarokHandler.blizzards.apply {
				val (id) = packet.intArray
				
				if (id < 0) removeAll { it.id == id }
				else if (packet.intArray.size == 5) {
					val (_, x1, z1, x2, z2) = packet.intArray
					add(RagnarokHandler.BlizzardData(x1, z1, x2, z2).apply { setId(id) })
				}
			}
			Mni.HEARTLOSS -> CardinalSystem.CommonSystem.updateLostHearts(mc.thePlayer, packet.intArray[0])
			Mni.INTERACTION -> Unit // server
			Mni.WINGS_BL -> AlfheimConfigHandler.wingsBlackList = packet.intArray
		}
	}
	
	fun handle(message: MessageContributor): Any {
		if (message.isRequest) {
			val info = File("contributor.info")
			var login = "login"
			var password = "password"
			
			if (info.exists()) {
				val creds = info.readLines()
				login = creds.getOrElse(0) { login }
				password = creds.getOrElse(1) { password }
			}
			
			return MessageContributor(login, HashHelper.hash(password))
		} else {
			// new alias registration
			ContributorsPrivacyHelper.contributors[message.key] = message.value
		}
		
		return Unit
	}
	
	fun handle(message: MessageEffect) {
		val e = mc.theWorld.getEntityByID(message.entity)
		
		if (e !is EntityLivingBase) return
		
		val pe = e.getActivePotionEffect(message.id)
		
		when (message.state) {
			1    /* add	*/  -> {
				if (pe == null) {
					e.addPotionEffect(PotionEffect(message.id, message.dur, message.amp))
					Potion.potionTypes[message.id].applyAttributesModifiersToEntity(e, e.getAttributeMap(), message.amp)
				} else {
					if (message.readd) Potion.potionTypes[message.id].removeAttributesModifiersFromEntity(e, e.getAttributeMap(), message.amp)
					pe.amplifier = message.amp
					pe.duration = message.dur
					if (message.readd) Potion.potionTypes[message.id].applyAttributesModifiersToEntity(e, e.getAttributeMap(), message.amp)
				}
			}
			
			0    /* upd	*/  -> {
				if (pe == null) {
					e.addPotionEffect(PotionEffect(message.id, message.dur, message.amp))
					Potion.potionTypes[message.id].applyAttributesModifiersToEntity(e, e.getAttributeMap(), message.amp)
				} else {
					if (message.readd) Potion.potionTypes[message.id].removeAttributesModifiersFromEntity(e, e.getAttributeMap(), message.amp)
					pe.amplifier = message.amp
					pe.duration = message.dur
					if (message.readd) Potion.potionTypes[message.id].applyAttributesModifiersToEntity(e, e.getAttributeMap(), message.amp)
				}
			}
			
			-1    /* rem	*/ -> {
				if (pe != null) {
					e.removePotionEffect(message.id)
					Potion.potionTypes[message.id].removeAttributesModifiersFromEntity(e, e.getAttributeMap(), message.amp)
				}
			}
		}
	}
	
	fun handle(message: MessageGleipnirLeash) {
		val world = mc.theWorld
		val target = world.loadedEntityList.firstOrNull { (it as Entity).entityId.toString() == message.targetID } as? EntityLivingBase ?: return
		val actor = world.playerEntities.firstOrNull { (it as EntityPlayer).commandSenderName == message.playerName } as? EntityPlayer
		
		if (message.playerName.isEmpty()) {
			target.leashedTo = null
		} else {
			target.leashedTo = actor ?: return
		}
	}
	
	fun handle(message: MessageParty) {
		PlayerSegmentClient.party = message.party
		PlayerSegmentClient.partyIndex = 0
	}
	
	fun handle(message: MessageRedstoneSignalsSync) {
		RedstoneSignalHandlerClient.redstoneSignals = message.signals
	}
	
	fun handle(message: MessageSpellParams) {
		val spell = AlfheimAPI.getSpellInstance(message.name) ?: return
		spell.damage = message.damage
		spell.duration = message.duration
		spell.efficiency = message.efficiency
		spell.radius = message.radius
	}
	
	fun handle(packet: MessageTileItem) {
		val world = mc.theWorld
		val te = world.getTileEntity(packet.x, packet.y, packet.z)
		if (te is TileItemContainer) te.item = packet.s
	}
	
	fun handle(packet: MessageTimeStop) {
		if (packet.party == null) packet.party = Party()
		TimeStopSystemClient.stop(packet.x, packet.y, packet.z, packet.party!!, packet.id)
	}
}
