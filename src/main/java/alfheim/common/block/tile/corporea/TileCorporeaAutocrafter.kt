package alfheim.common.block.tile.corporea

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import alfheim.common.block.tile.corporea.TileCorporeaAutocrafter.EnumState.*
import alfheim.common.core.helper.CorporeaAdvancedHelper.getFilters
import alfheim.common.core.helper.CorporeaAdvancedHelper.putOrDrop
import alfheim.common.core.helper.CorporeaAdvancedHelper.requestMatches
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.MathHelper
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.api.corporea.*
import vazkii.botania.common.block.tile.corporea.TileCorporeaFunnel
import vazkii.botania.common.core.helper.InventoryHelper
import kotlin.collections.sumOf

class TileCorporeaAutocrafter: ASJTile(), ICorporeaInterceptor, IInventory {
	
	var pendingRequest = false
	
	var request: Any? = null
	var leftToCraft = 0
	var requestMissing = 0
	var requestX = 0
	var requestY = -1
	var requestZ = 0
	
	var state = OK
	
	/** how many items will be produced from this autocrafter */
	var craftResult = 1
	
	var prevRedstone = false
	
	val spark: ICorporeaSpark? get() = CorporeaHelper.getSparkForBlock(worldObj, xCoord, yCoord, zCoord)
	
	override fun interceptRequestLast(request: Any?, count: Int, thisSpark: ICorporeaSpark?, requestorSpark: ICorporeaSpark, allFoundStacks: MutableList<ItemStack>, allScannedInventories: MutableList<IInventory>?, doIt: Boolean) {
		if (worldObj.isRemote) return
		
		val filters = getFilters(this)
		
		for (filter in filters)
			if (requestMatches(request, filter)) {
				var missing = count
				
				for (stack in allFoundStacks) missing -= stack.stackSize
				
				if (missing > 0) {
					val requestor = requestorSpark.inventory as? TileEntity ?: continue
					
					setPendingRequest(requestor.xCoord, requestor.yCoord, requestor.zCoord, request, missing)
					
					return
				}
			}
	}
	
	fun setPendingRequest(x: Int, y: Int, z: Int, req: Any?, missing: Int) {
		if (pendingRequest) return
		
		pendingRequest = true
		
		request = req
		requestMissing = missing
		leftToCraft = MathHelper.ceiling_float_int(missing / craftResult.F)
		
		requestX = x
		requestY = y
		requestZ = z
		
		worldObj.func_147453_f(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord))
		
