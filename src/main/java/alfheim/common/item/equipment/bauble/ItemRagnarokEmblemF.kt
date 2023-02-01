package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.ModInfo
import alfheim.client.core.helper.IconHelper
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.equipment.bauble.faith.ItemRagnarokEmblem
import baubles.api.BaubleType
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.*
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.*
import net.minecraft.util.IIcon
import net.minecraftforge.client.event.RenderPlayerEvent
import org.lwjgl.opengl.GL11
import vazkii.botania.api.item.IBaubleRender
import vazkii.botania.common.item.equipment.bauble.ItemBauble

class ItemRagnarokEmblemF: ItemBauble("ragnarokEmblem"), IBaubleRender {
	
	init {
		creativeTab = AlfheimTab
	}
	
	override fun getSubItems(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>?) {
		if (ItemRagnarokEmblem.canSeeTruth(mc.thePlayer)) super.getSubItems(item, tab, list)
	}
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack) = if (ASJUtilities.isClient && !ItemRagnarokEmblem.canSeeTruth(mc.thePlayer)) AlfheimItems.aesirEmblem.getUnlocalizedNameInefficiently(stack)
		else super.getUnlocalizedNameInefficiently(stack).replace("item\\.botania:".toRegex(), "item.${ModInfo.MODID}:")
	
	override fun getItemStackDisplayName(stack: ItemStack) = if (ASJUtilities.isClient && !ItemRagnarokEmblem.canSeeTruth(mc.thePlayer)) AlfheimItems.aesirEmblem.getItemStackDisplayName(stack)
		else super.getItemStackDisplayName(stack).replace("&".toRegex(), "\u00a7")
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
		gemIcons = Array(2) { IconHelper.forItem(reg, this, "Render$it") }
	}
	
	override fun requiresMultipleRenderPasses() = true
	
	override fun getBaubleType(stack: ItemStack) = BaubleType.AMULET
	
	override fun onPlayerBaubleRender(stack: ItemStack?, event: RenderPlayerEvent, type: IBaubleRender.RenderType?) {
		if (type != IBaubleRender.RenderType.BODY) return
		
		val player = event.entityPlayer
		
		mc.renderEngine.bindTexture(TextureMap.locationItemsTexture)
		IBaubleRender.Helper.rotateIfSneaking(player)
		val armor = player.getCurrentArmor(2) != null
		GL11.glTranslatef(-15 / 64f, 0f, -1 * if (armor) 0.2F else 0.125F)
		glScalef(0.5f)
		
		for ((id, icon) in gemIcons.withIndex()) {
			if (id != 0) ASJRenderHelper.setGlow()
			ItemRenderer.renderItemIn2D(Tessellator.instance, icon.maxU, icon.maxV, icon.minU, icon.minV, icon.iconWidth, icon.iconHeight, 1F / 32F)
			if (id != 0) ASJRenderHelper.discard()
		}
	}
	
	override fun hasPhantomInk(stack: ItemStack?) = false
	override fun setPhantomInk(stack: ItemStack?, ink: Boolean) = Unit
	
	companion object {
		
		lateinit var gemIcons: Array<IIcon>
		
	}
}
