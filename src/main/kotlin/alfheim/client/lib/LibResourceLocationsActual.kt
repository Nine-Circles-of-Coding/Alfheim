package alfheim.client.lib

import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.api.lib.LibResourceLocations.ResourceLocationIL
import alfheim.common.item.equipment.bauble.ItemPriestEmblem
import vazkii.botania.client.lib.LibResources

object LibResourceLocationsActual {
	
	fun init() {
		LibResourceLocations.babylon = ResourceLocationIL(LibResources.MISC_BABYLON)
		LibResourceLocations.elvenPylon = ResourceLocationIL(LibResources.MODEL_PYLON_PINK)
		LibResourceLocations.elvenPylonOld = ResourceLocationIL(LibResources.MODEL_PYLON_PINK_OLD)
		LibResourceLocations.glowCyan = ResourceLocationIL(LibResources.MISC_GLOW_CYAN)
		LibResourceLocations.halo = ResourceLocationIL(LibResources.MISC_HALO)
		LibResourceLocations.lexica = ResourceLocationIL(LibResources.MODEL_LEXICA)
		LibResourceLocations.manaInfuserOverlay = ResourceLocationIL(LibResources.GUI_MANA_INFUSION_OVERLAY)
		LibResourceLocations.petalOverlay = ResourceLocationIL(LibResources.GUI_PETAL_OVERLAY)
		LibResourceLocations.pixie = ResourceLocationIL(LibResources.MODEL_PIXIE)
		LibResourceLocations.spreader = ResourceLocationIL(LibResources.MODEL_SPREADER)
		
		LibResourceLocations.godCloak = Array(ItemPriestEmblem.TYPES) {
			ResourceLocationIL(ModInfo.MODID, "textures/model/armor/cloak/God$it.png")
		}
		
		LibResourceLocations.godCloakGlow = Array(ItemPriestEmblem.TYPES) {
			ResourceLocationIL(ModInfo.MODID, "textures/model/armor/cloak/God${it}_glow.png")
		}
	}
}