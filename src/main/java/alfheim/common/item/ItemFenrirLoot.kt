package alfheim.common.item

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.client.core.helper.IconHelper
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.tile.TileRainbowManaFlame
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.helper.*
import alfheim.common.potion.PotionEternity
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.item.*
import net.minecraft.potion.*
import net.minecraft.server.MinecraftServer
import net.minecraft.util.*
import net.minecraft.world.World
import vazkii.botania.api.internal.IManaBurst
import vazkii.botania.api.mana.*
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.entity.EntityManaBurst
import java.awt.Color

// All functional (feature-related) code was created by ChatGPT, I just adapted it
class ItemFenrirLoot: ItemMod("FenrirLoot"), ILensEffect {
	
	lateinit var icons: List<IIcon>
	lateinit var iconsCD: List<IIcon>
	
	init {
		setFull3D()
		setHasSubtypes(true)
		setMaxStackSize(1)
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (world.isRemote || stack.cooldown > 0) return stack
		
		when (stack.meta) {
			FenrirLootMetas.HatiEye.I         -> doHatiEye(stack, world, player)
			FenrirLootMetas.SkollBane.I       -> doSkollBane(stack, player)
			FenrirLootMetas.HowlingCharm.I    -> doHowlingCharm(stack, world, player)
			FenrirLootMetas.WolfBloodPotion.I -> doWolfBloodPotion(stack, player)
			FenrirLootMetas.FenrisulfrHeart.I -> doFenrisulfrHeart(stack, player)
		}
		
		if (player.capabilities.isCreativeMode) stack.cooldown = 0
		
		return stack
	}
	
	private fun doHatiEye(stack: ItemStack, world: World, player: EntityPlayer) {
		// Play sound and particles
		world.playSoundEffect(player.posX, player.posY, player.posZ, "mob.wolf.growl", 1f, 1f)
		world.spawnParticle("fireworksSpark", player.posX, player.posY, player.posZ, 0.0, 0.0, 0.0)
		
		var did = false
		
		// Blind nearby enemies
		try {
			val range = 10
			getEntitiesWithinAABB(world, EntityLivingBase::class.java, player.boundingBox.expand(range)).forEach { target ->
				if (target === player || Vector3.entityDistance(target, player) > range) return@forEach
				
				target.addPotionEffect(PotionEffect(Potion.blindness.id, 200))
				did = true
			}
			
			// light up dark areas
			if (!player.isSneaking) return
			
			val (i, j, k) = Vector3.fromEntity(player).mf()
			for (x in i.bidiRange(range))
				for (y in j.bidiRange(range))
					for (z in k.bidiRange(range)) {
						val at = world.getBlock(x, y, z)
						if (!at.isAir(world, x, y, z)) continue
						if (world.getBlockLightValue(x, y, z) > 8) continue
						if (!at.isReplaceable(world, x, y, z)) continue
						
						did = true
						
						world.setBlock(x, y, z, AlfheimBlocks.rainbowFlame, 0, 3)
						val tile = world.getTileEntity(x, y, z) as? TileRainbowManaFlame ?: continue
						tile.color = 0xDD0000
						tile.invisible = true
					}
		} finally {
			if (did) stack.cooldown = 100
		}
	}
	
	// region: Skoll Bane things
	
