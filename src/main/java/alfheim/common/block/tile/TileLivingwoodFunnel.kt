package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import alfheim.common.block.BlockFunnel
import net.minecraft.block.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.entity.item.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.tileentity.*
import net.minecraft.util.Facing
import net.minecraft.world.World
import org.lwjgl.opengl.GL11
import vazkii.botania.common.core.helper.InventoryHelper
import vazkii.botania.common.lib.LibMisc
import kotlin.math.min

class TileLivingwoodFunnel: ASJTile(), IHopper {
	
	private var inventory = arrayOfNulls<ItemStack>(1)
	
	private var transferCooldown = -1
	
	override fun getSizeInventory() = 1
	
	override fun getStackInSlot(par1: Int) = inventory[par1]
	
	override fun updateEntity() {
		if (worldObj == null || worldObj.isRemote) return
		--transferCooldown
		
		if (transferCooldown > 0) return
		transferCooldown = 0
		if (worldObj == null || worldObj.isRemote) return
		if (!BlockFunnel.getActiveStateFromMetadata(getBlockMetadata())) return
		var flag = false
		
		if (!isEmpty()) {
			flag = pushToAttachedInventory()
		}
		
		if (!isFull()) {
			flag = canHopperPull(this) || flag
		}
		
		if (flag) {
			transferCooldown = 8
			markDirty()
		}
	}
	
	fun getInventoryAt(world: World, x: Double, y: Double, z: Double): IInventory? {
		val i = x.mfloor()
		val j = y.mfloor()
		val k = z.mfloor()
		
		return InventoryHelper.getInventory(world, i, j, k) ?: getEntitiesWithinAABB(world, IInventory::class.java, getBoundingBox(x, y, z, x + 1, y + 1, z + 1)).random(world.rand)
	}
	
	fun canHopperPull(funnel: IHopper): Boolean {
		val iinventory = getInventoryAbove(funnel)
		if (iinventory != null) {
			if (inventoryEmpty(iinventory, 0)) return false
			
			if (iinventory is ISidedInventory) {
				for (slot in iinventory.getAccessibleSlotsFromSide(0)) {
					if (pullItemIn(funnel, iinventory, slot, 0)) return true
				}
			} else {
				for (slot in 0 until iinventory.sizeInventory) {
					if (pullItemIn(funnel, iinventory, slot, 0)) return true
				}
			}
		} else {
			val entityitem = entitiesOnFunnel(funnel.worldObj, funnel.xPos, funnel.yPos + 1.0, funnel.zPos)
			
			if (entityitem != null) {
				return pullEntityFromWorld(funnel, entityitem)
			}
		}
		
		return false
	}
	
	fun getInventoryAbove(hopper: IHopper) = getInventoryAt(hopper.worldObj, hopper.xPos, hopper.yPos + 1.0, hopper.zPos)
	
	private fun isFull(): Boolean {
		val aitemstack = inventory
		val i = aitemstack.size
		
		for (j in 0 until i) {
			val itemstack = aitemstack[j]
			
			if (itemstack == null || itemstack.stackSize != itemstack.maxStackSize) {
				return false
			}
		}
		
		return true
	}
	
	private fun isEmpty(): Boolean {
		val aitemstack = inventory
		val i = aitemstack.size
		
		for (j in 0 until i) {
			val itemstack = aitemstack[j]
			
			if (itemstack != null) {
				return false
			}
		}
		
		return true
	}
	
	private fun isFull(inventory: IInventory, side: Int): Boolean {
		if (inventory is ISidedInventory && side > -1) {
			val aint = inventory.getAccessibleSlotsFromSide(side)
			
			for (l in aint.indices) {
				val itemstack1 = inventory[aint[l]]
				
				if (itemstack1 == null || itemstack1.stackSize != itemstack1.maxStackSize) {
					return false
				}
			}
		} else {
			val j = inventory.sizeInventory
			
			for (k in 0 until j) {
				val itemstack = inventory[k]
				
				if (itemstack == null || itemstack.stackSize != itemstack.maxStackSize) {
					return false
				}
			}
		}
		
		return true
	}
	
