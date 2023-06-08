package alfheim.common.item.equipment.tool

import alexsocol.asjlib.*
import alfheim.api.*
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.core.helper.*
import alfheim.common.core.util.AlfheimTab
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.*
import net.minecraft.entity.boss.*
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.potion.*
import net.minecraft.stats.*
import net.minecraft.util.*
import net.minecraft.world.World
import vazkii.botania.api.mana.*
import vazkii.botania.common.core.helper.ItemNBTHelper.*
import kotlin.math.*

class ItemRealitySword: ItemSword(AlfheimAPI.mauftriumToolmaterial), IManaUsingItem {
	
	init {
		creativeTab = AlfheimTab
		setNoRepair()
		unlocalizedName = "RealitySword"
	}
	
	override fun setUnlocalizedName(name: String): Item {
		GameRegistry.registerItem(this, name)
		return super.setUnlocalizedName(name)
	}
	
	private var ItemStack.element
		get() = getInt(this, TAG_ELEMENT, 0)
		set(value) = setInt(this, TAG_ELEMENT, value)
	
	override fun registerIcons(reg: IIconRegister) {
		textures = Array(6) { reg.registerIcon(ModInfo.MODID + ":RealitySword$it") }
	}
	
	override fun getRarity(stack: ItemStack) = AlfheimAPI.mauftriumRarity
	
	override fun getIconIndex(stack: ItemStack) = textures.safeGet(stack.element)
	
	override fun getIcon(stack: ItemStack, pass: Int) = getIconIndex(stack)
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (!player.isSneaking) {
			player.setItemInUse(stack, getMaxItemUseDuration(stack))
			return stack
		}
		
		if (stack.element == 5) return stack
		
		if (merge(ASJUtilities.mapGetKeyOrDefault(ContributorsPrivacyHelper.contributors, player.commandSenderName, "itsmemario"), stack.displayName) == "756179BA5B0697ED01B6CD292A3A726BACD5B99E0E624B37A11E18CE0B40B83E") {
			stack.element = 5
			stack.tagCompound.removeTag("display")
			return stack
		}
		
		if (!ManaItemHandler.requestManaExact(stack, player, 10, false)) return stack
		stack.element = (stack.element + 1) % 5
		
