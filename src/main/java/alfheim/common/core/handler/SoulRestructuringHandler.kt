package alfheim.common.core.handler

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.block.tile.TileManaInfuser
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.item.AlfheimItems
import alfheim.common.item.relic.ItemFlugelSoul
import cpw.mods.fml.common.eventhandler.*
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.util.DamageSource
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.living.*
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent
import vazkii.botania.common.Botania
import vazkii.botania.common.block.tile.TileBrewery
import vazkii.botania.common.entity.EntityDoppleganger
import vazkii.botania.common.core.helper.Vector3 as Bector3

object SoulRestructuringHandler {
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun onGaiaSummoned(e: EntityJoinWorldEvent) {
		val gaia = e.entity as? EntityDoppleganger ?: return
		val world = gaia.worldObj
		val (x, y, z) = gaia.source
		val infuser = world.getTileEntity(x, y + 2, z) as? TileManaInfuser ?: return
		
		infuser.deGaiaingTime = 20
		world.setBlockMetadataWithNotify(x, y + 2, z, 2, 3)
		world.markBlockForUpdate(x, y + 2, z)
	}
	
	@SubscribeEvent
	fun onGaiaUpdate(e: LivingUpdateEvent) {
		val gaia = e.entity as? EntityDoppleganger ?: return
		
		noWaterCheat(gaia)
		
		val world = gaia.worldObj
		val (x, y, z) = gaia.source
		val infuser = world.getTileEntity(x, y + 2, z) as? TileManaInfuser ?: return
		
		if (infuser.blockMetadata != 2) return
		
		infuser.deGaiaingTime = 20
		world.markBlockForUpdate(x, y + 2, z)
		if (!gaia.isAggored && gaia.invulTime > 0 && gaia.mobSpawnTicks == EntityDoppleganger.MOB_SPAWN_TICKS) {
			infuser.prepareParticles()
			return
		}
		
		// like 3rd tier :D
		gaia.heal(0.1f)
		
		if (gaia.ticksExisted % 1200 == 600) {
			gaia.playersAround.forEach {
				world.addWeatherEffect(EntityLightningBolt(world, it.posX, it.posY, it.posZ))
			}
		}
		
		if (gaia.ticksExisted % 100 == 50) {
			gaia.playersAround.random()?.also {
				it.attackEntityFrom(DamageSourceSpell.anomaly, 2f)
				
				val (i, j, k) = TileManaInfuser.PYLONS.random()
				val v = Bector3(x.D, y.D, z.D).add(0.5).add(i.D, j.D + 2.8, k.D)
				Botania.proxy.lightningFX(gaia.worldObj, v, Bector3.fromEntity(it), 2f, gaia.worldObj.rand.nextLong(), 0, 0xFF0000)
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun onGaiaHurt(e: LivingHurtEvent) {
		val gaia = e.entity as? EntityDoppleganger ?: return
		val (x, y, z) = gaia.source
		val infuser = gaia.worldObj.getTileEntity(x, y + 2, z) as? TileManaInfuser ?: return
		
		if (infuser.blockMetadata != 2) return
		
		infuser.soulParticlesTime = 20
		VisualEffectHandler.sendPacket(VisualEffects.GAIA_SOUL, gaia.dimension, x.D, y + 2.0, z.D)
		
		// reflecting 10% damage
		e.source.entity?.let {
			if (!it.attackEntityFrom(e.source, e.ammount * 0.1f))
				it.attackEntityFrom(DamageSource.causeMobDamage(e.entityLiving), e.ammount * 0.1f)
		}
		e.ammount *= 0.9f
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun onGaiaDied(e: LivingDeathEvent) {
		val gaia = e.entity as? EntityDoppleganger ?: return
		val world = gaia.worldObj
		val (x, y, z) = gaia.source
		val infuser = world.getTileEntity(x, y + 2, z) as? TileManaInfuser ?: return
		
		if (infuser.blockMetadata != 2) return
		
		run exp@{
			if (world.isRemote) {
				return@exp
			}
			
			val soul = (world.getTileEntity(x, y + 5, z) as? TileBrewery)?.get(0)
			
			if (soul?.item === AlfheimItems.flugelSoul && ItemFlugelSoul.getBlocked(soul) > 0 && (gaia.isHardMode || gaia.rng.nextBoolean())) {
				world.setBlockMetadataWithNotify(x, y + 2, z, 0, 3)
				ItemFlugelSoul.setDisabled(soul, ItemFlugelSoul.getBlocked(soul), false)
				return@exp
			}
			
			world.newExplosion(null, x.D, y + 2.0, z.D, 10f, true, false)
			world.setBlockToAir(x, y + 2, z)
		}
		
		infuser.doneParticles()
		
		e.isCanceled = true
		
		// from #onDeath
		val entity = e.source.entity
		val entitylivingbase = gaia.func_94060_bK()
		if (gaia.scoreValue >= 0 && entitylivingbase != null) {
			entitylivingbase.addToPlayerScore(gaia, gaia.scoreValue)
		}
		entity?.onKillEntity(gaia)
		gaia.dead = true
		gaia.func_110142_aN().func_94549_h()
		gaia.worldObj.setEntityState(gaia, 3.toByte())
	}
	
	private fun noWaterCheat(gaia: EntityDoppleganger) {
		val world = gaia.worldObj
		val (x, y, z) = Vector3.fromEntity(gaia).mf()
		val range = -3..3
		
		for (i in range)
			for (j in range)
				for (k in range)
					if (world.getBlock(x + i, y + j, z + k).material.isLiquid)
						world.setBlockToAir(x + i, y + j, z + k)
	}
}