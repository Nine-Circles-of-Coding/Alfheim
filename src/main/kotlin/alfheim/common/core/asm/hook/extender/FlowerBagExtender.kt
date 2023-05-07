package alfheim.common.core.asm.hook.extender

import alexsocol.asjlib.*
import alfheim.api.lib.LibResourceLocations
import alfheim.common.block.AlfheimBlocks
import gloomyfolken.hooklib.asm.*
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector
import net.minecraftforge.event.entity.player.EntityItemPickupEvent
import org.lwjgl.opengl.GL11
import vazkii.botania.client.gui.SlotLocked
import vazkii.botania.client.gui.bag.*
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.item.ItemFlowerBag
import kotlin.math.min

object FlowerBagExtender {
	
	fun getStackSlotInFlowerBag(stack: ItemStack) =
		when (stack.item) {
			ModBlocks.flower.toItem()                -> stack.meta // 0..15
			ModBlocks.doubleFlower1.toItem()         -> stack.meta + 16 // 16..23
			ModBlocks.doubleFlower2.toItem()         -> stack.meta + 24 // 24..31
			AlfheimBlocks.rainbowGrass.toItem()      -> if (stack.meta == 2) 32 else -1
			AlfheimBlocks.rainbowTallFlower.toItem() -> if (stack.meta == 0) 33 else -1
			else                                     -> -1
		}
	
	// ######## ItemFlowerBag ########
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun onPickupItem(item: ItemFlowerBag, event: EntityItemPickupEvent) {
		val stack = event.item.entityItem
		val slot = getStackSlotInFlowerBag(stack)
		if (slot == -1 || stack.stackSize <= 0) return
		
		for (i in 0 until event.entityPlayer.inventory.sizeInventory) {
			if (stack.stackSize == 0) return
			if (i == event.entityPlayer.inventory.currentItem) continue  // prevent item deletion
			
			val bag = event.entityPlayer.inventory[i] ?: continue
			if (bag.item !== item) continue
			
			val bagInv = ItemFlowerBag.loadStacks(bag)
			val stackAt = bagInv[slot]
			if (stackAt == null) {
				bagInv[slot] = stack.copy()
				stack.stackSize = 0
			} else {
				val stackAtSize = stackAt.stackSize
				val stackSize = stack.stackSize
				val spare = 64 - stackAtSize
				val pass = min(spare, stackSize)
				if (pass <= 0) continue
				
				stackAt.stackSize += pass
				stack.stackSize -= pass
			}
			
			ItemFlowerBag.setStacks(bag, bagInv)
		}
	}
	
