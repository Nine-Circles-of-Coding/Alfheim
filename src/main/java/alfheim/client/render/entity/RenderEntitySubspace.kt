package alfheim.client.render.entity

import alexsocol.asjlib.glScalef
import alfheim.api.lib.LibResourceLocations
import alfheim.common.entity.EntitySubspace
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11.*
import kotlin.math.*

/**
 * @author ExtraMeteorP, CKATEPTb
 */
object RenderEntitySubspace: Render() {
	
	val so = ShadedObjectHaloPlane(LibResourceLocations.subspace)
	
	override fun getEntityTexture(entity: Entity) = LibResourceLocations.subspace
	
	override fun doRender(weapon: Entity, par2: Double, par4: Double, par6: Double, par8: Float, par9: Float) {
		weapon as EntitySubspace
		
		glPushMatrix()
		glTranslated(par2, par4, par6)
		glRotatef(weapon.rotation, 0f, 1f, 0f)
		glRotatef(-90f, 1f, 0f, 0f)
		glScalef(if (weapon.ticksExisted < weapon.liveTicks) min(weapon.size, max(0f, (weapon.ticksExisted - weapon.delay) / 10f)) else max(0f, weapon.size - (weapon.ticksExisted - weapon.liveTicks) / 5f))
		
		so.addTranslation()
		
		glPopMatrix()
	}
}