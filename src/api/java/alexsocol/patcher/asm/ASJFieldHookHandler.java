package alexsocol.patcher.asm;

import net.minecraft.entity.player.EntityPlayer;

import alexsocol.asjlib.asm.HookField;

public class ASJFieldHookHandler {

    @HookField(targetClassName = "net.minecraft.util.FoodStats")
    public EntityPlayer host;
}
