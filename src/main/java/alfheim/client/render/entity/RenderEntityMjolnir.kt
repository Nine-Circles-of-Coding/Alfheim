package alfheim.client.render.entity

import alexsocol.asjlib.F
import alexsocol.asjlib.math.Vector3
import alfheim.common.entity.EntityMjolnir
import net.minecraft.client.renderer.entity.*
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12
import vazkii.botania.common.Botania
import kotlin.math.*
import vazkii.botania.common.core.helper.Vector3 as Bector3

object RenderEntityMjolnir: Render() {
	
	override fun doRender(entity: Entity, x: Double, y: Double, z: Double, yawOld: Float, ticks: Float) {
		entity as EntityMjolnir
		
		val pink = entity.stack.displayName.trim().lowercase().let { it == "gloryhammer" || it == "glory hammer" }
		Botania.proxy.lightningFX(entity.worldObj, Bector3.fromEntity(entity), Bector3.fromEntity(entity).sub(Bector3(entity.motionX, entity.motionY, entity.motionZ).multiply(1.25)), 1f, if (pink) EntityMjolnir.colorP else EntityMjolnir.color, if (pink) EntityMjolnir.colorBP else EntityMjolnir.colorB)
		
		glPushMatrix()
		glTranslated(x, y, z)
		glEnable(GL12.GL_RESCALE_NORMAL)
		
		val v = Vector3(entity.motionX, entity.motionY, entity.motionZ).normalize()
		val (x1, _, z1) = v
		val (x2, _, z2) = Vector3.oZ
		
		val yaw = Math.toDegrees(-atan2(x1 * z2 - z1 * x2, x1 * x2 + z1 * z2)).F // Good DO NOT REMOVE
		val pitch = Math.toDegrees(atan(sqrt(v.x * v.x + v.z * v.z) / v.y)).F - 90f + if (v.y < 0) 180f else 0f // Working but maybe change...
		
		glRotatef(90f, 1f, 0f, 0f)
		glRotatef(yaw, 0f, 0f, 1f)
		glRotatef(pitch, 1f, 0f, 0f)
		
		glDisable(GL_CULL_FACE)
		RenderItem.renderInFrame = true
		RenderManager.instance.renderEntityWithPosYaw(EntityItem(entity.worldObj, 0.0, 0.0, 0.0, entity.stack).also { it.hoverStart = 0f }, 0.0, -0.2501, 0.0, 0f, 0f)
		RenderItem.renderInFrame = false
		glEnable(GL_CULL_FACE)
		
		glDisable(GL12.GL_RESCALE_NORMAL)
		glPopMatrix()
	}
	
	override fun getEntityTexture(entity: Entity): ResourceLocation {
		return TextureMap.locationItemsTexture
	}
}