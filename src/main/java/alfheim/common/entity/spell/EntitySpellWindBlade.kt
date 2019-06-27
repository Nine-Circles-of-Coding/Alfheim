package alfheim.common.entity.spell

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.math.Vector3
import alfheim.AlfheimCore
import alfheim.api.spell.*
import alfheim.common.core.handler.CardinalSystem.PartySystem
import alfheim.common.core.util.DamageSourceSpell
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import vazkii.botania.common.Botania
import java.util.*

class EntitySpellWindBlade(world: World): Entity(world), ITimeStopSpecific {
	
	private var caster: EntityLivingBase? = null
	
	override val isImmune: Boolean
		get() = false
	
	init {
		setSize(3f, 0.1f)
	}
	
	constructor(world: World, caster: EntityLivingBase): this(world) {
		this.caster = caster
		setPositionAndRotation(caster.posX, caster.posY + caster.height * 0.75, caster.posZ, caster.rotationYaw, caster.rotationPitch)
	}
	
	override fun onEntityUpdate() {
		if (!AlfheimCore.enableMMO || !worldObj.isRemote && (caster == null || caster!!.isDead || ticksExisted > 20)) {
			setDead()
			return
		}
		
		if (!ASJUtilities.isServer) {
			Botania.proxy.wispFX(worldObj, posX + Math.random() * 2 - 1, posY, posZ + Math.random() * 2 - 1, Math.random().toFloat() * 0.1f + 0.8f, Math.random().toFloat() * 0.1f + 0.9f, Math.random().toFloat() * 0.1f + 0.8f, Math.random().toFloat() * 0.3f + 0.2f, motionX.toFloat() / -10f, motionY.toFloat() / -10f, motionZ.toFloat() / -10f, 0.5f)
			return
		}
		
		if (isCollidedHorizontally) setDead()
		
		if (this.isDead) return
		
		val m = Vector3(ASJUtilities.getLookVec(this))
		moveEntity(m.x, 0.0, m.z)
		
		val l = worldObj.getEntitiesWithinAABB(EntityLivingBase::class.java, boundingBox) as MutableList<EntityLivingBase>
		l.remove(caster)
		for (e in l) if (!PartySystem.mobsSameParty(caster, e)) e.attackEntityFrom(DamageSourceSpell.blades(this, caster), SpellBase.over(caster, 6.0))
	}
	
	override fun affectedBy(uuid: UUID): Boolean {
		return caster!!.uniqueID != uuid
	}
	
	public override fun entityInit() {}
	
	public override fun readEntityFromNBT(nbt: NBTTagCompound) {
		if (nbt.hasKey("castername")) caster = worldObj.getPlayerEntityByName(nbt.getString("castername")) else setDead()
		if (caster == null) setDead()
	}
	
	public override fun writeEntityToNBT(nbt: NBTTagCompound) {
		if (caster is EntityPlayer) nbt.setString("castername", caster!!.commandSenderName)
	}
}