	private fun pushToAttachedInventory(): Boolean {
		val iinventory = getFacingInventory() ?: return false
		
		val i = Facing.oppositeSide[BlockHopper.getDirectionFromMetadata(getBlockMetadata())]
		
		if (this.isFull(iinventory, i)) {
			return false
		}
		for (j in 0 until this.sizeInventory) {
			if (getStackInSlot(j) != null) {
				val itemstack = getStackInSlot(j)?.copy()
				val itemstack1 = iinventory.addItemToSide(decrStackSize(j, 1), i)
				
				if (itemstack1 == null || itemstack1.stackSize == 0) {
					iinventory.markDirty()
					return true
				}
				
				setInventorySlotContents(j, itemstack)
			}
		}
		
		return false
	}
	
	private fun getFacingInventory(): IInventory? {
		val i = BlockFunnel.getDirectionFromMetadata(getBlockMetadata())
		return getInventoryAt(worldObj, (xCoord + Facing.offsetsXForSide[i]).D, (yCoord + Facing.offsetsYForSide[i]).D, (zCoord + Facing.offsetsZForSide[i]).D)
	}
	
	fun IInventory.addItemToSide(item: ItemStack?, side: Int): ItemStack? {
		var stack = item
		if (this is ISidedInventory && side > -1) {
			val aint = this.getAccessibleSlotsFromSide(side)
			
			var l = 0
			while (l < aint.size && stack != null && stack.stackSize > 0) {
				stack = pushToInventory(this, stack, aint[l], side)
				++l
			}
		} else {
			val j = this.sizeInventory
			
			var k = 0
			while (k < j && stack != null && stack.stackSize > 0) {
				stack = pushToInventory(this, stack, k, side)
				++k
			}
		}
		
		if (stack != null && stack.stackSize == 0) {
			stack = null
		}
		
		return stack
	}
	
	private fun canInsertItem(inventory: IInventory, stack: ItemStack, slot: Int, side: Int) =
		if (!inventory.isItemValidForSlot(slot, stack)) false else inventory !is ISidedInventory || inventory.canInsertItem(slot, stack, side)
	
	private fun pushToInventory(inventory: IInventory, item: ItemStack?, slot: Int, side: Int): ItemStack? {
		var stack = item
		val itemstack1 = inventory[slot]
		
		if (stack != null && canInsertItem(inventory, stack, slot, side)) {
			var flag = false
			
			if (itemstack1 == null) {
				val max = min(stack.maxStackSize, inventory.inventoryStackLimit)
				if (max >= stack.stackSize) {
					inventory[slot] = stack
					stack = null
				} else {
					inventory[slot] = stack.splitStack(max)
				}
				flag = true
			} else if (canAddToStack(itemstack1, stack)) {
				val max = min(stack.maxStackSize, inventory.inventoryStackLimit)
				if (max > itemstack1.stackSize) {
					val l = min(stack.stackSize, max - itemstack1.stackSize)
					stack.stackSize -= l
					itemstack1.stackSize += l
					flag = l > 0
				}
			}
			
			if (flag) {
				if (inventory is TileLivingwoodFunnel) {
					inventory.transferCooldown = 8
					inventory.markDirty()
				}
				
				inventory.markDirty()
			}
		}
		
		return stack
	}
	
	private fun canAddToStack(stack: ItemStack, mainStack: ItemStack) =
		if (stack.item !== mainStack.item) false else (if (stack.meta != mainStack.meta) false else (if (stack.stackSize > stack.maxStackSize) false else ItemStack.areItemStackTagsEqual(stack, mainStack)))
	
	fun pullEntityFromWorld(inventory: IInventory, item: EntityItem?): Boolean {
		var flag = false
		
		if (item == null) {
			return false
		}
		val itemstack = item.entityItem
		if (itemstack.itemInFrames()) {
			val itemstack1 = inventory.addItemToSide(itemstack, -1)
			
			if (itemstack1 != null && itemstack1.stackSize != 0) {
				item.setEntityItemStack(itemstack1)
			} else {
				flag = true
				item.setDead()
			}
		}
		
		return flag
	}
	
	fun entitiesOnFunnel(world: World, x: Double, y: Double, z: Double): EntityItem? {
		val list = selectEntitiesWithinAABB(world, EntityItem::class.java, getBoundingBox(x, y, z, x + 1, y + 1, z + 1)) {
			it.isEntityAlive && it.entityItem?.let { i -> i.stackSize > 0 } == true
		}
		return if (list.size > 0) list[0] else null
	}
	
