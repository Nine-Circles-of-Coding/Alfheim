package alfheim.common.world.dim.niflheim

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.tile.TileDomainLobby
import alfheim.common.integration.ThermalFoundationIntegration
import alfheim.common.world.dim.niflheim.biome.*
import alfheim.common.world.dim.niflheim.customgens.*
import alfheim.common.world.dim.niflheim.structure.*
import net.minecraft.block.*
import net.minecraft.entity.EnumCreatureType
import net.minecraft.init.Blocks
import net.minecraft.util.IProgressUpdate
import net.minecraft.world.World
import net.minecraft.world.biome.BiomeGenBase
import net.minecraft.world.chunk.*
import net.minecraft.world.gen.*
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.terraingen.PopulateChunkEvent
import ru.vamig.worldengine.WE_PerlinNoise.PerlinNoise2D
import java.util.*
import kotlin.math.*

class ChunkProviderNiflheim(world: World, val seed: Long, structures: Boolean): IChunkProvider {
	
	val world: World
	val random: Random
	val useStructures: Boolean
	var noiseField = DoubleArray(0)
	var gravelNoise: DoubleArray
	
	var noiseData1: DoubleArray? = null
	var noiseData2: DoubleArray? = null
	var noiseData3: DoubleArray? = null
	
	val noiseGen1: NoiseGeneratorOctaves
	val noiseGen2: NoiseGeneratorOctaves
	val noiseGen3: NoiseGeneratorOctaves
	val noiseGen4: NoiseGeneratorOctaves
	val ravineGenerator: MapGenBase
	val caveGenerator: MapGenBase
	var biomesForGeneration = emptyArray<BiomeGenBase>()
	
	init {
		ravineGenerator = MapGenCustomRavine
		caveGenerator = MapGenCustomCaves
		this.world = world
		random = Random(seed)
		useStructures = structures
		gravelNoise = DoubleArray(256)
		noiseGen1 = NoiseGeneratorOctaves(random, 16)
		noiseGen2 = NoiseGeneratorOctaves(random, 16)
		noiseGen3 = NoiseGeneratorOctaves(random, 8)
		noiseGen4 = NoiseGeneratorOctaves(random, 4)
	}
	
	override fun loadChunk(x: Int, z: Int): Chunk {
		return provideChunk(x, z)
	}
	
	override fun provideChunk(x: Int, z: Int): Chunk {
		random.setSeed(x * 341873128712L + z * 132897987541L)
		val metaArray = ByteArray(16 * 16 * 256) { 0 }
		
		val cavesArray = Array(16 * 16 * 128) { Blocks.air }
		val mountainsArray = Array(16 * 16 * 128) { Blocks.air }
		
		generateCaves(x, z, cavesArray)
		generateMountains(x, z, mountainsArray)
		
		biomesForGeneration = world.worldChunkManager.loadBlockGeneratorData(biomesForGeneration, x * 16, z * 16, 16, 16)
		replaceBlocksForBiome(x, z, cavesArray, biomesForGeneration)
		caveGenerator.func_151539_a(this, world, x, z, cavesArray)
		ravineGenerator.func_151539_a(this, world, x, z, cavesArray)
		
		val chunkArray = mergeArrays(cavesArray, mountainsArray)
		makeRiver(chunkArray, x, z)
		
		val chunk = Chunk(world, chunkArray, metaArray, x, z)
		chunk.generateSkylightMap()
		return chunk
	}
	
