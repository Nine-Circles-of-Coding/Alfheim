package alfheim.common.item.creator

import alexsocol.asjlib.*
import alfheim.api.*
import alfheim.client.core.helper.IconHelper
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.*
import alfheim.common.core.helper.*
import alfheim.common.core.util.*
import com.google.common.collect.*
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.*
import net.minecraft.entity.ai.attributes.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.*
import net.minecraft.potion.Potion
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.oredict.OreDictionary
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.mana.*
import vazkii.botania.common.item.equipment.tool.ToolCommons
import java.util.*
import kotlin.math.min

/**
 * seeeeeecrets
 */
class ItemWireAxe(val name: String = "axeRevelation", val toolMaterial: ToolMaterial = AlfheimAPI.RUNEAXE, val slayerDamage: Double = 6.0): ItemSword(toolMaterial), IManaUsingItem {
	
	companion object {
		val godSlayingDamage = RangedAttribute("${ModInfo.MODID}.godSlayingAttackDamage", 0.0, 0.0, Double.MAX_VALUE)
	}
	
	init {
		creativeTab = AlfheimTab
		maxDamage = toolMaterial.maxUses
		maxStackSize = 1
		unlocalizedName = name
	}
	
	override fun setUnlocalizedName(name: String): Item {
		GameRegistry.registerItem(this, name)
		return super.setUnlocalizedName(name)
	}
	
