package alfheim.common.block

import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.AlfheimCore
import alfheim.api.ModInfo
import cpw.mods.fml.relauncher.*
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.*
import net.minecraft.util.IIcon

class BlockElvenSandstone: BlockModMeta(Material.rock, 5, ModInfo.MODID) {
	
	lateinit var sides: Array<IIcon>
	lateinit var top: IIcon
	lateinit var bottom: IIcon
	
	init {
		setBlockName("ElvenSandstone")
		setCreativeTab(AlfheimCore.alfheimTab)
		setHardness(0.8F)
		setStepSound(soundTypePiston)
	}
	
	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int): IIcon {
		if (meta == names.size + 1) return bottom
		if (meta == names.size) return top
		
		val id = if (meta in 0 until names.size) meta else 0
		
		return when (side) {
			0       -> bottom
			1       -> top
			in 2..5 -> sides[id]
			else    -> top
		}
	}
	
	@SideOnly(Side.CLIENT)
	override fun getSubBlocks(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		(0 until subtypes).mapTo(list) { ItemStack(item, 1, it) }
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) {
		sides = Array(names.size) {
			reg.registerIcon("${ModInfo.MODID}:ElvenSandstone${names[it]}")
		}
		
		top = reg.registerIcon("${ModInfo.MODID}:ElvenSandstoneTop")
		bottom = reg.registerIcon("${ModInfo.MODID}:ElvenSandstoneBottom")
	}
	
	val names = arrayOf("Normal", "Carved", "Smooth")
}