	fun generateCaves(cX: Int, cZ: Int, chunk: Array<Block>) {
		val var4 = 4
		val floorLevel = 32
		val var6 = var4 + 1
		val var7 = 17
		val var8 = var4 + 1
		
		noiseField = initializeNoiseField(noiseField, cX * var4, 0, cZ * var4, var6, var7, var8)
		
		for (var9 in 0 until var4) {
			for (var10 in 0 until var4) {
				for (var11 in 0..15) { // half of world - 0..31 for full world
					val var12 = 0.125
					var var14 = noiseField[(var9 * var8 + var10) * var7 + var11]
					var var16 = noiseField[(var9 * var8 + var10 + 1) * var7 + var11]
					var var18 = noiseField[((var9 + 1) * var8 + var10) * var7 + var11]
					var var20 = noiseField[((var9 + 1) * var8 + var10 + 1) * var7 + var11]
					val var22 = (noiseField[(var9 * var8 + var10) * var7 + var11 + 1] - var14) * var12
					val var24 = (noiseField[(var9 * var8 + var10 + 1) * var7 + var11 + 1] - var16) * var12
					val var26 = (noiseField[((var9 + 1) * var8 + var10) * var7 + var11 + 1] - var18) * var12
					val var28 = (noiseField[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 1] - var20) * var12
					for (var30 in 0..7) {
						val var31 = 0.25
						var var33 = var14
						var var35 = var16
						val var37 = (var18 - var14) * var31
						val var39 = (var20 - var16) * var31
						for (var41 in 0..3) {
							val x = (var9 * 4) + var41
							val y = var11 * 8 + var30
							val z = var10 * 4
							
							var var42 = index(x, y, z)
							val var43 = 128
							val var44 = 0.25
							var var46 = var33
							val var48 = (var35 - var33) * var44
							for (var50 in 0..3) {
								var var51 = AlfheimBlocks.niflheimBlock
								
								if (var46 > 0.0) {
									var51 = Blocks.air
								}
								
								if (y < floorLevel) {
									var51 = AlfheimBlocks.niflheimBlock
								}
								
								val scale = 10.0
								if (y > PerlinNoise2D(world.seed, (cX * 16 + x) / scale, (cZ * 16 + z + var50) / scale, 5.0, 1) + 106) {
									var51 = AlfheimBlocks.niflheimBlock
								}
								
								chunk[var42] = var51
								var42 += var43
								var46 += var48
							}
							var33 += var37
							var35 += var39
						}
						var14 += var22
						var16 += var24
						var18 += var26
						var20 += var28
					}
				}
			}
		}
	}
	
	fun generateMountains(cX: Int, cZ: Int, chunk: Array<Block>) {
		val amplitude = 64.0
		val scaleXZMin = 64.0
		val scaleXZMax = 128.0
		val scaleY = 5.0
		
		for (i in 0..15) for (k in 0..15) {
			val x = cX * 16 + i
			val z = cZ * 16 + k
			
			var minHeight = PerlinNoise2D(world.seed, x / scaleXZMin, z / scaleXZMin, 8.0, 1)
			minHeight = max(0.0, min(minHeight, 127.0))
			
			var maxHeight = PerlinNoise2D(world.seed, x / scaleXZMax, z / scaleXZMax, amplitude, 1) * scaleY
			maxHeight = max(minHeight, min(maxHeight, 127.0))
			
			for (y in 0 until maxHeight.I) chunk[index(i, y, k)] = AlfheimBlocks.niflheimBlock
			chunk[index(i, maxHeight.I, k)] = Blocks.snow_layer
		}
	}
	
	fun index(x: Int, y: Int, z: Int) = (x shl 11) or (z shl 7) or y
	
	fun mergeArrays(cavesArray: Array<Block>, mountainsArray: Array<Block>): Array<Block> {
		val chunkArray = Array<Block>(cavesArray.size + mountainsArray.size) { Blocks.air }
		
		for (x in 0..15) for (z in 0..15) for (y in 0..255) {
			val i = (x shl 12) or (z shl 8) or y
			
			chunkArray[i] = (if (y < 128) cavesArray else mountainsArray)[index(x, y % 128, z)]
		}
		
		return chunkArray
	}
	