	// ######## InventoryFlowerBag ########
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "<clinit>")
	fun `InventoryFlowerBag$clinit`(static: InventoryFlowerBag?) {
		ASJReflectionHelper.setStaticFinalValue(InventoryFlowerBag::class.java, arrayOfNulls<ItemStack>(34), "InventoryFlowerBag")
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun getSizeInventory(inv: InventoryFlowerBag) = 34
	
	// ######## GuiFlowerBag ########
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "<clinit>")
	fun `GuiFlowerBag$clinit`(static: GuiFlowerBag?) {
		ASJReflectionHelper.setStaticFinalValue(GuiFlowerBag::class.java, LibResourceLocations.flowerBagExtended, "texture")
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "<init>")
	fun `GuiFlowerBag$init`(gui: GuiFlowerBag, player: EntityPlayer) {
		gui.ySize += 18 * 3
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun drawGuiContainerForegroundLayer(gui: GuiFlowerBag, p_146979_1_: Int, p_146979_2_: Int) {
		val s = StatCollector.translateToLocal("item.botania:flowerBag.name")
		gui.fontRendererObj.drawString(s, gui.xSize / 2 - gui.fontRendererObj.getStringWidth(s) / 2, 6, 4210752)
		gui.fontRendererObj.drawString(I18n.format("container.inventory"), 8, gui.ySize - 94, 4210752)
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun drawGuiContainerBackgroundLayer(gui: GuiFlowerBag, p_146976_1_: Float, p_146976_2_: Int, p_146976_3_: Int) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
		mc.textureManager.bindTexture(LibResourceLocations.flowerBagExtended)
		val k = (gui.width - gui.xSize) / 2
		val l = (gui.height - gui.ySize) / 2
		gui.drawTexturedModalRect(k, l, 0, 0, gui.xSize, gui.ySize + 18 * 3)
		val slotList = gui.inventorySlots.inventorySlots
		
		for (slot in slotList) {
			if (slot !is SlotFlowerExtended || slot.hasStack) continue
			val stack = stackForSlot(slot.slot)
			val x: Int = gui.guiLeft + slot.xDisplayPosition
			val y: Int = gui.guiTop + slot.yDisplayPosition
			RenderHelper.enableGUIStandardItemLighting()
			RenderItem.getInstance().renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, stack, x, y)
			RenderHelper.disableStandardItemLighting()
			mc.fontRenderer.drawStringWithShadow("0", x + 11, y + 9, 0xFF6666)
		}
	}
	
	fun stackForSlot(slot: Int) = when (slot) {
		in 0..15 -> ItemStack(ModBlocks.flower, 1, slot)
		in 16..23 -> ItemStack(ModBlocks.doubleFlower1, 1, slot + 16)
		in 24..31 -> ItemStack(ModBlocks.doubleFlower2, 1, slot + 24)
		32 -> ItemStack(AlfheimBlocks.rainbowGrass, 1, 2)
		33 -> ItemStack(AlfheimBlocks.rainbowTallFlower, 1, 0)
		else -> ItemStack(Blocks.red_flower)
	}
	
	// ######## ContainerFlowerBag ########
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "<init>")
	fun `ContainerFlowerBag$init`(bag: ContainerFlowerBag, player: EntityPlayer) {
		bag.inventorySlots.clear()
		bag.inventoryItemStacks.clear()
		
		val slot = player.inventory.currentItem
		val playerInv: IInventory = player.inventory
		bag.flowerBagInv = InventoryFlowerBag(player, slot)
		
		for (i in 0..3)
			for (j in 0..7) {
				val k = j + i * 8
				bag.addSlotToContainer(SlotFlowerExtended(bag.flowerBagInv, 17 + j * 18, 26 + i * 18, k))
			}
		
		bag.addSlotToContainer(SlotFlowerExtended(bag.flowerBagInv, 71, 98, 32))
		bag.addSlotToContainer(SlotFlowerExtended(bag.flowerBagInv, 89, 98, 33))
		
		for (i in 0..2)
			for (j in 0..8)
				bag.addSlotToContainer(Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 138 + i * 18))
				
		for (i in 0..8)
			if (player.inventory.currentItem == i)
				bag.addSlotToContainer(SlotLocked(playerInv, i, 8 + i * 18, 196))
			else
				bag.addSlotToContainer(Slot(playerInv, i, 8 + i * 18, 196))
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun transferStackInSlot(bag: ContainerFlowerBag, player: EntityPlayer?, slotID: Int): ItemStack? {
		val slot = bag.inventorySlots.getOrNull(slotID) as? Slot ?: return null
		if (!slot.hasStack) return null

		val itemstack1 = slot.stack
		val itemstack = itemstack1.copy()

		if (slotID < 34) {
			if (!bag.fuckingPrivateMergeItemStackCopy(itemstack1, 34, 70, true)) return null
		} else {
			val i = getStackSlotInFlowerBag(itemstack)
			if (i != -1) {
				val slot1 = bag.inventorySlots[i] as Slot
				if (slot1.isItemValid(itemstack) && !bag.fuckingPrivateMergeItemStackCopy(itemstack1, i, i + 1, true)) return null
			}
		}
		if (itemstack1.stackSize == 0) slot.putStack(null as ItemStack?) else slot.onSlotChanged()
		if (itemstack1.stackSize == itemstack.stackSize) return null

		slot.onPickupFromSlot(player, itemstack1)

		return itemstack
	}
	
	private fun Container.addSlotToContainer(slot: Slot): Slot {
		slot.slotNumber = inventorySlots.size
		inventorySlots.add(slot)
		inventoryItemStacks.add(null)
		return slot
	}
	
	private fun Container.fuckingPrivateMergeItemStackCopy(fuckingStack: ItemStack, fuckingSlotRangeStartInclusive: Int, fuckingSlotRangeEndExclusive: Int, fuckingReverseSlotSearch: Boolean): Boolean {
		var fuckingResult = false
		var fukingVariable = fuckingSlotRangeStartInclusive
		if (fuckingReverseSlotSearch) {
			fukingVariable = fuckingSlotRangeEndExclusive - 1
		}
		var fuckingSlot: Slot
		var fuckingStack1: ItemStack?
		if (fuckingStack.isStackable) {
			while (fuckingStack.stackSize > 0 && (!fuckingReverseSlotSearch && fukingVariable < fuckingSlotRangeEndExclusive || fuckingReverseSlotSearch && fukingVariable >= fuckingSlotRangeStartInclusive)) {
				fuckingSlot = inventorySlots[fukingVariable] as Slot
				fuckingStack1 = fuckingSlot.stack
				if (fuckingStack1 != null && fuckingStack1.item === fuckingStack.item && (!fuckingStack.hasSubtypes || fuckingStack.getItemDamage() == fuckingStack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(fuckingStack, fuckingStack1)) {
					val fuckingStackSize = fuckingStack1.stackSize + fuckingStack.stackSize
					if (fuckingStackSize <= fuckingStack.maxStackSize) {
						fuckingStack.stackSize = 0
						fuckingStack1.stackSize = fuckingStackSize
						fuckingSlot.onSlotChanged()
						fuckingResult = true
					} else if (fuckingStack1.stackSize < fuckingStack.maxStackSize) {
						fuckingStack.stackSize -= fuckingStack.maxStackSize - fuckingStack1.stackSize
						fuckingStack1.stackSize = fuckingStack.maxStackSize
						fuckingSlot.onSlotChanged()
						fuckingResult = true
					}
				}
				if (fuckingReverseSlotSearch) {
					--fukingVariable
				} else {
					++fukingVariable
				}
			}
		}
		if (fuckingStack.stackSize > 0) {
			fukingVariable = if (fuckingReverseSlotSearch) {
				fuckingSlotRangeEndExclusive - 1
			} else {
				fuckingSlotRangeStartInclusive
			}
			while (!fuckingReverseSlotSearch && fukingVariable < fuckingSlotRangeEndExclusive || fuckingReverseSlotSearch && fukingVariable >= fuckingSlotRangeStartInclusive) {
				fuckingSlot = inventorySlots[fukingVariable] as Slot
				fuckingStack1 = fuckingSlot.stack
				if (fuckingStack1 == null) {
					fuckingSlot.putStack(fuckingStack.copy())
					fuckingSlot.onSlotChanged()
					fuckingStack.stackSize = 0
					fuckingResult = true
					break
				}
				if (fuckingReverseSlotSearch) {
					--fukingVariable
				} else {
					++fukingVariable
				}
			}
		}
		return fuckingResult
	}
}

class SlotFlowerExtended(inv: InventoryFlowerBag, xPos: Int, yPos: Int, var slot: Int): Slot(inv, slot, xPos, yPos) {
	
	override fun onSlotChange(oldStack: ItemStack, newStack: ItemStack) {
		inventory[slot] = newStack
	}
	
	override fun isItemValid(stack: ItemStack) = FlowerBagExtender.getStackSlotInFlowerBag(stack) == slot
}