package alfheim.common.world.dim.domains.gen

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.api.world.domain.Domain
import alfheim.client.render.world.SkyRendererDomains
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.entity.boss.IForceKill
import alfheim.common.entity.boss.primal.EntityThrym
import alfheim.common.item.equipment.bauble.faith.ItemRagnarokEmblem
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.world.World

object ThrymDomain: Domain(ModInfo.MODID, "Thrym", 2, getBoundingBox(-45, -5, -75, 45, 50, 10)) {
	
	override val skyRenderer = object: SkyRendererDomains(0xFF7F7FFFU, 0xFFFFFBF2u) {}
	
	override val firstConquerors = arrayOf("Ilya3000", "Kompotik", "Pelmeshkins")
	override val firstConquerorsUnknown = arrayOf("ᛁᛚᛁᚨᛏᛉᛉᛉ", "ᚲᛟᛗᛈᛟᛏᛁᚲ", "ᛈᛖᛚᛗᛖᛊᚺᚲᛁᚾᛊ")
	
	override fun isLocked(world: World) = if (RagnarokHandler.finished || RagnarokHandler.winter) false else !RagnarokHandler.canStartWinter()
	
	override fun canEnter(players: List<EntityPlayer>): Boolean {
		if (!RagnarokHandler.thrymFirstTime) return true
		val check = players.any { ItemRagnarokEmblem.getEmblem(it) != null }
		if (!check) players.forEach { ASJUtilities.say(it, "alfheimmisc.ragnarok.domain.thrymnotallow") }
		return check
	}
	
	override fun postRestart(world: World, x: Int, y: Int, z: Int, players: List<EntityPlayer>) {
		// spawn new boss
		EntityThrym.summon(world, x, y + 1, z - 40, players)
	}
}