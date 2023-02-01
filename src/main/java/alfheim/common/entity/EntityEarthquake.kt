package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.AlfheimCore
import alfheim.client.render.particle.EntityFXSmoke
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler.isProtected
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderGameOverlayEvent
import kotlin.math.*

/**
 * @author Jokiboy (Nature Reborn)
 */
class EntityEarthquake(world: World): Entity(world) {
	
	var xM: Double
		get() = dataWatcher.getWatchableObjectFloat(2).D
		set(value) = dataWatcher.updateObject(2, value.F)
	
	var zM: Double
		get() = dataWatcher.getWatchableObjectFloat(3).D
		set(value) = dataWatcher.updateObject(3, value.F)
	
	var lifetime = 0
	var maxLifetime = 0
	
	init {
		if (!world.isRemote) {
			val destructionAngle = rand.nextDouble() * Math.PI * 2.0
			
			xM = cos(destructionAngle)
			zM = sin(destructionAngle)
		}
		
		setSize(0f, 0f)
		
		val r = AlfheimConfigHandler.faultLinePersistence
		maxLifetime = ASJUtilities.randInBounds(r / 2, r.I, world.rand)
	}
	
	constructor(world: World, x: Int, z: Int): this(world) {
		setPosition(x.D, -3.0, z.D)
	}
	
	override fun onUpdate() {
		if (!RagnarokHandler.ginnungagap) return setDead()
		
		if (worldObj.isProtected(posX.mfloor(), posY.mfloor(), posZ.mfloor(), false)) return setDead()
		
		val offset = rand.nextInt(15 + lifetime / 20)
		val radius = 30 + lifetime / 20 + offset
		
		for (i in -radius..radius) {
			val xx = (posX + xM * i).I
			val zz = (posZ + zM * i).I
			
			if (rand.nextDouble() < 3.0E-5 && !worldObj.isRemote)
				EntityEarthquakeFracture(worldObj, xx, zz, maxLifetime / 8 + 30).spawn()
			
			if (rand.nextDouble() >= 0.03) continue
			val range = lifetime / 30 + 3
			
			for (x in -range..range) {
				for (z in -range..range) {
					if (x * x + z * z >= range) continue
					
					for (y in 256 downTo 5) {
						val block = worldObj.getBlock(x + xx, y, z + zz)
						
						if (block === Blocks.air) continue
						
						if (rand.nextDouble() < 0.3) {
							if (!worldObj.isRemote && !worldObj.isProtected(x + xx, y, z + zz, false, false))
								worldObj.setBlock(x + xx, y, z + zz, if (y >= 12) Blocks.air else Blocks.flowing_lava)
							else if (worldObj.rand.nextDouble() < 0.05 && AlfheimCore.proxy.doParticle()) {
								val e = EntityFXSmoke(worldObj, xx + x + rand.nextDouble(), y.D, zz + z + rand.nextDouble(), 0.0, 0.2, 0.0, 2.0f + worldObj.rand.nextFloat() * 3f, 12f)
								mc.effectRenderer.addEffect(e)
							}
						}
						
						if (!block.material.run { isLiquid || isReplaceable } && !block.isLeaves(worldObj, x + xx, y, z + zz) && !block.isWood(worldObj, x + xx, y, z + zz)) break
					}
				}
			}
		}
		
		if (lifetime++ > maxLifetime) {
			return setDead()
		}
		
		if (worldObj.isRemote) {
			val newDist = Vector3.entityDistance(this, mc.thePlayer)
			dist = if (lastCheckTick != worldObj.totalWorldTime) newDist else min(dist, newDist)
			lastCheckTick = worldObj.totalWorldTime
		}
	}
	
	override fun entityInit() {
		dataWatcher.addObject(2, 0f)
		dataWatcher.addObject(3, 0f)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		if (nbt.hasKey("xM")) xM = nbt.getDouble("xM")
		if (nbt.hasKey("zM")) zM = nbt.getDouble("zM")
		if (nbt.hasKey("life")) lifetime = nbt.getInteger("life")
		if (nbt.hasKey("maxlife")) maxLifetime = nbt.getInteger("maxlife")
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setDouble("xM", xM)
		nbt.setDouble("zM", zM)
		nbt.setInteger("life", lifetime)
		nbt.setInteger("maxlife", maxLifetime)
	}
	
	companion object {
		
		var dist = Double.MAX_VALUE
		var lastCheckTick = 0L
		
		init {
			eventForge()
		}
		
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		fun onGuiRender(event: RenderGameOverlayEvent.Post) {
			if (event.type != RenderGameOverlayEvent.ElementType.HELMET) return
			
			if (mc.theWorld.totalWorldTime > lastCheckTick + 50) {
				dist = Double.MAX_VALUE
				return
			}
			
			mc.thePlayer.yOffset = 1.62f
			val shakeMult = 1 - dist / 128

			if (shakeMult <= 0) return
			if (mc.thePlayer == null || mc.thePlayer.isPlayerSleeping || !mc.thePlayer.onGround || mc.currentScreen != null && mc.currentScreen.doesGuiPauseGame()) return

			val shakeSpeed = 2.0 * shakeMult
			val offsetY = 0.2f * shakeMult
			val shake = (mc.theWorld.totalWorldTime % 24000).I * shakeSpeed
			mc.thePlayer.yOffset -= (sin(shake) * (offsetY / 2f) + offsetY / 2f).F
			mc.thePlayer.cameraPitch = (sin(shake) * offsetY / 4f).F
			mc.thePlayer.cameraYaw = (sin(shake) * offsetY / 4f).F
		}
	}
}