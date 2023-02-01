package alfheim.common.entity

import alexsocol.asjlib.F
import alexsocol.asjlib.math.Vector3
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.EventResourcesMetas
import net.minecraft.entity.*
import net.minecraft.entity.ai.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.World
import kotlin.math.PI

class EntityRollingMelon(world: World): EntityCreature(world) {
	
	var isLava: Boolean
		get() = getFlag(6)
		set(lava) = setFlag(6, lava)
	
	// in radians
	var rotation: Float
		get() = dataWatcher.getWatchableObjectFloat(13)
		set(rot) = dataWatcher.updateObject(13, rot)
	
	init {
		setSize(0.9f, 0.9f)
		tasks.addTask(1, EntityAIPanic(this, 2.0))
		tasks.addTask(5, EntityAIWander(this, 1.0))
		tasks.addTask(6, EntityAIWatchClosest(this, EntityPlayer::class.java, 6.0f))
		tasks.addTask(6, EntityAIWatchClosest(this, EntityRollingMelon::class.java, 6.0f))
		tasks.addTask(7, EntityAILookIdle(this))
	}
	
	override fun entityInit() {
		super.entityInit()
		dataWatcher.addObject(13, 0f)
	}
	
	override fun applyEntityAttributes() {
		super.applyEntityAttributes()
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 8.0
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).baseValue = 0.25
	}
	
	override fun isAIEnabled() = true
	
	override fun getCommandSenderName() = StatCollector.translateToLocal("entity.${EntityList.getEntityString(this) ?: "generic"}${if (isLava) ".lava" else ""}.name")!!
	
	override fun onLivingUpdate() {
		super.onLivingUpdate()
		
		rotation += Vector3(motionX, 0, motionZ).length().F
		if (rotation > PI * 2) rotation -= PI.F * 2
	}
	
	override fun attackEntityFrom(source: DamageSource?, amount: Float): Boolean {
		if (source?.damageType != DamageSource.outOfWorld.damageType) {
			val attacker = source?.entity as? EntityLivingBase ?: return false
			if (attacker.heldItem?.item !== Items.wooden_sword) return false
			
			if (isLava) {
				attacker.attackEntityFrom(DamageSource.causeMobDamage(this).setFireDamage(), 2f)
				attacker.setFire(rand.nextInt(5) + 5)
			}
		}
		
		return super.attackEntityFrom(source, amount)
	}
	
	override fun getDropItem() = if (isLava) AlfheimItems.eventResource else Items.melon!!
	
	override fun dropFewItems(gotHit: Boolean, looting: Int) {
		val item = dropItem
		val size = 1 + if (item !== AlfheimItems.eventResource) looting else 0
		val meta = if (item !== AlfheimItems.eventResource) 0 else EventResourcesMetas.LavaMelon
		entityDropItem(ItemStack(item, size, meta), 0f)
	}
	
	override fun onSpawnWithEgg(data: IEntityLivingData?): IEntityLivingData? {
		isLava = rand.nextInt(5) == 0
		return super.onSpawnWithEgg(data)
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		super.writeEntityToNBT(nbt)
		nbt.setBoolean(TAG_LAVA, isLava)
		nbt.setFloat(TAG_ROTATION, rotation)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		super.readEntityFromNBT(nbt)
		isLava = nbt.getBoolean(TAG_LAVA)
		rotation = nbt.getFloat(TAG_ROTATION)
	}
	
	companion object {
		
		const val TAG_LAVA = "isLava"
		const val TAG_ROTATION = "rotation"
	}
}
