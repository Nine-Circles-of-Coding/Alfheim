package alfheim.common.core.asm

import alexsocol.asjlib.asm.ASJASM
import alexsocol.patcher.asm.ASJHookLoader
import alfheim.api.ModInfo
import alfheim.common.core.handler.AlfheimConfigHandler
import cpw.mods.fml.relauncher.*
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion
import gloomyfolken.hooklib.minecraft.HookLoader
import org.apache.commons.io.FileUtils
import java.io.File

// -Dfml.coreMods.load=alfheim.common.core.asm.AlfheimHookLoader
// -username=AlexSocol
@MCVersion(value = "1.7.10")
class AlfheimHookLoader: HookLoader() {

	init {
		ModInfo.OBF = ASJHookLoader.OBF

		if (!ModInfo.OBF) { // FUCK YOU GRADLE, FUCK YOU INTELLIJ, FUCK YOU ALL
			FMLRelaunchLog.info("[${ModInfo.MODID.uppercase()}] MoViNg FuCkInG rEsOuRcEs BeCaUsE iDeA iS FUCKED UP!!!")
			FileUtils.copyDirectory(File("../src/main/resources/"), File("../build/classes/kotlin/main/"))
		}

		AlfheimConfigHandler.loadConfig(File("config/Alfheim/Alfheim.cfg"))
	}

	override fun getASMTransformerClass() = arrayOf(AlfheimClassTransformer::class.java.name)

	override fun registerHooks() {
		FMLRelaunchLog.info("[${ModInfo.MODID.uppercase()}] Loaded coremod. Registering hooks...")


		registerHookContainer("alfheim.common.core.asm.hook.AlfheimHookHandler")
		if (AlfheimConfigHandler.hpHooks) registerHookContainer("alfheim.common.core.asm.hook.AlfheimHPHooks")
		registerHookContainer("alfheim.common.core.asm.hook.Botania18AndUpBackport")
		registerHookContainer("alfheim.common.core.asm.hook.ElementalDamageAdapter")
		registerHookContainer("alfheim.common.core.asm.hook.extender.FlowerBagExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.FlowerSubtileInvalidator")
		registerHookContainer("alfheim.common.core.asm.hook.extender.FurnaceExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.ItemLensExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.ItemTwigWandExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.LightRelayExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.ManaSpreaderExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.PureDaisyExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.QuartzExtender")
		registerHookContainer("alfheim.common.core.asm.hook.extender.SparkExtender")
		registerHookContainer("alfheim.common.core.asm.hook.fixes.BotaniaGlowingRenderFixes")
		registerHookContainer("alfheim.common.core.asm.hook.fixes.FlightTiaraFix")
		registerHookContainer("alfheim.common.core.asm.hook.fixes.GodAttributesHooks")
		registerHookContainer("alfheim.common.core.asm.hook.fixes.RecipeAncientWillsFix")
		registerHookContainer("alfheim.common.integration.travellersgear.TGHandlerBotaniaAdapterHooks")
		registerHookContainer("alfheim.common.integration.tinkersconstruct.TraitFairySpawner")
		registerHookContainer("alfheim.common.item.rod.RedstoneRodHookHandled")
        
        ASJASM.registerFieldHookContainer("alfheim.common.core.asm.hook.AlfheimFieldHookHandler")
	}
}
