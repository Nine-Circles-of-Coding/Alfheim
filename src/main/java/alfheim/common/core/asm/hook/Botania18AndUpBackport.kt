@file:Suppress("UNUSED_PARAMETER", "unused")

package alfheim.common.core.asm.hook

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.achievement.AlfheimAchievements
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.magtrees.sealing.EventHandlerSealingOak
import alfheim.common.core.helper.DiceDropsHelper
import alfheim.common.entity.boss.EntityFlugel.Companion.isRecordPlaying
import alfheim.common.entity.boss.EntityFlugel.Companion.playRecord
import alfheim.common.entity.boss.EntityFlugel.Companion.stopRecord
import cpw.mods.fml.relauncher.*
import gloomyfolken.hooklib.asm.*
import gloomyfolken.hooklib.asm.Hook.ReturnValue
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.*
import net.minecraft.entity.passive.*
import net.minecraft.entity.player.*
import net.minecraft.init.Items
import net.minecraft.item.*
import net.minecraft.potion.*
import net.minecraft.util.*
import net.minecraft.world.*
import vazkii.botania.client.core.proxy.ClientProxy
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.block.subtile.functional.*
import vazkii.botania.common.block.subtile.generating.*
import vazkii.botania.common.block.tile.TileAlfPortal
import vazkii.botania.common.core.handler.InternalMethodHandler
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.entity.EntityMagicLandmine
import vazkii.botania.common.item.ItemGrassHorn
import vazkii.botania.common.item.relic.*
import vazkii.botania.common.lib.LibMisc

object Botania18AndUpBackport {

	// ######## Rannuncarpus filter ########
	
	var shouldntHookRannuncarpus = true
	var filter = ArrayList<ItemStack>()
	
	val orientationToDir = intArrayOf(3, 4, 2, 5)
	
	@JvmStatic
	@Hook
	fun onUpdate(tile: SubTileRannuncarpus) {
		if (tile.ticksExisted % 10 != 0) return
		
		shouldntHookRannuncarpus = false
		
		for (dir in LibMisc.CARDINAL_DIRECTIONS) {
			val x = tile.supertile.xCoord + dir.offsetX
			val y = tile.supertile.yCoord
			val z = tile.supertile.xCoord + dir.offsetZ
			val aabb = getBoundingBox(x, y, z, x + 1, y + 1, z + 1)
			
			getEntitiesWithinAABB(tile.supertile.worldObj, EntityItemFrame::class.java, aabb).forEach { frame ->
				val orientation = frame.hangingDirection
				if (orientationToDir[orientation] == dir.ordinal) filter.add(frame.displayedItem)
			}
		}
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "getEntitiesWithinAABB")
	fun originalNameAppearsInCodeCompletionSoMovedItToTargetMethodName(clazz: Class<*>, axisAlignedBB: AxisAlignedBB, @ReturnValue result: MutableList<*>?): List<*>? {
		if (shouldntHookRannuncarpus || result == null || filter.isEmpty()) return result
		shouldntHookRannuncarpus = false
		
		val iterator = result.iterator()
		while (iterator.hasNext()) {
			val entity = iterator.next() as? EntityItem ?: continue
			val stack = entity.entityItem ?: continue
			
			for (filterStack in filter) {
				if (filterStack.item === stack.item &&
					filterStack.getItemDamage() == stack.getItemDamage() &&
					ItemStack.areItemStackTagsEqual(filterStack, stack)) continue
					
				iterator.remove()
				break
			}
		}
		
		filter.clear()
		return result
	}
	
	// ######## Fallen Kanade heal tameables ########
	
	private const val SubTileFallenKanade_RANGE = 2
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun onUpdate(tile: SubTileFallenKanade) {
		val cost = 120
		
		if (tile.supertile.worldObj.isRemote || tile.supertile.worldObj.provider.dimensionId == 1) return
		if (tile.mana < cost) return
		
		val pets = selectEntitiesWithinAABB(tile.supertile.worldObj, EntityLivingBase::class.java, tile.supertile.boundingBox(SubTileFallenKanade_RANGE)) {
			it.isEntityAlive && (it is EntityTameable && it.isTamed || it is EntityHorse && it.isTame) && !it.isPotionActive(Potion.regeneration)
		}
		
		for (pet in pets) {
			if (tile.mana < cost) return
			
			pet.addPotionEffect(PotionEffect(Potion.regeneration.id, 60, 2))
			tile.mana -= cost
		}
	}
	
	// ######## Horn of the Canopy vines breaking ########
	
	var shouldHookVines = false
	
	@JvmStatic
	@Hook(targetMethod = "breakGrass")
	fun breakGrassPre(static: ItemGrassHorn?, world: World?, stack: ItemStack?, stackDmg: Int, srcx: Int, srcy: Int, srcz: Int) {
		shouldHookVines = true
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE)
	fun isLeaves(block: Block, world: IBlockAccess?, x: Int, y: Int, z: Int) = shouldHookVines && block.material === Material.vine
	
	@JvmStatic
	@Hook(targetMethod = "breakGrass")
	fun breakGrassPost(static: ItemGrassHorn?, world: World?, stack: ItemStack?, stackDmg: Int, srcx: Int, srcy: Int, srcz: Int) {
		shouldHookVines = false
	}
	
	// ######## https://botaniamod.net/changelog.html#r1-15-384-fake #5
	
	@JvmStatic
	@Hook
	fun onUnequipped(ring: ItemLokiRing, stack: ItemStack?, player: EntityLivingBase?) {
		ItemNBTHelper.setInt(stack, "xOrigin", 0)
		ItemNBTHelper.setInt(stack, "yOrigin", -1)
		ItemNBTHelper.setInt(stack, "zOrigin", 0)
	}
	
