package alfheim.common.lexicon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;

import alfheim.AlfheimCore;
import alfheim.api.AlfheimAPI;
import alfheim.api.spell.SpellBase;
import alfheim.client.core.handler.CardinalSystemClient;
import alfheim.common.block.BlockElvenOres;
import alfheim.common.block.tile.TileAlfheimPortal;
import alfheim.common.block.tile.TileManaInfuser;
import alfheim.common.block.tile.TileTradePortal;
import alfheim.common.core.handler.CardinalSystem.KnowledgeSystem.Knowledge;
import alfheim.common.core.registry.AlfheimAchievements;
import alfheim.common.core.registry.AlfheimBlocks;
import alfheim.common.core.registry.AlfheimItems;
import alfheim.common.core.registry.AlfheimRecipes;
import alfheim.common.core.registry.AlfheimItems.ElvenResourcesMetas;
import alfheim.common.core.util.AlfheimConfig;
import alfheim.common.lexicon.page.PageManaInfusorRecipe;
import alfheim.common.lexicon.page.PageMultiblockLearnable;
import alfheim.common.lexicon.page.PagePureDaisyRecipe;
import alfheim.common.lexicon.page.PageSpell;
import alfheim.common.lexicon.page.PageTextLearnableAchievement;
import alfheim.common.lexicon.page.PageTextLearnableKnowledge;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.KnowledgeType;
import vazkii.botania.api.lexicon.LexiconCategory;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;
import vazkii.botania.api.lexicon.LexiconRecipeMappings;
import vazkii.botania.api.recipe.RecipeRuneAltar;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.ModMultiblocks;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lexicon.BLexiconCategory;
import vazkii.botania.common.lexicon.BLexiconEntry;
import vazkii.botania.common.lexicon.LexiconData;
import vazkii.botania.common.lexicon.RLexiconEntry;
import vazkii.botania.common.lexicon.page.PageCraftingRecipe;
import vazkii.botania.common.lexicon.page.PageElvenRecipe;
import vazkii.botania.common.lexicon.page.PageMultiblock;
import vazkii.botania.common.lexicon.page.PageRecipe;
import vazkii.botania.common.lexicon.page.PageRuneRecipe;
import vazkii.botania.common.lexicon.page.PageText;

public class AlfheimLexiconData {
	
	public static final KnowledgeType kt = (AlfheimCore.enableElvenStory) ? BotaniaAPI.basicKnowledge : BotaniaAPI.elvenKnowledge;
	
	public static LexiconCategory categoryAlfheim;

	/** Lore alfheim page */
	public static LexiconEntry alfheim;
	/** Lore elves page */
	public static LexiconEntry elves;
	
	// Main addon content
	public static LexiconEntry auraAlf;
	public static LexiconEntry advMana;
	public static LexiconEntry anyavil;
	public static LexiconEntry elemSet;
	public static LexiconEntry elvenSet;
	public static LexiconEntry elvorium;
	public static LexiconEntry essences;
	public static LexiconEntry excalibr;	
	public static LexiconEntry flugel;	
	public static LexiconEntry infuser;
	public static LexiconEntry mask;
	//public static LexiconEntry mjolnir;
	public static LexiconEntry mobs;
	public static LexiconEntry ores;
	public static LexiconEntry pixie;
	public static LexiconEntry portal;
	public static LexiconEntry pylons;
	public static LexiconEntry reality;
	public static LexiconEntry ruling;
	public static LexiconEntry runes;
	public static LexiconEntry soul;
	public static LexiconEntry trade;
	//public static LexiconEntry trans;		// BACK
	public static LexiconEntry worldgen;

	// Elven Story information
	public static LexiconEntry es;
	public static LexiconEntry races;
	
	// MMO info
	public static LexiconEntry parties;
	public static LexiconEntry spells;
	public static LexiconEntry targets;
	
