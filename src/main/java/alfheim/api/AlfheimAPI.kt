package alfheim.api

import alexsocol.asjlib.ASJUtilities
import alfheim.api.block.tile.SubTileAnomalyBase
import alfheim.api.crafting.recipe.*
import alfheim.api.entity.EnumRace
import alfheim.api.item.ThrowableCollidingItem
import alfheim.api.lib.LibResourceLocations
import alfheim.api.spell.SpellBase
import alfheim.api.trees.*
import alfheim.api.world.domain.Domain
import net.minecraft.block.Block
import net.minecraft.init.*
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.common.util.EnumHelper
import vazkii.botania.api.recipe.RecipeElvenTrade

@Suppress("unused")
object AlfheimAPI {
	
	val elvoriumArmor = EnumHelper.addArmorMaterial("ALFHEIM_ELVORIUM", 50, intArrayOf(5, 8, 7, 4), 30)!!
	val elementalArmor = EnumHelper.addArmorMaterial("ALFHEIM_ELEMENTAL", 20, intArrayOf(2, 9, 5, 2), 20)!!
	
	val elvoriumToolMaterial = EnumHelper.addToolMaterial("ALFHEIM_ELVORIUM", 4, 2400, 9.5f, 3f, 30)!!
	val mauftriumToolmaterial = EnumHelper.addToolMaterial("ALFHEIM_MAUFTRIUM", 10, 9000, 3f, 8f, 40)!!
	
	// relic
	val EXCALIBER = EnumHelper.addToolMaterial("ALFHEIM_EXCALIBER", 3, -1, 6.2f, 6f, 40)!!
	val FENRIR = EnumHelper.addToolMaterial("ALFHEIM_FENRIR", 0, 2000, 0f, 3.0f, 14)!!
	var RUNEAXE = EnumHelper.addToolMaterial("ALFHEIM_RUNEAXE", 4, 1561, 10f, 2f, 50)!!
	val SOUL = EnumHelper.addToolMaterial("ALFHEIM_SOUL", -1, -1, -1f, -1f, -1)!! // ragnarok sword
	val SURTR = EnumHelper.addToolMaterial("ALFHEIM_SURTR", 0, 1, 0f, 4f, 0)!!
	val THRYM = EnumHelper.addToolMaterial("ALFHEIM_THRYM", 0, 1, 0f, 4f, 0)!!
	
	val mauftriumRarity = EnumHelper.addRarity("ALFHEIM_MAUFTRIUM", EnumChatFormatting.GOLD, "Mauftrium")!!
	val ragnarokRarity = EnumHelper.addRarity("ALFHEIM_RAGNAROK", EnumChatFormatting.DARK_RED, "Ragnarok")!!
	
	/** List of [RecipeElvenTrade] outputs banned for re-trading in Alfheim trade portal */
	val bannedRetrades = ArrayList<ItemStack>()
	
	/** List of recipes for mana infuser */
	val manaInfuserRecipes = ArrayList<RecipeManaInfuser>()
	
	/** List of all pink items with their relative pinkness */
	val pinkness = HashMap<ItemStack, Int>()
	
	/** List of all spells for all races */
	val spells = HashSet<SpellBase>()
	
	/** Map of elven spells associated with their race (affinity), sorted by name  */
	val spellMapping = HashMap<EnumRace, HashSet<SpellBase>>()
	
	/** Map of anomaly data  */
	val anomalies = HashMap<String, AnomalyData>()
	
	/** Map of anomaly behaviors for use in [Anomaly Harvester][alfheim.common.block.tile.TileAnomalyHarvester] */
	val anomalyBehaviors = HashMap<String, ((TileEntity) -> Unit)>()
	
	/** Petronia fuels map */
	val fuelMap = HashMap<String, Pair<Int, Int>>()
	
	/** Ores for Orechid Endium */
	val oreWeightsEnd = HashMap<String, Int>()
	
	/** Map of domains */
	val domains = LinkedHashMap<String, Domain>()
	
	/** Set of blocks that can increase entity's sheer cold value */
	val coldBlocks = mutableSetOf(Blocks.ice, Blocks.packed_ice, Blocks.snow)
	
