package alfheim.common.items;

import java.util.List;

import alfheim.AlfheimCore;
import alfheim.common.registry.AlfheimItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ElementalEarthChest extends ElementalArmor {
	
	public ElementalEarthChest() {
		super(1, "ElementalEarthChest");
		this.setCreativeTab(AlfheimCore.alfheimTab);
	}

	@Override
	public float getPixieChance(ItemStack stack) {
		return 0.17F;
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		if (armorType == 1 && player.getCurrentArmor(2) != null && player.getCurrentArmor(2).getItem() == AlfheimItems.elementalChestplate) player.addPotionEffect(new PotionEffect(Potion.resistance.id, 1, 1));
    }

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
		list.add(StatCollector.translateToLocal("item.ElementalArmor.desc3"));
		super.addInformation(stack, player, list, b);
	}
}
