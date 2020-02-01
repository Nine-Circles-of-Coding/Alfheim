package alfheim.common.entity.spell

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.math.Vector3
import alfheim.AlfheimCore
import alfheim.api.spell.*
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.core.util.*
import alfheim.common.spell.water.SpellAquaStream
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.MovingObjectPosition.MovingObjectType
import net.minecraft.world.World
import java.util.*

class EntitySpellAquaStream(world: World): Entity(world), ITimeStopSpecific {
	
	var caster: EntityLivingBase? = null
	
	override val isImmune: Boolean
		get() = false
	
	init {
		setSize(1f, 1f)
	}
	
	constructor(world: World, caster: EntityLivingBase): this(world) {
		this.caster = caster
		setPosition(caster.posX, caster.posY, caster.posZ)
	}
	
	override fun onEntityUpdate() {
		if (!AlfheimCore.enableMMO || caster == null || caster!!.isDead || caster!!.posX != posX || caster!!.posY != posY || caster!!.posZ != posZ || ticksExisted > SpellAquaStream.duration) {
			setDead()
			return
		}
		if (isDead || !ASJUtilities.isServer) return
		
		var mop = ASJUtilities.getMouseOver(caster, SpellAquaStream.radius, true)
		if (mop == null) mop = ASJUtilities.getSelectedBlock(caster!!, SpellAquaStream.radius, true)
		
		val hp: Vector3
		val look = Vector3(caster!!.lookVec)
		if (mop?.hitVec != null) {
			hp = Vector3(mop.hitVec)
			if (mop.typeOfHit == MovingObjectType.ENTITY) {
				mop.entityHit.attackEntityFrom(DamageSourceSpell.water(caster!!), SpellBase.over(caster, SpellAquaStream.damage.D))
			}
		} else {
			hp = look.copy().extend(SpellAquaStream.radius).add(Vector3.fromEntity(caster!!)).add(0.0, caster!!.eyeHeight.D, 0.0)
		}
		
		val d = 0.75
		VisualEffectHandler.sendPacket(VisualEffects.AQUASTREAM, dimension, look.x + caster!!.posX, look.y + caster!!.posY + caster!!.eyeHeight.D, look.z + caster!!.posZ, look.x / d, look.y / d, look.z / d)
		VisualEffectHandler.sendPacket(VisualEffects.AQUASTREAM_HIT, dimension, hp.x, hp.y, hp.z)
	}
	
	override fun affectedBy(uuid: UUID) = caster!!.uniqueID != uuid
	
	public override fun entityInit() = Unit
	
	public override fun readEntityFromNBT(nbt: NBTTagCompound) {
		if (nbt.hasKey("castername")) caster = worldObj.getPlayerEntityByName(nbt.getString("castername")) else setDead()
		if (caster == null) setDead()
	}
	
	public override fun writeEntityToNBT(nbt: NBTTagCompound) {
		if (caster is EntityPlayer) nbt.setString("castername", caster!!.commandSenderName)
	}
}