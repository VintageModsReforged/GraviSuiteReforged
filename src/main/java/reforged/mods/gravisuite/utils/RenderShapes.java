package reforged.mods.gravisuite.utils;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class RenderShapes {

    /**
     * Draws a translucent colored outline box (like vanilla block highlight).
     *
     * @param box The bounding box in world coords
     * @param r   Red   (0–255)
     * @param g   Green (0–255)
     * @param b   Blue  (0–255)
     * @param a   Alpha (0.0–1.0)
     * @param partialTicks render partial ticks
     * @param player the client player
     */
    public static void drawShape(AxisAlignedBB box, int r, int g, int b, float a,
                                 float partialTicks, EntityPlayer player) {

        double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        AxisAlignedBB shifted = box.getOffsetBoundingBox(-px, -py, -pz);

        Tessellator tess = Tessellator.instance;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LINE_BIT | GL11.GL_COLOR_BUFFER_BIT);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(true); // restore depth mask to allow hand rendering
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glLineWidth(2.0F);

        float fr = r / 255.0F;
        float fg = g / 255.0F;
        float fb = b / 255.0F;

        GL11.glColor4f(fr, fg, fb, a);

        tess.startDrawing(GL11.GL_LINES);
        addBoxEdges(tess, shifted);
        tess.draw();

        // Restore GL state
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private static void addBoxEdges(Tessellator tess, AxisAlignedBB bb) {
        double x1 = bb.minX, y1 = bb.minY, z1 = bb.minZ;
        double x2 = bb.maxX, y2 = bb.maxY, z2 = bb.maxZ;

        // Bottom square
        tess.addVertex(x1, y1, z1); tess.addVertex(x2, y1, z1);
        tess.addVertex(x2, y1, z1); tess.addVertex(x2, y1, z2);
        tess.addVertex(x2, y1, z2); tess.addVertex(x1, y1, z2);
        tess.addVertex(x1, y1, z2); tess.addVertex(x1, y1, z1);

        // Top square
        tess.addVertex(x1, y2, z1); tess.addVertex(x2, y2, z1);
        tess.addVertex(x2, y2, z1); tess.addVertex(x2, y2, z2);
        tess.addVertex(x2, y2, z2); tess.addVertex(x1, y2, z2);
        tess.addVertex(x1, y2, z2); tess.addVertex(x1, y2, z1);

        // Vertical edges
        tess.addVertex(x1, y1, z1); tess.addVertex(x1, y2, z1);
        tess.addVertex(x2, y1, z1); tess.addVertex(x2, y2, z1);
        tess.addVertex(x2, y1, z2); tess.addVertex(x2, y2, z2);
        tess.addVertex(x1, y1, z2); tess.addVertex(x1, y2, z2);
    }
}

