package alfheim.common.item.equipment.tool

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.*
import alfheim.client.core.helper.IconHelper
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.creator.*
import alfheim.common.item.equipment.armor.fenrir.ItemFenrirArmor
import alfheim.common.item.material.ElvenResourcesMetas
import com.google.common.collect.*
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.*
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.potion.*
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.event.entity.living.LivingHurtEvent
import vazkii.botania.common.core.helper.*
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.equipment.tool.manasteel.ItemManasteelSword
import vazkii.botania.common.lib.LibMisc

class ItemFenrirClaws: ItemManasteelSword(AlfheimAPI.FENRIR, "FenrirClaws") {
	
	val MANA_PER_DAMAGE = 40
	val attackDamage = 3.0
	
	lateinit var overlay: IIcon
	
	init {
		creativeTab = AlfheimTab
	}
	
	override fun isFull3D() = false
	
	override fun onUpdate(stack: ItemStack, world: World, player: Entity, itemSlot: Int, isSelected: Boolean) {
		super.onUpdate(stack, world, player, itemSlot, isSelected)
		if (player !is EntityPlayer) return
		ItemNBTHelper.setBoolean(stack, "SET", ItemFenrirArmor.hasSet(player))
	}
	
	override fun getIsRepairable(stack: ItemStack?, material: ItemStack?): Boolean {
		return material?.item === AlfheimItems.elvenResource && material.meta == ElvenResourcesMetas.MauftriumNugget.I
	}
	
	override fun getAttributeModifiers(stack: ItemStack): Multimap<String, AttributeModifier> {
		val set = ItemNBTHelper.getBoolean(stack, "SET", false)
		val multimap = HashMultimap.create<String, AttributeModifier>()
		multimap.put(SharedMonsterAttributes.attackDamage.attributeUnlocalizedName, AttributeModifier(field_111210_e, "Weapon modifier", attackDamage + if (set) (13.75 / 1.5 - 9 + attackDamage) else 0.0, 0))
		return multimap
	}
	
	override fun getItemEnchantability() = 14
	
	override fun getManaPerDamage() = MANA_PER_DAMAGE
	
	override fun getItemStackDisplayName(stack: ItemStack) =
		super.getItemStackDisplayName(stack).replace("&", "\u00a7")
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack) =
		super.getUnlocalizedNameInefficiently(stack).replace(LibMisc.MOD_ID, ModInfo.MODID)
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this, "0")
		overlay = IconHelper.forItem(reg, this, "1")
	}
	
	override fun requiresMultipleRenderPasses() = true
	
	override fun getRenderPasses(metadata: Int) = 3
	
	override fun getIcon(stack: ItemStack?, pass: Int): IIcon? {
		return when (pass) {
			0    -> getIconIndex(stack)
			
			1    -> {
				ASJRenderHelper.setGlow()
				overlay
			}
			
			else -> { // without that part armor will glow :(
				ASJRenderHelper.discard()
				
				// crutch because RenderItem#renderIcon has no null check :(
				// and some other places maybe too...
				// why not just use @Nullable ?
				getIconIndex(stack)
			}
		}
	}
	
	override fun getIconIndex(stack: ItemStack?) = itemIcon!! // no elucidator
	
	companion object {
		
		init {
			eventForge()
		}
		
		@SubscribeEvent(priority = EventPriority.LOWEST)
		fun onLivingAttacked(e: LivingHurtEvent) {
			val player = e.entityLiving
			val damage = e.source
			if (player !is EntityPlayer || damage !is EntityDamageSource || !player.isUsingItem) return
			
			val enemyEntity = damage.entity
			if (enemyEntity !is EntityLivingBase || enemyEntity == player) return
			
			val itemInUse = player.itemInUse
			if (itemInUse.item !is ItemFenrirClaws) return
			
			val lookVec = Vector3(player.lookVec)
			val targetVec = Vector3.fromEntityCenter(enemyEntity).sub(Vector3.fromEntityCenter(player))
			val epsilon = lookVec.dotProduct(targetVec) / (lookVec.mag() * targetVec.mag())
			if (epsilon <= 0.75) return
			
			e.ammount /= 2f
		}
	}
}
