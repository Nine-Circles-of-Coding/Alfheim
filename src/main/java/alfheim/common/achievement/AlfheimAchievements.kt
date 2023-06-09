package alfheim.common.achievement

import alexsocol.asjlib.capitalized
import alfheim.api.ModInfo
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.AlfheimAchievementHandler
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
import net.minecraft.block.Block
import net.minecraft.init.*
import net.minecraft.item.*
import net.minecraft.stats.Achievement
import net.minecraftforge.common.AchievementPage
import vazkii.botania.api.item.IRelic
import vazkii.botania.common.item.ModItems

object AlfheimAchievements {
	
	val achievements: MutableList<Achievement> = ArrayList()
	
	val alfheim: Achievement // go to alfheim
	val infuser: Achievement // build up infuser
	val wingedHussar: Achievement // become winged hussar
	
	// relics
	val akashic: Achievement
	val daolos: Achievement
	val excaliber: Achievement
	val flugelSoul: Achievement
	val gjallarhorn: Achievement
	val gleipnir: Achievement
	val gungnir: Achievement
	val mask: Achievement
	val mjolnir: Achievement
	val moonlightBow: Achievement
	val ringHeimdall: Achievement
	val ringNjord: Achievement
	val ringSif: Achievement
	val subspace: Achievement
	
	val breadBoom: Achievement // they are overfed
	val divineMarksman: Achievement // show your marksman skill
	val newChance: Achievement // remove race with akashic
	val rosaBomb: Achievement // bomb 'em all
	
	val flugelKill: Achievement
	val flugelHardKill: Achievement
	val outstander: Achievement
	
	val firework: Achievement
	
	init {
		alfheim = AlfheimAchievement("alfheim", 0, 0, ItemStack(AlfheimBlocks.alfheimPortal, 1, 1), null)
		infuser = AlfheimAchievement("infuser", 1, -2, AlfheimBlocks.manaInfuser, alfheim)
		
		wingedHussar = AlfheimAchievement("wingedHussaurs", -1, -3, AlfheimItems.elvoriumHelmet, infuser).setSpecial()
		
		flugelKill = AlfheimAchievement("flugelKill", 2, 0, ModItems.flightTiara, null)
		flugelSoul = AlfheimAchievement("flugelSoul", 4, 0, AlfheimItems.flugelSoul, flugelKill)
		
		flugelHardKill = AlfheimAchievement("flugelHardKill", 5, 1, ElvenResourcesMetas.MuspelheimEssence.stack, flugelSoul)
		mask = AlfheimAchievement("mask", 6, 0, AlfheimItems.mask, flugelSoul)
		outstander = AlfheimAchievement("outstander", 5, -1, Items.diamond_chestplate, mask).setSpecial()
		
		akashic = AlfheimAchievement("akashic", 8, -5, AlfheimItems.akashicRecords, mask)
		daolos = AlfheimAchievement("daolos", 8, 6, AlfheimItems.daolos, null)
		excaliber = AlfheimAchievement("excaliber", 8, 0, AlfheimItems.excaliber, mask)
		gjallarhorn = AlfheimAchievement("gjallarhorn", 8, 4, AlfheimItems.gjallarhorn, null)
		gleipnir = AlfheimAchievement("gleipnir", 8, 3, AlfheimItems.gleipnir, null)
		gungnir = AlfheimAchievement("gungnir", 8, 2, AlfheimItems.gungnir, null)
		mjolnir = AlfheimAchievement("mjolnir", 8, 5, AlfheimItems.mjolnir, null)
		moonlightBow = AlfheimAchievement("moonlightBow", 8, -1, AlfheimItems.moonlightBow, mask)
		ringHeimdall = AlfheimAchievement("ringHeimdall", 8, -4, AlfheimItems.priestRingHeimdall, mask)
		ringNjord = AlfheimAchievement("ringNjord", 8, -3, AlfheimItems.priestRingNjord, mask)
		ringSif = AlfheimAchievement("ringSif", 8, -2, AlfheimItems.priestRingSif, mask)
		subspace = AlfheimAchievement("subspace", 8, 1, AlfheimItems.subspaceSpear, null)
		
		breadBoom = AlfheimAchievement("breadBoom", 32, 32, Items.bread, null).setSpecial()
		newChance = AlfheimAchievement("newChance", 10, -5, Items.spawn_egg, akashic)
		divineMarksman = AlfheimAchievement("divineMarksman", 10, -1, ItemStack(Blocks.red_flower, 1, 2), moonlightBow).setSpecial()
		rosaBomb = AlfheimAchievement("rosaBomb", 10, 1, Blocks.red_flower, subspace).setSpecial()
		
		firework = AlfheimAchievement("firework", -3, 3, Items.fireworks, null).setSpecial()
		
		AchievementPage.registerAchievementPage(AchievementPage(ModInfo.MODID.capitalized(), *achievements.toTypedArray()))
		
		AlfheimAchievementHandler
	}
}

@Suppress("LeakingThis")
open class AlfheimAchievement(name: String, x: Int, y: Int, icon: ItemStack, parent: Achievement?): Achievement("achievement.${ModInfo.MODID}:$name", "${ModInfo.MODID}:$name", x, y, icon, parent) {
	
	init {
		AlfheimAchievements.achievements.add(this)
		registerStat()
		
		if (icon.item is IRelic) (icon.item as IRelic).bindAchievement = this
	}
	
	constructor(name: String, x: Int, y: Int, icon: Item, parent: Achievement?): this(name, x, y, ItemStack(icon), parent)
	
	constructor(name: String, x: Int, y: Int, icon: Block, parent: Achievement?): this(name, x, y, ItemStack(icon), parent)
}