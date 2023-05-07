package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.TileImmobile
import alexsocol.asjlib.math.Vector3
import alfheim.common.item.material.ElvenResourcesMetas
import net.minecraft.entity.item.EntityItem
import net.minecraft.nbt.NBTTagCompound

class TileYggFlower: TileImmobile() {
	
	val canHaveFruit get() = xCoord == 2 && yCoord == 226 && zCoord ==  -45 && worldObj?.provider?.dimensionId == -105
	
	var fruitTimer = 0
	
	var hasFruit = false
		set(value) {
			val dispatch = field != value
			field = value
			if (dispatch) ASJUtilities.dispatchTEToNearbyPlayers(this)
		}
		get() = if (!canHaveFruit) false else field
	
	override fun updateEntity() {
		super.updateEntity()
		
		if (!getBlockType().canBlockStay(worldObj, xCoord, yCoord, zCoord)) worldObj.setBlockToAir(xCoord, yCoord, zCoord)
		
		if (!canHaveFruit || hasFruit || worldObj.isRemote) return
		
		hasFruit = --fruitTimer <= 0
	}
	
	fun harvestFruit(): Boolean {
		if (!hasFruit || worldObj.isRemote) return false
		hasFruit = false
		fruitTimer = ASJUtilities.randInBounds(DAY, DAY * 3, worldObj.rand)
		
		val (x, y, z) = Vector3.fromTileEntityCenter(this)
		val fruit = EntityItem(worldObj, x, y, z, ElvenResourcesMetas.YggFruit.stack)
		fruit.setMotion(0.0)
		fruit.spawn()
		
		return true
	}
	
	fun setup() {
		hasFruit = true
		lock(xCoord, yCoord, zCoord, worldObj.provider.dimensionId)
	}
	
	override fun writeToNBT(nbt: NBTTagCompound) {
		super.writeToNBT(nbt)
		nbt.setInteger(TAG_TIME, fruitTimer)
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		super.writeCustomNBT(nbt)
		nbt.setBoolean(TAG_HAS, hasFruit)
	}
	
	override fun readFromNBT(nbt: NBTTagCompound) {
		super.readFromNBT(nbt)
		fruitTimer = nbt.getInteger(TAG_TIME)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		super.readCustomNBT(nbt)
		hasFruit = nbt.getBoolean(TAG_HAS)
	}
	
	companion object {
		
		const val DAY = 20 * 60 * 60 * 24
		const val TAG_HAS = "has"
		const val TAG_TIME = "time"
	}
}
