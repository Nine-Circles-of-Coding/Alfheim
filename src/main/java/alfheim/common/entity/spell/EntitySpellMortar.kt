package alfheim.common.entity.spell

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.spell.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.CardinalSystem.PartySystem
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.spell.earth.SpellMortar
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.World
import vazkii.botania.common.block.ModBlocks
import java.util.*
import kotlin.math.*

class EntitySpellMortar(world: World): Entity(world), ITimeStopSpecific {
	
	var caster: EntityLivingBase? = null
	
	override val isImmune = false
	
	init {
		setSize(1f, 1f)
	}
	
	constructor(world: World, shooter: EntityLivingBase): this(world) {
		caster = shooter
		setPositionAndRotation(shooter.posX, shooter.posY + shooter.height * 0.75, shooter.posZ, shooter.rotationYaw, shooter.rotationPitch)
		val m = Vector3(shooter.lookVec).mul(SpellMortar.efficiency)
		motionX = m.x
		motionY = m.y
		motionZ = m.z
	}
	
	fun onImpact(mop: MovingObjectPosition?) {
		if (!worldObj.isRemote) {
			if (mop?.entityHit?.attackEntityFrom(DamageSourceSpell.mortar(this, caster), SpellBase.over(caster, SpellMortar.damage.D)) == true)
				if (mop.entityHit is EntityPlayer)
					(mop.entityHit as EntityPlayer).inventory.damageArmor(MathHelper.ceiling_float_int(SpellBase.over(caster, SpellMortar.damage * 2.5)).F)
			
			val l = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(SpellMortar.radius))
			l.forEach {
				it.attackEntityFrom(DamageSourceSpell.mortar(this, caster), SpellBase.over(caster, SpellMortar.damage * 0.625))
			}
			setDead()
		}
	}
	
	override fun onUpdate() {
		super.onUpdate()
		
		if (!AlfheimConfigHandler.enableMMO || !worldObj.isRemote && (caster != null && caster!!.isDead || !worldObj.blockExists(posX.I, posY.I, posZ.I))) {
			return setDead()
		}
		
		if (ASJUtilities.isClient) {
			worldObj.spawnParticle("blockcrack_${ModBlocks.livingrock.id}_0", posX + Math.random() - 0.5, posY, posZ + Math.random() - 0.5, motionX / -10, -0.05, motionX / -10)
			return
		}
		
		if (ticksExisted == SpellMortar.duration) onImpact(null)
		
		val vec3 = Vec3.createVectorHelper(posX, posY, posZ)
		val vec31 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ)
		var movingobjectposition: MovingObjectPosition? = worldObj.rayTraceBlocks(vec3, vec31)
		
		if (movingobjectposition == null) {
			val l = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox.addCoord(motionX, motionY, motionZ))
			l.remove(caster)
			
			for (e in l)
				if (e.canBeCollidedWith() && !PartySystem.mobsSameParty(caster, e)) {
					movingobjectposition = MovingObjectPosition(e)
					break
				}
		}
		
		if (movingobjectposition != null) onImpact(movingobjectposition)
		
		prevPosX = posX
		prevPosY = posY
		prevPosZ = posZ
		lastTickPosX = posX
		lastTickPosY = posY
		lastTickPosZ = posZ
		
		motionY -= 0.00981
		moveEntity(motionX, motionY, motionZ)
		
		val f1 = sqrt(motionX * motionX + motionZ * motionZ)
		rotationYaw = (atan2(motionZ, motionX) * 180.0 / Math.PI).F + 90f
		
		rotationPitch = (atan2(f1.D, motionY) * 180.0 / Math.PI).F - 90f
		while (rotationPitch - prevRotationPitch < -180f) prevRotationPitch -= 360f
		while (rotationPitch - prevRotationPitch >= 180f) prevRotationPitch += 360f
		while (rotationYaw - prevRotationYaw < -180f) prevRotationYaw -= 360f
		while (rotationYaw - prevRotationYaw >= 180f) prevRotationYaw += 360f
		
		rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2f
		rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2f
	}
	
	override fun canBeCollidedWith(): Boolean {
		return true
	}
	
	override fun getCollisionBorderSize(): Float {
		return 5f
	}
	
	@SideOnly(Side.CLIENT)
	override fun getShadowSize(): Float {
		return 0f
	}
	
	override fun affectedBy(uuid: UUID): Boolean {
		return caster?.uniqueID != uuid
	}
	
	public override fun entityInit() = Unit
	
	public override fun readEntityFromNBT(nbt: NBTTagCompound) {
		if (nbt.hasKey("castername")) caster = worldObj.getPlayerEntityByName(nbt.getString("castername")) else setDead()
		if (caster == null) setDead()
	}
	
	public override fun writeEntityToNBT(nbt: NBTTagCompound) {
		if (caster is EntityPlayer) nbt.setString("castername", caster!!.commandSenderName)
	}
}