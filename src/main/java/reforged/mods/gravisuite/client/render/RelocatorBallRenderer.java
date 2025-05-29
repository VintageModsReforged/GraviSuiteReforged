package reforged.mods.gravisuite.client.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.items.tools.relocator.EntityRelocatorBall;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelocatorBallRenderer extends Render {

    private static final ResourceLocation plazmaTextloc = new ResourceLocation(Refs.id, "textures/models/plazma.png");

    private static final ResourceLocation particlesTextloc = new ResourceLocation(Refs.id, "textures/models/particles.png");

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


    public void renderCore(EntityRelocatorBall paramEntityPlasmaBall, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2) {
        int i = 0;
        int j = 0;
        i = getTextureSize("textures/models/plazma.png", 64);
        j = getTextureSize("textures/models/particles.png", 32);
        float f1 = ActiveRenderInfo.rotationX;
        float f2 = ActiveRenderInfo.rotationXZ;
        float f3 = ActiveRenderInfo.rotationZ;
        float f4 = ActiveRenderInfo.rotationYZ;
        float f5 = ActiveRenderInfo.rotationXY;
        float f6 = 1.0F;
        double d1 = (float) paramDouble2;
        double d2 = (float) paramDouble3;
        Tessellator tessellator = Tessellator.instance;
        Color color = Helpers.convertRGBtoColor(226, 88, 255);
        if (paramEntityPlasmaBall.getActionType() == 0)
            color = Helpers.convertRGBtoColor(254, 255, 131);
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        (FMLClientHandler.instance().getClient()).renderEngine.bindTexture(plazmaTextloc);
        int k = ((Entity) paramEntityPlasmaBall).ticksExisted % 16;
        float f7 = (i * 4);
        float f8 = i - 0.01F;
        float f9 = 1.0F / (i * i) * 2.0F;
        float f10 = 1.0F / i;
        float f11 = ((k % 4 * i) + 0.0F) / f7;
        float f12 = ((k % 4 * i) + f8) / f7;
        float f13 = ((k / 4 * i) + 0.0F) / f7;
        float f14 = ((k / 4 * i) + f8) / f7;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(240);
        tessellator.setColorRGBA_F(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
        tessellator.addVertexWithUV(paramDouble1 - (f1 * f6) - (f4 * f6), d1 - (f2 * f6), d2 - (f3 * f6) - (f5 * f6), f12, f14);
        tessellator.addVertexWithUV(paramDouble1 - (f1 * f6) + (f4 * f6), d1 + (f2 * f6), d2 - (f3 * f6) + (f5 * f6), f12, f13);
        tessellator.addVertexWithUV(paramDouble1 + (f1 * f6) + (f4 * f6), d1 + (f2 * f6), d2 + (f3 * f6) + (f5 * f6), f11, f13);
        tessellator.addVertexWithUV(paramDouble1 + (f1 * f6) - (f4 * f6), d1 - (f2 * f6), d2 + (f3 * f6) - (f5 * f6), f11, f14);
        tessellator.draw();
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        (FMLClientHandler.instance().getClient()).renderEngine.bindTexture(particlesTextloc);
        int m = ((Entity) paramEntityPlasmaBall).ticksExisted % 16;
        k = 24 + m;
        float f15 = (j * 8);
        f8 = j - 0.01F;
        f9 = 1.0F / (j * j) * 2.0F;
        f10 = 1.0F / j;
        f11 = ((k % 8 * j) + 0.0F) / f15;
        f12 = ((k % 8 * j) + f8) / f15;
        f13 = ((k / 8 * j) + 0.0F) / f15;
        f14 = ((k / 8 * j) + f8) / f15;
        float f16 = MathHelper.sin((((Entity) paramEntityPlasmaBall).ticksExisted + paramFloat2) / 10.0F) * 0.1F;
        f6 = 0.4F + f16;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(240);
        tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator.addVertexWithUV(paramDouble1 - (f1 * f6) - (f4 * f6), d1 - (f2 * f6), d2 - (f3 * f6) - (f5 * f6), f12, f14);
        tessellator.addVertexWithUV(paramDouble1 - (f1 * f6) + (f4 * f6), d1 + (f2 * f6), d2 - (f3 * f6) + (f5 * f6), f12, f13);
        tessellator.addVertexWithUV(paramDouble1 + (f1 * f6) + (f4 * f6), d1 + (f2 * f6), d2 + (f3 * f6) + (f5 * f6), f11, f13);
        tessellator.addVertexWithUV(paramDouble1 + (f1 * f6) - (f4 * f6), d1 - (f2 * f6), d2 + (f3 * f6) - (f5 * f6), f11, f14);
        tessellator.draw();
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity paramEntity, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2) {
        renderCore((EntityRelocatorBall) paramEntity, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return plazmaTextloc;
    }
}
