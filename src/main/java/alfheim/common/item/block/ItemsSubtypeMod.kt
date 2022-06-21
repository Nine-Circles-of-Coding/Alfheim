package alfheim.common.item.block

import alexsocol.asjlib.meta
import alfheim.api.ModInfo
import alfheim.common.block.base.*
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.util.StatCollector

open class ItemSubtypedBlockMod(block: Block): ItemBlockWithMetadata(block, block) {
	
	override fun getMetadata(meta: Int): Int {
		if (field_150939_a is BlockLeavesMod) return meta or (field_150939_a as BlockLeavesMod).decayBit()
		return meta
	}
	
	override fun getUnlocalizedNameInefficiently(par1ItemStack: ItemStack) =
		super.getUnlocalizedNameInefficiently(par1ItemStack).replace("tile.", "tile.${ModInfo.MODID}:").replace("\\d+$".toRegex(), "")
	
	fun addStringToTooltip(s: String, tooltip: MutableList<Any?>?) {
		tooltip!!.add(s.replace("&".toRegex(), "\u00a7"))
	}
	
	override fun addInformation(stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>?, par4: Boolean) {
		if (stack == null) return
		addStringToTooltip("&7" + StatCollector.translateToLocal("misc.${ModInfo.MODID}.color." + stack.meta) + "&r", list)
	}
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN") // fucking reflection
class ItemUniqueSubtypedBlockMod(block: Block, val subtypes: Integer): ItemBlockWithMetadata(block, block) {
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack) =
		super.getUnlocalizedNameInefficiently(stack).replace("tile.", "tile.${ModInfo.MODID}:") + stack.meta % subtypes.toInt()
}
