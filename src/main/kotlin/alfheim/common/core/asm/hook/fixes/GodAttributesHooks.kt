package alfheim.common.core.asm.hook.fixes

import alexsocol.asjlib.*
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.item.equipment.bauble.ItemPriestEmblem
import alfheim.common.item.equipment.bauble.faith.ItemRagnarokEmblem
import alfheim.common.item.relic.*
import cpw.mods.fml.relauncher.*
import gloomyfolken.hooklib.asm.*
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.*
import net.minecraft.entity.item.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.item.equipment.tool.terrasteel.*
import vazkii.botania.common.item.relic.*
import kotlin.math.*

@Suppress("unused", "UNUSED_PARAMETER", "FunctionName", "INACCESSIBLE_TYPE")
object GodAttributesHooks {
	
	/**
	 * walk on water with Njord Emblem
	 */
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun addCollisionBoxesToList(liquid: BlockLiquid, world: World, x: Int, y: Int, z: Int, entitysBox: AxisAlignedBB, list: MutableList<Any?>, player: Entity?) {
		if (RagnarokHandler.blockedPowers[2]) return
		
		var aabb = liquid.getCollisionBoundingBoxFromPool(world, x, y, z)
		
		if (player is EntityPlayer && !player.capabilities.isFlying) {
			val emblem = ItemPriestEmblem.getEmblem(2, player) ?: return
			
			val cost = if (liquid.material === Material.lava) 25 else 5
			var take = false
			if (!ManaItemHandler.requestManaExact(emblem, player, cost, false)) return
			
			val fullBB = getBoundingBox(x, y, z).offset(0.5).expand(0.5)
			
			if (entitysBox.intersectsWith(fullBB)) {
				player.motionY = if (!player.isSneaking) max(player.motionY, 0.5) else min(player.motionY, -0.5)
				take = true
			}
			
			if (!player.isSneaking && world.getBlock(x, y + 1, z).isAir(world, x, y, z) && (player.posY - if (ASJUtilities.isClient && mc.thePlayer === player) 1.62 else 0.0) >= y + 1) {
				aabb = fullBB
				take = true
			}
			
			if (take) {
				ManaItemHandler.requestManaExact(emblem, player, cost, true)
				player.inWater = true
			}
		}
		
		if (aabb != null && entitysBox.intersectsWith(aabb)) {
			list.add(aabb)
		}
	}
	
	/**
	 * Decrease food usage with Odin Ring
	 */
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun addExhaustion(player: EntityPlayer, lvl: Float) {
		if (RagnarokHandler.blockedPowers[5]) return
		
		if (!player.capabilities.disableDamage) {
			if (!player.worldObj.isRemote) {
				val dec = lvl / if (ItemPriestEmblem.getEmblem(5, player) != null) 4f else if (ItemRagnarokEmblem.getEmblem(player, 5) != null) 8f else 1f
				player.foodStats.addExhaustion(dec)
			}
		}
	}
	
	/**
	 * Expand Terra Truncator area with Sif Ring
	 */
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun breakOtherBlock(axe: ItemTerraAxe, player: EntityPlayer, stack: ItemStack?, x: Int, y: Int, z: Int, originX: Int, originY: Int, originZ: Int, side: Int) {
		if (RagnarokHandler.blockedPowers[1]) return
		
		if (axe.shouldBreak(player)) {
			val coords = ChunkCoordinates(x, y, z)
			val range = if (ItemSifRing.getSifRing(player) != null) 64 else ItemTerraAxe.BLOCK_RANGE
			ItemTerraAxe.addBlockSwapper(player.worldObj, player, stack, coords, range, true)
		}
	}
	
	/**
	 * Expand Rod of Terra Firma with Sif ring
	 * Used in Class Transformers
	 */
	fun getRange(player: EntityPlayer, prev: Int): Int {
		if (RagnarokHandler.blockedPowers[1]) return 0
		
		return prev + if (ItemSifRing.getSifRing(player) != null) 8 else 0
	}
	
