@file:Suppress("unused", "UNUSED_PARAMETER")

package alfheim.common.core.asm.hook.fixes

import alexsocol.asjlib.*
import cpw.mods.fml.relauncher.*
import gloomyfolken.hooklib.asm.*
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.RenderPlayerEvent
import org.lwjgl.opengl.GL11
import vazkii.botania.api.item.IBaubleRender
import vazkii.botania.common.item.equipment.bauble.ItemFlightTiara
import java.nio.charset.StandardCharsets
import java.security.*
import kotlin.math.*

// Fixed tiara hash not working somewhere because of different SecureRandom implementations and different charsets (code from 1.16.5)
// And yes, I changed the "password" since I have no idea what the original one is, and one from new version is inapplicable to current
// If anyone has relevant information - let me know
// Also fixed animation for 'Cirno' and 'The One'
object FlightTiaraFix {
	
	const val WING_TYPES = 9
	
	@JvmStatic
	@Hook(injectOnExit = true)
	fun onEquipped(item: ItemFlightTiara, stack: ItemStack, player: EntityLivingBase?) {
		if(stack.getItemDamage() != WING_TYPES && getHash(stack.displayName) == "0DAB89CC38A6546EDBC2127844CD92F7C4774E22446F0D0CCE3523E475CE3910") {
			stack.setItemDamage(WING_TYPES)
			stack.tagCompound.removeTag("display")
		}
	}
	
	private val HEX_CHARS = "0123456789ABCDEF".toCharArray()
	
	private fun getHash(str: String?): String {
		if (str != null) {
			try {
				val md = MessageDigest.getInstance("SHA-256")
				val ret = StringBuilder()
				val bytes = md.digest(dontRainbowTableMeOrMySonEverAgain(str).toByteArray(StandardCharsets.UTF_8))
				for (b in bytes) {
					ret.append(HEX_CHARS[b.toInt() shr 4 and 0xF])
					ret.append(HEX_CHARS[b.toInt() and 0xF])
				}
				return ret.toString()
			} catch (e: NoSuchAlgorithmException) {
				e.printStackTrace()
			}
		}
		return ""
	}
	
	private fun dontRainbowTableMeOrMySonEverAgain(str: String): String {
		var input = str
		input += reverseString(input)
		val rand = SecureRandom.getInstance("SHA1PRNG")
		rand.setSeed(input.toByteArray(StandardCharsets.UTF_8))
		val l = input.length
		val steps: Int = rand.nextInt(l)
		val chrs = input.toCharArray()
		for (i in 0 until steps) {
			val indA: Int = rand.nextInt(l)
			var indB: Int
			do {
				indB = rand.nextInt(l)
			} while (indB == indA)
			val c = (chrs[indA].code xor chrs[indB].code).toChar()
			chrs[indA] = c
		}
		return String(chrs)
	}
	
	private fun reverseString(str: String): String {
		return StringBuilder(str).reverse().toString()
	}
	
	@SideOnly(Side.CLIENT)
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE)
	fun onPlayerBaubleRender(item: ItemFlightTiara, stack: ItemStack, event: RenderPlayerEvent, type: IBaubleRender.RenderType): Boolean {
		val meta = stack.getItemDamage()
		if (meta != 3 && meta != 9) return false
		if (type != IBaubleRender.RenderType.BODY) return false
		
		val icon = ItemFlightTiara.wingIcons[meta - 1]
		mc.renderEngine.bindTexture(TextureMap.locationItemsTexture)
		val player = event.entityPlayer
		val flying = player.capabilities.isFlying
		var rz = 120f
		var rx = 20f + ((sin((player.ticksExisted + event.partialRenderTick).D * if (flying) 0.4f else 0.2f) + 0.5f) * if (flying) 30f else 5f).F
		var ry = 0f
		var h = 0.2f
		var x = 0f
		var z = 0.15f
		GL11.glPushMatrix()
		GL11.glEnable(GL11.GL_BLEND)
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
		GL11.glColor4f(1f, 1f, 1f, 1f)
		val light = 15728880
		val lightmapX = light % 65536
		val lightmapY = light / 65536
		when (meta) {
			3 -> {
				// Cirno
				h = -0.1f
				rz = 0f
				ry = -rx
				rx = 0f
				z = 0.1f
			}
			9 -> {
				// The One
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightmapX.F, lightmapY.F)
				rz = 180f
				rx = 0f
				ry = -((sin((player.ticksExisted + event.partialRenderTick) * 0.2) + 0.6) * if (flying) 12 else 5).F - 5
				h = 0.85f
				x = -0.125f
				GL11.glColor4f(1f, 1f, 1f, 0.5f + if (flying) cos((player.ticksExisted + event.partialRenderTick) * 0.3).F * 0.25f + 0.25f else 0f)
			}
		}
		val f = icon.minU
		val f1 = icon.maxU
		val f2 = icon.minV
		val f3 = icon.maxV
		IBaubleRender.Helper.rotateIfSneaking(player)
		
		fun render() {
			GL11.glTranslatef(x, h, z)
			GL11.glRotatef(rz, 0f, 0f, 1f)
			GL11.glRotatef(rx, 1f, 0f, 0f)
			GL11.glRotatef(ry, 0f, 1f, 0f)
			ItemRenderer.renderItemIn2D(Tessellator.instance, f1, f2, f, f3, icon.iconWidth, icon.iconHeight, 1f / 32f)
			GL11.glRotatef(-ry, 0f, 1f, 0f)
			GL11.glRotatef(-rx, 1f, 0f, 0f)
			GL11.glRotatef(-rz, 0f, 0f, 1f)
			GL11.glTranslatef(-x, -h, -z)
		}
		
		render()
		GL11.glScalef(-1f, 1f, 1f)
		render()
		
		GL11.glColor4f(1f, 1f, 1f, 1f)
		GL11.glPopMatrix()
		
		return true
	}
}