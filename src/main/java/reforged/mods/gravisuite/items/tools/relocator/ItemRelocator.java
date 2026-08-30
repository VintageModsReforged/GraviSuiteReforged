package reforged.mods.gravisuite.items.tools.relocator;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.ElectricItem;
import ic2.core.IC2;
import mods.vintage.core.helpers.StackHelper;
import mods.vintage.core.helpers.pos.BlockPos;
import mods.vintage.core.platform.lang.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.tools.base.ItemToolElectric;
import reforged.mods.gravisuite.utils.*;

import java.util.*;

public class ItemRelocator extends ItemToolElectric {

    public static final String TAG_MODE = "toolMode";
    static int MAX_POINTS = 10;

    final int ENERGY_STANDARD_TP;
    final int ENERGY_CROSS_TP;
    final int ENERGY_PORTAL;
    final int ENERGY_SHOOT;

    public ItemRelocator() {
        super(GraviSuiteConfig.RELOCATOR_ID.get(), "relocator", EnergyValues.RELOCATOR.tier, EnergyValues.RELOCATOR.transfer, EnergyValues.RELOCATOR.maxCapacity, EnumToolMaterial.STONE);
        this.setIconIndex(36);
        this.ENERGY_STANDARD_TP = GraviSuiteConfig.ENERGY_STANDARD_TP;
        this.ENERGY_CROSS_TP = GraviSuiteConfig.ENERGY_CROSS_TP;
        this.ENERGY_PORTAL = GraviSuiteConfig.ENERGY_PORTAL;
        this.ENERGY_SHOOT = GraviSuiteConfig.ENERGY_SHOOT;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        EnumRarity[] CYCLING_RARITIES = new EnumRarity[]{EnumRarity.epic, EnumRarity.rare};
        if (Minecraft.getMinecraft().theWorld == null) return EnumRarity.common; // fallback

        long time = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
        int index = (int) ((time / 20) % CYCLING_RARITIES.length);
        return CYCLING_RARITIES[index];
    }

