package alfheim.common.entity.spell

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.spell.ITimeStopSpecific
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.PartySystem
import alfheim.common.spell.sound.SpellHarp
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.*
import net.minecraft.world.World
import java.util.*

class EntitySpellHarp(world: World): Entity(world), ITimeStopSpecific {
	
	var caster: EntityLivingBase? = null
	
	override val isImmune = false
	
	init {
		setSize(0.1f, 0.1f)
		renderDistanceWeight = 10.0
	}
	
	constructor(world: World, caster: EntityLivingBase, x: Double, y: Double, z: Double): this(world) {
		setPosition(x, y, z)
		this.caster = caster
	}
	
	override fun onUpdate() {
		if (!AlfheimConfigHandler.enableMMO || !worldObj.isRemote && (caster == null || caster!!.isDead)) {
			setDead()
		} else {
			if (ASJUtilities.isClient) return
			super.onUpdate()
			
			if (ticksExisted >= SpellHarp.duration) setDead()
			
			val pt = PartySystem.getMobParty(caster)
			if (pt == null || pt.count == 0) {
				setDead()
				return
			}
			
			if (worldObj.rand.nextInt() % (SpellHarp.efficiency.I / pt.count) == 0) {
				var mr = pt[worldObj.rand.nextInt(pt.count)] ?: return
				if (Vector3.entityDistance(this, mr) > SpellHarp.radius) return
				mr.heal(SpellHarp.damage)
				
				mr = pt[worldObj.rand.nextInt(pt.count)] ?: return
				for (o in mr.activePotionEffects) {
					if (Potion.potionTypes[(o as PotionEffect).potionID].isBadEffect) {
						mr.removePotionEffect(o.potionID)
						break
					}
				}
				
				VisualEffectHandler.sendPacket(VisualEffects.NOTE, this)
			}
		}
	}
	
	override fun affectedBy(uuid: UUID) = caster?.uniqueID != uuid
	
	override fun entityInit() = Unit
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		if (nbt.hasKey("castername")) caster = worldObj.getPlayerEntityByName(nbt.getString("castername")) else setDead()
		if (caster == null) setDead()
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		if (caster is EntityPlayer) nbt.setString("castername", caster!!.commandSenderName)
	}
}