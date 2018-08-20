package alfheim.common.core.util;

import alexsocol.asjlib.ASJUtilities;
import alfheim.common.core.registry.AlfheimRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class KeyBindingsUtils {
	
	public static void enableFlight(EntityPlayerMP player, boolean boost) {
		if (player.capabilities.isCreativeMode || player.getEntityAttribute(AlfheimRegistry.RACE).getAttributeValue() == 0) return;
		player.capabilities.allowFlying = true;
		player.capabilities.isFlying = !player.capabilities.isFlying;
		player.sendPlayerAbilities();
		if (boost) player.getAttributeMap().getAttributeInstance(AlfheimRegistry.FLIGHT).setBaseValue(player.getAttributeMap().getAttributeInstance(AlfheimRegistry.FLIGHT).getAttributeValue() - 300);
	}

	public static void atack(EntityPlayerMP player) {
		MovingObjectPosition mop = ASJUtilities.getMouseOver(player, player.theItemInWorldManager.getBlockReachDistance(), true);
		if (mop != null && mop.typeOfHit == MovingObjectType.ENTITY && mop.entityHit != null) {
			player.attackTargetEntityWithCurrentItem(mop.entityHit);
		}
	} 
}
