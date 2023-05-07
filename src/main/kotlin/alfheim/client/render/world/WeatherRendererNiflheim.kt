package alfheim.client.render.world

import alexsocol.asjlib.*
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.renderer.*
import net.minecraft.util.MathHelper
import net.minecraftforge.client.IRenderHandler
import org.lwjgl.opengl.GL11.*
import kotlin.math.sqrt

object WeatherRendererNiflheim: IRenderHandler() {
	
	override fun render(partialTicks: Float, world: WorldClient, mc: Minecraft) {
		mc.entityRenderer.enableLightmap(partialTicks.D)
		
		// the fuck is that ???
		if (mc.entityRenderer.rainXCoords == null) {
			mc.entityRenderer.rainXCoords = FloatArray(1024)
			mc.entityRenderer.rainYCoords = FloatArray(1024)
			
			for (i in 0..31) {
				for (j in 0..31) {
					val f2 = (j - 16).F
					val f3 = (i - 16).F
					val f4 = sqrt(f2 * f2 + f3 * f3)
					mc.entityRenderer.rainXCoords[i shl 5 or j] = -f3 / f4
					mc.entityRenderer.rainYCoords[i shl 5 or j] = f2 / f4
				}
			}
		}
		
		val entity = mc.renderViewEntity
		val k2 = entity.posX.mfloor()
		val l2 = entity.posY.mfloor()
		val i3 = entity.posZ.mfloor()
		val tes = Tessellator.instance
		glDisable(GL_CULL_FACE)
		glNormal3f(0f, 1f, 0f)
		glEnable(GL_BLEND)
		OpenGlHelper.glBlendFunc(770, 771, 1, 0)
		glAlphaFunc(GL_GREATER, 0.1f)
		val d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks.D
		val d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks.D
		val d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks.D
		val k = MathHelper.floor_double(d1)
		val b0 = if (mc.gameSettings.fancyGraphics) 10 else 5
		var b1 = -1
		val f5 = (mc.entityRenderer.rendererUpdateCount.F * 8f) + partialTicks
		
		glColor4f(1f, 1f, 1f, 1f)
		
		for (l in i3 - b0..i3 + b0) {
			for (i1 in k2 - b0..k2 + b0) {
				val j1 = (l - i3 + 16) * 32 + i1 - k2 + 16
				val f6 = mc.entityRenderer.rainXCoords[j1] * 0.5f
				val f7 = mc.entityRenderer.rainYCoords[j1] * 0.5f
				val k1 = world.getPrecipitationHeight(i1, l)
				var l1 = l2 - b0
				var i2 = l2 + b0
				if (l1 < k1)
					l1 = k1
				
				if (i2 < k1)
					i2 = k1
				
				val f8 = 1f
				var j2 = k1
				if (k1 < k)
					j2 = k
				
				if (l1 == i2) continue
				
				mc.entityRenderer.random.setSeed((i1 * i1 * 3121 + i1 * 45238971 xor l * l * 418711 + l * 13761).toLong())
				
				if (b1 != 1) {
					if (b1 >= 0)
						tes.draw()
					
					b1 = 1
					mc.textureManager.bindTexture(EntityRenderer.locationSnowPng)
					tes.startDrawingQuads()
				}
				
				val f10 = ((mc.entityRenderer.rendererUpdateCount and 511).F + partialTicks) / 64f
				val f16 = mc.entityRenderer.random.nextFloat() + f5 * 0.01f * mc.entityRenderer.random.nextGaussian().F
				val f11 = mc.entityRenderer.random.nextFloat() + f5 * mc.entityRenderer.random.nextGaussian().F * 0.001f
				val d4 = (i1.F + 0.5f).D - entity.posX
				val d5 = (l.F + 0.5f).D - entity.posZ
				val f14 = sqrt(d4 * d4 + d5 * d5).F / b0.F
				val f15 = 1f
				tes.setBrightness((world.getLightBrightnessForSkyBlocks(i1, j2, l, 0) * 3 + 15728880) / 4)
				tes.setColorRGBA_F(f15, f15, f15, (1f - f14 * f14) * 0.3f + 0.5f)
				tes.setTranslation(-d0 * 1.0, -d1 * 1.0, -d2 * 1.0)
				tes.addVertexWithUV((i1.F - f6).D + 0.5, l1.D, (l.F - f7).D + 0.5, (0f * f8 + f16).D, (l1.F * f8 / 4f + f10 * f8 + f11).D)
				tes.addVertexWithUV((i1.F + f6).D + 0.5, l1.D, (l.F + f7).D + 0.5, (1f * f8 + f16).D, (l1.F * f8 / 4f + f10 * f8 + f11).D)
				tes.addVertexWithUV((i1.F + f6).D + 0.5, i2.D, (l.F + f7).D + 0.5, (1f * f8 + f16).D, (i2.F * f8 / 4f + f10 * f8 + f11).D)
				tes.addVertexWithUV((i1.F - f6).D + 0.5, i2.D, (l.F - f7).D + 0.5, (0f * f8 + f16).D, (i2.F * f8 / 4f + f10 * f8 + f11).D)
				tes.setTranslation(0.0, 0.0, 0.0)
			}
		}
		
		if (b1 >= 0)
			tes.draw()
		
		glEnable(GL_CULL_FACE)
		glDisable(GL_BLEND)
		glAlphaFunc(GL_GREATER, 0.1f)
		mc.entityRenderer.disableLightmap(partialTicks.D)
	}
}