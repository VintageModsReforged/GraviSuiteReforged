package reforged.mods.gravisuite.items.armors;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.item.ElectricItem;
import ic2.core.util.StackUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteMainConfig;
import reforged.mods.gravisuite.audio.IAudioProvider;
import reforged.mods.gravisuite.items.armors.base.ItemBaseEnergyPack;
import reforged.mods.gravisuite.keyboard.GraviSuiteKeyboardClient;
import reforged.mods.gravisuite.proxy.CommonProxy;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.util.List;

public class ItemAdvancedQuant extends ItemBaseEnergyPack implements ISpecialArmor, IAudioProvider {

    public int ENERGY_PER_DAMAGE = 800;
    public static int MIN_CHARGE = 80000;
    public int BOOST_MULTIPLIER;
    public int USAGE_IN_AIR;
    public int USAGE_ON_GROUND;
    public float BOOST_SPPED;
    public static byte TOGGLE_TIMER;

    public ItemAdvancedQuant() {
        super(GraviSuiteMainConfig.ADVANCED_QUANT_ID, 4, "advanced_quant", 2, 20000, 10000000);
        this.USAGE_IN_AIR = 278;
        this.USAGE_ON_GROUND = 1;
        this.BOOST_SPPED = 0.2F;
        this.BOOST_MULTIPLIER = 3;
        MinecraftForge.EVENT_BUS.register(this);
        this.TOGGLE_TIMER = 5;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
        super.addInformation(stack, player, tooltip, par4);
        boolean isGraviEngineOn = readFlyStatus(stack);
        boolean isLevitationOn = readWorkMode(stack);
        String gravitationEngine = isGraviEngineOn ? Refs.status_on : Refs.status_off;
        String levitationStatus = isLevitationOn ? Refs.status_on : Refs.status_off;
        tooltip.add(Refs.gravitation_engine + " " + gravitationEngine);
        tooltip.add(Refs.gravitation_levitation + " " + levitationStatus);
        if (Helpers.isShiftKeyDown()) {
            tooltip.add(Helpers.pressXForY(Refs.to_enable_1, StatCollector.translateToLocal(GraviSuiteKeyboardClient.engine_toggle.keyDescription), Refs.GRAVITATION_ENGINE + ".stat"));
            tooltip.add(Helpers.pressXAndYForZ(Refs.to_enable_2, "Mode Switch Key", StatCollector.translateToLocal(Minecraft.getMinecraft().gameSettings.keyBindJump.keyDescription), Refs.LEVITATION + ".stat"));
            tooltip.add(Helpers.pressXForY(Refs.to_enable_1, "Boost Key", Refs.BOOST_MODE));
        } else {
            tooltip.add(Helpers.pressForInfo(Refs.SNEAK_KEY));
        }
    }

    @Override
    public void onArmorTickUpdate(World worldObj, EntityPlayer player, ItemStack itemStack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(itemStack);
        byte toggleTimer = tag.getByte("toggleTimer");

        if (GraviSuite.KEYBOARD.isEngineToggleKeyDown(player) && toggleTimer == 0) {
            switchFlyState(player, itemStack);
        }

        if (IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
            switchWorkMode(player, itemStack);
        }

        if (IC2.platform.isSimulating() && toggleTimer > 0) {
            toggleTimer--;
            tag.setByte("toggleTimer", toggleTimer);
        }

        if (readFlyStatus(itemStack)) {
            use(player, itemStack);
            player.capabilities.allowFlying = true;
            if (readWorkMode(itemStack)) {
                player.capabilities.isFlying = true;
            }
        } else {
            if (!player.capabilities.isCreativeMode) {
                player.capabilities.allowFlying = false;
                player.capabilities.isFlying = false;
                CommonProxy.isFlyActive.put(player, true);
                IC2.platform.resetPlayerInAirTime(player);
            }
        }

        CommonProxy.wasUndressed.put(player, false);
        if (player.isBurning()) {
            player.extinguish();
        }
    }

