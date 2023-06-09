package alfheim.common.item.block

import alexsocol.asjlib.ASJUtilities
import alfheim.api.ModInfo
import alfheim.common.block.*
import alfheim.common.block.tile.TileAnomaly
import alfheim.common.block.tile.TileAnomaly.Companion.TAG_SUBTILE_COUNT
import alfheim.common.block.tile.TileAnomaly.Companion.TAG_SUBTILE_MAIN
import alfheim.common.block.tile.TileAnomaly.Companion.TAG_SUBTILE_NAME
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.util.IIcon
import net.minecraft.world.World
import vazkii.botania.api.lexicon.IRecipeKeyProvider
import vazkii.botania.common.core.helper.ItemNBTHelper.*

class ItemBlockAnomaly(block: Block): ItemBlock(block), IRecipeKeyProvider {
	
	init {
		maxStackSize = 1
		setTextureName(ModInfo.MODID + ":undefined")
	}
	
	override fun getIcon(stack: ItemStack, pass: Int): IIcon {
		return BlockAnomaly.iconUndefined
	}
	
	override fun getUnlocalizedName(stack: ItemStack?): String {
		return "tile.Anomaly." + getString(stack, TAG_SUBTILE_MAIN, TYPE_UNDEFINED)
	}
	
	override fun getMetadata(meta: Int): Int {
		return meta
	}
	
	override fun placeBlockAt(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, metadata: Int): Boolean {
		if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) return false
		
		if (!player.capabilities.isCreativeMode) return true
		
		val te = world.getTileEntity(x, y, z) as? TileAnomaly ?: return true
		te.readCustomNBT(getNBT(stack))
		te.lock(x, y, z, world.provider.dimensionId)
		
		if (world.isRemote)
			return true
		
		world.markBlockForUpdate(x, y, z)
		ASJUtilities.dispatchTEToNearbyPlayers(te)
		
		return true
	}
	
	override fun getKey(stack: ItemStack) = "${stack.unlocalizedName}~${getType(stack)}"
	
	companion object {
		
		const val TYPE_UNDEFINED = "undefined"
		
		fun getType(stack: ItemStack): String {
			return if (detectNBT(stack)) getString(stack, TAG_SUBTILE_MAIN, TYPE_UNDEFINED) else TYPE_UNDEFINED
		}
		
		fun ofType(type: String): ItemStack {
			return ofType(ItemStack(AlfheimBlocks.anomaly), type)
		}
		
		fun ofType(stack: ItemStack, type: String?): ItemStack {
			var t = type
			if (type.isNullOrEmpty()) t = TYPE_UNDEFINED
			setString(stack, TAG_SUBTILE_MAIN, t)
			setInt(stack, TAG_SUBTILE_COUNT, 1)
			setString(stack, TAG_SUBTILE_NAME + "1", t)
			
			return stack
		}
	}
}
