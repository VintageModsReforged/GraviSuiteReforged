package reforged.mods.gravisuite.items.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.item.ElectricItem;
import ic2.core.util.StackUtil;
import mods.vintage.core.helpers.BlockHelper;
import mods.vintage.core.helpers.StackHelper;
import mods.vintage.core.helpers.ToolHelper;
import mods.vintage.core.helpers.pos.BlockPos;
import net.minecraft.block.Block;
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
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import reforged.mods.gravisuite.GraviSuiteMainConfig;
import reforged.mods.gravisuite.items.IToolTipProvider;
import reforged.mods.gravisuite.items.tools.base.ItemBaseElectricItem;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ItemVajra extends ItemBaseElectricItem {

    public ItemVajra() {
        super(GraviSuiteMainConfig.VAJRA_ID, "vajra", 2, 10000, 1000000, EnumToolMaterial.EMERALD);
        this.efficiencyOnProperMaterial = 1.0F;
        this.setIconIndex(Refs.TOOLS_ID + 2);
        MinecraftForge.setToolClass(this, "pickaxe", 4);
        MinecraftForge.setToolClass(this, "shovel", 4);
        MinecraftForge.setToolClass(this, "axe", 4);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, final List tooltip, boolean par4) {
        super.addInformation(stack, player, tooltip, par4);
        VajraMode mode = readToolMode(stack);
        VajraProps props = readToolProps(stack);
        tooltip.add(Refs.tool_mining_mode_gold + " " + mode.name);
        tooltip.add(Refs.eff_tool_mode_gold + " " + props.name);
        addKeyTooltips(tooltip, new IToolTipProvider() {
            @Override
            public void addTooltip() {
                tooltip.add(Helpers.pressXAndYForZ(Refs.to_change_2, "Mode Switch Key", Refs.USE_KEY, Refs.MINING_MODE + ".stat"));
                tooltip.add(Helpers.pressXAndYForZ(Refs.to_change_2, "IC2 Alt Key", Refs.USE_KEY, Refs.EFF_MODE + ".stat"));
                tooltip.add(Helpers.pressXAndYForZ(Refs.to_change_2, Refs.SNEAK_KEY, Refs.USE_KEY, Refs.ENCH_MODE + ".stat"));
            }
        });
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (IC2.platform.isSimulating()) {
            if (IC2.keyboard.isModeSwitchKeyDown(player)) {
                VajraMode nextMode = readNextToolMode(stack);
                saveToolMode(stack, nextMode);
                IC2.platform.messagePlayer(player, Refs.tool_mode + " " + nextMode.name);

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
            for (ItemStack oreStack : StackHelper.getStackFromOre("ore")) {
                if (oreStack.isItemEqual(blockStack)) {
                    isOre = true;
                    break;
                }
            }
            boolean veinGeneral = ((mode == VajraMode.VEIN && isOre) || mode == VajraMode.VEIN_EXTENDED);
            if (veinGeneral && !player.capabilities.isCreativeMode) {
                BlockPos origin = new BlockPos(x, y, z);
                for (BlockPos coord : BlockHelper.veinPos(world, origin, player.isSneaking() ? 0 : 128)) {
                    if (coord.equals(origin)) {
                        continue;
                    }
                    if (!ElectricItem.canUse(stack, props.energyCost)) {
                        break;
                    }
                    if (ElectricItem.canUse(stack, props.energyCost)) {
                        if (canHarvestBlock(block) && ToolHelper.harvestBlock(world, coord.getX(), coord.getY(), coord.getZ(), player)) {
                            ElectricItem.use(stack, props.energyCost, player);
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
        if (ElectricItem.use(stack, props.energyCost * 2, null)) {
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
                ElectricItem.use(stack, props.energyCost, null);
            } else {
                ElectricItem.discharge(stack, props.energyCost, this.TIER, true, false);
            }
        }
        return false;
    }

    @Override
    public boolean canHarvestBlock(Block block) {
        return block != Block.bedrock && block != Block.portal;
    }

    @Override
    public float getStrVsBlock(ItemStack stack, Block block, int meta) {
        VajraProps props = readToolProps(stack);
        if (!ElectricItem.canUse(stack, props.energyCost)) {
            return 0.5F;
        }
        if (canHarvestBlock(block)) {
            return props.efficiency;
        }
        return 0.5F;
    }

    public static VajraMode readToolMode(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return VajraMode.getFromId(tag.getInteger("toolMode"));
    }

    public static VajraMode readNextToolMode(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return VajraMode.getFromId(tag.getInteger("toolMode") + 1);
    }

    public static void saveToolMode(ItemStack stack, VajraMode mode) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        tag.setInteger("toolMode", mode.ordinal());
    }

    public static VajraProps readToolProps(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return VajraProps.getFromId(tag.getInteger("toolProps"));
    }

    public static VajraProps readNextToolProps(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return VajraProps.getFromId(tag.getInteger("toolProps") + 1);
    }

    public static void saveToolProps(ItemStack stack, VajraProps mode) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
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
