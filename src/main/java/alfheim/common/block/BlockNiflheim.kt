package alfheim.common.block

import alexsocol.asjlib.*
import alfheim.api.lib.LibRenderIDs
import alfheim.client.core.helper.IconHelper
import alfheim.common.block.base.BlockMod
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.world.World

class BlockNiflheim: BlockMod(Material.rock) {
	
	lateinit var icons: Array<IIcon>
	lateinit var iconsPillar: Array<IIcon>
	lateinit var iconsRunic: Array<IIcon>
	
	init {
		setBlockName("NiflheimBlock")
		setHardness(3f)
		setHarvestLevel("pickaxe", 1)
		setResistance(30f)
		setStepSound(soundTypeStone)
	}
	
	override fun onBlockPlaced(world: World?, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, meta: Int): Int {
		return if (meta == 7 || meta == 10) {
			when (side) {
				2, 3 -> meta + 2
				4, 5 -> meta + 1
				else -> meta
			}
		} else meta
	}
	
	override fun getPickBlock(target: MovingObjectPosition, world: World, x: Int, y: Int, z: Int, player: EntityPlayer) =
		createStackedBlock(world.getBlockMetadata(x, y, z))
	
	override fun createStackedBlock(meta: Int) = ItemStack(this, 1, if (meta == 2) 2 else damageDropped(meta))
	
	override fun shouldRegisterInNameSet() = false
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockNiflheim::class.java, name)
		return super.setBlockName(name)
	}
	
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = Array(NiflheimBlockMetas.values().size - 6) { IconHelper.forBlock(reg, this, NiflheimBlockMetas.values()[it].modname) }
		arrayOf("Side", "Top").apply {
			iconsPillar = map { IconHelper.forBlock(reg, this@BlockNiflheim, "Pillar$it") }.toTypedArray()
			iconsRunic = map { IconHelper.forBlock(reg, this@BlockNiflheim, "Runic$it") }.toTypedArray()
		}
	}
	
	override fun getSubBlocks(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		var prev: String? = null
		
		NiflheimBlockMetas.values().forEachIndexed { id, it ->
			val s = it.modname
			if (s != prev)
				list.add(ItemStack(item, 1, id))
			prev = s
		}
		
//		subtypes.forEachIndexed { i, s -> if (s.isNotEmpty()) list.add(ItemStack(item, 1, i)) }
	}
	
	override fun getIcon(side: Int, meta: Int) = when (meta) {
		in 0..6 -> icons[meta]
		7, 10   -> (if (meta < 10) iconsPillar else iconsRunic)[if (side in 0..1) 1 else 0]
		8, 11   -> (if (meta < 10) iconsPillar else iconsRunic)[if (side in 4..5) 1 else 0]
		9, 12   -> (if (meta < 10) iconsPillar else iconsRunic)[if (side in 2..3) 1 else 0]
		else    -> null
	}
	
	override fun damageDropped(meta: Int) = when (meta) {
		2         -> ElvenResourcesMetas.Nifleur.I
		8, 9      -> 7
		11, 12    -> 10
		in 13..15 -> 0
		else      -> meta
	}
	
	override fun getDrops(world: World, x: Int, y: Int, z: Int, meta: Int, fortune: Int): ArrayList<ItemStack> {
		val ret = ArrayList<ItemStack>()
		val item = if (meta == 2) AlfheimItems.elvenResource else this.toItem()
		ret.add(ItemStack(item, if (meta == 2) fortune + 1 else 1, damageDropped(meta)))
		return ret
	}
	
	override fun getDamageValue(world: World, x: Int, y: Int, z: Int) = damageDropped(world.getBlockMetadata(x, y, z))
	
	override fun getRenderType() = LibRenderIDs.idNiflheim
	
	enum class NiflheimBlockMetas {
		STONE, COBBLESTONE, ORE, BRICKS, CRACKED, CHISELED, POLISHED, PILLAR, PILLAR_1, PILLAR_2, RUNIC, RUNIC_1, RUNIC_2;
		
		val modname get() = name.lowercase().capitalized().substringBefore("_")
		val I get() = ordinal
		val stack get() = ItemStack(AlfheimBlocks.niflheimBlock, 1, ordinal)
		fun stack(size: Int) = ItemStack(AlfheimBlocks.niflheimBlock, size, ordinal)
	}
	
	class ItemBlockNiflheim(block: Block): ItemMultiTexture(block, block, NiflheimBlockMetas.values().map { it.modname }.toTypedArray())
}
