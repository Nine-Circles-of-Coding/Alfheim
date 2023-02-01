package alfheim.common.item.rod

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.item.ColorOverrideHelper
import alfheim.api.lib.LibResourceLocations
import alfheim.client.core.helper.InterpolatedIconHelper
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.*
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.helper.*
import alfheim.common.item.ItemMod
import alfheim.common.item.equipment.bauble.ItemPriestEmblem
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.*
import net.minecraft.entity.boss.IBossDisplayData
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import vazkii.botania.api.item.*
import vazkii.botania.api.mana.*
import vazkii.botania.common.Botania
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.relic.ItemThorRing
import java.awt.Color
import java.util.*
import vazkii.botania.common.core.helper.Vector3 as Bector3

open class ItemRodLightning(name: String = "rodLightning"): ItemMod(name), IManaUsingItem, IAvatarWieldable {
	
	init {
		maxStackSize = 1
		MinecraftForge.EVENT_BUS.register(this)
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) = Unit
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 1) {
			itemIcon = InterpolatedIconHelper.forItem(event.map, this)
		}
	}
	
	override fun getItemUseAction(par1ItemStack: ItemStack?) = EnumAction.bow
	
	override fun getMaxItemUseDuration(par1ItemStack: ItemStack?) = 72000
	
	override fun onPlayerStoppedUsing(stack: ItemStack?, world: World?, player: EntityPlayer?, itemInUse: Int) {
		super.onPlayerStoppedUsing(stack, world, player, itemInUse)
		ItemNBTHelper.setInt(stack, "target", -1)
	}
	
	override fun onUsingTick(stack: ItemStack, player: EntityPlayer, count: Int) {
		if (count == getMaxItemUseDuration(stack) || player.worldObj.isRemote) return
		val thor = (!RagnarokHandler.blockedPowers[0] && ItemThorRing.getThorRing(player) != null)
		val priest = (!RagnarokHandler.blockedPowers[0] && ItemPriestEmblem.getEmblem(0, player) != null)
		val prowess = IManaProficiencyArmor.Helper.hasProficiency(player)
		
		val color = ColorOverrideHelper.getColor(player, 0x0079C4)
		val innerColor = Color(color).brighter().brighter().rgb
		
		if (!ManaItemHandler.requestManaExactForTool(stack, player, getCost(thor, prowess, priest), false)) return
		val target = getTarget(player.worldObj, player, ItemNBTHelper.getInt(stack, "target", -1)) ?: return
		ItemNBTHelper.setInt(stack, "target", target.entityId)
		
		val shockspeed = getSpeed(thor, prowess, priest)
		val damage = getDamage(thor, prowess, priest)
		
		val (x1, y1, z1) = Vector3.fromEntityCenter(target).add(0.0, 0.75, 0.0).add(Vector3(target.lookVec).mul(-0.25))
		val (x2, y2, z2) = Vector3(x1, y1, z1).add(getHeadOrientation(target))
		
		val (x3, y3, z3) = Vector3.fromEntityCenter(player).add(0.0, 0.75, 0.0).add(Vector3(player.lookVec).mul(-0.25))
		val (x4, y4, z4) =  Vector3(x3, y3, z3).add(getHeadOrientation(player))
		
		if (count % (shockspeed / 10) == 0) {
			VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.LIGHTNING, player.dimension, x1, y1, z1, x2, y2, z2, 2.0, color.D, innerColor.D)
			VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.LIGHTNING, player.dimension, x3, y3, z3, x4, y4, z4, 2.0, color.D, innerColor.D)
		}
		
		if (count % shockspeed != 0) return
		if (AlfheimConfigHandler.realLightning && thor) {
			if (spawnLightning(player.worldObj, target.posX, target.posY, target.posZ)) {
				if (ManaItemHandler.requestManaExactForTool(stack, player, getCost(true, prowess, priest), true))
					target.attackEntityFrom(DamageSource.causePlayerDamage(player).setTo(ElementalDamage.ELECTRIC), damage)
			}
			return
		}
		if (ManaItemHandler.requestManaExactForTool(stack, player, getCost(thor, prowess, priest), true)) {
			target.attackEntityFrom(DamageSource.causePlayerDamage(player).setTo(ElementalDamage.ELECTRIC), damage)
			val (x5, y5, z5) = Vector3.fromEntityCenter(target)
			VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.LIGHTNING, player.dimension, x3, y3, z3, x5, y5, z5, 1.0, color.D, innerColor.D)
			player.worldObj.playSoundEffect(target.posX, target.posY, target.posZ, "ambient.weather.thunder", 100f, 0.8f + player.worldObj.rand.nextFloat() * 0.2f)
		}
		chainLightning(stack, target, player, thor, prowess, priest, color, innerColor)
	}
	
	fun getCost(thor: Boolean, prowess: Boolean, priest: Boolean) =
		COST + (if (thor) THOR_COST else 0) + (if (prowess) PROWESS_COST else 0) + (if (priest) PRIEST_COST else 0)
	
	fun getSpeed(thor: Boolean, prowess: Boolean, priest: Boolean) =
		SPEED - (if (thor) THOR_SPEEDUP else 0) - (if (prowess) PROWESS_SPEEDUP else 0) - (if (priest) PRIEST_SPEEDUP else 0)
	
	fun getDamage(thor: Boolean, prowess: Boolean, priest: Boolean) =
		DAMAGE + (if (thor) THOR_POWERUP else 0f) + (if (prowess) PROWESS_POWERUP else 0f) + (if (priest) PRIEST_POWERUP else 0f)
	
	fun getRange(thor: Boolean, prowess: Boolean, priest: Boolean) =
		CHAINRANGE + (if (thor) THOR_RANGEUP else 0f) + (if (prowess) PROWESS_RANGEUP else 0f) + (if (priest) PRIEST_RANGEUP else 0f)
	
	fun getTargetCap(thor: Boolean, prowess: Boolean, priest: Boolean) =
		TARGETS + (if (thor) THOR_TARGETS else 0) + (if (prowess) PROWESS_TARGETS else 0) + (if (priest) PRIEST_TARGETS else 0)
	
	override fun onItemRightClick(par1ItemStack: ItemStack, par2World: World?, par3EntityPlayer: EntityPlayer?): ItemStack {
		par3EntityPlayer!!.setItemInUse(par1ItemStack, getMaxItemUseDuration(par1ItemStack))
		return par1ItemStack
	}
	
	fun getTarget(world: World, player: EntityPlayer, trial_target: Int, range: Float = 12f): EntityLivingBase? {
		val potential = selectEntitiesWithinAABB(world, EntityLivingBase::class.java, player.boundingBox(range)) { it is IMob && it !is IBossDisplayData }
		
		if (trial_target >= 0) {
			val target = world.getEntityByID(trial_target)
			
			if (target != null && target is EntityCreature)
				if (target.health > 0f && !target.isDead && potential.contains(target)) {
					return target
				}
		}
		
		if (potential.size > 0)
			while (potential.size > 0) {
				val i = world.rand.nextInt(potential.size)
				if (!potential[i].isDead) {
					return potential[i]
				}
				
				potential.removeAt(i)
			}
		
		return null
	}
	
	fun chainLightning(stack: ItemStack, entity: EntityLivingBase, attacker: EntityLivingBase?, thor: Boolean, prowess: Boolean, priest: Boolean, color: Int, innerColor: Int): Boolean {
		if (entity !is EntityPlayer) {
			val range = getRange(thor, prowess, priest)
			val targets = getTargetCap(thor, prowess, priest)
			var dmg = getDamage(thor, prowess, priest)
			
			val alreadyTargetedEntities = ArrayList<Entity>()
			val lightningSeed = ItemNBTHelper.getLong(stack, "lightningSeed", 0L)
			val rand = Random(lightningSeed)
			var lightningSource = entity
			
			for (i in 0..targets) {
				val entities = selectEntitiesWithinAABB(entity.worldObj, Entity::class.java, lightningSource.boundingBox(range)) {
					it is IMob && it !is EntityPlayer && !alreadyTargetedEntities.contains(it)
				}
				entities.remove(lightningSource)
				if (entities.isEmpty()) break
				
				val target = entities[rand.nextInt(entities.size)] as EntityLivingBase
				if (attacker != null && attacker is EntityPlayer) {
					target.attackEntityFrom(DamageSource.causePlayerDamage(attacker as EntityPlayer?).setTo(ElementalDamage.ELECTRIC), dmg)
				} else {
					target.attackEntityFrom(DamageSource.causeMobDamage(attacker).setTo(ElementalDamage.ELECTRIC), dmg)
				}
				
				val (x1, y1, z1) = Vector3.fromEntityCenter(lightningSource)
				val (x2, y2, z2) = Vector3.fromEntityCenter(target)
				
				VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.LIGHTNING, entity.dimension, x1, y1, z1, x2, y2, z2, 1.0, color.D, innerColor.D)
				alreadyTargetedEntities.add(target)
				lightningSource = target
				--dmg
			}
			
			if (!entity.worldObj.isRemote) {
				ItemNBTHelper.setLong(stack, "lightningSeed", entity.worldObj.rand.nextLong())
			}
		}
		
		return super.hitEntity(stack, entity, attacker)
	}
	
	fun spawnLightning(world: World, posX: Double, posY: Double, posZ: Double) =
		world.addWeatherEffect(EntityLightningBolt(world, posX, posY, posZ))
	
	override fun isFull3D() = true
	
	override fun usesMana(stack: ItemStack) = true
	
	fun getHeadOrientation(entity: EntityLivingBase): Vector3 {
		val f1 = MathHelper.cos(-entity.rotationYaw * 0.017453292F - Math.PI.F)
		val f2 = MathHelper.sin(-entity.rotationYaw * 0.017453292F - Math.PI.F)
		val f3 = -MathHelper.cos(-(entity.rotationPitch - 90) * 0.017453292F)
		val f4 = MathHelper.sin(-(entity.rotationPitch - 90) * 0.017453292F)
		return Vector3((f2 * f3).D, f4.D, (f1 * f3).D)
	}
	
	override fun onAvatarUpdate(tile: IAvatarTile, stack: ItemStack) {
		val te = tile as TileEntity
		val world = te.worldObj
		val range = 18
		
		val color = 0x0079C4
		val innerColor = Color(color).brighter().brighter().rgb
		
		if (tile.currentMana >= COST_AVATAR && tile.isEnabled && tile.elapsedFunctionalTicks % 10 == 0) {
			val entities = selectEntitiesWithinAABB(world, EntityLivingBase::class.java, te.boundingBox(range)) { it !is EntityPlayer && it !is IBossDisplayData }
			
			if (entities.size == 0) return
			
			val trial_target = ItemNBTHelper.getInt(stack, "target", -1)
			var target: EntityLivingBase? = null
			
			if (trial_target >= 0) {
				val ttarget = world.getEntityByID(trial_target)
				
				if (ttarget != null && ttarget is EntityCreature)
					if (ttarget.health > 0f && !ttarget.isDead && entities.contains(ttarget)) {
						target = ttarget
					}
			}
			
			if (target == null) {
				while (entities.size > 0) {
					val i = world.rand.nextInt(entities.size)
					
					if (entities[i] is IMob && entities[i] !is EntityPlayer && entities[i] !is EntityPlayerMP) {
						val entity: EntityLivingBase = entities[i]
						if (!entity.isDead) {
							target = entity
							break
						}
					}
					
					entities.remove(entities[i])
				}
			}
			
			if (target != null) {
				ItemNBTHelper.setInt(stack, "target", target.entityId)
				
				val targetCenter = Bector3.fromEntityCenter(target).add(0.0, 0.75, 0.0).add(Bector3(target.lookVec).multiply(-0.25))
				val head = getHeadOrientation(target)
				val targetShift = targetCenter.copy().add(Bector3(head.x, head.y, head.z))
				
				val thisCenter = Bector3.fromTileEntityCenter(te).add(0.0, 0.5, 0.0)
				val thisShift = thisCenter.copy().add(0.0, 1.0, 0.0)
				
				if (tile.elapsedFunctionalTicks % 10 == 0) {
					Botania.proxy.lightningFX(world, targetCenter, targetShift, 2f, color, innerColor)
					Botania.proxy.lightningFX(world, thisCenter, thisShift, 2f, color, innerColor)
				}
				
				if (tile.elapsedFunctionalTicks % 100 == 0) {
					
					target.attackEntityFrom(DamageSource.causeMobDamage(null).setTo(ElementalDamage.ELECTRIC), DAMAGE)
					
					if (!world.isRemote) tile.recieveMana(-COST_AVATAR)
					
					val vect = Bector3.fromTileEntityCenter(te).add(0.0, 0.5, 0.0)
					
					Botania.proxy.lightningFX(world, vect, Bector3.fromEntityCenter(target), 1f, color, innerColor)
					
					world.playSoundEffect(target.posX, target.posY, target.posZ, "ambient.weather.thunder", 100f, 0.8f + world.rand.nextFloat() * 0.2f)
					
					@Suppress("BooleanLiteralArgument")
					chainLightning(stack, target, null, false, false, false, color, innerColor)
				}
			}
			
		}
	}
	
	override fun getOverlayResource(tile: IAvatarTile, stack: ItemStack) = LibResourceLocations.avatarLightning
	
	companion object {
		
		const val COST_AVATAR = 150
		
		const val COST = 300
		const val PRIEST_COST = 200
		const val THOR_COST = 700
		const val PROWESS_COST = 50
		
		const val SPEED = 90
		const val PRIEST_SPEEDUP = 30
		const val THOR_SPEEDUP = 10
		const val PROWESS_SPEEDUP = 10
		
		const val DAMAGE = 8f
		const val PRIEST_POWERUP = 3f
		const val THOR_POWERUP = 7f
		const val PROWESS_POWERUP = 2f
		
		const val CHAINRANGE = 7f
		const val PRIEST_RANGEUP = -1f
		const val THOR_RANGEUP = 1f
		const val PROWESS_RANGEUP = 1f
		
		const val TARGETS = 4
		const val PRIEST_TARGETS = 2
		const val THOR_TARGETS = -2
		const val PROWESS_TARGETS = 1
	}
}
