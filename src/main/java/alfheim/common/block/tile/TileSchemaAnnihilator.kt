package alfheim.common.block.tile

import net.minecraft.entity.player.EntityPlayer

class TileSchemaAnnihilator: TileSchemaController() {
	
	override fun blockActivated(player: EntityPlayer) {
		if (pos_x != null && pos_y != null && pos_z != null) {
			for (x in xCoord re pos_x!!.x) {
				for (y in yCoord re pos_y!!.y) {
					for (z in zCoord re pos_z!!.z) {
						worldObj.setBlockToAir(x, y, z)
					}
				}
			}
		}
	}
}
