package reforged.mods.gravisuite.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.tiles.TileEntityRelocatorPortal;

public class BlockRelocatorPortalRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int i, int i1, RenderBlocks renderBlocks) {
        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        TileEntityRelocatorPortal te = new TileEntityRelocatorPortal();
        new TileRelocatorPortalRenderer().renderTileEntityAt(te, 0, 0, 0, 0);
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess iBlockAccess, int i, int i1, int i2, Block block, int i3, RenderBlocks renderBlocks) {
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory() {
        return true;
    }

    @Override
    public int getRenderId() {
        return GraviSuite.blockRelocatorPortalRenderID;
    }
}
