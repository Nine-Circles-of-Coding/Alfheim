package alfheim.common.world.dim.domains.gen

import alexsocol.asjlib.getBoundingBox
import alfheim.api.ModInfo
import alfheim.api.world.domain.Domain
import alfheim.client.render.world.SkyRendererDomains
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.entity.boss.EntityFenrir
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.World

object FenrirDomain: Domain(ModInfo.MODID, "Fenrir", 1, getBoundingBox(-63, -1, -32, 63, 31, 94), ChunkCoordinates(0, 0, 31)) {
	
	override val skyRenderer = object: SkyRendererDomains(0xFFFFEECCu, 0xFFFFFBF2u) {}
	
	override val firstConquerors = arrayOf("Kompotik")
	override val firstConquerorsUnknown = arrayOf("ᚲᛟᛗᛈᛟᛏᛁᚲ")
	
	override fun isLocked(world: World) = if (RagnarokHandler.finished) false else !RagnarokHandler.canBringBackSunAndMoon()
	
	override fun canEnter(players: List<EntityPlayer>) = true
	
	override fun postRestart(world: World, x: Int, y: Int, z: Int, players: List<EntityPlayer>) {
		// spawn new boss
		EntityFenrir.summon(world, x, y, z + 31)
	}
}
