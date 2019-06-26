package alfheim.common.core.handler;

import alexsocol.asjlib.ASJUtilities;
import alexsocol.asjlib.math.Vector3;
import alfheim.AlfheimCore;
import alfheim.api.entity.EnumRace;
import alfheim.api.event.*;
import alfheim.client.render.world.SpellEffectHandlerClient;
import alfheim.client.render.world.SpellEffectHandlerClient.Spells;
import alfheim.common.core.handler.CardinalSystem.*;
import alfheim.common.core.handler.CardinalSystem.PartySystem.Party;
import alfheim.common.core.handler.CardinalSystem.TargetingSystem.Target;
import alfheim.common.core.registry.*;
import alfheim.common.core.util.*;
import alfheim.common.entity.EntityAlfheimPixie;
import alfheim.common.entity.Flight;
import alfheim.common.entity.boss.EntityFlugel;
import alfheim.common.network.Message1d;
import alfheim.common.network.Message2d;
import alfheim.common.network.Message2d.m2d;
import alfheim.common.network.MessageEffect;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.api.recipe.ElvenPortalUpdateEvent;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.tile.TileAlfPortal;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.equipment.tool.elementium.ItemElementiumAxe;

public class EventHandler {
	
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent e) {
		if (InfoLoader.doneChecking && !InfoLoader.triedToWarnPlayer) {
			InfoLoader.triedToWarnPlayer = true;
			for (String s : InfoLoader.info) {
				if (s.startsWith("$json")) e.player.addChatComponentMessage(IChatComponent.Serializer.func_150699_a(s.replace("$json", "")));
				else e.player.addChatMessage(new ChatComponentText(s));
			}
		}
		
		if (e.player instanceof EntityPlayerMP) {
			AlfheimCore.network.sendTo(new Message2d(m2d.MODES, AlfheimCore.enableElvenStory ? 1 : 0, AlfheimCore.enableMMO ? 1 : 0), (EntityPlayerMP) e.player);
			if (AlfheimCore.enableElvenStory) {
				AlfheimCore.network.sendTo(new Message1d(Message1d.m1d.DEATH_TIMER, AlfheimConfig.deathScreenAddTime), (EntityPlayerMP) e.player);
				AlfheimCore.network.sendTo(new Message1d(Message1d.m1d.CL_SLOWDOWN, AlfheimConfig.slowDownClients ? 1 : 0), (EntityPlayerMP) e.player);
				if (!((EntityPlayerMP) e.player).func_147099_x().hasAchievementUnlocked(AlfheimAchievements.alfheim) && e.player.dimension != AlfheimConfig.dimensionIDAlfheim) {
					ASJUtilities.sendToDimensionWithoutPortal(e.player, AlfheimConfig.dimensionIDAlfheim, 0.5, 253, 0.5);
					e.player.triggerAchievement(AlfheimAchievements.alfheim);
					e.player.addChatComponentMessage(new ChatComponentTranslation("elvenstory.welcome0"));
					e.player.addChatComponentMessage(new ChatComponentTranslation("elvenstory.welcome1"));
					e.player.addChatComponentMessage(new ChatComponentTranslation("elvenstory.welcome2"));
					e.player.inventory.addItemStackToInventory(new ItemStack(ModItems.lexicon));
					e.player.setSpawnChunk(new ChunkCoordinates(0, 253, 0), true, AlfheimConfig.dimensionIDAlfheim);
				}
				if (AlfheimCore.enableMMO) CardinalSystem.transfer((EntityPlayerMP) e.player);
			}
		}
	}
	
	@SubscribeEvent
	public void onNetherPortalActivation(NetherPortalActivationEvent e) {
		if (e.worldObj.provider.dimensionId == AlfheimConfig.dimensionIDAlfheim) e.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onAlfPortalUpdate(ElvenPortalUpdateEvent e) {
		if (e.portalTile.getWorldObj().provider.dimensionId == AlfheimConfig.dimensionIDAlfheim && ((TileAlfPortal)e.portalTile).ticksOpen >= 0) ((TileAlfPortal) e.portalTile).ticksOpen = 0;
	}
	
	@SubscribeEvent
	public void onEntityDrops(LivingDropsEvent event) {
		if(event.recentlyHit && event.source.getEntity() != null && event.source.getEntity() instanceof EntityPlayer) {
			ItemStack weapon = ((EntityPlayer) event.source.getEntity()).getCurrentEquippedItem();
			if(weapon != null && weapon.getItem() instanceof ItemElementiumAxe &&event.entityLiving instanceof EntityFlugel && event.entity.worldObj.rand.nextInt(13) < 1 + EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, weapon)) {
				EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, new ItemStack(AlfheimItems.flugelHead));
				entityitem.delayBeforeCanPickup = 10;
				event.drops.add(entityitem);
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (AlfheimCore.enableElvenStory && !Botania.gardenOfGlassLoaded) {
			ItemStack equipped = event.entityPlayer.getCurrentEquippedItem();
			if (equipped != null && equipped.getItem() == Items.bowl && event.action == Action.RIGHT_CLICK_BLOCK && !event.world.isRemote) {
				MovingObjectPosition movingobjectposition = ToolCommons.raytraceFromEntity(event.world, event.entityPlayer, true, 4.5F);
				
				if (movingobjectposition != null && movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && !event.world.isRemote) {
					int i = movingobjectposition.blockX;
					int j = movingobjectposition.blockY;
					int k = movingobjectposition.blockZ;
					
					if (event.world.getBlock(i, j, k).getMaterial() == Material.water) {
						--equipped.stackSize;
						
						if (equipped.stackSize <= 0) event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, new ItemStack(ModItems.waterBowl));
						else event.entityPlayer.dropPlayerItemWithRandomChoice(new ItemStack(ModItems.waterBowl), false);
					}
				}
			}
		}
	}
	
	// ################################ ATTRIBUTES ################################
	
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing e) {
		if (AlfheimCore.enableElvenStory) {
			if (e.entity instanceof EntityPlayer) {
				EnumRace.ensureExistance(((EntityPlayer) e.entity));
				Flight.ensureExistence(((EntityPlayer) e.entity));
			}
		}
	}
	
	@SubscribeEvent
	public void onClonePlayer(PlayerEvent.Clone e) {
		if (AlfheimCore.enableElvenStory) {
			int r = EnumRace.getRaceID(e.original);
			EnumRace.setRaceID(e.entityPlayer, r);
			if (!e.wasDeath) Flight.set(e.entityPlayer, Flight.get(e.original));
		}
	}
	
	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if (AlfheimCore.enableElvenStory) {
			if (!AlfheimConfig.enableWingsNonAlfheim && e.player.worldObj.provider.dimensionId != AlfheimConfig.dimensionIDAlfheim) return;
			e.player.capabilities.allowFlying = !EnumRace.getRace(e.player).equals(EnumRace.HUMAN);
		}
	}
	
	@SubscribeEvent
	public void onPlayerChangeDimension(PlayerChangedDimensionEvent e) {
		if (AlfheimCore.enableElvenStory) {
			if (e.player instanceof EntityPlayerMP) {
				AlfheimCore.network.sendTo(new Message2d(m2d.ATTRIBUTE, 0, EnumRace.getRaceID(e.player)), (EntityPlayerMP) e.player);
				AlfheimCore.network.sendTo(new Message2d(m2d.ATTRIBUTE, 1, Flight.get(e.player)), (EntityPlayerMP) e.player);
			}
		}
	}
	
	// ################################### POTIONS & STUFF ####################################
	// not decentralized because of importance of the order
	
	@SubscribeEvent
	public void onEntityAttacked(LivingAttackEvent e) {
		float ammount = e.ammount;
		
		if (e.source.getEntity() != null && e.source.getEntity() instanceof EntityLivingBase) 
			if (((EntityLivingBase) e.source.getEntity()).isPotionActive(AlfheimRegistry.berserk))
				ammount *= 1.2F;
			else
			if (((EntityLivingBase) e.source.getEntity()).isPotionActive(AlfheimRegistry.ninja))
				ammount *= 0.8F;
		
		if (AlfheimCore.enableMMO) {
			if (e.source.getEntity() != null && e.source.getEntity() instanceof EntityLivingBase && ((EntityLivingBase) e.source.getEntity()).isPotionActive(AlfheimRegistry.quadDamage))
				ammount *= 4.0F;
			
			if ((e.source.getEntity() != null && e.source.getEntity() instanceof EntityLivingBase && ((EntityLivingBase) e.source.getEntity()).isPotionActive(AlfheimRegistry.leftFlame)) || (e.entityLiving.isPotionActive(AlfheimRegistry.leftFlame))) {
				e.setCanceled(true);
				return;
			}
			if ((e.source.damageType.equalsIgnoreCase(DamageSource.inWall.damageType) || e.source.damageType.equalsIgnoreCase(DamageSource.drown.damageType)) && e.entityLiving.isPotionActive(AlfheimRegistry.noclip)) {
				e.setCanceled(true);
				return;
			}		
		}		
		if (e.entityLiving instanceof EntityAlfheimPixie && e.source.getDamageType().equals(DamageSource.inWall.getDamageType())) {
			e.setCanceled(true);
			return;
		}
		if (AlfheimCore.enableElvenStory && e.entityLiving instanceof EntityPlayer && e.source.getDamageType().equals(DamageSource.fall.getDamageType()) && EnumRace.getRace((EntityPlayer) e.entityLiving) != EnumRace.HUMAN) {
			e.setCanceled(true);
			return;
		}
		if (PartySystem.friendlyFire(e.entityLiving, e.source)) {
			e.setCanceled(true);
			return;
		}
		if (e.source.isFireDamage() && !e.source.isUnblockable() && e.entityLiving instanceof EntityPlayer && ((EntityPlayer)e.entityLiving).getCurrentArmor(1) != null && ((EntityPlayer)e.entityLiving).getCurrentArmor(1).getItem() == AlfheimItems.elementalLeggings && ManaItemHandler.requestManaExact(((EntityPlayer)e.entityLiving).getCurrentArmor(1), ((EntityPlayer)e.entityLiving), MathHelper.ceiling_float_int(10 * ammount), !e.entityLiving.worldObj.isRemote)) {
			e.setCanceled(true);
			return;
		}
		
		// ################################################################ NOT CANCELING ################################################################
		
		if (AlfheimCore.enableMMO && e.entityLiving.isPotionActive(AlfheimRegistry.decay) && !e.source.isFireDamage() && !e.source.isMagicDamage() && !(e.source instanceof DamageSourceSpell) && !e.source.damageType.equals(DamageSourceSpell.bleeding.damageType))
			e.entityLiving.addPotionEffect(new PotionEffect(AlfheimRegistry.bleeding.id, 120, 0, true));
	}
	
	@SubscribeEvent
	public void onEntityHurt(LivingHurtEvent e) {
		if (PartySystem.friendlyFire(e.entityLiving, e.source)) {
			e.setCanceled(true);
			return;
		}
		
		if (e.source.getEntity() != null && e.source.getEntity() instanceof EntityLivingBase) 
			if (((EntityLivingBase) e.source.getEntity()).isPotionActive(AlfheimRegistry.berserk))
				e.ammount *= 1.2F;
			else
			if (((EntityLivingBase) e.source.getEntity()).isPotionActive(AlfheimRegistry.ninja))
				e.ammount *= 0.8F;
		
		if (AlfheimCore.enableMMO) {
			if (e.source.getEntity() != null && e.source.getEntity() instanceof EntityLivingBase && ((EntityLivingBase) e.source.getEntity()).isPotionActive(AlfheimRegistry.quadDamage)) {
				e.ammount *= 4.0F;
				SpellEffectHandler.sendPacket(Spells.QUADH, e.source.getEntity());
			}
			
			PotionEffect pe = e.entityLiving.getActivePotionEffect(AlfheimRegistry.nineLifes);
			nl: if (pe != null) {
				boolean blockable = e.source.damageType.equals(DamageSource.fall.damageType)		||
									e.source.damageType.equals(DamageSource.drown.damageType)		||
									e.source.damageType.equals(DamageSource.inFire.damageType)		||
									e.source.damageType.equals(DamageSource.onFire.damageType)		||
									e.source.damageType.equals(DamageSourceSpell.poison.damageType)	||
									e.source.damageType.equals(DamageSource.wither.damageType)		;
				
				if (blockable) {
					if (pe.amplifier == 4) {
						if (ASJUtilities.willEntityDie(e)) {
							if (e.source.damageType.equals(DamageSource.wither.damageType) && e.entityLiving.worldObj.rand.nextBoolean()) break nl;
							pe.amplifier = 0;
							pe.duration = 100;
							if (ASJUtilities.isServer()) AlfheimCore.network.sendToAll(new MessageEffect(e.entityLiving.getEntityId(), pe.potionID, pe.duration, pe.amplifier));
							e.setCanceled(true);
							return;
						}
					} else if (pe.amplifier == 0) {
						e.setCanceled(true);
						return;
					}
				} else if (e.source.getEntity() != null && e.source.getEntity() instanceof EntityLivingBase && e.source.getEntity().isEntityAlive() && e.entityLiving.worldObj.rand.nextInt(3) == 0) {
					e.source.getEntity().attackEntityFrom(e.source, e.ammount / 2);
				}
			}
			
			pe = e.entityLiving.getActivePotionEffect(AlfheimRegistry.stoneSkin);
			if (pe != null && !e.source.isMagicDamage() && !e.source.isDamageAbsolute()) {
				e.setCanceled(true);
				e.entityLiving.removePotionEffect(AlfheimRegistry.stoneSkin.id);
				e.entityLiving.addPotionEffect(new PotionEffect(Potion.field_76444_x.id, pe.duration, 3, true));
				return;
			}
			
			pe = e.entityLiving.getActivePotionEffect(AlfheimRegistry.butterShield);
			if (pe != null && pe.duration > 0 && e.source.isMagicDamage() && !e.source.isDamageAbsolute()) {
				e.setCanceled(true);
				if ((--pe.amplifier) <= 0) pe.duration = 0; // e.entityLiving.removePotionEffect(AlfheimRegistry.butterShield.id); <- ConcurrentModificationException :(
				if (ASJUtilities.isServer()) AlfheimCore.network.sendToAll(new MessageEffect(e.entityLiving.getEntityId(), pe.potionID, pe.duration, pe.amplifier));
				return;
			}
			
			// ################################################################ NOT CANCELING ################################################################
			
			pe = e.entityLiving.getActivePotionEffect(AlfheimRegistry.butterShield);
			if (!e.source.isMagicDamage() && !e.source.isDamageAbsolute() && pe != null && pe.duration > 0) {
				e.ammount /= 2.F;
				int dur = Math.max(pe.duration -= (e.ammount * 20), 0);
				if (ASJUtilities.isServer()) AlfheimCore.network.sendToAll(new MessageEffect(e.entityLiving.getEntityId(), pe.potionID, dur, pe.amplifier));
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent e) {
		if (AlfheimCore.enableMMO) {
			if (e.entityLiving instanceof EntityPlayer && !MinecraftServer.getServer().isSinglePlayer()) {
				e.entityLiving.clearActivePotions();
				e.entityLiving.addPotionEffect(new PotionEffect(AlfheimRegistry.leftFlame.id, AlfheimConfig.deathScreenAddTime, 0, true));
				e.entityLiving.getDataWatcher().updateObject(6, 1F);
			}
		
			Party pt = PartySystem.getMobParty(e.entityLiving);
			if (pt != null) pt.setDead(e.entityLiving, true);
		}
	}
	
	@SubscribeEvent
	public void onServerTick(ServerTickEvent e) {
		if (AlfheimCore.enableMMO) {
			if (e.phase == Phase.START) {
				SpellCastingSystem.tick();
				TimeStopSystem.tick();
			}
			for (String name : CardinalSystem.playerSegments.keySet()) {
				EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(name);
				if (player == null) CardinalSystem.playerSegments.get(name).target = new Target(null, false);
				else {
					Target tg = TargetingSystem.getTarget(player);
					if (tg != null && tg.target != null && (!tg.target.isEntityAlive() || Vector3.entityDistance(player, tg.target) > (tg.target instanceof IBossDisplayData ? 128 : 32)))
						TargetingSystem.setTarget(player, null, false);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent e) {
		if (AlfheimCore.enableMMO) {
			if (e.entityLiving.isPotionActive(AlfheimRegistry.leftFlame)) {
				PotionEffect pe = e.entityLiving.getActivePotionEffect(AlfheimRegistry.leftFlame);
				pe.duration--;
				if(!ASJUtilities.isServer()) SpellEffectHandlerClient.onDeathTick(e.entityLiving);
				if (pe.duration <= 0) e.entityLiving.removePotionEffect(pe.potionID);
				else e.setCanceled(true);
			}
			
			if (e.entityLiving.isDead) {
				Party pt = PartySystem.getMobParty(e.entityLiving);
				if (pt != null) pt.setDead(e.entityLiving, true);
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerUpdate(PlayerTickEvent e) {
		if (e.phase == Phase.START) return;
		EntityPlayer player = e.player;
		
//		player.rotationYaw = player.rotationPitch = 90;
		
		if (!player.capabilities.isCreativeMode) {
			if (AlfheimCore.enableElvenStory) {
				if (Flight.get(player) >= 0 && Flight.get(player) <= Flight.max()) {
					if (player.capabilities.isFlying) {
						Flight.sub(player, (player.isSprinting() ? 4 : (player.motionX != 0.0 || player.motionY > 0.0 || player.motionZ != 0.0) ? 2 : 1));
						if (player.isSprinting()) player.moveFlying(0F, 1F, 0.01F);
					} else Flight.add(player, Flight.get(player) < Flight.max() ? 1 : 0);
				}
				
				if (Flight.get(player) <= 0)	player.capabilities.isFlying = false;
			}
		}
	}
	
	public static void checkAddAttrs() {
		if (!AlfheimCore.enableElvenStory) return;
		for (Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			EntityPlayerMP player = (EntityPlayerMP) o;
			EnumRace.ensureExistance(player);
			Flight.ensureExistence(player);
		}
	}
	
	@SubscribeEvent
	public void onNewPotionEffect(LivingPotionEvent.Add.Post e) {
		if (ASJUtilities.isServer()) AlfheimCore.network.sendToAll(new MessageEffect(e.entityLiving.getEntityId(), e.effect.potionID, e.effect.duration, e.effect.amplifier));
	}
	
	@SubscribeEvent
	public void onChangedPotionEffect(LivingPotionEvent.Change.Post e) {
		if (ASJUtilities.isServer()) AlfheimCore.network.sendToAll(new MessageEffect(e.entityLiving.getEntityId(), e.effect.potionID, e.effect.duration, e.effect.amplifier, e.update));
	}
	
	@SubscribeEvent
	public void onFinishedPotionEffect(LivingPotionEvent.Remove.Post e) {
		if (ASJUtilities.isServer()) AlfheimCore.network.sendToAll(new MessageEffect(e.entityLiving.getEntityId(), e.effect.potionID, e.effect.duration, e.effect.amplifier));
	}
	
	@SubscribeEvent
	public void onEntityUpdate(EntityUpdateEvent e) {
		if (!e.entity.isEntityAlive()) return;
		if ((ASJUtilities.isServer() || AlfheimConfig.slowDownClients) && !e.entity.canEntityUpdate) {
			e.setCanceled(true);
			e.entity.canEntityUpdate = true;
		}
	}
	
	@SubscribeEvent
	public void onTileUpdate(TileUpdateEvent e) {
		if (!e.tile.canTileUpdate) {
			e.setCanceled(true);
			e.tile.canTileUpdate = true;
		}
	}
}