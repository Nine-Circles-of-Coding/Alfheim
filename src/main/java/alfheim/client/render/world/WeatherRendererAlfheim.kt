package alfheim.client.render.world

import alexsocol.asjlib.*
import alfheim.api.lib.LibResourceLocations
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler.blizzards
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.EntityRenderer.*
import net.minecraft.util.MathHelper
import net.minecraftforge.client.IRenderHandler
import org.lwjgl.opengl.GL11.*
import kotlin.math.max

object WeatherRendererAlfheim: IRenderHandler() {
	
	override fun render(partialTicks: Float, world: WorldClient, mc: Minecraft) {
		val f1 = mc.theWorld.getRainStrength(partialTicks)
		if (f1 <= 0f) return
//		if (!RagnarokHandler.summer && f1 <= 0f) return
		
		mc.entityRenderer.enableLightmap(partialTicks.D)
		
		if (mc.entityRenderer.rainXCoords == null) {
			mc.entityRenderer.rainXCoords = FloatArray(1024)
			mc.entityRenderer.rainYCoords = FloatArray(1024)
			for (i in 0..31) {
				for (j in 0..31) {
					val f2 = j - 16f
					val f3 = i - 16f
					val f4 = MathHelper.sqrt_float(f2 * f2 + f3 * f3)
					mc.entityRenderer.rainXCoords[i shl 5 or j] = -f3 / f4
					mc.entityRenderer.rainYCoords[i shl 5 or j] = f2 / f4
				}
			}
		}
		
		val entitylivingbase = mc.renderViewEntity
		val worldclient = mc.theWorld
		val k2 = MathHelper.floor_double(entitylivingbase.posX)
		val l2 = MathHelper.floor_double(entitylivingbase.posY)
		val i3 = MathHelper.floor_double(entitylivingbase.posZ)
		val tes = Tessellator.instance
		glDisable(GL_CULL_FACE)
		glNormal3f(0f, 1f, 0f)
		glEnable(GL_BLEND)
		OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)
		glAlphaFunc(GL_GREATER, 0.1f)
		val d0 = entitylivingbase.lastTickPosX + (entitylivingbase.posX - entitylivingbase.lastTickPosX) * partialTicks
		val d1 = entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * partialTicks
		val d2 = entitylivingbase.lastTickPosZ + (entitylivingbase.posZ - entitylivingbase.lastTickPosZ) * partialTicks
		val k = d1.mfloor()
		val b0 = if (mc.gameSettings.fancyGraphics) 10 else 5
		var b1 = -1
		val f5 = mc.entityRenderer.rendererUpdateCount.F + partialTicks
		
		glColor4f(1f, 1f, 1f, 1f)
		