	public static void preInit() {
		BotaniaAPI.addCategory(categoryAlfheim = new BLexiconCategory("Alfheim", 5));

		auraAlf = new BLexiconEntry("auraAlf", categoryAlfheim);
		advMana	= new BLexiconEntry("advMana",	categoryAlfheim);
		alfheim	= new BLexiconEntry("alfheim",	categoryAlfheim);
		anyavil	= new BLexiconEntry("anyavil",	categoryAlfheim);
		elemSet	= new BLexiconEntry("elemSet",	categoryAlfheim);
		elvenSet= new BLexiconEntry("elvenSet",	categoryAlfheim);
		elves	= new BLexiconEntry("elves",	categoryAlfheim);
		elvorium= new BLexiconEntry("elvorium",	categoryAlfheim);
		essences= new BLexiconEntry("essences",	categoryAlfheim);
		flugel	= new BLexiconEntry("flugel",	categoryAlfheim);
		infuser	= new BLexiconEntry("infuser",	categoryAlfheim);
		mobs	= new BLexiconEntry("mobs",		categoryAlfheim);
		ores	= new BLexiconEntry("ores",		categoryAlfheim);
		pixie	= new BLexiconEntry("pixie", 	categoryAlfheim);
		portal	= new BLexiconEntry("portal",	categoryAlfheim);
		pylons	= new BLexiconEntry("pylons",	categoryAlfheim);
		reality	= new BLexiconEntry("reality",	categoryAlfheim);
		ruling	= new BLexiconEntry("ruling",	categoryAlfheim);
		runes	= new BLexiconEntry("runes",	categoryAlfheim);
		trade	= new BLexiconEntry("trade",	categoryAlfheim);
		//trans	= new BLexiconEntry("trans",	categoryAlfheim); BACK
		worldgen= new BLexiconEntry("worldgen",	categoryAlfheim);
		
		if (AlfheimCore.enableElvenStory) preInitElvenStory();
	}
	
	private static void preInitElvenStory() {
		es		= new BLexiconEntry("es",		categoryAlfheim);
		races	= new BLexiconEntry("races",	categoryAlfheim);
		
		preInitMMO();
	}
	
	private static void preInitMMO() {
		parties	= new BLexiconEntry("parties",	categoryAlfheim);
		spells	= new BLexiconEntry("spells",	categoryAlfheim);
		targets	= new BLexiconEntry("targets",	categoryAlfheim);
	}
	
	public static void preInit2() {
		excalibr= new RLexiconEntry("excaliber",categoryAlfheim	, AlfheimAchievements.excaliber);
		mask	= new RLexiconEntry("mask",		categoryAlfheim	, AlfheimAchievements.mask);
		//mjolnir	= new RLexiconEntry("mjolnir",	categoryAlfheim	, AlfheimAchievements.mjolnir);
		soul	= new RLexiconEntry("soul",		categoryAlfheim	, AlfheimAchievements.flugelSoul);
	}
	
	public static void init() {
		// In progress order
		
		alfheim	.setPriority().setKnowledgeType(BotaniaAPI.basicKnowledge)
				.setLexiconPages(new PageText("0"), new PageText("1"))
				.setIcon(new ItemStack(AlfheimBlocks.alfheimPortal));
		
		elves	.setPriority().setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"), new PageText("1"), new PageText("2"), new PageText("3"), new PageText("4"));
		
		pylons	.setPriority().setKnowledgeType(BotaniaAPI.basicKnowledge)
				.setLexiconPages(new PageText("0"), new PageCraftingRecipe("1", AlfheimRecipes.recipeElvenPylon), new PageCraftingRecipe("2", AlfheimRecipes.recipeGaiaPylon))
				.setIcon(new ItemStack(AlfheimBlocks.alfheimPylons, 1, 0));
		
		class PageTextFlaged extends PageText {
			public PageTextFlaged(String unName) { super(unName); }
			public String getUnlocalizedName() { return unlocalizedName + (AlfheimConfig.destroyPortal ? "t" : "f"); }
		}
		
		portal	.setPriority().setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"), new PageText("1"), new PageText("2"), new PageText("3"),
								 new PageCraftingRecipe("4", AlfheimRecipes.recipeAlfheimPortal),
								 new PageElvenRecipe("5", AlfheimRecipes.recipeInterdimensional),
								 new PageMultiblock("6", TileAlfheimPortal.makeMultiblockSet()),
								 new PageText("7"), new PageTextFlaged("8"));
		
