package alfheim.common.item.material

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.client.core.helper.IconHelper
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenFoodMetas.*
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.*
import net.minecraft.nbt.*
import net.minecraft.potion.PotionEffect
import net.minecraft.util.IIcon
import net.minecraft.world.World

class ItemElvenFood: ItemFood(0, 0f, false) {
	
	val subItems = values().size
	
	lateinit var icons: Array<IIcon>
	
	// #### ItemMod ####
	
	init {
		setHasSubtypes(true)
		creativeTab = AlfheimTab
		unlocalizedName = "ElvenFood"
	}
	
	override fun setUnlocalizedName(name: String): Item {
		GameRegistry.registerItem(this, name)
		return super.setUnlocalizedName(name)
	}
	
	override fun getItemStackDisplayName(stack: ItemStack) =
		super.getItemStackDisplayName(stack).replace("&".toRegex(), "\u00a7")
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack) =
		getUnlocalizedName(stack).replace("item\\.".toRegex(), "item.${ModInfo.MODID}:") + stack.meta
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		icons = Array(subItems) { IconHelper.forItem(reg, this, it, "materials/food") }
	}
	
	override fun getIconFromDamage(meta: Int) = icons.safeGet(meta)
	
	// #### ItemFood ####
	
	// foodLevel
	override fun func_150905_g(stack: ItemStack): Int {
		return when (values().getOrNull(stack.meta)) {
			Lembas                 -> 20
			RedGrapes, WhiteGrapes -> 2
			Nectar                 -> 1
			RedWine, WhiteWine     -> 3
			JellyBottle            -> 3
			JellyBread             -> 6
			JellyCod               -> 9
			DreamCherry            -> 2
			else                   -> 0
		}
	}
	
	// foodSaturationLevel
	override fun func_150906_h(stack: ItemStack): Float {
		return when (values().getOrNull(stack.meta)) {
			Lembas                 -> 5f
			RedGrapes, WhiteGrapes -> 0.3f
			Nectar                 -> 0.15f
			RedWine, WhiteWine     -> 0.1f
			JellyBottle            -> 0.5f
			JellyBread             -> 0.8f
			JellyCod               -> 1.2f
			DreamCherry            -> 0.3f
			else                   -> 0f
		}
	}
	
	val drinkables = arrayOf(RedWine.I, WhiteWine.I, JellyBottle.I)
	
	override fun getItemUseAction(stack: ItemStack) = if (stack.meta in drinkables) EnumAction.drink else EnumAction.eat
	
	override fun getMaxItemUseDuration(stack: ItemStack): Int {
		return super.getMaxItemUseDuration(stack)
	}
	
	override fun onEaten(stack: ItemStack, world: World?, player: EntityPlayer): ItemStack {
		getPotions(stack).forEach {
			it ?: return@forEach
			
			val eff = player.getActivePotionEffect(it.potionID) ?: it
			eff.duration = it.duration
			eff.amplifier = it.amplifier
		}
		
		val ret = super.onEaten(stack, world, player)
		
		return getContainerItem(stack) ?: ret
	}
	
	// #### Item ####
	
	override fun hasContainerItem(stack: ItemStack) = true
	
	override fun getContainerItem(stack: ItemStack): ItemStack? {
		return when (ElvenFoodMetas.values().getOrNull(stack.meta)) {
			RedWine, WhiteWine -> ElvenResourcesMetas.Jug.stack
			JellyBottle        -> ItemStack(Items.glass_bottle)
			else               -> null
		}
	}
	
	override fun getSubItems(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
		(0 until subItems).forEach { list.add(ItemStack(item, 1, it)) }
	}
	
	override fun getItemStackLimit(stack: ItemStack): Int {
		return if (stack.meta in drinkables) 1 else super.getItemStackLimit(stack)
	}
	
	companion object {
		
		const val TAG_POTIONS = "potions"
		
		fun addPotion(stack: ItemStack, effect: PotionEffect) {
			ItemNBTHelper.getList(stack, TAG_POTIONS, NBTBase.NBTTypes.indexOf("COMPOUND")).appendTag(NBTTagCompound().apply { effect.writeCustomPotionEffectToNBT(this) })
		}
		
		fun getPotions(stack: ItemStack): List<PotionEffect?> {
			return ItemNBTHelper.getList(stack, TAG_POTIONS, NBTBase.NBTTypes.indexOf("COMPOUND")).tagList.map {
				PotionEffect.readCustomPotionEffectFromNBT(it as NBTTagCompound)
			}
		}
	}
}

enum class ElvenFoodMetas {
	
	Lembas,
	RedGrapes,
	WhiteGrapes,
	Nectar,
	RedWine,
	WhiteWine,
	JellyBottle,
	JellyBread,
	JellyCod,
	DreamCherry;
	
	val I get() = ordinal
	
	val stack get() = stack()
	fun stack(size: Int = 1) = ItemStack(AlfheimItems.elvenFood, size, I)
}