package alfheim.common.world.dim.niflheim.customgens

import alexsocol.asjlib.*
import alfheim.common.block.AlfheimBlocks
import alfheim.common.world.dim.niflheim.ChunkProviderNiflheim
import alfheim.common.world.dim.niflheim.biome.BiomeGenSnow
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import net.minecraft.world.gen.MapGenBase
import java.util.*

// I'm not going to prettify this
object MapGenCustomRavine: MapGenBase() {
	
	private val field_75046_d = FloatArray(1024)
	
	fun generateRavine(par1: Long, par3: Int, par4: Int, blocks: Array<Block>, par6: Double, par8: Double, par10: Double, par12: Float, par13: Float, par14: Float, par15: Int, par16: Int, par17: Double) {
		var par6 = par6
		var par8 = par8
		var par10 = par10
		var par13 = par13
		var par14 = par14
		var par15 = par15
		var par16 = par16
		val var19 = Random(par1)
		val var20 = (par3 * 16 + 8).D
		val var22 = (par4 * 16 + 8).D
		var var24 = 0f
		var var25 = 0f
		if (par16 <= 0) {
			val var54 = range * 16 - 16
			par16 = var54 - var19.nextInt(var54 / 4)
		}
		var var61 = false
		if (par15 == -1) {
			par15 = par16 / 2
			var61 = true
		}
		var var27 = 1f
		for (var53 in 0..127) {
			if (var53 == 0 || var19.nextInt(3) == 0) {
				var27 = 1f + var19.nextFloat() * var19.nextFloat() * 1f
			}
			field_75046_d[var53] = var27 * var27
		}
		while (par15 < par16) {
			var var62 = 1.5 + (MathHelper.sin(par15.F * 3.1415927f / par16.F) * par12 * 1f).D
			var var30 = var62 * par17
			var62 *= var19.nextFloat().D * 0.25 + 0.75
			var30 *= var19.nextFloat().D * 0.25 + 0.75
			val var32 = MathHelper.cos(par14)
			val var33 = MathHelper.sin(par14)
			par6 += (MathHelper.cos(par13) * var32).D
			par8 += var33.D
			par10 += (MathHelper.sin(par13) * var32).D
			par14 *= 0.7f
			par14 += var25 * 0.05f
			par13 += var24 * 0.05f
			var25 *= 0.8f
			var24 *= 0.5f
			var25 += (var19.nextFloat() - var19.nextFloat()) * var19.nextFloat() * 2f
			var24 += (var19.nextFloat() - var19.nextFloat()) * var19.nextFloat() * 4f
			if (var61 || var19.nextInt(4) != 0) {
				val var34 = par6 - var20
				val var36 = par10 - var22
				val var38 = (par16 - par15).D
				val var40 = (par12 + 2f + 16f).D
				if (var34 * var34 + var36 * var36 - var38 * var38 > var40 * var40) {
					return
				}
				if (par6 >= var20 - 16.0 - var62 * 2.0 && par10 >= var22 - 16.0 - var62 * 2.0 && par6 <= var20 + 16.0 + var62 * 2.0 && par10 <= var22 + 16.0 + var62 * 2.0) {
					var var56 = MathHelper.floor_double(par6 - var62) - par3 * 16 - 1
					var var35 = MathHelper.floor_double(par6 + var62) - par3 * 16 + 1
					var var55 = MathHelper.floor_double(par8 - var30) - 1
					var var37 = MathHelper.floor_double(par8 + var30) + 1
					var var57 = MathHelper.floor_double(par10 - var62) - par4 * 16 - 1
					var var39 = MathHelper.floor_double(par10 + var62) - par4 * 16 + 1
					if (var56 < 0) {
						var56 = 0
					}
					if (var35 > 16) {
						var35 = 16
					}
					if (var55 < 1) {
						var55 = 1
					}
					if (var37 > 120) {
						var37 = 120
					}
					if (var57 < 0) {
						var57 = 0
					}
					if (var39 > 16) {
						var39 = 16
					}
					var var58 = false
					var var41: Int
					var var44: Int
					var41 = var56
					while (!var58 && var41 < var35) {
						var var59 = var57
						while (!var58 && var59 < var39) {
							var var43 = var37 + 1
							while (!var58 && var43 >= var55 - 1) {
								var44 = (var41 * 16 + var59) * 128 + var43
								if (var43 < 128) {
									if (blocks[var44] === Blocks.flowing_water || blocks[var44] === Blocks.water) {
										var58 = true
									}
									if (var43 != var55 - 1 && var41 != var56 && var41 != var35 - 1 && var59 != var57 && var59 != var39 - 1) {
										var43 = var55
									}
								}
								--var43
							}
							++var59
						}
						++var41
					}
					if (!var58) {
						var41 = var56
						while (var41 < var35) {
							val var63 = ((var41 + par3 * 16).D + 0.5 - par6) / var62
							var44 = var57
							while (var44 < var39) {
								val var45 = ((var44 + par4 * 16).D + 0.5 - par10) / var62
								var var47 = (var41 * 16 + var44) * 128 + var37
								if (var63 * var63 + var45 * var45 < 1.0) {
									for (var49 in var37 - 1 downTo var55) {
										val var50 = (var49.D + 0.5 - par8) / var30
										if ((var63 * var63 + var45 * var45) * field_75046_d[var49].D + var50 * var50 / 6.0 < 1.0) {
											val var52 = blocks[var47]
											if (var52 inl ChunkProviderNiflheim.surfaceBlocks) {
												if (var49 < 10) {
													if (worldObj.getBiomeGenForCoords(var41 + par3 * 16, var44 + par4 * 16) is BiomeGenSnow) {
														blocks[var47] = Blocks.snow
													} else {
														blocks[var47] = AlfheimBlocks.poisonIce
													}
												} else {
													blocks[var47] = Blocks.air
												}
											}
										}
										--var47
									}
								}
								++var44
							}
							++var41
						}
						if (var61) {
							break
						}
					}
				}
			}
			++par15
		}
	}
	
	override fun func_151538_a(p_151538_1_: World, par2: Int, par3: Int, par4: Int, par5: Int, blocks: Array<Block>) {
		if (rand.nextInt(50) == 0) {
			val var7 = (par2 * 16 + rand.nextInt(16)).D
			val var9 = (rand.nextInt(rand.nextInt(40) + 8) + 20).D
			val var11 = (par3 * 16 + rand.nextInt(16)).D
			val var15 = rand.nextFloat() * 3.1415927f * 2f
			val var16 = (rand.nextFloat() - 0.5f) * 2f / 8f
			val var17 = (rand.nextFloat() * 2f + rand.nextFloat()) * 2f
			generateRavine(rand.nextLong(), par4, par5, blocks, var7, var9, var11, var17, var15, var16, 0, 0, 3.0)
		}
	}
}