		ASJUtilities.dispatchTEToNearbyPlayers(this)

//		ASJUtilities.chatLog("CAC at ${Vector3.fromTileEntity(this)} got request $count of $req (missing $missing) from $x $y $z. Need to craft $requestMissing times.")
	}
	
	override fun updateEntity() {
		if (worldObj.isRemote) return
		
		checkRedstone()
		
		if (!pendingRequest) return
		
		if (leftToCraft > 0)
			return doAutocraft().also {
				ASJUtilities.dispatchTEToNearbyPlayers(this)
			}
		
		val spark = spark
		if (spark == null || spark.master == null) return
		
		val stacks = CorporeaHelper.requestItem(request, -1, spark, request is ItemStack, false)
		val count = stacks.sumOf { it.stackSize }
		
		if (count >= requestMissing) {
			fulfillRequest()
			ASJUtilities.dispatchTEToNearbyPlayers(this)
		}
	}
	
	var buffer = arrayOfNulls<ItemStack?>(27)
	var waitingForIngredient
		get() = state == WAITING
		set(value) {
			state = if (value) WAITING else OK
		}
	
	/** Used for HUD only */
	var awaitedIngredient: ItemStack? = null
	
	fun doAutocraft() {
		if (waitingForIngredient) return
		
		val patterns = updateBufferSize() ?: return
		val down = InventoryHelper.getInventory(worldObj, xCoord, yCoord - 1, zCoord) ?: InventoryHelper.getInventory(worldObj, xCoord, yCoord - 2, zCoord)
		           ?: return changeState(NO_TARGET)
		
		// not enough slots in container below
		if (down.sizeInventory < (0 until patterns.sizeInventory).indexOfLast { patterns[it] != null })
			return changeState(NOT_ENOUGH_SLOTS)
		
		// not all slots can be inserted from above
		if (down is ISidedInventory && (0 until patterns.sizeInventory).filterTo(mutableListOf()) { patterns[it] != null }.apply { removeAll(down.getAccessibleSlotsFromSide(1).asList()) }.isNotEmpty())
			return changeState(INVALID_TARGET_SLOTS)
		
		changeState(OK)
		
		val spark = spark
		
		for (i in 0 until patterns.sizeInventory) {
			val pattern = patterns[i] ?: continue
			
			if (buffer[i] != null) continue
			
			val got = CorporeaHelper.requestItem(pattern, pattern.stackSize, spark, true, true)
			val gotSize = got.sumOf { it.stackSize }
			
			// not enough
			if (gotSize < pattern.stackSize) {
				waitingForIngredient = true
				awaitedIngredient = pattern.copy().also { it.stackSize -= gotSize }
				
				got.forEach { putOrDrop(this, spark, it) }
				return
			}
			
			// else all fine
			buffer[i] = got[0].copy().apply { stackSize = gotSize }
		}
		
		// buffer is filled and everything can be passed to crafter
		
		for (i in 0 until patterns.sizeInventory) {
			patterns[i] ?: continue
			
			val ret = buffer.getOrNull(i) ?: continue
			buffer[i] = null
			
			if (down !is TileCorporeaFunnel)
				InventoryHelper.insertItemIntoInventory(down, ret, ForgeDirection.UP, i, true, true)
			
			if (ret.stackSize > 0)
				putOrDrop(this, spark, ret)
		}
		
		--leftToCraft
	}
	
	fun fulfillRequest() {
		if (!pendingRequest) return
		
		val spark = CorporeaHelper.getSparkForBlock(worldObj, requestX, requestY, requestZ)
		
		if (spark != null) {
			val inv = spark.inventory
			
			if (inv is ICorporeaRequestor) {
				inv.doCorporeaRequest(request, requestMissing, spark)
				worldObj.func_147453_f(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord))
			} else if (inv is TileCorporeaAutocrafter) {
				inv.waitingForIngredient = false
				inv.awaitedIngredient = null
			}
		}
		
		onWanded()
	}
	
	fun changeState(state: EnumState) {
		this.state = state
	}
	
	fun onWanded(): Boolean {
		pendingRequest = false
		request = null
		requestMissing = 0
		leftToCraft = 0
		requestX = 0
		requestY = -1
		requestZ = 0
		
		waitingForIngredient = false
		awaitedIngredient = null
		
		val spark = spark
		
		buffer.forEach { putOrDrop(this, spark, it) }
		buffer.fill(null)
		
		changeState(OK)
		
		updateBufferSize()
		ASJUtilities.dispatchTEToNearbyPlayers(this)
		
		return true
	}
	
	fun checkRedstone() {
		val redstone = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)
		
		if (!prevRedstone && redstone) {
			waitingForIngredient = false
			awaitedIngredient = null
		}
		
		prevRedstone = redstone
	}
	
	fun updateBufferSize(): IInventory? {
		val spark = spark
		val inv: IInventory? = InventoryHelper.getInventory(worldObj, xCoord, yCoord + 1, zCoord)
		
		if (inv == null) {
			buffer.forEach { putOrDrop(this, spark, it) }
			buffer = emptyArray()
			changeState(NO_PATTERN)
			return null
		}
		
		changeState(OK)
		
		buffer = if (inv.sizeInventory < buffer.size) {
			buffer.sliceArray(inv.sizeInventory until buffer.size).forEach { putOrDrop(this, spark, it) }
			buffer.sliceArray(0 until inv.sizeInventory)
		} else {
			buffer.ensureCapacity(inv.sizeInventory)
		}
		
		return inv
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		super.writeCustomNBT(nbt)
		
		nbt.setInteger(TAG_CRAFT_RESULT, craftResult)
		
		nbt.setBoolean(TAG_PENDING_REQUEST, pendingRequest)
		
		when (request) {
			null         -> nbt.setInteger(TAG_REQUEST_TYPE, REQUEST_NULL)
			
			is String    -> {
				nbt.setInteger(TAG_REQUEST_TYPE, REQUEST_STRING)
				nbt.setString(TAG_REQUEST_CONTENTS, request as String)
			}
			
			is ItemStack -> {
				nbt.setInteger(TAG_REQUEST_TYPE, REQUEST_ITEMSTACK)
				nbt.setTag(TAG_REQUEST_CONTENTS, (request as ItemStack).writeToNBT(NBTTagCompound()))
			}
		}
		
		nbt.setInteger(TAG_REQUEST_COUNT, requestMissing)
		nbt.setInteger(TAG_LEFT_TO_CRAFT, leftToCraft)
		
		nbt.setInteger(TAG_REQUEST_X, requestX)
		nbt.setInteger(TAG_REQUEST_Y, requestY)
		nbt.setInteger(TAG_REQUEST_Z, requestZ)
		
		nbt.setString(TAG_STATE, state.toString())
		
		val awaitData = NBTTagCompound()
		nbt.setTag(TAG_AWAITED_CONTENT, awaitedIngredient?.writeToNBT(awaitData) ?: awaitData)
		
		val invNbt = NBTTagCompound()
		invNbt.setInteger("TAG_COUNT", buffer.size)
		
		for (i in buffer.indices) {
			buffer.getOrNull(i)?.let { invNbt.setTag("TAG_SLOT_$i", it.writeToNBT(NBTTagCompound())) }
		}
		
		nbt.setTag("TAG_INVENTORY", invNbt)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		super.readCustomNBT(nbt)
		
		craftResult = nbt.getInteger(TAG_CRAFT_RESULT)
		
		pendingRequest = nbt.getBoolean(TAG_PENDING_REQUEST)
		
		request = when (nbt.getInteger(TAG_REQUEST_TYPE)) {
			REQUEST_STRING    -> nbt.getString(TAG_REQUEST_CONTENTS)
			REQUEST_ITEMSTACK -> ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(TAG_REQUEST_CONTENTS))
			else              -> null
		}
		
		requestMissing = nbt.getInteger(TAG_REQUEST_COUNT)
		leftToCraft = nbt.getInteger(TAG_LEFT_TO_CRAFT)
		
		requestX = nbt.getInteger(TAG_REQUEST_X)
		requestY = nbt.getInteger(TAG_REQUEST_Y)
		requestZ = nbt.getInteger(TAG_REQUEST_Z)
		
		state = EnumState.valueOf(nbt.getString(TAG_STATE))
		
		awaitedIngredient = ItemStack.loadItemStackFromNBT(nbt.getTag(TAG_AWAITED_CONTENT) as NBTTagCompound)
		
		val invNbt = nbt.getTag("TAG_INVENTORY") as NBTTagCompound
		
		buffer = arrayOfNulls(invNbt.getInteger("TAG_COUNT"))
		for (i in buffer.indices)
			buffer[i] = if (invNbt.hasKey("TAG_SLOT_$i")) ItemStack.loadItemStackFromNBT(invNbt.getTag("TAG_SLOT_$i") as NBTTagCompound) else null
	}
	
	enum class EnumState(val color: Int = 0xFF0000) {
		OK(0x0), NO_PATTERN, NO_TARGET, NOT_ENOUGH_SLOTS, INVALID_TARGET_SLOTS, WAITING(0xFFFF00)
	}
	
	companion object {
		
		const val TAG_CRAFT_RESULT = "craftResult"
		
		const val TAG_PENDING_REQUEST = "pendingRequest"
		const val TAG_REQUEST_TYPE = "requestType"
		const val TAG_REQUEST_CONTENTS = "requestContents"
		const val TAG_REQUEST_COUNT = "requestCount"
		const val TAG_LEFT_TO_CRAFT = "leftToCraft"
		const val TAG_REQUEST_X = "requestX"
		const val TAG_REQUEST_Y = "requestY"
		const val TAG_REQUEST_Z = "requestZ"
		
		const val TAG_AWAITED_CONTENT = "waitingFor"
		const val TAG_STATE = "state"
		
		const val REQUEST_NULL = 0
		const val REQUEST_ITEMSTACK = 1
		const val REQUEST_STRING = 2
	}
	
	// UNUSED
	
	override fun interceptRequest(request: Any?, count: Int, thisSpark: ICorporeaSpark?, requestorSpark: ICorporeaSpark?, currentlyFoundStacks: MutableList<ItemStack>?, currentlyScannedInventories: MutableList<IInventory>?, doIt: Boolean) = Unit
	
	override fun getStackInSlot(slot: Int) = null
	override fun decrStackSize(slot: Int, size: Int) = null
	override fun getSizeInventory() = 0
	override fun getStackInSlotOnClosing(slot: Int) = null
	override fun hasCustomInventoryName() = false
	override fun isItemValidForSlot(slot: Int, stack: ItemStack?) = false
	override fun getInventoryName() = null
	override fun getInventoryStackLimit() = 0
	override fun isUseableByPlayer(player: EntityPlayer?) = false
	override fun openInventory() = Unit
	override fun closeInventory() = Unit
	override fun setInventorySlotContents(slot: Int, stack: ItemStack?) = Unit
}