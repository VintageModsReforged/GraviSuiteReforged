package reforged.mods.gravisuite.items.tools;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.item.ElectricItem;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.audio.PositionSpec;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import mods.railcraft.api.core.items.IToolCrowbar;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.tools.base.ItemToolElectric;
import reforged.mods.gravisuite.utils.BlockHelper;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;
import universalelectricity.prefab.implement.IToolConfigurator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ItemGraviTool extends ItemToolElectric implements IToolWrench, IToolCrowbar, IToolConfigurator {


    public static Icon[] iconsList = new Icon[4];
    public int energy_per_use = 50;

    public String CHANGE_SOUND = "Tools/change.ogg";
    public String TOOL_WRENCH = "Tools/wrench.ogg";

    public ItemGraviTool() {
        super(GraviSuiteConfig.GRAVI_TOOL_ID, "gravitool", 2, 5000, 100000, EnumToolMaterial.IRON);
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
    public boolean shouldPassSneakingClickToBlock(World world, int x, int y, int z) {
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
            if (IC2.platform.isRendering())
                IC2.audioManager.playOnce(player, PositionSpec.Hand, CHANGE_SOUND, false, 0.05F);
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        ToolMode mode = readToolMode(stack);
        if (IC2.platform.isSimulating()) {
            if (IC2.keyboard.isModeSwitchKeyDown(player)) {
                return false;
            } else if (ElectricItem.manager.canUse(stack, this.energy_per_use)) {
                if (mode == ToolMode.HOE) {
                    return Ic2Items.electricHoe.getItem().onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
                } else if (mode == ToolMode.TREETAP) {
                    boolean treetap = Ic2Items.treetap.getItem().onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
                    if (treetap) {
                        ElectricItem.manager.use(stack, this.energy_per_use, player);
                    }
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
        if (IC2.platform.isSimulating()) {
            if (IC2.keyboard.isModeSwitchKeyDown(player)) {
                return false;
            } else {
                if (mode == ToolMode.WRENCH) {
                    boolean wrench = onWrenchUse(stack, player, world, x, y, z, side);
                    if (wrench) {
                        IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, true, IC2.audioManager.defaultVolume);
                    }
                    return wrench;
                } else if (mode == ToolMode.SCREWDRIVER) {
                    boolean screwdriver = onScrewdriverUse(stack, player, world, x, y, z, side);
                    if (screwdriver) {
                        IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, true, IC2.audioManager.defaultVolume);
                    }
                    return screwdriver;
                }
            }
        }
        return false;
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
                ElectricItem.manager.use(stack, this.energy_per_use, player);
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
                    ElectricItem.manager.use(stack, this.energy_per_use, player);
                    return simulating;
                }

                if (wrenchable.wrenchCanRemove(player)) {
                    if (simulating) {
                        if (GraviSuiteConfig.log_wrench) {
                            String blockName = block.getLocalizedName();
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
                    world.setBlockToAir(x, y, z);
                    ElectricItem.manager.use(stack, 10000, player);
                    return true;
                } else {
                    if (IC2.platform.isSimulating()) {
                        if (side != 0 && side != 1) {
                            wrenchable.setFacing((short)side);
                        } else if (wrenchable instanceof IEnergySource && wrenchable instanceof IEnergySink) {
                            wrenchable.setFacing((short)side);
                        }
                        ElectricItem.manager.use(stack, this.energy_per_use, player);
                        return true;
                    }
                }
            }
            if (Loader.isModLoaded("GregTech_Addon")) {
                if (tile instanceof gregtechmod.api.metatileentity.BaseMetaTileEntity) {
                    gregtechmod.api.metatileentity.BaseMetaTileEntity baseTileEntity = (gregtechmod.api.metatileentity.BaseMetaTileEntity) tile;
                    gregtechmod.api.metatileentity.MetaTileEntity metaTileEntity = baseTileEntity.getMetaTileEntity();
                    if (metaTileEntity != null) {
                        side = gregtechmod.api.util.GT_Utility.determineWrenchingSide((byte) side, x, y, z);
                        if (baseTileEntity.isValidFacing((byte) side) && side != baseTileEntity.getFrontFacing()) {
                            baseTileEntity.setFrontFacing((byte) side);
                            ElectricItem.manager.use(stack, this.energy_per_use, player);
                        } else if (metaTileEntity.isWrenchable()) {
                            ItemStack drop = null;
                            try {
                                Method getDropMethod = gregtechmod.api.metatileentity.BaseMetaTileEntity.class.getDeclaredMethod("getDrop");
                                getDropMethod.setAccessible(true);
                                drop = (ItemStack) getDropMethod.invoke(baseTileEntity);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            if (drop != null) {
                                world.spawnEntityInWorld(new EntityItem(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5, drop));
                                world.setBlockToAir(x, y, z);
                                ElectricItem.manager.use(stack, 10000, player);
                            }
                        }
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

    public boolean onScrewdriverUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        int blockId = world.getBlockId(x, y, z);
        int blockMeta = world.getBlockMetadata(x, y, z);
        Block block = Block.blocksList[blockId];
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (BlockHelper.getRotateType(block) != 0) {
            if (player.isSneaking()) {
                world.setBlockMetadataWithNotify(x, y, z, BlockHelper.rotateAlt(world, blockId, blockMeta, x, y, z), 3);
            } else {
                world.setBlockMetadataWithNotify(x, y, z, BlockHelper.rotate(world, blockId, blockMeta, x, y, z), 3);
            }
            ElectricItem.manager.use(stack, this.energy_per_use, player);
            return IC2.platform.isSimulating();
        } else {
            if (!(tile instanceof IEnergySource)) {
                if (Block.blocksList[blockId] != null && Block.blocksList[blockId].rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))) {
                    ElectricItem.manager.use(stack, this.energy_per_use, player);
                    return IC2.platform.isSimulating();
                }
            }
        }
        return false;
    }


    /**
     *
     * {@link IToolWrench} and
     * {@link IToolConfigurator}
     *
     * */

    @Override
    public boolean canWrench(EntityPlayer player, int x, int y, int z) {
        ItemStack stack = player.getHeldItem();
        ToolMode mode = readToolMode(stack);
        return mode == ToolMode.WRENCH;
    }

    @Override
    public void wrenchUsed(EntityPlayer player, int x, int y, int z) {
        IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, false, IC2.audioManager.defaultVolume);
        ElectricItem.manager.use(player.getHeldItem(), this.energy_per_use, player);
    }

    /**
     *
     * {@link IToolCrowbar}
     *
     * */

    @Override
    public boolean canWhack(EntityPlayer player, ItemStack stack, int x, int y, int z) {
        ToolMode mode = readToolMode(stack);
        return mode == ToolMode.WRENCH;
    }

    @Override
    public void onWhack(EntityPlayer player, ItemStack stack, int x, int y, int z) {
        IC2.audioManager.playOnce(player, PositionSpec.Hand, TOOL_WRENCH, false, IC2.audioManager.defaultVolume);
        ElectricItem.manager.use(player.getHeldItem(), this.energy_per_use, player);
    }

    @Override
    public boolean canLink(EntityPlayer player, ItemStack stack, EntityMinecart minecart) {
        ToolMode mode = readToolMode(stack);
        return mode == ToolMode.SCREWDRIVER;
    }

    @Override
    public void onLink(EntityPlayer var1, ItemStack var2, EntityMinecart var3) {}

    @Override
    public boolean canBoost(EntityPlayer var1, ItemStack var2, EntityMinecart var3) {
        return false;
    }

    @Override
    public void onBoost(EntityPlayer var1, ItemStack var2, EntityMinecart var3) {}

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
