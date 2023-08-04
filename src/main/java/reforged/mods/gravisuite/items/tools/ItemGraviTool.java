package reforged.mods.gravisuite.items.tools;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.audio.PositionSpec;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.tools.base.ItemToolElectric;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemGraviTool extends ItemToolElectric implements IToolWrench {


    public static Icon[] iconsList = new Icon[4];
    public int energy_per_use = 150;

    public String CHANGE_SOUND = "Tools/change.ogg";
    public String TOOL_WRENCH = "Tools/wrench.ogg";
    public String TOOL_TREETAP = "Tools/Treetap.ogg";
    public boolean LOW_ENERGY = false;

    public ItemGraviTool() {
        super(GraviSuiteConfig.GRAVI_TOOL_ID, "gravitool", 2, 5000, 100000, EnumRarity.uncommon, EnumToolMaterial.IRON);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister icons) {
        iconsList[0] = icons.registerIcon(Refs.id + ":gravitool/hoe");
        iconsList[1] = icons.registerIcon(Refs.id + ":gravitool/treetap");
        iconsList[2] = icons.registerIcon(Refs.id + ":gravitool/wrench");
        iconsList[3] = icons.registerIcon(Refs.id + ":gravitool/screwdriver");
        this.itemIcon = iconsList[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(ItemStack stack, int pass) {
        int index = readToolMode(stack).ordinal();
        return iconsList[index];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean isDebugMode) {
        super.addInformation(stack, player, tooltip, isDebugMode);
        ToolMode mode = readToolMode(stack);
        tooltip.add(Refs.tool_mode_gold + " " + mode.name);
        if (Helpers.isShiftKeyDown()) {
            tooltip.add(Helpers.pressXAndYForZ(Refs.to_change_2, "Mode Switch Key", "Right Click", Refs.MODE + ".stat"));
        } else {
            tooltip.add(Helpers.pressForInfo(Refs.SNEAK_KEY));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (IC2.keyboard.isModeSwitchKeyDown(player)) {
            if (IC2.platform.isSimulating()) {
                ToolMode nextMode = readNextToolMode(stack);
                saveToolMode(stack, nextMode);
                IC2.platform.messagePlayer(player, Refs.tool_mode + " " + nextMode.name);
            }
            if (IC2.platform.isRendering())
                IC2.audioManager.playOnce(player, PositionSpec.Hand, CHANGE_SOUND, false, IC2.audioManager.defaultVolume);
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        ToolMode mode = readToolMode(stack);
        if (!IC2.keyboard.isModeSwitchKeyDown(player)) {
            if (mode == ToolMode.HOE) {
                boolean hoe = Ic2Items.electricHoe.getItem().onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
                if (hoe) {
                    ElectricItem.manager.use(stack, this.energy_per_use, player);
                    return true;
                }
            } else if (mode == ToolMode.TREETAP) {
                boolean treetap = Ic2Items.electricTreetap.getItem().onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
                if (treetap) {
                    ElectricItem.manager.use(stack, this.energy_per_use, player);
                    if (IC2.platform.isRendering())
                        IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_TREETAP, false, IC2.audioManager.defaultVolume);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        ToolMode mode = readToolMode(stack);
        if (mode == ToolMode.WRENCH) {
            boolean wrench = onWrenchUse(stack, player, world, x, y, z, side);
            if (wrench) {
                if (IC2.platform.isRendering())
                    IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, false, IC2.audioManager.defaultVolume);
                return true;
            }
        } else if (mode == ToolMode.SCREWDRIVER) {
            boolean screwdriver = onScrewdriverUseNew(stack, player, world, x, y, z);
            if (screwdriver) {
                if (IC2.platform.isRendering())
                    IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, false, IC2.audioManager.defaultVolume);
                return true;
            }
        }
        return false;
    }

    public boolean onWrenchUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        if (ElectricItem.manager.canUse(stack, this.energy_per_use)) {
            int blockId = world.getBlockId(x, y, z);
            int metaData = world.getBlockMetadata(x, y, z);
            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
            try {
                if (tileEntity.getClass().getName().equals("TileEntityTerra")) {
                    Method terraMethod = tileEntity.getClass().getMethod("ejectBlueprint");
                    if ((Boolean) terraMethod.invoke((Object) null)) {
                        if (IC2.platform.isSimulating()) {
                            ElectricItem.use(stack, this.energy_per_use, player);
                        }
                        if (IC2.platform.isRendering()) {
                            IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, true, IC2.audioManager.defaultVolume);
                        }
                        return IC2.platform.isSimulating();
                    }
                }
            } catch (Throwable ignored) {
            }
            if (tileEntity instanceof IWrenchable) {
                IWrenchable wrenchable = (IWrenchable) tileEntity;
                if (IC2.keyboard.isAltKeyDown(player)) {
                    if (player.isSneaking()) {
                        side = (wrenchable.getFacing() + 5) % 6;
                    } else {
                        side = (wrenchable.getFacing() + 1) % 6;
                    }
                } else if (player.isSneaking()) {
                    side += side % 2 * -2 + 1;
                }

                if (wrenchable.wrenchCanSetFacing(player, side)) {
                    if (IC2.platform.isSimulating()) {
                        wrenchable.setFacing((short) side);
                        ElectricItem.manager.use(stack, this.energy_per_use, player);
                    }

                    if (IC2.platform.isRendering()) {
                        IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, true, IC2.audioManager.defaultVolume);
                    }
                    return IC2.platform.isSimulating();
                }

                if (ElectricItem.manager.canUse(stack, this.energy_per_use) && wrenchable.wrenchCanRemove(player)) {
                    if (IC2.platform.isSimulating()) {
                        if (GraviSuiteConfig.log_wrench) {
                            String blockName = tileEntity.getClass().getName().replace("TileEntity", "");
                            MinecraftServer.getServer().logInfo("Player " + player.username + " used the wrench to remove the " + blockName + " (" + blockId + "-" + metaData + ") at " + x + "/" + y + "/" + z);
                        }

                        Block block = Block.blocksList[blockId];
                        boolean dropOriginalBlock = false;
                        if (wrenchable.getWrenchDropRate() < 1.0F) {
                            if (!ElectricItem.manager.canUse(stack, this.energy_per_use)) {
                                IC2.platform.messagePlayer(player, Refs.status_low);
                                return true;
                            }
                            dropOriginalBlock = true;
                            ElectricItem.manager.use(stack, this.energy_per_use, player);
                        } else {
                            dropOriginalBlock = world.rand.nextFloat() <= wrenchable.getWrenchDropRate();
                        }

                        ArrayList<ItemStack> drops = block.getBlockDropped(world, x, y, z, metaData, 0);
                        if (dropOriginalBlock) {
                            if (drops.isEmpty()) {
                                drops.add(wrenchable.getWrenchDrop(player));
                            } else {
                                drops.set(0, wrenchable.getWrenchDrop(player));
                            }
                        }

                        Iterator<ItemStack> iterator = drops.iterator();
                        while (iterator.hasNext()) {
                            ItemStack itemStack = iterator.next();
                            Helpers.dropAsEntity(world, x, y, z, itemStack);
                        }
                        if (world.setBlock(x, y, z, 0)) {
                            world.notifyBlockChange(x, y, z, 0);
                        }
                    }
                }
                if (IC2.platform.isRendering()) {
                    IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, true, IC2.audioManager.defaultVolume);
                }
            }
        }
        return false;
    }

    public boolean onScrewdriverUseNew(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
        boolean stack1 = false;
        if ((player != null) && (player.isSneaking())) {
            stack1 = true;
        }
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        int stack3 = world.getBlockMetadata(x, y, z);
        if ((block != Block.redstoneRepeaterIdle) && (block != Block.redstoneRepeaterActive)) {
            if (block == Block.dispenser) {
                if (!ElectricItem.manager.canUse(stack, this.energy_per_use)) {
                    LOW_ENERGY = true;
                    return false;
                }
                stack3 = stack3 & 0x3 ^ stack3 >> 2;
                stack3 += 2;
                if (IC2.platform.isSimulating()) {
                    ElectricItem.manager.use(stack, this.energy_per_use, player);
                }
                return IC2.platform.isSimulating();
            }
            stack3++;
            if (!ElectricItem.manager.canUse(stack, this.energy_per_use)) {
                LOW_ENERGY = true;
                return false;
            }
            if (stack3 > 5) {
                stack3 = 0;
            }

            if (IC2.platform.isSimulating()) {
                ElectricItem.manager.use(stack, this.energy_per_use, player);
                world.setBlockMetadataWithNotify(x, y, z, stack3, 7);
            }
            return IC2.platform.isSimulating();
        }
        if (!ElectricItem.manager.canUse(stack, this.energy_per_use)) {
            LOW_ENERGY = true;
            return false;
        }
        if (IC2.platform.isSimulating()) {
            ElectricItem.manager.use(stack, this.energy_per_use, player);
            world.setBlockMetadataWithNotify(x, y, z, stack3 & 0xC | stack3 + 1 & 0x3, 7);
            IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, false, IC2.audioManager.defaultVolume);
        }
        if (LOW_ENERGY && IC2.platform.isSimulating()) {
            IC2.platform.messagePlayer(player, Refs.status_low);
        }
        return IC2.platform.isSimulating();
    }

    @Override
    public boolean canWrench(EntityPlayer player, int x, int y, int z) {
        ItemStack stack = player.getHeldItem();
        ToolMode mode = readToolMode(stack);
        return mode.equals(ToolMode.WRENCH);
    }

    @Override
    public void wrenchUsed(EntityPlayer player, int x, int y, int z) {
        if (IC2.platform.isRendering())
            IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, false, IC2.audioManager.defaultVolume);
        ElectricItem.manager.use(player.getHeldItem(), this.energy_per_use, player);
    }

    public enum ToolMode {
        HOE(Refs.tool_mode_hoe), TREETAP(Refs.tool_mode_treetap), WRENCH(Refs.tool_mode_wrench), SCREWDRIVER(Refs.tool_mode_screwdriver);

        public static final ToolMode[] VALUES = values();

        public final String name;

        ToolMode(String name) {
            this.name = name;
        }

        public static ToolMode getFromId(int id) {
            return VALUES[id % VALUES.length];
        }
    }

    public static ToolMode readToolMode(ItemStack stack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        return ToolMode.getFromId(tag.getInteger("toolMode"));
    }

    public static ToolMode readNextToolMode(ItemStack stack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        return ToolMode.getFromId(tag.getInteger("toolMode") + 1);
    }

    public static void saveToolMode(ItemStack stack, ToolMode mode) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        tag.setInteger("toolMode", mode.ordinal());
    }
}
