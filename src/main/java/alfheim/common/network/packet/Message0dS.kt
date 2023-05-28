package alfheim.common.network.packet

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.common.item.equipment.bauble.*
import alfheim.common.network.M0ds
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.network.simpleimpl.IMessage
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.equipment.bauble.ItemTravelBelt

class Message0dS(ty: M0ds, var type: Int = ty.ordinal): ASJPacket(), AlfheimPacket<Message0dS> {
	override fun handleServer(packet: Message0dS, player: EntityPlayerMP) {
		when (M0ds.values()[packet.type]) {
			M0ds.DODGE     -> dodge(player)
			M0ds.JUMP      -> jump(player)
			M0ds.HEIMBLINK -> blink(player)
		}
	}

	private fun dodge(player: EntityPlayerMP) {
		player.playSoundAtEntity("botania:dash", 1f, 1f)

		val baublesInv = PlayerHandler.getPlayerBaubles(player)
		var ringStack: ItemStack? = baublesInv[1]

		if (ringStack == null || ringStack.item !is ItemDodgeRing) {
			ringStack = baublesInv[2]
			if (ringStack == null || ringStack.item !is ItemDodgeRing) {
				player.playerNetServerHandler.kickPlayerFromServer(StatCollector.translateToFallback("alfheimmisc.invalidDodge"))
				return
			}
		}

		if (ItemNBTHelper.getInt(ringStack, ItemDodgeRing.TAG_DODGE_COOLDOWN, 0) > 0) {
			player.playerNetServerHandler.kickPlayerFromServer(StatCollector.translateToFallback("alfheimmisc.invalidDodge"))
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

	fun blink(player: EntityPlayerMP): IMessage? {
		if (ItemPriestCloak.getCloak(4, player) != null) {
			val look = player.lookVec
			val dist = 6.0
			val (x, y, z) = Vector3.fromEntity(player).add(Vector3(look).mul(dist))

			if (!player.worldObj.getBlock(x.I, y.I, z.I).isNormalCube && !player.worldObj.getBlock(x.I, y.I + 1, z.I).isNormalCube) {
				player.playerNetServerHandler.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch)
				// ctx.serverHandler.func_184342_d() captureCurrentPosition ???
				player.worldObj.playSoundEffect(x, y, z, "mob.endermen.portal", 1f, 1f)
				player.playSound("mob.endermen.portal", 1f, 1f)
			}
		}

		return null
	}
}