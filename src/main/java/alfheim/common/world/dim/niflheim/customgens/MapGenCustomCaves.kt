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
object MapGenCustomCaves: MapGenBase() {
	
	fun generateLargeCaveNode(par1: Long, par3: Int, par4: Int, arrayOfBlocks: Array<Block>, par6: Double, par8: Double, par10: Double) {
		generateCaveNode(par1, par3, par4, arrayOfBlocks, par6, par8, par10, 1f + rand.nextFloat() * 6f, 0f, 0f, -1, -1, 0.5)
	}
	
	fun generateCaveNode(par1: Long, par3: Int, par4: Int, arrayOfBlocks: Array<Block>, par6: Double, par8: Double, par10: Double, par12: Float, par13: Float, par14: Float, par15: Int, par16: Int, par17: Double) {
		var par6 = par6
		var par8 = par8
		var par10 = par10
		var par13 = par13
		var par14 = par14
		var par15 = par15
		var par16 = par16
		val var19 = (par3 * 16 + 8).D
		val var21 = (par4 * 16 + 8).D
		var var23 = 0f
		var var24 = 0f
		val var25 = Random(par1)
		if (par16 <= 0) {
			val var54 = range * 16 - 16
			par16 = var54 - var25.nextInt(var54 / 4)
		}
		var var62 = false
		if (par15 == -1) {
			par15 = par16 / 2
			var62 = true
		}
		val var27 = var25.nextInt(par16 / 2) + par16 / 4
		val var28 = var25.nextInt(6) == 0
		while (par15 < par16) {
			val var29 = 1.5 + (MathHelper.sin(par15.F * 3.1415927f / par16.F) * par12 * 1f).D
			val var31 = var29 * par17
			val var33 = MathHelper.cos(par14)
			val var34 = MathHelper.sin(par14)
			par6 += (MathHelper.cos(par13) * var33).D
			par8 += var34.D
			par10 += (MathHelper.sin(par13) * var33).D
			par14 *= if (var28) {
				0.92f
			} else {
				0.7f
			}
			par14 += var24 * 0.1f
			par13 += var23 * 0.1f
			var24 *= 0.9f
			var23 *= 0.75f
			var24 += (var25.nextFloat() - var25.nextFloat()) * var25.nextFloat() * 2f
			var23 += (var25.nextFloat() - var25.nextFloat()) * var25.nextFloat() * 4f
			if (!var62 && par15 == var27 && par12 > 1f && par16 > 0) {
				generateCaveNode(var25.nextLong(), par3, par4, arrayOfBlocks, par6, par8, par10, var25.nextFloat() * 0.5f + 0.5f, par13 - 1.5707964f, par14 / 3f, par15, par16, 1.0)
				generateCaveNode(var25.nextLong(), par3, par4, arrayOfBlocks, par6, par8, par10, var25.nextFloat() * 0.5f + 0.5f, par13 + 1.5707964f, par14 / 3f, par15, par16, 1.0)
				return
			}
			if (var62 || var25.nextInt(4) != 0) {
				val var35 = par6 - var19
				val var37 = par10 - var21
				val var39 = (par16 - par15).D
				val var41 = (par12 + 2f + 16f).D
				if (var35 * var35 + var37 * var37 - var39 * var39 > var41 * var41) {
					return
				}
				if (par6 >= var19 - 16.0 - var29 * 2.0 && par10 >= var21 - 16.0 - var29 * 2.0 && par6 <= var19 + 16.0 + var29 * 2.0 && par10 <= var21 + 16.0 + var29 * 2.0) {
					var var55 = MathHelper.floor_double(par6 - var29) - par3 * 16 - 1
					var var36 = MathHelper.floor_double(par6 + var29) - par3 * 16 + 1
					var var57 = MathHelper.floor_double(par8 - var31) - 1
					var var38 = MathHelper.floor_double(par8 + var31) + 1
					var var56 = MathHelper.floor_double(par10 - var29) - par4 * 16 - 1
					var var40 = MathHelper.floor_double(par10 + var29) - par4 * 16 + 1
					if (var55 < 0) {
						var55 = 0
					}
					if (var36 > 16) {
						var36 = 16
					}
					if (var57 < 1) {
						var57 = 1
					}
					if (var38 > 120) {
						var38 = 120
					}
					if (var56 < 0) {
						var56 = 0
					}
					if (var40 > 16) {
						var40 = 16
					}
					var var58 = false
					var var42: Int
					var var45: Int
					var42 = var55
					while (!var58 && var42 < var36) {
						var var59 = var56
						while (!var58 && var59 < var40) {
							var var44 = var38 + 1
							while (!var58 && var44 >= var57 - 1) {
								var45 = (var42 * 16 + var59) * 128 + var44
								if (var44 < 128) {
									if (arrayOfBlocks[var45] === Blocks.flowing_water || arrayOfBlocks[var45] === Blocks.water) {
										var58 = true
									}
									if (var44 != var57 - 1 && var42 != var55 && var42 != var36 - 1 && var59 != var56 && var59 != var40 - 1) {
										var44 = var57
									}
								}
								--var44
							}
							++var59
						}
						++var42
					}
					var var46: Double
					var var48: Int
					var var63: Double
					var42 = var55
					while (var42 < var36) {
						var63 = ((var42 + par3 * 16).D + 0.5 - par6) / var29
						var45 = var56
						while (var45 < var40) {
							var46 = ((var45 + par4 * 16).D + 0.5 - par10) / var29
							var48 = (var42 * 16 + var45) * 128 + var38
							if (var63 * var63 + var46 * var46 < 1.0) {
								for (var49 in var38 - 1 downTo var57) {
									val var50 = (var49.D + 0.5 - par8) / var31
									if (var50 > -1.6 && var63 * var63 + var50 * var50 + var46 * var46 < 1.9) {
										val var53 = arrayOfBlocks[var48]
										if (worldObj.getBiomeGenForCoords(var42 + par3 * 16, var45 + par4 * 16) is BiomeGenSnow && (var53 inl ChunkProviderNiflheim.surfaceBlocks)) {
											arrayOfBlocks[var48] = Blocks.ice
										}
									}
									--var48
								}
							}
							++var45
						}
						++var42
					}
					if (!var58) {
						var42 = var55
						while (var42 < var36) {
							var63 = ((var42 + par3 * 16).D + 0.5 - par6) / var29
							var45 = var56
							while (var45 < var40) {
								var46 = ((var45 + par4 * 16).D + 0.5 - par10) / var29
								var48 = (var42 * 16 + var45) * 128 + var38
								if (var63 * var63 + var46 * var46 < 1.0) {
									for (var65 in var38 - 1 downTo var57) {
										val var51 = (var65.D + 0.5 - par8) / var31
										if (var51 > -0.7 && var63 * var63 + var51 * var51 + var46 * var46 < 1.0) {
											val var531 = arrayOfBlocks[var48]
											if (var531 inl ChunkProviderNiflheim.surfaceBlocks) {
												if (var65 < 10) {
													if (worldObj.getBiomeGenForCoords(var42 + par3 * 16, var45 + par4 * 16) is BiomeGenSnow) {
														arrayOfBlocks[var48] = Blocks.snow
													} else {
														arrayOfBlocks[var48] = AlfheimBlocks.poisonIce
													}
												} else {
													arrayOfBlocks[var48] = Blocks.air
												}
											}
										}
										--var48
									}
								}
								++var45
							}
							++var42
						}
						if (var62) {
							break
						}
					}
				}
			}
			++par15
		}
	}
	