	fun replaceBlocksForBiome(cX: Int, cZ: Int, chunk: Array<Block>, biomes: Array<BiomeGenBase>) {
		val var5 = 33
		val var6 = 0.03125
		
		gravelNoise = noiseGen4.generateNoiseOctaves(gravelNoise, cX * 16, 109, cZ * 16, 16, 1, 16, var6, 1.0, var6)
		
		for (x in 0..15) {
			for (z in 0..15) {
				val biome = biomes[z + x * 16]
				val gravel = gravelNoise[x + z * 16] + random.nextDouble() * 0.2 > 0.0
				val temperature = biome.getFloatTemperature(0, 65, 0)
				val var12 = (gravelNoise[x + z * 16] / 3.0 + 3.0 + random.nextDouble() * 0.25).I
				var var13 = -1
				var top = biome.topBlock
				var filler = biome.fillerBlock
				
				for (y in 127 downTo 0) {
					val index = (z * 16 + x) * 128 + y
					if (index >= chunk.size) continue
					
					if (y == 0) {
						chunk[index] = Blocks.bedrock
						continue
					}
					
					val current = chunk[index]
					if (index + 1 < chunk.size && current === Blocks.air && chunk[index + 1] === AlfheimBlocks.niflheimBlock && top === Blocks.snow) {
						chunk[index + 1] = Blocks.ice
					}
					
					if (index + 2 < chunk.size && current === Blocks.air && chunk[index + 1] === Blocks.ice && chunk[index + 2] === AlfheimBlocks.niflheimBlock && top === Blocks.snow) {
						chunk[index + 2] = Blocks.ice
					}
					
					if (current === Blocks.air)
						var13 = -1
					
					if (current !== AlfheimBlocks.niflheimBlock)
						continue
					
					if (var13 == -1) {
						if (var12 <= 0) {
							top = Blocks.air
							filler = AlfheimBlocks.niflheimBlock
						} else if (y >= var5 - 4 && y <= var5 + 1) {
							top = biome.topBlock
							filler = biome.fillerBlock
						}
						if (y >= var5 - 4 && y <= var5 + 1) {
							if (gravel && top === AlfheimBlocks.niflheimBlock) {
								top = AlfheimBlocks.niflheimBlock // gravel
								filler = AlfheimBlocks.niflheimBlock
							} else if (gravel && top === Blocks.snow) {
								filler = Blocks.snow
							}
						}
						if (y < var5 && top === Blocks.air) {
							top = if (temperature < 0.15f) {
								Blocks.ice
							} else {
								Blocks.water
							}
						}
						var13 = var12
						
						if (y >= var5 - 1) {
							chunk[index] = top
						} else {
							chunk[index] = filler
						}
					} else if (var13 > 0) {
						--var13
						chunk[index] = filler
						if (var13 == 0 && filler === AlfheimBlocks.niflheimBlock/*sand*/) {
							var13 = random.nextInt(4)
							filler = AlfheimBlocks.niflheimBlock // sandstone
						}
					}
					
					// факинг ступид бич
					if (y > 123)
						chunk[index] = AlfheimBlocks.niflheimBlock
				}
			}
		}
	}
	
	fun initializeNoiseField(_list: DoubleArray, par2: Int, par3: Int, par4: Int, par5: Int, par6: Int, par7: Int): DoubleArray {
		var list: DoubleArray = _list
		
		if (list.size < par5 * par6 * par7) list = DoubleArray(par5 * par6 * par7)
		
		val d = 684.41
		val d1 = 2053.23
		
		noiseData1 = noiseGen3.generateNoiseOctaves(noiseData1, par2, par3, par4, par5, par6, par7, d / 90, d1 / 60, d / 70)
		noiseData2 = noiseGen1.generateNoiseOctaves(noiseData2, par2, par3, par4, par5, par6, par7, d, d1, d)
		noiseData3 = noiseGen2.generateNoiseOctaves(noiseData3, par2, par3, par4, par5, par6, par7, d, d1, d)
		
		var i = 0
		val ad = DoubleArray(par6)
		var l = 0
		
		while (l < par6) {
			ad[l] = cos(l.D * 3.141592653589793 * 6.0 / par6.D) * 2.0
			var i1 = l.D
			if (l > par6 / 2) {
				i1 = (par6 - 1 - l).D
			}
			if (i1 < 4.0) {
				i1 = 4.0 - i1
				ad[l] -= i1 * i1 * i1 * 10.0
			}
			++l
		}
		
		l = 0
		
		while (l < par5) {
			for (var36 in 0 until par7) {
				val d4 = 0.0
				for (j1 in 0 until par6) {
					var d6: Double
					val d7 = ad[j1]
					val d8 = noiseData2!![i] / 512.0
					val d9 = noiseData3!![i] / 512.0
					val d10 = (noiseData1!![i] / 10.0 + 1.0) / 2.0
					d6 = when {
						d10 < 0.0 -> {
							d8
						}
						
						d10 > 1.0 -> {
							d9
						}
						
						else      -> {
							d8 + (d9 - d8) * d10
						}
					}
					d6 -= d7
					var d12: Double
					if (j1 > par6 - 4) {
						d12 = (j1 - (par6 - 4)) / 3.0
						d6 = d6 * (1.0 - d12) + -10.0 * d12
					}
					if (j1.D < d4) {
						d12 = (d4 - j1.D) / 4.0
						if (d12 < 0.0) {
							d12 = 0.0
						}
						if (d12 > 1.0) {
							d12 = 1.0
						}
						d6 = d6 * (1.0 - d12) + -10.0 * d12
					}
					list[i] = d6
					++i
				}
			}
			++l
		}
		return list
	}
	
