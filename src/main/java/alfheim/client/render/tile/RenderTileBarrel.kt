package alfheim.client.render.tile

import alexsocol.asjlib.mc
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.block.ModelBarrel
import alfheim.common.block.tile.TileBarrel
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import org.lwjgl.opengl.GL11.*

object RenderTileBarrel: TileEntitySpecialRenderer() {
	
	override fun renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, partialTicks: Float) {
		if (tile !is TileBarrel) return
		
		val f5 = 0.0625f
		
		glPushMatrix()
		glTranslated(x, y, z)
		glRotatef(180f, 1f, 0f, 0f)
		glTranslated(0.5, -1.0, -0.5)
		glRotatef(90f, 0f, 1f, 0f)
		
		mc.renderEngine.bindTexture(LibResourceLocations.barrel)
		ModelBarrel.render(f5)
		
		if (tile.closed)
			ModelBarrel.renderCover(f5)
		
		glTranslatef(0f, (tile.wineLevel - 2) / -16f - 0.01f, 0f)
		
		if (tile.wineStage <= TileBarrel.WINE_STAGE_MASH) {
			if (tile.wineType == TileBarrel.WINE_TYPE_RED)
				ModelBarrel.redMash.render(f5)
			
			if (tile.wineType == TileBarrel.WINE_TYPE_WHITE)
				ModelBarrel.greenMash.render(f5)
		}
		
		if (tile.wineStage >= TileBarrel.WINE_STAGE_MASH) {
			glEnable(GL_BLEND)
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
			glTranslatef(0f, -1 / 64f, 0f)
			
			val a = when (tile.wineStage) {
				TileBarrel.WINE_STAGE_MASH   -> 0.5f
				TileBarrel.WINE_STAGE_LIQUID -> 1f
				TileBarrel.WINE_STAGE_READY  -> 0.9f
				else                         -> -1f
			}
			
			glColor4f(1f, 1f, 1f, a)
			
			if (tile.wineType == TileBarrel.WINE_TYPE_RED)
				ModelBarrel.redWine.render(f5)
			
			if (tile.wineType == TileBarrel.WINE_TYPE_WHITE)
				ModelBarrel.greenWine.render(f5)
			
			glDisable(GL_BLEND)
			
			glColor4f(1f, 1f, 1f, 1f)
		}
		
		glPopMatrix()
	}
}