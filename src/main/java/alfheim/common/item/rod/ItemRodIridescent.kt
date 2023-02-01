package alfheim.common.item.rod

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.api.event.PlayerInteractAdequateEvent
import alfheim.api.lib.LibResourceLocations
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.item.ItemIridescent
import alfheim.common.item.equipment.bauble.ItemPriestEmblem
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.block.Block
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.*
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.StatCollector
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.api.item.*
import vazkii.botania.api.mana.*
import vazkii.botania.client.core.handler.ItemsRemainingRenderHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.core.helper.Vector3
import java.awt.Color

class ItemRodIridescent(name: String = "rodColorfulSkyDirt"): ItemIridescent(name), IAvatarWieldable, IManaUsingItem, IBlockProvider {
	
	val COST = 150
	
	companion object {
		
		fun place(
			stack: ItemStack, player: EntityPlayer, world: World,
			x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float,
			toPlace: ItemStack?, cost: Int, r: Float, g: Float, b: Float,
		): Boolean {
			if (!ManaItemHandler.requestManaExactForTool(stack, player, cost, false)) return false
			val dir = ForgeDirection.getOrientation(side)
			
			val aabb = getBoundingBox(x, y, z, x + 1, y + 1, z + 1).offset(dir.offsetX, dir.offsetY, dir.offsetZ)
			val entities = getEntitiesWithinAABB(world, EntityLivingBase::class.java, aabb).size
			
			if (entities != 0) return false
			toPlace!!.tryPlaceItemIntoWorld(player, world, x, y, z, side, hitX, hitY, hitZ)
			
			if (toPlace.stackSize != 0) return false
			
			ManaItemHandler.requestManaExactForTool(stack, player, cost, true)
			for (i in 0..6)
				Botania.proxy.sparkleFX(world, x + dir.offsetX + Math.random(), y + dir.offsetY + Math.random(), z + dir.offsetZ + Math.random(), r, g, b, 1F, 5)
			
			return true
		}
	}
	
	init {
		maxStackSize = 1
		eventForge()
	}
	
