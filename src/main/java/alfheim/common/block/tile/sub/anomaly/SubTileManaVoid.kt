package alfheim.common.block.tile.sub.anomaly

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.block.tile.SubTileAnomalyBase
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import vazkii.botania.api.mana.ManaItemHandler

class SubTileManaVoid: SubTileAnomalyBase() {
	
	var mana: Int = 0
	internal val v = Vector3()
	
	override val targets: List<Any>
		get() = if (inWG()) EMPTY_LIST else allAround(EntityPlayer::class.java, radius.D)
	
	public override fun update() {
		if (mana >= 120000) {
			for (player in allAround(EntityPlayer::class.java, radius.D)) {
				radius = 50
				for (i in 0..99) performEffect(player)
			}
			
			radius = 10
			worldObj.createExplosion(null, x.D, y.D, z.D, radius.F, false)
			
			VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.MANABURST, worldObj.provider.dimensionId, x.D, y.D, z.D)
			
			mana = 0
		}
	}
	
	override fun performEffect(target: Any) {
		if (target !is EntityPlayer) return
		
		val m = ManaItemHandler.requestMana(ItemStack(Blocks.stone), target, 100, true)
		if (m > 0) {
			mana += m
			
			if (!target.worldObj.isRemote) VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.MANAVOID, worldObj.provider.dimensionId, x.D, y.D, z.D, target.posX, target.posY + 1.0, target.posZ)
		}
	}
	
	override fun typeBits() = MANA
	
	override fun writeCustomNBT(cmp: NBTTagCompound) {
		super.writeCustomNBT(cmp)
		cmp.setInteger(TAG_MANA, mana)
	}
	
	override fun readCustomNBT(cmp: NBTTagCompound) {
		super.writeCustomNBT(cmp)
		mana = cmp.getInteger(TAG_MANA)
	}
	
	companion object {
		
		const val TAG_MANA = "mana"
		var radius = 10
	}
}
