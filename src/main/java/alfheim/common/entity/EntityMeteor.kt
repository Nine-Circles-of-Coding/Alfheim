package alfheim.common.entity

import alexsocol.asjlib.*
import alfheim.client.render.particle.EntityFXSmoke
import alfheim.common.block.*
import alfheim.common.core.handler.ragnarok.RagnarokHandler.isProtected
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityFallingBlock
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import kotlin.math.*

class EntityMeteor(world: World, x: Int, z: Int): Entity(world) {
	
	var scale = 0
	var initVel = 0.0
	
	init {
		scale = world.rand.nextInt(16) + 4
		val d = 16.0
		val a = rand.nextDouble() * Math.PI * 2.0
		val y = world.getPrecipitationHeight(x, z)
		val xx = x - cos(a) * d
		val zz = z + sin(a) * d
		val yy = y + d
		val speed = rand.nextDouble() + 1
		setPosition(xx, yy, zz)
		setMotion(-cos(a + Math.PI) * speed, -speed, sin(a + Math.PI) * speed)
		initVel = abs(motionX * motionY * motionZ) / 3.0
	}
	
	constructor(world: World): this(world, 0, 0)
	
	override fun onUpdate() {
		if (worldObj.isProtected(posX.mfloor(), posY.mfloor(), posZ.mfloor(), false)) return setDead()
		
		prevPosX = posX
		prevPosY = posY
		prevPosZ = posZ
		
		moveEntity(motionX, motionY, motionZ)
		
		if (worldObj.isRemote)
			for (i in 0 until 11 + worldObj.rand.nextInt(13)) {
				if (worldObj.rand.nextDouble() < 0.3) {
					val x = posX + rand.nextDouble() * scale * 2.0 - scale
					val y = posY + rand.nextDouble() * scale * 2.0 - scale
					val z = posZ + rand.nextDouble() * scale * 2.0 - scale
					val e = EntityFXSmoke(worldObj, x, y, z, 0.0, 0.0, 0.0, 5.0f + worldObj.rand.nextFloat() * 10.0f, 60.0f)
					var r = (1.0 - posY / 256.0).F * 0.3f
					if (r > 0.4) r = 0.4f
					if (rand.nextDouble() < 0.25) {
						e.setRBGColorF(0.3f, 0.3f - r * rand.nextFloat(), 0.3f - r)
					} else {
						r = rand.nextFloat() * 0.4f
						e.setRBGColorF(0.3f + r, 0.3f + r, 0.3f + r)
					}
					e.setMaxAge(100 + rand.nextInt(600))
					mc.effectRenderer.addEffect(e)
				}
			}
		
		if (!worldObj.isRemote && rand.nextDouble() * 256 - 64 < posY && rand.nextDouble() < 0.03) {
			var id = AlfheimFluffBlocks.livingrockDark
			
			if (rand.nextDouble() < 0.1) {
				id = Blocks.obsidian
			} else if (rand.nextDouble() < 0.25) {
				id = Blocks.flowing_lava
			}
			
			val shard = EntityFallingBlock(worldObj, posX, posY, posZ, id, 0)
			shard.field_145812_b = -1200 // fallTime
			shard.setMotion(motionX + (rand.nextDouble() - 0.5) * 3.0, motionY + (rand.nextDouble() - 0.5) * 3.0, motionZ + (rand.nextDouble() - 0.5) * 3.0)
			shard.spawn()
		}
		
		var numObsidian = 0
		
		for (x in -scale..scale) {
			for (y in -scale..scale) {
				for (z in -scale..scale) {
					if (worldObj.getBlock(this, x, y, z) === Blocks.obsidian) numObsidian++
				}
			}
		}
		
		if (numObsidian > scale) {
			var k = 0
			while (k < abs(motionX * motionY * motionZ) / 3.0) {
				worldObj.createExplosion(this, posX - k * motionX, posY - k * motionY, posZ - k * motionZ, (scale * 7).F, true)
				k++
			}
			
			for (x in -scale / 16..scale / 16) {
				for (y in -scale / 16..scale / 16) {
					for (z in -scale / 16..scale / 16) {
						val d = sqrt((x * x + y * y + z * z).D)
						
						if (d < scale) {
							val id = Blocks.diamond_block
							worldObj.setBlock(this, id, x, y - scale, z)
						}
					}
				}
			}
			setDead()
			return
		}
		
		val blockAt = worldObj.getBlock(this, y = -1)
		if (blockAt === Blocks.air) return
		
		if (!blockAt.material.isLiquid) {
			var j = 0
			while (j < abs(motionX * motionY * motionZ) / 3.0) {
				worldObj.createExplosion(this, posX - j * motionX, posY - j * motionY, posZ - j * motionZ, (scale * 4 * (abs(motionX * motionY * motionZ) / 3.0 + 1.0)).F, true)
				j++
			}
			motionX *= 0.99 - (if (posY < 64.0) 1.0 - posY / 64.0 else 0.0) / 2.0
			motionY *= 0.99 - (if (posY < 64.0) 1.0 - posY / 64.0 else 0.0) / 2.0
			motionZ *= 0.99 - (if (posY < 64.0) 1.0 - posY / 64.0 else 0.0) / 2.0
		} else {
			motionX *= 0.98
			motionY *= 0.98
			motionZ *= 0.98
		}
		
		if (abs(motionX * motionY * motionZ) / 3 >= 0.1) return
		
		if (!worldObj.isRemote)
			for (j in -scale..scale) {
				for (y in -scale..scale) {
					for (z in -scale..scale) {
						val d = sqrt((j * j + y * y + z * z).D)
						
						if (d >= scale) continue
						var id = AlfheimFluffBlocks.livingrockDark
						
						if (rand.nextDouble() < 0.1) {
							id = Blocks.obsidian
						} else if (rand.nextDouble() < 0.25) {
							id = Blocks.flowing_lava
						}
						
						worldObj.setBlock(posX.toInt() + j, posY.toInt() + y, posZ.toInt() + z, id)
					}
				}
			}
		
		setDead()
	}
	
	override fun entityInit() = Unit
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		scale = nbt.getInteger("scale")
		initVel = nbt.getDouble("initVel")
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setInteger("scale", scale)
		nbt.setDouble("initVel", initVel)
	}
}
