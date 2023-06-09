package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.api.item.equipment.bauble.IManaDiscountBauble
import alfheim.client.core.helper.IconHelper
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.*
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler.timesDied
import alfheim.common.core.util.*
import alfheim.common.item.AlfheimItems
import alfheim.common.item.equipment.bauble.faith.IFaithHandler.Companion.getFaithHandler
import alfheim.common.item.equipment.bauble.faith.IFaithHandler.FaithBauble.EMBLEM
import alfheim.common.potion.PotionEternity
import baubles.api.BaubleType
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.relauncher.*
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.*
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.potion.Potion
import net.minecraft.util.*
import net.minecraftforge.client.event.RenderPlayerEvent
import org.lwjgl.opengl.GL11.*
import vazkii.botania.api.item.IBaubleRender
import vazkii.botania.api.mana.*
import vazkii.botania.common.item.equipment.bauble.ItemBauble

class ItemPriestEmblem: ItemBauble("priestEmblem"), IBaubleRender, IManaUsingItem, IManaDiscountBauble {
	
	companion object {
		
		const val COST = 2
		const val TYPES = 6
		
		lateinit var icons: Array<IIcon>
		lateinit var baubleIcons: Array<IIcon>
		
		fun getEmblem(meta: Int, player: EntityPlayer?): ItemStack? {
			val stack = PlayerHandler.getPlayerBaubles(player ?: return null)[0] ?: return null
			return if ((stack.item === AlfheimItems.priestEmblem && (meta == -1 || stack.meta == meta) || stack.item == AlfheimItems.aesirEmblem) && isActive(stack)) stack else null
		}
		
		fun isActive(stack: ItemStack) = ItemNBTHelper.getBoolean(stack, "active", false)
		
		fun setActive(stack: ItemStack, state: Boolean) = ItemNBTHelper.setBoolean(stack, "active", state)
		
		fun isDangerous(stack: ItemStack) = ItemNBTHelper.getBoolean(stack, "dangerous", false)
		
		fun setDangerous(stack: ItemStack, state: Boolean) = ItemNBTHelper.setBoolean(stack, "dangerous", state)
	}
	
