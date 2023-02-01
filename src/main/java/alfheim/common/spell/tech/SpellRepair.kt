package alfheim.common.spell.tech

import alexsocol.asjlib.I
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.AlfheimConfigHandler
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.entity.EntityLivingBase
import kotlin.math.*

object SpellRepair: SpellBase("repair", EnumRace.LEPRECHAUN, 25000, 100, 50) {
	
	override var efficiency = 100.0
	
	override val usableParams = arrayOf(efficiency)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val stack = caster.heldItem ?: return SpellCastResult.NOTARGET
		val item = stack.item
		val dmg = stack.getItemDamage()
		if (!item.isRepairable || dmg <= 0) return SpellCastResult.WRONGTGT
		if (GameRegistry.findUniqueIdentifierFor(item).toString() in AlfheimConfigHandler.repairBlackList) return SpellCastResult.WRONGTGT
		
		val result = checkCast(caster)
		if (result == SpellCastResult.OK) stack.setItemDamage(max(0, dmg - max(0, efficiency.I)))
		return result
	}
}