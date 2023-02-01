package alfheim.client.render.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.render.*
import alfheim.api.lib.LibResourceLocations
import alfheim.common.block.*
import alfheim.common.block.tile.TileAlfheimPylon
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.*
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL
import vazkii.botania.client.core.handler.*
import vazkii.botania.client.core.helper.ShaderHelper
import vazkii.botania.client.model.*
import vazkii.botania.common.core.handler.ConfigHandler
import java.util.*
import kotlin.math.*

object RenderTileAlfheimPylons: TileEntitySpecialRenderer() {
	
	val model: IPylonModel = if (ConfigHandler.oldPylonModel) ModelPylonOld() else ModelPylon()
	var orange = false
	var red = false
	var creation = false
	var hand = false
	
	val rand = Random()
	
	val shObjRO = ShadedObjectPylon(LibResourceLocations.antiPylonOld)
	val shObjR = ShadedObjectPylon(LibResourceLocations.antiPylon)
	val shObjPO = ShadedObjectPylon(LibResourceLocations.elvenPylonOld)
	val shObjP = ShadedObjectPylon(LibResourceLocations.elvenPylon)
	val shObjOO = ShadedObjectPylon(LibResourceLocations.yordinPylonOld)
	val shObjO = ShadedObjectPylon(LibResourceLocations.yordinPylon)
	
	init {
		RenderPostShaders.registerShadedObject(shObjO)
		RenderPostShaders.registerShadedObject(shObjP)
		RenderPostShaders.registerShadedObject(shObjR)
		RenderPostShaders.registerShadedObject(shObjOO)
		RenderPostShaders.registerShadedObject(shObjPO)
		RenderPostShaders.registerShadedObject(shObjRO)
	}
	
