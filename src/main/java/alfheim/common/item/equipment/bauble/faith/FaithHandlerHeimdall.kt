package alfheim.common.item.equipment.bauble.faith

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.item.ColorOverrideHelper
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.item.*
import alfheim.common.item.equipment.bauble.*
import alfheim.common.item.equipment.bauble.faith.IFaithHandler.FaithBauble.*
import alfheim.common.item.relic.ItemHeimdallRing
import alfheim.common.network.*
import alfheim.common.network.packet.Message0dS
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.potion.*
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.fluids.IFluidBlock
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.block.tile.TileBifrost
import vazkii.botania.common.item.ModItems
import java.awt.Color
import kotlin.math.abs

object FaithHandlerHeimdall: IFaithHandler {
	
	init {
		eventForge()
	}
	
	override fun onWornTick(stack: ItemStack, player: EntityPlayer, type: IFaithHandler.FaithBauble) {
		if (RagnarokHandler.blockedPowers[4]) return
		
		when (type) {
			EMBLEM -> onEmblemWornTick(stack, player)
			CLOAK  -> onCloakWornTick(player)
		}
	}
	
	fun onEmblemWornTick(stack: ItemStack, player: EntityPlayer) {
		bifrostPlatform(player, stack)
		
		val b = player.isPotionActive(Potion.blindness)
		val nv = player.getActivePotionEffect(Potion.nightVision)
		
		if (!b && nv != null && nv.duration > 50) return

		if (!player.worldObj.isRemote && ManaItemHandler.requestManaExact(stack, player, 1, true)) {
			player.addPotionEffect(PotionEffect(Potion.nightVision.id, 100, 0))
			if (b) player.removePotionEffect(Potion.blindness.id)
		}
	}
	
	fun onCloakWornTick(player: EntityPlayer) {
		if (!player.worldObj.isRemote || !player.isSprinting || player.jumpTicks != 10) return
		
		val look = player.lookVec
		val dist = 6.0
		val (x, y, z) = Vector3.fromEntity(player).add(Vector3(look).mul(dist))
		
		if (!player.worldObj.getBlock(x.I, y.I, z.I).isNormalCube && !player.worldObj.getBlock(x.I, y.I + 1, z.I).isNormalCube) {
			player.isJumping = false
			NetworkService.sendToServer(Message0dS(M0ds.HEIMBLINK))
		}
	}
	
	fun bifrostPlatform(player: EntityPlayer, emblem: ItemStack) {
		if (RagnarokHandler.ragnarok || player.capabilities.isFlying) return
		
		val world = player.worldObj
		if (world.isRemote) return
		
		if (player.heldItem?.item !== ModItems.rainbowRod) return
		if (!ManaItemHandler.requestManaExact(emblem, player, 10, false)) return
		val motVec = getMotionVec(player)
		val (x, y, z) = Vector3(player.posX + motVec.x, (player.posY + if (player.isSneaking) -2.99 else -0.99).mfloor(), player.posZ + motVec.z).mf()
		
		if (y < 0 || y >= 256) return
		
		for (i in -2..2)
			for (k in -2..2) {
				if (abs(i) == 2 && abs(k) == 2) continue
				
				if (InteractionSecurity.isPlacementBanned(player, x + i, y, z + k, world, ModBlocks.bifrost))
					continue
				
				val block = world.getBlock(x + i, y, z + k)
				if (block is IFluidBlock) continue
				
				if (block.isAir(world, x + i, y, z + k) || block.isReplaceable(world, x + i, y, z + k)) {
					world.setBlock(x + i, y, z + k, ModBlocks.bifrost)
					
					val tileBifrost = world.getTileEntity(x + i, y, z + k) as TileBifrost
					
					tileBifrost.ticks = 5
					player.fallDistance = 0f
					ManaItemHandler.requestManaExact(emblem, player, 10, true)
				} else if (block == ModBlocks.bifrost) {
					val tileBifrost = world.getTileEntity(x + i, y, z + k) as TileBifrost
					
					if (tileBifrost.ticks < 2) {
						tileBifrost.ticks = 5
						ManaItemHandler.requestManaExact(emblem, player, 10, true)
					}
				}
			}
	}
	
	fun getMotionVec(e: Entity): Vector3 {
		if (e is EntityPlayer) {
			val last = Vector3(e.prevPosX, e.prevPosY, e.prevPosZ)
			val vec = Vector3.fromEntity(e).sub(last)
			if (vec.length() < 10)
				return vec
		}
		
		return Vector3(e.motionX, e.motionY, e.motionZ)
	}
	
	@SubscribeEvent
	fun onLivingHurt(e: LivingHurtEvent) {
		if (RagnarokHandler.blockedPowers[4]) return
		
		if (e.source.damageType != "player" || Math.random() > 0.1) return
		val player = e.source.entity as? EntityPlayer ?: return
		if (ItemPriestEmblem.getEmblem(4, player) == null) return
		val lvl = getGodPowerLevel(player)
		e.ammount *= (lvl * 0.1f / 7) + 1.05f
	}
	
	override fun getGodPowerLevel(player: EntityPlayer): Int {
		if (RagnarokHandler.blockedPowers[4]) return 0
		
		var lvl = 0
		
		if (player.inventory.hasItemStack(ItemStack(AlfheimItems.gjallarhorn))) lvl += 4
		if (ItemPriestCloak.getCloak(4, player) != null) lvl += 3
		if (ItemPriestEmblem.getEmblem(4, player) != null) lvl += 2
		if (ItemHeimdallRing.getHeimdallRing(player) != null) lvl += 1
		if (player.inventory.hasItemStack(ItemStack(ModItems.rainbowRod))) lvl += 1
		
		return lvl
	}
	
	override fun doParticles(stack: ItemStack, player: EntityPlayer) {
		if (player.worldObj.getBlock(player, y = -1) inl arrayOf(ModBlocks.bifrost, ModBlocks.bifrostPerm)) return
		
		val color = Color(ColorOverrideHelper.getColor(player, ItemIridescent.rainbowColor()))
		val r = color.red / 255f
		val g = color.green / 255f
		val b = color.blue / 255f
		
		val (x, y, z) = Vector3.fromEntity(player)
		
		for (i in -4..4)
			for (k in -4..4)
				Botania.proxy.sparkleFX(player.worldObj, x + i / 8.0, y - 0.1, z + k / 8.0, r, g, b, 1f, 1, true)
	}
}