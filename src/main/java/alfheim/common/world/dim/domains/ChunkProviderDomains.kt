package alfheim.common.world.dim.domains

import alexsocol.asjlib.SchemaUtils
import alfheim.api.AlfheimAPI
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.entity.EnumCreatureType
import net.minecraft.init.Blocks
import net.minecraft.util.IProgressUpdate
import net.minecraft.world.*
import net.minecraft.world.chunk.*

class ChunkProviderDomains(val world: World): IChunkProvider {
	
	override fun populate(thiz: IChunkProvider?, cx: Int, cz: Int) {
		if (cx != 0 || cz != 0 || !AlfheimConfigHandler.domainImmediate) return
		
		for (data in AlfheimAPI.domains.values) {
			val domain = SchemaUtils.loadStructure("${data.modid}/schemas/${data.name}")
			val x = data.id * AlfheimConfigHandler.domainDistance + AlfheimConfigHandler.domainStartX
			
			for (i in 0 until AlfheimConfigHandler.domainMaxCount) {
				val z = i * AlfheimConfigHandler.domainDistance + AlfheimConfigHandler.domainStartZ
				
				world.setBlock(x, 0, z, AlfheimBlocks.barrier)
				
				SchemaUtils.generate(world, x, 64, z, domain)
			}
		}
	}
	
	override fun chunkExists(x: Int, z: Int) = true
	override fun provideChunk(x: Int, z: Int) = Chunk(world, Array(65536) { Blocks.air }, ByteArray(65536), x, z)
	override fun loadChunk(x: Int, z: Int) = provideChunk(x, z)
	override fun saveChunks(p_73151_1_: Boolean, p_73151_2_: IProgressUpdate?) = true
	override fun unloadQueuedChunks() = false
	override fun canSave() = true
	override fun makeString() = "Domains"
	override fun getPossibleCreatures(type: EnumCreatureType?, x: Int, y: Int, z: Int) = emptyList<Any?>()
	override fun func_147416_a(world: World?, name: String?, x: Int, y: Int, z: Int) = ChunkPosition(0, 64, 0)
	override fun getLoadedChunkCount() = 0
	override fun recreateStructures(x: Int, z: Int) = Unit
	override fun saveExtraData() = Unit
}
