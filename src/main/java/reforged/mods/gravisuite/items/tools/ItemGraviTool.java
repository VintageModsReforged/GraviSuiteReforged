package reforged.mods.gravisuite.items.tools;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.IWrenchable;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.audio.PositionSpec;
import ic2.core.item.ElectricItem;
import ic2.core.util.StackUtil;
import reforged.mods.gravisuite.GraviSuiteMainConfig;
import reforged.mods.gravisuite.items.tools.base.ItemBaseElectricItem;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class ItemGraviTool extends ItemBaseElectricItem {

    public int ENERGY_PER_USE = 150;
    public boolean LOW_ENERGY = false;

    public String CHANGE_SOUND = "toolchange.ogg";
    public String TOOL_WRENCH = "Tools/wrench.ogg";
    public String TOOL_TREETAP = "Tools/Treetap.ogg";

    public ItemGraviTool() {
        super(GraviSuiteMainConfig.GRAVI_TOOL_ID, "gravitool", 2, 1000, 100000, EnumRarity.uncommon, EnumToolMaterial.IRON);
        this.setIconIndex(Refs.GRAVITOOL_ID);
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
        super.addInformation(stack, player, tooltip, par4);
        ToolMode mode = readToolMode(stack);
        tooltip.add(Refs.tool_mode_gold + " " + mode.name);
        if (par4) {
            tooltip.add("Texture Index: " + mode.index);
        }
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
        if (mode == ToolMode.HOE) {
            boolean hoe = Ic2Items.electricHoe.getItem().onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
            if (hoe) {
                ElectricItem.use(stack, ENERGY_PER_USE, player);
                return true;
            }
        } else if (mode == ToolMode.TREETAP) {
            boolean treetap = Ic2Items.electricTreetap.getItem().onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
            if (treetap) {
                ElectricItem.use(stack, ENERGY_PER_USE, player);
                if (IC2.platform.isRendering())
                    IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_TREETAP, false, IC2.audioManager.defaultVolume);
                return true;
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
        if (ElectricItem.canUse(stack, this.ENERGY_PER_USE)) {
            int blockId = world.getBlockId(x, y, z);
            int metaData = world.getBlockMetadata(x, y, z);
            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
            try {
                if (tileEntity.getClass().getName().equals("TileEntityTerra")) {
                    Method terraMethod = tileEntity.getClass().getMethod("ejectBlueprint");
                    if ((Boolean) terraMethod.invoke((Object) null)) {
                        if (IC2.platform.isSimulating()) {
                            ElectricItem.use(stack, this.ENERGY_PER_USE, player);
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
                        ElectricItem.use(stack, this.ENERGY_PER_USE, player);
                    }

                    if (IC2.platform.isRendering()) {
                        IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, true, IC2.audioManager.defaultVolume);
                    }
                    return IC2.platform.isSimulating();
                }

                if (ElectricItem.canUse(stack, this.ENERGY_PER_USE) && wrenchable.wrenchCanRemove(player)) {
                    if (IC2.platform.isSimulating()) {
                        if (GraviSuiteMainConfig.LOG_WRENCH) {
                            String blockName = tileEntity.getClass().getName().replace("TileEntity", "");
                            MinecraftServer.logger.log(Level.INFO, "Player " + player.username + " used the wrench to remove the " + blockName + " (" + blockId + "-" + metaData + ") at " + x + "/" + y + "/" + z);
                        }

                        Block block = Block.blocksList[blockId];
                        boolean dropOriginalBlock = false;
                        if (wrenchable.getWrenchDropRate() < 1.0F) {
                            if (!ElectricItem.canUse(stack, this.ENERGY_PER_USE)) {
                                IC2.platform.messagePlayer(player, Refs.status_low);
                                return true;
                            }
                            dropOriginalBlock = true;
                            ElectricItem.use(stack, this.ENERGY_PER_USE, player);
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
                        world.setBlockWithNotify(x, y, z, 0);
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
                if (!ElectricItem.canUse(stack, this.ENERGY_PER_USE)) {
                    LOW_ENERGY = true;
                    return false;
                }
                stack3 = stack3 & 0x3 ^ stack3 >> 2;
                stack3 += 2;
                if (IC2.platform.isSimulating()) {
                    ElectricItem.use(stack, this.ENERGY_PER_USE, player);
                }
                return IC2.platform.isSimulating();
            }
            if ((block != Block.pistonBase) && (block != Block.pistonStickyBase)) {
                TileEntity iRotatableTileEntity = world.getBlockTileEntity(x, y, z);
                if (Loader.isModLoaded("RedPowerCore") && iRotatableTileEntity instanceof com.eloraam.redpower.core.IRotatable) {
                    if (!ElectricItem.canUse(stack, this.ENERGY_PER_USE)) {
                        LOW_ENERGY = true;
                        return false;
                    }
                    MovingObjectPosition stack5 = Helpers.retraceBlock(world, player, x, y, z);
                    if (stack5 == null) {
                        return false;
                    }
                    int stack6 = ((com.eloraam.redpower.core.IRotatable) iRotatableTileEntity).getPartMaxRotation(stack5.subHit, stack1);
                    if (stack6 == 0) {
                        return false;
                    }
                    int stack7 = ((com.eloraam.redpower.core.IRotatable) iRotatableTileEntity).getPartRotation(stack5.subHit, stack1);
                    stack7++;
                    if (stack7 > stack6) {
                        stack7 = 0;
                    }
                    if (IC2.platform.isSimulating()) {
                        ElectricItem.use(stack, this.ENERGY_PER_USE, player);
                        ((com.eloraam.redpower.core.IRotatable) iRotatableTileEntity).setPartRotation(stack5.subHit, stack1, stack7);
                    }
                    return IC2.platform.isSimulating();
                }
                return false;
            }
            stack3++;
            if (!ElectricItem.canUse(stack, this.ENERGY_PER_USE)) {
                LOW_ENERGY = true;
                return false;
            }
            if (stack3 > 5) {
                stack3 = 0;
            }

            if (IC2.platform.isSimulating()) {
                ElectricItem.use(stack, this.ENERGY_PER_USE, player);
                world.setBlockMetadataWithNotify(x, y, z, stack3);
            }
            return IC2.platform.isSimulating();
        }
        if (!ElectricItem.canUse(stack, this.ENERGY_PER_USE)) {
            LOW_ENERGY = true;
            return false;
        }
        if (IC2.platform.isSimulating()) {
            ElectricItem.use(stack, this.ENERGY_PER_USE, player);
            world.setBlockMetadataWithNotify(x, y, z, stack3 & 0xC | stack3 + 1 & 0x3);
            IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, false, IC2.audioManager.defaultVolume);
        }
        if (LOW_ENERGY && IC2.platform.isSimulating()) {
            IC2.platform.messagePlayer(player, Refs.status_low);
        }
        return IC2.platform.isSimulating();
    }

    public boolean onScrewdriverUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
        boolean var11 = false;
        if (player != null && player.isSneaking()) {
            var11 = true;
        }
        int var12 = world.getBlockId(x, y, z);
        int var13 = world.getBlockMetadata(x, y, z);
        if (var12 != Block.redstoneRepeaterIdle.blockID && var12 != Block.redstoneRepeaterActive.blockID) {
            if (var12 == Block.dispenser.blockID) {
                if (!ElectricItem.canUse(stack, ENERGY_PER_USE)) {
                    if (IC2.platform.isSimulating()) {
                        IC2.platform.messagePlayer(player, Refs.status_low);
                    }
                    return false;
                } else {
                    var13 = var13 & 3 ^ var13 >> 2;
                    var13 += 2;
                    if (IC2.platform.isRendering()) {
                        IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, true, IC2.audioManager.defaultVolume);
                    }
                    if (IC2.platform.isSimulating()) {
                        ElectricItem.use(stack, this.ENERGY_PER_USE, player);
                        world.setBlockMetadataWithNotify(x, y, z, var13);
                    }
                    return IC2.platform.isSimulating();
                }
            } else if (var12 != Block.pistonBase.blockID && var12 != Block.pistonStickyBase.blockID) {
                TileEntity iRotatableTileEntity = world.getBlockTileEntity(x, y, z);
                if (Loader.isModLoaded("RedPowerCore") && iRotatableTileEntity instanceof com.eloraam.redpower.core.IRotatable) {
                    if (!ElectricItem.canUse(stack, this.ENERGY_PER_USE)) {
                        if (IC2.platform.isSimulating()) {
                            IC2.platform.messagePlayer(player, Refs.status_low);
                        }
                        return false;
                    } else {
                        MovingObjectPosition var15 = Helpers.retraceBlock(world, player, x, y, z);
                        if (var15 == null) {
                            return false;
                        } else {
                            int var16 = ((com.eloraam.redpower.core.IRotatable) iRotatableTileEntity).getPartMaxRotation(var15.subHit, var11);
                            if (var16 == 0) {
                                return false;
                            } else {
                                int var17 = ((com.eloraam.redpower.core.IRotatable) iRotatableTileEntity).getPartRotation(var15.subHit, var11);
                                ++var17;
                                if (var17 > var16) {
                                    var17 = 0;
                                }

                                if (IC2.platform.isRendering()) {
                                    IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, true, IC2.audioManager.defaultVolume);
                                }

                                if (IC2.platform.isSimulating()) {
                                    ElectricItem.use(stack, this.ENERGY_PER_USE, player);
                                    ((com.eloraam.redpower.core.IRotatable) iRotatableTileEntity).setPartRotation(var15.subHit, var11, var17);
                                }

                                return IC2.platform.isSimulating();
                            }
                        }
                    }
                } else {
                    return false;
                }
            } else {
                ++var13;
                if (!ElectricItem.canUse(stack, this.ENERGY_PER_USE)) {
                    if (IC2.platform.isSimulating()) {
                        IC2.platform.messagePlayer(player, Refs.status_low);
                    }
                    return false;
                } else {
                    if (var13 > 5) {
                        var13 = 0;
                    }
                    if (IC2.platform.isRendering()) {
                        IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, true, IC2.audioManager.defaultVolume);
                    }

                    if (IC2.platform.isSimulating()) {
                        ElectricItem.use(stack, this.ENERGY_PER_USE, player);
                        world.setBlockMetadataWithNotify(x, y, z, var13);
                    }

                    return IC2.platform.isSimulating();
                }
            }
        } else if (!ElectricItem.canUse(stack, this.ENERGY_PER_USE)) {
            if (IC2.platform.isSimulating()) {
                IC2.platform.messagePlayer(player, Refs.status_low);
            }
            return false;
        } else {
            if (IC2.platform.isRendering()) {
                IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, true, IC2.audioManager.defaultVolume);
            }
            if (IC2.platform.isSimulating()) {
                ElectricItem.use(stack, this.ENERGY_PER_USE, player);
                world.setBlockMetadataWithNotify(x, y, z, var13 & 12 | var13 + 1 & 3);
            }
            return IC2.platform.isSimulating();
        }
    }

    public enum ToolMode {
        HOE(Refs.tool_mode_hoe, Refs.GRAVITOOL_ID), TREETAP(Refs.tool_mode_treetap, Refs.GRAVITOOL_ID + 1),
        WRENCH(Refs.tool_mode_wrench, Refs.GRAVITOOL_ID + 2), SCREWDRIVER(Refs.tool_mode_screwdriver, Refs.GRAVITOOL_ID + 3);

        public static final ToolMode[] VALUES = values();

        public final String name;
        public final int index;

        ToolMode(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public static ToolMode getFromId(int id) {
            return VALUES[id % VALUES.length];
        }
    }

    public static ToolMode readToolMode(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return ToolMode.getFromId(tag.getInteger("toolMode"));
    }

    public static ToolMode readNextToolMode(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return ToolMode.getFromId(tag.getInteger("toolMode") + 1);
    }

    public static void saveToolMode(ItemStack stack, ToolMode mode) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        tag.setInteger("toolMode", mode.ordinal());
    }
}
