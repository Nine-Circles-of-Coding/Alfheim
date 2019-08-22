package alfheim.common.block

import alexsocol.asjlib.ASJUtilities
import alfheim.AlfheimCore
import alfheim.api.ModInfo
import alfheim.common.achievement.AlfheimAchievements
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileAlfheimPortal
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.helper.*
import alfheim.common.item.AlfheimItems
import alfheim.common.item.AlfheimItems.ElvenResourcesMetas
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.*
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.*
import vazkii.botania.api.lexicon.ILexiconable

class BlockAlfheimPortal: BlockContainerMod(Material.wood), ILexiconable {
	
	init {
		setBlockName("AlfheimPortal")
		setBlockTextureName(ModInfo.MODID + ":AlfheimPortal")
		setCreativeTab(AlfheimCore.alfheimTab)
		setHardness(10.0f)
		setResistance(600.0f)
		setStepSound(Block.soundTypeWood)
	}
	
	override fun loadTextures(map: TextureMap) {
		textures = Array(2) { InterpolatedIconHelper.forBlock(map, this, it) }
	}
	
	override fun registerBlockIcons(reg: IIconRegister) {
		blockIcon = IconHelper.forBlock(reg, this)
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val newMeta = (world.getTileEntity(x, y, z) as TileAlfheimPortal).validMetadata
		if (newMeta == 0) return false
		
		if (world.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim) {
			if (world.getBlockMetadata(x, y, z) == 0 && player.currentEquippedItem?.item === AlfheimItems.elvenResource && player.currentEquippedItem.itemDamage == ElvenResourcesMetas.InterdimensionalGatewayCore) {
				ASJUtilities.consumeItemStack(player.inventory, ItemStack(AlfheimItems.elvenResource, 1, ElvenResourcesMetas.InterdimensionalGatewayCore))
			} else
				return false
		}
		
		val did = (world.getTileEntity(x, y, z) as TileAlfheimPortal).onWanded(newMeta)
		if (did) player.addStat(AlfheimAchievements.alfheim, 1)
		return did
	}
	
	override fun isInterpolated() = true
	override fun getIcon(side: Int, meta: Int) = if (meta == 1) textures[0] else blockIcon
	override fun createNewTileEntity(world: World, meta: Int) = TileAlfheimPortal()
	override fun getLightValue(world: IBlockAccess, x: Int, y: Int, z: Int) = if (world.getBlockMetadata(x, y, z) == 0) 0 else 15
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = AlfheimLexiconData.portal
	
	override fun breakBlock(world: World, x: Int, y: Int, z: Int, block: Block?, meta: Int) {
		if (world.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim && meta != 0) world.spawnEntityInWorld(EntityItem(world, x + 0.5, y + 0.5, z + 0.5, ItemStack(AlfheimItems.elvenResource, 1, ElvenResourcesMetas.InterdimensionalGatewayCore)))
		super.breakBlock(world, x, y, z, block, meta)
	}
	
	companion object {
		lateinit var textures: Array<IIcon?>
	}
}