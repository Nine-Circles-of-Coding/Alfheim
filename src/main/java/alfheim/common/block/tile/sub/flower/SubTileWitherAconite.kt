package alfheim.common.block.tile.sub.flower

import alexsocol.asjlib.*
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.entity.item.EntityItem
import net.minecraft.init.Items
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.IIcon
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.subtile.RadiusDescriptor.Square
import vazkii.botania.api.subtile.SubTileGenerating

class SubTileWitherAconite: SubTileGenerating() {
	
	var burnTime = 0
	
	override fun onUpdate() {
		super.onUpdate()
		
		if (linkedCollector == null) return
		
		if (burnTime > 0) {
			if (supertile.worldObj.rand.nextInt(10) == 0)
				supertile.worldObj.spawnParticle("iconcrack_399_0", supertile.xCoord + 0.4 + Math.random() * 0.2, supertile.yCoord + 0.65, supertile.zCoord + 0.4 + Math.random() * 0.2, 0.0, 0.0, 0.0)
			
			burnTime--
			
			return
		}
		
		if (mana >= maxMana) return
		
		var didSomething = false
		val slowdown = slowdownFactor
		val items = getEntitiesWithinAABB(supertile.worldObj, EntityItem::class.java, supertile.boundingBox(RANGE))
		
		for (item in items) {
			if (item.age < 59 + slowdown || item.isDead) continue
			
			val stack = item.entityItem ?: continue
			if (stack.item !== Items.nether_star || stack.stackSize <= 0) continue
			
			burnTime = 5000
			
			if (supertile.worldObj.isRemote) {
				supertile.worldObj.spawnParticle("iconcrack_399_0", supertile.xCoord + 0.4 + Math.random() * 0.2, supertile.yCoord + 0.65, supertile.zCoord + 0.4 + Math.random() * 0.2, 0.0, 0.0, 0.0)
			} else {
				stack.stackSize--
				supertile.worldObj.playSoundEffect(supertile.xCoord.toDouble(), supertile.yCoord.toDouble(), supertile.zCoord.toDouble(), "botania:endoflame", 0.2f, 1f)
				if (stack.stackSize == 0) item.setDead()
				didSomething = true
			}
			
			break
		}
		
		if (didSomething) sync()
	}
	
	override fun getRadius() = Square(toChunkCoordinates(), RANGE)
	
	override fun writeToPacketNBT(cmp: NBTTagCompound) {
		super.writeToPacketNBT(cmp)
		cmp.setInteger(TAG_BURN_TIME, burnTime)
	}
	
	override fun readFromPacketNBT(cmp: NBTTagCompound) {
		super.readFromPacketNBT(cmp)
		burnTime = cmp.getInteger(TAG_BURN_TIME)
	}
	
	override fun canGeneratePassively() = burnTime > 0
	
	override fun getColor() = 0x333333
	
	override fun getEntry() = AlfheimLexiconData.flowerAconite
	
	override fun getDelayBetweenPassiveGeneration() = 10
	
	override fun getValueForPassiveGeneration() = maxMana
	
	override fun getMaxMana() = 1000
	
	override fun getIcon(): IIcon? = BotaniaAPI.getSignatureForName("witherAconite").getIconForStack(null)
	
	companion object {
		
		private const val TAG_BURN_TIME = "burnTime"
		private const val RANGE = 3
	}
}
