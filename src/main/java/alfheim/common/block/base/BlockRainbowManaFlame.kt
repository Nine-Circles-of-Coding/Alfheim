package alfheim.common.block.base

import alfheim.api.entity.EnumRace
import alfheim.common.block.tile.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.common.Optional
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.util.EnumChatFormatting.*
import net.minecraft.world.*
import vazkii.botania.api.lexicon.ILexiconable

class BlockRainbowManaFlame: BlockMod(Material.cloth), ILexiconable {
	
	init {
		setBlockName("rainbowFlame")
		val f = 0.25f
		setBlockBounds(f, f, f, 1f - f, 1f - f, 1f - f)
		setCreativeTab(null)
		setLightLevel(1f)
		setStepSound(soundTypeCloth)
	}
	
	@Suppress("OVERRIDE_DEPRECATION", "DEPRECATION") // stupid WAILA uses deprecated method -_-
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int): ItemStack {
		val stack = super.getPickBlock(target, world, x, y, z)
		(world.getTileEntity(x, y, z) as? TileRainbowManaFlame)?.apply {
			if (soul) {
				val name: String
				val eColor: EnumChatFormatting
				if (this !is TileVafthrudnirSoul) {
					val race = EnumRace.values().firstOrNull { it.rgbColor == color } ?: return@apply
					name = StatCollector.translateToLocal("tile.lostsoul.$race.name")
					eColor = race.enumColor
				} else {
					name = StatCollector.translateToLocal("tile.vafthrudnir.name")
					eColor = RED
				}
				stack.setStackDisplayName("$RESET$eColor$name")
			}
			else if (exit) stack.setStackDisplayName("$RESET$GOLD\u001B")
		}
		return stack
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (world.provider.dimensionId != AlfheimConfigHandler.dimensionIDHelheim) return false
		
		val tile = world.getTileEntity(x, y, z) as? TileRainbowManaFlame ?: return false
		if (tile.exit) {
			if (x != 0 || y != 255 || z != 0) return false
			tile.exitPlayer(player)
		} else if (tile is TileVafthrudnirSoul && !world.isRemote) {
			tile.startCharades(player)
		} else {
			return false
		}
		
		return true
	}
	
	@Optional.Method(modid = "easycoloredlights")
	override fun getLightValue(world: IBlockAccess, x: Int, y: Int, z: Int) =
		(world.getTileEntity(x, y, z) as TileRainbowManaFlame).getLightColor()
	
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	override fun getRenderType(): Int = -1
	override fun getIcon(side: Int, meta: Int) = Blocks.fire.getIcon(side, meta)!!
	override fun isOpaqueCube() = false
	override fun renderAsNormalBlock() = false
	override fun hasTileEntity(metadata: Int) = true
	override fun getBlocksMovement(world: IBlockAccess?, x: Int, y: Int, z: Int) = true
	override fun getCollisionBoundingBoxFromPool(world: World?, x: Int, y: Int, z: Int) = null
	override fun getDrops(world: World, x: Int, y: Int, z: Int, metadata: Int, fortune: Int) = ArrayList<ItemStack>()
	override fun createTileEntity(world: World?, meta: Int) = if (meta == 1) TileVafthrudnirSoul() else TileRainbowManaFlame()
	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.rodPrismatic
}