package alfheim.common.potion

import alexsocol.asjlib.*
import alfheim.common.block.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.item.equipment.bauble.ItemPendant
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11.*

object PotionSoulburn: PotionAlfheim(AlfheimConfigHandler.potionIDSoulburn, "soulburn", true, 0xCC4400) {
	
	var time = 0
	
	override fun isReady(dur: Int, amp: Int): Boolean {
		time = dur
		return dur % 20 == 0
	}
	
	override fun performEffect(living: EntityLivingBase, amp: Int) {
		if (living is EntityPlayer && ItemPendant.canProtect(living, ItemPendant.Companion.EnumPrimalWorldType.MUSPELHEIM, time)) {
			living.getActivePotionEffect(id)?.duration = 1
			return
		}
		
		living.attackEntityFrom(DamageSourceSpell.soulburn, (amp + 1).F)
	}
	
	@SideOnly(Side.CLIENT)
	fun renderFireInFirstPerson() {
		val tessellator = Tessellator.instance
		glColor4f(1f, 1f, 1f, 0.9f)
		glEnable(GL_BLEND)
		OpenGlHelper.glBlendFunc(770, 771, 1, 0)
		val f1 = 1f
		
		for (i in 0..1) {
			glPushMatrix()
			val iicon = (AlfheimBlocks.redFlame as BlockRedFlame).icons[1]
			mc.textureManager.bindTexture(TextureMap.locationBlocksTexture)
			val f2 = iicon.minU
			val f3 = iicon.maxU
			val f4 = iicon.minV
			val f5 = iicon.maxV
			val f6 = (0f - f1) / 2f
			val f7 = f6 + f1
			val f8 = 0f - f1 / 2f
			val f9 = f8 + f1
			val f10 = -0.5f
			glTranslatef((-(i * 2 - 1)).F * 0.24f, -0.3f, 0f)
			glRotatef((i * 2 - 1).F * 10f, 0f, 1f, 0f)
			tessellator.startDrawingQuads()
			tessellator.addVertexWithUV(f6.D, f8.D, f10.D, f3.D, f5.D)
			tessellator.addVertexWithUV(f7.D, f8.D, f10.D, f2.D, f5.D)
			tessellator.addVertexWithUV(f7.D, f9.D, f10.D, f2.D, f4.D)
			tessellator.addVertexWithUV(f6.D, f9.D, f10.D, f3.D, f4.D)
			tessellator.draw()
			glPopMatrix()
		}
		
		glColor4f(1f, 1f, 1f, 1f)
		glDisable(GL_BLEND)
	}
}
