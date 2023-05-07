package alfheim.api.world.domain

import alexsocol.asjlib.*
import alfheim.api.AlfheimAPI
import alfheim.common.entity.boss.IForceKill
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.World
import net.minecraftforge.client.IRenderHandler
import java.lang.IllegalArgumentException

abstract class Domain(val modid: String, val name: String, val minPlayers: Int, val boundBox: AxisAlignedBB, val genOffset: ChunkCoordinates = ChunkCoordinates()) {
	
	val id = nextDomainID
	val schema = SchemaUtils.loadStructure("${modid}/schemas/${name}")
	
	init {
		val prev = AlfheimAPI.domains.put(name, this)
		if (prev != null) throw IllegalArgumentException("Domain with name $name already exists")
	}
	
	abstract val skyRenderer: IRenderHandler
	
	abstract val firstConquerors: Array<String>
	abstract val firstConquerorsUnknown: Array<String>
	
	abstract fun isLocked(world: World): Boolean
	
	abstract fun canEnter(players: List<EntityPlayer>): Boolean
	
	fun restart(world: World, x: Int, y: Int, z: Int, players: List<EntityPlayer>) {
		val bb = boundBox // load all chunks to find boss
		for (i in (bb.minX.I + x - 16)..(bb.maxX.I + x + 16) step 16) {
			for (k in (bb.minZ.I + z - 16)..(bb.maxZ.I + z + 16) step 16) {
				world.getBlock(i, 64, k)
			}
		}
		
		// remove all entities except players
		getEntitiesWithinAABB(world, Entity::class.java, boundBox.copy().offset(x, y, z))
			.forEach { if (it !is EntityPlayer) if (it is IForceKill) it.forceKill() else it.setDead() }
		
		postRestart(world, x, y, z, players)
	}
	
	abstract fun postRestart(world: World, x: Int, y: Int, z: Int, players: List<EntityPlayer>)
	
	companion object {
		private var idCounter = 0
		val nextDomainID get() = idCounter++
	}
}