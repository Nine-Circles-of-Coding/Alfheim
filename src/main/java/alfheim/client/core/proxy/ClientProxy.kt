package alfheim.client.core.proxy

import alexsocol.asjlib.*
import alexsocol.asjlib.render.*
import alfheim.AlfheimCore
import alfheim.api.event.AlfheimModeChangedEvent
import alfheim.api.item.DoubleBoundItemRender
import alfheim.api.lib.*
import alfheim.client.core.handler.*
import alfheim.client.core.handler.CardinalSystemClient.TimeStopSystemClient
import alfheim.client.core.util.AlfheimBotaniaModifiersClient
import alfheim.client.gui.*
import alfheim.client.lib.LibResourceLocationsActual
import alfheim.client.render.block.*
import alfheim.client.render.entity.*
import alfheim.client.render.item.*
import alfheim.client.render.particle.*
import alfheim.client.render.tile.*
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.tile.*
import alfheim.common.core.asm.hook.extender.FurnaceHandler
import alfheim.common.core.handler.*
import alfheim.common.core.proxy.CommonProxy
import alfheim.common.crafting.recipe.AlfheimRecipes
import alfheim.common.entity.*
import alfheim.common.entity.EntitySubspace
import alfheim.common.entity.EntitySubspaceSpear
import alfheim.common.entity.boss.*
import alfheim.common.entity.boss.primal.*
import alfheim.common.entity.item.EntityItemImmortal
import alfheim.common.entity.spell.*
import alfheim.common.integration.travellersgear.TGHandlerBotaniaRenderer
import alfheim.common.item.AlfheimItems
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.client.registry.*
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.client.settings.KeyBinding
import net.minecraft.world.World
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.common.MinecraftForge
import org.apache.commons.lang3.ArrayUtils
import org.lwjgl.input.Keyboard
import vazkii.botania.client.core.helper.ShaderHelper
import vazkii.botania.client.core.proxy.ClientProxy
import vazkii.botania.client.fx.FXSparkle
import vazkii.botania.client.render.item.RenderLens
import vazkii.botania.client.render.tile.RenderTileFloatingFlower
import vazkii.botania.common.block.tile.TileFloatingFlower
import vazkii.botania.common.core.handler.ConfigHandler

object ClientProxy : CommonProxy() {
	
	override fun preInit() {
		super.preInit()
		LibResourceLocationsActual.init()
	}
	
