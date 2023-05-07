package alfheim.common.world.data

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.core.handler.AlfheimConfigHandler
import com.google.common.collect.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagIntArray
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.*

class CustomWorldData(datakey: String): WorldSavedData(datakey) {
	
	var spawnpoint: ChunkCoordinates? = null
		set(value) {
			field = value
			markDirty()
		}
	
	/** Set of structure coordinates so no structures are overlapping or being placed near each other */
	val structures: Multimap<String, Pair<Int, Int>> = HashMultimap.create()
	
	val data = HashMap<String, String>()
	
	override fun writeToNBT(nbt: NBTTagCompound) {
		spawnpoint?.let {
			val (x, y, z) = it
			nbt.setIntArray(TAG_SPAWNPOINT, intArrayOf(x, y, z))
		}
		
		val structs = NBTTagCompound()
		for (key in structures.keySet()) {
			val list = NBTTagList()
			for ((x, z) in structures.get(key))
				list.tagList.add(NBTTagIntArray(intArrayOf(x, z)))
			
			structs.setTag(key, list)
		}
		nbt.setTag(TAG_STRUCTURES, structs)
		
		val adata = NBTTagCompound()
		for ((k, v) in data.entries) {
			adata.setString(k, v)
		}
		nbt.setTag(TAG_DATA, adata)
	}
	
	override fun readFromNBT(nbt: NBTTagCompound) {
		val ints = nbt.getIntArray(TAG_SPAWNPOINT)
		if (ints.size == 3) {
			val (x, y, z) = ints
			spawnpoint = ChunkCoordinates(x, y, z)
		}
		
		nbt.getCompoundTag(TAG_STRUCTURES).apply {
			if (hasNoTags()) return@apply
			structures.clear()
			
			for (key in func_150296_c()) {
				val list = getTag(key.toString()) as? NBTTagList ?: continue
				
				for (e in list.tagList) {
					val (x, z) = (e as NBTTagIntArray).func_150302_c()
					structures.put(key.toString(), x to z)
				}
			}
		}
		
		val adata = nbt.getCompoundTag(TAG_DATA)
		for (k in adata.tagMap.keys)
			data[k.toString()] = adata.getString(k.toString())
	}
	
	companion object {
		
		const val TAG_DATA = "data"
		const val TAG_SPAWNPOINT = "spawnpoint"
		const val TAG_STRUCTURES = "structures"
		
		val datakeys = mapOf(
			-1 to "MuspelheimData",
			AlfheimConfigHandler.dimensionIDAlfheim to "AlfheimData",
			AlfheimConfigHandler.dimensionIDNiflheim to "NiflheimData",
							)
		
		val World.customData: CustomWorldData
			get() {
				val dimensionId = provider.dimensionId
				val name = "${ModInfo.MODID}_${datakeys[dimensionId] ?: dimensionId.toString()}"
				var data = perWorldStorage.loadData(CustomWorldData::class.java, name) as? CustomWorldData
				if (data == null) {
					data = CustomWorldData(name)
					data.markDirty()
					perWorldStorage.setData(name, data)
				}
				
				return data
			}
	}
}