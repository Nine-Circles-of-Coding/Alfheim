package alfheim.common.item.relic

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.*
import alfheim.api.event.PlayerInteractAdequateEvent
import alfheim.client.core.helper.IconHelper
import alfheim.common.core.handler.*
import alfheim.common.core.helper.*
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.*
import net.minecraft.entity.boss.EntityDragonPart
import net.minecraft.entity.monster.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.*
import net.minecraft.potion.*
import net.minecraft.stats.*
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.event.entity.player.PlayerEvent
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.item.IRelic
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.relic.ItemRelic
import kotlin.math.*

class ItemDaolos: ItemAxe(AlfheimAPI.RUNEAXE), IRelic {
	
	init {
		creativeTab = AlfheimTab
		maxStackSize = 1
		maxDamage = 0
		unlocalizedName = "Daolos"
	}
	
	override fun onLeftClickEntity(stack: ItemStack, player: EntityPlayer, entity: Entity): Boolean {
		leftClickEntity(stack, player, entity)
		return true
	}
	
	fun leftClickEntity(stack: ItemStack, player: EntityPlayer, entity: Entity) {
		if (!entity.canAttackWithItem()) return
		if (entity.hitByEntity(player)) return
		
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
		
		val src = DamageSource.causePlayerDamage(player).setTo(ElementalDamage.WATER)
		
		val succ = entity.attackEntityFrom(src, damage)
		if (!succ) return
		
		val fire = EnchantmentHelper.getFireAspectModifier(player) * 4
		entity.setFire(fire)
		
		if (player.isSprinting)
			++knockback
		
		if (knockback > 0) {
			entity.addVelocity((-sin(player.rotationYaw * Math.PI / 180) * knockback * 0.5), 0.1, (cos(player.rotationYaw * Math.PI / 180) * knockback * 0.5))
			player.motionX *= 0.6
			player.motionZ *= 0.6
			player.isSprinting = false
		}
		
		if (crit) player.onCriticalHit(entity)
		if (addDamage > 0f) player.onEnchantmentCritical(entity)
		if (damage >= 18f) player.triggerAchievement(AchievementList.overkill)
		
		player.setLastAttacker(entity)
		
		if (entity is EntityLivingBase) EnchantmentHelper.func_151384_a(entity, player)
		
		EnchantmentHelper.func_151385_b(player, entity)
		
		player.addExhaustion(0.3f)
		
		var target = entity
		if (target is EntityDragonPart && target.entityDragonObj is Entity)
			target = target.entityDragonObj as Entity
		
		if (target !is EntityLivingBase) return
		
		stack.hitEntity(target, player)
		if (stack.stackSize <= 0)
			player.destroyCurrentEquippedItem()
		
		player.addStat(StatList.damageDealtStat, (damage * 10f).roundToInt())
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (player.isSneaking)
			extinguish(stack, world, player)
		else
			player.setItemInUse(stack, getMaxItemUseDuration(stack))
		
		return stack
	}
	
	fun extinguish(stack: ItemStack, world: World, player: EntityPlayer) {
		var atATime = 8
		
		val (x, y, z) = Vector3.fromEntity(player).mf()
		
		for (i in x.bidiRange(16))
			for (j in y.bidiRange(16))
				for (k in z.bidiRange(16)) {
					if (atATime <= 0) return
					
					if (world.getBlock(i, j, k) !== Blocks.fire) continue
					if (InteractionSecurity.isBreakingBanned(player, i, j ,k, world, Blocks.fire)) continue
					
					if (ManaItemHandler.requestManaExact(stack, player, if (world.isRaining) 2 else 8, true)) {
						world.setBlockToAir(i, j, k)
						--atATime
					} else
						return
				}
	}
	
	override fun getMaxItemUseDuration(stack: ItemStack?) = 72000
	
	override fun getItemUseAction(stack: ItemStack?) = EnumAction.bow
	
	override fun onUsingTick(stack: ItemStack, player: EntityPlayer, count: Int) {
		val world = player.worldObj
		push(stack, world, player)
	}
	
	fun push(stack: ItemStack, world: World, player: EntityPlayer) {
		if (world.getBlock(player, y = -1) !is BlockLiquid || player.isInsideOfMaterial(Material.water) || player.isInsideOfMaterial(Material.lava)) return
		if (!ManaItemHandler.requestManaExact(stack, player, if (world.isRaining) 1 else 4, true)) return
		
		val (x, _, z) = Vector3(player.lookVec).mul(1, 0, 1).normalize().mul(0.25)
		player.motionX += x
		player.motionZ += z
	}
	
	// ItemRelic
	
	var achievement: Achievement? = null
	
