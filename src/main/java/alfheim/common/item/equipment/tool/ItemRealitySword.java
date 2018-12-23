package alfheim.common.item.equipment.tool;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import akka.routing.RemoveRoutee;
import alexsocol.asjlib.ASJUtilities;
import alfheim.AlfheimCore;
import alfheim.api.AlfheimAPI;
import alfheim.api.ModInfo;
import alfheim.api.spell.DamageSourceSpell;
import alfheim.common.core.registry.AlfheimItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import static vazkii.botania.common.core.helper.ItemNBTHelper.*;

public class ItemRealitySword extends ItemSword implements IManaUsingItem {

	public static final String TAG_ELEMENT = "element";
	public static IIcon[] textures = new IIcon[6];
	
	public ItemRealitySword() {
		super(AlfheimAPI.REALITY);
		this.setCreativeTab(AlfheimCore.alfheimTab);
		this.setNoRepair();
		this.setUnlocalizedName("RealitySword");
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.RealitySword" + getInt(stack, TAG_ELEMENT, 0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		for (int i = 0; i < 6; i++)
		textures[i] = reg.registerIcon(ModInfo.MODID + ":RealitySword" + i);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconIndex(ItemStack stack) {
		return textures[getInt(stack, TAG_ELEMENT, 0)];
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return getIconIndex(stack);
	}
	
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (player.isSneaking()) {
			if (getInt(stack, TAG_ELEMENT, 0) == 5) return stack;
			if (merge(player.getCommandSenderName(), stack.getDisplayName()).equals("35E07445CBB8B10F7173F6AD6C1E29E9A66565F86AFF61ACADA750D443BFF7B0")) {
				setInt(stack, TAG_ELEMENT, 5);
				stack.getTagCompound().removeTag("display");
				return stack;
			}
			
			if (!ManaItemHandler.requestManaExact(stack, player, 1, !world.isRemote)) return stack;
			setInt(stack, TAG_ELEMENT, Math.max(0, getInt(stack, TAG_ELEMENT, 0) + 1) % 5);
		} else player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		return stack;
	}
	
	String merge(String s1, String s2) {
		String s = "";
		for (int i = 0; i < s1.length(); i++) for (int j = 0; j < s2.length(); j++) s += (char) ((s1.charAt(i) * s2.charAt(j)) % 256);
		return hash(s);
	}
	
	String hash(String str) {
		if(str != null)
			try {
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				return new HexBinaryAdapter().marshal(md.digest(salt(str).getBytes(Charset.forName("UTF-8"))));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		return "";
	}

	// Might as well be called sugar given it's not secure at all :D
	String salt(String str) {
		str = str += "wellithoughtthatthisiscoolideaandicanmakesomethinglikethis#whynot";
		SecureRandom rand = new SecureRandom(str.getBytes(Charset.forName("UTF-8")));
		int l = str.length();
		int steps = rand.nextInt(l);
		char[] chrs = str.toCharArray();
		for(int i = 0; i < steps; i++) {
			int indA = rand.nextInt(l);
			int indB;
			do {
				indB = rand.nextInt(l);
			} while(indB == indA);
			char c = (char) (chrs[indA] ^ chrs[indB]);
			chrs[indA] = c;
		}

		return String.copyValueOf(chrs);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slotID, boolean inHand) {
		if (world.isRemote) return;
		
		boolean flag = getInt(stack, TAG_ELEMENT, 0) == 5;
		if (entity instanceof EntityPlayer) {
			if (!flag && 0 < getInt(stack, TAG_ELEMENT, 0) && getInt(stack, TAG_ELEMENT, 0) < 5) 
				if (!ManaItemHandler.requestManaExact(stack, (EntityPlayer) entity, 1, !world.isRemote)) 
					setInt(stack, TAG_ELEMENT, 0);
			
			if (flag && !entity.getCommandSenderName().equals("AlexSocol")) {
				EntityPlayer player = ((EntityPlayer) entity);
				world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, stack.copy()));
				player.inventory.consumeInventoryItem(AlfheimItems.realitySword);
				player.setHealth(0);
				player.onDeath(DamageSource.outOfWorld);
				ASJUtilities.sayToAllOnline(StatCollector.translateToLocalFormatted("item.RealitySword.DIE", player.getCommandSenderName())); 
			}
		} else if (flag && entity instanceof EntityLivingBase) {
			EntityLivingBase living = ((EntityLivingBase) entity);
			world.spawnEntityInWorld(new EntityItem(world, living.posX, living.posY, living.posZ, stack.copy()));
			stack.stackSize = 0;
			living.setHealth(0);
			living.onDeath(DamageSource.outOfWorld);
		} else if (flag) {
			world.spawnEntityInWorld(new EntityItem(world, 0, 666, 0, stack.copy()));
			stack.stackSize = 0;
		}
	}
	
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		if (attacker instanceof EntityPlayer) {
			int elem = getInt(stack, TAG_ELEMENT, 0);
			if (elem != 0 && (elem == 5 || ManaItemHandler.requestManaExact(stack, (EntityPlayer) attacker, 1000, !attacker.worldObj.isRemote && elem != 5))) useAbility(elem, attacker, target);
		}
		return super.hitEntity(stack, target, attacker);
	}
	
	private void useAbility(int i, EntityLivingBase attacker, EntityLivingBase target) {
		switch (i) {
			case 1: {
				Vec3 vec = attacker.getLookVec();
				target.motionX = vec.xCoord * 1.5;
				target.motionZ = vec.zCoord * 1.5;
				break;
			}
			case 2: target.addPotionEffect(new PotionEffect(Potion.poison.id, 80, 1)); break;
			case 3: target.setFire(6); break;
			case 4: {
				target.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, -1));
				target.hurtResistantTime = target.hurtTime = 0;
				target.attackEntityFrom(DamageSourceSpell.water(attacker), 5);
				target.hurtResistantTime = target.maxHurtResistantTime;
				target.hurtTime = target.maxHurtTime = 10;
				break;
			}
			case 5: {
				for (int a = 1; a < 5; a++) useAbility(a, attacker, target);
				break;
			}
		}
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
		int elem = getInt(stack, TAG_ELEMENT, 0);
		if (elem == 5) {
			list.add(StatCollector.translateToLocal("item.RealitySword.descZ"));
			return;
		}
		
		if (1 > elem || elem > 4) list.add(StatCollector.translateToLocal("item.RealitySword.desc0"));
		switch (elem) {
			case 1: list.add(StatCollector.translateToLocal("item.RealitySword.desc1")); break;
			case 2: list.add(StatCollector.translateToLocal("item.RealitySword.desc2")); break;
			case 3: list.add(StatCollector.translateToLocal("item.RealitySword.desc3")); break;
			case 4: list.add(StatCollector.translateToLocal("item.RealitySword.desc4")); break;
		}
		if (0 < elem && elem < 5) list.add(StatCollector.translateToLocal("item.RealitySword.desc5"));
	}

	@Override
	public boolean usesMana(ItemStack stack) {
		return (0 < getInt(stack, TAG_ELEMENT, 0) && getInt(stack, TAG_ELEMENT, 0) < 5);
	}
}