	/** Set of blocks that can reduce entity's sheer cold value */
	val warmBlocks = mutableSetOf(Blocks.fire, Blocks.lava, Blocks.flowing_lava, Blocks.lit_furnace, Blocks.torch, Blocks.lit_pumpkin)
	
	val treeRecipes: MutableList<RecipeTreeCrafting> = ArrayList()
	val treeVariants: MutableList<IIridescentSaplingVariant> = ArrayList()
	val collidingItemHashMap: MutableMap<String, ThrowableCollidingItem> = LinkedHashMap()
	val fallbackTcl = ThrowableCollidingItem("${ModInfo.MODID}_fallback", ItemStack(Items.blaze_rod)) { _, _ -> }
	
	fun addInfuserRecipe(rec: RecipeManaInfuser?): RecipeManaInfuser? {
		if (rec != null) manaInfuserRecipes.add(rec)
		return rec
	}
	
	fun addInfuserRecipe(result: ItemStack, mana: Int, vararg ingredients: Any): RecipeManaInfuser {
		val rec = RecipeManaInfuser(mana, result, *ingredients)
		manaInfuserRecipes.add(rec)
		return rec
	}
	
	fun removeInfusionRecipe(rec: RecipeManaInfuser?): RecipeManaInfuser? =
		if (rec != null && manaInfuserRecipes.remove(rec)) rec else null
	
	fun removeInfusionRecipe(result: ItemStack): RecipeManaInfuser? =
		manaInfuserRecipes.indices
			.firstOrNull { ASJUtilities.isItemStackEqualCrafting(manaInfuserRecipes[it].output, result) }
			?.let { manaInfuserRecipes.removeAt(it) }
	
	/** Remove [output] from Alfheim trade portal  */
	fun banRetrade(output: ItemStack) =
		bannedRetrades.add(output)
	
	/** Check if [output] isn't banned to be obtained through Alfheim trade portal  */
	fun isRetradeable(output: ItemStack) =
		bannedRetrades.none { ASJUtilities.isItemStackEqualCrafting(output, it) }
	
	/** Map a [pink] stack to it's pinkness [weight]. Also can override old values  */
	fun addPink(pink: ItemStack, weight: Int) =
		pinkness.put(pink, weight)
	
	fun getPinkness(item: ItemStack) =
		pinkness.keys
			.firstOrNull { ASJUtilities.isItemStackEqualCrafting(it, item) }
			?.let { pinkness[it]!! } ?: 0
	
	/**
	 * Registers spell for some race by affinity
	 *
	 * Note:
	 * Salamander - Fire
	 * Sylph - Wind
	 * Cait Sith - Nature
	 * Pooka - Sound
	 * Gnome - Earth
	 * Leprechaun - Tech
	 * Spriggan - Illusion
	 * Undine - Water
	 * Imp - Darkness
	 */
	fun registerSpell(spell: SpellBase) {
		// here goes hook to disable spell in configs
		require(spell.race != EnumRace.HUMAN) { "Spell race must not be human (spell ${spell.name})" }
		require(spell.race != EnumRace.ALV) { "Alv race is currently not supported (spell ${spell.name})" }
		
		if (spell !in spells) {
			spells.add(spell)
			spellMapping.computeIfAbsent(spell.race) { HashSet(8) }.add(spell)
			
			if (ASJUtilities.isClient)
				LibResourceLocations.add(spell.name)
		} else
			ASJUtilities.warn("Trying to register spell ${spell.name} twice. Skipping.")
		
	}
	
	fun getSpellsFor(affinity: EnumRace): List<SpellBase> {
		return spellMapping.computeIfAbsent(affinity) { HashSet(8) }.sortedBy { it.name }
	}
	
	fun getSpellInstance(name: String) = spells.firstOrNull { it.name == name }
	
	fun getSpellByIDs(raceID: Int, spellID: Int): SpellBase? {
		var i = 0
		for (sb in getSpellsFor(EnumRace[raceID])) if (i++ == spellID) return sb
		return null
	}
	
