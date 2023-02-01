package alfheim.common.block.tile.sub.flower

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.*
import net.minecraft.nbt.NBTTagCompound
import vazkii.botania.api.subtile.RadiusDescriptor.Square
import vazkii.botania.api.subtile.SubTileGenerating
import vazkii.botania.common.lexicon.LexiconData
import kotlin.math.min

class SubTileGourmaryllisUltra: SubTileGenerating() {
	
	private var cooldown = 0
	private var storedMana = 0
	private var lastFood = LimitedHashQueueArray<HashableItemStack>()
	private var lastFoodIndex = -1
	
	val worldObj get() = supertile.worldObj!!
	
	override fun onUpdate() {
		super.onUpdate()
		
		if (worldObj.isRemote) return
		
		if (cooldown > -1) cooldown--
		
		val (x, y, z) = Vector3.fromTileEntityCenter(supertile)
		
		if (storedMana != 0) {
			val lastFood = lastFood[lastFoodIndex].stack
			val lastFoodCount = lastFood.stackSize
			val munchInterval = 2 + 2 * lastFoodCount
			
			if (cooldown == 0) {
				mana = min(maxMana, mana + storedMana)
				storedMana = 0
				val burpPitch = 1 - (lastFoodCount - 1) * 0.05f
				worldObj.playSoundEffect(x, y, z, "random.burp", 1f, burpPitch)
				sync()
			} else if (cooldown % munchInterval == 0) {
				worldObj.playSoundEffect(x, y, z, "random.eat", 0.5f, 1f)
				
				val id = Item.getIdFromItem(lastFood.item)
				val meta = if (lastFood.hasSubtypes) lastFood.getItemDamage() else -1
				
				VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.ICONCRACK, worldObj.provider.dimensionId, x, y, z, id.D, meta.D)
			}
		}
		
		if (cooldown > 0) return
		
		val slowdown = slowdownFactor
		val items = getEntitiesWithinAABB(worldObj, EntityItem::class.java, supertile.boundingBox(RANGE))
		
		for (item in items) {
			val stack = item.entityItem ?: continue
			
			if (stack.item !is ItemFood || stack.stackSize <= 0 || item.isDead || item.age < slowdown) continue
			val hash = stack.copy().hash
			val cached = lastFood[hash]
			
			val lastFoodCount: Int
			
			if (cached != null) {
				lastFoodCount = ++cached.stack.stackSize
				lastFoodIndex = lastFood.indexOf(cached)
			} else {
				lastFoodIndex = lastFood.push(hash)
				lastFoodCount = 1
			}
			
			val heal = (stack.item as ItemFood).func_150905_g(stack)
			storedMana = (heal * heal * 64 * (1 - lastFoodCount / 8f + 0.125)).I
			cooldown = heal * 10
			item.playSound("random.eat", 0.2f, 0.6f)
			sync()
			
			val id = Item.getIdFromItem(stack.item)
			val meta = if (stack.hasSubtypes) stack.getItemDamage() else -1
			
			VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.ICONCRACK, worldObj.provider.dimensionId, x, y, z, id.D, meta.D)
			
			--stack.stackSize
			if (--stack.stackSize <= 0) item.setDead()
			
			break
		}
	}
	
	override fun writeToPacketNBT(cmp: NBTTagCompound) {
		super.writeToPacketNBT(cmp)
		cmp.setInteger(TAG_COOLDOWN, cooldown)
		cmp.setInteger(TAG_DIGESTING_MANA, storedMana)
		cmp.setInteger(TAG_LAST_FOOD_INDEX, lastFoodIndex)
		cmp.setInteger(TAG_LAST_FOOD_SIZE, lastFood.size)
		
		for (i in 0 until lastFood.size) {
			cmp.setTag(TAG_LAST_FOOD + "$i", lastFood[i].stack.writeToNBT(NBTTagCompound()))
		}
	}
	
	override fun readFromPacketNBT(cmp: NBTTagCompound) {
		super.readFromPacketNBT(cmp)
		cooldown = cmp.getInteger(TAG_COOLDOWN)
		storedMana = cmp.getInteger(TAG_DIGESTING_MANA)
		lastFoodIndex = cmp.getInteger(TAG_LAST_FOOD_INDEX)
		
		for (i in 0 until cmp.getInteger(TAG_LAST_FOOD_SIZE)) {
			val stack = ItemStack.loadItemStackFromNBT(cmp.getCompoundTag(TAG_LAST_FOOD + "$i"))
			lastFood.push(HashableItemStack(stack))
		}
	}
	
	override fun getRadius() = Square(toChunkCoordinates(), RANGE)
	override fun getMaxMana() = 9000
	override fun getColor() = 0xD3D604
	override fun getEntry() = LexiconData.gourmaryllis!!
	
	companion object {
		
		private const val TAG_COOLDOWN = "cooldown"
		private const val TAG_DIGESTING_MANA = "digestingMana"
		private const val TAG_LAST_FOOD = "lastFood"
		private const val TAG_LAST_FOOD_INDEX = "lastFoodIndex"
		private const val TAG_LAST_FOOD_SIZE = "lastFoodSize"
		private const val RANGE = 1
	}
}

private val ItemStack.hash get() = HashableItemStack(this, 1)

private class HashableItemStack(val stack: ItemStack, size: Int = stack.stackSize) {
	
	init {
		stack.stackSize = size
	}
	
	override fun hashCode() = "${GameRegistry.findUniqueIdentifierFor(stack.item)}:${stack.meta}:${stack.tagCompound}".hashCode()
	
	override fun equals(other: Any?) = other is HashableItemStack && stack.isItemEqual(other.stack) && ItemStack.areItemStackTagsEqual(stack, other.stack)
}

@Suppress("UNCHECKED_CAST") // :schizoid.jpg:
private class LimitedHashQueueArray<E>(val limit: Int = 8) {
	
	var array = arrayOfNulls<Any?>(limit)
	val set = HashMap<E, E>(limit)
	val indexes = HashMap<E, Int>()
	
	var size: Int = 0
	
	operator fun get(index: Int): E = array[index] as E
	operator fun get(key: E): E? = set[key]
	
	fun indexOf(element: E) = indexes[element] ?: -1
	
	fun push(element: E): Int {
		if (get(element) != null) return indexOf(element)
		
		set[element] = element
		if (size == limit) {
			val lastIndex = limit-1
			
			set.remove(array[0])
			indexes.remove(array[0])
			
			for (i in 0 until lastIndex) {
				array[i] = array[i + 1]
				indexes[array[i] as E] = i
			}
			
			array[limit-1] = element
			indexes[array[limit-1] as E] = limit-1
			
			return lastIndex
		} else {
			array[size] = element
			indexes[element] = size
			return ++size - 1
		}
	}
}