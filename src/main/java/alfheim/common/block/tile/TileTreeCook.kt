package alfheim.common.block.tile

import alexsocol.asjlib.*
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemFood
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.tileentity.TileEntity

class TileTreeCook: TileEntity() {
	
	override fun updateEntity() {
		if (worldObj.totalWorldTime % 20 != 0L) return
		getEntitiesWithinAABB(worldObj, EntityItem::class.java, boundingBox(8)).forEach {
			if (it.entityItem.item !is ItemFood || it.entityItem.stackSize < 1) return@forEach
			
			val result = FurnaceRecipes.smelting().getSmeltingResult(it.entityItem) ?: return@forEach
			if (result.item !is ItemFood) return@forEach
			
			if (worldObj.isRemote) {
				for (i in 0..1) worldObj.spawnParticle("lava", it.posX, it.posY, it.posZ, 0.0, 0.0, 0.0)
				
				return
			} else {
				if (EntityItem(worldObj, it.posX, it.posY, it.posZ, result.copy()).spawn()) {
					--it.entityItem.stackSize
					
					return
				}
			}
		}
	}
}