	override fun func_151538_a(p_151538_1_: World, par2: Int, par3: Int, par4: Int, par5: Int, blocks: Array<Block>) {
		var var7 = rand.nextInt(rand.nextInt(rand.nextInt(40) + 1) + 1)
		if (rand.nextInt(15) != 0) {
			var7 = 0
		}
		for (var8 in 0 until var7) {
			val var9 = (par2 * 16 + rand.nextInt(16)).D
			val var11 = rand.nextInt(rand.nextInt(120) + 8).D
			val var13 = (par3 * 16 + rand.nextInt(16)).D
			var var15 = 1
			if (rand.nextInt(4) == 0) {
				generateLargeCaveNode(rand.nextLong(), par4, par5, blocks, var9, var11, var13)
				var15 += rand.nextInt(4)
			}
			for (var16 in 0 until var15) {
				val var17 = rand.nextFloat() * 3.1415927f * 2f
				val var18 = (rand.nextFloat() - 0.5f) * 2f / 8f
				var var19 = rand.nextFloat() * 2f + rand.nextFloat()
				if (rand.nextInt(10) == 0) {
					var19 *= rand.nextFloat() * rand.nextFloat() * 3f + 1f
				}
				generateCaveNode(rand.nextLong(), par4, par5, blocks, var9, var11, var13, var19, var17, var18, 0, 0, 1.0)
			}
		}
	}
}