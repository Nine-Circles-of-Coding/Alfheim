package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.entity.boss.EntityFlugel
import alfheim.common.entity.boss.ai.flugel.AIEnergy
import alfheim.common.entity.spell.EntitySpellIsaacMissile
import alfheim.common.spell.sound.SpellIsaacStorm
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.*
import net.minecraft.util.MovingObjectPosition.MovingObjectType
import net.minecraft.world.World
import vazkii.botania.common.Botania
import vazkii.botania.common.entity.EntityThrowableCopy
import kotlin.math.abs

class EntityCharge(world: World, val flugel: EntityFlugel?, val target: EntityLivingBase?, ai: AIEnergy?): EntityThrowableCopy(world) {
	
	constructor(world: World): this(world, null, null, null)
	
	init {
		setSize(0f, 0f)
		
		if (flugel != null && ai != null) {
			val look = Vector3(flugel.lookVec).mul(1.5).rotateOY(-45f + ai.left * (90f / ai.max))
			val (x, y, z) = Vector3.fromEntityCenter(flugel).add(look)
			setPosition(x, y, z)
			noClip = true
			
			val (mX, mY, mZ) = if (target != null) Vector3.fromEntityCenter(target).sub(x, y, z).normalize() else look
			setThrowableHeading(mX, mY, mZ, 2f, 0f)
		}
	}
	
	override fun onEntityUpdate() {
		super.onEntityUpdate()
		
		for (i in 1..6)
			Botania.proxy.wispFX(worldObj, posX, posY, posZ, 0.2f, 0f, 0.8f, i / 10f)
		
		EntitySpellIsaacMissile.chaseTarget(this, target ?: return) { attackTarget(target) }
	}
	
	public override fun onImpact(pos: MovingObjectPosition?) {
		attackTarget(pos?.entityHit as? EntityLivingBase ?: return)
	}
	
	fun attackTarget(target: EntityLivingBase) {
		if (flugel != null)
			target.attackEntityFrom(DamageSourceSpell.shadow(flugel), if (flugel.isUltraMode) 10f else 5f)
		else
			target.attackEntityFrom(DamageSource.magic, 5f)
		
		setDead()
	}
}