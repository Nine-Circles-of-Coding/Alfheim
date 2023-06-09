package alfheim.common.block

import alexsocol.asjlib.extendables.block.*
import alfheim.api.ModInfo
import alfheim.client.core.helper.IconHelper
import alfheim.common.block.AlfheimBlocks.setHarvestLevelI
import alfheim.common.block.alt.BlockYggDecor
import alfheim.common.block.base.BlockStairsMod
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.ItemBlockLeavesMod
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.*
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.client.lib.LibResources
import vazkii.botania.common.block.*
import vazkii.botania.common.block.decor.slabs.BlockModSlab
import vazkii.botania.common.block.decor.stairs.BlockModStairs
import vazkii.botania.common.block.decor.walls.BlockModWall

@Suppress("JoinDeclarationAndAssignment")
object AlfheimFluffBlocks {
	
	val dreamwoodFence: Block
	val dreamwoodFenceGate: Block
	val dreamwoodBarkFence: Block
	val dreamwoodBarkFenceGate: Block
	val dwarfLantern: Block
	val dwarfPlanks: Block
	val dwarfTrapDoor: Block
	val elfQuartzWall: Block
	val elvenSandstone: Block
	val elvenSandstoneStairs: List<Block>
	val elvenSandstoneSlab: Block
	val elvenSandstoneSlabFull: Block
	val elvenSandstoneSlab2: Block
	val elvenSandstoneSlab2Full: Block
	val elvenSandstoneWalls: List<Block>
	val livingcobbleStairs: Block
	val livingcobbleStairs1: Block
	val livingcobbleStairs2: Block
	val livingcobbleSlab: Block
	val livingcobbleSlabFull: Block
	val livingcobbleSlab1: Block
	val livingcobbleSlabFull1: Block
	val livingcobbleSlab2: Block
	val livingcobbleSlabFull2: Block
	val livingcobbleWall: Block
	val livingMountain: Block
	val livingrockBrickWall: Block
	val livingrockDark: Block
	val livingrockDarkStairs: List<Block>
	val livingrockDarkSlabs: List<Block>
	val livingrockDarkSlabsFull: List<Block>
	val livingrockDarkWalls: List<Block>
	val livingwoodFence: Block
	val livingwoodFenceGate: Block
	val livingwoodBarkFence: Block
	val livingwoodBarkFenceGate: Block
	val roofTile: Block
	val roofTileSlabs: List<Block>
	val roofTileSlabsFull: List<Block>
	val roofTileStairs: List<Block>
	val shrineLight: Block
	val shrineGlass: Block
	val shrinePanel: Block
	val shrinePillar: Block
	val shrineRock: Block
	val shrineRockWhiteSlab: Block
	val shrineRockWhiteSlabFull: Block
	val shrineRockWhiteStairs: Block
	val yggDecor: Block
	
