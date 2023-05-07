package alfheim.api.entity

import net.minecraft.entity.Entity

interface IMuspelheimEntity

interface INiflheimEntity

interface IIntersectAttackEntity {
	fun getExtraReach(): Double
	
	/**
	 * Check if this entity is an allie for [e] so it won't set it as attack target
	 */
	fun isAllie(e: Entity?): Boolean
}