package alfheim.common.item.lens

import alexsocol.asjlib.*
import net.minecraft.entity.*
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.item.ItemStack
import vazkii.botania.api.internal.IManaBurst
import vazkii.botania.api.mana.IManaSpreader
import vazkii.botania.common.block.tile.mana.TileSpreader
import vazkii.botania.common.item.lens.Lens

class LensTripwire: Lens() {
	
	override fun allowBurstShooting(stack: ItemStack?, spreader: IManaSpreader?, redstone: Boolean): Boolean {
		if (spreader !is TileSpreader) return false
		val burst = runBurstSimulation(spreader)
		val e = burst as Entity
		return e.entityData.getBoolean(TAG_TRIPPED)
	}
	
	override fun updateBurst(burst: IManaBurst, entity: EntityThrowable?, stack: ItemStack?) {
		if (!burst.isFake) return
		if (entity!!.worldObj.isRemote) return
		
		val axis = getBoundingBox(entity.posX, entity.posY, entity.posZ, entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).expand(0.25)
		val entities = getEntitiesWithinAABB(entity.worldObj, EntityLivingBase::class.java, axis)
		if (entities.isEmpty()) return
		val e = burst as Entity
		e.entityData.setBoolean(TAG_TRIPPED, true)
	}
	
	fun runBurstSimulation(spreader: TileSpreader): IManaBurst {
		val fakeBurst = spreader.getBurst(true)
		fakeBurst.setScanBeam()
		fakeBurst.getCollidedTile(true)
		return fakeBurst
	}
	
	companion object {
		private const val TAG_TRIPPED = "tripwireLensTripped"
	}
}