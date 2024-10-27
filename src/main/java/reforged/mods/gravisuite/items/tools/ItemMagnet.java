package reforged.mods.gravisuite.items.tools;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.item.ElectricItem;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteMainConfig;
import reforged.mods.gravisuite.items.IToolTipProvider;
import reforged.mods.gravisuite.items.tools.base.ItemBaseElectricItem;
import reforged.mods.gravisuite.keyboard.GraviSuiteKeyboardClient;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.util.ArrayList;
import java.util.List;

public class ItemMagnet extends ItemBaseElectricItem {

    public static final String NBT_ACTIVE = "active";
    public static final String NBT_TICKER = "magnetTicker";

    public static final int ENERGY_COST = 1;
    public byte MAGNET_TICKER;

    public ItemMagnet() {
        super(GraviSuiteMainConfig.MAGNET_ID, "magnet", 1, 500, 10000, EnumToolMaterial.IRON);
        this.setIconIndex(Refs.TOOLS_ID + 3);
        this.MAGNET_TICKER = 10;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, final List tooltip, boolean debugMode) {
        super.addInformation(stack, player, tooltip, debugMode);
        addKeyTooltips(tooltip, new IToolTipProvider() {
            @Override
            public void addTooltip() {
                tooltip.add(Helpers.pressXForY(Refs.to_enable_1, StatCollector.translateToLocal(GraviSuiteKeyboardClient.magnet_toggle.keyDescription), Refs.MAGNET_MODE + ".stat"));
            }
        });
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return StackUtil.getOrCreateNbtData(stack).getBoolean(NBT_ACTIVE);
    }

    public void changeMode(ItemStack stack, EntityPlayer player) {
        String message;
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        if (Helpers.getCharge(stack) > ENERGY_COST) {
            tag.setByte(NBT_TICKER, MAGNET_TICKER);
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

        if (IC2.platform.isSimulating()) {
            IC2.platform.messagePlayer(player, message);
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int hand, boolean update) {
        EntityPlayer player = (EntityPlayer) entity;
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        byte ticker = tag.getByte(NBT_TICKER);
        if (IC2.platform.isSimulating()) {
            if (ticker > 0) {
                ticker--;
                tag.setByte(NBT_TICKER, ticker);
            }
            if (GraviSuite.KEYBOARD.isMagnetToggleKeyDown(player) && ticker <= 0) {
                changeMode(stack, player);
            }
            if (!tag.getBoolean(NBT_ACTIVE))
                return;
            if (world.getTotalWorldTime() % 20 == 0) {
                if (ElectricItem.canUse(stack, ENERGY_COST)) {
                    ElectricItem.use(stack, ENERGY_COST, player);
                } else {
                    tag.setBoolean(NBT_ACTIVE, false);
                }
            }
            if (world.getTotalWorldTime() % 2 != 0)
                return;
            if (world.getChunkProvider().chunkExists(player.chunkCoordX, player.chunkCoordZ)) {
                double x = player.posX;
                double y = player.posY;
                double z = player.posZ;
                int range = GraviSuiteMainConfig.MAGNET_RANGE;
                AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x - range, y - range, z - range, x + range, y + range, z + range);
                List<Entity> items = selectEntitiesWithinAABB(world, aabb);
                if (items.isEmpty())
                    return;
                for (Entity item : items) {
                    if (item != null && !player.isSneaking()) {
                        this.onCollideWithPlayer(player, item);
                    }
                }
            }
        }
    }

    private List<Entity> selectEntitiesWithinAABB(World world, AxisAlignedBB bb) {
        List<Entity> arraylist = new ArrayList<Entity>();

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
                                    if (arraylist.size() >= GraviSuiteMainConfig.MAGNET_MAX_CAPACITY) {
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

    public boolean onCollideWithPlayer(EntityPlayer player, Entity drop) {
        World world = player.worldObj;
        if (!world.isRemote) {
            if (drop instanceof EntityXPOrb) {
                EntityXPOrb xpOrb = (EntityXPOrb) drop;
                if (xpOrb.field_70532_c == 0 && player.xpCooldown == 0) {
                    player.xpCooldown = 2;
                    xpOrb.playSound("random.pop", 0.02F, 1F);
                    player.onItemPickup(xpOrb, 1);
                    player.addExperience(xpOrb.getXpValue());
                    xpOrb.setDead();
                    return true;
                }
            }
            if (drop instanceof EntityItem) {
                EntityItem itemDrop = (EntityItem) drop;
                if (itemDrop.delayBeforeCanPickup > 0) {
                    return false;
                }

                EntityItemPickupEvent event = new EntityItemPickupEvent(player, itemDrop);
                if (MinecraftForge.EVENT_BUS.post(event)) {
                    return false;
                }

                ItemStack stack = itemDrop.getEntityItem();
                int stackSize = stack.stackSize;
                if (itemDrop.delayBeforeCanPickup <= 0 && (event.getResult() == Event.Result.ALLOW || stackSize <= 0 || player.inventory.addItemStackToInventory(stack))) {
                    if (stack.itemID == Block.wood.blockID) {
                        player.triggerAchievement(AchievementList.mineWood);
                    }

                    if (stack.itemID == Item.leather.itemID) {
                        player.triggerAchievement(AchievementList.killCow);
                    }

                    if (stack.itemID == Item.diamond.itemID) {
                        player.triggerAchievement(AchievementList.diamonds);
                    }

                    if (stack.itemID == Item.blazeRod.itemID) {
                        player.triggerAchievement(AchievementList.blazeRod);
                    }

                    GameRegistry.onPickupNotification(player, itemDrop);
                    itemDrop.playSound("random.pop", 0.02F, 1F);
                    player.onItemPickup(itemDrop, stackSize);
                    if (stack.stackSize <= 0) {
                        itemDrop.setDead();
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
