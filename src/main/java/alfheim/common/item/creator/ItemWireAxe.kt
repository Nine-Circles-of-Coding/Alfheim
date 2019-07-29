package alfheim.common.item.creator

import alfheim.AlfheimCore
import alfheim.api.*
import alfheim.common.core.helper.IconHelper
import alfheim.common.core.registry.AlfheimRegistry
import alfheim.common.core.util.AlfheimConfig
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
import net.minecraft.potion.*
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.oredict.OreDictionary
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.mana.*
import vazkii.botania.common.Botania
import vazkii.botania.common.core.helper.Vector3
import vazkii.botania.common.item.equipment.tool.ToolCommons
import java.util.*
import kotlin.math.min

/**
 * seeeeeecrets
 */
class ItemWireAxe(val name: String = "axeRevelation", val toolMaterial: ToolMaterial = ShadowFoxAPI.RUNEAXE, val slayerDamage: Double = 6.0): ItemSword(toolMaterial), IManaUsingItem {
	
	companion object {
		class DamageSourceGodslayer(player: EntityLivingBase, creative: Boolean): EntityDamageSource("player", player) {
			init {
				setDamageBypassesArmor()
				setDamageIsAbsolute()
				if (creative)
					setDamageAllowedInCreativeMode()
			}
		}
		
		val godSlayingDamage = RangedAttribute("${ModInfo.MODID}.godSlayingAttackDamage", 0.0, 0.0, Double.MAX_VALUE)
	}
	
	init {
		setMaxStackSize(1)
		maxDamage = toolMaterial.maxUses
		creativeTab = AlfheimCore.baTab
		unlocalizedName = name
	}
	
	override fun setUnlocalizedName(par1Str: String): Item {
		GameRegistry.registerItem(this, par1Str)
		return super.setUnlocalizedName(par1Str)
	}
	
	override fun getItemStackDisplayName(stack: ItemStack) =
		super.getItemStackDisplayName(stack).replace("&".toRegex(), "\u00a7")
	
	override fun getUnlocalizedNameInefficiently(par1ItemStack: ItemStack) =
		super.getUnlocalizedNameInefficiently(par1ItemStack).replace("item\\.".toRegex(), "item.${ModInfo.MODID}:")
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(par1IconRegister: IIconRegister) {
		itemIcon = IconHelper.forItem(par1IconRegister, this)
	}
	
	override fun getRarity(stack: ItemStack): EnumRarity = BotaniaAPI.rarityRelic
	
	fun getManaPerDamage(): Int = 60
	
	fun ItemStack.damageStack(damage: Int, entity: EntityLivingBase?) {
		ToolCommons.damageItem(this, damage, entity, getManaPerDamage())
	}
	
	override fun onUpdate(stack: ItemStack, world: World, player: Entity, par4: Int, par5: Boolean) {
		if (!world.isRemote && player is EntityPlayer && stack.itemDamage > 0 && ManaItemHandler.requestManaExactForTool(stack, player, getManaPerDamage() * 2, true))
			stack.itemDamage = stack.itemDamage - 1
	}
	
	override fun usesMana(stack: ItemStack) = true
	
	override fun getToolClasses(stack: ItemStack?): MutableSet<String>? = Sets.newHashSet("axe", "sword")
	
	override fun getItemUseAction(stack: ItemStack) = EnumAction.bow
	
	override fun getMaxItemUseDuration(stack: ItemStack?) = 72000
	
	override fun onItemRightClick(par1ItemStack: ItemStack, par2World: World?, par3EntityPlayer: EntityPlayer?): ItemStack {
		par3EntityPlayer!!.setItemInUse(par1ItemStack, getMaxItemUseDuration(par1ItemStack))
		return par1ItemStack
	}
	
	private fun randomVec(length: Int): Vector3 {
		val vec = Vector3(0.0, Math.random() * length, 0.0)
		vec.rotate(Math.random() * Math.PI * 2, Vector3(1.0, 0.0, 0.0))
		vec.rotate(Math.random() * Math.PI * 2, Vector3(0.0, 0.0, 1.0))
		return vec
	}
	
	override fun isFull3D() = true
	
