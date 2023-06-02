package alfheim.client.render.world

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.AlfheimCore
import alfheim.api.ModInfo
import alfheim.api.entity.*
import alfheim.client.gui.GUIDeathTimer
import alfheim.client.render.entity.RenderEntityThrym
import alfheim.client.render.particle.EntityTornadoFX
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects.*
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.tile.*
import alfheim.common.block.tile.sub.anomaly.SubTileManaVoid
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.entity.boss.primal.EntityPrimalBoss
import alfheim.common.item.AlfheimItems
import alfheim.common.item.rod.ItemRodInterdiction
import alfheim.common.spell.illusion.SpellSmokeScreen
import alfheim.common.spell.water.*
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityFallingBlock
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.world.World
import vazkii.botania.client.fx.FXWisp
import vazkii.botania.common.Botania
import vazkii.botania.common.entity.EntityManaBurst
import java.awt.Color
import kotlin.math.*
import vazkii.botania.common.core.helper.Vector3 as Bector3

object VisualEffectHandlerClient {
	
	val activeEmblems = HashMap<Int, Boolean>()
	val v = Vector3()
	val b = Bector3()
	
	fun select(s: VisualEffects, d: DoubleArray) {
		if (mc.theWorld == null) return
		
		when (s) {
			ACID               -> spawnAcid(d[0], d[1], d[2])
			AQUABIND           -> spawnAquaBind(d[0], d[1], d[2])
			AQUASTREAM_HIT     -> spawnAquaStreamHit(d[0], d[1], d[2])
			BIFROST            -> spawnBifrost(d[0], d[1], d[2])
			BIFROST_DONE       -> spawnBifrostFinish(d[0], d[1], d[2], d[3].I)
			CREATION           -> TileAlfheimPylon.doCreationParticles(d[0], d[1], d[2], d[3].I)
			DISPEL             -> spawnBurst(d[0], d[1], d[2], 1f, 0f, 0f)
			ECHO               -> spawnEcho(d[0], d[1], d[2])
			ECHO_ENTITY        -> spawnEchoEntity(d[0], d[1], d[2])
			ECHO_ITEM          -> spawnEchoItem(d[0], d[1], d[2])
			ECHO_MOB           -> spawnEchoMob(d[0], d[1], d[2])
			ECHO_PLAYER        -> spawnEchoPlayer(d[0], d[1], d[2])
			EMBLEM_ACTIVATION  -> activateEmblem(d[0], d[1])
			EXPL               -> spawnExplosion(d[0], d[1], d[2])
			FALLING            -> spawnFalling(d[0].I, d[1].I, d[2].I, d[3])
			FEATHER            -> spawnFeather(d[0], d[1], d[2], d[3], d[4], d[5], d[6].I, d[7].F, d[8].F, d[9].F)
			FENRIR_AREA        -> FenrirVisualEffectsRenderer.addArea(d[0], d[1], d[2], d.getOrNull(3)?.I ?: 50)
			FENRIR_AREA_END    -> spawnFenrirAreaEnd(d[0], d[1], d[2])
			FENRIR_DASH        -> spawnFenrirDash(d[0].I, d[1], d[2])
			FLAMESTAR          -> spawnFlameStar(d[0], d[1], d[2], d[3], d[4], d[5], d[6].F)
			GAIA_SOUL          -> spawnGaiaSoul(d[0], d[1], d[2])
			GRAVITY            -> spawnGravity(d[0], d[1], d[2], d[3], d[4], d[5])
			GUNGNIR            -> spawnGungnir(d[0].I, d[1].I)
			HEAL               -> spawnBurst(d[0], d[1], d[2], 0f, 1f, 0f)
			HORN               -> horn(d[0], d[1], d[2])
			ICELENS            -> addIceLens()
			ICONCRACK          -> spawnIconCrack(d[0], d[1], d[2], d[3].I, d[4].I)
			LIGHTNING          -> spawnLightning(d[0], d[1], d[2], d[3], d[4], d[5], d[6].F, d[7].I, d[8].I, d.getOrElse(9) { 1.0 }.I)
			MANA               -> addMana(d[0], d[1].I)
			MANABURST          -> spawnManaburst(d[0], d[1], d[2])
			MANAVOID           -> spawnManaVoid(d[0], d[1], d[2], d[3], d[4], d[5])
			MIST               -> spawnMist(d[0], d[1], d[2])
			MOON               -> moonBoom(d[0], d[1], d[2])
			NOTE               -> spawnNote(d[0], d[1], d[2])
			NVISION            -> spawnBurst(d[0], d[1], d[2], 0f, 0f, 1f)
			POTION             -> spawnPotion(d[0], d[1], d[2], d[3].I, d[4] == 1.0)
			PRIMAL_BOSS_ATTACK -> spawnPrimalBossHandEffect(mc.theWorld.getEntityByID(d[0].I) as? EntityPrimalBoss ?: return)
			PURE               -> spawnBurst(d[0], d[1], d[2], 0f, 0.75f, 1f)
			PURE_AREA          -> spawnPure(d[0], d[1], d[2])
			QUAD               -> quadDamage()
			QUADH              -> quadHurt()
			SEAROD             -> (AlfheimItems.rodInterdiction as ItemRodInterdiction).particleRing(mc.theWorld, d[0], d[1], d[2], d[3].I, d[4].F, d[5].F, d[6].F)
			SHADOW             -> spawnBurst(d[0], d[1], d[2], 0.75f, 0.75f, 0.75f)
			SMOKE              -> spawnSmoke(d[0], d[1], d[2])
			SNICE_MARK         -> spawnSniceMark(d[0], d[1], d[2])
			SPLASH             -> spawnSplash(d[0], d[1], d[2])
			SURTRWALL          -> spawnSurtrWall(d[0], d[1], d[2], d[3])
			THROW              -> spawnThrow(d[0], d[1], d[2], d[3], d[4], d[5])
			THRYM_DOME         -> RenderEntityThrym.domes[Vector3(d[0], d[1], d[2])] = mc.theWorld.totalWorldTime + 20
			TREMORS            -> spawnTremors(d[0], d[1], d[2])
			UPHEAL             -> spawnBurst(d[0], d[1], d[2], 1f, 0.75f, 0f)
			WIRE               -> spawnWire(d[0], d[1], d[2], d[3])
			WISP               -> spawnWisp(d[0], d[1], d[2], d[3].F, d[4].F, d[5].F, d[6].F, d[7].F, d[8].F, d[9].F, d[10].F, d.getOrNull(11) == null)
			WHIRL              -> spawnWhirl(d[0], d[1], d[2], d[3].I)
		}
	}
	
