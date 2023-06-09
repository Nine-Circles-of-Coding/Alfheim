package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.item.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.item.ItemStack
import net.minecraft.potion.*
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World
import vazkii.botania.common.brew.ModPotions
import kotlin.math.sqrt

class EntityThrownPotion: EntityThrowable {
	
	constructor(world: World): super(world) {
		stack = ItemStack(AlfheimItems.splashPotion)
		effects = emptyList()
		
		color = 0xFFFFFF
	}
	
	constructor(world: World, st: ItemStack): super(world) {
		stack = st
		val brew = stack.item as ItemSplashPotion
		effects = brew.getBrew(stack).getPotionEffects(stack)
		color = brew.getColor(stack)
	}
	
	constructor(player: EntityPlayer, st: ItemStack): super(player.worldObj, player) {
		stack = st
		val brew = stack.item as ItemSplashPotion
		effects = brew.getBrew(stack).getPotionEffects(stack)
		color = brew.getColor(stack)
	}
	
	val effects: List<PotionEffect>
	val stack: ItemStack
	
	var color
		get() = dataWatcher.getWatchableObjectInt(31)
		set(value) {
			dataWatcher.updateObject(31, value)
		}
	
	override fun entityInit() {
		super.entityInit()
		dataWatcher.addObject(30, 0.1f)
		dataWatcher.setObjectWatched(30)
		
		dataWatcher.addObject(31, 0)
		dataWatcher.setObjectWatched(31)
	}
	
	val peClear = PotionEffect(ModPotions.clear.id, 0, 0)
	
	override fun onImpact(movingObject: MovingObjectPosition?) {
		if (worldObj.isRemote || movingObject == null || effects.isEmpty()) return setDead()
		
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.POTION, dimension, posX, posY, posZ, color.D, if (effects.contains(peClear)) 1.0 else 0.0)
		
		val list = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox.expand(5.0, 2.5, 5.0))
		if (list.isEmpty()) return setDead()
		list.forEach { living ->
			val d0 = getDistanceSqToEntity(living)
			
			if (d0 >= 16.0) return@forEach
			var d1 = 1.0 - sqrt(d0) / 4.0
			
			if (living === movingObject.entityHit) d1 = 1.0
			
			if (effects.any { Potion.potionTypes[it.potionID].isBadEffect }) {
				if (!InteractionSecurity.canHurtEntity(thrower ?: return@forEach, living)) return@forEach
			} else {
				if (!InteractionSecurity.canInteractWithEntity(thrower ?: return@forEach, living)) return@forEach
			}
			
			for (e: PotionEffect in effects) {
				if (!Potion.potionTypes[e.potionID].isInstant) {
					val j = (d1 * e.duration.D + 0.5).I
					
					if (j <= 20) continue
					living.addPotionEffect(PotionEffect(e.potionID, j, e.amplifier))
					continue
				}
				
				Potion.potionTypes[e.potionID].affectEntity(thrower, living, e.amplifier, d1)
			}
		}
		
		setDead()
	}
	
	override fun getGravityVelocity() = dataWatcher.getWatchableObjectFloat(30)
	
	public override fun func_70183_g() = -10f
	
	public override fun func_70182_d() = 1f
}
