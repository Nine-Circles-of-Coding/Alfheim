package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.*
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import baubles.api.BaubleType
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraftforge.client.event.RenderPlayerEvent
import org.lwjgl.opengl.GL11.*
import vazkii.botania.api.item.IBaubleRender
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.client.core.helper.IconHelper
import vazkii.botania.common.item.equipment.bauble.ItemBauble

open class ItemPendant(name: String): ItemBauble(name), IBaubleRender {
	
	lateinit var gemIcon: IIcon
	
	init {
		creativeTab = AlfheimTab
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		super.registerIcons(reg)
		registerGem(reg)
	}
	
	open fun registerGem(reg: IIconRegister) {
		gemIcon = IconHelper.forItem(reg, this, "Gem")
	}
	
	override fun getBaubleType(stack: ItemStack) = BaubleType.AMULET
	
	override fun onPlayerBaubleRender(stack: ItemStack, event: RenderPlayerEvent, type: IBaubleRender.RenderType) {
		if (type != IBaubleRender.RenderType.BODY) return
		
		mc.renderEngine.bindTexture(TextureMap.locationItemsTexture)
		IBaubleRender.Helper.rotateIfSneaking(event.entityPlayer)
		val armor = event.entityPlayer.getCurrentArmor(2) != null
		glPushMatrix()
		glRotatef(180f, 1f, 0f, 0f)
		glTranslated(-0.25, -0.4, if (armor) 0.21 else 0.14)
		glScaled(0.5)
		ItemRenderer.renderItemIn2D(Tessellator.instance, gemIcon.maxU, gemIcon.minV, gemIcon.minU, gemIcon.maxV, gemIcon.iconWidth, gemIcon.iconHeight, 1f / 32f)
		glPopMatrix()
	}
	
	companion object {
		
		fun canProtect(player: EntityPlayer, type: EnumPrimalWorldType, cost: Int): Boolean {
			val amulet = PlayerHandler.getPlayerBaubles(player)[0] ?: return false
			return (amulet.item === (if (type == EnumPrimalWorldType.NIFLHEIM) AlfheimItems.elfIcePendant else AlfheimItems.elfFirePendant) || amulet.item === AlfheimItems.aesirEmblem || amulet.item === AlfheimItems.ragnarokEmblem) && ManaItemHandler.requestManaExact(amulet, player, cost, true)
		}
		
		enum class EnumPrimalWorldType {
			MUSPELHEIM, NIFLHEIM
		}
	}
}