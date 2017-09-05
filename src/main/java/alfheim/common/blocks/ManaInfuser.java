package alfheim.common.blocks;

import alfheim.AlfheimCore;
import alfheim.Constants;
import alfheim.common.blocks.tileentity.ManaInfuserTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ManaInfuser extends Block implements ITileEntityProvider {

	public static IIcon[] textures = new IIcon[4];
	
	public ManaInfuser() {
		super(Material.rock);
		this.setBlockName("ManaInfuser");
		this.setBlockTextureName(Constants.MODID + ":ManaInfuser");
		this.setCreativeTab(AlfheimCore.alfheimTab);
		this.setHardness(3);
		this.setHarvestLevel("pickaxe", 1);
		this.setResistance(60);
		this.setStepSound(soundTypeStone);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ManaInfuserTileEntity();
	}
	
	@Override
	public void registerBlockIcons(IIconRegister reg) {
		textures[0] = reg.registerIcon(this.getTextureName() + "Bottom");
		textures[1] = reg.registerIcon(this.getTextureName() + "Top");
		textures[2] = reg.registerIcon(this.getTextureName() + "Top_Active");
		textures[3] = reg.registerIcon(this.getTextureName() + "Side");
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 0 ? textures[0] : side == 1 ? (meta == 1 ? textures[2] : textures[1]) : textures[3];
	}
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && player.isSneaking()) {
			player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.AQUA + "Mana: " + ((ManaInfuserTileEntity) world.getTileEntity(x, y, z)).getCurrentMana()));
			return true;
		}
		return false;
	}
}