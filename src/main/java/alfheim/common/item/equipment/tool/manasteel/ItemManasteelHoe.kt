package alfheim.common.item.equipment.tool.manasteel

import alexsocol.asjlib.*
import alexsocol.asjlib.security.InteractionSecurity
import alfheim.api.ModInfo
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.relic.ItemSifRing
import cpw.mods.fml.common.eventhandler.Event.Result
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.*
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.UseHoeEvent
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.mana.*
import vazkii.botania.common.Botania
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.equipment.tool.ToolCommons

open class ItemManasteelHoe @JvmOverloads constructor(mat: ToolMaterial = BotaniaAPI.manasteelToolMaterial, name: String = "ManasteelHoe"): ItemHoe(mat), IManaUsingItem {
	
	init {
		creativeTab = AlfheimTab
		setTextureName("${ModInfo.MODID}:$name")
		unlocalizedName = name
	}
	
	override fun setUnlocalizedName(name: String): Item {
		GameRegistry.registerItem(this, name)
		return super.setUnlocalizedName(name)
	}
	
	override fun hitEntity(par1ItemStack: ItemStack?, par2EntityLivingBase: EntityLivingBase?, par3EntityLivingBase: EntityLivingBase?): Boolean {
		ToolCommons.damageItem(par1ItemStack, 1, par3EntityLivingBase, MANA_PER_DAMAGE)
		return true
	}
	
	override fun onBlockDestroyed(stack: ItemStack?, world: World?, block: Block, x: Int, y: Int, z: Int, entity: EntityLivingBase?): Boolean {
		if (block.getBlockHardness(world, x, y, z) != 0f)
			ToolCommons.damageItem(stack, 1, entity, MANA_PER_DAMAGE)
		
		return true
	}
	
	override fun onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (world.getBlock(x, y, z) == Blocks.farmland) return false
		
		val extraRange = if (player.isSneaking) 0 else getExtraRange(stack, player)
		
		fun doUse(xOffset: Int, zOffset: Int): Boolean {
			if (!useHoe(stack, player, world, x + xOffset, y, z + zOffset, side))
				return false
			
			for (i in 0..2)
				Botania.proxy.sparkleFX(world, x + xOffset - 0.1 + Math.random() * 1.2, y - 0.1 + Math.random() * 1.2, z + zOffset - 0.1 + Math.random() * 1.2, 0.5f, 0.2f, 0f, 1f, 1)
			
			return true
		}
		
		if (!doUse(0, 0)) return false
		
		var did = false
		
		outer@ for (xOffset in 0.bidiRange(extraRange))
			for (zOffset in 0.bidiRange(extraRange)) {
				if (!ManaItemHandler.requestManaExactForTool(stack, player, MANA_PER_DAMAGE, false) && stack.getItemDamage() >= stack.maxDamage) break@outer
				
				did = doUse(xOffset, zOffset) || did
			}
		
		return did
	}
	
	open fun getExtraRange(stack: ItemStack, player: EntityPlayer) = if (!RagnarokHandler.blockedPowers[1] && ItemSifRing.getSifRing(player) != null) 1 else 0
	
	fun useHoe(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int): Boolean {
		if (!player.canPlayerEdit(x, y, z, side, stack)) return false
		if (InteractionSecurity.isInteractionBanned(player, x, y, z, world)) return false
		
		val event = UseHoeEvent(player, stack, world, x, y, z)
		if (MinecraftForge.EVENT_BUS.post(event))
			return false
		
		if (event.result == Result.ALLOW) {
			ToolCommons.damageItem(stack, 1, player, MANA_PER_DAMAGE)
			return true
		}
		
		val block = world.getBlock(x, y, z)
		
		if (!(side != 0 && world.isAirBlock(x, y + 1, z) && (block === Blocks.grass || block === Blocks.dirt || block === AlfheimBlocks.snowGrass)))
			return false
			
		val block1 = Blocks.farmland
		world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, block1.stepSound.stepResourcePath, (block1.stepSound.getVolume() + 1) * 0.5f, block1.stepSound.pitch * 0.8f)
		
		if (world.isRemote)
			return true
		
		world.setBlock(x, y, z, block1)
		ToolCommons.damageItem(stack, 1, player, MANA_PER_DAMAGE)
		return true
	}
	
	override fun onUpdate(stack: ItemStack?, world: World, player: Entity?, par4: Int, par5: Boolean) {
		if (!world.isRemote && player is EntityPlayer && stack!!.meta > 0 && ManaItemHandler.requestManaExactForTool(stack, (player as EntityPlayer?)!!, MANA_PER_DAMAGE * 2, true))
			stack.meta = stack.meta - 1
	}
	
	override fun getIsRepairable(stack: ItemStack?, material: ItemStack) =
		material.item === ModItems.manaResource && material.meta == 0
	
	override fun usesMana(stack: ItemStack): Boolean {
		return true
	}
	
	companion object {
		const val MANA_PER_DAMAGE = 60
	}
}