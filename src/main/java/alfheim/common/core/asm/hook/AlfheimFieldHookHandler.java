package alfheim.common.core.asm.hook;

import alexsocol.asjlib.asm.HookField;
import net.minecraft.entity.player.EntityPlayer;

@SuppressWarnings("unused")
class AlfheimFieldHookHandler {
	
	@HookField(targetClassName = "net.minecraft.inventory.ContainerWorkbench")
	public EntityPlayer alfheim_synthetic_thePlayer;
	
	@HookField(targetClassName = "net.minecraft.util.DamageSource")
	public int alfheim_synthetic_elementalFlag;
}