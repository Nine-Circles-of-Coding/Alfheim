package alfheim.common.entity

import alexsocol.asjlib.*
import alfheim.common.core.util.DamageSourceSpell
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.World

class EntityBlock(world: World): Entity(world) {
	
	var block: Block
		get() = GameRegistry.findBlock(dataWatcher.getWatchableObjectString(2), dataWatcher.getWatchableObjectString(3)) ?: Blocks.stone
		set(value) {
			val ui = GameRegistry.findUniqueIdentifierFor(value) ?: GameRegistry.UniqueIdentifier("minecraft:stone")
			dataWatcher.updateObject(2, ui.modId)
			dataWatcher.updateObject(3, ui.name)
		}
	
	var meta: Int
		get() = dataWatcher.getWatchableObjectInt(4)
		set(value) = dataWatcher.updateObject(4, value)
	
	init {
		setSize(1f, 1f)
	}
	
	override fun entityInit() {
		dataWatcher.addObject(2, "minecraft")
		dataWatcher.addObject(3, "stone")
		dataWatcher.addObject(4, 0)
	}
	
	override fun onEntityUpdate() {
		if (block === Blocks.air) return setDead()
		
		prevPosX = posX
		prevPosY = posY
		prevPosZ = posZ
		rotationYaw = 0f
		rotationPitch = 0f
	}
	
	override fun attackEntityFrom(src: DamageSource, damage: Float): Boolean {
		if (worldObj.isRemote || src is EntityDamageSourceIndirect || src is DamageSourceSpell) return false
		
		val player = src.entity as? EntityPlayer ?: return false
		val stack = player.heldItem ?: return false
		if (stack.item.func_150893_a(stack, block) <= 1f) return false
		
		setDead()
		entityDropItem(ItemStack(block, 1, meta), 0f)
		stack.damageItem(1, player)
		block = Blocks.air
		
		return true
	}
	
	@SideOnly(Side.CLIENT)
	override fun setPositionAndRotation2(x: Double, y: Double, z: Double, yaw: Float, pitch: Float, nope: Int) {
		setPosition(x, y, z)
		setRotation(yaw, pitch)
		// fuck you "push out of blocks"!
	}
	
	override fun applyEntityCollision(against: Entity?) = Unit
	override fun canBeCollidedWith() = true
	override fun canBePushed() = false
	override fun getBoundingBox() = boundingBox
	override fun getCollisionBox(against: Entity?) = against?.boundingBox
	override fun getCollisionBorderSize() = 0.1f
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		block = GameRegistry.findBlock(nbt.getString(TAG_BLOCK_MODID), nbt.getString(TAG_BLOCK_NAME)) ?: Blocks.stone
		meta = nbt.getInteger(TAG_META)
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		val ui = GameRegistry.findUniqueIdentifierFor(block) ?: return
		nbt.setString(TAG_BLOCK_MODID, ui.modId)
		nbt.setString(TAG_BLOCK_NAME, ui.name)
		nbt.setInteger(TAG_META, meta)
	}
	
	companion object {
		const val TAG_BLOCK_MODID = "blockModid"
		const val TAG_BLOCK_NAME = "blockName"
		const val TAG_META = "meta"
	}
}
