package alfheim.common.item

import alexsocol.asjlib.*
import cpw.mods.fml.relauncher.*
import net.minecraft.block.BlockLiquid
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.world.World

class ItemSpawnEgg: ItemMod("SpawnEgg") {
	
	lateinit var icons: Array<IIcon>
	
	init {
		creativeTab = CreativeTabs.tabMisc
		hasSubtypes = true
	}
	
	/**
	 * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
	 * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
	 */
	override fun onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (world.isRemote) return true
		
		var i = x
		var j = y
		var k = z
		
		val block = world.getBlock(i, j, k)
		i += Facing.offsetsXForSide[side]
		j += Facing.offsetsYForSide[side]
		k += Facing.offsetsZForSide[side]
		
		var yOff = 0.0
		if (side == 1 && block.renderType == 11) yOff = 0.5
		
		val entity = spawnCreature(world, stack.getItemDamage(), i.D + 0.5, j.D + yOff, k.D + 0.5) ?: return false
		
		if (entity is EntityLivingBase && stack.hasDisplayName()) (entity as EntityLiving).customNameTag = stack.displayName
		
		if (!player.capabilities.isCreativeMode) --stack.stackSize
		
		return true
	}
	
	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (world.isRemote) return stack
		
		val mop = getMovingObjectPositionFromPlayer(world, player, true) ?: return stack
		if (mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return stack
		
		val i = mop.blockX
		val j = mop.blockY
		val k = mop.blockZ
		
		if (!world.canMineBlock(player, i, j, k))
			return stack
		
		if (!player.canPlayerEdit(i, j, k, mop.sideHit, stack))
			return stack
		
		if (world.getBlock(i, j, k) !is BlockLiquid) return stack
		
		val entity = spawnCreature(world, stack.getItemDamage(), i.D, j.D, k.D) ?: return stack
		
		if (entity is EntityLivingBase && stack.hasDisplayName())
			(entity as EntityLiving).customNameTag = stack.displayName
		
		if (!player.capabilities.isCreativeMode)
			--stack.stackSize
		
		return stack
	}
	
	/**
	 * Spawns the creature specified by the egg's type in the location specified by the last three parameters.
	 * Parameters: world, entityID, x, y, z.
	 */
	fun spawnCreature(world: World, id: Int, x: Double, y: Double, z: Double): Entity? {
		if (id !in mappings.indices) return null
		
		val entity = EntityList.createEntityByName(EntityList.classToStringMapping[mappings[id].first].toString(), world) ?: return null
		
		entity.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0f), 0.0f)
		
		if (entity is EntityLivingBase) {
			entity.rotationYawHead = entity.rotationYaw
			entity.renderYawOffset = entity.rotationYaw
		}
		
		if (entity is EntityLiving) {
			entity.onSpawnWithEgg(null as IEntityLivingData?)
			entity.playLivingSound()
		}
		
		entity.spawn()
		
		return entity
	}
	
	override fun getSubItems(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (i in mappings.indices) list.add(ItemStack(item, 1, i))
	}
	
	override fun getItemStackDisplayName(stack: ItemStack): String {
		val s = Items.spawn_egg.getItemStackDisplayName(ItemStack(Items.spawn_egg))
		val s1 = EntityList.classToStringMapping[mappings.getOrNull(stack.getItemDamage())?.first] ?: return s
		return "$s ${StatCollector.translateToLocal("entity.$s1.name")}"
	}
	
	override fun registerIcons(reg: IIconRegister) = Unit
	
	override fun getIconFromDamageForRenderPass(meta: Int, pass: Int) = Items.spawn_egg.getIconFromDamageForRenderPass(meta, pass)
	
	@SideOnly(Side.CLIENT)
	override fun requiresMultipleRenderPasses() = true
	
	@SideOnly(Side.CLIENT)
	override fun getColorFromItemStack(stack: ItemStack, pass: Int): Int {
		val mapping = mappings.getOrNull(stack.meta)
		val color = if (pass == 0) mapping?.second ?: 0xFFFFFF else mapping?.third ?: 0x0
		if (color == -1) return ItemIridescent.rainbowColor()
		return color
	}
	
	companion object {
		
		val mappings = ArrayList<Triple<Class<out Entity>, Int, Int>>()
		
		fun addMapping(clazz: Class<out Entity>, color1: Int, color2: Int) {
			mappings.add(clazz to color1 with color2)
		}
	}
}
