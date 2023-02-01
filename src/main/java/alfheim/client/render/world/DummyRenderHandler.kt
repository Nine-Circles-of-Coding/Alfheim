package alfheim.client.render.world

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.WorldClient
import net.minecraftforge.client.IRenderHandler

object DummyRenderHandler: IRenderHandler() {
	override fun render(partialTicks: Float, world: WorldClient?, mc: Minecraft) = Unit
}