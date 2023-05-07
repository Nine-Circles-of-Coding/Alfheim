package alfheim.common.item.equipment.bauble.faith

import alexsocol.asjlib.*
import alexsocol.asjlib.ItemNBTHelper.getBoolean
import alexsocol.asjlib.ItemNBTHelper.getByteArray
import alexsocol.asjlib.ItemNBTHelper.getInt
import alexsocol.asjlib.ItemNBTHelper.getIntArray
import alexsocol.asjlib.ItemNBTHelper.setBoolean
import alexsocol.asjlib.ItemNBTHelper.setInt
import alexsocol.asjlib.ItemNBTHelper.setIntArray
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.AlfheimCore
import alfheim.api.ModInfo
import alfheim.api.event.PlayerInteractAdequateEvent
import alfheim.api.item.equipment.bauble.IManaDiscountBauble
import alfheim.client.core.handler.CardinalSystemClient
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.alt.BlockAltLeaves
import alfheim.common.core.handler.*
import alfheim.common.core.handler.ragnarok.*
import alfheim.common.core.handler.ragnarok.RagnarokHandler.timesDied
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.equipment.bauble.*
import alfheim.common.item.material.ElvenResourcesMetas
import alfheim.common.item.relic.*
import alfheim.common.world.dim.niflheim.ChunkProviderNiflheim
import baubles.api.BaubleType
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.relauncher.*
import net.minecraft.block.*
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.*
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.entity.player.*
import net.minecraft.init.*
import net.minecraft.item.*
import net.minecraft.potion.Potion
import net.minecraft.util.*
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.event.entity.EntityStruckByLightningEvent
import net.minecraftforge.event.entity.living.*
import org.lwjgl.opengl.GL11
import travellersgear.api.TravellersGearAPI
import vazkii.botania.api.item.IBaubleRender
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.api.subtile.ISpecialFlower
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.core.handler.ConfigHandler
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.equipment.bauble.ItemBauble
import vazkii.botania.common.item.relic.*

class ItemRagnarokEmblem: ItemBauble("aesirEmblemWeak"), IBaubleRender, IManaDiscountBauble {
	
	init {
		creativeTab = AlfheimTab
		setHasSubtypes(true)
	}
	
	val godRelics: Array<Item> by lazy { arrayOf(AlfheimItems.mjolnir, ModItems.infiniteFruit, AlfheimItems.daolos, AlfheimItems.gleipnir, AlfheimItems.gjallarhorn, AlfheimItems.gungnir,
	                                             ModItems.thorRing, AlfheimItems.priestRingSif, AlfheimItems.priestRingNjord, ModItems.lokiRing, AlfheimItems.priestRingHeimdall, ModItems.odinRing, ModItems.aesirRing) }
	
	override fun canEquip(stack: ItemStack, player: EntityLivingBase): Boolean {
		if (player !is EntityPlayer) return false
		
		return if (RagnarokHandler.winter || RagnarokHandler.ragnarok) {
			player.timesDied < 5
		} else true
	}
	
	override fun onEquippedOrLoadedIntoWorld(stack: ItemStack, player: EntityLivingBase) {
		if (stack.hasSoul(0)) FaithHandlerThor.onEquipped(stack, player as? EntityPlayer ?: return, IFaithHandler.FaithBauble.EMBLEM)
	}
	
