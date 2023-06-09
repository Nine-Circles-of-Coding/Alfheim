package alfheim.common.spell.water

import alexsocol.asjlib.D
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.network.*
import alfheim.common.network.packet.Message3d
import net.minecraft.entity.EntityLivingBase
import net.minecraft.server.MinecraftServer

object SpellRain: SpellBase("rain", EnumRace.UNDINE, 30000, 6000, 50) {
	
	override val usableParams
		get() = emptyArray<Any>()
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		for (world in MinecraftServer.getServer().worldServers) {
			val r = caster.worldObj.rand.nextInt(12000) + 3600
			val t = caster.worldObj.rand.nextInt(168000) + 12000
			world.worldInfo.isRaining = true
			world.worldInfo.rainTime = r
			world.worldInfo.isThundering = false
			world.worldInfo.thunderTime = t
			NetworkService.sendToDim(Message3d(M3d.WEATHER, 1.0, r.D, t.D), world.provider.dimensionId)
		}
		
		return result
	}
}