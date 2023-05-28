package alfheim.common.core.command

import alexsocol.asjlib.*
import alfheim.AlfheimCore
import alfheim.api.ModInfo
import alfheim.api.event.AlfheimModeChangedEvent
import alfheim.common.achievement.AlfheimAchievements
import alfheim.common.block.BlockNiflheimPortal
import alfheim.common.block.tile.TileDomainLobby
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.CardinalSystem
import alfheim.common.core.handler.ESMHandler
import alfheim.common.crafting.recipe.AlfheimRecipes
import alfheim.common.network.M3d
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.Message3d
import alfheim.common.world.data.CustomWorldData.Companion.customData
import alfheim.common.world.dim.niflheim.ChunkProviderNiflheim
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.StatCollector
import net.minecraftforge.common.AchievementPage
import net.minecraftforge.common.MinecraftForge
import java.util.*

object CommandAlfheim: CommandBase() {
	
	override fun getCommandName() = "alfheim"
	override fun getCommandAliases() = listOf("alf")
	override fun getCommandUsage(sender: ICommandSender) = "/$commandName help"
	override fun getRequiredPermissionLevel() = MinecraftServer.getServer().opPermissionLevel
	
	override fun processCommand(sender: ICommandSender, args: Array<String>) {
		when (args.getOrNull(0)) {
			"help" -> help(sender)
			"mode" -> changeModes(sender, args)
			"randgen" -> printRandGen(sender)
			"knowledge" -> gainKnowledge(sender, args)
			"surtrregen" -> regenerateSurtrDomain(sender, args)
			else -> throw WrongUsageException(getCommandUsage(sender))
		}
	}
	
	override fun addTabCompletionOptions(sender: ICommandSender?, args: Array<String>): MutableList<Any?> {
		if (args.size == 1) return getListOfStringsMatchingLastWord(args, "help", "mode", "randgen", "knowledge")
		
		if (args.size == 2 || args.size == 3)
			when (args[0]) {
				"mode" -> return if (args.size == 2)
					getListOfStringsMatchingLastWord(args, "ESM", "MMO")
				else
					ArrayList()
				"randgen", "help" -> return ArrayList()
				"knowledge" -> return if (args.size == 2)
					getListOfStringsMatchingLastWord(args, "ALL", *CardinalSystem.KnowledgeSystem.Knowledge.values().map { it.toString() }.toTypedArray())
				else
					getListOfStringsMatchingLastWord(args, *MinecraftServer.getServer().configurationManager.allUsernames)
			}
		
		return ArrayList()
	}
	
	fun help(sender: ICommandSender) {
		ASJUtilities.say(sender, "/$commandName mode <ESM|MMO> - change current game mode")
		ASJUtilities.say(sender, "/$commandName randgen - print coordinates of random gen")
		ASJUtilities.say(sender, "/$commandName knowledge <knowledge> [player] - add knowledge to player")
		ASJUtilities.say(sender, "/$commandName surtrregen true - Reset Surtr domain data")
	}
	
	fun changeModes(sender: ICommandSender, args: Array<String>) {
		if (args.size != 2)
			throw WrongUsageException(getCommandUsage(sender))
		
		val esmOld = AlfheimConfigHandler.enableElvenStory
		val mmoOld = AlfheimConfigHandler.enableMMO
		
		when {
			args[1].equals("ESM", true) -> {
				AlfheimConfigHandler.enableElvenStory = !AlfheimConfigHandler.enableElvenStory
				toggleESM(AlfheimConfigHandler.enableElvenStory)
			}
			
			args[1].equals("MMO", true) -> {
				AlfheimConfigHandler.enableMMO = !AlfheimConfigHandler.enableMMO
				toggleMMO(AlfheimConfigHandler.enableMMO)
			}
			
			else                                     -> throw WrongUsageException(getCommandUsage(sender))
		}
		
		ASJUtilities.sayToAllOnline(String.format(StatCollector.translateToLocal("alfheim.commands.setmode.done"),
		                                          sender.commandSenderName,
		                                          if (AlfheimConfigHandler.enableElvenStory) EnumChatFormatting.GREEN else EnumChatFormatting.DARK_RED,
		                                          EnumChatFormatting.RESET,
		                                          if (AlfheimConfigHandler.enableMMO) EnumChatFormatting.GREEN else EnumChatFormatting.DARK_RED,
		                                          EnumChatFormatting.RESET))

		NetworkService.sendToAll(Message3d(M3d.TOGGLER, (if (args[1].equals("ESM", true)) 1 else 0).D, ((if (esmOld) 1 else 0) shl 1 or if (AlfheimConfigHandler.enableElvenStory) 1 else 0).D, ((if (mmoOld) 1 else 0) shl 1 or if (AlfheimConfigHandler.enableMMO) 1 else 0).D))
		
		MinecraftForge.EVENT_BUS.post(AlfheimModeChangedEvent(AlfheimConfigHandler.enableElvenStory, AlfheimConfigHandler.enableMMO, esmOld, mmoOld))
		
		ESMHandler.writeModes(AlfheimCore.save)
	}
	
