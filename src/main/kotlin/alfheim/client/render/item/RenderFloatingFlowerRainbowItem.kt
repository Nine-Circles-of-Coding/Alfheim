package alfheim.client.render.item

import alexsocol.asjlib.*
import alfheim.common.block.tile.TileFloatingFlowerRainbow
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer.ItemRenderType
import org.lwjgl.opengl.GL11
import vazkii.botania.client.render.item.RenderFloatingFlowerItem
import vazkii.botania.common.block.tile.TileFloatingFlower

object RenderFloatingFlowerRainbowItem: RenderFloatingFlowerItem() {
	
	override fun renderItem(type: ItemRenderType?, stack: ItemStack, vararg data: Any?) {
		GL11.glPushMatrix()
		if (type == ItemRenderType.ENTITY) glTranslated(-0.65)
		GL11.glEnable(GL11.GL_BLEND)
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
		val s = 1.4f
		GL11.glScalef(s, s, s)
		GL11.glRotatef(-5f, 1f, 0f, 0f)
		TileFloatingFlower.forcedStack = TileFloatingFlowerRainbow.getDisplayStack { stack.meta }
		TileEntityRendererDispatcher.instance.renderTileEntityAt(TileFloatingFlowerRainbow(), 0.0, 0.0, 0.0, 0.0f)
		GL11.glDisable(GL11.GL_BLEND)
		GL11.glPopMatrix()
	}
}
