package alfheim.api.entity

import alfheim.common.core.helper.*
import net.minecraft.entity.Entity
import java.util.*

interface IMuspelheimEntity: IElementalEntity {
	override val elements get() = EnumSet.of(ElementalDamage.FIRE)!!
}

interface INiflheimEntity: IElementalEntity {
	override val elements get() = EnumSet.of(ElementalDamage.ICE)!!
}

interface IIntersectAttackEntity {
	fun getExtraReach(): Double
	
	/**
	 * Check if this entity is an allie for [e] so it won't set it as attack target
	 */
	fun isAllie(e: Entity?): Boolean
}