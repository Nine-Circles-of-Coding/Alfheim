package alfheim.common.core.handler

import alexsocol.asjlib.*
import alexsocol.patcher.event.*
import alfheim.AlfheimCore
import alfheim.api.ModInfo
import alfheim.api.entity.EnumRace.*
import alfheim.api.entity.race
import alfheim.client.core.handler.CardinalSystemClient.PlayerSegmentClient
import alfheim.common.core.command.CommandAlfheim
import alfheim.common.core.helper.*
import alfheim.common.network.*
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.common.gameevent.PlayerEvent.*
import cpw.mods.fml.common.gameevent.TickEvent
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent
import net.minecraft.entity.passive.*
import net.minecraft.entity.player.*
import net.minecraft.init.*
import net.minecraft.item.ItemStack
import net.minecraft.potion.*
import net.minecraft.server.MinecraftServer
import net.minecraft.util.MovingObjectPosition
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.event.entity.player.*
import net.minecraftforge.oredict.OreDictionary
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.equipment.bauble.ItemFlightTiara
import vazkii.botania.common.item.equipment.tool.ToolCommons
import vazkii.botania.common.world.WorldTypeSkyblock
import java.io.File
import kotlin.math.*
import cpw.mods.fml.common.gameevent.TickEvent.Phase as TickPhase

object ESMHandler {
	
	fun checkAddAttrs() {
		if (!AlfheimConfigHandler.enableElvenStory) return
		for (o in MinecraftServer.getServer().configurationManager.playerEntityList) {
			ElvenFlightHelper.ensureExistence(o as EntityPlayerMP)
		}
	}
	
	// EVENTS
	
	@SubscribeEvent
	fun onServerStarted(e: ServerStartingEvent) {
		readModes(e.save)
	}
	
	private fun file(save: String): File {
		val dir = File("$save/data/${ModInfo.MODID}")
		dir.mkdirs()
		return File(dir, "AlfheimModes.txt")
	}
	
	fun readModes(save: String) {
		val file = file(save)
		if (!file.exists()) return writeModes(save)
		
		val modes = file.readText().toInt()
		
		val isMMO = modes == 2
		val isESM = modes == 1 || isMMO
		
		if (isESM != AlfheimConfigHandler.enableElvenStory) {
			AlfheimConfigHandler.enableElvenStory = isESM
			CommandAlfheim.toggleESM(AlfheimConfigHandler.enableElvenStory)
		}
		
		if (isMMO != AlfheimConfigHandler.enableMMO) {
			AlfheimConfigHandler.enableMMO = isMMO
			CommandAlfheim.toggleESM(AlfheimConfigHandler.enableMMO)
		}
	}
	
	fun writeModes(save: String) {
		val modes = if (AlfheimConfigHandler.enableMMO) 2 else if (AlfheimConfigHandler.enableElvenStory) 1 else 0
		
		file(save).writeText(modes.toString())
	}
	
	@SubscribeEvent
	fun onPlayerChangeDimension(e: PlayerChangedDimensionEvent) {
		if (AlfheimConfigHandler.enableElvenStory && !e.player.capabilities.isCreativeMode && e.toDim in AlfheimConfigHandler.wingsBlackList) {
			e.player.capabilities.allowFlying = false
			e.player.capabilities.isFlying = false
			e.player.sendPlayerAbilities()
		}
	}
	
