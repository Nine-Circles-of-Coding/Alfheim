package alfheim.common.item

import alexsocol.asjlib.*
import alfheim.common.entity.EntityThunderChakram
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import vazkii.botania.common.achievement.*

class ItemThunderChakram: ItemMod("ThunderChakram"), ICraftAchievement {
	
	init {
		maxStackSize = 6
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		--stack.stackSize
		player.playSoundAtEntity("random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f))
		
		if (!world.isRemote)
			EntityThunderChakram(world, player).spawn()
		
		return stack
	}
	
	override fun getAchievementOnCraft(stack: ItemStack?, player: EntityPlayer?, matrix: IInventory?) = ModAchievements.terrasteelWeaponCraft!!
}
