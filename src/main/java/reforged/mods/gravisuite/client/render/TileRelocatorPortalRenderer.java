package reforged.mods.gravisuite.client.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.tiles.TileEntityRelocatorPortal;
import reforged.mods.gravisuite.utils.Helpers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileRelocatorPortalRenderer extends TileEntitySpecialRenderer {

    private static final String plazmaTextloc = "/mods/gravisuite/textures/models/plazma.png";

    private static final String particlesTextloc = "/mods/gravisuite/textures/models/particles.png";

    private static final Map<List<Serializable>, Integer> textureSizeCache = new HashMap<List<Serializable>, Integer>();

    public int ticker;

    public static int getTextureSize(String path, int paramInt) {
        List<Serializable> key = Arrays.asList(new Serializable[]{path, paramInt});
        if (textureSizeCache.get(key) != null)
            return textureSizeCache.get(key);
        try {
            InputStream inputStream = GraviSuite.class.getResourceAsStream("/mods/gravisuite/" + path);
            if (inputStream == null)
                throw new Exception("Image not found: " + path);
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            int i = bufferedImage.getWidth() / paramInt;
            textureSizeCache.put(key, i);
            return i;
        } catch (Exception exception) {
            exception.printStackTrace();
            return 16;
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity paramTileEntity, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat) {
        renderTileEntityAt((TileEntityRelocatorPortal) paramTileEntity, paramDouble1, paramDouble2, paramDouble3, paramFloat);
    }

    public void renderCore(TileEntity paramTileEntity, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat) {
        int texturePlasma = this.tileEntityRenderer.renderEngine.getTexture(plazmaTextloc);
        int textureParticles = this.tileEntityRenderer.renderEngine.getTexture(particlesTextloc);
        int i = 0;
        int j = 0;
        this.ticker++;
        if (this.ticker > 161)
            this.ticker = 1;
        i = getTextureSize("textures/models/plazma.png", 64);
        j = getTextureSize("textures/models/particles.png", 32);
        float f1 = ActiveRenderInfo.rotationX;
        float f2 = ActiveRenderInfo.rotationXZ;
        float f3 = ActiveRenderInfo.rotationZ;
        float f4 = ActiveRenderInfo.rotationYZ;
        float f5 = ActiveRenderInfo.rotationXY;
        float f6 = 1.2F;
        float f7 = (float) paramDouble1 + 0.5F;
        float f8 = (float) paramDouble2 + 0.5F;
        float f9 = (float) paramDouble3 + 0.5F;
        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        (FMLClientHandler.instance().getClient()).renderEngine.bindTexture(texturePlasma);
        int k = this.ticker % 16;
        float f10 = (i * 4);
        float f11 = i - 0.01F;
        float f12 = 1.0F / (i * i) * 2.0F;
        float f13 = 1.0F / i;
        float f14 = ((k % 4 * i) + 0.0F) / f10;
        float f15 = ((k % 4 * i) + f11) / f10;
        float f16 = ((k / 4 * i) + 0.0F) / f10;
        float f17 = ((k / 4 * i) + f11) / f10;
        tessellator.startDrawingQuads();
        Color color = Helpers.convertRGBtoColor(226, 88, 255);
        tessellator.setColorRGBA_F(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
        tessellator.addVertexWithUV((f7 - f1 * f6 - f4 * f6), (f8 - f2 * f6), (f9 - f3 * f6 - f5 * f6), f15, f17);
        tessellator.addVertexWithUV((f7 - f1 * f6 + f4 * f6), (f8 + f2 * f6), (f9 - f3 * f6 + f5 * f6), f15, f16);
        tessellator.addVertexWithUV((f7 + f1 * f6 + f4 * f6), (f8 + f2 * f6), (f9 + f3 * f6 + f5 * f6), f14, f16);
        tessellator.addVertexWithUV((f7 + f1 * f6 - f4 * f6), (f8 - f2 * f6), (f9 + f3 * f6 - f5 * f6), f14, f17);
        tessellator.draw();
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        (FMLClientHandler.instance().getClient()).renderEngine.bindTexture(textureParticles);
        int m = this.ticker % 16;
        k = 24 + m;
        float f18 = (j * 8);
        f11 = j - 0.01F;
        f12 = 1.0F / (j * j) * 2.0F;
        f13 = 1.0F / j;
        f14 = ((k % 8 * j) + 0.0F) / f18;
        f15 = ((k % 8 * j) + f11) / f18;
        f16 = ((k / 8 * j) + 0.0F) / f18;
        f17 = ((k / 8 * j) + f11) / f18;
        float f19 = MathHelper.sin(this.ticker / 10.0F) * 0.1F;
        f6 = 0.4F + f19;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(240);
        tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator.addVertexWithUV((f7 - f1 * f6 - f4 * f6), (f8 - f2 * f6), (f9 - f3 * f6 - f5 * f6), f15, f17);
        tessellator.addVertexWithUV((f7 - f1 * f6 + f4 * f6), (f8 + f2 * f6), (f9 - f3 * f6 + f5 * f6), f15, f16);
        tessellator.addVertexWithUV((f7 + f1 * f6 + f4 * f6), (f8 + f2 * f6), (f9 + f3 * f6 + f5 * f6), f14, f16);
        tessellator.addVertexWithUV((f7 + f1 * f6 - f4 * f6), (f8 - f2 * f6), (f9 + f3 * f6 - f5 * f6), f14, f17);
        tessellator.draw();
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    public void renderTileEntityAt(TileEntityRelocatorPortal paramTileEntityRelocatorPortal, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) paramDouble1 + 0.5F, (float) paramDouble2 + 1.5F, (float) paramDouble3 + 0.5F);
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        renderCore(paramTileEntityRelocatorPortal, paramDouble1, paramDouble2, paramDouble3, paramFloat);
        GL11.glPopMatrix();
    }
}
