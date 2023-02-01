package alfheim.common.item.relic

import alexsocol.asjlib.*
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.*
import alfheim.common.core.helper.*
import alfheim.common.core.util.AlfheimTab
import alfheim.common.entity.*
import net.minecraft.entity.*
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.world.World
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.relic.ItemRelic

class ItemGungnir: ItemRelic("Gungnir") {
	
	init {
		creativeTab = AlfheimTab
		setFull3D()
	}
	
	override fun shouldRotateAroundWhenRendering(): Boolean {
		return false
	}
	
	override fun onUpdate(stack: ItemStack, world: World?, entity: Entity?, slot: Int, inHand: Boolean) {
		super.onUpdate(stack, world, entity, slot, inHand)
		
		val player = entity as? EntityPlayer ?: return
		
		if (stack.cooldown > 0) {
			if (player.capabilities.isCreativeMode)
				stack.cooldown = 0
			else if (ManaItemHandler.requestManaExact(stack, player, 50, true))
				--stack.cooldown
		}
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World?, player: EntityPlayer): ItemStack {
		if (stack.cooldown > 0) return stack
		
		player.setItemInUse(stack, getMaxItemUseDuration(stack))
		return stack
	}
	
	override fun getMaxItemUseDuration(stack: ItemStack?) = 72000
	
	override fun getItemUseAction(stack: ItemStack?) = EnumAction.bow
	
	override fun onPlayerStoppedUsing(stack: ItemStack, world: World, player: EntityPlayer, left: Int) {
		if (world.isRemote) return
		
		if (player.isSneaking)
			shoot(stack, world, player)
		else
			onePunchMan(stack, world, player)
	}
	
	fun onePunchMan(stack: ItemStack, world: World, player: EntityPlayer) {
		var target: EntityLivingBase? = null
		
		if (AlfheimConfigHandler.enableMMO) {
			val tgt = CardinalSystem.TargetingSystem.getTarget(player)
			if (!tgt.isParty) target = tgt.target
			if (target != null && ASJUtilities.isNotInFieldOfVision(target, player)) target = null
		}
		
		if (target == null)
			target = ASJUtilities.getMouseOver(player, 128.0, false)?.entityHit as? EntityLivingBase ?: return
		
		val src = DamageSource.causePlayerDamage(player).setDamageBypassesArmor().setTo(ElementalDamage.LIGHTNESS)
		if (target is EntityDragon)
			target.attackEntityFromPart(target.dragonPartHead, src, 100f)
		else
			target.attackEntityFrom(src, 100f)
		
		
		if (!player.capabilities.isCreativeMode) stack.cooldown = 1000
		
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.GUNGNIR, player.dimension, player.entityId.D, target.entityId.D)
	}
	
	fun shoot(stack: ItemStack, world: World, player: EntityPlayer) {
		if (!world.isRemote) EntitySubspaceSpear(world, player).also {
			it.damage = 20f
			it.life = 200
			it.type = 1
			it.noClip = true
			it.pitch = -player.rotationPitch
			it.rotationYaw = player.rotationYaw
			it.rotation = MathHelper.wrapAngleTo180_float(-player.rotationYaw + 180)
			it.shoot(player, player.rotationPitch, player.rotationYaw, 0f, 1.45f, 1f)
			it.setPosition(player)
			it.posY += player.eyeHeight
		}.spawn()
		
		if (!player.capabilities.isCreativeMode) stack.cooldown = 500
	}
	
	private var ItemStack.cooldown
		get() = ItemNBTHelper.getInt(this, TAG_COOLDOWN, 0)
		set(value) = ItemNBTHelper.setInt(this, TAG_COOLDOWN, value)
	
	companion object {
		
		const val TAG_COOLDOWN = "cooldown"
	}
}
