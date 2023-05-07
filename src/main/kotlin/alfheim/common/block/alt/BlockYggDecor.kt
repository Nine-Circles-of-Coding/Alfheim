package alfheim.common.block.alt

import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.util.IIcon
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.util.ForgeDirection

class BlockYggDecor: BlockModMeta(Material.wood, 3, ModInfo.MODID, "Wisdomwood", AlfheimTab, -1f, resist = Float.MAX_VALUE, folder = "decor/") {
	
	lateinit var topIcon: IIcon
	
	override fun registerBlockIcons(reg: IIconRegister) {
		super.registerBlockIcons(reg)
		
		topIcon = reg.registerIcon("$modid:$folder${name}1Top")
	}
	
	override fun getIcon(side: Int, meta: Int) = if (meta == 1 && side in 0..1) topIcon else super.getIcon(side, meta)
	
	override fun getFireSpreadSpeed(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = 0
	override fun isFlammable(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = false
}
