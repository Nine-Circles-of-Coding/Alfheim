package alfheim.common.item.lens

import alexsocol.asjlib.*
import alexsocol.asjlib.security.InteractionSecurity
import net.minecraft.block.Block
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.oredict.OreDictionary
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.internal.IManaBurst
import vazkii.botania.api.mana.IManaBlock
import vazkii.botania.common.Botania
import vazkii.botania.common.core.handler.ConfigHandler
import vazkii.botania.common.item.lens.Lens

class LensDaisy: Lens() {
	
	val cost = 20
	
	override fun collideBurst(burst: IManaBurst, entity: EntityThrowable, pos: MovingObjectPosition?, isManaBlock: Boolean, isDead: Boolean, stack: ItemStack?): Boolean {
		if (burst.isFake || burst.mana < cost) return false
		
		val world = entity.worldObj
		if (world.isRemote || pos == null || pos.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return false
		
		val x = pos.blockX
		val y = pos.blockY
		val z = pos.blockZ
		
		val source = burst.burstSourceChunkCoordinates
		if (source == ChunkCoordinates(x, y, z) || world.getTileEntity(x, y, z) is IManaBlock) return isDead
		if (burst.hasAlreadyCollidedAt(x, y, z)) return false
		
		if (entity.thrower != null && InteractionSecurity.isPlacementBanned(entity.thrower, x, y, z)) return false
		
		val block = world.getBlock(x, y, z)
		if (block.isAir(world, x, y, z)) return false
		
		val meta = world.getBlockMetadata(x, y, z)
		val recipe = BotaniaAPI.pureDaisyRecipes.firstOrNull { it.matches(world, x, y, z, null, block, meta) } ?: return purifyItems(world, x, y, z, pos.sideHit)
		
		if (recipe.set(world, x, y, z, null)) {
			burst.mana -= cost
			
			for (p in 0..24) {
				val i = x + Math.random()
				val j = y + Math.random() + 0.5
				val k = z + Math.random()
				Botania.proxy.wispFX(world, i, j, k, 1f, 1f, 1f, Math.random().F / 2f)
			}
			
			if (ConfigHandler.blockBreakParticles) world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(recipe.output) + (recipe.outputMeta shl 12))
		}
		
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
