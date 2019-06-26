package alfheim.common.entity.boss;

import alexsocol.asjlib.math.Vector3;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import net.minecraft.world.World;
import vazkii.botania.api.boss.IBotaniaBoss;
import vazkii.botania.client.core.handler.BossBarHandler;

import java.awt.*;

public class EntityRook extends EntityCreature implements IBotaniaBoss { // EntityFlugel, EntityIronGolem, EntityWither
	
	private static final double MAX_HP = 1000;
	
	public EntityRook(World world) {
		super(world);
		setSize(3F, 5F);
		isImmuneToFire = true;
		
		getNavigator().setAvoidsWater(true);
		getNavigator().setCanSwim(false);
		
		tasks.addTask(1, new EntityAIAttackOnCollide(this, 1, true));
		tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.9, 32));
		tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1));
		tasks.addTask(6, new EntityAIWander(this, 0.5));
		tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6));
		tasks.addTask(8, new EntityAILookIdle(this));
		targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, false, true, new IEntitySelector() {@Override public boolean isEntityApplicable(Entity e) {return e instanceof EntityLivingBase;}}));
	}
	
	public static void spawn(World world, int x, int y, int z) {
		if (!world.isRemote) {
			EntityRook rook = new EntityRook(world);
			rook.setPositionAndRotation(x, y, z, 0, 0);
			world.spawnEntityInWorld(rook);
		}
	}
	
	public void onLivingUpdate() {
		super.onLivingUpdate();
		
		heal(0.1F);
		tickAttackTimer();
		
		if (this.motionX * this.motionX + this.motionZ * this.motionZ > 2.5E-7D && this.rand.nextInt(5) == 0) {
			int i = MathHelper.floor_double(this.posX);
			int j = MathHelper.floor_double(this.posY - 0.2D - (double) this.yOffset);
			int k = MathHelper.floor_double(this.posZ);
			Block block = this.worldObj.getBlock(i, j, k);
			
			if (block.getMaterial() != Material.air) this.worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock(block) + "_" + this.worldObj.getBlockMetadata(i, j, k), this.posX + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, this.boundingBox.minY + 0.1D, this.posZ + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, 4.0D * ((double) this.rand.nextFloat() - 0.5D), 0.5D, ((double) this.rand.nextFloat() - 0.5D) * 4.0D);
		}
	}
	
	/*	================================	AI and Data STUFF	================================	*/
	
	@Override
	public void entityInit() {
		super.entityInit();
		dataWatcher.addObject(21, 0);	// Attack Timer
	}
	
	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(MAX_HP);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0);
	}
	
	@Override
	public boolean canDespawn() {
		return false;
	}
	
	@Override
	public boolean isAIEnabled() {
		return true;
	}
	
	public int getAttackTimer() {
		return dataWatcher.getWatchableObjectInt(21);
	}
	
	public void setAttackTimer(int timer) {
		dataWatcher.updateObject(21, timer);
	}
	
	public void tickAttackTimer() {
		int attackTimer = getAttackTimer();
		if (attackTimer > 0) dataWatcher.updateObject(21, --attackTimer);
	}
	
	public int decreaseAirSupply(int air) {
		return air;
	}
	
	@Override
	public void collideWithEntity(Entity collided) {
		super.collideWithEntity(collided);
		
		// if (rand.nextInt(20) != 0) return;
		if (collided instanceof EntityPlayer && ((EntityPlayer) collided).capabilities.disableDamage) return;
		if (collided instanceof EntityLivingBase && collided.isEntityInvulnerable()) return;
		
		if (collided instanceof EntityLivingBase) setAttackTarget((EntityLivingBase) collided);
	}
	
	public boolean attackEntityAsMob(Entity target) {
		if (target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.disableDamage) return false;
		if (target instanceof EntityLivingBase && target.isEntityInvulnerable()) return false;
		if (getAttackTimer() > 0) return false;
		
		setAttackTimer(20);
		worldObj.setEntityState(this, (byte) 4);
		boolean flag = target.attackEntityFrom(DamageSource.causeMobDamage(this), (float) (12 + this.rand.nextInt(6)));
			
		if (flag) {
			Vector3 zis = Vector3.fromEntity(this);
			Vector3 zat = Vector3.fromEntity(target);
			zis.sub(zat).set(zis.x, 0, zis.z).normalize().mul(0.2);
			target.motionX = zis.x;
			target.motionZ = zis.z;
		}
			
		playSound("mob.irongolem.throw", 1.0F, 1.0F);
		return flag;
	}
	
	public boolean canAttackClass(Class clazz) {
		return true;
	}
	
	public AxisAlignedBB getCollisionBox(Entity entity) {
		return entity.boundingBox;
	}
	
	public AxisAlignedBB getBoundingBox() {
		return boundingBox;
	}
	
	public boolean canBePushed() {
		return false;
	}
	
	/*	================================	HEALTHBAR STUFF	================================	*/
	
	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getBossBarTexture() {
		return BossBarHandler.defaultBossBar;
	}
	
	@SideOnly(Side.CLIENT)
	private static Rectangle barRect;
	@SideOnly(Side.CLIENT)
	private static Rectangle hpBarRect;
	
	@Override
	@SideOnly(Side.CLIENT)
	public Rectangle getBossBarTextureRect() {
		if(barRect == null)
			barRect = new Rectangle(0, 0, 185, 15);
		return barRect;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Rectangle getBossBarHPTextureRect() {
		if(hpBarRect == null)
			hpBarRect = new Rectangle(0, barRect.y + barRect.height, 181, 7);
		return hpBarRect;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void bossBarRenderCallback(ScaledResolution res, int x, int y) {
		// NO-OP for now
		/*glPushMatrix();
		int px = x + 160;
		int py = y + 12;
		
		Minecraft mc = Minecraft.getMinecraft();
		ItemStack stack = new ItemStack(Items.skull, 1, 3);
		mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
		glEnable(GL_RESCALE_NORMAL);
		RenderItem.getInstance().renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, stack, px, py);
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		
		boolean unicode = mc.fontRenderer.getUnicodeFlag();
		mc.fontRenderer.setUnicodeFlag(true);
		mc.fontRenderer.drawStringWithShadow("" + getPlayerCount(), px + 15, py + 4, 0xFFFFFF);
		mc.fontRenderer.setUnicodeFlag(unicode);
		glPopMatrix();*/
	}
}