package alfheim.common.block.tile

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.math.Vector3
import alfheim.AlfheimCore
import alfheim.api.entity.raceID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.AlfheimItems
import alfheim.common.item.AlfheimItems.ElvenResourcesMetas
import com.google.common.base.Function
import net.minecraft.block.Block
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import vazkii.botania.api.lexicon.multiblock.*
import vazkii.botania.common.Botania
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.block.tile.TileMod
import vazkii.botania.common.block.tile.mana.TilePool
import vazkii.botania.common.core.handler.ConfigHandler
import java.util.*
import kotlin.math.*

class TileAlfheimPortal: TileMod() {
	
	var ticksOpen = 0
	private var closeNow = false
	private var hasUnloadedParts = false
	
	var posX = 0
	var posY = -1
	var posZ = 0
	
	internal val portalAABB: AxisAlignedBB
		get() {
			var aabb = AxisAlignedBB.getBoundingBox((xCoord - 1).toDouble(), (yCoord + 1).toDouble(), zCoord + 0.25, (xCoord + 2).toDouble(), (yCoord + 4).toDouble(), zCoord + 0.75)
			if (getBlockMetadata() == 2)
				aabb = AxisAlignedBB.getBoundingBox(xCoord + 0.25, (yCoord + 1).toDouble(), (zCoord - 1).toDouble(), xCoord + 0.75, (yCoord + 4).toDouble(), (zCoord + 2).toDouble())
			
			return aabb
		}
	
