package alfheim.common.core.handler

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.AlfheimCore
import alfheim.api.event.PlayerInteractAdequateEvent
import alfheim.client.core.handler.KeyBindingHandlerClient
import alfheim.common.block.tile.TileRaceSelector
import alfheim.common.core.helper.*
import alfheim.common.entity.EntityLolicorn
import alfheim.common.item.equipment.bauble.*
import alfheim.common.network.*
import alfheim.common.network.Message0dS.M0ds
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.network.simpleimpl.*
import net.minecraft.entity.player.*
import net.minecraft.item.ItemStack
import net.minecraft.network.NetHandlerPlayServer
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ChatComponentTranslation
import net.minecraftforge.common.MinecraftForge
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.equipment.bauble.ItemTravelBelt
import kotlin.math.max

object PacketHandlerServer: IMessageHandler<IMessage?, IMessage?> {
	
	override fun onMessage(message: IMessage?, ctx: MessageContext): IMessage? {
		return when (message) {
			is Message0dS              -> handle(message, ctx)
			is MessageContributor      -> handle(message, ctx)
			is MessageHotSpellS        -> { CardinalSystem.HotSpellsSystem.setHotSpellID(ctx.serverHandler.playerEntity, message.slot, message.id); return null }
			is MessageKeyBindS         -> handle(message, ctx)
			is MessageNI               -> handle(message, ctx)
			is MessageRaceSelection    -> handle(message, ctx)
			null                       -> return null
			else                       -> throw IllegalArgumentException("Unknown message type: ${message::class.java}")
		}
	}
	
	fun handle(message: Message0dS, ctx: MessageContext): IMessage? {
		when (M0ds.values()[message.type]) {
			M0ds.DODGE     -> DOGIE(ctx.serverHandler)
			M0ds.JUMP      -> jump(ctx.serverHandler.playerEntity)
			M0ds.HEIMBLINK -> blink(ctx.serverHandler)
		}
		
		return null
	}
	
	fun handle(message: MessageContributor, ctx: MessageContext): IMessage? {
		// we are on server
		val player = ctx.serverHandler.playerEntity
		val username = player.commandSenderName
		
		// auth packet received - no hacking (probably)
		ContributorsPrivacyHelper.authTimeout.remove(player.commandSenderName)
		
		val passMatch = ContributorsPrivacyHelper.getPassHash(message.key)?.let { if (it.isBlank()) true else it == HashHelper.hash(message.value) } ?: false
		
		// are you the person you are saying you are ?
		if (ContributorsPrivacyHelper.isRegistered(username)) {
			if (message.key != username) {
				player.playerNetServerHandler.kickPlayerFromServer("Invalid login provided, it must be equal to your username")
				return null
			}
			
			// yes, you are. Welcome!
			if (passMatch) {
				// --> proceed
			} else {
				// no, you not. Get out!
				player.playerNetServerHandler.kickPlayerFromServer("Incorrect credentials for your contributor username")
				return null
			}
		} else {
			// so you are noname...
			
			// do you want to stay nobody ?
			if (message.key == "login" && message.value == "CCF9AF1939482C852F74C84A7CEDFE4EDC65B2FDCC4C11F7AD9F393D91948BFC")
				return null
			
			// ok, your new identity will be set
			if (passMatch) {
				// --> proceed
			} else {
				// incorrect password for identity
				player.playerNetServerHandler.kickPlayerFromServer("Incorrect contributor credentials")
				return null
			}
		}
		
		// --> proceeding here:
		
		// set contributor name alias to current username
		ContributorsPrivacyHelper.contributors[message.key] = username
		
		// tell everyone about new alias
		MinecraftServer.getServer()?.configurationManager?.playerEntityList?.forEach {
			if (it is EntityPlayerMP)
				AlfheimCore.network.sendTo(MessageContributor(message.key, username), it)
		}
		
		// send all aliases to new player
		ContributorsPrivacyHelper.contributors.forEach { (k, v) -> AlfheimCore.network.sendTo(MessageContributor(k, v), player) }
		
		return null
	}
	
	fun handle(message: MessageKeyBindS, ctx: MessageContext): IMessage? {
		val player = ctx.serverHandler.playerEntity
		
		when (KeyBindingHandlerClient.KeyBindingIDs.values()[message.action]) {
			KeyBindingHandlerClient.KeyBindingIDs.CORN    -> EntityLolicorn.call(player)
			KeyBindingHandlerClient.KeyBindingIDs.FLIGHT  -> KeyBindingHandler.enableFlight(player, message.state)
			KeyBindingHandlerClient.KeyBindingIDs.ESMABIL -> KeyBindingHandler.toggleESMAbility(player)
			KeyBindingHandlerClient.KeyBindingIDs.CAST    -> KeyBindingHandler.cast(player, message.state, message.data)
			KeyBindingHandlerClient.KeyBindingIDs.UNCAST  -> KeyBindingHandler.unCast(player)
			KeyBindingHandlerClient.KeyBindingIDs.SEL     -> KeyBindingHandler.select(player, message.state, message.data)
			KeyBindingHandlerClient.KeyBindingIDs.SECRET  -> KeyBindingHandler.secret(player)
		}
		return null
	}
	
