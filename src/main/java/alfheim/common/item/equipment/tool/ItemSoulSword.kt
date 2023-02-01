package alfheim.common.item.equipment.tool

import alexsocol.asjlib.*
import alfheim.api.*
import alfheim.client.core.helper.IconHelper
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.common.entity.boss.EntityFlugel
import alfheim.common.item.AlfheimItems
import com.google.common.collect.*
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.*
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.boss.EntityDragonPart
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.potion.Potion
import net.minecraft.stats.*
import net.minecraft.util.EntityDamageSource
import net.minecraft.world.World
import net.minecraftforge.event.entity.living.*
import net.minecraftforge.oredict.OreDictionary
import vazkii.botania.api.mana.*
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.lib.LibOreDict
import kotlin.math.*

class ItemSoulSword: ItemSword(AlfheimAPI.SOUL), IManaUsingItem {
	
	class DamageSourceSoulSword(dealer: Entity): EntityDamageSource("player", dealer)
	
	init {
		creativeTab = AlfheimTab
		unlocalizedName = "SoulSword"
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World?, player: EntityPlayer): ItemStack {
		if (player.isSneaking && stack.getItemDamage() > 0 && ASJUtilities.consumeItemStack(player.inventory, ItemStack(ModItems.manaResource, 1, 5)))
			repair(stack, 100)
		
		return super.onItemRightClick(stack, world, player)
	}
	
	override fun onLeftClickEntity(stack: ItemStack, player: EntityPlayer, entity: Entity): Boolean {
		if (!entity.canAttackWithItem()) return true
		if (entity.hitByEntity(player)) return true
		
		var f = getDamageFromLevel(stack) + 1
		var i = 0
		if (entity is EntityLivingBase) i += EnchantmentHelper.getKnockbackModifier(player, entity)
		
		if (player.isSprinting) ++i
		
		val crit = player.fallDistance > 0.0f && !player.onGround && !player.isOnLadder && !player.isInWater && !player.isPotionActive(Potion.blindness) && player.ridingEntity == null && entity is EntityLivingBase
		if (crit) f *= 1.5f
		
		val success = entity.attackEntityFrom(DamageSourceSoulSword(player), f)
		if (!success) return true
		
		if (i > 0) {
			entity.addVelocity((-sin(player.rotationYaw * Math.PI / 180) * i * 0.5), 0.1, (cos(player.rotationYaw * Math.PI / 180) * i * 0.5))
			player.motionX *= 0.6
			player.motionZ *= 0.6
			player.isSprinting = false
		}
		
		if (crit) player.onCriticalHit(entity)
		if (f >= 18f) player.triggerAchievement(AchievementList.overkill)
		
		player.setLastAttacker(entity)
		if (entity is EntityLivingBase) EnchantmentHelper.func_151384_a(entity, player)
		
		EnchantmentHelper.func_151385_b(player, entity)
		val itemstack: ItemStack = player.currentEquippedItem
		
		var target: Any = entity
		if (target is EntityDragonPart) {
			if (target.entityDragonObj is EntityLivingBase)
				target = target.entityDragonObj
		}
		
		if (target is EntityLivingBase) {
			itemstack.hitEntity(target, player)
			if (itemstack.stackSize <= 0) player.destroyCurrentEquippedItem()
		}
		
		if (entity is EntityLivingBase)
			player.addStat(StatList.damageDealtStat, (f * 10f).roundToInt())
		
		player.addExhaustion(0.3f)
		
		return true
	}
	
	override fun getAttributeModifiers(stack: ItemStack): Multimap<String, AttributeModifier>? {
		val multimap = HashMultimap.create<String, AttributeModifier>()
		multimap.put(SharedMonsterAttributes.attackDamage.attributeUnlocalizedName, AttributeModifier(Item.field_111210_e, "Weapon modifier", getDamageFromLevel(stack).D, 0))
		return multimap
	}
	
	override fun getMaxDamage(stack: ItemStack) = getMaxUsesFromLevel(stack)
	
	override fun usesMana(stack: ItemStack) = stack.level < AlfheimConfigHandler.soulSwordMaxLvl
	
	override fun setUnlocalizedName(name: String?): Item? {
		GameRegistry.registerItem(this, name)
		return super.setUnlocalizedName(name)
	}
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack?): String {
		return super.getUnlocalizedNameInefficiently(stack).replace("item.".toRegex(), "item.${ModInfo.MODID}:")
	}
	
	override fun getIsRepairable(sword: ItemStack?, material: ItemStack?) = OreDictionary.getOres(LibOreDict.LIFE_ESSENCE).any { ore ->
		OreDictionary.itemMatches(material, ore, false)
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
	}
	
	companion object {
		
		private const val TAG_SOUL_LEVEL = "soulLevel"
		
		private var ItemStack.level: Int
			get() = ItemNBTHelper.getInt(this, TAG_SOUL_LEVEL, 0)
			set(lvl) = ItemNBTHelper.setInt(this, TAG_SOUL_LEVEL, lvl)
		
		init {
			eventForge()
		}
		
		fun setLevelP(stack: ItemStack, lvl: Int) {
			stack.level = lvl
		}
		
		fun getLevelP(stack: ItemStack) = stack.level
		
		private fun getDamageFromLevel(stack: ItemStack) = stack.level / 100f
		
		private fun getMaxUsesFromLevel(stack: ItemStack) = max(100, stack.level / 10)
		
		private fun repair(stack: ItemStack, amount: Int) {
			stack.meta = max(0, stack.meta - amount)
		}
		
		const val TAG_WONT_DROP_SOUL = "${ModInfo.MODID}:wontDropSoul"
		
		@SubscribeEvent(priority = EventPriority.LOWEST)
		fun onLivingHurt(e: LivingHurtEvent) {
			e.isCanceled = checkAndReturn(e)
		}
		
		fun checkAndReturn(e: LivingHurtEvent): Boolean {
			if (e.source !is DamageSourceSoulSword) {
				if (e.source.entity is EntityLivingBase)
					e.entityLiving.entityData.setBoolean(TAG_WONT_DROP_SOUL, true)
				
				return false
			}
			
			val player = e.source.entity as? EntityPlayer ?: return true
//			if (!EntityFlugel.isTruePlayer(player)) return true
			
			val stack = player.heldItem ?: return true
			if (stack.item !== AlfheimItems.soulSword) return true
			
			e.ammount = getDamageFromLevel(stack).F + 1
			
			return false
		}
		
		@SubscribeEvent(priority = EventPriority.LOWEST)
		fun onLivingDeath(e: LivingDeathEvent) {
			if (e.entityLiving.entityData.getBoolean(TAG_WONT_DROP_SOUL)) return
			if (e.source !is DamageSourceSoulSword) return
			val attacker = e.source.entity as? EntityPlayer ?: return
			if (!EntityFlugel.isTruePlayer(attacker)) return
			val stack = attacker.heldItem ?: return
			if (stack.item !== AlfheimItems.soulSword) return
			if (!ManaItemHandler.requestManaExact(stack, attacker, 1, true)) return
			repair(stack, 1)
			
			if (stack.level + 1 < stack.level) return // overflow check
			stack.level = min(AlfheimConfigHandler.soulSwordMaxLvl, stack.level + 1)
		}
	}
}
