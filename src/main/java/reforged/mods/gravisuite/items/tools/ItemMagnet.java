package reforged.mods.gravisuite.items.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.tools.base.ItemToolElectric;
import reforged.mods.gravisuite.proxy.ClientProxy;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.util.ArrayList;
import java.util.List;

public class ItemMagnet extends ItemToolElectric {

    public static final String NBT_ACTIVE = "active";
    public static final String NBT_TICKER = "magnetTicker";

    public static final int ENERGY_COST = 1;
    public byte MAGNET_TICKER;

    public ItemMagnet() {
        super(GraviSuiteConfig.MAGNET_ID, "magnet", 1, 500, 10000, EnumRarity.uncommon, EnumToolMaterial.WOOD);
        this.MAGNET_TICKER = 10;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean isDebugMode) {
        super.addInformation(stack, player, tooltip, isDebugMode);
        if (Helpers.isShiftKeyDown()) {
            tooltip.add(Helpers.pressXForY(Refs.to_enable_1, StatCollector.translateToLocal(ClientProxy.magnet_toggle.keyDescription), Refs.MAGNET_MODE + ".stat"));
        } else {
            tooltip.add(Helpers.pressForInfo(Refs.SNEAK_KEY));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return Helpers.getOrCreateTag(stack).getBoolean(NBT_ACTIVE);
    }

    public void changeMode(ItemStack stack, EntityPlayer player) {
        String message;
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        if (Helpers.getCharge(stack) > ENERGY_COST) {
            if (tag.getBoolean(NBT_ACTIVE)) {
                tag.setBoolean(NBT_ACTIVE, false);
                message = Refs.tool_mode_magnet + " " + Refs.status_off;
            } else {
                tag.setBoolean(NBT_ACTIVE, true);
                message = Refs.tool_mode_magnet + " " + Refs.status_on;
            }
        } else {
            message = Refs.status_low;
        }
        IC2.platform.messagePlayer(player, message);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int hand, boolean update) {
        EntityPlayer player = (EntityPlayer) entity;
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        byte ticker = tag.getByte(NBT_TICKER);
        if (IC2.platform.isSimulating()) {
            if (ticker > 0) {
                ticker--;
                tag.setByte(NBT_TICKER, ticker);
            }
            if (ClientProxy.magnet_toggle.pressed && ticker <= 0) {
                changeMode(stack, player);
                tag.setByte(NBT_TICKER, MAGNET_TICKER);
            }
            if (!tag.getBoolean(NBT_ACTIVE))
                return;
            if (world.getTotalWorldTime() % 2 != 0)
                return;
            if (world.getChunkProvider().chunkExists(player.chunkCoordX, player.chunkCoordZ)) {
                double x = player.posX;
                double y = player.posY;
                double z = player.posZ;
                int range = GraviSuiteConfig.magnet_range;
                AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x - range, y - range, z - range, x + range, y + range, z + range);
                List<Entity> items = selectEntitiesWithinAABB(world, aabb);
                if (items.isEmpty())
                    return;
                for (Entity item : items) {
                    if (item != null && !player.isSneaking()) {
                        if (ElectricItem.manager.canUse(stack, ENERGY_COST)) {
                            item.onCollideWithPlayer(player);
                            ElectricItem.manager.use(stack, ENERGY_COST, player);
                        } else {
                            tag.setBoolean(NBT_ACTIVE, false);
                        }
                    }
                }
            }
        }
    }

    private List<Entity> selectEntitiesWithinAABB(World world, AxisAlignedBB bb) {
        int itemsRemaining = 200;
        List<Entity> arraylist = new ArrayList<Entity>(itemsRemaining);

        final int minChunkX = MathHelper.floor_double((bb.minX) / 16.0D);
        final int maxChunkX = MathHelper.floor_double((bb.maxX) / 16.0D);
        final int minChunkZ = MathHelper.floor_double((bb.minZ) / 16.0D);
        final int maxChunkZ = MathHelper.floor_double((bb.maxZ) / 16.0D);
        final int minChunkY = MathHelper.floor_double((bb.minY) / 16.0D);
        final int maxChunkY = MathHelper.floor_double((bb.maxY) / 16.0D);

        for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ) {
                Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
                final int minChunkYClamped = MathHelper.clamp_int(minChunkY, 0, chunk.entityLists.length - 1);
                final int maxChunkYClamped = MathHelper.clamp_int(maxChunkY, 0, chunk.entityLists.length - 1);
                for (int chunkY = minChunkYClamped; chunkY <= maxChunkYClamped; ++chunkY) {
                    List<Entity> list = world.getEntitiesWithinAABB(Entity.class, bb);
                    for (Entity entity : list) {
                        if (entity instanceof EntityItem || entity instanceof EntityXPOrb) {
                            if (!entity.isDead) {
                                if (entity.boundingBox.intersectsWith(bb)) {
                                    arraylist.add(entity);
                                    if (itemsRemaining-- <= 0) {
                                        return arraylist;
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        return arraylist;
    }
}
