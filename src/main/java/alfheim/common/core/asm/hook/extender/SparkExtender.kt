package alfheim.common.core.asm.hook.extender

import alexsocol.asjlib.*
import gloomyfolken.hooklib.asm.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ChunkCoordinates
import vazkii.botania.api.mana.spark.*
import vazkii.botania.common.block.tile.*
import vazkii.botania.common.block.tile.mana.TilePool
import vazkii.botania.common.entity.EntitySpark

@Suppress("unused")
object SparkExtender {
	
	// ####################################################################################
	// #### Fix for client "push out of blocks" bug causing no beams on mana transfer: ####
	// ####################################################################################
	
	const val TAG_ATTACHED_TILE_COORDS = "alfheim:attachedTileCoords"
	const val TILE_COORDS_DATA_WATCHER_KEY = 29
	
	var EntitySpark.attachedTileCoords: ChunkCoordinates
		get() = dataWatcher.getWatchableObjectChunkCoordinates(TILE_COORDS_DATA_WATCHER_KEY)
		set(value) = dataWatcher.updateObject(TILE_COORDS_DATA_WATCHER_KEY, value)
	
	fun ISparkEntity?.attachTile(tile: ISparkAttachable?) {
		if (this is EntitySpark && tile is TileEntity) attachedTileCoords = ChunkCoordinates(tile.xCoord, tile.yCoord, tile.zCoord)
	}
	
	@JvmStatic
	@Hook
	fun entityInit(spark: EntitySpark) {
		spark.dataWatcher.addObject(TILE_COORDS_DATA_WATCHER_KEY, ChunkCoordinates(0, -1, 0))
		spark.dataWatcher.setObjectWatched(TILE_COORDS_DATA_WATCHER_KEY)
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_NOT_NULL)
	fun getAttachedTile(spark: EntitySpark): ISparkAttachable? {
		val (x, y, z) = spark.attachedTileCoords
		return spark.worldObj.getTileEntity(x, y, z) as? ISparkAttachable
	}
	
	@JvmStatic
	@Hook
	fun readEntityFromNBT(spark: EntitySpark, nbt: NBTTagCompound) {
		val coords = nbt.getIntArray(TAG_ATTACHED_TILE_COORDS)
		if (coords?.size != 3) return
		
		val (x, y, z) = coords
		spark.attachedTileCoords = ChunkCoordinates(x, y, z)
	}
	
	@JvmStatic
	@Hook
	fun writeEntityToNBT(spark: EntitySpark, nbt: NBTTagCompound) {
		val (x, y, z) = spark.attachedTileCoords
		nbt.setIntArray(TAG_ATTACHED_TILE_COORDS, intArrayOf(x, y, z))
	}
	
	@JvmStatic
	@Hook
	fun attachSpark(tile: TileEnchanter, entity: ISparkEntity?) {
		entity.attachTile(tile)
	}
	
	@JvmStatic
	@Hook
	fun attachSpark(tile: TilePool, entity: ISparkEntity?) {
		entity.attachTile(tile)
	}
	
	@JvmStatic
	@Hook
	fun attachSpark(tile: TileTerraPlate, entity: ISparkEntity?) {
		entity.attachTile(tile)
	}
	
	// ################################
	// #### Spark length extender: ####
	// ################################
//
//	@JvmStatic
//	@Hook(injectOnExit = true)
//	fun onUpdate(spark: EntitySpark) {
//		if (spark.attachedTile !is IManaPool) return
//		if (spark.upgrade != 5) return
//		if (spark.transfers.isEmpty()) return
//
//		// Conductor
//		SparkHelper.getSparksAround(spark.worldObj, spark.posX, spark.posY, spark.posZ).filter {
//			it !== spark && (it.upgrade == 0 || it.upgrade == 5) && it.attachedTile is IManaPool && it !in spark.transfers // && Vector3.entityDistance(spark, it as Entity) <= SPARK_SCAN_RANGE
//		}.minByOrNull { Vector3.entityDistance(spark, it as Entity) }?.registerTransfer(spark)
//	}
//
//	@JvmStatic
//	@Hook(returnCondition = ReturnCondition.ALWAYS)
//	fun getTransfers(spark: EntitySpark): MutableCollection<ISparkEntity?>? {
//		spark.transfers.removeAll { e ->
//			val upgr = spark.upgrade
//			val supgr = e.upgrade
//			val atile = e.attachedTile
//
//			val keep = e !== spark && !e.areIncomingTransfersDone() && atile != null && !atile.isFull && (
//				atile !is IManaPool ||                                      // not to pool
//				upgr == 0 && (supgr == 2 || supgr == 5) ||                  // from Regular     to Dominant/Conductor pool
//				upgr == 3 && (supgr == 0 || supgr == 1 || supgr == 5) ||    // from Recessive   to Regular/Dispersive/Conductor pool
//				upgr == 5 && (supgr == 0 || supgr == 5)                     // from Conductor   to Regular/Conductor pool
//			)
//
//			!keep
//		}
//
//		return spark.transfers
//	}
//
//	@JvmStatic
//	@Hook(returnCondition = ReturnCondition.ON_TRUE)
//	fun areIncomingTransfersDone(spark: EntitySpark): Boolean {
//		if (spark.upgrade != 5) return false
//		return spark.transfers.isEmpty()
//	}
//
//	// item
//
//	lateinit var iconConductor: IIcon
//	lateinit var iconConductorWorld: IIcon
//
//	@JvmStatic
//	@Hook(injectOnExit = true)
//	@SideOnly(Side.CLIENT)
//	fun registerIcons(upgrade: ItemSparkUpgrade, reg: IIconRegister) {
//		iconConductorWorld = IconHelper.forItem(reg, upgrade, "L4")
//		iconConductor = IconHelper.forItem(reg, upgrade, 4)
//	}
//
//	@JvmStatic
//	@Hook(returnCondition = ReturnCondition.ON_NOT_NULL)
//	@SideOnly(Side.CLIENT)
//	fun getIconFromDamage(upgrade: ItemSparkUpgrade, meta: Int) = if (meta == 4) iconConductor else null
//
//	@JvmStatic
//	@Hook(injectOnExit = true)
//	fun getSubItems(upgrade: ItemSparkUpgrade, item: Item?, tab: CreativeTabs?, list: MutableList<ItemStack>) {
//		list.add(ItemStack(item, 1, 4))
//	}
//
//	// render
//
//	@JvmStatic
//	@Hook(returnCondition = ReturnCondition.ON_NOT_NULL)
//	@SideOnly(Side.CLIENT)
//	fun getSpinningIcon(render: RenderSpark, entity: EntitySpark) = if (entity.upgrade - 1 == 4) iconConductorWorld else null
}