		worldgen.setKnowledgeType(kt)
				.setLexiconPages(new PageTextLearnableKnowledge("0", Knowledge.GLOWSTONE),
								 new PagePureDaisyRecipe("1", AlfheimRecipes.recipeDreamwood),
								 //new PageCraftingRecipe("2", AlfheimRecipes.recipeGlowstone),
								 new PageText("3"),
								 new PageCraftingRecipe("4", AlfheimRecipes.recipeLivingcobble),
								 new PageCraftingRecipe("5", AlfheimRecipes.recipeLivingrockPickaxe),
								 new PageCraftingRecipe("6", AlfheimRecipes.recipeManasteelHoe),
								 new PageCraftingRecipe("7", AlfheimRecipes.recipeEmentiumHoe),
								 new PageCraftingRecipe("8", AlfheimRecipes.recipeFurnace))
				.setIcon(new ItemStack(AlfheimBlocks.dreamLeaves));
		worldgen.addExtraDisplayedRecipe(new ItemStack(AlfheimBlocks.elvenSand));
		worldgen.addExtraDisplayedRecipe(new ItemStack(AlfheimBlocks.dreamLog));
		worldgen.addExtraDisplayedRecipe(new ItemStack(AlfheimBlocks.dreamLeaves));
		worldgen.addExtraDisplayedRecipe(new ItemStack(AlfheimBlocks.dreamSapling));
		LexiconRecipeMappings.map(new ItemStack(AlfheimBlocks.elvenSand), worldgen, 3);
		LexiconRecipeMappings.map(new ItemStack(AlfheimBlocks.dreamLog), worldgen, 1);
		LexiconRecipeMappings.map(new ItemStack(AlfheimBlocks.dreamLeaves), worldgen, 1);
		LexiconRecipeMappings.map(new ItemStack(AlfheimBlocks.dreamSapling), worldgen, 1);
		
		
		ores	.setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"), new PageText("1"), new PageText("2"))
				.setIcon(new ItemStack(AlfheimBlocks.elvenOres, 1, 4));
		for (int i = 0; i < ((BlockElvenOres) AlfheimBlocks.elvenOres).names.length; i++) ores.addExtraDisplayedRecipe(new ItemStack(AlfheimBlocks.elvenOres, 1, i));
		LexiconRecipeMappings.map(new ItemStack(AlfheimBlocks.elvenOres, 1, 1), ores, 1);
		LexiconRecipeMappings.map(new ItemStack(AlfheimBlocks.elvenOres, 1, 0), ores, 2);
		LexiconRecipeMappings.map(new ItemStack(AlfheimBlocks.elvenOres, 1, 2), ores, 2);
		LexiconRecipeMappings.map(new ItemStack(AlfheimBlocks.elvenOres, 1, 3), ores, 2);
		LexiconRecipeMappings.map(new ItemStack(AlfheimBlocks.elvenOres, 1, 4), ores, 3);
		LexiconRecipeMappings.map(new ItemStack(AlfheimItems.elvenResource, 1, ElvenResourcesMetas.IffesalDust), ores, 3);
		
		mobs	.setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"), new PageText("1"))
				.setIcon(new ItemStack(ModItems.manaResource, 1, 8));
		
		pixie	.setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"), new PageCraftingRecipe("1", AlfheimRecipes.recipePixieAttractor))
				.setIcon(new ItemStack(AlfheimItems.pixieAttractor));
		
