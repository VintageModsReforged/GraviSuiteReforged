package reforged.mods.gravisuite.items.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import mods.vintage.core.helpers.BlockHelper;
import mods.vintage.core.helpers.StackHelper;
import mods.vintage.core.helpers.ToolHelper;
import mods.vintage.core.helpers.pos.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.features.IHighlightProvider;
import reforged.mods.gravisuite.items.features.IPropsProvider;
import reforged.mods.gravisuite.items.tools.base.ItemToolElectric;
import reforged.mods.gravisuite.utils.EnergyValues;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.KeyDescriptionHelper;
import reforged.mods.gravisuite.utils.Messages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemAdvancedChainsaw extends ItemToolElectric implements IHighlightProvider {

    public int energyPerOperation = 100;
    public Set<Block> mineableBlocks = new HashSet<Block>();

    public static final String NBT_SHEARS = "shears", NBT_TCAPITATOR = "capitator";

    public ItemAdvancedChainsaw() {
        super(GraviSuiteConfig.ADVANCED_CHAINSAW_ID.get(), "advanced_chainsaw", EnergyValues.CHAINSAW.tier, EnergyValues.CHAINSAW.transfer, EnergyValues.CHAINSAW.maxCapacity, EnumToolMaterial.EMERALD);
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
        tooltip.add(Messages.Translations.TOOL_MODE_SHEAR.toTooltip().format(modeShear));
        if (GraviSuiteConfig.chainsaw_tree_capitator) {
            tooltip.add(Messages.Translations.TOOL_MODE_CAPITATOR.toTooltip().format(modeCapitator));
        }
        if (GraviSuite.proxy.isSneakKeyDown()) {
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.MODE_KEY, KeyDescriptionHelper.Keys.RIGHT_CLICK, KeyDescriptionHelper.KeyMode.ENABLE, Messages.Translations.SHEARS_STAT.format()));
            if (GraviSuiteConfig.chainsaw_tree_capitator) {
                tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.SNEAK_KEY, KeyDescriptionHelper.Keys.RIGHT_CLICK, KeyDescriptionHelper.KeyMode.ENABLE, Messages.Translations.CAPITATOR_STAT.format()));
            }
        } else {
            tooltip.add(Helpers.pressForInfo(KeyDescriptionHelper.Keys.SNEAK_KEY.getName()));
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
                    if (StackHelper.areStacksEqual(check, blockStack) || BlockHelper.isLog(block)) {
                        isLog = true;
                        break;
                    }
                }

                if (isLog) {
                    BlockPos origin = new BlockPos(x, y, z);
                    List<BlockPos> connectedLogs = BlockHelper.scanForTree(world, origin).getPositions();
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
                IC2.platform.messagePlayer(player, Messages.Translations.TOOL_MODE_SHEAR.format(Helpers.getStatusMessage(shears)));
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
                    IC2.platform.messagePlayer(player, Messages.Translations.TOOL_MODE_CAPITATOR.format(Helpers.getStatusMessage(capitator)));
                }
            }
        }
        return itemStack;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int blockId, int xPos, int yPos, int zPos, EntityLivingBase entity) {
        if (entity != null) {
            ElectricItem.manager.use(stack, this.energyPerOperation, entity);
        } else {
            ElectricItem.manager.discharge(stack, this.energyPerOperation, this.tier, true, false);
        }
        return false;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase attacker) {
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
        NBTTagCompound tag = StackHelper.getOrCreateTag(stack);
        return tag.getBoolean(mode);
    }

    public static void saveToolMode(ItemStack stack, String mode, boolean value) {
        NBTTagCompound tag = StackHelper.getOrCreateTag(stack);
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

    @Override
    public boolean isProvidingHighlight(ItemStack stack) {
        return readToolMode(stack, NBT_TCAPITATOR);
    }

    @Override
    public List<BlockPos> getHighlightArea(BlockPos start, EntityPlayer player, MovingObjectPosition hitResult) {
        return BlockHelper.scanForTree(player.worldObj, start).getPositions();
    }

    @Override
    public int[] getHighlightColor(ItemStack stack) {
        return IPropsProvider.Props.LOW_POWER.COLOR; // green?
    }
}
