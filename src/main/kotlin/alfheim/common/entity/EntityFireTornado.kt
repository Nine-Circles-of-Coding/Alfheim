package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.client.render.particle.EntityTornadoFX
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler.isProtected
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.util.DamageSource
import net.minecraft.world.World

class EntityFireTornado(world: World): Entity(world) {
	
	override fun onUpdate() {
		if (!RagnarokHandler.ginnungagap) return setDead()
		
		if (ticksExisted > rand.nextInt(1200) + 2400) return setDead()
		
		if (worldObj.isProtected(posX.mfloor(), posY.mfloor(), posZ.mfloor(), false)) return setDead()
		
		super.onUpdate()
		
		extinguish()
		moveEntity(motionX, motionY, motionZ)
		
		if (worldObj.isRemote) {
			for (i in 0..64) {
				val yPos = rand.nextDouble() * 34 - 2
				mc.effectRenderer.addEffect(EntityTornadoFX(worldObj, posX, posY + yPos, posZ, rand.nextDouble() * yPos / 3 + 0.5, motionX, motionZ, Blocks.fire, 0, 0, 100))
			}
			
			return
		}
		
		val bb = getBoundingBox().expand(32, 4, 32)
		for (x in bb.minX.mfloor()..bb.maxX.mfloor())
			for (z in bb.minZ.mfloor()..bb.maxZ.mfloor())
				for (y in bb.minY.mfloor()..bb.maxY.mfloor())
					if (worldObj.rand.nextInt(300) == 0 &&
						Vector3.pointDistancePlane(x, z, posX, posZ) < (17 + (y - posY) / 3) &&
						worldObj.getBlock(x, y, z).isReplaceable(worldObj, x, y, z) &&
						Blocks.fire.canPlaceBlockAt(worldObj, x, y, z) &&
						!worldObj.isProtected(x, y, z, false, false))
							worldObj.setBlock(x, y, z, Blocks.fire)
		
		val list = getEntitiesWithinAABB(worldObj, Entity::class.java, getBoundingBox())
		list.remove(this)
		list.forEach {
			val dx = (if (posX - it.posX > 0) 0.5 else -0.5) - (posX - it.posX) / 8
			val dz = (if (posZ - it.posZ > 0) 0.5 else -0.5) - (posZ - it.posZ) / 8
			
			it.attackEntityFrom(DamageSource.inFire, 1f)
			it.setFire(5)
			
			it.motionX = dx
			it.motionY += 0.2
			it.motionZ = dz
			
			if (it is EntityPlayerMP) it.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(it))
		}
	}
	
	override fun entityInit() {
		noClip = true
		setSize(0f, 0f)
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setDouble("mX", motionX)
		nbt.setDouble("mZ", motionZ)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		motionX = nbt.getDouble("mX")
		motionZ = nbt.getDouble("mZ")
	}
	
	override fun getBoundingBox() = getBoundingBox(posX - 8, posY - 2, posZ - 8, posX + 8, posY + 32, posZ + 8)
}