	fun makeRiver(chunkArray: Array<Block>, cx: Int, cz: Int) {
		for (i in 0..15)
			for (k in 0..15) {
				val x = cx * 16 + i
				val z = cz * 16 + k
				
				val f = f(x)
				if (f in z.bidiRange(6)) {
					val deltaZ = 6 - abs(z - f)
					
					for (y in 121..134) {
						val deltaY = abs(127 - y) / 1.5
						if (deltaY > deltaZ) continue
						
						val id = (i shl 12) or (k shl 8) or y
						chunkArray[id] = if (y < 128) Blocks.water else Blocks.air
					}
					
					if (chunkArray[(i shl 12) or (k shl 8) or 135] === Blocks.snow_layer)
						chunkArray[(i shl 12) or (k shl 8) or 135] = Blocks.air
					
					chunkArray[(i shl 12) or (k shl 8) or 120] = AlfheimBlocks.niflheimBlock
				}
			}
	}
	
	override fun populate(provider: IChunkProvider, cX: Int, cZ: Int) {
		BlockSand.fallInstantly = true
		
		random.setSeed(world.seed * cX + cZ * cZ * 107L + 2394720L)
		MinecraftForge.EVENT_BUS.post(PopulateChunkEvent.Pre(provider, world, random, cX, cZ, false))
		
		val i = cX * 16
		val k = cZ * 16
		val biomegenbase = world.worldChunkManager.getBiomeGenAt(i, k)
		var m: Int
		var x: Int
		var y: Int
		var z: Int
		m = 0
		while (m < 8) {
			x = i + random.nextInt(16) + 8
			y = random.nextInt(120) + 4
			z = k + random.nextInt(16) + 8
			WorldGenRoot.generate(world, random, x, y, z)
			++m
		}
//        m = 0
//        while (m < 8) {
//            x = i + random.nextInt(16) + 8
//            y = random.nextInt(120) + 4
//            z = k + random.nextInt(16) + 8
//            WorldGenRoot.generateDark(world, random, x, y, z)
//            ++m
//        }
		var biomegenbase2: BiomeGenBase?
		m = 0
		while (m < 8) {
			x = i + random.nextInt(16) + 8
			y = random.nextInt(128)
			z = k + random.nextInt(16) + 8
			biomegenbase2 = world.worldChunkManager.getBiomeGenAt(x, z)
			WorldGenDungeons.generate(world, random, x, y, z, biomegenbase2)
			++m
		}
		var var12: BiomeGenBase?
		if (random.nextInt(4) == 0 && biomegenbase !is BiomeGenPoison) {
			m = i + random.nextInt(16) + 8
			x = random.nextInt(100)
			y = k + random.nextInt(16) + 8
			var12 = world.worldChunkManager.getBiomeGenAt(m, y)
			if (var12 !is BiomeGenPoison) {
				WorldGenLakes.generate(world, random, m, x, y, Blocks.water, var12)
			}
		}
		if ((biomegenbase is BiomeGenIce || biomegenbase is BiomeGenPoison) && random.nextInt(8) == 0) {
			m = i + random.nextInt(16) + 8
			x = random.nextInt(random.nextInt(92) + 8)
			y = k + random.nextInt(16) + 8
			var12 = world.worldChunkManager.getBiomeGenAt(m, y)
			if (x < 63 || random.nextInt(10) == 0 && (var12 is BiomeGenIce || var12 is BiomeGenPoison)) {
				WorldGenLakes.generate(world, random, m, x, y, AlfheimBlocks.poisonIce, var12)
			}
		}
		if (biomegenbase is BiomeGenPoison && random.nextInt(3) == 0) {
			m = i + random.nextInt(16) + 8
			x = random.nextInt(random.nextInt(92) + 8)
			y = k + random.nextInt(16) + 8
			var12 = world.worldChunkManager.getBiomeGenAt(m, y)
			if (var12 is BiomeGenPoison) {
				WorldGenLakes.generate(world, random, m, x, y, AlfheimBlocks.poisonIce, var12)
			}
		}
		if (biomegenbase is BiomeGenSnow) {
			m = 0
			while (m < 20) {
				x = i + random.nextInt(16) + 8
				y = random.nextInt(50) + 20
				z = k + random.nextInt(16) + 8
				biomegenbase2 = world.worldChunkManager.getBiomeGenAt(x, z)
				if (biomegenbase2 is BiomeGenSnow) {
					if (ThermalFoundationIntegration.loaded && random.nextInt(10) == 0) {
						WorldGenWaterfall.generateRandom(world, random, x, y, z, ThermalFoundationIntegration.cryothenumBlock)
					} else
						WorldGenWaterfall.generateFrozenDirect(world, x, y, z, Blocks.ice)
				}
				++m
			}
		}
		if (biomegenbase is BiomeGenIce) {
			m = 0
			while (m < 20) {
				x = i + random.nextInt(16) + 8
				y = random.nextInt(50) + 20
				z = k + random.nextInt(16) + 8
				biomegenbase2 = world.worldChunkManager.getBiomeGenAt(x, z)
				if (biomegenbase2 is BiomeGenIce) {
					WorldGenWaterfall.generateDirect(world, random, x, y, z, Blocks.flowing_water, Blocks.water)
				}
				++m
			}
		}
		if (biomegenbase is BiomeGenIce || biomegenbase is BiomeGenPoison) {
			m = 0
			while (m < 20) {
				x = i + random.nextInt(16) + 8
				y = random.nextInt(50) + 20
				z = k + random.nextInt(16) + 8
				biomegenbase2 = world.worldChunkManager.getBiomeGenAt(x, z)
				if (biomegenbase2 is BiomeGenIce || biomegenbase2 is BiomeGenPoison) {
					WorldGenWaterfall.generateFrozenDirect(world, x, y, z, AlfheimBlocks.poisonIce)
				}
				++m
			}
		}
		if (biomegenbase is BiomeGenIce) {
			m = 0
			while (m < 2) {
				x = i + random.nextInt(16) + 8
				y = random.nextInt(100) + 10
				z = k + random.nextInt(16) + 8
				biomegenbase2 = world.worldChunkManager.getBiomeGenAt(x, z)
				if (biomegenbase2 is BiomeGenIce) {
					WorldGenWaterfall.generateRandom(world, random, x, y, z, Blocks.flowing_water /*dirt*/)
				}
				++m
			}
		}
		if (biomegenbase is BiomeGenPoison) {
			m = 0
			while (m < 6) {
				x = i + random.nextInt(16) + 8
				y = random.nextInt(100) + 10
				z = k + random.nextInt(16) + 8
				biomegenbase2 = world.worldChunkManager.getBiomeGenAt(x, z)
				if (biomegenbase2 is BiomeGenPoison) {
					WorldGenWaterfall.generateRandom(world, random, x, y, z, Blocks.flowing_water /*obsidian*/)
				}
				++m
			}
		}
		if (useStructures) {
			m = 0
			while (m < 4) {
				x = i + random.nextInt(16) + 8
				y = random.nextInt(60) + 50
				z = k + random.nextInt(16) + 8
				WorldGenBridge.generateX(world, random, x, y, z)
				++m
			}
			m = 0
			while (m < 4) {
				x = i + random.nextInt(16) + 8
				y = random.nextInt(60) + 50
				z = k + random.nextInt(16) + 8
				WorldGenBridge.generateZ(world, random, x, y, z)
				++m
			}
			
			x = i + random.nextInt(16) + 8
			y = random.nextInt(30) + 5
			z = k + random.nextInt(16) + 8
			WorldGenRuins.generate(world, random, x, y, z)
		}
        m = 0
        while (m < 8) {
            x = i + random.nextInt(16) + 8
            y = random.nextInt(128)
            z = k + random.nextInt(16) + 8
            WorldGenNifleur.generate(world, random, x, y, z)
            ++m
        }
		m = 0
		while (m < 128) {
			x = i + random.nextInt(16) + 8
			y = random.nextInt(128)
			z = k + random.nextInt(16) + 8
			biomegenbase2 = world.worldChunkManager.getBiomeGenAt(x, z)
			WorldGenCaveHangs.generate(world, random, x, y, z, biomegenbase2)
			++m
		}
		if (biomegenbase is BiomeGenSnow) {
			m = 0
			while (m < 6) {
				x = i + random.nextInt(16) + 8
				y = random.nextInt(128)
				z = k + random.nextInt(16) + 8
				biomegenbase2 = world.worldChunkManager.getBiomeGenAt(x, z)
				if (biomegenbase2 is BiomeGenSnow) {
					WorldGenBigDungeons.generate(world, random, x, y, z)
				}
				++m
			}
		}
		if (random.nextInt(2) == 0) {
			m = i + random.nextInt(16) + 8
			x = random.nextInt(2) + 98
			y = k + random.nextInt(16) + 8
			WorldGenLakes.generate(world, random, m, x, y, Blocks.water, biomegenbase)
		}
		m = 0
		run {
			if (biomegenbase !is BiomeGenSnow && random.nextInt(5) != 0) return@run
			
			while (m < if (biomegenbase !is BiomeGenSnow) 1 else 5) {
				x = i + random.nextInt(16) + 8
				z = k + random.nextInt(16) + 8
				y = world.getTopSolidOrLiquidBlock(x, z)
				WorldGenIcePikes.generate(world, random, x, y, z)
				++m
			}
		}
		if (random.nextInt(8) == 0) {
			x = i + random.nextInt(16) + 8
			z = k + random.nextInt(16) + 8
			y = world.getTopSolidOrLiquidBlock(x, z)
			
			WorldGenHole.generate(world, random, x, y - 1, z)
		}
		if (random.nextInt(64) == 0) {
			x = i + random.nextInt(16) + 8
			y = random.nextInt(20) + 4
			z = k + random.nextInt(16) + 8
			var12 = world.worldChunkManager.getBiomeGenAt(x, z)
			WorldGenLakes.generate(world, random, x, y, z, AlfheimBlocks.niflheimPortal, var12)
		}
		
		MinecraftForge.EVENT_BUS.post(PopulateChunkEvent.Post(provider, world, random, cX, cZ, false))
		
		generateThrymDomain(cX, cZ)
		
		BlockSand.fallInstantly = false
		
		if (cX == 0 && cZ == 0) {
			val top = world.getTopSolidOrLiquidBlock(0, 0)
			world.setSpawnLocation(0, top + 1, 0)
		}
	}
	
