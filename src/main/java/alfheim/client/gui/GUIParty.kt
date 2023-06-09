package alfheim.client.gui

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.*
import alfheim.api.entity.*
import alfheim.api.lib.*
import alfheim.client.core.handler.CardinalSystemClient.PlayerSegmentClient
import alfheim.client.render.entity.RenderWings
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.CardinalSystem.PartySystem.Party
import alfheim.common.core.helper.*
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.Tessellator
import net.minecraft.entity.*
import net.minecraft.entity.boss.IBossDisplayData
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.*
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.mana.*
import vazkii.botania.client.core.helper.ShaderHelper
import java.awt.Color
import java.text.DecimalFormat
import kotlin.math.*

object GUIParty: Gui() {
	
	val format = DecimalFormat("0.0#")
	
	@SubscribeEvent
	fun onOverlayRendering(event: RenderGameOverlayEvent.Post) {
		if (event.type != ElementType.HOTBAR) return
		
		val player = mc.thePlayer
		val font = mc.fontRenderer
		
		val pt = PlayerSegmentClient.party ?: let {
			PlayerSegmentClient.party = Party(mc.thePlayer)
			PlayerSegmentClient.party!!
		}
		
		var color: Int
		val red = -0x230000
		val yellow = -0x222300
		val green = -0xff2300
		var data: String
		zLevel = -90f
		val s = AlfheimConfigHandler.partyHUDScale
		
		glPushMatrix()
		glEnable(GL_BLEND)
		glColor4d(1.0, 1.0, 1.0, 1.0)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		glScaled(s)
		
		// ################################################################ SELF ################################################################
		
		if (AlfheimConfigHandler.selfHealthUI) {
			glPushMatrix()
			
			glColor4d(1.0, 1.0, 1.0, 1.0)
			mc.renderEngine.bindTexture(LibResourceLocations.health)
			drawTexturedModalRect(0, 0, 0, 0, 200, 40)
			ASJRenderHelper.glColor1u(ASJRenderHelper.addAlpha(player.race.rgbColor, 255))
			drawTexturedModalRect(0, 0, 0, 40, 38, 40)
			
			// ################ health: ################
			run {
				val mod = (min(player.health, player.maxHealth) / max(player.maxHealth, 1f) * 158.0).I / 158.0
				ASJRenderHelper.glColor1u(if (mod > 0.5) green else if (mod > 0.1) yellow else red)
				val length = (158 * mod).mfloor()
				
				if (length <= 10) {
					drawTexturedModalRect(38, 14 + (10 - length), 38, 54 + (10 - length), 1, length)
					drawTexturedModalRect(39, 14, 186 + (11 - length), 54, 10, 10)
				} else {
					drawTexturedModalRect(38, 14, 38, 54, 1, 10)
					drawTexturedModalRect(39 + length - 11, 14, 186, 54, 10, 10)
				}
				
				if (length > 11) drawTexturedModalRect(39, 14, 39, 54, length - 11, 10)
				
				glColor4d(1.0, 1.0, 1.0, 1.0)
			}
			
			// ################ mana: ################
			run mana@{
				var totalMana = 0
				var totalMaxMana = 0
				var anyRequest = false
				var creative = player.capabilities.isCreativeMode
				
				val mainInv = player.inventory
				val baublesInv = BotaniaAPI.internalHandler.getBaublesInventory(player)
				
				val invSize = mainInv.sizeInventory
				var size = invSize
				if (baublesInv != null)
					size += baublesInv.sizeInventory
				
				for (i in 0 until size) {
					val useBaubles = i >= invSize
					val inv = if (useBaubles) baublesInv else mainInv
					val stack = inv[i - if (useBaubles) invSize else 0]
					
					if (stack != null) {
						val item = stack.item
						if (item is IManaUsingItem)
							anyRequest = anyRequest || (item as IManaUsingItem).usesMana(stack)
						
						if (creative) continue
						
						if (item is IManaItem) {
							if (!(item as IManaItem).isNoExport(stack)) {
								totalMana += (item as IManaItem).getMana(stack)
								totalMaxMana += (item as IManaItem).getMaxMana(stack)
							}
						}
						
						if (item is ICreativeManaProvider && (item as ICreativeManaProvider).isCreative(stack))
							creative = true
					}
				}
				
				totalMaxMana = max(totalMana, totalMaxMana)
				
				val col = Color(Color.HSBtoRGB(0.55f, if (anyRequest) min(1.0, sin(System.currentTimeMillis() / 1000.0) * 0.25 + 1.0).F else 1f, 1f))
				glColor4ub(col.red.toByte(), col.green.toByte(), col.blue.toByte(), 255.toByte())
				
				var length = 158
				
				if (!creative) {
					length = if (totalMaxMana == 0)
						0
					else {
						val temp = totalMana.D / totalMaxMana.D * length
						temp.I
					}
				}
				
				if (length == 0) {
					if (totalMana > 0)
						length = 1
					else
						return@mana
				}
				
				if (length <= 10) {
					drawTexturedModalRect(38, 26, 38, 66, 1, length)
					drawTexturedModalRect(39, 26, 186 + (11 - length), 66, 10, 10)
				} else {
					drawTexturedModalRect(38, 26, 38, 66, 1, 10)
					drawTexturedModalRect(39 + length - 11, 26, 186, 66, 10, 10)
				}
				
				if (length > 11) drawTexturedModalRect(39, 26, 39, 66, length - 11, 10)
			}
			
			// ################ hp: ################
			run {
				glTranslated(0.0, -0.5, -89.0)
				data = (format.format(player.health.D) + "/" + format.format(player.maxHealth.D)).replace(',', '.')
				font.drawString(data, 117 - font.getStringWidth(data) / 2, 16, 0x0)
				glTranslated(0.0, 0.5, 89.0)
			}
			
			// ################ name: ################
			run {
				data = mc.thePlayer.commandSenderName
				
				var shadow = true
				
				for (c in data) {
					if ("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(c) == -1) {
						shadow = false
						break
					}
				}
				
				glTranslated(0.0, 0.0, -89.0)
				font.drawString(data, 88 - font.getStringWidth(data) / 2, 3, if (PlayerSegmentClient.target === mc.thePlayer) green else if (pt[0] === mc.thePlayer) red else -0x1, shadow)
				glTranslated(0.0, 0.0, 89.0)
			}
			glColor4d(1.0, 1.0, 1.0, 1.0)
			
			glScaled(0.5)
			glTranslatef(5f, 80f, 0f)
			renderPotions(player, player.activePotionEffects.size, null, 19)
			
			glPopMatrix()
		}
		
		// ################################################################ PARTY ################################################################
		var l: EntityLivingBase?
		
		run {
			glPushMatrix()
			var hp: Float
			var hpm: Float
			var y = 10
			var col = -0x222223 // bg color
			var st = false
			var shadow = true
			
			// ################ rest: ################
			for (i in 0 until pt.count) {
				
				// ################ colors: ################
				
				l = pt[i]
				if (l === mc.thePlayer) continue
				
				color = when {
					i == 0         -> red        // PL
					pt.isPlayer(i) -> -0x1       // Player
					else           -> yellow     // Mob
				}
				
				if (l == null) {
					color = 0xCCCCCC
					col = when (val it = pt.getType(i)) {
						in EnumRace.values().indices -> EnumRace.getRGBColor(it)
						LibResourceLocations.BOSS    -> 0xA2018C
						LibResourceLocations.NPC     -> -0xFF5501
						LibResourceLocations.MOB     -> col
						else                         -> -0x777778
					}
					hpm = pt.getMaxHealth(i)
					hp = min(pt.getHealth(i), hpm)
				} else {
					when (l) {
						is EntityPlayer     -> col = (l as EntityPlayer).race.rgbColor
						
						is INpc             -> {
							color = -0xff5501
							col = color
						}
						
						is IBossDisplayData -> {
							color = 0xA2018C
							col = color
						}
					}
					
					if (PlayerSegmentClient.target === l) color = 0x00FF00                // selected target
					if (Vector3.entityDistance(player, l!!) > 32) color = 0xCCCCCC        // out of reach
					//if (mc.thePlayer.dimension != l.dimension) color = 0x888888		  // other dim
					hpm = l!!.maxHealth
					hp = min(l!!.health, hpm)
				}
				
				if (pt.isDead(i)) {                                                        // dead
					color = 0x444444
					hpm = 0f
					hp = hpm
					st = true
				}
				
				// ################ hp bg: ################
				mc.renderEngine.bindTexture(LibResourceLocations.health)
				y += 40
				if (PlayerSegmentClient.partyIndex == i) glColor4f(0.75f, 1f, 0.75f, 1f)
				drawTexturedModalRect(0, y, 0, 80, 136, 40)
				if (PlayerSegmentClient.partyIndex == i) glColor4f(1f, 1f, 1f, 1f)
				// ################ ava bg: ################
				ASJRenderHelper.glColor1u(ASJRenderHelper.addAlpha(col, 255))
				drawTexturedModalRect(0, y, 0, 120, 32, 40)
				
				// ################ health: ################
				run health@{
					if (pt.isDead(i)) return@health
					
					val mod: Double
					if (hp != -1f && hpm != -1f) {
						mod = (hp / max(hpm, 1f) * 100.0).I / 100.0
						ASJRenderHelper.glColor1u(if (mod > 0.5) green else if (mod > 0.1) yellow else red)
					} else {
						mod = 1.0
						ASJRenderHelper.glColor1u(-0xbbbbbc)
					}
					
					val length = (100 * mod).mfloor()
					
					when {
						length < 2  -> drawTexturedModalRect(34, y + 17 + 2, 133, 137, 1, 4)
						
						length == 2 -> {
							drawTexturedModalRect(34, y + 17, 34, 137, 1, 6)
							drawTexturedModalRect(35, y + 17, 132 + (3 - length), 137, 2, 6)
						}
						
						else        -> {
							drawTexturedModalRect(34, y + 17, 34, 137, 1, 6)
							drawTexturedModalRect(35 + length - 3, y + 17, 132, 137, 2, 6)
						}
					}
					
					if (length > 3) drawTexturedModalRect(35, y + 17, 35, 137, length - 3, 6)
					
					glColor4d(1.0, 1.0, 1.0, 1.0)
				}
				
				// ################ mana: ################
				run mana@{
					if (l != null && !pt.isPlayer(i)) return@mana
					val length = min(100, pt.getMana(i) / 10000)
					
					if (length <= 0) return@mana
					
					ASJRenderHelper.glColor1u(-0xff4d01)
					
					if (l == null) ASJRenderHelper.glColor1u(-0xbbbbbc)
					
					when {
						length < 2  -> drawTexturedModalRect(34, y + 25, 133, 145, 1, 4)
						
						length == 2 -> {
							drawTexturedModalRect(34, y + 25, 34, 145, 1, 6)
							drawTexturedModalRect(35, y + 25, 132 + (3 - length), 145, 2, 6)
						}
						
						else        -> {
							drawTexturedModalRect(34, y + 25, 34, 145, 1, 6)
							drawTexturedModalRect(35 + length - 3, y + 25, 132, 145, 2, 6)
						}
					}
					
					if (length > 3) drawTexturedModalRect(35, y + 25, 35, 137, length - 3, 6)
					
					if (pt.getMana(i) > 1000000) {
						glTranslated(0.5, 0.5, 0.0)
						font.drawString(if (pt.getMana(i) == Integer.MAX_VALUE) "*" else "+", 128, y + 24, 0x0000FF)
						glTranslated(-0.5, -0.5, 0.0)
					}
				}
				
				// ################ name: ################
				run {
					data = if (l != null) l!!.commandSenderName else pt.getName(i)
					/*var flag = false
					while (font.getStringWidth(data) > 82) {
						data = data.substring(0, data.length - 1)
						flag = true
					}
					if (flag) data = "$data..."*/
					
					var j = 0
					while (j < data.length && shadow) {
						val c = data[j]
						if ("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(c) == -1) {
							shadow = false
						}
						j++
					}
					
					if (st) data = EnumChatFormatting.STRIKETHROUGH.toString() + data
					glTranslated(0.0, 0.0, -85.0)
					font.drawString(data, 36, y + 4, color, shadow)
					glTranslated(0.0, 0.0, 85.0)
					
					/*val hpm: Float
					val hp: Float
					
					if (l != null) {
						hpm = l!!.maxHealth
						hp = l!!.health
					} else {
						hpm = pt.getMaxHealth(i)
						hp = pt.getHealth(i)
					}*/
					
					glTranslated(0.0, -0.5, -85.0)
					val unicode = font.unicodeFlag
					font.unicodeFlag = true
					data = (format.format(hp.D) + "/" + format.format(hpm.D)).replace(',', '.')
					font.drawString(data, 84 - font.getStringWidth(data) / 2, y + 16, 0x0)
					font.unicodeFlag = unicode
					glTranslated(0.0, 0.5, 85.0)
				}
				
				// ################ debuffs: ################
				run debuffs@{
					if (l == null) return@debuffs
					val pes = l!!.activePotionEffects
					if (pes.isEmpty()) return@debuffs
					glPushMatrix()
					glTranslated(34.0, (y + 32).D, 0.0)
					val s2 = 0.5
					glScaled(s2)
					glColor4d(1.0, 1.0, 1.0, 1.0)
					
					renderPotions(l!!, pes.size, null, 10)
					
					glPopMatrix()
				}
				
				st = false
				shadow = true
				col = -0x222223
				glColor4d(1.0, 1.0, 1.0, 1.0)
			}
			
			glPopMatrix()
		}
		
		// ################################################################ TARGET ################################################################
		if (AlfheimConfigHandler.targetUI && PlayerSegmentClient.target != null) {
			glPushMatrix()
			glColor4d(1.0, 1.0, 1.0, 1.0)
			glTranslated(event.resolution.scaledWidth.D / 2.0 / s - 120, 0.0, 0.0)
			zLevel = -80f
			l = PlayerSegmentClient.target
			var hp = min(l!!.health, l!!.maxHealth)
			var hpm = l!!.maxHealth
			var col = -0x222223 // bg color
			var st = false
			var shadow = true
			
			// ################ colors: ################
			run {
				color = yellow
				
				if (l is EntityPlayer) {
					color = 0xFFFFFF
					col = (l as EntityPlayer).race.rgbColor
				}
				if (l is INpc) {
					color = -0xff5501
					col = color
					//shadow = false;
				}
				if (PlayerSegmentClient.target is IBossDisplayData) {
					color = 0xA2018C
					col = color
				}
				color = if (PlayerSegmentClient.isParty) 0x00FF00 else color
				if (!l!!.isEntityAlive) {
					color = 0x444444
					hpm = 0f
					hp = hpm
					st = true
				}
			}
			
			// ################ hp bg: ################
			mc.renderEngine.bindTexture(LibResourceLocations.health)
			drawTexturedModalRect(0, 0, 0, 160, 240, 50)
			// ################ ava bg: ################
			ASJRenderHelper.glColor1u(ASJRenderHelper.addAlpha(col, 255))
			drawTexturedModalRect(0, 2, 0, 210, 34, 48)
			
			// ################ health: ################
			run health@{
				if (!l!!.isEntityAlive) return@health
				
				val mod = (hp / max(hpm, 1f) * 200.0).I / 200.0
				ASJRenderHelper.glColor1u(if (mod > 0.5) green else if (mod > 0.1) yellow else red)
				val length = (200 * mod).mfloor()
				
				when {
					length >= 107 -> {
						drawTexturedModalRect(34, 2, 34, 210, 100, 48)
						drawTexturedModalRect(134, 2, 134 + (100 - (length - 100)), 210, length - 100, 48)
					}
					
					length >= 14  -> {
						drawTexturedModalRect(35, 2, 35 + (100 - (length - 7)), 210, length - 7, 48)
						drawTexturedModalRect(34 + length - 7, 2, 227, 210, 7, 48)
						drawTexturedModalRect(34, 2, 34, 210, 1, 30)
					}
					
					length >= 7   -> {
						drawTexturedModalRect(35, 2, 35 + (100 - (length - 7)), 210, length - 7, 48)
						drawTexturedModalRect(34 + length - 7, 2, 227, 210, 7, 48)
						drawTexturedModalRect(34, 2, 34, 210, 1, 30 - (14 - length))
					}
					
					else          -> {
						drawTexturedModalRect(34, 2, 227 + (7 - length), 210, length, 48)
						drawTexturedModalRect(34, 2, 34, 210, 1, 30 - (14 - length))
					}
				}
				
				glColor4d(1.0, 1.0, 1.0, 1.0)
			}
			
			// ################ name: ################
			run {
				data = l!!.commandSenderName
				
				var j = 0
				while (j < data.length && shadow) {
					val c = data[j]
					if ("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(c) == -1) {
						shadow = false
					}
					j++
				}
				
				if (st) data = EnumChatFormatting.STRIKETHROUGH.toString() + data
				
				glTranslated(0.0, 0.0, -79.0)
				font.drawString(data, 36, 6, color, shadow)
				font.drawString(String.format("(%.2fm)", Vector3.entityDistance(player, PlayerSegmentClient.target!!)), 128, 6, color, shadow)
				glTranslated(0.0, 0.0, 79.0)
			}
			
			// ################ potions: ################
			run potions@{
				@Suppress("UNCHECKED_CAST")
				val pes = l!!.activePotionEffects as Collection<PotionEffect>
				if (pes.isEmpty()) return@potions
				
				glPushMatrix()
				val s2 = 0.5
				glScaled(s2)
				glColor4d(1.0, 1.0, 1.0, 1.0)
				
				var bads = 0
				var goods = 0
				for (pe in pes) if (Potion.potionTypes[pe.potionID].isBadEffect) ++bads else ++goods
				
				glTranslated(274.0, 56.0, 0.0)
				renderPotions(PlayerSegmentClient.target!!, bads, true, 9)
				glTranslated(-198.0, 22.0, 0.0)
				renderPotions(PlayerSegmentClient.target!!, goods, false, 20)
				
				glPopMatrix()
			}
			
			glColor4d(1.0, 1.0, 1.0, 1.0)
			glPopMatrix()
		}
		
		// ################ icon: ################
		run {
			var y = 0
			zLevel = -80f
			glMatrixMode(GL_TEXTURE)
			glPushMatrix()
			glScaled(512.0 / 464, 512.0 / 464, 1.0)
			glTranslated(-1.0 / 24, -1.0 / 24, 0.0)
			glMatrixMode(GL_MODELVIEW)
			
			if (AlfheimConfigHandler.selfHealthUI) {
				mc.textureManager.bindTexture(RenderWings.getPlayerIconTexture(player))
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER)
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER)
				glColor4d(1.0, 1.0, 1.0, 0.5)
				Tessellator.instance.startDrawingQuads()
				Tessellator.instance.addVertexWithUV(4.0, 4.0, 0.0, 0.0, 0.0)
				Tessellator.instance.addVertexWithUV(4.0, 36.0, 0.0, 0.0, 1.0)
				Tessellator.instance.addVertexWithUV(36.0, 36.0, 0.0, 1.0, 1.0)
				Tessellator.instance.addVertexWithUV(36.0, 4.0, 0.0, 1.0, 0.0)
				Tessellator.instance.draw()
				
				if (ShaderHelper.useShaders()) ASJShaderHelper.useShader(LibShaderIDs.idShadow)
				
				val mod = if (mc.thePlayer.race == EnumRace.HUMAN) 1.0 else mc.thePlayer.flight.mfloor() / ElvenFlightHelper.max
				val time = sin((mc.theWorld.totalWorldTime / 2).D) * 0.5
				glColor4d(1.0, 1.0, 1.0, if (mc.thePlayer.capabilities.isFlying) if (mod > 0.1) time + 0.5 else time else 1.0)
				
				Tessellator.instance.startDrawingQuads()
				Tessellator.instance.addVertexWithUV(4.0, 36 - mod * 32, 0.0, 0.0, 1 - mod)
				Tessellator.instance.addVertexWithUV(4.0, 36.0, 0.0, 0.0, 1.0)
				Tessellator.instance.addVertexWithUV(36.0, 36.0, 0.0, 1.0, 1.0)
				Tessellator.instance.addVertexWithUV(36.0, 36 - mod * 32, 0.0, 1.0, 1 - mod)
				Tessellator.instance.draw()
			} else {
				if (ShaderHelper.useShaders()) ASJShaderHelper.useShader(LibShaderIDs.idShadow)
			}
			
