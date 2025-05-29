package reforged.mods.gravisuite.utils;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.core.IC2;
import mods.vintage.core.platform.lang.FormattedTranslator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import org.lwjgl.opengl.GL11;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.items.tools.relocator.TeleportPoint;

import java.awt.*;
import java.util.List;

public class Helpers {

    public static Entity teleportEntity(Entity entityToTeleport, TeleportPoint point) {
        boolean bool = entityToTeleport.worldObj.provider.dimensionId != point.DIMENSION_ID;
        if (bool) {
            teleportToDimensionNew(entityToTeleport, point);
        } else {
            Entity entity = entityToTeleport.ridingEntity;
            if (entityToTeleport.ridingEntity != null) {
                entityToTeleport.mountEntity(null);
                entity = teleportEntity(entity, point);
            }
            if (entityToTeleport instanceof EntityPlayerMP) {
                EntityPlayerMP entityPlayerMP = (EntityPlayerMP) entityToTeleport;
                entityPlayerMP.setPositionAndUpdate(point.POS.getX(), point.POS.getY(), point.POS.getZ());
            } else {
                entityToTeleport.setPosition(point.POS.getX(), point.POS.getY(), point.POS.getZ());
            }
            entityToTeleport.setLocationAndAngles(point.POS.getX(), point.POS.getY(), point.POS.getZ(), (float) point.YAW, (float) point.PITCH);
            if (entity != null)
                entityToTeleport.mountEntity(entity);
        }
        return entityToTeleport;
    }

    public static Entity teleportToDimensionNew(Entity entity, TeleportPoint target) {
        if (IC2.platform.isRendering()) return entity;

        Entity riding = entity.ridingEntity;
        if (riding != null) {
            entity.mountEntity(null);
            riding = teleportToDimensionNew(riding, target);
        }

        int oldDim = entity.dimension;
        int newDim = target.DIMENSION_ID;
        MinecraftServer server = MinecraftServer.getServer();
        WorldServer oldWorld = server.worldServerForDimension(oldDim);
        WorldServer newWorld = server.worldServerForDimension(newDim);

        GraviSuite.logger.info("Teleport entity: " + entity);

        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;

            // Transfer
            server.getConfigurationManager().transferPlayerToDimension(player, newDim, new TeleporterDummy(newWorld));

            // Set orientation
            player.setLocationAndAngles(target.POS.getX(), target.POS.getY(), target.POS.getZ(), (float) target.YAW, (float) target.PITCH);
            player.setPositionAndUpdate(target.POS.getX(), target.POS.getY(), target.POS.getZ());

            // Sync
            player.theItemInWorldManager.setWorld(newWorld);
            server.getConfigurationManager().updateTimeAndWeatherForPlayer(player, newWorld);
            server.getConfigurationManager().syncPlayerInventory(player);

            return player;
        }

        // For non-player entities, load target chunk
        int chunkX = target.POS.getX() >> 4;
        int chunkZ = target.POS.getZ() >> 4;
        newWorld.theChunkProviderServer.loadChunk(chunkX, chunkZ);

        // Remove from old world and prepare NBT
        oldWorld.removeEntity(entity);
        entity.isDead = false;
        NBTTagCompound nbt = new NBTTagCompound();
        entity.writeToNBTOptional(nbt);
        entity.isDead = true;

        // Create and spawn entity in new world
        Entity newEntity = EntityList.createEntityFromNBT(nbt, newWorld);
        if (newEntity != null) {
            newEntity.setLocationAndAngles(target.POS.getX(), target.POS.getY(), target.POS.getZ(), (float) target.YAW, (float) target.PITCH);
            newEntity.dimension = newDim;
            newWorld.spawnEntityInWorld(newEntity);
            newWorld.updateEntityWithOptionalForce(newEntity, false);
        }

        // Remount if needed
        if (riding != null && newEntity != null) {
            newEntity.mountEntity(riding);
            newWorld.updateEntities();
            teleportEntity(newEntity, target);
        }

