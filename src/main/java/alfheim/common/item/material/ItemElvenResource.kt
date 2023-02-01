package alfheim.common.item.material

import alexsocol.asjlib.*
import alfheim.AlfheimCore
import alfheim.api.ModInfo
import alfheim.client.core.helper.*
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.rainbow.BlockRainbowGrass
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem
import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem.Knowledge
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.item.*
import alfheim.common.item.material.ElvenResourcesMetas.*
import alfheim.common.item.material.ElvenResourcesMetas.Companion.of
import alfheim.common.world.dim.niflheim.ChunkProviderNiflheim
import cpw.mods.fml.common.IFuelHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.*
import net.minecraft.init.*
import net.minecraft.inventory.IInventory
import net.minecraft.item.*
import net.minecraft.potion.*
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import vazkii.botania.api.recipe.*
import vazkii.botania.common.Botania
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.entity.EntityDoppleganger
import java.awt.Color
import kotlin.math.sin

class ItemElvenResource: ItemMod("ElvenItems"), IElvenItem, IFlowerComponent, IFuelHandler {
	
	val texture = arrayOfNulls<IIcon>(values().size)
	
	init {
		setHasSubtypes(true)
		if (ASJUtilities.isClient)
			MinecraftForge.EVENT_BUS.register(this)
		
		GameRegistry.registerFuelHandler(this)
	}
	
	override fun getRenderPasses(meta: Int) =
		when (meta) {
			ElvenWeed.I -> 2
			else        -> 1
		}
	
	override fun requiresMultipleRenderPasses() = true
	
	override fun isElvenItem(stack: ItemStack) = stack.meta == InterdimensionalGatewayCore.I
	
	fun isInterpolated(meta: Int) = when (of(meta)) {
		ThunderwoodTwig, NetherwoodCoal, RainbowQuartz, Nifleur -> true
		else                                                    -> false
	}
	
	fun isFlowerComponent(meta: Int) = when (of(meta)) {
		NetherwoodCoal, RainbowPetal, IffesalDust -> true
		else                                      -> false
	}
	
	override fun canFit(stack: ItemStack, inventory: IInventory) = isFlowerComponent(stack.meta)
	
	override fun getParticleColor(stack: ItemStack): Int {
		return when (of(stack.meta)) {
			NetherwoodCoal -> 0x6B2406
			IffesalDust    -> 0x0519E2
			RainbowPetal   -> ItemIridescent.rainbowColor()
			else           -> 0xFFFFFF
		}
	}
	
	override fun getColorFromItemStack(stack: ItemStack, pass: Int) =
		if ((stack.meta == ElvenWeed.I && pass == 1) || stack.meta == RiftShardEmpty.I)
			Color.HSBtoRGB(Botania.proxy.worldElapsedTicks * 2 % 360 / 360f, 0.25f, 1f)
		else if (stack.meta == RiftShardGinnungagap.I)
			Color.HSBtoRGB(0f, 0f, (sin(Botania.proxy.worldElapsedTicks / 36.0).F + 1) / 20 + 0.05F)
		else if (stack.meta == RiftShardMuspelheim.I)
			Color.HSBtoRGB(0.05f, (sin(Botania.proxy.worldElapsedTicks / 36.0).F + 1) / 8 + 0.75f, 1f)
		else if (stack.meta == RiftShardNiflheim.I)
			Color.HSBtoRGB(2/3f, (sin(Botania.proxy.worldElapsedTicks / 36.0).F + 1) / 8 + 0.75f, 1f)
		else if (stack.meta == RainbowPetal.I || stack.meta == RainbowDust.I)
			ItemIridescent.rainbowColor()
		else
			super.getColorFromItemStack(stack, pass)
	
	val riftIcons = arrayOf(RiftShardGinnungagap.I, RiftShardMuspelheim.I, RiftShardNiflheim.I)
	
