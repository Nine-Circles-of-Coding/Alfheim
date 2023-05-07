package alfheim.client.render.item

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper.discard
import alexsocol.asjlib.render.ASJRenderHelper.setBlend
import alexsocol.asjlib.render.ASJRenderHelper.setGlow
import alexsocol.asjlib.render.ASJRenderHelper.setTwoside
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.block.ModelYggFlower
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer
import org.lwjgl.opengl.GL11.*

object RenderItemYggFlower: IItemRenderer {
	
	override fun handleRenderType(item: ItemStack?, type: IItemRenderer.ItemRenderType) = true
	
	override fun shouldUseRenderHelper(type: IItemRenderer.ItemRenderType, item: ItemStack, helper: IItemRenderer.ItemRendererHelper) = true
	
	override fun renderItem(type: IItemRenderer.ItemRenderType, item: ItemStack?, vararg data: Any?) {
		glPushMatrix()
		setBlend()
		setGlow()
		setTwoside()
		
		glTranslated(0.5, 0.0625, 0.5)
		glRotatef(180f, 0f, 0f, 1f)
		
		if (type == IItemRenderer.ItemRenderType.ENTITY) glTranslated(0.5, 0.0, -0.5)
		else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) glTranslatef(0f, -0.5f, 0f)
		
		glScaled(1.5)
		
		mc.renderEngine.bindTexture(LibResourceLocations.yggFlower)
		ModelYggFlower.render()
		
		discard()
		glPopMatrix()
	}
}
