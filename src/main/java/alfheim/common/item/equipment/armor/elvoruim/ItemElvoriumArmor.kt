package alfheim.common.item.equipment.armor.elvoruim

import alexsocol.asjlib.*
import alfheim.api.*
import alfheim.client.core.helper.IconHelper
import alfheim.client.model.armor.ModelElvoriumArmor
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
import com.google.common.collect.*
import cpw.mods.fml.common.Optional
import cpw.mods.fml.relauncher.*
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.*
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.World
import thaumcraft.api.IVisDiscountGear
import thaumcraft.api.aspects.Aspect
import vazkii.botania.api.item.IManaProficiencyArmor
import vazkii.botania.api.mana.IManaDiscountArmor
import vazkii.botania.common.Botania
import vazkii.botania.common.core.handler.ConfigHandler
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.equipment.armor.manasteel.ItemManasteelArmor
import java.util.*

@Optional.Interface(modid = "Thaumcraft", iface = "thaumcraft.api.IVisDiscountGear", striprefs = true)
open class ItemElvoriumArmor(type: Int, name: String): ItemManasteelArmor(type, name, AlfheimAPI.elvoriumArmor), IManaDiscountArmor, IManaProficiencyArmor, IVisDiscountGear {
	
	init {
		creativeTab = AlfheimTab
	}
	
	@SideOnly(Side.CLIENT)
	override fun provideArmorModelForSlot(stack: ItemStack?, slot: Int): ModelBiped {
		models[slot] = if (AlfheimConfigHandler.minimalGraphics) ModelBiped() else ModelElvoriumArmor(slot)
		return models[slot]
	}
	
	override fun getArmorTextureAfterInk(stack: ItemStack?, slot: Int): String {
		return if (ConfigHandler.enableArmorModels && !AlfheimConfigHandler.minimalGraphics) ModInfo.MODID + ":textures/model/armor/ElvoriumArmor.png" else if (slot == 2) ModInfo.MODID + ":textures/model/armor/ElvoriumArmor1.png" else ModInfo.MODID + ":textures/model/armor/ElvoriumArmor0.png"
	}
	
	override fun getIsRepairable(armor: ItemStack?, material: ItemStack): Boolean {
		return material.item === AlfheimItems.elvenResource && material.meta == ElvenResourcesMetas.ElvoriumIngot.I
	}
	
	override fun getAttributeModifiers(stack: ItemStack): Multimap<String, AttributeModifier> {
		val multimap = HashMultimap.create<String, AttributeModifier>()
		val uuid = UUID(unlocalizedName.hashCode().toLong(), 0)
		multimap.put(SharedMonsterAttributes.knockbackResistance.attributeUnlocalizedName, AttributeModifier(uuid, "Elvorium modifier $type", 0.25, 0))
		return multimap
	}
	
	override fun getArmorSetStacks() = armorset
	
	override fun hasArmorSetItem(player: EntityPlayer, i: Int): Boolean {
		val stack = player.inventory.armorInventory[3 - i] ?: return false
		
		when (i) {
			0 -> return stack.item === AlfheimItems.elvoriumHelmet || AlfheimItems.elvoriumHelmetRevealing?.let { stack.item === it } ?: false
			1 -> return stack.item === AlfheimItems.elvoriumChestplate
			2 -> return stack.item === AlfheimItems.elvoriumLeggings
			3 -> return stack.item === AlfheimItems.elvoriumBoots
		}
		
		return false
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
	}
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack): String {
		val s = this.getUnlocalizedName(stack)
		return if (s == null) "" else StatCollector.translateToLocal(s)
	}
	
	override fun getArmorSetName(): String {
		return StatCollector.translateToLocal("alfheim.armorset.elvorium.name")
	}
	
	override fun addArmorSetDescription(stack: ItemStack?, list: List<String>) {
		addStringToTooltip(StatCollector.translateToLocal("alfheim.armorset.elvorium.desc0"), list)    // -30% mana cost
		addStringToTooltip(StatCollector.translateToLocal("alfheim.armorset.elvorium.desc1"), list)    // Powerful rods
		if (Botania.thaumcraftLoaded) addStringToTooltip(EnumChatFormatting.DARK_PURPLE.toString() + StatCollector.translateToLocal("alfheim.armorset.elvorium.desc2"), list)    // -20% vis discount
		if (Botania.thaumcraftLoaded) addStringToTooltip(EnumChatFormatting.GOLD.toString() + StatCollector.translateToLocal("alfheim.armorset.elvorium.desc3"), list)    // 8 pts of runic shield
		addStringToTooltip(StatCollector.translateToLocal("botania.armorset.terrasteel.desc1"), list)    // Regen w/o full hungerbar
		addStringToTooltip(StatCollector.translateToLocal("botania.armorset.terrasteel.desc2"), list)    // Passive mana regen
	}
	
	override fun onArmorTick(world: World, player: EntityPlayer, stack: ItemStack) {
		super.onArmorTick(world, player, stack)
		ItemNBTHelper.setBoolean(stack, "SET", hasArmorSet(player))
	}
	
	@Optional.Method(modid = "Thaumcraft")
	override fun getRunicCharge(stack: ItemStack): Int {
		return if (ItemNBTHelper.getBoolean(stack, "SET", false)) 2 else 0
	}
	
	override fun getDiscount(stack: ItemStack, slot: Int, player: EntityPlayer): Float {
		return if (hasArmorSet(player)) 0.3f / 4f else 0f
	}
	
	override fun shouldGiveProficiency(stack: ItemStack, slot: Int, player: EntityPlayer): Boolean {
		return hasArmorSet(player)
	}
	
	@Optional.Method(modid = "Thaumcraft")
	override fun getVisDiscount(stack: ItemStack, player: EntityPlayer, aspect: Aspect): Int {
		return if (hasArmorSet(player)) 5 else 0
	}
	
	companion object {
		
		val armorset: Array<ItemStack> by lazy { arrayOf(ItemStack(AlfheimItems.elvoriumHelmet), ItemStack(AlfheimItems.elvoriumChestplate), ItemStack(AlfheimItems.elvoriumLeggings), ItemStack(AlfheimItems.elvoriumBoots)) }
		
		init {
			eventForge()
		}
		
		fun hasSet(player: EntityPlayer) = (AlfheimItems.elvoriumChestplate as ItemElvoriumArmor).hasArmorSet(player)
		
//		@SubscribeEvent(priority = EventPriority.LOWEST)
//		fun onLivingHurt(e: LivingHurtEvent) {
//			if (e.source.isUnblockable) return
//			val player = e.entity as? EntityPlayer ?: return
//			if (!hasSet(player)) return
//			e.ammount -= ManaItemHandler.requestManaForTool(player.inventory.armorInventory[0], player, e.ammount.mceil() * 100, true) / 100f
//		}
	}
}