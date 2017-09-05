package alfheim.common.items;

import java.util.List;

import alexsocol.asjlib.ASJUtilities;
import alfheim.AlfheimCore;
import alfheim.Constants;
import alfheim.common.dimension.world.gen.structure.ArenaStructure;
import alfheim.common.utils.AlfheimConfig;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ElvenResource extends Item {
	
	public static final String[] subItems = new String[] { "ManaInfusionCore", "ElvoriumIngot", "MauftriumIngot", "MuspelheimPowerIngot", "NiflheimPowerIngot", "ElvoriumNugget", "MauftriumNugget", "MuspelheimEssence", "NiflheimEssence", "IffesalDust", "PrimalRune", "MuspelheimRune", "NiflheimRune", "InfusedDreamwoodTwig", "TestItem" };
	private IIcon[] texture = new IIcon[subItems.length];
	
	public ElvenResource() {
		this.setCreativeTab(AlfheimCore.alfheimTab);
		this.setHasSubtypes(true);
		this.setUnlocalizedName("ElvenItems");
	}
	
	public void registerIcons(IIconRegister iconRegister){
		for (int i = 0; i < subItems.length; i++){
			texture[i] = iconRegister.registerIcon(Constants.MODID + ":materials/" + subItems[i]);
		}
	}

    public IIcon getIconFromDamage(int i) {
    	if (i < texture.length) {
        	return texture[i];
    	} else {
    		return texture[0];
    	}
    }

    public String getUnlocalizedName(ItemStack stack) {
    	if (stack.getItemDamage() < subItems.length) {
        	return "item." + subItems[stack.getItemDamage()];
    	} else {
    		return subItems[0];
    	}
    }

    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < subItems.length; ++i) {
            list.add(new ItemStack(item, 1, i));
        }
    }
    
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote && stack.getItemDamage() == subItems.length - 1) {
			if (!player.isSneaking()) {
				//ASJUtilities.sendToDimensionWithoutPortal(player, AlfheimConfig.dimensionIDAlfheim, player.posX, 228.0D, player.posZ);
				(new ArenaStructure()).generate(world, player.getRNG(), MathHelper.floor_double(player.posX), world.getTopSolidOrLiquidBlock(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posZ)), MathHelper.floor_double(player.posZ));
			} else {
				player.addChatComponentMessage(new ChatComponentText("Current dimension id: " + player.dimension));
			}
		}
		return stack;
	}
}