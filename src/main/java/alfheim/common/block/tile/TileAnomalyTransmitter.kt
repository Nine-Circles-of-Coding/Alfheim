package alfheim.common.block.tile

import alexsocol.asjlib.extendables.block.ASJTile
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ChunkCoordinates
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.api.wand.IWandBindable

class TileAnomalyTransmitter: ASJTile(), IWandBindable {
	
	var toX = 0
	var toY = -1
	var toZ = 0
	
	fun getAnomaly() = ForgeDirection.VALID_DIRECTIONS.firstNotNullOfOrNull { d -> worldObj.getTileEntity(xCoord + d.offsetX, yCoord + d.offsetY, zCoord + d.offsetZ) as? TileAnomaly }
	
	override fun bindTo(player: EntityPlayer?, wand: ItemStack?, x: Int, y: Int, z: Int, side: Int): Boolean {
		toX = x
		toY = y
		toZ = z
		
		return true
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		nbt.setInteger("toX", toX)
		nbt.setInteger("toY", toY)
		nbt.setInteger("toZ", toZ)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		toX = nbt.getInteger("toX")
		toY = nbt.getInteger("toY")
		toZ = nbt.getInteger("toZ")
	}
	
	override fun canUpdate() = false
	override fun getBinding() = if (toY == -1) null else ChunkCoordinates(toX, toY, toZ)
	override fun canSelect(player: EntityPlayer?, wand: ItemStack?, x: Int, y: Int, z: Int, side: Int) = true
}
