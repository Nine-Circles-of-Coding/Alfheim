package alfheim.client.render.entity

import alexsocol.asjlib.glScalef
import alfheim.api.lib.LibResourceLocations
import alfheim.common.entity.EntityLightningMark
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11.*
import java.util.*
import kotlin.math.min

object RenderEntityLightningMark: Render() {
	
	val so = ShadedObjectHaloPlane(LibResourceLocations.markLightning)
	
	private val rand = Random()
	
	init {
		shadowSize = 0f
	}
	
	override fun getEntityTexture(entity: Entity) = null
	
	override fun doRender(entity: Entity, x: Double, y: Double, z: Double, yaw: Float, partialTick: Float) {
		val mark = entity as EntityLightningMark
		glPushMatrix()
		glTranslated(x, y + 0.01, z)
		
		rand.setSeed(mark.uniqueID.mostSignificantBits)
		
		val live = mark.ticksExisted / 2f
		val charge = min(10f, live + partialTick)
		var s = charge / 10f
		s += min(1f, (live + partialTick) * 0.2f)
		s /= 2f
		glScalef(s)
		
		glRotatef(charge * 9f + (mark.ticksExisted + partialTick) * 0.5f + rand.nextFloat() * 360f, 0f, 1f, 0f)
		
		so.addTranslation()
		
		glPopMatrix()
	}
}