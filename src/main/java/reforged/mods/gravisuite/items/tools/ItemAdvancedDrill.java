package reforged.mods.gravisuite.items.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.item.ElectricItem;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import reforged.mods.gravisuite.GraviSuiteMainConfig;
import reforged.mods.gravisuite.items.tools.base.ItemBaseElectricItem;
import reforged.mods.gravisuite.utils.BlockPos;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ItemAdvancedDrill extends ItemBaseElectricItem {

    public Set<Block> mineableBlocks = new HashSet<Block>();
    public Set<Material> mineableBlockMaterials = new HashSet<Material>();

    public ItemAdvancedDrill() {
        super(GraviSuiteMainConfig.ADVANCED_DRILL_ID, "advanced_diamond_drill", 2, 500, 15000, EnumToolMaterial.EMERALD);
        this.setIconIndex(Refs.TOOLS_ID);
        MinecraftForge.setToolClass(this, "pickaxe", 4);
        MinecraftForge.setToolClass(this, "shovel", 4);
        init();
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List toolTips, boolean flag) {
        super.addInformation(stack, player, toolTips, flag);
        DrillMode mode = readToolMode(stack);
        DrillProps props = readToolProps(stack);
        toolTips.add(Refs.tool_mining_mode_gold + " " + mode.NAME);
        toolTips.add(Refs.eff_tool_mode_gold + " " + props.NAME);
        if (Helpers.isShiftKeyDown()) {
            toolTips.add(Helpers.pressXAndYForZ(Refs.to_change_2, "Mode Switch Key", "Right Click", Refs.MINING_MODE + ".stat"));
            toolTips.add(Helpers.pressXAndYForZ(Refs.to_change_2, "IC2 Alt Key", "Right Click", Refs.EFF_MODE + ".stat"));
        } else {
            toolTips.add(Helpers.pressForInfo(Refs.SNEAK_KEY));
        }
    }

    @Override
    public boolean canHarvestBlock(Block block) {
        return pickaxeDiamond.canHarvestBlock(block) || shovelDiamond.canHarvestBlock(block) ||
                this.mineableBlocks.contains(block) || this.mineableBlockMaterials.contains(block.blockMaterial);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, Block block, int meta) {
        DrillProps props = readToolProps(stack);
        if (!ElectricItem.canUse(stack, props.ENERGY_COST)) {
            return 0.5F;
        }
        if (canHarvestBlock(block) || ForgeHooks.isToolEffective(stack, block, meta)) {
            return props.EFFICIENCY;
        }
        return 0.5F;
    }

    @Override
    public float getStrVsBlock(ItemStack stack, Block block) {
        DrillProps props = readToolProps(stack);
        if (!ElectricItem.canUse(stack, props.ENERGY_COST)) {
            return 0.5F;
        }
        if (canHarvestBlock(block) || ForgeHooks.isToolEffective(stack, block, 0)) {
            return props.EFFICIENCY;
        }
        return 0.5F;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        if (IC2.platform.isSimulating()) {
            World world = player.worldObj;
            DrillMode mode = readToolMode(stack);
            DrillProps props = readToolProps(stack);
            MovingObjectPosition mop = Helpers.raytraceFromEntity(world, player, false, 4.5D);
            int block = world.getBlockId(x, y, z);
            int radius = player.isSneaking() ? 0 : 1;
            float refStrength = Block.blocksList[block].getBlockHardness(world, x, y, z);
            if (block == 0)
                return false;
            if (!ElectricItem.canUse(stack, props.ENERGY_COST))
                return false;
            if (mode == DrillMode.BIG_HOLES) {
                if (mop == null) { // cancel 3x3 when rayTrace fails
                    return false;
                }
                if (refStrength != 0.0D) {
                    int xRange = radius, yRange = radius, zRange = radius;
                    switch (mop.sideHit) {
                        case 0:
                        case 1:
                            yRange = 0;
                            break;
                        case 2:
                        case 3:
                            zRange = 0;
                            break;
                        case 4:
                        case 5:
                            xRange = 0;
                            break;
                    }
                    BlockPos origin = new BlockPos(x, y, z);
                    for (BlockPos pos : BlockPos.getAllInBoxMutable(origin.add(-xRange, -yRange, -zRange), origin.add(xRange, yRange, zRange))) {
                        Block adjBlock = Block.blocksList[world.getBlockId(pos.getX(), pos.getY(), pos.getZ())];
                        if (!world.isAirBlock(pos.getX(), pos.getY(), pos.getZ())) {
                            float strength = adjBlock.getBlockHardness(world, pos.getX(), pos.getY(), pos.getZ());
                            if (strength > 0f && strength / refStrength <= 8f) {
                                if (ElectricItem.canUse(stack, props.ENERGY_COST)) {
                                    if (canHarvestBlock(adjBlock) && harvestBlock(world, pos.getX(), pos.getY(), pos.getZ(), player)) {
                                        ElectricItem.use(stack, props.ENERGY_COST, player);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                ElectricItem.use(stack, props.ENERGY_COST, player);
            }
        }
        return false;
    }



    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int blockId, int x, int y, int z, EntityLiving entity) {
        DrillMode mode = readToolMode(stack);
        DrillProps props = readToolProps(stack);
        if (blockId != 0) {
            return false;
        }
        if (!ElectricItem.canUse(stack, props.ENERGY_COST)) {
            return false;
        }
        if (mode == DrillMode.NORMAL) {
            ElectricItem.use(stack, props.ENERGY_COST, null);
        }
        return true;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        if (!IC2.keyboard.isModeSwitchKeyDown(player) && !IC2.keyboard.isAltKeyDown(player) && !IC2.keyboard.isSneakKeyDown(player)) {
            for (int i = 0; i < player.inventory.mainInventory.length; i++) {
                ItemStack check = player.inventory.mainInventory[i];
                if (check != null) {
                    if(check.getDisplayName().toLowerCase(Locale.ENGLISH).contains("torch")) {
                        Item item = check.getItem();
                        if (item instanceof net.minecraft.item.ItemBlock) {
                            int oldMeta = check.getItemDamage();
                            int oldSize = check.stackSize;
                            boolean result = check.tryPlaceItemIntoWorld(player, world, x, y, z, side, xOffset,
                                    yOffset, zOffset);
                            if (player.capabilities.isCreativeMode) {
                                check.setItemDamage(oldMeta);
                                check.stackSize = oldSize;
                            } else if (check.stackSize <= 0) {
                                ForgeEventFactory.onPlayerDestroyItem(player, check);
                                player.inventory.mainInventory[i] = null;
                            }
                            if (result)
                                return true;
                        }
                    }
                }
            }
        }
        return super.onItemUse(stack, player, world, x, y, z, side, xOffset, yOffset, zOffset);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (IC2.platform.isSimulating()) {
            if (IC2.keyboard.isModeSwitchKeyDown(player)) {
                DrillMode nextMode = readNextToolMode(stack);
                saveToolMode(stack, nextMode);
                IC2.platform.messagePlayer(player, Refs.tool_mining_mode + " " + nextMode.NAME);
            }
            if (IC2.keyboard.isAltKeyDown(player)) {
                DrillProps nextProps = readNextToolProps(stack);
                saveToolProps(stack, nextProps);
                IC2.platform.messagePlayer(player, Refs.eff_tool_mode + " " + nextProps.NAME);
            }
        }
        return stack;
    }

    public static DrillMode readToolMode(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return DrillMode.getFromId(tag.getInteger("toolMode"));
    }

    public static DrillMode readNextToolMode(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return DrillMode.getFromId(tag.getInteger("toolMode") + 1);
    }

    public static void saveToolMode(ItemStack stack, DrillMode mode) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        tag.setInteger("toolMode", mode.ordinal());
    }

    public static DrillProps readToolProps(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return DrillProps.getFromId(tag.getInteger("toolProps"));
    }

    public static DrillProps readNextToolProps(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return DrillProps.getFromId(tag.getInteger("toolProps") + 1);
    }

    public static void saveToolProps(ItemStack stack, DrillProps mode) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        tag.setInteger("toolProps", mode.ordinal());
    }

    public enum DrillMode {
        NORMAL(Refs.tool_mode_normal), BIG_HOLES(Refs.tool_mode_big_holes);

        public static final DrillMode[] VALUES = values();
        public final String NAME;
        DrillMode(String name) {
            this.NAME = name;
        }

        public static DrillMode getFromId(int id) {
            return VALUES[id % values().length];
        }
    }

    public enum DrillProps {
        NORMAL(35.0F, 160, Refs.eff_tool_mode_normal), LOW_POWER(16.0F, 80, Refs.eff_tool_mode_low),
        FINE(10.0F, 50, Refs.eff_tool_mode_fine);

        public static final DrillProps[] VALUES = values();
        public final String NAME;
        public final float EFFICIENCY;
        public final int ENERGY_COST;

        DrillProps(float efficiency, int energyCost, String name) {
            this.NAME = name;
            this.EFFICIENCY = efficiency;
            this.ENERGY_COST = energyCost;
        }

        public static DrillProps getFromId(int id) {
            return VALUES[id % VALUES.length];
        }
    }

    public void init() {
        this.mineableBlocks.add(Block.grass);
        this.mineableBlocks.add(Block.dirt);
        this.mineableBlocks.add(Block.mycelium);
        this.mineableBlocks.add(Block.sand);
        this.mineableBlocks.add(Block.gravel);
        this.mineableBlocks.add(Block.snow);
        this.mineableBlocks.add(Block.blockSnow);
        this.mineableBlocks.add(Block.blockClay);
        this.mineableBlocks.add(Block.slowSand);
        this.mineableBlocks.add(Block.glass);
        this.mineableBlocks.add(Block.thinGlass);
        this.mineableBlocks.add(Block.glowStone);
        this.mineableBlocks.add(Block.silverfish);
        this.mineableBlocks.add(Block.cobblestone);
        this.mineableBlocks.add(Block.stoneSingleSlab);
        this.mineableBlocks.add(Block.stoneDoubleSlab);
        this.mineableBlocks.add(Block.stairCompactCobblestone);
        this.mineableBlocks.add(Block.stone);
        this.mineableBlocks.add(Block.sandStone);
        this.mineableBlocks.add(Block.stairsSandStone);
        this.mineableBlocks.add(Block.cobblestoneMossy);
        this.mineableBlocks.add(Block.oreIron);
        this.mineableBlocks.add(Block.blockSteel);
        this.mineableBlocks.add(Block.oreCoal);
        this.mineableBlocks.add(Block.blockGold);
        this.mineableBlocks.add(Block.oreGold);
        this.mineableBlocks.add(Block.oreDiamond);
        this.mineableBlocks.add(Block.blockDiamond);
        this.mineableBlocks.add(Block.ice);
        this.mineableBlocks.add(Block.netherrack);
        this.mineableBlocks.add(Block.oreLapis);
        this.mineableBlocks.add(Block.blockLapis);
        this.mineableBlocks.add(Block.oreRedstone);
        this.mineableBlocks.add(Block.oreRedstoneGlowing);
        this.mineableBlocks.add(Block.brick);
        this.mineableBlocks.add(Block.stairsBrick);
        this.mineableBlocks.add(Block.tilledField);
        this.mineableBlocks.add(Block.stoneBrick);
        this.mineableBlocks.add(Block.stairsStoneBrickSmooth);
        this.mineableBlocks.add(Block.netherBrick);
        this.mineableBlocks.add(Block.stairsNetherBrick);
        this.mineableBlocks.add(Block.obsidian);
        this.mineableBlocks.add(Block.anvil);

        this.mineableBlockMaterials.add(Material.anvil);
        this.mineableBlockMaterials.add(Material.circuits);
        this.mineableBlockMaterials.add(Material.clay);
        this.mineableBlockMaterials.add(Material.craftedSnow);
        this.mineableBlockMaterials.add(Material.dragonEgg);
        this.mineableBlockMaterials.add(Material.glass);
        this.mineableBlockMaterials.add(Material.grass);
        this.mineableBlockMaterials.add(Material.ground);
        this.mineableBlockMaterials.add(Material.iron);
        this.mineableBlockMaterials.add(Material.piston);
        this.mineableBlockMaterials.add(Material.redstoneLight);
        this.mineableBlockMaterials.add(Material.rock);
        this.mineableBlockMaterials.add(Material.sand);
        this.mineableBlockMaterials.add(Material.snow);
    }
}