	val validMetadata: Int
		get() {
			if (checkConverter(null))
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
		
		if (!hasUnloadedParts) {
			ticksOpen++
			
			val aabb = portalAABB
			
			if (ticksOpen > 60) {
				val players = worldObj.getEntitiesWithinAABB(EntityPlayer::class.java, aabb)
				if (!worldObj.isRemote)
					for (player in players) {
						player as EntityPlayer
						if (player.isDead) continue
						
						if (player.dimension == AlfheimConfigHandler.dimensionIDAlfheim) {
							var coords: ChunkCoordinates? = player.getBedLocation(0)
							if (coords == null) coords = MinecraftServer.getServer().worldServerForDimension(0).spawnPoint
							if (coords == null) coords = ChunkCoordinates(0, MinecraftServer.getServer().worldServerForDimension(0).getHeightValue(0, 0) + 3, 0)
							
							if (AlfheimConfigHandler.destroyPortal && (xCoord != 0 || zCoord != 0)) {
								worldObj.newExplosion(player, xCoord.toDouble(), yCoord.toDouble(), zCoord.toDouble(), 5f, false, false)
								val x = if (meta == 1) 2 else 0
								val z = if (meta == 1) 0 else 2
								worldObj.setBlockToAir(xCoord - x, yCoord + 2, zCoord - z)
								worldObj.setBlockToAir(xCoord + x, yCoord + 2, zCoord + z)
								worldObj.setBlockToAir(xCoord, yCoord + 4, zCoord)
								worldObj.setBlockToAir(xCoord, yCoord, zCoord)
							}
							
							ASJUtilities.sendToDimensionWithoutPortal(player, 0, coords.posX.toDouble(), coords.posY.toDouble(), coords.posZ.toDouble())
						} else {
							if (AlfheimCore.enableElvenStory) {
								val race = player.raceID - 1 // for array length
								if (race in 0..8)
									ASJUtilities.sendToDimensionWithoutPortal(player, AlfheimConfigHandler.dimensionIDAlfheim, AlfheimConfigHandler.zones[race].x, AlfheimConfigHandler.zones[race].y, AlfheimConfigHandler.zones[race].z)
								else {
									if (AlfheimConfigHandler.bothSpawnStructures)
										findAndTP(player)
									else
										ASJUtilities.sendToDimensionWithoutPortal(player, AlfheimConfigHandler.dimensionIDAlfheim, 0.5, 253.0, 0.5)
								}
							} else
								findAndTP(player)
						}
					}
				if (ConfigHandler.elfPortalParticlesEnabled) blockParticle(meta)
			}
		} else
			closeNow = false
		
		if (closeNow) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3)
			if (!worldObj.isRemote && posX == xCoord && posY == yCoord  && posZ == zCoord) worldObj.spawnEntityInWorld(EntityItem(worldObj, xCoord + 0.5, yCoord + 1.5, zCoord + 0.5, ItemStack(AlfheimItems.elvenResource, 1, ElvenResourcesMetas.InterdimensionalGatewayCore)))
			for (i in 0..35)
				blockParticle(meta)
			closeNow = false
		} else if (newMeta != meta) {
			if (newMeta == 0) {
				if (!worldObj.isRemote && posX == xCoord && posY == yCoord  && posZ == zCoord) worldObj.spawnEntityInWorld(EntityItem(worldObj, xCoord + 0.5, yCoord + 1.5, zCoord + 0.5, ItemStack(AlfheimItems.elvenResource, 1, ElvenResourcesMetas.InterdimensionalGatewayCore)))
				for (i in 0..35)
					blockParticle(meta)
			}
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, newMeta, 3)
		}
		
		hasUnloadedParts = false
	}
	
	private fun findAndTP(player: EntityPlayer) {
		ASJUtilities.sendToDimensionWithoutPortal(player, AlfheimConfigHandler.dimensionIDAlfheim, 0.5, 75.0, -1.5)
		val alfheim = MinecraftServer.getServer().worldServerForDimension(AlfheimConfigHandler.dimensionIDAlfheim)
		for (y in 50..149) {
			if (alfheim.getBlock(0, y, 0) === AlfheimBlocks.alfheimPortal && alfheim.getBlockMetadata(0, y, 0) == 1) {
				ASJUtilities.sendToDimensionWithoutPortal(player, AlfheimConfigHandler.dimensionIDAlfheim, 0.5, (y + 1).toDouble(), -1.5)
				break
			}
		}
	}
	
	private fun blockParticle(meta: Int) {
		val i = worldObj.rand.nextInt(AIR_POSITIONS.size)
		var pos: DoubleArray? = doubleArrayOf((AIR_POSITIONS[i][0] + 0.5f).toDouble(), (AIR_POSITIONS[i][1] + 0.5f).toDouble(), (AIR_POSITIONS[i][2] + 0.5f).toDouble())
		if (meta == 2)
			pos = CONVERTER_X_Z_FP.apply(pos)
		
		val motionMul = 0.2f
		Botania.proxy.wispFX(getWorldObj(), xCoord + pos!![0], yCoord + pos[1], zCoord + pos[2],
							 Math.random().toFloat() * 0.25f + 0.5f, Math.random().toFloat() * 0.25f + 0.5f, Math.random().toFloat() * 0.25f,
							 (Math.random() * 0.15f + 0.1f).toFloat(),
							 (Math.random() - 0.5f).toFloat() * motionMul, (Math.random() - 0.5f).toFloat() * motionMul, (Math.random() - 0.5f).toFloat() * motionMul)
	}
	
	fun onWanded(newMeta: Int): Boolean {
		val meta = getBlockMetadata()
		if (meta == 0 && newMeta != 0) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, newMeta, 3)
			posX = xCoord
			posY = yCoord
			posZ = zCoord
			return true
		}
		
		return false
	}
	
	override fun writeCustomNBT(cmp: NBTTagCompound) {
		cmp.setInteger(TAG_TICKS_OPEN, ticksOpen)
		cmp.setInteger(TAG_POS_X, posX)
		cmp.setInteger(TAG_POS_Y, posY)
		cmp.setInteger(TAG_POS_Z, posZ)
	}
	
	override fun readCustomNBT(cmp: NBTTagCompound) {
		ticksOpen = cmp.getInteger(TAG_TICKS_OPEN)
		posX = cmp.getInteger(TAG_POS_X)
		posY = cmp.getInteger(TAG_POS_Y)
		posZ = cmp.getInteger(TAG_POS_Z)
	}
	
	private fun checkConverter(baseConverter: Function<IntArray, IntArray>?) =
		checkMultipleConverters(arrayOf(baseConverter)) || checkMultipleConverters(arrayOf(CONVERTER_Z_SWAP, baseConverter))
	
	private fun checkMultipleConverters(converters: Array<Function<IntArray, IntArray>?>?): Boolean {
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
	
	private fun lightPylons(converters: Array<Function<IntArray, IntArray>?>?) {
		if (ticksOpen < 50)
			return
		
		val cost = if (ticksOpen == 50) activation else idle
		
		for (pos in PYLON_POSITIONS) {
			var pos = pos
			converters?.forEach { pos = it?.apply(pos) ?: pos }

			var tile = worldObj.getTileEntity(xCoord + pos[0], yCoord + pos[1], zCoord + pos[2])
			if (tile is TileAlfheimPylon) {
				
				val centerBlock = Vector3(xCoord + 0.5, yCoord.toDouble() + 0.75 + (Math.random() - 0.5 * 0.25), zCoord + 0.5)
				
				if (ConfigHandler.elfPortalParticlesEnabled) {
					var worldTime = worldObj.totalWorldTime.toDouble()
					rand.setSeed((xCoord + pos[0] xor yCoord + pos[1] xor zCoord + pos[2]).toLong())
					worldTime += rand.nextInt(1000).toDouble()
					worldTime /= 5.0
					
					val r = 0.75f + Math.random().toFloat() * 0.05f
					val x = xCoord.toDouble() + pos[0].toDouble() + 0.5 + cos(worldTime) * r
					val z = zCoord.toDouble() + pos[2].toDouble() + 0.5 + sin(worldTime) * r
					
					Botania.proxy.wispFX(worldObj, x, yCoord.toDouble() + pos[1].toDouble() + 0.25, z,
										 0.75f + Math.random().toFloat() * 0.25f, Math.random().toFloat() * 0.25f, 0.75f + Math.random().toFloat() * 0.25f,
										 0.25f + Math.random().toFloat() * 0.1f, -0.075f - Math.random().toFloat() * 0.015f)
					
					if (worldObj.rand.nextInt(3) == 0)
						Botania.proxy.wispFX(worldObj, x, yCoord.toDouble() + pos[1].toDouble() + 0.25, z,
											 Math.random().toFloat() * 0.25f, Math.random().toFloat() * 0.25f, 0.75f + Math.random().toFloat() * 0.25f,
											 0.25f + Math.random().toFloat() * 0.1f,
											 centerBlock.x.toFloat(), centerBlock.y.toFloat(), centerBlock.z.toFloat())
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
	
	private fun wrong2DArray(positions: Array<IntArray>, block: Block, meta: Int, converters: Array<Function<IntArray, IntArray>?>?): Boolean {
		for (pos in positions) {
			var pos = pos

			converters?.forEach { pos = it?.apply(pos) ?: pos }

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
	
	override fun getRenderBoundingBox() = TileEntity.INFINITE_EXTENT_AABB!!
	
	companion object {
		
		private val DREAMWOOD_POSITIONS = arrayOf(intArrayOf(-1, 0, 0), intArrayOf(1, 0, 0), intArrayOf(-2, 1, 0), intArrayOf(2, 1, 0), intArrayOf(-2, 3, 0), intArrayOf(2, 3, 0), intArrayOf(-1, 4, 0), intArrayOf(1, 4, 0))
		private val GLIMMERING_DREAMWOOD_POSITIONS = arrayOf(intArrayOf(-2, 2, 0), intArrayOf(2, 2, 0), intArrayOf(0, 4, 0))
		private val PYLON_POSITIONS = arrayOf(intArrayOf(-3, 1, 3), intArrayOf(3, 1, 3))
		private val POOL_POSITIONS = arrayOf(intArrayOf(-3, 0, 3), intArrayOf(3, 0, 3))
		private val AIR_POSITIONS = arrayOf(intArrayOf(-1, 1, 0), intArrayOf(0, 1, 0), intArrayOf(1, 1, 0), intArrayOf(-1, 2, 0), intArrayOf(0, 2, 0), intArrayOf(1, 2, 0), intArrayOf(-1, 3, 0), intArrayOf(0, 3, 0), intArrayOf(1, 3, 0))
		
		private const val TAG_TICKS_OPEN = "ticksOpen"
		private const val TAG_POS_X = "posX"
		private const val TAG_POS_Y = "posY"
		private const val TAG_POS_Z = "posZ"
		
		private const val activation = 75000
		private const val idle = 2
		
		private val CONVERTER_X_Z = Function<IntArray, IntArray> { input -> intArrayOf(input!![2], input[1], input[0]) }
		
		private val CONVERTER_X_Z_FP = Function<DoubleArray, DoubleArray> { input -> doubleArrayOf(input!![2], input[1], input[0]) }
		
		private val CONVERTER_Z_SWAP = Function<IntArray, IntArray> { input -> intArrayOf(input!![0], input[1], -input[2]) }
		
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