	private fun pullItemIn(hopper: IHopper, inventory: IInventory, slot: Int, side: Int): Boolean {
		val itemstack = inventory[slot]
		
		if (itemstack != null && canPullItem(inventory, itemstack, slot, side)) {
			if (itemstack.itemInFrames()) {
				
				val itemstack1 = itemstack.copy()
				val itemstack2 = hopper.addItemToSide(inventory.decrStackSize(slot, 1), -1)
				
				if (itemstack2 == null || itemstack2.stackSize == 0) {
					inventory.markDirty()
					return true
				}
				
				inventory[slot] = itemstack1
			}
		}
		
		return false
	}
	
	private fun ItemStack.itemInFrames(): Boolean {
		val frameItems: MutableList<ItemStack> = arrayListOf()
		for (i in LibMisc.CARDINAL_DIRECTIONS) {
			val var21 = getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).offset(i.offsetX, i.offsetY, i.offsetZ)
			getEntitiesWithinAABB(worldObj, EntityItemFrame::class.java, var21).forEach {
				if (it.displayedItem != null) frameItems.add(it.displayedItem)
			}
		}
		if (frameItems.isEmpty()) return true
		
		return frameItems.any { this.item === it.item && meta == it.meta }
	}
	
	private fun canPullItem(inventory: IInventory, stack: ItemStack, slot: Int, side: Int) =
		inventory !is ISidedInventory || inventory.canExtractItem(slot, stack, side)
	
	private fun inventoryEmpty(inventory: IInventory, side: Int): Boolean {
		if (inventory is ISidedInventory && side > -1) {
			for (slot in inventory.getAccessibleSlotsFromSide(side)) {
				if (inventory[slot] != null) {
					return false
				}
			}
		} else {
			for (slot in 0 until inventory.sizeInventory) {
				if (inventory[slot] != null) {
					return false
				}
			}
		}
		
		return true
	}
	
	fun renderHUD(mc: Minecraft, res: ScaledResolution) {
		val stack = getStackInSlot(0) ?: return
		if (stack.stackSize <= 0) return
		
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting()
		
		val xc = res.scaledWidth / 2.0
		val yc = res.scaledHeight / 2.0
		GL11.glTranslated(xc, yc, 0.0)
		RenderItem.getInstance().renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, stack, 0, 0)
		GL11.glTranslated(-xc, -yc, 0.0)
		
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting()
	}
	
	override fun decrStackSize(slot: Int, size: Int): ItemStack? {
		val inSlot = inventory[slot] ?: return null
		
		if (!worldObj.isRemote)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)
		
		if (inSlot.stackSize <= size) {
			inventory[slot] = null
			markDirty()
			return inSlot
		}
		
		val itemstack = inSlot.splitStack(size)
		
		if (inSlot.stackSize <= 0)
			inventory[slot] = null
		
		markDirty()
		return itemstack
		
	}
	
	override fun getStackInSlotOnClosing(slot: Int): ItemStack? {
		val inSlot = inventory[slot] ?: return null
		inventory[slot] = null
		return inSlot
	}
	
	override fun setInventorySlotContents(slot: Int, stack: ItemStack?) {
		inventory[slot] = stack
		
		if (stack != null && stack.stackSize > inventoryStackLimit)
			stack.stackSize = inventoryStackLimit
		
		markDirty()
		if (!worldObj.isRemote)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)
	}
	
	override fun getInventoryName() = "container.livingwoodHopper"
	
	override fun isUseableByPlayer(player: EntityPlayer?) = false
	
	override fun hasCustomInventoryName() = false
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		val list = nbt.getTagList("Items", 10)
		inventory = arrayOfNulls(sizeInventory)
		
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
			val stack = inventory[i] ?: continue
			
			val nbti = NBTTagCompound()
			nbti.setByte("Slot", i.toByte())
			stack.writeToNBT(nbti)
			list.appendTag(nbti)
		}
		
		nbt.setTag("Items", list)
	}
	
	override fun getXPos() = xCoord.D
	override fun getYPos() = yCoord.D
	override fun getZPos() = zCoord.D
	override fun openInventory() = Unit
	override fun closeInventory() = Unit
	override fun getInventoryStackLimit() = 1
	override fun isItemValidForSlot(par1: Int, par2ItemStack: ItemStack) = true
}
