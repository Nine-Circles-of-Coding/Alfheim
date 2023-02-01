package alfheim.common.block.tile

import alexsocol.asjlib.*
import alfheim.common.block.AlfheimBlocks
import net.minecraft.block.Block
import net.minecraft.tileentity.TileEntity
import vazkii.botania.api.lexicon.multiblock.Multiblock
import vazkii.botania.api.lexicon.multiblock.MultiblockSet
import vazkii.botania.common.block.ModBlocks

class TileSpire: TileEntity() {
	
	override fun canUpdate() = false
	
	companion object {
		
		fun makeMultiblockSet(): MultiblockSet {
			val mb = Multiblock()
			
			for (ele in SchemaUtils.parse(TileAlfheimPylon.schema)) {
				val block = Block.getBlockFromName(ele.block) ?: continue
				
				for (loc in ele.location) mb.addComponent(loc.x, loc.y + 3, loc.z, block, loc.meta)
			}
			
			mb.addComponent(0, 1, 0, ModBlocks.pool, 0)
			mb.addComponent(0, 3, 0, AlfheimBlocks.alfheimPylon, 3)
			
			return MultiblockSet(Array(4) { mb })
		}
	}
}