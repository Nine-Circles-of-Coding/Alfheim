package alfheim.common.block.schema

import alfheim.common.block.base.BlockMod
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import vazkii.botania.api.lexicon.ILexiconable
import java.util.*

/**
 * Created by l0nekitsune on 1/3/16.
 */
class BlockSchemaFiller: BlockMod(Material.wood), ILexiconable {
	
	init {
		//        val size = 0.1875f
		//        this.setBlockBounds(size, size, size, 1f - size, 1f - size, 1f - size)
		setBlockName("schemaFiller")
		setBlockUnbreakable()
	}
	
	override fun getItemDropped(meta: Int, rand: Random, fortune: Int) = null
	
	override fun isOpaqueCube() = false
	
	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = null
}