    @SuppressWarnings({"unchecked"})
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean isDebugMode) {
        super.addInformation(stack, player, tooltip, isDebugMode);
        ToolMode mode = getToolMode(stack);
        tooltip.add(Messages.Translations.TOOL_MODE_RELOCATOR.toTooltip().format(mode.name));
        TeleportPoint point = getDefaultPoint(stack);
        if (point != null) {
            tooltip.add(Translator.GOLD.format("message.info.relocator.default", Translator.AQUA.literal(point.NAME)));
        } else {
            tooltip.add(Translator.GOLD.format("message.info.relocator.default", Translator.AQUA.literal(" - ")));
        }
        if (GraviSuite.PROXY.isSneakKeyDown()) {
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.MODE_KEY, KeyDescriptionHelper.Keys.RIGHT_CLICK, KeyDescriptionHelper.KeyMode.CHANGE, Messages.Translations.TOOL_RELOCATOR_STAT.format()));
            tooltip.add("");
            String type = mode.name().toLowerCase(Locale.ROOT);
            tooltip.add(Translator.LIGHT_PURPLE.format("tooltip.relocator." + type + ".line1"));
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.SNEAK_KEY, KeyDescriptionHelper.Keys.RIGHT_CLICK, Translator.YELLOW.format("tooltip.relocator." + type + ".line2")));
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.RIGHT_CLICK, Translator.YELLOW.format("tooltip.relocator." + type + ".line3")));
            if (mode == ToolMode.PERSONAL) {
                String cost = Translator.RESET.format("tooltip.relocator.personal.line4",
                        Translator.AQUA.literal(this.ENERGY_STANDARD_TP + ""),
                        Translator.AQUA.literal(this.ENERGY_CROSS_TP + ""));
                Collections.addAll(tooltip, cost.split("#"));
            } else {
                int energy = mode == ToolMode.TRANSLOCATOR ? ENERGY_SHOOT : ENERGY_PORTAL;
                tooltip.add(Translator.RESET.format("tooltip.relocator." + type + ".line4",
                        Translator.AQUA.literal(energy + "")));
            }
        } else {
            tooltip.add(Helpers.pressForInfo(Messages.Translations.KEY_SNEAK.format()));
        }
    }


    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (IC2.keyboard.isModeSwitchKeyDown(player)) {
            ToolMode nextMode = cycleAndSave(stack);
            if (IC2.platform.isRendering())
                IC2.platform.messagePlayer(player, Messages.Translations.TOOL_MODE_RELOCATOR.format(nextMode.name));
            return stack;
        }
        ToolMode mode = getToolMode(stack);
        if (player.isSneaking()) {
            if (mode == ToolMode.PERSONAL) {
                player.openGui(GraviSuite.instance, 1, world, (int) player.posX, (int) player.posY, (int) player.posZ);
            } else {
                player.openGui(GraviSuite.instance, 3, world, (int) player.posX, (int) player.posY, (int) player.posZ);
            }
        } else if (mode == ToolMode.PERSONAL) {
            player.openGui(GraviSuite.instance, 2, world, (int) player.posX, (int) player.posY, (int) player.posZ);
        } else if (mode == ToolMode.TRANSLOCATOR || mode == ToolMode.PORTAL) {
            TeleportPoint point = getDefaultPoint(stack);
            if (point != null) {
                int energy;
                byte actionType;
                if (mode == ToolMode.TRANSLOCATOR) {
                    energy = this.ENERGY_SHOOT;
                    actionType = 0;
                    if (!GraviSuiteConfig.enableTranslocator) {
                        if (IC2.platform.isRendering())
                            IC2.platform.messagePlayer(player, Translator.RED.format("message.tool.relocator.mode.translocator.disabled"));
                        return stack;
                    }
                } else {
                    energy = this.ENERGY_PORTAL;
                    actionType = 1;
                    if (!GraviSuiteConfig.enablePortal) {
                        if (IC2.platform.isRendering())
                            IC2.platform.messagePlayer(player, Translator.RED.format("message.tool.relocator.mode.portal.disabled"));
                        return stack;
                    }
                }
                if (!ElectricItem.canUse(stack, energy) && !player.capabilities.isCreativeMode) {
                    if (IC2.platform.isRendering())
                        IC2.platform.messagePlayer(player, Translator.RED.format("message.text.low_energy"));
                } else {
                    if (IC2.platform.isSimulating() && !player.capabilities.isCreativeMode) {
                        ElectricItem.use(stack, energy, player);
                    }
                    if (IC2.platform.isSimulating()) {
                        EntityRelocatorBall plasmaBall = new EntityRelocatorBall(world, player, point, actionType);
                        world.spawnEntityInWorld(plasmaBall);
                    }
                    player.swingItem();
                }
            } else {
                if (IC2.platform.isRendering())
                    IC2.platform.messagePlayer(player, Translator.RED.format("message.text.relocator.default.not_set"));
            }
        }

        return stack;
    }

    public void teleportPlayer(EntityPlayer player, ItemStack stack, String name) {
        if (stack != null && player != null) {
            TeleportPoint point = getTeleportPointByName(stack, name);
            if (point != null) {
                int energy;
                int dimensionId = player.worldObj.provider.dimensionId;
                if (dimensionId == point.DIMENSION_ID) {
                    energy = this.ENERGY_STANDARD_TP;
                } else {
                    energy = this.ENERGY_CROSS_TP;
                }
                if (!ElectricItem.canUse(stack, energy) && !player.capabilities.isCreativeMode) {
                    IC2.platform.messagePlayer(player, Translator.RED.format("message.text.low_energy"));
                } else {
                    if (IC2.platform.isSimulating() && !player.capabilities.isCreativeMode) {
                        ElectricItem.use(stack, energy, player);
                    }
                    Helpers.teleportEntity(player, point);
                }
            }
        }
    }

    public static TeleportPoint getDefaultPoint(ItemStack stack) {
        if (stack == null)
            return null;
        List<TeleportPoint> list = readPointsFromStack(stack);
        for (TeleportPoint teleportPoint : list) {
            if (teleportPoint.DEFAULT)
                return teleportPoint;
        }
        return null;
    }

    public void addNewPoint(EntityPlayer player, ItemStack stack, TeleportPoint point) {
        if (stack != null && point != null) {
            ArrayList<TeleportPoint> points = new ArrayList<TeleportPoint>(readPointsFromStack(stack));
            if (points.size() >= MAX_POINTS) {
                IC2.platform.messagePlayer(player, Translator.RED.format("message.text.relocator.full"));
            } else {
                boolean alreadyAdded = false;
                for (TeleportPoint teleportPoint : points) {
                    if (teleportPoint.NAME.equalsIgnoreCase(point.NAME)) {
                        IC2.platform.messagePlayer(player, Translator.WHITE.format("message.text.relocator.added", Translator.GOLD.literal(point.NAME)));
                        alreadyAdded = true;
                        break;
                    }
                }
                if (!alreadyAdded) {
                    IC2.platform.messagePlayer(player, Translator.WHITE.format("message.text.relocator.added", Translator.GOLD.literal(point.NAME)));
                    points.add(point);
                    writePointsToStack(stack, points);
                }
            }
        }
    }

    public void removePoint(ItemStack stack, String name) {
        if (stack == null) return;
        boolean found = false;
        List<TeleportPoint> points = new ArrayList<TeleportPoint>(readPointsFromStack(stack));
        Iterator<TeleportPoint> pointsIterator = points.iterator();
        while (pointsIterator.hasNext()) {
            TeleportPoint point = pointsIterator.next();
            if (point.NAME.equalsIgnoreCase(name)) {
                pointsIterator.remove();
                found = true;
                break;
            }
        }
        if (found) {
            writePointsToStack(stack, points);
        }
    }

    public void setDefaultPoint(EntityPlayer player, ItemStack stack, String name) {
        if (stack == null) return;
        List<TeleportPoint> points = readPointsFromStack(stack);
        boolean defaultSet = false;
        for (TeleportPoint point : points) {
            if (point.NAME.equalsIgnoreCase(name)) {
                point.DEFAULT = true;
                defaultSet = true;
                continue;
            }
            point.DEFAULT = false;
        }
        if (defaultSet) {
            writePointsToStack(stack, points);
            IC2.platform.messagePlayer(player, Translator.WHITE.format("message.text.relocator.default.set", Translator.GOLD.literal(name)));
        } else {
            IC2.platform.messagePlayer(player, Translator.WHITE.format("message.text.relocator.default.none"));
        }
    }

    public static TeleportPoint getTeleportPointByName(ItemStack stack, String name) {
        if (stack != null && !name.isEmpty()) {
            List<TeleportPoint> list = readPointsFromStack(stack);
            for (TeleportPoint teleportPoint : list) {
                if (teleportPoint.NAME.equalsIgnoreCase(name))
                    return teleportPoint;
            }
        }
        return null;
    }

    public static List<TeleportPoint> readPointsFromStack(ItemStack stack) {
        if (stack == null)
            return null;
        NBTTagCompound tag = StackHelper.getOrCreateTag(stack);
        NBTTagList list = tag.getTagList("pointsList");
        ArrayList<TeleportPoint> arrayList = Lists.newArrayList();
        for (byte i = 0; i < list.tagCount(); i++) {
            TeleportPoint teleportPoint = new TeleportPoint();
            NBTTagCompound tagAt = (NBTTagCompound) list.tagAt(i);
            teleportPoint.NAME = tagAt.getString("pointName");
            teleportPoint.DIMENSION_ID = tagAt.getInteger("pointDimId");
            double x = tagAt.getDouble("pointPosX");
            double y = tagAt.getDouble("pointPosY");
            double z = tagAt.getDouble("pointPosZ");
            teleportPoint.POS = new BlockPos(x, y, z);
            teleportPoint.YAW = tagAt.getDouble("pointYaw");
            teleportPoint.PITCH = tagAt.getDouble("pointPitch");
            teleportPoint.DEFAULT = tagAt.getBoolean("pointDefault");
            arrayList.add(teleportPoint);
        }
        return arrayList;
    }

    public static void writePointsToStack(ItemStack stack, List<TeleportPoint> points) {
        NBTTagCompound tag = StackHelper.getOrCreateTag(stack);
        NBTTagList list = new NBTTagList();
        for (TeleportPoint point : points) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("pointName", point.NAME);
            compound.setInteger("pointDimId", point.DIMENSION_ID);
            compound.setDouble("pointPosX", point.POS.getX());
            compound.setDouble("pointPosY", point.POS.getY());
            compound.setDouble("pointPosZ", point.POS.getZ());
            compound.setDouble("pointYaw", point.YAW);
            compound.setDouble("pointPitch", point.PITCH);
            compound.setBoolean("pointDefault", point.DEFAULT);
            list.appendTag(compound);
        }
        tag.setTag("pointsList", list);
    }

    public enum ToolMode {
        PERSONAL(Translator.YELLOW.format("message.tool.relocator.mode.personal")),
        TRANSLOCATOR(Translator.AQUA.format("message.tool.relocator.mode.translocator")),
        PORTAL(Translator.LIGHT_PURPLE.format("message.tool.relocator.mode.portal"));

        public static final ToolMode[] VALUES = values();

        public final String name;

        ToolMode(String name) {
            this.name = name;
        }

        public static ToolMode getFromId(int id) {
            return VALUES[id % VALUES.length];
        }
    }

    public ToolMode getToolMode(ItemStack stack) {
        NBTTagCompound tag = StackHelper.getOrCreateTag(stack);
        return ToolMode.getFromId(tag.getInteger(TAG_MODE));
    }

    public ToolMode cycleAndSave(ItemStack stack) {
        ToolMode current = getToolMode(stack);
        ToolMode next = ToolMode.getFromId(current.ordinal() + 1);
        NBTTagCompound tag = StackHelper.getOrCreateTag(stack);
        tag.setInteger(TAG_MODE, next.ordinal());
        return next;
    }
}
