@file:Suppress("DEPRECATION")

package alfheim.common.core.handler

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.patcher.event.LivingPotionEvent
import alexsocol.patcher.event.NetherPortalActivationEvent
import alexsocol.patcher.event.ServerWakeUpEvent
import alfheim.api.entity.EnumRace
import alfheim.api.entity.race
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.achievement.AlfheimAchievements
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.helper.ContributorsPrivacyHelper
import alfheim.common.core.helper.ElvenFlightHelper
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.core.util.InfoLoader
import alfheim.common.entity.EntityLolicorn
import alfheim.common.entity.boss.EntityFlugel
import alfheim.common.entity.item.EntityItemImmortalRelic
import alfheim.common.item.AlfheimItems
import alfheim.common.item.equipment.tool.ItemSoulSword
import alfheim.common.item.relic.ItemTankMask
import alfheim.common.network.*
import alfheim.common.network.packet.*
import alfheim.common.spell.darkness.SpellDecay
import cpw.mods.fml.common.IFuelHandler
import cpw.mods.fml.common.eventhandler.Event
import cpw.mods.fml.common.eventhandler.EventPriority
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent
import cpw.mods.fml.common.gameevent.TickEvent.*
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ChunkCoordinates
import net.minecraft.util.DamageSource
import net.minecraft.util.IChatComponent
import net.minecraft.util.MathHelper
import net.minecraft.world.storage.DerivedWorldInfo
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.FuelBurnTimeEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.living.LivingDropsEvent
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.event.entity.player.BonemealEvent
import net.minecraftforge.event.entity.player.EntityInteractEvent
import net.minecraftforge.event.world.BlockEvent
import ru.vamig.worldengine.WE_Biome
import vazkii.botania.api.item.IRelic
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.api.recipe.ElvenPortalUpdateEvent
import vazkii.botania.common.block.tile.TileAlfPortal
import vazkii.botania.common.block.tile.string.TileRedStringFertilizer
import vazkii.botania.common.entity.EntityDoppleganger
import vazkii.botania.common.item.ModItems
import kotlin.math.max

@Suppress("unused")
object EventHandler {
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun onPlayerLoggedIn(e: PlayerLoggedInEvent) {
		val player = e.player
		
		if (InfoLoader.doneChecking && !InfoLoader.triedToWarnPlayer) {
			InfoLoader.triedToWarnPlayer = true
			for (s in InfoLoader.info) {
				if (s.startsWith("\$json"))
					player.addChatMessage(IChatComponent.Serializer.func_150699_a(s.replace("\$json", "")))
				else
					ASJUtilities.say(player, s)
			}
		}
		
		if (player is EntityPlayerMP) {
			NetworkService.sendTo(Message2d(M2d.MODES, (if (AlfheimConfigHandler.enableElvenStory) 1 else 0).D, (if (AlfheimConfigHandler.enableMMO) 1 else 0).D), player)
			CardinalSystem.transfer(player)
			if (AlfheimConfigHandler.enableElvenStory) {
				NetworkService.sendTo(Message1d(M1d.ELVEN_FLIGHT_MAX, ElvenFlightHelper.max), player)
				NetworkService.sendTo(MessageNI(Mni.WINGS_BL, *AlfheimConfigHandler.wingsBlackList), player)
				if (!player.hasAchievement(AlfheimAchievements.alfheim) && player.dimension != AlfheimConfigHandler.dimensionIDAlfheim) {
					val (x, y, z) = MinecraftServer.getServer().worldServerForDimension(AlfheimConfigHandler.dimensionIDAlfheim).provider.spawnPoint
					ASJUtilities.sendToDimensionWithoutPortal(player, AlfheimConfigHandler.dimensionIDAlfheim, x + 0.5, y + 0.5, z + 0.5)
					
					player.rotationYaw = 180f
					player.rotationPitch = 0f
					player.triggerAchievement(AlfheimAchievements.alfheim)
					ASJUtilities.say(player, "elvenstory.welcome0")
					ASJUtilities.say(player, "elvenstory.welcome1")
					player.inventory.addItemStackToInventory(ItemStack(ModItems.lexicon))
					player.setSpawnChunk(ChunkCoordinates(0, 250, 0), true, AlfheimConfigHandler.dimensionIDAlfheim)
				}
				
				if (AlfheimConfigHandler.enableMMO)
					NetworkService.sendTo(Message1d(M1d.DEATH_TIMER, AlfheimConfigHandler.deathScreenAddTime.D), player)
			}
		}
	}
	
