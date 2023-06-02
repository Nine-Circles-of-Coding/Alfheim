package alfheim.common.network.packet

import alexsocol.asjlib.mc
import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.common.item.relic.LeashingHandler.leashedTo
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer

class MessageGleipnirLeash(var targetID: String, var playerName: String): ASJPacket(), AlfheimPacket<MessageGleipnirLeash> {
	override fun handleClient(packet: MessageGleipnirLeash) {
		val world = mc.theWorld
		val target = world.loadedEntityList.firstOrNull { (it as Entity).entityId.toString() == packet.targetID } as? EntityLivingBase ?: return
		val actor = world.playerEntities.firstOrNull { (it as EntityPlayer).commandSenderName == packet.playerName } as? EntityPlayer

		if (packet.playerName.isEmpty()) {
			target.leashedTo = null
		} else {
			target.leashedTo = actor ?: return
		}
	}
}