package alfheim.common.item.equipment.armor.elemental

import alexsocol.asjlib.meta
import alfheim.api.*
import alfheim.client.model.armor.ModelElementalArmor
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import cpw.mods.fml.relauncher.*
import net.minecraft.client.model.ModelBiped
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector
import vazkii.botania.api.item.IPixieSpawner
import vazkii.botania.common.core.handler.ConfigHandler
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.equipment.armor.manasteel.ItemManasteelArmor

abstract class ElementalArmor(type: Int, name: String): ItemManasteelArmor(type, name, AlfheimAPI.elementalArmor), IPixieSpawner {
	
	init {
		creativeTab = AlfheimTab
	}
	
	@SideOnly(Side.CLIENT)
	override fun provideArmorModelForSlot(stack: ItemStack?, slot: Int): ModelBiped {
		models[slot] = ModelElementalArmor(slot)
		return models[slot]
	}
	
	override fun getArmorTextureAfterInk(stack: ItemStack?, slot: Int) =
		"${ModInfo.MODID}:textures/model/armor/ElementalArmor_${(if (ConfigHandler.enableArmorModels) "new" else if (armorType == 2) "1" else "0")}.png"
	
	override fun getIsRepairable(stack: ItemStack?, material: ItemStack) =
		material.item === ModItems.manaResource && material.meta == 7
	
	override fun getArmorSetStacks(): Array<ItemStack> {
		if (armorset == null)
			armorset = arrayOf(ItemStack(AlfheimItems.elementalHelmet), ItemStack(AlfheimItems.elementalChestplate), ItemStack(AlfheimItems.elementalLeggings), ItemStack(AlfheimItems.elementalBoots))
		
		return armorset!!
	}
	
	override fun hasArmorSetItem(player: EntityPlayer, i: Int): Boolean {
		val stack = player.inventory.armorInventory[3 - i] ?: return false
		
		when (i) {
			0 -> return stack.item === AlfheimItems.elementalHelmet || AlfheimItems.elementalHelmetRevealing?.let { stack.item === it } ?: false
			1 -> return stack.item === AlfheimItems.elementalChestplate
			2 -> return stack.item === AlfheimItems.elementalLeggings
			3 -> return stack.item === AlfheimItems.elementalBoots
		}
		
		return false
	}
	
	override fun getArmorSetName(): String {
		return StatCollector.translateToLocal("alfheim.armorset.elemental.name")
	}
	
	override fun addArmorSetDescription(stack: ItemStack?, list: List<String>) {
		super.addArmorSetDescription(stack, list)
		addStringToTooltip(StatCollector.translateToLocal("botania.armorset.elementium.desc"), list)
	}
	
	companion object {
		
		internal var armorset: Array<ItemStack>? = null
	}
}