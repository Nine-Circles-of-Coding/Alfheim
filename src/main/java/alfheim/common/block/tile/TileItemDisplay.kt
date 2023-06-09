package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*

// change to TileItemContainer ???
class TileItemDisplay: ASJTile(), ISidedInventory {
	
	private val slots = intArrayOf(0)
	
	private var inventory = arrayOfNulls<ItemStack>(1)
	
	override fun getSizeInventory() = 1
	
	override fun getStackInSlot(par1: Int) = inventory[par1]
	
	override fun decrStackSize(slot: Int, size: Int): ItemStack? {
		if (inventory[slot] != null) {
			
			if (!worldObj.isRemote) {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)
			}
			
			val itemstack: ItemStack
			
			return if (inventory[slot]!!.stackSize <= size) {
				itemstack = inventory[slot]!!
				inventory[slot] = null
				markDirty()
				itemstack
			} else {
				itemstack = inventory[slot]!!.splitStack(size)
				if (inventory[slot]!!.stackSize == 0) {
					inventory[slot] = null
				}
				
				markDirty()
				itemstack
			}
		}
		return null
	}
	
	override fun getStackInSlotOnClosing(par1: Int): ItemStack? {
		return if (inventory[par1] != null) {
			val itemstack = inventory[par1]
			inventory[par1] = null
			itemstack
		} else {
			null
		}
	}
	
	override fun setInventorySlotContents(par1: Int, par2ItemStack: ItemStack?) {
		inventory[par1] = par2ItemStack
		if (par2ItemStack != null && par2ItemStack.stackSize > this.inventoryStackLimit) {
			par2ItemStack.stackSize = this.inventoryStackLimit
		}
		
		ASJUtilities.dispatchTEToNearbyPlayers(this)
	}
	
	override fun getInventoryName() = "container.itemDisplay"
	
	override fun isUseableByPlayer(player: EntityPlayer) = false
	
	override fun hasCustomInventoryName() = false
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		val list = nbt.getTagList("Items", 10)
		inventory = arrayOfNulls(this.sizeInventory)
		
		for (i in 0 until list.tagCount()) {
			val nbti = list.getCompoundTagAt(i)
			
			val b0: Int = (nbti.getByte("Slot")).I
			
			if (b0 >= 0 && b0 < inventory.size) {
				inventory[b0] = ItemStack.loadItemStackFromNBT(nbti)
			}
		}
		
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		val list = NBTTagList()
		
		for (i in inventory.indices) {
			if (inventory[i] != null) {
				val nbti = NBTTagCompound()
				nbti.setByte("Slot", i.toByte())
				inventory[i]!!.writeToNBT(nbti)
				list.appendTag(nbti)
			}
		}
		
		nbt.setTag("Items", list)
	}
	
	override fun openInventory() = Unit
	override fun closeInventory() = Unit
	override fun getInventoryStackLimit() = 1
	override fun canUpdate() = false
	override fun isItemValidForSlot(par1: Int, par2ItemStack: ItemStack?) = true
	override fun getAccessibleSlotsFromSide(par1: Int) = slots
	override fun canInsertItem(par1: Int, par2ItemStack: ItemStack?, par3: Int) = getStackInSlot(par1) == null
	override fun canExtractItem(par1: Int, par2ItemStack: ItemStack?, par3: Int) = true
}