	@SubscribeEvent
	fun onEntityJoinWorld(e: EntityJoinWorldEvent) {
		if (e.entity is EntityDoppleganger)
			fixGaiaAbuse(e.entity as EntityDoppleganger)
		
		if (e.entity is IMob && e.world.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim && e.world.getBiomeGenForCoords(e.entity.posX.mfloor(), e.entity.posZ.mfloor()) is WE_Biome)
			e.isCanceled = true
		
		val player = e.entity as? EntityPlayerMP ?: return
		val seed = player.worldObj.seed
		NetworkService.sendTo(Message1l(M1l.SEED, seed), player)
	}
	
	// additional checks in AlfheimHookHandler#noDupe<Pre/Post>
	fun fixGaiaAbuse(e: EntityDoppleganger) {
		e.playersAround.forEach {
			if (EntityDoppleganger.isTruePlayer(it))
				e.playersWhoAttacked.add(it.commandSenderName)
		}
	}
	
	@SubscribeEvent
	fun onNetherPortalActivation(e: NetherPortalActivationEvent) {
		if (e.worldObj.provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim ||
			e.worldObj.provider.dimensionId == AlfheimConfigHandler.dimensionIDNiflheim) e.isCanceled = true
	}
	
	@SubscribeEvent
	fun onAlfPortalUpdate(e: ElvenPortalUpdateEvent) {
		if (e.portalTile.worldObj.provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim && (e.portalTile as TileAlfPortal).ticksOpen >= 0) (e.portalTile as TileAlfPortal).ticksOpen = 0
	}
	
	val beheadItems = arrayOf(ModItems.elementiumAxe, AlfheimItems.wireAxe)
	