	init {
		creativeTab = AlfheimTab
		setHasSubtypes(true)
	}
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack) =
		super.getUnlocalizedNameInefficiently(stack).replace("item\\.botania:".toRegex(), "item.${ModInfo.MODID}:")
	
	override fun getItemStackDisplayName(stack: ItemStack) =
		super.getItemStackDisplayName(stack).replace("&".toRegex(), "\u00a7")
	
	override fun registerIcons(reg: IIconRegister) {
		icons = Array(TYPES) { IconHelper.forItem(reg, this, it) }
		baubleIcons = Array(TYPES) { IconHelper.forItem(reg, this, "Render$it") }
	}
	
	override fun getIconFromDamage(meta: Int) = icons.safeGet(meta)
	
	fun getBaubleIconFromDamage(meta: Int) = baubleIcons.safeGet(meta)
	
	override fun getSubItems(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (i in 0 until TYPES)
			list.add(ItemStack(item, 1, i))
	}
	
	override fun addInformation(stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, adv: Boolean) {
		if (stack == null || player == null) return
		if (!GuiScreen.isShiftKeyDown() && !player.capabilities.isCreativeMode) {
			if (player.health <= 6f)
				this.addStringToTooltip(StatCollector.translateToLocal("misc.${ModInfo.MODID}.healthDanger"), list)
			else
				this.addStringToTooltip(StatCollector.translateToLocal("misc.${ModInfo.MODID}.healthWarning"), list)
		}
		super.addInformation(stack, player, list, adv)
	}
	
	override fun addHiddenTooltip(stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, adv: Boolean) {
		if (stack == null || player == null) return
		if (!player.capabilities.isCreativeMode) {
			if (player.health <= 6f)
				addStringToTooltip(StatCollector.translateToLocal("misc.${ModInfo.MODID}.crisisOfFaith"), list)
			else
				addStringToTooltip(StatCollector.translateToLocal("misc.${ModInfo.MODID}.lackOfFaith"), list)
		}
		super.addHiddenTooltip(stack, player, list, adv)
	}
	
	fun addStringToTooltip(s: String, tooltip: MutableList<Any?>) {
		tooltip.add(s.replace("&".toRegex(), "\u00a7"))
	}
	
	override fun getBaubleType(stack: ItemStack) = BaubleType.AMULET
	
	override fun getUnlocalizedName(stack: ItemStack) = super.getUnlocalizedName(stack) + stack.meta
	
	override fun canEquip(stack: ItemStack, player: EntityLivingBase): Boolean {
		if (player !is EntityPlayer) return false
		
		return if (RagnarokHandler.ragnarok) {
			player.timesDied < 5
		} else true
	}
	
	override fun onEquippedOrLoadedIntoWorld(stack: ItemStack, player: EntityLivingBase) {
		setDangerous(stack, false)
		
		if (player is EntityPlayer)
			getFaithHandler(stack).onEquipped(stack, player, EMBLEM)
	}
	
	override fun onWornTick(stack: ItemStack, player: EntityLivingBase) {
		if (player !is EntityPlayer) return
		
		if (player.timesDied >= 5) {
			PlayerHandler.getPlayerBaubles(player)[0] = null
			
			if (!player.inventory.addItemStackToInventory(stack))
				player.dropPlayerItemWithRandomChoice(stack, false)
			
			return
		}
		
		getFaithHandler(stack).onWornTick(stack, player, EMBLEM)
		
		if (player.ticksExisted % 10 != 0) return
		val flag: Boolean
		setActive(stack, ManaItemHandler.requestManaExact(stack, player, COST, true).also { flag = it })
		setDangerous(stack, true)
		
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.EMBLEM_ACTIVATION, player.dimension, player.entityId.D, if (flag) 1.0 else 0.0)
	}
	
	override fun canUnequip(stack: ItemStack?, player: EntityLivingBase?): Boolean {
		return !RagnarokHandler.ragnarok
	}
	
	override fun onUnequipped(stack: ItemStack, player: EntityLivingBase) {
		if (ASJUtilities.isClient) return
		
		if (!(!isDangerous(stack) || (player is EntityPlayer && player.capabilities.isCreativeMode))) {
			player.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDEternity, 150, PotionEternity.ATTACK or PotionEternity.IRREMOVABLE))
			player.addPotionEffect(PotionEffectU(Potion.blindness.id, 150))
			player.addPotionEffect(PotionEffectU(Potion.confusion.id, 150, 2))
			player.addPotionEffect(PotionEffectU(Potion.moveSlowdown.id, 300, 2))
			player.addPotionEffect(PotionEffectU(Potion.weakness.id, 300, 2))
		}
		setDangerous(stack, false)
		
		if (player is EntityPlayer)
			getFaithHandler(stack).onUnequipped(stack, player, EMBLEM)
	}
	
	override fun usesMana(stack: ItemStack) = true
	
	@SideOnly(Side.CLIENT)
	override fun onPlayerBaubleRender(stack: ItemStack, event: RenderPlayerEvent, type: IBaubleRender.RenderType) {
		if (type == IBaubleRender.RenderType.BODY) {
			val player = event.entityPlayer
			
			if (VisualEffectHandlerClient.activeEmblems.getOrDefault(player.entityId, false))
				if (!(player === mc.thePlayer && mc.gameSettings.thirdPersonView == 0))
					getFaithHandler(stack).doParticles(stack, player)
			
			mc.renderEngine.bindTexture(TextureMap.locationItemsTexture)
			IBaubleRender.Helper.rotateIfSneaking(player)
			val armor = player.getCurrentArmor(2) != null
			glRotatef(180F, 1F, 0F, 0F)
			glTranslatef(-0.26F, -0.4F, if (armor) 0.21F else 0.15F)
			glScaled(0.5)
			
			val icon = getBaubleIconFromDamage(stack.meta)
			ItemRenderer.renderItemIn2D(Tessellator.instance, icon.maxU, icon.minV, icon.minU, icon.maxV, icon.iconWidth, icon.iconHeight, 1F / 32F)
		}
	}
	
	override fun getDiscount(stack: ItemStack, slot: Int, player: EntityPlayer) = if (stack.meta == 5) 0.1f else 0f
}