	private fun doSkollBane(stack: ItemStack, player: EntityPlayer) {
		getBurst(player, stack).spawn()
		player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "mob.wolf.growl", 1f, 1f)
		stack.cooldown = 50
	}
	
	fun getBurst(player: EntityPlayer, stack: ItemStack): EntityManaBurst {
		val burst = EntityManaBurst(player)
		
		val motionModifier = 7f
		
		burst.color = Color(0xFFDA20).rgb
		burst.mana = 1
		burst.startingMana = 1
		burst.minManaLoss = 200
		burst.manaLossPerTick = 1f
		burst.gravity = 0f
		burst.setMotion(burst.motionX * motionModifier, burst.motionY * motionModifier, burst.motionZ * motionModifier)
		
		val lens = stack.copy()
		ItemNBTHelper.setString(lens, TAG_ATTACKER_USERNAME, player.commandSenderName)
		burst.sourceLens = lens
		return burst
	}
	
	override fun apply(stack: ItemStack?, props: BurstProperties?) = Unit
	
	override fun collideBurst(burst: IManaBurst?, pos: MovingObjectPosition?, isManaBlock: Boolean, dead: Boolean, stack: ItemStack?) = dead
	
	override fun updateBurst(burst: IManaBurst, stack: ItemStack?) {
		burst as EntityThrowable
		
		if (burst.isFake || burst.worldObj.isRemote) return
		
		val axis = getBoundingBox(burst.posX, burst.posY, burst.posZ, burst.lastTickPosX, burst.lastTickPosY, burst.lastTickPosZ).expand(1)
		val entities = getEntitiesWithinAABB(burst.worldObj, EntityLivingBase::class.java, axis)
		
		val attacker = burst.worldObj.getPlayerEntityByName(ItemNBTHelper.getString(burst.sourceLens, TAG_ATTACKER_USERNAME, ""))
		
		entities.forEach {
			if (it is EntityPlayer && !(it !== attacker && (MinecraftServer.getServer() == null || MinecraftServer.getServer().isPVPEnabled))) return@forEach
			if (it.hurtTime != 0) return@forEach
			val damage = if (it.isEntityUndead) 20f else 10f
			val did = it.attackEntityFrom(if (attacker == null) DamageSource.magic else DamageSource.causePlayerDamage(attacker).setDamageBypassesArmor().setMagicDamage().setFireDamage().setTo(ElementalDamage.LIGHTNESS), damage)
			if (!did) return@forEach
			if (attacker != null) it.knockback(attacker, 2f)
			it.setFire(5)
			burst.setDead()
			return
		}
	}
	
	override fun doParticles(burst: IManaBurst?, stack: ItemStack?) = true
	// endregion
	
	private fun doHowlingCharm(stack: ItemStack, world: World, player: EntityPlayer) {
		world.playSoundAtEntity(player, "mob.wolf.growl", 1f, 1f)
		
		var did = false
		
		val range = 10
		for (entity in getEntitiesWithinAABB(world, EntityLivingBase::class.java, player.boundingBox.expand(range))) {
			if (entity === player) continue
			if (Vector3.entityDistance(entity, player) > range) continue
			
			// Damage the enemy
			entity.attackEntityFrom(DamageSource.causePlayerDamage(player).setTo(ElementalDamage.AIR), 10f)
			// Stun the enemy
			(entity as? EntityLiving)?.attackTarget = null
			entity.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDEternity, 50, PotionEternity.STUN or PotionEternity.IRREMOVABLE))
			
			did = true
		}
		
		if (did) stack.cooldown = 200
	}
	
	private fun doWolfBloodPotion(stack: ItemStack, player: EntityPlayer) {
		player.addPotionEffect(PotionEffect(Potion.damageBoost.id, 2400, 1))
		player.addPotionEffect(PotionEffect(Potion.moveSpeed.id, 2400, 1))
		player.addPotionEffect(PotionEffect(Potion.jump.id, 2400, 1))
		player.addPotionEffect(PotionEffect(AlfheimConfigHandler.potionIDBeastWithin, 2400))
		
		// Inform the player that the potion has been consumed
		player.addChatMessage(ChatComponentText(EnumChatFormatting.YELLOW.toString() + "You feel the power of the wolf blood course through your veins!"))
		
		stack.cooldown = 12000
	}
	
	private fun doFenrisulfrHeart(stack: ItemStack, player: EntityPlayer) {
		player.addPotionEffect(PotionEffect(Potion.damageBoost.id, 2400, 3))
		player.addPotionEffect(PotionEffect(Potion.regeneration.id, 1200, 2))
		player.addPotionEffect(PotionEffect(Potion.resistance.id, 3600, 4))
		player.addPotionEffect(PotionEffect(Potion.fireResistance.id, 3600, 0))
		
		player.playSoundAtEntity("mob.enderdragon.growl", 1.0f, 1.0f)
		
		stack.cooldown = 6000
	}
	
	// common things
	
	override fun onUpdate(stack: ItemStack, world: World?, entity: Entity?, slot: Int, inHand: Boolean) {
		if (stack.cooldown > 0) --stack.cooldown
	}
	
	override fun registerIcons(reg: IIconRegister) {
		icons = FenrirLootMetas.values().map { IconHelper.forName(reg, it.name) }
		iconsCD = FenrirLootMetas.values().map { IconHelper.forName(reg, "${it}CD") }
	}
	
	override fun getIcon(stack: ItemStack, pass: Int) = getIconIndex(stack)
	
	override fun getIconIndex(stack: ItemStack) = (if (stack.cooldown > 0) iconsCD else icons).safeGet(stack.meta)
	
	override fun getUnlocalizedName(stack: ItemStack) = "item.${FenrirLootMetas.of(stack.meta).toString()}"
	
	override fun getSubItems(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (type in FenrirLootMetas.values())
			list.add(type.stack)
	}
	
	override fun shouldRotateAroundWhenRendering() = mc.gameSettings.thirdPersonView != 0
	
	companion object {
		
		private const val TAG_COOLDOWN = "cooldown"
		private const val TAG_ATTACKER_USERNAME = "attackerUsername"
		
		private var ItemStack.cooldown
			get() = ItemNBTHelper.getInt(this, TAG_COOLDOWN, 0)
			set(value) = ItemNBTHelper.setInt(this, TAG_COOLDOWN, value)
	}
	
	enum class FenrirLootMetas {
		HatiEye,
		SkollBane,
		HowlingCharm,
		WolfBloodPotion,
		FenrisulfrHeart;
		
		val I get() = ordinal
		
		val stack get() = ItemStack(AlfheimItems.fenrirLoot, 1, I)
		
		companion object {
			fun of(meta: Int) = values().getOrNull(meta)
		}
	}
}
