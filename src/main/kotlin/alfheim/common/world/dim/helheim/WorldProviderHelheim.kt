package alfheim.common.world.dim.helheim

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper.toVec3
import alexsocol.patcher.event.PlayerEatingEvent
import alfheim.api.event.SpellCastEvent
import alfheim.client.render.world.*
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.tile.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.AlfheimConfigHandler.dimensionIDHelheim
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.*
import alfheim.common.world.dim.domains.WorldProviderDomains
import alfheim.common.world.dim.helheim.gen.WorldGenHelheim
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.entity.player.*
import net.minecraft.potion.PotionEffect
import net.minecraft.util.*
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.client.event.EntityViewRenderEvent
import net.minecraftforge.event.entity.living.*
import net.minecraftforge.event.entity.player.*
import net.minecraftforge.event.world.*
import org.lwjgl.opengl.GL11
import ru.vamig.worldengine.*
import ru.vamig.worldengine.standardcustomgen.WE_TerrainGenerator
import java.awt.Color

class WorldProviderHelheim: WE_WorldProvider() {
	
	init {
		dimensionId = dimensionIDHelheim
		hasNoSky = true
		rainfall = 0f
	}
	
	override fun getCloudRenderer(): IRenderHandler {
//		val prev = mc.renderGlobal.cloudTickCounter
//		mc.renderGlobal.cloudTickCounter = 0
//		mc.renderGlobal.renderCloudsFancy(0f)
//		mc.renderGlobal.cloudTickCounter = prev
		
		return DummyRenderHandler
	}
	
	override fun genSettings(cp: WE_ChunkProvider) {
		cp.createChunkGen_List.clear()
		cp.decorateChunkGen_List.clear()
		
		WE_Biome.setBiomeMap(cp, 1.0, 1, 1.0, 1.0)
		
		val terrainGenerator = WE_TerrainGenerator()
		terrainGenerator.worldStoneBlock = AlfheimBlocks.helheimBlock
		terrainGenerator.worldSeaGen = false
		cp.createChunkGen_List.add(terrainGenerator)
		
		WE_Biome.addBiomeToGeneration(cp, BiomeHelheim)
		
		cp.decorateChunkGen_List.add(WorldGenHelheim)
	}
	
	override fun generateLightBrightnessTable() {
		val modifier = 0.0f
		for (steps in 0..15) {
			val var3 = 1.0f - steps / 15.0f
			lightBrightnessTable[steps] = ((0.0f + var3) / (var3 * 3.0f + 1.0f) * (1.0f - modifier) + modifier) * 3
		}
	}
	
	override fun getRandomizedSpawnPoint(): ChunkCoordinates {
		val (x, _, z) = Vector3().rand().sub(0.5).mul(1, 0, 1).normalize().mul(Math.random() * 3000 + 2000).I
		return ChunkCoordinates(x, 300, z)
	}
	
	override fun getSpawnPoint() = randomizedSpawnPoint
	override fun getDimensionName() = "Helheim"
	override fun getSkyRenderer() = DummyRenderHandler
	override fun getWeatherRenderer() = DummyRenderHandler
	override fun isSurfaceWorld() = false
	override fun shouldMapSpin(entity: String?, x: Double, y: Double, z: Double) = true
	override fun getBiomeGenForCoords(x: Int, z: Int) = BiomeHelheim
	override fun isBlockHighHumidity(x: Int, y: Int, z: Int) = false
	override fun canSnowAt(x: Int, y: Int, z: Int, checkLight: Boolean) = false
	override fun calculateCelestialAngle(time: Long, partialTicks: Float) = 0f
	override fun getMoonPhase(time: Long) = 4
	override fun canCoordinateBeSpawn(x: Int, z: Int) = worldObj.getTopBlock(x, z) === AlfheimBlocks.helheimBlock
	override fun getHorizon() = worldObj.height / 4.0 - 800
	override fun getSkyColor(cameraEntity: Entity?, partialTicks: Float) = Color(0x222222).toVec3()
	override fun getFogColor(sunAngle: Float, partialTicks: Float) = Color(0x222222).toVec3()
	override fun canBlockFreeze(x: Int, y: Int, z: Int, byWater: Boolean) = false
	override fun canRespawnHere() = false
	override fun getRespawnDimension(player: EntityPlayerMP?) = AlfheimConfigHandler.dimensionIDNiflheim
	
