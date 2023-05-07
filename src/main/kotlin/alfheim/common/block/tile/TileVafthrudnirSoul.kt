package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.core.handler.*
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraftforge.event.ServerChatEvent
import java.awt.Color
import java.util.*
import kotlin.math.min

class TileVafthrudnirSoul: TileRainbowManaFlame() {
	
	var started = false
	var charadeTimer = 0
	var questionNumber = -1
	var answers = intArrayOf()
	var interactor = ""
	var correctIndex = -1
	var answering = false
	var answeringTimer = 0
	
	init {
		soul = true
	}
	
	override fun getColor() = Color(0xFF8080).rgb
	
	override fun updateEntity() {
		super.updateEntity()
		
		if (worldObj.isRemote || !started) return
		if (answering) answerPlayer()
		
		if (charadeTimer++ > 1200) incorrectOrTimeout()
	}
	
	fun startCharades(player: EntityPlayer) {
		if (player !is EntityPlayerMP) return
		if (!RagnarokHandler.ginnungagap || started || CardinalSystem.KnowledgeSystem.know(player, CardinalSystem.KnowledgeSystem.Knowledge.ABYSS_TRUTH)) return
		
		started = true
		interactor = player.commandSenderName
		ASJUtilities.say(player, "charades.started")
		nextQuestion(player)
	}
	
	fun nextQuestion(player: EntityPlayer) {
		if (worldObj.isRemote) return
		
		if (++questionNumber >= correctList.size) {
			answering = true
			return
		}
		
		val amplifier = min(2, player.getActivePotionEffect(AlfheimConfigHandler.potionIDWisdom)?.amplifier ?: -1)
		
		answers = when(amplifier) {
			-1 -> answersList[questionNumber].toList().shuffled().take(4)
			0 -> {
				val hasTrue = ASJUtilities.chance(10)
				answersList[questionNumber].toList().shuffled().take(if (hasTrue) 3 else 4).run {
					if (hasTrue) toMutableList().apply { add(correctList[questionNumber]); shuffle() } else this
				}
			}
			1 -> {
				val hasTrue = ASJUtilities.chance(50)
				answersList[questionNumber].toList().shuffled().take(if (hasTrue) 1 else 2).run {
					if (hasTrue) toMutableList().apply { add(correctList[questionNumber]); shuffle() } else this
				}
			}
			2 -> {
				listOf(correctList[questionNumber])
			}
			else -> return
		}.toIntArray()
		
		correctIndex = answers.indexOf(correctList[questionNumber])
		
		ASJUtilities.say(player, "charades.question${questionNumber+1}")
		
		for ((id, answer) in answers.withIndex()) {
			ASJUtilities.say(player, "charades.answer$answer", 'a'.code.plus(id).toChar())
		}
		
		charadeTimer = 0
	}
	
	fun answerPlayer() {
		if (++answeringTimer % 50 != 0) return
		
		val player = MinecraftServer.getServer().configurationManager.func_152612_a(interactor) ?: return
		val i = answeringTimer / 50
		
		if (i < 7) {
			if (i % 2 == 1) ASJUtilities.say(player, "charades.final$i", player.commandSenderName)
			else ASJUtilities.say(player, "charades.final$i")
		}
		
		if (i != 7) return
		
		ASJUtilities.say(player, "charades.completed")
		player.removePotionEffect(AlfheimConfigHandler.potionIDWisdom)
		CardinalSystem.KnowledgeSystem.learn(player, CardinalSystem.KnowledgeSystem.Knowledge.ABYSS_TRUTH)
		reset()
	}
	
	fun incorrectOrTimeout() {
		reset()
		
		val player = MinecraftServer.getServer().configurationManager.func_152612_a(interactor) ?: return
		ASJUtilities.say(player, "charades.failed")
		
		val (x, y, z) = worldObj.provider.randomizedSpawnPoint
		player.setPositionAndUpdate(x + 0.5, y + 0.5, z + 0.5)
	}
	
	fun reset() {
		started = false
		questionNumber = -1
		answers = intArrayOf()
		correctIndex = -1
		charadeTimer = 0
		answering = false
		answeringTimer = 0
	}
	
	companion object {
		
		var rng: Random
		var numbers: MutableList<Int>
		var answersList: Array<IntArray>
		var correctList: Array<Int>
		
		init {
			eventForge()
			
			rng = Random("The Lay Of Vafthrudnir".hashCode().toLong())
			numbers = Array(35) { it }.toMutableList()
			answersList = Array(7) { IntArray(4) { numbers.random(rng)!!.also { numbers.remove(it) } + 1 } }
			correctList = Array(7) { numbers.random(rng)!!.also { numbers.remove(it) } + 1 }
		}
		
		@SubscribeEvent
		fun onSomeoneSaidSomething(e: ServerChatEvent) {
			val player = e.player
			val soul = player.worldObj.loadedTileEntityList.filterIsInstance<TileVafthrudnirSoul>().minByOrNull { Vector3.entityTileDistance(player, it) } ?: return
			if (!soul.started || player.commandSenderName != soul.interactor || Vector3.entityTileDistance(player, soul) > 5) return
			
			val correctIndex = soul.correctIndex
			if (correctIndex == -1)
				soul.incorrectOrTimeout()
			else {
				if (AlfheimConfigHandler.chatLimiters.format('a'.code.plus(correctIndex).toChar()) == e.message)
					soul.nextQuestion(player)
				else
					soul.incorrectOrTimeout()
			}
			
			e.isCanceled = true
		}
	}
}