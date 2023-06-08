package alfheim.common.item.equipment.armor

import alexsocol.asjlib.*
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.ModInfo
import alfheim.client.core.helper.IconHelper
import alfheim.client.model.armor.ModelSnowArmor
import alfheim.common.core.handler.SheerColdHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.*
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.relauncher.*
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.common.util.EnumHelper
import net.minecraftforge.event.entity.living.*
import vazkii.botania.api.mana.*
import vazkii.botania.common.Botania
import vazkii.botania.common.core.handler.ConfigHandler
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.equipment.armor.manasteel.ItemManasteelArmor
import kotlin.math.max

open class ItemSnowArmor(type: Int, name: String): ItemManasteelArmor(type, name, snow), IManaDiscountArmor {
	
	companion object {
		
		private const val MANA_PER_DAMAGE = 70
		
		val snow = EnumHelper.addArmorMaterial("snow", 25, intArrayOf(2, 6, 5, 2), 16)!!
		
		var model1: ModelBiped? = null
		var model2: ModelBiped? = null
		var model3: ModelBiped? = null
		
		var model: ModelBiped? = null
		
		var replacePairs = arrayOf(Blocks.water to Blocks.ice, Blocks.flowing_water to Blocks.ice, Blocks.lava to Blocks.obsidian, Blocks.flowing_lava to Blocks.cobblestone)
		
		init {
			SnowArmorAbilityHandler
		}
	}
	
	init {
		creativeTab = AlfheimTab
	}
	
	fun repair(stack: ItemStack, world: World, player: EntityPlayer) {
		if (stack.meta > 0 && ManaItemHandler.requestManaExact(stack, player, MANA_PER_DAMAGE * 2, world.isRemote))
			stack.meta = stack.meta - 1
	}
	
	override fun onUpdate(stack: ItemStack, world: World, player: Entity, slot: Int, inHand: Boolean) {
		repair(stack, world, player as? EntityPlayer ?: return)
	}
	
	override fun onArmorTick(world: World, player: EntityPlayer, stack: ItemStack) {
		if (player.ticksExisted % 20 == 0 &&
			!world.provider.hasNoSky && world.isDaytime &&
			!world.isRaining &&
			world.getCelestialAngle(0f).times(360f).let { it in 350f..360f || it in 0f..10f } &&
			world.canBlockSeeTheSky(player.posX.mfloor(), player.posY.mfloor(), player.posZ.mfloor()) &&
			!ManaItemHandler.requestManaExact(stack, player, MANA_PER_DAMAGE, world.isRemote)) {
			
			stack.damageItem(1, player)
			
			if (stack.stackSize <= 0) {
				player.setCurrentItemOrArmor(4 - armorType, null)
				return
			}
		}
		
		repair(stack, world, player)
		
		if (stack.item === AlfheimItems.snowBoots && hasArmorSet(player) && player.isSneaking) {
			fun checkSet(world: World, player: EntityPlayer, x: Int, y: Int, z: Int) {
				val block = world.getBlock(x, y, z)
				
				for (pair in replacePairs)
					if (pair.first === block && InteractionSecurity.isPlacementBanned(player, x, y, z, world, pair.second))
						world.setBlock(x, y, z, pair.second)
			}
			
			val x = player.posX.mfloor()
			val y = player.boundingBox.minY.mfloor() - 1
			val z = player.posZ.mfloor()
			
			for (i in -1..1)
				for (j in -1..1)
					checkSet(world, player, x + i, y, z + j)
		}
	}
	
