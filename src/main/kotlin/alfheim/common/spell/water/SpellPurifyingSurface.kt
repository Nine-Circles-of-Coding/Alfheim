package alfheim.common.spell.water

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.EnumRace
import alfheim.api.lib.LibResourceLocations
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.CardinalSystem.PartySystem
import alfheim.common.core.handler.VisualEffectHandler
import net.minecraft.client.renderer.Tessellator
import net.minecraft.entity.EntityLivingBase
import net.minecraft.potion.Potion
import org.lwjgl.opengl.GL11.*

object SpellPurifyingSurface: SpellBase("purifyingsurface", EnumRace.UNDINE, 5000, 600, 20) {
	
	override var duration = 3600
	override var radius = 5.0
	
	override val usableParams
		get() = arrayOf(duration, radius)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		VisualEffectHandler.sendPacket(VisualEffects.PURE_AREA, caster)
		
		val list = getEntitiesWithinAABB(caster.worldObj, EntityLivingBase::class.java, caster.boundingBox.expand(radius))
		list.forEach {
			if (!PartySystem.mobsSameParty(caster, it) || Vector3.entityDistancePlane(it, caster) > radius) return@forEach
			
			it.extinguish()
			it.removePotionEffect(Potion.poison.id)
			it.addPotionEffect(PotionEffectU(Potion.fireResistance.id, duration))
			
			VisualEffectHandler.sendPacket(VisualEffects.PURE, it)
		}
		return result
	}
	
	override fun render(caster: EntityLivingBase) {
		glDisable(GL_CULL_FACE)
		glAlphaFunc(GL_GREATER, 1 / 255f)
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		glTranslated(0.0, -1.61, 0.0)
		mc.renderEngine.bindTexture(LibResourceLocations.target)
		Tessellator.instance.startDrawingQuads()
		Tessellator.instance.addVertexWithUV(caster.posX - radius, caster.posY, caster.posZ - radius, 0.0, 0.0)
		Tessellator.instance.addVertexWithUV(caster.posX - radius, caster.posY, caster.posZ + radius, 0.0, 1.0)
		Tessellator.instance.addVertexWithUV(caster.posX + radius, caster.posY, caster.posZ + radius, 1.0, 1.0)
		Tessellator.instance.addVertexWithUV(caster.posX + radius, caster.posY, caster.posZ - radius, 1.0, 0.0)
		Tessellator.instance.draw()
		glDisable(GL_BLEND)
		glAlphaFunc(GL_GREATER, 0.1f)
		glEnable(GL_CULL_FACE)
	}
}