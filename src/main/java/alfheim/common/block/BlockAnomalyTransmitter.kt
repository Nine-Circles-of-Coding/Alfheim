package alfheim.common.block

import alexsocol.asjlib.spawn
import alfheim.api.ModInfo
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileAnomalyTransmitter
import net.minecraft.block.material.Material
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.api.wand.IWandable

class BlockAnomalyTransmitter: BlockContainerMod(Material.iron), IWandable {
	
	init {
//		setBlockName("AnomalyTransmitter")
		setBlockTextureName("${ModInfo.MODID}:AnomalyTransmitter")
		setBlockUnbreakable()
	}
	
	override fun canPlaceBlockAt(world: World, x: Int, y: Int, z: Int): Boolean {
		return ForgeDirection.VALID_DIRECTIONS.any { d -> world.getBlock(x + d.offsetX, y + d.offsetY, z + d.offsetZ) === AlfheimBlocks.anomaly } && super.canPlaceBlockAt(world, x, y, z)
	}
	
	override fun onUsedByWand(player: EntityPlayer?, stack: ItemStack, world: World, x: Int, y: Int, z: Int, side: Int): Boolean {
		world.setBlockToAir(x, y, z)
		EntityItem(world, x + 0.5, y + 0.5, z + 0.5, ItemStack(this)).spawn()
		return true
	}
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileAnomalyTransmitter()
}
