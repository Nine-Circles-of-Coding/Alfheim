package alfheim.common.core.command

import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.network.*
import alfheim.common.network.packet.Message0dC
import net.minecraft.command.*
import net.minecraft.entity.player.EntityPlayerMP

object CommandMTSpellInfo: CommandBase() {
	
	override fun getCommandName() = "mtspell"
	override fun getCommandUsage(sender: ICommandSender) = "/$commandName"
	override fun getRequiredPermissionLevel() = 0
	
	override fun processCommand(sender: ICommandSender?, params: Array<String>?) {
		if (sender !is EntityPlayerMP)
			return
		
		if (!AlfheimConfigHandler.enableMMO)
			throw WrongUsageException("alfheim.commands.mtspell.wrong")
		
		NetworkService.sendTo(Message0dC(M0dc.MTSPELL), sender)
	}
}