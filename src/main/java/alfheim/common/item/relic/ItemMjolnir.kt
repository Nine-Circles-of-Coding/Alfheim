package alfheim.common.item.relic

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.item.ColorOverrideHelper
import alfheim.client.core.helper.IconHelper
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.helper.*
import alfheim.common.entity.EntityMjolnir
import alfheim.common.entity.spell.EntitySpellFenrirStorm
import alfheim.common.item.equipment.bauble.*
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.*
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.util.MovingObjectPosition.MovingObjectType
import net.minecraft.world.World
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.core.helper.ItemNBTHelper.*
import vazkii.botania.common.item.relic.*
import java.awt.Color
import java.util.*
import kotlin.math.min
import vazkii.botania.common.core.helper.Vector3 as Bector3

class ItemMjolnir: ItemRelic("Mjolnir") {
	
	init {
		setHasSubtypes(true)
		setFull3D()
	}
	
	// ################ Checker ################
	
	fun isWorthy(player: EntityLivingBase): Boolean {
		if (RagnarokHandler.blockedPowers[0]) return false
		
		if (player !is EntityPlayer) return false
		if (player.capabilities.isCreativeMode) return true
		return ItemPriestCloak.getCloak(0, player) != null && ItemPriestEmblem.getEmblem(0, player) != null && ItemThorRing.getThorRing(player) != null
	}
	
	// ################ Left-click ################
	
	override fun onEntitySwing(entity: EntityLivingBase, stack: ItemStack?): Boolean {
		if (!entity.worldObj.isRemote && entity.isSneaking && isWorthy(entity))
			EntitySpellFenrirStorm(entity.worldObj, entity, true).spawn()
		
		return super.onEntitySwing(entity, stack)
	}
	
	override fun hitEntity(stack: ItemStack?, entity: EntityLivingBase, attacker: EntityLivingBase): Boolean {
		if (isWorthy(attacker)) return false
		
		val range = 10
		var dmg = 8f
		
		val alreadyTargetedEntities = ArrayList<EntityLivingBase>()
		var lightningSource = entity
		
		val lightningSeed = getLong(stack, TAG_LIGHTNING_SEED, 0)
		val rand = Random(lightningSeed)
		
		for (i in 0..5) {
			val entities = selectEntitiesWithinAABB(entity.worldObj, EntityLivingBase::class.java, lightningSource.boundingBox(range)) {
				it is IMob && it !is EntityPlayer && !alreadyTargetedEntities.contains(it)
			}
			if (entities.isEmpty()) break
			
			val target = entities[rand.nextInt(entities.size)]
			
			target.attackEntityFrom(if (attacker is EntityPlayer) DamageSource.causePlayerDamage(attacker) else DamageSource.causeMobDamage(attacker).setTo(ElementalDamage.ELECTRIC), dmg)
			
			var color = 0x0079C4
			if (attacker is EntityPlayer) color = ColorOverrideHelper.getColor(attacker, color)
			
			Botania.proxy.lightningFX(entity.worldObj, Bector3.fromEntityCenter(lightningSource), Bector3.fromEntityCenter(target), 1f, color, Color(color).brighter().brighter().rgb)
			alreadyTargetedEntities.add(target)
			lightningSource = target
			dmg--
		}
		
		if (!entity.worldObj.isRemote)
			setLong(stack, TAG_LIGHTNING_SEED, entity.worldObj.rand.nextLong())
		
		return true
	}
	
	override fun getAttributeModifiers(stack: ItemStack?) = super.getAttributeModifiers(stack).apply {
		put(SharedMonsterAttributes.attackDamage.attributeUnlocalizedName, AttributeModifier(Item.field_111210_e, "Weapon modifier", 8.0, 0))
	}
	
	// ################ Right-click ################
	
	override fun getMaxItemUseDuration(stack: ItemStack) = 72000
	
	override fun getItemUseAction(stack: ItemStack) = EnumAction.bow
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (stack.cooldown > 0) return stack
		
		if (isWorthy(player) && getInt(stack, TAG_SHAKE_TIMER, 0) <= 0) {
			player.setItemInUse(stack, getMaxItemUseDuration(stack))
		}
		
