package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.TileImmobile
import alexsocol.asjlib.math.Vector3
import alfheim.api.block.tile.SubTileAnomalyBase
import alfheim.common.item.equipment.bauble.ItemSpatiotemporalRing
import alfheim.common.world.dim.alfheim.biome.*
import net.minecraft.entity.EntityList
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import vazkii.botania.common.Botania

class TileAnomaly: TileImmobile() {
	
	var seed = 0L
	val subTiles = HashMap<String, SubTileAnomalyBase>()
	var mainSubTile: String? = null
	var compatibilityBit = 0 // not serializing because will be recalculated on load
	
	override fun updateEntity() {
		super.updateEntity()
		
		if (seed == 0L)
			seed = worldObj.rand.nextLong()
		
		val main = subTiles[mainSubTile] ?: return
		val l = main.targets as MutableList<Any?>
		
		l.removeIf { it is EntityPlayer && ItemSpatiotemporalRing.hasProtection(it) }
		for (subTile in subTiles.values) subTile.updateEntity(l)
		
		if (Botania.thaumcraftLoaded && worldObj.rand.nextInt(6000) == 0) spawnWisps()
	}
	
	fun spawnWisps() {
		if (worldObj.isRemote || !worldObj.getBiomeGenForCoords(xCoord, zCoord).let { it is BiomeField || it is BiomeForest || it is BiomeForest2 }) return
		if (mainSubTile != "Warp" && mainSubTile != "Lightning") return
		
		for (i in 0..worldObj.rand.nextInt(3))
			EntityList.createEntityByName("Thaumcraft.Wisp", worldObj)?.apply {
				val (x, y, z) = Vector3.fromTileEntity(this@TileAnomaly).add(0.5)
				setPosition(x, y, z)
				spawn()
			}
	}
	
	fun onActivated(stack: ItemStack?, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Boolean {
		var flag = false
		for (subTile in subTiles.values) flag = flag or subTile.onActivated(stack, player, world, x, y, z)
		return flag
	}
	
	fun addSubTile(name: String): TileAnomaly {
		return addSubTile(SubTileAnomalyBase.forName(name), name)
	}
	
	fun addSubTile(sub: SubTileAnomalyBase?, name: String): TileAnomaly {
		if (sub == null || !canAdd(sub)) return this
		
		compatibilityBit = compatibilityBit or sub.typeBits()
		
		if (mainSubTile == null || mainSubTile!!.isEmpty()) mainSubTile = name
		
		subTiles[name] = sub
		sub.superTile = this
		return sub.superTile as TileAnomaly
	}
	
	fun canAdd(sub: SubTileAnomalyBase): Boolean {
		return compatibilityBit and sub.typeBits() == 0
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		super.writeCustomNBT(nbt)
		nbt.setLong(TAG_SEED, seed)
		
		if (mainSubTile == null) return
		
		try {
			nbt.setString(TAG_SUBTILE_MAIN, mainSubTile!!)
			
			var c = subTiles.keys.size
			nbt.setInteger(TAG_SUBTILE_COUNT, c)
			
			var subCmp: NBTTagCompound
			
			for (name in subTiles.keys) {
				nbt.setString(TAG_SUBTILE_NAME + c, name)
				
				subCmp = NBTTagCompound()
				nbt.setTag(TAG_SUBTILE_CMP + c--, subCmp)
				
				subTiles[name]!!.writeToNBT(subCmp)
			}
		} catch (e: Throwable) {
			ASJUtilities.error("Got exception writing anomaly data. It will be discarded.")
			e.printStackTrace()
		}
		
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		super.readCustomNBT(nbt)
		
		seed = nbt.getLong(TAG_SEED)
		mainSubTile = nbt.getString(TAG_SUBTILE_MAIN)
		
		var c = nbt.getInteger(TAG_SUBTILE_COUNT)
		
		var subTileName: String
		var subCmp: NBTTagCompound
		var subTile: SubTileAnomalyBase?
		
		while (c > 0) {
			subTileName = nbt.getString(TAG_SUBTILE_NAME + c)
			subTile = SubTileAnomalyBase.forName(subTileName)
			
			subCmp = nbt.getCompoundTag(TAG_SUBTILE_CMP + c)
			if (subTile != null && !subCmp.hasNoTags())
				subTile.readFromNBT(subCmp)
			
			addSubTile(subTile, subTileName)
			c--
		}
	}
	
	companion object {
		
		const val TAG_SUBTILE_MAIN = "subTileMain"
		const val TAG_SUBTILE_NAME = "subTileName"
		const val TAG_SUBTILE_CMP = "subTileCmp"
		const val TAG_SUBTILE_COUNT = "subTileCount"
		const val TAG_SEED = "seed"
	}
}