	init {
		yggDecor = BlockYggDecor()
		shrineRock = BlockModMeta(Material.rock, 16, ModInfo.MODID, "ShrineRock", AlfheimTab, 10f, harvLvl = 2, resist = 10000f, folder = "decor/")
		shrinePillar = BlockShrinePillar()
		shrineRockWhiteStairs = object: BlockStairsMod(shrineRock, 0, "ShrineRockWhiteStairs") {
			override fun register() {
				GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
			}
			
			override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = null
		}
		shrineRockWhiteSlab = BlockRockShrineWhiteSlab(false).setCreativeTab(AlfheimTab).setHardness(1.5f)
		shrineRockWhiteSlabFull = BlockRockShrineWhiteSlab(true).setCreativeTab(null).setHardness(1.5f)
		(shrineRockWhiteSlab as BlockModSlab).register()
		(shrineRockWhiteSlabFull as BlockModSlab).register()
		
		val roofs = 3
		roofTile = BlockModMeta(Material.rock, 3, ModInfo.MODID, "CustomRoof", AlfheimTab, 2f, resist = 5f, folder = "decor/").setStepSound(Block.soundTypeStone)
		roofTileSlabs = (0 until roofs).map { BlockRoofTileSlab(false, it).setCreativeTab(AlfheimTab) }
		roofTileSlabsFull = (0 until roofs).map { BlockRoofTileSlab(true, it).setCreativeTab(AlfheimTab) }
		roofTileSlabs.forEach { (it as BlockModSlab).register() }
		roofTileSlabsFull.forEach { (it as BlockModSlab).register() }
		roofTileStairs = (0 until roofs).map { BlockModStairs(roofTile, it, "CustomRoofStairs$it").setCreativeTab(AlfheimTab) }
		
		livingMountain = BlockLivingMountain()
		
		val metas = (0..3) - 2
		livingrockDark = BlockModMeta(Material.rock, 4, ModInfo.MODID, "DarkLivingRock", AlfheimTab, 2f, resist = 10f, folder = "decor/")
		livingrockDarkStairs = metas.map { BlockModStairs(livingrockDark, it, "DarkLivingRockStairs$it").setCreativeTab(AlfheimTab) }
		
		livingrockDarkSlabs = metas.map { BlockLivingrockDarkSlab(false, it).setCreativeTab(AlfheimTab) }
		livingrockDarkSlabsFull = metas.map { BlockLivingrockDarkSlab(true, it).setCreativeTab(AlfheimTab) }
		livingrockDarkSlabs.forEach { (it as BlockModSlab).register() }
		livingrockDarkSlabsFull.forEach { (it as BlockModSlab).register() }
		
		livingrockDarkWalls = (0..1).map {
			BlockModWall(livingrockDark, it)
				.setCreativeTab(AlfheimTab)
				.setHarvestLevelI("pickaxe", 2)
		}
		
		dwarfLantern = BlockDwarfLantern()
		
		shrineLight = BlockModMeta(Material.glass, 6, ModInfo.MODID, "ShrineLight", AlfheimTab, resist = 6000f, folder = "decor/").setLightLevel(1f).setLightOpacity(0)
		shrineGlass = BlockShrineGlass()
		shrinePanel = object: BlockPaneMeta(Material.glass, 4, "ShrinePanel", "decor/") {
			override fun getRenderBlockPass() = 1
			override fun canPaneConnectTo(world: IBlockAccess, x: Int, y: Int, z: Int, dir: ForgeDirection) = super.canPaneConnectTo(world, x, y, z, dir) || world.getBlock(x, y, z) == shrineGlass
		}.setBlockName("ShrinePanel")
			.setCreativeTab(AlfheimTab)
			.setLightOpacity(0)
			.setHardness(1f)
			.setHarvestLevelI("pickaxe", 1)
			.setResistance(600f)
			.setStepSound(Block.soundTypeGlass)
		
		elfQuartzWall = BlockModWall(ModFluffBlocks.elfQuartz, 0)
			.setCreativeTab(AlfheimTab)
			.setHarvestLevelI("pickaxe", 2)
		
		dwarfPlanks = BlockModMeta(Material.wood, 1, ModInfo.MODID, "DwarfPlanks", AlfheimTab, 3f, "axe", 1, 100f, "decor/")
		
		elvenSandstone = BlockElvenSandstone()
		elvenSandstoneStairs = arrayOf(0, 2).map {
			BlockModStairs(elvenSandstone, it, "ElvenSandstoneStairs$it")
				.setCreativeTab(AlfheimTab)
		}
		
		elvenSandstoneSlab = BlockElvenSandstoneSlab(false).setCreativeTab(AlfheimTab).setHardness(1.5f)
		elvenSandstoneSlabFull = BlockElvenSandstoneSlab(true).setCreativeTab(null).setHardness(1.5f)
		(elvenSandstoneSlab as BlockModSlab).register()
		(elvenSandstoneSlabFull as BlockModSlab).register()
		
		elvenSandstoneSlab2 = BlockElvenSandstoneSlab2(false).setCreativeTab(AlfheimTab).setHardness(1.5f)
		elvenSandstoneSlab2Full = BlockElvenSandstoneSlab2(true).setCreativeTab(null).setHardness(1.5f)
		(elvenSandstoneSlab2 as BlockModSlab).register()
		(elvenSandstoneSlab2Full as BlockModSlab).register()
		
		elvenSandstoneWalls = arrayOf(0, 2).map {
			BlockModWall(elvenSandstone, it)
				.setCreativeTab(AlfheimTab)
		}
		
		livingcobbleStairs = BlockModStairs(AlfheimBlocks.livingcobble, 0, "LivingCobbleStairs").setCreativeTab(AlfheimTab)
		livingcobbleStairs1 = BlockModStairs(AlfheimBlocks.livingcobble, 1, "LivingCobbleStairs1").setCreativeTab(AlfheimTab)
		livingcobbleStairs2 = BlockModStairs(AlfheimBlocks.livingcobble, 2, "LivingCobbleStairs2").setCreativeTab(AlfheimTab)
		
		livingcobbleSlab = BlockLivingCobbleSlab(false).setCreativeTab(AlfheimTab).setHardness(1.5f)
		livingcobbleSlabFull = BlockLivingCobbleSlab(true).setCreativeTab(null).setHardness(1.5f)
		(livingcobbleSlab as BlockModSlab).register()
		(livingcobbleSlabFull as BlockModSlab).register()
		
		livingcobbleSlab1 = BlockLivingCobbleSlab1(false).setCreativeTab(AlfheimTab).setHardness(1.5f)
		livingcobbleSlabFull1 = BlockLivingCobbleSlab1(true).setCreativeTab(null).setHardness(1.5f)
		(livingcobbleSlab1 as BlockModSlab).register()
		(livingcobbleSlabFull1 as BlockModSlab).register()
		
		livingcobbleSlab2 = BlockLivingCobbleSlab2(false).setCreativeTab(AlfheimTab).setHardness(1.5f)
		livingcobbleSlabFull2 = BlockLivingCobbleSlab2(true).setCreativeTab(null).setHardness(1.5f)
		(livingcobbleSlab2 as BlockModSlab).register()
		(livingcobbleSlabFull2 as BlockModSlab).register()
		
		livingcobbleWall = BlockModWall(AlfheimBlocks.livingcobble, 0)
			.setCreativeTab(AlfheimTab)
			.setHarvestLevelI("pickaxe", 2)
		
		livingrockBrickWall = BlockModWall(ModBlocks.livingrock, 1)
			.setCreativeTab(AlfheimTab)
			.setHarvestLevelI("pickaxe", 2)
		
		livingwoodBarkFenceGate = BlockModFenceGate(ModBlocks.livingwood, 0)
			.setCreativeTab(AlfheimTab)
			.setBlockName("LivingwoodBarkFenceGate")
			.setHardness(2f)
			.setResistance(5f)
			.setStepSound(Block.soundTypeWood)
		
		livingwoodBarkFence = BlockModFence(LibResources.PREFIX_MOD + "livingwood0", Material.wood, livingwoodBarkFenceGate)
			.setBlockName("LivingwoodBarkFence")
			.setCreativeTab(AlfheimTab)
			.setHardness(2f)
			.setResistance(5f)
			.setStepSound(Block.soundTypeWood)
		
		dreamwoodBarkFenceGate = BlockModFenceGate(ModBlocks.dreamwood, 0)
			.setBlockName("DreamwoodBarkFenceGate")
			.setCreativeTab(AlfheimTab)
			.setHardness(2f)
			.setResistance(5f)
			.setStepSound(Block.soundTypeWood)
		
		dreamwoodBarkFence = BlockModFence(LibResources.PREFIX_MOD + "dreamwood0", Material.wood, dreamwoodBarkFenceGate)
			.setBlockName("DreamwoodBarkFence")
			.setCreativeTab(AlfheimTab)
			.setHardness(2f)
			.setResistance(5f)
			.setStepSound(Block.soundTypeWood)
		
		livingwoodFenceGate = BlockModFenceGate(ModBlocks.livingwood, 1)
			.setBlockName("LivingwoodFenceGate")
			.setCreativeTab(AlfheimTab)
			.setHardness(2f)
			.setResistance(5f)
			.setStepSound(Block.soundTypeWood)
		
		livingwoodFence = BlockModFence(LibResources.PREFIX_MOD + "livingwood1", Material.wood, livingwoodFenceGate)
			.setBlockName("LivingwoodFence")
			.setCreativeTab(AlfheimTab)
			.setHardness(2f)
			.setResistance(5f)
			.setStepSound(Block.soundTypeWood)
		
		dreamwoodFenceGate = BlockModFenceGate(ModBlocks.dreamwood, 1)
			.setBlockName("DreamwoodFenceGate")
			.setCreativeTab(AlfheimTab)
			.setHardness(2f)
			.setResistance(5f)
			.setStepSound(Block.soundTypeWood)
		
		dreamwoodFence = BlockModFence(LibResources.PREFIX_MOD + "dreamwood1", Material.wood, dreamwoodFenceGate)
			.setBlockName("DreamwoodFence")
			.setCreativeTab(AlfheimTab)
			.setHardness(2f)
			.setResistance(5f)
			.setStepSound(Block.soundTypeWood)
		
		dwarfTrapDoor = object: BlockModTrapDoor(Material.wood, "DwarfTrapDoor") {
			override fun registerBlockIcons(reg: IIconRegister) {
				blockIcon = IconHelper.forBlock(reg, this, "", "decor")
			}
		}
			.setHardness(3f)
			.setStepSound(Block.soundTypeWood)
	}
}