	override fun onUpdate(stack: ItemStack?, world: World?, entity: Entity?, slot: Int, inHand: Boolean) {
		if (entity is EntityPlayer) updateRelic(stack, entity)
	}
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer, list: MutableList<Any?>, adv: Boolean) {
		if (GuiScreen.isShiftKeyDown()) {
			val bind = getSoulbindUsernameS(stack)
			if (bind.isEmpty()) addStringToTooltip(list, "botaniamisc.relicUnbound") else {
				addStringToTooltip(list, "botaniamisc.relicSoulbound", bind)
				if (!isRightPlayer(player, stack))
					addStringToTooltip(list, "botaniamisc.notYourSagittarius", bind)
			}
			
			val name = stack.unlocalizedName + ".poem"
			if (StatCollector.canTranslate("${name}0")) {
				addStringToTooltip(list, "")
				
				for (i in 0..3)
					addStringToTooltip(list, EnumChatFormatting.ITALIC.toString() + StatCollector.translateToLocal(name + i))
			}
		} else addStringToTooltip(list, "botaniamisc.shiftinfo")
	}
	
	fun getSoulbindUsernameS(stack: ItemStack?): String {
		return ItemNBTHelper.getString(stack, TAG_SOULBIND, "")
	}
	
	fun updateRelic(stack: ItemStack?, player: EntityPlayer) {
		if (stack == null || stack.item !is IRelic) return
		
		if (getSoulbindUsernameS(stack).isEmpty()) {
			player.addStat((stack.item as IRelic).bindAchievement, 1)
			bindToPlayer(player, stack)
		}
		
		if (!isRightPlayer(player, stack) && player.ticksExisted % 10 == 0)
			player.attackEntityFrom(ItemRelic.damageSource(), 2f)
	}
	
	fun bindToPlayer(player: EntityPlayer, stack: ItemStack?) {
		bindToUsernameS(player.commandSenderName, stack)
	}
	
	fun bindToUsernameS(username: String?, stack: ItemStack?) {
		ItemNBTHelper.setString(stack, TAG_SOULBIND, username)
	}
	
	fun isRightPlayer(player: EntityPlayer, stack: ItemStack?): Boolean {
		return isRightPlayer(player.commandSenderName, stack)
	}
	
	fun isRightPlayer(player: String, stack: ItemStack?): Boolean {
		return getSoulbindUsernameS(stack) == player
	}
	
	override fun bindToUsername(playerName: String?, stack: ItemStack?) {
		bindToUsernameS(playerName, stack)
	}
	
	override fun getSoulbindUsername(stack: ItemStack?): String {
		return getSoulbindUsernameS(stack)
	}
	
	override fun getBindAchievement(): Achievement? {
		return achievement
	}
	
	override fun setBindAchievement(achievement: Achievement?) {
		this.achievement = achievement
	}
	
	override fun getRarity(stack: ItemStack?): EnumRarity? {
		return BotaniaAPI.rarityRelic
	}
	
	// ItemMod
	
	override fun setUnlocalizedName(name: String?): Item? {
		GameRegistry.registerItem(this, name)
		return super.setUnlocalizedName(name)
	}
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack?): String {
		return super.getUnlocalizedNameInefficiently(stack).replace("item.", "item.${ModInfo.MODID}:")
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
	}
	
	companion object {
		
		const val TAG_SOULBIND = "soulbind"
		
		init {
			eventForge()
		}
		
		@SubscribeEvent
		fun onWaterLeftClick(e: PlayerInteractAdequateEvent.LeftClick) {
			if (e.action != PlayerInteractAdequateEvent.LeftClick.Action.LEFT_CLICK_LIQUID) return
			val stack = e.player.heldItem ?: return
			if (stack.item !== AlfheimItems.daolos) return
			
			val (x, y, z) = Vector3(e.x, e.y, e.z).I
			val world = e.player.worldObj
			val block = world.getBlock(x, y, z)
			
			if (block != Blocks.water && block != Blocks.flowing_water) return
			
			if (ManaItemHandler.requestManaExact(stack, e.player, if (world.isRaining) 25 else 100, true)) e.player.heal(1f)
			
			val list = getEntitiesWithinAABB(world, EntityLivingBase::class.java, getBoundingBox(x, y, z).expand(16))
			list.remove(e.player)
			
			list.forEach {
				if (world.getBlock(it) !== block) return@forEach
				if (!InteractionSecurity.canHurtEntity(e.player, it)) return@forEach
				if (AlfheimConfigHandler.enableMMO && e.player.worldObj.isRemote && CardinalSystem.PartySystem.sameParty(e.player, it)) return@forEach
				
				if (ManaItemHandler.requestManaExact(stack, e.player, if (world.isRaining) 25 else 100, true))
					it.attackEntityFrom(DamageSource.causePlayerDamage(e.player).setTo(ElementalDamage.WATER), 3f)
				else
					return
			}
		}
		
		@SubscribeEvent
		fun getBreakSpeed(e: PlayerEvent.BreakSpeed) {
			if (e.originalSpeed > 1 && e.entityPlayer.heldItem?.item === AlfheimItems.daolos && e.entityPlayer.worldObj.isRaining)
				e.newSpeed = Float.MAX_VALUE
		}
	}
}
