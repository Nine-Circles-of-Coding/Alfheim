package alfheim.common.block.tile.sub.anomaly

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.block.tile.SubTileAnomalyBase
import alfheim.common.core.asm.AlfheimClassTransformer
import net.minecraft.item.ItemStack
import vazkii.botania.common.Botania
import vazkii.botania.common.entity.EntityManaBurst
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.lens.ItemLens

class SubTileManaTornado: SubTileAnomalyBase() {
	
	internal val v = Vector3()
	
	override val targets: List<Any>
		get() {
			if (worldObj.rand.nextInt(100) == 0)
				return spawnBurst()?.let { mutableListOf(it) } ?: EMPTY_LIST
			
			return EMPTY_LIST
		}
	
	public override fun update() {
		if (inWG()) return
		
		val c = ASJRenderHelper.colorCode[worldObj.rand.nextInt(ASJRenderHelper.colorCode.size)]
		v.rand().sub(0.5).normalize().mul(Math.random()).add(superTile!!).add(0.5)
		Botania.proxy.wispFX(worldObj, v.x, v.y, v.z, (c shr 16 and 0xFF) / 255f, (c shr 8 and 0xFF) / 255f, (c and 0xFF) / 255f, (Math.random() * 0.25 + 0.25).F, 0f, (Math.random() * 2 + 1).F)
	}
	
	fun spawnBurst(): EntityManaBurst? {
		if (inWG()) return null
		
		val burst = EntityManaBurst(worldObj)
		val motionModifier = 0.5f
		burst.color = ASJRenderHelper.colorCode[worldObj.rand.nextInt(ASJRenderHelper.colorCode.size)]
		burst.mana = 120
		burst.startingMana = 340
		burst.minManaLoss = 50
		burst.manaLossPerTick = 1f
		burst.gravity = 0f
		
		val lenses = ItemLens.SUBTYPES + AlfheimClassTransformer.moreLenses
		
		var meta = worldObj.rand.nextInt(lenses + 1)
		if (meta == lenses) meta = ItemLens.STORM
		
		val lens = ItemStack(ModItems.lens, 1, meta)
		burst.sourceLens = lens
		
		v.rand().sub(0.5).normalize().mul(motionModifier.D).add(0.5).add(superTile!!)
		burst.setPosition(v.x, v.y, v.z)
		v.sub(0.5).sub(superTile!!)
		burst.setMotion(v.x, v.y, v.z)
		
		return burst
	}
	
	override fun performEffect(target: Any) {
		if (target is EntityManaBurst) target.spawn(worldObj)
	}
	
	override fun typeBits() = ALL
}