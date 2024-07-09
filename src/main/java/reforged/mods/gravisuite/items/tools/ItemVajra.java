package reforged.mods.gravisuite.items.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.tools.base.ItemToolElectric;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;
import reforged.mods.gravisuite.utils.pos.BlockPos;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ItemVajra extends ItemToolElectric {

    public ItemVajra() {
        super(GraviSuiteConfig.VAJRA_ID, "vajra", 3, 5000, 1000000, EnumToolMaterial.EMERALD);
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
        VajraMode mode = readToolMode(stack);
        VajraProps props = readToolProps(stack);
        tooltip.add(Refs.tool_mining_mode_gold + " " + mode.name);
        tooltip.add(Refs.eff_tool_mode_gold + " " + props.name);
        if (Helpers.isShiftKeyDown()) {
            tooltip.add(Helpers.pressXAndYForZ(Refs.to_change_2, "Mode Switch Key", "Right Click", Refs.MINING_MODE + ".stat"));
            tooltip.add(Helpers.pressXAndYForZ(Refs.to_change_2, "IC2 Alt Key", "Right Click", Refs.EFF_MODE + ".stat"));
            tooltip.add(Helpers.pressXAndYForZ(Refs.to_change_2, Refs.SNEAK_KEY, "Right Click", Refs.ENCH_MODE + ".stat"));
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
        VajraProps props = readToolProps(stack);
        return ElectricItem.manager.canUse(stack, props.energyCost);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (IC2.platform.isSimulating()) {
            if (IC2.keyboard.isModeSwitchKeyDown(player)) {
                VajraMode nextMode = readNextToolMode(stack);
                saveToolMode(stack, nextMode);
                IC2.platform.messagePlayer(player, Refs.tool_mining_mode + " " + nextMode.name);

            }
            if (IC2.keyboard.isAltKeyDown(player)) {
                VajraProps nextProps = readNextToolProps(stack);
                saveToolProps(stack, nextProps);
                IC2.platform.messagePlayer(player, Refs.eff_tool_mode + " " + nextProps.name);
                this.efficiencyOnProperMaterial = nextProps.efficiency;
            }

            if (IC2.keyboard.isSneakKeyDown(player)) {
                Map<Integer, Integer> enchMap = new IdentityHashMap<Integer, Integer>();
                NBTTagList enchTagList = stack.getEnchantmentTagList();
                if (EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack) == 0) {
                    enchMap.put(Enchantment.silkTouch.effectId, 1);
                    IC2.platform.messagePlayer(player, Refs.ench_mode_yellow + " " + Refs.tool_mode_silk);
                } else {
                    enchMap.put(Enchantment.fortune.effectId, 3);
                    IC2.platform.messagePlayer(player, Refs.ench_mode_yellow + " " + Refs.tool_mode_fortune);
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
        VajraMode mode = readToolMode(stack);
        VajraProps props = readToolProps(stack);

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
            boolean veinGeneral = ((mode == VajraMode.VEIN && isOre) || mode == VajraMode.VEIN_EXTENDED);
            if (veinGeneral) {
                BlockPos origin = new BlockPos(x, y, z);
                for (BlockPos coord : Helpers.veinPos(world, origin, 128)) {
                    if (coord.equals(origin)) {
                        continue;
                    }
                    if (!canOperate(stack)) {
                        break;
                    }
                    if (canOperate(stack)) {
                        if (canHarvestBlock(block) && harvestBlock(world, coord.getX(), coord.getY(), coord.getZ(), player) && !player.capabilities.isCreativeMode) {
                            ElectricItem.manager.use(stack, props.energyCost, player);
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
    public boolean hitEntity(ItemStack stack, EntityLiving entityliving, EntityLiving attacker) {
        VajraProps props = readToolProps(stack);
        if (ElectricItem.manager.use(stack, props.energyCost * 2, attacker)) {
            entityliving.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), 25);
        } else {
            entityliving.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), 1);
        }
        return false;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int blockID, int xPos, int yPos, int zPos, EntityLiving entity) {
        Block block = Block.blocksList[blockID];
        VajraProps props = readToolProps(stack);
        if (block.getBlockHardness(world, xPos, yPos, zPos) != 0.0D) {
            if (entity != null) {
                ElectricItem.manager.use(stack, props.energyCost, entity);
            } else {
                ElectricItem.manager.discharge(stack, props.energyCost, this.tier, true, false);
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
        VajraProps props = readToolProps(stack);
        if (!canOperate(stack)) {
            return 0.5F;
        }
        if (canHarvestBlock(block)) {
            return props.efficiency;
        }
        return 0.5F;
    }

    public static VajraMode readToolMode(ItemStack stack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        return VajraMode.getFromId(tag.getInteger("toolMode"));
    }

    public static VajraMode readNextToolMode(ItemStack stack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        return VajraMode.getFromId(tag.getInteger("toolMode") + 1);
    }

    public static void saveToolMode(ItemStack stack, VajraMode mode) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        tag.setInteger("toolMode", mode.ordinal());
    }

    public static VajraProps readToolProps(ItemStack stack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        return VajraProps.getFromId(tag.getInteger("toolProps"));
    }

    public static VajraProps readNextToolProps(ItemStack stack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        return VajraProps.getFromId(tag.getInteger("toolProps") + 1);
    }

    public static void saveToolProps(ItemStack stack, VajraProps mode) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        tag.setInteger("toolProps", mode.ordinal());
    }

    public enum VajraMode {
        NORMAL(Refs.tool_mode_normal),
        VEIN(Refs.tool_mode_vein),
        VEIN_EXTENDED(Refs.tool_mode_vein_extended);

        public static final VajraMode[] VALUES = values();
        public final String name;

        VajraMode(String name) {
            this.name = name;
        }

        public static VajraMode getFromId(int id) {
            return VALUES[id % VALUES.length];
        }
    }

    public enum VajraProps {
        NORMAL(128.0F, 3200, Refs.eff_tool_mode_normal),
        LOW_POWER(24.0F, 1600, Refs.eff_tool_mode_low),
        FINE(10.0F, 800, Refs.eff_tool_mode_fine);
        public static final VajraProps[] VALUES = values();
        public final String name;

        public final float efficiency;
        public final int energyCost;

        VajraProps(float efficiency, int energyCost, String name) {
            this.efficiency = efficiency;
            this.energyCost = energyCost;
            this.name = name;
        }

        public static VajraProps getFromId(int id) {
            return VALUES[id % VALUES.length];
        }
    }
}