		anyavil	.setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"), new PageCraftingRecipe("1", AlfheimRecipes.recipeAnyavil));
		
		infuser	.setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"), new PageText("1"),
								 new PageCraftingRecipe("2", AlfheimRecipes.recipeManaInfusionCore),
								 new PageCraftingRecipe("3", AlfheimRecipes.recipeManaInfuser),
								 new PageText("4"),
								 new PageMultiblockLearnable("5", TileManaInfuser.makeMultiblockSetUnknown(), TileManaInfuser.makeMultiblockSet(), AlfheimAchievements.infuser))
				.setIcon(new ItemStack(AlfheimBlocks.manaInfuser));
		
		elvorium.setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"),
								 new PageManaInfusorRecipe("1", AlfheimRecipes.recipeElvorium))
				.setIcon(new ItemStack(AlfheimItems.elvenResource, 1, ElvenResourcesMetas.ElvoriumIngot));
		LexiconRecipeMappings.map(new ItemStack(AlfheimBlocks.elvoriumBlock), elvorium, 2);
		LexiconRecipeMappings.map(new ItemStack(AlfheimItems.elvenResource, 1, ElvenResourcesMetas.ElvoriumIngot), elvorium, 2);
		
		trade	.setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"), new PageText("1"),
								 new PageCraftingRecipe("2", AlfheimRecipes.recipeElvoriumPylon),
								 new PageCraftingRecipe("3", AlfheimRecipes.recipeTradePortal),
								 new PageMultiblock("4", TileTradePortal.makeMultiblockSet()))
				.setIcon(new ItemStack(AlfheimBlocks.tradePortal));
		
		List<IRecipe> powerRecipes = new ArrayList();
		powerRecipes.add(AlfheimRecipes.recipeMuspelheimPowerIngot);
		powerRecipes.add(AlfheimRecipes.recipeNiflheimPowerIngot);
		essences.setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"),
								 new PageTextLearnableAchievement("2", AlfheimAchievements.flugelKill),
								 new PageText("4"), new PageText("5"), new PageCraftingRecipe("6", powerRecipes),
								 new PageText("7"), new PageManaInfusorRecipe("8", AlfheimRecipes.recipeMauftrium))
				.setIcon(new ItemStack(ModItems.manaResource, 1, 5));
		essences.addExtraDisplayedRecipe(new ItemStack(AlfheimItems.elvenResource, 1, ElvenResourcesMetas.NiflheimPowerIngot));
		essences.addExtraDisplayedRecipe(new ItemStack(AlfheimItems.elvenResource, 1, ElvenResourcesMetas.NiflheimEssence));
		essences.addExtraDisplayedRecipe(new ItemStack(AlfheimItems.elvenResource, 1, ElvenResourcesMetas.MuspelheimEssence));
		LexiconRecipeMappings.map(new ItemStack(AlfheimItems.elvenResource, 1, ElvenResourcesMetas.MuspelheimEssence), essences, 2);
		LexiconRecipeMappings.map(new ItemStack(AlfheimItems.elvenResource, 1, ElvenResourcesMetas.NiflheimEssence), essences, 2);
		LexiconRecipeMappings.map(new ItemStack(AlfheimBlocks.mauftriumBlock), essences, 8);
		LexiconRecipeMappings.map(new ItemStack(AlfheimItems.elvenResource, 1, ElvenResourcesMetas.MauftriumNugget), essences, 8);
		
		List<RecipeRuneAltar> runeRecipes = new ArrayList();
		runeRecipes.add(AlfheimRecipes.recipeMuspelheimRune);
		runeRecipes.add(AlfheimRecipes.recipeNiflheimRune);
		runes	.setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"), new PageRuneRecipe("1", runeRecipes),
								 new PageText("2"), new PageText("3"), new PageRuneRecipe("4", AlfheimRecipes.recipeRealityRune))
				.setIcon(new ItemStack(AlfheimItems.elvenResource, 1, ElvenResourcesMetas.PrimalRune));
		runes	.addExtraDisplayedRecipe(new ItemStack(AlfheimItems.elvenResource, 1, ElvenResourcesMetas.NiflheimRune));
		
		List<IRecipe> elvHelmRecs = new ArrayList();
		elvHelmRecs.add(AlfheimRecipes.recipeElvoriumHelmet);
		if (Botania.thaumcraftLoaded) elvHelmRecs.add(AlfheimRecipes.recipeElvoriumHelmetRevealing);
		elvenSet.setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"),
								 new PageCraftingRecipe("1", elvHelmRecs),
								 new PageCraftingRecipe("2", AlfheimRecipes.recipeElvoriumChestplate),
								 new PageCraftingRecipe("3", AlfheimRecipes.recipeElvoriumLeggings),
								 new PageCraftingRecipe("4", AlfheimRecipes.recipeElvoriumBoots))
				.setIcon(new ItemStack(AlfheimItems.elvoriumHelmet));
		
		List<IRecipe> elemHelmRecs = new ArrayList();
		elemHelmRecs.add(AlfheimRecipes.recipeEmentalHelmet);
		if (Botania.thaumcraftLoaded) elemHelmRecs.add(AlfheimRecipes.recipeEmentalHelmetRevealing);
		elemSet	.setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"),
								 new PageCraftingRecipe("1", elemHelmRecs),
								 new PageCraftingRecipe("2", AlfheimRecipes.recipeEmentalChestplate),
								 new PageCraftingRecipe("3", AlfheimRecipes.recipeEmentalLeggings),
								 new PageCraftingRecipe("4", AlfheimRecipes.recipeEmentalBoots))
				.setIcon(new ItemStack(AlfheimItems.elementalHelmet));
		
		List<IRecipe> ringRecipes = new ArrayList();
		ringRecipes.add(AlfheimRecipes.recipeManaRingGod1);
		ringRecipes.add(AlfheimRecipes.recipeManaRingGod2);
		advMana	.setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"), new PageText("1"),
								 new PageManaInfusorRecipe("2", AlfheimRecipes.recipeManaStone),
								 new PageManaInfusorRecipe("3", AlfheimRecipes.recipeManaStoneGreater),
								 new PageText("4"),
								 new PageCraftingRecipe("5", AlfheimRecipes.recipeManaRingElven),
								 new PageCraftingRecipe("6", ringRecipes))
				.setIcon(new ItemStack(AlfheimItems.manaStone));
		
		auraAlf	.setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"),
								 new PageCraftingRecipe("1", AlfheimRecipes.recipeAuraRingElven),
								 new PageCraftingRecipe("2", AlfheimRecipes.recipeAuraRingGod))
				.setIcon(new ItemStack(AlfheimItems.auraRingElven));
		
		List<IRecipe> amuletRecipes = new ArrayList();
		amuletRecipes.add(AlfheimRecipes.recipeMuspelheimPendant);
		amuletRecipes.add(AlfheimRecipes.recipeNiflheimPendant);
		ruling	.setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"), new PageText("1"),
								 new PageCraftingRecipe("2", AlfheimRecipes.recipeMuspelheimRod),
								 new PageCraftingRecipe("3", AlfheimRecipes.recipeNiflheimRod),
								 new PageText("4"), new PageCraftingRecipe("5", amuletRecipes))
				.setIcon(new ItemStack(AlfheimItems.rodFire));
		
		reality	.setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"), new PageText("1"),
								 new PageCraftingRecipe("2", AlfheimRecipes.recipeSword))
				.setIcon(new ItemStack(AlfheimItems.realitySword));
		
		flugel	.setKnowledgeType(kt)
				.setLexiconPages(new PageText("0"), new PageText("1"), new PageText("2"), new PageText("3"))
				.setIcon(new ItemStack(ModItems.flightTiara, 1, 1));
		LexiconRecipeMappings.map(new ItemStack(AlfheimBlocks.flugelHead), flugel, 1);
		LexiconRecipeMappings.map(new ItemStack(AlfheimItems.flugelHead), flugel, 1);
		LexiconRecipeMappings.map(new ItemStack(AlfheimItems.flugelDisc), flugel, 1);
		
		soul	.setKnowledgeType(BotaniaAPI.relicKnowledge)
				.setLexiconPages(new PageText("0"), new PageText("1"))
				.setIcon(new ItemStack(AlfheimItems.flugelSoul));
		LexiconRecipeMappings.map(new ItemStack(AlfheimItems.flugelSoul), soul, 1);
		
		mask	.setKnowledgeType(BotaniaAPI.relicKnowledge)
				.setLexiconPages(new PageText("0"))
				.setIcon(new ItemStack(AlfheimItems.mask));
		LexiconRecipeMappings.map(new ItemStack(AlfheimItems.mask), mask, 1);
		
		excalibr.setKnowledgeType(BotaniaAPI.relicKnowledge)
				.setLexiconPages(new PageTextLearnableAchievement("0", AlfheimAchievements.excaliber))
				.setIcon(new ItemStack(AlfheimItems.excaliber));
		LexiconRecipeMappings.map(new ItemStack(AlfheimItems.excaliber), excalibr, 1);
		
		/*mjolnir	.setKnowledgeType(BotaniaAPI.relicKnowledge)
				.setLexiconPages(new PageText("0"))
				.setIcon(new ItemStack(AlfheimItems.mjolnir));*/
		
		if (AlfheimCore.enableElvenStory) initElvenStory();
		
		LexiconData.gaiaRitual.pages.clear();
		LexiconData.gaiaRitual.setLexiconPages(new PageText("0"), new PageCraftingRecipe("1", AlfheimRecipes.recipeGaiaPylon),
				new PageMultiblock("2", ModMultiblocks.gaiaRitual), new PageText("3"), new PageText("4"),
				new PageText("5"));
		
		LexiconData.sparks.pages.clear();
		LexiconData.sparks.setLexiconPages(new PageText("0"), new PageText("1"),
				new PageCraftingRecipe("2", AlfheimRecipes.recipesSpark),
				new PageText("3"));
	}
	
	private static void initElvenStory() {
		es		.setKnowledgeType(BotaniaAPI.basicKnowledge).setPriority()
				.setLexiconPages(new PageText("0"));
		
		races	.setKnowledgeType(BotaniaAPI.basicKnowledge).setPriority()
				.setLexiconPages(new PageText("0"), new PageText("1"), new PageText("2"));
		
		initMMO();
	}
	
	private static void initMMO() {
		parties	.setKnowledgeType(BotaniaAPI.basicKnowledge).setPriority()
				.setLexiconPages(new PageText("0"), new PageText("1"), new PageCraftingRecipe("2", AlfheimRecipes.recipePeacePipe),
								 new PageText("3"), new PageCraftingRecipe("4", AlfheimRecipes.recipePaperBreak))
				.setIcon(null);
		
		spells	.setKnowledgeType(BotaniaAPI.basicKnowledge).setPriority()
				.setLexiconPages(new PageText("0"), new PageText("1"), new PageText("2"), new PageText("3"));
		
		targets	.setKnowledgeType(BotaniaAPI.basicKnowledge).setPriority()
				.setLexiconPages(new PageText("0"), new PageText("1"));
	}
	
	public static void postInit() {
		postInitMMO();
	}
	
	private static void postInitMMO() {
		ArrayList<SpellBase> l = Lists.newArrayList(AlfheimAPI.spells);
		l.sort(new Comparator<SpellBase>() {
			@Override
			public int compare(SpellBase s1, SpellBase s2) {
				return s1.name.compareTo(s2.name);
			}
		});
		for (SpellBase spell : l) spells.addPage(new PageSpell(spell));
	}
}