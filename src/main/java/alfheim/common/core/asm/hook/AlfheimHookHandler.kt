package alfheim.common.core.asm.hook

import alexsocol.asjlib.*
import alexsocol.asjlib.command.CommandDimTP
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.AlfheimCore
import alfheim.api.*
import alfheim.api.block.IHourglassTrigger
import alfheim.api.boss.*
import alfheim.api.entity.*
import alfheim.api.item.equipment.bauble.IManaDiscountBauble
import alfheim.api.lib.LibResourceLocations
import alfheim.api.spell.SpellBase
import alfheim.client.core.handler.CardinalSystemClient
import alfheim.common.block.*
import alfheim.common.block.alt.BlockAltLeaves
import alfheim.common.block.colored.BlockAuroraDirt
import alfheim.common.block.tile.*
import alfheim.common.core.handler.*
import alfheim.common.core.handler.AlfheimConfigHandler.dimensionIDAlfheim
import alfheim.common.core.handler.AlfheimConfigHandler.dimensionIDDomains
import alfheim.common.core.handler.AlfheimConfigHandler.dimensionIDHelheim
import alfheim.common.core.handler.AlfheimConfigHandler.dimensionIDNiflheim
import alfheim.common.core.handler.AlfheimConfigHandler.increasedSpiritsRange
import alfheim.common.core.handler.HilarityHandler.AttributionNameChecker.getCurrentNickname
import alfheim.common.core.handler.SheerColdHandler.cold
import alfheim.common.core.handler.ragnarok.RagnarokHandler.MAX_SUMMER_TICKS
import alfheim.common.core.handler.ragnarok.RagnarokHandler.noSunAndMoon
import alfheim.common.core.handler.ragnarok.RagnarokHandler.ragnarok
import alfheim.common.core.handler.ragnarok.RagnarokHandler.summer
import alfheim.common.core.handler.ragnarok.RagnarokHandler.summerTicks
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.entity.*
import alfheim.common.entity.ai.EntityAICreeperAvoidPooka
import alfheim.common.entity.boss.EntityFlugel
import alfheim.common.item.*
import alfheim.common.item.equipment.armor.ItemSnowArmor
import alfheim.common.item.equipment.bauble.ItemPendant
import alfheim.common.item.equipment.bauble.ItemPendant.Companion.EnumPrimalWorldType.MUSPELHEIM
import alfheim.common.item.equipment.bauble.faith.ItemRagnarokEmblem
import alfheim.common.item.rod.ItemRodClicker
import alfheim.common.potion.PotionSoulburn
import alfheim.common.spell.earth.SpellGoldRush
import alfheim.common.world.data.CustomWorldData.Companion.customData
import alfheim.common.world.mobspawn.MobSpawnHandler
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.relauncher.*
import cpw.mods.fml.relauncher.Side.CLIENT
import gloomyfolken.hooklib.asm.*
import gloomyfolken.hooklib.asm.Hook.ReturnValue
import gloomyfolken.hooklib.asm.ReturnCondition.*
import net.minecraft.block.*
import net.minecraft.block.material.Material
import net.minecraft.client.gui.*
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.*
import net.minecraft.command.*
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.enchantment.*
import net.minecraft.entity.*
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.entity.item.*
import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.player.*
import net.minecraft.entity.projectile.*
import net.minecraft.init.*
import net.minecraft.inventory.*
import net.minecraft.item.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.*
import net.minecraft.server.MinecraftServer
import net.minecraft.server.management.ServerConfigurationManager
import net.minecraft.tileentity.*
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraft.world.biome.*
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.structure.*
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fluids.IFluidBlock
import org.lwjgl.opengl.GL11.*
import ru.vamig.worldengine.*
import travellersgear.api.TravellersGearAPI
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.boss.IBotaniaBoss
import vazkii.botania.api.internal.*
import vazkii.botania.api.item.*
import vazkii.botania.api.lexicon.*
import vazkii.botania.api.mana.*
import vazkii.botania.api.recipe.*
import vazkii.botania.api.subtile.SubTileEntity
import vazkii.botania.client.core.handler.*
import vazkii.botania.client.core.helper.IconHelper
import vazkii.botania.client.core.proxy.ClientProxy
import vazkii.botania.client.fx.*
import vazkii.botania.client.gui.lexicon.*
import vazkii.botania.client.integration.nei.recipe.RecipeHandlerPetalApothecary
import vazkii.botania.client.lib.LibResources
import vazkii.botania.client.model.ModelMiniIsland
import vazkii.botania.client.render.tile.*
import vazkii.botania.common.Botania
import vazkii.botania.common.achievement.ModAchievements
import vazkii.botania.common.block.*
import vazkii.botania.common.block.decor.*
import vazkii.botania.common.block.decor.walls.BlockModWall
import vazkii.botania.common.block.mana.*
import vazkii.botania.common.block.subtile.generating.*
import vazkii.botania.common.block.tile.*
import vazkii.botania.common.block.tile.mana.*
import vazkii.botania.common.core.BotaniaCreativeTab
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.core.proxy.CommonProxy
import vazkii.botania.common.crafting.recipe.*
import vazkii.botania.common.entity.*
import vazkii.botania.common.item.*
import vazkii.botania.common.item.block.ItemBlockSpecialFlower
import vazkii.botania.common.item.equipment.bauble.ItemBauble
import vazkii.botania.common.item.equipment.tool.ToolCommons
import vazkii.botania.common.item.lens.LensFirework
import vazkii.botania.common.item.material.ItemManaResource
import vazkii.botania.common.item.relic.*
import vazkii.botania.common.item.rod.*
import vazkii.botania.common.lib.LibBlockNames
import java.awt.Color
import java.util.*
import java.util.regex.*
import kotlin.math.*

@Suppress("UNUSED_PARAMETER", "NAME_SHADOWING", "unused", "FunctionName")
object AlfheimHookHandler {
	
	var updatingTile = false
	var updatingEntity = false
	const val TAG_TRANSFER_STACK = "transferStack"
	
