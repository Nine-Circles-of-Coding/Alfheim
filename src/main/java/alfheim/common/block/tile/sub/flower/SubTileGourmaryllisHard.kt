package alfheim.common.block.tile.sub.flower

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.*
import net.minecraft.nbt.NBTTagCompound
import vazkii.botania.api.subtile.RadiusDescriptor.Square
import vazkii.botania.api.subtile.SubTileGenerating
import vazkii.botania.common.lexicon.LexiconData
import kotlin.math.min

class SubTileGourmaryllisHard: SubTileGenerating() {
	
	private var cooldown = 0
	private var storedMana = 0
	private var lastFood: ItemStack? = null
	private var lastFoodCount = 0
	
	val worldObj get() = supertile.worldObj!!
	
	override fun onUpdate() {
		super.onUpdate()
		
		if (worldObj.isRemote) return
		
		if (cooldown > -1) cooldown--
		
		val (x, y, z) = Vector3.fromTileEntityCenter(supertile)
		
		if (storedMana != 0) {
			val munchInterval = 2 + 2 * lastFoodCount
			
			if (cooldown == 0) {
				mana = min(maxMana, mana + storedMana)
				storedMana = 0
				val burpPitch = 1 - (lastFoodCount - 1) * 0.05f
				worldObj.playSoundEffect(x, y, z, "random.burp", 1f, burpPitch)
				sync()
			} else if (cooldown % munchInterval == 0) {
				worldObj.playSoundEffect(x, y, z, "random.eat", 0.5f, 1f)
				
				lastFood?.let {
					val id = Item.getIdFromItem(it.item)
					val meta = if (it.hasSubtypes) it.getItemDamage() else -1
					
					VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.ICONCRACK, worldObj.provider.dimensionId, x, y, z, id.D, meta.D)
				}
			}
		}
		
		if (cooldown > 0) return
		
		val slowdown = slowdownFactor
		val items = getEntitiesWithinAABB(worldObj, EntityItem::class.java, supertile.boundingBox(RANGE))
		
		for (item in items) {
			val stack = item.entityItem ?: continue
			
			if (stack.item !is ItemFood || stack.stackSize <= 0 || item.isDead || item.age < slowdown) continue
			if (stack.isItemEqual(lastFood) && ItemStack.areItemStackTagsEqual(stack, lastFood)) {
				lastFoodCount++
			} else {
				lastFood = stack.copy()
				lastFood!!.stackSize = 1
				lastFoodCount = 1
			}
			val heal = (stack.item as ItemFood).func_150905_g(stack)
			storedMana = heal * heal * 64
			storedMana *= (1f / lastFoodCount).I
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
		cmp.setInteger(TAG_LAST_FOOD_COUNT, lastFoodCount)
		
		if (lastFood != null)
			cmp.setTag(TAG_LAST_FOOD, lastFood!!.writeToNBT(NBTTagCompound()))
	}
	
	override fun readFromPacketNBT(cmp: NBTTagCompound) {
		super.readFromPacketNBT(cmp)
		cooldown = cmp.getInteger(TAG_COOLDOWN)
		storedMana = cmp.getInteger(TAG_DIGESTING_MANA)
		lastFoodCount = cmp.getInteger(TAG_LAST_FOOD_COUNT)
		
		if (cmp.hasKey(TAG_LAST_FOOD))
			lastFood = ItemStack.loadItemStackFromNBT(cmp.getCompoundTag(TAG_LAST_FOOD))
	}
	
	override fun getRadius() = Square(toChunkCoordinates(), RANGE)
	override fun getMaxMana() = 9000
	override fun getColor() = 0xD3D604
	override fun getEntry() = LexiconData.gourmaryllis!!
	
	companion object {
		
		private const val TAG_COOLDOWN = "cooldown"
		private const val TAG_DIGESTING_MANA = "digestingMana"
		private const val TAG_LAST_FOOD = "lastFood"
		private const val TAG_LAST_FOOD_COUNT = "lastFoodCount"
		private const val RANGE = 1
	}
}