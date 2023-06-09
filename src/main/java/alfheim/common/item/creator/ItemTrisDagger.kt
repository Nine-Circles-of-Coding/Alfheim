package alfheim.common.item.creator

import alexsocol.asjlib.*
import alfheim.api.*
import alfheim.client.core.helper.*
import alfheim.common.core.helper.*
import alfheim.common.core.util.AlfheimTab
import com.google.common.collect.*
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.*
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.potion.*
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.oredict.OreDictionary
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.mana.*
import vazkii.botania.common.Botania
import vazkii.botania.common.core.helper.Vector3
import vazkii.botania.common.item.equipment.tool.ToolCommons

/**
 * @author WireSegal
 * Created at 10:40 AM on 2/8/16.
 */
class ItemTrisDagger(val name: String = "reactionDagger", val toolMaterial: ToolMaterial = AlfheimAPI.RUNEAXE): ItemSword(toolMaterial), IManaUsingItem {
	
	var dunIcon: IIcon? = null
	
	companion object {
		
		const val minBlockLength = 0
		const val maxBlockLength = 10
	}
	
	init {
		creativeTab = AlfheimTab
		maxDamage = toolMaterial.maxUses
		maxStackSize = 1
		unlocalizedName = name
		
		DaggerEventHandler
		
		if (ASJUtilities.isClient)
			eventForge()
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
		dunIcon = IconHelper.forItem(reg, this, "Dun")
	}
	
	@SubscribeEvent
	fun iconRegistration(e: TextureStitchEvent.Pre) {
		if (e.map.textureType == 1) {
			itemIcon = InterpolatedIconHelper.forItem(e.map, this)
		}
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
	
	override fun onBlockDestroyed(stack: ItemStack, world: World?, block: Block, x: Int, y: Int, z: Int, player: EntityLivingBase?): Boolean {
		if (block.getBlockHardness(world, x, y, z) != 0f)
			stack.damageStack(2, player)
		
		return true
	}
	
	override fun usesMana(stack: ItemStack): Boolean = true
	
	override fun getMaxItemUseDuration(stack: ItemStack?) = 72000
	
	override fun onItemRightClick(par1ItemStack: ItemStack, par2World: World?, par3EntityPlayer: EntityPlayer?): ItemStack {
		par3EntityPlayer!!.setItemInUse(par1ItemStack, getMaxItemUseDuration(par1ItemStack))
		return par1ItemStack
	}
	
	override fun getIcon(stack: ItemStack, renderPass: Int, player: EntityPlayer, usingItem: ItemStack?, useRemaining: Int): IIcon? {
		if (usingItem != null && usingItem.item == this && getMaxItemUseDuration(stack) - useRemaining > maxBlockLength) {
			return dunIcon
		}
		return itemIcon
	}
	
	override fun getAttributeModifiers(stack: ItemStack): Multimap<Any, Any> {
		val multimap = HashMultimap.create<Any, Any>()
		multimap.put(SharedMonsterAttributes.attackDamage.attributeUnlocalizedName, AttributeModifier(Item.field_111210_e, "Weapon modifier", toolMaterial.damageVsEntity.D, 0))
		return multimap
	}
	
	override fun isFull3D() = true
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer?, list: MutableList<Any?>, par4: Boolean) {
		super.addInformation(stack, player, list, par4)
		val greyitalics = "${EnumChatFormatting.GRAY}${EnumChatFormatting.ITALIC}"
		val grey = EnumChatFormatting.GRAY
		if (GuiScreen.isShiftKeyDown()) {
			addStringToTooltip(list, "$greyitalics${StatCollector.translateToLocal("misc.${ModInfo.MODID}.tline1")}")
			addStringToTooltip(list, "$greyitalics${StatCollector.translateToLocal("misc.${ModInfo.MODID}.tline2")}")
			addStringToTooltip(list, "$greyitalics${StatCollector.translateToLocal("misc.${ModInfo.MODID}.tline3")}")
			addStringToTooltip(list, "")
			addStringToTooltip(list, "$grey\"My inward eye sees the depths of my soul!")
			addStringToTooltip(list, "${grey}I accept both sides, and reject my downfall!\"")
		} else addStringToTooltip(list, StatCollector.translateToLocal("botaniamisc.shiftinfo"))
	}
	
