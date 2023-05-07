package alfheim.common.block

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.extendables.block.BlockModContainerMeta
import alfheim.api.ModInfo
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.tile.TilePowerStone
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.common.core.helper.ItemNBTHelper
import java.util.*

class BlockPowerStone: BlockModContainerMeta(Material.rock, 5, ModInfo.MODID, "PowerStone", AlfheimTab), ILexiconable {
	
	init {
		setBlockUnbreakable()
	}
	
	override fun onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, placer: EntityLivingBase?, stack: ItemStack?) {
		val player = placer as? EntityPlayer ?: return
		if (!player.capabilities.isCreativeMode) return
		
		val te = world.getTileEntity(x, y, z) as? TilePowerStone ?: return
		te.readCustomNBT(ItemNBTHelper.getNBT(stack))
		te.lock(x, y, z, world.provider.dimensionId)
		
		if (world.isRemote)
			return
		
		world.markBlockForUpdate(x, y, z)
		ASJUtilities.dispatchTEToNearbyPlayers(te)
	}
	
	override fun getItemDropped(meta: Int, random: Random?, fortune: Int) = null
	override fun quantityDropped(random: Random?) = 0
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float) = (world.getTileEntity(x, y, z) as? TilePowerStone)?.onBlockActivated(player) ?: false
	override fun createNewTileEntity(world: World?, meta: Int) = TilePowerStone()
	override fun isOpaqueCube() = AlfheimConfigHandler.minimalGraphics
	override fun renderAsNormalBlock() = AlfheimConfigHandler.minimalGraphics
	override fun getRenderType() = if (AlfheimConfigHandler.minimalGraphics) 0 else LibRenderIDs.idPowerStone
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = AlfheimLexiconData.shrines
	override fun getIcon(side: Int, meta: Int) = icons.getOrNull(meta) ?: blockIcon
	
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = if (!AlfheimConfigHandler.minimalGraphics) emptyArray()
		else Array(subtypes) { if (it == 0) AlfheimBlocks.manaInfuser.getIcon(0, 2) else reg.registerIcon("$modid:$folder$name$it") }
	}
}