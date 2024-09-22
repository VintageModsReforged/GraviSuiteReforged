package reforged.mods.gravisuite.items.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import net.minecraft.block.Block;
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
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.tools.base.ItemToolElectric;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;
import reforged.mods.gravisuite.utils.pos.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemAdvancedChainsaw extends ItemToolElectric {

    public int energyPerOperation = 100;
    public Set<Block> mineableBlocks = new HashSet<Block>();
    public static boolean wasEquipped = false;
    public static AudioSource audioSource;

    public static final String NBT_SHEARS = "shears", NBT_TCAPITATOR = "capitator";

    public ItemAdvancedChainsaw() {
        super(GraviSuiteConfig.ADVANCED_CHAINSAW_ID, "advanced_chainsaw", 2, 500, 15000, EnumToolMaterial.EMERALD);
        this.efficiencyOnProperMaterial = 35.0F;
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
                List<ItemStack> logs = Helpers.getStackFromOre("log");
                logs.addAll(Helpers.getStackFromOre("wood")); // just in case some mod uses old oredict name
                for (ItemStack check : logs) {
                    if (Helpers.areStacksEqual(check, blockStack)) {
                        isLog = true;
                        break;
                    }
                }

                if (isLog) {
                    BlockPos origin = new BlockPos(x, y, z);
                    Set<BlockPos> vein = Helpers.veinPos(world, origin, 128);
                    for (BlockPos coord : vein) {
                        if (coord.equals(origin)) {
                            continue;
                        }
                        if (!canOperate(stack)) {
                            break;
                        }
                        if (canOperate(stack)) {
                            if (canHarvestBlock(block, stack) && harvestBlock(world, coord.getX(), coord.getY(), coord.getZ(), player) && !player.capabilities.isCreativeMode) {
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

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int i, boolean flag) {
        boolean isEquipped = flag && entity instanceof EntityLiving;
        if (IC2.platform.isRendering()) {
            if (isEquipped && !wasEquipped) {
                if (audioSource == null) {
                    audioSource = IC2.audioManager.createSource(entity, PositionSpec.Hand, "Tools/Chainsaw/ChainsawIdle.ogg", true, false, IC2.audioManager.defaultVolume);
                }
                if (audioSource != null) {
                    audioSource.play();
                }
            } else if (!isEquipped && audioSource != null) {
                audioSource.stop();
                audioSource.remove();
                audioSource = null;
                if (entity instanceof EntityLiving) {
                    IC2.audioManager.playOnce(entity, PositionSpec.Hand, "Tools/Chainsaw/ChainsawStop.ogg", true, IC2.audioManager.defaultVolume);
                }
            } else if (audioSource != null) {
                audioSource.updatePosition();
            }
            wasEquipped = isEquipped;
        }
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
        if (audioSource != null) {
            audioSource.stop();
            audioSource.remove();
            audioSource = null;
        }
        return true;
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
}
