package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.*
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.util.DamageSourceSpell
import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.AxisAlignedBB
import vazkii.botania.common.Botania
import vazkii.botania.common.block.tile.mana.TilePool
import vazkii.botania.common.core.handler.ConfigHandler
import java.awt.Color
import kotlin.math.*

class TileAlfheimPylon: ASJTile() {
	
	var activated = false
	var centerX: Int = 0
	var centerY: Int = 0
	var centerZ: Int = 0
	var ticks = 0
	var ginnungagapProgress = 0
	
	override fun updateEntity() {
		++ticks
		val meta = getBlockMetadata()
		
		when (meta) {
			1 -> updateYordin()
			3 -> updateCreation()
		}
		
		if (worldObj.rand.nextBoolean() && worldObj.isRemote)
			Botania.proxy.sparkleFX(worldObj, xCoord + Math.random(), yCoord + Math.random() * 1.5, zCoord + Math.random(), 1f, if (meta != 2) 0.5f else 0f, (if (meta == 0) 1 else 0).F, Math.random().F, 2)
	}
	
	fun updateYordin() {
		if (!activated || !worldObj.isRemote) return
		
		if (worldObj.getBlock(centerX, centerY, centerZ) !== AlfheimBlocks.tradePortal || worldObj.getBlockMetadata(centerX, centerY, centerZ) == 0) {
			activated = false
			return
		}
		
		if (!ConfigHandler.elfPortalParticlesEnabled) return
		
		val centerBlock = Vector3(centerX + 0.5, centerY.D + 0.75 + (Math.random() - 0.5 * 0.25), centerZ + 0.5)
		var worldTime = ticks.D
		worldTime += Math.random() * 1000
		worldTime /= 5.0
		
		val r = 0.75f + Math.random().F * 0.05f
		val x = xCoord.D + 0.5 + cos(worldTime) * r
		val z = zCoord.D + 0.5 + sin(worldTime) * r
		
		centerBlock.sub(0.0, 0.5, 0.0).sub(x, yCoord + 0.75, z).normalize().mul(0.2)
		
		Botania.proxy.wispFX(worldObj, x, yCoord + 0.25, z, 0.75f + Math.random().F * 0.25f, 0.5f + Math.random().F * 0.25f, Math.random().F * 0.25f, 0.25f + Math.random().F * 0.1f, -0.075f - Math.random().F * 0.015f)
		if (worldObj.rand.nextInt(3) == 0)
			Botania.proxy.wispFX(worldObj, x, yCoord + 0.25, z, 0.75f + Math.random().F * 0.25f, 0.5f + Math.random().F * 0.25f, Math.random().F * 0.25f, 0.25f + Math.random().F * 0.1f, centerBlock.x.F, centerBlock.y.F, centerBlock.z.F)
	}
	
