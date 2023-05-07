@file:Suppress("unused")

package alfheim.common.core.asm.hook.extender

import alexsocol.asjlib.ASJReflectionHelper
import alfheim.common.block.tile.sub.flower.SubTileBudOfYggdrasil
import gloomyfolken.hooklib.asm.Hook
import vazkii.botania.api.subtile.SubTileEntity
import vazkii.botania.common.block.tile.TileSpecialFlower

object FlowerSubtileInvalidator {
	
	val `SubTileEntity#invalidate()` by lazy {
		ASJReflectionHelper.getMethod(SubTileEntity::class.java, "invalidate", emptyArray())
	}
	
	@JvmStatic
	@Hook(createMethod = true)
	fun invalidate(tile: TileSpecialFlower) {
		tile.tileEntityInvalid = true
		ASJReflectionHelper.invoke<SubTileEntity, Unit>(`SubTileEntity#invalidate()`, tile.subTile, emptyArray())
	}
	
	@JvmStatic
	@Hook(createMethod = true)
	fun invalidate(tile: SubTileEntity) = Unit
	
	@JvmStatic
	@Hook(createMethod = true)
	fun invalidate(tile: SubTileBudOfYggdrasil) {
		tile.disable()
	}
}