	override fun registerIcons(reg: IIconRegister) {
		for (type in values())
			if (!isInterpolated(type.I) && type.I !in riftIcons)
				texture[type.I] = IconHelper.forName(reg, type.toString(), "materials")
		
		for (meta in riftIcons)
			texture[meta] = texture[RiftShardEmpty.I]
		
		amulet = reg.registerIcon(ModInfo.MODID + ":misc/amulet")
		candy = IconHelper.forName(reg, "CandyCane", "materials")
		flugel = reg.registerIcon(ModInfo.MODID + ":misc/flugelBack")
		kitty = reg.registerIcon(ModInfo.MODID + ":misc/kitty")
		harp = reg.registerIcon(ModInfo.MODID + ":misc/harp")
		mine = reg.registerIcon(ModInfo.MODID + ":misc/mine")
		wind = reg.registerIcon(ModInfo.MODID + ":misc/wind")
		wing = reg.registerIcon(ModInfo.MODID + ":misc/wing")
		
		weed1 = IconHelper.forName(reg, "materials/${ElvenWeed}1")
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 1)
			for (type in values())
				if (isInterpolated(type.I))
					texture[type.I] = InterpolatedIconHelper.forName(event.map, type.toString(), "materials")
	}
	
	override fun getIcon(stack: ItemStack, pass: Int) =
		if (stack.meta == ElvenWeed.I && pass == 1)
			weed1
		else if (stack.meta in riftIcons)
			texture[RiftShardEmpty.I]
		else if (AlfheimCore.jingleTheBells && stack.meta == InfusedDreamwoodTwig.I)
			candy
		else
			texture.safeGet(stack.meta)
	
	override fun getUnlocalizedName(stack: ItemStack) =
		if (AlfheimCore.jingleTheBells && stack.meta == InfusedDreamwoodTwig.I)
			"item.InfusedCandy"
		else
			"item.${of(stack.meta).toString()}"
	
	override fun getSubItems(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (type in values())
			if (type !in ElvenResourcesMetas.displayBlackList)
				list.add(type.stack)
	}
	
	override fun onLeftClickEntity(stack: ItemStack, player: EntityPlayer, target: Entity): Boolean {
		return if (stack.meta == DasRheingold.I && target is EntityPlayer)
			ItemNBTHelper.setString(stack, "nick", target.commandSenderName).let { true }
		else
			super.onLeftClickEntity(stack, player, target)
	}
	
	val ids = arrayOf(Potion.moveSpeed.id, Potion.regeneration.id, Potion.jump.id, Potion.hunger.id, Potion.confusion.id)
	
	val usable = arrayOf(ElvenWeed.I, WisdomBottle.I, YggFruit.I)
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (stack.meta in usable) {
			if (stack.meta == WisdomBottle.I && (!RagnarokHandler.ginnungagap || player is EntityPlayerMP && KnowledgeSystem.know(player, Knowledge.ABYSS_TRUTH))) return stack
			player.setItemInUse(stack, getMaxItemUseDuration(stack))
		} else
		// rift shard filling
		if (stack.meta == RiftShardEmpty.I) {
			if (!RagnarokHandler.ginnungagap || player !is EntityPlayerMP || !KnowledgeSystem.know(player, Knowledge.ABYSS_TRUTH)) return stack
			
			val mop = ASJUtilities.getSelectedBlock(player, player.theItemInWorldManager.blockReachDistance, true)
			if (mop?.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return stack
			val (x, y, z) = intArrayOf(mop.blockX, mop.blockY, mop.blockZ)
			
			stack.meta = when (player.dimension) {
				-1 -> {
					if (y > 31) return stack
					
					for (i in x.bidiRange(5))
						for (j in (y - 5)..y)
							for (k in z.bidiRange(5))
								if (world.getBlock(i, j, k) != Blocks.lava)
									return stack
					
					RiftShardMuspelheim.I
				}
				AlfheimConfigHandler.dimensionIDNiflheim -> {
					if (y != 127 || ChunkProviderNiflheim.f(x) !in z.bidiRange(6)) return stack
					
					RiftShardNiflheim.I
				}
				!in emptyArray<Int>() -> { // bruh
					if (world.getBlock(x, y, z) !== AlfheimBlocks.rift) return stack
					
					RiftShardGinnungagap.I
				}
				else -> return stack
			}
		}
		
		return stack
	}
	
	override fun getMaxItemUseDuration(stack: ItemStack) = if (stack.meta in usable) 40 else 0
	
	override fun getItemUseAction(stack: ItemStack) = when (of(stack.meta)) {
		ElvenWeed    -> EnumAction.bow
		WisdomBottle -> EnumAction.drink
		YggFruit     -> EnumAction.eat
		else         -> EnumAction.none
	}
	
	override fun onEaten(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (ASJUtilities.isClient || player !is EntityPlayerMP) return stack
		
		when (of(stack.meta)) {
			ElvenWeed -> for (i in ids) player.addPotionEffect(PotionEffect(i, 600))
			WisdomBottle -> {
				if (!RagnarokHandler.ginnungagap) return stack
				
				val usages = ItemNBTHelper.getInt(stack, TAG_USAGES, 0) + 1
				CardinalSystem.forPlayer(player).wisdom = usages
				ItemNBTHelper.setInt(stack, TAG_USAGES, usages)
				return if (usages >= 3) ItemStack(Items.glass_bottle) else stack
			}
			YggFruit -> CardinalSystem.CommonSystem.loseHearts(player, -1)
			else -> Unit
		}
		
		stack.stackSize--
		return stack
	}
	
	override fun hasEffect(stack: ItemStack, pass: Int) = stack.meta == WisdomBottle.I || stack.meta == YggFruit.I
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer?, tooltip: MutableList<Any?>, advanced: Boolean) {
		if (stack.meta == DomainKey.I) addStringToTooltip(tooltip, "alfheimmisc.creative")
	}
	
	override fun getItemStackLimit(stack: ItemStack) = if (stack.meta == WisdomBottle.I || stack.meta == DomainKey.I) 1 else 64
	
	override fun onItemUse(stack: ItemStack, player: EntityPlayer?, world: World, x: Int, y: Int, z: Int, side: Int, par8: Float, par9: Float, par10: Float): Boolean {
		val block = world.getBlock(x, y, z)
		// Fabulous manapool
		if (block === ModBlocks.pool && world.getBlockMetadata(x, y, z) == 0 && stack.meta == RainbowDust.I) {
			world.setBlockMetadataWithNotify(x, y, z, 3, 2)
			stack.stackSize--
			return true
		} else
		// Rainbow flower
		if (block === ModBlocks.flower && stack.meta == RainbowDust.I) {
			world.setBlock(x, y, z, AlfheimBlocks.rainbowGrass, BlockRainbowGrass.FLOWER, 3)
			for (i in 0..40) {
				val color = Color.getHSBColor(Math.random().F + 1f / 2f, 1f, 1f)
				Botania.proxy.wispFX(world,
									 x.D + Math.random(), y.D + Math.random(), z.D + Math.random(),
									 color.red / 255f, color.green / 255f, color.blue / 255f,
									 0.5f, 0f, 0.125f, 0f)
			}
			world.playSoundEffect(x.D, y.D, z.D, "botania:enchanterEnchant", 1f, 1f)
			stack.stackSize--
			return true
		} else
		// Burying petal
		if (side == 1 && AlfheimBlocks.rainbowGrass.canBlockStay(world, x, y + 1, z) && stack.meta == RainbowPetal.I) {
			world.setBlock(x, y + 1, z, AlfheimBlocks.rainbowGrass, BlockRainbowGrass.BURIED, 3)
			stack.stackSize--
			return true
		} else
		// summon Gaia in Alfheim
		if (block === Blocks.beacon && stack.meta == ElvoriumIngot.I) {
			return if (world.provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim) {
				EntityDoppleganger.spawn(player, stack, world, x, y, z, false)
			} else {
				if (!world.isRemote) ASJUtilities.say(player, "alfheimmisc.gaia.wrongitem")
				false
			}
		}
		return false
	}
	
	override fun getBurnTime(fuel: ItemStack): Int {
		if (fuel.item === AlfheimItems.elvenResource) {
			return when (of(fuel.meta)) {
				InfusedDreamwoodTwig, ThunderwoodTwig     -> 600 // 2
				NetherwoodTwig                            -> 4000 // 20
				MuspelheimEssence                         -> 12800 // 64
				NetherwoodSplinters, ThunderwoodSplinters -> 100 // 0.5
				NetherwoodCoal                            -> 2400 // 12
				else                                      -> 0
			}
		}
		return 0
	}
	
	companion object {
		
		lateinit var amulet: IIcon
		lateinit var candy: IIcon
		lateinit var flugel: IIcon
		lateinit var harp: IIcon
		lateinit var kitty: IIcon
		lateinit var mine: IIcon
		lateinit var wind: IIcon
		lateinit var wing: IIcon
		
		lateinit var weed1: IIcon
		
		const val TAG_USAGES = "usages"
	}
}

