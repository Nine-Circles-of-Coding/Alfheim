package alfheim.common.integration.tinkersconstruct

import alexsocol.asjlib.ASJUtilities
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.integration.tinkersconstruct.modifier.*
import alfheim.common.item.compat.tinkersconstruct.*
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import gloomyfolken.hooklib.asm.*
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.*
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent
import tconstruct.library.TConstructRegistry
import tconstruct.library.client.TConstructClientRegistry
import tconstruct.library.crafting.*
import tconstruct.library.modifier.IModifyable
import tconstruct.library.weaponry.IAmmo
import tconstruct.modifiers.tools.ModInteger
import tconstruct.smeltery.TinkerSmeltery
import tconstruct.tools.TinkerTools
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.lib.LibOreDict

object TinkersConstructAlfheimModule {
	
	val liquidElvorium: Fluid
	val liquidElvoriumBlock: Block
	
	val liquidElementium: Fluid
	val liquidElementiumBlock: Block
	
	val liquidManasteel: Fluid
	val liquidManasteelBlock: Block
	
	val liquidMauftrium: Fluid
	val liquidMauftriumBlock: Block
	
	val liquidTerrasteel: Fluid
	val liquidTerrasteelBlock: Block
	
	val naturalFluids: Array<Fluid>
	val naturalFluidBlocks: Array<Block>
	
	val naturalBucket: Item
	val naturalMaterial: Item
	
	val manaGenMaterials = intArrayOf(AlfheimConfigHandler.materialIDs[1], AlfheimConfigHandler.materialIDs[3], AlfheimConfigHandler.materialIDs[4])
	val manaGenDelay = hashMapOf(manaGenMaterials[0] to 10, manaGenMaterials[1] to 5, manaGenMaterials[2] to 12)
	
	val manaRepairMaterials = IntArray(5) { AlfheimConfigHandler.materialIDs[it] }
	val manaRepairCost = hashMapOf(manaRepairMaterials[0] to 4, manaRepairMaterials[1] to 5, manaRepairMaterials[2] to 4, manaRepairMaterials[3] to 7, manaRepairMaterials[4] to 6)
	
	init {
		// fuck you Forge and your fluid names lowercasing
		liquidElementium = registerSmelteryFluid("elementium", ModBlocks.storage, 2)
		liquidElementiumBlock = liquidElementium.block
		
		liquidElvorium = registerSmelteryFluid("elvorium", AlfheimBlocks.alfStorage, 0)
		liquidElvoriumBlock = liquidElvorium.block
		
		liquidManasteel = registerSmelteryFluid("manasteel", ModBlocks.storage, 0)
		liquidManasteelBlock = liquidManasteel.block
		
		liquidMauftrium = registerSmelteryFluid("mauftrium", AlfheimBlocks.alfStorage, 1)
		liquidMauftriumBlock = liquidMauftrium.block
		
		liquidTerrasteel = registerSmelteryFluid("terrasteel", ModBlocks.storage, 1)
		liquidTerrasteelBlock = liquidTerrasteel.block
		
		naturalFluids = arrayOf(liquidElementium, liquidElvorium, liquidManasteel, liquidMauftrium, liquidTerrasteel)
		naturalFluidBlocks = arrayOf(liquidElementiumBlock, liquidElvoriumBlock, liquidManasteelBlock, liquidMauftriumBlock, liquidTerrasteelBlock)
		
		naturalBucket = ItemNaturalBucket()
		naturalMaterial = ItemNaturalMaterial()
		
		extendModifierList()
		
		ModifyBuilder.registerModifier(ModManaRepair)
		TConstructRegistry.registerActiveToolMod(AModNatural)
		
		if (!ASJUtilities.isServer)
			TConstructRegistry.getToolMapping().forEach {
				if (it is IAmmo) return@forEach
				
				TConstructClientRegistry.addEffectRenderMapping(it, AlfheimConfigHandler.modifierIDs[0], "tinker", "modifiers/ManaCore/mana_core", false)
			}
	}
	
	fun registerSmelteryFluid(name: String, renderBlock: Block, renderMeta: Int, texture: String = "liquid_$name", fluidName: String = "$name.molten", blockName: String = "fluid.molten.$name", density: Int = 3000, viscosity: Int = 6000, temperature: Int = 1300, material: Material = Material.lava) =
		TinkerSmeltery.registerFluid(name, fluidName, blockName, texture, density, viscosity, temperature, material).also {
			FluidType.registerFluidType(name, renderBlock, renderMeta, it.temperature, it, false)
		}!!
	
	fun extendModifierList() {
		ModifyBuilder.instance.itemModifiers.firstOrNull { it is ModInteger && it.key == "Moss" }?.stacks?.add(ItemStack(ModItems.vineBall))
	}
	
	val sharpnessOres = arrayOf<String>(LibOreDict.PRISMARINE_SHARD, *LibOreDict.QUARTZ, alfheim.api.lib.LibOreDict.RAINBOW_QUARTZ)
	
	@SubscribeEvent
	fun onOreRegistration(e: OreRegisterEvent) {
		if (e.Name in sharpnessOres)
			TinkerTools.modAttack.addStackToMatchList(e.Ore, 2)
	}
}

object TraitFairySpawner {
	
	@Hook(returnCondition = ReturnCondition.ON_NOT_NULL)
	fun getChance(stack: ItemStack?): Float? {
		if (stack == null) return null
		val tool = stack.item as? IModifyable ?: return null
		val tag = ItemNBTHelper.getCompound(stack, tool.baseTagName, true) ?: return null
		val headMaterial = tag.getInteger("Head")
		if (headMaterial == AlfheimConfigHandler.materialIDs[0]) return 0.1f
		return null
	}
}