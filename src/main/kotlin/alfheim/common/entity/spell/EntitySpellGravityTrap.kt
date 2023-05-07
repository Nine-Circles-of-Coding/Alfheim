package alfheim.common.entity.spell

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.spell.*
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.PartySystem
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.spell.tech.SpellGravityTrap
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import java.util.*

class EntitySpellGravityTrap @JvmOverloads constructor(world: World, var caster: EntityLivingBase? = null, x: Double = 0.0, y: Double = 0.0, z: Double = 0.0): Entity(world), ITimeStopSpecific {
	
	override val isImmune = false
	
	init {
		setPosition(x, y, z)
		setSize(SpellGravityTrap.radius.F * 2, 0.01f)
	}
	
	@SideOnly(Side.CLIENT)
	override fun setPositionAndRotation2(x: Double, y: Double, z: Double, yaw: Float, pitch: Float, nope: Int) {
		setPosition(x, y, z)
		setRotation(yaw, pitch)
		// fuck you "push out of blocks"!
	}
	
	override fun onEntityUpdate() {
		if (!AlfheimConfigHandler.enableMMO || ticksExisted > SpellGravityTrap.duration) {
			setDead()
			return
		}
		if (isDead || ticksExisted < 20 || ASJUtilities.isClient || caster == null) return
		
		getEntitiesWithinAABB(worldObj, Entity::class.java, getBoundingBox(posX, posY + 8, posZ, posX, posY + 8, posZ).expand(SpellGravityTrap.radius, 9.0, SpellGravityTrap.radius)).forEach {
			if (it === this || it === caster || it is EntityLivingBase && PartySystem.mobsSameParty(caster, it) && !AlfheimConfigHandler.frienldyFire || it is EntityPlayer && it.capabilities.isCreativeMode) return@forEach
			
			if (Vector3.entityDistancePlane(it, this) <= SpellGravityTrap.radius) {
				it.attackEntityFrom(DamageSourceSpell.gravity(this, caster), SpellBase.over(caster, SpellGravityTrap.damage.D))
				
				if (!InteractionSecurity.canInteractWithEntity(caster ?: return@forEach, it)) return@forEach
				
				val dist = Vector3.fromEntity(it).sub(this)
				
				it.motionY -= 1.0
				it.motionX -= dist.x / 5
				it.motionZ -= dist.z / 5
			}
		}
		
		if (worldObj.rand.nextBoolean()) {
			val p = Vector3().rand().sub(0.5).normalize().mul(Math.random() * 4).add(this)
			val m = Vector3.fromEntity(this).sub(p).mul(0.05)
			VisualEffectHandler.sendPacket(VisualEffects.GRAVITY, dimension, p.x, p.y, p.z, m.x, m.y, m.z)
		}
	}
	
	override fun affectedBy(uuid: UUID) = caster!!.uniqueID != uuid
	
	public override fun entityInit() = Unit
	
	public override fun readEntityFromNBT(nbt: NBTTagCompound) {
		if (nbt.hasKey("castername")) caster = worldObj.getPlayerEntityByName(nbt.getString("castername")) else setDead()
		if (caster == null) setDead()
		ticksExisted = nbt.getInteger("ticksExisted")
	}
	
	public override fun writeEntityToNBT(nbt: NBTTagCompound) {
		if (caster is EntityPlayer) nbt.setString("castername", caster!!.commandSenderName)
		nbt.setInteger("ticksExisted", ticksExisted)
	}
}