enum class ElvenResourcesMetas {
	
	InterdimensionalGatewayCore,
	ManaInfusionCore,
	DasRheingold,
	ElvoriumIngot,
	MauftriumIngot,
	MuspelheimPowerIngot,
	NiflheimPowerIngot,
	ElvoriumNugget,
	MauftriumNugget,
	MuspelheimEssence,
	NiflheimEssence,
	RainbowQuartz,
	RainbowPetal,
	RainbowDust,
	IffesalDust,
	PrimalRune,
	MuspelheimRune,
	NiflheimRune,
	InfusedDreamwoodTwig,
	ThunderwoodTwig,
	NetherwoodTwig,
	ThunderwoodSplinters,
	NetherwoodSplinters,
	NetherwoodCoal,
	ElvenWeed,
	Jug,
	GrapeLeaf,
	FenrirFur,
	WisdomBottle,
	Nifleur,
	YggFruit,
	RiftShardEmpty,
	RiftShardGinnungagap,
	RiftShardMuspelheim,
	RiftShardNiflheim,
	DomainKey,
	;
	
	val I get() = ordinal
	
	val stack get() = ItemStack(AlfheimItems.elvenResource, 1, I)
	
	fun stack(size: Int) = ItemStack(AlfheimItems.elvenResource, size, I)
	
	companion object {
		
		val displayBlackList = arrayOf(ElvenWeed, WisdomBottle)
		
		fun of(meta: Int) = values().getOrNull(meta)
	}
}