	// ######## https://botaniamod.net/changelog.html#r1-11-378-fake #16
	
	@JvmStatic
	@Hook
	fun addItem(tile: TileAlfPortal, stack: ItemStack?) {
		if (stack?.item !== Items.bread || tile.worldObj.isRemote) return
		
		val (x, y, z) = Vector3.fromTileEntityCenter(tile)
		tile.worldObj.createExplosion(null, x, y + 2, z, 3f, true)
		
		getEntitiesWithinAABB(tile.worldObj, EntityPlayerMP::class.java, getBoundingBox(x, y, z).expand(8)).forEach {
			it.triggerAchievement(AlfheimAchievements.breadBoom)
		}
	}
	
	// ######## https://botaniamod.net/changelog.html#1-16-5-420-1-fake #3 AND https://botaniamod.net/changelog.html#r1-7-229-fake #11
	// looks like someone not learning their mistakes
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun getMaxMana(tile: SubTileNarslimmus) = 13120
	
	// ######## https://botaniamod.net/changelog.html#1-16-4-413-fake #4
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS, injectOnExit = true)
	fun isBotaniaFlower(imh: InternalMethodHandler, world: World, x: Int, y: Int, z: Int, @ReturnValue result: Boolean): Boolean {
		val block = world.getBlock(x, y, z)
		val meta = world.getBlockMetadata(x, y, z)
		
		return result || block === ModBlocks.floatingFlower || block === ModBlocks.floatingSpecialFlower || block === AlfheimBlocks.rainbowFlowerFloating || (block === AlfheimBlocks.rainbowGrass && (meta == 2 || meta == 3))
	}
	
	// ######## https://botaniamod.net/changelog.html#r1-10-355-fake #27
	
	@SideOnly(Side.CLIENT)
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun playRecordClientSided(proxy: ClientProxy, world: World, x: Int, y: Int, z: Int, record: ItemRecord?) {
		if (record == null) return world.stopRecord(x, y, z)
		if (world.isRecordPlaying(x, y, z)) return
		world.playRecord(record, x, y, z)
	}
	
	// ######## https://botaniamod.net/changelog.html#1-16-5-420-1-fake #2
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE)
	fun breakGrass(static: ItemGrassHorn?, world: World, stack: ItemStack?, stackDmg: Int, srcx: Int, srcy: Int, srcz: Int) =
		ASJUtilities.chance(100f - EventHandlerSealingOak.calculateMultiplier(world, srcx, srcy, srcz) * 100)
	
	
	
	// ######## https://botaniamod.net/changelog.html#1-16-5-417-fake #24
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS) // fuck it, I'll just overwrite that shit
	fun onItemRightClick(dice: ItemDice, stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (!ItemRelic.isRightPlayer(player, stack) || player.worldObj.isRemote) return stack
		
		var relic = ItemDice.SIDES_FOR_MOON_PHASES[world.provider.getMoonPhase(world.worldTime)]
		if (dice.hasRelicAlready(player, relic)) {
			val possible = java.util.ArrayList<Int>()
			val alreadyHas = java.util.ArrayList<Int>()
			for (i in 0..5) (if (dice.hasRelicAlready(player, i)) alreadyHas else possible).add(i)
			if (alreadyHas.size > 0) possible.add(alreadyHas.random(world.rand)!!)
			relic = possible.random(world.rand)!!
		}
		world.playSoundAtEntity(player, "random.bow", 0.5f, 0.4f / (world.rand.nextFloat() * 0.4f + 0.8f))
		if (dice.hasRelicAlready(player, relic)) {
			val s = if (DiceDropsHelper.addRewards(player, relic)) "botaniamisc.diceRoll" else "botaniamisc.dudDiceRoll"
			player.addChatMessage(ChatComponentTranslation(s, relic + 1).setChatStyle(ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)))
			stack.stackSize--
			return stack
		}
		player.addChatMessage(ChatComponentTranslation("botaniamisc.diceRoll", relic + 1).setChatStyle(ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)))
		return ItemDice.relicStacks[relic].copy()
	}
	
	// ######## https://botaniamod.net/changelog.html#r1-8-311-fake #4
	
	@JvmStatic
	@Hook(targetMethod = "onUpdate")
	fun onUpdatePre(entity: EntityMagicLandmine) {
		hookAttackEntityFrom = true
	}
	
	var hookAttackEntityFrom = false
	
	@JvmStatic
	@Hook
	fun attackEntityFrom(player: EntityPlayer?, source: DamageSource?, amount: Float): Boolean {
		if (hookAttackEntityFrom) source?.setMagicDamage()
		return false
	}
	
	@JvmStatic
	@Hook(targetMethod = "onUpdate", injectOnExit = true)
	fun onUpdatePost(entity: EntityMagicLandmine) {
		hookAttackEntityFrom = false
	}
	
	// ######## https://botaniamod.net/changelog.html#r1-9-322-fake #... (too lazy to count)
	
	@JvmStatic
	@Hook
	fun onUpdate(tile: SubTileArcaneRose) {
		if (tile.mana >= tile.maxMana || tile.ticksExisted % 5 != 0) return
		
		getEntitiesWithinAABB(tile.supertile.worldObj, EntityXPOrb::class.java, tile.supertile.boundingBox().expand(1, 0, 1)).forEach { xp ->
			val new = tile.mana + (xp.xpValue * 35)
			if (new > tile.maxMana) return@forEach
			
			tile.mana = new
			xp.xpValue = 0
			xp.playSoundAtEntity("random.orb", 0.1f, 0.5f * ((xp.worldObj.rand.nextFloat() - xp.worldObj.rand.nextFloat()) * 0.7f + 1.8f))
			xp.setDead()
			
			return
		}
	}
}