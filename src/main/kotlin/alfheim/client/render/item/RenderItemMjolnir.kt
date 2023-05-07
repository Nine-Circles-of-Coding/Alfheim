package alfheim.client.render.item

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.material.ItemElvenResource
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.IItemRenderer
import net.minecraftforge.client.IItemRenderer.ItemRenderType
import net.minecraftforge.client.IItemRenderer.ItemRenderType.*
import net.minecraftforge.client.model.AdvancedModelLoader
import org.lwjgl.opengl.GL11.*

object RenderItemMjolnir: IItemRenderer {
	
	val model = if (AlfheimConfigHandler.minimalGraphics) null else AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/Mjolnir.obj"))
	
	override fun renderItem(type: ItemRenderType, stack: ItemStack, vararg data: Any?) {
		if (model == null) return // renderer won't be registered at all
		
		glPushMatrix()
		
		if (type == EQUIPPED_FIRST_PERSON || type == EQUIPPED) {
			glRotatef(135f, 0f, 1f, 0f)
			glTranslatef(-0.7f, 0.5f, -0.15f)
			glRotatef(if (type == EQUIPPED_FIRST_PERSON) -5f else -15f, 1f, 0f, 0f)
			glScaled(0.75)
		}
		
		if (type == INVENTORY) {
			RenderHelper.enableStandardItemLighting()
			glRotatef(-45f, 1f, 1f, 1f)
			glRotatef(-67.5f, 0f, 1f, 0f)
			glScaled(0.5)
			glTranslatef(0f, -1f, 0f)
		}
		
		if (type == ENTITY) {
			glScaled(0.5)
			glTranslatef(0f, 1f, 0f)
		}
		
		if (stack.displayName.trim().lowercase().let { it == "gloryhammer" || it == "glory hammer" }) {
			glPushMatrix()
			if (type == INVENTORY) glEnable(GL_BLEND)
			glScaled(0.5)
			
			val icon = ItemElvenResource.kitty
			mc.renderEngine.bindTexture(TextureMap.locationItemsTexture)
			
			glTranslatef(-0.5f, 3.625f, 1.84375f)
			ItemRenderer.renderItemIn2D(Tessellator.instance, icon.maxU, icon.minV, icon.minU, icon.maxV, icon.iconWidth, icon.iconHeight, 1f / 16f)
			
			glTranslatef(1f, 0f, -3.6875f)
			glRotatef(180f, 0f, 1f, 0f)
			ItemRenderer.renderItemIn2D(Tessellator.instance, icon.maxU, icon.minV, icon.minU, icon.maxV, icon.iconWidth, icon.iconHeight, 1f / 16f)
			
			
			if (type == INVENTORY) glDisable(GL_BLEND)
			glPopMatrix()
			
			mc.renderEngine.bindTexture(LibResourceLocations.mjolnirKitty)
		} else
			mc.renderEngine.bindTexture(LibResourceLocations.mjolnir)
		
		model.renderAll()
		
		glPopMatrix()
	}
	
	override fun handleRenderType(item: ItemStack?, type: ItemRenderType?) = true
	override fun shouldUseRenderHelper(type: ItemRenderType?, item: ItemStack?, helper: IItemRenderer.ItemRendererHelper?) = helper != IItemRenderer.ItemRendererHelper.BLOCK_3D
}