	override fun onWornTick(stack: ItemStack, player: EntityLivingBase?) {
		if (player !is EntityPlayer) return
		
		if (player.timesDied >= 5) {
			
			PlayerHandler.getPlayerBaubles(player)[0] = null
			
			if (!player.inventory.addItemStackToInventory(stack))
				player.dropPlayerItemWithRandomChoice(stack, false)
			
			return
		}
		
		super.onWornTick(stack, player)
		
		if (stack.hasSoul(1))
			doReversedSif(stack, player)
		
		if (stack.hasSoul(2))
			doReversedNjord(stack, player)
		
		if (stack.hasSoul(3))
			FaithHandlerLoki.onWornTick(stack, player, IFaithHandler.FaithBauble.EMBLEM)
		
		if (stack.hasSoul(4) && !player.worldObj.isRemote && ManaItemHandler.requestManaExact(stack, player, 1, !player.worldObj.isRemote)) {
			if (player.dimension != AlfheimConfigHandler.dimensionIDHelheim)
				player.addPotionEffect(PotionEffectU(Potion.nightVision.id, 10))
			player.removePotionEffect(Potion.blindness.id)
		}
		
		if (ASJUtilities.isClient) return
		
		if (ItemThorRing.getThorRing(player) != null ||
			ItemSifRing.getSifRing(player) != null ||
			ItemNjordRing.getNjordRing(player) != null ||
			ItemLokiRing.getLokiRing(player) != null ||
			ItemHeimdallRing.getHeimdallRing(player) != null ||
			ItemOdinRing.getOdinRing(player) != null ||
			godRelics.any {
				val slot = ASJUtilities.getSlotWithItem(it, player.inventory)
				if (slot == -1) return@any false
				val relic = player.inventory[slot]
				relic != null && ItemRelic.isRightPlayer(player, relic)
			}) {
			
			stack.instability++
			
			when {
				stack.instability == 600  -> ASJUtilities.say(player, "alfheimmisc.ragnarok.instability1")
				stack.instability == 1200 -> ASJUtilities.say(player, "alfheimmisc.ragnarok.instability2")
				stack.instability == 1800 -> ASJUtilities.say(player, "alfheimmisc.ragnarok.instability3")
				
				stack.instability >= 2400 -> {
					ASJUtilities.say(player, "alfheimmisc.ragnarok.instabilityS")
					
					PlayerHandler.getPlayerBaubles(player)[0] = null
				}
			}
		}
		
		for (meta in 0 until ItemPriestEmblem.TYPES) {
			ItemPriestCloak.getCloak(meta, player) ?: continue
			
			stack.instability++
			
			when {
				stack.instability == 600  -> ASJUtilities.say(player, "alfheimmisc.ragnarok.instability1")
				stack.instability == 1200 -> ASJUtilities.say(player, "alfheimmisc.ragnarok.instability2")
				stack.instability == 1800 -> ASJUtilities.say(player, "alfheimmisc.ragnarok.instability3")
				
				stack.instability >= 2400 -> {
					ASJUtilities.say(player, "alfheimmisc.ragnarok.instabilityO")
					
					if (AlfheimCore.TravellersGearLoaded) {
						val tg = TravellersGearAPI.getExtendedInventory(player)
						tg[0] = null
						TravellersGearAPI.setExtendedInventory(player, tg)
					} else {
						PlayerHandler.getPlayerBaubles(player)[3] = null
					}
					
					stack.instability = 0
				}
			}
		}
		
		goHeimdall(stack, player)
		goNjord(stack, player)
		goLoki(stack, player)
		goSif(stack, player)
		goOdin(stack, player)
	}
	
	override fun canUnequip(stack: ItemStack?, player: EntityLivingBase?): Boolean {
		return !(RagnarokHandler.winter || RagnarokHandler.ragnarok)
	}
	
	override fun onUnequipped(stack: ItemStack, player: EntityLivingBase) {
		if (stack.hasSoul(0))
			FaithHandlerThor.onUnequipped(stack, player as? EntityPlayer ?: return, IFaithHandler.FaithBauble.EMBLEM)
	}
	
	fun doReversedSif(stack: ItemStack, player: EntityPlayer) {
		if (player.rng.nextInt(120) != 0) return
		if (!ManaItemHandler.requestManaExact(stack, player, 10, true)) return
		
		val (srcx, srcy, srcz) = Vector3.fromEntity(player).mf()
		val world = player.worldObj
		
		val range = 8
		val rangeY = 4
		val coords: MutableList<ChunkCoordinates?> = ArrayList()
		
		for (i in -range..range) {
			for (j in -rangeY..rangeY) {
				for (k in -range..range) {
					val x = srcx + i
					val y = srcy + j
					val z = srcz + k
					
					val block = world.getBlock(x, y, z)
					
					if (!(block is BlockBush || block is ISpecialFlower || block.isLeaves(world, x, y, z)))
						continue
					
					coords.add(ChunkCoordinates(x, y, z))
				}
			}
		}
		
		if (coords.isEmpty()) return
		
		val currCoords = coords.random()!!
		val block = world.getBlock(currCoords.posX, currCoords.posY, currCoords.posZ)
		val meta = world.getBlockMetadata(currCoords.posX, currCoords.posY, currCoords.posZ)
		val items = block.getDrops(world, currCoords.posX, currCoords.posY, currCoords.posZ, meta, 0)
		
		if (!world.isRemote) {
			world.setBlockToAir(currCoords.posX, currCoords.posY, currCoords.posZ)
			
			if (ConfigHandler.blockBreakParticles)
				world.playAuxSFX(2001, currCoords.posX, currCoords.posY, currCoords.posZ, Block.getIdFromBlock(block) + (meta shl 12))
			
			items.forEach { EntityItem(world, currCoords.posX.toDouble() + 0.5, currCoords.posY.toDouble() + 0.5, currCoords.posZ.toDouble() + 0.5, it).spawn() }
		}
	}
	
