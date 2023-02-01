package alfheim.common.block.tile.sub.flower

import alexsocol.asjlib.*
import alfheim.AlfheimCore
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraftforge.common.ForgeChunkManager.*
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.subtile.*
import vazkii.botania.common.item.ItemTwigWand

class SubTileBudOfYggdrasil: SubTileFunctional() {
	
	var chunkTicket: Ticket? = null
	var creative = false
	
	override fun onUpdate() {
		super.onUpdate()
		
		val can = (creative || mana >= COST) && redstoneSignal <= 0
		
		if (chunkTicket == null) {
			if (can) init() else return
		} else {
			if (!can) return disable()
		}
		
		if (!creative) mana -= COST
	}
	
	fun init() {
		if (supertile.worldObj.isRemote) return
		
		requestTicket(AlfheimCore, supertile.worldObj, Type.NORMAL)?.apply {
			chunkTicket = this
			modData.setInteger("subtileX", supertile.xCoord)
			modData.setInteger("subtileY", supertile.yCoord)
			modData.setInteger("subtileZ", supertile.zCoord)
			forceChunkLoading()
		}
	}
	
	fun forceChunkLoading() = try_ {
		forceChunk(chunkTicket, ChunkCoordIntPair(supertile.xCoord shr 4, supertile.zCoord shr 4))
		ASJUtilities.log("Inited ticket $chunkTicket")
	}
	
	fun disable() {
		if (supertile.worldObj.isRemote) return
		
		releaseTicket(chunkTicket ?: return)
		
		ASJUtilities.log("Released ticket $chunkTicket")
		
		chunkTicket = null
	}
	
	override fun onWanded(player: EntityPlayer?, wand: ItemStack?): Boolean {
		val result = super.onWanded(player, wand)
		if (ItemTwigWand.getBindMode(wand)) return result
		if (player?.capabilities?.isCreativeMode != true) return result
		creative = !creative
		return result
	}
	
	override fun renderHUD(mc: Minecraft?, res: ScaledResolution?) {
		var name = StatCollector.translateToLocal("tile.botania:flower.$unlocalizedName.name")
		if (creative) name += " [${StatCollector.translateToLocal("alfheimmisc.creative")}]"
		BotaniaAPI.internalHandler.drawComplexManaHUD(color, knownMana, maxMana, name, res, BotaniaAPI.internalHandler.getBindDisplayForFlowerType(this), isValidBinding)
	}
	
	override fun getIcon() = BotaniaAPI.getSignatureForName("budOfYggdrasil").getIconForStack(null)
	
	override fun readFromPacketNBT(nbt: NBTTagCompound) {
		super.readFromPacketNBT(nbt)
		creative = nbt.getBoolean(TAG_CREATIVE)
	}
	
	override fun writeToPacketNBT(nbt: NBTTagCompound) {
		super.writeToPacketNBT(nbt)
		nbt.setBoolean(TAG_CREATIVE, creative)
	}
	
	override fun acceptsRedstone() = true
	override fun getMaxMana() = 100_000
	override fun getColor() = 0xAAFF44
	override fun getRadius(): RadiusDescriptor = ChunkRadiusDescriptor(toChunkCoordinates())
	override fun getEntry() = AlfheimLexiconData.flowerBud
	
	private class ChunkRadiusDescriptor(coords: ChunkCoordinates): RadiusDescriptor(coords) {
		
		val aabb: AxisAlignedBB
		
		init {
			val (x, y, z) = coords
			val cx = x shr 4
			val cz = z shr 4
			
			aabb = getBoundingBox(cx * 16, y, cz * 16, cx * 16 + 16, y, cz * 16 + 16)
		}
		
		override fun getAABB(): AxisAlignedBB {
			return aabb
		}
	}
	
	companion object: LoadingCallback {
		
		const val COST = 10
		const val TAG_CREATIVE = "creative"
		
		init {
			setForcedChunkLoadingCallback(AlfheimCore, this)
		}
		
		override fun ticketsLoaded(tickets: MutableList<Ticket>?, world: World?) {
			ASJUtilities.log("Loaded tickets '$tickets' for world '$world'")
		}
	}
}
