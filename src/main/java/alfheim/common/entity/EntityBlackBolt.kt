package alfheim.common.entity

import alexsocol.asjlib.*
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.util.DamageSourceSpell
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.effect.EntityWeatherEffect
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.*

class EntityBlackBolt(world: World): EntityWeatherEffect(world) {
	
	/** Declares which state the lightning bolt is in. Whether it's in the air, hit the ground, etc.  */
	var lightningState = 0
	/** A random long that is used to change the vertex of the lightning rendered in RenderLightningBolt  */
	var boltVertex: Long = 0
	/** Determines the time before the EntityLightningBolt is destroyed. It is a random integer decremented over time.  */
	var boltLivingTime = 0
	
	var targetBlock: Block
		get() = Block.getBlockFromName(dataWatcher.getWatchableObjectString(2))
		set(value) = dataWatcher.updateObject(2, GameRegistry.findUniqueIdentifierFor(value).toString())
	
	init {
		lightningState = 2
		boltVertex = rand.nextLong()
		boltLivingTime = rand.nextInt(3) + 1
		
		if (!world.isRemote) targetBlock = if (rand.nextBoolean()) AlfheimBlocks.redFlame else AlfheimBlocks.poisonIce
	}
	
	override fun onUpdate() {
		if (lightningState == 2) {
			worldObj.playSoundEffect(posX, posY, posZ, "ambient.weather.thunder", 10000.0f, 0.8f + rand.nextFloat() * 0.2f)
			worldObj.playSoundEffect(posX, posY, posZ, "random.explode", 2.0f, 0.5f + rand.nextFloat() * 0.2f)
		}
		
		if (lightningState == 1 && !worldObj.isRemote) placeBlocks(10)
		
		--lightningState
		
		if (lightningState < 0) {
			if (boltLivingTime == 0) {
				setDead()
			} else if (lightningState < -rand.nextInt(10)) {
				--boltLivingTime
				lightningState = 1
				boltVertex = rand.nextLong()
				if (!worldObj.isRemote) placeBlocks(1)
			}
		}
		
		if (lightningState < 0) return
		
		if (worldObj.isRemote) {
			worldObj.lastLightningBolt = 2
			return
		}
		
		val list = getEntitiesWithinAABB(worldObj, Entity::class.java, boundingBox(4).expand(0, 1, 0).offset(0, 1, 0))
		list.remove(this)
		list.forEach {
			it.attackEntityFrom(if (targetBlock === AlfheimBlocks.redFlame) DamageSourceSpell.soulburn else DamageSourceSpell.nifleice, 10f)
		}
	}
	
	fun placeBlocks(count: Int) {
		if (worldObj.getBlock(this) === Blocks.air)
			worldObj.setBlock(this, targetBlock)
		
		for (a in 0 until count) {
			val i = ASJUtilities.randInBounds(-3, 3, rand)
			val j = ASJUtilities.randInBounds(-3, 3, rand)
			val k = ASJUtilities.randInBounds(-3, 3, rand)
			
			if (worldObj.getBlock(this, i, j, k) === Blocks.air) {
				worldObj.setBlock(this, targetBlock, i, j, k)
			}
		}
	}
	
	override fun entityInit() {
		dataWatcher.addObject(2, "minecraft:fire")
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound?) = Unit
	override fun writeEntityToNBT(nbt: NBTTagCompound?) = Unit
}
