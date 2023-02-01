package alfheim.api.event

import cpw.mods.fml.common.eventhandler.Event

class AlfheimModeChangedEvent(val esm: Boolean, val mmo: Boolean, val esmOld: Boolean, val mmoOld: Boolean): Event()