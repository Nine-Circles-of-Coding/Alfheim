package alfheim.common.entity.spell

import alexsocol.asjlib.*
import alexsocol.asjlib.math.*
import alfheim.api.spell.*
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.PartySystem
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.spell.fire.SpellFirewall
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.DamageSource
import net.minecraft.world.World
import vazkii.botania.common.Botania
import java.util.*

class EntitySpellFirewall(world: World): Entity(world), ITimeStopSpecific {
	
	var caster: EntityLivingBase? = null
	var obb: OrientedBB? = null
	
	override val isImmune = false
	
	init {
		setSize(0f, 0f)
	}
	
	constructor(world: World, caster: EntityLivingBase): this(world) {
		this.caster = caster
		val v = Vector3(caster.lookVec).mul(1, 0, 1).normalize().mul(5)
		setLocationAndAngles(caster.posX + v.x, caster.posY, caster.posZ + v.z, caster.rotationYaw, caster.rotationPitch)
	}
	
	constructor(world: World, x: Double, y: Double, z: Double): this(world) {
		setLocationAndAngles(x, y, z, rotationYaw, rotationPitch)
	}
	
	override fun attackEntityFrom(source: DamageSource?, damage: Float) = false
	
	override fun onUpdate() {
		if (!AlfheimConfigHandler.enableMMO || !worldObj.isRemote && caster != null && caster!!.isDead) {
			return setDead()
		}
		
		super.onUpdate()
		
		if (ticksExisted >= SpellFirewall.duration) {
			setDead()
			return
		}
		
		if (obb == null) {
			obb = OrientedBB(getBoundingBox(-3, -1, -0.5, 3, 4, 0.5)).translate(posX, posY, posZ).rotateOY(rotationYaw.D)
		}
		
		val obb = obb!!
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, obb.toAABB()).forEach {
			if (it === caster || !obb.intersectsWith(it.boundingBox())) return@forEach
			if (!it.attackEntityFrom(DamageSourceSpell.firewall(this, caster), SpellBase.over(caster, SpellFirewall.damage.D))) return@forEach
			if (!PartySystem.mobsSameParty(caster, it) || AlfheimConfigHandler.frienldyFire) it.setFire(3)
		}
		
		// no ideas why twice to Rad (here and in #rotateOY), but it works -_-
		val a = obb.a.copy().sub(obb.pos).rotateOY(Math.toRadians(-rotationYaw.D)).add(obb.pos)
		val b = obb.b.copy().sub(obb.pos).rotateOY(Math.toRadians(-rotationYaw.D)).add(obb.pos)
		val d = obb.d.copy().sub(obb.pos).rotateOY(Math.toRadians(-rotationYaw.D)).add(obb.pos)
		val v = d.copy().sub(d.copy().sub(a).mul(0.5))

		val sources = 20
		val power = 5
		for (i in 0 until sources) {
			v.sub(a.copy().sub(b.copy()).mul(1.0 / sources))
			for (j in 0 until power) Botania.proxy.wispFX(worldObj, v.x + Math.random() * 0.5, v.y + Math.random() * 2, v.z + Math.random() * 0.5, 1f, Math.random().F * 0.25f, Math.random().F * 0.075f, 0.5f + Math.random().F * 0.5f, -0.15f, Math.random().F * 1.5f)
		}
	}
	
	@SideOnly(Side.CLIENT)
	override fun getShadowSize() = 0f
	override fun affectedBy(uuid: UUID) = caster?.uniqueID != uuid
	public override fun entityInit() {}
	public override fun readEntityFromNBT(nbt: NBTTagCompound) = Unit
	public override fun writeEntityToNBT(nbt: NBTTagCompound) = Unit
}