	fun getSpellID(spell: SpellBase): Int {
		for (race in EnumRace.values()) {
			var i = -1
			for (sb in getSpellsFor(race)) {
				++i
				if (sb === spell) return i
			}
		}
		
		throw IllegalArgumentException("Client-server spells desynchronization. Not found ID for " + spell.name)
	}
	
	/** Register anomaly [subtile] with unique [name] */
	fun registerAnomaly(name: String, subtile: Class<out SubTileAnomalyBase>, rarity: SubTileAnomalyBase.EnumAnomalyRarity, strip: Int, color: Int = 0xFFFFFF) {
		if (anomalies.containsKey(name))
			throw IllegalArgumentException("Anomaly \"$name\" is already registered")
		
		anomalies[name] = AnomalyData(subtile, rarity, strip, color)
	}
	
	fun getAnomaly(name: String) = anomalies[name] ?: fallbackAnomalyData
	
	fun registerFuel(name: String, burnTime: Int, manaPerTick: Int) {
		fuelMap[name] = burnTime to manaPerTick
	}
	
	/**
	 * Maps an ore (ore dictionary key) to it's weight on the End world generation.
	 * This is used for the Orechid Endium flower.
	 * Alternatively get the values with the OreDetector mod:
	 * https://gist.github.com/Vazkii/9493322
	 *
	 * Higher weight means higher chance
	 */
	fun addOreWeightEnd(ore: String, weight: Int) {
		oreWeightsEnd[ore] = weight
	}
	
	/**
	 * Adds a tree crafting recipe to the registry.
	 *
	 * @param recipe - The recipe to add.
	 * @return The recipe that was added to the registry.
	 */
	fun addTreeRecipe(recipe: RecipeTreeCrafting) =
		recipe.also { treeRecipes.add(it) }
	
	/**
	 * Adds a tree crafting recipe with the specified parameters to the registry.
	 *
	 * @param mana   - The mana cost for the recipe.
	 * @param out    - The block that is created from the recipe.
	 * @param outTileId - The id for tile that may be loaded from out nbt
	 * @param core   - The core block in center that will be changed.
	 * @param inputs - The items used in the infusion.
	 * @return The recipe that was added to the registry.
	 */
	fun addTreeRecipe(mana: Int, out: ItemStack, outTileId: String?, core: ItemStack, vararg inputs: Any) =
		addTreeRecipe(RecipeTreeCrafting(mana, out, outTileId, core, *inputs))
	
	/**
	 * Adds a tree crafting recipe with the specified parameters to the registry.
	 *
	 * @param mana      - The mana cost for the recipe.
	 * @param out       - The block that is created from the recipe.
	 * @param outTileId - The id for tile that may be loaded from out nbt
	 * @param core      - The core block in center that will be changed.
	 * @param inputs    - The items used in the infusion.
	 * @param throttle  - The maximum mana that can be absorbed per tick for this recipe.
	 * @return The recipe that was added to the registry.
	 */
	fun addTreeRecipe(mana: Int, out: ItemStack, outTileId: String?, core: ItemStack, throttle: Int, vararg inputs: Any) =
		addTreeRecipe(RecipeTreeCrafting(mana, out, outTileId, core, throttle, *inputs))
	
	fun removeTreeRecipe(rec: RecipeTreeCrafting?): RecipeTreeCrafting? =
		if (rec != null && treeRecipes.remove(rec)) rec else null
	
	fun removeTreeRecipe(result: ItemStack): RecipeTreeCrafting? =
		treeRecipes.indices
			.firstOrNull { ASJUtilities.isItemStackEqualCrafting(treeRecipes[it].output, result) }
			?.let { treeRecipes.removeAt(it) }
	
	/**
	 * Adds an Iridescent Sapling variant to the registry.
	 *
	 * @param variant - The variant to add.
	 * @return The variant added to the registry.
	 */
	fun addTreeVariant(variant: IIridescentSaplingVariant) =
		variant.also { treeVariants.add(it) }
	
