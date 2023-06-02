package alfheim.common.block

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.client.core.helper.*
import alfheim.common.core.util.AlfheimTab
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.util.IIcon
import net.minecraftforge.client.event.TextureStitchEvent

class BlockRealmPowerCollector: BlockModMeta(Material.wood, 4, ModInfo.MODID, "RealmPowerCollector", AlfheimTab, 10f, "axe", 0) {
	
	var iconMuspelCoreFace: IIcon? = null
	var iconNifleCoreFace: IIcon? = null
	var iconMuspelSide: IIcon? = null
	var iconNifleSide: IIcon? = null
	var iconMuspelTop: IIcon? = null
	var iconNifleTop: IIcon? = null
	
	lateinit var iconMuspelFrameFace: IIcon
	lateinit var iconNifleFrameFace: IIcon
	
	init {
		if (ASJUtilities.isClient)
			eventForge()
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType != 0) return
		
		iconMuspelCoreFace = InterpolatedIconHelper.forBlock(event.map, this, "MuspelCoreFace")
		iconMuspelSide = InterpolatedIconHelper.forBlock(event.map, this, "MuspelSide")
		iconMuspelTop = InterpolatedIconHelper.forBlock(event.map, this, "MuspelTop")
		iconNifleCoreFace = InterpolatedIconHelper.forBlock(event.map, this, "NifleCoreFace")
		iconNifleSide = InterpolatedIconHelper.forBlock(event.map, this, "NifleSide")
		iconNifleTop = InterpolatedIconHelper.forBlock(event.map, this, "NifleTop")
	}
	
	override fun registerBlockIcons(reg: IIconRegister) {
		iconMuspelFrameFace = IconHelper.forBlock(reg, this, "MuspelFrameFace")
		iconNifleFrameFace = IconHelper.forBlock(reg, this, "NifleFrameFace")
	}
	
	override fun getIcon(side: Int, meta: Int): IIcon {
		val icon = when (meta) {
			0 -> if (side == 0 || side == 1) iconMuspelTop else if (side == 2 || side == 3) iconMuspelCoreFace else iconMuspelSide
			1 -> if (side == 0 || side == 1) iconNifleTop else if (side == 2 || side == 3) iconNifleCoreFace else iconNifleSide
			2 -> if (side == 0 || side == 1) iconMuspelTop else if (side == 2 || side == 3) iconMuspelFrameFace else iconMuspelSide
			3 -> if (side == 0 || side == 1) iconNifleTop else if (side == 2 || side == 3) iconNifleFrameFace else iconNifleSide
			else -> null
		}
		
		return icon ?: if (meta % 2 == 0) iconMuspelFrameFace else iconNifleFrameFace
	}
}
