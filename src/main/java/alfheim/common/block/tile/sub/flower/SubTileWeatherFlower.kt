package alfheim.common.block.tile.sub.flower

import alexsocol.asjlib.I
import alfheim.common.block.tile.sub.flower.AlfheimSignature.Companion.isOnSpecialSoil
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.util.IIcon
import net.minecraft.world.World
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.subtile.signature.PassiveFlower
import vazkii.botania.common.block.subtile.generating.SubTilePassiveGenerating

abstract class SubTileWeatherFlower: SubTilePassiveGenerating() {
	
	override fun getDelayBetweenPassiveGeneration() = 2
	
	override fun getValueForPassiveGeneration() = 1
	
	override fun getMaxMana() = if (isOnSpecialSoil) 400 else 200
	
	override fun getColor() = 0x9CFFFF
	
	override fun canGeneratePassively(): Boolean {
		return canGenerate(supertile.worldObj, supertile.xCoord, supertile.yCoord, supertile.zCoord)
	}
	
	abstract fun canGenerate(world: World, x: Int, y: Int, z: Int): Boolean
}

@PassiveFlower
class SubTileRainFlower: SubTileWeatherFlower() {
	
	override fun canGenerate(world: World, x: Int, y: Int, z: Int): Boolean {
		return world.canLightningStrikeAt(x, y, z)
	}
	
	override fun getEntry() = AlfheimLexiconData.flowerRain
	
	override fun getIcon(): IIcon? = BotaniaAPI.getSignatureForName("rainFlower").getIconForStack(null)
}

@PassiveFlower
class SubTileSnowFlower: SubTileWeatherFlower() {
	
	override fun canGenerate(world: World, x: Int, y: Int, z: Int): Boolean {
		return world.isRaining && world.func_147478_e(x, y, z, false) && world.canBlockSeeTheSky(x, y, z)
	}
	
	override fun getEntry() = AlfheimLexiconData.flowerSnow
	
	override fun getIcon(): IIcon? = BotaniaAPI.getSignatureForName("snowFlower").getIconForStack(null)
}

@PassiveFlower
class SubTileWindFlower: SubTileWeatherFlower() {
	
	var newMana = -1
	
	override fun canGenerate(world: World, x: Int, y: Int, z: Int): Boolean {
		val ret = y > 127 && world.canBlockSeeTheSky(x, y + 1, z)
		
		newMana = (if (ret) y / 100 * if (world.canLightningStrikeAt(x, y, z)) 1.5 else 0.75 else 0.0).I
		
		return ret
	}
	
	override fun getValueForPassiveGeneration(): Int {
		return newMana
	}
	
	override fun getMaxMana() = 300
	
	override fun getEntry() = AlfheimLexiconData.flowerWind
	
	override fun getIcon(): IIcon? = BotaniaAPI.getSignatureForName("windFlower").getIconForStack(null)
}