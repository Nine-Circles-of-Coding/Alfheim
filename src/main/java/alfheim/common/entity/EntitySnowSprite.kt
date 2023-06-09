package alfheim.common.entity

import alexsocol.asjlib.*
import alfheim.api.entity.INiflheimEntity
import alfheim.common.core.handler.*
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.helper.*
import alfheim.common.entity.boss.EntityDedMoroz
import alfheim.common.item.material.ElvenResourcesMetas
import alfheim.common.world.dim.alfheim.biome.BiomeField
import alfheim.common.world.dim.alfheim.customgens.NiflheimLocationGenerator
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.event.entity.living.LivingDeathEvent
import ru.vamig.worldengine.*
import vazkii.botania.common.Botania
import vazkii.botania.common.entity.EntityFlyingCreature
import kotlin.math.*

class EntitySnowSprite(world: World): EntityFlyingCreature(world), INiflheimEntity {
	
	private var spawnPosition: ChunkCoordinates? = null
	
	init {
		setSize(0.25f, 0.25f)
	}
	
	override fun applyEntityAttributes() {
		super.applyEntityAttributes()
		
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 8.0
	}
	
	override fun updateEntityActionState() {
		super.updateEntityActionState()
		
		rotationYaw = (-atan2(motionX, motionZ) * 180 / Math.PI).F
		renderYawOffset = rotationYaw
	}
	
	override fun canBePushed() = true
	override fun collideWithEntity(entity: Entity) = Unit
	override fun collideWithNearbyEntities() = Unit
	override fun isAIEnabled() = true
	override fun canTriggerWalking() = false
	override fun doesEntityNotTriggerPressurePlate() = true
	override fun getDropItem() = Items.snowball!!
	override fun dropFewItems(hit: Boolean, looting: Int) {
		entityDropItem(if (rng.nextInt(20) == 0) ElvenResourcesMetas.IffesalDust.stack else ItemStack(dropItem, looting + 1), 0f)
	}
	
	private val immuneTo = arrayOf(DamageSource.inWall.damageType, DamageSource.drown.damageType, DamageSource.fall.damageType)
	
	override fun attackEntityFrom(src: DamageSource, amount: Float): Boolean {
		if (src.damageType in immuneTo) return false
		
		return super.attackEntityFrom(src, amount)
	}
	
	override fun onEntityUpdate() {
		Botania.proxy.sparkleFX(worldObj, posX + (Math.random() - 0.5) * 0.5, posY + (Math.random() - 0.5) * 0.5, posZ + (Math.random() - 0.5) * 0.5, (Math.random() * 0.25 + 0.25).F, 1f, 1f, 1f + Math.random().F * 0.25f, 10)
		
		motionY *= 0.6
		if (worldObj.rand.nextInt(600) == 0) motionY -= 1.0
		
		if (worldObj.provider.dimensionId != AlfheimConfigHandler.dimensionIDDomains && (worldObj.worldTime % 24000L).I !in 13333..22666 && !RagnarokHandler.winter) {
			if (!worldObj.isRemote)
				EntityAlfheimPixie(worldObj).also { it.setPosition(posX, posY, posZ) }.spawn()
			
			setDead()
		}
		
		super.onEntityUpdate()
	}
	
	override fun updateAITasks() {
		//super.updateAITasks();
		if (spawnPosition != null && (!worldObj.isAirBlock(spawnPosition!!.posX, spawnPosition!!.posY, spawnPosition!!.posZ) || spawnPosition!!.posY < 1)) {
			spawnPosition = null
		}
		
		if (spawnPosition == null || rand.nextInt(30) == 0 || spawnPosition!!.getDistanceSquared(posX.I, posY.I, posZ.I) < 4f) {
			spawnPosition = ChunkCoordinates(posX.I + rand.nextInt(7) - rand.nextInt(7), posY.I + rand.nextInt(6) - 2, posZ.I + rand.nextInt(7) - rand.nextInt(7))
		}
		
		val d0 = spawnPosition!!.posX.D + 0.5 - posX
		val d1 = spawnPosition!!.posY.D + 0.1 - posY
		val d2 = spawnPosition!!.posZ.D + 0.5 - posZ
		motionX += (sign(d0) * 0.5 - motionX) * 0.01
		motionY += (sign(d1) * 0.7 - motionY) * 0.01
		motionZ += (sign(d2) * 0.5 - motionZ) * 0.01
		val f = (atan2(motionZ, motionX) * 180.0 / Math.PI).F - 90f
		val f1 = MathHelper.wrapAngleTo180_float(f - rotationYaw)
		moveForward = 0.05f
		rotationYaw += f1
	}
	
	override fun setDead() {
		dead = true
		isDead = dead
		if (worldObj.isRemote)
			for (i in 0..11)
				Botania.proxy.sparkleFX(worldObj, posX + (Math.random() - 0.5) * 0.5, posY + (Math.random() - 0.5) * 0.5, posZ + (Math.random() - 0.5) * 0.5, (Math.random() * 0.25 + 0.25).F, 1f, 1f, 1f + Math.random().F * 0.25f, 10)
	}
	
	override fun getCanSpawnHere(): Boolean {
		if (worldObj.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim) return false
		
		setPosition(posX, posY + 5, posZ)
		val flagTime = (worldObj.worldTime % 24000L).I in 13333..22666 && worldObj.isRaining
		
		val flagBiome = if (WRATH_OF_THE_WINTER) {
			val cp = (worldObj.provider as? WE_WorldProvider)?.cp ?: return false
			WE_Biome.getBiomeAt(cp, posX.mfloor().toLong(), posZ.mfloor().toLong()) === BiomeField
		} else {
			val (xOff, zOff) = NiflheimLocationGenerator.portalXZ(worldObj)
			NiflheimLocationGenerator.yobaFunction2d(posX.mfloor() - xOff, posZ.mfloor() - zOff)
		}
		
		return flagTime && flagBiome && posY > 64 && super.getCanSpawnHere()
	}
	
	@SideOnly(Side.CLIENT)
	override fun isInRangeToRenderDist(distance: Double) = super.isInRangeToRenderDist(distance / 16.0)
}

object SpriteKillHandler {
	
	val regions = HashMap<Pair<Int, Int>, Int>()
	
	@SubscribeEvent
	fun onSpriteKilled(e: LivingDeathEvent) {
		if (e.entity.worldObj.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim) return
		
		val sprite = e.entityLiving as? EntitySnowSprite ?: return
		val killer = e.source.entity as? EntityPlayer ?: return
		
		val pointer = (sprite.posX.mfloor() shr 6) to (sprite.posZ.mfloor() shr 6)
		val kills = regions.getOrDefault(pointer, 0) + 1
		
		if (kills >= 16) {
			val ded = EntityDedMoroz(sprite.worldObj, sprite.posX, sprite.posY, sprite.posZ)
			ded.attackTarget = killer
			ded.spawn()
			
			ASJUtilities.say(killer, "alfheimmisc.ded.awakening")
			
			regions[pointer] = 0
		} else {
			regions[pointer] = kills
		}
	}
}