    public void use(EntityPlayer player, ItemStack itemStack) {
        double currCharge = Helpers.getCharge(itemStack);
        if (!player.capabilities.isCreativeMode) {
            if (currCharge < USAGE_IN_AIR) {
                IC2.platform.messagePlayer(player, Refs.status_shutdown);
                switchFlyState(player, itemStack);
            } else if (!player.onGround) {
                ElectricItem.discharge(itemStack, USAGE_IN_AIR, 3, false, false);
            } else {
                ElectricItem.discharge(itemStack, USAGE_ON_GROUND, 3, false, false);
            }
        }
        player.fallDistance = 0.0F;
        if ((!player.onGround) && (player.capabilities.isFlying) && (IC2.keyboard.isBoostKeyDown(player))) {
            boostMode(player, itemStack);
            if ((currCharge > USAGE_IN_AIR * BOOST_MULTIPLIER) || (player.capabilities.isCreativeMode)) {
                if (IC2.keyboard.isJumpKeyDown(player)) {
                    player.motionY += BOOST_SPPED + 0.1F;
                }
                if (IC2.keyboard.isSneakKeyDown(player)) {
                    player.motionY -= BOOST_SPPED + 0.1F;
                }
                if (!player.capabilities.isCreativeMode) {
                    ElectricItem.discharge(itemStack, USAGE_IN_AIR * BOOST_MULTIPLIER, 3, true, false);
                }
            } else {
                IC2.platform.messagePlayer(player, Refs.status_low);

            }
        }
    }

    public void boostMode(EntityPlayer player, ItemStack itemstack) {
        if ((readFlyStatus(itemstack)) && (!player.onGround) && (player.capabilities.isFlying)
                && (!player.isInWater())) {
            double currCharge = Helpers.getCharge(itemstack);
            if ((currCharge > USAGE_IN_AIR * BOOST_MULTIPLIER) || (player.capabilities.isCreativeMode)) {
                player.moveFlying(0.0F, 0.4F, BOOST_SPPED);

                if (!player.capabilities.isCreativeMode) {
                    ElectricItem.discharge(itemstack, USAGE_IN_AIR * BOOST_MULTIPLIER, 3, true, false);
                }
            }
        }
    }

    public static boolean readWorkMode(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return tag.getBoolean("isLevitationActive");
    }

    public static void saveWorkMode(ItemStack stack, boolean workMode) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        tag.setBoolean("isLevitationActive", workMode);
        tag.setByte("toggleTimer", TOGGLE_TIMER);
    }

    public static void switchWorkMode(EntityPlayer player, ItemStack itemstack) {
        String message;
        if (readWorkMode(itemstack)) {
            saveWorkMode(itemstack, false);
            message = Refs.gravitation_levitation + " " + Refs.status_off;
        } else {
            saveWorkMode(itemstack, true);
            message = Refs.gravitation_levitation + " " + Refs.status_on;
        }
        if (IC2.platform.isSimulating()) {
            IC2.platform.messagePlayer(player, message);
        }
    }

    public static boolean readFlyStatus(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return tag.getBoolean("isFlyActive");
    }

    public static void saveFlyStatus(ItemStack stack, boolean flyMode) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        tag.setBoolean("isFlyActive", flyMode);
        tag.setByte("toggleTimer", TOGGLE_TIMER);
    }

    public static void switchFlyState(EntityPlayer player, ItemStack itemstack) {
        String message;
        if (readFlyStatus(itemstack)) {
            saveFlyStatus(itemstack, false);
            message = Refs.gravitation_engine + " " + Refs.status_off;
        } else {
            double currCharge = Helpers.getCharge(itemstack);
            if ((currCharge >= MIN_CHARGE) || (player.capabilities.isCreativeMode)) {
                message = Refs.gravitation_engine + " " + Refs.status_on;
                saveFlyStatus(itemstack, true);
            } else {
                message = Refs.status_low;
            }
        }
        if (IC2.platform.isSimulating()) {
            IC2.platform.messagePlayer(player, message);
        }
    }

    @Override
    public ArmorProperties getProperties(EntityLiving entityLiving, ItemStack armor, DamageSource damageSource, double damage, int slot) {
        int energyPerDamage = ENERGY_PER_DAMAGE;
        int damageLimit = Integer.MAX_VALUE;
        if (energyPerDamage > 0) {
            damageLimit = Math.min(damageLimit, 25 * Helpers.getCharge(armor) / energyPerDamage);
        }
        double absorptionRatio = 1.1D * 0.4D;
        return new ArmorProperties(8, absorptionRatio, damageLimit);
    }

    @Override
    public int getArmorDisplay(EntityPlayer entityPlayer, ItemStack itemStack, int i) {
        return 9;
    }

    @Override
    public void damageArmor(EntityLiving entityLiving, ItemStack stack, DamageSource damageSource, int damage, int slot) {
        ElectricItem.discharge(stack, damage * ENERGY_PER_DAMAGE, Integer.MAX_VALUE, true, false);
    }

    @Override
    public AudioSource getAudio(EntityPlayer player) {
        return IC2.audioManager.createSource(player, PositionSpec.Backpack, "graviengine.ogg", true, false, IC2.audioManager.defaultVolume);
    }
}
