package alfheim.client.render.entity

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.api.entity.raceID
import alfheim.api.lib.LibResourceLocations
import alfheim.client.core.handler.CardinalSystemClient
import alfheim.common.core.handler.AlfheimConfigHandler
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.entity.RenderBiped
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.AdvancedModelLoader
import org.lwjgl.opengl.GL11.*
import vazkii.botania.api.item.IBaubleRender

object RenderBooba {
	
	val model = if (AlfheimConfigHandler.minimalGraphics) null else AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/booba.obj"))
	
	@SideOnly(Side.CLIENT)
	fun render(player: EntityPlayer) {
		if (!AlfheimConfigHandler.enableElvenStory) return
		if (CardinalSystemClient.playerSkinsData[player.commandSenderName]?.first != true) return
		
		val booba = model ?: return
		
		glPushMatrix()
		glScaled(0.0625)
		glRotatef(180f, 0f, 1f, 0f)
		glTranslatef(0f, 2.7f, 1.9f)
		glRotatef(180f, 0f, 0f, 1f)
		
		if (player.isSneaking) {
			IBaubleRender.Helper.applySneakingRotation()
			glTranslatef(0f, -1f, -0.5f)
		}
		
		mc.renderEngine.bindTexture(LibResourceLocations.oldFemale[player.raceID - 1])
		booba.renderAll()
		
		player.inventory.armorInventory[2]?.let {
			mc.renderEngine.bindTexture(RenderBiped.getArmorResource(player, it, 1, null))
			glScaled(1.1)
			booba.renderAll()
		}
		
		glPopMatrix()
	}
}
