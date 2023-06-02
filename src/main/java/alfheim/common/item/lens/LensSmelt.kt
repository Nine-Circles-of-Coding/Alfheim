package alfheim.common.item.lens

import alexsocol.asjlib.*
import alexsocol.asjlib.security.InteractionSecurity
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.util.MovingObjectPosition
import vazkii.botania.api.internal.IManaBurst
import vazkii.botania.api.mana.IManaBlock
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.core.handler.ConfigHandler
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.lens.*

class LensSmelt: Lens() {
	
	val cost = 40
	
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
		
		val harvestLevel = ConfigHandler.harvestLevelBore
		val tile = world.getTileEntity(x, y, z)
		val hardness = block.getBlockHardness(world, x, y, z)
		val neededHarvestLevel = block.getHarvestLevel(meta)
		val mana = burst.mana
		val coords = burst.burstSourceChunkCoordinates
		
		if (!(coords.posX != x || coords.posY != y || coords.posZ != z) || tile is IManaBlock || neededHarvestLevel > harvestLevel || hardness == -1f || hardness >= 50f || !(burst.isFake || mana >= cost))
			return isDead
		
		if (burst.hasAlreadyCollidedAt(x, y, z) || burst.isFake || entity.worldObj.isRemote) return isDead
		if (entity.thrower != null && InteractionSecurity.isBreakingBanned(entity.thrower, x, y, z, world, block, meta)) return isDead
		
		val target = ItemStack(block, 1, meta)
		if (target.item == null) return isDead
		
		val result = FurnaceRecipes.smelting().getSmeltingResult(target)?.copy()
		
		if (result?.item == null) return isDead
		burst.mana -= cost
		
		world.setBlockToAir(x, y, z)
		
		if (!entity.worldObj.isRemote) {
			val xp = FurnaceRecipes.smelting().func_151398_b(result)
			if (xp >= 1f) entity.worldObj.getBlock(x, y, z).dropXpOnBlockBreak(entity.worldObj, x, y, z, xp.I)
			
			val offBounds = coords.posY < 0
			val doWarp = warp && !offBounds
			val dropX = if (doWarp) coords.posX else x
			val dropY = if (doWarp) coords.posY else y
			val dropZ = if (doWarp) coords.posZ else z
			
			world.spawnEntityInWorld(EntityItem(world, dropX + 0.5, dropY + 0.5, dropZ + 0.5, result))
		} else {
			if (!ConfigHandler.blockBreakParticles) return false
			
			world.playAuxSFX(2001, x, y, z, block.id + (meta shl 12))
			for (i in 0..2) entity.worldObj.spawnParticle("flame", x + Math.random() - 0.5f, y + Math.random() - 0.5f, z + Math.random() - 0.5f, 0.0, 0.0, 0.0)
		}
		
		return false
	}
}
