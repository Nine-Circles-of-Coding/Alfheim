package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.mc
import alfheim.common.network.M0ds
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.Message0dS
import cpw.mods.fml.relauncher.*
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import vazkii.botania.api.item.IBaubleRender

class ItemCloudPendant @JvmOverloads constructor(name: String = "CloudPendant", val maxAllowedJumps: Int = 2): ItemPendant(name), IBaubleRender {
	
	override fun onWornTick(stack: ItemStack, player: EntityLivingBase) {
		super.onWornTick(stack, player)
		if (player.worldObj.isRemote) clientWornTick(player)
	}
	
	@SideOnly(Side.CLIENT)
	fun clientWornTick(player: EntityLivingBase) {
		if (player is EntityPlayerSP && player === mc.thePlayer) {
			
			if (player.onGround)
				timesJumped = 0
			else {
				jumpDown = if (player.movementInput.jump) {
					if (!jumpDown && timesJumped < maxAllowedJumps) {
						player.jump()
						NetworkService.sendToServer(Message0dS(M0ds.JUMP))
						timesJumped++
					}
					true
				} else
					false
			}
		}
	}
	
	companion object {
		
		private var timesJumped: Int = 0
		private var jumpDown: Boolean = false
	}
}