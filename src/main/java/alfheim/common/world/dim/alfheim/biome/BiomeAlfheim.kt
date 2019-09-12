package alfheim.common.world.dim.alfheim.biome

import alfheim.common.entity.EntityElf
import net.minecraft.entity.passive.*
import net.minecraftforge.common.BiomeDictionary
import net.minecraftforge.common.BiomeDictionary.Type
import ru.vamig.worldengine.WE_Biome

open class BiomeAlfheim @JvmOverloads constructor(ID_FOR_ALL_WE_BIOMES: Int, r: Boolean = false): WE_Biome(ID_FOR_ALL_WE_BIOMES, r) {
	
	init {
		
		BiomeDictionary.registerBiomeType(this, Type.MAGICAL)
		
		clearSpawn()
		setBiomeName("Alfheim")
		setColor(0xA67C00)
		waterColorMultiplier = 0x1D1D4E
		
		createChunkGen_InXZ_List.clear()
		createChunkGen_InXYZ_List.clear()
		decorateChunkGen_List.clear()
		
		spawnableCreatureList.add(SpawnListEntry(EntityElf::class.java, 1, 2, 4))
		
		spawnableCreatureList.add(SpawnListEntry(EntitySheep::class.java, 12, 4, 4))
		spawnableCreatureList.add(SpawnListEntry(EntityPig::class.java, 10, 4, 4))
		spawnableCreatureList.add(SpawnListEntry(EntityChicken::class.java, 10, 4, 4))
		spawnableCreatureList.add(SpawnListEntry(EntityCow::class.java, 8, 4, 4))
	}
}