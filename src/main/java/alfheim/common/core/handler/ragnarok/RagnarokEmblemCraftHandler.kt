package alfheim.common.core.handler.ragnarok

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.block.tile.*
import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem
import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem.Knowledge
import alfheim.common.entity.item.EntityItemImmortal
import alfheim.common.item.AlfheimItems
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.entity.item.EntityItem
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.event.entity.EntityStruckByLightningEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent

object RagnarokEmblemCraftHandler {
	
	@SubscribeEvent
	fun spawnLightningForPendant(e: PlayerInteractEvent) {
		if (e.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return
		val player = e.entityPlayer
		if (player.heldItem?.item !== AlfheimItems.wiltedLotus || player.heldItem.meta != 1) return
		val tile = e.world.getTileEntity(e.x, e.y, e.z) as? TileAnomaly ?: return
		if (tile.mainSubTile != "Lightning") return
		e.world.addWeatherEffect(EntityLightningBolt(e.world, e.x.D, e.y.D, e.z.D))
		--player.heldItem.stackSize
	}
	
	val METAS = arrayOf(4, 2, 0, 3, 1, 5)
	const val AETHER = -1
	const val WATER = 3362227
	const val AIR = 15132211
	const val FIRE = 10040115
	const val EARTH = 6717491
	const val ORDER = 16777215
	const val VOID = 1710618
	
	@SubscribeEvent
	fun craftPendant(e: EntityStruckByLightningEvent) {
		val entityItem = e.entity as? EntityItem ?: return
		if (entityItem.entityItem?.item !== AlfheimItems.attributionBauble) return
		val world = entityItem.worldObj
		if (world.isRemote) return
		val anomaly = world.getTileEntity(entityItem) as? TileAnomaly ?: return
		if (anomaly.mainSubTile != "Lightning") return
		
		val (x, y, z) = Vector3.fromEntity(entityItem).mf()
		
		val ragnar = MinecraftServer.getServer().configurationManager.func_152612_a(entityItem.func_145800_j()) ?: return
		if (!KnowledgeSystem.know(ragnar, Knowledge.ABYSS)) return
		
		val poses = mutableListOf<Vector3>()
		
		for (i in x.bidiRange(5))
			for (j in y.bidiRange(5))
				for (k in z.bidiRange(5)) {
					if (Vector3.pointDistanceSpace(x, y, z, i, j, k) > 5) continue
					val star = world.getTileEntity(i, j, k) as? TileCracklingStar ?: continue
					
					if (star.color == AETHER)
						poses.add(Vector3(i, j, k))
				}
		
		mainLoop@ for (pos in poses) {
			val (path, connections, colors) = walkPath(pos, world, 5)
			if (path.size != 6) continue
			if (connections[5] != path[0]) continue
			if (colors[0] != AETHER || colors[1] != WATER || colors[2] != AIR || colors[3] != FIRE || colors[4] != EARTH || colors[5] != ORDER) continue
			for (i in path.indices)
				(checkItem(path, i, world) ?: continue@mainLoop).setDead()
			
			entityItem.setDead()
			e.lightning.setDead()
			
			val entity = EntityItemImmortal(world, entityItem.posX, entityItem.posY + 1, entityItem.posZ, ItemStack(AlfheimItems.ragnarokEmblem))
			entity.motionY = 1.0
			entity.delayBeforeCanPickup = 30
			entity.spawn()
			
			RagnarokHandler.allowThrymKill()
			
			for (i in 0.bidiRange(3))
				for (j in 0.bidiRange(3))
					for (k in 0.bidiRange(3))
						if (world.getBlock(entityItem, i, j, k) === Blocks.fire)
							world.setBlock(entityItem, Blocks.air, i, j, k)
			
			for (p in path) {
				val tile = p.getTileEntity(world) as? TileCracklingStar ?: continue
				tile.color = VOID
				tile.pos.set(0, -1, 0)
				tile.markDirty()
			}
			
//			ragnar.triggerAchievement(AlfheimAchievements.ragnarok)
			KnowledgeSystem.learn(ragnar, Knowledge.NIFLHEIM, AlfheimLexiconData.abyss)
			
			return
		}
	}
	
	fun walkPath(
		start: Vector3, world: World, max: Int, walked: Array<Vector3> = arrayOf(start),
		walkedConnections: Array<Vector3> = arrayOf((start.getTileEntity(world) as TileCracklingStar).pos),
		walkedColors: IntArray = intArrayOf((start.getTileEntity(world) as TileCracklingStar).color),
	)
		: Triple<Array<Vector3>, Array<Vector3>, IntArray> {
		
		if (walked.size > max) return Triple(walked, walkedConnections, walkedColors)
		val tile = start.getTileEntity(world)
		if (tile is TileCracklingStar) {
			val link = tile.pos.copy()
			if (link == Vector3(0, -1, 0)) return Triple(walked, walkedConnections, walkedColors)
			if (link in walked) return Triple(walked, walkedConnections, walkedColors)
			val linked = link.getTileEntity(world)
			if (linked is TileCracklingStar) {
				val linkPos = linked.pos.copy()
				if (linkPos != Vector3(0, -1, 0))
					return walkPath(link, world, max, arrayOf(*walked, link), arrayOf(*walkedConnections, linkPos), intArrayOf(*walkedColors, linked.color))
			}
		}
		return Triple(walked, walkedConnections, walkedColors)
	}
	
	fun Vector3.getTileEntity(world: World): TileEntity? = world.getTileEntity(x.mfloor(), y.mfloor(), z.mfloor())
	
	fun checkItem(path: Array<Vector3>, index: Int, world: World): EntityItem? {
		val (x, y, z) = path[index]
		val items = getEntitiesWithinAABB(world, EntityItem::class.java, getBoundingBox(x, y, z, x + 1, y + 1, z + 1))
		return items.firstOrNull { it.entityItem?.item === AlfheimItems.priestEmblem && it.entityItem.meta == METAS[index] }
	}
}