package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.asm.hook.extender.SparkExtender.attachTile
import com.google.gson.Gson
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.BlockFluidBase
import vazkii.botania.api.lexicon.multiblock.*
import vazkii.botania.api.mana.IManaPool
import vazkii.botania.api.mana.spark.*
import vazkii.botania.common.block.tile.mana.TilePool
import kotlin.math.*

class TileRealityAnchor: ASJTile(), ISparkAttachable {
	
	var mana = 0
	
	override fun updateEntity() {
		recieveMana(-50)
		
		val spark = attachedSpark ?: return
		val (x, y, z) = Vector3.fromTileEntityCenter(this)
		
		SparkHelper.getSparksAround(worldObj, x, y, z).forEach {
			if (it.attachedTile is IManaPool)
				it.registerTransfer(spark)
		}
	}
	
	fun checkStructure(): Boolean {
		for (i in 0.bidiRange(2))
			for (j in 0.bidiRange(2))
				for (k in 0.bidiRange(2)) {
					if (abs(i) != 2 && abs(j) != 2 && abs(k) != 2) continue
					if (worldObj.getBlockMetadata(xCoord + i, yCoord + j, zCoord + k) != 0) return false
					if ((worldObj.getBlock(xCoord + i, yCoord + j, zCoord + k) as? BlockFluidBase)?.fluid?.name != "mana") return false
				}
		
		return SchemaUtils.checkStructure(worldObj, xCoord, yCoord, zCoord, schema)
	}
	
	override fun getCurrentMana() = mana
	
	override fun isFull() = mana >= MAX_MANA
	
	override fun recieveMana(mana: Int) {
		this.mana = max(0, min(MAX_MANA, this.mana + mana))
		ASJUtilities.dispatchTEToNearbyPlayers(this)
	}
	
	override fun canRecieveManaFromBursts() = true
	
	override fun canAttachSpark(stack: ItemStack?) = true
	
	override fun attachSpark(entity: ISparkEntity?) {
		entity.attachTile(this)
	}
	
	override fun getAvailableSpaceForMana() = max(0, MAX_MANA - mana)
	
	override fun getAttachedSpark(): ISparkEntity? {
		val sparks = getEntitiesWithinAABB(worldObj, ISparkEntity::class.java, boundingBox(1).offset(0, 1, 0))
			.filter { it.attachedTile === this }
		
		return if (sparks.size == 1) sparks[0] else null
	}
	
	override fun areIncomingTranfersDone() = false // !RagnarokHandler.ginnungagap
	
	companion object {
		
		const val MAX_MANA = TilePool.MAX_MANA * 10
		
		val schema = SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/RealityAnchor")
		
		fun makeMultiblockSet(): MultiblockSet {
			val mb = Multiblock()
			
			for (ele in SchemaUtils.parse(schema)) {
				val block = Block.getBlockFromName(ele.block) ?: continue
				
				for (loc in ele.location)
					mb.addComponent(loc.x, loc.y + 4, loc.z, block, loc.meta)
			}
			
			for (i in 0.bidiRange(2))
				for (j in 0.bidiRange(2))
					for (k in 0.bidiRange(2)) {
						if (abs(i) != 2 && abs(j) != 2 && abs(k) != 2) continue
						mb.addComponent(i, j + 4, k, AlfheimBlocks.manaFluidBlock, 0)
					}
			
			mb.setRenderOffset(0, -2, 0)
			
			return mb.makeSet()
		}
	}
}