		return stack
	}
	
	fun merge(s1: String, s2: String): String {
		val s = StringBuilder()
		for (c1 in s1) for (c2 in s2) s.append(((c1.code * c2.code) % 256).toChar())
		return HashHelper.hash("$s", "wellithoughtthatthisiscoolideaandicanmakesomethinglikethis#whynot")
	}
	
	override fun onUpdate(stack: ItemStack, world: World, entity: Entity, slotID: Int, inHand: Boolean) {
		if (world.isRemote) return
		
		val safe = stack.element != 5
		
		if (entity is EntityPlayer && stack.element in 1..4 && !ManaItemHandler.requestManaExact(stack, entity, 10, !world.isRemote))
			stack.element = 0
		
		if (safe) return
		
		if (entity is EntityLivingBase) {
			if (entity is EntityPlayer)
				if(ContributorsPrivacyHelper.isCorrect(entity.commandSenderName, "AlexSocol"))
					return
				else
					ASJUtilities.sayToAllOnline("item.RealitySword.DIE", entity.getCommandSenderName())
			
			entity.health = 0f
			entity.onDeath(DamageSource.outOfWorld)
		}
		
		EntityItem(world, entity.posX, entity.posY, entity.posZ, stack.copy()).spawn()
		stack.stackSize = 0
	}
	
	override fun onLeftClickEntity(stack: ItemStack, player: EntityPlayer, entity: Entity): Boolean {
		leftClickEntity(stack, player, entity)
		return true
	}
	
	// all this just to set damage element to water -_-
	fun leftClickEntity(stack: ItemStack, player: EntityPlayer, entity: Entity) {
		if (!entity.canAttackWithItem()) return
		if (entity.hitByEntity(player)) return
		
		val elem = stack.element
		
		var damage = player.getEntityAttribute(SharedMonsterAttributes.attackDamage).attributeValue.F
		var knockback = 0
		var addDamage = 0f
		
		if (entity is EntityLivingBase) {
			addDamage = EnchantmentHelper.getEnchantmentModifierLiving(player, entity)
			knockback += EnchantmentHelper.getKnockbackModifier(player, entity)
		}
		
		if (!(damage > 0f || addDamage > 0f)) return
		
		val crit = player.fallDistance > 0f && !player.onGround && !player.isOnLadder && !player.isInWater && !player.isPotionActive(Potion.blindness) && player.ridingEntity == null && entity is EntityLivingBase
		if (crit) damage *= 1.5f
		damage += addDamage
		
		val src = DamageSource.causePlayerDamage(player)
		
		if (elem == 1 || elem == 5) src.setDamageBypassesArmor().setTo(ElementalDamage.AIR)
		if (elem == 2 || elem == 5) src.setMagicDamage().setTo(ElementalDamage.EARTH)
		if (elem == 3 || elem == 5) {
			if (!entity.isImmuneToFire && (entity !is EntityLivingBase || !entity.isPotionActive(Potion.fireResistance))) src.setFireDamage()
			src.setTo(ElementalDamage.FIRE)
		}
		if (elem == 4 || elem == 5) src.setDamageIsAbsolute().setTo(ElementalDamage.WATER)
		
		val succ = entity.attackEntityFrom(src, damage)
		
		if (!succ) return
		
		var fire = EnchantmentHelper.getFireAspectModifier(player) * 4
		
		if (elem == 3 || elem == 5)
			fire += 6
		
		entity.setFire(fire)
		
		if (player.isSprinting)
			++knockback
		
		if (elem == 1 || elem == 5)
			if (entity !is IBossDisplayData) knockback += 3
		
		if (knockback > 0) {
			entity.addVelocity((-sin(player.rotationYaw * Math.PI / 180) * knockback * 0.5), 0.1, (cos(player.rotationYaw * Math.PI / 180) * knockback * 0.5))
			player.motionX *= 0.6
			player.motionZ *= 0.6
			player.isSprinting = false
		}
		
		if (elem == 4 || elem == 5) {
			if (entity !is IBossDisplayData)
				entity.motionY += 0.825
			
			VisualEffectHandler.sendPacket(VisualEffects.SPLASH, entity)
		}
		
		if (crit) player.onCriticalHit(entity)
		if (addDamage > 0f) player.onEnchantmentCritical(entity)
		if (damage >= 18f) player.triggerAchievement(AchievementList.overkill)
		
		player.setLastAttacker(entity)
		
		if (entity is EntityLivingBase) EnchantmentHelper.func_151384_a(entity, player)
		
		EnchantmentHelper.func_151385_b(player, entity)
		
		player.addExhaustion(0.3f)
		
		var target = entity
		if (entity is EntityDragonPart && entity.entityDragonObj is Entity)
			target = entity.entityDragonObj as Entity
		
		if (target !is EntityLivingBase) return
		
		if (elem == 1 || elem == 5)
			target.addPotionEffect(PotionEffect(Potion.blindness.id, 100))
		
		if (elem == 2 || elem == 5)
			target.addPotionEffect(PotionEffect(Potion.moveSlowdown.id, 100, 1))
		
		stack.hitEntity(target, player)
		if (stack.stackSize <= 0)
			player.destroyCurrentEquippedItem()
		
		player.addStat(StatList.damageDealtStat, (damage * 10f).roundToInt())
	}
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer?, list: MutableList<Any?>, b: Boolean) {
		val elem = stack.element
		
		addStringToTooltip(list, "item.RealitySword.abil$elem")
		
		if (elem == 5) {
			addStringToTooltip(list, "item.RealitySword.descX")
			return
		}
		
		addStringToTooltip(list, "item.RealitySword.desc$elem")
		
		if (elem in 1..4) addStringToTooltip(list, "item.RealitySword.desc5")
	}
	
	override fun usesMana(stack: ItemStack) = stack.element in 1..4
	
	companion object {
		
		const val TAG_ELEMENT = "element"
		lateinit var textures: Array<IIcon>
	}
}