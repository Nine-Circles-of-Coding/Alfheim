package alfheim.common.core.asm.hook.extender

import alexsocol.asjlib.*
import alfheim.common.item.lens.*
import cpw.mods.fml.relauncher.*
import gloomyfolken.hooklib.asm.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.*
import net.minecraft.util.IIcon
import vazkii.botania.api.mana.IManaSpreader
import vazkii.botania.client.core.helper.IconHelper
import vazkii.botania.common.item.lens.ItemLens

object ItemLensExtender {
	
	const val PROP_NONE = 0
	const val PROP_POWER = 1
	const val PROP_ORIENTATION = 2
	const val PROP_TOUCH = 4
	const val PROP_INTERACTION = 8
	const val PROP_DAMAGE = 16
	const val PROP_CONTROL = 32
	
	const val MESSANGER = 22
	const val TRIPWIRE = 23
	
	const val PUSH = 24
	const val SMELT = 25
	const val SUPERCONDUCTOR = 26
	const val TRACK = 27
	
	const val DAISY = 28
	
	/**
	 * Change [alfheim.common.core.asm.AlfheimClassTransformer.moreLenses] when adding new lens.
	 *
	 * Add name in [alfheim.common.core.asm.AlfheimClassTransformer].LibItemNames$ClassVisitor.LibItemNames$clinit$MethodVisitor
	 */
	@JvmStatic
	@Hook(injectOnExit = true, isMandatory = true, targetMethod = "<clinit>")
	fun `ItemLens$clinit`(lens: ItemLens?) {
		// Botania
		ItemLens.setProps(MESSANGER, PROP_POWER)
		ItemLens.setProps(TRIPWIRE, PROP_CONTROL)
		// ExtraBotany
		ItemLens.setProps(PUSH, PROP_NONE)
		ItemLens.setProps(SMELT, PROP_NONE)
		ItemLens.setProps(SUPERCONDUCTOR, PROP_DAMAGE)
		ItemLens.setProps(TRACK, PROP_CONTROL)
		// new
		ItemLens.setProps(DAISY, PROP_INTERACTION or PROP_TOUCH)
		
		// Botania
		ItemLens.setLens(MESSANGER, LensMessanger())
		ItemLens.setLens(TRIPWIRE, LensTripwire())
		ItemLens.setLens(TRIPWIRE, LensTripwire())
		// ExtraBotany
		ItemLens.setLens(PUSH, LensPush())
		ItemLens.setLens(SMELT, LensSmelt())
		ItemLens.setLens(SUPERCONDUCTOR, LensSuperconductor())
		ItemLens.setLens(TRACK, LensTrack())
		// new
		ItemLens.setLens(DAISY, LensDaisy())
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun getSubItems(thiz: ItemLens, item: Item?, tab: CreativeTabs?, list: MutableList<ItemStack>) {
		list.add(ItemStack(item, 1, ItemLens.STORM))
	}
	
	var lensStormIcon: IIcon? = null
	
	@SideOnly(Side.CLIENT)
	@JvmStatic
	@Hook(injectOnExit = true)
	fun registerIcons(thiz: ItemLens, reg: IIconRegister) {
		lensStormIcon = IconHelper.forName(reg, "lensStorm")
	}
	
	@SideOnly(Side.CLIENT)
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ReturnCondition.ON_NOT_NULL)
	fun getIconFromDamageForRenderPass(thiz: ItemLens, meta: Int, pass: Int) = if (pass == 1 && meta == ItemLens.STORM) lensStormIcon else null
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun allowBurstShooting(thiz: ItemLens, stack: ItemStack, spreader: IManaSpreader?, redstone: Boolean): Boolean {
		return ItemLens.getLens(stack.getItemDamage()).allowBurstShooting(stack, spreader, redstone)
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun onControlledSpreaderTick(thiz: ItemLens, stack: ItemStack, spreader: IManaSpreader?, redstone: Boolean) {
		ItemLens.getLens(stack.getItemDamage()).onControlledSpreaderTick(stack, spreader, redstone)
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun onControlledSpreaderPulse(thiz: ItemLens, stack: ItemStack, spreader: IManaSpreader?, redstone: Boolean) {
		ItemLens.getLens(stack.getItemDamage()).onControlledSpreaderPulse(stack, spreader, redstone)
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE, intReturnConstant = 28)
	fun getProps(thiz: ItemLens, stack: ItemStack): Boolean {
		return stack.meta == ItemLens.STORM
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_NOT_NULL)
	fun getUnlocalizedName(thiz: ItemLens, stack: ItemStack) = if (stack.meta == 5000) "item.lensStorm" else null
	
	private const val TAG_COMPOSITE_LENS = "compositeLens"
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun doesContainerItemLeaveCraftingGrid(thiz: ItemLens, stack: ItemStack) = !hasContainerItem(thiz, stack)
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun hasContainerItem(thiz: ItemLens, stack: ItemStack) = ItemNBTHelper.getNBT(stack).hasKey(TAG_COMPOSITE_LENS)
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun getContainerItem(thiz: ItemLens, stack: ItemStack): ItemStack? {
		if (!hasContainerItem(thiz, stack)) return null
		
		val result = stack.copy()
		ItemNBTHelper.getNBT(result).removeTag(TAG_COMPOSITE_LENS)
		
		return result
	}
}