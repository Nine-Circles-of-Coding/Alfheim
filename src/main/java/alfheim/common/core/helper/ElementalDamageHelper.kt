@file:Suppress("unused")

package alfheim.common.core.helper

import alexsocol.asjlib.*
import alfheim.api.entity.*
import alfheim.api.lib.LibResourceLocations
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.SheerColdHandler.cold
import alfheim.common.core.helper.ElementalDamage.*
import alfheim.common.core.helper.ElementalDamageBridge.*
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.*
import net.minecraft.entity.passive.EntityWaterMob
import net.minecraft.util.*
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.event.entity.living.*
import org.lwjgl.opengl.GL11.*
import vazkii.botania.common.item.equipment.bauble.ItemMonocle
import java.util.*
import kotlin.math.*

object ElementalDamageHandler {
	
	init {
		for (ed in ElementalDamage.values()) require(ElementalDamageBridge.valueOf("${ed}_").real == ed)
		
		DamageSource.inWall.setTo(EARTH)
		DamageSource.drown.setTo(WATER)
		DamageSource.cactus.setTo(NATURE)
		DamageSource.fall.setTo(EARTH)
		DamageSource.wither.setTo(DARKNESS)
		DamageSource.anvil.setTo(EARTH)
		DamageSource.fallingBlock.setTo(EARTH)
	}
	
	private val elementalMobs: Map<String, EnumSet<ElementalDamage>> by lazy {
		AlfheimConfigHandler.mobElements.associate { entry ->
			val (name, eList) = entry.split(":")
			val elements = eList.split(",").mapTo(EnumSet.noneOf(ElementalDamage::class.java), ElementalDamage::valueOf)
			name to elements
		}
	}
	
	val EntityLivingBase.elements: EnumSet<ElementalDamage>
		get() {
			if (this is IElementalEntity) return elements
			
			return elementalMobs[EntityList.getEntityString(this)] ?: EnumSet.of(COMMON)
		}
	
