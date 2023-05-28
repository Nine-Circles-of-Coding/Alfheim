package alfheim.common.spell.fire

import alexsocol.asjlib.D
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.network.*
import alfheim.common.network.packet.Message3d
import net.minecraft.entity.EntityLivingBase
import net.minecraft.server.MinecraftServer

object SpellSun: SpellBase("sun", EnumRace.SALAMANDER, 30000, 6000, 50) {
	
	override val usableParams
		get() = emptyArray<Any>()
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		for (world in MinecraftServer.getServer().worldServers) {
			val time = caster.worldObj.rand.nextInt(168000) + 12000
			world.worldInfo.isRaining = false
			world.worldInfo.rainTime = time
			world.worldInfo.isThundering = false
			world.worldInfo.thunderTime = time
			NetworkService.sendToDim(Message3d(M3d.WEATHER, 0.0, time.D, time.D), world.provider.dimensionId)
		}
		
		return result
	}
}