package alfheim.client.render.block

import alexsocol.asjlib.render.RenderGlowingLayerBlock
import alfheim.api.lib.LibRenderIDs
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.world.IBlockAccess

object RenderBlockDomainLobby: RenderGlowingLayerBlock() {
	
	override fun renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderer: RenderBlocks): Boolean {
		when (world.getBlockMetadata(x, y, z)) {
			1 -> return super.renderWorldBlock(world, x, y, z, block, modelId, renderer)
			2 -> renderer.setRenderBounds(0.0, 0.0, 0.25, 1.0, 1.0, 0.75)
		}
		
		renderer.renderStandardBlock(block, x, y, z)
		return true
	}
	
	override fun renderInventoryBlock(block: Block, meta: Int, modelID: Int, renderer: RenderBlocks) {
		if (meta == 1) return super.renderInventoryBlock(block, meta, modelID, renderer)
		
		if (meta == 2) renderer.setRenderBounds(0.0, 0.0, 0.25, 1.0, 1.0, 0.75)
		drawFaces(renderer, block) { block.getIcon(it, meta) }
	}
	
	override fun shouldRender3DInInventory(modelId: Int) = true
	override fun getRenderId() = LibRenderIDs.idDomainDoor
}
