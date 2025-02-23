package reforged.mods.gravisuite.items.tools;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import mods.vintage.core.helpers.BlockHelper;
import mods.vintage.core.helpers.StackHelper;
import mods.vintage.core.helpers.ToolHelper;
import mods.vintage.core.helpers.Utils;
import mods.vintage.core.helpers.pos.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import org.jetbrains.annotations.Nullable;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.tools.base.ItemToolElectric;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.util.*;

public class ItemAdvancedChainsaw extends ItemToolElectric {

    public int energyPerOperation = 100;
    public Set<Block> mineableBlocks = new HashSet<Block>();

    public static final String NBT_SHEARS = "shears", NBT_TCAPITATOR = "capitator";

    public ItemAdvancedChainsaw() {
        super(GraviSuiteConfig.ADVANCED_CHAINSAW_ID, "advanced_chainsaw", 2, 500, 15000, EnumToolMaterial.EMERALD);
        this.efficiencyOnProperMaterial = 35.0F;
        MinecraftForge.setToolClass(this, "axe", 4);
        MinecraftForge.EVENT_BUS.register(this);
        init();
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
        super.addInformation(stack, player, tooltip, par4);
        boolean isShearsOn = readToolMode(stack, NBT_SHEARS);
        boolean isCapitatorOn = readToolMode(stack, NBT_TCAPITATOR);
        String modeShear = Helpers.getStatusMessage(isShearsOn);
        String modeCapitator = Helpers.getStatusMessage(isCapitatorOn);
        tooltip.add(Refs.tool_mode_shear_gold + " " + modeShear);
        if (GraviSuiteConfig.chainsaw_tree_capitator) {
            tooltip.add(Refs.tool_mode_capitator_gold + " " + modeCapitator);
        }
        if (Helpers.isShiftKeyDown()) {
            tooltip.add(Helpers.pressXAndYForZ(Refs.to_enable_2, "Mode Switch Key", "Right Click", Refs.SHEAR_MODE + ".stat"));
            if (GraviSuiteConfig.chainsaw_tree_capitator) {
                tooltip.add(Helpers.pressXAndYForZ(Refs.to_enable_2, Refs.SNEAK_KEY, "Right Click", Refs.CAPITATOR_MODE + ".stat"));
            }
        } else {
            tooltip.add(Helpers.pressForInfo(Refs.SNEAK_KEY));
        }
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack stack) {
        return ((Item.axeDiamond.canHarvestBlock(block))
                || (Item.axeDiamond.getStrVsBlock(stack, block) > 1.0F)
                || (Item.swordDiamond.canHarvestBlock(block))
                || (Item.swordDiamond.getStrVsBlock(stack, block) > 1.0F)) || (this.mineableBlocks.contains(block));
    }

    @Override
    public float getStrVsBlock(ItemStack stack, Block block, int meta) {
        if (!ElectricItem.manager.canUse(stack, this.energyPerOperation))
            return 0.5F;
        if (canHarvestBlock(block, stack))
            return this.efficiencyOnProperMaterial;
        return 1.0F;
    }

