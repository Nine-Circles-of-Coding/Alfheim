package alfheim.common.block

import alexsocol.asjlib.*
import alexsocol.patcher.event.EntityUpdateEvent
import alfheim.api.ModInfo
import alfheim.client.core.helper.IconHelper
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.*
import alfheim.common.core.handler.AlfheimConfigHandler.dimensionIDAlfheim
import alfheim.common.core.handler.AlfheimConfigHandler.dimensionIDNiflheim
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.EventResourcesMetas
import alfheim.common.world.dim.alfheim.customgens.NiflheimLocationGenerator
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.init.*
import net.minecraft.item.EnumRarity
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.*
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.event.entity.player.FillBucketEvent
import net.minecraftforge.fluids.*
import java.util.*

class BlockNiflheimPortal: BlockFluidClassic(niflheimPortalFluid, Material.water) {
	
	init {
		setBlockName("NiflheimPortal")
		setBlockUnbreakable()
		setCreativeTab(AlfheimTab)
	}
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, name)
		return super.setBlockName(name)
	}
	
	override fun onEntityCollidedWithBlock(world: World, x: Int, y: Int, z: Int, target: Entity) {
		if (world.isRemote) return
		
		val dimto: Int
		val posto: ChunkCoordinates
		
		if (world.provider.dimensionId == dimensionIDNiflheim) {
			dimto = dimensionIDAlfheim
			val (i, j, k) = onlyPortalPosition(world)
			posto = ChunkCoordinates(i, j - 8, k - 16)
		} else {
			dimto = dimensionIDNiflheim
			posto = MinecraftServer.getServer().worldServerForDimension(dimto).spawnPoint
		}
		
		val (i, j, k) = posto
		ASJUtilities.sendToDimensionWithoutPortal(target, dimto, i + 0.5, j.D, k + 0.5)
	}
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		super.updateTick(world, x, y, z, random)
		
		if (world.provider.dimensionId != dimensionIDNiflheim || !world.isAirBlock(x, y + 1, z)) return
		
		var foundSolid = false
		
		for (yo in (y + 2)..255) {
			val air = world.isAirBlock(x, yo, z)
			
			if (!foundSolid) {
				if (!air) foundSolid = true
				continue
			} else if (air)
				return VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.WISP, dimensionIDNiflheim, x + Math.random(), yo + Math.random() * 0.2 + 0.1, z + Math.random(), 0.025, 0.025, 0.05, 3.0, 0.0, 0.0, 0.0, 10.0)
		}
	}
	
	override fun randomDisplayTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		if (world.totalWorldTime < lastAudioTick) return
		lastAudioTick = world.totalWorldTime + 500
		
		world.playSound(x + 0.5, y + 0.5, z + 0.5, "${ModInfo.MODID}:niflportal", 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false)
	}
	
	override fun isReplaceable(world: IBlockAccess, x: Int, y: Int, z: Int) = false
	override fun canDrain(world: World?, x: Int, y: Int, z: Int) = false
	override fun drain(world: World?, x: Int, y: Int, z: Int, doDrain: Boolean) = null
	
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) {
		blockIcon = IconHelper.forBlock(reg, this, "Still")
		niflheimPortalFluid.setIcons(blockIcon, IconHelper.forBlock(reg, this, "Flowing"))
	}
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int) = if (side < 2) definedFluid.stillIcon else definedFluid.flowingIcon
	
	companion object {
		
		var lastAudioTick = 0L
		val niflheimPortalFluid = Fluid("NiflheimPortal").setDensity(10).setLuminosity(5).setTemperature(0).setViscosity(0).setGaseous(true).setRarity(EnumRarity.epic).apply { FluidRegistry.registerFluid(this) }
		
		init {
			eventForge().eventFML()
		}
		
		fun onlyPortalPosition(world: World): ChunkCoordinates {
			val (xOff, zOff) = NiflheimLocationGenerator.portalXZ(world)
			return ChunkCoordinates(xOff, 32, zOff)
		}
		
		@SubscribeEvent
		fun preventDraining(e: FillBucketEvent) {
			val flagDrain = e.current?.item === Items.bucket && e.world.getBlock(e.target.blockX, e.target.blockY, e.target.blockZ) == AlfheimBlocks.niflheimPortal
			val dir = ForgeDirection.getOrientation(e.target.sideHit)
			val flagReplace = e.world.getBlock(e.target.blockX + dir.offsetX, e.target.blockY + dir.offsetY, e.target.blockZ + dir.offsetZ) == AlfheimBlocks.niflheimPortal
			
			if (flagDrain || flagReplace) e.isCanceled = !e.entityPlayer.capabilities.isCreativeMode
		}
		
		@SubscribeEvent
		fun initPortalFromWaterCreation(e: EntityUpdateEvent) {
			val entity = e.entity as? EntityItem ?: return
			if (entity.dimension != dimensionIDAlfheim) return
			
			val world = entity.worldObj
			if (world.isRemote) return
			
			val stack = entity.entityItem ?: return
			if (stack.item !== AlfheimItems.eventResource || stack.meta != EventResourcesMetas.SnowRelic) return
			
			val (x, y, z) = onlyPortalPosition(world)
			if (world.getBlock(x, y, z) !== Blocks.water) return
			if (!entity.boundingBox().intersectsWith(getBoundingBox(x, y, z).offset(0.5).expand(0.5))) return
			
			world.setBlock(x, y, z, AlfheimBlocks.poisonIce, 1, 2)
			world.scheduleBlockUpdate(x, y, z, AlfheimBlocks.poisonIce, 1)
			
			BlockNiflheimIce.destroyNextTick = false
			
			stack.stackSize = 0
			entity.setEntityItemStack(null)
			entity.setDead()
		}
		
		@SubscribeEvent
		fun updateDestroyFrost(e: TickEvent.WorldTickEvent) {
//			if (e.world.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim) return
			if (e.side != Side.SERVER || e.phase != TickEvent.Phase.END) return
			BlockNiflheimIce.destroyNextTick = true
		}
	}
}