	@SubscribeEvent
	fun getWaterBowl(event: PlayerInteractEvent) {
		val player = event.entityPlayer
		
		if (!(AlfheimConfigHandler.enableElvenStory && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) return
		
		val equipped = player.currentEquippedItem ?: return
		
		val mop = ToolCommons.raytraceFromEntity(event.world, player, true, (player as? EntityPlayerMP)?.theItemInWorldManager?.blockReachDistance ?: 4.5) ?: return
		
		if (mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return
		
		var i = mop.blockX
		var j = mop.blockY
		var k = mop.blockZ
		
		if (equipped.item === Items.bowl) run {
			if (Botania.gardenOfGlassLoaded && WorldTypeSkyblock.isWorldSkyblock(event.world)) return@run
			
			if (event.world.getBlock(i, j, k) !== Blocks.water) return@run
			
			--equipped.stackSize
			
			val bowl = ItemStack(ModItems.waterBowl)
			
			if (equipped.stackSize <= 0)
				player.inventory[player.inventory.currentItem] = bowl
			else {
				if (!player.inventory.addItemStackToInventory(bowl))
					player.dropPlayerItemWithRandomChoice(bowl, false)
			}
		} else if (equipped.item === ModItems.waterBowl && ASJUtilities.getAmount(player.inventory, ItemStack(ModItems.waterBowl)) >= 4) run {
			if (event.world.getBlock(i, j, k) === ModBlocks.altar) return@run
			
			when (mop.sideHit) {
				0 -> j--
				1 -> j++
				2 -> k--
				3 -> k++
				4 -> i--
				5 -> i++
			}
			
			if (!event.world.isAirBlock(i, j, k)) return@run
			
			--equipped.stackSize
			ASJUtilities.consumeItemStack(player.inventory, ItemStack(ModItems.waterBowl, 4))
			
			event.world.setBlock(i, j, k, Blocks.flowing_water)
			
			val bowl = ItemStack(Items.bowl, 4)

//			if (equipped.stackSize <= 0)
//				player.inventory[player.inventory.currentItem] = bowl
//			else {
//				if (!player.inventory.addItemStackToInventory(bowl))
			player.dropPlayerItemWithRandomChoice(bowl, false)
//			}
		}
	}
	
	@SubscribeEvent
	fun onPlayerUpdate(e: PlayerTickEvent) {
		if (e.phase != TickEvent.Phase.START) return
		
		if (AlfheimConfigHandler.enableElvenStory) {
			doRaceAbility(e.player)
		}
		
		if (ASJUtilities.isClient)
			fixSpriggan()
	}
	
	fun doRaceAbility(player: EntityPlayer) {
		when (player.race) {
			SALAMANDER -> doSalamander(player)
			SYLPH      -> doSylph(player)
			CAITSITH   -> Unit // below
			POOKA      -> doPooka(player) // also hook  scaring away creepers
			GNOME      -> doGnome(player)
			LEPRECHAUN -> Unit // below
			SPRIGGAN   -> doSpriggan(player)
			UNDINE     -> doUndine(player)
			IMP        -> Unit // hook: +20% mana discount for tools
			else       -> Unit // NO-OP
		}
	}
	
	fun doSalamander(player: EntityPlayer) {
		if (isAbilityDisabled(player)) return
		
		checkRemove(player, Potion.blindness.id)
		checkRemove(player, Potion.poison.id)
		checkRemove(player, Potion.confusion.id)
	}
	
	fun checkRemove(player: EntityPlayer, id: Int) {
		val effect = player.getActivePotionEffect(id) ?: return
		var dur = effect.duration
		var amp = effect.amplifier
		
		if (player.rng.nextInt(max(1, dur * (amp + 1) * 5)) != 0) return
		
		--amp
		if (amp < 0) {
			amp = 0
			dur = 0
		}
		
		effect.amplifier = amp
		effect.duration = dur
		
		if (!player.worldObj.isRemote) AlfheimCore.network.sendToAll(MessageEffect(player.entityId, id, dur, amp))
	}
	
	fun doSylph(player: EntityPlayer) {
		if (isAbilityDisabled(player)) return
		
		if (player.capabilities.isFlying) {
			if (player.moveForward > 1e-4f)
				player.moveFlying(0f, 1f, 0.005f)
		} else {
			if (player.ticksExisted % 5 == 0)
				ElvenFlightHelper.regen(player, 1)
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun doCaitSith(e: EntityInteractEvent) {
		if (!AlfheimConfigHandler.enableElvenStory || e.entityPlayer.race !== CAITSITH) return
		
		val player = e.entityPlayer
		if (isAbilityDisabled(player)) return
		
		val tg = e.target
		if (tg is EntityTameable && !tg.isTamed) {
			tg.isTamed = true
			tg.func_152115_b(player.uniqueID.toString())
		} else if (tg is EntityHorse) {
			tg.setTamedBy(player)
			tg.worldObj.setEntityState(tg, 7.toByte())
		} else return
		
		player.addPotionEffect(PotionEffect(Potion.regeneration.id, 600))
		player.addPotionEffect(PotionEffect(Potion.field_76444_x.id, 1800))
	}
	
	fun doPooka(player: EntityPlayer) {
		if (player.worldObj.isRemote || isAbilityDisabled(player)) return
		
		val seg = CardinalSystem.forPlayer(player)
		if (seg.standingStill > 200) {
			if (player.ticksExisted % 50 == 0) if (player.health < player.maxHealth * 0.75 + 0.5) player.heal(0.5f)
			if (player.ticksExisted % 100 == 0) if (player.foodStats.foodLevel < 16) player.foodStats.addStats(1, 0.05f)
		}
	}
	
	fun doGnome(player: EntityPlayer) {
		if (ASJUtilities.isServer || !player.isSneaking) return
		if (isAbilityDisabled(player)) return
		
		val x = player.posX.mfloor() - 8
		val y = player.posY.mfloor() - 8
		val z = player.posZ.mfloor() - 8
		
		Botania.proxy.setWispFXDepthTest(false)
		
		for (i in x until (x + 16)) {
			val j = y + player.ticksExisted % 17
			for (k in z until (z + 16)) {
				val block = player.worldObj.getBlock(i, j, k)
				if (block === Blocks.air) continue
				
				val meta = player.worldObj.getBlockMetadata(i, j, k)
				
				if (OreDictionary.getOreIDs(ItemStack(block, 1, meta)).any { OreDictionary.getOreName(it).startsWith("ore") })
					Botania.proxy.wispFX(player.worldObj, i + 0.5, j + 0.5, k + 0.5, 0f, 1f, 0f, 0.25f)
			}
		}
		
		Botania.proxy.setWispFXDepthTest(true)
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun doLeprechaun(e: LivingHurtEvent) {
		val player = e.source.entity as? EntityPlayer ?: return
		
		if (AlfheimConfigHandler.enableElvenStory && player.race === LEPRECHAUN && e.source.damageType == "player" && !isAbilityDisabled(player))
			e.ammount *= 1.1f
	}
	
	fun doSpriggan(player: EntityPlayer) {
		if (isAbilityDisabled(player)) return
		
		if (ASJUtilities.isServer) {
			player.removePotionEffect(Potion.nightVision.id)
			player.removePotionEffect(Potion.blindness.id)
		} else {
			player.removePotionEffectClient(Potion.nightVision.id)
			player.removePotionEffectClient(Potion.blindness.id)
			mc.gameSettings.gammaSetting = 10f
		}
	}
	
	fun fixSpriggan() {
		if (AlfheimConfigHandler.enableElvenStory && mc.thePlayer.race == SPRIGGAN && !isAbilityDisabled(mc.thePlayer)) return
		mc.gameSettings.gammaSetting = min(1f, mc.gameSettings.gammaSetting)
	}
	
	fun doUndine(player: EntityPlayer) {
		if (!isAbilityDisabled(player)) {
			player.air = 300
			
			if (player.ticksExisted % 20 == 0 && player.isInWater) ManaItemHandler.dispatchMana(ItemStack(Blocks.stone), player, 1, true)
		}
	}
	
	fun isAbilityDisabled(player: EntityPlayer) =
		if (ASJUtilities.isServer)
			!CardinalSystem.forPlayer(player).esmAbility
		else
			!PlayerSegmentClient.esmAbility
}

object ElvenFlightHandler {
	
	@SubscribeEvent
	fun onEntityConstructing(e: EntityConstructing) {
		if (!AlfheimConfigHandler.enableElvenStory) return
		if (e.entity !is EntityPlayer) return
		ElvenFlightHelper.ensureExistence(e.entity as EntityPlayer)
	}
	
	@SubscribeEvent
	fun onClonePlayer(e: PlayerEvent.Clone) {
		if (!AlfheimConfigHandler.enableElvenStory) return
		e.entityPlayer.flight = if (e.wasDeath) 0.0 else e.original.flight
	}
	
	@SubscribeEvent
	fun onPlayerRespawn(e: PlayerRespawnEvent) {
		if (!AlfheimConfigHandler.enableElvenStory) return
		if (AlfheimConfigHandler.wingsBlackList.contains(e.player.worldObj.provider.dimensionId)) return
		e.player.capabilities.allowFlying = e.player.race != HUMAN
	}
	
	@SubscribeEvent
	fun onPlayerChangeDimension(e: PlayerChangedDimensionEvent) {
		if (!AlfheimConfigHandler.enableElvenStory) return
		if (e.player !is EntityPlayerMP) return
		AlfheimCore.network.sendTo(Message2d(Message2d.M2d.ATTRIBUTE, 1.0, e.player.flight), e.player as EntityPlayerMP)
	}
	
	@SubscribeEvent
	fun onPlayerUpdate(e: PlayerTickEvent) {
		if (e.phase == TickPhase.START) return
		val player = e.player
		
		--KeyBindingHandler.flightEnableCooldown
		
		if (!AlfheimConfigHandler.enableElvenStory || player.race == HUMAN || ESMHandler.isAbilityDisabled(player)) return
		if ((ModItems.flightTiara as ItemFlightTiara).shouldPlayerHaveFlight(player))
			return ElvenFlightHelper.regen(player, if (player.moveForward == 0f && player.moveStrafing == 0f && player.onGround && player.isSneaking) 2 else 1)
			
		if (player.flight >= 0 && player.flight <= ElvenFlightHelper.max) {
			if (player.capabilities.isFlying) {
				if (player.isSprinting) player.moveFlying(0f, 1f, 0.00625f)
				ElvenFlightHelper.sub(player, if (player.isSprinting) 3 else if (abs(player.motionX) > 1e-4f || player.motionY > 1e-4f || abs(player.motionZ) > 1e-4f) 2 else 1)
			} else {
				ElvenFlightHelper.regen(player, if (player.moveForward == 0f && player.moveStrafing == 0f && player.onGround && player.isSneaking) 2 else 1)
			}
		}
		
		if (player.flight <= 0) {
			player.capabilities.isFlying = false
			KeyBindingHandler.flightEnableCooldown = (ElvenFlightHelper.max / 10).I + 1
		}
	}
	
	@SubscribeEvent
	fun onPlayerSlept(e: PlayerWakeUpEvent) {
		if (!AlfheimConfigHandler.enableElvenStory) return
		e.entityPlayer.flight = ElvenFlightHelper.max
	}
}