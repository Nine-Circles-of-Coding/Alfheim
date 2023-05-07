package alfheim.common.world.dim.alfheim.structure

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.block.BlockNiflheimPortal
import alfheim.common.world.dim.alfheim.customgens.NiflheimLocationGenerator
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.World

object StructurePortalToNiflheim {
	
	fun generate(world: World, chunkX: Int, chunkZ: Int) {
		val (xOff, zOff) = NiflheimLocationGenerator.portalXZ(world)
		
		if (xOff shr 4 != chunkX || zOff shr 4 != chunkZ) return
		
		ASJUtilities.log("Generated portal to Niflheim at $xOff $zOff")
		SchemaUtils.generate(world, xOff, 33, zOff, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/PortalToNiflheim"))
	}
}
