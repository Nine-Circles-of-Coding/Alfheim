package alfheim.client.render.entity

import alexsocol.asjlib.D
import alfheim.common.entity.*
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11.*
import java.util.*

object RenderFakeLightning: Render() {
	
	// not gonna prettify this mess
	override fun doRender(entity: Entity, x: Double, y: Double, z: Double, yaw: Float, ticks: Float) {
		entity as FakeLightning
		
		val tessellator = Tessellator.instance
		glDisable(GL_TEXTURE_2D)
		glDisable(GL_LIGHTING)
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE)
		val adouble = DoubleArray(8)
		val adouble1 = DoubleArray(8)
		var d3 = 0.0
		var d4 = 0.0
		val random = Random(entity.boltVertex)
		for (i in 7 downTo 0) {
			adouble[i] = d3
			adouble1[i] = d4
			d3 += (random.nextInt(11) - 5).D
			d4 += (random.nextInt(11) - 5).D
		}
		for (k1 in 0..3) {
			val random1 = Random(entity.boltVertex)
			for (j in 0..2) {
				var k = 7
				var l = 0
				if (j > 0) {
					k = 7 - j
				}
				if (j > 0) {
					l = k - 2
				}
				var d5 = adouble[k] - d3
				var d6 = adouble1[k] - d4
				for (i1 in k downTo l) {
					val d7 = d5
					val d8 = d6
					if (j == 0) {
						d5 += (random1.nextInt(11) - 5).D
						d6 += (random1.nextInt(11) - 5).D
					} else {
						d5 += (random1.nextInt(31) - 15).D
						d6 += (random1.nextInt(31) - 15).D
					}
					tessellator.startDrawing(5)
					val f2 = 0.5f
					tessellator.setColorRGBA_F(0.9f * f2, 0.9f * f2, 1.0f * f2, 0.3f)
					var d9 = 0.1 + k1.D * 0.2
					if (j == 0) {
						d9 *= i1.D * 0.1 + 1.0
					}
					var d10 = 0.1 + k1.D * 0.2
					if (j == 0) {
						d10 *= (i1 - 1).D * 0.1 + 1.0
					}
					for (j1 in 0..4) {
						var d11 = x + 0.5 - d9
						var d12 = z + 0.5 - d9
						if (j1 == 1 || j1 == 2) {
							d11 += d9 * 2.0
						}
						if (j1 == 2 || j1 == 3) {
							d12 += d9 * 2.0
						}
						var d13 = x + 0.5 - d10
						var d14 = z + 0.5 - d10
						if (j1 == 1 || j1 == 2) {
							d13 += d10 * 2.0
						}
						if (j1 == 2 || j1 == 3) {
							d14 += d10 * 2.0
						}
						tessellator.addVertex(d13 + d5, y + (i1 * 16).D, d14 + d6)
						tessellator.addVertex(d11 + d7, y + ((i1 + 1) * 16).D, d12 + d8)
					}
					tessellator.draw()
				}
			}
		}
		glDisable(GL_BLEND)
		glEnable(GL_LIGHTING)
		glEnable(GL_TEXTURE_2D)
	}
	
	override fun getEntityTexture(entity: Entity?) = null
}