	fun doReversedNjord(stack: ItemStack, player: EntityPlayer) {
		if (player.rng.nextInt(60) != 0) return
		if (!ManaItemHandler.requestManaExact(stack, player, 10, true)) return
		
		val (srcx, srcy, srcz) = Vector3.fromEntity(player).mf()
		val world = player.worldObj
		
		val range = 8
		val rangeY = 4
		val coords: MutableList<ChunkCoordinates?> = ArrayList()
		
		for (i in -range..range) {
			for (j in -rangeY..rangeY) {
				for (k in -range..range) {
					val x = srcx + i
					val y = srcy + j
					val z = srcz + k
					
					val block = world.getBlock(x, y, z)
					
					if (!(block === Blocks.fire || block === Blocks.flowing_lava || block === Blocks.lava))
						continue
					
					coords.add(ChunkCoordinates(x, y, z))
				}
			}
		}
		
		if (coords.isEmpty()) return
		
		val currCoords = coords.random()!!
		val block = world.getBlock(currCoords.posX, currCoords.posY, currCoords.posZ)
		val meta = world.getBlockMetadata(currCoords.posX, currCoords.posY, currCoords.posZ)
		
		if (!world.isRemote) {
			if (block === Blocks.lava)
				world.setBlock(currCoords.posX, currCoords.posY, currCoords.posZ, Blocks.cobblestone)
			else
				world.setBlockToAir(currCoords.posX, currCoords.posY, currCoords.posZ)
			
			if (ConfigHandler.blockBreakParticles)
				world.playAuxSFX(2001, currCoords.posX, currCoords.posY, currCoords.posZ, Block.getIdFromBlock(block) + (meta shl 12))
		}
	}
	
	private var ItemStack.instability
		get() = getInt(this, TAG_INSTABILITY, 0)
		set(value) = setInt(this, TAG_INSTABILITY, value)
	
	override fun getSubItems(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>?) {
		if (canSeeTruth(mc.thePlayer)) super.getSubItems(item, tab, list)
	}
	
