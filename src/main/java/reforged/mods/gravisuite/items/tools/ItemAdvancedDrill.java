package reforged.mods.gravisuite.items.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import mods.vintage.core.VintageConfig;
import mods.vintage.core.helpers.BlockHelper;
import mods.vintage.core.helpers.ToolHelper;
import mods.vintage.core.helpers.pos.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.features.*;
import reforged.mods.gravisuite.items.tools.base.ItemToolElectric;
import reforged.mods.gravisuite.utils.*;

import java.util.*;

public class ItemAdvancedDrill extends ItemToolElectric implements IPropsProvider, IAOEProvider, IHighlightProvider {

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
        Mode mode = AOEHelper.getMode(stack);
        Props props = PropsHelper.getProps(stack);
        tooltip.add(Messages.Translations.TOOL_MODE_AOE.toTooltip().format(mode.MESSAGE));
        tooltip.add(Messages.Translations.EFF_TOOL_MODE.toTooltip().format(props.MESSAGE));
        if (GraviSuite.proxy.isSneakKeyDown()) {
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.MODE_KEY, KeyDescriptionHelper.Keys.RIGHT_CLICK, KeyDescriptionHelper.KeyMode.CHANGE, Messages.Translations.AOE_STAT.format()));
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.ALT_KEY, KeyDescriptionHelper.Keys.RIGHT_CLICK, KeyDescriptionHelper.KeyMode.CHANGE, Messages.Translations.EFF_MODE_STAT.format()));
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
        Props props = PropsHelper.getProps(stack);
        return ElectricItem.manager.canUse(stack, props.COST.getDrill());
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
        Props props = PropsHelper.getProps(stack);
        if (!canOperate(stack)) {
            return 0.1F;
        }
        if (canHarvestBlock(block, stack)) {
            return props.EFF.getDrill();
        }
        return 0.1F;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        if (IC2.platform.isSimulating()) {
            World world = player.worldObj;
            Mode mode = AOEHelper.getMode(stack);
            Props props = PropsHelper.getProps(stack);
            int block = world.getBlockId(x, y, z);
            int radius = player.isSneaking() ? 0 : 1;
            float refStrength = Block.blocksList[block].getBlockHardness(world, x, y, z);
            if (block == 0)
                return false;
            if (!canOperate(stack))
                return false;
            if (mode == Mode.BIG_HOLES) {
                if (refStrength != 0.0D) {
                    BlockPos origin = new BlockPos(x, y, z);
                    for (BlockPos pos : ToolHelper.getAOE(player, origin, radius)) {
                        Block adjBlock = Block.blocksList[world.getBlockId(pos.getX(), pos.getY(), pos.getZ())];
                        if (!world.isAirBlock(pos.getX(), pos.getY(), pos.getZ())) {
                            float strength = adjBlock.getBlockHardness(world, pos.getX(), pos.getY(), pos.getZ());
                            if (strength > 0f && strength / refStrength <= 8f) {
                                if (canOperate(stack)) {
                                    if (canHarvestBlock(adjBlock, stack) && ToolHelper.harvestBlock(world, pos.getX(), pos.getY(), pos.getZ(), player)) {
                                        ElectricItem.manager.use(stack, props.COST.getDrill(), player);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                ElectricItem.manager.use(stack, props.COST.getDrill(), player);
            }
        }
        return false;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int blockId, int x, int y, int z, EntityLivingBase entity) {
        Mode mode = AOEHelper.getMode(stack);
        Props props = PropsHelper.getProps(stack);
        if (blockId != 0) {
            return false;
        }
        if (!canOperate(stack)) {
            return false;
        }
        if (mode == Mode.NORMAL) {
            ElectricItem.manager.use(stack, props.COST.getDrill(), entity);
        }
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (IC2.platform.isSimulating()) {
            if (IC2.keyboard.isModeSwitchKeyDown(player)) {
                Mode nextMode = AOEHelper.cycleAndSave(stack);
                IC2.platform.messagePlayer(player, Messages.Translations.TOOL_MODE_AOE.format(nextMode.MESSAGE));
            }
            if (IC2.keyboard.isAltKeyDown(player)) {
                Props nextProps = PropsHelper.cycleAndSave(stack);
                IC2.platform.messagePlayer(player, Messages.Translations.EFF_TOOL_MODE.format(nextProps.MESSAGE));
            }
        }
        return stack;
    }

    @Override
    public boolean isProvidingHighlight(ItemStack stack) {
        return AOEHelper.getMode(stack) == Mode.BIG_HOLES;
    }

    @Override
    public List<BlockPos> getHighlightArea(BlockPos start, EntityPlayer player, MovingObjectPosition hitResult) {
        return ToolHelper.getAOE(player, start, player.isSneaking() ? 0 : 1);
    }

    @Override
    public int[] getHighlightColor(ItemStack stack) {
        return PropsHelper.getProps(stack).COLOR;
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
            super(GraviSuiteConfig.ADVANCED_DIAMOND_DRILL.get(), "advanced_diamond_drill", EnergyValues.DIAMOND_DRILL.tier, EnergyValues.DIAMOND_DRILL.transfer, EnergyValues.DIAMOND_DRILL.maxCapacity);
        }
    }

    public static class ItemAdvancedIridiumDrill extends ItemAdvancedDrill {

        private final int energy_per_use;
        private final float efficiency;

        public ItemAdvancedIridiumDrill() {
            super(GraviSuiteConfig.ADVANCED_IRIDIUM_DRILL.get(), "advanced_iridium_drill", EnergyValues.IRIDIUM_DRILL.tier, EnergyValues.IRIDIUM_DRILL.transfer, EnergyValues.IRIDIUM_DRILL.maxCapacity);
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
            this.addEnergyInfo(stack, tooltip);
            tooltip.add(Messages.Translations.VEIN_MINER.format());
            tooltip.add(Messages.Translations.ENCH_MODE.toTooltip().format(IEnchantmentProvider.EnchantHelper.getEnch(stack).MESSAGE));
            if (GraviSuite.proxy.isSneakKeyDown()) {
                tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.SNEAK_KEY, KeyDescriptionHelper.Keys.RIGHT_CLICK, KeyDescriptionHelper.KeyMode.CHANGE, Messages.Translations.ENCH_MODE_STAT.format()));
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
                    IEnchantmentProvider.EnchantmentMode nextEnch = IEnchantmentProvider.EnchantHelper.cycleAndSave(stack);
                    IC2.platform.messagePlayer(player, Messages.Translations.ENCH_MODE.format(nextEnch.MESSAGE));
                }
            }
            return stack;
        }

        @Override
        public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
            World world = player.worldObj;

            if (!IC2.platform.isSimulating() || player.capabilities.isCreativeMode) return false;

            if (!IVeinMiner.Vein.ORES.matches(world, x, y, z)) return super.onBlockStartBreak(stack, x, y, z, player);

            if (!ElectricItem.manager.canUse(stack, this.energy_per_use)) return false;

            BlockPos origin = new BlockPos(x, y, z);

            for (BlockPos coord : BlockHelper.veinPos(world, origin, VintageConfig.veinMaxCount)) {
                if (coord.equals(origin)) continue; // Skip the origin block

                if (!ElectricItem.manager.canUse(stack, this.energy_per_use)) break;

                Block block = BlockHelper.getBlock(world, coord);
                if (!canHarvestBlock(block, stack)) continue;

                if (ToolHelper.harvestBlock(world, coord.getX(), coord.getY(), coord.getZ(), player)) {
                    ElectricItem.manager.use(stack, this.energy_per_use, player);
                }
            }
            return false;
        }

        @Override
        public boolean hasEffect(ItemStack stack) {
            return false;
        }

        @Override
        public boolean isProvidingHighlight(ItemStack stack) {
            return true;
        }

        @Override
        public int[] getHighlightColor(ItemStack stack) {
            return Props.FINE.COLOR;
        }

        @Override
        public List<BlockPos> getHighlightArea(BlockPos start, EntityPlayer player, MovingObjectPosition hitResult) {
            World world = player.worldObj;
            if (IVeinMiner.Vein.ORES.matches(world, start.getX(), start.getY(), start.getZ())) {
                return new ArrayList<BlockPos>(BlockHelper.veinPos(world, start, VintageConfig.veinMaxCount));
            } else return Collections.emptyList();
        }
    }
}
