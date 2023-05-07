package alfheim.common.core.command

import alexsocol.asjlib.ASJReflectionHelper
import alexsocol.asjlib.ASJUtilities
import alfheim.common.core.handler.CardinalSystem
import alfheim.common.core.handler.SheerColdHandler.cold
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import net.minecraft.command.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer

object CommandDebug: CommandBase() {
	
	override fun getCommandName() = "adbg"
	
	override fun getCommandUsage(sender: ICommandSender) = "/adbg"
	
	override fun getRequiredPermissionLevel() = MinecraftServer.getServer().opPermissionLevel
	
	override fun processCommand(sender: ICommandSender, args: Array<out String>) {
		when (args[0]) {
			"cold" -> {
				if (sender is EntityLivingBase) ASJUtilities.say(sender, "${sender.cold}")
			}
			"know" -> {
				val target = args.getOrNull(1)?.let {
					MinecraftServer.getServer().configurationManager.func_152612_a(it)
				} ?: sender as? EntityPlayerMP ?:
				throw WrongUsageException("/adbg know [player]", 0)
				
				ASJUtilities.say(sender, CardinalSystem.forPlayer(target).knowledge.toString())
			}
			"rag" -> {
				var gotObj: Any? = null
				
				when (args[1]) {
					"winter" -> {
						if (args[2] == "get") gotObj = RagnarokHandler.winter else if (args[2] == "set") RagnarokHandler.winter = args[3].toBoolean()
					}
					"winterTicks" -> {
						if (args[2] == "get") gotObj = RagnarokHandler.winterTicks else if (args[2] == "set") RagnarokHandler.winterTicks = args[3].toInt()
					}
					"summer" -> {
						if (args[2] == "get") gotObj = RagnarokHandler.summer else if (args[2] == "set") RagnarokHandler.summer = args[3].toBoolean()
					}
					"summerTicks" -> {
						if (args[2] == "get") gotObj = RagnarokHandler.summerTicks else if (args[2] == "set") RagnarokHandler.summerTicks = args[3].toInt()
					}
					"ragnarok" -> {
						if (args[2] == "get") gotObj = RagnarokHandler.ragnarok else if (args[2] == "set") RagnarokHandler.ragnarok = args[3].toBoolean()
					}
					"ragnarokTicks" -> {
						if (args[2] == "get") gotObj = RagnarokHandler.ragnarokTicks else if (args[2] == "set") RagnarokHandler.ragnarokTicks = args[3].toInt()
					}
					"noSunAndMoon" -> {
						if (args[2] == "get") gotObj = RagnarokHandler.noSunAndMoon else if (args[2] == "set") RagnarokHandler.noSunAndMoon = args[3].toBoolean()
					}
					"ginnungagap" -> {
						if (args[2] == "get") gotObj = RagnarokHandler.ginnungagap else if (args[2] == "set") RagnarokHandler.ginnungagap = args[3].toBoolean()
					}
					"finished" -> {
						if (args[2] == "get") gotObj = RagnarokHandler.finished else if (args[2] == "set") RagnarokHandler.finished = args[3].toBoolean()
					}
					"blockedPowers" -> {
						if (args[2] == "get") gotObj = RagnarokHandler.blockedPowers
					}
					"func" -> {
						gotObj = ASJReflectionHelper.invoke<RagnarokHandler, Any>(RagnarokHandler, emptyArray(), arrayOf<Any>(args[2], emptyArray<Class<*>>()))
					}
					else -> throw WrongUsageException("Invalid 2nd argument ${args[1]}")
				}
				
				if (gotObj == null) return
				ASJUtilities.say(sender, gotObj.toString())
			}
			else -> throw WrongUsageException("Invalid 1st argument ${args[0]}")
		}
	}
}