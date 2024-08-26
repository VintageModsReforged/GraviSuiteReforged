package reforged.mods.gravisuite.items.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.tools.base.ItemToolElectric;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;
import reforged.mods.gravisuite.utils.TextFormatter;
import reforged.mods.gravisuite.utils.pos.BlockPos;

import java.util.*;

public class ItemAdvancedDrill extends ItemToolElectric {

    public Set<Material> mineableBlockMaterials = new HashSet<Material>();
    public Set<Block> mineableBlocks = new HashSet<Block>();

    public ItemAdvancedDrill(int id, String name, int tier, int transfer, int capacity) {
        super(id, name, tier, transfer, capacity, EnumToolMaterial.EMERALD);
        init();
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean flag) {
        super.addInformation(stack, player, tooltip, flag);
        DrillMode mode = readToolMode(stack);
        DrillProps props = readToolProps(stack);
        tooltip.add(Refs.tool_mining_mode_gold + " " + mode.name);
        tooltip.add(Refs.eff_tool_mode_gold + " " + props.name);
        if (Helpers.isShiftKeyDown()) {
            tooltip.add(Helpers.pressXAndYForZ(Refs.to_change_2, "Mode Switch Key", "Right Click", Refs.MINING_MODE + ".stat"));
            tooltip.add(Helpers.pressXAndYForZ(Refs.to_change_2, "IC2 Alt Key", "Right Click", Refs.EFF_MODE + ".stat"));
        } else {
            tooltip.add(Helpers.pressForInfo(Refs.SNEAK_KEY));
        }
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return Item.pickaxeDiamond.isBookEnchantable(stack, book) || Item.shovelDiamond.isBookEnchantable(stack, book);
    }

    @Override
    public int getItemEnchantability() {
        return this.toolMaterial.getEnchantability();
    }

    @Override
    public boolean canOperate(ItemStack stack) {
        DrillProps props = readToolProps(stack);
        return ElectricItem.manager.canUse(stack, props.energy_cost);
    }

    @Override
    public boolean canHarvestBlock(Block block) {
        return Item.pickaxeDiamond.canHarvestBlock(block) || Item.shovelDiamond.canHarvestBlock(block) || this.mineableBlocks.contains(block) || this.mineableBlockMaterials.contains(block.blockMaterial);
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack stack) {
        if (canOperate(stack))
            return canHarvestBlock(block);
        return false;
    }