	override fun getItemEnchantability() = toolMaterial.enchantability
	
	override fun getIsRepairable(stack: ItemStack?, material: ItemStack?): Boolean {
		val mat = toolMaterial.repairItemStack
		return mat != null && OreDictionary.itemMatches(mat, material, false)
	}
}

/**
 * @author WireSegal
 * Created at 10:53 AM on 2/8/16.
 */
object DaggerEventHandler {
	
	init {
		eventForge()
	}
	
	fun dealOculusDamage(entity: EntityLivingBase) = EntityDamageSource("${ModInfo.MODID}.oculus", entity).setDamageBypassesArmor().setMagicDamage().setTo(ElementalDamage.LIGHTNESS)
	
	fun getHeadOrientation(entity: EntityLivingBase): Vector3 {
		val f1 = MathHelper.cos(-entity.rotationYaw * 0.017453292F - Math.PI.F)
		val f2 = MathHelper.sin(-entity.rotationYaw * 0.017453292F - Math.PI.F)
		val f3 = -MathHelper.cos(-(entity.rotationPitch - 90) * 0.017453292F)
		val f4 = MathHelper.sin(-(entity.rotationPitch - 90) * 0.017453292F)
		return Vector3((f2 * f3).D, f4.D, (f1 * f3).D)
	}
	
	@SubscribeEvent
	fun onLivingAttacked(e: LivingAttackEvent) {
		val player = e.entityLiving
		val damage = e.source
		if (player !is EntityPlayer || damage !is EntityDamageSource || !player.isUsingItem || damage.damageType == "${ModInfo.MODID}.oculus") return
		
		val enemyEntity = damage.entity
		if (enemyEntity !is EntityLivingBase || enemyEntity == player) return
		
		val itemInUse = player.itemInUse
		val itemInUseCount = player.itemInUse.maxItemUseDuration - player.itemInUseCount
		if (itemInUse.item !is ItemTrisDagger || itemInUseCount > ItemTrisDagger.maxBlockLength || itemInUseCount < ItemTrisDagger.minBlockLength) return
		
		val lookVec = Vector3(player.lookVec)
		val targetVec = Vector3.fromEntityCenter(enemyEntity).sub(Vector3.fromEntityCenter(player))
		val epsilon = lookVec.dotProduct(targetVec) / (lookVec.mag() * targetVec.mag())
		if (epsilon <= 0.75) return
		
		e.isCanceled = true
		player.stopUsingItem()
		
		if (!player.worldObj.isRemote) {
			if (damage is EntityDamageSourceIndirect) return
			enemyEntity.attackEntityFrom(dealOculusDamage(player), e.ammount * 2f) // dammit cpw, you misspelled amount
			enemyEntity.playSoundAtEntity("random.anvil_land", 1f, 0.9f + 0.1f * Math.random().F)
			if (enemyEntity is EntityPlayer && enemyEntity.currentEquippedItem != null)
				enemyEntity.currentEquippedItem.damageItem(30, enemyEntity)
			enemyEntity.knockback(player, 1f)
			enemyEntity.addPotionEffect(PotionEffect(Potion.weakness.id, 60, 1))
			enemyEntity.addPotionEffect(PotionEffect(Potion.moveSlowdown.id, 60, 2))
		} else {
			val mainVec = Vector3.fromEntityCenter(player).add(lookVec)
			val baseVec = getHeadOrientation(player).crossProduct(lookVec).normalize()
			for (i in 0..360 step 15) {
				val rotVec = baseVec.copy().rotate(i * 180 / Math.PI, lookVec)
				val endVec = mainVec.copy().add(rotVec)
				Botania.proxy.lightningFX(player.worldObj, mainVec, endVec, 3f, 0xFF94A1, 0xFBAAB5)
			}
		}
	}
}