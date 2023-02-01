package alfheim.common.block.tile.sub.flower

import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.util.IIcon
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.lexicon.LexiconEntry
import vazkii.botania.api.subtile.*

class SubTileAquapanthus: SubTileFunctional() {
	
	override fun onUpdate() {
		super.onUpdate()
		
		if (supertile.worldObj.isRemote) return
		
		if (redstoneSignal > 0 || mana < 5) return
		mana -= 5
		
		for (i in -RANGE..RANGE)
			for (j in -RANGE..RANGE)
				for (k in -RANGE..RANGE) {
					val x = i + supertile.xCoord
					val y = j + supertile.yCoord
					val z = k + supertile.zCoord
					
					supertile.worldObj.getBlock(x, y, z).fillWithRain(supertile.worldObj, x, y, z)
				}
	}
	
	override fun getRadius() = RadiusDescriptor.Square(toChunkCoordinates(), RANGE)
	
	override fun acceptsRedstone() = true
	
	override fun getColor() = 0x4444FF
	
	override fun getMaxMana() = 750
	
	override fun getEntry() = AlfheimLexiconData.flowerAquapanthus
	
	override fun getIcon(): IIcon? = BotaniaAPI.getSignatureForName("aquapanthus").getIconForStack(null)
	
	companion object {
		const val RANGE = 3
	}
}