package thermalexpansion.api.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IDismantleable {

    ItemStack dismantleBlock(EntityPlayer entityPlayer, World world, int x, int y, int z, boolean returnBlock);
    boolean canDismantle(EntityPlayer entityPlayer, World world, int x, int y, int z);
}
