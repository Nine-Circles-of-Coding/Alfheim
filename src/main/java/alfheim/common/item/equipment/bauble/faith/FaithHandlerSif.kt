package alfheim.common.item.equipment.bauble.faith

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.item.ColorOverrideHelper
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.item.AlfheimItems
import alfheim.common.item.equipment.bauble.*
import alfheim.common.item.relic.ItemSifRing
import cpw.mods.fml.common.eventhandler.*
import net.minecraft.block.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.*
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.player.*
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.core.helper.ItemNBTHelper.*
import vazkii.botania.common.item.ModItems
import java.awt.Color

object FaithHandlerSif: IFaithHandler {
	
	const val COOLDOWN_PLANT = 15
	const val COOLDOWN_FLOWER = 200
	const val RANGE = 5
	const val TAG_COOLDOWN = "growth_cooldown"
	
	init {
		eventForge()
	}
	
	@SubscribeEvent
	fun onLivingAttacked(e: LivingAttackEvent) {
		if (RagnarokHandler.blockedPowers[1]) return
		
		if (e.source.isUnblockable || e.entity.worldObj.isRemote) return
		
		val attacker = e.source.entity as? EntityLivingBase ?: return
		val player = e.entityLiving as? EntityPlayer ?: return
		if (ItemPriestCloak.getCloak(1, player) == null) return
		if (getGodPowerLevel(player) < 4) return
		
		if (!isInsideOfSector(Vector3.fromEntity(attacker), Vector3.fromEntity(player), -player.rotationYaw, if (attacker is EntityPlayerMP) attacker.theItemInWorldManager.blockReachDistance else 5.0))
			e.isCanceled = true
	}
	
	val reusable = Vector3()
	
	// https://stackoverflow.com/questions/13652518/efficiently-find-points-inside-a-circle-sector :)
	fun isInsideOfSector(point: Vector3, center: Vector3, angle: Float, radius: Double): Boolean {
		point.sub(center)
		return !areClockwise(reusable.set(-1, 0, 1).rotateOY(angle), point) && areClockwise(reusable.set(1, 0, 1).rotateOY(angle), point) && isWithinRadius(point, radius)
	}
	
	fun areClockwise(v1: Vector3, v2: Vector3): Boolean {
		return -v1.x * v2.z + v1.z * v2.x > 0
	}
	
	fun isWithinRadius(v: Vector3, radius: Double): Boolean {
		return v.x * v.x + v.z * v.z <= radius * radius
	}
	
	val grow = ArrayList<Pair<ChunkCoordinates, IGrowable>>()
	
