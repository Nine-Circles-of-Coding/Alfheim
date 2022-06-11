package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.security.InteractionSecurity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.init.Blocks
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World

class EntityThrowableItem: EntityThrowable {
	
	constructor(world: World): super(world)
	
	constructor(player: EntityPlayer): super(player.worldObj, player)
	
	override fun onImpact(movingObject: MovingObjectPosition?) {
		if (!worldObj.isRemote && movingObject != null) {
			val axisalignedbb = boundingBox.expand(8.0, 2.0, 8.0)
			val list1 = worldObj.getEntitiesWithinAABB(EntityLivingBase::class.java, axisalignedbb)
			
			if (list1 != null && list1.isNotEmpty()) {
				val iterator = list1.iterator()
				
				while (iterator.hasNext()) {
					val living = iterator.next() as EntityLivingBase
					
					if (getDistanceSqToEntity(living) < 16.0) {
						if (InteractionSecurity.canHurtEntity(thrower ?: continue, living))
							living.setFire(10)
					}
				}
			}
			
			val v = Vector3.fromEntity(this)
			val (xo, yo, zo) = v.mf()
			
			worldObj.playAuxSFX(2002, xo, yo, zo, 16451) // fire resistance meta
			setDead()
			
			tryToSetFire(xo, yo, zo)
			
			for (n in 0..36) {
				val (x, y, z) = v.rand().mul(6).sub(3).add(this).mf()
				
				tryToSetFire(x, y, z)
			}
		}
	}
	
	private fun tryToSetFire(x: Int, y: Int, z: Int) {
		if (InteractionSecurity.isPlacementBanned(thrower ?: return, x, y, z, worldObj, Blocks.fire)) return
		if (!worldObj.isAirBlock(x, y, z) || !Blocks.fire.canPlaceBlockAt(worldObj, x, y, z)) return
		worldObj.setBlock(x, y, z, Blocks.fire)
	}
	
	public override fun func_70183_g() = -10f
	
	public override fun func_70182_d() = 1f
}