	val EntityLivingBase.appliedElements: EnumSet<ElementalDamage>
		get() {
			val set = EnumSet.noneOf(ElementalDamage::class.java)
			
			if (isBurning && !isImmuneToFire) set.add(FIRE)
			if (isWet && this !is EntityWaterMob) set.add(WATER)
			if (cold > 50) set.add(ICE)
			
			return set
		}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onAttacked(e: LivingAttackEvent) {
		val targetEl = e.entityLiving.elements
		val attackEl = e.source.elements()
		
		e.isCanceled = attackEl.any { targetEl.any(it::isImmune) }
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun onHurt(e: LivingHurtEvent) {
		val targetEl = e.entityLiving.elements
		val attackEl = e.source.elements()
		
		if (targetEl.any { attackEl.any(it::isResistant) } ) e.ammount *= 0.5f
		
		targetEl.addAll(e.entityLiving.appliedElements)
		if (targetEl.any { attackEl.any(it::isVulnerable) } ) e.ammount *= 2f
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun drawStatusIcons(e: RenderLivingEvent.Specials.Post) {
		if (mc.theWorld == null || mc.thePlayer == null) return // in-menu render
		
		if (!ItemMonocle.hasMonocle(mc.thePlayer)) return
		
		val applied = e.entity.appliedElements
		val elements = e.entity.elements.plus(applied).filter { it != COMMON }
		if (elements.isEmpty()) return
		
		val size = max(e.entity.width.D * 8, 8.0)
		
		val f1 = 0.02666667f
		glPushMatrix()
		glColor4f(1f, 1f, 1f, 1f)
		glTranslated(e.x, e.y + e.entity.height + 0.5 + 0.03125 * size, e.z)
		glNormal3f(0f, 1f, 0f)
		glRotatef(-RenderManager.instance.playerViewY, 0f, 1f, 0f)
		glRotatef(RenderManager.instance.playerViewX, 1f, 0f, 0f)
		glScalef(-f1, -f1, f1)
		glDisable(GL_LIGHTING)
		glEnable(GL_BLEND)
		OpenGlHelper.glBlendFunc(770, 771, 1, 0)
		
		glTranslatef(elements.size * size.F / -2, 0f, 0f)
		
		mc.renderEngine.bindTexture(LibResourceLocations.elements)
		
		val debufSignPoses = mutableListOf<Double>()
		val uOff = 1.0/11
		
		val tes = Tessellator.instance
		tes.startDrawingQuads()
		for ((id, element) in elements.withIndex()) {
			val x = id * size
			val u = (element.ordinal - 1) * uOff
			
			tes.addVertexWithUV(x       ,  0.0, 0.0, u       , 0.0)
			tes.addVertexWithUV(x       , size, 0.0, u       , 1.0)
			tes.addVertexWithUV(x + size, size, 0.0, u + uOff, 1.0)
			tes.addVertexWithUV(x + size,  0.0, 0.0, u + uOff, 0.0)
			
			if (element in applied) debufSignPoses.add(x)
		}
		tes.draw()
		
		debufSignPoses.forEach { x ->
			mc.fontRenderer.drawString("-", x.I, (size / 2).I, 0x8B0000)
		}
		
		glEnable(GL_LIGHTING)
		glDisable(GL_BLEND)
		glColor4f(1f, 1f, 1f, 1f)
		glPopMatrix()
	}
}

private enum class ElementalDamageBridge {
	COMMON_, FIRE_, WATER_, AIR_, EARTH_, ICE_, ELECTRIC_, NATURE_, LIGHTNESS_, DARKNESS_, PSYCHIC_, ALIEN_;
	val real get() = ElementalDamage.values()[ordinal]
}

enum class ElementalDamage(private val x2: Array<ElementalDamageBridge>, private val x05: Array<ElementalDamageBridge>) {
	COMMON(arrayOf(), arrayOf()),
	FIRE(arrayOf(WATER_, EARTH_), arrayOf(AIR_, NATURE_)),
	WATER(arrayOf(ICE_, ELECTRIC_), arrayOf(FIRE_, NATURE_)),
	AIR(arrayOf(FIRE_, ICE_), arrayOf(WATER_, EARTH_)),
	EARTH(arrayOf(WATER_, NATURE_), arrayOf(ICE_, ELECTRIC_)),
	ICE(arrayOf(FIRE_, ELECTRIC_), arrayOf(WATER_, NATURE_)),
	ELECTRIC(arrayOf(FIRE_, ICE_), arrayOf(WATER_, AIR_)),
	NATURE(arrayOf(FIRE_, ICE_), arrayOf(WATER_, ELECTRIC_)),
	LIGHTNESS(arrayOf(DARKNESS_), arrayOf(PSYCHIC_)),
	DARKNESS(arrayOf(LIGHTNESS_), arrayOf(ALIEN_)),
	PSYCHIC(arrayOf(ALIEN_), arrayOf(LIGHTNESS_)),
	ALIEN(arrayOf(PSYCHIC_), arrayOf(DARKNESS_));
	
	fun isVulnerable(type: ElementalDamage): Boolean {
		return ElementalDamageBridge.values()[type.ordinal] in x2
	}
	
	fun isResistant(type: ElementalDamage): Boolean {
		return ElementalDamageBridge.values()[type.ordinal] in x05
	}
	
	fun isImmune(type: ElementalDamage): Boolean {
		return if (type == COMMON) false else this == type
	}
}

interface IElementalEntity {
	val elements: EnumSet<ElementalDamage>
}

fun DamageSource.setTo(type: ElementalDamage): DamageSource {
	alfheim_synthetic_elementalFlag = if (type == COMMON) 0 else ASJBitwiseHelper.setBit(alfheim_synthetic_elementalFlag, type.ordinal, true)
	return this
}

fun DamageSource.isOf(type: ElementalDamage): Boolean {
	return if (type == COMMON) alfheim_synthetic_elementalFlag == 0 else {
		val stored = ASJBitwiseHelper.getBit(alfheim_synthetic_elementalFlag, type.ordinal)
		if (type == FIRE) isFireDamage || stored else stored
	}
}

fun DamageSource.elements(): EnumSet<ElementalDamage> {
	return when (damageType) {
		"basalz" -> EnumSet.of(EARTH)
		"blitz"  -> EnumSet.of(ELECTRIC)
		"blizz"  -> EnumSet.of(ICE)
		else     -> EnumSet.copyOf(ElementalDamage.values().filter { isOf(it) })
	}
}