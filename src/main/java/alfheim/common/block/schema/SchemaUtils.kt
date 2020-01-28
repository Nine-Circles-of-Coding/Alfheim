package alfheim.common.block.schema

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.nbt.*
import net.minecraft.world.World

object SchemaGenerator {
	
	fun generate(world: World, x: Int, y: Int, z: Int, schemaText: String) {
		val type = object: TypeToken<List<BlockElement>>() {}.type
		
		val arr = Gson().fromJson<List<BlockElement>>(schemaText, type)
		world.setBlock(x, y, z, Blocks.air, 0, 4)
		
		for (ele in arr) {
			for (loc in ele.location) {
				world.setBlock(x + loc.x, y + loc.y, z + loc.z, Block.getBlockFromName(ele.block), loc.meta, 3)
				if (loc.nbt != null)
					world.getTileEntity(x + loc.x, y + loc.y, z + loc.z)?.let {
						it.readFromNBT(JsonToNBT.func_150315_a(loc.nbt) as NBTTagCompound)
						it.xCoord = x + loc.x
						it.yCoord = y + loc.y
						it.zCoord = z + loc.z
					}
			}
		}
	}
}

class BlockElement(val block: String, val location: List<LocationElement>)

class LocationElement(val x: Int, val y: Int, val z: Int, val meta: Int, val nbt: String?)