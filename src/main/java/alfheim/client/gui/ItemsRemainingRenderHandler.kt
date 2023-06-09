package alfheim.client.gui

import alexsocol.asjlib.*
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.event.RenderGameOverlayEvent
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL
import java.util.regex.*
import kotlin.math.max

object ItemsRemainingRenderHandler {
	
	private const val maxTicks = 30
	private const val leaveTicks = 20
	
	private var stack = ItemStack(Blocks.stone)
	private var customString: String? = null
	private var ticks: Int = 0
	private var count: Int = 0
	
	init {
		eventForge()
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun onDrawScreenPost(event: RenderGameOverlayEvent.Post) {
		if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) return
		if (ticks <= 0 || !isNotEmpty(stack))
			return
		
		val resolution: ScaledResolution = event.resolution
		val partTicks: Float = event.partialTicks
		
		val pos = maxTicks - ticks
		val x = resolution.scaledWidth / 2 + 10 + max(0, pos - leaveTicks)
		val y = resolution.scaledHeight / 2
		
		val start = maxTicks - leaveTicks
		val alpha = if (ticks + partTicks > start) 1f else (ticks + partTicks) / start
		
		glDisable(GL_ALPHA_TEST)
		glEnable(GL_BLEND)
		glEnable(GL_RESCALE_NORMAL)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		
		glColor4f(1f, 1f, 1f, alpha)
		RenderHelper.enableGUIStandardItemLighting()
		val xp = x + (16f * (1f - alpha)).I
		glTranslatef(xp.F, y.F, 0f)
		glScalef(alpha, 1f, 1f)
		RenderItem.getInstance().renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, stack, 0, 0)
		glScalef(1f / alpha, 1f, 1f)
		glTranslatef((-xp).F, (-y).F, 0f)
		RenderHelper.disableStandardItemLighting()
		glColor4f(1f, 1f, 1f, 1f)
		glEnable(GL_BLEND)
		
		var text = ""
		
		if (customString == null) {
			if (isNotEmpty(stack)) {
				text = EnumChatFormatting.GREEN.toString() + stack.displayName
				if (count >= 0) {
					val max = stack.maxStackSize
					val stacks = count / max
					val rem = count % max
					
					text = if (stacks == 0)
						"$count"
					else
						"$count (${EnumChatFormatting.AQUA}stacks${EnumChatFormatting.RESET}*${EnumChatFormatting.GRAY}$max${EnumChatFormatting.RESET}+${EnumChatFormatting.YELLOW}$rem${EnumChatFormatting.RESET})"
				} else if (count == -1)
					text = "\u221E"
			}
		} else
			text = customString!!
		
		val color = 0x00FFFFFF or ((alpha * 0xFF).I shl 24)
		mc.fontRenderer.drawStringWithShadow(text, x + 20, y + 6, color)
		
		glDisable(GL_BLEND)
		glEnable(GL_ALPHA_TEST)
	}
	
	@SideOnly(Side.CLIENT)
	fun tick() {
		if (ticks > 0)
			--ticks
	}
	
	fun set(stack: ItemStack, str: String) {
		set(stack, 0, str)
	}
	
	@JvmOverloads
	fun set(stack: ItemStack, count: Int, str: String? = null) {
		this.stack = stack
		this.count = count
		customString = str
		ticks = if (stack.item === Blocks.air.toItem()) 0 else maxTicks
	}
	
	fun set(player: EntityPlayer, displayStack: ItemStack, pattern: Pattern) {
		var count = 0
		for (i in 0 until player.inventory.sizeInventory) {
			val stack = player.inventory[i] ?: continue
			if (isNotEmpty(stack) && pattern.matcher(stack.displayName).find())
				count += stack.stackSize
		}
		
		set(displayStack, count)
	}
	
	fun isNotEmpty(stack: ItemStack): Boolean {
		return stack.item !== Blocks.air.toItem()
	}
}