	companion object {
		
		init {
			eventForge()
		}
		
		fun forbid(e: LivingEvent) = forbid(e.entityLiving)
		
		fun forbid(e: EntityLivingBase?) = e?.dimension == dimensionIDHelheim && if (e is EntityPlayer) !e.capabilities.isCreativeMode else true
		
		@SubscribeEvent
		fun onLivingUpdate(e: LivingEvent.LivingUpdateEvent) {
			if (!forbid(e)) return
			
			val target = e.entityLiving
			
			if (target is EntityPlayer) {
				target.capabilities.allowFlying = false
				target.capabilities.isFlying = false
			}
			
			target.ridingEntity?.apply {
				target.dismountEntity(this)
				riddenByEntity = null
			}
			target.ridingEntity = null
			
			target.noClip = false
			
			if (target.worldObj.isRemote) return
			val iterator = target.activePotionsMap.keys.iterator()
			while (iterator.hasNext()) {
				val id = iterator.next() as Int
				if (id == AlfheimConfigHandler.potionIDWisdom) continue
				
				iterator.remove()
				val whyIsThereNPE = target.activePotionsMap[id] as? PotionEffect ?: continue
				target.onFinishedPotionEffect(whyIsThereNPE)
			}
		}
		
		@SubscribeEvent
		fun onLivingAttacked(e: LivingAttackEvent) {
			e.isCanceled = forbid(e)
		}
		
		@SubscribeEvent
		fun onLivingHurt(e: LivingHurtEvent) {
			e.isCanceled = forbid(e)
		}
		
		@SubscribeEvent
		fun onLivingHeal(e: LivingHealEvent) {
			e.isCanceled = forbid(e)
		}
		
		@SubscribeEvent
		fun onPlayerEating(e: PlayerEatingEvent) {
			e.entityPlayer ?: return
			e.isCanceled = forbid(e)
		}
		
		@SubscribeEvent
		fun onLivingDeath(e: LivingDeathEvent) {
			e.isCanceled = forbid(e)
		}
		
		@SubscribeEvent
		fun onBlockBreak(e: BlockEvent.BreakEvent) {
			e.isCanceled = forbid(e.player)
		}
		
		@SubscribeEvent
		fun onBlockPlace(e: BlockEvent.PlaceEvent) {
			e.isCanceled = forbid(e.player)
		}
		
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		fun onBlockMultiPlace(e: BlockEvent.MultiPlaceEvent) {
			e.isCanceled = forbid(e.player)
		}
		
		@SubscribeEvent(priority = EventPriority.LOWEST)
		fun onBlockHarvest(e: BlockEvent.HarvestDropsEvent) {
			if (!WorldProviderDomains.forbid(e.harvester)) return
			
			e.drops.clear()
			e.dropChance = 0f
		}
		
		@SubscribeEvent
		fun onBlockBreakSpeed(e: PlayerEvent.BreakSpeed) {
			e.isCanceled = forbid(e.entityPlayer)
		}
		
		@SubscribeEvent
		fun onBlockHarvest(e: PlayerEvent.HarvestCheck) {
			if (!e.success) return
			e.success = !forbid(e.entityPlayer)
		}
		
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		fun onInteract(e: PlayerInteractEvent) {
			if (!forbid(e.entityPlayer)) return
			
			val soul = e.world.getTileEntity(e.x, e.y, e.z)
			if (soul is TileVafthrudnirSoul || soul is TileRainbowManaFlame && soul.exit) return
			
			val stack = e.entityPlayer.heldItem
			if (e.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR && stack?.item === AlfheimItems.elvenResource && stack.meta == ElvenResourcesMetas.WisdomBottle.I) return
			
			e.isCanceled = true
		}
		
		@SubscribeEvent
		fun onExplosion(e: ExplosionEvent.Start) {
			e.isCanceled = e.world.provider.dimensionId == dimensionIDHelheim
		}
		
		@SubscribeEvent
		fun onSpellCast(e: SpellCastEvent.Pre) {
			e.isCanceled = forbid(e.caster)
		}
		
		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		fun changeFogDistance(e: EntityViewRenderEvent.FogDensity) {
			if (mc.thePlayer.dimension != dimensionIDHelheim || mc.thePlayer.capabilities.isCreativeMode) return
			
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP)
			e.density = 0.2f
			e.isCanceled = true
		}
	}
}
