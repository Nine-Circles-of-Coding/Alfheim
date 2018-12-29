package alfheim.client.core.proxy;

import org.lwjgl.input.Keyboard;

import alexsocol.asjlib.ASJUtilities;
import alexsocol.asjlib.render.ASJShaderHelper;
import alfheim.AlfheimCore;
import alfheim.api.lib.*;
import alfheim.client.core.handler.EventHandlerClient;
import alfheim.client.gui.*;
import alfheim.client.lib.LibResourceLocationsActual;
import alfheim.client.model.entity.*;
import alfheim.client.render.block.*;
import alfheim.client.render.entity.*;
import alfheim.client.render.tile.*;
import alfheim.common.block.tile.*;
import alfheim.common.core.proxy.CommonProxy;
import alfheim.common.core.util.AlfheimBotaniaModifiers;
import alfheim.common.entity.*;
import alfheim.common.entity.boss.EntityFlugel;
import alfheim.common.entity.spell.*;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
	
	public static final KeyBinding keyCast = new KeyBinding("key.cast.desc", Keyboard.KEY_V, "key.categories.gameplay");
	public static final KeyBinding keyFlight = new KeyBinding("key.flight.desc", Keyboard.KEY_F, "key.categories.movement");
	public static final KeyBinding keySelMob = new KeyBinding("key.selmob.desc", Keyboard.KEY_R, "key.categories.gameplay");
	public static final KeyBinding keySelTeam = new KeyBinding("key.selteam.desc", Keyboard.KEY_T, "key.categories.gameplay");
	
	@Override
	public void preInit() {
		super.preInit();
		LibResourceLocationsActual.init();
	}
	
	@Override
	public void registerRenderThings() {
		LibRenderIDs.idAnyavil = RenderingRegistry.getNextAvailableRenderId();
		LibRenderIDs.idPylon = RenderingRegistry.getNextAvailableRenderId();
		LibRenderIDs.idTransferer = RenderingRegistry.getNextAvailableRenderId();
		
		LibShaderIDs.idFire = ASJShaderHelper.createProgram(null, "shaders/fire.frag");
		LibShaderIDs.idGravity = ASJShaderHelper.createProgram(null, "shaders/gravity.frag");
		LibShaderIDs.idNoise = ASJShaderHelper.createProgram("shaders/position.vert", "shaders/noise4d.frag");
		LibShaderIDs.idShadow = ASJShaderHelper.createProgram(null, "shaders/shadow.frag");

		RenderingRegistry.registerBlockHandler(LibRenderIDs.idAnyavil, new RenderBlockAnyavil());
		RenderingRegistry.registerBlockHandler(LibRenderIDs.idPylon, new RenderBlockAlfheimPylons());
		RenderingRegistry.registerBlockHandler(LibRenderIDs.idTransferer, new RenderBlockTransferer());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileAlfheimPortal.class, new RenderTileAlfheimPortal());
		ClientRegistry.bindTileEntitySpecialRenderer(TileAlfheimPylons.class, new RenderTileAlfheimPylons());
		ClientRegistry.bindTileEntitySpecialRenderer(TileAnyavil.class, new RenderTileAnyavil());
		ClientRegistry.bindTileEntitySpecialRenderer(TileFlugelHead.class, new RenderTileFlugelHead());
		ClientRegistry.bindTileEntitySpecialRenderer(TileTradePortal.class, new RenderTileTradePortal());
		ClientRegistry.bindTileEntitySpecialRenderer(TileTransferer.class, new RenderTileTransferer());

		if (AlfheimCore.enableElvenStory) {
			ClientRegistry.registerKeyBinding(keyCast);
			ClientRegistry.registerKeyBinding(keyFlight);
			ClientRegistry.registerKeyBinding(keySelMob);
			ClientRegistry.registerKeyBinding(keySelTeam);
		}
		
		RenderingRegistry.registerEntityRenderingHandler(EntityAlfheimPixie.class, new RenderEntityAlfheimPixie());
		RenderingRegistry.registerEntityRenderingHandler(EntityElf.class, new RenderEntityElf(new ModelEntityElf(), 0.25F));
		RenderingRegistry.registerEntityRenderingHandler(EntityFlugel.class, new RenderEntityFlugel(new ModelEntityFlugel(), 0.25F));
		RenderingRegistry.registerEntityRenderingHandler(EntityLightningMark.class, new RenderEntityLightningMark());
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellHarp.class, new RenderEntityArfa());
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellDriftingMine.class, new RenderEntityDriftingMine());
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellGravityTrap.class, new RenderEntityGravityTrap());
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellFenrirStorm.class, new RenderEntityFenrirStorm());
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellWindBlade.class, new RenderEntityWindBlade());
	}

	@Override
	public void registerKeyBinds() {}

	@Override
	public void initializeAndRegisterHandlers() {
		super.initializeAndRegisterHandlers();
		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
		FMLCommonHandler.instance().bus().register(new EventHandlerClient());
		if (AlfheimCore.enableElvenStory) {
			ASJUtilities.log("Registering ES GUIs");
			MinecraftForge.EVENT_BUS.register(new GUIIceLens(Minecraft.getMinecraft()));
			MinecraftForge.EVENT_BUS.register(new GUIParty(Minecraft.getMinecraft()));
			MinecraftForge.EVENT_BUS.register(new GUISpells(Minecraft.getMinecraft()));
		}
	}
	
	@Override
	public void postInit() {
		super.postInit();
		AlfheimBotaniaModifiers.postInit();
	}
}