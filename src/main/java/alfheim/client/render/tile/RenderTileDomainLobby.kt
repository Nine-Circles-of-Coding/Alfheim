package alfheim.client.render.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.AlfheimAPI
import alfheim.api.lib.LibResourceLocations
import alfheim.common.block.tile.TileDomainLobby
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.equipment.bauble.faith.ItemRagnarokEmblem
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import org.lwjgl.opengl.GL11.*

object RenderTileDomainLobby: TileEntitySpecialRenderer() {
	
	override fun renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, ticks: Float) {
		if (tile.worldObj == null || tile.worldObj.provider.dimensionId == AlfheimConfigHandler.dimensionIDDomains) return
		
		glPushMatrix()
		glTranslated(x, y, z)
		glDisable(GL_CULL_FACE)
		
		drawFirstConquerors(tile as TileDomainLobby)
		
		mc.renderEngine.bindTexture(LibResourceLocations.domainDoor)
		
		val tes = Tessellator.instance
		tes.setBrightness(tile.getBlockType().getMixedBrightnessForBlock(tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord))
		tes.startDrawingQuads()
		
		tes.addVertexWithUV( 3.0,  3.0, 0.249, 0.0, 0.0)
		tes.addVertexWithUV( 3.0, -2.0, 0.249, 0.0, 1.0)
		tes.addVertexWithUV(-2.0, -2.0, 0.249, 1.0, 1.0)
		tes.addVertexWithUV(-2.0,  3.0, 0.249, 1.0, 0.0)
		
		tes.addVertexWithUV( 3.0,  3.0, 0.751, 0.0, 0.0)
		tes.addVertexWithUV( 3.0, -2.0, 0.751, 0.0, 1.0)
		tes.addVertexWithUV(-2.0, -2.0, 0.751, 1.0, 1.0)
		tes.addVertexWithUV(-2.0,  3.0, 0.751, 1.0, 0.0)
		
		tes.draw()
		
		glEnable(GL_CULL_FACE)
		glPopMatrix()
	}
	
	fun drawFirstConquerors(tile: TileDomainLobby) {
		val domain = AlfheimAPI.domains[tile.name] ?: return
		val list = domain.firstConquerors.toMutableList()
		val know = ItemRagnarokEmblem.canSeeTruth(mc.thePlayer) || mc.thePlayer.commandSenderName in list
		if (list.isEmpty()) return
		
		if (!know) {
			list.clear()
			list.addAll(domain.firstConquerorsUnknown)
		}
		
		list.add(0, EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal("alfheimmisc.ragnarok.conquerors" + if (know) "" else ".unknown"))
		
		glPushMatrix()
		ASJRenderHelper.setGlow()
		glTranslatef(0.5f, 1.25f, 0f)
		glScalef(-0.02f, -0.02f, 0.02f)
		
		val font = mc.fontRenderer
		for (i in list.indices.reversed()) {
			val name = list[i]
			val x = font.getStringWidth(name) / 2f
			val y = font.FONT_HEIGHT
			glTranslatef(-x, 0f, 0f)
			font.drawString(name, 0, 0, 0xFFD400)
			glTranslatef(x, y * -1.5f, 0f)
		}
		
		glColor4f(1f, 1f, 1f, 1f)
		ASJRenderHelper.discard()
		glPopMatrix()
	}
}
