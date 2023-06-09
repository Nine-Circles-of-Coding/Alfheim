package alfheim.common.item

import alexsocol.asjlib.*
import alfheim.common.entity.EntityThrownPotion
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.potion.Potion
import net.minecraft.util.*
import net.minecraft.world.World
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.brew.*
import vazkii.botania.client.core.helper.IconHelper
import vazkii.botania.common.Botania
import vazkii.botania.common.core.helper.ItemNBTHelper
import java.awt.Color
import kotlin.math.*

class ItemSplashPotion: ItemMod("splashPotion"), IBrewItem, IBrewContainer {
	
	lateinit var itemIconFluid: IIcon
	
	init {
		maxStackSize = 1
	}
	
	override fun getSubItems(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (brew in BotaniaAPI.brewMap.keys)
			list.add(getItemForBrew(BotaniaAPI.brewMap[brew] as Brew, ItemStack(this)))
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (!world.isRemote) {
			EntityThrownPotion(player, stack).spawn(world)
			
			stack.stackSize--
		}
		
		return stack
	}
	
	fun getColor(stack: ItemStack?): Int {
		if (stack != null) {
			val color = Color(getBrew(stack).getColor(stack))
			val add = (sin(Botania.proxy.worldElapsedTicks.D * 0.1) * 16.0).I
			val r = max(0, min(255, color.red + add))
			val g = max(0, min(255, color.green + add))
			val b = max(0, min(255, color.blue + add))
			return (r shl 16) or (g shl 8) or b
		}
		
		return 0xFFFFFF
	}
	
	override fun getColorFromItemStack(stack: ItemStack?, pass: Int): Int {
		return if (pass == 0) {
			0xCCCCCFF
		} else getColor(stack)
	}
	
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forName(reg, "vial" + "0")
		itemIconFluid = IconHelper.forName(reg, "vial" + "1_0")
	}
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer?, list: MutableList<Any?>, adv: Boolean) {
		val brew = getBrew(stack)
		addStringToTooltip("${EnumChatFormatting.DARK_PURPLE}${StatCollector.translateToLocalFormatted("botaniamisc.brewOf", StatCollector.translateToLocal(brew.getUnlocalizedName(stack)))}", list)
		
		for (effect in brew.getPotionEffects(stack)) {
			val potion = Potion.potionTypes[effect.potionID]
			val format = if (potion.isBadEffect) EnumChatFormatting.RED else EnumChatFormatting.GRAY
			addStringToTooltip("" + format + StatCollector.translateToLocal(effect.effectName) + (if (effect.amplifier == 0) "" else " " + StatCollector.translateToLocal("botania.roman" + (effect.amplifier + 1))) + EnumChatFormatting.GRAY + (if (potion.isInstant) "" else " (" + Potion.getDurationString(effect) + ")"), list)
		}
	}
	
	override fun getItemForBrew(brew: Brew, stack: ItemStack?): ItemStack {
		val brewStack = ItemStack(this)
		setBrew(brewStack, brew)
		return brewStack
	}
	
	internal fun addStringToTooltip(s: String, tooltip: MutableList<Any?>?) {
		tooltip?.add(s.replace("&".toRegex(), "§"))
	}
	
	override fun requiresMultipleRenderPasses() = true
	
	override fun getRenderPasses(metadata: Int) = 2
	
	override fun getIcon(stack: ItemStack, pass: Int) = (if (pass == 0) itemIcon else itemIconFluid)!!
	
	override fun getBrew(stack: ItemStack): Brew {
		val key = ItemNBTHelper.getString(stack, "brewKey", "")
		return BotaniaAPI.getBrewFromKey(key)
	}
	
	override fun getManaCost(p0: Brew?, p1: ItemStack?) = p0?.manaCost?.times(1.5)?.I ?: 400
	
	fun setBrew(stack: ItemStack, brew: Brew?) {
		setBrew(stack, (brew ?: BotaniaAPI.fallbackBrew).key)
	}
	
	fun setBrew(stack: ItemStack, brew: String) {
		ItemNBTHelper.setString(stack, "brewKey", brew)
	}
}
