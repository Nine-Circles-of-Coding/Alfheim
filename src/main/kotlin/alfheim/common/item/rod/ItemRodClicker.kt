package alfheim.common.item.rod

import alexsocol.asjlib.*
import alexsocol.asjlib.ItemNBTHelper.getBoolean
import alexsocol.asjlib.ItemNBTHelper.getLong
import alexsocol.asjlib.ItemNBTHelper.setBoolean
import alexsocol.asjlib.ItemNBTHelper.setLong
import alexsocol.asjlib.math.Vector3
import alfheim.api.lib.LibResourceLocations
import alfheim.client.core.helper.IconHelper
import alfheim.common.entity.boss.EntityFlugel
import alfheim.common.item.ItemMod
import com.mojang.authlib.GameProfile
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.eventhandler.Event
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import net.minecraft.network.*
import net.minecraft.server.MinecraftServer
import net.minecraft.server.management.ItemInWorldManager
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.common.util.*
import net.minecraftforge.event.ForgeEventFactory
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import vazkii.botania.api.item.*
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.block.tile.TileAvatar
import vazkii.botania.common.core.helper.InventoryHelper
import java.util.*
import kotlin.math.max

class ItemRodClicker: ItemMod("RodClicker"), IAvatarWieldable {
	
	init {
		maxStackSize = 1
		setFull3D()
	}
	
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this, "Right")
		iconLeft = IconHelper.forItem(reg, this, "Left")
	}
	
	override fun getIconIndex(stack: ItemStack) = if (stack.isLeftClick) iconLeft else itemIcon!!
	
	override fun getIcon(stack: ItemStack, pass: Int) = getIconIndex(stack)
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer?, list: MutableList<Any?>, adv: Boolean) {
		list.add(StatCollector.translateToLocal("${this.unlocalizedName}.leftclick.${stack.isLeftClick}"))
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World?, player: EntityPlayer?): ItemStack {
		stack.isLeftClick = !stack.isLeftClick
		return stack
	}
	
	override fun onUpdate(stack: ItemStack, world: World?, holder: Entity?, slot: Int, inHand: Boolean) {
		stack.prevTick = -1
	}
	
	override fun onEntityItemUpdate(entity: EntityItem): Boolean {
		entity.entityItem.prevTick = -1
		return false
	}
	
	override fun onAvatarUpdate(avatar: IAvatarTile, wand: ItemStack) {
		val tile = avatar as? TileAvatar ?: return
		val world = tile.worldObj ?: return
		
		val (x, y, z) = Vector3.fromTileEntity(tile).I
		if (world.isRemote || !world.isBlockIndirectlyGettingPowered(x, y, z)) return
		
		if (tile.currentMana < COST_AVATAR) return
		
		var xl = x
		var yl = y
		var zl = z
		var s = tile.getBlockMetadata()
		
		when (s - 2) {
			0    -> zl -= 2
			2    -> xl -= 2
			3    -> xl += 2
			else -> zl += 2
		}
		
		val leftClick = wand.isLeftClick
		
		val entities = getEntitiesWithinAABB(world, Entity::class.java, getBoundingBox(xl, yl, zl).offset(0.5).expand(0.5))
		
		if (world.isAirBlock(xl, yl, zl) && !leftClick) {
			yl -= 1
			s = 1
		} else {
			s = ForgeDirection.values()[s].opposite.ordinal
		}
		
		val player = getFake(world.provider.dimensionId)
		player.isSneaking = !world.isAirBlock(x, y + 1, z)
		player.setPositionAndRotation(tile.xCoord + 0.5, tile.yCoord.D, tile.zCoord + 0.5, when (tile.blockMetadata) {
			2    -> 180f
			4    -> 90f
			5    -> -90f
			else -> 0f
		}, 0f)
		
		val currTick = world.totalWorldTime
		val prevTick = wand.prevTick
		wand.prevTick = currTick
		val skipped = if (prevTick == -1L) 0 else (currTick - prevTick).I
		
		val inv = InventoryHelper.getInventory(world, x, y - 1, z)
		equipPlayer(player, inv, skipped)
		
		FMLCommonHandler.instance().onPlayerPostTick(player)
		
		val manaForCharging = max(0, tile.currentMana - COST_AVATAR)
		if (manaForCharging > 0) {
			val sent = if (ManaItemHandler.dispatchManaExact(wand, player, manaForCharging, true))
				manaForCharging
			else
				ManaItemHandler.dispatchMana(wand, player, manaForCharging, true)
			
			tile.recieveMana(-sent)
		}
		
		try_ {
			var done = false
			
			if (leftClick) {
				run {
					entities.removeAll { !it.canAttackWithItem() }
					if (entities.isEmpty()) return@run
					
					val mods = player.heldItem?.attributeModifiers
					
					if (mods != null)
						player.getAttributeMap().applyAttributeModifiers(mods)
					
					entities.minByOrNull { Vector3.entityDistancePlane(player, it) }?.let { player.attackTargetEntityWithCurrentItem(it) }
					
					if (mods != null)
						player.getAttributeMap().removeAttributeModifiers(mods)
					
					done = true
				}
				
				if (!done)
					done = player.theItemInWorldManager.onBlockSafeClicked(xl, yl, zl, s)
			} else {
				val entity = if (entities.isEmpty()) null else entities.random()
				
				done = entity != null && player.interactWith(entity)
				if (!done) done = player.theItemInWorldManager.activateBlockOrUseItem(player, world, player.heldItem, xl, yl, zl, s, 0.5f, 0.5f, 0.5f)
				if (!done) run {
					player.heldItem ?: return@run
					val event = ForgeEventFactory.onPlayerInteract(player, PlayerInteractEvent.Action.RIGHT_CLICK_AIR, 0, 0, 0, -1, world)
					if (event.isCanceled || event.useItem == Event.Result.DENY) return@run
					done = player.theItemInWorldManager.tryUseItem(player, world, player.heldItem)
				}
			}
			
			if (done) tile.recieveMana(-COST_AVATAR)
		}
		
		unequipPlayer(player, inv)
	}
	
	fun ItemInWorldManager.onBlockSafeClicked(x: Int, y: Int, z: Int, side: Int): Boolean {
		if (gameType.isAdventure && !thisPlayerMP.isCurrentToolAdventureModeExempt(x, y, z)) return false
		
		val event = ForgeEventFactory.onPlayerInteract(thisPlayerMP, PlayerInteractEvent.Action.LEFT_CLICK_BLOCK, x, y, z, side, theWorld)
		if (event.isCanceled)
//			thisPlayerMP.playerNetServerHandler.sendPacket(S23PacketBlockChange(x, y, z, theWorld))
			return false
		
		var f = 1f
		val block = theWorld.getBlock(x, y, z)
		
		var clicked = false
		
		if (!block.isAir(theWorld, x, y, z)) {
			if (event.useBlock != Event.Result.DENY) {
				block.onBlockClicked(theWorld, x, y, z, thisPlayerMP)
				theWorld.extinguishFire(thisPlayerMP, x, y, z, side)
				clicked = true
			}
//			else {
//				thisPlayerMP.playerNetServerHandler.sendPacket(S23PacketBlockChange(x, y, z, theWorld))
//			}
			
			f = block.getPlayerRelativeBlockHardness(thisPlayerMP, thisPlayerMP.worldObj, x, y, z)
		}
		
		if (event.useItem == Event.Result.DENY) {
//			if (f >= 1.0f) {
//				thisPlayerMP.playerNetServerHandler.sendPacket(S23PacketBlockChange(x, y, z, theWorld))
//			}
			return clicked
		}
		
		if (!block.isAir(theWorld, x, y, z) && f >= 1f)
			return tryHarvestBlock(x, y, z) || clicked
		
		return clicked
//		else {
//			this.isDestroyingBlock = true
//			this.partiallyDestroyedBlockX = x
//			this.partiallyDestroyedBlockY = y
//			this.partiallyDestroyedBlockZ = z
//			val i1 = (f * 10.0f).toInt()
//			this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.getEntityId(), x, y, z, i1)
//			this.durabilityRemainingOnBlock = i1
//		}
	}
	
	fun equipPlayer(player: FakePlayer, inv: IInventory?, ticksSkipped: Int) {
		if (inv == null) return
		
		val accessibleSlots = (inv as? ISidedInventory)?.getAccessibleSlotsFromSide(1)
		for (i in 0 until player.inventory.sizeInventory) {
			if (i >= inv.sizeInventory) break
			
			if (inv is ISidedInventory && i !in accessibleSlots!!) continue
			
			val stack = inv[i]?.copy() ?: continue
			if (inv is ISidedInventory && !inv.canExtractItem(i, stack, 1)) continue
			inv[i] = null
			
			if (stack.stackSize <= 0) continue
			
			player.inventory[i] = stack
		}
		
		for (i in 0 until player.inventory.sizeInventory) {
			val stack = player.inventory[i] ?: continue
			
			repeat(ticksSkipped) {
				stack.item.onUpdate(stack, player.worldObj, player, i, i == 0)
			}
		}
		
		return
	}
	
	fun unequipPlayer(player: FakePlayer, inv: IInventory?) {
		val accessibleSlots = (inv as? ISidedInventory)?.getAccessibleSlotsFromSide(1)
		
		for (i in 0 until player.inventory.sizeInventory) {
			var stack = player.inventory[i]?.copy()
			player.inventory[i] = null
			
			if (stack != null && stack.stackSize <= 0) stack = null
			if (stack == null) continue
			
			if (inv == null || i >= inv.sizeInventory || (inv is ISidedInventory && (i !in accessibleSlots!! || !inv.canInsertItem(i, stack, 1)))) {
				player.dropPlayerItemWithRandomChoice(stack, true)
				continue
			}
			
			inv[i] = stack
		}
	}
	
	override fun getOverlayResource(tile: IAvatarTile?, stack: ItemStack?) = LibResourceLocations.avatarClicker
	
	private var ItemStack.isLeftClick
		get() = getBoolean(this, TAG_MODE, false)
		set(left) = setBoolean(this, TAG_MODE, left)
	
	private var ItemStack.prevTick
		get() = getLong(this, TAG_TICK, -1L)
		set(left) = setLong(this, TAG_TICK, left)
	
	companion object {
		
		const val TAG_MODE = "mode"
		const val TAG_TICK = "tick"
		const val COST_AVATAR = 10
		
		lateinit var iconLeft: IIcon
		
		val profileMap = HashMap<Int, GameProfile>()
		
		init {
			eventForge()
		}
		
		fun getFake(dim: Int): FakePlayer {
			val gp = profileMap[dim] ?: GameProfile(UUID(dim.toLong(), dim.toLong()), "Avatar-Clicker_$dim").also { profileMap[dim] = it }
			val fake = FakePlayerFactory.get(MinecraftServer.getServer().worldServerForDimension(dim), gp)
			if (fake.playerNetServerHandler == null)
				fake.playerNetServerHandler = NetHandlerPlayServer(MinecraftServer.getServer(), NetworkManager(false), fake)
			
			return fake
		}
		
		fun isFakeNotAvatar(player: EntityPlayer) = player.commandSenderName.startsWith("Avatar-Clicker_") || EntityFlugel.isTruePlayer(player)
	}
}
