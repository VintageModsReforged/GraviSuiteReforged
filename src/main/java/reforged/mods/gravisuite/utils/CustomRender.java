package reforged.mods.gravisuite.utils;

import cpw.mods.fml.client.TextureFXManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class CustomRender {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public final MapItemRenderer mapItemRenderer;

    public CustomRender() {
        this.mapItemRenderer = new MapItemRenderer(mc.fontRenderer, mc.gameSettings, mc.renderEngine);
    }

    public static void renderItem(String textureName, int textureIndex, ItemStack par2ItemStack, int par3) {
        GL11.glPushMatrix();
        GL11.glBindTexture(3553, mc.renderEngine.getTexture(textureName));
        Tessellator var5 = Tessellator.instance;
        float var7 = ((float) (textureIndex % 16 * 16) + 0.0F) / 256.0F;
        float var8 = ((float) (textureIndex % 16 * 16) + 15.99F) / 256.0F;
        float var9 = ((float) (textureIndex / 16 * 16) + 0.0F) / 256.0F;
        float var10 = ((float) (textureIndex / 16 * 16) + 15.99F) / 256.0F;
        float var11 = 0.0F;
        float var12 = 0.3F;
        renderItemIn2D(var5, var8, var9, var7, var10, 0.0625F);
        if (par2ItemStack != null && par2ItemStack.hasEffect() && par3 == 0) {
            GL11.glDepthFunc(514);
            GL11.glDisable(2896);
            mc.renderEngine.bindTexture(mc.renderEngine.getTexture("%blur%/misc/glint.png"));
            GL11.glEnable(3042);
            GL11.glBlendFunc(768, 1);
            float var14 = 0.76F;
            GL11.glColor4f(0.5F * var14, 0.25F * var14, 0.8F * var14, 1.0F);
            GL11.glMatrixMode(5890);
            GL11.glPushMatrix();
            float var15 = 0.125F;
            GL11.glScalef(var15, var15, var15);
            float var16 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
            GL11.glTranslatef(var16, 0.0F, 0.0F);
            GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
            renderItemIn2D(var5, 0.0F, 0.0F, 1.0F, 1.0F, 0.0625F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(var15, var15, var15);
            var16 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
            GL11.glTranslatef(-var16, 0.0F, 0.0F);
            GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
            renderItemIn2D(var5, 0.0F, 0.0F, 1.0F, 1.0F, 0.0625F);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
            GL11.glDisable(3042);
            GL11.glEnable(2896);
            GL11.glDepthFunc(515);
        }

        GL11.glPopMatrix();
    }

    public static void renderItemIn2D(Tessellator par0Tessellator, float par1, float par2, float par3, float par4, float par5) {
        float var6 = 1.0F;
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 0.0F, 1.0F);
        par0Tessellator.addVertexWithUV(0.0, 0.0, 0.0, (double) par1, (double) par4);
        par0Tessellator.addVertexWithUV((double) var6, 0.0, 0.0, (double) par3, (double) par4);
        par0Tessellator.addVertexWithUV((double) var6, 1.0, 0.0, (double) par3, (double) par2);
        par0Tessellator.addVertexWithUV(0.0, 1.0, 0.0, (double) par1, (double) par2);
        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 0.0F, -1.0F);
        par0Tessellator.addVertexWithUV(0.0, 1.0, (double) (0.0F - par5), (double) par1, (double) par2);
        par0Tessellator.addVertexWithUV((double) var6, 1.0, (double) (0.0F - par5), (double) par3, (double) par2);
        par0Tessellator.addVertexWithUV((double) var6, 0.0, (double) (0.0F - par5), (double) par3, (double) par4);
        par0Tessellator.addVertexWithUV(0.0, 0.0, (double) (0.0F - par5), (double) par1, (double) par4);
        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        int tileSize = TextureFXManager.instance().getTextureDimensions(GL11.glGetInteger(32873)).width / 16;
        float tx = 1.0F / (float) (32 * tileSize);
        float tz = 1.0F / (float) tileSize;

        int var7;
        float var8;
        float var9;
        float var10;
        for (var7 = 0; var7 < tileSize; ++var7) {
            var8 = (float) var7 / (float) tileSize;
            var9 = par1 + (par3 - par1) * var8 - tx;
            var10 = var6 * var8;
            par0Tessellator.addVertexWithUV((double) var10, 0.0, (double) (0.0F - par5), (double) var9, (double) par4);
            par0Tessellator.addVertexWithUV((double) var10, 0.0, 0.0, (double) var9, (double) par4);
            par0Tessellator.addVertexWithUV((double) var10, 1.0, 0.0, (double) var9, (double) par2);
            par0Tessellator.addVertexWithUV((double) var10, 1.0, (double) (0.0F - par5), (double) var9, (double) par2);
        }

        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(1.0F, 0.0F, 0.0F);

        for (var7 = 0; var7 < tileSize; ++var7) {
            var8 = (float) var7 / (float) tileSize;
            var9 = par1 + (par3 - par1) * var8 - tx;
            var10 = var6 * var8 + tz;
            par0Tessellator.addVertexWithUV((double) var10, 1.0, (double) (0.0F - par5), (double) var9, (double) par2);
            par0Tessellator.addVertexWithUV((double) var10, 1.0, 0.0, (double) var9, (double) par2);
            par0Tessellator.addVertexWithUV((double) var10, 0.0, 0.0, (double) var9, (double) par4);
            par0Tessellator.addVertexWithUV((double) var10, 0.0, (double) (0.0F - par5), (double) var9, (double) par4);
        }

        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 1.0F, 0.0F);

        for (var7 = 0; var7 < tileSize; ++var7) {
            var8 = (float) var7 / (float) tileSize;
            var9 = par4 + (par2 - par4) * var8 - tx;
            var10 = var6 * var8 + tz;
            par0Tessellator.addVertexWithUV(0.0, (double) var10, 0.0, (double) par1, (double) var9);
            par0Tessellator.addVertexWithUV((double) var6, (double) var10, 0.0, (double) par3, (double) var9);
            par0Tessellator.addVertexWithUV((double) var6, (double) var10, (double) (0.0F - par5), (double) par3, (double) var9);
            par0Tessellator.addVertexWithUV(0.0, (double) var10, (double) (0.0F - par5), (double) par1, (double) var9);
        }

        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, -1.0F, 0.0F);

        for (var7 = 0; var7 < tileSize; ++var7) {
            var8 = (float) var7 / (float) tileSize;
            var9 = par4 + (par2 - par4) * var8 - tx;
            var10 = var6 * var8;
            par0Tessellator.addVertexWithUV((double) var6, (double) var10, 0.0, (double) par3, (double) var9);
            par0Tessellator.addVertexWithUV(0.0, (double) var10, 0.0, (double) par1, (double) var9);
            par0Tessellator.addVertexWithUV(0.0, (double) var10, (double) (0.0F - par5), (double) par1, (double) var9);
            par0Tessellator.addVertexWithUV((double) var6, (double) var10, (double) (0.0F - par5), (double) par3, (double) var9);
        }

        par0Tessellator.draw();
    }
}
