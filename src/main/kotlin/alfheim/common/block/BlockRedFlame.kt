package alfheim.common.block

import alexsocol.asjlib.PotionEffectU
import alfheim.api.ModInfo
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.common.item.equipment.bauble.ItemPendant
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.block.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.api.lexicon.ILexiconable
import java.util.*

class BlockRedFlame: BlockFire(), ILexiconable {
	
	lateinit var icons: Array<IIcon>
	
	init {
		setBlockName("MuspelheimFire")
		setBlockUnbreakable()
		setCreativeTab(null)
		setLightLevel(1f)
		setLightOpacity(0)
	}
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
		return super.setBlockName(name)
	}
	
	override fun getPlayerRelativeBlockHardness(player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Float {
		val metadata = world.getBlockMetadata(x, y, z)
		var hardness = getBlockHardness(world, x, y, z)
		
		if (ItemPendant.canProtect(player, ItemPendant.Companion.EnumPrimalWorldType.MUSPELHEIM, 5)) hardness = 2f
		
		if (hardness < 0f) return 0f
		
		return if (!ForgeHooks.canHarvestBlock(this, player, metadata))
			player.getBreakSpeed(this, true, metadata, x, y, z) / hardness / 100f
		else
			player.getBreakSpeed(this, false, metadata, x, y, z) / hardness / 30f
	}
	
	override fun isCollidable() = false
	
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = arrayOf(reg.registerIcon(ModInfo.MODID + ":MuspelheimFire0"), reg.registerIcon(ModInfo.MODID + ":MuspelheimFire1"))
	}
	
	@SideOnly(Side.CLIENT)
	override fun getFireIcon(i: Int) = icons[i]
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(p_149691_1_: Int, p_149691_2_: Int) = icons[0]
	
	override fun onEntityCollidedWithBlock(world: World, x: Int, y: Int, z: Int, entity: Entity) {
		if (entity is EntityPlayer && ItemPendant.canProtect(entity, ItemPendant.Companion.EnumPrimalWorldType.MUSPELHEIM, 50)) return
		
		entity.setInWeb()
		
		if (entity !is EntityLivingBase)
			return
		
		val soulburn = PotionEffectU(AlfheimConfigHandler.potionIDSoulburn, 200)
		entity.addPotionEffect(soulburn)
	}
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, rand: Random) {
		if (!world.gameRules.getGameRuleBooleanValue("doFireTick")) return
		if (!canPlaceBlockAt(world, x, y, z) || (world.rand.nextInt(100) == 0 && !world.getBlock(x, y - 1, z).isFireSource(world, x, y - 1, z, ForgeDirection.UP)))
			world.setBlockToAir(x, y, z)
	}
	
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = AlfheimLexiconData.ruling
}
