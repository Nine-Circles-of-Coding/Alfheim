package alfheim.common.entity

import alexsocol.asjlib.*
import alfheim.api.entity.*
import alfheim.common.core.handler.SheerColdHandler
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.item.AlfheimItems
import alfheim.common.item.equipment.bauble.ItemPendant
import alfheim.common.item.equipment.armor.ItemSnowArmor
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.eventhandler.*
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.Potion
import net.minecraft.world.World
import net.minecraftforge.common.ISpecialArmor
import net.minecraftforge.event.entity.living.LivingHealEvent
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.item.ModItems
import kotlin.collections.*
import kotlin.math.abs

class EntityPrimalBossChunkAttack(world: World, val summoner: EntityLivingBase?, target: EntityPlayer?): Entity(world) {
	
	var isIce
		get() = getFlag(6)
		set(value) = setFlag(6, value)
	
	init {
		setSize(0f, 0f)
		if (target != null) setPosition(target.chunkCoordX.D * 16 + 8, 64.0, target.chunkCoordZ.D * 16 + 8)
		if (summoner != null) isIce = summoner is INiflheimEntity
	}
	
	override fun entityInit() {
		dataWatcher.addObject(2, 0)
	}
	
	constructor(world: World): this(world, null, null)
	
	fun iceAttack(target: EntityLivingBase, extra: Boolean) {
		val dmg = DamageSourceSpell.nifleice(summoner)
		if (extra) dmg.setDamageBypassesArmor()
		
		if (target !is EntityPlayer || !ItemPendant.canProtect(target, ItemPendant.Companion.EnumPrimalWorldType.NIFLHEIM, 500))
			target.attackEntityFrom(dmg, 2.5f)
		
		if (extra) {
			target.addPotionEffect(PotionEffectU(Potion.digSlowdown.id, 100, 2))
			target.addPotionEffect(PotionEffectU(Potion.weakness.id, 100, 2))
		}
	}
	
	fun fireAttack(target: EntityLivingBase, extra: Boolean) {
		val dmg = DamageSourceSpell.soulburn(summoner)
		if (extra) dmg.setDamageBypassesArmor()
		
		for (i in 1..4) target.getEquipmentInSlot(i)?.apply {
			if (item is ItemSnowArmor) return@apply
			(item as? ISpecialArmor)?.damageArmor(target, this, DamageSourceSpell.soulburn, 1, i) ?: damageItem(1, target)
		}
		
		if (target !is EntityPlayer || !ItemPendant.canProtect(target, ItemPendant.Companion.EnumPrimalWorldType.MUSPELHEIM, 500))
			target.attackEntityFrom(dmg, 2.5f)
		
		if (target !is EntityPlayer) return
		ManaItemHandler.requestMana(ItemStack(Blocks.stone), target, 1000, true)
		
		if (ASJUtilities.chance(90)) return
		PlayerHandler.getPlayerBaubles(target).apply {
			if (this[0]?.item !== ModItems.bloodPendant) return@apply
			this[0] = ItemStack(ModItems.bloodPendant)
		}
		
		val inv = target.inventory
		val map = HashMap<Int, ItemStack>()
		for (i in 0 until inv.sizeInventory) {
			val stack = inv[i] ?: continue
			if (stack.item inl removableItems)
				map[i] = stack
		}
		val (slot, stack) = map.entries.random(rand) ?: return
		inv[slot] = when (stack.item) {
			Items.potionitem          -> ItemStack(Items.glass_bottle)
			ModItems.brewVial,
			AlfheimItems.splashPotion -> ItemStack(ModItems.vial)
			ModItems.brewFlask        -> ItemStack(ModItems.vial, 1, 1)
			ModItems.bloodPendant     -> ItemStack(ModItems.bloodPendant)
			else                      -> return
		}
	}
	
	override fun onUpdate() {
		if (ticksExisted > 150) return setDead()
		if (!worldObj.isRemote) return
		for (i in 0..if (isIce) 63 else 127) {
			val (r, g, b) = if (isIce) arrayOf(0.0125f, 0.0125f, 0.025f) else arrayOf(0.05f, 0.0125f, 0f)
			Botania.proxy.wispFX(worldObj, chunkCoordX * 16 + Math.random() * 16, posY + 0.01, chunkCoordZ * 16 + Math.random() * 16, r, g, b, 3f, 0f, if (isIce) 0f else 0.1f, 0f, 3f)
		}
	}
	
	operator fun contains(target: EntityLivingBase): Boolean {
		if (isIce && target is INiflheimEntity) return false
		if (!isIce && target is IMuspelheimEntity) return false
		
		val (cx, cy, cz) = arrayOf(chunkCoordX * 16, posY.I, chunkCoordZ * 16)
		return getBoundingBox(cx, cy, cz, cx + 16, cy + if (isIce) 2 else 6, cz + 16).intersectsWith(target.boundingBox)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		isIce = nbt.getBoolean("ice")
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setBoolean("ice", isIce)
	}
	
	companion object {
		
		val removableItems = arrayOf(Items.potionitem, ModItems.brewVial, ModItems.brewFlask, AlfheimItems.splashPotion, ModItems.bloodPendant)
		
		init {
			eventForge()
		}
		
		@SubscribeEvent(priority = EventPriority.HIGH)
		fun affectPlayersInside(e: SheerColdHandler.SheerColdTickEvent) {
			val target = e.entityLiving
			val epbca = target.worldObj.loadedEntityList.firstOrNull { it is EntityPrimalBossChunkAttack && target in it } as? EntityPrimalBossChunkAttack ?: return
			
			e.delta = (e.delta ?: 0f) + 0.25f * if (epbca.isIce) 1 else -1
		}
		
		@SubscribeEvent(priority = EventPriority.LOWEST)
		fun attackUnprotectedPlayers(e: SheerColdHandler.SheerColdTickEvent) {
			val target = e.entityLiving
			val epbca = target.worldObj.loadedEntityList.firstOrNull { it is EntityPrimalBossChunkAttack && target in it } as? EntityPrimalBossChunkAttack ?: return
			
			val extra = abs(e.delta ?: 0f) > 0
			if (epbca.isIce) epbca.iceAttack(e.entityLiving, extra) else epbca.fireAttack(e.entityLiving, extra)
		}
		
		@SubscribeEvent
		fun healingEvent(e: LivingHealEvent) {
			val target = e.entityLiving
			e.isCanceled = target.worldObj.loadedEntityList.any { it is EntityPrimalBossChunkAttack && target in it }
		}
	}
}
