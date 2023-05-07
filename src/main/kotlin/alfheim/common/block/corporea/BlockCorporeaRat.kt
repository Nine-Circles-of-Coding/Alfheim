package alfheim.common.block.corporea

import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.corporea.TileCorporeaRat
import alfheim.common.core.util.AlfheimTab
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import vazkii.botania.api.lexicon.ILexiconable

class BlockCorporeaRat: BlockContainerMod(Material.iron), ILexiconable {
	
	init {
		setBlockName("CorporeaRatBase")
		setHardness(5f)
	}
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileCorporeaRat()
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.corpQuandex
}