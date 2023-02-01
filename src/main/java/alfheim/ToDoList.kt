//package alfheim
//
//import alexsocol.asjlib.*
//import alexsocol.patcher.KotlinAdapter
//import alfheim.api.ModInfo
//import cpw.mods.fml.common.Mod
//import cpw.mods.fml.common.event.FMLPreInitializationEvent
//import cpw.mods.fml.common.eventhandler.SubscribeEvent
//import cpw.mods.fml.common.gameevent.PlayerEvent
//
//@Mod(modid = "todolist", modLanguageAdapter = KotlinAdapter.className, dependencies = "required-after:alfheim")
//object ToDoList {
//
//	val list = arrayOf(
//		"",
//	)
//
//	@Mod.EventHandler
//	fun preInit(e: FMLPreInitializationEvent) {
//		if (ModInfo.DEV) eventFML()
//	}
//
//	@SubscribeEvent
//	fun onPlayerLoggedInEvent(e: PlayerEvent.PlayerLoggedInEvent) {
//		list.forEach {
//			ASJUtilities.say(e.player, it)
//		}
//	}
//}
//