	/**
	 * Adds an Iridescent Sapling variant with the specified parameters to the registry, ignoring metadata.
	 *
	 * @param soil   - The soil block the variant uses.
	 * @param wood   - The wood block the variant uses.
	 * @param leaves - The leaves block the variant uses.
	 * @return The variant that was added to the registry.
	 */
	fun addTreeVariant(soil: Block, wood: Block, leaves: Block) =
		addTreeVariant(IridescentSaplingBaseVariant(soil, wood, leaves))
	
	/**
	 * Adds an Iridescent Sapling variant with the specified parameters to the registry, with a specific metadata.
	 *
	 * @param soil   - The soil block the variant uses.
	 * @param wood   - The wood block the variant uses.
	 * @param leaves - The leaves block the variant uses.
	 * @param meta   - The metadata of the soil the variant uses.
	 * @return The variant that was added to the registry.
	 */
	fun addTreeVariant(soil: Block, wood: Block, leaves: Block, meta: Int) =
		addTreeVariant(IridescentSaplingBaseVariant(soil, wood, leaves, meta))
	
	/**
	 * Adds an Iridescent Sapling variant with the specified parameters to the registry, using a range of metadata.
	 *
	 * @param soil    - The soil block the variant uses.
	 * @param wood    - The wood block the variant uses.
	 * @param leaves  - The leaves block the variant uses.
	 * @param metaMin - The minimum meta value of the soil the variant uses.
	 * @param metaMax - The maximum meta value of the soil the variant uses.
	 * @return The variant that was added to the registry.
	 */
	fun addTreeVariant(soil: Block, wood: Block, leaves: Block, metaMin: Int, metaMax: Int) =
		addTreeVariant(IridescentSaplingBaseVariant(soil, wood, leaves, metaMin, metaMax))
	
	/**
	 * Adds an Iridescent Sapling variant with the specified parameters to the registry, using a range of metadata.
	 *
	 * @param soil      - The soil block the variant uses.
	 * @param wood      - The wood block the variant uses.
	 * @param leaves    - The leaves block the variant uses.
	 * @param metaMin   - The minimum meta value of the soil the variant uses.
	 * @param metaMax   - The maximum meta value of the soil the variant uses.
	 * @param metaShift - The amount to subtract from the soil's metadata value to make the leaf metadata.
	 * @return The variant that was added to the registry.
	 */
	fun addTreeVariant(soil: Block, wood: Block, leaves: Block, metaMin: Int, metaMax: Int, metaShift: Int) =
		addTreeVariant(IridescentSaplingBaseVariant(soil, wood, leaves, metaMin, metaMax, metaShift))
	
	fun registerThrowable(tcl: ThrowableCollidingItem) =
		tcl.also { collidingItemHashMap[it.key] = it }
	
	fun getThrowableFromKey(key: String) =
		collidingItemHashMap[key] ?: fallbackTcl
	
	/**
	 * Gets a list of all acceptable Iridescent Sapling soils.
	 *
	 * @return A list of all Iridescent Sapling soils.
	 */
	fun getIridescentSoils(): List<Block> {
		val soils = ArrayList<Block>()
		for (variant in treeVariants) {
			soils.addAll(variant.acceptableSoils)
		}
		return soils
	}
	
	/**
	 * Gets the variant for a given soil.
	 *
	 * @param soil The block the sapling is placed on.
	 * @param meta The meta of the block the sapling is on.
	 * @return The variant, if there is one.
	 */
	fun getTreeVariant(soil: Block, meta: Int) =
		treeVariants.firstOrNull { it.matchesSoil(soil, meta) }
	
	private object FallbackAnomaly: SubTileAnomalyBase() {
		override val targets: List<Any> = emptyList()
		override fun performEffect(target: Any) = Unit
		override fun typeBits() = 0
	}
	
	private val fallbackAnomalyData = AnomalyData(FallbackAnomaly::class.java, SubTileAnomalyBase.EnumAnomalyRarity.COMMON, 0, 0)
	
	data class AnomalyData(val subtileClass: Class<out SubTileAnomalyBase>, val rarity: SubTileAnomalyBase.EnumAnomalyRarity, val strip: Int, val color: Int)
}