	override fun onPlayerStoppedUsing(stack: ItemStack, world: World, player: EntityPlayer, inUseTicks: Int) {
		if (!ManaItemHandler.requestManaExact(stack, player, 1, false)) return
		val range = min(getMaxItemUseDuration(stack) - inUseTicks, 200) / 20 + 1
		val entities = world.getEntitiesWithinAABBExcludingEntity(player, AxisAlignedBB.getBoundingBox(player.posX + range, player.posY + range, player.posZ + range, player.posX - range, player.posY - range, player.posZ - range))
		if (!player.capabilities.isCreativeMode) stack.damageStack(1, player)
		var count = 0
		for (entity in entities) {
			if (entity is EntityPlayer && ManaItemHandler.requestManaExact(stack, entity, 1, false)) {
				count++
				if (!world.isRemote) entity.addChatMessage(ChatComponentText(StatCollector.translateToLocal("misc.${ModInfo.MODID}.wayOfUndoing").replace('&', '\u00a7')))
				entity.addPotionEffect(PotionEffect(AlfheimRegistry.manaVoid.id, 10, 0, true))
			}
		}
		if (count > 0) {
			world.playSoundAtEntity(player, "botania:enchanterEnchant", 1f, 1f)
			if (!world.isRemote) player.addChatMessage(ChatComponentText(StatCollector.translateToLocal("misc.${ModInfo.MODID}.wayOfUndoing").replace('&', '\u00a7')))
			stack.damageStack(5, player)
			for (var11 in 0..20) {
				Botania.proxy.lightningFX(world, Vector3.fromEntityCenter(player), Vector3.fromEntityCenter(player).add(randomVec(range)), range * 0.01f, 255 shl 16, 0)
			}
			player.addPotionEffect(PotionEffect(AlfheimRegistry.manaVoid.id, 2 * count, 0, true))
		}
	}
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer?, list: MutableList<Any?>, par4: Boolean) {
		super.addInformation(stack, player, list, par4)
		val greyitalics = "${EnumChatFormatting.GRAY}${EnumChatFormatting.ITALIC}"
		val grey = EnumChatFormatting.GRAY
		if (GuiScreen.isShiftKeyDown()) {
			addStringToTooltip("$greyitalics${StatCollector.translateToLocal("misc.${ModInfo.MODID}.wline1")}", list)
			addStringToTooltip("$greyitalics${StatCollector.translateToLocal("misc.${ModInfo.MODID}.wline2")}", list)
			addStringToTooltip("$greyitalics${StatCollector.translateToLocal("misc.${ModInfo.MODID}.wline3")}", list)
			addStringToTooltip("${grey}I awaken the ancients within all of you!", list)
			addStringToTooltip("${grey}From my soul's fire the world burns anew!", list)
		} else addStringToTooltip(StatCollector.translateToLocal("botaniamisc.shiftinfo"), list)
	}
	
	fun addStringToTooltip(s: String, tooltip: MutableList<Any?>) {
		tooltip.add(s.replace("&", "\u00a7"))
	}
	
	val godUUID = UUID.fromString("CB3D55D3-645C-4F38-A497-9C13A33DB5CF")!!
	
	override fun getItemAttributeModifiers(): Multimap<Any, Any> {
		val multimap = HashMultimap.create<Any, Any>()
		multimap.put(SharedMonsterAttributes.attackDamage.attributeUnlocalizedName, AttributeModifier(Item.field_111210_e, "Weapon modifier", toolMaterial.damageVsEntity.toDouble(), 0))
		multimap.put(godSlayingDamage.attributeUnlocalizedName, AttributeModifier(godUUID, "Weapon modifier", slayerDamage, 0))
		return multimap
	}
	
	override fun onLeftClickEntity(stack: ItemStack, player: EntityPlayer, entity: Entity): Boolean {
		val godslaying = stack.attributeModifiers[godSlayingDamage.attributeUnlocalizedName]
		if (godslaying != null) {
			for (attr in godslaying) {
				if (attr is AttributeModifier)
					attackEntity(player, entity, attr.amount, DamageSourceGodslayer(player, AlfheimConfig.grantWireUnlimitedPower))
			}
		}
		
		return super.onLeftClickEntity(stack, player, entity)
	}
	
	fun attackEntity(player: EntityLivingBase, entity: Entity, f0: Double, damageSource: DamageSource) {
		var f = f0
		var f1 = 0.0f
		if (entity is EntityLivingBase)
			f1 = EnchantmentHelper.getEnchantmentModifierLiving(player, entity)
		
		if (f > 0.0 || f1 > 0.0f) {
			val flag = player.fallDistance > 0.0f &&
					   !player.onGround &&
					   !player.isOnLadder &&
					   !player.isInWater &&
					   !player.isPotionActive(Potion.blindness) &&
					   player.ridingEntity == null &&
					   entity is EntityLivingBase
			if (flag && f > 0.0)
				f *= 1.5
			f += f1.toDouble()
			val flag2 = entity.attackEntityFrom(damageSource, f.toFloat())
			if (flag2) {
				if (flag && player is EntityPlayer)
					player.onCriticalHit(entity)
				if (f1 > 0.0f && player is EntityPlayer)
					player.onEnchantmentCritical(entity)
				player.setLastAttacker(entity)
				if (entity is EntityLivingBase)
					EnchantmentHelper.func_151384_a(entity, player)
				EnchantmentHelper.func_151385_b(player, entity)
			}
		}
	}
	
	override fun onBlockDestroyed(stack: ItemStack, world: World?, block: Block, x: Int, y: Int, z: Int, player: EntityLivingBase?): Boolean {
		if (block.getBlockHardness(world, x, y, z).toDouble() != 0.0) {
			stack.damageStack(1, player)
		}
		
		return true
	}
	
	override fun getItemEnchantability(): Int = toolMaterial.enchantability
	
	fun getEfficiencyOnProperMaterial(): Float = toolMaterial.efficiencyOnProperMaterial
	
	override fun getIsRepairable(stack: ItemStack?, materialstack: ItemStack?): Boolean {
		val mat = toolMaterial.repairItemStack
		if (mat != null && OreDictionary.itemMatches(mat, materialstack, false)) return true
		return super.getIsRepairable(stack, materialstack)
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
