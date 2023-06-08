package alfheim.client.render.block

import alexsocol.asjlib.glScaled
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.tile.TileAnyavil
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.world.IBlockAccess
import org.lwjgl.opengl.GL11.*

object RenderBlockAnyavil: ISimpleBlockRenderingHandler {
	
	override fun renderInventoryBlock(block: Block, metadata: Int, modelID: Int, renderer: RenderBlocks) {
		glPushMatrix()
		glRotatef(-90f, 0f, 1f, 0f)
		glTranslated(-0.45, -0.6, -0.45)
		glScaled(0.95)
		
		TileEntityRendererDispatcher.instance.renderTileEntityAt(TileAnyavil(), 0.0, 0.0, 0.0, 0f)
		glPopMatrix()
	}
	
	override fun renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderer: RenderBlocks) = false
	override fun shouldRender3DInInventory(modelId: Int) = true
	override fun getRenderId() = LibRenderIDs.idAnyavil
}