package alfheim.client.render.particle

import alexsocol.asjlib.F
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.client.particle.EntityDiggingFX
import net.minecraft.world.World
import java.awt.Color
import kotlin.math.*

@SideOnly(Side.CLIENT)
class EntityTornadoFX(world: World?, originX: Double, yPos: Double, originZ: Double, val radius: Double, val velX: Double, val velZ: Double, block: Block, meta: Int, side: Int, maxAge: Int): EntityDiggingFX(world, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, block, meta, side) {
    
    var angle = rand.nextDouble() * Math.PI * 2
    val speed = rand.nextDouble() * 2 + 1
    var fullBrightness = false
    
    init {
        val x = -cos(angle) * radius + originX
        val z = +sin(angle) * radius + originZ
        setPosition(x, yPos, z)
        particleMaxAge = maxAge
        noClip = true
    
        val color = Color(block.getRenderColor(side))
        setRBGColorF(color.red / 255f, color.green / 255f, color.blue / 255f)
        
        if (block.lightValue == 0) {
            particleRed *= 0.75f
            particleGreen *= 0.75f
            particleBlue *= 0.75f
        } else {
            fullBrightness = true
        }
    }
    
    override fun onUpdate() {
        prevPosX = posX
        prevPosY = posY
        prevPosZ = posZ
        
        if (particleAge++ >= particleMaxAge) {
            setDead()
        }
        
        // This is in radians per tick...
        val omega = sign(speed) * (Math.PI * 2 / 20 - speed / (20 * radius))
        
        // v = r times omega; therefore the normalised velocity vector needs to be r times the angle increment / 2 pi.
        angle += omega
        motionZ = radius * omega * cos(angle)
        motionX = radius * omega * sin(angle)
        
        moveEntity(motionX + velX, 0.0, motionZ + velZ)
        
        if (particleAge > particleMaxAge / 2) {
            setAlphaF(1f - (particleAge.F - (particleMaxAge / 2).F) / particleMaxAge.F)
        }
    }
    
    override fun getBrightnessForRender(ticks: Float): Int {
        return if (fullBrightness) 0xF000F0 else super.getBrightnessForRender(ticks)
    }
    
    override fun getBrightness(ticks: Float): Float {
        return if (fullBrightness) 1f else super.getBrightness(ticks)
    }
}