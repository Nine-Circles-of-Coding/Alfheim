package alfheim.common.core.proxy;

import alfheim.AlfheimCore;
import alfheim.common.core.registry.AlfheimAchievements;
import alfheim.common.core.registry.AlfheimBlocks;
import alfheim.common.core.registry.AlfheimItems;
import alfheim.common.core.registry.AlfheimRecipes;
import alfheim.common.core.registry.AlfheimRegistry;
import alfheim.common.event.CommonEventHandler;
import alfheim.common.lexicon.AlfheimLexiconData;
import alfheim.common.network.KeyBindMessage;
import alfheim.common.world.dim.DimensionUtil;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {

	public void preInit() {
    	AlfheimLexiconData.preInit();
    	AlfheimBlocks.init();
    	AlfheimItems.init();
		AlfheimRecipes.preInit();
		AlfheimRegistry.preInit();
		AlfheimAchievements.init();
    	AlfheimLexiconData.preInit2();
	}

	public void registerRenderThings() {}

	public void registerKeyBinds() {}

	public void init() {
    	AlfheimLexiconData.init();
		AlfheimRegistry.init();
		DimensionUtil.init();
		AlfheimRecipes.init();
	}
	
	public void postInit() {
		AlfheimRegistry.postInit();
	}
	
	public void initializeAndRegisterHandlers() {
		MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
		FMLCommonHandler.instance().bus().register(new CommonEventHandler());
	}

	public void registerPackets() {
		AlfheimCore.network.registerMessage(KeyBindMessage.Handler.class, KeyBindMessage.class, 0, Side.SERVER);
	}
}
