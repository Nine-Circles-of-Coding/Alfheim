package alfheim.common.core.helper

import alexsocol.asjlib.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.MessageContributor
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.*
import net.minecraft.entity.player.*
import net.minecraft.server.MinecraftServer
import java.net.URL
import java.nio.charset.Charset
import java.security.*
import java.util.*
import javax.xml.bind.annotation.adapters.HexBinaryAdapter

object ContributorsPrivacyHelper {
	
	//  contributor - username alias
	val contributors = HashMap<String, String>()
	private val authCredits = HashMap<String, String>()
	
	val auras = HashMap<String, String>()
	val shields = HashMap<String, Int>()
	val wings = HashMap<String, String>()
	
	init {
		eventFML()
		download()
	}
	
	private fun download() {
		try {
			connect("hashes.txt") { paired().forEach { (k, v) -> register(k, v) } }
		} catch (e: Throwable) {
			ASJUtilities.error("Failed to register contributors, using default parameters")
			// default username:password pairs just in case
			register("AlexSocol", "FAD66A8AE739A30F66325679CB4CFF0B21428912D1DDBE45EA1692AB87DC1822")
			register("GedeonGrays", "B2612EA4C009B2C3FDDCAA7D6C1FFB8DD6C9C7ECFFD785DCD1A08BB41CAD47C0")
			register("KAIIIAK", "D761FAABD0C7F4042189C0CE308FDAD79566B198416BFDE23361EBA8DCB0BB96")
		}
		
		try {
			connect("auras.txt") { forEach { it.split(":").also { (k, v) -> auras[k] = v } } }
		} catch (e: Throwable) {
			ASJUtilities.error("Failed to register custom auras")
		}
		
		try {
			connect("patrons.txt") { forEach { it.split(":").also { (k, v) -> shields[k] = v.toIntOrNull() ?: 0 } } }
		} catch (e: Throwable) {
			ASJUtilities.error("Failed to register patrons")
		}
		
		try {
			connect("wings.txt") { forEach { it.split(":").also { (k, v) -> wings[k] = v } } }
		} catch (e: Throwable) {
			ASJUtilities.error("Failed to register custom wings")
		}
	}
	
	fun connect(file: String, action: List<String>.() -> Unit) {
		URL("https://bitbucket.org/AlexSocol/alfheim/raw/master/$file").openConnection().also { it.connectTimeout = 5000; it.readTimeout = 5000 }.getInputStream().bufferedReader().readLines().also { action(it) }
	}
	
	private fun register(contributor: String, passwordHash: String) {
		authCredits[contributor] = passwordHash
		
		if (MinecraftServer.getServer()?.isSinglePlayer != false)
			contributors[contributor] = contributor // no power on server if no response
	}
	
	fun isRegistered(login: String) = authCredits.contains(login)
	
	fun getPassHash(login: String) = authCredits[login]
	
	fun isCorrect(user: EntityPlayer, contributor: String) = isCorrect(user.commandSenderName, contributor)
	
	fun isCorrect(user: String, contributor: String) = contributors[contributor] == user
	
	val authTimeout = WeakHashMap<EntityPlayerMP, Int>()
	
	@SubscribeEvent
	fun onPlayerTick(e: TickEvent.PlayerTickEvent) {
		if (ASJUtilities.isClient || e.phase != TickEvent.Phase.START) return
		
		val player = e.player as EntityPlayerMP
		authTimeout[player]?.let {
			val time = it - 1
			
			if (time < 0)
				player.playerNetServerHandler.kickPlayerFromServer("Authentication request timed out")
			else
				authTimeout[player] = time
		}
	}
	
	@SubscribeEvent
	fun onPlayerLogin(e: PlayerEvent.PlayerLoggedInEvent) {
		val player = e.player as? EntityPlayerMP ?: return
		
		if (MinecraftServer.getServer()?.isSinglePlayer == true) return

		NetworkService.sendTo(MessageContributor(isRequest = true), player)
		
		if (isRegistered(player.commandSenderName))
			authTimeout[player] = AlfheimConfigHandler.authTimeout
	}
	
	@SubscribeEvent
	fun onPlayerLogout(e: PlayerEvent.PlayerLoggedOutEvent) {
		if (MinecraftServer.getServer()?.isSinglePlayer == true) return

		contributors.values.removeAll { it == e.player.commandSenderName }
	}
}

object HashHelper {
	
	fun hash(str: String?, salt: String = "soyeahthatsjustarandomuselesssecuritysaltthingsoyeah"): String {
		if (str != null)
			try {
				val md = MessageDigest.getInstance("SHA-256")
				return HexBinaryAdapter().marshal(md.digest(salt(str, salt).toByteArray(Charset.forName("UTF-8"))))
			} catch (e: NoSuchAlgorithmException) {
				e.printStackTrace()
			}
		
		return ""
	}
	
	// Might as well be called sugar given it's not secure at all :D
	fun salt(str: String, salt: String): String {
		val salted = str + salt
		val rand = Random(salted.length.toLong())
		val l = salted.length
		val steps = rand.nextInt(l)
		val chars = salted.toCharArray()
		for (i in 0 until steps) {
			val indA = rand.nextInt(l)
			var indB: Int
			do {
				indB = rand.nextInt(l)
			} while (indB == indA)
			val c = (chars[indA].code xor chars[indB].code).toChar()
			chars[indA] = c
		}
		
		return String(chars)
	}
}