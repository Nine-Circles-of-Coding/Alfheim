package alfheim.common.world.dim.alfheim.customgens

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.AlfheimCore
import alfheim.api.ModInfo
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.block.BlockNiflheimPortal
import alfheim.common.core.handler.*
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent
import net.minecraft.block.Block
import net.minecraft.block.BlockBush
import net.minecraft.init.Blocks
import net.minecraft.world.*
import net.minecraftforge.event.terraingen.PopulateChunkEvent
import ru.vamig.worldengine.additions.*
import java.awt.Color
import java.util.Random
import kotlin.math.min

object NiflheimLocationGenerator: WE_CreateChunkGen() {
	
	val keepAir = javaClass.getResourceAsStream("/assets/${ModInfo.MODID}/schemas/PortalToNiflheimSafeFromIce")!!.bufferedReader().use {
		it.readLines()
	}.map {
		val (x, y, z) = it.replace("[^\\d\\- ]", "").split(" ")
		x.toInt() to y.toInt() with z.toInt()
	}.toHashSet()
	
	init {
		eventForge().eventFML()
	}
	
	override fun gen(data: WE_GeneratorData) {
		if (data.chunkProvider.world.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim) return
		
		val x16 = data.chunk_X.I
		val z16 = data.chunk_Z.I
		
		val (xOff, zOff) = portalXZ(data.chunkProvider.world)
		
		for (i in 0 until 16)
			for (k in 0 until 16) {
				val x = x16 + i
				val z = z16 + k
				
				if (!yobaFunction2d(x - xOff, z - zOff)) continue
				
				var c = 0
				var block: Block?
				do {
					block = getBlock(data, i, ++c, k)
				} while (block != null && block != Blocks.air)
				
				for (j in c downTo (c - 4)) {
					when (data.chunkProvider.world.rand.nextInt(3)) {
						0 -> setBlock(data, Blocks.dirt  , 1, i, j, k) // coarse dirt
						1 -> setBlock(data, Blocks.sand  , 0, i, j, k)
						2 -> setBlock(data, Blocks.gravel, 0, i, j, k)
					}
				}
				
				for (j in c..min(255, c+64))
					setBlock(data, Blocks.ice, 1, i, j, k) // unmeltable ice
			}
	}
	
	fun yobaFunction2d(x: Int, z: Int): Boolean {
		return YggdrasilGenerator.yobaFunction(x, 1, z, 2.4f, 4.3f, 3.2f)
	}
	
	@SubscribeEvent
	fun fillCavesFreezeLiquidsEtc(e: PopulateChunkEvent.Post) {
		if (e.world.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim) return
		
		val x16 = e.chunkX * 16
		val z16 = e.chunkZ * 16
		
		val (xOff, zOff) = portalXZ(e.world)
		
		for (i in 0 until 16)
			for (k in 0 until 16) {
				val x = x16 + i
				val z = z16 + k
				
				if (!yobaFunction2d(x - xOff, z - zOff)) continue
				
				var j = 256
				var block: Block?
				do {
					block = e.world.getBlock(x, --j, z)
				} while (block == Blocks.air)
				
				for (y in 0..j) {
					if (x - xOff to y - 33 with z - zOff in keepAir) continue
					
					block = e.world.getBlock(x, y, z)
					if (block === Blocks.air || block === Blocks.water || block === Blocks.flowing_water || block is BlockBush) e.world.setBlock(x, y, z, Blocks.ice, 1, 2)
					else if (block === Blocks.lava || block === Blocks.flowing_lava) e.world.setBlock(x, y, z, Blocks.obsidian, 0, 2)
				}
			}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	fun freezePlayers(e: SheerColdHandler.SheerColdTickEvent) {
		val entity = e.entityLiving
		if (entity.dimension != AlfheimConfigHandler.dimensionIDAlfheim) return
		
		val world = entity.worldObj
		val (xOff, zOff) = portalXZ(world)
		
		val (x, _, z) = Vector3.fromEntity(entity).sub(xOff, 0, zOff).mf()
		if (!yobaFunction2d(x, z)) return
		
		val dist = Vector3.vecEntityDistance(Vector3(BlockNiflheimPortal.onlyPortalPosition(world)), entity).F
		val cold = 0.01f / (dist / 64)
		e.delta = (e.delta ?: 0f) + cold
	}
	
	@SubscribeEvent
	fun worldTickClient(e: WorldTickEvent) {
		if (e.world.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim || ASJUtilities.isClient || e.world.rand.nextInt(5) != 0) return
		
		val (i, j, k) = BlockNiflheimPortal.onlyPortalPosition(e.world)
		if (e.world.getBlock(i, j, k) != Blocks.water) return
		
		val (x, y, z) =  Vector3(i, j, k).add(0.5)
		val (mx, my, mz) = Vector3().rand().sub(0.5).normalize().mul(Math.random() * 0.25)
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.WISP, AlfheimConfigHandler.dimensionIDAlfheim,
		                               x, y, z, 0.75, 0.95, 1.0, Math.random() * 0.1 + 0.2, mx, my, mz, Math.random() * 3 + 1)
	}
	
	fun portalXZ(world: World): Pair<Int, Int> {
		val rand = Random(world.seed)
		return ASJUtilities.randInBounds(-512, 512, rand) to ASJUtilities.randInBounds(-4096, -2048, rand)
	}
}