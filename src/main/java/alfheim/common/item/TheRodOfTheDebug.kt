package alfheim.common.item

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJShaderHelper
import alfheim.AlfheimCore
import alfheim.api.ModInfo
import alfheim.api.entity.*
import alfheim.api.event.PlayerInteractAdequateEvent
import alfheim.api.lib.LibShaderIDs
import alfheim.client.core.handler.CardinalSystemClient
import alfheim.common.core.handler.CardinalSystem
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.integration.thaumcraft.ThaumcraftAlfheimModule
import cpw.mods.fml.common.*
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.*
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.World
import org.lwjgl.opengl.*
import vazkii.botania.common.Botania
import vazkii.botania.common.core.helper.ItemNBTHelper

class TheRodOfTheDebug: ItemMod("TheRodOfTheDebug") {
	
	init {
		maxStackSize = 1
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (!ModInfo.DEV) return stack
		
		try {
			if (!player.isSneaking) {
				if (!world.isRemote) {
				
				} else {
				
				}
			} else {
				player.raceID = (player.race.ordinal + 1) % 11
				ASJUtilities.chatLog("${player.race.ordinal} - ${player.race}", player)
			}
		} catch (e: Throwable) {
			ASJUtilities.log("Oops!")
			e.printStackTrace()
		}
		
		return stack
	}
	
	override fun onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (!ModInfo.DEV) return false
		
		try {
			val te = world.getTileEntity(x, y, z)
			if (te != null) {
				val nbt = NBTTagCompound()
				te.writeToNBT(nbt)
				for (s in ASJUtilities.toString(nbt).split("\n")) ASJUtilities.chatLog(s, world)
				
				//				if (te instanceof TileAnomaly) ((TileAnomaly) te).addSubTile("Lightning");
				
				//				if (te instanceof TileNode) ((TileNode) te).setAspects(new AspectList().add(Aspect.WATER, 500).add(Aspect.AIR, 500).add(Aspect.FIRE, 500).add(Aspect.EARTH, 500).add(Aspect.ORDER, 500).add(Aspect.ENTROPY, 500));
			}
		} catch (e: Throwable) {
			ASJUtilities.log("Oops!")
			e.printStackTrace()
		}
		
		return false
	}
	
	companion object {
		
		init {
			eventForge()
		}
		
		@Mod.EventHandler
		fun onClickedEntity(e: PlayerInteractAdequateEvent.RightClick) {
			if (e.action != PlayerInteractAdequateEvent.RightClick.Action.RIGHT_CLICK_ENTITY) return
			val stack = e.player.heldItem ?: return
			if (stack.item !== AlfheimItems.`DEV-NULL`) return
			
			val source = DamageSource.causePlayerDamage(e.player)
			val name = stack.displayName
			
			if ("fire" in name) source.setFireDamage()
			if ("magic" in name) source.setMagicDamage()
			if ("expl" in name) source.setExplosion()
			if ("proj" in name) source.setProjectile()
			if ("pierce" in name) source.setDamageBypassesArmor()
			if ("potion" in name) source.setDamageIsAbsolute()
			if ("creative" in name) source.setDamageAllowedInCreativeMode()
			
			val dmg = stack.meta.F
			
			e.entity?.attackEntityFrom(source, dmg)
		}
		
		fun royalStaff(player: EntityPlayer) {
			if (!Botania.thaumcraftLoaded) return
			
			val stk = ItemStack(GameRegistry.findItem("Thaumcraft", "WandCasting"), 1, 148)
			ItemNBTHelper.setBoolean(stk, "sceptre", true)
			ItemNBTHelper.setString(stk, "cap", ThaumcraftAlfheimModule.capMauftriumName)
			ItemNBTHelper.setString(stk, "rod", "primal_staff")
			ItemNBTHelper.setInt(stk, "aer", 37500)
			ItemNBTHelper.setInt(stk, "terra", 37500)
			ItemNBTHelper.setInt(stk, "ignis", 37500)
			ItemNBTHelper.setInt(stk, "aqua", 37500)
			ItemNBTHelper.setInt(stk, "ordo", 37500)
			ItemNBTHelper.setInt(stk, "perditio", 37500)
			val focus = NBTTagCompound()
			focus.setShort("id", 4109.toShort())
			focus.setShort("Damage", 0.toShort())
			focus.setBoolean("Count", true)
			stk.stackTagCompound.setTag("focus", focus)
			player.inventory.addItemStackToInventory(stk)
		}
	}
}