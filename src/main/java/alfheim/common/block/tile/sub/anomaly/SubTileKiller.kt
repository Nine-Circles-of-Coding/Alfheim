package alfheim.common.block.tile.sub.anomaly

import alexsocol.asjlib.*
import alfheim.api.block.tile.SubTileAnomalyBase
import net.minecraft.entity.Entity
import net.minecraft.util.DamageSource
import vazkii.botania.common.block.ModBlocks

class SubTileKiller: SubTileAnomalyBase() {
	
	override val targets: List<Any> get() {
		return getEntitiesWithinAABB(worldObj, Entity::class.java, superTile?.boundingBox(0.25) ?: return emptyList())
	}
	
	override fun typeBits() = HEALTH
	
	override fun performEffect(target: Any) {
		if (target is Entity)
			target.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE)
	}
	
	override fun update() {
		if (!worldGen || ticks != 0 || rand.nextBoolean()) return
		
		for (i in -1..1)
			for (j in -1..3)
				for (k in -1..1) {
					if (i == 0 && j in 0..2 && k == 0)
						continue
					
					worldObj.setBlock(x + i, y + j, z + k, ModBlocks.livingrock, if (i == 0 && k == 0) 4 else 1, 3)
				}
	}
}