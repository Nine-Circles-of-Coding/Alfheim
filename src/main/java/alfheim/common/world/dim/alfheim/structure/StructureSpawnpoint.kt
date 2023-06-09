package alfheim.common.world.dim.alfheim.structure

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.AlfheimItems
import net.minecraft.init.*
import net.minecraft.item.*
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.world.World
import vazkii.botania.common.item.ModItems
import java.util.*
import kotlin.math.min

object StructureSpawnpoint {
	
	var inGen = false // possible stack overflow fix
	
	fun generate(world: World, rand: Random) {
		if (inGen) return
		inGen = true
		
		if (!AlfheimConfigHandler.enableElvenStory || AlfheimConfigHandler.bothSpawnStructures) generatePortal(world, 0, 220, 0)
		if (AlfheimConfigHandler.enableElvenStory || AlfheimConfigHandler.bothSpawnStructures) generateStartBox(world, 0, 0, 0, rand)
		
		ASJUtilities.log("Spawn created")
		
		inGen = false
	}
	
	fun generatePortal(world: World, x: Int, y: Int, z: Int) {
		world.setSpawnLocation(x, y, z - 3)
		SchemaUtils.generate(world, x, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/portal"))
	}
	
	fun generateStartBox(world: World, x: Int, y: Int, z: Int, rand: Random) {
		world.setSpawnLocation(x, y + 2, z)
		SchemaUtils.generate(world, x, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/spawnbox"))
		world.setBlock(x, y, z, Blocks.bedrock)
		
		if (!AlfheimConfigHandler.bonusChest) return
		
		world.setBlock(x, y + 2, z - 5, Blocks.chest, 0, 2)
		val tile = world.getTileEntity(x, y + 2, z - 5) as? TileEntityChest ?: return
		
		var count = ASJUtilities.randInBounds((items.size * 0.25).I, (items.size * 0.75).I, rand)
		
		val itemPoses = items.indices.shuffled().toMutableSet()
		
		val slots = (0 until tile.sizeInventory).shuffled().toMutableSet()
		
		while (count > 0) {
			val pos = itemPoses.removeRandom() ?: continue
			val item = items[pos]
			
			if (!ASJUtilities.chance(item.chance)) {
				itemPoses.add(pos)
				continue
			}
			
			--count
			val meta = if (item.maxMeta == -1) item.meta else ASJUtilities.randInBounds(item.meta, item.maxMeta, rand)
			tile[slots.removeRandom() ?: continue] = ItemStack(item.item, min(item.item.getItemStackLimit(ItemStack(item.item, 1, meta)), ASJUtilities.randInBounds(item.countMin, item.countMax, rand)), meta)
		}
	}
	
	val items = arrayOf(
		RandomItemHolder(Items.wooden_pickaxe, 0, 1, 2, 50),
		RandomItemHolder(Items.wooden_axe, 0, 1, 2, 50),
		RandomItemHolder(ModItems.manaResource, 13, 1, 4, 25),
		RandomItemHolder(ModItems.manaResource, 22, 4, 8, 10),
		RandomItemHolder(Items.apple, 0, 5, 10, 75),
		RandomItemHolder(Items.bread, 0, 5, 10, 75),
		RandomItemHolder(ModItems.manaBottle, 0, 1, 3, 50),
		RandomItemHolder(ModItems.blackLotus, 0, 1, 2, 10),
		RandomItemHolder(ModItems.blackLotus, 1, 1, 1, 1),
		RandomItemHolder(ModItems.fertilizer, 1, 5, 15, 50),
		RandomItemHolder(Blocks.sapling.toItem()!!, 0, 2, 3, 50),
		RandomItemHolder(AlfheimBlocks.dreamSapling.toItem()!!, 0, 2, 3, 50),
		RandomItemHolder(ModItems.cosmetic, 0, 1, 1, 25, 31),
		RandomItemHolder(ModItems.cosmetic, 0, 1, 1, 10, 31),
		RandomItemHolder(AlfheimItems.coatOfArms, 0, 1, 1, 25, 17),
		RandomItemHolder(AlfheimItems.coatOfArms, 0, 1, 1, 25, 17),
		RandomItemHolder(Blocks.log.toItem()!!, 0, 8, 16, 45),
		RandomItemHolder(Blocks.log2.toItem()!!, 1, 8, 16, 45),
		RandomItemHolder(AlfheimBlocks.altWood1.toItem()!!, 3, 8, 16, 45)
	)
	
	data class RandomItemHolder(val item: Item, val meta: Int, val countMin: Int, val countMax: Int, val chance: Int, val maxMeta: Int = -1)
}