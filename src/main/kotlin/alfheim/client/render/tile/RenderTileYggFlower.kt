package alfheim.client.render.tile

import alexsocol.asjlib.mc
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.block.ModelYggFlower
import alfheim.common.block.tile.TileYggFlower
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import org.lwjgl.opengl.GL11.*

object RenderTileYggFlower: TileEntitySpecialRenderer() {
	
	override fun renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, ticks: Float) {
		glPushMatrix()
		ASJRenderHelper.setBlend()
		ASJRenderHelper.setGlow()
		ASJRenderHelper.setTwoside()
		
		glTranslated(x + 0.5, y + 0.0625, z + 0.5)
		glRotatef(180f, 0f, 0f, 1f)
		mc.renderEngine.bindTexture(LibResourceLocations.yggFlower)
		ModelYggFlower.render()
		
		if ((tile as? TileYggFlower)?.hasFruit == true) ModelYggFlower.renderFruit()
		
		ASJRenderHelper.discard()
		glPopMatrix()
	}
}
