package alfheim.client.render.item

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.item.ModelSurtrSword
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer
import org.lwjgl.opengl.GL11.*

object RenderItemSurtrSword: IItemRenderer {
	
	override fun handleRenderType(item: ItemStack?, type: IItemRenderer.ItemRenderType?) = true
	
	override fun shouldUseRenderHelper(type: IItemRenderer.ItemRenderType, item: ItemStack?, helper: IItemRenderer.ItemRendererHelper) =
		helper == IItemRenderer.ItemRendererHelper.ENTITY_BOBBING || helper == IItemRenderer.ItemRendererHelper.ENTITY_ROTATION
	
	override fun renderItem(type: IItemRenderer.ItemRenderType?, item: ItemStack?, vararg data: Any?) {
		glPushMatrix()
		
		if (type == IItemRenderer.ItemRenderType.INVENTORY) {
			glScalef(8f)
			glRotatef(45f, 0f, 0f, 1f)
			glTranslatef(1.4f, 0.75f, 0f)
		} else {
			glRotatef(-135f, 0f, 0f, 1f)
			glTranslatef(-0.7f, 0f, 0f)
		}
		
		mc.renderEngine.bindTexture(LibResourceLocations.surtrSword)
		ModelSurtrSword.render(0.0625f)
		mc.renderEngine.bindTexture(LibResourceLocations.surtrSwordGlow)
		ASJRenderHelper.setGlow()
		ModelSurtrSword.render(0.0625f)
		ASJRenderHelper.discard()
		glPopMatrix()
	}
}