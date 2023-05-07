package alfheim.common.block.magtrees.sealing

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.client.audio.*
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.client.event.sound.PlaySoundEvent17

/**
 * @author WireSegal
 * Created at 8:49 AM on 1/27/16.
 */
object EventHandlerSealingOak {
	
	const val MAXRANGE = 16
	
	@SubscribeEvent
	fun onSound(event: PlaySoundEvent17) {
		if (event.result == null || event.result is ITickableSound) return
		
		val world = mc.theWorld ?: return
		val x = event.result.xPosF.I
		val y = event.result.yPosF.I
		val z = event.result.zPosF.I
		
		val volumeMultiplier = calculateMultiplier(world, x, y, z)
		
		if (volumeMultiplier == 1f) return
		
		event.result = VolumeModSound(event.result, volumeMultiplier)
	}
	
	fun calculateMultiplier(world: World, x: Int, y: Int, z: Int): Float {
		var volumeMultiplier = 1f
		
		for (dx in x.bidiRange(MAXRANGE)) {
			for (dy in y.bidiRange(MAXRANGE)) {
				for (dz in z.bidiRange(MAXRANGE)) {
					val block = world.getBlock(dx, dy, dz)
					if (block !is ISoundSilencer) continue
					
					val distance = Vector3.pointDistanceSpace(dx + 0.5, dy + 0.5, dz + 0.5, x, y, z)
					if (distance > MAXRANGE || !block.canSilence(world, dx, dy, dz, distance)) continue
					
					volumeMultiplier *= block.getVolumeMultiplier(world, dx, dy, dz, distance)
				}
			}
		}
		
		return volumeMultiplier
	}
	
	class VolumeModSound(val sound: ISound, val volumeMult: Float): ISound {
		
		override fun getPositionedSoundLocation(): ResourceLocation = sound.positionedSoundLocation
		override fun canRepeat(): Boolean = sound.canRepeat()
		override fun getRepeatDelay(): Int = sound.repeatDelay
		override fun getVolume(): Float = sound.volume * volumeMult
		override fun getPitch(): Float = sound.pitch
		override fun getXPosF(): Float = sound.xPosF
		override fun getYPosF(): Float = sound.yPosF
		override fun getZPosF(): Float = sound.zPosF
		override fun getAttenuationType(): ISound.AttenuationType = sound.attenuationType
	}
}
