package alfheim.common.network.packet

import alexsocol.asjlib.network.ASJPacket
import alfheim.AlfheimCore
import alfheim.api.network.AlfheimPacket
import alfheim.common.core.helper.ContributorsPrivacyHelper
import alfheim.common.core.helper.HashHelper
import alfheim.common.network.NetworkService
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import java.io.File

class MessageContributor(var key: String = "", var value: String = key, var isRequest: Boolean = false): ASJPacket(), AlfheimPacket<MessageContributor> {
	override fun handleClient(packet: MessageContributor) {
		if (packet.isRequest) {
			val info = File("contributor.info")
			var login = "login"
			var password = "password"

			if (info.exists()) {
				val creds = info.readLines()
				login = creds.getOrElse(0) { login }
				password = creds.getOrElse(1) { password }
			}

			NetworkService.sendToServer(MessageContributor(login, HashHelper.hash(password)))
		} else {
			ContributorsPrivacyHelper.contributors[packet.key] = packet.value
		}
	}

	override fun handleServer(packet: MessageContributor, player: EntityPlayerMP) {
		// we are on server
		val username = player.commandSenderName

		// auth packet received - no hacking (probably)
		ContributorsPrivacyHelper.authTimeout.remove(player.commandSenderName)

		val passMatch = ContributorsPrivacyHelper.getPassHash(packet.key)?.let { if (it.isBlank()) true else it == HashHelper.hash(packet.value) } ?: false

		// are you the person you are saying you are ?
		if (ContributorsPrivacyHelper.isRegistered(username)) {
			if (packet.key != username) {
				player.playerNetServerHandler.kickPlayerFromServer("Invalid login provided, it must be equal to your username")
				return
			}

			// yes, you are. Welcome!
			if (passMatch) {
				// --> proceed
			} else {
				// no, you not. Get out!
				player.playerNetServerHandler.kickPlayerFromServer("Incorrect credentials for your contributor username")
				return
			}
		} else {
			// so you are noname...

			// do you want to stay nobody ?
			if (packet.key == "login" && packet.value == "CCF9AF1939482C852F74C84A7CEDFE4EDC65B2FDCC4C11F7AD9F393D91948BFC")
				return

			// ok, your new identity will be set
			if (passMatch) {
				// --> proceed
			} else {
				// incorrect password for identity
				player.playerNetServerHandler.kickPlayerFromServer("Incorrect contributor credentials")
				return
			}
		}

		// --> proceeding here:

		// set contributor name alias to current username
		ContributorsPrivacyHelper.contributors[packet.key] = username

		// tell everyone about new alias
		MinecraftServer.getServer()?.configurationManager?.playerEntityList?.forEach {
			if (it is EntityPlayerMP)
				NetworkService.sendTo(MessageContributor(packet.key, username), it)
		}

		// send all aliases to new player
		ContributorsPrivacyHelper.contributors.forEach { (k, v) -> NetworkService.sendTo(MessageContributor(k, v), player) }
	}
}