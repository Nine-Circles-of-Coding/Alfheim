package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.common.core.handler.*
import alfheim.common.potion.PotionEternity
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

class EntityGleipnir: Entity {
	
	var thrower: EntityPlayer? = null
	
	constructor(world: World): super(world) {
		setSize(10f, 10f)
	}
	
	constructor(world: World, player: EntityPlayer): this(world) {
		thrower = player
	}
	
	@SideOnly(Side.CLIENT)
	override fun setPositionAndRotation2(x: Double, y: Double, z: Double, yaw: Float, pitch: Float, nope: Int) {
		setPosition(x, y, z)
		setRotation(yaw, pitch)
		// fuck you "push out of blocks"!
	}
	
	override fun onUpdate() {
		super.onUpdate()
		val thrower = thrower
		
		if ((!worldObj.isRemote && (thrower == null || !thrower.isEntityAlive)) || ticksExisted > 300) {
			setDead()
			return
		}
		
		thrower ?: return
		if (worldObj.isRemote) return
		
		val targets = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox)
		targets.remove(thrower)
		targets.removeAll { !InteractionSecurity.canInteractWithEntity(thrower, it) }
		
		if (AlfheimConfigHandler.enableMMO) {
			val pt = CardinalSystem.PartySystem.getParty(thrower)
			targets.removeAll { pt.isMember(it) }
		}
		
		targets.forEach { it.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDEternity, 5, PotionEternity.STUN or PotionEternity.IRREMOVABLE)) }
	}
	
	override fun entityInit() = Unit
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setInteger(TAG_TICKS, ticksExisted)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		ticksExisted = nbt.getInteger(TAG_TICKS)
	}
	
	companion object {
		const val TAG_TICKS = "ticks"
	}
}