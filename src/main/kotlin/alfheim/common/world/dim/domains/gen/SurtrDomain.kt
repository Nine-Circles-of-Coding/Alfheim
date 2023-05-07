package alfheim.common.world.dim.domains.gen

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.api.world.domain.Domain
import alfheim.client.render.world.SkyRendererDomains
import alfheim.common.block.tile.TileDomainLobby
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.entity.boss.primal.EntitySurtr
import alfheim.common.item.AlfheimItems
import alfheim.common.item.equipment.bauble.ItemPriestEmblem
import alfheim.common.item.equipment.bauble.faith.ItemRagnarokEmblem
import alfheim.common.world.data.CustomWorldData.Companion.customData
import alfheim.common.item.material.EventResourcesMetas
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

object SurtrDomain: Domain(ModInfo.MODID, "Surtr", 2, getBoundingBox(-45, -5, -75, 45, 50, 10)) {
	
	override val skyRenderer = object: SkyRendererDomains(0xFFFF4D00U, 0xFF220000U) {}
	
	override val firstConquerors = arrayOf("BreadX22", "Ilya3000", "Kompotik", "krumplerban", "Pelmeshkins")
	override val firstConquerorsUnknown = arrayOf("ᛒᚱᛖᛞᚲᛊᛏᛏ", "ᛁᛚᛁᚨᛏᛉᛉᛉ", "ᚲᛟᛗᛈᛟᛏᛁᚲ", "ᚲᚱᚢᛗᛈᛚᛖᚱᛒᚨᚾ", "ᛈᛖᛚᛗᛖᛊᚺᚲᛁᚾᛊ")
	
	override fun isLocked(world: World): Boolean {
		if (RagnarokHandler.finished) return false
		if (!RagnarokHandler.canEndSummer()) return true
		
		val data = world.customData
		val (x, z) = data.structures["Surtr"].firstOrNull() ?: run { // Chunk Pregenerator / some server kernel bug fix -_-
			val core = world.loadedTileEntityList.firstOrNull { it is TileDomainLobby } as? TileDomainLobby ?: return@run null
			if (core.name != this.name) return@run null
			core.xCoord to core.zCoord - 30
		} ?: (Int.MIN_VALUE to Int.MAX_VALUE)
		
		val y = data.data["SurtrY"]?.toInt() ?: run { // Chunk Pregenerator / some server kernel bug fix -_-
			val core = world.loadedTileEntityList.firstOrNull { it is TileDomainLobby } as? TileDomainLobby ?: return@run null
			if (core.name != this.name) return@run null
			core.yCoord + 5
		} ?: -1
		
		if (x == Int.MIN_VALUE || y == -1) return true
		
		val bb = getBoundingBox(x, y + 3, z + 23).offset(0.5).expand(0.5)
		
		getEntitiesWithinAABB(world, EntityItem::class.java, bb).firstOrNull {
			it.entityItem?.item === AlfheimItems.eventResource && it.entityItem.meta == EventResourcesMetas.VolcanoRelic
		}?.let {
			it.entityItem?.stackSize = 0
			it.setEntityItemStack(null)
			it.setDead()
			data.data["SurtrUnlocked"] = "true"
			data.markDirty()
			
			getEntitiesWithinAABB(world, EntityPlayer::class.java, bb.offset(0, -8, 0).expand(16)).forEach { p ->
				ASJUtilities.say(p, "alfheimmisc.ragnarok.surtrofferok")
			}
		}
		
		if (data.data["SurtrUnlocked"]?.toBoolean() != true) {
			getEntitiesWithinAABB(world, EntityPlayer::class.java, bb.offset(0, -8, 0).expand(16)).forEach {
				ASJUtilities.say(it, "alfheimmisc.ragnarok.surtroffer")
			}
			return true
		}
		
		return false
	}
	
	override fun canEnter(players: List<EntityPlayer>): Boolean {
		if (!RagnarokHandler.surtrFirstTime) return true
		val check = players.any { ItemRagnarokEmblem.getEmblem(it) != null || ItemPriestEmblem.getEmblem(-1, it) != null }
		if (!check) players.forEach { ASJUtilities.say(it, "alfheimmisc.ragnarok.domain.surtrnotallow") }
		return check
	}
	
	override fun postRestart(world: World, x: Int, y: Int, z: Int, players: List<EntityPlayer>) {
		// spawn new boss
		EntitySurtr.summon(world, x, y + 1, z - 40, players)
	}
}