	fun toggleESM(on: Boolean) {
		if (on) {
			AlfheimConfigHandler.initWorldCoordsForElvenStory(AlfheimCore.save)
			ESMHandler.checkAddAttrs()
			AchievementPage.getAchievementPage(ModInfo.MODID.capitalized()).achievements.add(AlfheimAchievements.newChance)
//			if (Botania.thaumcraftLoaded) ThaumcraftAlfheimModule.addESMRecipes()
		} else {
//			if (Botania.thaumcraftLoaded) ThaumcraftAlfheimModule.removeESMRecipes()
			AlfheimConfigHandler.enableMMO = false
			toggleMMO(AlfheimConfigHandler.enableMMO)
			AchievementPage.getAchievementPage(ModInfo.MODID.capitalized()).achievements.remove(AlfheimAchievements.newChance)
			
			MinecraftServer.getServer().configurationManager.playerEntityList.forEach { (it as EntityPlayerMP).capabilities.apply { isFlying = false; allowFlying = false } }
		}
	}
	
	fun toggleMMO(on: Boolean) {
		if (on) {
			CardinalSystem.load(AlfheimCore.save)
			AlfheimRecipes.addMMORecipes()
			AlfheimConfigHandler.enableElvenStory = true
			toggleESM(AlfheimConfigHandler.enableElvenStory)
			for (o in MinecraftServer.getServer().configurationManager.playerEntityList) CardinalSystem.transfer(o as EntityPlayerMP)
		} else {
			CardinalSystem.save()
			AlfheimRecipes.removeMMORecipes()
		}
	}
	
	fun printRandGen(sender: ICommandSender) {
		val (nx, ny, nz) = BlockNiflheimPortal.onlyPortalPosition(MinecraftServer.getServer().worldServerForDimension(AlfheimConfigHandler.dimensionIDAlfheim))
		ASJUtilities.say(sender, "Portal to Niflheim at $nx $ny $nz")
		
		val tx = ASJUtilities.randInBounds(-1000, 1000, Random(MinecraftServer.getServer().worldServerForDimension(AlfheimConfigHandler.dimensionIDNiflheim).seed))
		val ty = 128
		val tz = ChunkProviderNiflheim.f(tx) + 16
		ASJUtilities.say(sender, "Thrym domain at $tx $ty $tz")
		
		val data = MinecraftServer.getServer().worldServerForDimension(-1).customData
		val (sx, sz) = data.structures.get("Surtr")?.firstOrNull() ?: (Int.MIN_VALUE to Int.MAX_VALUE)
		val sy = data.data["SurtrY"]?.toInt() ?: -1
		if (sx == Int.MIN_VALUE || sy == -1)
			ASJUtilities.say(sender, "Surtr domain not generated")
		else
			ASJUtilities.say(sender, "Surtr domain at $sx $sy $sz")
		
		val rand = Random(sender.entityWorld.seed)
		val vx = ASJUtilities.randInBounds(-256, 256, rand)
		val vz = ASJUtilities.randInBounds(-768, -256, rand)
		ASJUtilities.say(sender, "Vafthrudnir soul at $vx $vz")
	}
	
	fun gainKnowledge(sender: ICommandSender, args: Array<String>) {
		if (args.size !in 2..3) throw WrongUsageException(getCommandUsage(sender))
		if (args.size != 3 && sender !is EntityPlayerMP) return ASJUtilities.say(sender, "not player")
		
		val target = if (args.size == 3) MinecraftServer.getServer().configurationManager.func_152612_a(args[2]) else sender as? EntityPlayerMP
		if (target !is EntityPlayerMP) return ASJUtilities.say(sender, "not player")
		
		if (args[1] != "ALL") {
			val knowledge = CardinalSystem.KnowledgeSystem.Knowledge.valueOf(args[1])
			CardinalSystem.KnowledgeSystem.learn(target, knowledge)
		} else {
			CardinalSystem.KnowledgeSystem.Knowledge.values().forEach {
				CardinalSystem.KnowledgeSystem.learn(target, it)
			}
		}
	}
	
	fun regenerateSurtrDomain(sender: ICommandSender, args: Array<String>) {
		if (args.size == 1) ASJUtilities.say(sender, "Reset Surtr domain data. Pass 'true' as next parameter to approve it.")
		if (args.size != 2) throw WrongUsageException(getCommandUsage(sender))
		
		val world = MinecraftServer.getServer().worldServerForDimension(-1)
		val data = world.customData
		val structs = data.structures
		if (!structs.containsKey("Surtr")) return
		val y = data.data["SurtrY"]?.toInt() ?: return
		
		structs.get("Surtr").forEach { (x, z) ->
			if (world.getTileEntity(x, y - 5, z + 30) !is TileDomainLobby) return@forEach
			world.setBlockToAir(x, y - 5, z + 30)
		}
		
		data.structures.removeAll("Surtr")
		data.data.remove("SurtrY")
		
		ASJUtilities.say(sender, "Surtr domain data reset.")
	}
}
