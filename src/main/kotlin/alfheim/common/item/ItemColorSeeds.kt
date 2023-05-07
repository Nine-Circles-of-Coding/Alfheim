package alfheim.common.item

import alexsocol.asjlib.*
import alfheim.api.lib.LibResourceLocations
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.BlockAuroraDirt
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.item.relic.ItemSifRing
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.passive.EntitySheep
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.inventory.IInventory
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.world.World
import vazkii.botania.api.recipe.IFlowerComponent
import vazkii.botania.common.Botania
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.block.decor.IFloatingFlower
import vazkii.botania.common.item.IFloatingFlowerVariant
import java.awt.Color
import java.util.*

class ItemColorSeeds: ItemIridescent("irisSeeds"), IFlowerComponent, IFloatingFlowerVariant {
	
	override fun getIslandType(stack: ItemStack) = islandTypes[stack.meta % (TYPES + 2)]
	
	override fun canFit(stack: ItemStack, inventory: IInventory) = stack.meta == TYPES
	
	override fun getParticleColor(stack: ItemStack) = rainbowColor()
	
	override fun getSubItems(par1: Item, par2: CreativeTabs?, par3: MutableList<Any?>) {
		for (i in 0..AURORA)
			par3.add(ItemStack(par1, 1, i))
	}
	
	override fun onItemUse(stack: ItemStack, player: EntityPlayer?, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val block = world.getBlock(x, y, z)
		val bmeta = world.getBlockMetadata(x, y, z)
		val meta = stack.meta
		
		if (!(block === Blocks.dirt || block === Blocks.grass) || bmeta != 0) return false
		
		addBlockSwapper(world, player, x, y, z, meta)
		
		val color = Color(if (meta == AURORA) BlockAuroraDirt.getBlockColor(x, y, z) else getColorFromItemStack(stack, 0))
		val r = color.red / 255F
		val g = color.green / 255F
		val b = color.blue / 255F
		
		var px: Double
		var py: Double
		var pz: Double
		val velMul = 0.025f
		
		for (i in 0..49) {
			px = (Math.random() - 0.5) * 3
			py = Math.random() - 0.5 + 1
			pz = (Math.random() - 0.5) * 3
			Botania.proxy.wispFX(world, x + 0.5 + px, y + 0.5 + py, z + 0.5 + pz, r, g, b, Math.random().F * 0.15f + 0.15f, (-px).F * velMul, (-py).F * velMul, (-pz).F * velMul)
		}
		stack.stackSize--
		
		return true
	}
	