	override fun registerRenderThings() {
		ClientProxy.jingleTheBells = AlfheimCore.jingleTheBells
		
		LibRenderIDs
		
		if (ShaderHelper.useShaders()) {
			LibShaderIDs.idColor3d = ASJShaderHelper.createProgram("shaders/position.vert", "shaders/color3d.frag")
			LibShaderIDs.idGravity = ASJShaderHelper.createProgram(null, "shaders/gravity.frag")
			LibShaderIDs.idNoise = ASJShaderHelper.createProgram("shaders/position.vert", "shaders/noise4d.frag")
			LibShaderIDs.idShadow = ASJShaderHelper.createProgram(null, "shaders/shadow.frag")
			LibShaderIDs.idSun = ASJShaderHelper.createProgram("shaders/position.vert", "shaders/sun.frag")
			LibShaderIDs.idWorley = ASJShaderHelper.createProgram("shaders/position.vert", "shaders/worley.frag")
		}
		
		ClientRegistry.registerKeyBinding(keyLolicorn)
		
		MinecraftForgeClient.registerItemRenderer(AlfheimItems.akashicRecords, RenderItemAkashicRecords)
		MinecraftForgeClient.registerItemRenderer(AlfheimBlocks.anomaly.toItem(), RenderItemAnomaly)
		MinecraftForgeClient.registerItemRenderer(AlfheimItems.fenrirClaws, RenderItemFenrirClaws)
		MinecraftForgeClient.registerItemRenderer(AlfheimItems.invisibleFlameLens, RenderLens())
		MinecraftForgeClient.registerItemRenderer(AlfheimItems.moonlightBow, RenderMoonBow)
		MinecraftForgeClient.registerItemRenderer(AlfheimBlocks.rainbowFlowerFloating.toItem(), RenderFloatingFlowerRainbowItem)
		MinecraftForgeClient.registerItemRenderer(AlfheimItems.royalStaff, RenderItemRoyalStaff)
		MinecraftForgeClient.registerItemRenderer(AlfheimItems.surtrSword, RenderItemSurtrSword)
		MinecraftForgeClient.registerItemRenderer(AlfheimItems.thrymAxe, RenderItemThrymAxe)
		MinecraftForgeClient.registerItemRenderer(AlfheimBlocks.yggFlower.toItem(), RenderItemYggFlower)
		
		if (!AlfheimConfigHandler.minimalGraphics) {
			MinecraftForgeClient.registerItemRenderer(AlfheimItems.mjolnir, RenderItemMjolnir)
			MinecraftForgeClient.registerItemRenderer(AlfheimItems.snowSword, RenderItemSnowSword)
		}
		
		RenderingRegistry.registerBlockHandler(LibRenderIDs.idAnyavil, RenderBlockAnyavil)
		RenderingRegistry.registerBlockHandler(LibRenderIDs.idBarrel, RenderBlockBarrel)
		RenderingRegistry.registerBlockHandler(LibRenderIDs.idDomainDoor, RenderBlockDomainLobby)
		RenderingRegistry.registerBlockHandler(LibRenderIDs.idGrapeRedPlanted, RenderBlockGrapeRedPlanted)
		RenderingRegistry.registerBlockHandler(LibRenderIDs.idGrapeWhite, RenderBlockGrapeGreen)
		RenderingRegistry.registerBlockHandler(LibRenderIDs.idHarvester, RenderBlockAnomalyHarvester)
		RenderingRegistry.registerBlockHandler(LibRenderIDs.idManaAccelerator, RenderBlockItemHolder)
		RenderingRegistry.registerBlockHandler(LibRenderIDs.idNiflheim, RenderBlockNiflheimSet)
		RenderingRegistry.registerBlockHandler(LibRenderIDs.idPowerStone, RenderBlockPowerStone)
		RenderingRegistry.registerBlockHandler(LibRenderIDs.idPylon, RenderBlockAlfheimPylons)
		RenderingRegistry.registerBlockHandler(LibRenderIDs.idShrinePanel, RenderBlockShrinePanel)
		RenderingRegistry.registerBlockHandler(LibRenderIDs.idSpire, RenderBlockSpire)
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileAlfheimPortal::class.java, RenderTileAlfheimPortal)
		ClientRegistry.bindTileEntitySpecialRenderer(TileAlfheimPylon::class.java, RenderTileAlfheimPylons)
		ClientRegistry.bindTileEntitySpecialRenderer(TileAnimatedTorch::class.java, RenderTileAnimatedTorch)
		ClientRegistry.bindTileEntitySpecialRenderer(TileAnomaly::class.java, RenderTileAnomaly)
		ClientRegistry.bindTileEntitySpecialRenderer(TileAnomalyHarvester::class.java, RenderTileAnomalyHarvester)
		ClientRegistry.bindTileEntitySpecialRenderer(TileAnyavil::class.java, RenderTileAnyavil)
		ClientRegistry.bindTileEntitySpecialRenderer(TileBarrel::class.java, RenderTileBarrel)
		ClientRegistry.bindTileEntitySpecialRenderer(TileDomainLobby::class.java, RenderTileDomainLobby)
		ClientRegistry.bindTileEntitySpecialRenderer(TileFloatingFlowerRainbow::class.java, TileEntityRendererDispatcher.instance.mapSpecialRenderers[TileFloatingFlower::class.java] as RenderTileFloatingFlower)
		ClientRegistry.bindTileEntitySpecialRenderer(TileHeadFlugel::class.java, RenderTileHeadFlugel)
		ClientRegistry.bindTileEntitySpecialRenderer(TileHeadMiku::class.java, RenderTileHeadMiku)
		ClientRegistry.bindTileEntitySpecialRenderer(TileItemDisplay::class.java, RenderTileItemDisplay)
		ClientRegistry.bindTileEntitySpecialRenderer(TileManaAccelerator::class.java, RenderTileManaAccelerator)
		ClientRegistry.bindTileEntitySpecialRenderer(TilePowerStone::class.java, RenderTilePowerStone)
		ClientRegistry.bindTileEntitySpecialRenderer(TileRaceSelector::class.java, RenderTileRaceSelector)
		ClientRegistry.bindTileEntitySpecialRenderer(TileSpire::class.java, RenderTileSpire)
		ClientRegistry.bindTileEntitySpecialRenderer(TileStar::class.java, RenderStar)
		ClientRegistry.bindTileEntitySpecialRenderer(TileTradePortal::class.java, RenderTileTradePortal)
		ClientRegistry.bindTileEntitySpecialRenderer(TileYggFlower::class.java, RenderTileYggFlower)
		
