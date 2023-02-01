package alfheim.common.item.equipment.armor.elvoruim

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.potion.PotionEternity
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.potion.*
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.event.entity.living.LivingHurtEvent
import org.lwjgl.opengl.GL11.*
import vazkii.botania.api.item.IAncientWillContainer
import vazkii.botania.api.mana.*
import vazkii.botania.client.core.helper.IconHelper
import vazkii.botania.common.Botania
import vazkii.botania.common.core.helper.ItemNBTHelper

open class ItemElvoriumHelmet(name: String): ItemElvoriumArmor(0, name), IAncientWillContainer, IManaGivingItem {
	
	constructor(): this("ElvoriumHelmet")
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		super.registerIcons(reg)
		willIcon = IconHelper.forName(reg, "willFlame")
	}
	
	override fun onArmorTick(world: World, player: EntityPlayer, stack: ItemStack) {
		super.onArmorTick(world, player, stack)
		if (!hasArmorSet(player)) return
		
		val food = player.foodStats.foodLevel
		if (food in 1..17 && player.shouldHeal() && player.ticksExisted % 80 == 0)
			player.heal(1f)
		
		ManaItemHandler.dispatchMana(stack, player, 2, true)
	}
	
	override fun addAncientWill(stack: ItemStack, will: Int) {
		ItemNBTHelper.setBoolean(stack, TAG_ANCIENT_WILL + will, true)
	}
	
	override fun hasAncientWill(stack: ItemStack?, will: Int): Boolean {
		return hasAncientWill_(stack, will)
	}
	
	@SideOnly(Side.CLIENT)
	override fun addArmorSetDescription(stack: ItemStack?, list: List<String>) {
		super.addArmorSetDescription(stack, list)
		
		for (i in 0..6)
			if (hasAncientWill(stack, i))
				addStringToTooltip(StatCollector.translateToLocal("botania.armorset.will$i.desc"), list)
	}
	
	companion object {
		
		private const val TAG_ANCIENT_WILL = "AncientWill"
		internal lateinit var willIcon: IIcon
		
		init {
			eventForge()
		}
		
		fun hasAncientWill_(stack: ItemStack?, will: Int): Boolean {
			return ItemNBTHelper.getBoolean(stack, TAG_ANCIENT_WILL + will, false)
		}
		
		fun hasAnyWill(stack: ItemStack): Boolean {
			for (i in 0..6)
				if (hasAncientWill_(stack, i))
					return true
			
			return false
		}
		
		@SubscribeEvent
		fun onEntityHurt(e: LivingHurtEvent) {
			val attacker = e.source.entity as? EntityPlayer ?: return
			
			if (!hasSet(attacker)) return
			
			val stack = attacker.inventory.armorItemInSlot(3) ?: return
			val crit = attacker.fallDistance > 0f && !attacker.onGround && !attacker.isOnLadder && !attacker.isInWater && !attacker.isPotionActive(Potion.blindness) && attacker.ridingEntity == null
			
			if (crit && stack.item is ItemElvoriumHelmet) {
				if (hasAncientWill_(stack, 0))
					e.entityLiving.addPotionEffect(PotionEffect(Potion.weakness.id, 20, 1))
				
				if (hasAncientWill_(stack, 1))
					e.ammount *= 1f + (1f - attacker.health / attacker.maxHealth) * 0.5f
				
				if (hasAncientWill_(stack, 2))
					attacker.heal(e.ammount * 0.25f)
				
				if (hasAncientWill_(stack, 3))
					e.entityLiving.addPotionEffect(PotionEffect(Potion.moveSlowdown.id, 60, 1))
				
				if (hasAncientWill_(stack, 4))
					e.source.setDamageBypassesArmor()
				
				if (hasAncientWill_(stack, 5))
					e.entityLiving.addPotionEffect(PotionEffect(Potion.wither.id, 60, 1))
				
				if (hasAncientWill_(stack, 6)) {
					e.entityLiving.addPotionEffect(PotionEffect(Potion.blindness.id, 60, 1))
					e.entityLiving.addPotionEffect(PotionEffect(AlfheimConfigHandler.potionIDEternity, 60, PotionEternity.STUN or PotionEternity.IRREMOVABLE))
				}
			}
		}
		
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		fun onPlayerRender(event: RenderPlayerEvent.Specials.Post) {
			if (event.entityLiving.getActivePotionEffect(Potion.invisibility.id) != null)
				return
			
			val player = event.entityPlayer
			
			val yaw = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * event.partialRenderTick
			val yawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * event.partialRenderTick
			val pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * event.partialRenderTick
			
			glPushMatrix()
			glRotatef(yawOffset, 0f, -1f, 0f)
			glRotatef(yaw - 270, 0f, 1f, 0f)
			glRotatef(pitch, 0f, 0f, 1f)
			
			val helm = player.inventory.armorItemInSlot(3)
			if (helm != null && helm.item is ItemElvoriumHelmet)
				renderOnPlayer(helm, event)
			
			glPopMatrix()
		}
		
		@SideOnly(Side.CLIENT)
		fun renderOnPlayer(stack: ItemStack, event: RenderPlayerEvent) {
			if (hasAnyWill(stack) && !(stack.item as ItemElvoriumArmor).hasPhantomInk(stack)) {
				glPushMatrix()
				val f = willIcon.minU
				val f1 = willIcon.maxU
				val f2 = willIcon.minV
				val f3 = willIcon.maxV
				if (event.entityPlayer !in mc.theWorld.playerEntities) glTranslatef(0f, -1.68f, 0f) // fix for armorstands
				vazkii.botania.api.item.IBaubleRender.Helper.translateToHeadLevel(event.entityPlayer)
				mc.renderEngine.bindTexture(TextureMap.locationItemsTexture)
				glRotatef(90f, 0f, 1f, 0f)
				glRotatef(180f, 1f, 0f, 0f)
				glTranslated(-0.26, 0.15, -0.32)
				glScaled(0.5)
				ItemRenderer.renderItemIn2D(Tessellator.instance, f1, f2, f, f3, willIcon.iconWidth, willIcon.iconHeight, 1f / 16f)
				glPopMatrix()
			}
		}
	}
}
