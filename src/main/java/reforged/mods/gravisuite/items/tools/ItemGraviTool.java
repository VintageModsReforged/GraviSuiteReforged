package reforged.mods.gravisuite.items.tools;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.IWrenchable;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.audio.PositionSpec;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.item.ElectricItem;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import reforged.mods.gravisuite.GraviSuiteMainConfig;
import reforged.mods.gravisuite.items.tools.base.ItemBaseElectricItem;
import reforged.mods.gravisuite.utils.BlockHelper;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;
import thermalexpansion.api.core.IDismantleable;

import java.util.ArrayList;
import java.util.List;

public class ItemGraviTool extends ItemBaseElectricItem {

    public int ENERGY_PER_USE = 50;
    public boolean LOW_ENERGY = false;

    public String CHANGE_SOUND = "toolchange.ogg";
    public String TOOL_WRENCH = "Tools/wrench.ogg";
    public String TOOL_TREETAP = "Tools/Treetap.ogg";

    public ItemGraviTool() {
        super(GraviSuiteMainConfig.GRAVI_TOOL_ID, "gravitool", 2, 10000, 100000, EnumToolMaterial.IRON);
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
    public boolean shouldPassSneakingClickToBlock(World par2World, int par4, int par5, int par6) {
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (IC2.keyboard.isModeSwitchKeyDown(player)) {
            if (IC2.platform.isSimulating()) {
                ToolMode nextMode = readNextToolMode(stack);
                saveToolMode(stack, nextMode);
                IC2.platform.messagePlayer(player, Refs.tool_mode + " " + nextMode.name);
            }
            IC2.audioManager.playOnce(player, PositionSpec.Hand, CHANGE_SOUND, false, IC2.audioManager.defaultVolume);
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        ToolMode mode = readToolMode(stack);
        if (IC2.platform.isSimulating()) {
            if (IC2.keyboard.isModeSwitchKeyDown(player)) {
                return false;
            } else if (ElectricItem.canUse(stack, this.ENERGY_PER_USE)) {
                if (mode == ToolMode.HOE) {
                    return Ic2Items.electricHoe.getItem().onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
                } else if (mode == ToolMode.TREETAP) {
                    return Ic2Items.electricTreetap.getItem().onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
                }
            } else {
                IC2.platform.messagePlayer(player, Refs.status_low);
            }
        }
        return false;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        ToolMode mode = readToolMode(stack);
        boolean actionDone = false;
        if (IC2.platform.isSimulating()) {
            if (IC2.keyboard.isModeSwitchKeyDown(player)) {
                return false;
            } else {
                if (mode == ToolMode.WRENCH) {
                    actionDone = onWrenchUse(stack, player, world, x, y, z, side);
                } else if (mode == ToolMode.SCREWDRIVER) {
                    actionDone = onScrewdriverUse(stack, player, world, x, y, z);
                }
            }
        }
        if (IC2.platform.isSimulating() && actionDone) {
            IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, false, IC2.audioManager.defaultVolume);
        }
        return actionDone;
    }

    public boolean onWrenchUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        boolean simulating = IC2.platform.isSimulating();
        if (world.isRemote) {
            return false;
        } else {
            int blockID = world.getBlockId(x, y, z);
            int blockMetadata = world.getBlockMetadata(x, y, z);
            Block block = Block.blocksList[blockID];
            TileEntity tile = world.getBlockTileEntity(x, y, z);
            if (tile instanceof TileEntityTerra && side == 1) {
                TileEntityTerra terra = (TileEntityTerra) tile;
                terra.ejectBlueprint();
                ElectricItem.use(stack, this.ENERGY_PER_USE, player);
                return simulating;
            }

            if (tile instanceof IWrenchable) {
                IWrenchable wrenchable = (IWrenchable) tile;
                ItemStack tileDrop = wrenchable.getWrenchDrop(player);
                if (player.isSneaking()) {
                    side = BlockHelper.SIDE_OPPOSITE[side];
                }

                if (wrenchable.wrenchCanSetFacing(player, side)) {
                    wrenchable.setFacing((short) side);
                    ElectricItem.use(stack, this.ENERGY_PER_USE, player);
                    return simulating;
                }

                if (wrenchable.wrenchCanRemove(player)) {
                    if (simulating) {
                        if (GraviSuiteMainConfig.LOG_WRENCH) {
                            String blockName = block.translateBlockName();
                            MinecraftServer.getServer().logInfo("Player " + player.username + " used the wrench to remove the " + blockName + " (" + blockID + "-" + blockMetadata + ") at " + x + "/" + y + "/" + z);
                        }
                    }

                    List<ItemStack> drops = new ArrayList<ItemStack>(block.getBlockDropped(world, x, y, z, blockMetadata, 0));
                    if (drops.isEmpty()) {
                        drops.add(tileDrop);
                    } else {
                        drops.set(0, tileDrop);
                    }
                    dropAsItem(world, drops, x, y, z);
                    world.setBlock(x, y, z, 0);
                    ElectricItem.use(stack, 10000, player);
                    return true;
                } else {
                    if (IC2.platform.isSimulating()) {
                        if (side != 0 && side != 1) {
                            wrenchable.setFacing((short)side);
                        } else if (wrenchable instanceof IEnergySource && wrenchable instanceof IEnergySink) {
                            wrenchable.setFacing((short)side);
                        }
                        ElectricItem.use(stack, this.ENERGY_PER_USE, player);
                        return true;
                    }
                }
            }
            if (Loader.isModLoaded("GregTech_Addon")) {
                if (tile instanceof gregtechmod.api.BaseMetaTileEntity) {
                    gregtechmod.api.BaseMetaTileEntity baseTileEntity = (gregtechmod.api.BaseMetaTileEntity) tile;
                    gregtechmod.api.MetaTileEntity metaTileEntity = baseTileEntity.mMetaTileEntity;
                    if (metaTileEntity != null) {
                        ItemStack drop = baseTileEntity.getWrenchDrop(player);
                        if (drop != null) {
                            world.spawnEntityInWorld(new EntityItem(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5, drop));
                            world.setBlock(x, y, z, 0);
                            ElectricItem.use(stack, 10000, player);
                        }
                        return true;
                    }
                }
            }
            if (Loader.isModLoaded("ThermalExpansion")) {
                if (block instanceof IDismantleable) {
                    IDismantleable dismantleable = (IDismantleable) block;
                    if (dismantleable.canDismantle(player, world, x, y, z)) {
                        dismantleable.dismantleBlock(player, world, x, y, z, false);
                        ElectricItem.use(stack, ENERGY_PER_USE, player);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void dropAsItem(World world, List<ItemStack> drops, int x, int y, int z) {
        for (ItemStack drop : drops) {
            float f = 0.7F;
            double x2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5;
            double y2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5;
            double z2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5;
            if (drop != null) {
                EntityItem entity = new EntityItem(world, (double) x + x2, (double) y + y2, (double) z + z2, drop.copy());
                entity.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(entity);
            }
        }
    }

    public boolean onScrewdriverUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
        boolean stack1 = (player != null) && (player.isSneaking());
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
