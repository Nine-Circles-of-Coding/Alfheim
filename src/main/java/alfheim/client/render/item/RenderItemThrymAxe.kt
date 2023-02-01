package alfheim.client.render.item

import alexsocol.asjlib.*
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.item.ModelThrymAxe
import alfheim.common.entity.boss.primal.EntityThrym
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer
import org.lwjgl.opengl.GL11.*
import kotlin.math.min

object RenderItemThrymAxe: IItemRenderer {
	
	override fun handleRenderType(item: ItemStack?, type: IItemRenderer.ItemRenderType?) = true
	
	override fun shouldUseRenderHelper(type: IItemRenderer.ItemRenderType, item: ItemStack?, helper: IItemRenderer.ItemRendererHelper) =
		helper == IItemRenderer.ItemRendererHelper.ENTITY_BOBBING || helper == IItemRenderer.ItemRendererHelper.ENTITY_ROTATION
	
	override fun renderItem(type: IItemRenderer.ItemRenderType?, item: ItemStack?, vararg data: Any?) {
		glPushMatrix()
		
		if (type == IItemRenderer.ItemRenderType.INVENTORY) {
			glScalef(8f)
			glRotatef(45f, 0f, 0f, 1f)
			glTranslatef(1.5f, 0.5f, 0f)
		} else {
			glRotatef(-150f, 0f, 0f, 1f)
			glTranslatef(-0.7f, 0f, 0f)
		}
		glRotatef(95f, 0f, 1f, 0f)
		
		if (type == IItemRenderer.ItemRenderType.EQUIPPED) run {
			val host = data.getOrNull(1) as? EntityThrym ?: return@run
			val ult = host.ultAnimationTicks
			
			if (ASJBitwiseHelper.getBit(ult, 9)) return@run
			
			glTranslatef(0f, 0f, -2 * when (ult) {
				in 1..10  -> min(0.01f * ult + mc.timer.renderPartialTicks, 0.1f)
				in 11..69 -> 0.1f
				in 70..73 -> min(0.03f * (ult - 70) + mc.timer.renderPartialTicks, 0.1f)
				else -> 0f
			})
		}
		
		mc.renderEngine.bindTexture(LibResourceLocations.thrymAxe)
		ModelThrymAxe.render(0.0625f)
		glPopMatrix()
	}
}