	fun updateCreation() {
		if (!RagnarokHandler.ginnungagap || worldObj.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim) return

		val (x, y, z) = Vector3.fromTileEntity(this)
		
		val bbsouth = getBoundingBox(x - 1, y - 1, z + 1, x + 2, y + 2, z + 6)
		val bbnorth = getBoundingBox(x - 1, y - 1, z - 5, x + 2, y + 2, z)
		val bbeast = getBoundingBox(x + 1, y - 1, z, x + 5, y + 1, z + 1)
		val bbwest = getBoundingBox(x - 4, y - 1, z, x, y + 1, z + 1)
		val rayeast = getBoundingBox(x - 5, y + 1, z, x - 4, y + 256, z + 1)
		val raywest = getBoundingBox(x + 5, y + 1, z, x - 6, y + 256, z + 1)
		
		val pool = checkStructure()

		if (pool == null || pool.currentMana < MANA_PER_TICK || arrayOf(bbeast, bbnorth, bbsouth, bbwest, rayeast, raywest).any { worldObj.func_147461_a(it).isNotEmpty() }) {
			activated = false
			if (ginnungagapProgress == 0) return
			return youAreSoFuckedUp()
		}
		
		activated = true
		
		pool.recieveMana(-MANA_PER_TICK)
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, bbsouth).forEach {
			it.attackEntityFrom(DamageSourceSpell.soulburn, 1f)
		}
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, bbnorth).forEach {
			it.attackEntityFrom(DamageSourceSpell.nifleice, 1f)
		}
		
		arrayOf(bbeast, bbwest, rayeast, raywest).forEach { bb ->
			getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, bb).forEach {
				it.attackEntityFrom(DamageSourceSpell.faith, 3f)
			}
		}
		
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.CREATION, worldObj.provider.dimensionId, x + 0.5, y + 0.5, z + 0.5, ticks.D)
		
		if (ginnungagapProgress++ >= 20*60*30) {
			ginnungagapProgress = 0
			RagnarokHandler.endGinnungagap()
			activated = false
			worldObj.playBroadcastSound(1013, xCoord, yCoord, zCoord, 0)
			worldObj.setBlockToAir(xCoord, yCoord, zCoord)
			return
		}
	}
	
	fun checkStructure(): TilePool? {
		if (!SchemaUtils.checkStructure(worldObj, xCoord, yCoord, zCoord, schema)) return null
		return worldObj.getTileEntity(xCoord, yCoord - 2, zCoord) as? TilePool ?: return null
	}
	
	fun youAreSoFuckedUp() {
		worldObj.setBlockToAir(xCoord, yCoord, zCoord)
		worldObj.newExplosion(null, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 1000f, true, true)

		repeat(ASJUtilities.randInBounds(10, 20, worldObj.rand)) { RagnarokHandler.doGinnungagapCataclysm(worldObj) }
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		ginnungagapProgress = nbt.getInteger(TAG_GG_TICKS)
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		if (getBlockMetadata() == 3)
			nbt.setInteger(TAG_GG_TICKS, ginnungagapProgress)
	}
	
	override fun getRenderBoundingBox(): AxisAlignedBB {
		return if (getBlockMetadata() == 3) getBoundingBox(xCoord - 5, yCoord - 1, zCoord - 5, xCoord + 6, yCoord + 256, zCoord + 6) else super.getRenderBoundingBox()
	}
	
	companion object {
		const val MANA_PER_TICK = 2000
		const val TAG_GG_TICKS = "ggticks"
		
		val schema = SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/Cosmogonic")
		
		fun doCreationParticles(x: Double, y: Double, z: Double, ticks: Int) {
			for (k in arrayOf(-5, 5)) {
				val t = (if (k < 0) 30 - (ticks % 30) else ticks % 30) / 10.0
				val (r, g, b) = (if (k < 0) Color.BLUE else Color.RED).getRGBColorComponents(null)
				val c = 0.1f
				
				for ((id, it) in arrayOf(-1.5 to -1.5, 1.5 to 1.5, -1.5 to 1.5, 1.5 to -1.5).withIndex()) {
					var i = it.first
					var j = it.second
					
					when (id) {
						0 -> j += t
						1 -> j -= t
						2 -> i += t
						3 -> i -= t
					}
					val (u, v, w) = Vector3(x, y, z).add(i, j, k)
					val (mx, my, mz) = Vector3(x, y + 0.25, z).sub(u, v, w).mul(0.04).F
					Botania.proxy.wispFX(mc.theWorld, u, v, w, r + Math.random().F * c, g + Math.random().F * c, b + Math.random().F * c, (Math.random() * 0.5 + 0.5).F, mx, my, mz)
				}
			}
			
			for (i in arrayOf(-5, 5)) {
				val (r, g, b) = Color(0xFFA400).getRGBColorComponents(null)
				val (mx, my, mz) = Vector3(i, -1, 0).mul(0.035).F
				Botania.proxy.wispFX(mc.theWorld, x + 0.5 * if (i > 0) 1 else -1, y + 0.25, z, r, g, b, (Math.random() * 0.5 + 0.5).F, mx, my, mz)
			}
		}
	}
}