		return stack
	}
	
	override fun onUsingTick(stack: ItemStack, player: EntityPlayer, itemInUseCountunt: Int) {
		if (player.worldObj.isRemote) return
		
		if (getCharge(stack) < MAX_CHARGE) {
			val req = min(MAX_CHARGE - getCharge(stack), CHARGE_PER_TICK)
			addCharge(stack, ManaItemHandler.requestMana(stack, player, req, true))
		}
	}
	
	override fun onPlayerStoppedUsing(stack: ItemStack, world: World, player: EntityPlayer, itemInUseCount: Int) {
		val charge = getCharge(stack)
		setCharge(stack, 0)
		if (charge < MAX_CHARGE) return
		if (player.isSneaking) {
			setCharge(stack, 0)
			if (!world.isRemote)
				EntityMjolnir(world, player, stack.copy()).spawn()
			
			stack.cooldown = 600
			return
		}
		
		if (player !is EntityPlayerMP) return
		val mop = ASJUtilities.getSelectedBlock(player, player.theItemInWorldManager.blockReachDistance, true)
		
		if (mop == null || mop.typeOfHit != MovingObjectType.BLOCK) return
		setInt(stack, TAG_SHAKE_TIMER, 16)
		setInt(stack, TAG_SHAKE_X, mop.blockX)
		setInt(stack, TAG_SHAKE_Y, mop.blockY)
		setInt(stack, TAG_SHAKE_Z, mop.blockZ)
		
		val start = Bector3(mop.blockX.D + 0.5, mop.blockY.D + 1.5, mop.blockZ.D + 0.5)
		val end = Bector3()
		val oY = Bector3(0.0, 1.0, 0.0)
		
		val color = ColorOverrideHelper.getColor(player, 0x0079C4)
		
		for (i in 0 until 360 step 5) {
			end.set(5.0, 1.0, 0.0).rotate(i.D, oY).add(start)
			Botania.proxy.lightningFX(world, start, end, 3f, color, Color(color).brighter().brighter().rgb)
		}
	}
	
	override fun onUpdate(stack: ItemStack, world: World, entity: Entity, slot: Int, inHand: Boolean) {
		if (stack.cooldown > 0) stack.cooldown--
		
		if (entity !is EntityLivingBase) return
		
		val timer = getInt(stack, TAG_SHAKE_TIMER, 0)
		
		if (timer > 0 && timer % 3 == 0) {
			val x = getInt(stack, TAG_SHAKE_X, 0)
			val y = getInt(stack, TAG_SHAKE_Y, 0)
			val z = getInt(stack, TAG_SHAKE_Z, 0)
			
			val radius = 5 - timer / 3 + 1.0
			
			val center = Vector3(x + 0.5, y, z + 0.5)
			getEntitiesWithinAABB(world, EntityLivingBase::class.java, getBoundingBox(x, y + 1, z).offset(0.5).expand(radius)).forEach { e ->
				if (e === entity) return@forEach
				
				if (Vector3.vecEntityDistance(center, e) in (radius - 1)..(radius + 1)) {
					val src = (if (entity is EntityPlayer) DamageSource.causePlayerDamage(entity) else DamageSource.causeMobDamage(entity)).setTo(ElementalDamage.ELECTRIC)
					if (e.onGround) {
						e.attackEntityFrom(src, 10f)
						e.hurtResistantTime = 0
						e.hurtTime = 0
					}
					
					e.attackEntityFrom(src, 5f)
				}
			}
			
			VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.FALLING, world.provider.dimensionId, x.D, y.D, z.D, radius)
		}
		
		if (timer > 0) setInt(stack, TAG_SHAKE_TIMER, timer - 1)
		
		super.onUpdate(stack, world, entity, slot, inHand)
	}
	
	// ################ Render ################
	
	@SideOnly(Side.CLIENT)
	override fun getColorFromItemStack(stack: ItemStack?, pass: Int): Int {
		return super.getColorFromItemStack(stack, pass)
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		icons = Array(2) { IconHelper.forItem(reg, this, it) }
	}
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(stack: ItemStack, pass: Int) = icons[pass]
	
	override fun requiresMultipleRenderPasses() = true
	
	override fun getRenderPasses(meta: Int) = 2
	
	private var ItemStack.cooldown
		get() = getInt(this, TAG_COOLDOWN, 0)
		set(value) = setInt(this, TAG_COOLDOWN, value)
	
	companion object {
		
		const val CHARGE_PER_TICK = 1000
		const val MAX_CHARGE = 10000
		
		const val TAG_COOLDOWN = "cooldown"
		
		const val TAG_CHARGE = "charge"
		const val TAG_LIGHTNING_SEED = "lightningSeed"
		
		const val TAG_SHAKE_TIMER = "shakeTimer"
		const val TAG_SHAKE_X = "shakeX"
		const val TAG_SHAKE_Y = "shakeY"
		const val TAG_SHAKE_Z = "shakeZ"
		
		lateinit var icons: Array<IIcon>
		
		fun addCharge(stack: ItemStack?, charge: Int) {
			setInt(stack!!, TAG_CHARGE, getCharge(stack) + charge)
		}
		
		fun setCharge(stack: ItemStack?, charge: Int) {
			setInt(stack!!, TAG_CHARGE, charge)
		}
		
		fun getCharge(stack: ItemStack?) = getInt(stack, TAG_CHARGE, 0)
	}
}