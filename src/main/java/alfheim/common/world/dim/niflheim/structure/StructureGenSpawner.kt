package alfheim.common.world.dim.niflheim.structure

import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntityMobSpawner
import net.minecraft.world.World
import java.util.*

object StructureGenSpawner {
	
	fun generate(world: World, random: Random, type: Int, i: Int, j: Int, k: Int): Boolean {
		world.setBlock(i, j, k, Blocks.mob_spawner, 0, 3)
		val tile = world.getTileEntity(i, j, k) as TileEntityMobSpawner
		val mobID = when (type) {
			0 -> spawner0(random)
			1 -> spawner1(random)
			2 -> "CaveSpider"
			3 -> spawner3(random)
			4 -> spawner4(random)
			else -> "Zombie"
		}
		tile.func_145881_a().setEntityName(mobID)
		return true
	}
	
	fun spawner0(random: Random): String {
		return when (random.nextInt(4)) {
			0    -> "CaveSpider"
			1, 2 -> "Silverfish"
			3    -> "Spider"
			else -> "Zombie"
		}
	}
	
	fun spawner1(random: Random): String {
		return when (random.nextInt(5)) {
			3    -> "Skeleton"
			4    -> "Spider"
			else -> "Zombie"
		}
	}
	
	fun spawner3(random: Random): String {
		return when (random.nextInt(4)) {
			0    -> "Skeleton"
			2    -> "Creeper"
			3    -> "Spider"
			else -> "Zombie"
		}
	}
	
	fun spawner4(random: Random): String {
		return when (random.nextInt(4)) {
			0    -> "Skeleton"
			3    -> "Spider"
			else -> "Zombie"
		}
	}
}