    @Override
    public boolean canOperate(ItemStack stack) {
        return ElectricItem.manager.canUse(stack, this.energyPerOperation);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        if (IC2.platform.isSimulating()) {
            if (!canOperate(stack)) {
                return false;
            }
            World world = player.worldObj;
            Block block = Block.blocksList[world.getBlockId(x, y, z)];
            if (block instanceof IShearable && readToolMode(stack, NBT_SHEARS)) {
                IShearable target = (IShearable) block;
                if (target.isShearable(stack, player.worldObj, x, y, z)) {
                    ArrayList<ItemStack> drops = target.onSheared(stack, player.worldObj, x, y, z, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));
                    for (ItemStack drop : drops) {
                        EntityItem entityitem = new EntityItem(player.worldObj, x + 0.5, y + 0.5f, z + 0.5f, drop);
                        entityitem.delayBeforeCanPickup = 10;
                        player.worldObj.spawnEntityInWorld(entityitem);
                    }
                    player.addStat(net.minecraft.stats.StatList.mineBlockStatArray[world.getBlockId(x, y, z)], 1);
                }
            }
            if (GraviSuiteConfig.chainsaw_tree_capitator && readToolMode(stack, NBT_TCAPITATOR)) {
                ItemStack blockStack = new ItemStack(block, 1, 32767);
                boolean isLog = false;
                List<ItemStack> logs = StackHelper.getStackFromOre("log");
                logs.addAll(StackHelper.getStackFromOre("wood")); // just in case some mod uses old oredict name
                for (ItemStack check : logs) {
                    if (StackHelper.areStacksEqual(check, blockStack) || isLog(block)) {
                        isLog = true;
                        break;
                    }
                }

                if (isLog) {
                    BlockPos origin = new BlockPos(x, y, z);
                    LinkedList<BlockPos> connectedLogs = scanForTree(world, origin, player.isSneaking() ? 0 : 256);
                    for (BlockPos coord : connectedLogs) {
                        if (coord.equals(origin)) {
                            continue;
                        }
                        if (!canOperate(stack)) {
                            break;
                        }
                        if (canOperate(stack)) {
                            if (canHarvestBlock(block, stack) && ToolHelper.harvestBlock(world, coord.getX(), coord.getY(), coord.getZ(), player) && !player.capabilities.isCreativeMode) {
                                ElectricItem.manager.use(stack, this.energyPerOperation, player);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (IC2.platform.isSimulating()) {
            if (IC2.keyboard.isModeSwitchKeyDown(player)) {
                boolean shears = false;
                if (!readToolMode(itemStack, NBT_SHEARS)) {
                    saveToolMode(itemStack, NBT_SHEARS, true);
                    shears = true;
                } else {
                    saveToolMode(itemStack, NBT_SHEARS, false);
                }
                IC2.platform.messagePlayer(player, Refs.tool_mode_shear + " " + Helpers.getStatusMessage(shears));
            }
            if (GraviSuiteConfig.chainsaw_tree_capitator) {
                if (IC2.keyboard.isSneakKeyDown(player)) {
                    boolean capitator = false;
                    if (!readToolMode(itemStack, NBT_TCAPITATOR)) {
                        saveToolMode(itemStack, NBT_TCAPITATOR, true);
                        capitator = true;
                    } else {
                        saveToolMode(itemStack, NBT_TCAPITATOR, false);
                    }
                    IC2.platform.messagePlayer(player, Refs.tool_mode_capitator + " " + Helpers.getStatusMessage(capitator));
                }
            }
        }
        return itemStack;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int blockId, int xPos, int yPos, int zPos, EntityLiving entity) {
        if (entity != null) {
            ElectricItem.manager.use(stack, this.energyPerOperation, entity);
        } else {
            ElectricItem.manager.discharge(stack, this.energyPerOperation, this.tier, true, false);
        }
        return false;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLiving entity, EntityLiving attacker) {
        if (ElectricItem.manager.use(stack, this.energyPerOperation, attacker)) {
            entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), 13);
        } else {
            entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), 1);
        }
        if (attacker != null && entity instanceof EntityCreeper && entity.getHealth() <= 0.0F) {
            IC2.achievements.issueAchievement((EntityPlayer)attacker, "killCreeperChainsaw");
        }
        return false;
    }

    @ForgeSubscribe
    public void onEntityInteract(EntityInteractEvent event) {
        if (IC2.platform.isSimulating()) {
            Entity entity = event.target;
            EntityPlayer player = event.entityPlayer;
            ItemStack itemstack = player.inventory.getStackInSlot(player.inventory.currentItem);
            if ((itemstack != null) && (itemstack.getItem() == this) && ((entity instanceof IShearable))
                    && (readToolMode(itemstack, NBT_SHEARS))
                    && (ElectricItem.manager.use(itemstack, this.energyPerOperation, player))) {
                IShearable target = (IShearable) entity;
                if (target.isShearable(itemstack, entity.worldObj, (int) entity.posX, (int) entity.posY, (int) entity.posZ)) {
                    ArrayList<ItemStack> drops = target.onSheared(itemstack, entity.worldObj, (int) entity.posX, (int) entity.posY, (int) entity.posZ,
                            EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));
                    for (ItemStack stack : drops) {
                        EntityItem ent = entity.entityDropItem(stack, 1.0F);
                        ent.motionY += itemRand.nextFloat() * 0.05F;
                        ent.motionX += (itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F;
                        ent.motionZ += (itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F;
                    }
                }
            }
        }
    }

    public static boolean readToolMode(ItemStack stack, String mode) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        return tag.getBoolean(mode);
    }

    public static void saveToolMode(ItemStack stack, String mode, boolean value) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        tag.setBoolean(mode, value);
    }

    public void init() {
        this.mineableBlocks.add(Block.planks);
        this.mineableBlocks.add(Block.bookShelf);
        this.mineableBlocks.add(Block.woodSingleSlab);
        this.mineableBlocks.add(Block.woodDoubleSlab);
        this.mineableBlocks.add(Block.chest);
        this.mineableBlocks.add(Block.lockedChest);
        this.mineableBlocks.add(Block.leaves);
        this.mineableBlocks.add(Block.web);
        this.mineableBlocks.add(Block.cloth);
        this.mineableBlocks.add(Block.pumpkin);
        this.mineableBlocks.add(Block.melon);
        this.mineableBlocks.add(Block.cactus);
        this.mineableBlocks.add(Block.snow);
    }

