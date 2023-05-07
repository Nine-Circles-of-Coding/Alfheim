package alfheim.common.core.asm.hook;

import net.minecraft.entity.player.EntityPlayer;

import alexsocol.asjlib.asm.HookField;

@SuppressWarnings("unused")
class AlfheimFieldHookHandler {

    @HookField(targetClassName = "net.minecraft.inventory.ContainerWorkbench")
    public EntityPlayer alfheim_synthetic_thePlayer;

    @HookField(targetClassName = "net.minecraft.util.DamageSource")
    public int alfheim_synthetic_elementalFlag;
}