	companion object {
		
		var worldGen = false
		
		private val blockSwappers = HashMap<Int, MutableList<BlockSwapper>>()
		
		init {
			eventFML()
		}
		
		const val AURORA = 17
		
		val islandOvergrowth = IFloatingFlower.IslandType("OVERGROWTH", LibResourceLocations.miniIslandOvergrowth)
		
		val islandTypes: Array<IFloatingFlower.IslandType> = Array(TYPES + 2) { i ->
			IridescentIslandType("IRIDESCENT$i", LibResourceLocations.miniIsland, i)
		}
		
		fun dirtFromMeta(meta: Int): Block {
			if (meta == AURORA)
				return AlfheimBlocks.auroraDirt
			if (meta == TYPES)
				return AlfheimBlocks.rainbowDirt
			return AlfheimBlocks.irisDirt
		}
		
		fun addBlockSwapper(world: World, player: EntityPlayer?, x: Int, y: Int, z: Int, meta: Int) {
			val swapper = BlockSwapper(world, player, ChunkCoordinates(x, y, z), meta, worldGen)
			world.setBlock(x, y, z, swapper.blockToSet, swapper.metaToSet, 3)
			blockSwappers.computeIfAbsent(world.provider.dimensionId) { ArrayList() }.add(swapper)
			
			if (meta == 1000) return
			
			val aBlock = world.getBlock(x, y + 1, z)
			val aMeta = world.getBlockMetadata(x, y + 1, z)
			
			if (aBlock == Blocks.tallgrass && aMeta == 1) {
				if (meta >= 16)
					world.setBlock(x, y + 1, z, AlfheimBlocks.rainbowGrass, meta - 16, 4)
				else
					world.setBlock(x, y + 1, z, AlfheimBlocks.irisGrass, swapper.metaToSet, 4)
			} else if (aBlock == Blocks.double_plant && aMeta == 2) {
				if (meta >= 16) {
					world.setBlock(x, y + 1, z, AlfheimBlocks.rainbowTallGrass, meta - 16, 2)
					world.setBlock(x, y + 2, z, AlfheimBlocks.rainbowTallGrass, meta - 8, 2)
				} else if (swapper.metaToSet < 8) {
					world.setBlock(x, y + 1, z, AlfheimBlocks.irisTallGrass0, swapper.metaToSet, 2)
					world.setBlock(x, y + 2, z, AlfheimBlocks.irisTallGrass0, 8, 2)
				} else {
					world.setBlock(x, y + 1, z, AlfheimBlocks.irisTallGrass1, swapper.metaToSet - 8, 2)
					world.setBlock(x, y + 2, z, AlfheimBlocks.irisTallGrass1, 8, 2)
				}
			}
		}
		
		@SubscribeEvent
		fun onTickEnd(event: TickEvent.WorldTickEvent) {
			if (event.phase != TickEvent.Phase.END) return
			blockSwappers[event.world.provider.dimensionId]?.removeAll { !it.tick() }
		}
		
		class IridescentIslandType(name: String, rs: ResourceLocation, val colorIndex: Int): IFloatingFlower.IslandType(name, rs) {
			
			override fun getColor(): Int {
				if (colorIndex == TYPES) {
					return Color(rainbowColor()).darker().rgb
				}
				if (colorIndex >= EntitySheep.fleeceColorTable.size + 1)
					return 0xFFFFFF
				
				val color = EntitySheep.fleeceColorTable[colorIndex]
				return Color(color[0], color[1], color[2]).darker().rgb
			}
		}
		
		private class BlockSwapper(var world: World, player: EntityPlayer?, coords: ChunkCoordinates, meta: Int, worldGen: Boolean) {
			
			var rand: Random
			var blockToSet: Block
			var rainbow: Boolean
			var metaToSet: Int
			
			var grassBlock: Block
			var tallGrassMeta: Int
			var tallGrassBlock: Block
			
			var startCoords: ChunkCoordinates
			var ticksExisted = 0
			
			val range: Int
			val TICK_RANGE = 1
			
			init {
				val seed = coords.posX xor coords.posY xor coords.posZ
				rand = Random(seed.toLong())
				blockToSet = if (meta == 1000) ModBlocks.enchantedSoil else dirtFromMeta(meta)
				rainbow = meta >= 16
				metaToSet = if (meta == 1000) 0 else meta % 16
				
				grassBlock = if (rainbow) AlfheimBlocks.rainbowGrass else AlfheimBlocks.irisGrass
				tallGrassMeta = metaToSet % 8
				tallGrassBlock = if (rainbow) AlfheimBlocks.rainbowTallGrass else (if (meta > 8) AlfheimBlocks.irisTallGrass1 else AlfheimBlocks.irisTallGrass0)
				
				startCoords = coords
				
				range = if (worldGen && meta != 1000) rand.nextInt(8) + 16 else if (player != null && !RagnarokHandler.blockedPowers[1] && ItemSifRing.getSifRing(player) != null) 6 else 3
			}
			
			fun tick(): Boolean {
				ticksExisted++
				for (i in -range..range) {
					for (j in -2..2) {
						for (k in -range..range) {
							val x = startCoords.posX + i
							val y = startCoords.posY + j
							val z = startCoords.posZ + k
							val block = world.getBlock(x, y, z)
							val meta = world.getBlockMetadata(x, y, z)
							
							if (block === blockToSet && meta == metaToSet) {
								// Only make changes every 20 ticks
								if (ticksExisted % 20 != 0) continue
								
								tickBlock(x, y, z)
							}
						}
					}
				}
				
				return ticksExisted < 80
			}
			
			fun tickBlock(x: Int, y: Int, z: Int) {
				val validCoords = ArrayList<ChunkCoordinates>()
				
				for (xOffset in -TICK_RANGE..TICK_RANGE) {
					for (yOffset in -TICK_RANGE..TICK_RANGE) {
						for (zOffset in -TICK_RANGE..TICK_RANGE) {
//							if (xOffset == 0 && yOffset == 0 && zOffset == 0) continue
							
							if (isValidSwapPosition(x + xOffset, y + yOffset, z + zOffset))
								validCoords.add(ChunkCoordinates(x + xOffset, y + yOffset, z + zOffset))
						}
					}
				}
				
				if (validCoords.isEmpty() || world.isRemote) return
				
				val (tX, tY, tZ) = validCoords.random(rand)!!
				world.setBlock(tX, tY, tZ, blockToSet, metaToSet, 3)
				if (blockToSet == ModBlocks.enchantedSoil) return
				
				val blockAbove = world.getBlock(tX, tY + 1, tZ)
				val metaAbove = world.getBlockMetadata(tX, tY + 1, tZ)
				
				if (blockAbove == Blocks.tallgrass && metaAbove == 1) {
					world.setBlock(tX, tY + 1, tZ, grassBlock, metaToSet, 1 or 2)
				} else if (blockAbove == Blocks.double_plant && metaAbove == 2) {
					world.setBlock(tX, tY + 1, tZ, tallGrassBlock, tallGrassMeta, 2)
					world.setBlock(tX, tY + 2, tZ, tallGrassBlock, 8, 2)
				}
			}
			
			fun isValidSwapPosition(x: Int, y: Int, z: Int): Boolean {
				val block = world.getBlock(x, y, z)
				val meta = world.getBlockMetadata(x, y, z)
				val aboveBlock = world.getBlock(x, y + 1, z)
				
				return (block == Blocks.dirt || block == Blocks.grass)
						&& (meta == 0)
						&& (aboveBlock.getLightOpacity(world, x, y, z) <= 1)
			}
		}
	}
}
