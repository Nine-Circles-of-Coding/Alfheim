package alfheim.common.block.tile.sub.anomaly

import alexsocol.asjlib.*
import alfheim.api.block.tile.SubTileAnomalyBase
import alfheim.common.block.tile.TileAnomaly
import net.minecraft.entity.Entity
import net.minecraft.tileentity.TileEntity
import vazkii.botania.common.Botania
import kotlin.math.*

class SubTileSpeedUp: SubTileAnomalyBase() {
	
	override val targets: List<Any>
		get() {
			if (inWG()) return EMPTY_LIST
			
			val l = allAround(Entity::class.java, 8.0)
			l.removeAll { !it.isEntityAlive }
			
			val tiles = ArrayList<Any>(l)
			
			for (i in -radius..radius)
				for (j in -radius..radius)
					for (k in -radius..radius) {
						if (i == 0 && j == 0 && k == 0) continue
						val t = worldObj.getTileEntity(x + i, y + j, z + k)
						if (t != null && t.canUpdate() && !t.isInvalid && t !is TileAnomaly) tiles.add(t)
					}
			
			return tiles + l
		}
	
	override fun update() {
		if (inWG()) return
		
		rand.setSeed((x xor y xor z).toLong())
		val worldTime = (worldObj.totalWorldTime + rand.nextInt(1000)) / 10.0
		val r = 0.75f + Math.random().F * 0.05f
		
		Botania.proxy.wispFX(worldObj, x + 0.5, y.D + 0.5 + cos(worldTime) * r, z.D + 0.5 + sin(worldTime) * r,
							 Math.random().F * 0.25f, 0.75f + Math.random().F * 0.25f, Math.random().F * 0.25f,
							 0.1f + Math.random().F * 0.1f)
		
		Botania.proxy.wispFX(worldObj, x.D + 0.5 + sin(worldTime) * r, y + 0.5, z.D + 0.5 + cos(worldTime) * r,
							 Math.random().F * 0.25f, 0.75f + Math.random().F * 0.25f, Math.random().F * 0.25f,
							 0.1f + Math.random().F * 0.1f)
		
		Botania.proxy.wispFX(worldObj, x.D + 0.5 + cos(worldTime) * r, y.D + 0.5 + sin(worldTime) * r, z + 0.5,
							 Math.random().F * 0.25f, 0.75f + Math.random().F * 0.25f, Math.random().F * 0.25f,
							 0.1f + Math.random().F * 0.1f)
	}
	
	override fun performEffect(target: Any) {
		if (target is Entity) target.onUpdate()
		if (target is TileEntity) target.updateEntity()
	}
	
	override fun typeBits() = TIME
	
	companion object {
		
		const val radius = 8
	}
}
