package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.*
import alfheim.common.core.util.AlfheimTab
import baubles.api.BaubleType
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.StatCollector
import net.minecraft.world.World
import vazkii.botania.api.item.IPixieSpawner
import vazkii.botania.api.mana.*
import vazkii.botania.common.block.tile.mana.TilePool
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.equipment.bauble.ItemBauble
import kotlin.math.min

class ItemManaStorageRing(name: String, maxManaCap: Double, val pixieChance: Float = 0f): ItemBauble(name), IManaItem, IManaTooltipDisplay, IPixieSpawner {
	
	val MAX_MANA = (TilePool.MAX_MANA * maxManaCap).I
	
	init {
		creativeTab = AlfheimTab
		maxDamage = 1000
		maxStackSize = 1
		setNoRepair()
	}
	
	override fun getSubItems(item: Item, creativeTab: CreativeTabs?, list: MutableList<Any?>) {
		list.add(ItemStack(item, 1, 1000))
		/*ItemStack full = new ItemStack(par1, 1, 1);
		setMana(full, MAX_MANA);
		par3List.add(full);*/
	}
	
	override fun getDamage(stack: ItemStack): Int {
		val mana = getMana(stack).F
		return 1000 - (mana / getMaxMana(stack) * 1000).I
	}
	
	@Deprecated("This isn't 'display' its normal Damage", ReplaceWith("getDamage(stack)"))
	override fun getDisplayDamage(stack: ItemStack) = getDamage(stack)
	
	override fun getEntityLifespan(stack: ItemStack?, world: World?) = Integer.MAX_VALUE
	
	override fun getMana(stack: ItemStack?) = ItemNBTHelper.getInt(stack, TAG_MANA, 0)
	
	override fun getMaxMana(stack: ItemStack?) = MAX_MANA
	
	override fun addMana(stack: ItemStack, mana: Int) {
		setMana(stack, min(getMana(stack) + mana, getMaxMana(stack)))
		stack.meta = getDamage(stack)
	}
	
	override fun canReceiveManaFromPool(stack: ItemStack, pool: TileEntity) = true
	
	override fun canReceiveManaFromItem(stack: ItemStack, otherStack: ItemStack) = true
	
	override fun canExportManaToPool(stack: ItemStack, pool: TileEntity) = true
	
	override fun canExportManaToItem(stack: ItemStack, otherStack: ItemStack) = true
	
	override fun isNoExport(stack: ItemStack) = false
	
	override fun getManaFractionForDisplay(stack: ItemStack) = getMana(stack).F / getMaxMana(stack).F
	
	override fun getBaubleType(stack: ItemStack) = BaubleType.RING
	
	override fun addHiddenTooltip(stack: ItemStack, player: EntityPlayer?, list: MutableList<Any?>, adv: Boolean) {
		list.add(StatCollector.translateToLocalFormatted("item.manastorage.desc0", MAX_MANA / TilePool.MAX_MANA))
		list.add("")
		
		super.addHiddenTooltip(stack, player, list, adv)
	}
	
	override fun getPixieChance(stack: ItemStack?) = pixieChance
	
	companion object {
		
		const val TAG_MANA = "mana"
		fun setMana(stack: ItemStack, mana: Int) {
			ItemNBTHelper.setInt(stack, TAG_MANA, mana)
		}
	}
}
