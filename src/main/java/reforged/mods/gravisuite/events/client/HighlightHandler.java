package reforged.mods.gravisuite.events.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.vintage.core.helpers.BlockHelper;
import mods.vintage.core.helpers.pos.BlockPos;
import mods.vintage.core.utils.VeinSearchResult;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.event.ForgeSubscribe;
import reforged.mods.gravisuite.items.features.IHighlightProvider;
import reforged.mods.gravisuite.items.tools.ItemAdvancedChainsaw;
import reforged.mods.gravisuite.utils.RenderShapes;
import reforged.mods.gravisuite.utils.VeinDataHandler;

import java.util.List;

@SideOnly(Side.CLIENT)
public class HighlightHandler {

    @ForgeSubscribe
    public void onBlockHighlightEvent(DrawBlockHighlightEvent e) {
        if (e.target.typeOfHit == EnumMovingObjectType.ENTITY) return;

        EntityPlayer player = e.player;
        World world = player.worldObj;
        BlockPos pos = BlockPos.fromMovingObjectPosition(e.target);
        Block targetBlock = BlockHelper.getBlock(world, pos);
        ItemStack heldStack = player.getHeldItem();

        if (heldStack == null || !(heldStack.getItem() instanceof IHighlightProvider)) return;
        IHighlightProvider provider = (IHighlightProvider) heldStack.getItem();
        if (!provider.isProvidingHighlight(heldStack)) return;

        int[] rgb = provider.getHighlightColor(heldStack);
        int count = 0;
        boolean saw = heldStack.getItem() instanceof ItemAdvancedChainsaw && BlockHelper.isLog(targetBlock);
        if (saw) {
            VeinSearchResult veinSearchResult = BlockHelper.scanForTree(world, pos);
            VeinDataHandler.setSearchResult(player, veinSearchResult);
            VeinDataHandler.setShow(player, true);
        } else {
            VeinDataHandler.setShow(player, false);
        }

        List<BlockPos> area = provider.getHighlightArea(pos, player, e.target);
        if (!area.isEmpty()) {
            for (BlockPos block : area) {
                if (!BlockHelper.isAir(world, block)) {
                    Block b = BlockHelper.getBlock(world, block);
                    if (b == null) return;
                    AxisAlignedBB box = b.getSelectedBoundingBoxFromPool(world, block.getX(), block.getY(), block.getZ())
                            .expand(0.002, 0.002, 0.002);
                    RenderShapes.drawShape(box, rgb[0], rgb[1], rgb[2], 0.4F, e.partialTicks, player);
                    ++count;
                }
            }
        } else {
            AxisAlignedBB box = targetBlock.getSelectedBoundingBoxFromPool(world, pos.getX(), pos.getY(), pos.getZ())
                    .expand(0.002, 0.002, 0.002);
            RenderShapes.drawShape(box, 255, 1, 1, 0.4F, e.partialTicks, player);
            ++count;
        }

        if (count > 0) e.setCanceled(true); // disable vanilla highlight
    }
}