		for (l in i3 - b0..i3 + b0) {
			for (i1 in k2 - b0..k2 + b0) {
				val j1 = (l - i3 + 16) * 32 + i1 - k2 + 16
				val f6 = mc.entityRenderer.rainXCoords[j1] * 0.5f
				val f7 = mc.entityRenderer.rainYCoords[j1] * 0.5f
				val biomegenbase = worldclient.getBiomeGenForCoords(i1, l)
				
				val inBlizzard = blizzards.any { it.contains(i1, l) }
				val inSandstorm = inBlizzard && RagnarokHandler.summer
				
				if (!biomegenbase.canSpawnLightningBolt() && !biomegenbase.enableSnow) continue
				
				val k1 = worldclient.getPrecipitationHeight(i1, l)
				val l1 = max(k1, l2 - b0)
				val i2 = max(k1, l2 + b0)
				val f8 = 1.0
				val j2 = max(k1, k)
				
				if (l1 == i2) continue
				
				mc.entityRenderer.random.setSeed((i1 * i1 * 3121 + i1 * 45238971 xor l * l * 418711 + l * 13761).toLong())
				val f9 = biomegenbase.getFloatTemperature(i1, l1, l)
				var f10: Float
				var d4: Double
				
				if (worldclient.worldChunkManager.getTemperatureAtHeight(f9, k1) >= 0.15f && !inSandstorm) {
					if (b1 != 0) {
						if (b1 >= 0) tes.draw()
						
						b1 = 0
						mc.textureManager.bindTexture(locationRainPng)
						tes.startDrawingQuads()
					}
					
					f10 = ((mc.entityRenderer.rendererUpdateCount + i1 * i1 * 3121 + i1 * 45238971 + l * l * 418711 + l * 13761 and 31).F + partialTicks) / 32f * (3f + mc.entityRenderer.random.nextFloat())
					val d3 = i1 + 0.5 - entitylivingbase.posX
					d4 = l + 0.5 - entitylivingbase.posZ
					val f12 = MathHelper.sqrt_double(d3 * d3 + d4 * d4) / b0.F
					val f13 = 1f
					tes.setBrightness(worldclient.getLightBrightnessForSkyBlocks(i1, j2, l, 0))
					tes.setColorRGBA_F(f13, f13, f13, ((1f - f12 * f12) * 0.5f + 0.5f) * f1)
					tes.setTranslation(-d0 * 1.0, -d1 * 1.0, -d2 * 1.0)
					tes.addVertexWithUV(i1 - f6 + 0.5, l1.D, l - f7 + 0.5, 0.0, l1 * f8 / 4 + f10 * f8)
					tes.addVertexWithUV(i1 + f6 + 0.5, l1.D, l + f7 + 0.5, f8 , l1 * f8 / 4 + f10 * f8)
					tes.addVertexWithUV(i1 + f6 + 0.5, i2.D, l + f7 + 0.5, f8 , i2 * f8 / 4 + f10 * f8)
					tes.addVertexWithUV(i1 - f6 + 0.5, i2.D, l - f7 + 0.5, 0.0, i2 * f8 / 4 + f10 * f8)
					tes.setTranslation(0.0, 0.0, 0.0)
					
					continue
				}
				
				if (b1 != 1) {
					if (b1 >= 0) tes.draw()
					
					b1 = 1
					mc.textureManager.bindTexture(if (inSandstorm) LibResourceLocations.sandstormWeather else locationSnowPng)
					tes.startDrawingQuads()
				}
				
				f10 = ((mc.entityRenderer.rendererUpdateCount and 511).F + partialTicks) / if (inBlizzard) 64f else 512f
				val f16: Float = mc.entityRenderer.random.nextFloat() + f5 * 0.01f * mc.entityRenderer.random.nextGaussian().F
				val f11: Float = mc.entityRenderer.random.nextFloat() + f5 * mc.entityRenderer.random.nextGaussian().F * 0.001f
				d4 = i1 + 0.5 - entitylivingbase.posX
				val d5 = l + 0.5 - entitylivingbase.posZ
				val f14 = MathHelper.sqrt_double(d4 * d4 + d5 * d5) / b0.F
				val f15 = 1f
				tes.setBrightness((worldclient.getLightBrightnessForSkyBlocks(i1, j2, l, 0) * 3 + 15728880) / 4)
				tes.setColorRGBA_F(f15, f15, f15, if (inSandstorm) 1f else ((1f - f14 * f14) * 0.3f + 0.5f) * f1)
				tes.setTranslation(-d0 * 1.0, -d1 * 1.0, -d2 * 1.0)
				tes.addVertexWithUV(i1 - f6 + 0.5, l1.D, l - f7 + 0.5, f16.D   , l1 * f8 / 4 + f10 * f8 + f11)
				tes.addVertexWithUV(i1 + f6 + 0.5, l1.D, l + f7 + 0.5, f8 + f16, l1 * f8 / 4 + f10 * f8 + f11)
				tes.addVertexWithUV(i1 + f6 + 0.5, i2.D, l + f7 + 0.5, f8 + f16, i2 * f8 / 4 + f10 * f8 + f11)
				tes.addVertexWithUV(i1 - f6 + 0.5, i2.D, l - f7 + 0.5, f16.D   , i2 * f8 / 4 + f10 * f8 + f11)
				tes.setTranslation(0.0, 0.0, 0.0)
			}
		}
		
		if (b1 >= 0) tes.draw()
		
		glEnable(GL_CULL_FACE)
		glDisable(GL_BLEND)
		glAlphaFunc(GL_GREATER, 0.1f)
		mc.entityRenderer.disableLightmap(partialTicks.D)
	}
}