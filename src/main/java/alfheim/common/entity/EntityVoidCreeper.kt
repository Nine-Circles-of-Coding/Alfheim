package alfheim.common.entity

import alexsocol.asjlib.*
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import vazkii.botania.common.item.ModItems

/**
 * All the mana is mine mahhhaahahha
 */
class EntityVoidCreeper(world: World): EntityCreeper(world) {
	
	private var lastActiveTime: Int = 0
	private var timeSinceIgnited: Int = 0
	private var range: Int = 3
	private var fuseTime = 30
	
	override fun writeEntityToNBT(tag: NBTTagCompound) {
		super.writeEntityToNBT(tag)
		
		tag.setBoolean("powered", getFlag(6))
		tag.setInteger("Fuse", fuseTime)
		tag.setBoolean("ignited", func_146078_ca())
	}
	
	override fun readEntityFromNBT(tag: NBTTagCompound) {
		super.readEntityFromNBT(tag)
		setFlag(6, tag.getBoolean("powered"))
		if (tag.hasKey("Fuse")) fuseTime = tag.getInteger("Fuse")
		if (tag.getBoolean("ignited")) func_146079_cb()
	}
	
	override fun getDropItem() = Items.gunpowder!!
	
	override fun dropFewItems(par1: Boolean, par2: Int) {
		if (par1 && Math.random() < AlfheimConfigHandler.blackLotusDropRate)
			entityDropItem(ItemStack(ModItems.blackLotus), 1F)
	}
	
	override fun onUpdate() {
		super.onUpdate()
		
		if (!isEntityAlive) return
		lastActiveTime = timeSinceIgnited
		
		if (func_146078_ca()) {
			creeperState = 1
		}
		
		val i = creeperState
		
		if (i > 0 && timeSinceIgnited == 0)
			playSound("creeper.primed", 1f, 0.5f)
		
		timeSinceIgnited += i
		
		if (timeSinceIgnited < 0)
			timeSinceIgnited = 0
		
		if (timeSinceIgnited >= fuseTime) {
			timeSinceIgnited = fuseTime
			creeperGoBoom()
		}
	}
	
	override fun fall(distance: Float) {
		super.fall(distance)
		timeSinceIgnited = (timeSinceIgnited.F + distance * 1.5f).I
		
		if (timeSinceIgnited > fuseTime - 5)
			timeSinceIgnited = fuseTime - 5
	}
	
	private fun creeperGoBoom() {
		if (worldObj.isRemote) return
		val r = range * if (powered) 2 else 1
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(r)).forEach {
			if (it !is EntityPlayer) return@forEach
			it.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDManaVoid, if (powered) 1200 else 120, 0))
		}
		
		worldObj.createExplosion(this, posX, posY, posZ, 1f, false)
		setDead()
	}
}