	override fun getArmorTextureAfterInk(stack: ItemStack?, slot: Int) =
		"${ModInfo.MODID}:textures/model/armor/snow${if (slot == 3) "0" else if (ConfigHandler.enableArmorModels) "New" else if (slot == 2) "1" else "0"}.png"
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
	}
	
	override fun getIsRepairable(stack: ItemStack, material: ItemStack) =
		stack.item === AlfheimItems.elvenResource && stack.meta == ElvenResourcesMetas.NiflheimPowerIngot.I
	
	var armorSet: Array<ItemStack>? = null
	
	override fun getArmorSetStacks(): Array<ItemStack> {
		if (armorSet == null)
			armorSet = arrayOf(ItemStack(AlfheimItems.snowHelmet), ItemStack(AlfheimItems.snowChest), ItemStack(AlfheimItems.snowLeggings), ItemStack(AlfheimItems.snowBoots))
		
		return armorSet!!
	}
	
	override fun hasArmorSetItem(player: EntityPlayer, i: Int): Boolean {
		val stack = player.inventory.armorInventory[3 - i] ?: return false
		when (i) {
			0 -> return stack.item === AlfheimItems.snowHelmet || (Botania.thaumcraftLoaded && stack.item === AlfheimItems.snowHelmetRevealing)
			1 -> return stack.item === AlfheimItems.snowChest
			2 -> return stack.item === AlfheimItems.snowLeggings
			3 -> return stack.item === AlfheimItems.snowBoots
		}
		return false
	}
	
	override fun addInformationAfterShift(stack: ItemStack?, player: EntityPlayer, list: MutableList<Any?>, adv: Boolean) {
		addStringToTooltip(getArmorSetTitle(player), list as MutableList<String>)
		addArmorSetDescription(stack, list as MutableList<String>)
		
		var count = 0
		for (i in armorSetStacks.indices)
			addStringToTooltip("${if (hasArmorSetItem(player, i)) { count++; EnumChatFormatting.GREEN } else ""} - ${armorSetStacks[i].displayName}", list)
		
		if (count == 4 && PlayerHandler.getPlayerBaubles(player)?.get(3)?.item === ModItems.superTravelBelt)
			list.add("${EnumChatFormatting.RED} - ${EnumChatFormatting.ITALIC}D\u00E9j\u00E0 Vu")
		
		if (hasPhantomInk(stack))
			addStringToTooltip(StatCollector.translateToLocal("botaniamisc.hasPhantomInk"), list)
	}
	
	override fun getArmorSetName() = StatCollector.translateToLocal("alfheim.armorset.snow.name")!!
	
	override fun getArmorSetTitle(player: EntityPlayer?) =
		"${StatCollector.translateToLocal("botaniamisc.armorset")} $armorSetName (${getSetPiecesEquipped(player)}/${armorSetStacks.size})"
	
	override fun addArmorSetDescription(stack: ItemStack?, list: MutableList<String>) {
		addStringToTooltip(StatCollector.translateToLocal("alfheim.armorset.snow.desc"), list)
	}
	
	@SideOnly(Side.CLIENT)
	override fun getArmorModel(living: EntityLivingBase, stack: ItemStack, slot: Int): ModelBiped? {
		if (!ConfigHandler.enableArmorModels) return super.getArmorModel(living, stack, slot)
		
		if (model1 == null) model1 = ModelSnowArmor(1f)
		if (model2 == null) model2 = ModelSnowArmor(0.5f)
		if (model3 == null) model3 = ModelBiped()
		
		model = when (slot) {
			0    -> model2
			1    -> model1
			2    -> model2
			3    -> model3
			else -> model
		}
		
		val model = model!!
		
		model.bipedHead.showModel = slot == 0
		model.bipedHeadwear.showModel = slot == 0
		model.bipedBody.showModel = slot == 1 || slot == 2
		model.bipedRightArm.showModel = slot == 1
		model.bipedLeftArm.showModel = slot == 1
		model.bipedRightLeg.showModel = slot == 2 || slot == 3
		model.bipedLeftLeg.showModel = slot == 2 || slot == 3
		model.isSneak = living.isSneaking
		model.isRiding = living.isRiding
		model.isChild = living.isChild
		model.aimedBow = false
		model.heldItemRight = if (living.heldItem != null) 1 else 0
		
		if (living is EntityPlayer && living.itemInUseDuration > 0) {
			val enumaction = living.getItemInUse().itemUseAction
			if (enumaction == EnumAction.block) {
				model.heldItemRight = 3
			} else if (enumaction == EnumAction.bow) {
				model.aimedBow = true
			}
		}
		
		return model
	}
	
	override fun getDiscount(stack: ItemStack, slot: Int, player: EntityPlayer) = -0.05f
}

object SnowArmorAbilityHandler {
	
	init {
		eventForge()
	}
	
	@SubscribeEvent
	fun onLivingUpdate(e: LivingEvent.LivingUpdateEvent) {
		val player = e.entityLiving as? EntityPlayer ?: return
		
		if ((AlfheimItems.snowHelmet as ItemSnowArmor).hasArmorSet(player)) {
			player.isInWeb = false
			
			if (player.worldObj.isRemote) return
			val rider = player.riddenByEntity as? EntityLivingBase ?: return
			val className = rider::class.java.name
			
			if (className == "thaumcraft.common.entities.monster.EntityEldritchCrab" && player.rng.nextInt(5) == 0) {
				rider.dismountEntity(rider.ridingEntity)
				rider.ridingEntity = null
				player.riddenByEntity = null
			}
		}
	}
	
	@SubscribeEvent
	fun onLivingHurt(e: LivingHurtEvent) {
		val player = e.entityLiving as? EntityPlayer ?: return
		
		if (!(AlfheimItems.snowHelmet as ItemSnowArmor).hasArmorSet(player)) return
		
		if (e.source.damageType == "frost") {
			e.isCanceled = true
			return
		}
		
		if (e.source.isFireDamage)
			e.ammount /= 2
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	fun freezePlayer(e: SheerColdHandler.SheerColdTickEvent) {
		val target = e.entityLiving
		if (!RagnarokHandler.checkSet(target, (AlfheimItems.snowChest as ItemSnowArmor).armorSetStacks)) return
		if (target is EntityPlayer && ASJUtilities.getAmount(target.inventory, ItemStack(AlfheimItems.eventResource, 1, EventResourcesMetas.VolcanoRelic)) > 0) return
		e.delta = max(e.delta ?: 0f, 100f/(30*60*20)) // max so that if other source gives more cold it won't override
	}
}