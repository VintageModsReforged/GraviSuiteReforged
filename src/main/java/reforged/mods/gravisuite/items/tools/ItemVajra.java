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
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.features.IEnchantmentProvider;
import reforged.mods.gravisuite.items.features.IHighlightProvider;
import reforged.mods.gravisuite.items.features.IPropsProvider;
import reforged.mods.gravisuite.items.features.IVeinMiner;
import reforged.mods.gravisuite.items.tools.base.ItemToolElectric;
import reforged.mods.gravisuite.utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ItemVajra extends ItemToolElectric implements IPropsProvider, IVeinMiner, IEnchantmentProvider, IHighlightProvider {

    public ItemVajra() {
        super(GraviSuiteConfig.VAJRA_ID.get(), "vajra", EnergyValues.VAJRA.tier, EnergyValues.VAJRA.transfer, EnergyValues.VAJRA.maxCapacity, EnumToolMaterial.EMERALD);
        MinecraftForge.setToolClass(this, "axe", 4);
        MinecraftForge.setToolClass(this, "pickaxe", 4);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
        super.addInformation(stack, player, tooltip, par4);
        Vein vein = VeinMinerHelper.getVein(stack);
        Props props = PropsHelper.getProps(stack);
        tooltip.add(Messages.Translations.TOOL_VEIN_MODE.toTooltip().format(vein.MESSAGE));
        tooltip.add(Messages.Translations.EFF_TOOL_MODE.toTooltip().format(props.MESSAGE));
        tooltip.add(Messages.Translations.ENCH_MODE.toTooltip().format(EnchantHelper.getEnch(stack).MESSAGE));
        if (GraviSuite.proxy.isSneakKeyDown()) {
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.MODE_KEY, KeyDescriptionHelper.Keys.RIGHT_CLICK, KeyDescriptionHelper.KeyMode.CHANGE, Messages.Translations.VEIN_MODE_STAT.format()));
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.ALT_KEY, KeyDescriptionHelper.Keys.RIGHT_CLICK, KeyDescriptionHelper.KeyMode.CHANGE, Messages.Translations.EFF_MODE_STAT.format()));
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.SNEAK_KEY, KeyDescriptionHelper.Keys.RIGHT_CLICK, KeyDescriptionHelper.KeyMode.CHANGE, Messages.Translations.ENCH_MODE_STAT.format()));
        } else {
            tooltip.add(Helpers.pressForInfo(Refs.SNEAK_KEY));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canOperate(ItemStack stack) {
        Props props = PropsHelper.getProps(stack);
        return ElectricItem.manager.canUse(stack, props.COST.getVajra());
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (IC2.platform.isSimulating()) {
            if (IC2.keyboard.isModeSwitchKeyDown(player)) {
                Vein nextVein = VeinMinerHelper.cycleAndSave(stack);
                IC2.platform.messagePlayer(player, Messages.Translations.TOOL_VEIN_MODE.format(nextVein.MESSAGE));
            }
            if (IC2.keyboard.isAltKeyDown(player)) {
                Props nextProps = PropsHelper.cycleAndSave(stack);
                IC2.platform.messagePlayer(player, Messages.Translations.EFF_TOOL_MODE.format(nextProps.MESSAGE));
                this.efficiencyOnProperMaterial = nextProps.EFF.getVajra();
            }

            if (IC2.keyboard.isSneakKeyDown(player)) {
                EnchantmentMode next = EnchantHelper.cycleAndSave(stack);
                IC2.platform.messagePlayer(player, Messages.Translations.ENCH_MODE.format(next.MESSAGE));
            }
        }
        return stack;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        World world = player.worldObj;
        Vein vein = VeinMinerHelper.getVein(stack);
        Props props = PropsHelper.getProps(stack);

        if (!IC2.platform.isSimulating()) return false;

        if (!vein.matches(world, x, y, z)) return super.onBlockStartBreak(stack, x, y, z, player);

        BlockPos origin = new BlockPos(x, y, z);
        Set<BlockPos> veinSet = BlockHelper.veinPos(world, origin, player.isSneaking() ? 0 : VintageConfig.veinMaxCount);

        for (BlockPos coord : veinSet) {
            if (coord.equals(origin)) continue; // skip origin

            if (!canOperate(stack)) break; // stop if we can't operate

            Block block = BlockHelper.getBlock(world, coord);
            if (!canHarvestBlock(block)) continue; // skip non-harvestable blocks

            if (ToolHelper.harvestBlock(world, coord.getX(), coord.getY(), coord.getZ(), player) && !player.capabilities.isCreativeMode) {
                ElectricItem.manager.use(stack, props.COST.getVajra(), player);
            }
        }

        return false;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLiving entityliving, EntityLiving attacker) {
        Props props = PropsHelper.getProps(stack);
        if (ElectricItem.manager.use(stack, props.COST.getVajra() * 2, attacker)) {
            entityliving.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), 25);
        } else {
            entityliving.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), 1);
        }
        return false;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int blockID, int xPos, int yPos, int zPos, EntityLiving entity) {
        Block block = Block.blocksList[blockID];
        Props props = PropsHelper.getProps(stack);
        int cost = props.COST.getVajra();
        if (block.getBlockHardness(world, xPos, yPos, zPos) != 0.0D) {
            if (entity != null) {
                ElectricItem.manager.use(stack, cost, entity);
            } else {
                ElectricItem.manager.discharge(stack, cost, this.tier, true, false);
            }
        }
        return true;
    }

    @Override
    public boolean canHarvestBlock(Block block) {
        return block != Block.bedrock && block != Block.portal;
    }

    @Override
    public float getStrVsBlock(ItemStack stack, Block block, int meta) {
        Props props = PropsHelper.getProps(stack);
        if (!canOperate(stack)) {
            return 0.5F;
        }
        if (canHarvestBlock(block)) {
            return props.EFF.getVajra();
        }
        return 0.5F;
    }

    @Override
    public boolean isProvidingHighlight(ItemStack stack) {
        Vein vein = VeinMinerHelper.getVein(stack);
        return vein == Vein.ORES || vein == Vein.EXTENDED;
    }

    @Override
    public List<BlockPos> getHighlightArea(BlockPos start, EntityPlayer player, MovingObjectPosition hitResult) {
        World world = player.worldObj;
        Vein vein = VeinMinerHelper.getVein(player.getHeldItem());
        if (vein != Vein.OFF && vein.matches(world, start.getX(), start.getY(), start.getZ())) {
            return new ArrayList<BlockPos>(BlockHelper.veinPos(world, start, VintageConfig.veinMaxCount));
        } else return Collections.emptyList();
    }

    @Override
    public int[] getHighlightColor(ItemStack stack) {
        Props props = PropsHelper.getProps(stack);
        return props.COLOR;
    }
}