        return newEntity;
    }


    public static int convertRGBcolorToInt(int paramInt1, int paramInt2, int paramInt3) {
        float f = 255.0F;
        Color color = new Color(paramInt1 / f, paramInt2 / f, paramInt3 / f);
        return color.getRGB();
    }

    public static Color convertRGBtoColor(int paramInt1, int paramInt2, int paramInt3) {
        float f = 255.0F;
        return new Color(paramInt1 / f, paramInt2 / f, paramInt3 / f);
    }

    public static void renderTooltip(int paramInt1, int paramInt2, List<String> strings) {
        int i = convertRGBcolorToInt(0, 149, 218);
        int j = convertRGBcolorToInt(119, 187, 218);
        GL11.glDisable(32826);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(2896);
        GL11.glDisable(2929);
        if (!strings.isEmpty()) {
            int k = 0;
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
            for (String str : strings) {
                int i3 = fontRenderer.getStringWidth(str);
                if (i3 > k)
                    k = i3;
            }
            int m = paramInt1 + 12;
            int n = paramInt2 - 12;
            int i1 = 8;
            if (strings.size() > 1)
                i1 += 2 + (strings.size() - 1) * 10;
            float f = 300.0F;
            drawGradientRect(m - 3, n - 4, f, m + k + 3, n - 3, j, j);
            drawGradientRect(m - 3, n + i1 + 3, f, m + k + 3, n + i1 + 4, j, j);
            drawGradientRect(m - 3, n - 3, f, m + k + 3, n + i1 + 3, j, j);
            drawGradientRect(m - 4, n - 3, f, m - 3, n + i1 + 3, j, j);
            drawGradientRect(m + k + 3, n - 3, f, m + k + 4, n + i1 + 3, j, j);
            int i2 = (i & 0xFFFFFF) >> 1 | i & 0xFF000000;
            drawGradientRect(m - 3, n - 3 + 1, f, m - 3 + 1, n + i1 + 3 - 1, i, i2);
            drawGradientRect(m + k + 2, n - 3 + 1, f, m + k + 3, n + i1 + 3 - 1, i, i2);
            drawGradientRect(m - 3, n - 3, f, m + k + 3, n - 3 + 1, i, i);
            drawGradientRect(m - 3, n + i1 + 2, f, m + k + 3, n + i1 + 3, i2, i2);
            for (byte b = 0; b < strings.size(); b++) {
                String str = strings.get(b);
                fontRenderer.drawStringWithShadow(str, m, n, -1);
                if (b == 0)
                    n += 2;
                n += 10;
            }
        }
    }

    public static void drawGradientRect(int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        float f1 = (paramInt5 >> 24 & 0xFF) / 255.0F;
        float f2 = (paramInt5 >> 16 & 0xFF) / 255.0F;
        float f3 = (paramInt5 >> 8 & 0xFF) / 255.0F;
        float f4 = (paramInt5 & 0xFF) / 255.0F;
        float f5 = (paramInt6 >> 24 & 0xFF) / 255.0F;
        float f6 = (paramInt6 >> 16 & 0xFF) / 255.0F;
        float f7 = (paramInt6 >> 8 & 0xFF) / 255.0F;
        float f8 = (paramInt6 & 0xFF) / 255.0F;
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f2, f3, f4, f1);
        tessellator.addVertex(paramInt3, paramInt2, paramFloat);
        tessellator.addVertex(paramInt1, paramInt2, paramFloat);
        tessellator.setColorRGBA_F(f6, f7, f8, f5);
        tessellator.addVertex(paramInt1, paramInt4, paramFloat);
        tessellator.addVertex(paramInt3, paramInt4, paramFloat);
        tessellator.draw();
        GL11.glShadeModel(7424);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glEnable(3553);
    }

    public static ItemStack withSize(ItemStack stack, int count) {
        ItemStack returnStack = stack.copy();
        returnStack.stackSize = count;
        return returnStack;
    }

    public static ItemStack getCharged(Item item, int charge) {
        if (!(item instanceof IElectricItem)) {
            throw new IllegalArgumentException(item + " must be an instanceof IElectricItem");
        } else {
            ItemStack ret = new ItemStack(item);
            ElectricItem.manager.charge(ret, charge, Integer.MAX_VALUE, true, false);
            return ret;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addChargeVariants(Item item, List list) {
        list.add(getCharged(item, 0));
        list.add(getCharged(item, Integer.MAX_VALUE));
    }

    public static int getCharge(ItemStack stack) {
        NBTTagCompound tag = getOrCreateTag(stack);
        return tag.getInteger("charge");
    }

    public static NBTTagCompound getOrCreateTag(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound("gravi_data");
            stack.setTagCompound(tag);
        }
        return tag;
    }

    public static String pressForInfo(String data) {
        return FormattedTranslator.GRAY.format("message.info.press", FormattedTranslator.GOLD.format(data));
    }

    public static String pressXForY(String message, String key1, String action) {
        return FormattedTranslator.GRAY.format(message, FormattedTranslator.GOLD.literal(key1), FormattedTranslator.YELLOW.format(action));
    }

    public static String pressXAndYForZ(String message, String key1, String key2, String action) {
        return FormattedTranslator.GRAY.format(message, FormattedTranslator.GOLD.literal(key1), FormattedTranslator.GOLD.literal(key2), FormattedTranslator.YELLOW.format(action));
    }

    public static String clickFor(String key, String message) {
        return FormattedTranslator.GRAY.format("message.info.click.block", FormattedTranslator.GOLD.literal(key), FormattedTranslator.YELLOW.format(message));
    }

    public static String getStatusMessage(boolean status) {
        return status ? Refs.status_on : Refs.status_off;
    }
}