	var rt = 0f
	var gt = 0f
	var bt = 0f
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "<init>")
	fun `ContainerWorkbench$init`(con: ContainerWorkbench, playerInv: InventoryPlayer, world: World?, x: Int, y: Int, z: Int) {
		con.alfheim_synthetic_thePlayer = playerInv.player
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun registerSpell(api: AlfheimAPI, spell: SpellBase) =
		AlfheimConfigHandler.disabledSpells.contains(spell.name).also {
			if (it) ASJUtilities.log("${spell.name} was blacklisted in configs. Skipping registration")
		}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun createBonusChest(world: WorldServer) = AlfheimConfigHandler.enableElvenStory
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "<init>")
	fun `EntityCreeper$init`(e: EntityCreeper, world: World?) {
		e.tasks.addTask(3, EntityAICreeperAvoidPooka(e))
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun isPotionActive(e: EntityLivingBase, p: Potion) =
		if (p === Potion.resistance) {
			e.activePotionsMap.containsKey(Potion.resistance.id) || e.activePotionsMap.containsKey(AlfheimConfigHandler.potionIDTank)
		} else e.activePotionsMap.containsKey(p.id)
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun getActivePotionEffect(e: EntityLivingBase, p: Potion): PotionEffect? {
		var pe = e.activePotionsMap[p.id] as PotionEffect?
		if (p.id != Potion.resistance.id || !e.isPotionActive(AlfheimConfigHandler.potionIDTank)) return pe
		
		val tank = e.activePotionsMap[AlfheimConfigHandler.potionIDTank] as PotionEffect
		if (pe == null) pe = PotionEffectU(Potion.resistance.id, tank.duration)
		pe.amplifier += tank.amplifier
		
		return pe
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun despawnEntity(e: EntityLiving) = e.dimension in MobSpawnHandler.mobNames.keys && EntityList.getEntityString(e) in MobSpawnHandler.mobNames[e.dimension]!!
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun attackEntityFrom(dragon: EntityDragon, src: DamageSource, dmg: Float): Boolean {
		if (src is DamageSourceSpell)
			dragon.attackEntityFromPart(dragon.dragonPartHead, src, dmg)
		
		return false
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE) // NO, CCC, you won't mess with fire on my territory!
	fun updateTick(portal: BlockFire, world: World, x: Int, y: Int, z: Int, random: Random?): Boolean {
		return world.provider.dimensionId == dimensionIDDomains
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun updateTick(portal: BlockPortal, world: World, x: Int, y: Int, z: Int, random: Random?): Boolean {
		if (world.provider.dimensionId != dimensionIDAlfheim && world.provider.dimensionId != dimensionIDNiflheim)
			return false
		
		world.setBlockToAir(x, y, z)
		return true
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun onEntityCollidedWithBlock(portal: BlockPortal, world: World, x: Int, y: Int, z: Int, entity: Entity?): Boolean {
		if (world.provider.dimensionId != dimensionIDAlfheim && world.provider.dimensionId != dimensionIDNiflheim)
			return false
		
		world.setBlockToAir(x, y, z)
		return true
	}
	
	@JvmStatic
	@Hook
	fun processCommand(cmd: CommandDimTP, sender: ICommandSender, args: Array<String>) {
		allowtp = true
	}
	
	var allowtp = false
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun transferPlayerToDimension(scm: ServerConfigurationManager, player: EntityPlayerMP, dimTo: Int, teleporter: Teleporter?): Boolean {
		val let = false
		val block = true
		
		if (allowtp) {
			allowtp = false
			return let
		}
		
		if (player.capabilities.isCreativeMode) return let
		if (dimTo == dimensionIDHelheim) return let
		if (dimTo == dimensionIDDomains) return block // only with TileDomainLobby
		
		return when (player.dimension) {
			dimensionIDDomains  -> dimTo != (player.entityData.getIntArray(TileDomainLobby.TAG_DOMAIN_ENTRANCE).getOrNull(3) ?: dimTo)
			dimensionIDAlfheim  -> dimTo != 0 && dimTo != dimensionIDNiflheim
			dimensionIDNiflheim -> dimTo != dimensionIDAlfheim
			dimensionIDHelheim  -> block // no way out except TileRainbowManaFlame#exitPlayer
			else                -> let
		}
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun onBlockActivated(flower: BlockFloatingFlower, world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val stack = player?.heldItem ?: return false
		if (stack.item !== ModItems.overgrowthSeed) return false
		
		val tile = world.getTileEntity(x, y, z) as? IFloatingFlower ?: return false
		if (tile.islandType === ItemColorSeeds.islandOvergrowth) return false
		
		if (!world.isRemote) {
			tile.islandType = ItemColorSeeds.islandOvergrowth
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(world, x, y, z)
		}
		
		if (!player.capabilities.isCreativeMode) stack.stackSize--
		
		return true
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun isOnSpecialSoil(flower: TileFloatingSpecialFlower) = flower.islandType === ItemColorSeeds.islandOvergrowth
	
	@SideOnly(CLIENT)
	@JvmStatic
	@Hook
	fun renderTileEntityAt(render: RenderTileFloatingFlower, tile: TileEntity, d0: Double, d1: Double, d2: Double, t: Float) {
		hookColor = (tile as IFloatingFlower).islandType === ItemColorSeeds.islandTypes.last()
		if (!hookColor) return
		
		Color(BlockAuroraDirt.getBlockColor(tile.xCoord, tile.yCoord, tile.zCoord)).getRGBColorComponents(colors)
	}
	
	var hookColor = false
	val colors = floatArrayOf(1f, 1f, 1f)
	
	@SideOnly(CLIENT)
	@JvmStatic
	@Hook
	fun render(model: ModelMiniIsland) {
		if (!hookColor) return
		hookColor = false
		
		val (r, g, b) = colors
		glColor3f(r, g, b)
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun requestManaExact(handler: ManaItemHandler?, stack: ItemStack?, player: EntityPlayer, manaToGet: Int, remove: Boolean) = player.capabilities.isCreativeMode
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE, returnType = "int", returnAnotherMethod = "requestManaChecked")
	fun requestMana(handler: ManaItemHandler?, stack: ItemStack?, player: EntityPlayer, manaToGet: Int, remove: Boolean) = player.capabilities.isCreativeMode
	
	@JvmStatic
	fun requestManaChecked(handler: ManaItemHandler?, stack: ItemStack?, player: EntityPlayer, manaToGet: Int, remove: Boolean) = manaToGet
	
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ALWAYS)
	fun getFullDiscountForTools(handler: ManaItemHandler?, player: EntityPlayer, @ReturnValue discount: Float): Float {
		var ret = discount + getBaublesDiscountForTools(player) + getTravellersDiscountForTools(player)
		if (AlfheimConfigHandler.enableElvenStory && player.race === EnumRace.IMP && !ESMHandler.isAbilityDisabled(player)) ret += 0.2f
		return ret
	}
	
	/**
	 * Gets the sum of all the discounts on IManaDiscountBauble items equipped
	 * on the player passed in.
	 */
	@JvmStatic
	fun getBaublesDiscountForTools(player: EntityPlayer): Float {
		val baubles = PlayerHandler.getPlayerBaubles(player)
		return (0 until baubles.sizeInventory).sumOf { i -> (baubles[i]?.let { (it.item as? IManaDiscountBauble)?.getDiscount(it, i, player) } ?: 0f) }
	}
	
	@JvmStatic
	fun getTravellersDiscountForTools(player: EntityPlayer): Float {
		if (!AlfheimCore.TravellersGearLoaded) return 0f
		val gear = TravellersGearAPI.getExtendedInventory(player)
		return gear.indices.sumOf { i -> (gear[i]?.let { (it.item as? IManaDiscountBauble)?.getDiscount(it, i, player) } ?: 0f) }
	}
	
	var stoneHook = false
	var cobbleHook = false
	
	@JvmStatic
	@Hook
	fun updateTick(block: BlockDynamicLiquid, world: World, x: Int, y: Int, z: Int, rand: Random) {
		stoneHook = world.provider.dimensionId == dimensionIDAlfheim
	}
	
	@JvmStatic
	@Hook
	fun func_149805_n(block: BlockLiquid, world: World, x: Int, y: Int, z: Int) {
		cobbleHook = world.provider.dimensionId == dimensionIDAlfheim
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE, returnAnotherMethod = "replaceSetBlock")
	fun setBlock(world: World, x: Int, y: Int, z: Int, block: Block): Boolean {
		return (cobbleHook && block === Blocks.cobblestone) || (stoneHook && block === Blocks.stone)
	}
	
	@JvmStatic
	fun replaceSetBlock(world: World, x: Int, y: Int, z: Int, block: Block): Boolean {
		var newBlock = block
		
		if (cobbleHook && block === Blocks.cobblestone) {
			cobbleHook = false
			newBlock = AlfheimBlocks.livingcobble
		}
		
		if (stoneHook && block === Blocks.stone) {
			stoneHook = false
			newBlock = ModBlocks.livingrock
		}
		
		return world.setBlock(x, y, z, newBlock, 0, 3)
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "<init>")
	fun `BlockModWall$init`(wall: BlockModWall, block: Block, meta: Int) {
		wall.setCreativeTab(BotaniaCreativeTab.INSTANCE)
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun addBlock(tab: BotaniaCreativeTab, block: Block) {
		if (block === ModFluffBlocks.elfQuartzStairs)
			tab.addBlock(AlfheimFluffBlocks.elfQuartzWall)
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "<init>")
	fun `BlockSpreader$init`(spreader: BlockSpreader) {
		val f = 1 / 16f
		spreader.setBlockBounds(f, f, f, 1 - f, 1 - f, 1 - f)
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun tickRate(block: BlockHourglass, world: World?) = 2
	
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ALWAYS)
	fun getStackItemTime(tile: TileHourglass?, stack: ItemStack?, @ReturnValue time: Int) =
		if (stack != null && time == 0) {
			if (stack.item === AlfheimBlocks.elvenSand.toItem()) 600 else 0
		} else time
	
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ALWAYS)
	fun getColor(tile: TileHourglass, @ReturnValue color: Int): Int {
		val stack = tile[0]
		return if (stack != null && color == 0) {
			if (stack.item === AlfheimBlocks.elvenSand.toItem()) 0xf7f5d9 else 0
		} else color
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun writeCustomNBT(tile: TileEnchanter, cmp: NBTTagCompound) {
		if (tile.itemToEnchant == null) cmp.removeTag("item")
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun readCustomNBT(tile: TileEnchanter, cmp: NBTTagCompound) {
		if (!cmp.hasKey("item")) tile.itemToEnchant = null
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun setCosmeticItem(item: ItemBauble, stack: ItemStack, cosmetic: ItemStack?) {
		if (cosmetic == null)
			stack.tagCompound.removeTag("cosmeticItem")
	}
	
	const val TAG_COCOONED = "Botania-CocoonSpawned"
	var cocooned = false
	
	@JvmStatic
	@Hook
	fun hatch(tile: TileCocoon) {
		cocooned = true
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun spawnEntityInWorld(world: World, entity: Entity?, @ReturnValue result: Boolean): Boolean {
		if (cocooned && result && entity != null) {
			entity.entityData.setBoolean(TAG_COCOONED, true)
			cocooned = false
		}
		
		return result
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun moveFlying(e: Entity, x: Float, y: Float, z: Float) {
		if (AlfheimConfigHandler.enableMMO && e is EntityLivingBase && e.isPotionActive(AlfheimConfigHandler.potionIDLeftFlame)) {
			e.motionZ = 0.0
			e.motionY = 0.0
			e.motionX = 0.0
		}
	}
	
	@JvmStatic
	@Hook
	fun onLivingUpdate(e: EntityDoppleganger) {
		updatingEntity = true
		EntityDoppleganger.isPlayingMusic = false
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "onLivingUpdate")
	fun onLivingUpdatePost(e: EntityDoppleganger) {
		updatingEntity = false
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun spawn(gaia: EntityDoppleganger?, player: EntityPlayer, stack: ItemStack, world: World, x: Int, y: Int, z: Int, hard: Boolean): Boolean {
		if (stack.item === ModItems.manaResource && stack.meta == 4 && world.provider.dimensionId == dimensionIDAlfheim) {
			if (!world.isRemote) ASJUtilities.say(player, "alfheimmisc.gaia.wrongitem")
			return true
		}
		
		val tile = world.getTileEntity(x, y, z) as? TileEntityBeacon ?: return true
		
		if (tile.levels < 1 || tile.primaryEffect == 0) {
			if ((world.getTileEntity(x, y + 2, z) as? TileManaInfuser)?.isReadyToKillGaia == true) return false
			
			if (!world.isRemote) ASJUtilities.say(player, "alfheimmisc.flugel.inactive")
			return true
		}
		
		return false
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE, booleanReturnConstant = false, targetMethod = "attackEntityFrom")
	fun disableGod(gaia: EntityDoppleganger, src: DamageSource, dmg: Float): Boolean {
		val player = src.entity as? EntityPlayer ?: return false
		return !player.capabilities.isCreativeMode && player.capabilities.disableDamage
	}
	
	var hadPlayer = false
	
	@JvmStatic
	@Hook(targetMethod = "attackEntityFrom")
	fun noDupePre(gaia: EntityDoppleganger, src: DamageSource, dmg: Float): Boolean {
		val player = src.entity as? EntityPlayer ?: return false
		hadPlayer = gaia.playersWhoAttacked.contains(player.commandSenderName)
		return false
	}
	
	@JvmStatic
	@Hook(targetMethod = "attackEntityFrom", injectOnExit = true)
	fun noDupePost(gaia: EntityDoppleganger, src: DamageSource, dmg: Float, @ReturnValue result: Boolean): Boolean {
		val player = src.entity as? EntityPlayer ?: return false
		if (!hadPlayer) gaia.playersWhoAttacked.remove(player.commandSenderName)
		return result
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun onDeath(gaia: EntityDoppleganger, src: DamageSource?) {
		if (ASJUtilities.isClient) return
		
		gaia.playersWhoAttacked.forEach {
			val player = MinecraftServer.getServer().configurationManager.func_152612_a(it)
			if (!EntityDoppleganger.isTruePlayer(player)) return@forEach
			
			player.triggerAchievement(ModAchievements.gaiaGuardianKill)
			
			if (!gaia.anyWithArmor)
				player.triggerAchievement(ModAchievements.gaiaGuardianNoArmor)
		}
		
		gaia.playersAround.forEach { it.removePotionEffect(Potion.wither.id) }
		
		val (x, y, z) = gaia.source
		val arenaBB = getBoundingBox(x, y, z).offset(0.5).expand(15)
		selectEntitiesWithinAABB(gaia.worldObj, EntityPixie::class.java, arenaBB) { it.isEntityAlive && it.type == 1 }.forEach(EntityPixie::setDead)
		getEntitiesWithinAABB(gaia.worldObj, EntityMagicMissile::class.java, arenaBB).forEach(EntityMagicMissile::setDead)
		getEntitiesWithinAABB(gaia.worldObj, EntityMagicLandmine::class.java, arenaBB).forEach(EntityMagicLandmine::setDead)
	}
	
	@SideOnly(CLIENT)
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ALWAYS)
	fun getNameColor(gaia: EntityDoppleganger) = AlfheimConfigHandler.gaiaNameColor
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun collideBurst(lens: LensFirework, burst: IManaBurst, entity: EntityThrowable, pos: MovingObjectPosition, isManaBlock: Boolean, dead: Boolean, stack: ItemStack?): Boolean {
		if (burst.isFake) return false
		
		val allow = when (AlfheimConfigHandler.rocketRide) {
						-1   -> pos.entityHit !is EntityPlayer && pos.entityHit != null
						1    -> pos.entityHit is EntityPlayer
						2    -> pos.entityHit != null
						else -> false
					} && !entity.worldObj.isRemote && pos.entityHit?.isSneaking == false
		
		if (!allow) return false
		if (entity.thrower != null && !InteractionSecurity.canInteractWithEntity(entity.thrower, pos.entityHit)) return false
		
		val fireworkStack = lens.generateFirework(burst.color)
		val rocket = EntityFireworkRocket(entity.worldObj, entity.posX, entity.posY, entity.posZ, fireworkStack)
		rocket.spawn()
		pos.entityHit.mountEntity(rocket)
		
		return true
	}
	
	@JvmStatic
	@Hook(targetMethod = "<init>", injectOnExit = true)
	fun `EntityManaBurst$init`(obj: EntityManaBurst, player: EntityPlayer?) {
		obj.thrower = player
		obj.throwerName = player?.commandSenderName
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun wispFX(proxy: CommonProxy, world: World, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float, size: Float, gravity: Float) {
		var r = r
		var g = g
		var b = b
		if (updatingEntity) {
			rt = Math.random().F * 0.3f
			r = rt
			gt = 0.7f + Math.random().F * 0.3f
			g = gt
			bt = 0.7f + Math.random().F * 0.3f
			b = bt
		}
		Botania.proxy.wispFX(world, x, y, z, r, g, b, size, gravity, 1f)
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun wispFX(proxy: CommonProxy, world: World, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float, size: Float, motionx: Float, motiony: Float, motionz: Float) {
		var r = r
		var g = g
		var b = b
		if (updatingEntity && size == 0.4f) {
			r = rt
			g = gt
			b = bt
		}
		Botania.proxy.wispFX(world, x, y, z, r, g, b, size, motionx, motiony, motionz, 1f)
	}
	
	@JvmStatic
	@Hook(createMethod = true)
	fun setEntryDataToOpen(proxy: CommonProxy, data: LexiconRecipeMappings.EntryData?) = Unit
	
	@SideOnly(CLIENT)
	@JvmStatic
	@Hook(createMethod = true)
	fun setEntryDataToOpen(proxy: ClientProxy, data: LexiconRecipeMappings.EntryData) {
		GuiLexicon.currentOpenLexicon = GuiLexiconEntry(data.entry, GuiLexiconIndex(data.entry.category)).apply { page = data.page }
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS, injectOnExit = true)
	fun getKnowledgeType(entry: LexiconEntry, @ReturnValue type: KnowledgeType): KnowledgeType {
		return if (type === BotaniaAPI.elvenKnowledge && AlfheimConfigHandler.enableElvenStory) BotaniaAPI.basicKnowledge else type
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun updateTick(grass: BlockGrass, world: World, x: Int, y: Int, z: Int, random: Random) {
		if (AlfheimCore.winter && world.provider.dimensionId == dimensionIDAlfheim && world.rand.nextInt(20) == 0 && !world.isRemote && world.getPrecipitationHeight(x, z) <= y)
			world.setBlock(x, y, z, AlfheimBlocks.snowGrass)
	}
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ALWAYS)
	@SideOnly(CLIENT)
	fun randomDisplayTick(grass: BlockGrass, world: World, x: Int, y: Int, z: Int, rand: Random) {
		if (!increasedSpiritsRange && world.provider.dimensionId == dimensionIDAlfheim)
			BlockAltLeaves.spawnRandomSpirit(world, x, y + 1 + rand.nextInt(5), z, rand, rand.nextFloat(), 1f, 0f)
	}
	
	@JvmStatic
	@Hook
	@SideOnly(CLIENT)
	fun doVoidFogParticles(world: WorldClient, i: Int, j: Int, k: Int) {
		if (!increasedSpiritsRange || world.provider.dimensionId != dimensionIDAlfheim || world.worldTime % 24000 !in 13333..22666) return
		
		val random = Random()
		val range = max(4, mc.gameSettings.renderDistanceChunks - 2) * 16
		val max = (1312.5 * range - 20000).I
		
		for (l in 0..max) {
			val x = i + ASJUtilities.randInBounds(-range, range, world.rand)
			val y = j + ASJUtilities.randInBounds(-range, range, world.rand)
			val z = k + ASJUtilities.randInBounds(-range, range, world.rand)
			
			val block = world.getBlock(x, y, z)
			
			if (block === Blocks.grass) {
				BlockAltLeaves.spawnRandomSpirit(world, x, y + 1 + random.nextInt(5), z, random, random.nextFloat(), 1f, 0f)
			} else if (block === AlfheimBlocks.altLeaves && world.getBlockMetadata(x, y, z) % 8 == 7)
				BlockAltLeaves.spawnRandomSpirit(world, x, y, z, random, 0f, random.nextFloat() * 0.25f + 0.5f, 1f)
		}
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun updateTick(snow: BlockSnow, world: World, x: Int, y: Int, z: Int, random: Random) {
		if (world.provider.dimensionId != dimensionIDAlfheim || world.isRemote) return
		
		if (AlfheimCore.winter) {
			world.setBlock(x, y, z, AlfheimBlocks.snowLayer)
		} else if (world.rand.nextInt(20) == 0) {
			world.setBlockToAir(x, y, z)
		}
	}
	
	@JvmStatic
	@Hook
	fun updateTick(grass: BlockAltGrass, world: World, x: Int, y: Int, z: Int, random: Random) {
		val summer = summer || ragnarok
		if (!summer) return
		
		val burnLife = summerTicks / MAX_SUMMER_TICKS.F > 1/3f || ragnarok
		val metaMust = if (burnLife) 3 else 0
		
		val meta = world.getBlockMetadata(x, y, z)
		if (meta == 3 || meta == metaMust) return
		world.setBlockMetadataWithNotify(x, y, z, metaMust, 3)
		
		for (l in 0..3) {
			val i = x + random.nextInt(3) - 1
			val j = y + random.nextInt(4) - 3
			val k = z + random.nextInt(3) - 1
			
			if (world.getPrecipitationHeight(i, k) > k) continue
			
			val meta = world.getBlockMetadata(i, j, k)
			
			when (world.getBlock(i, j, k)) {
				Blocks.dirt, Blocks.grass -> if (meta != 0) continue
				ModBlocks.altGrass -> if (meta == 3 || meta == metaMust) continue
			}
			
			world.setBlock(i, j, k, grass, metaMust, 3)
		}
	}
	
	@JvmStatic
	@Hook(targetMethod = "updateTick", returnCondition = ON_TRUE)
	fun updateTickPre(ice: BlockIce, world: World, x: Int, y: Int, z: Int, random: Random) = world.provider.dimensionId == dimensionIDNiflheim || world.getBlockMetadata(x, y, z) == 1
	
	@JvmStatic
	@Hook(targetMethod = "updateTick", injectOnExit = true)
	fun updateTickPost(ice: BlockIce, world: World, x: Int, y: Int, z: Int, random: Random) {
		if (!AlfheimCore.winter && world.provider.dimensionId == dimensionIDAlfheim && world.rand.nextInt(20) == 0 && !world.isRemote)
			world.setBlock(x, y, z, Blocks.flowing_water)
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun getSubItems(target: ItemAncientWill, item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
		list.add(ItemStack(item, 1, 6))
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun registerIcons(target: ItemAncientWill, reg: IIconRegister?) {
		target.icons += IconHelper.forItem(reg, target, 6)
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun addInformation(target: ItemAncientWill, stack: ItemStack, player: EntityPlayer?, list: List<String>, adv: Boolean): Boolean {
		if (stack.meta != 6) return false
		
		target.addStringToTooltip(StatCollector.translateToLocal("alfheimmisc.craftToAddWill"), list)
		target.addStringToTooltip(StatCollector.translateToLocal("botania.armorset.will" + stack.getItemDamage() + ".shortDesc"), list)
		
		return true
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun onBlockActivated(block: BlockAvatar, world: World, x: Int, y: Int, z: Int, player: EntityPlayer, s: Int, xs: Float, ys: Float, zs: Float, @ReturnValue result: Boolean): Boolean {
		if (result) ASJUtilities.dispatchTEToNearbyPlayers(world, x, y, z)
		return result
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun onItemUse(floral: ItemDye, stack: ItemStack, player: EntityPlayer?, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, @ReturnValue result: Boolean): Boolean {
		if (result) ASJUtilities.dispatchTEToNearbyPlayers(world, x, y, z)
		return result
	}
	
	// fix for IFluidBlock
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun onItemRightClick(item: ItemOpenBucket, stack: ItemStack?, world: World, player: EntityPlayer): ItemStack? {
		// #### Item#getMovingObjectPositionFromPlayer START
		val f = 1.0f
		val f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f
		val f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f
		val d0 = player.prevPosX + (player.posX - player.prevPosX) * f
		val d1 = player.prevPosY + (player.posY - player.prevPosY) * f + (if (world.isRemote) player.getEyeHeight() - player.defaultEyeHeight else player.getEyeHeight()) // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
		val d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * f
		val vec3 = Vec3.createVectorHelper(d0, d1, d2)
		val f3 = MathHelper.cos(-f2 * 0.017453292f - Math.PI.F)
		val f4 = MathHelper.sin(-f2 * 0.017453292f - Math.PI.F)
		val f5 = -MathHelper.cos(-f1 * 0.017453292f)
		val f6 = MathHelper.sin(-f1 * 0.017453292f)
		val f7 = f4 * f5
		val f8 = f3 * f5
		
		val d3 = if (player is EntityPlayerMP)
			player.theItemInWorldManager.blockReachDistance
		else
			mc.playerController.blockReachDistance.D
			
		val vec31 = vec3.addVector(f7 * d3, f6 * d3, f8 * d3)
		val mop = world.rayTraceBlocks(vec3, vec31, true) ?: return stack
		// #### Item#getMovingObjectPositionFromPlayer END
		
		if (mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return stack
		
		val i = mop.blockX
		val j = mop.blockY
		val k = mop.blockZ
		if (!world.canMineBlock(player, i, j, k) || !player.canPlayerEdit(i, j, k, mop.sideHit, stack)) return stack
		
		val block = world.getBlock(i, j, k)
		if (block is IFluidBlock && !block.canDrain(world, i, j, k)) return stack
		
		val material = block.material
		if (!(material === Material.lava || material === Material.water)) return stack
		
		if (block is IFluidBlock) block.drain(world, i, j, k, true)
		else world.setBlockToAir(i, j, k)
		
		for (x in 0..4) world.spawnParticle("explode", i + Math.random(), j + Math.random(), k + Math.random(), 0.0, 0.0, 0.0)
		
		return stack
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun displayRemainderCounter(item: ItemExchangeRod, player: EntityPlayer, stack: ItemStack?) {
		val block = ItemExchangeRod.getBlock(stack)
		val meta = ItemExchangeRod.getBlockMeta(stack)
		val count = ItemExchangeRod.getInventoryItemCount(player, stack, block, meta)
		
		if (player.worldObj.isRemote && player === mc.thePlayer)
			ItemsRemainingRenderHandler.set(ItemStack(block, 1, meta), count)
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_NOT_NULL)
	fun onItemRightClick(item: ItemMissileRod, stack: ItemStack?, world: World?, player: EntityPlayer): ItemStack? {
		if (player.commandSenderName.startsWith("Avatar-Clicker_")) {
			item.onUsingTick(stack, player, 2)
			return stack
		}
		
		return null
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun set(thiz: ItemsRemainingRenderHandler?, player: EntityPlayer, displayStack: ItemStack?, pattern: Pattern) = !player.worldObj.isRemote || player !== mc.thePlayer
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun onBlockActivated(block: BlockSpreader, par1World: World, par2: Int, par3: Int, par4: Int, par5EntityPlayer: EntityPlayer, par6: Int, par7: Float, par8: Float, par9: Float, @ReturnValue res: Boolean): Boolean {
		if (!res) return false
		par1World.getTileEntity(par2, par3, par4)?.markDirty()
		return true
	}
	
	// dupe fix
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun setDead(spark: EntitySpark) = spark.isDead
	
	// dupe fix
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun setDead(spark: EntityCorporeaSpark) = spark.isDead
	
	// dupe fix
	@JvmStatic
	@Hook(targetMethod = "<init>")
	fun `ItemRainbowRod$init`(item: ItemRainbowRod) {
		item.setNoRepair()
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_NOT_NULL)
	fun onItemRightClick(item: ItemRainbowRod, stack: ItemStack, world: World, player: EntityPlayer): ItemStack? {
		return if (ragnarok && ItemRagnarokEmblem.getEmblem(player, -1) == null) stack else null
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_NULL)
	fun getContainerItem(item: ItemRainbowRod, stack: ItemStack): ItemStack? {
		return if (ragnarok) null else stack.copy()
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun onAvatarUpdate(item: ItemRainbowRod, tile: IAvatarTile, stack: ItemStack?) {
		if (ragnarok) return
		
		val MANA_COST_AVATAR = 10
		
		val te = tile as TileEntity
		val world = te.worldObj
		if (world.isRemote || tile.currentMana < MANA_COST_AVATAR * 25 || !tile.isEnabled) return
		val x = te.xCoord
		val y = te.yCoord
		val z = te.zCoord
		val w = 1
		val h = 1
		val l = 20
		val axis = when (te.getBlockMetadata() - 2) {
			0 -> getBoundingBox(x - w, y - h, z - l, x + w + 1, y + h, z)
			1 -> getBoundingBox(x - w, y - h, z + 1, x + w + 1, y + h, z + l + 1)
			2 -> getBoundingBox(x - l, y - h, z - w, x, y + h, z + w + 1)
			3 -> getBoundingBox(x + 1, y - h, z - w, x + l + 1, y + h, z + w + 1)
			else -> return
		}
		
		getEntitiesWithinAABB(world, EntityPlayer::class.java, axis).forEach { p ->
			var (px, py, pz) = Vector3.fromEntity(p).mf()
			--py
			
			val dist = 5
			val diff = dist / 2
			
			for (i in 0 until dist)
				for (j in 0 until dist) {
					val ex = px + i - diff
					val ez = pz + j - diff
					
					if (!axis.isVecInside(Vec3.createVectorHelper(ex + 0.5, py + 1.0, ez + 0.5))) continue
					if (!world.blockExists(ex, py, ez)) continue // bruh crash fix
					
					val block = world.getBlock(ex, py, ez)
					
					if (block.isAir(world, ex, py, ez)) {
						world.setBlock(ex, py, ez, ModBlocks.bifrost)
						val tileBifrost = world.getTileEntity(ex, py, ez) as TileBifrost
						tileBifrost.ticks = 10
						tile.recieveMana(-MANA_COST_AVATAR)
					} else if (block === ModBlocks.bifrost) {
						val tileBifrost = world.getTileEntity(ex, py, ez) as TileBifrost
						if (tileBifrost.ticks < 2) {
							tileBifrost.ticks = 10
							tile.recieveMana(-MANA_COST_AVATAR)
						}
					}
				}
		}
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun onBlockActivated(block: BlockBellows, world: World, x: Int, y: Int, z: Int, player: EntityPlayer, s: Int, xs: Float, ys: Float, zs: Float): Boolean {
		if (!ItemRodClicker.isFakeNotAvatar(player))
			(world.getTileEntity(x, y, z) as TileBellows).interact()
		return true
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "updateEntity")
	fun `TileHourglass$updateEntity`(tile: TileHourglass) {
		if (tile.blockMetadata != 1 || tile.flipTicks != 3) return
		var block: Block
		for (dir in ForgeDirection.VALID_DIRECTIONS) {
			block = tile.worldObj.getBlock(tile.xCoord + dir.offsetX, tile.yCoord + dir.offsetY, tile.zCoord + dir.offsetZ)
			if (block is IHourglassTrigger)
				block.onTriggeredByHourglass(tile.worldObj, tile.xCoord + dir.offsetX, tile.yCoord + dir.offsetY, tile.zCoord + dir.offsetZ, tile)
		}
	}
	
	@JvmStatic
	@Hook(targetMethod = "updateEntity")
	fun `TilePool$updateEntity`(tile: TilePool) {
		if (tile.manaCap == -1 && tile.getBlockMetadata() == 3) tile.manaCap = AlfheimConfigHandler.poolRainbowCapacity
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun collideEntityItem(tile: TilePool, item: EntityItem): Boolean {
		if (item.isDead) return false
		
		var didChange = false
		val stack = item.entityItem ?: return false
		
		if (stack.item is IManaDissolvable) {
			(stack.item as IManaDissolvable).onDissolveTick(tile, stack, item)
			if (stack.stackSize == 0)
				item.setDead()
		}
		
		if (item.age in 101..129 || !tile.catalystsRegistered) return false
		
		for (recipe in BotaniaAPI.manaInfusionRecipes) {
			if (recipe.matches(stack) && (!recipe.isAlchemy || tile.alchemy) && (!recipe.isConjuration || tile.conjuration)) {
				val mana = recipe.manaToConsume
				if (tile.currentMana >= mana) {
					tile.recieveMana(-mana)
					
					if (!tile.worldObj.isRemote) {
						stack.stackSize -= recipe.inputSize
						if (stack.stackSize == 0) item.setDead()
						val output = recipe.output.copy()
						val outputItem = EntityItem(tile.worldObj, tile.xCoord + 0.5, tile.yCoord + 1.5, tile.zCoord + 0.5, output)
						outputItem.age = 105
						outputItem.spawn()
					}
					
					tile.craftingFanciness()
					didChange = true
				}
				
				break
			}
		}
		
		return didChange
	}
	
	val RecipeManaInfusion.inputSize get() = if (input is ItemStack) (input as ItemStack).stackSize else 1
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE, injectOnExit = true, returnAnotherMethod = "sizeCheck")
	fun matches(recipe: RecipeManaInfusion, stack: ItemStack, @ReturnValue matches: Boolean): Boolean {
		if (!matches) return false
		return recipe.input is ItemStack
	}
	
	@JvmStatic
	fun sizeCheck(recipe: RecipeManaInfusion, stack: ItemStack, @ReturnValue matches: Boolean): Boolean {
		return stack.stackSize >= recipe.inputSize
	}
	
	@JvmStatic
	@Hook(targetMethod = "updateEntity")
	fun `TilePylon$updateEntity`(tile: TilePylon) {
		updatingTile = tile.worldObj.isRemote
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "updateEntity")
	fun `TilePylon$updateEntityPost`(tile: TilePylon) {
		if (tile.worldObj.isRemote) {
			updatingTile = false
			if (tile.worldObj.rand.nextBoolean()) {
				val meta = tile.getBlockMetadata()
				Botania.proxy.sparkleFX(tile.worldObj, tile.xCoord + Math.random(), tile.yCoord + Math.random() * 1.5, tile.zCoord + Math.random(), if (meta == 2) 0f else 0.5f, if (meta == 0) 0.5f else 1f, if (meta == 1) 0.5f else 1f, Math.random().F, 2)
			}
		}
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun sparkleFX(proxy: ClientProxy, world: World, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float, size: Float, m: Int, fake: Boolean) = updatingTile
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun getSubBlocks(flower: BlockSpecialFlower, item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (s in BotaniaAPI.subtilesForCreativeMenu) {
			list.add(ItemBlockSpecialFlower.ofType(s))
			if (BotaniaAPI.miniFlowers.containsKey(s))
				list.add(ItemBlockSpecialFlower.ofType(BotaniaAPI.miniFlowers[s]))
			if (s == LibBlockNames.SUBTILE_DAYBLOOM)
				list.add(ItemBlockSpecialFlower.ofType(LibBlockNames.SUBTILE_DAYBLOOM_PRIME))
			if (s == LibBlockNames.SUBTILE_NIGHTSHADE)
				list.add(ItemBlockSpecialFlower.ofType(LibBlockNames.SUBTILE_NIGHTSHADE_PRIME))
		}
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS, createMethod = true)
	fun isValidArmor(item: ItemGaiaHead, stack: ItemStack, armorType: Int, entity: Entity) = armorType == 0
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun displayAllReleventItems(tab: BotaniaCreativeTab, list: List<Any?>) {
		AlfheimItems.thinkingHand.getSubItems(AlfheimItems.thinkingHand, tab, list)
	}
	
	@JvmStatic
	@Hook
	fun onBlockPlacedBy(subtile: SubTileEntity, world: World?, x: Int, y: Int, z: Int, entity: EntityLivingBase?, stack: ItemStack?) {
		if (subtile is SubTileDaybloom && subtile.isPrime) subtile.setPrimusPosition()
	}
	
	@JvmStatic
	@Hook
	fun onBlockAdded(subtile: SubTileEntity, world: World, x: Int, y: Int, z: Int) {
		if (subtile is SubTileDaybloom && subtile.isPrime) subtile.setPrimusPosition()
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun getIcon(pylon: BlockPylon, side: Int, meta: Int) =
		(if (meta == 0 || meta == 1) ModBlocks.storage.getIcon(side, meta) else Blocks.diamond_block.getIcon(side, 0))!!
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE, booleanReturnConstant = false)
	fun matches(recipe: RecipePureDaisy, world: World, x: Int, y: Int, z: Int, pureDaisy: SubTileEntity?, block: Block, meta: Int): Boolean {
		if (recipe.output !== ModBlocks.livingwood) return false
		
		if (block === AlfheimBlocks.altWood1 && meta in arrayOf(3, 7, 11, 15)) return true
		
		return world.provider.dimensionId == dimensionIDAlfheim
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun matches(recipe: AesirRingRecipe, inv: InventoryCrafting, world: World?): Boolean {
		val crafter = (inv.eventHandler as? ContainerWorkbench)?.alfheim_synthetic_thePlayer ?: return false
		if (PlayerHandler.getPlayerBaubles(crafter)[0]?.item !== AlfheimItems.aesirEmblem) return false
		
		var foundThorRing = false
		var foundSifRing = false
		var foundNjordRing = false
		var foundLokiRing = false
		var foundHeimdallRing = false
		var foundOdinRing = false
		
		for (i in 0 until inv.sizeInventory) {
			val stack = inv[i] ?: continue
			
			if (stack.item === ModItems.thorRing && !foundThorRing) foundThorRing = true else
			if (stack.item === AlfheimItems.priestRingSif && !foundSifRing) foundSifRing = true else
			if (stack.item === AlfheimItems.priestRingNjord && !foundNjordRing) foundNjordRing = true else
			if (stack.item === ModItems.lokiRing && !foundLokiRing) foundLokiRing = true else
			if (stack.item === AlfheimItems.priestRingHeimdall && !foundHeimdallRing) foundHeimdallRing = true else
			if (stack.item === ModItems.odinRing && !foundOdinRing) foundOdinRing = true else
			return false // Found an invalid item, breaking the recipe
		}
		
		return foundThorRing && foundSifRing && foundNjordRing && foundLokiRing && foundHeimdallRing && foundOdinRing
	}
	
	val specialHeads by lazy { arrayOf("AlexSocol", "Vazkii", getCurrentNickname("yrsegal"), getCurrentNickname("l0nekitsune"), getCurrentNickname("Tristaric")) }
	
	@JvmStatic
	@Hook
	fun getOutput(recipe: HeadRecipe) {
		if (recipe.name in specialHeads)
			recipe.name = ""
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun getRecipes(rh: RecipeHandlerPetalApothecary): List<RecipePetals> {
		return BotaniaAPI.petalRecipes.filter { alexsocol.asjlib.ItemNBTHelper.getString(it.output, "SkullOwner", "") !in specialHeads }
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun onItemUse(eye: ItemFlugelEye, stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float) =
		// Stupid Et Futurum
		if (player.isSneaking) EntityFlugel.spawn(player, stack, world, x, y, z, false, false) else false
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS, createMethod = true)
	fun onItemUse(item: ItemBottledMana, stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (stack.meta > 0) return false
		
		var x = x
		var y = y
		var z = z
		when (side) {
			0 -> --y
			1 -> ++y
			2 -> --z
			3 -> ++z
			4 -> --x
			5 -> ++x
		}
		
		val at = world.getBlock(x, y, z)
		if (!player.canPlayerEdit(x, y, z, side, stack) || !at.isReplaceable(world, x, y, z)) return false
		if (at === AlfheimBlocks.manaFluidBlock && world.getBlockMetadata(x, y, z) == 0) return false
		
		world.setBlock(x, y, z, AlfheimBlocks.manaFluidBlock)
		stack.stackSize--
		
		val bottle = ItemStack(Items.glass_bottle)
		if (!player.inventory.addItemStackToInventory(bottle))
			player.dropPlayerItemWithRandomChoice(bottle, false)
		
		return true
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun addBindInfo(static: ItemRelic?, list: List<String>, stack: ItemStack, player: EntityPlayer?) {
		if (GuiScreen.isShiftKeyDown()) {
			val bind = ItemRelic.getSoulbindUsernameS(stack)
			
			if (bind.isEmpty())
				ItemRelic.addStringToTooltip(StatCollector.translateToLocal("botaniamisc.relicUnbound"), list)
			else {
				ItemRelic.addStringToTooltip(String.format(StatCollector.translateToLocal("botaniamisc.relicSoulbound"), bind), list)
				
				if (!ItemRelic.isRightPlayer(player, stack))
					ItemRelic.addStringToTooltip(String.format(StatCollector.translateToLocal("botaniamisc.notYourSagittarius"), bind), list)
			}
			
			if (stack.item === ModItems.aesirRing)
				ItemRelic.addStringToTooltip(StatCollector.translateToLocal("botaniamisc.dropIkea"), list)
			
			val name = stack.unlocalizedName + ".poem"
			if (StatCollector.canTranslate("${name}0")) {
				ItemRelic.addStringToTooltip("", list)
				
				for (i in 0..3)
					ItemRelic.addStringToTooltip(EnumChatFormatting.ITALIC.toString() + StatCollector.translateToLocal(name + i), list)
			}
		} else ItemRelic.addStringToTooltip(StatCollector.translateToLocal("botaniamisc.shiftinfo"), list)
	}
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ALWAYS)
	fun onItemRightClick(item: ItemGaiaHead, stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (player.getCurrentArmor(3) == null) player.setCurrentItemOrArmor(4, stack.splitStack(1))
		return stack
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun getFortuneModifier(h: EnchantmentHelper?, e: EntityLivingBase) =
		EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, e.heldItem) + if (AlfheimConfigHandler.enableMMO && e.isPotionActive(AlfheimConfigHandler.potionIDGoldRush)) SpellGoldRush.efficiency.I else 0
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun getSubBlocks(block: BlockAltar, item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		list.add(ItemStack(item, 1, 9))
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun getIcon(block: BlockAltar, side: Int, meta: Int): IIcon =
		if (meta == 9) AlfheimBlocks.livingcobble.getIcon(0, 0) else if (meta in 1..8) ModFluffBlocks.biomeStoneA.getIcon(side, meta + 7) else Blocks.cobblestone.getIcon(side, meta)
	
	@JvmStatic
	@Hook(targetMethod = "collideEntityItem")
	fun collideEntityItemPre(tile: TileAltar, item: EntityItem): Boolean {
		val stack = item.entityItem
		if (stack == null || item.isDead) return false
		
		if (tile.hasWater() || tile.hasLava()) return false
		
		if (stack.item === ModItems.waterBowl && !tile.worldObj.isRemote) {
			tile.setWater(true)
			tile.worldObj.func_147453_f(tile.xCoord, tile.yCoord, tile.zCoord, tile.worldObj.getBlock(tile.xCoord, tile.yCoord, tile.zCoord))
			stack.func_150996_a(Items.bowl)
			
			return false
		}
		
		return true
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "collideEntityItem")
	fun collideEntityItemPost(tile: TileAltar, item: EntityItem, @ReturnValue res: Boolean): Boolean {
		item.setEntityItemStack(item.entityItem)
		
		return res
	}
	
	private var renderingTile = false
	
	@JvmStatic
	@Hook
	fun renderTileEntityAt(renderer: RenderTileAltar, tile: TileEntity, x: Double, y: Double, z: Double, pticks: Float) {
		val blockMeta = if (tile.blockMetadata == -1) {
			tile.worldObj?.getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord) ?: 0
		} else tile.blockMetadata
		
		if (RenderTileAltar.forceMeta == 9 || blockMeta == 9)
			renderingTile = true
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun bindTexture(tm: TextureManager, loc: ResourceLocation?): Boolean {
		if (renderingTile) {
			renderingTile = false
			tm.bindTexture(LibResourceLocations.altar9)
			return true
		}
		return false
	}
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun hasSearchBar(tab: BotaniaCreativeTab) = AlfheimConfigHandler.searchTabBotania
	
	var chunkCoors = Int.MAX_VALUE to Int.MAX_VALUE
	
	@JvmStatic
	@Hook(targetMethod = "getChunkFromBlockCoords")
	fun getChunkFromBlockCoords(world: World, x: Int, z: Int) {
		chunkCoors = x to z
	}
	
	var replace = false
	
	@JvmStatic
	@Hook
	fun getCanSpawnHere(entity: EntityAnimal): Boolean {
		replace = entity.worldObj.provider.dimensionId == dimensionIDAlfheim
		return replace
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ALWAYS)
	fun getBlock(world: World, x: Int, y: Int, z: Int, @ReturnValue block: Block): Block {
		if (replace && (block === AlfheimBlocks.snowGrass || block === AlfheimBlocks.snowLayer || block === Blocks.snow_layer)) {
			replace = false
			return Blocks.grass
		}
		return block
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ALWAYS)
	fun getBiomeGenForWorldCoords(c: Chunk, x: Int, z: Int, cm: WorldChunkManager, @ReturnValue oldBiome: BiomeGenBase): BiomeGenBase? {
		if (chunkCoors.first != Int.MAX_VALUE || chunkCoors.second != Int.MAX_VALUE) {
			val biome = WE_Biome.getBiomeAt((cm as? WE_WorldChunkManager ?: return oldBiome).cp, chunkCoors.first.toLong(), chunkCoors.second.toLong())
			chunkCoors = Int.MAX_VALUE to Int.MAX_VALUE
			return biome
		} else
			return oldBiome
	}
	
	@SideOnly(CLIENT)
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ALWAYS)
	fun getItemIconName(block: BlockGaiaHead) = "${LibResources.PREFIX_MOD}gaiaHead"
	
	// chunkloading fix
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	@Suppress("INACCESSIBLE_TYPE")
	fun getManaPool(mirror: ItemManaMirror, stack: ItemStack): IManaPool? {
		val server = MinecraftServer.getServer() ?: return ItemManaMirror.fallbackPool
		
		val dim = mirror.getDimension(stack)
		val world = server.worldServerForDimension(dim) ?: return null
		
		val (x, y, z) = mirror.getPoolCoords(stack)
		if (!world.blockExists(x, y, z)) return null // here
		
		return world.getTileEntity(x, y, z) as? IManaPool
	}
	
	// chunkloading fix
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun getBinding(mirror: ItemManaMirror, stack: ItemStack): ChunkCoordinates? {
		val world = mc.theWorld ?: return null
		
		if (world.provider.dimensionId != ItemNBTHelper.getInt(stack, "dim", Int.MAX_VALUE)) return null
		
		val coords = mirror.getPoolCoords(stack)
		val (x, y, z) = coords
		if (!world.blockExists(x, y, z)) return null // here
		
		return if (world.getTileEntity(x, y, z) is IManaPool) coords else null
	}
	
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ALWAYS)
	fun getDamage(item: ItemManaMirror, stack: ItemStack, @ReturnValue result: Int) = result.clamp(0, item.getMaxDamage(stack))
	
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ALWAYS)
	fun getManaFractionForDisplay(item: ItemManaMirror, stack: ItemStack, @ReturnValue result: Float) = result.clamp(0f, 1f - Float.MIN_VALUE)
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun getColorFromItemStack(item: ItemManaMirror, stack: ItemStack, pass: Int): Int {
		val mana = item.getMana(stack).F
		return if (pass == 1) Color.HSBtoRGB(0.528f, (mana / TilePool.MAX_MANA).clamp(0f, 1f), 1f) else 0xFFFFFF
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun canFit(item: ItemManaResource, stack: ItemStack, apothecary: IInventory?): Boolean {
		return stack.meta == 9 ||    // Dragonstone
			   stack.meta == 15        // Ender Air Bottle
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun renderManaInvBar(hh: HUDHandler, res: ScaledResolution, hasCreative: Boolean, totalMana: Int, totalMaxMana: Int): Boolean {
		if (totalMana > totalMaxMana) {
			hh.renderManaInvBar(res, hasCreative, totalMana, totalMana)
			return true
		}
		
		return false
	}
	
	var moveText = false
	
	@JvmStatic
	@Hook
	fun drawSimpleManaHUD(hh: HUDHandler?, color: Int, mana: Int, maxMana: Int, name: String?, res: ScaledResolution) {
		moveText = mana >= 0
	}
	
	var numMana = true
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun renderManaBar(hh: HUDHandler?, x: Int, y: Int, color: Int, alpha: Float, mana: Int, maxMana: Int) {
		if (mana < 0 || !AlfheimConfigHandler.numericalMana || !numMana) return
		glPushMatrix()
		
		val text = "$mana/$maxMana"
		val x = x + 51 - mc.fontRenderer.getStringWidth(text) / 2
		val y = y - mc.fontRenderer.FONT_HEIGHT
		mc.fontRenderer.drawString(text, x, y, color, mc.currentScreen == null)
		glPopMatrix()
	}
	
	@SideOnly(CLIENT)
	@JvmStatic
	@Hook
	fun renderOverlays(renderer: ItemRenderer, partialTicks: Float) {
		if (mc.thePlayer.isPotionActive(AlfheimConfigHandler.potionIDSoulburn)) {
			glDisable(GL_ALPHA_TEST)
			PotionSoulburn.renderFireInFirstPerson()
			glEnable(GL_ALPHA_TEST)
		}
	}
	
	var renderingBoss = false
	
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun setCurrentBoss(handler: BossBarHandler?, status: IBotaniaBoss?) {
		BossBarHandler.currentBoss = if (AlfheimConfigHandler.enableMMO && AlfheimConfigHandler.targetUI) null else status
	}
	
	@JvmStatic
	@Hook
	fun render(handler: BossBarHandler?, res: ScaledResolution) {
		if (BossBarHandler.currentBoss == null) return
		renderingBoss = true
	}
	
	@JvmStatic
	@Hook
	fun translateToLocal(sc: StatCollector?, text: String?): String? {
		if (text == "botaniamisc.manaUsage")
			moveText = true
		
		return text
	}
	
	@SideOnly(CLIENT)
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun drawString(font: FontRenderer, string: String?, x: Int, y: Int, color: Int): Int {
		return font.drawString(string, x, y - if (moveText) font.FONT_HEIGHT + 1 else 0, color, false).also { moveText = false }
	}
	
	@SideOnly(CLIENT)
	@JvmStatic
	@Hook(returnCondition = ALWAYS)
	fun drawStringWithShadow(font: FontRenderer, string: String?, x: Int, y: Int, color: Int): Int {
		val ny = y - if (moveText) font.FONT_HEIGHT + 1 else 0
		moveText = false
		
		if (renderingBoss) { // fix for FontRenderer being called before Botania init
			val boss = BossBarHandler.currentBoss
			
			if (color == 0xA2018C && (boss is IBotaniaBossWithName || boss is IBotaniaBossWithShaderAndName)) {
				val color = if (boss is IBotaniaBossWithName) boss.getNameColor() else if (boss is IBotaniaBossWithShaderAndName) boss.getNameColor() else 0
				val result = font.drawString(string, x, ny, color, true)
				renderingBoss = false
				glColor4f(1f, 1f, 1f, 1f)
				return result
			}
		}
		
		val result = font.drawString(string, x, ny, color, true)
		glColor4f(1f, 1f, 1f, 1f)
		return result
	}
	
	@JvmStatic
	@Hook // for blue line above item tooltip
	fun drawManaBar(handler: TooltipAdditionDisplayHandler?, stack: ItemStack, display: IManaTooltipDisplay, mouseX: Int, mouseY: Int, offx: Int, offy: Int, width: Int, height: Int) {
		val item = stack.item
		
		if (item is IManaItem && AlfheimConfigHandler.numericalMana) {
			glDisable(GL_DEPTH_TEST)
			mc.fontRenderer.drawStringWithShadow("${item.getMana(stack)}/${item.getMaxMana(stack)}", mouseX + offx - 1, mouseY - offy - height - 1 - mc.fontRenderer.FONT_HEIGHT, Color.HSBtoRGB(0.528f, (sin((ClientTickHandler.ticksInGame.F + ClientTickHandler.partialTicks).D * 0.2).F + 1f) * 0.3f + 0.4f, 1f))
			glEnable(GL_DEPTH_TEST)
		}
	}
	
	var wispNoclip = true
	
	@JvmStatic
	@Hook(targetMethod = "<init>", injectOnExit = true)
	fun `FXWisp$init`(fx: FXWisp, world: World?, d: Double, d1: Double, d2: Double, size: Float, red: Float, green: Float, blue: Float, distanceLimit: Boolean, depthTest: Boolean, maxAgeMul: Float) {
		fx.noClip = wispNoclip
	}
	
	@JvmStatic
	@Hook(targetMethod = "<clinit>", injectOnExit = true)
	fun `FXWisp$clinit`(fx: FXWisp?) {
		FXWisp.queuedRenders = PriorityQueue { fx1, fx2 -> fx1.blendmode.compareTo(fx2.blendmode) }
		FXWisp.queuedDepthIgnoringRenders = PriorityQueue { fx1, fx2 -> fx1.blendmode.compareTo(fx2.blendmode) }
	}
	
	var prevBlendMode = -1
	
	@JvmStatic
	@Hook
	fun renderQueued(fx: FXWisp, tessellator: Tessellator?, depthEnabled: Boolean) {
		if (fx.blendmode != prevBlendMode) glBlendFunc(GL_SRC_ALPHA, fx.blendmode)
	}
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun dispatchQueuedRenders(fx: FXWisp?, tessellator: Tessellator?) {
		prevBlendMode = -1
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun renderQueued(fx: FXSparkle, tessellator: Tessellator): Boolean {
		return fx.dimension != mc.thePlayer.dimension
	}
	
	@SideOnly(CLIENT)
	@JvmStatic
	@Hook(injectOnExit = true, returnCondition = ALWAYS)
	fun isInvisibleToPlayer(player: EntityPlayer, thePlayer: EntityPlayer?, @ReturnValue result: Boolean): Boolean {
		if (result && AlfheimConfigHandler.enableMMO && CardinalSystemClient.PlayerSegmentClient.party?.isMember(player) == true)
			return false
		
		return result
	}
	
	@SideOnly(CLIENT)
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ALWAYS)
	fun isInvisibleToPlayer(entity: EntityLivingBase, thePlayer: EntityPlayer?): Boolean {
		if (AlfheimConfigHandler.enableMMO && CardinalSystemClient.PlayerSegmentClient.party?.isMember(entity) == true)
			return false
		
		return entity.isInvisible
	}
	
	@SideOnly(CLIENT)
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun updatePlayerMoveState(input: MovementInputFromOptions): Boolean {
		if (mc.thePlayer.capabilities.isCreativeMode || mc.thePlayer.cold > -90f) return false
		if (ItemPendant.canProtect(mc.thePlayer, MUSPELHEIM, 0)) return false
		
		input.moveStrafe = 0.0f
		input.moveForward = 0.0f
		
		val states = mc.gameSettings.run { arrayOf(keyBindForward, keyBindBack, keyBindLeft, keyBindRight, keyBindJump, keyBindSneak).map { it.isKeyPressed } }.toMutableList()
		
		operator fun <T> List<T>.component6() = this[5]
		states.shuffle(Random(mc.thePlayer.ticksExisted / 200L))
		val (w, a, s, d, space, shift) = states
		
		if (w) ++input.moveForward
		if (a) ++input.moveStrafe
		if (s) --input.moveForward
		if (d) --input.moveStrafe
		
		input.jump = space
		input.sneak = shift
		
		if (input.sneak) {
			input.moveStrafe = input.moveStrafe * 0.3f
			input.moveForward = input.moveForward * 0.3f
		}
		
		return true
	}
	
	@JvmStatic
	@Hook(targetMethod = "getEntryFromForce")
	fun getEntryFromForcePre(static: ItemLexicon?, stack: ItemStack): LexiconEntry? {
		forceHack = true
		return null
	}
	
	var forceHack = false
	
	@JvmStatic
	@Hook(targetMethod = "getEntryFromForce", injectOnExit = true)
	fun getEntryFromForcePost(static: ItemLexicon?, stack: ItemStack, @ReturnValue result: LexiconEntry?): LexiconEntry? {
		forceHack = false
		return result
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE, floatReturnConstant = 0.5f)
	fun calculateCelestialAngle(provider: WorldProvider, worldTicks: Long, partialTicks: Float) = noSunAndMoon
	
	@SideOnly(CLIENT)
	@JvmStatic
	@Hook(targetMethod = "renderSky")
	fun renderSkyPre(rg: RenderGlobal, partialTicks: Float) {
		if (!noSunAndMoon) return
		
		locationMoonPhasesPng = RenderGlobal.locationMoonPhasesPng
		locationSunPng = RenderGlobal.locationSunPng
		
		RenderGlobal.locationMoonPhasesPng = LibResourceLocations.blank
		RenderGlobal.locationSunPng = LibResourceLocations.blank
	}
	
	lateinit var locationMoonPhasesPng: ResourceLocation
	lateinit var locationSunPng: ResourceLocation
	
	@SideOnly(CLIENT)
	@JvmStatic
	@Hook(targetMethod = "renderSky", injectOnExit = true)
	fun renderSkyPost(rg: RenderGlobal, partialTicks: Float) {
		if (!noSunAndMoon) return
		
		RenderGlobal.locationMoonPhasesPng = locationMoonPhasesPng
		RenderGlobal.locationSunPng = locationSunPng
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE)
	fun addComponentParts(end: StructureNetherBridgePieces.End, world: World, random: Random, bb: StructureBoundingBox?): Boolean {
		if (world.isRemote) return false
		
		val data = world.customData
		if (data.structures.containsKey("Surtr")) return false
		if (!ASJUtilities.chance(10)) return false
		
		var x = end.x(2, 0)
		val y = end.y(5)
		var z = end.z(2, 0)
		
		when (end.coordBaseMode) {
			0 -> if (cantGen(world, x, y, z)) return false
			1 -> if (cantGen(world, x - 23, y, z - 23)) return false
			2 -> if (cantGen(world, x, y, z - 46)) return false
			3 -> if (cantGen(world, x + 23, y, z - 23)) return false
		}
		
		when (end.coordBaseMode) {
			0 -> {
				SchemaUtils.generate(world, x, y, z + 6, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrBridgeNS"))
				
				SchemaUtils.generate(world, x, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrBridgeEXP"))
				SchemaUtils.generate(world, x, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrBridgeWXN"))
				SchemaUtils.generate(world, x, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrBridgeSZP"))
			}
			1 -> {
				SchemaUtils.generate(world, x - 6, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrBridgeEW"))
				
				x -= 23
				z -= 23
				
				SchemaUtils.generate(world, x, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrBridgeNZN"))
				SchemaUtils.generate(world, x, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrBridgeWXN"))
				SchemaUtils.generate(world, x, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrBridgeSZP"))
			}
			2 -> {
				SchemaUtils.generate(world, x, y, z - 6, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrBridgeNS"))
				
				z -= 46
				
				SchemaUtils.generate(world, x, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrBridgeNZN"))
				SchemaUtils.generate(world, x, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrBridgeEXP"))
				SchemaUtils.generate(world, x, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrBridgeWXN"))
			}
			3 -> {
				SchemaUtils.generate(world, x + 6, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrBridgeEW"))
				
				x += 23
				z -= 23
				
				SchemaUtils.generate(world, x, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrBridgeNZN"))
				SchemaUtils.generate(world, x, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrBridgeEXP"))
				SchemaUtils.generate(world, x, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrBridgeSZP"))
			}
		}
		
		SchemaUtils.generate(world, x, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/SurtrTower"))
		ASJUtilities.fillGenHoles(world, Blocks.nether_brick, 0, x, y - 9, z + 23, 11)
		
		data.structures.put("Surtr", x to z)
		data.data["SurtrY"] = y.toString()
		data.markDirty()
		
		val loot = arrayOf(WeightedRandomChestContent(Items.diamond, 0, 1, 3, 5), WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 5), WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 15), WeightedRandomChestContent(Items.golden_sword, 0, 1, 1, 5), WeightedRandomChestContent(Items.golden_chestplate, 0, 1, 1, 5), WeightedRandomChestContent(Items.flint_and_steel, 0, 1, 1, 5), WeightedRandomChestContent(Items.nether_wart, 0, 3, 7, 5), WeightedRandomChestContent(Items.saddle, 0, 1, 1, 10), WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 8), WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 5), WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 3))
		val lootRare = arrayOf(WeightedRandomChestContent(Items.diamond, 0, 1, 3, 5), WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 5), WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 15), WeightedRandomChestContent(Items.golden_sword, 0, 1, 1, 5), WeightedRandomChestContent(Items.golden_chestplate, 0, 1, 1, 5), WeightedRandomChestContent(Items.flint_and_steel, 0, 1, 1, 5), WeightedRandomChestContent(Items.nether_wart, 0, 3, 7, 5), WeightedRandomChestContent(Items.saddle, 0, 1, 1, 10), WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 8), WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 5), WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 3))
		val rand = world.rand
		
		arrayOf(arrayOf(-4, 23), arrayOf(0, 27), arrayOf(4, 23)).forEach { (i, k) ->
			val chest = world.getTileEntity(x + i, y, z + k) as? TileEntityChest ?: return@forEach
			WeightedRandomChestContent.generateChestContents(rand, loot, chest, ASJUtilities.randInBounds(2, 5, rand))
		}
		
		(world.getTileEntity(x, y, z + 23) as? TileEntityChest)?.let { chest ->
			WeightedRandomChestContent.generateChestContents(rand, lootRare, chest, ASJUtilities.randInBounds(6, 10, rand))
		}
		
		(world.getTileEntity(x, y - 5, z + 30) as TileDomainLobby).apply {
			lock(x, y - 5, z + 30, -1)
			name = "Surtr"
		}
		
		return true
	}
	
	fun cantGen(world: World, x: Int, y: Int, z: Int): Boolean {
		for (i in -17..17)
			for (j in 7..41)
				for (k in -10..20)
					if (!world.isAirBlock(x, y, z)) return true
		
		return false
	}
	
	fun StructureNetherBridgePieces.End.x(x: Int, z: Int): Int {
		return when (this.coordBaseMode) {
			0, 2 -> this.boundingBox.minX + x
			1    -> this.boundingBox.maxX - z
			3    -> this.boundingBox.minX + z
			else -> x
		}
	}
	
	fun StructureNetherBridgePieces.End.y(y: Int): Int {
		return if (this.coordBaseMode == -1) y else y + this.boundingBox.minY
	}
	
	fun StructureNetherBridgePieces.End.z(x: Int, z: Int): Int {
		return when (this.coordBaseMode) {
			0    -> this.boundingBox.minZ + z
			1, 3 -> this.boundingBox.minZ + x
			2    -> this.boundingBox.maxZ - z
			else -> z
		}
	}
	
	@JvmStatic
	@Hook(targetMethod = "<clinit>", injectOnExit = true)
	fun `ItemTerraformRod$clinit`(static: ItemTerraformRod?) {
		val newList = ArrayList(ItemTerraformRod.validBlocks)
		newList.add("livingrock")
		newList.add("grassSnow")
		ItemTerraformRod.validBlocks = newList
	}
	
	// Wrath of the Winter hooks
	
	@JvmStatic
	@Hook(injectOnExit = true, targetMethod = "onImpact")
	fun spawnSpriteFromSnowball(ball: EntitySnowball, mop: MovingObjectPosition) {
		if (WRATH_OF_THE_WINTER) {
			if (!ball.worldObj.isRemote && mop.entityHit == null && ball.worldObj.isRaining && ball.worldObj.rand.nextInt(32) == 0) {
				val sprite = EntitySnowSprite(ball.worldObj)
				sprite.setPosition(ball.posX, ball.posY + 1, ball.posZ)
				
				if (sprite.canSpawnHere) sprite.spawn()
			}
		}
	}
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ALWAYS, targetMethod = "getRelativeSlipperiness")
	fun icyFloorForBoots(block: Block, requester: Entity): Float {
		return if (requester is EntityPlayer && !requester.isSneaking && (AlfheimItems.snowHelmet as ItemSnowArmor).hasArmorSet(requester)) 0.99f else block.slipperiness
	}
	
	// Hellish Vacation hooks
	
	var replaceMelonWithMob = false
	
	@JvmStatic
	@Hook(targetMethod = "updateTick")
	fun replaceMelonWithMobPre(block: BlockStem, world: World?, x: Int, y: Int, z: Int, rand: Random?) {
		replaceMelonWithMob = HELLISH_VACATION && block === Blocks.melon_stem
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE, booleanReturnConstant = false, targetMethod = "setBlock")
	fun replaceMelonWithMob(world: World, x: Int, y: Int, z: Int, block: Block?): Boolean {
		if (replaceMelonWithMob && block === Blocks.melon_block && world.rand.nextInt(10) == 0) {
			replaceMelonWithMob = false
			if (world.isRemote) return true
			return EntityRollingMelon(world).apply { setPosition(x + 0.5, y + 0.5, z + 0.5); onSpawnWithEgg(null) }.spawn()
		}
		
		return false
	}
	
	@JvmStatic
	@Hook(targetMethod = "updateTick", injectOnExit = true)
	fun replaceMelonWithMobPost(block: BlockStem, world: World?, x: Int, y: Int, z: Int, rand: Random?) {
		replaceMelonWithMob = false
	}
	
	@JvmStatic
	@Hook(targetMethod = "<init>", injectOnExit = true)
	fun spawnMuspelsonsInNetherFortress(gen: MapGenNetherBridge) {
		gen.spawnList.add(BiomeGenBase.SpawnListEntry(EntityMuspelson::class.java, 6, 2, 3))
	}
	
	@JvmStatic
	@Hook(returnCondition = ON_TRUE, returnAnotherMethod = "getDigSpeed")
	fun func_150893_a(item: ItemPickaxe, stack: ItemStack?, block: Block) = block.material === Material.glass
	@JvmStatic
	fun getDigSpeed(item: ItemPickaxe, stack: ItemStack?, block: Block) = item.func_150913_i().efficiencyOnProperMaterial
	
	@JvmStatic
	@Hook(targetMethod = "onPlayerInteract")
	fun onPlayerInteractPre(item: ItemManaResource, event: PlayerInteractEvent?) {
		hookRaytrace = true
	}
	
	var hookRaytrace = false
	
	@JvmStatic
	@Hook(returnCondition = ON_NOT_NULL)
	fun raytraceFromEntity(static: ToolCommons?, world: World?, player: Entity?, stopOnLiquid: Boolean, range: Double): MovingObjectPosition? {
		if (!hookRaytrace) return null
		hookRaytrace = false
		
		return ToolCommons.raytraceFromEntity(world, player, true, range)
	}
	
	@JvmStatic
	@Hook(targetMethod = "onPlayerInteract", injectOnExit = true)
	fun onPlayerInteractPost(item: ItemManaResource, event: PlayerInteractEvent?) {
		hookRaytrace = false
	}
}