	override fun onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float) =
		place(stack, player, world, x, y, z, side, hitX, hitY, hitZ, dirtStack(stack.meta), COST, 0.35F, 0.2F, 0.05F)
	
	@SubscribeEvent
	fun onItemLeftClick(e: PlayerInteractAdequateEvent.LeftClick) {
		if (e.action !== PlayerInteractAdequateEvent.LeftClick.Action.LEFT_CLICK_AIR) return
		val player = e.player
		val world = player.worldObj
		val stack = player.heldItem ?: return
		
		if (!player.isSneaking || stack.item !== this) return
		
		var damage = stack.meta
		if (!world.isRemote) {
			if (stack.meta <= 0) stack.meta = 17 else stack.meta--
			damage = stack.meta
		} else if (damage <= 0) damage = 17 else damage--
		
		player.playSoundAtEntity("botania:ding", 0.1F, 1F)
		val blockstack = dirtStack(damage)
		blockstack.setStackDisplayName(StatCollector.translateToLocal("misc.${ModInfo.MODID}.color.$damage"))
		ItemsRemainingRenderHandler.set(blockstack, -2)
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		var blockstack = dirtStack(stack.meta)
		
		/*val beltStack = ItemToolbelt.getEquippedBelt(player)
		if (beltStack != null && ItemToolbelt.isEquipped(beltStack))
			return stack*/
		
		if (player.isSneaking) {
			var damage = stack.meta
			if (!world.isRemote) {
				if (stack.meta >= 17) stack.meta = 0 else stack.meta++
				damage = stack.meta
			} else if (damage >= 17) damage = 0 else damage++
			
			player.playSoundAtEntity("botania:ding", 0.1F, 1F)
			blockstack = dirtStack(damage)
			blockstack.setStackDisplayName(StatCollector.translateToLocal("misc.${ModInfo.MODID}.color.$damage"))
			
			if (world.isRemote && player == mc.thePlayer)
				ItemsRemainingRenderHandler.set(blockstack, -2)
		} else if (!world.isRemote && ManaItemHandler.requestManaExactForTool(stack, player, COST * 2, false)) run {
			val color = Color(getColorFromItemStack(stack, 0))
			val r = color.red / 255F
			val g = color.green / 255F
			val b = color.blue / 255F
			
			val sif = (!RagnarokHandler.blockedPowers[1] && ItemPriestEmblem.getEmblem(1, player) != null)
			var basePlayerRange = 5.0
			if (player is EntityPlayerMP)
				basePlayerRange = player.theItemInWorldManager.blockReachDistance
			val distmultiplier = if (sif) basePlayerRange - 1 else 3.0
			
			val playerVec = Vector3.fromEntityCenter(player)
			val lookVec = Vector3(player.lookVec).multiply(distmultiplier)
			val placeVec = playerVec.copy().add(lookVec)
			
			val x = placeVec.x.mfloor()
			val y = placeVec.y.mfloor() + 1
			val z = placeVec.z.mfloor()
			
			val entities = getEntitiesWithinAABB(world, EntityLivingBase::class.java, getBoundingBox(x, y, z, x + 1, y + 1, z + 1)).size
			
			if (entities != 0) return@run
			blockstack.tryPlaceItemIntoWorld(player, world, x, y, z, 0, 0F, 0F, 0F)
			
			if (blockstack.stackSize != 0) return@run
			
			ManaItemHandler.requestManaExactForTool(stack, player, COST * 2, true)
			for (i in 0..6) Botania.proxy.sparkleFX(world, x + Math.random(), y + Math.random(), z + Math.random(), r, g, b, 1F, 5)
		}
		if (world.isRemote)
			player.swingItem()
		
		return stack
	}
	
	override fun onAvatarUpdate(tile: IAvatarTile, stack: ItemStack) {
		val te = tile as TileEntity
		val world = te.worldObj
		val x = te.xCoord
		val y = te.yCoord
		val z = te.zCoord
		
		var xl = 0
		var zl = 0
		
		when (te.getBlockMetadata() - 2) {
			0 -> zl = -2
			1 -> zl = 2
			2 -> xl = -2
			3 -> xl = 2
		}
		
		val block = world.getBlock(x + xl, y, z + zl)
		
		val color = Color(getColorFromItemStack(stack, 0))
		val r = color.red / 255F
		val g = color.green / 255F
		val b = color.blue / 255F
		
		if (tile.currentMana >= COST && block.isAir(world, x + xl, y, z + zl) && tile.elapsedFunctionalTicks % 50 == 0 && tile.isEnabled) {
			world.setBlock(x + xl, y, z + zl, dirtFromMeta(stack.meta), stack.meta, 1 or 2)
			tile.recieveMana(-COST)
			for (i in 0..6)
				Botania.proxy.sparkleFX(world, x + xl + Math.random(), y + Math.random(), z + zl + Math.random(),
										r, g, b, 1F, 5)
			when (stack.meta) {
				17    -> world.playAuxSFX(2001, x + xl, y, z + zl, AlfheimBlocks.auroraDirt.id)
				TYPES -> world.playAuxSFX(2001, x + xl, y, z + zl, AlfheimBlocks.rainbowDirt.id)
				else  -> world.playAuxSFX(2001, x + xl, y, z + zl, AlfheimBlocks.irisDirt.id + (stack.meta shl 12))
			}
		}
	}
	
	override fun getOverlayResource(tile: IAvatarTile, stack: ItemStack) = LibResourceLocations.avatarColorDirt
	
	override fun isFull3D() = true
	
	override fun usesMana(stack: ItemStack) = true
	
	override fun provideBlock(player: EntityPlayer, requestor: ItemStack, stack: ItemStack, block: Block, meta: Int, doit: Boolean): Boolean {
		if (block === AlfheimBlocks.irisDirt || block === AlfheimBlocks.rainbowDirt || block === AlfheimBlocks.auroraDirt)
			return !doit || ManaItemHandler.requestManaExactForTool(requestor, player, COST, true)
		return false
	}
	
	override fun getBlockCount(player: EntityPlayer, requestor: ItemStack, stack: ItemStack, block: Block, meta: Int): Int {
		if (block === AlfheimBlocks.irisDirt || block === AlfheimBlocks.rainbowDirt || block === AlfheimBlocks.auroraDirt)
			return -1
		return 0
	}
}