    @Override
    public float getStrVsBlock(ItemStack stack, Block block) {
        DrillProps props = readToolProps(stack);
        if (!canOperate(stack)) {
            return 0.1F;
        }
        if (canHarvestBlock(block, stack)) {
            return props.efficiency;
        }
        return 0.0F;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        if (IC2.platform.isSimulating()) {
            World world = player.worldObj;
            DrillMode mode = readToolMode(stack);
            DrillProps props = readToolProps(stack);
            MovingObjectPosition mop = Helpers.raytraceFromEntity(world, player, true, 4.5D);
            int block = world.getBlockId(x, y, z);
            int radius = player.isSneaking() ? 0 : 1;
            float refStrength = Block.blocksList[block].getBlockHardness(world, x, y, z);
            if (block == 0)
                return false;
            if (!canOperate(stack))
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
                                if (canOperate(stack)) {
                                    if (canHarvestBlock(adjBlock, stack) && harvestBlock(world, pos.getX(), pos.getY(), pos.getZ(), player)) {
                                        ElectricItem.manager.use(stack, props.energy_cost, player);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                ElectricItem.manager.use(stack, props.energy_cost, player);
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
        if (!canOperate(stack)) {
            return false;
        }
        if (mode == DrillMode.NORMAL) {
            ElectricItem.manager.use(stack, props.energy_cost, entity);
        }
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (IC2.platform.isSimulating()) {
            if (IC2.keyboard.isModeSwitchKeyDown(player)) {
                DrillMode nextMode = readNextToolMode(stack);
                saveToolMode(stack, nextMode);
                IC2.platform.messagePlayer(player, Refs.tool_mining_mode + " " + nextMode.name);
            }
            if (IC2.keyboard.isAltKeyDown(player)) {
                DrillProps nextProps = readNextToolProps(stack);
                saveToolProps(stack, nextProps);
                IC2.platform.messagePlayer(player, Refs.eff_tool_mode + " " + nextProps.name);
            }
        }
        return stack;
    }

    public static DrillMode readToolMode(ItemStack stack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        return DrillMode.getFromId(tag.getInteger("toolMode"));
    }

    public static DrillMode readNextToolMode(ItemStack stack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        return DrillMode.getFromId(tag.getInteger("toolMode") + 1);
    }

    public static void saveToolMode(ItemStack stack, DrillMode mode) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        tag.setInteger("toolMode", mode.ordinal());
    }

    public static DrillProps readToolProps(ItemStack stack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        return DrillProps.getFromId(tag.getInteger("toolProps"));
    }

    public static DrillProps readNextToolProps(ItemStack stack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        return DrillProps.getFromId(tag.getInteger("toolProps") + 1);
    }

    public static void saveToolProps(ItemStack stack, DrillProps mode) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        tag.setInteger("toolProps", mode.ordinal());
    }

    public enum DrillMode {
        NORMAL(Refs.tool_mode_normal), BIG_HOLES(Refs.tool_mode_big_holes);

        public static final DrillMode[] VALUES = values();
        public final String name;
        DrillMode(String name) {
            this.name = name;
        }

        public static DrillMode getFromId(int id) {
            return VALUES[id % values().length];
        }
    }

    public enum DrillProps {
        NORMAL(35.0F, 160, Refs.eff_tool_mode_normal), LOW_POWER(16.0F, 80, Refs.eff_tool_mode_low),
        FINE(10.0F, 50, Refs.eff_tool_mode_fine);

        public static final DrillProps[] VALUES = values();
        public final String name;
        public final float efficiency;
        public final int energy_cost;

        DrillProps(float efficiency, int energyCost, String name) {
            this.name = name;
            this.efficiency = efficiency;
            this.energy_cost = energyCost;
        }

        public static DrillProps getFromId(int id) {
            return VALUES[id % VALUES.length];
        }
    }

    public void init() {
        this.mineableBlocks.add(Block.glass);
        this.mineableBlocks.add(Block.thinGlass);
        this.mineableBlocks.add(Block.glowStone);
        this.mineableBlocks.add(Block.silverfish);
        this.mineableBlocks.add(Block.ice);

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

    public static class ItemAdvancedDiamondDrill extends ItemAdvancedDrill {

        public ItemAdvancedDiamondDrill() {
            super(GraviSuiteConfig.ADVANCED_DIAMOND_DRILL, "advanced_diamond_drill", 2, 500, 45000);
        }
    }

    public static class ItemAdvancedIridiumDrill extends ItemAdvancedDrill {

        private final int energy_per_use;
        private final float efficiency;

        public ItemAdvancedIridiumDrill() {
            super(GraviSuiteConfig.ADVANCED_IRIDIUM_DRILL, "advanced_iridium_drill", 3, 5000, 100000);
            this.energy_per_use = 1000;
            this.efficiency = 24.0F;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public EnumRarity getRarity(ItemStack stack) {
            return EnumRarity.rare;
        }

        @SuppressWarnings("unchecked")
        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean flag) {
            tooltip.add(TextFormatter.AQUA.format(Helpers.getCharge(stack) + "/" + this.getMaxCharge(stack) + " EU" + " @ Tier " + this.tier));
            tooltip.add(TextFormatter.GOLD.format(Refs.vein_miner));
            if (Helpers.isShiftKeyDown()) {
                tooltip.add(Helpers.pressXAndYForZ(Refs.to_change_2, Refs.SNEAK_KEY, "Right Click", Refs.ENCH_MODE + ".stat"));
            } else {
                tooltip.add(Helpers.pressForInfo(Refs.SNEAK_KEY));
            }
        }

        @Override
        public float getStrVsBlock(ItemStack stack, Block block, int meta) {
            if (!ElectricItem.manager.canUse(stack, this.energy_per_use)) {
                return 0.1F;
            }
            if (canHarvestBlock(block)) {
                return this.efficiency;
            }
            return 0.0F;
        }

        @Override
        public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
            if (IC2.platform.isSimulating()) {
                if (IC2.keyboard.isSneakKeyDown(player)) {
                    Map<Integer, Integer> enchMap = new IdentityHashMap<Integer, Integer>();
                    NBTTagList enchTagList = stack.getEnchantmentTagList();
                    if (EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack) == 0) {
                        enchMap.put(Enchantment.silkTouch.effectId, 1);
                        IC2.platform.messagePlayer(player, Refs.tool_mining_mode + " " + Refs.tool_mode_silk);
                    } else {
                        enchMap.put(Enchantment.fortune.effectId, 3);
                        IC2.platform.messagePlayer(player, Refs.tool_mining_mode + " " + Refs.tool_mode_fortune);
                    }
                    if (enchTagList != null) {
                        for (int i = 0; i < enchTagList.tagCount(); i++) {
                            int id = ((NBTTagCompound) enchTagList.tagAt(i)).getShort("id");
                            int lvl = ((NBTTagCompound) enchTagList.tagAt(i)).getShort("lvl");
                            if (id != Enchantment.fortune.effectId && id != Enchantment.silkTouch.effectId) {
                                enchMap.put(id, lvl);
                            }
                        }
                    }
                    EnchantmentHelper.setEnchantments(enchMap, stack);
                }
            }
            return stack;
        }

        @Override
        public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
            World world = player.worldObj;
            if (IC2.platform.isSimulating()) {
                Block block = Block.blocksList[world.getBlockId(x, y, z)];
                if (block == Block.oreRedstoneGlowing) {
                    block = Block.oreRedstone;
                }
                ItemStack blockStack = new ItemStack(block, 1, world.getBlockMetadata(x, y, z));
                boolean isOre = false;
                for (ItemStack oreStack : Helpers.getStackFromOre("ore")) {
                    if (oreStack.isItemEqual(blockStack)) {
                        isOre = true;
                        break;
                    }
                }
                if (!ElectricItem.manager.canUse(stack, this.energy_per_use))
                    return false;
                if (isOre && !player.capabilities.isCreativeMode) {
                    BlockPos origin = new BlockPos(x, y, z);
                    for (BlockPos coord : Helpers.veinPos(world, origin, 128)) {
                        if (coord.equals(origin)) {
                            continue;
                        }
                        if (!ElectricItem.manager.canUse(stack, this.energy_per_use)) {
                            break;
                        }
                        if (ElectricItem.manager.canUse(stack, this.energy_per_use)) {
                            if (canHarvestBlock(block, stack) && harvestBlock(world, coord.getX(), coord.getY(), coord.getZ(), player)) {
                                ElectricItem.manager.use(stack, this.energy_per_use, player);
                            }
                        }
                    }
                } else {
                    super.onBlockStartBreak(stack, x, y, z, player);
                }
            }
            return false;
        }

        @Override
        public boolean hasEffect(ItemStack stack) {
            return false;
        }
    }
}
