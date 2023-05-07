package alfheim.client.render.particle

import alexsocol.asjlib.*
import net.minecraft.client.particle.EntityFX
import net.minecraft.client.renderer.Tessellator
import net.minecraft.world.World

/**
 * @author Jokiboy (Nature Reborn)
 */
class EntityFXSmoke(world: World, x: Double, y: Double, z: Double, mx: Double, my: Double, mz: Double, scaleMod: Float = 1f, val density: Float = 1f): EntityFX(world, x, y, z, 0.0, 0.0, 0.0) {
    
    var smokeParticleScale: Float
    
    init {
        motionX *= 0.10000000149011612
        motionY *= 0.10000000149011612
        motionZ *= 0.10000000149011612
        motionX += mx
        motionY += my
        motionZ += mz
        particleBlue = (Math.random() * 0.25).F
        particleGreen = particleBlue
        particleRed = particleGreen
        particleScale *= 0.75f
        particleScale *= scaleMod
        smokeParticleScale = particleScale
        particleMaxAge = (8.0 / (Math.random() * 0.8 + 0.2)).I
        particleMaxAge = (particleMaxAge * scaleMod).I
        noClip = false
    }
    
    override fun renderParticle(par1Tessellator: Tessellator, par2: Float, par3: Float, par4: Float, par5: Float, par6: Float, par7: Float) {
        var f6 = (particleAge + par2) / particleMaxAge * 32.0f
        if (f6 < 0.0f) f6 = 0.0f
        if (f6 > 1.0f) f6 = 1.0f
        particleScale = smokeParticleScale * f6
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7)
    }
    
    override fun onUpdate() {
        prevPosX = posX
        prevPosY = posY
        prevPosZ = posZ
        if (particleAge++ >= particleMaxAge) setDead()
        setParticleTextureIndex(7 - particleAge * 8 / particleMaxAge)
        motionY += 0.004 / density
        moveEntity(motionX, motionY, motionZ)
        if (posY == prevPosY) {
            motionX *= 1.1
            motionZ *= 1.1
        }
//        motionX += windX / 50.0f * (posY + posY) / 256.0 / density
        motionX *= 1.03
        motionX *= 0.95
        motionY *= 0.985
//        motionZ += windZ / 50.0f * (posY + posY) / 256.0 / density
        motionZ *= 1.03
        motionZ *= 0.95
        if (onGround) {
            motionX *= 0.699999988079071
            motionZ *= 0.699999988079071
        }
    }
    
    fun setMaxAge(i: Int) {
        if (i > particleAge) particleMaxAge = i
    }
}