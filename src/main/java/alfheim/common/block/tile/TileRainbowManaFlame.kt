package alfheim.common.block.tile

import alexsocol.asjlib.*
import alfheim.common.core.asm.hook.AlfheimHookHandler
import alfheim.common.core.handler.*
import alfheim.common.item.relic.ItemTankMask.Companion.limboCounter
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.server.MinecraftServer
import vazkii.botania.client.core.handler.ClientTickHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.block.tile.TileManaFlame
import vazkii.botania.common.integration.coloredlights.ColoredLightHelper
import java.awt.Color
import java.util.*

open class TileRainbowManaFlame: TileManaFlame() {
	
	var invisible = false
	var soul = false
	var exit = false
	
	init {
		color = -1
	}
	
	override fun updateEntity() {
		if (!shouldRender()) return
		
		if (soul) {
			val (r, g, b) = Color(getColor()).getRGBColorComponents(null)
			Botania.proxy.wispFX(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, r, g, b, (Math.random() * 0.5).F, (Math.random() * 0.015 - 0.0075).F, (Math.random() * 0.025).F, (Math.random() * 0.015 - 0.0075).F, 2f)
		} else
			super.updateEntity()
	}
	
	fun exitPlayer(player: EntityPlayer) {
		if (ASJUtilities.isClient || player !is EntityPlayerMP) return
		
		if (CardinalSystem.CommonSystem.cantLostHearts(player))
			return ASJUtilities.say(player, "alfheimmisc.nowayout")
		
		CardinalSystem.CommonSystem.loseHearts(player, 1)
		
		player.limboCounter = 0
		
		val world = MinecraftServer.getServer().worldServerForDimension(AlfheimConfigHandler.dimensionIDNiflheim)
		AlfheimHookHandler.allowtp = true
		ASJUtilities.sendToDimensionWithoutPortal(player, AlfheimConfigHandler.dimensionIDNiflheim, 0.5, world.getTopSolidOrLiquidBlock(0, 0) + 0.5, 0.5)
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		super.writeCustomNBT(nbt)
		nbt.setBoolean(TAG_SOUL, soul)
		if (exit) nbt.setBoolean(TAG_EXIT, true)
		nbt.setBoolean(TAG_INVISIBLE, invisible)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		super.readCustomNBT(nbt)
		soul = nbt.getBoolean(TAG_SOUL)
		exit = nbt.getBoolean(TAG_EXIT)
		invisible = nbt.getBoolean(TAG_INVISIBLE)
	}
	
	override fun getColor(): Int {
		if (exit) return 0xFFD400
		if (color != -1) return super.getColor()
		
		var time = ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks
		time += Random((xCoord xor yCoord xor zCoord).toLong()).nextInt(100000)
		return Color.HSBtoRGB(time * 0.005F, 1F, 1F)
	}
	
	override fun getLightColor(): Int {
		val (r, g, b) = Color(getColor()).getRGBColorComponents(null)
		return ColoredLightHelper.makeRGBLightValue(r, g, b, 1f)
	}
	
	fun shouldRender() = ASJUtilities.isClient && (Botania.proxy.isClientPlayerWearingMonocle || !invisible)
	
	companion object {
		private val TAG_SOUL = "soul"
		private val TAG_EXIT = "exit"
		private val TAG_INVISIBLE = "invisible"
	}
}
