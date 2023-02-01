package alfheim.common.block

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.INiflheimEntity
import alfheim.common.block.base.BlockMod
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.equipment.bauble.ItemPendant
import alfheim.common.lexicon.AlfheimLexiconData
import alfheim.common.potion.PotionEternity
import cpw.mods.fml.relauncher.*
import net.minecraft.block.material.Material
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.potion.Potion
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.common.core.handler.ConfigHandler
import java.util.*

class BlockNiflheimIce: BlockMod(Material.packedIce), ILexiconable {
	
	init {
		setBlockName("NiflheimIce")
		setCreativeTab(null)
		setBlockUnbreakable()
		setHarvestLevel("pick", 2)
		setLightOpacity(0)
		setStepSound(soundTypeGlass)
		tickRandomly = true
		slipperiness = 0.98f
	}
	
	override fun getCollisionBoundingBoxFromPool(world: World?, x: Int, y: Int, z: Int): AxisAlignedBB {
		return super.getCollisionBoundingBoxFromPool(world, x, y, z).expand(-0.01)
	}
	
	override fun getPlayerRelativeBlockHardness(player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Float {
		val metadata = world.getBlockMetadata(x, y, z)
		var hardness = getBlockHardness(world, x, y, z)
		
		if (ItemPendant.canProtect(player, ItemPendant.Companion.EnumPrimalWorldType.NIFLHEIM, 5)) hardness = 2f
		
		if (hardness < 0f) return 0f
		
		return if (!ForgeHooks.canHarvestBlock(this, player, metadata))
			player.getBreakSpeed(this, true, metadata, x, y, z) / hardness / 100f
		else
			player.getBreakSpeed(this, false, metadata, x, y, z) / hardness / 30f
	}
	
	override fun isOpaqueCube() = false
	
	@SideOnly(Side.CLIENT)
	override fun getRenderBlockPass() = 1
	
	@SideOnly(Side.CLIENT)
	override fun shouldSideBeRendered(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int) =
		world.getBlock(x, y, z) !== this && !world.getBlock(x, y, z).isOpaqueCube
	
	override fun quantityDropped(r: Random?) = 0
	
	override fun dropBlockAsItem(w: World, x: Int, y: Int, z: Int, s: ItemStack) = Unit
	
	override fun onEntityWalking(w: World, x: Int, y: Int, z: Int, e: Entity) {
		if (e is INiflheimEntity) return
		if (e is EntityPlayer && ItemPendant.canProtect(e, ItemPendant.Companion.EnumPrimalWorldType.NIFLHEIM, 50)) return
		
		e.setInWeb()
		if (w.isRemote || e !is EntityLivingBase) return
		
		e.addPotionEffect(PotionEffectU(Potion.moveSlowdown.id, 25, 2))
		if (!e.isPotionActive(AlfheimConfigHandler.potionIDEternity))
			e.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDEternity, 100, PotionEternity.ATTACK))
	}
	
	override fun onEntityCollidedWithBlock(w: World, x: Int, y: Int, z: Int, e: Entity) {
		onEntityWalking(w, x, y, z, e)
	}
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, rand: Random) {
		if (world.getBlockMetadata(x, y, z) == 1) replaceNearestWater(world, x, y, z)
		
		if (world.provider.dimensionId == AlfheimConfigHandler.dimensionIDNiflheim) return
		if (!world.gameRules.getGameRuleBooleanValue("doFireTick")) return
		if (world.getBlockMetadata(x, y, z) == 2) return
		
		val below = world.getBlock(x, y - 1, z)
		if ((below != Blocks.packed_ice || below != this) && world.rand.nextInt(100) == 0)
			world.setBlockToAir(x, y, z)
	}
	
	fun replaceNearestWater(world: World, x: Int, y: Int, z: Int) {
		for (d in ForgeDirection.VALID_DIRECTIONS) {
			val (i, j, k) = Vector3(x, y, z).add(d.offsetX, d.offsetY, d.offsetZ).I
			if (world.getBlock(i, j, k) !== Blocks.water) continue
			
			world.setBlock(i, j, k, this, 1, 2)
			world.scheduleBlockUpdate(i, j, k, this, 1)
			destroyNextTick = false
		}
		
		if (destroyNextTick) {
			if (ConfigHandler.blockBreakParticles) world.playAuxSFX(2001, x, y, z, getIdFromBlock(this) + (0 shl 12))
			
			if (ChunkCoordinates(x, y, z) != BlockNiflheimPortal.onlyPortalPosition(world))
				world.setBlockToAir(x, y, z)
			else
				world.setBlock(x, y, z, AlfheimBlocks.niflheimPortal)
			
			return
		}
		
		world.scheduleBlockUpdate(x, y, z, this, 5)
	}
	
	override fun tickRate(world: World?) = 1
	
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = AlfheimLexiconData.ruling
	
	companion object {
		var destroyNextTick = true
	}
}