package alfheim.common.entity

import alexsocol.asjlib.*
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler.isProtected
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import ru.vamig.worldengine.WE_Biome
import kotlin.math.*

/**
 * @author Jokiboy (Nature Reborn)
 */
class EntityEarthquakeFracture(world: World): Entity(world) {
	
	var a = 0.0
	var lifetime = 0
	var maxLifetime = 0
	
	init {
		setSize(0f, 0f)
	}
	
	constructor(world: World, x: Int, z: Int, maxLife: Int): this(world) {
		a = rand.nextDouble() * Math.PI * 2
		
		setPosition(x.D, -3.0, z.D)
		
		maxLifetime = maxLife
		
		if (worldObj.isProtected(posX.mfloor(), posY.mfloor(), posZ.mfloor(), false))
			setDead()
		else
			for (y in 256 downTo 12) {
				val block = worldObj.getBlock(x, y, z)
				if (block.isAir(worldObj, x, y, z) || block === Blocks.flowing_water || block === Blocks.water) continue
				
				world.createExplosion(this, x.D, y.D, z.D, 6f, true)
			}
	}
	
	override fun onUpdate() {
		if (!RagnarokHandler.ginnungagap || isDead) return setDead()
		
		if (worldObj.isProtected(posX.mfloor(), posY.mfloor(), posZ.mfloor(), false)) return setDead()
		
		a += (rand.nextDouble() - 0.5) / 12.0

		setPosition(posX + cos(a) * 0.1, -3.0, posZ + sin(a) * 0.1)

		val x = posX.mfloor()
		val z = posZ.mfloor()
		val range = (maxLifetime - lifetime) / 20 + 3

		for (i in -range..range) {
			for (k in -range..range) {
				if (i * i + k * k >= range) continue
				
				val maxY = (worldObj.getBiomeGenForCoords(x, z) as? WE_Biome)?.biomeSurfaceHeight ?: 64
				for (j in maxY + 10 downTo 12) {
					if (worldObj.isAirBlock(i + x, j, k + z)) continue

					if (rand.nextDouble() < 0.3 && !worldObj.isRemote && !worldObj.isProtected(i + x, j, k + z, false, false))
						worldObj.setBlockToAir(i + x, j, k + z)

					val block = worldObj.getBlock(i + x, j, k + z)
					if (block !== Blocks.flowing_water && block !== Blocks.water && block !== Blocks.flowing_lava && block !== Blocks.lava)
						break
				}
			}
		}

		if (maxLifetime < lifetime++) setDead()
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		a = nbt.getDouble("a")
		lifetime = nbt.getInteger("life")
		maxLifetime = nbt.getInteger("maxlife")
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setDouble("a", a)
		nbt.setInteger("life", lifetime)
		nbt.setInteger("maxlife", maxLifetime)
	}
	
	override fun entityInit() = Unit
}