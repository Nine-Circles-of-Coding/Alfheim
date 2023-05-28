package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import alexsocol.asjlib.math.Vector3
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler.isProtected
import alfheim.common.item.relic.ItemTankMask
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.*
import net.minecraft.world.World
import kotlin.math.sqrt

// Hungry node copy -_-
class TileRift: ASJTile() {
	
	var ticks = 0
	
	override fun updateEntity() {
		if (ASJUtilities.isClient) return
		
		if (!RagnarokHandler.ginnungagap || worldObj.isProtected(xCoord, yCoord, zCoord, true)) {
			worldObj.setBlockToAir(xCoord, yCoord, zCoord)
			return
		}
		
		val p = Vector3().rand().sub(0.5).normalize().mul(Math.random() * 4).add(this)
		val m = Vector3.fromTileEntityCenter(this).sub(p).mul(0.05)
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.GRAVITY, worldObj.provider.dimensionId, p.x, p.y, p.z, m.x, m.y, m.z)
		
		// suck blocks
		if (!worldObj.isRemote && ticks++ % 10 == 0) {
			var tx = xCoord + ASJUtilities.randInBounds(-16, 16, worldObj.rand)
			var ty = yCoord + ASJUtilities.randInBounds(-16, 16, worldObj.rand)
			var tz = zCoord + ASJUtilities.randInBounds(-16, 16, worldObj.rand)
			
			val heightValue = worldObj.getHeightValue(tx, tz)
			if (ty > heightValue) ty = heightValue
			
			val start = Vec3.createVectorHelper(xCoord.D + 0.5, yCoord.D + 0.5, zCoord.D + 0.5)
			val end = Vec3.createVectorHelper(tx.D + 0.5, ty.D + 0.5, tz.D + 0.5)
			val mop = rayTraceIgnoringSource(worldObj, start, end)
			
			if (mop != null && getDistanceFrom(mop.blockX.D, mop.blockY.D, mop.blockZ.D) < (16 * 16)) {
				tx = mop.blockX
				ty = mop.blockY
				tz = mop.blockZ
				
				val block = worldObj.getBlock(tx, ty, tz)
				if (!block.isAir(worldObj, tx, ty, tz)) {
					val h = block.getBlockHardness(worldObj, tx, ty, tz)
					if (h in 0f..5f) worldObj.func_147480_a(tx, ty, tz, true)
				}
			}
		}
		
		// suck entities
		val list = getEntitiesWithinAABB(worldObj, Entity::class.java, boundingBox(15))
		if (list.size <= 0) return
		
		for (e in list) {
			if (e is EntityPlayer && e.capabilities.disableDamage) continue
			
			if (e.isEntityAlive && !e.isEntityInvulnerable && Vector3.entityTileDistance(e, this) < 2.0)
				if (e.attackEntityFrom(DamageSource.outOfWorld, 1.0f) && e is EntityPlayer && ASJUtilities.chance(1))
					ItemTankMask.sendToHelheim(e)
			
			val dx = (xCoord + 0.5 - e.posX) / 15
			val dy = (yCoord + 0.5 - e.posY) / 15
			val dz = (zCoord + 0.5 - e.posZ) / 15
			val dist = sqrt(dx * dx + dy * dy + dz * dz)
			var undist = 1.0 - dist
			
			if (undist > 0.0) {
				undist *= undist
				e.motionX += dx / dist * undist * 0.15
				e.motionY += dy / dist * undist * 0.25
				e.motionZ += dz / dist * undist * 0.15
			}
		}
	}
	
	fun rayTraceIgnoringSource(world: World, v1: Vec3, v2: Vec3): MovingObjectPosition? {
		if (v1.xCoord.isNaN() || v1.yCoord.isNaN() || v1.zCoord.isNaN() || v2.xCoord.isNaN() || v2.yCoord.isNaN() || v2.zCoord.isNaN())
			return null
		
		val i = v2.xCoord.mfloor()
		val j = v2.yCoord.mfloor()
		val k = v2.zCoord.mfloor()
		var l = v1.xCoord.mfloor()
		var i1 = v1.yCoord.mfloor()
		var j1 = v1.zCoord.mfloor()
		
		world.getBlock(l, i1, j1)
		world.getBlockMetadata(l, i1, j1)
		
		var k1 = 200
		
		while (k1-- >= 0) {
			if (v1.xCoord.isNaN() || v1.yCoord.isNaN() || v1.zCoord.isNaN())
				return null
			
			if (l == i && i1 == j && j1 == k) continue
			
			var flag6 = true
			var flag3 = true
			var flag4 = true
			var d0 = 999.0
			var d1 = 999.0
			var d2 = 999.0
			
			if (i > l) {
				d0 = l.D + 1
			} else if (i < l) {
				d0 = l.D + 0
			} else {
				flag6 = false
			}
			if (j > i1) {
				d1 = i1.D + 1
			} else if (j < i1) {
				d1 = i1.D + 0
			} else {
				flag3 = false
			}
			if (k > j1) {
				d2 = j1.D + 1
			} else if (k < j1) {
				d2 = j1.D + 0
			} else {
				flag4 = false
			}
			var d3 = 999.0
			var d4 = 999.0
			var d5 = 999.0
			val d6 = v2.xCoord - v1.xCoord
			val d7 = v2.yCoord - v1.yCoord
			val d8 = v2.zCoord - v1.zCoord
			if (flag6) {
				d3 = (d0 - v1.xCoord) / d6
			}
			if (flag3) {
				d4 = (d1 - v1.yCoord) / d7
			}
			if (flag4) {
				d5 = (d2 - v1.zCoord) / d8
			}
			
			var b0: Int
			if (d3 < d4 && d3 < d5) {
				b0 = if (i > l) 4 else 5
				v1.xCoord = d0
				v1.yCoord += d7 * d3
				v1.zCoord += d8 * d3
			} else if (d4 < d5) {
				b0 = if (j > i1) 0 else 1
				v1.xCoord += d6 * d4
				v1.yCoord = d1
				v1.zCoord += d8 * d4
			} else {
				b0 = if (k > j1) {
					2
				} else {
					3
				}
				v1.xCoord += d6 * d5
				v1.yCoord += d7 * d5
				v1.zCoord = d2
			}
			val vec32 = Vec3.createVectorHelper(v1.xCoord, v1.yCoord, v1.zCoord)
			l = v1.xCoord.mfloor().also { vec32.xCoord = it.D }
			if (b0 == 5) {
				--l
				++vec32.xCoord
			}
			i1 = v1.yCoord.mfloor().also { vec32.yCoord = it.D }
			if (b0 == 1) {
				--i1
				++vec32.yCoord
			}
			j1 = v1.zCoord.mfloor().also { vec32.zCoord = it.D }
			if (b0 == 3) {
				--j1
				++vec32.zCoord
			}
			val block1 = world.getBlock(l, i1, j1)
			val l1 = world.getBlockMetadata(l, i1, j1)
			if (block1.canCollideCheck(l1, true))
				return block1.collisionRayTrace(world, l, i1, j1, v1, v2)
		}
		return null
	}
}
