package alfheim.common.world.dim.alfheim.structure

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.common.block.tile.TilePowerStone
import alfheim.common.world.data.CustomWorldData.Companion.customData
import net.minecraft.world.World
import ru.vamig.worldengine.standardcustomgen.StructureBaseClass
import java.util.*

object StructureShrine: StructureBaseClass() {
	
	val shrines = listOf(
		SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/shrineBerserk"),
		SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/shrineNinja"),
		SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/shrineOvermage"),
		SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/shrineTank")
	                     )
	
	override fun generate(world: World, rand: Random, x: Int, y: Int, z: Int): Boolean {
		if (ASJUtilities.isClient) return false // just in case
		if (x shr 4 in -32 until 32 || z shr 4 in -32 until 32) return false // no shrines in Yggdrasil pit
		
		val data = world.customData
		val locs = data.structures["any"]
		
		if (locs.any { Vector3.pointDistancePlane(x, z, it.first, it.second) < 128 }) return false
		
		SchemaUtils.generate(world, x, y, z, shrines.random(rand)!!)

		for (i in 0..2) {
			val tile = world.getTileEntity(x, y + i, z) as? TilePowerStone ?: continue
			tile.lock(x, y + i, z, world.provider.dimensionId)
		}
		
		locs.add(x to z)
		data.markDirty()
		
		return true
	}
}
