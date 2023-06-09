package alfheim.common.item.rod

import alexsocol.asjlib.*
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.ModInfo
import alfheim.api.item.ColorOverrideHelper
import alfheim.api.lib.LibResourceLocations
import alfheim.client.core.helper.InterpolatedIconHelper
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.item.ItemMod
import alfheim.common.item.equipment.bauble.ItemPriestEmblem
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.command.IEntitySelector
import net.minecraft.entity.*
import net.minecraft.entity.boss.IBossDisplayData
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import vazkii.botania.api.internal.IManaBurst
import vazkii.botania.api.item.*
import vazkii.botania.api.mana.*
import vazkii.botania.common.Botania
import java.awt.Color
import kotlin.math.*

open class ItemRodInterdiction(name: String = "rodInterdiction"): ItemMod(name), IManaUsingItem, IAvatarWieldable {
	
	init {
		maxStackSize = 1
		MinecraftForge.EVENT_BUS.register(this)
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 1) {
			itemIcon = InterpolatedIconHelper.forItem(event.map, this)
		}
	}
	
	override fun getItemUseAction(par1ItemStack: ItemStack?) = EnumAction.bow
	
	override fun getMaxItemUseDuration(par1ItemStack: ItemStack?) = 72000
	
	override fun isFull3D() = true
	
	override fun usesMana(stack: ItemStack) = true
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		player.setItemInUse(stack, getMaxItemUseDuration(stack))
		return stack
	}
	
	fun particleRing(world: World, x: Double, y: Double, z: Double, range: Int, r: Float, g: Float, b: Float) {
		val m = 0.15F
		val mv = 0.35F
		for (i in 0..359 step 8) {
			val rad = i.D * Math.PI / 180.0
			val dispx = x + 0.5 - cos(rad) * range.F
			val dispy = y + 0.5
			val dispz = z + 0.5 - sin(rad) * range.F
			
			Botania.proxy.wispFX(world, dispx, dispy, dispz, r, g, b, 0.2F, (Math.random() - 0.5).F * m, (Math.random() - 0.5).F * mv, (Math.random() - 0.5F).F * m)
		}
	}
	
	fun pushEntities(x: Double, y: Double, z: Double, range: Int, velocity: Double, player: EntityPlayer?, entities: List<Entity>): Boolean {
		var flag = false
		for (entity in entities) {
			if (player != null && !InteractionSecurity.canInteractWithEntity(player, entity)) continue
			
			val xDif = entity.posX - x
			val yDif = entity.posY - (y + 1)
			val zDif = entity.posZ - z
			val dist = sqrt(xDif * xDif + yDif * yDif + zDif * zDif)
			if (dist <= range) {
				entity.motionX = velocity * xDif
				entity.motionY = velocity * yDif
				entity.motionZ = velocity * zDif
				entity.fallDistance = 0f
				flag = true
			}
		}
		return flag
	}
	
	override fun onUsingTick(stack: ItemStack, player: EntityPlayer, count: Int) {
		val world = player.worldObj
		val x = player.posX
		val y = player.posY
		val z = player.posZ
		
		val priest = (!RagnarokHandler.blockedPowers[2] && ItemPriestEmblem.getEmblem(2, player) != null)
		val prowess = IManaProficiencyArmor.Helper.hasProficiency(player)
		
		val cost = getCost(prowess, priest)
		val range = getRange(prowess, priest)
		val velocity = getVelocity(prowess, priest)
		
		if (ManaItemHandler.requestManaExactForTool(stack, player, cost, false)) {
			val color = Color(ColorOverrideHelper.getColor(player, 0x0000FF))
			val r = color.red.F / 255f
			val g = color.green.F / 255f
			val b = color.blue.F / 255f
			if (count % 5 == 0) VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.SEAROD, world.provider.dimensionId, x, y, z, range.D, r.D, g.D, b.D)
			
			val entities = selectEntitiesWithinAABB(world, Entity::class.java, getBoundingBox(x, y, z).expand(range)) { PLAYER_SELECTOR.isEntityApplicable(it) }
			entities.remove(player)
			
			if (pushEntities(x, y, z, range, velocity, player, entities)) {
				if (count % 3 == 0) player.playSoundAtEntity("${ModInfo.MODID}:wind", 0.4F, 1F)
				ManaItemHandler.requestManaExactForTool(stack, player, cost, true)
			}
		}
	}
	
	fun getCost(prowess: Boolean, priest: Boolean) =
		COST + if (prowess) PROWESS_COST else 0 + if (priest) PRIEST_COST else 0
	
	fun getVelocity(prowess: Boolean, priest: Boolean) =
		VELOCITY + if (prowess) PROWESS_VELOCITY else 0.0 + if (priest) PRIEST_VELOCITY else 0.0
	
	fun getRange(prowess: Boolean, priest: Boolean) =
		RANGE + if (prowess) PROWESS_RANGE else 0 + if (priest) PRIEST_RANGE else 0
	
	override fun onAvatarUpdate(tile: IAvatarTile, stack: ItemStack) {
		val te = tile as TileEntity
		val world = te.worldObj
		val x = te.xCoord.D
		val y = te.yCoord.D
		val z = te.zCoord.D
		
		if (tile.currentMana >= AVATAR_COST) {
			if (tile.elapsedFunctionalTicks % 5 == 0) particleRing(world, x, y, z, RANGE, 0f, 0f, 1f)
			
			val entities = selectEntitiesWithinAABB(world, EntityLivingBase::class.java, getBoundingBox(x, y, z).expand(RANGE)) { AVATAR_SELECTOR.isEntityApplicable(it) }
			
			if (pushEntities(x, y, z, RANGE, VELOCITY, null, entities)) {
				if (tile.elapsedFunctionalTicks % 3 == 0) world.playSoundEffect(x, y, z, "${ModInfo.MODID}:wind", 0.4F, 1F)
				tile.recieveMana(-AVATAR_COST)
			}
		}
	}
	
	override fun getOverlayResource(tile: IAvatarTile, stack: ItemStack) = LibResourceLocations.avatarInterdiction
	
	companion object {
		
		const val COST = 5
		const val PROWESS_COST = -1
		const val PRIEST_COST = -2
		const val AVATAR_COST = 4
		
		const val VELOCITY = 0.05
		const val PROWESS_VELOCITY = 0.02
		const val PRIEST_VELOCITY = 0.07
		
		const val RANGE = 5
		const val PROWESS_RANGE = 1
		const val PRIEST_RANGE = 2
		
		object PLAYER_SELECTOR: IEntitySelector {
			
			override fun isEntityApplicable(e: Entity) =
				(e is EntityLivingBase && (e !is IBossDisplayData)) || (e is IProjectile && e !is IManaBurst)
		}
		
		object AVATAR_SELECTOR: IEntitySelector {
			
			override fun isEntityApplicable(e: Entity) =
				e is EntityLivingBase && e !is EntityPlayer && e !is IBossDisplayData
		}
	}
}