	override fun renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, ticks: Float) {
		tile as TileAlfheimPylon
		
		glPushMatrix()
		glEnable(GL_RESCALE_NORMAL)
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		
		val a = if (MultiblockRenderHandler.rendering) 0.6f else 1f
		glColor4f(1f, 1f, 1f, a)
		if (tile.worldObj != null) {
			orange = tile.getBlockMetadata() == 1
			red = tile.getBlockMetadata() == 2
			creation = tile.getBlockMetadata() == 3
		}
		
		if (ConfigHandler.oldPylonModel) {
			if (creation)
				(LibResourceLocations.creationPylonOld as ResourceLocationAnimated).bind()
			else
				mc.renderEngine.bindTexture(if (red) LibResourceLocations.antiPylonOld else if (orange) LibResourceLocations.yordinPylonOld else LibResourceLocations.elvenPylonOld)
		} else {
			if (creation)
				(LibResourceLocations.creationPylon as ResourceLocationAnimated).bind()
			else
				mc.renderEngine.bindTexture(if (red) LibResourceLocations.antiPylon else if (orange) LibResourceLocations.yordinPylon else LibResourceLocations.elvenPylon)
		}
		
		var worldTime = if (tile.worldObj == null) 0.0 else (ClientTickHandler.ticksInGame + ticks).D
		
		rand.setSeed((tile.xCoord xor tile.yCoord xor tile.zCoord).toLong())
		worldTime += rand.nextInt(360).D
		
		if (ConfigHandler.oldPylonModel) {
			glTranslated(x + 0.5, y + 2.2, z + 0.5)
			glScalef(1f, -1.5f, -1f)
		} else {
			glTranslated(x + 0.2 + if (orange) -0.1 else 0.0, y + 0.05, z + 0.8 + if (orange) 0.1 else 0.0)
			val scale = if (orange) 0.8f else 0.6f
			glScalef(scale, 0.6f, scale)
		}
		
		if (!orange) {
			glPushMatrix()
			if (!ConfigHandler.oldPylonModel)
				glTranslatef(0.5f, 0f, -0.5f)
			glRotatef(worldTime.F * 1.5f, 0f, 1f, 0f)
			if (!ConfigHandler.oldPylonModel)
				glTranslatef(-0.5f, 0f, 0.5f)
			
			model.renderRing()
			glTranslated(0.0, sin(worldTime / 20.0) / 20 - 0.025, 0.0)
			model.renderGems()
			glPopMatrix()
		}
		
		glPushMatrix()
		glTranslated(0.0, sin(worldTime / 20.0) / 17.5, 0.0)
		
		if (!ConfigHandler.oldPylonModel)
			glTranslatef(0.5f, 0f, -0.5f)
		
		glRotatef((-worldTime).F, 0f, 1f, 0f)
		if (!ConfigHandler.oldPylonModel)
			glTranslatef(-0.5f, 0f, 0.5f)
		
		ASJRenderHelper.setTwoside()
		model.renderCrystal()
		
		glColor4f(1f, 1f, 1f, a)
		
		if (!ShaderHelper.useShaders() || hand) {
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f)
			val alpha = ((sin((ClientTickHandler.ticksInGame + ticks) / 20.0) / 2.0 + 0.5) / if (ConfigHandler.oldPylonModel) 1.0 else 2.0).F
			glColor4f(1f, 1f, 1f, a * (alpha + 0.183f))
		}
		
		glDisable(GL_ALPHA_TEST)
		glScaled(1.1)
		if (!ConfigHandler.oldPylonModel)
			glTranslatef(-0.05f, -0.1f, 0.05f)
		else
			glTranslatef(0f, -0.09f, 0f)
		
		if (!RenderPostShaders.allowShaders) {
			ASJRenderHelper.setGlow()
			val alpha = ((sin(worldTime / 20.0) / 2.0 + 0.5) / if (ConfigHandler.oldPylonModel) 1.0 else 2.0).F
			glColor4f(1f, 1f, 1f, a * (alpha + 0.183f))
		}
		
		if (!hand && RenderPostShaders.allowShaders && !creation) {
			val shObj = if (ConfigHandler.oldPylonModel) { if (red) shObjRO else if (orange) shObjOO else shObjPO } else { if (red) shObjR else if (orange) shObjO else shObjP }
			shObj.addTranslation()
		} else {
			if (creation) ShaderHelper.useShader(ShaderHelper.pylonGlow)
			model.renderCrystal()
			if (creation) ShaderHelper.releaseShader()
		}
		
		glEnable(GL_ALPHA_TEST)
		glPopMatrix()
		
		glDisable(GL_BLEND)
		glEnable(GL_RESCALE_NORMAL)
		glPopMatrix()
		
		ASJRenderHelper.discard()
		
		hand = false
		
		if (!creation || !tile.activated) return
		
		glPushMatrix()
		glTranslated(x, y, z)
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		glDisable(GL_ALPHA_TEST)
		glDisable(GL_LIGHTING)
		glDisable(GL_CULL_FACE)
		glColor4d(1.0, 1.0, 1.0, min(1.0, (sin(((ClientTickHandler.ticksInGame + ticks) / 8).D) + 1) / 7 + 0.6) * 0.5)
		
		mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture)
		
		glPushMatrix()
		glTranslatef(-1f, -1f, 0.25f)
		for ((off, icon) in arrayOf(-5.0 to BlockAlfheimPylon.bluePortalIcon, 9.5 to BlockAlfheimPylon.redPortalIcon)) {
			glTranslated(0.0, 0.0, off)
			RenderTileAlfheimPortal.renderIcon(0, 0, icon, 3, 3, 240)
			glTranslated(0.0, 0.0, 0.5)
			RenderTileAlfheimPortal.renderIcon(0, 0, icon, 3, 3, 240)
		}
		glPopMatrix()
		
		bindTexture(ResourceLocation("textures/entity/beacon_beam.png"))
		glPushMatrix()
		glTranslatef(-5f, -1f, 0f)
		renderBeam()
		glTranslatef(10f, 0f, 0f)
		renderBeam()
		glPopMatrix()
		
		glColor4d(1.0, 1.0, 1.0, 1.0)
		glEnable(GL_CULL_FACE)
		glEnable(GL_LIGHTING)
		glEnable(GL_ALPHA_TEST)
		glDisable(GL_BLEND)
		glPopMatrix()
	}
	
	fun renderBeam() {
		val tessellator = Tessellator.instance
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, 10497.0f)
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, 10497.0f)
		glDisable(GL_BLEND)
		glDepthMask(true)
		OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE, GL_ONE, GL_ZERO)
		val f2 = mc.theWorld.totalWorldTime + mc.timer.renderPartialTicks
		val f3 = -f2 * 0.2f - MathHelper.floor_float(-f2 * 0.1f).F
		val d3 = f2 * -0.0375
		tessellator.startDrawingQuads()
		tessellator.setColorRGBA(255, 255, 255, 32)
		val d5 = 0.2
		val d7 = 0.5 + cos(d3 + 2.356194490192345) * d5
		val d9 = 0.5 + sin(d3 + 2.356194490192345) * d5
		val d11 = 0.5 + cos(d3 + Math.PI / 4.0) * d5
		val d13 = 0.5 + sin(d3 + Math.PI / 4.0) * d5
		val d15 = 0.5 + cos(d3 + 3.9269908169872414) * d5
		val d17 = 0.5 + sin(d3 + 3.9269908169872414) * d5
		val d19 = 0.5 + cos(d3 + 5.497787143782138) * d5
		val d21 = 0.5 + sin(d3 + 5.497787143782138) * d5
		val d28 = f3 - 1.0
		val d26 = 256 + d28
		val d29 = 640 + d28
		tessellator.addVertexWithUV(d7, 256.0, d9, 1.0, d29)
		tessellator.addVertexWithUV(d7, 0.0, d9, 1.0, d28)
		tessellator.addVertexWithUV(d11, 0.0, d13, 0.0, d28)
		tessellator.addVertexWithUV(d11, 256.0, d13, 0.0, d29)
		tessellator.addVertexWithUV(d19, 256.0, d21, 1.0, d29)
		tessellator.addVertexWithUV(d19, 0.0, d21, 1.0, d28)
		tessellator.addVertexWithUV(d15, 0.0, d17, 0.0, d28)
		tessellator.addVertexWithUV(d15, 256.0, d17, 0.0, d29)
		tessellator.addVertexWithUV(d11, 256.0, d13, 1.0, d29)
		tessellator.addVertexWithUV(d11, 0.0, d13, 1.0, d28)
		tessellator.addVertexWithUV(d19, 0.0, d21, 0.0, d28)
		tessellator.addVertexWithUV(d19, 256.0, d21, 0.0, d29)
		tessellator.addVertexWithUV(d15, 256.0, d17, 1.0, d29)
		tessellator.addVertexWithUV(d15, 0.0, d17, 1.0, d28)
		tessellator.addVertexWithUV(d7, 0.0, d9, 0.0, d28)
		tessellator.addVertexWithUV(d7, 256.0, d9, 0.0, d29)
		tessellator.draw()
		glEnable(GL_BLEND)
		OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)
		glDepthMask(false)
		tessellator.startDrawingQuads()
		tessellator.setColorRGBA(255, 255, 255, 32)
		tessellator.addVertexWithUV(0.2, 256.0, 0.2, 1.0, d26)
		tessellator.addVertexWithUV(0.2, 0.0, 0.2, 1.0, d28)
		tessellator.addVertexWithUV(0.8, 0.0, 0.2, 0.0, d28)
		tessellator.addVertexWithUV(0.8, 256.0, 0.2, 0.0, d26)
		tessellator.addVertexWithUV(0.8, 256.0, 0.8, 1.0, d26)
		tessellator.addVertexWithUV(0.8, 0.0, 0.8, 1.0, d28)
		tessellator.addVertexWithUV(0.2, 0.0, 0.8, 0.0, d28)
		tessellator.addVertexWithUV(0.2, 256.0, 0.8, 0.0, d26)
		tessellator.addVertexWithUV(0.8, 256.0, 0.2, 1.0, d26)
		tessellator.addVertexWithUV(0.8, 0.0, 0.2, 1.0, d28)
		tessellator.addVertexWithUV(0.8, 0.0, 0.8, 0.0, d28)
		tessellator.addVertexWithUV(0.8, 256.0, 0.8, 0.0, d26)
		tessellator.addVertexWithUV(0.2, 256.0, 0.8, 1.0, d26)
		tessellator.addVertexWithUV(0.2, 0.0, 0.8, 1.0, d28)
		tessellator.addVertexWithUV(0.2, 0.0, 0.2, 0.0, d28)
		tessellator.addVertexWithUV(0.2, 256.0, 0.2, 0.0, d26)
		tessellator.draw()
		glEnable(GL_TEXTURE_2D)
		glDepthMask(true)
	}
	
	class ShadedObjectPylon(rl: ResourceLocation): ShadedObject(ShaderHelper.pylonGlow, matID, rl) {
		
		override fun preRender() {
			glEnable(GL_RESCALE_NORMAL)
			glEnable(GL_BLEND)
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
			val a = if (MultiblockRenderHandler.rendering) 0.6f else 1f
			glColor4f(1f, 1f, 1f, a)
			glDisable(GL_CULL_FACE)
			glDisable(GL_ALPHA_TEST)
		}
		
		override fun drawMesh(data: Array<out Any?>) {
			model.renderCrystal()
		}
		
		override fun postRender() {
			glEnable(GL_ALPHA_TEST)
			glEnable(GL_CULL_FACE)
			glDisable(GL_BLEND)
			glEnable(GL_RESCALE_NORMAL)
		}
		
		companion object {
			
			val matID = RenderPostShaders.nextAvailableRenderObjectMaterialID
		}
	}
}