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
import net.minecraft.item.EnumRarity;
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
import reforged.mods.gravisuite.utils.Refs;
import reforged.mods.gravisuite.items.tools.base.ItemToolElectric;
import reforged.mods.gravisuite.utils.Helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemAdvancedChainsaw extends ItemToolElectric {

    public int energyPerOperation = 100;
    public Set<Block> mineableBlocks = new HashSet<Block>();
    public static boolean wasEquipped = false;
    public static AudioSource audioSource;

    public ItemAdvancedChainsaw() {
        super(GraviSuiteConfig.ADVANCED_CHAINSAW_ID, "advanced_chainsaw", 2, 500, 45000, EnumRarity.uncommon, EnumToolMaterial.EMERALD);
        this.efficiencyOnProperMaterial = 30.0F;
        MinecraftForge.EVENT_BUS.register(this);
        init();
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
        super.addInformation(stack, player, tooltip, par4);
        boolean isShearsOn = readToolMode(stack);
        String mode = isShearsOn ? Refs.status_on : Refs.status_off;
        tooltip.add(Refs.tool_mode_shear_gold + " " + mode);
        if (Helpers.isShiftKeyDown()) {
            tooltip.add(Helpers.pressXAndYForZ(Refs.to_enable_2, "Mode Switch Key", "Right Click", Refs.SHEAR_MODE + ".stat"));
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
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        if (IC2.platform.isRendering()) {
            return false;
        }
        if (!readToolMode(itemstack)) {
            return false;
        }
        World world = player.worldObj;
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if ((block instanceof IShearable)) {
            IShearable target = (IShearable) block;
            if ((target.isShearable(itemstack, player.worldObj, x, y, z))
                    && (ElectricItem.use(itemstack, this.energyPerOperation, player))) {
                ArrayList<ItemStack> drops = target.onSheared(itemstack, player.worldObj, x, y, z,
                        EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));
                for (ItemStack stack : drops) {
                    float f = 0.7F;
                    double d = itemRand.nextFloat() * f + (1.0F - f) * 0.5D;
                    double d1 = itemRand.nextFloat() * f + (1.0F - f) * 0.5D;
                    double d2 = itemRand.nextFloat() * f + (1.0F - f) * 0.5D;
                    EntityItem entityitem = new EntityItem(player.worldObj, x + d, y + d1, z + d2, stack);
                    entityitem.delayBeforeCanPickup = 10;
                    player.worldObj.spawnEntityInWorld(entityitem);
                }
                player.addStat(net.minecraft.stats.StatList.mineBlockStatArray[world.getBlockId(x, y, z)], 1);
            }
        }
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (IC2.platform.isSimulating()) {
            if (IC2.keyboard.isModeSwitchKeyDown(player)) {
                if (!readToolMode(itemStack)) {
                    saveToolMode(itemStack, true);
                    IC2.platform.messagePlayer(player, Refs.tool_mode_shear + " " + Refs.status_on);
                } else {
                    saveToolMode(itemStack, false);
                    IC2.platform.messagePlayer(player, Refs.tool_mode_shear + " " + Refs.status_off);
                }
            }
        }
        return itemStack;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int blockId, int xPos, int yPos, int zPos, EntityLiving entity) {
        Block block = Block.blocksList[blockId];
        if (block.getBlockHardness(world, xPos, yPos, zPos) != 0.0D) {
            if (canHarvestBlock(block, stack)) {
                if (entity != null) {
                    ElectricItem.manager.use(stack, this.energyPerOperation, null);
                } else {
                    ElectricItem.manager.discharge(stack, this.energyPerOperation, this.tier, true, false);
                }
            }
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
                    && (readToolMode(itemstack))
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

    public static boolean readToolMode(ItemStack stack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        return tag.getBoolean("shears");
    }

    public static void saveToolMode(ItemStack stack, boolean value) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        tag.setBoolean("shears", value);
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
