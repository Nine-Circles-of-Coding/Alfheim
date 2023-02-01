package alfheim.common.block.fluid

import alexsocol.asjlib.*
import alfheim.api.event.PlayerInteractAdequateEvent
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.init.Items
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.fluids.*
import vazkii.botania.common.Botania
import vazkii.botania.common.item.ModItems
import java.awt.Color
import java.util.*

class BlockManaFluid: BlockFluidClassic(ManaFluid, Material.water) {
	
	init {
		setBlockName("manaFluid")
		setHardness(2000.0f)
		setLightLevel(ManaFluid.luminosity / 15f)
		setLightOpacity(2)
		setQuantaPerBlock(13)
	}
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, name)
		return super.setBlockName(name)
	}
	
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int): IIcon? = if (side < 2) definedFluid.stillIcon else definedFluid.flowingIcon
	
	@SideOnly(Side.CLIENT)
	override fun randomDisplayTick(world: World?, x: Int, y: Int, z: Int, random: Random?) {
		if (!ASJUtilities.chance(20)) return
		val (r, g, b) = Color(0x00C6FF).getRGBColorComponents(null)
		Botania.proxy.wispFX(world, x + Math.random(), y + Math.random() * 0.25, z + Math.random(), r, g, b, Math.random().F / 3f, -Math.random().F / 25f, 2f)
	}
	
	companion object {
		
		init {
			eventForge()
		}
		
		@SubscribeEvent
		fun onPlayerInteract(e: PlayerInteractAdequateEvent.RightClick) {
			if (e.action != PlayerInteractAdequateEvent.RightClick.Action.RIGHT_CLICK_LIQUID) return
			
			val player = e.player
			val stack = player.heldItem ?: return
			if (stack.item !== Items.glass_bottle || stack.stackSize < 1) return
			
			val world = player.worldObj
			val block = world.getBlock(e.x, e.y, e.z) as? BlockFluidBase ?: return
			if (block.fluid.name != "mana") return
			if (world.getBlockMetadata(e.x, e.y, e.z) != 0) return
			
			world.setBlockToAir(e.x, e.y, e.z)
			
			val bottle = ItemStack(ModItems.manaBottle)
			stack.stackSize--
			if (stack.stackSize == 0)
				player.inventory.setInventorySlotContents(player.inventory.currentItem, bottle)
			else if (!player.inventory.addItemStackToInventory(bottle))
				player.dropPlayerItemWithRandomChoice(bottle, true)
			
			if (world.isRemote) player.swingItem()
		}
	}
	
	object ManaFluid: Fluid("mana") {
		
		init {
			setLuminosity(4)
			setDensity(600)
			setViscosity(600)
			setRarity(EnumRarity.uncommon)
			
			FluidRegistry.registerFluid(this)
		}
		
		override fun getFlowingIcon(): IIcon? = FluidRegistry.WATER.flowingIcon
		
		override fun getStillIcon(): IIcon? = FluidRegistry.WATER.stillIcon
	}
}