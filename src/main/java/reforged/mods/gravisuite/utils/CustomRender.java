package reforged.mods.gravisuite.utils;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import org.lwjgl.opengl.GL11;
import reforged.mods.gravisuite.items.tools.ItemGraviTool;

@SideOnly(Side.CLIENT)
public class CustomRender {

    private static final Minecraft mc = Minecraft.getMinecraft();
    public final MapItemRenderer mapItemRenderer;

    public CustomRender() {
        this.mapItemRenderer = new MapItemRenderer(mc.fontRenderer, mc.gameSettings, mc.renderEngine);
    }

    public static void renderItem(int textureIndex, ItemStack par2ItemStack, int par3) {
        GL11.glPushMatrix();
        Icon icon = reforged.mods.gravisuite.items.tools.ItemGraviTool.iconsList[textureIndex];
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(ItemGraviTool.iconsList[textureIndex].getIconName());
        GL11.glBindTexture(3553, mc.renderEngine.getTexture(ItemGraviTool.iconsList[textureIndex].getIconName()));
        mc.renderEngine.getTexture("/gui/items.png");
        Tessellator tessellator = Tessellator.instance;
        float f = icon.getMinU();
        float f1 = icon.getMaxU();
        float f2 = icon.getMinV();
        float f3 = icon.getMaxV();
        renderItemIn2D(tessellator, f1, f2, f, f3, icon.getSheetWidth(), icon.getSheetHeight(), 0.0625F);
        if (par2ItemStack.hasEffect() && par3 == 0) {
            GL11.glDepthFunc(514);
            GL11.glDisable(2896);
            mc.renderEngine.getTexture("%blur%/misc/glint.png");
            GL11.glEnable(3042);
            GL11.glBlendFunc(768, 1);
            float f7 = 0.76F;
            GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
            GL11.glMatrixMode(5890);
            GL11.glPushMatrix();
            float f8 = 0.125F;
            GL11.glScalef(f8, f8, f8);
            float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
            GL11.glTranslatef(f9, 0.0F, 0.0F);
            GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
            renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(f8, f8, f8);
            f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
            GL11.glTranslatef(-f9, 0.0F, 0.0F);
            GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
            renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
            GL11.glDisable(3042);
            GL11.glEnable(2896);
            GL11.glDepthFunc(515);
        }

        GL11.glPopMatrix();
    }

    public static void renderItemIn2D(Tessellator par0Tessellator, float par1, float par2, float par3, float par4, int par5, int par6, float par7) {
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 0.0F, 1.0F);
        par0Tessellator.addVertexWithUV(0.0, 0.0, 0.0, (double)par1, (double)par4);
        par0Tessellator.addVertexWithUV(1.0, 0.0, 0.0, (double)par3, (double)par4);
        par0Tessellator.addVertexWithUV(1.0, 1.0, 0.0, (double)par3, (double)par2);
        par0Tessellator.addVertexWithUV(0.0, 1.0, 0.0, (double)par1, (double)par2);
        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 0.0F, -1.0F);
        par0Tessellator.addVertexWithUV(0.0, 1.0, (double)(0.0F - par7), (double)par1, (double)par2);
        par0Tessellator.addVertexWithUV(1.0, 1.0, (double)(0.0F - par7), (double)par3, (double)par2);
        par0Tessellator.addVertexWithUV(1.0, 0.0, (double)(0.0F - par7), (double)par3, (double)par4);
        par0Tessellator.addVertexWithUV(0.0, 0.0, (double)(0.0F - par7), (double)par1, (double)par4);
        par0Tessellator.draw();
        float f5 = (float)par5 * (par1 - par3);
        float f6 = (float)par6 * (par4 - par2);
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(-1.0F, 0.0F, 0.0F);

        int k;
        float f7;
        float f8;
        for(k = 0; (float)k < f5; ++k) {
            f7 = (float)k / f5;
            f8 = par1 + (par3 - par1) * f7 - 0.5F / (float)par5;
            par0Tessellator.addVertexWithUV((double)f7, 0.0, (double)(0.0F - par7), (double)f8, (double)par4);
            par0Tessellator.addVertexWithUV((double)f7, 0.0, 0.0, (double)f8, (double)par4);
            par0Tessellator.addVertexWithUV((double)f7, 1.0, 0.0, (double)f8, (double)par2);
            par0Tessellator.addVertexWithUV((double)f7, 1.0, (double)(0.0F - par7), (double)f8, (double)par2);
        }

        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(1.0F, 0.0F, 0.0F);

        float f9;
        for(k = 0; (float)k < f5; ++k) {
            f7 = (float)k / f5;
            f8 = par1 + (par3 - par1) * f7 - 0.5F / (float)par5;
            f9 = f7 + 1.0F / f5;
            par0Tessellator.addVertexWithUV((double)f9, 1.0, (double)(0.0F - par7), (double)f8, (double)par2);
            par0Tessellator.addVertexWithUV((double)f9, 1.0, 0.0, (double)f8, (double)par2);
            par0Tessellator.addVertexWithUV((double)f9, 0.0, 0.0, (double)f8, (double)par4);
            par0Tessellator.addVertexWithUV((double)f9, 0.0, (double)(0.0F - par7), (double)f8, (double)par4);
        }

        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 1.0F, 0.0F);

        for(k = 0; (float)k < f6; ++k) {
            f7 = (float)k / f6;
            f8 = par4 + (par2 - par4) * f7 - 0.5F / (float)par6;
            f9 = f7 + 1.0F / f6;
            par0Tessellator.addVertexWithUV(0.0, (double)f9, 0.0, (double)par1, (double)f8);
            par0Tessellator.addVertexWithUV(1.0, (double)f9, 0.0, (double)par3, (double)f8);
            par0Tessellator.addVertexWithUV(1.0, (double)f9, (double)(0.0F - par7), (double)par3, (double)f8);
            par0Tessellator.addVertexWithUV(0.0, (double)f9, (double)(0.0F - par7), (double)par1, (double)f8);
        }

        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, -1.0F, 0.0F);

        for(k = 0; (float)k < f6; ++k) {
            f7 = (float)k / f6;
            f8 = par4 + (par2 - par4) * f7 - 0.5F / (float)par6;
            par0Tessellator.addVertexWithUV(1.0, (double)f7, 0.0, (double)par3, (double)f8);
            par0Tessellator.addVertexWithUV(0.0, (double)f7, 0.0, (double)par1, (double)f8);
            par0Tessellator.addVertexWithUV(0.0, (double)f7, (double)(0.0F - par7), (double)par1, (double)f8);
            par0Tessellator.addVertexWithUV(1.0, (double)f7, (double)(0.0F - par7), (double)par3, (double)f8);
        }

        par0Tessellator.draw();
    }
}