	override fun getItemStackDisplayName(stack: ItemStack) =
		super.getItemStackDisplayName(stack).replace("&".toRegex(), "\u00a7")
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack) =
		super.getUnlocalizedNameInefficiently(stack).replace("item\\.".toRegex(), "item.${ModInfo.MODID}:")
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
	}
	
	override fun getRarity(stack: ItemStack): EnumRarity = BotaniaAPI.rarityRelic
	
	fun getManaPerDamage(): Int = 60
	
	fun ItemStack.damageStack(damage: Int, entity: EntityLivingBase?) {
		ToolCommons.damageItem(this, damage, entity, getManaPerDamage())
	}
	
	override fun onUpdate(stack: ItemStack, world: World, player: Entity, par4: Int, par5: Boolean) {
		if (!world.isRemote && player is EntityPlayer && stack.meta > 0 && ManaItemHandler.requestManaExactForTool(stack, player, getManaPerDamage() * 2, true))
			stack.meta = stack.meta - 1
	}
	
	override fun usesMana(stack: ItemStack) = true
	
	override fun getToolClasses(stack: ItemStack?): MutableSet<String>? = Sets.newHashSet("axe", "sword")
	
	override fun getItemUseAction(stack: ItemStack) = EnumAction.bow
	
	override fun getMaxItemUseDuration(stack: ItemStack?) = 72000
	
	override fun onItemRightClick(par1ItemStack: ItemStack, par2World: World?, par3EntityPlayer: EntityPlayer?): ItemStack {
		par3EntityPlayer!!.setItemInUse(par1ItemStack, getMaxItemUseDuration(par1ItemStack))
		return par1ItemStack
	}
	
	override fun isFull3D() = true
	
	override fun onPlayerStoppedUsing(stack: ItemStack, world: World, player: EntityPlayer, inUseTicks: Int) {
		if (!ManaItemHandler.requestManaExact(stack, player, 100, false)) return
		
		val range = min(getMaxItemUseDuration(stack) - inUseTicks, 200) / 20 + 1
		val entities = getEntitiesWithinAABB(world, EntityPlayer::class.java, player.boundingBox(range))
		entities.remove(player)
		if (!player.capabilities.isCreativeMode) stack.damageStack(1, player)
		
		var count = 0
		entities.forEach {
			if (!ManaItemHandler.requestManaExact(stack, it, 1, false)) return@forEach
			if (!it.attackEntityFrom(DamageSource.causePlayerDamage(player).setTo(ElementalDamage.LIGHTNESS), 0.001f)) return@forEach
			count++
			if (!world.isRemote) ASJUtilities.say(it, "misc.${ModInfo.MODID}.wayOfUndoing")
//			it.addPotionEffect(PotionEffect(AlfheimConfigHandler.potionIDManaVoid, 10, 0, true))
			ManaItemHandler.dispatchMana(stack, player, ManaItemHandler.requestMana(stack, it, 1000, true), true)
		}
		
		if (count > 0) {
			player.playSoundAtEntity("botania:enchanterEnchant", 1f, 1f)
			if (!world.isRemote) player.addChatMessage(ChatComponentText(StatCollector.translateToLocal("misc.${ModInfo.MODID}.wayOfUndoing").replace('&', '\u00a7')))
			stack.damageStack(5, player)
			if (!world.isRemote) VisualEffectHandler.sendPacket(VisualEffects.WIRE, player.dimension, player.posX, player.posY - player.yOffset + player.height / 2.0, player.posZ, range.D, 0.0, 0.0)
//			player.addPotionEffect(PotionEffect(AlfheimConfigHandler.potionIDManaVoid, 2 * count, 0, true))
			ManaItemHandler.requestManaExact(stack, player, 100, true)
		}
	}
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer?, list: MutableList<Any?>, par4: Boolean) {
		super.addInformation(stack, player, list, par4)
		val greyitalics = "${EnumChatFormatting.GRAY}${EnumChatFormatting.ITALIC}"
		val grey = EnumChatFormatting.GRAY
		if (GuiScreen.isShiftKeyDown()) {
			addStringToTooltip(list, "$greyitalics${StatCollector.translateToLocal("misc.${ModInfo.MODID}.wline1")}")
			addStringToTooltip(list, "$greyitalics${StatCollector.translateToLocal("misc.${ModInfo.MODID}.wline2")}")
			addStringToTooltip(list, "$greyitalics${StatCollector.translateToLocal("misc.${ModInfo.MODID}.wline3")}")
			addStringToTooltip(list, "")
			addStringToTooltip(list, "$grey\"I awaken the Ancients within all of you!")
			addStringToTooltip(list, "${grey}From my soul's fire the world burns anew!\"")
		} else addStringToTooltip(list, StatCollector.translateToLocal("botaniamisc.shiftinfo"))
	}
	
	fun addStringToTooltip(s: String, tooltip: MutableList<Any?>) {
		tooltip.add(s.replace("&", "\u00a7"))
	}
	
	val godUUID = UUID.fromString("CB3D55D3-645C-4F38-A497-9C13A33DB5CF")!!
	
	override fun getAttributeModifiers(stack: ItemStack): Multimap<Any, Any> {
		val multimap = HashMultimap.create<Any, Any>()
		multimap.put(SharedMonsterAttributes.attackDamage.attributeUnlocalizedName, AttributeModifier(Item.field_111210_e, "Weapon modifier", toolMaterial.damageVsEntity.D, 0))
		multimap.put(godSlayingDamage.attributeUnlocalizedName, AttributeModifier(godUUID, "Weapon modifier", slayerDamage, 0))
		return multimap
	}
	
	override fun onLeftClickEntity(stack: ItemStack, player: EntityPlayer, entity: Entity): Boolean {
		val damage = ModifiableAttributeInstance(ServersideAttributeMap(), godSlayingDamage).apply { stack.attributeModifiers[godSlayingDamage.attributeUnlocalizedName].forEach { applyModifier(it as AttributeModifier) } }.attributeValue
		if (damage <= 0 || !entity.canAttackWithItem() || entity.hitByEntity(player)) return false
		
		attackEntity(player, entity, damage, DamageSourceSpell.godslayer(player, AlfheimConfigHandler.wireoverpowered))
		entity.hurtResistantTime = 0
		
		return false
	}
	
	fun attackEntity(attacker: EntityLivingBase, target: Entity, amount: Double, damageSource: DamageSource) {
		var damage = amount
		
		val crit = attacker.fallDistance > 0f &&
				   !attacker.onGround &&
				   !attacker.isOnLadder &&
				   !attacker.isInWater &&
				   !attacker.isPotionActive(Potion.blindness) &&
				   attacker.ridingEntity == null &&
				   target is EntityLivingBase
		
		if (crit && damage > 0.0)
			damage *= 1.5
		
		val success = target.attackEntityFrom(damageSource, damage.F)
		
		if (!success) return
		
		if (crit && attacker is EntityPlayer)
			attacker.onCriticalHit(target)
		
		attacker.setLastAttacker(target)
		if (target is EntityLivingBase)
			EnchantmentHelper.func_151384_a(target, attacker)
		
		EnchantmentHelper.func_151385_b(attacker, target)
	}
	
	override fun onBlockDestroyed(stack: ItemStack, world: World?, block: Block, x: Int, y: Int, z: Int, player: EntityLivingBase?): Boolean {
		if (block.getBlockHardness(world, x, y, z).D != 0.0)
			stack.damageStack(1, player)
		
		return true
	}
	
	override fun getItemEnchantability(): Int = toolMaterial.enchantability
	
	fun getEfficiencyOnProperMaterial(): Float = toolMaterial.efficiencyOnProperMaterial
	
	override fun getIsRepairable(stack: ItemStack?, material: ItemStack?): Boolean {
		val mat = toolMaterial.repairItemStack
		return mat != null && OreDictionary.itemMatches(mat, material, false)
	}
	
	override fun getHarvestLevel(stack: ItemStack?, toolClass: String?): Int {
		val level = super.getHarvestLevel(stack, toolClass)
		return if (level == -1 && toolClass != null && (toolClass == "axe" || toolClass == "sword"))
			toolMaterial.harvestLevel
		else
			level
	}
	
	override fun getDigSpeed(stack: ItemStack, block: Block, meta: Int): Float =
		if (ForgeHooks.isToolEffective(stack, block, meta) ||
			block.material == Material.wood ||
			block.material == Material.plants ||
			block.material == Material.vine ||
			block.material == Material.coral ||
			block.material == Material.leaves ||
			block.material == Material.gourd)
			getEfficiencyOnProperMaterial()
		else if (block == Blocks.web)
			15f
		else
			1f
	
}
