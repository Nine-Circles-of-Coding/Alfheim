package alfheim.common.potion

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.spell.darkness.SpellSacrifice
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.ai.attributes.BaseAttributeMap
import net.minecraft.entity.boss.IBossDisplayData
import vazkii.botania.common.Botania

object PotionSacrifice: PotionAlfheim(AlfheimConfigHandler.potionIDSacrifice, "sacrifice", false, 0) {
	
	var timeQueued: Int = 0
	
	override fun isReady(time: Int, mod: Int): Boolean {
		timeQueued = time
		return AlfheimConfigHandler.enableMMO && timeQueued <= 32
	}
	
	override fun performEffect(target: EntityLivingBase, mod: Int) {
		if (!AlfheimConfigHandler.enableMMO) return
		if (timeQueued == 32)
			for (i in 0..7)
				target.worldObj.playSoundEffect(target.posX, target.posY, target.posZ, ModInfo.MODID + ":redexp", 10000f, 0.8f + target.worldObj.rand.nextFloat() * 0.2f)
		else
			particles(target, 32 - timeQueued)
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase, attributes: BaseAttributeMap, ampl: Int) {
		super.removeAttributesModifiersFromEntity(target, attributes, ampl)
		if (!AlfheimConfigHandler.enableMMO) return
		val l = getEntitiesWithinAABB(target.worldObj, EntityLivingBase::class.java, target.boundingBox.copy().expand(SpellSacrifice.radius))
		for (e in l) {
			if (e is IBossDisplayData && !AlfheimConfigHandler.superSpellBosses) continue
			val dmg = if (e === target) DamageSourceSpell.sacrifice else DamageSourceSpell.sacrifice(target)
			e.attackEntityFrom(dmg, SpellSacrifice.damage)
		}
	}
	
	fun particles(target: EntityLivingBase, time: Int) {
		val v = Vector3()
		for (i in 1..(SpellSacrifice.radius.I * 4)) {
			v.rand().sub(0.5).normalize().mul(time / 32.0 * SpellSacrifice.radius)
			Botania.proxy.wispFX(target.worldObj, target.posX + v.x, target.posY + v.y, target.posZ + v.z, 1f, Math.random().F * 0.5f, Math.random().F * 0.075f, (Math.random() * time + 1).F, 0f, (Math.random() * 3.0 + 2).F)
		}
	}
}