		RenderingRegistry.registerEntityRenderingHandler(EntityAlfheimPixie::class.java, RenderEntityAlfheimPixie)
		RenderingRegistry.registerEntityRenderingHandler(EntityBlackBolt::class.java, RenderEntityBlackBolt)
		RenderingRegistry.registerEntityRenderingHandler(EntityBlock::class.java, RenderEntityBlock)
		RenderingRegistry.registerEntityRenderingHandler(EntityButterfly::class.java, RenderEntityButterfly)
		RenderingRegistry.registerEntityRenderingHandler(EntityDedMoroz::class.java, RenderEntityDedMoroz)
		RenderingRegistry.registerEntityRenderingHandler(EntityElf::class.java, RenderEntityElf)
		RenderingRegistry.registerEntityRenderingHandler(EntityFenrir::class.java, RenderEntityFenrir)
		RenderingRegistry.registerEntityRenderingHandler(EntityFlugel::class.java, RenderEntityFlugel)
		RenderingRegistry.registerEntityRenderingHandler(EntityGleipnir::class.java, RenderEntityGleipnir)
		RenderingRegistry.registerEntityRenderingHandler(EntityGrieferCreeper::class.java, RenderEntityGrieferCreeper)
		RenderingRegistry.registerEntityRenderingHandler(EntityPrimalMark::class.java, RenderEntityPrimalMark)
		RenderingRegistry.registerEntityRenderingHandler(EntityIcicle::class.java, RenderEntityIcicle)
		RenderingRegistry.registerEntityRenderingHandler(EntityItemImmortal::class.java, RenderEntityItemImmortal)
		RenderingRegistry.registerEntityRenderingHandler(EntityLightningMark::class.java, RenderEntityLightningMark)
		RenderingRegistry.registerEntityRenderingHandler(EntityJellyfish::class.java, RenderEntityJellyfish)
		RenderingRegistry.registerEntityRenderingHandler(EntityLolicorn::class.java, RenderEntityLolicorn)
		RenderingRegistry.registerEntityRenderingHandler(EntityMjolnir::class.java, RenderEntityMjolnir)
		RenderingRegistry.registerEntityRenderingHandler(EntityMuspelheimSun::class.java, RenderEntityMuspelheimSun)
		RenderingRegistry.registerEntityRenderingHandler(EntityMuspelheimSunSlash::class.java, RenderEntityMuspelheimSunSlash)
		RenderingRegistry.registerEntityRenderingHandler(EntityMuspelson::class.java, RenderEntityMuspelson)
		RenderingRegistry.registerEntityRenderingHandler(EntityRollingMelon::class.java, RenderEntityRollingMelon)
		RenderingRegistry.registerEntityRenderingHandler(EntityRook::class.java, RenderEntityRook)
		RenderingRegistry.registerEntityRenderingHandler(EntitySniceBall::class.java, RenderEntitySniceBall)
		RenderingRegistry.registerEntityRenderingHandler(EntitySnowSprite::class.java, RenderEntitySnowSprite)
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellHarp::class.java, RenderEntityHarp)
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellDriftingMine::class.java, RenderEntityDriftingMine)
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellGravityTrap::class.java, RenderEntityGravityTrap)
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellFenrirStorm::class.java, RenderEntityFenrirStorm)
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellMortar::class.java, RenderEntityMortar)
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellWindBlade::class.java, RenderEntityWindBlade)
		RenderingRegistry.registerEntityRenderingHandler(EntitySubspace::class.java, RenderEntitySubspace)
		RenderingRegistry.registerEntityRenderingHandler(EntitySubspaceSpear::class.java, RenderEntitySubspaceSpear)
		RenderingRegistry.registerEntityRenderingHandler(EntitySurtr::class.java, RenderEntitySurtr)
		RenderingRegistry.registerEntityRenderingHandler(EntityThrownPotion::class.java, RenderEntityThrownPotion)
		RenderingRegistry.registerEntityRenderingHandler(EntityThrowableItem::class.java, RenderEntityThrownItem)
		RenderingRegistry.registerEntityRenderingHandler(EntityThrym::class.java, RenderEntityThrym)
		RenderingRegistry.registerEntityRenderingHandler(EntityThunderChakram::class.java, RenderEntityThunderChakram)
		RenderingRegistry.registerEntityRenderingHandler(EntityVoidCreeper::class.java, RenderEntityGrieferCreeper)
		RenderingRegistry.registerEntityRenderingHandler(FakeLightning::class.java, RenderFakeLightning)
		
		RenderingRegistry.registerBlockHandler(RenderBlockColoredDoubleGrass)
		RenderingRegistry.registerBlockHandler(MultipassRenderer)
		RenderingRegistry.registerBlockHandler(RenderBlockHopper)
	}
	
	override fun registerKeyBinds() {
		if (AlfheimConfigHandler.enableElvenStory) addESMKeyBinds()
		if (AlfheimConfigHandler.enableMMO) addMMOKeyBinds()
	}
	
	override fun initializeAndRegisterHandlers() {
		super.initializeAndRegisterHandlers()
		EventHandlerClient
		FurnaceHandler
		ItemsRemainingRenderHandler
		
		HUDCorporeaRat.eventForge()
		
		if (ConfigHandler.boundBlockWireframe) DoubleBoundItemRender
		if (AlfheimCore.TravellersGearLoaded) TGHandlerBotaniaRenderer
		if (AlfheimConfigHandler.enableElvenStory) enableESMGUIs()
		if (AlfheimConfigHandler.enableMMO) enableMMOGUIs()
		
		GUIScreenOverlay.eventForge()
		GUISheerCold.eventForge()
		GUIBanner.eventForge().eventFML()
		
		RenderPostShaders.allowShaders = !AlfheimConfigHandler.minimalGraphics && OpenGlHelper.shadersSupported
	}
	
	override fun postInit() {
		super.postInit()
		AlfheimBotaniaModifiersClient.postInit()
	}
	
	override fun bloodFX(world: World, x: Double, y: Double, z: Double, lifetime: Int, size: Float, gravity: Float) {
		if (mc.renderViewEntity == null || mc.effectRenderer == null || !doParticle()) return
		mc.effectRenderer.addEffect(EntityBloodFx(world, x, y, z, size, lifetime, gravity))
	}
	
	override fun featherFX(world: World, x: Double, y: Double, z: Double, color: Int, size: Float, lifetime: Float, distance: Float, must: Boolean, motionX: Double, motionY: Double, motionZ: Double) {
		if (mc.renderViewEntity == null || mc.effectRenderer == null) return
		val particle = EntityFeatherFx(world, x, y, z, color, size, lifetime)
		particle.setMotion(motionX, motionY, motionZ)
		
		if (!must) {
			if (!doParticle()) return
			val distanceX: Double = mc.renderViewEntity.posX - particle.posX
			val distanceY: Double = mc.renderViewEntity.posY - particle.posY
			val distanceZ: Double = mc.renderViewEntity.posZ - particle.posZ
			if (distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ > distance * distance) {
				return
			}
		}
		
		mc.effectRenderer.addEffect(particle)
	}
	
	override fun sparkleFX(world: World, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float, size: Float, ageMultiplier: Int, motionX: Double, motionY: Double, motionZ: Double, fake: Boolean, noclip: Boolean) {
		if (!doParticle() && !fake) return
		val sparkle = FXSparkle(world, x, y, z, size, r, g, b, ageMultiplier)
		sparkle.setMotion(motionX, motionY, motionZ)
		sparkle.fake = fake
		sparkle.noClip = noclip
//		if (ClientProxy.noclipEnabled) sparkle.noClip = true
//		if (ClientProxy.corruptSparkle) sparkle.corrupt = true
		mc.effectRenderer.addEffect(sparkle)
	}
	
	override fun doParticle() = if (!ConfigHandler.useVanillaParticleLimiter) true else Math.random() < 1f - 0.4f * mc.gameSettings.particleSetting
	
	val keyLolicorn = KeyBinding("key.lolicorn.desc", Keyboard.KEY_L, "key.categories.alfheim")
	val keyESMAbility = KeyBinding("key.esmability.desc", Keyboard.KEY_M, "key.categories.alfheim")
	val keyFlight = KeyBinding("key.flight.desc", Keyboard.KEY_F, "key.categories.alfheim")
	val keyCast = KeyBinding("key.cast.desc", Keyboard.KEY_C, "key.categories.alfheim")
	val keyUnCast = KeyBinding("key.uncast.desc", Keyboard.KEY_X, "key.categories.alfheim")
	val keySelMob = KeyBinding("key.selmob.desc", Keyboard.KEY_R, "key.categories.alfheim")
	val keySelTeam = KeyBinding("key.selteam.desc", if (mc.session.username == "AlexSocol") Keyboard.KEY_T else Keyboard.KEY_Y, "key.categories.alfheim")
	
	init {
		removeKeyBinding(keyFlight)
		removeKeyBinding(keyESMAbility)
		removeKeyBinding(keyCast)
		removeKeyBinding(keyUnCast)
		removeKeyBinding(keySelMob)
		removeKeyBinding(keySelTeam)
	}
	
	fun toggelModes(b: Boolean, esm: Boolean, mmo: Boolean, esmOld: Boolean, mmoOld: Boolean) {
		MinecraftForge.EVENT_BUS.post(AlfheimModeChangedEvent(esm, mmo, esmOld, mmoOld))
		
		if (b)
			toggleESM(esm, mmo, esmOld, mmoOld)
		else
			toggleMMO(esm, mmo, esmOld, mmoOld)
	}
	
	fun enableESM() {
//		if (AlfheimConfigHandler.enableElvenStory) return
		AlfheimConfigHandler.enableElvenStory = true
		AlfheimLexiconData.reEnableESM()
//		if (Botania.thaumcraftLoaded) ThaumcraftAlfheimModule.addESMRecipes()
		enableESMGUIs()
		addESMKeyBinds()
		ESMHandler.checkAddAttrs()
	}
	
	fun disableESM() {
//		if (!AlfheimConfigHandler.enableElvenStory) return
		AlfheimConfigHandler.enableElvenStory = false
		AlfheimLexiconData.disableESM()
//		if (Botania.thaumcraftLoaded) ThaumcraftAlfheimModule.removeESMRecipes()
		disableESMGUIs()
		removeESMKeyBinds()
		disableMMO()
	}
	
	fun enableMMO() {
//		if (AlfheimConfigHandler.enableMMO) return
		AlfheimConfigHandler.enableMMO = true
		AlfheimLexiconData.reEnableMMO()
		AlfheimRecipes.addMMORecipes()
		enableMMOGUIs()
		addMMOKeyBinds()
		enableESM()
	}
	
	fun disableMMO() {
//		if (!AlfheimConfigHandler.enableMMO) return
		AlfheimConfigHandler.enableMMO = false
		AlfheimLexiconData.disableMMO()
		AlfheimRecipes.removeMMORecipes()
		disableMMOGUIs()
		removeMMOKeyBinds()
		TimeStopSystemClient.clear()
	}
	
	private fun toggleESM(esm: Boolean, mmo: Boolean, esmOld: Boolean, mmoOld: Boolean) {
		if (esmOld == esm) return
		AlfheimConfigHandler.enableElvenStory = esm
		
		if (esm) {
			AlfheimLexiconData.reEnableESM()
			addESMKeyBinds()
		} else {
			AlfheimLexiconData.disableESM()
			removeESMKeyBinds()
			if (mmoOld != mmo) toggleMMO(false, mmo, true, mmoOld)
		}
	}
	
	private fun toggleMMO(esm: Boolean, mmo: Boolean, esmOld: Boolean, mmoOld: Boolean) {
		if (mmoOld == mmo) return
		AlfheimConfigHandler.enableMMO = mmo
		
		if (mmo) {
			AlfheimLexiconData.reEnableMMO()
			enableMMOGUIs()
			addMMOKeyBinds()
			if (esm) toggleESM(true, true, esmOld, false)
		} else {
			AlfheimLexiconData.disableMMO()
			disableMMOGUIs()
			removeMMOKeyBinds()
			TimeStopSystemClient.clear()
		}
	}
	
	private fun enableESMGUIs() {
		ASJUtilities.log("Registering ESM GUIs")
		MinecraftForge.EVENT_BUS.register(GUIRace)
	}
	
	private fun disableESMGUIs() {
		ASJUtilities.log("Unregistering ESM GUIs")
		MinecraftForge.EVENT_BUS.unregister(GUIRace)
	}
	
	private fun enableMMOGUIs() {
		ASJUtilities.log("Registering MMO GUIs")
		MinecraftForge.EVENT_BUS.register(GUIParty)
		MinecraftForge.EVENT_BUS.register(GUISpells)
		
		MinecraftForge.EVENT_BUS.unregister(GUIRace)
	}
	
	private fun disableMMOGUIs() {
		ASJUtilities.log("Unregistering MMO GUIs")
		MinecraftForge.EVENT_BUS.unregister(GUIParty)
		MinecraftForge.EVENT_BUS.unregister(GUISpells)
		
		MinecraftForge.EVENT_BUS.register(GUIRace)
	}
	
	private fun addESMKeyBinds() {
		addKeyBinding(keyFlight)
		addKeyBinding(keyESMAbility)
		
		KeyBinding.resetKeyBindingArrayAndHash()
	}
	
	private fun removeESMKeyBinds() {
		unregisterKeyBinding(keyFlight)
		unregisterKeyBinding(keyESMAbility)
		
		KeyBinding.resetKeyBindingArrayAndHash()
	}
	
	private fun addMMOKeyBinds() {
		addKeyBinding(keyCast)
		addKeyBinding(keyUnCast)
		addKeyBinding(keySelMob)
		addKeyBinding(keySelTeam)
		
		KeyBinding.resetKeyBindingArrayAndHash()
	}
	
	private fun removeMMOKeyBinds() {
		unregisterKeyBinding(keyCast)
		unregisterKeyBinding(keyUnCast)
		unregisterKeyBinding(keySelMob)
		unregisterKeyBinding(keySelTeam)
		
		KeyBinding.resetKeyBindingArrayAndHash()
	}
	
	private fun unregisterKeyBinding(key: KeyBinding) {
		removeKeyBinding(key)
		val id = ASJUtilities.indexOfComparableArray(mc.gameSettings.keyBindings, key)
		if (id < 0 || id > mc.gameSettings.keyBindings.size) return
		mc.gameSettings.keyBindings = ArrayUtils.remove(mc.gameSettings.keyBindings, id)
	}
	
	private fun addKeyBinding(key: KeyBinding) {
		key.keyCode = key.keyCodeDefault
		ClientRegistry.registerKeyBinding(key)
	}
	
	private fun removeKeyBinding(key: KeyBinding) {
		key.keyCode = 0
	}
}