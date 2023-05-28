package alfheim.common.item.lens

import alexsocol.asjlib.*
import alexsocol.asjlib.security.InteractionSecurity
import net.minecraft.block.Block
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.oredict.OreDictionary
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.internal.IManaBurst
import vazkii.botania.api.mana.IManaBlock
import vazkii.botania.common.Botania
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.core.handler.ConfigHandler
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.lens.*

class LensDaisy: Lens() {
	
	val cost = 20
	
	override fun collideBurst(burst: IManaBurst, entity: EntityThrowable, pos: MovingObjectPosition, isManaBlock: Boolean, isDead: Boolean, stack: ItemStack?): Boolean {
		val world = entity.worldObj
		val x = pos.blockX
		val y = pos.blockY
		val z = pos.blockZ
		val block = world.getBlock(x, y, z)
		val meta = world.getBlockMetadata(x, y, z)
		val composite = (ModItems.lens as ItemLens).getCompositeLens(stack)
		val warp = composite != null && composite.item === ModItems.lens && composite.getItemDamage() == ItemLens.WARP
		
		if (warp && (block === ModBlocks.pistonRelay || block === Blocks.piston || block === Blocks.piston_extension || block === Blocks.piston_head)) return false
		
		val tile = world.getTileEntity(x, y, z)
		val mana = burst.mana
		val coords = burst.burstSourceChunkCoordinates
		
		if (!(coords.posX != x || coords.posY != y || coords.posZ != z) || tile is IManaBlock || !(burst.isFake || mana >= cost))
			return isDead
		
		if (burst.hasAlreadyCollidedAt(x, y, z) || burst.isFake || entity.worldObj.isRemote) return false
		if (entity.thrower != null && InteractionSecurity.isBreakingBanned(entity.thrower, x, y, z, world, block, meta)) return true
		
		if (block.isAir(world, x, y, z)) return false
		
		val recipe = BotaniaAPI.pureDaisyRecipes.firstOrNull { it.matches(world, x, y, z, null, block, meta) } ?: return purifyItems(world, x, y, z, pos.sideHit)
		burst.mana -= cost
		burst.setCollidedAt(x, y, z)
		
		if (burst.isFake) return false
		
		if (!recipe.set(world, x, y, z, null)) return false
		
		for (p in 0..24) {
			val i = x + Math.random()
			val j = y + Math.random() + 0.5
			val k = z + Math.random()
			Botania.proxy.wispFX(world, i, j, k, 1f, 1f, 1f, Math.random().F / 2f)
		}
		
		if (ConfigHandler.blockBreakParticles) world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(recipe.output) + (recipe.outputMeta shl 12))
		
		return false
	}
	
	private fun purifyItems(world: World, x: Int, y: Int, z: Int, side: Int): Boolean {
		val d = ForgeDirection.getOrientation(side)
		val aabb = getBoundingBox(x, y, z, x + 1, y + 1, z + 1).offset(d.offsetX, d.offsetY, d.offsetZ)
		val list = getEntitiesWithinAABB(world, EntityItem::class.java, aabb)
		if (list.isEmpty()) return false
		list.removeAll { it.isDead || it.entityItem == null || it.entityItem.stackSize == 0 }
		list.forEach {
			val recipe = BotaniaAPI.manaInfusionRecipes.firstOrNull { rec ->
				!rec.isAlchemy && !rec.isConjuration && ASJUtilities.isItemStackEqualCrafting(it.entityItem, rec.output)
			} ?: return@forEach
			if (recipe.output.stackSize != it.entityItem.stackSize) return@forEach
			
			if (recipe.input is ItemStack) {
				it.setEntityItemStack(recipe.input as ItemStack)
			} else if (recipe.input !is String) return@forEach
			
			val validStack = OreDictionary.getOres(recipe.input as String).firstOrNull() ?: return@forEach
			val vsc = validStack.copy()
			if (vsc.getItemDamage() == Short.MAX_VALUE.I) vsc.setItemDamage(0)
			
			it.entityItem.stackSize = 0
			it.setEntityItemStack(null)
			it.setDead()
			
			EntityItem(it.worldObj, it.posX, it.posY, it.posZ, vsc).apply {
				setMotion(0.0)
				spawn()
				age = 120
			}
		}
		
		return true
	}
}