	@SubscribeEvent
	fun dropFlugelHead(event: LivingDropsEvent) {
		if (!event.recentlyHit || event.source.entity !is EntityLivingBase) return
		val weapon = (event.source.entity as EntityLivingBase).heldItem ?: return
		val target = event.entityLiving
		if (weapon.item !in beheadItems || target !is EntityFlugel || event.entity.worldObj.rand.nextInt(13) >= 1 + EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, weapon)) return
		val head = EntityItem(target.worldObj, target.posX, target.posY, target.posZ, ItemStack(if (target.customNameTag == "Hatsune Miku") AlfheimItems.flugelHead2 else AlfheimItems.flugelHead))
		head.delayBeforeCanPickup = 10
		event.drops.add(head)
	}
	
	@Suppress("DEPRECATION") // stfu not providing alternative
	@SubscribeEvent(priority = EventPriority.HIGHEST) // highest priority for other mods to change values
	fun onFuelValueCheck(e: FuelBurnTimeEvent) {
		if (e.fuel?.item is IFuelHandler) {
			e.burnTime = (e.fuel.item as IFuelHandler).getBurnTime(e.fuel)
			e.result = Event.Result.ALLOW
		} else if (e.fuel?.item?.toBlock() is IFuelHandler) {
			e.burnTime = (e.fuel.item.toBlock() as IFuelHandler).getBurnTime(e.fuel)
			e.result = Event.Result.ALLOW
		}
	}
	
	// ################################### POTIONS & STUFF ####################################
	// not decentralized because of importance of the order
	
	val nineLifesBlockable = arrayOf(DamageSource.fall.damageType, DamageSource.drown.damageType, DamageSource.inFire.damageType, DamageSource.onFire.damageType, DamageSourceSpell.poison.damageType, DamageSourceSpell.poisonMagic.damageType, DamageSource.wither.damageType)
	
	val DamageSource.isMagical: Boolean
		get() = isMagicDamage || this is DamageSourceSpell
	
	@SubscribeEvent
	fun onEntityAttacked(e: LivingAttackEvent) {
		var amount = e.ammount // oh srsly 'mm' ?
		val target = e.entityLiving
		val attacker = e.source.entity
		
		if ((attacker as? EntityLivingBase)?.isPotionActive(AlfheimConfigHandler.potionIDBerserk) == true)
			amount *= 1.2f
		if ((attacker as? EntityLivingBase)?.isPotionActive(AlfheimConfigHandler.potionIDOvermage) == true && (e.source is DamageSourceSpell || (e.source.isMagicDamage && (attacker as? EntityPlayer)?.let { SpellBase.consumeMana(it, (amount * 100).I, true) } == true)))
			amount *= 1.2f
		if ((attacker as? EntityLivingBase)?.isPotionActive(AlfheimConfigHandler.potionIDNinja) == true)
			amount *= 0.8f
		
		if (AlfheimConfigHandler.enableMMO) {
			if (CardinalSystem.PartySystem.friendlyFire(target, e.source)) {
				e.isCanceled = true
				return
			}
			if ((attacker as? EntityLivingBase)?.isPotionActive(AlfheimConfigHandler.potionIDQuadDamage) == true)
				amount *= 4f
			
			if ((attacker as? EntityLivingBase)?.isPotionActive(AlfheimConfigHandler.potionIDLeftFlame) == true || target.isPotionActive(AlfheimConfigHandler.potionIDLeftFlame)) {
				e.isCanceled = true
				return
			}
		}
		
		if (AlfheimConfigHandler.enableElvenStory && e.source.damageType == DamageSource.fall.damageType && target is EntityPlayer && target.race != EnumRace.HUMAN) {
			e.isCanceled = true
			return
		}
		
		if (e.source.isFireDamage && !e.source.isUnblockable && (target as? EntityPlayer)?.getCurrentArmor(1)?.item === AlfheimItems.elementalLeggings && ManaItemHandler.requestManaExact(target.getCurrentArmor(1), target, MathHelper.ceiling_float_int(10 * amount), !target.worldObj.isRemote)) {
			e.isCanceled = true
			return
		}
		
		// ################################################################ NOT CANCELING ################################################################
		
		if (AlfheimConfigHandler.enableMMO && target.isPotionActive(AlfheimConfigHandler.potionIDDecay) && !e.source.isFireDamage && !e.source.isMagical && e.source.damageType != DamageSourceSpell.bleeding.damageType)
			target.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDBleeding, SpellDecay.duration / 5, SpellDecay.efficiency.I))
	}
	
	@SubscribeEvent
	fun onEntityHurt(e: LivingHurtEvent) {
		val target = e.entityLiving
		val attacker = e.source.entity
		
		if ((attacker as? EntityLivingBase)?.isPotionActive(AlfheimConfigHandler.potionIDBerserk) == true)
			e.ammount *= 1.2f
		if ((attacker as? EntityLivingBase)?.isPotionActive(AlfheimConfigHandler.potionIDOvermage) == true && e.source.isMagical)
			e.ammount *= 1.2f
		if ((attacker as? EntityLivingBase)?.isPotionActive(AlfheimConfigHandler.potionIDNinja) == true)
			e.ammount *= 0.8f
		
		if (AlfheimConfigHandler.enableMMO) {
			if (CardinalSystem.PartySystem.friendlyFire(target, e.source)) {
				e.isCanceled = true
				return
			}
			
			if ((attacker as? EntityLivingBase)?.isPotionActive(AlfheimConfigHandler.potionIDQuadDamage) == true) {
				e.ammount *= 4f
				VisualEffectHandler.sendPacket(VisualEffects.QUADH, attacker)
			}
			
			var pe: PotionEffect? = target.getActivePotionEffect(AlfheimConfigHandler.potionIDNineLifes)
			run nl@{
				@Suppress("NAME_SHADOWING")
				val pe = pe ?: return@nl
				
				val blockable = e.source.damageType in nineLifesBlockable
				
				if (blockable) {
					if (pe.amplifier == 4) {
						if (ASJUtilities.willEntityDie(e)) {
							if (e.source.damageType == DamageSource.wither.damageType && target.worldObj.rand.nextBoolean()) return@nl
							pe.amplifier = 0
							pe.duration = 100
							if (ASJUtilities.isServer) NetworkService.sendToAll(MessageEffect(target.entityId, pe.potionID, pe.duration, pe.amplifier))
							e.isCanceled = true
							return
						}
					} else if (pe.amplifier == 0) {
						e.isCanceled = true
						return
					}
				} else if (attacker is EntityLivingBase && attacker.isEntityAlive && target.worldObj.rand.nextInt(3) == 0) {
					attacker.attackEntityFrom(e.source, e.ammount / 2)
				}
			}
			
			pe = target.getActivePotionEffect(AlfheimConfigHandler.potionIDStoneSkin)
			if (pe != null && !e.source.isMagical && !e.source.isDamageAbsolute) {
				e.isCanceled = true
				target.removePotionEffect(AlfheimConfigHandler.potionIDStoneSkin)
				target.addPotionEffect(PotionEffect(Potion.field_76444_x.id, pe.duration))
				return
			}
			
			pe = target.getActivePotionEffect(AlfheimConfigHandler.potionIDButterShield)
			if (pe != null && pe.duration > 0 && e.source.isMagical && !e.source.isDamageAbsolute) {
				e.isCanceled = true
				if (--pe.amplifier <= 0) pe.duration = 0 // target.removePotionEffect(AlfheimRegistry.butterShield.id) <- ConcurrentModificationException :(
				if (ASJUtilities.isServer) NetworkService.sendToAll(MessageEffect(target.entityId, pe.potionID, pe.duration, pe.amplifier))
				return
			}
			
			// ################################################################ NOT CANCELING ################################################################
			
			pe = target.getActivePotionEffect(AlfheimConfigHandler.potionIDButterShield)
			if (!e.source.isMagical && !e.source.isDamageAbsolute && pe != null && pe.duration > 0) {
				e.ammount /= 2f
				pe.duration -= (e.ammount * 20).I
				val dur = max(pe.duration, 0)
				if (ASJUtilities.isServer) NetworkService.sendToAll(MessageEffect(target.entityId, pe.potionID, dur, pe.amplifier))
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST) // if something can cancel death
	fun onEntityDeath(e: LivingDeathEvent) {
		if (!AlfheimConfigHandler.enableMMO) return
		
		CardinalSystem.PartySystem.getMobParty(e.entityLiving)?.setDead(e.entityLiving, true)
		
		if (e.entityLiving !is EntityPlayer || e.source.damageType == "Respawn" || MinecraftServer.getServer()?.isSinglePlayer != false || AlfheimConfigHandler.deathScreenAddTime <= 0 || ItemTankMask.canBeSaved(e.entityLiving as EntityPlayer)) return
		
		if (!e.entityLiving.isPotionActive(AlfheimConfigHandler.potionIDLeftFlame)) {
			e.entityLiving.clearActivePotions()
			e.entityLiving.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDLeftFlame, AlfheimConfigHandler.deathScreenAddTime))
		}
		
		e.entityLiving.dataWatcher.updateObject(6, 1f)
		e.isCanceled = true
		
		RagnarokHandler.consumePriestEmblem(e.source.entity as? EntityPlayer ?: return, e.entityLiving, e.source !is ItemSoulSword.DamageSourceSoulSword)
	}
	
	@SubscribeEvent
	fun onServerTick(e: ServerTickEvent) {
		if (e.phase == Phase.START)
			EntityLolicorn.tick()
	}
	
	@SubscribeEvent
	fun onBlockBreak(e: BlockEvent.BreakEvent) {
		val item = e.player.currentEquippedItem?.item ?: return
		if (item === AlfheimItems.flugelSoul && e.player.currentEquippedItem.meta != 0xFACE17) e.isCanceled = true
	}
	
	@SubscribeEvent
	fun onLivingUpdate(e: LivingUpdateEvent) {
		if (AlfheimConfigHandler.enableMMO) {
			if (e.entityLiving.isPotionActive(AlfheimConfigHandler.potionIDLeftFlame)) {
				val pe = e.entityLiving.getActivePotionEffect(AlfheimConfigHandler.potionIDLeftFlame)!!
				pe.duration--
				if (ASJUtilities.isClient) VisualEffectHandlerClient.onDeathTick(e.entityLiving)
				if (pe.duration <= 0)
					e.entityLiving.removePotionEffect(pe.potionID)
				else
					e.isCanceled = true
			}
			
			if (e.entityLiving.isDead) {
				val pt = CardinalSystem.PartySystem.getMobParty(e.entityLiving)
				pt?.setDead(e.entityLiving, true)
			}
		}
	}
	
	@SubscribeEvent
	fun onPlayerUpdate(e: PlayerTickEvent) {
		if (e.phase != Phase.START) return
		val player = e.player
		
		if (AlfheimConfigHandler.enableElvenStory && player.race == EnumRace.POOKA && !player.worldObj.isRemote) {
			val seg = CardinalSystem.forPlayer(player)
			val pos = Vector3.fromEntity(player)
			
			if (seg.lastPos == pos && player.fallDistance == 0f && !player.capabilities.isFlying)
				seg.standingStill++
			else
				seg.standingStill = 0
			
			seg.lastPos = pos
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	fun onRelicItemAppear(e: EntityJoinWorldEvent) {
		val entity = e.entity as? EntityItem ?: return
		
		if (entity.isDead) return
		
		val stack = entity.dataWatcher.getWatchableObjectItemStack(10) ?: return
		if (stack.stackSize < 1) return
		
		if (entity.entityItem.item is IRelic) {
			e.isCanceled = true
			entity.setDead()
			EntityItemImmortalRelic(entity).spawn()
		}
	}
	
	@SubscribeEvent
	fun onNewPotionEffect(e: LivingPotionEvent.Add.Post) {
		if (ASJUtilities.isServer) NetworkService.sendToAll(MessageEffect(e.entityLiving.entityId, e.effect.potionID, e.effect.duration, e.effect.amplifier, false, 1))
		
		onlyOneStoneEffect(e)
	}
	
	val stoneEffects = arrayOf(AlfheimConfigHandler.potionIDBerserk, AlfheimConfigHandler.potionIDNinja, AlfheimConfigHandler.potionIDOvermage, AlfheimConfigHandler.potionIDTank)
	
	fun onlyOneStoneEffect(e: LivingPotionEvent.Add.Post) {
		if (e.effect.potionID !in stoneEffects) return
		
		for (id in stoneEffects) {
			if (id == e.effect.potionID) continue
			
			e.entityLiving.removePotionEffect(id)
		}
	}
	
	@SubscribeEvent
	fun onChangedPotionEffect(e: LivingPotionEvent.Change.Post) {
		if (ASJUtilities.isServer) NetworkService.sendToAll(MessageEffect(e.entityLiving.entityId, e.effect.potionID, e.effect.duration, e.effect.amplifier, e.update, 0))
	}
	
	@SubscribeEvent
	fun onFinishedPotionEffect(e: LivingPotionEvent.Remove.Post) {
		if (ASJUtilities.isServer) NetworkService.sendToAll(MessageEffect(e.entityLiving.entityId, e.effect.potionID, e.effect.duration, e.effect.amplifier, false, -1))
	}
	
//	@SubscribeEvent
//	fun onEntityUpdate(e: EntityUpdateEvent) {
//		if (!e.entity.isEntityAlive) return
//	}
	
	@SubscribeEvent
	fun onInteract(e: EntityInteractEvent) {
		if (!e.entityPlayer.isSneaking && e.entityPlayer.heldItem?.item === Items.stick && ContributorsPrivacyHelper.contributors.values.contains(e.entityPlayer.commandSenderName))
			if (e.target !== e.entityPlayer.riddenByEntity)
				e.entityPlayer.mountEntity(e.target)
	}
	
	@SubscribeEvent
	fun onPlayerWakeUp(e: ServerWakeUpEvent) { // because there is some bug in sleeping
		if (AlfheimConfigHandler.alfheimSleepExtraCheck && e.world.provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim && e.world.worldInfo is DerivedWorldInfo && e.world.gameRules.getGameRuleBooleanValue("doDaylightCycle")) {
			val i = e.world.worldInfo.worldTime + 24000L
			(e.world.worldInfo as DerivedWorldInfo).theWorldInfo.worldTime = i - i % 24000L
		}
	}
	
	// Red String Nutrifier (Fertilizer) fix
	@SubscribeEvent
	fun onBonemeal(e: BonemealEvent) {
		val tile = e.world.getTileEntity(e.x, e.y, e.z) as? TileRedStringFertilizer ?: return
		
		val binding = tile.binding ?: return
		
		val event = BonemealEvent(e.entityPlayer, e.world, tile.blockAtBinding, binding.posX, binding.posY, binding.posZ)
		if (MinecraftForge.EVENT_BUS.post(event)) {
			e.isCanceled = true
			return
		}
		
		if (event.result == Event.Result.ALLOW) {
			e.result = Event.Result.ALLOW
			return
		}
	}
	
	@SubscribeEvent
	fun replaceGaiaDropsInAlfheim(e: LivingDropsEvent) {
		if (e.entityLiving !is EntityDoppleganger) return
		
		e.drops.forEach { item ->
			if (item?.entityItem?.item !== ModItems.manaResource) return@forEach
			
			item.entityItem.meta = when (item.entityItem.meta) {
				0    -> 7
				1    -> 8
				2    -> 9
				else -> item.entityItem.meta
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	fun heatPlayerInMuspelheim(e: SheerColdHandler.SheerColdTickEvent) {
		if (!AlfheimConfigHandler.hotHell) return
		
		val entity = e.entityLiving
		if (entity.worldObj.provider.dimensionId != -1) return
		
		var heat = -0.05f
		if (entity.posY <= 35) // if near lava
			heat -= 0.0375f
		
		e.delta = (e.delta ?: 0f) + heat
	}
}