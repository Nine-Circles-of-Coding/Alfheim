package alfheim.common.spell.wind

import alexsocol.asjlib.D
import alfheim.AlfheimCore
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.network.Message3d
import alfheim.common.network.Message3d.M3d
import net.minecraft.entity.EntityLivingBase
import net.minecraft.server.MinecraftServer

object SpellThunder: SpellBase("thunder", EnumRace.SYLPH, 30000, 6000, 50) {
	
	override val usableParams
		get() = emptyArray<Any>()
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		for (world in MinecraftServer.getServer().worldServers) {
			val time = caster.worldObj.rand.nextInt(12000) + 3600
			world.worldInfo.isRaining = true
			world.worldInfo.rainTime = time
			world.worldInfo.isThundering = true
			world.worldInfo.thunderTime = time
			AlfheimCore.network.sendToDimension(Message3d(M3d.WEATHER, 2.0, time.D, time.D), world.provider.dimensionId)
		}
		
		return result
	}
}