			y += 20
			
			for (i in 0 until pt.count) {
				l = pt[i]
				if (l === mc.thePlayer) continue
				
				run icon@{
					y += 40
					glColor4f(1f, 1f, 1f, if (l == null) 0.9f else 1f)
					
					glPushMatrix()
					glTranslated(4.0, y.D, 0.0)
					mc.textureManager.bindTexture(LibResourceLocations.icons[pt.getType(i)])
					glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER)
					glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER)
					Tessellator.instance.startDrawingQuads()
					Tessellator.instance.addVertexWithUV(0.0, 0.0, 0.0, 0.0, 0.0)
					Tessellator.instance.addVertexWithUV(0.0, 28.0, 0.0, 0.0, 1.0)
					Tessellator.instance.addVertexWithUV(28.0, 28.0, 0.0, 1.0, 1.0)
					Tessellator.instance.addVertexWithUV(28.0, 0.0, 0.0, 1.0, 0.0)
					Tessellator.instance.draw()
					glPopMatrix()
				}
			}
			
			glColor4f(1f, 1f, 1f, 1f)
			
			run tg_icon@{
				l = PlayerSegmentClient.target
				if (l == null) return@tg_icon
				if (!AlfheimConfigHandler.targetUI) return@tg_icon
				
				glPushMatrix()
				glTranslated(event.resolution.scaledWidth.D / 2.0 / s - 116, 11.0, 0.0)
				mc.textureManager.bindTexture(if (l is EntityPlayer) RenderWings.getPlayerIconTexture(l as EntityPlayer) else if (l is IBossDisplayData) LibResourceLocations.icons[LibResourceLocations.BOSS] else if (l is INpc) LibResourceLocations.icons[LibResourceLocations.NPC] else LibResourceLocations.icons[LibResourceLocations.MOB])
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER)
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER)
				Tessellator.instance.startDrawingQuads()
				Tessellator.instance.addVertexWithUV(0.0, 0.0, 0.0, 0.0, 0.0)
				Tessellator.instance.addVertexWithUV(0.0, 28.0, 0.0, 0.0, 1.0)
				Tessellator.instance.addVertexWithUV(28.0, 28.0, 0.0, 1.0, 1.0)
				Tessellator.instance.addVertexWithUV(28.0, 0.0, 0.0, 1.0, 0.0)
				Tessellator.instance.draw()
				glPopMatrix()
			}
			
			glMatrixMode(GL_TEXTURE)
			glPopMatrix()
			glMatrixMode(GL_MODELVIEW)
			
			if (ShaderHelper.useShaders()) ASJShaderHelper.releaseShader()
		}
		
		glDisable(GL_BLEND)
		glPopMatrix()
	}
	
	fun renderPotions(e: EntityLivingBase, count: Int, bads: Boolean?, maxCount: Int) {
		if (count < 1) return
		
		var potion: Potion
		var j = 0.0
		val maxSpace = maxCount * 18.0
		val k = if (count > maxCount) maxSpace / (count - 1) else 18.0
		
		for (o in e.activePotionEffects) {
			val pe = o as PotionEffect
			potion = Potion.potionTypes[pe.getPotionID()]
			if (bads != null && bads != potion.isBadEffect) continue
			
			if (potion.hasStatusIcon()) {
				glDisable(GL_BLEND)
				glColor4f(if (potion.isBadEffect) 1f else 0f, if (potion.isBadEffect) 0f else 1f, 0f, 1f)
				mc.textureManager.bindTexture(LibResourceLocations.widgets)
				drawTexturedModalRect(j, 0.0, 1.0, 1.0, 20.0, 20.0)
				glEnable(GL_BLEND)
				glColor4f(1f, 1f, 1f, if (pe.duration < 100) sin(e.ticksExisted / 2f) / 2 + 0.75f else 1f)
				mc.textureManager.bindTexture(LibResourceLocations.inventory)
				val l = potion.statusIconIndex
				drawTexturedModalRect(j + 1, 1.0, (l % 8 * 18).D, (198 + l / 8 * 18).D, 18.0, 18.0)
				j += k
			}
		}
		
		glColor4f(1f, 1f, 1f, 1f)
	}
	
	fun drawTexturedModalRect(x: Double, y: Double, u: Double, v: Double, width: Double, height: Double) {
		val f = 0.00390625f
		val f1 = 0.00390625f
		Tessellator.instance.startDrawingQuads()
		Tessellator.instance.addVertexWithUV(x, y + height, zLevel.D, u * f, (v + height) * f1)
		Tessellator.instance.addVertexWithUV(x + width, y + height, zLevel.D, (u + width) * f, (v + height) * f1)
		Tessellator.instance.addVertexWithUV(x + width, y, zLevel.D, (u + width) * f, v * f1)
		Tessellator.instance.addVertexWithUV(x, y, zLevel.D, u * f, v * f1)
		Tessellator.instance.draw()
	}
}