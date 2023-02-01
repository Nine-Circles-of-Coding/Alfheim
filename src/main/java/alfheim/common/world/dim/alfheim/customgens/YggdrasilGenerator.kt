package alfheim.common.world.dim.alfheim.customgens

import alexsocol.asjlib.*
import alfheim.AlfheimCore
import alfheim.api.ModInfo
import alfheim.common.block.AlfheimBlocks
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.MathHelper
import ru.vamig.worldengine.additions.*
import kotlin.math.*

object YggdrasilGenerator: WE_CreateChunkGen() {
	
	override fun gen(data: WE_GeneratorData) {
		val cx = (data.chunk_X / 16).I
		val cz = (data.chunk_Z / 16).I
		
		makeHole(data, cx, cz)
		generateTree(data, cx, cz)
	}
	
	fun generateTree(data: WE_GeneratorData, cx: Int, cz: Int) {
		if (cx !in -16 until 16 || cz !in -16 until 16) return
		
		val lines = yggFileLines(cx, cz) ?: return
		var block = ""
		var meta = 0
		
		for (line in lines) {
			if (' ' !in line) {
				val newMeta = line.toIntOrNull()
				if (newMeta == null)
					block = line
				else
					meta = if (newMeta == 12) 14 else newMeta
				
				continue
			}
			
			val (x, y, z) = line.split(" ")
			
			val xFix = x.toInt() - cx * 16
			val zFix = z.toInt() - cz * 16
			
			setBlock(data, Block.getBlockFromName(block), meta.toByte(), xFix, y.toInt(), zFix)
		}
	}
	
	fun yggFileLines(cx: Int, cz: Int): List<String>? {
		return javaClass.getResourceAsStream("/assets/${ModInfo.MODID}/schemas/yggdrasil/ygg_${cx}_$cz")?.bufferedReader()?.readLines()
	}
	
	fun makeHole(data: WE_GeneratorData, cx: Int, cz: Int) {
		if (cx !in -32 until 32 || cz !in -32 until 32) return
		
		val x16 = cx * 16
		val z16 = cz * 16
		
		val grass = if (AlfheimCore.winter) AlfheimBlocks.snowGrass else Blocks.grass
		
		for (i in 0 until 16)
			for (k in 0 until 16)
				for (j in 5 until 256) {
					if (!yobaFunction(x16 + i, j, z16 + k)) continue
					
					val blockAt = getBlock(data, i, j, k) ?: break
					if (blockAt === Blocks.air) break
					
					setBlock(data, Blocks.dirt, 0, i, j-3, k)
					setBlock(data, Blocks.dirt, 0, i, j-2, k)
					setBlock(data, Blocks.dirt, 0, i, j-1, k)
					setBlock(data, grass, 0, i, j, k)
					
					var y = j + 1
					
					while (y < 256)
						setBlock(data, Blocks.air, 0, i, y++, k)
					
					break
				}
	}
	
	fun yobaFunction(i: Int, y: Int, k: Int, ix: Float = 3.9f, iy: Float = 1f, iz: Float = -2.5f): Boolean {
		val x = i / 50f
		val z = k / 50f
		return x * x + z * z + atan(MathHelper.sin(x * ix)) + atan(MathHelper.sin(z * iz)) < y * iy
	}
}
