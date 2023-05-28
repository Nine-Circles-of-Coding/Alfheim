package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.raceID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.material.ElvenResourcesMetas
import net.minecraft.block.Block
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.util.*
import net.minecraft.world.World
import vazkii.botania.api.lexicon.multiblock.*
import vazkii.botania.common.Botania
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.block.tile.mana.TilePool
import vazkii.botania.common.core.handler.ConfigHandler
import java.util.*
import kotlin.math.*

class TileAlfheimPortal: ASJTile() {
	
	var activated = false
	var ticksOpen = 0
	private var closeNow = false
	private var hasUnloadedParts = false
	
	val portalAABB: AxisAlignedBB
		get() = if (getBlockMetadata() == 2)
				getBoundingBox(xCoord + 0.25, yCoord + 1, zCoord - 1, xCoord + 0.75, yCoord + 4, zCoord + 2)
			else
				getBoundingBox(xCoord - 1, yCoord + 1, zCoord + 0.25, xCoord + 2, yCoord + 4, zCoord + 0.75)
	
	val validMetadata: Int
		get() {
			if (checkConverter(CONVERTER_NOP))
				return 1
			
			return if (checkConverter(CONVERTER_X_Z)) 2 else 0
			
		}
	
	internal val rand = Random()
	
	override fun updateEntity() {
		val meta = getBlockMetadata()
		if (meta == 0) {
			ticksOpen = 0
			return
		}
		val newMeta = validMetadata
		
		if (meta > 2 || newMeta > 2) {
			worldObj.setBlockToAir(xCoord, yCoord, zCoord)
			return
		}
		
		if (!hasUnloadedParts) run {
			ticksOpen++
			
			val aabb = portalAABB
			
			if (ticksOpen <= 60) return@run
			if (ConfigHandler.elfPortalParticlesEnabled) blockParticle(meta)
			if (worldObj.isRemote) return@run
			
			getEntitiesWithinAABB(worldObj, EntityPlayer::class.java, aabb).forEach { player ->
				if (player.isDead) return@forEach
				
				if (player.dimension == AlfheimConfigHandler.dimensionIDAlfheim) {
					sendToMidgard(player)
				} else {
					sendToAlfheim(player)
				}
			}
		} else
			closeNow = false
		
		if (closeNow) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3)
			if (!worldObj.isRemote && worldObj.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim)
				EntityItem(worldObj, xCoord + 0.5, yCoord + 1.5, zCoord + 0.5, ElvenResourcesMetas.InterdimensionalGatewayCore.stack).spawn()
			for (i in 0..35)
				blockParticle(meta)
			closeNow = false
			activated = false
		} else if (newMeta != meta) {
			if (newMeta == 0) {
				if (!worldObj.isRemote && worldObj.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim)
					EntityItem(worldObj, xCoord + 0.5, yCoord + 1.5, zCoord + 0.5, ElvenResourcesMetas.InterdimensionalGatewayCore.stack).spawn()
				for (i in 0..35)
					blockParticle(meta)
				activated = false
			}
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, newMeta, 3)
		}
		
		hasUnloadedParts = false
	}
	
	private fun sendToAlfheim(player: EntityPlayer) {
		val alfheim = MinecraftServer.getServer().worldServerForDimension(AlfheimConfigHandler.dimensionIDAlfheim)
		
		val coords = if (AlfheimConfigHandler.enableElvenStory) {
			val race = player.raceID - 1 // for array length
			
			if (race in 0..8) {
				val (x, y, z) = AlfheimConfigHandler.zones[race].I
				ChunkCoordinates(x, y, z)
			} else alfheim.spawnPoint
		} else alfheim.spawnPoint
		
		ASJUtilities.sendToDimensionWithoutPortal(player, AlfheimConfigHandler.dimensionIDAlfheim, coords.posX + 0.5, coords.posY + 0.5, coords.posZ + 0.5)
	}
	
	private fun sendToMidgard(player: EntityPlayer) {
		val midgard = MinecraftServer.getServer().worldServerForDimension(0)
		
		var checkBB = true
		val coords = run {
			if (!AlfheimConfigHandler.grabMidgardPortal) return@run null
			
			midgard.loadedTileEntityList
				.filterIsInstance<TileAlfheimPortal>()
				.filter(TileAlfheimPortal::activated)
				.shuffled()
				.firstNotNullOfOrNull { tile ->
					val (x, y, z) = Vector3.fromTileEntity(tile).mf()
					(if (tile.getBlockMetadata() == 2) VALID_TELEPORTATION_SPOTS.map(CONVERTER_X_Z) else VALID_TELEPORTATION_SPOTS)
						.firstNotNullOfOrNull findSpot@{
							val (i, _, k) = it
							val X = x + i
							val Z = z + k
							for (j in 0..3) {
								val Y = y + j
//								if (!World.doesBlockHaveSolidTopSurface(tile.worldObj, X, Y - 1, Z)) continue
								val bb = player.boundingBox.copy().offset(-player.posX, -player.posY, -player.posZ).offset(X, Y, Z).offset(0.5)
								if (!tile.worldObj.checkNoEntityCollision(bb) || tile.worldObj.func_147461_a(bb).isNotEmpty() || tile.worldObj.isAnyLiquid(bb)) continue
								checkBB = false
								return@findSpot ChunkCoordinates(X, Y, Z)
							}
							return@findSpot null
						}
				}
		} ?: midgard.spawnPoint ?: ChunkCoordinates()
		
		if (checkBB) {
			val bb = player.boundingBox.expand(0).offset(-player.posX, -player.posY, -player.posZ).offset(coords.posX, coords.posY, coords.posZ)
			if (midgard.func_147461_a(bb).isNotEmpty())
				coords.posY = midgard.getTopSolidOrLiquidBlock(coords.posX, coords.posZ)
		}
		
		ASJUtilities.sendToDimensionWithoutPortal(player, 0, coords.posX + 0.5, coords.posY + 0.5, coords.posZ + 0.5)
	}
	
	private fun blockParticle(meta: Int) {
		val i = worldObj.rand.nextInt(AIR_POSITIONS.size)
		var pos = doubleArrayOf((AIR_POSITIONS[i][0] + 0.5f).D, (AIR_POSITIONS[i][1] + 0.5f).D, (AIR_POSITIONS[i][2] + 0.5f).D)
		if (meta == 2)
			pos = CONVERTER_X_Z_FP(pos)
		
		val motionMul = 0.2f
		Botania.proxy.wispFX(worldObj, xCoord + pos[0], yCoord + pos[1], zCoord + pos[2],
							 Math.random().F * 0.25f + 0.5f, Math.random().F * 0.25f + 0.5f, Math.random().F * 0.25f,
							 (Math.random() * 0.15f + 0.1f).F,
							 (Math.random() - 0.5f).F * motionMul, (Math.random() - 0.5f).F * motionMul, (Math.random() - 0.5f).F * motionMul)
	}
	
	fun onWanded(newMeta: Int): Boolean {
		val meta = getBlockMetadata()
		if (meta == 0 && newMeta != 0) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, newMeta, 3)
			activated = true
			return true
		}
		
		return false
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		nbt.setInteger(TAG_TICKS_OPEN, ticksOpen)
		nbt.setBoolean(TAG_ACTIVATED, activated)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		ticksOpen = nbt.getInteger(TAG_TICKS_OPEN)
		activated = nbt.getBoolean(TAG_ACTIVATED)
	}
	
	private fun checkConverter(baseConverter: (IntArray) -> IntArray) =
		checkMultipleConverters(arrayOf(baseConverter)) || checkMultipleConverters(arrayOf(CONVERTER_Z_SWAP, baseConverter))
	
	private fun checkMultipleConverters(converters: Array<(IntArray) -> IntArray>): Boolean {
		if (wrong2DArray(AIR_POSITIONS, Blocks.air, -1, converters))
			return false
		if (wrong2DArray(DREAMWOOD_POSITIONS, ModBlocks.dreamwood, 0, converters))
			return false
		if (wrong2DArray(GLIMMERING_DREAMWOOD_POSITIONS, ModBlocks.dreamwood, 5, converters))
			return false
		if (wrong2DArray(PYLON_POSITIONS, AlfheimBlocks.alfheimPylon, 0, converters) && worldObj.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim)
			return false
		if (wrong2DArray(POOL_POSITIONS, ModBlocks.pool, -1, converters) && worldObj.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim)
			return false
		
		lightPylons(converters)
		return true
	}
	
	private fun lightPylons(converters: Array<(IntArray) -> IntArray>) {
		if (ticksOpen < 50)
			return
		
		val cost = if (ticksOpen == 50) activation else idle
		
		for (pp in PYLON_POSITIONS) {
			var pos = pp
			converters.forEach { pos = it(pos) }
			
			var tile = worldObj.getTileEntity(xCoord + pos[0], yCoord + pos[1], zCoord + pos[2])
			if (tile is TileAlfheimPylon) {
				
				if (ConfigHandler.elfPortalParticlesEnabled) {
					var worldTime = worldObj.totalWorldTime.D
					rand.setSeed((xCoord + pos[0] xor yCoord + pos[1] xor zCoord + pos[2]).toLong())
					worldTime += rand.nextInt(1000).D
					worldTime /= 5.0
					
					val r = 0.75f + Math.random().F * 0.05f
					val x = xCoord.D + pos[0].D + 0.5 + cos(worldTime) * r
					val z = zCoord.D + pos[2].D + 0.5 + sin(worldTime) * r
					
					val movementVector = Vector3(xCoord - x + 0.5, Math.random() - 1.125, zCoord - z + 0.5).normalize().mul(0.2)
					
					Botania.proxy.wispFX(worldObj, x, yCoord.D + pos[1].D + 0.25, z,
										 0.75f + Math.random().F * 0.25f, Math.random().F * 0.25f, 0.75f + Math.random().F * 0.25f,
										 0.25f + Math.random().F * 0.1f, -0.075f - Math.random().F * 0.015f)
					
					if (worldObj.rand.nextInt(3) == 0)
						Botania.proxy.wispFX(worldObj, x, yCoord.D + pos[1].D + 0.25, z,
											 Math.random().F * 0.25f, Math.random().F * 0.25f, 0.75f + Math.random().F * 0.25f,
											 0.25f + Math.random().F * 0.1f,
											 movementVector.x.F, movementVector.y.F, movementVector.z.F)
				}
			}
			
			tile = worldObj.getTileEntity(xCoord + pos[0], yCoord + pos[1] - 1, zCoord + pos[2])
			if (tile is TilePool) {
				val pool = tile
				if (pool.currentMana < cost)
					closeNow = true
				else if (!worldObj.isRemote)
					pool.recieveMana(-cost)
			}
		}
	}
	
	private fun wrong2DArray(positions: Array<IntArray>, block: Block, meta: Int, converters: Array<(IntArray) -> IntArray>): Boolean {
		for (pp in positions) {
			var pos = pp
			
			converters.forEach { pos = it(pos) }
			
			if (!checkPosition(pos, block, meta))
				return true
		}
		return false
	}
	
	private fun checkPosition(pos: IntArray, block: Block, meta: Int): Boolean {
		val x = xCoord + pos[0]
		val y = yCoord + pos[1]
		val z = zCoord + pos[2]
		if (!worldObj.blockExists(x, y, z)) {
			hasUnloadedParts = true
			return true // Don't fuck everything up if there's a chunk unload
		}
		
		val blockat = worldObj.getBlock(x, y, z)
		if (if (block === Blocks.air) blockat.isAir(worldObj, x, y, z) else blockat === block) {
			if (meta == -1)
				return true
			
			val metaat = worldObj.getBlockMetadata(x, y, z)
			return meta == metaat
		}
		
		return false
	}
	
	override fun getRenderBoundingBox() = INFINITE_EXTENT_AABB!!
	
	companion object {
		
		private val DREAMWOOD_POSITIONS = arrayOf(intArrayOf(-1, 0, 0), intArrayOf(1, 0, 0), intArrayOf(-2, 1, 0), intArrayOf(2, 1, 0), intArrayOf(-2, 3, 0), intArrayOf(2, 3, 0), intArrayOf(-1, 4, 0), intArrayOf(1, 4, 0))
		private val GLIMMERING_DREAMWOOD_POSITIONS = arrayOf(intArrayOf(-2, 2, 0), intArrayOf(2, 2, 0), intArrayOf(0, 4, 0))
		private val PYLON_POSITIONS = arrayOf(intArrayOf(-3, 1, 3), intArrayOf(3, 1, 3))
		private val POOL_POSITIONS = arrayOf(intArrayOf(-3, 0, 3), intArrayOf(3, 0, 3))
		private val AIR_POSITIONS = arrayOf(intArrayOf(-1, 1, 0), intArrayOf(0, 1, 0), intArrayOf(1, 1, 0), intArrayOf(-1, 2, 0), intArrayOf(0, 2, 0), intArrayOf(1, 2, 0), intArrayOf(-1, 3, 0), intArrayOf(0, 3, 0), intArrayOf(1, 3, 0))
		
		private val VALID_TELEPORTATION_SPOTS = listOf(intArrayOf(0, 0, 1), intArrayOf(0, 0, -1), intArrayOf(1, 0, 1), intArrayOf(-1, 0, 1), intArrayOf(1, 0, -1), intArrayOf(-1, 0, -1))
		
		private const val TAG_TICKS_OPEN = "ticksOpen"
		private const val TAG_ACTIVATED = "activated"
		
		private const val activation = 75000
		private const val idle = 2
		
		private val CONVERTER_NOP = { input: IntArray -> input }
		
		private val CONVERTER_X_Z = { input: IntArray -> intArrayOf(input[2], input[1], input[0]) }
		
		private val CONVERTER_X_Z_FP = { input: DoubleArray -> doubleArrayOf(input[2], input[1], input[0]) }
		
		private val CONVERTER_Z_SWAP = { input: IntArray -> intArrayOf(input[0], input[1], -input[2]) }
		
		fun makeMultiblockSet(): MultiblockSet {
			val mb = Multiblock()
			
			for (l in DREAMWOOD_POSITIONS)
				mb.addComponent(l[0], l[1] + 1, l[2], ModBlocks.dreamwood, 0)
			for (g in GLIMMERING_DREAMWOOD_POSITIONS)
				mb.addComponent(g[0], g[1] + 1, g[2], ModBlocks.dreamwood, 5)
			for (p in PYLON_POSITIONS)
				mb.addComponent(-p[0], p[1] + 1, -p[2], AlfheimBlocks.alfheimPylon, 0)
			for (p in POOL_POSITIONS)
				mb.addComponent(-p[0], p[1] + 1, -p[2], ModBlocks.pool, 0)
			
			mb.addComponent(0, 1, 0, AlfheimBlocks.alfheimPortal, 0)
			mb.setRenderOffset(0, -1, 0)
			
			return mb.makeSet()
		}
	}
}