	override fun onWornTick(stack: ItemStack, player: EntityPlayer, type: IFaithHandler.FaithBauble) {
		if (RagnarokHandler.blockedPowers[1]) return
		
		if (type != IFaithHandler.FaithBauble.EMBLEM) return
		
		val world = player.worldObj
		
		if (!world.isRemote) {
			val cooldown = getInt(stack, TAG_COOLDOWN, 0)
			if (cooldown > 0) setInt(stack, TAG_COOLDOWN, cooldown - 1)
		}
		
		if (getGodPowerLevel(player) < 4) return
		
		if (!ManaItemHandler.requestManaExact(stack, player, 10, false)) return
		
		if (world.totalWorldTime % 40 == 0L)
			for (x in 0.bidiRange(RANGE))
				for (y in 0.bidiRange(RANGE))
					for (z in 0.bidiRange(RANGE)) {
						val block = world.getBlock(player, x, y, z)
						
						if (block is BlockSapling && block.func_149851_a(world, player.posX.mfloor() + x, player.posY.mfloor() + y, player.posZ.mfloor() + z, world.isRemote))
							grow.add(ChunkCoordinates(player.posX.mfloor() + x, player.posY.mfloor() + y, player.posZ.mfloor() + z) to block)
					}
		
		val pair = grow.random() ?: return
		val (x, y, z) = pair.first
		bonemeal(world, pair.second, x, y, z, player, stack, 10, true)
		
		grow.clear()
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun onClick(e: PlayerInteractEvent) {
		if (RagnarokHandler.blockedPowers[1]) return
		
		val player = e.entityPlayer
		
		if (e.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return
		val emblem = ItemPriestEmblem.getEmblem(1, player) ?: return
		
		val cooldown = getInt(emblem, TAG_COOLDOWN, 0)
		if (cooldown != 0 || e.entityPlayer.isSneaking || player.heldItem != null || !ManaItemHandler.requestManaExact(emblem, e.entityPlayer, 50, false)) return
		
		val world = e.world
		val block = world.getBlock(e.x, e.y, e.z)
		
		if (block is IGrowable && block !== Blocks.grass && block.func_149851_a(world, e.x, e.y, e.z, world.isRemote) &&
			bonemeal(world, block, e.x, e.y, e.z, player, emblem, 50, false) &&
			!world.isRemote && !player.capabilities.isCreativeMode)
			setInt(emblem, TAG_COOLDOWN, COOLDOWN_PLANT)
		
		val lvl = getGodPowerLevel(player)
		if (lvl <= 5) return
		
		val newMeta = world.rand.nextInt(16)
		if (!world.isRemote && block === Blocks.grass && e.face == 1 &&
			world.getBlock(e.x, e.y + 1, e.z).isAir(world, e.x, e.y + 1, e.z) &&
			!InteractionSecurity.isPlacementBanned(player, e.x, e.y + 1, e.z, world, ModBlocks.flower, newMeta) &&
			(!world.provider.hasNoSky || e.y < 255) &&
			ModBlocks.flower.canBlockStay(world, e.x, e.y + 1, e.z) &&
			ManaItemHandler.requestManaExact(emblem, e.entityPlayer, 500, true) &&
			world.setBlock(e.x, e.y + 1, e.z, ModBlocks.flower, newMeta, 3) &&
			!player.capabilities.isCreativeMode)
			setInt(emblem, TAG_COOLDOWN, COOLDOWN_FLOWER)
	}
	
	fun bonemeal(world: World, block: IGrowable, x: Int, y: Int, z: Int, player: EntityPlayer, stack: ItemStack, cost: Int, interactCheck: Boolean): Boolean {
		if (interactCheck && InteractionSecurity.isInteractionBanned(player, x, y, z, world))
			return false
		
		val event = BonemealEvent(player, world, block as Block, x, y, z)
		if (MinecraftForge.EVENT_BUS.post(event))
			return false
		
		if (event.result == Event.Result.ALLOW)
			return ManaItemHandler.requestManaExact(stack, player, cost, !world.isRemote)
		
		if (world.isRemote) {
			world.playAuxSFX(2005, x, y, z, 0)
			return true
		} else if (ManaItemHandler.requestManaExact(stack, player, cost, true)) {
			if (block.func_149852_a(world, world.rand, x, y, z)) {
				block.func_149853_b(world, world.rand, x, y, z)
				world.playSound(x.D, y.D, z.D, "liquid.lavapop", 1f, 0.1f, false)
			}
			
			return true
		}
		
		return false
	}
	
	override fun getGodPowerLevel(player: EntityPlayer): Int {
		if (RagnarokHandler.blockedPowers[1]) return 0
		
		var lvl = 0
		
		if (player.inventory.hasItemStack(ItemStack(ModItems.infiniteFruit))) lvl += 4
		if (ItemPriestCloak.getCloak(1, player) != null) lvl += 3
		if (ItemPriestEmblem.getEmblem(1, player) != null) lvl += 2
		if (ItemSifRing.getSifRing(player) != null) lvl += 1
		if (player.inventory.hasItemStack(ItemStack(AlfheimItems.rodColorfulSkyDirt))) lvl += 1
		
		return lvl
	}
	
	override fun doParticles(stack: ItemStack, player: EntityPlayer) {
		if (player.ticksExisted % 10 != 0) return
		
		for (i in 0..6) {
			val color = Color(ColorOverrideHelper.getColor(player, 0x964B00))
			val r = color.red.F / 255F
			val g = color.green.F / 255F
			val b = color.blue.F / 255F
			
			val (x, y, z) = Vector3.fromEntity(player)
			val motionX = (Math.random() - 0.5) * 0.15
			val motionZ = (Math.random() - 0.5) * 0.15
			
			Botania.proxy.wispFX(mc.theWorld, x, y, z, r, g, b, Math.random().F * 0.15f + 0.15f, motionX.F, 0.0075f, motionZ.F)
		}
	}
}