package alfheim.common.core.handler

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.*
import alfheim.api.entity.*
import alfheim.api.event.PlayerInteractAdequateEvent
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.item.equipment.bauble.ItemPendant
import alfheim.common.item.equipment.bauble.ItemPendant.Companion.EnumPrimalWorldType.*
import alfheim.common.network.M1d
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.Message1d
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.*
import net.minecraft.potion.Potion
import net.minecraft.util.MathHelper
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.living.LivingEvent.*
import kotlin.math.*

object SheerColdHandler {
	
	const val TAG_SHEER_COLD = "${ModInfo.MODID}.SheerCold"
	
	var EntityLivingBase.cold: Float
		get() = entityData.getFloat(TAG_SHEER_COLD)
		set(value) {
			entityData.setFloat(TAG_SHEER_COLD, value)
			
			if (this is EntityPlayerMP)
				NetworkService.sendTo(Message1d(M1d.COLD, value.D), this)
		}
	
	@SubscribeEvent
	fun onLivingUpdate(e: LivingUpdateEvent) {
		val target = e.entityLiving
		if (target.worldObj.isRemote || !target.isEntityAlive) return
		
		if (target is EntityPlayerMP && target.capabilities.isCreativeMode) {
			target.cold = 0f
			return
		}
		
		val event = SheerColdTickEvent(target)
		if (MinecraftForge.EVENT_BUS.post(event)) return
		
		var defaultDelta = if (target.cold == 0f) 0f else 0.5f * target.cold.sign * -1
		
		if (defaultDelta != 0f) run {
			val (x, y, z) = Vector3.fromEntity(target).mf()
			
			for (i in x.bidiRange(2))
				for (j in y.bidiRange(2))
					for (k in z.bidiRange(2)) {
						val near = target.worldObj.getBlock(i, j, k)
						
						defaultDelta = when (near) {
							in AlfheimAPI.coldBlocks -> if (defaultDelta > 0) 1f else continue
							in AlfheimAPI.warmBlocks -> if (defaultDelta < 0) -1f else continue
							else                     -> continue
						}
						
						return@run
					}
		}
		
		var delta = event.delta ?: if (target.cold > 0) max(-target.cold, defaultDelta) else min(-target.cold, defaultDelta)
		if (delta.isNaN()) delta = 0f
		if (event.delta != null && (event.delta!! > 0 || target.cold > 0) && defaultDelta == -1f) delta = max(defaultDelta + delta, -target.cold)
		if (event.delta != null && (event.delta!! < 0 || target.cold < 0) && defaultDelta == 1f) delta = min(defaultDelta + delta, -target.cold)
		delta = MathHelper.clamp_float(delta, -100f, 100f)
		
		target.cold = MathHelper.clamp_float(target.cold + delta, -100f, 100f)
		if (EntityList.getEntityString(target) in AlfheimConfigHandler.overcoldBlacklist) target.cold = min(0f, target.cold)
		if (EntityList.getEntityString(target) in AlfheimConfigHandler.overheatBlacklist) target.cold = max(0f, target.cold)
		
		if (target.cold >= 100f && target !is INiflheimEntity && !canProtect(target, NIFLHEIM)) target.attackEntityFrom(DamageSourceSpell.nifleice, target.maxHealth * 0.01f + 0.15f)
		if (target.cold <= -100f && target !is IMuspelheimEntity && !canProtect(target, MUSPELHEIM)) target.attackEntityFrom(DamageSourceSpell.soulburn, target.maxHealth * 0.01f + 0.15f)
	}
	
	private fun canProtect(target: EntityLivingBase, type: ItemPendant.Companion.EnumPrimalWorldType): Boolean {
		if (target !is EntityPlayer) return false
		return ItemPendant.canProtect(target, type, 1)
	}
	
	val neutralSounds = arrayOf("bat.idle", "cat.meow", "chicken.say", "cow.say", "pig.say", "sheep.say", "wolf.bark")
	val hostileSounds = arrayOf("blaze.breathe", "creeper.say", "enderdragon.growl", "enderdragon.wings", "endermen.idle", "endermen.scream", "ghast.moan", "ghast.scream", "magmacube.big", "silverfish.say", "skeleton.say", "slime.big", "spider.say", "wither.idle", "wolf.growl", "zombie.say", "zombiepig.zpig")
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun resetCold(e: LivingDeathEvent) {
		e.entityLiving.cold = 0f
	}
	
	@SubscribeEvent
	fun onPlayerOvercold(e: LivingUpdateEvent) {
		val player = e.entityLiving as? EntityPlayer ?: return
		if (ItemPendant.canProtect(player, NIFLHEIM, 0)) return
		
		val cold = player.cold
		if (cold < 25f) return
		
		val amp = (cold / 25).I - 1
		player.addPotionEffect(PotionEffectU(Potion.moveSlowdown.id, 100, amp))
	}
	
	// additional "lag" with controls - AlfheimHookHandler#updatePlayerMoveState
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	fun onPlayerOverheat(e: LivingUpdateEvent) {
		val player = mc.thePlayer
		if (player !== e.entityLiving) return
		if (ItemPendant.canProtect(player, MUSPELHEIM, 0)) return
		
		val heat = -player.cold
		
		if (heat < 25f) return
		if (player.rng.nextInt(1000) == 0) return player.playSoundAtEntity("mob." + (if (ASJUtilities.chance((heat + 50) * -2)) hostileSounds else neutralSounds).random(), 1f, 1f)
		
		if (heat < 50f) return
		if (player.rng.nextInt(3000) == 0) {
			var entity: Entity? = null
			
			if (ASJUtilities.chance(25))
				while (entity !is EntityLivingBase) {
					entity = EntityList.createEntityByName(EntityList.stringToClassMapping.keys.random(player.rng)!!.toString(), mc.theWorld)
				}
			else {
				val item = Item.itemRegistry.toList().random(player.rng) as? Item ?: return
				
				entity = EntityItem(mc.theWorld, 0.0, 0.0, 0.0, ItemStack(item))
			}
			
			do {
				val (x, _, z) = Vector3().rand().mul(64).add(player)
				entity.setPosition(x, mc.theWorld.getTopSolidOrLiquidBlock(x.I, z.I) + 1.0, z)
			} while (!ASJUtilities.isNotInFieldOfVision(entity, player))
			
			entity.spawn()
		}
	}
	
	@SubscribeEvent
	fun weakHands(e: PlayerInteractAdequateEvent) {
		if (abs(e.player.cold) < 90) return

		if (e.player.cold > 0 && ItemPendant.canProtect(e.player, NIFLHEIM, 0)) return
		if (e.player.cold < 0 && ItemPendant.canProtect(e.player, MUSPELHEIM, 0)) return

		if (ASJUtilities.chance(0.5))
			e.player.dropOneItem(true)
	}
	
	/**
	 * Event for calculating temperature [delta] to add to [entity]'s temperature.
	 *
	 * General contract for handling this event:
	 * * if you want to add some source, such as cold weather — use [high][EventPriority.HIGH] priority, so protection is processed after it;
	 * * if you want to add some protection, such as warm closes — use [low][EventPriority.LOW] priority;
	 * * use [lowest][EventPriority.LOWEST] and [highest][EventPriority.HIGHEST] priority for special cases;
	 * * [normal][EventPriority.NORMAL] priority at your discretion.
	 *
	 * Canceling event is allowed only if you want to *lock* [entity]'s temperature for some reason.
	 */
	@Cancelable
	class SheerColdTickEvent(entity: EntityLivingBase, var delta: Float? = null): LivingEvent(entity)
}
