package alfheim.common.core.command

import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.network.M0dc
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.Message0dC
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
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