	override fun getDiscount(stack: ItemStack, slot: Int, player: EntityPlayer): Float {
		return getByteArray(stack, TAG_CONSUMED, ByteArray(6)).sum() / 6f * 0.25f
	}
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack) = if (ASJUtilities.isClient && !canSeeTruth(mc.thePlayer)) AlfheimItems.aesirEmblem.getUnlocalizedNameInefficiently(stack)
		else super.getUnlocalizedNameInefficiently(stack).replace("item\\.botania:".toRegex(), "item.${ModInfo.MODID}:")
	
	override fun getItemStackDisplayName(stack: ItemStack) = if (ASJUtilities.isClient && !canSeeTruth(mc.thePlayer)) AlfheimItems.aesirEmblem.getItemStackDisplayName(stack)
		else super.getItemStackDisplayName(stack).replace("&".toRegex(), "\u00a7")
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) = Unit
	
	override fun getIcon(stack: ItemStack, pass: Int): IIcon {
		val truth = canSeeTruth(mc.thePlayer)
		return if (getBoolean(stack, TAG_GEM_FLAG, false)) {
			if (truth) ItemRagnarokEmblemF.gemIcons.safeGet(pass) else ItemAesirEmblem.baubleIcon
		} else {
			(if (truth) AlfheimItems.ragnarokEmblemF else AlfheimItems.aesirEmblem).getIcon(stack, pass)
		}
	}
	
	override fun requiresMultipleRenderPasses() = true
	
	override fun getBaubleType(stack: ItemStack) = BaubleType.AMULET
	
	override fun onPlayerBaubleRender(stack: ItemStack?, event: RenderPlayerEvent, type: IBaubleRender.RenderType?) {
		if (type != IBaubleRender.RenderType.BODY) return
		
		val player = event.entityPlayer
		val truth = canSeeTruth(mc.thePlayer)
		
		if (truth && (player != mc.thePlayer || mc.gameSettings.thirdPersonView != 0)) {
			val invert = player.rng.nextBoolean()
			val (mx, my, mz) = Vector3().rand().sub(0.5).normalize().mul(0.1)
			val (x, y, z) = Vector3.fromEntity(player).add(0, player.height * 0.6875, 0)
			
			if (invert)
				mc.theWorld.spawnParticle("smoke", x + mx * 10, y + my * 10, z + mz * 10, -mx, -my, -mz)
			else
				mc.theWorld.spawnParticle("smoke", x, y, z, mx, my, mz)
		}
		
		mc.renderEngine.bindTexture(TextureMap.locationItemsTexture)
		IBaubleRender.Helper.rotateIfSneaking(player)
		val armor = player.getCurrentArmor(2) != null
		GL11.glTranslatef(-15 / 64f, 0f, -1 * if (armor) 0.2F else 0.125F)
		glScalef(0.5f)
		
		if (truth) for ((id, icon) in ItemRagnarokEmblemF.gemIcons.withIndex()) {
			if (id != 0) ASJRenderHelper.setGlow()
			ItemRenderer.renderItemIn2D(Tessellator.instance, icon.maxU, icon.maxV, icon.minU, icon.minV, icon.iconWidth, icon.iconHeight, 1F / 32F)
			if (id != 0) ASJRenderHelper.discard()
		} else {
			GL11.glTranslatef(-1/32f, -13f/64f, 0f)
			val icon = ItemAesirEmblem.baubleIcon
			ItemRenderer.renderItemIn2D(Tessellator.instance, icon.maxU, icon.maxV, icon.minU, icon.minV, icon.iconWidth, icon.iconHeight, 1F / 32F)
		}
	}
	
	override fun hasPhantomInk(stack: ItemStack?) = false
	override fun setPhantomInk(stack: ItemStack?, ink: Boolean) = Unit
	
	companion object {
		
		const val TAG_CONSUMED = "consumedPowers"
		const val TAG_GEM_FLAG = "renderGem"
		const val TAG_INSTABILITY = "instability"
		
		const val TAG_STOLEN = "${ModInfo.MODID}:ragnarok.stolen"
		
		init {
			eventForge()
		}
		
		fun canSeeTruth(player: EntityPlayer?): Boolean {
			return RagnarokHandler.finished || ItemPriestEmblem.getEmblem(4, player) != null ||
					if (player is EntityPlayerMP)
						CardinalSystem.KnowledgeSystem.know(player, CardinalSystem.KnowledgeSystem.Knowledge.ABYSS_TRUTH)
					else
						CardinalSystem.KnowledgeSystem.Knowledge.ABYSS_TRUTH.toString() in CardinalSystemClient.PlayerSegmentClient.knowledge
		}
		
		private fun ItemStack.hasSoul(meta: Int): Boolean = getByteArray(this, TAG_CONSUMED, ByteArray(6))[meta] > 0
		
		fun getEmblem(player: EntityPlayer?, meta: Int = -1): ItemStack? {
			val baubles = PlayerHandler.getPlayerBaubles(player ?: return null)
			val stack = baubles[0] ?: return null
			if (stack.item !== AlfheimItems.ragnarokEmblem) return null
			if (meta != -1 && !stack.hasSoul(meta)) return null
			return stack
		}
		
		@SubscribeEvent
		fun onLivingHurt(e: LivingHurtEvent) {
			if (e.source.damageType == "player") return
			
			val player = e.source.entity as? EntityPlayer ?: return
			if (getEmblem(player, 4) != null && Math.random() > 0.25)
				e.ammount *= 1.5f
			
			if (getEmblem(player, 5) != null)
				e.ammount *= 1 + (1 - player.health / player.maxHealth) * 0.5f
		}
		
		@SubscribeEvent
		fun stealFromVillagers(e: PlayerInteractAdequateEvent.RightClick) {
			val target = e.entity as? EntityVillager ?: return
			if (e.player.heldItem != null || !e.player.isSneaking) return
			if (target.entityData.getBoolean(TAG_STOLEN)) return
			if (getEmblem(e.player) == null) return
			
			val stack = if (target.isChild) ElvenResourcesMetas.ElvenWeed.stack else ItemStack(Items.emerald)
			if (!e.player.inventory.addItemStackToInventory(stack))
				e.player.dropPlayerItemWithRandomChoice(stack, true)
			
			target.entityData.setBoolean(TAG_STOLEN, true)
		}
		
		@SubscribeEvent
		fun restoreStolenVillager(e: LivingEvent.LivingUpdateEvent) {
			val target = e.entity as? EntityVillager ?: return
			if (!target.entityData.getBoolean(TAG_STOLEN)) return
			if (target.worldObj.rand.nextInt(10000) != 0) return
			target.entityData.setBoolean(TAG_STOLEN, false)
		}
		
		private var ItemStack.HEIMDAL
			get() = getBoolean(this, "HEIMDAL", false)
			set(v) = setBoolean(this, "HEIMDAL", v)
		
		private var ItemStack.NJORD
			get() = getBoolean(this, "NJORD", false)
			set(v) = setBoolean(this, "NJORD", v)
		
		private var ItemStack.THOR
			get() = getBoolean(this, "THOR", false)
			set(v) = setBoolean(this, "THOR", v)
		
		private var ItemStack.LOKI
			get() = getBoolean(this, "LOKI", false)
			set(v) = setBoolean(this, "LOKI", v)
		
		private var ItemStack.SIF
			get() = getBoolean(this, "SIF", false)
			set(v) = setBoolean(this, "SIF", v)
		
		private var ItemStack.start: Vector3I
			get() = Vector3I(getIntArray(this, "heimdal_start", intArrayOf(-1, -1, -1)))
			set(v) = setIntArray(this, "heimdal_start", v.ints)
		
		fun goHeimdall(stack: ItemStack, player: EntityPlayer) {
			if (stack.HEIMDAL) return
			
			if (player is EntityPlayerMP) {
				if (!CardinalSystem.KnowledgeSystem.know(player, CardinalSystem.KnowledgeSystem.Knowledge.ABYSS_TRUTH))
					return
			}
			
			if ((0..5).any { !stack.hasSoul(it) }) return
			
			val (x, y, z) = Vector3.fromEntity(player).mf()
			val world = player.worldObj
			val block1 = world.getBlock(x, y - 1, z)
			val block2 = world.getBlock(x, y - 2, z)
			
			val (sx, sy, sz) = stack.start
			if (sy != -1 && (player.capabilities.isFlying || player.isSprinting)) {
				stack.start = Vector3I(-1, -1, -1)
				return
			}
			
			if (block1 === ModBlocks.bifrost || block2 === ModBlocks.bifrost) {
				if (sy == -1) {
					stack.start = Vector3I(x, y, z)
					return
				}
			} else {
				if (sy == -1) return
				stack.start = Vector3I(-1, -1, -1)
			}
			
			val distance = Vector3.pointDistanceSpace(x, y, z, sx, sy, sz)
			if (distance < 1000.0) return
			
			ASJUtilities.say(player, "alfheimmisc.ragnarok.forgiven0")
			stack.HEIMDAL = true
		}
		
		private var ItemStack.waterTicks: Int
			get() = getInt(this, "water_ticks", 0)
			set(v) = setInt(this, "water_ticks", v)
		
		fun goNjord(stack: ItemStack, player: EntityPlayer) {
			if (!stack.HEIMDAL || stack.NJORD) return
			
			fun check(): Boolean {
				if (player.dimension != AlfheimConfigHandler.dimensionIDNiflheim) return false
				if (player.isPotionActive(Potion.waterBreathing)) return false
				
				val x = player.posX.mfloor()
				val y = (player.posY + player.getEyeHeight()).mfloor()
				val z = player.posZ.mfloor()
				val block = player.worldObj.getBlock(x, y, z)
				
				if (block !== Blocks.water) return false
				
				val f = ChunkProviderNiflheim.f(x)
				if (f !in z.bidiRange(6)) return false
				if (y !in 121..127) return false
				
				return true
			}
			
			if (!check())
				stack.waterTicks = 0
			else if (stack.waterTicks++ >= 6000) {
				ASJUtilities.say(player, "alfheimmisc.ragnarok.forgiven1")
				stack.NJORD = true
			}
		}
		
		private var ItemStack.lightnings: Int
			get() = getInt(this, "lightnings", 0)
			set(v) = setInt(this, "lightnings", v)
		
		@SubscribeEvent(priority = EventPriority.LOWEST)
		fun goThor(e: EntityStruckByLightningEvent) {
			val player = e.entity as? EntityPlayer ?: return
			if (player.worldObj.isRemote) return
			
			val stack = PlayerHandler.getPlayerBaubles(player)[0] ?: return
			if (stack.item !== AlfheimItems.ragnarokEmblem) return
			
			if (!stack.HEIMDAL || !stack.NJORD || stack.THOR) return
			
			if (stack.lightnings++ <= 60) return
			
			ASJUtilities.say(player, "alfheimmisc.ragnarok.forgiven2")
			stack.THOR = true
		}
		
		private var ItemStack.lavaTicks: Int
			get() = getInt(this, "lava_ticks", 0)
			set(v) = setInt(this, "lava_ticks", v)
		
		fun goLoki(stack: ItemStack, player: EntityPlayer) {
			if (!stack.HEIMDAL || !stack.NJORD || !stack.THOR || stack.LOKI) return
			
			fun check(): Boolean {
				if (player.dimension != -1) return false
				
				val x = player.posX.mfloor()
				val y = (player.posY + player.getEyeHeight()).mfloor()
				val z = player.posZ.mfloor()
				val block = player.worldObj.getBlock(x, y, z)
				
				if (block !== Blocks.lava) return false
				
				if (y > 32) return false
				if (player.isPotionActive(Potion.fireResistance)) return false
				return true
			}
			
			if (!check())
				stack.lavaTicks = 0
			else if (stack.lavaTicks++ >= 6000) {
				ASJUtilities.say(player, "alfheimmisc.ragnarok.forgiven3")
				stack.LOKI = true
			}
		}
		
		private var ItemStack.soilTicks: Int
			get() = getInt(this, "soil_ticks", 0)
			set(v) = setInt(this, "soil_ticks", v)
		
		fun goSif(stack: ItemStack, player: EntityPlayer) {
			if (!stack.HEIMDAL || !stack.NJORD || !stack.THOR || !stack.LOKI || stack.SIF) return
			
			fun check(): Boolean {
				if (player.dimension != 0) return false
				
				val x = player.posX.mfloor()
				val y = (player.posY + player.getEyeHeight()).mfloor()
				val z = player.posZ.mfloor()
				val block = player.worldObj.getBlock(x, y, z)
				
				if (block !== ModBlocks.enchantedSoil) return false
				
				return true
			}
			
			if (!check())
				stack.soilTicks = 0
			else if (stack.soilTicks++ >= 6000) {
				ASJUtilities.say(player, "alfheimmisc.ragnarok.forgiven4")
				stack.SIF = true
			}
		}
		
		private var ItemStack.sunTicks: Int
			get() = getInt(this, "sun_ticks", 0)
			set(v) = setInt(this, "sun_ticks", v)
		
		fun goOdin(stack: ItemStack, player: EntityPlayer) {
			if (!stack.HEIMDAL || !stack.NJORD || !stack.THOR || !stack.LOKI || !stack.SIF) return
			
			fun check(): Boolean {
				if (player.dimension != AlfheimConfigHandler.dimensionIDAlfheim) return false
				if (RagnarokHandler.noSunAndMoon) return false
				
				val (x, y, z) = Vector3.fromEntity(player).sub(0, 1, 0).mf()
				if (y < 250) return false
				
				val world = player.worldObj
				if (!world.canBlockSeeTheSky(x, y + 1, z)) return false
				
				val block = world.getBlock(player, y = -1)
				val meta = world.getBlockMeta(player, y = -1) % 8
				
				if (block !== AlfheimBlocks.altLeaves || meta != BlockAltLeaves.yggMeta) return false
				return true
			}
			
			if (!check())
				stack.sunTicks = 0
			else if (stack.sunTicks++ >= 6000) {
				ASJUtilities.say(player, "alfheimmisc.ragnarok.forgiven5")
				PlayerHandler.getPlayerBaubles(player)[0] = ItemStack(AlfheimItems.aesirEmblem)
				
				if (RagnarokHandler.canEndRagnarok()) RagnarokHandler.endRagnarok()
			}
		}
	}
	
	private data class Vector3I(var x: Int, var y: Int, var z: Int) {
		constructor(ints: IntArray): this(ints[0], ints[1], ints[2])
		val ints get() = intArrayOf(x, y, z)
	}
}