    public boolean isLog(Block block) {
        String[] logs = GraviSuiteConfig.logs;
        boolean configLogs = false;
        for (String log : logs) {
            if (Utils.instanceOf(block, log)) configLogs = true;
            break;
        }
        return block instanceof BlockLog || configLogs;
    }

    public boolean isLeaves(World world, BlockPos pos) {
        Block block = BlockHelper.getBlock(world, pos);
        String[] leaves = GraviSuiteConfig.leaves;
        boolean configLeaves = false;
        for (String leave : leaves) {
            if (Utils.instanceOf(block, leave)) configLeaves = true;
            break;
        }
        return getBOPStatus(world, pos) || configLeaves;
    }

    private boolean getBOPStatus(World world, BlockPos pos) {
        int meta = BlockHelper.getBlockMetadata(world, pos) | 8;
        Block block = BlockHelper.getBlock(world, pos);
        if (Loader.isModLoaded("BiomesOPlenty")) {
            if (Utils.instanceOf(block, "biomesoplenty.blocks.BlockBOPPetals") ||
                    Utils.instanceOf(block, "biomesoplenty.blocks.BlockBOPLeaves") ||
                    Utils.instanceOf(block, "biomesoplenty.blocks.BlockBOPColorizedLeaves") ||
                    Utils.instanceOf(block, "biomesoplenty.blocks.BlockBOPAppleLeaves")) {
                return meta >= 8 && meta <= 15;
            }
        }
        return false;
    }

    private interface BlockAction {
        boolean onBlock(BlockPos pos, Block block, boolean isRightBlock);
    }

    public LinkedList<BlockPos> scanForTree(final World world, final BlockPos startPos, int limit) {
        Block block = BlockHelper.getBlock(world, startPos);
        ItemStack blockStack = new ItemStack(block, 1, 32767);
        boolean isLog = false;
        List<ItemStack> logs = StackHelper.getStackFromOre("log");
        logs.addAll(StackHelper.getStackFromOre("wood")); // just in case some mod uses old oredict name
        for (ItemStack check : logs) {
            if (StackHelper.areStacksEqual(check, blockStack) || isLog(block)) {
                isLog = true;
                break;
            }
        }
        if (!isLog) {
            return new LinkedList<BlockPos>();
        }
        final boolean[] leavesFound = new boolean[1];
        LinkedList<BlockPos> result = recursiveSearch(world, startPos, new BlockAction() {
            @Override
            public boolean onBlock(BlockPos pos, Block block, boolean isRightBlock) {
                int metadata = BlockHelper.getBlockMetadata(world, pos) | 8;
                boolean isLeave = metadata >= 8 && metadata <= 11;
                if (block.isLeaves(world, pos.getX(), pos.getY(), pos.getZ()) && isLeave || isLeaves(world, pos)) leavesFound[0] = true;
                return true;
            }
        }, limit);
        return leavesFound[0] ? result : new LinkedList<BlockPos>();
    }

    // Recursively scan 3x3x3 cubes while keeping track of already scanned blocks to avoid cycles.
    private static LinkedList<BlockPos> recursiveSearch(final World world, final BlockPos start, @Nullable final BlockAction action, int limit) {
        Block wantedBlock = BlockHelper.getBlock(world, start);
        boolean abort = false;
        final LinkedList<BlockPos> result = new LinkedList<BlockPos>();
        final Set<BlockPos> visited = new HashSet<BlockPos>();
        final LinkedList<BlockPos> queue = new LinkedList<BlockPos>();
        queue.push(start);

        while (!queue.isEmpty()) {
            final BlockPos center = queue.pop();
            final int x0 = center.getX();
            final int y0 = center.getY();
            final int z0 = center.getZ();
            for (int z = z0 - 1; z <= z0 + 1 && !abort; ++z) {
                for (int y = y0 - 1; y <= y0 + 1 && !abort; ++y) {
                    for (int x = x0 - 1; x <= x0 + 1 && !abort; ++x) {
                        final BlockPos pos = new BlockPos(x, y, z);
                        Block checkBlock = BlockHelper.getBlock(world, pos);
                        if ((BlockHelper.isAir(world, pos) || !visited.add(pos))) {
                            continue;
                        }
                        final boolean isRightBlock = checkBlock.blockID == wantedBlock.blockID;
                        if (isRightBlock) {
                            result.add(pos);
                            if (queue.size() > limit) {
                                abort = true;
                                break;
                            }
                            queue.push(pos);
                        }
                        if (action != null) {
                            abort = !action.onBlock(pos, checkBlock, isRightBlock);
                        }
                    }
                }
            }
        }
        return !abort ? result : new LinkedList<BlockPos>();
    }
}
