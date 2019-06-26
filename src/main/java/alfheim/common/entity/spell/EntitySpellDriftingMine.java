package alfheim.common.entity.spell;

import alexsocol.asjlib.ASJUtilities;
import alexsocol.asjlib.math.Vector3;
import alfheim.AlfheimCore;
import alfheim.api.spell.*;
import alfheim.client.render.world.SpellEffectHandlerClient.Spells;
import alfheim.common.core.handler.CardinalSystem.PartySystem;
import alfheim.common.core.handler.SpellEffectHandler;
import alfheim.common.core.util.DamageSourceSpell;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.*;

public class EntitySpellDriftingMine extends Entity implements ITimeStopSpecific {
	
	public EntityLivingBase caster;
	
	public EntitySpellDriftingMine(World world) {
		super(world);
		setSize(1, 1);
	}
	
	public EntitySpellDriftingMine(World world, EntityLivingBase shooter) {
		this(world);
		this.caster = shooter;
		setPositionAndRotation(caster.posX, caster.posY + caster.height * 0.75, caster.posZ, caster.rotationYaw, caster.rotationPitch);
		if (caster.isSneaking()) return;
		Vector3 m = new Vector3(caster.getLookVec()).mul(0.05);
		motionX = m.x;
		motionY = m.y;
		motionZ = m.z;
	}
	
	public void onImpact(MovingObjectPosition mop) {
		if (!worldObj.isRemote) {
			if (mop != null && mop.entityHit != null) mop.entityHit.attackEntityFrom(DamageSourceSpell.explosion(this, caster), SpellBase.over(caster, 6));
			for (Object o : worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX, posY, posZ).expand(5, 5, 5))) {
				EntityLivingBase e = (EntityLivingBase) o;
				if (!PartySystem.mobsSameParty(e, caster)) e.attackEntityFrom(DamageSourceSpell.explosion(this, caster), SpellBase.over(caster, 6));
			}
			worldObj.playSoundEffect(posX, posY, posZ, "random.explode", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
			SpellEffectHandler.sendPacket(Spells.EXPL, this);
			setDead();
		}
	}
	
	@Override
	public void onUpdate() {
		if (!AlfheimCore.enableMMO || (!worldObj.isRemote && (caster != null && caster.isDead || !worldObj.blockExists((int)posX, (int)posY, (int)posZ)))) {
			setDead();
		} else {
			moveEntity(motionX, motionY, motionZ);
			
			if (!ASJUtilities.isServer()) return;
			super.onUpdate();
			
			if (ticksExisted == 2400) onImpact(null);
			
			Vec3 vec3 = Vec3.createVectorHelper(posX, posY, posZ);
			Vec3 vec31 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
			MovingObjectPosition movingobjectposition = worldObj.rayTraceBlocks(vec3, vec31);
			
			if (movingobjectposition == null) {
				List<EntityLivingBase> l = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, boundingBox.addCoord(motionX, motionY, motionZ).expand(1, 1, 1));
				l.remove(caster);
					
				for (EntityLivingBase e : l) if (e.canBeCollidedWith() && !PartySystem.mobsSameParty(caster, e) && Vector3.entityDistance(this, e) < 3)  {
					movingobjectposition = new MovingObjectPosition(e);
					break;
				}
			}
			
			if (movingobjectposition != null) onImpact(movingobjectposition);
			
			float f1 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
			rotationYaw = (float)(Math.atan2(motionZ, motionX) * 180.0D / Math.PI) + 90.0F;
			
			rotationPitch = (float)(Math.atan2((double)f1, motionY) * 180.0D / Math.PI) - 90.0F;
			while (rotationPitch - prevRotationPitch < -180.0F) prevRotationPitch -= 360.0F;
			while (rotationPitch - prevRotationPitch >= 180.0F) prevRotationPitch += 360.0F;
			while (rotationYaw - prevRotationYaw < -180.0F) prevRotationYaw -= 360.0F;
			while (rotationYaw - prevRotationYaw >= 180.0F) prevRotationYaw += 360.0F;
			
			rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
			rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
			
		}
	}
	
	public boolean canBeCollidedWith() {
		return true;
	}
	
	public float getCollisionBorderSize() {
		return 0.5F;
	}
	
	@SideOnly(Side.CLIENT)
	public float getShadowSize() {
		return 0.0F;
	}
	
	public boolean isImmune() {
		return false;
	}
	
	public boolean affectedBy(UUID uuid) {
		return !caster.getUniqueID().equals(uuid);
	}
	
	public void entityInit() {}
	
	public void readEntityFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("castername")) caster = worldObj.getPlayerEntityByName(nbt.getString("castername")); else setDead();
		if (caster == null) setDead();
	}
	
	public void writeEntityToNBT(NBTTagCompound nbt) {
		if (caster instanceof EntityPlayer) nbt.setString("castername", caster.getCommandSenderName());
	}
}