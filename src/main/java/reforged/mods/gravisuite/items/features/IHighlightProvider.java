package reforged.mods.gravisuite.items.features;

import mods.vintage.core.helpers.pos.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

import java.util.List;

public interface IHighlightProvider {

    boolean isProvidingHighlight(ItemStack stack);
    List<BlockPos> getHighlightArea(BlockPos start, EntityPlayer player, MovingObjectPosition hitResult);
    int[] getHighlightColor(ItemStack stack);
}
