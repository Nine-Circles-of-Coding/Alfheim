@file:Suppress("unused")

package alfheim.common.core.helper

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.ModInfo
import alfheim.api.entity.*
import alfheim.common.core.helper.ElementalDamage.*
import alfheim.common.core.helper.ElementalDamageBridge.*
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.*
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
	
	private val elementalMobs: Map<String, EnumSet<ElementalDamage>> get() = mapOf(
		"Blaze" to EnumSet.of(FIRE),
		"EnderDragon" to EnumSet.of(ALIEN),
		"Enderman" to EnumSet.of(ALIEN),
		"Ghast" to EnumSet.of(AIR, PSYCHIC),
		"LavaSlime" to EnumSet.of(FIRE),
		"MushroomCow" to EnumSet.of(NATURE),
		"SnowMan" to EnumSet.of(ICE),
		"Slime" to EnumSet.of(NATURE, WATER),
		"VillagerGolem" to EnumSet.of(EARTH),
		"WitherBoss" to EnumSet.of(DARKNESS),
		"Thaumcraft.EldritchGolem" to EnumSet.of(EARTH),
		"Thaumcraft.EldritchGuardian" to EnumSet.of(ALIEN, DARKNESS, PSYCHIC),
		"Thaumcraft.EldritchWarden" to EnumSet.of(ALIEN, DARKNESS, PSYCHIC),
		"Thaumcraft.Firebat" to EnumSet.of(FIRE),
		"Thaumcraft.Golem" to EnumSet.of(EARTH),
		"Thaumcraft.MindSpider" to EnumSet.of(PSYCHIC),
		"Thaumcraft.ThaumSlime" to EnumSet.of(WATER, DARKNESS),
		"Thaumcraft.Wisp" to EnumSet.of(AIR),
		"ThermalFoundation.Blizz" to EnumSet.of(ICE),
		"ThermalFoundation.Blitz" to EnumSet.of(ELECTRIC),
		"ThermalFoundation.Basalz" to EnumSet.of(ICE),
	)
	
	val EntityLivingBase.elements: EnumSet<ElementalDamage>
		get() {
			if (this is IElementalEntity) return elements
			
			return elementalMobs[EntityList.getEntityString(this)] ?: EnumSet.of(COMMON)
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
		
		if (e.entityLiving.isBurning) targetEl.add(FIRE)
		if (e.entityLiving.isWet) targetEl.add(WATER)
		
		if (targetEl.any { attackEl.any(it::isVulnerable) } ) e.ammount *= 2f
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun drawStatusIcons(e: RenderLivingEvent.Specials.Post) {
		if (!ItemMonocle.hasMonocle(mc.thePlayer)) return
		
		val elements = e.entity.elements.filter { it != COMMON }
		if (elements.isEmpty()) return
		
		val size = max(e.entity.width.D * 8, 8.0)
		
		val f1 = 0.02666667f
		glPushMatrix()
		glTranslated(e.x, e.y + e.entity.height + 0.03125 * size, e.z)
		glNormal3f(0f, 1f, 0f)
		glRotatef(-RenderManager.instance.playerViewY, 0f, 1f, 0f)
		glRotatef(RenderManager.instance.playerViewX, 1f, 0f, 0f)
		glScalef(-f1, -f1, f1)
		glDisable(GL_LIGHTING)
		glEnable(GL_BLEND)
		OpenGlHelper.glBlendFunc(770, 771, 1, 0)
		
		glTranslatef(elements.size * size.F / -2, 0f, 0f)
		val tes = Tessellator.instance
		
		for (element in elements) {
			mc.renderEngine.bindTexture(ResourceLocation(ModInfo.MODID, "textures/misc/elements/${element.name}.png"))
			ASJRenderHelper.glColor1u(ASJRenderHelper.addAlpha(element.color, 255))
			
			tes.startDrawingQuads()
			tes.addVertexWithUV( 0.0,  0.0, 0.0, 0.0, 0.0)
			tes.addVertexWithUV( 0.0, size, 0.0, 0.0, 1.0)
			tes.addVertexWithUV(size, size, 0.0, 1.0, 1.0)
			tes.addVertexWithUV(size,  0.0, 0.0, 1.0, 0.0)
			tes.draw()
			
			glTranslatef(size.F, 0f, 0f)
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

enum class ElementalDamage(private val x2: Array<ElementalDamageBridge>, private val x05: Array<ElementalDamageBridge>, val color: Int) {
	COMMON(arrayOf(), arrayOf(), 0xFFFFFF),
	FIRE(arrayOf(WATER_, EARTH_), arrayOf(NATURE_, ICE_), 0xFF5A01),
	WATER(arrayOf(ELECTRIC_, NATURE_), arrayOf(FIRE_, ICE_), 0x3CD4FC),
	AIR(arrayOf(ELECTRIC_, ICE_), arrayOf(EARTH_, NATURE_), 0xFFFF7E),
	EARTH(arrayOf(WATER_, NATURE_), arrayOf(FIRE_, ELECTRIC_), 0x56C000),
	ICE(arrayOf(FIRE_, ELECTRIC_), arrayOf(PSYCHIC_, NATURE_), 0xE1FFFF),
	ELECTRIC(arrayOf(FIRE_, ICE_), arrayOf(NATURE_, AIR_), 0xAA00FF),
	NATURE(arrayOf(FIRE_, ICE_), arrayOf(WATER_, EARTH_), 0x01AC00),
	LIGHTNESS(arrayOf(DARKNESS_), arrayOf(PSYCHIC_), 0xDDDDDD),
	DARKNESS(arrayOf(LIGHTNESS_), arrayOf(ALIEN_), 0x222222),
	PSYCHIC(arrayOf(LIGHTNESS_, DARKNESS_), arrayOf(COMMON_), 0xFFC2B3),
	ALIEN(arrayOf(LIGHTNESS_), arrayOf(DARKNESS_), 0x805080);
	
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