	private operator fun IntArray.component6() = this[5]
	private operator fun IntArray.component7() = this[6]
	
	fun handle(m: MessageNI, ctx: MessageContext): IMessage? {
		when (MessageNI.Mni.values()[m.type]) {
			MessageNI.Mni.INTERACTION -> run {
				val player = ctx.serverHandler.playerEntity
				val (left, type, x, y, z, side, id) = m.intArray
				val entity = ctx.serverHandler.playerEntity.worldObj.getEntityByID(id)
				
				if (AlfheimConfigHandler.interactEventChecks) {
					val src = Vector3.fromEntity(player)
					val dst = if (y != -1) Vector3(x, y, z) else if (entity != null) Vector3.fromEntity(entity) else src
					if (Vector3.vecDistance(src, dst) > player.theItemInWorldManager.blockReachDistance + if (entity != null) max(entity.width, entity.height) else 0f)
						return@run
				}
				
				MinecraftForge.EVENT_BUS.post(
					if (left == 1)
						PlayerInteractAdequateEvent.LeftClick(player, PlayerInteractAdequateEvent.LeftClick.Action.values()[type], x, y, z, side, entity)
				    else
						PlayerInteractAdequateEvent.RightClick(player, PlayerInteractAdequateEvent.RightClick.Action.values()[type], x, y, z, side, entity))
			}
			MessageNI.Mni.BLIZZARD,
			MessageNI.Mni.HEARTLOSS,
			MessageNI.Mni.WINGS_BL    -> Unit // client
		}
		
		return null
	}
	
	fun handle(message: MessageRaceSelection, ctx: MessageContext): IMessage? {
		if (Vector3.vecEntityDistance(Vector3(message.x, message.y, message.z), ctx.serverHandler.playerEntity) > 5) return null
		
		val world = MinecraftServer.getServer().worldServerForDimension(message.dim) ?: return null
		val tile = world.getTileEntity(message.x, message.y, message.z) as? TileRaceSelector ?: return null
		
		if (message.doMeta) {
			world.setBlockMetadataWithNotify(message.x, message.y, message.z, message.meta, 3)
			
			tile.custom = message.custom
			tile.female = message.female
		}
		
		tile.activeRotation = message.arot
		tile.rotation = message.rot
		tile.timer = message.timer
		
		if (message.give) tile.giveRaceAndReset(ctx.serverHandler.playerEntity)
		
		ASJUtilities.dispatchTEToNearbyPlayers(tile)
		
		return null
	}
	
	fun blink(sh: NetHandlerPlayServer): IMessage? {
		val player = sh.playerEntity
		if (ItemPriestCloak.getCloak(4, player) != null) {
			val look = player.lookVec
			val dist = 6.0
			val (x, y, z) = Vector3.fromEntity(player).add(Vector3(look).mul(dist))
			
			if (!player.worldObj.getBlock(x.I, y.I, z.I).isNormalCube && !player.worldObj.getBlock(x.I, y.I + 1, z.I).isNormalCube) {
				sh.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch)
				// ctx.serverHandler.func_184342_d() captureCurrentPosition ???
				player.worldObj.playSoundEffect(x, y, z, "mob.endermen.portal", 1f, 1f)
				player.playSound("mob.endermen.portal", 1f, 1f)
			}
		}
		
		return null
	}
	
	private fun DOGIE(sh: NetHandlerPlayServer) {
		val player = sh.playerEntity
		
		player.playSoundAtEntity("botania:dash", 1f, 1f)
		
		val baublesInv = PlayerHandler.getPlayerBaubles(player)
		var ringStack: ItemStack? = baublesInv[1]
		
		if (ringStack == null || ringStack.item !is ItemDodgeRing) {
			ringStack = baublesInv[2]
			if (ringStack == null || ringStack.item !is ItemDodgeRing) {
				sh.netManager.closeChannel(ChatComponentTranslation("alfheimmisc.invalidDodge"))
				return
			}
		}
		
		if (ItemNBTHelper.getInt(ringStack, ItemDodgeRing.TAG_DODGE_COOLDOWN, 0) > 0) {
			sh.netManager.closeChannel(ChatComponentTranslation("alfheimmisc.invalidDodge"))
			return
		}
		
		player.addExhaustion(0.3f)
		ItemNBTHelper.setInt(ringStack, ItemDodgeRing.TAG_DODGE_COOLDOWN, ItemDodgeRing.MAX_CD)
	}
	
	private fun jump(player: EntityPlayerMP) {
		val baublesInv = PlayerHandler.getPlayerBaubles(player)
		val amuletStack = baublesInv[0]
		
		if (amuletStack != null && amuletStack.item is ItemCloudPendant) {
			player.addExhaustion(0.3f)
			player.fallDistance = 0f
			
			val belt = baublesInv[3]
			
			if (belt != null && belt.item is ItemTravelBelt) {
				val fall = (belt.item as ItemTravelBelt).fallBuffer
				player.fallDistance = -fall * (amuletStack.item as ItemCloudPendant).maxAllowedJumps
			}
		}
	}
}