package alfheim.common.item

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.*
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderGameOverlayEvent
import org.lwjgl.opengl.GL11
import vazkii.botania.api.lexicon.multiblock.component.AnyComponent
import vazkii.botania.client.core.handler.MultiblockRenderHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.item.ItemSextant.MultiblockSextant
import vazkii.botania.common.item.equipment.tool.ToolCommons
import kotlin.math.*

class ItemArmilla: ItemMod("Armilla") {
	
	init {
		maxStackSize = 1
	}
	
	override fun getItemUseAction(par1ItemStack: ItemStack?) = EnumAction.bow
	
	override fun getMaxItemUseDuration(par1ItemStack: ItemStack?) = 72000
	
	override fun onUsingTick(stack: ItemStack?, player: EntityPlayer, count: Int) {
		if (count % 10 != 0 || getMaxItemUseDuration(stack) - count < 10) return
		
		val y = ItemNBTHelper.getInt(stack, TAG_SOURCE_Y, -1)
		if (y == -1) return
		
		val x = ItemNBTHelper.getInt(stack, TAG_SOURCE_X, 0)
		val z = ItemNBTHelper.getInt(stack, TAG_SOURCE_Z, 0)
		
		val world = player.worldObj
		val source = Vector3(x, y, z)
		val radius = calculateRadius(stack, player)
		
		for (i in 0..359) {
			val radian = i * Math.PI / 180
			val xp = x + cos(radian) * radius
			val zp = z + sin(radian) * radius
			Botania.proxy.wispFX(world, xp + 0.5, source.y + 1, zp + 0.5, 0f, 1f, 1f, 0.3f, -0.01f)
		}
	}
	
	override fun onPlayerStoppedUsing(stack: ItemStack?, world: World?, player: EntityPlayer, time: Int) {
		if (ASJUtilities.isServer) return
		
		val radius = calculateRadius(stack, player).I
		if (radius <= 1) return
		
		val y = ItemNBTHelper.getInt(stack, TAG_SOURCE_Y, -1)
		if (y == -1) return
		
		val x = ItemNBTHelper.getInt(stack, TAG_SOURCE_X, 0)
		val z = ItemNBTHelper.getInt(stack, TAG_SOURCE_Z, 0)
		
		setMultiblock(x, y, z, radius, Blocks.cobblestone)
	}
	
	fun setMultiblock(x: Int, y: Int, z: Int, radius: Int, block: Block?) {
		val mb = MultiblockSextant()
		val radius1 = radius + 1
		
		for (i in 0 until radius1 * 2 + 1)
			for (j in 0 until radius1 * 2 + 1)
				for (k in 0 until radius1 * 2 + 1) {
				val xp = x + i - radius1
				val yp = y + j - radius1
				val zp = z + k - radius1
				
				if (floor(Vector3.pointDistanceSpace(xp, yp, zp, x, y, z)).I == radius1 - 1)
					mb.addComponent(AnyComponent(ChunkCoordinates(xp - x, yp - y, zp - z), block, 0))
		}
		
		MultiblockRenderHandler.setMultiblock(mb.makeSet())
		MultiblockRenderHandler.anchor = ChunkCoordinates(x, y, z)
	}
	
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		Botania.proxy.removeSextantMultiblock()
		
		if (player.isSneaking) return stack
		
		val pos = ToolCommons.raytraceFromEntity(world, player, false, 128.0)
		
		if (pos != null && pos.entityHit == null) {
			if (!world.isRemote) {
				ItemNBTHelper.setInt(stack, TAG_SOURCE_X, pos.blockX)
				ItemNBTHelper.setInt(stack, TAG_SOURCE_Y, pos.blockY)
				ItemNBTHelper.setInt(stack, TAG_SOURCE_Z, pos.blockZ)
			}
		} else
			ItemNBTHelper.setInt(stack, TAG_SOURCE_Y, -1)
		
		player.setItemInUse(stack, getMaxItemUseDuration(stack))
		
		return stack
	}
	
	companion object {
		
		private const val TAG_SOURCE_X = "sourceX"
		private const val TAG_SOURCE_Y = "sourceY"
		private const val TAG_SOURCE_Z = "sourceZ"
		
		init {
			if (ASJUtilities.isClient) eventForge()
		}
		
		@SubscribeEvent
		fun onDrawScreenPost(event: RenderGameOverlayEvent.Post) {
			if (event.type != RenderGameOverlayEvent.ElementType.ALL) return
			
			val stack = mc.thePlayer.heldItem ?: return
			if (stack.item !is ItemArmilla) return
			
			renderHUD(event.resolution, mc.thePlayer, stack)
		}
		
		@SideOnly(Side.CLIENT)
		fun renderHUD(resolution: ScaledResolution, player: EntityPlayer, stack: ItemStack) {
			val onUse = player.getItemInUse()
			val time = player.getItemInUseCount()
			
			if (onUse != stack || stack.item.getMaxItemUseDuration(stack) - time < 10) return
			
			var radius = calculateRadius(stack, player)
			val font = Minecraft.getMinecraft().fontRenderer
			val x = resolution.scaledWidth / 2 + 30
			val y = resolution.scaledHeight / 2
			val s = "${radius.I}"
			font.drawStringWithShadow(s, x - font.getStringWidth(s) / 2, y - 4, 0xFFFFFF)
			if (radius <= 0) return
			
			radius += 4.0
			GL11.glDisable(GL11.GL_TEXTURE_2D)
			GL11.glLineWidth(3f)
			GL11.glBegin(GL11.GL_LINE_STRIP)
			GL11.glColor4f(0f, 1f, 1f, 1f)
			for (i in 0..360) {
				val radian = i * Math.PI / 180
				val xp = x + cos(radian) * radius
				val yp = y + sin(radian) * radius
				GL11.glVertex2d(xp, yp)
			}
			GL11.glEnd()
			GL11.glEnable(GL11.GL_TEXTURE_2D)
		}
		
		fun calculateRadius(stack: ItemStack?, player: EntityPlayer): Double {
			val x = ItemNBTHelper.getInt(stack, TAG_SOURCE_X, 0).D
			val y = ItemNBTHelper.getInt(stack, TAG_SOURCE_Y, -1).D
			val z = ItemNBTHelper.getInt(stack, TAG_SOURCE_Z, 0).D
			
			val world = player.worldObj
			val source = Vector3(x, y, z)
			Botania.proxy.wispFX(world, source.x + 0.5, source.y + 1, source.z + 0.5, 1f, 0f, 0f, 0.2f, -0.1f)
			
			val centerVec = Vector3.fromEntityCenter(player)
			val diffVec = source.copy().sub(centerVec)
			val lookVec = Vector3(player.lookVec)
			val mul = diffVec.y / lookVec.y
			lookVec.mul(mul).add(centerVec)
			lookVec.x = lookVec.x.mfloor().D
			lookVec.z = lookVec.z.mfloor().D
			
			val radius = Vector3.pointDistancePlane(source.x, source.z, lookVec.x, lookVec.z)
			
			return min(radius, 256.0)
		}
	}
}
