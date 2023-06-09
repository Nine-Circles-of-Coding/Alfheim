package alfheim.common.block.compat.thaumcraft

import alfheim.common.integration.thaumcraft.ThaumcraftAlfheimModule
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.particle.EffectRenderer
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraftforge.common.util.ForgeDirection
import thaumcraft.client.lib.UtilsFX
import thaumcraft.common.config.ConfigItems
import vazkii.botania.api.lexicon.*
import java.util.*

class BlockAlfheimThaumOre: Block(Material.rock), ILexiconable {
	
	val rand = Random()
	
	init {
		setBlockName("blockCustomOre")
		setCreativeTab(ThaumcraftAlfheimModule.tcnTab)
		setHardness(1.5f)
		setHarvestLevel("pickaxe", 2, 0)
		setHarvestLevel("pickaxe", 2, 7)
		setResistance(5f)
		setStepSound(soundTypeStone)
		tickRandomly = true
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(ir: IIconRegister) {
		icon = arrayOf(
			ir.registerIcon("thaumcraft:cinnibarAlf"),
			ir.registerIcon("thaumcraft:infusedorestoneAlf"),
			ir.registerIcon("thaumcraft:infusedore"),
			ir.registerIcon("thaumcraft:amberoreAlf")
		)
	}
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int): IIcon = if (meta == 0) icon[0] else if (meta == 7) icon[3] else icon[1]
	
	override fun canSilkHarvest(world: World?, player: EntityPlayer?, x: Int, y: Int, z: Int, metadata: Int) = true
	
	override fun damageDropped(meta: Int) = meta
	
	@SideOnly(Side.CLIENT)
	override fun getSubBlocks(item: Item, tab: CreativeTabs?, subs: MutableList<Any?>) {
		for (i in 0..7)
			subs.add(ItemStack(item, 1, i))
	}
	
	@SideOnly(Side.CLIENT)
	override fun addHitEffects(worldObj: World, target: MovingObjectPosition, effectRenderer: EffectRenderer?): Boolean {
		val meta = worldObj.getBlockMetadata(target.blockX, target.blockY, target.blockZ)
		
		if (meta in 1..6) {
			UtilsFX.infusedStoneSparkle(worldObj, target.blockX, target.blockY, target.blockZ, meta)
		}
		
		return super.addHitEffects(worldObj, target, effectRenderer)
	}
	
	override fun getDrops(world: World, x: Int, y: Int, z: Int, meta: Int, fortune: Int): ArrayList<ItemStack> {
		val ret = ArrayList<ItemStack>()
		when (meta) {
			0    -> ret.add(ItemStack(ThaumcraftAlfheimModule.alfheimThaumOre, 1, 0))
			7    -> ret.add(ItemStack(ConfigItems.itemResource, 1 + world.rand.nextInt(fortune + 1), 6))
			
			else -> {
				val q = 1 + world.rand.nextInt(2 + fortune)
				
				for (a in 0 until q) {
					ret.add(ItemStack(ConfigItems.itemShard, 1, meta - 1))
				}
			}
		}
		
		return ret
	}
	
	override fun getExpDrop(world: IBlockAccess?, meta: Int, fortune: Int): Int {
		return if (meta != 0 && meta != 7)
			MathHelper.getRandomIntegerInRange(rand, 0, 3)
		else if (meta == 7)
			MathHelper.getRandomIntegerInRange(rand, 1, 4)
		else
			super.getExpDrop(world, meta, fortune)
	}
	
	override fun isSideSolid(world: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) = true
	
	override fun renderAsNormalBlock() = false
	
	override fun getRenderType() = ThaumcraftAlfheimModule.renderIDOre
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.ores
	
	companion object {
		
		lateinit var icon: Array<IIcon>
	}
}