	/**
	 * Increases Luck of the Sea enchantment on player with Njord ring
	 */
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS, injectOnExit = true)
	fun func_151386_g(static: EnchantmentHelper?, entity: EntityLivingBase, @Hook.ReturnValue ret: Int): Int {
		if (RagnarokHandler.blockedPowers[2]) return 0
		
		return ret + if (ItemNjordRing.getNjordRing(entity as? EntityPlayer ?: return ret) != null) 20 else 0
	}
	
	// Make boat unbreakable if rider is with Njord Ring
	
	var dontDie = false
	
	@JvmStatic
	@Hook(targetMethod = "onUpdate")
	fun onUpdatePre(boat: EntityBoat) {
		if (RagnarokHandler.blockedPowers[2]) return
		
		dontDie = (boat.riddenByEntity as? EntityPlayer)?.let { ItemNjordRing.getNjordRing(it) != null } == true
		if (dontDie) // used just as flag
			boat.moveEntity(boat.motionX, boat.motionY, boat.motionZ)
	}
	
	@JvmStatic
	@Hook(targetMethod = "onUpdate", injectOnExit = true)
	fun onUpdatePost(boat: EntityBoat) {
		dontDie = false
	}
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun setDead(boat: EntityBoat) {
		boat.isDead = !dontDie
	}
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun func_145778_a(boat: EntityBoat, item: Item?, count: Int, force: Float): EntityItem? {
		return if (dontDie) null else boat.entityDropItem(ItemStack(item, count, 0), force)
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun onUnequipped(ring: ItemAesirRing, stack: ItemStack, player: EntityLivingBase) {
		if (RagnarokHandler.blockedPowers[2]) return
		
		player.stepHeight = 0.5f
	}
	
	// block Thor ring powers
	
	var noThorRing = false
	
	@JvmStatic
	@Hook(targetMethod = "breakOtherBlock")
	fun breakOtherBlockPre(ring: ItemTerraPick, player: EntityPlayer?, stack: ItemStack?, x: Int, y: Int, z: Int, originX: Int, originY: Int, originZ: Int, side: Int) {
		noThorRing = RagnarokHandler.blockedPowers[0]
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_NULL)
	fun getThorRing(ring: ItemThorRing?, player: EntityPlayer?) = if (noThorRing) null else ItemStack(Blocks.stone)
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "breakOtherBlock")
	fun breakOtherBlockPost(ring: ItemTerraPick, player: EntityPlayer?, stack: ItemStack?, x: Int, y: Int, z: Int, originX: Int, originY: Int, originZ: Int, side: Int) {
		noThorRing = false
	}
	
	// block Loki ring powers
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE)
	fun onPlayerInteract(ring: ItemLokiRing, event: PlayerInteractEvent?) = RagnarokHandler.blockedPowers[3]
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE)
	fun breakOnAllCursors(ring: ItemLokiRing?, player: EntityPlayer?, item: Item?, stack: ItemStack?, x: Int, y: Int, z: Int, side: Int) = RagnarokHandler.blockedPowers[3]
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_NULL)
	fun getSourceWireframe(ring: ItemLokiRing, player: EntityPlayer?, stack: ItemStack?): ChunkCoordinates? = if (RagnarokHandler.blockedPowers[3]) null else ChunkCoordinates()
	
	@SideOnly(Side.CLIENT)
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_NULL)
	fun getWireframesToDraw(ring: ItemLokiRing, player: EntityPlayer?, stack: ItemStack?): List<ChunkCoordinates?>? = if (RagnarokHandler.blockedPowers[3]) null else emptyList()
	
	// block Odin ring powers
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE)
	fun onValidPlayerWornTick(ring: ItemOdinRing, stack: ItemStack?, player: EntityPlayer?) = RagnarokHandler.blockedPowers[5]
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE)
	fun onEquippedOrLoadedIntoWorld(ring: ItemOdinRing, stack: ItemStack?, player: EntityLivingBase?) = RagnarokHandler.blockedPowers[5]
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE)
	fun onPlayerAttacked(ring: ItemOdinRing, event: LivingAttackEvent?) = RagnarokHandler.blockedPowers[5]
}