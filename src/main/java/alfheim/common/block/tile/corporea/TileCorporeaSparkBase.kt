package alfheim.common.block.tile.corporea

import alexsocol.asjlib.extendables.block.ASJTile
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack

class TileCorporeaSparkBase: ASJTile(), IInventory {
	override fun canUpdate() = false
	override fun getSizeInventory() = 0
	override fun getInventoryStackLimit() = 0
	override fun isItemValidForSlot(slot: Int, stack: ItemStack?) = false
	override fun setInventorySlotContents(slot: Int, stack: ItemStack?) = Unit
	override fun getStackInSlot(slot: Int) = null
	override fun decrStackSize(slot: Int, size: Int) = null
	override fun getStackInSlotOnClosing(slot: Int) = null
	override fun hasCustomInventoryName() = false
	override fun getInventoryName() = null
	override fun isUseableByPlayer(player: EntityPlayer?) = false
	override fun openInventory() = Unit
	override fun closeInventory() = Unit
}
