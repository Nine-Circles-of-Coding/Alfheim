package alfheim.common.item.relic

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.AlfheimAPI
import alfheim.common.core.helper.*
import alfheim.common.core.util.AlfheimTab
import com.google.common.collect.*
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.*
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.item.ItemStack
import net.minecraft.potion.Potion
import net.minecraft.server.MinecraftServer
import net.minecraft.stats.Achievement
import net.minecraft.util.*
import net.minecraft.world.World
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.internal.IManaBurst
import vazkii.botania.api.item.IRelic
import vazkii.botania.api.mana.*
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.entity.EntityManaBurst
import vazkii.botania.common.item.equipment.tool.manasteel.ItemManasteelSword
import vazkii.botania.common.item.relic.ItemRelic
import java.util.*

/**
 * This code is completely copied from 208th Botania version and fully made by Vazkii or whoever... :D<br></br>
 * Hope all required stuff is already done by Botania using iterfaces and stuff...
 */
class ItemExcaliber: ItemManasteelSword(AlfheimAPI.EXCALIBER, "Excaliber"), IRelic, ILensEffect {
	
	internal lateinit var achievement: Achievement
	
	init {
		creativeTab = AlfheimTab
	}
	
	override fun onUpdate(stack: ItemStack, world: World, player: Entity?, slotID: Int, inHand: Boolean) {
		if (player !is EntityPlayer) return
		ItemRelic.updateRelic(stack, player)
		if (!ItemRelic.isRightPlayer(player, stack)) return
		val haste = player.getActivePotionEffect(Potion.digSpeed.id)
		val check = if (haste == null) 1f / 6f else if (haste.getAmplifier() == 0) 0.4f else if (haste.getAmplifier() == 2) 1f / 3f else 0.5f
		if (world.isRemote || !inHand || player.swingProgress != check) return
		getBurst(player, stack).spawn()
		player.playSoundAtEntity("botania:terraBlade", 0.4f, 1.4f)
	}
	
	override fun getIsRepairable(stack: ItemStack?, material: ItemStack?) = false
	
	override fun addInformation(stack: ItemStack?, player: EntityPlayer?, infoList: List<Any?>, advTooltip: Boolean) =
		ItemRelic.addBindInfo(infoList, stack, player)
	
	override fun bindToUsername(playerName: String, stack: ItemStack) =
		ItemRelic.bindToUsernameS(playerName, stack)
	
	override fun getSoulbindUsername(stack: ItemStack) = ItemRelic.getSoulbindUsernameS(stack)!!
	
	override fun getBindAchievement() = achievement
	
	override fun setBindAchievement(achievement: Achievement) {
		this.achievement = achievement
	}
	
	override fun usesMana(stack: ItemStack?) = false
	
	override fun isItemTool(p_77616_1_: ItemStack) = true
	
	override fun getEntityLifespan(itemStack: ItemStack?, world: World?) = Integer.MAX_VALUE
	
	override fun getAttributeModifiers(stack: ItemStack): Multimap<String, AttributeModifier> {
		val multimap = HashMultimap.create<String, AttributeModifier>()
		multimap.put(SharedMonsterAttributes.attackDamage.attributeUnlocalizedName, AttributeModifier(field_111210_e, "Weapon modifier", 10.0, 0))
		multimap.put(SharedMonsterAttributes.movementSpeed.attributeUnlocalizedName, AttributeModifier(uuid, "Weapon modifier", 0.3, 1))
		return multimap
	}
	
	fun getBurst(player: EntityPlayer, stack: ItemStack): EntityManaBurst {
		val burst = EntityManaBurst(player)
		
		val motionModifier = 7f
		
		burst.color = 0xFFFF20
		burst.mana = 1
		burst.startingMana = 1
		burst.minManaLoss = 200
		burst.manaLossPerTick = 1f
		burst.gravity = 0f
		burst.setMotion(burst.motionX * motionModifier, burst.motionY * motionModifier, burst.motionZ * motionModifier)
		
		val lens = stack.copy()
		ItemNBTHelper.setString(lens, TAG_ATTACKER_USERNAME, player.commandSenderName)
		burst.sourceLens = lens
		return burst
	}
	
	override fun apply(stack: ItemStack, props: BurstProperties) = Unit
	
	override fun collideBurst(burst: IManaBurst, pos: MovingObjectPosition, isManaBlock: Boolean, dead: Boolean, stack: ItemStack) = dead
	
	override fun updateBurst(burst: IManaBurst, stack: ItemStack) {
		val entity = burst as EntityThrowable
		val axis = getBoundingBox(entity.posX, entity.posY, entity.posZ, entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).expand(1)
		
		val attacker = ItemNBTHelper.getString(burst.sourceLens, TAG_ATTACKER_USERNAME, "")
		var homeID = ItemNBTHelper.getInt(stack, TAG_HOME_ID, -1)
		if (homeID == -1) {
			val axis1 = getBoundingBox(entity.posX, entity.posY, entity.posZ, entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).expand(5)
			val entities = getEntitiesWithinAABB(entity.worldObj, EntityLivingBase::class.java, axis1)
			entities.forEach {
				if (it is EntityPlayer || it !is IMob || it.hurtTime != 0) return@forEach
				homeID = it.entityId
				ItemNBTHelper.setInt(stack, TAG_HOME_ID, homeID)
			}
		}
		val entities = getEntitiesWithinAABB(entity.worldObj, EntityLivingBase::class.java, axis)
		val home: Entity?
		if (homeID != -1) {
			home = entity.worldObj.getEntityByID(homeID)
			if (home != null) {
				val vecMotion = Vector3.fromEntityCenter(home).sub(Vector3.fromEntityCenter(entity))
				vecMotion.normalize().mul(Vector3(entity.motionX, entity.motionY, entity.motionZ).length())
				burst.setMotion(vecMotion.x, vecMotion.y, vecMotion.z)
			}
		}
		
		entities.forEach {
			if (it is EntityPlayer && !(it.commandSenderName != attacker && (MinecraftServer.getServer() == null || MinecraftServer.getServer().isPVPEnabled))) return@forEach
			if (it.hurtTime != 0) return@forEach
			val cost = 1
			val mana = burst.mana
			if (mana < cost) return@forEach
			burst.mana = mana - cost
			var damage = 4f + AlfheimAPI.EXCALIBER.damageVsEntity
			if (burst.isFake || entity.worldObj.isRemote) return@forEach
			val player = it.worldObj.getPlayerEntityByName(attacker)
			val mod = player?.getAttributeMap()?.getAttributeInstance(SharedMonsterAttributes.attackDamage)?.attributeValue?.F
			damage = mod ?: damage
			if (player != null) damage += EnchantmentHelper.getEnchantmentModifierLiving(player, it)
			it.attackEntityFrom(if (player == null) DamageSource.magic else DamageSource.causePlayerDamage(player).setDamageBypassesArmor().setMagicDamage().setTo(ElementalDamage.LIGHTNESS), damage)
			entity.setDead()
			return
		}
	}
	
	override fun doParticles(burst: IManaBurst, stack: ItemStack) = true
	
	override fun getRarity(sta: ItemStack) = BotaniaAPI.rarityRelic!!
	
	companion object {
		
		val uuid = UUID.fromString("7d5ddaf0-15d2-435c-8310-bdfc5fd1522d")!!
		
		const val TAG_ATTACKER_USERNAME = "attackerUsername"
		const val TAG_HOME_ID = "homeID"
	}
}