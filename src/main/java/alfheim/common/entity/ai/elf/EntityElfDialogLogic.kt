package alfheim.common.entity.ai.elf

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.api.entity.EnumRace
import alfheim.common.core.handler.CardinalSystem
import alfheim.common.entity.EntityElf
import alfheim.common.network.M1d
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.Message1d
import com.google.gson.Gson
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.event.ClickEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.event.entity.living.LivingEvent
import java.io.File
import java.io.Serializable
import java.nio.file.*
import java.util.stream.Stream

object EntityElfDialogLogic {
	
	val commonDialogs = Array(EnumRace.values().size - 1) { ArrayList<DialogPattern>() }
	val merchantDialogs = Array(EnumRace.values().size - 2) { ArrayList<DialogPattern>() }
	val guardDialogs = Array(EnumRace.values().size - 2) { ArrayList<DialogPattern>() }
	
	fun launchDialog(elf: EntityElf, speaker: EntityPlayer): Dialog? {
		val lvl = CardinalSystem.ElvenReputationSystem.getReputationLevel(speaker, elf.race)
		if (lvl < -1) {
			ASJUtilities.say(speaker, "dialog.nodialog$lvl")
			return null
		}
		
		val pattern = when (elf.job) {
			EntityElf.EnumElfJob.WILD     -> commonDialogs[0].random(elf.rng)
			EntityElf.EnumElfJob.CITIZEN  -> commonDialogs[elf.race.ordinal].random(elf.rng)
			EntityElf.EnumElfJob.MERCHANT -> {
				if (elf.jobSubrole == 0) return JunkmanDialog(elf, speaker)
				
				merchantDialogs[elf.race.ordinal - 1].random(elf.rng) // TODO add product types
			}
			EntityElf.EnumElfJob.GUARD    -> guardDialogs[elf.race.ordinal - 1].random(elf.rng)
			EntityElf.EnumElfJob.PRAETOR  -> TODO()
			EntityElf.EnumElfJob.PRIEST   -> TODO()
		} ?: return null
		
		return Dialog(pattern, elf, speaker).start()
	}
	
	@SubscribeEvent
	fun endDialogIfPlayerLeft(e: LivingEvent.LivingUpdateEvent) {
		val elf = e.entityLiving as? EntityElf ?: return
		val player = elf.interactor ?: return
		if (Vector3.entityDistance(elf, player) < 5) return
		
		if (player is EntityPlayerMP)
			NetworkService.sendTo(Message1d(M1d.RLCM, 0.0), player)
		
		elf.dialog?.end()
	}
	
	@SubscribeEvent
	fun chat(e: ServerChatEvent) {
		val (x, y, z) = Vector3.fromEntity(e.player)
		val list = getEntitiesWithinAABB(e.player.worldObj, EntityElf::class.java, getBoundingBox(x, y, z).expand(5))
		val chattingWith = list.firstOrNull { it.dialog != null && it.interactor === e.player } ?: return
		val response = chattingWith.dialog!!.pattern.conversations[e.message] ?: return

		NetworkService.sendTo(Message1d(M1d.RLCM, 1.0), e.player)
		
		val wontEnd = response.continuation.isNotEmpty()
		
		if (wontEnd)
			ASJUtilities.say(e.player, "\u00a7b\u00a7m" + " ".repeat(64))
		
		response.answerlines.forEach { ASJUtilities.say(e.player, it) }
		
		if (response.answerlines.isNotEmpty())
			ASJUtilities.say(e.player, "")
		
		if (wontEnd)
			addDialogOptions(e.player, response.continuation)
		else
			chattingWith.dialog!!.end()
		
		e.isCanceled = true
	}
	
	val gson = Gson()
	
	init {
		eventForge()
		
		val uri = this::class.java.getResource("/assets/${ModInfo.MODID}/dialogs/")!!.toURI()
		val root = if (uri.scheme.equals("jar")) {
			val fileSystem: FileSystem = FileSystems.newFileSystem(uri, emptyMap<String, Any>())
			fileSystem.getPath("/assets/${ModInfo.MODID}/dialogs/")
		} else {
			Paths.get(uri)
		}
		
		val walk: Stream<Path> = Files.walk(root, 1)
		for (dir in walk) {
			val files = dir.toFile().listFiles() ?: continue
			for (file in files) {
				if (!file.isFile) continue
				
				when (file.parentFile.name) {
					"common" -> registerDialog(file, commonDialogs)
					"guard"  -> registerDialog(file, guardDialogs)
					"merchant"  -> registerDialog(file, merchantDialogs)
				}
			}
		}
	}
	
	fun registerDialog(file: File, dialogs: Array<ArrayList<DialogPattern>>) {
		val pattern = gson.fromJson(file.readText(), DialogPattern::class.java)
		
		if (file.name.startsWith('A'))
			return dialogs.forEach { it += pattern }
		
		if (file.name.startsWith('E'))
			return dialogs.forEachIndexed { id, it -> if (id != 0) it += pattern }
		
		dialogs[file.name[0].code].add(pattern)
	}
}

open class Dialog(val pattern: DialogPattern, val elf: EntityElf, val player: EntityPlayer) {
	
	open fun start(): Dialog? {
		pattern.greet(elf, player)
		
		if (pattern.startingLines.isEmpty()) {
			pattern.goodbye(elf, player)
			return null
		}
		
		addDialogOptions(player, pattern.startingLines)
		
		return this
	}
	
	open fun onInteract(elf: EntityElf, player: EntityPlayer) = Unit
	
	open fun end() {
		pattern.goodbye(elf, player)
		elf.dialog = null
		elf.interactor = null
	}
}


@Suppress("ArrayInDataClass")
data class DialogPattern(val greetingsByLevel: Array<String>, val goodbyesByLevel: Array<String>, val startingLines: Array<String>, val conversations: HashMap<String, Response>): Serializable {
	
	fun greet(elf: EntityElf, player: EntityPlayer) {
		ASJUtilities.say(player, "\u00a7b\u00a7m" + " ".repeat(64))
		ASJUtilities.say(player, greetingsByLevel[CardinalSystem.ElvenReputationSystem.getReputationLevel(player, elf.race)], player.commandSenderName)
		ASJUtilities.say(player, "")
	}
	
	fun goodbye(elf: EntityElf, player: EntityPlayer) {
		ASJUtilities.say(player, "\u00a7b\u00a7m" + " ".repeat(64))
		ASJUtilities.say(player, goodbyesByLevel[CardinalSystem.ElvenReputationSystem.getReputationLevel(player, elf.race)])
		ASJUtilities.say(player, "")
	}
}

@Suppress("ArrayInDataClass")
data class Response(val answerlines: Array<String>, val continuation: Array<String>): Serializable

val JunkmanDialogPattern = DialogPattern(emptyArray(), emptyArray(), emptyArray(), hashMapOf()) // TODO

class JunkmanDialog(elf: EntityElf, player: EntityPlayer): Dialog(JunkmanDialogPattern, elf, player) {
	
	override fun onInteract(elf: EntityElf, player: EntityPlayer) {
	
	}
}

private fun addDialogOptions(player: EntityPlayer, options: Array<String>) = options.forEach { player.addChatComponentMessage(ChatComponentText(EnumChatFormatting.AQUA + "* ").appendSibling(ChatComponentTranslation(it)).apply { chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, it) }) }