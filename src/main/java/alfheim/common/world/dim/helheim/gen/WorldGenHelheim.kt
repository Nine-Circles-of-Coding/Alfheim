package alfheim.common.world.dim.helheim.gen

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.api.entity.EnumRace
import alfheim.common.block.*
import alfheim.common.block.tile.*
import alfheim.common.core.handler.AlfheimConfigHandler
import cpw.mods.fml.common.IWorldGenerator
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import java.util.*
import kotlin.math.abs

object WorldGenHelheim: IWorldGenerator {
	
	override fun generate(random: Random, chunkX: Int, chunkZ: Int, world: World, chunkGenerator: IChunkProvider?, chunkProvider: IChunkProvider?) {
		if (world.provider.dimensionId != AlfheimConfigHandler.dimensionIDHelheim) return
		if (chunkX == 0 && chunkZ == 0) genStairs(world)
		
		genSoul(world, random, chunkX, chunkZ)
	}
	
	fun genSoul(world: World, random: Random, chunkX: Int, chunkZ: Int) {
		if (genVafthrudnir(world, chunkX, chunkZ)) return
		
		if (random.nextBoolean()) return
		
		val x = chunkX * 16 + random.nextInt(16)
		val z = chunkZ * 16 + random.nextInt(16)
		
		if (abs(x) < 24 && abs(z) < 24) return // no gen near stairs
		
		val y = world.getTopSolidOrLiquidBlock(x, z) + ASJUtilities.randInBounds(1, 5, random)
		
		if (!world.setBlock(x, y, z, AlfheimBlocks.rainbowFlame)) return
		val tile = world.getTileEntity(x, y, z)
		
		if (tile !is TileRainbowManaFlame) return
		tile.color = soulColors.random(random)!!
		tile.soul = true
	}
	
	fun genVafthrudnir(world: World, chunkX: Int, chunkZ: Int): Boolean {
		val rand = Random(world.seed)
		val x = ASJUtilities.randInBounds(-256, 256, rand)
		val z = ASJUtilities.randInBounds(-768, -256, rand)
		
		if (x shr 4 != chunkX || z shr 4 != chunkZ) return false
		
		val y = world.getTopSolidOrLiquidBlock(x, z) + ASJUtilities.randInBounds(1, 3, rand)
		
		if (!world.setBlock(x, y, z, AlfheimBlocks.rainbowFlame, 1, 3) || world.getTileEntity(x, y, z) !is TileVafthrudnirSoul)
			throw RuntimeException("Unable to generate Vafthrudnir soul on designated coords [$x, $y, $z]. This would make ragnarok impossible to complete. Please, regenerate Helheim chunk at [$chunkX, $chunkZ]")
		
		return true
	}
	
	val soulColors = EnumRace.values().map { it.rgbColor }
	
	fun genStairs(world: World) {
		val stairs = SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/helstairs")
		val yStart = world.getTopSolidOrLiquidBlock(0, 0)
		
		for (y in yStart until 256 step 4)
			SchemaUtils.generate(world, 0, y, 0, stairs)
		
		for (x in -4..4)
			for (z in -4..4)
				world.setBlockToAir(x, 255, z)
		
		for (x in -1..1)
			for (z in -1..1)
				if (x != 0 || z != 0)
					world.setBlock(x, 254, z, AlfheimFluffBlocks.shrineRockWhiteSlab, 8, 3)
		
		if (!world.setBlock(0, 255, 0, AlfheimBlocks.rainbowFlame)) {
			return ASJUtilities.fatal("!!!WARNING!!!\nSevere issue!!!\n" +
					                   "Something prevented exit block from being set in Helheim dim " +
					                   "(${world.provider.dimensionId}) on 0 255 0 coordinates!!!\n" +
					                   "Make sure to do something about it or else there will be no exit!!!")
		}
		
		(world.getTileEntity(0, 255, 0) as TileRainbowManaFlame).exit = true
	}
}