	fun generateThrymDomain(cX: Int, cZ: Int) {
		val x = ASJUtilities.randInBounds(-1000, 1000, Random(seed))
		if (x shr 4 != cX) return
		
		val z = f(x) + 16
		if (z shr 4 != cZ) return
		
		val y = 128
		
		for (i in -3..3) {
			for (k in -16..4) {
				val minJ = if (abs(i) == 3) 1 else 0
				val maxJ = if (abs(i) == 3) 5 else 6
				for (j in minJ..maxJ) {
					if (world.getBlock(x + i, y + j, z + k) === AlfheimBlocks.niflheimBlock)
						world.setBlockToAir(x + i, y + j, z + k)
				}
			}
		}
		
		SchemaUtils.generate(world, x, y, z, SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/Domain"))
		
		val lobby = world.getTileEntity(x, y + 3, z + 3) as TileDomainLobby
		lobby.name = "Thrym"
		lobby.lock(x, y + 3, z + 3, world.provider.dimensionId)
	}
	
	// findClosestStructure
	override fun func_147416_a(par1World: World, par2Str: String, par3: Int, i: Int, j: Int) = null
	override fun getPossibleCreatures(type: EnumCreatureType, x: Int, y: Int, z: Int): MutableList<Any?>? = world.getBiomeGenForCoords(x, z)?.getSpawnableList(type)
	override fun getLoadedChunkCount() = 0
	override fun recreateStructures(x: Int, z: Int) = Unit
	override fun unloadQueuedChunks() = false
	override fun saveExtraData() = Unit
	override fun chunkExists(chunkX: Int, chunkZ: Int) = true
	override fun saveChunks(flag: Boolean, iprogressupdate: IProgressUpdate) = false
	override fun canSave() = true
	override fun makeString() = "Niflheim"
	
	companion object {
		
		val surfaceBlocks = arrayOf(AlfheimBlocks.niflheimBlock, Blocks.snow, Blocks.ice, Blocks.packed_ice, AlfheimBlocks.poisonIce)
		
		fun f(x: Int): Int {
			return ((sin(x / 32.0) + cos(x / 128.0)) * 16).I
		}
	}
}