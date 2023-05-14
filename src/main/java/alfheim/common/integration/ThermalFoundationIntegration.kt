package alfheim.common.integration

import alexsocol.asjlib.*
import alfheim.AlfheimCore
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.integration.tinkersconstruct.*
import cpw.mods.fml.common.Loader
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.init.*
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.oredict.OreDictionary
import tconstruct.smeltery.TinkerSmeltery
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.lib.LibOreDict

object ThermalFoundationIntegration {
	
	val loaded = Loader.isModLoaded("ThermalFoundation")
	
	var cryothenumBlock: Block = Blocks.flowing_water!!

	init {
		if (loaded) init()
	}
	
	fun init() {
		cryothenumBlock = GameRegistry.findBlock("ThermalFoundation", "FluidCryotheum")
		
		addOreDict()
	}
	
	fun addOreDict() {
		GameRegistry.findItem("ThermalFoundation", "material")?.let {
			OreDictionary.registerOre(LibOreDict.MANA_STEEL, ItemStack(it, 1, 70))
			OreDictionary.registerOre(LibOreDict.MANASTEEL_NUGGET, ItemStack(it, 1, 102))
		}
	}
	
	fun addTinkersCastingIfNotLoaded() {
		if (loaded) return
		if (!AlfheimCore.TiCLoaded) return // just in case
		
		TinkersConstructAlfheimConfig.addSmelteryMeltCastGroup(AlfheimConfigHandler.materialIDs[TinkersConstructAlfheimConfig.MANASTEEL], TinkerSmeltery.moltenMithrilFluid, ModBlocks.storage, 0, ModItems.manaResource, 0, ModItems.manaResource, 17)
	}
	
	@Suppress("unused")
	object Hooks {
		
		// fixing stupid TF mistake -_-
		// THAT IS NOT C++ !!!
		@JvmStatic
		fun postRegisterFluid(name: String) {
			val fluid = FluidRegistry.getFluid(name) ?: return // unreachable but just in case
			val TFFluids = Class.forName("cofh.thermalfoundation.fluid.TFFluids")
			ASJReflectionHelper.setStaticValue(TFFluids, fluid, "fluid${name.capitalized()}")
		}
	}
}
