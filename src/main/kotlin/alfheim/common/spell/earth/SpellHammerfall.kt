package alfheim.common.spell.earth

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.EnumRace
import alfheim.api.lib.LibResourceLocations
import alfheim.api.spell.SpellBase
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.CardinalSystem.PartySystem
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.core.util.DamageSourceSpell
import net.minecraft.client.renderer.Tessellator
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.common.util.ForgeDirection
import org.lwjgl.opengl.GL11.*

object SpellHammerfall: SpellBase("hammerfall", EnumRace.GNOME, 10000, 200, 20) {
	
	override var damage = 10f
	override var radius = 10.0
	
	override val usableParams
		get() = arrayOf(damage, radius)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		if (!caster.onGround || caster.worldObj.isAirBlock(caster.posX.mfloor(), caster.posY.mfloor() - 1, caster.posZ.mfloor())) return SpellCastResult.WRONGTGT
		
		// if (!WorldGuardCommons.canDoSomethingHere(caster)) return SpellCastResult.NOTALLOW
		
		val result = checkCastOver(caster)
		if (result != SpellCastResult.OK) return result
		
		VisualEffectHandler.sendPacket(VisualEffects.TREMORS, caster)
		
		val list = getEntitiesWithinAABB(caster.worldObj, EntityLivingBase::class.java, caster.boundingBox.expand(radius, radius / 5, radius))
		list.remove(caster)
		
		list.forEach {
			val block = it.worldObj.getBlock(it.posX.mfloor(), (it.posY - 1).mfloor(), it.posZ.mfloor())
			if (!(it.onGround &&
				block.material.isSolid &&
				block.isSideSolid(it.worldObj, it.posX.mfloor(), (it.posY - 1).mfloor(), it.posZ.mfloor(), ForgeDirection.UP) &&
				block.getBlockHardness(it.worldObj, it.posX.mfloor(), (it.posY - 1).mfloor(), it.posZ.mfloor()) < 2 &&
				!PartySystem.mobsSameParty(caster, it) &&
				Vector3.entityDistancePlane(it, caster) < radius)) return@forEach
				
			it.attackEntityFrom(DamageSourceSpell.hammerfall(caster), over(caster, damage.D))
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