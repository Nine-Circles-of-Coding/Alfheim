package alfheim.common.block.schema

import alfheim.client.core.helper.IconHelper
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileSchemaController
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.World
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.api.wand.IWandable
import java.util.*

class BlockSchemaContoller: BlockContainerMod(Material.wood), IWandable, ILexiconable {
	
	lateinit var icon1: IIcon
	lateinit var icon2: IIcon
	lateinit var icon3: IIcon
	
	init {
		setBlockName("schemaController")
		setBlockUnbreakable()
	}
	
	override fun getItemDropped(meta: Int, rand: Random, fortune: Int) = null
	
	override fun getIcon(side: Int, meta: Int) =
		when (side) {
			0, 1 -> icon1
			2, 3 -> icon2
			else -> icon3
		}
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileSchemaController()
	
	override fun registerBlockIcons(reg: IIconRegister) {
		icon1 = IconHelper.forName(reg, "schema1")
		icon2 = IconHelper.forName(reg, "schema2")
		icon3 = IconHelper.forName(reg, "schema3")
	}
	
	override fun onUsedByWand(player: EntityPlayer, stack: ItemStack, world: World, x: Int, y: Int, z: Int, side: Int): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileSchemaController ?: return false
		tile.blockActivated(player)
		return true
	}
	
	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = null
}