	fun activateEmblem(eID: Double, active: Double) {
		activeEmblems[eID.I] = active != 0.0
	}
	
	fun addIceLens() {
		mc.thePlayer.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDIceLens, SpellIceLens.duration))
	}
	
	fun addMana(enID: Double, mana: Int) {
		val e = mc.theWorld.getEntityByID(enID.I) as? EntityPlayer ?: return
		
		if (mana == 0 || mana.I == Int.MAX_VALUE) {
			var d = 0.0
			while (d < 1.0) {
				spawnBurst(e.posX, e.posY + d, e.posZ, 0.975f, if (mana == 0) 0.1f else 0.85f, 0.1f)
				d += .2
			}
		} else
			e.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDShowMana, mana, 100))
	}
	
	fun horn(x: Double, y: Double, z: Double) {
		mc.theWorld.playSound(x, y, z, "${ModInfo.MODID}:horn.bhorn", 100f, 0.8f + mc.theWorld.rand.nextFloat() * 0.2f, false)
	}
	
	fun moonBoom(x: Double, y: Double, z: Double) {
		for (i in 0..63) {
			v.rand().sub(0.5).normalize().mul(0.1)
			Botania.proxy.wispFX(mc.theWorld, x, y, z, 0.3f + (Math.random().F - 0.5f) * 0.1f, 0.85f + (Math.random().F - 0.5f) * 0.1f, 0.85f + (Math.random().F - 0.5f) * 0.1f, 0.5f, v.x.F, v.y.F, v.z.F)
		}
	}
	
	fun quadDamage() {
		mc.thePlayer.playSound(ModInfo.MODID + ":quad", 10f, 1f)
	}
	
	fun quadHurt() {
		mc.thePlayer.playSound(ModInfo.MODID + ":quadh", 1f, 1f)
	}
	
	fun spawnAcid(x: Double, y: Double, z: Double) {
		for (i in 0..255) {
			v.set(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).normalize().mul(Math.random() * 9, Math.random() * 9, Math.random() * 9)
			Botania.proxy.wispFX(mc.theWorld, x + v.x, y + v.y, z + v.z, (Math.random() * 0.2).F, 1f, 0f, 2f, 0f, 2f)
		}
	}
	
	fun spawnAquaBind(x: Double, y: Double, z: Double) {
		var j = 0
		while (j < 360) {
			val i = cos(j.D) * SpellAquaBind.radius
			val k = sin(j.D) * SpellAquaBind.radius
			Botania.proxy.wispFX(mc.theWorld, x + i, y, z + k, 0f, 0.5f, 1f, 0.5f)
			j += 5
		}
	}
	
	fun spawnWisp(x: Double, y: Double, z: Double, red: Float, green: Float, blue: Float, size: Float, mx: Float, my: Float, mz: Float, age: Float, depth: Boolean) {
		Botania.proxy.setWispFXDepthTest(depth)
		Botania.proxy.wispFX(mc.theWorld, x, y, z, red, green, blue, size, mx, my, mz, age)
		Botania.proxy.setWispFXDepthTest(true)
	}
	
	fun spawnAquaStreamHit(x: Double, y: Double, z: Double) {
		Botania.proxy.wispFX(mc.theWorld, x, y, z, 0f, 0.5f, 1f, 0.5f)
	}
	
	fun spawnBifrost(x: Double, y: Double, z: Double) {
		for (a in 0..15) {
			val color = Color(Color.HSBtoRGB(mc.theWorld.rand.nextFloat(), 1F, 1F))
			Botania.proxy.wispFX(mc.theWorld, x + Math.random() * 3 - 1, y + Math.random(), z + Math.random() * 3 - 1, color.red / 255f, color.green / 255f, color.blue / 255f, 1f, -1f, 2f)
		}
	}
	
	fun spawnBifrostFinish(x: Double, y: Double, z: Double, id: Int) {
		mc.theWorld.getEntityByID(id)?.let { it.motionY += 5.0 }
		
		for (a in 0..64) {
			v.rand().normalize().sub(0.5).mul(0.2)
			val color = Color(Color.HSBtoRGB(mc.theWorld.rand.nextFloat(), 1F, 1F))
			Botania.proxy.wispFX(mc.theWorld, x, y, z, color.red / 255f, color.green / 255f, color.blue / 255f, 1f, v.x.F, v.y.F, v.z.F, 2f)
		}
	}
	
	fun spawnBurst(x: Double, y: Double, z: Double, r: Float, g: Float, b: Float) {
		for (i in 0..7) Botania.proxy.wispFX(mc.theWorld, x + Math.random() - 0.5, y + Math.random() * 0.25, z + Math.random() - 0.5, r, g, b, 1f, (Math.random() * -0.075).F)
	}
	
	fun spawnEcho(x: Double, y: Double, z: Double) {
		for (i in 0..63) {
			v.set(Math.random() - 0.5, 0.0, Math.random() - 0.5).normalize().mul(0.5)
			Botania.proxy.wispFX(mc.theWorld, x, y + 0.2, z, 0.5f, 0.5f, 0.5f, 1f, v.x.F, 0f, v.z.F)
		}
	}
	
	fun spawnEchoEntity(x: Double, y: Double, z: Double) {
		Botania.proxy.setWispFXDepthTest(false)
		Botania.proxy.wispFX(mc.theWorld, x, y + 0.2, z, 1f, 1f, 0f, 1f, 0f, 3f)
		Botania.proxy.setWispFXDepthTest(true)
	}
	
	fun spawnEchoItem(x: Double, y: Double, z: Double) {
		Botania.proxy.setWispFXDepthTest(false)
		Botania.proxy.wispFX(mc.theWorld, x, y + 0.2, z, 0f, 1f, 0f, 1f, 0f, 3f)
		Botania.proxy.setWispFXDepthTest(true)
	}
	
	fun spawnEchoMob(x: Double, y: Double, z: Double) {
		Botania.proxy.setWispFXDepthTest(false)
		Botania.proxy.wispFX(mc.theWorld, x, y + 0.2, z, 1f, 0f, 0f, 1f, 0f, 3f)
		Botania.proxy.setWispFXDepthTest(true)
	}
	
	fun spawnEchoPlayer(x: Double, y: Double, z: Double) {
		Botania.proxy.setWispFXDepthTest(false)
		Botania.proxy.wispFX(mc.theWorld, x, y + 0.2, z, 0f, 0f, 1f, 1f, 0f, 3f)
		Botania.proxy.setWispFXDepthTest(true)
	}
	
	fun spawnExplosion(x: Double, y: Double, z: Double) {
		mc.theWorld.spawnParticle("largeexplode", x, y, z, 1.0, 0.0, 0.0)
		
		for (i in 0..31) {
			v.set(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).normalize().mul(0.15)
			Botania.proxy.wispFX(mc.theWorld, x, y, z, 1f, Math.random().F * 0.25f, Math.random().F * 0.075f, 0.25f + Math.random().F * 0.45f, v.x.F, v.y.F, v.z.F, 0.5f)
		}
	}
	
	fun spawnFalling(x: Int, y: Int, z: Int, radius: Double) {
		val iradius = (radius + 1).I
		for (i in 0 until iradius * 2 + 1) {
			for (j in 0 until iradius * 2 + 1) {
				val xp: Int = x + i - iradius
				val zp: Int = z + j - iradius
				
				if (floor(Vector3.pointDistancePlane(xp.D, zp.D, x.D, z.D)).I == iradius - 1) {
					val block = mc.theWorld.getBlock(xp, y, zp)
					val meta = mc.theWorld.getBlockMetadata(xp, y, zp)
					
					EntityFallingBlock(mc.theWorld, xp.D + 0.5, y.D, zp.D + 0.5, block, meta).apply { motionY += 0.5; noClip = true }.spawn()
				}
			}
		}
	}
	
	fun spawnFeather(x: Double, y: Double, z: Double, mx: Double, my: Double, mz: Double, color: Int, size: Float, life: Float, distance: Float) {
		AlfheimCore.proxy.featherFX(mc.theWorld, x, y, z, color, size, life, distance, false, mx, my, mz)
	}
	
	fun spawnFenrirAreaEnd(x: Double, y: Double, z: Double) {
		for (i in 0..256) {
			v.rand().sub(0.5).mul(1, 0, 1).normalize()
			val (mx, _, mz) = v.mul(Math.random() * 0.5 + 0.5)
			AlfheimCore.proxy.sparkleFX(mc.theWorld, x, y, z, 0.25f, 1f, 1f, 5f, 5, mx, Math.random() * 0.5 + 0.5, mz, noclip = true)
		}
	}
	
	fun spawnFenrirDash(id: Int, mx: Double, mz: Double) {
		val fenrir = mc.theWorld.getEntityByID(id) ?: return
		val (x, y, z) = Vector3.fromEntity(fenrir)
		
		for (i in 0..100)
			for (j in 0..40) {
				val (a, b, c) = v.set(mx * 1.5, 0, mz * 1.5).mul(i / 100f).add(x, y + 0.1, z).add(Math.random() * 4 - 2, 0, Math.random() * 4 - 2)
				Botania.proxy.sparkleFX(mc.theWorld, a, b, c, 0.25f, 1f, 1f, 5f, 5)
			}
	}
	
	fun spawnFlameStar(x: Double, y: Double, z: Double, r: Double, g: Double, b: Double, size: Float) {
		Botania.proxy.sparkleFX(mc.theWorld, x, y, z, r.F, g.F, b.F, size, 5)
	}
	
	fun spawnGaiaSoul(x: Double, y: Double, z: Double) {
		(mc.theWorld.getTileEntity(x.I, y.I, z.I) as? TileManaInfuser)?.soulParticlesTime = 20
	}
	
	fun spawnGravity(x: Double, y: Double, z: Double, x2: Double, y2: Double, z2: Double) {
		mc.theWorld.spawnParticle("smoke", x, y, z, x2, y2, z2)
	}
	
	fun spawnGungnir(shooterID: Int, targetID: Int) {
		val player = mc.theWorld.getEntityByID(shooterID) as? EntityPlayer ?: return
		val target = mc.theWorld.getEntityByID(targetID)
		
		if (target != null)
			ASJUtilities.faceEntity(player, target, 360f, 360f)
		
		EntityManaBurst(player).apply {
			val motionModifier = 32
			color = 0xFFD400
			mana = 1
			startingMana = 1
			minManaLoss = 200
			manaLossPerTick = 1f
			gravity = 0f
			setMotion(motionX * motionModifier, motionY * motionModifier, motionZ * motionModifier)
		}.spawn(mc.theWorld)
		
		player.rotationYaw = player.prevRotationYaw
		player.rotationPitch = player.prevRotationPitch
	}
	
	fun spawnIconCrack(x: Double, y: Double, z: Double, id: Int, meta: Int) {
		val s = "iconcrack_$id" + if (meta != -1) "_$meta" else ""
		
		mc.theWorld.spawnParticle(s, x, y, z, 0.0, 0.0, 0.0)
	}
	
	fun spawnLightning(x: Double, y: Double, z: Double, x2: Double, y2: Double, z2: Double, speed: Float, color: Int, color2: Int, count: Int) {
		for (i in 0 until count)
			Botania.proxy.lightningFX(mc.theWorld, b.set(x, y, z), Bector3(x2, y2, z2), speed, color, color2)
	}
	
	fun spawnMana(living: EntityLivingBase, mana: Double) {
		val d = Math.random() * mana
		v.set(Math.random() - 0.5, 0.0, Math.random() - 0.5).normalize().mul(Math.random()).mul(1.0 * (mana * 0.25) - d / mana * (mana * 2.0 / 7.0)).add(0.0, d, 0.0)
		Botania.proxy.wispFX(mc.theWorld, living.posX + v.x, living.posY + v.y, living.posZ + v.z,
							 0.025f, 0.15f, 0.9f, (Math.random() * (mana * 0.5) + 0.5).F,
							 0f, (Math.random() * 2.0 + 1.0).F)
	}
	
	fun spawnManaburst(x: Double, y: Double, z: Double) {
		for (i in 0..127) {
			v.rand().sub(0.5).normalize().mul(Math.random() * 0.1)
			Botania.proxy.wispFX(mc.theWorld, x + 0.5, y + 0.5, z + 0.5, 0.01f, 0.75f, 1f, 0.25f, v.x.F, v.y.F, v.z.F, 2f)
		}
	}
	
	fun spawnManaVoid(x: Double, y: Double, z: Double, x2: Double, y2: Double, z2: Double) {
		val l = v.set(x, y, z).add(0.5).sub(x2, y2, z2).length()
		v.normalize().mul(l / 40)
		Botania.proxy.wispFX(mc.theWorld, x2, y2, z2, 0.01f, 0.75f, 1f, SubTileManaVoid.radius / 40f, v.x.F, v.y.F, v.z.F, 2f)
	}
	
	fun spawnMist(x: Double, y: Double, z: Double) {
		for (`_` in 0..5) {
			val sizeFactor = 6
			val color = 0.025f
			val (i, j, k) = v.rand().sub(0.5).normalize().mul(Math.random() * 10, Math.random() * 6, Math.random() * 10)
			wispFX(mc.theWorld, x + i, y + j, z + k, color / 2, color / 2, color, Math.random().F * sizeFactor + 1, 0f, 0f, 0f, 100)
		}
	}
	
	fun wispFX(world: World?, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float, size: Float, motionx: Float, motiony: Float, motionz: Float, maxAge: Int) {
		val wisp = FXWisp(world, x, y, z, size, r, g, b, true, true, maxAge.F)
		
		wisp.particleMaxAge = maxAge
		wisp.moteHalfLife = maxAge / 2
		
		wisp.motionX = motionx.D
		wisp.motionY = motiony.D
		wisp.motionZ = motionz.D
		
		mc.effectRenderer.addEffect(wisp)
	}
	
	fun spawnNote(x: Double, y: Double, z: Double) {
		mc.theWorld.spawnParticle("note", x, y, z, mc.theWorld.rand.nextInt(25) / 24.0, 0.0, 0.0)
	}
	
	fun spawnPotion(x: Double, y: Double, z: Double, color: Int, insta: Boolean) {
		val worldObj = mc.theWorld
		val rand = worldObj.rand
		
		for (acc in worldObj.worldAccesses) {
			if (acc !is RenderGlobal) continue
			
			val s = "iconcrack_${AlfheimItems.splashPotion.id}_0"
			
			for (i in 0..8) worldObj.spawnParticle(s, x, y, z, rand.nextGaussian() * 0.15, rand.nextDouble() * 0.2, rand.nextGaussian() * 0.15)
			
			val f = (color shr 16 and 255).F / 255f
			val f1 = (color shr 8 and 255).F / 255f
			val f2 = (color shr 0 and 255).F / 255f
			val s1 = if (insta) "instantSpell" else "spell"
			
			for (l2 in 1..100) {
				val d4 = rand.nextDouble() * 4.0
				val d13 = rand.nextDouble() * Math.PI * 2.0
				val d5 = cos(d13) * d4
				val d6 = 0.01 + rand.nextDouble() * 0.5
				val d7 = sin(d13) * d4
				
				val entityfx = acc.doSpawnParticle(s1, x + d5 * 0.1, y + 0.3, z + d7 * 0.1, d5, d6, d7) ?: continue
				
				val f4 = 0.75f + rand.nextFloat() * 0.25f
				entityfx.setRBGColorF(f * f4, f1 * f4, f2 * f4)
				entityfx.multiplyVelocity(d4.F)
			}
			
			worldObj.playSound(x + 0.5, y + 0.5, z + 0.5, "game.potion.smash", 1f, worldObj.rand.nextFloat() * 0.1f + 0.9f, false)
		}
	}
	
	fun spawnPure(x: Double, y: Double, z: Double) {
		for (i in 0..63) {
			v.rand().sub(0.5).normalize().mul(SpellPurifyingSurface.radius / 25)
			Botania.proxy.wispFX(mc.theWorld, x, y + 0.2, z, 0f, 0.75f, 1f, 1f, v.x.F, 0f, v.z.F)
		}
	}
	
	fun spawnSmoke(x: Double, y: Double, z: Double) {
		for (i in 0..255) {
			v.rand().sub(0.5).normalize().mul(Math.random() * SpellSmokeScreen.radius)
			Botania.proxy.wispFX(mc.theWorld, x + v.x, y + v.y, z + v.z, 0.1f, 0.1f, 0.1f, (Math.random() * 4 + 4).F, (Math.random() * -0.075).F)
		}
	}
	
	fun spawnSniceMark(x: Double, y: Double, z: Double) {
		for (i in 0..360 step 5)
			Botania.proxy.sparkleFX(mc.theWorld, x + cos(Math.toRadians(i.D)), y - 16, z + sin(Math.toRadians(i.D)), 0.25f, 1f, 1f, 1f, 10)
	}
	
	fun spawnSplash(x: Double, y: Double, z: Double) {
		for (j in 0..31) {
			v.rand().sub(0.5, 0, 0.5).normalize().mul(Math.random() * 0.5 + 0.5).mul(0.5).mul(0.5, 2.0, 0.5)
			Botania.proxy.wispFX(mc.theWorld, x, y, z, 0.1f, 0.5f, 1f, 0.5f, v.x.F, v.y.F, v.z.F, 0.5f)
		}
	}
	
	fun spawnSurtrWall(x: Double, y: Double, z: Double, radius: Double) {
		val range = 0 until (Math.PI * radius * 2).I
		for (i in range) {
			v.rand().sub(0.5, 0, 0.5).mul(1, 0, 1).normalize().mul(radius).add(x, y + Math.random() * 7, z)
			Botania.proxy.wispFX(mc.theWorld, v.x, v.y, v.z, 0.5f, 0.25f, 0f, 1f, -0.05f, 2f)
		}
	}
	
	fun spawnThrow(x: Double, y: Double, z: Double, x2: Double, y2: Double, z2: Double) {
		for (i in 0..7)
			Botania.proxy.wispFX(mc.theWorld, x + Math.random() - 0.5, y + Math.random() * 0.25, z + Math.random() - 0.5, 0f, 1f, 0.25f, 1f, x2.F, y2.F, z2.F)
	}
	
	fun spawnPrimalBossHandEffect(host: EntityPrimalBoss) {
		val (x, y, z) = Vector3.fromEntity(host)
		val (i, _, k) = v.set(0, 0, 2).rotate(-host.rotationYawHead, Vector3.oY).rotate(host.rotationPitch, Vector3.oX)
		
		val r = if (host is INiflheimEntity) 0.0125f else 0.025f
		val b = if (host is IMuspelheimEntity) 0.0125f else 0.025f
		
		for (a in 0..7)
			Botania.proxy.wispFX(mc.theWorld, x + i + Math.random() - 0.5, y + 5 + Math.random() - 0.5, z + k + Math.random() - 0.5, r, 0.0125f, b, 1f)
	}
	
	fun spawnTremors(x: Double, y: Double, z: Double) {
		val block = mc.theWorld.getBlock(x.mfloor(), y.mfloor() - 1, z.mfloor())
		val meta = mc.theWorld.getBlockMetadata(x.mfloor(), y.mfloor() - 1, z.mfloor())
		for (i in 0..511) {
			v.set(Math.random() - 0.5, 0.0, Math.random() - 0.5).normalize().mul(Math.random() * 1.5 + 0.5).set(v.x, Math.random() * 0.25, v.z)
			mc.theWorld.spawnParticle("blockdust_${block.id}_$meta", x, y + 0.25, z, v.x, v.y, v.z)
		}
	}
	
	fun spawnWire(x: Double, y: Double, z: Double, range: Double) {
		for (i in 0..20) {
			Botania.proxy.lightningFX(mc.theWorld, b.set(x, y, z), b.add(randomVec(range)), (range * 0.01).F, 255 shl 16, 0)
		}
	}
	
	val whirlBlocks = arrayOf(
		listOf(Blocks.ice, Blocks.snow, AlfheimBlocks.poisonIce),
		listOf(Blocks.fire, Blocks.lava, AlfheimBlocks.redFlame)
	                         )
	
	fun spawnWhirl(x: Double, y: Double, z: Double, set: Int) {
		for (i in 0..90) {
			mc.effectRenderer.addEffect(EntityTornadoFX(mc.theWorld, x, y + i * 0.1, z, i * 0.1 / 3 + 1, 0.0, 0.0, whirlBlocks[set].random(mc.theWorld.rand)!!, 0, 0, 20))
		}
	}
	
	private fun randomVec(length: Double): vazkii.botania.common.core.helper.Vector3 {
		val vec = Bector3(0.0, Math.random() * length, 0.0)
		vec.rotate(Math.random() * Math.PI * 2, Bector3(1.0, 0.0, 0.0))
		vec.rotate(Math.random() * Math.PI * 2, Bector3(0.0, 0.0, 1.0))
		return vec
	}
	
	enum class VisualEffects {
		ACID, AQUABIND, AQUASTREAM_HIT, BIFROST, BIFROST_DONE, CREATION, DISPEL, ECHO, ECHO_ENTITY, ECHO_ITEM, ECHO_MOB, ECHO_PLAYER, EMBLEM_ACTIVATION, EXPL, FALLING, FEATHER, FENRIR_AREA, FENRIR_AREA_END, FENRIR_DASH, FLAMESTAR, GAIA_SOUL, GRAVITY, GUNGNIR, HEAL, HORN, ICELENS, ICONCRACK, LIGHTNING, MANA, MANABURST, MANAVOID, MIST, MOON, NOTE, NVISION, POTION, PRIMAL_BOSS_ATTACK, PURE, PURE_AREA, QUAD, QUADH, SEAROD, SHADOW, SMOKE, SNICE_MARK, SPLASH, SURTRWALL, THROW, THRYM_DOME, TREMORS, UPHEAL, WIRE, WISP, WHIRL;
	}
	
	fun onDeath(target: EntityLivingBase) {
		if (!AlfheimConfigHandler.enableMMO) return
		target.hurtTime = 0
		target.deathTime = 0
		target.attackTime = 0
		
		if (mc.thePlayer === target) {
			mc.displayGuiScreen(GUIDeathTimer())
			mc.setIngameNotInFocus()
		}
	}
	
	fun onDeathTick(target: EntityLivingBase) {
		if (!AlfheimConfigHandler.enableMMO) return
		if (target === mc.thePlayer && mc.currentScreen !is GUIDeathTimer)
			mc.displayGuiScreen(GUIDeathTimer())
		
		target.hurtTime = 0
		target.deathTime = 0
		target.attackTime = 0
		
		var c = 0xFFFFFF
		if (target is EntityPlayer) c = target.race.rgbColor
		Botania.proxy.wispFX(target.worldObj, target.posX, target.posY - if (mc.thePlayer === target) 1.5 else 0.0, target.posZ, (c shr 16 and 0xFF) / 255f, (c shr 8 and 0xFF) / 255f, (c and 0xFF) / 255f, (Math.random() * 0.5).F, (Math.random() * 0.015 - 0.0075).F, (Math.random() * 0.025).F, (Math.random() * 0.015 - 0.0075).F, 2f)
	}
}
