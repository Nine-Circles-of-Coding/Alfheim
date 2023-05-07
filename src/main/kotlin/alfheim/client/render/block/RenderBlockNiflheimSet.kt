package alfheim.client.render.block

import alfheim.api.lib.LibRenderIDs
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.IIcon
import net.minecraft.world.IBlockAccess
import org.lwjgl.opengl.GL11

object RenderBlockNiflheimSet: ISimpleBlockRenderingHandler {
	
	override fun renderInventoryBlock(block: Block, meta: Int, modelId: Int, renderer: RenderBlocks) {
		renderStandardInvBlock(renderer, block, meta)
	}
	
	fun renderStandardInvBlock(renderblocks: RenderBlocks, block: Block, meta: Int) {
		val tessellator = Tessellator.instance
		
		val wasnt = !tessellator.isDrawing
		
		GL11.glTranslatef(-0.5f, -0.5f, -0.5f)
		if (wasnt) tessellator.startDrawingQuads()
		tessellator.setNormal(0.0f, -1.0f, 0.0f)
		renderblocks.renderFaceYNeg(block, 0.0, 0.0, 0.0, getIconSafe(block.getIcon(0, meta)))
		tessellator.draw()
		tessellator.startDrawingQuads()
		tessellator.setNormal(0.0f, 1.0f, 0.0f)
		renderblocks.renderFaceYPos(block, 0.0, 0.0, 0.0, getIconSafe(block.getIcon(1, meta)))
		tessellator.draw()
		tessellator.startDrawingQuads()
		tessellator.setNormal(0.0f, 0.0f, -1.0f)
		renderblocks.renderFaceZNeg(block, 0.0, 0.0, 0.0, getIconSafe(block.getIcon(2, meta)))
		tessellator.draw()
		tessellator.startDrawingQuads()
		tessellator.setNormal(0.0f, 0.0f, 1.0f)
		renderblocks.renderFaceZPos(block, 0.0, 0.0, 0.0, getIconSafe(block.getIcon(3, meta)))
		tessellator.draw()
		tessellator.startDrawingQuads()
		tessellator.setNormal(-1.0f, 0.0f, 0.0f)
		renderblocks.renderFaceXNeg(block, 0.0, 0.0, 0.0, getIconSafe(block.getIcon(4, meta)))
		tessellator.draw()
		tessellator.startDrawingQuads()
		tessellator.setNormal(1.0f, 0.0f, 0.0f)
		renderblocks.renderFaceXPos(block, 0.0, 0.0, 0.0, getIconSafe(block.getIcon(5, meta)))
		if (wasnt) tessellator.draw()
		GL11.glTranslatef(0.5f, 0.5f, 0.5f)
	}
	
	fun getIconSafe(icon: IIcon?) = icon ?: (Minecraft.getMinecraft().textureManager.getTexture(TextureMap.locationBlocksTexture) as TextureMap).getAtlasSprite("missingno")
	
	override fun renderWorldBlock(world: IBlockAccess?, x: Int, y: Int, z: Int, block: Block?, modelId: Int, renderer: RenderBlocks): Boolean {
		val l = renderer.blockAccess.getBlockMetadata(x, y, z)
		
		if (l == 8 || l == 11) {
			renderer.uvRotateEast = 1
			renderer.uvRotateWest = 1
			renderer.uvRotateTop = 1
			renderer.uvRotateBottom = 1
		} else if (l == 9 || l == 12) {
			renderer.uvRotateSouth = 1
			renderer.uvRotateNorth = 1
		}
		
		val flag = renderer.renderStandardBlock(block, x, y, z)
		
		renderer.uvRotateSouth = 0
		renderer.uvRotateEast = 0
		renderer.uvRotateWest = 0
		renderer.uvRotateNorth = 0
		renderer.uvRotateTop = 0
		renderer.uvRotateBottom = 0
		
		return flag
	}
	
	override fun shouldRender3DInInventory(modelId: Int) = true
	
	override fun getRenderId() = LibRenderIDs.idNiflheim
}
